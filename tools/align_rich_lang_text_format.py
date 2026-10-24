"""
Align rich text format from source localization to target
localization.

- For keys where target is already rich text: leave unchanged.
- For keys where source is rich text but target is plain text: apply formatting.
- For everything else: leave unchanged.

The target text is preserved; only formatting (color etc.) is added.

Licensed under MIT.
"""
import argparse
import json
import pathlib
import typing


JsonValue = str | int | float | bool | None | list["JsonValue"] | dict[str, "JsonValue"]
Style = dict[str, JsonValue] | None
RichTextNode = str | list["RichTextNode"] | dict[str, JsonValue]
FlatEntry = tuple[str, Style]
RichTextObject = dict[str, JsonValue]


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
    """Return a list of styles (one per character) for target_text."""
    source_text: str = "".join(t for t, _ in source_flat)

    # Exact match: map by character position
    if source_text == target_text:
        styles: list[dict[str, typing.Any] | None] = []
        for text, style in source_flat:
            styles.extend([style] * len(text))
        return styles

    # Fallback: proportional mapping by character position
    source_length: int = len(source_text)
    target_length: int = len(target_text)
    if source_length == 0:
        return [None] * target_length

    result: list[dict[str, typing.Any] | None] = []
    source_position: int = 0
    for (text, style) in source_flat:
        source_start: int = source_position
        source_end: int = source_position + len(text)
        source_position = source_end
        target_start: int = round(source_start / source_length * target_length)
        target_end: int = round(source_end / source_length * target_length)
        for _ in range(target_start, target_end):
            result.append(style)

    while len(result) < target_length:
        result.append(None)
    return result[:target_length]


def rebuild(flat: list[tuple[str, dict[str, typing.Any] | None]]) -> dict[str, str | list[str | dict[str, typing.Any]]]:
    """Rebuild [(text, style), ...] into {text: '', extra: [...]}."""
    extra: list[str | dict[str, typing.Any]] = []
    for (text, style) in flat:
        if style is None:
            extra.append(text)
        else:
            item: dict[str, str] = {"text": text}
            item.update(style)
            extra.append(item)
    return {"text": "", "extra": extra}


def apply_rich_text(source_node: RichTextNode, target_value: JsonValue) -> JsonValue:
    """Apply rich text format from source_node to target_value."""
    if not is_rich_text(source_node):
        return target_value

    source_flat: list[FlatEntry] = flatten(source_node)

    # Normalize target to a list of plain-text segments
    target_elements: list[str]
    target_text: str
    if isinstance(target_value, list):
        target_elements = []
        for item in target_value:
            if isinstance(item, str):
                target_elements.append(item)
            elif is_rich_text(item):
                target_elements.append("".join(t for t, _ in flatten(item)))
            else:
                return target_value
        target_text = "".join(target_elements)
    elif isinstance(target_value, str):
        target_elements = [target_value]
        target_text = target_value
    elif isinstance(target_value, dict):
        target_text = "".join(t for t, _ in flatten(target_value))
        target_elements = [target_text]
    else:
        return target_value

    styles: list[Style] = align_styles(source_flat, target_text)

    # Rebuild each target segment with its own style groups
    result_elements: list[RichTextObject] = []
    position: int = 0
    for element in target_elements:
        element_length: int = len(element)
        element_style: list[Style] = styles[position : position + element_length]
        position += element_length

        groups: list[FlatEntry] = []
        if element:
            current_style: Style = element_style[0]
            current_text: str = element[0]
            for i in range(1, element_length):
                if element_style[i] == current_style:
                    current_text += element[i]
                else:
                    groups.append((current_text, current_style))
                    current_text = element[i]
                    current_style = element_style[i]
            groups.append((current_text, current_style))
        result_elements.append(rebuild(groups))

    # Preserve target structure: list -> list, string/dict -> single object
    if isinstance(target_value, list):
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

        # Already rich text in target: don't touch it
        if is_rich_text(target_value):
            result[key] = target_value
            continue

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