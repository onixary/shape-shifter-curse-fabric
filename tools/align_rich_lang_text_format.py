"""
Align rich text format from source localization to target
localization.

- Output structure follows the source file: N top-level elements in the
  source produce N top-level elements in the target, regardless of how the
  target was split.
- Source has rich text: rebuild target using source styles (target text preserved).
- Source has no rich text: leave target unchanged.

The target text is preserved; only formatting (color etc.) is added.

Licensed under MIT.
"""
import argparse
import collections
import json
import pathlib
import re
import typing


JsonValue = str | int | float | bool | None | list["JsonValue"] | dict[str, "JsonValue"]
Style = dict[str, JsonValue] | None
RichTextNode = str | list["RichTextNode"] | dict[str, JsonValue]
FlatEntry = tuple[str, Style]
RichTextObject = dict[str, JsonValue]


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

    Words are aligned by token (handles spelling variants like colour/color).
    Gaps (whitespace runs) are aligned by position, so newlines keep their
    original unstyled state instead of inheriting the previous word's style.
    """
    if not target_text:
        return []

    # Expand source into per-character styles
    source_text: str = "".join(t for t, _ in source_flat)
    source_character_styles: list[dict[str, typing.Any] | None] = []
    for (text, style) in source_flat:
        source_character_styles.extend([style] * len(text))

    # Exact match: map by character position
    if source_text == target_text:
        return source_character_styles

    source_word_matches: list[re.Match[str]] = list(re.finditer(r"\S+", source_text))
    target_word_matches: list[re.Match[str]] = list(re.finditer(r"\S+", target_text))

    source_word_count: int = len(source_word_matches)
    target_word_count: int = len(target_word_matches)

    if source_word_count == 0:
        return [None] * len(target_text)

    # Word-level style for source: majority style inside each word
    source_words: list[dict[str, typing.Any] | None] = []
    for match in source_word_matches:
        styles: list[dict[str, typing.Any] | None] = source_character_styles[match.start():match.end()]
        if styles:
            key_to_style: dict[str, dict[str, typing.Any] | None] = {}
            for candidate in styles:
                key_to_style[style_key(candidate)] = candidate
            counts: collections.Counter[str] = collections.Counter(style_key(candidate) for candidate in styles)
            style: dict[str, typing.Any] | None = key_to_style[counts.most_common(1)[0][0]]
        else:
            style = None
        source_words.append(style)

    # Map target words to source words
    if source_word_count == target_word_count:
        word_styles: list[dict[str, typing.Any] | None] = source_words
    else:
        word_styles = []
        for i in range(target_word_count):
            index: int = min(round(i / target_word_count * source_word_count), source_word_count - 1)
            word_styles.append(source_words[index])

    result: list[dict[str, typing.Any] | None] = [None] * len(target_text)

    # Fill words
    for match, style in zip(target_word_matches, word_styles):
        for j in range(match.start(), match.end()):
            result[j] = style

    # Extract gaps (whitespace runs between words, including leading/trailing)
    def extract_gaps(text: str, word_matches: list[re.Match[str]]) -> list[tuple[int, int]]:
        gaps: list[tuple[int, int]] = []
        previous_end: int = 0
        for match in word_matches:
            gaps.append((previous_end, match.start()))
            previous_end = match.end()
        gaps.append((previous_end, len(text)))
        return gaps

    source_gaps: list[tuple[int, int]] = extract_gaps(source_text, source_word_matches)
    target_gaps: list[tuple[int, int]] = extract_gaps(target_text, target_word_matches)

    # If gap counts match, map gaps one-to-one (newlines keep their unstyled state)
    if len(source_gaps) == len(target_gaps):
        for (source_gap_start, source_gap_end), (target_gap_start, target_gap_end) in zip(source_gaps, target_gaps):
            if source_gap_start < source_gap_end:
                styles_in_gap: list[dict[str, typing.Any] | None] = source_character_styles[source_gap_start:source_gap_end]
                key_to_style: dict[str, dict[str, typing.Any] | None] = {}
                for candidate in styles_in_gap:
                    key_to_style[style_key(candidate)] = candidate
                counts: collections.Counter[str] = collections.Counter(style_key(candidate) for candidate in styles_in_gap)
                gap_style: dict[str, typing.Any] | None = key_to_style[counts.most_common(1)[0][0]]
            else:
                gap_style = None
            for j in range(target_gap_start, target_gap_end):
                result[j] = gap_style
    else:
        # Fallback: character-proportional mapping for gaps only
        source_length: int = len(source_text)
        target_length: int = len(target_text)
        source_position: int = 0
        for (text, style) in source_flat:
            source_start: int = source_position
            source_end: int = source_position + len(text)
            source_position = source_end
            target_start: int = round(source_start / source_length * target_length)
            target_end: int = round(source_end / source_length * target_length)
            for j in range(target_start, target_end):
                if target_text[j].isspace() and result[j] is None:
                    result[j] = style

    return result


def rebuild(flat: list[tuple[str, dict[str, typing.Any] | None]]) -> dict[str, str | list[str | dict[str, typing.Any]]]:
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
            extra.append(text)
        else:
            item: dict[str, typing.Any] = {"text": text}
            item.update(style)
            extra.append(item)
    return {"text": "", "extra": extra}


def build_element(groups: list[FlatEntry]) -> JsonValue:
    """Build one top-level element without unnecessary nesting.

    - Single unstyled segment -> plain string.
    - Single styled segment   -> {"text": ..., ...style}.
    - Multiple segments       -> {"text": "", "extra": [...]}.
    """
    merged: list[FlatEntry] = []
    for (text, style) in groups:
        if merged and merged[-1][1] == style:
            merged[-1] = (merged[-1][0] + text, style)
        else:
            merged.append((text, style))

    if not merged:
        return ""
    if len(merged) == 1:
        text, style = merged[0]
        if style is None:
            return text
        item: dict[str, typing.Any] = {"text": text}
        item.update(style)
        return item
    return rebuild(merged)


def build_element_from_styles(text: str, styles: list[Style]) -> JsonValue:
    """Build one element from a text slice and its per-character styles."""
    if not text:
        return ""
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
    return build_element(groups)


def compute_split_points(source_texts: list[str], target_text: str) -> list[int]:
    """Split target_text into len(source_texts) slices, proportional by source length.

    Each split point is nudged forward to the next whitespace boundary so that
    words are not cut in half.
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
        while position < len(target_text) and not target_text[position].isspace():
            position += 1
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
        result_elements.append(build_element_from_styles(element_text, element_styles))

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
