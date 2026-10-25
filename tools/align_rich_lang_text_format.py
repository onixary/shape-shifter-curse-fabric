"""
Align rich text format from source localization to target
localization.

- Output structure follows the source file: N top-level elements in the
  source produce N top-level elements in the target, regardless of how the
  target was split.
- Each target element mirrors its source element's nesting style.
- Unstyled segments are wrapped as {"text": ...} when the source wraps them
  that way, and left as bare strings otherwise.
- Character-level alignment via difflib.SequenceMatcher keeps punctuation and
  spelling variants mapped to the correct source styles.
- Source has rich text: rebuild target using source styles (target text preserved).
- Source has no rich text: leave target unchanged.

The target text is preserved; only formatting (color etc.) is added.

Licensed under MIT.
"""
import argparse
import difflib
import json
import pathlib
import typing


JsonValue = str | int | float | bool | None | list["JsonValue"] | dict[str, "JsonValue"]
Style = dict[str, JsonValue] | None
RichTextNode = str | list["RichTextNode"] | dict[str, JsonValue]
FlatEntry = tuple[str, Style]
RichTextObject = dict[str, JsonValue]


UNFILLED: str = "__UNFILLED__"


def style_key(style: Style) -> str:
    """Hashable key for a style dict (or None)."""
    return json.dumps(style, sort_keys=True, ensure_ascii=False)


def is_rich_text(node: typing.Any) -> bool:
    """True if node is a rich text node (dict with 'text'/'extra', or list containing such)."""
    if isinstance(node, dict):
        return "text" in node or "extra" in node
    if isinstance(node, list):
        return any(is_rich_text(item) for item in node)
    return False


def prefers_text_object(node: RichTextNode) -> bool:
    """True if the source wraps unstyled segments as {"text": ...} objects."""
    unstyled_object_count: int = 0
    unstyled_string_count: int = 0

    def walk(current: typing.Any) -> None:
        nonlocal unstyled_object_count, unstyled_string_count
        if isinstance(current, str):
            unstyled_string_count += 1
            return
        if isinstance(current, list):
            for item in current:
                walk(item)
            return
        if isinstance(current, dict):
            text: str = current.get("text", "")
            style_keys: list[str] = [key for key in current.keys() if key not in ("text", "extra")]
            if text and not style_keys:
                unstyled_object_count += 1
            for item in current.get("extra", []):
                walk(item)

    walk(node)
    return unstyled_object_count > unstyled_string_count


def uses_extra_layer(source_element: RichTextNode) -> bool:
    """True if the source element is written as {text, extra}."""
    return isinstance(source_element, dict) and "extra" in source_element


def flatten(node: str | list[typing.Any] | dict[str, typing.Any]) -> list[tuple[str, dict[str, typing.Any] | None]]:
    """Flatten rich text into [(text, style), ...]. style=None for unstyled text."""
    if isinstance(node, str):
        return [(node, None)]
    if isinstance(node, list):
        out: list[tuple[str, dict[str, typing.Any] | None]] = []
        for item in node:
            out.extend(flatten(item))
        return out
    if isinstance(node, dict):
        style: dict[str, typing.Any] = {key: value for key, value in node.items() if key not in ("text", "extra")}
        out: list[tuple[str, dict[str, typing.Any] | None]] = []
        text: str = node.get("text", "")
        if text:
            out.append((text, style if style else None))
        for item in node.get("extra", []):
            out.extend(flatten(item))
        return out
    return []


def align_styles(
        source_flat: list[tuple[str, dict[str, typing.Any] | None]], target_text: str
) -> list[dict[str, typing.Any] | None]:
    """Return a list of styles (one per character) for target_text.

    Uses difflib.SequenceMatcher for character-level alignment.
    Unfilled positions are tracked separately from unstyled positions (None),
    so that unstyled characters are not accidentally recolored.
    """
    if not target_text:
        return []

    source_text: str = "".join(t for t, _ in source_flat)
    source_character_styles: list[dict[str, typing.Any] | None] = []
    for (text, style) in source_flat:
        source_character_styles.extend([style] * len(text))

    if source_text == target_text:
        return source_character_styles

    matcher: difflib.SequenceMatcher[str] = difflib.SequenceMatcher(
        None, source_text, target_text, autojunk=False
    )
    result: list[dict[str, typing.Any] | None | str] = [UNFILLED] * len(target_text)

    for tag, i1, i2, j1, j2 in matcher.get_opcodes():
        if tag == "equal":
            for k in range(j2 - j1):
                result[j1 + k] = source_character_styles[i1 + k]
        elif tag == "replace":
            source_length: int = i2 - i1
            target_length: int = j2 - j1
            for k in range(target_length):
                if source_length > 0:
                    source_index: int = i1 + min(int(k * source_length / target_length), source_length - 1)
                    result[j1 + k] = source_character_styles[source_index]
                else:
                    result[j1 + k] = None
        elif tag == "insert":
            for k in range(j2 - j1):
                if j1 + k > 0 and result[j1 + k - 1] is not UNFILLED:
                    result[j1 + k] = result[j1 + k - 1]
                elif i1 > 0:
                    result[j1 + k] = source_character_styles[i1 - 1]
                else:
                    result[j1 + k] = None

    final: list[dict[str, typing.Any] | None] = []
    for item in result:
        if item is UNFILLED:
            final.append(None)
        else:
            final.append(typing.cast(dict[str, typing.Any] | None, item))
    return final


def rebuild(flat: list[tuple[str, dict[str, typing.Any] | None]], wrap_unstyled: bool = False) -> dict[str, str | list[str | dict[str, typing.Any]]]:
    """Rebuild [(text, style), ...] into {text: '', extra: [...]}."""
    merged: list[tuple[str, dict[str, typing.Any] | None]] = []
    for (text, style) in flat:
        if merged and merged[-1][1] == style:
            merged[-1] = (merged[-1][0] + text, style)
        else:
            merged.append((text, style))

    extra: list[str | dict[str, typing.Any]] = []
    for (text, style) in merged:
        if style is None:
            if wrap_unstyled:
                extra.append({"text": text})
            else:
                extra.append(text)
        else:
            item: dict[str, typing.Any] = {"text": text}
            item.update(style)
            extra.append(item)
    return {"text": "", "extra": extra}


def build_element(groups: list[FlatEntry], wrap_unstyled: bool = False, force_extra: bool = False) -> JsonValue:
    """Build one top-level element.

    - force_extra=True: always use {"text": "", "extra": [...]}, mirroring
      a source element written as {text, extra}.
    - Otherwise, a single segment is emitted directly (string or
      {"text": ..., ...style}), and multiple segments use the extra layer.
    """
    merged: list[FlatEntry] = []
    for (text, style) in groups:
        if merged and merged[-1][1] == style:
            merged[-1] = (merged[-1][0] + text, style)
        else:
            merged.append((text, style))

    if not merged:
        return "" if not force_extra else rebuild([], wrap_unstyled)

    if len(merged) == 1 and not force_extra:
        text, style = merged[0]
        if style is None:
            if wrap_unstyled:
                return {"text": text}
            return text
        item: dict[str, typing.Any] = {"text": text}
        item.update(style)
        return item

    return rebuild(merged, wrap_unstyled)


def build_element_from_styles(text: str, styles: list[Style], wrap_unstyled: bool = False, force_extra: bool = False) -> JsonValue:
    """Build one element from a text slice and its per-character styles."""
    if not text:
        return "" if not force_extra else rebuild([], wrap_unstyled)
    groups: list[FlatEntry] = []
    current_style: Style = styles[0]
    current_text: str = text[0]
    for i in range(1, len(text)):
        if styles[i] == current_style:
            current_text += text[i]
        else:
            groups.append((current_text, current_style))
            current_text = text[i]
            current_style = styles[i]
    groups.append((current_text, current_style))
    return build_element(groups, wrap_unstyled, force_extra)


def compute_split_points(source_texts: list[str], target_text: str) -> list[int]:
    """Split target_text into len(source_texts) slices, proportional by source length.

    When the source boundary falls inside a word, the proportional position is
    kept as-is: aligning to whitespace would move text across the boundary and
    break the intended structure. Otherwise, the position is snapped to the
    nearest whitespace boundary.
    """
    count: int = len(source_texts)
    if count <= 1:
        return [0, len(target_text)]

    total_source_length: int = sum(len(s) for s in source_texts)
    if total_source_length == 0:
        return [0] + [len(target_text)] * (count - 1) + [len(target_text)]

    points: list[int] = [0]
    accumulated: int = 0
    for i in range(count - 1):
        accumulated += len(source_texts[i])
        position: int = round(accumulated / total_source_length * len(target_text))

        source_left: str = source_texts[i][-1] if source_texts[i] else ""
        source_right: str = source_texts[i + 1][0] if i + 1 < len(source_texts) and source_texts[i + 1] else ""
        boundary_inside_word: bool = (
            bool(source_left) and not source_left.isspace()
            and bool(source_right) and not source_right.isspace()
        )

        if not boundary_inside_word:
            if position <= 0:
                position = 0
            elif position >= len(target_text):
                position = len(target_text)
            elif target_text[position].isspace() or target_text[position - 1].isspace():
                pass
            else:
                forward: int = position
                while forward < len(target_text) and not target_text[forward].isspace():
                    forward += 1
                backward: int = position
                while backward > 0 and not target_text[backward - 1].isspace():
                    backward -= 1
                if position - backward <= forward - position:
                    position = backward
                else:
                    position = forward

        if position < points[-1]:
            position = points[-1]
        points.append(position)
    points.append(len(target_text))
    return points


def apply_rich_text(source_node: RichTextNode, target_value: JsonValue) -> JsonValue:
    """Apply rich text format from source_node to target_value.

    Output top-level structure follows source_node: a dict source yields a
    single dict, an N-element list source yields an N-element list.
    """
    if not is_rich_text(source_node):
        return target_value

    # Collect all plain text from target, ignoring its own splitting
    target_text: str = ""
    if isinstance(target_value, list):
        for item in target_value:
            if isinstance(item, str):
                target_text += item
            elif is_rich_text(item):
                target_text += "".join(t for t, _ in flatten(item))
            else:
                return target_value
    elif isinstance(target_value, str):
        target_text = target_value
    elif isinstance(target_value, dict):
        if is_rich_text(target_value):
            target_text = "".join(t for t, _ in flatten(target_value))
        else:
            return target_value
    else:
        return target_value

    if not target_text:
        return target_value

    # Top-level elements of the source define the output structure
    if isinstance(source_node, list):
        source_elements: list[RichTextNode] = source_node
    else:
        source_elements = [source_node]

    source_flat: list[FlatEntry] = flatten(source_node)
    styles: list[Style] = align_styles(source_flat, target_text)

    source_texts: list[str] = ["".join(t for t, _ in flatten(element)) for element in source_elements]
    split_points: list[int] = compute_split_points(source_texts, target_text)

    result_elements: list[JsonValue] = []
    for i in range(len(split_points) - 1):
        start: int = split_points[i]
        end: int = split_points[i + 1]
        element_text: str = target_text[start:end]
        element_styles: list[Style] = styles[start:end]

        source_element: RichTextNode = source_elements[i] if i < len(source_elements) else ""
        force_extra: bool = uses_extra_layer(source_element)
        wrap_unstyled: bool = prefers_text_object(source_element)

        result_elements.append(
            build_element_from_styles(element_text, element_styles, wrap_unstyled, force_extra)
        )

    if isinstance(source_node, list):
        return result_elements
    return result_elements[0]


def parse_arguments() -> argparse.Namespace:
    parser: argparse.ArgumentParser = argparse.ArgumentParser()
    parser.add_argument("-s", "--source", required=True, help="source file to align")
    parser.add_argument("-t", "--target", required=True, help="target file to align")

    return parser.parse_args()


def main() -> None:
    arguments: argparse.Namespace = parse_arguments()

    source_path: pathlib.Path = pathlib.Path(arguments.source)
    target_path: pathlib.Path = pathlib.Path(arguments.target)

    source: dict[str, JsonValue] = json.loads(source_path.read_text(encoding="utf-8"))
    target: dict[str, JsonValue] = json.loads(target_path.read_text(encoding="utf-8"))

    result: dict[str, JsonValue] = {}
    for key, source_value in source.items():
        if key not in target:
            print(f"[Warning] Key '{key}' not found in target.")
            result[key] = source_value
            continue

        target_value: JsonValue = target[key]

        # Source has no formatting: keep target as-is
        if not is_rich_text(source_value):
            result[key] = target_value
            continue

        result[key] = apply_rich_text(source_value, target_value)

    # Keep keys only present in target
    for (key, target_value) in target.items():
        if key not in result:
            result[key] = target_value

    target_path.write_text(
        json.dumps(result, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(f"Done. Wrote {len(result)} keys to {target_path}")


if __name__ == "__main__":
    main()
