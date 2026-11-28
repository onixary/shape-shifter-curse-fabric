#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Project       : GenEnUdLang
# File          : GenEnUdLang.py
# Author        : ShapeShifterCurse

# Description   : 由 en_gb 生成 en_ud（倒置英语）的 rich_lang 与 lang 文件

# LICENSE       : MIT (ShapeShifterCurse 开发组 - XuHaoNan & Onixary)

"""生成 en_ud（倒置英语）语言文件。

规则取自原版 en_ud（`assets/minecraft/lang/en_ud.json`）：
  1. 整串字符反转；
  2. 逐字符映射为倒置字形（见 FLIP_MAP，由原版 en_gb / en_ud 对照统计得出）；
  3. `%s` / `%d` 等占位符整体保留：只有一个占位符时原样保留；
     同一串有多个占位符时，未编号的会转成 `%n$s`，保证反转后参数顺序正确；
  4. rich_lang 的富文本按「组件顺序反转 + 每段文本翻转」处理，颜色等样式随原组件保留；
  5. 映射表里没有的字符原样保留。

用法：
  python GenEnUdLang.py                  # 由 en_gb 生成 en_ud（rich_lang + lang）
  python GenEnUdLang.py --check          # 只校验现有 en_ud 与生成结果是否一致
  python GenEnUdLang.py --base en_gb.json --target en_ud.json
"""

from __future__ import annotations

import argparse
import json
import logging
import os
import re

FLIP_MAP = {
    '!': '¡',
    '"': ',,',
    '&': '⅋',
    "'": ',',
    '(': ')',
    ')': '(',
    ',': "'",
    '.': '˙',
    '1': '⥝',
    '2': 'ᘔ',
    '3': 'Ɛ',
    '4': '߈',
    '5': 'ϛ',
    '6': '9',
    '7': 'ㄥ',
    '9': '6',
    ';': '⸵',
    '<': '>',
    '>': '<',
    '?': '¿',
    'A': 'Ɐ',
    'B': 'ᗺ',
    'C': 'Ɔ',
    'D': 'ᗡ',
    'E': 'Ǝ',
    'F': 'Ⅎ',
    'G': '⅁',
    'J': 'Ր',
    'K': 'Ʞ',
    'L': 'Ꞁ',
    'M': 'W',
    'P': 'Ԁ',
    'Q': 'Ꝺ',
    'R': 'ᴚ',
    'T': '⟘',
    'U': '∩',
    'V': 'Ʌ',
    'W': 'M',
    'Y': '⅄',
    '[': ']',
    ']': '[',
    '_': '‾',
    'a': 'ɐ',
    'b': 'q',
    'c': 'ɔ',
    'd': 'p',
    'e': 'ǝ',
    'f': 'ɟ',
    'g': 'ᵷ',
    'h': 'ɥ',
    'i': 'ᴉ',
    'j': 'ɾ',
    'k': 'ʞ',
    'l': 'ꞁ',
    'm': 'ɯ',
    'n': 'u',
    'p': 'd',
    'q': 'b',
    'r': 'ɹ',
    't': 'ʇ',
    'u': 'n',
    'v': 'ʌ',
    'w': 'ʍ',
    'y': 'ʎ',
    '{': '}',
    '}': '{',
    '↑': '↓',
    '↓': '↑',
}

PLACEHOLDER = re.compile(r"%(?:\d+\$)?[a-zA-Z%]")


def GetBasePath() -> str:
    """脚本在 tools 目录时自动定位到 assets/shape-shifter-curse，否则使用当前目录。"""
    ScriptDir = os.path.dirname(os.path.abspath(__file__))
    if os.path.basename(ScriptDir).lower() == "tools":
        BasePath = os.path.normpath(os.path.join(ScriptDir, "..", "src", "main", "resources", "assets", "shape-shifter-curse"))
        if os.path.isdir(BasePath):
            logging.info("Using resource path: %s", BasePath)
            return BasePath
        logging.warning("Expected resource directory not found: %s", BasePath)
    logging.info("Using current working directory: %s", os.getcwd())
    return os.getcwd()


def Tokenize(Text: str) -> list[tuple[str, str]]:
    Tokens = []
    Index = 0
    while Index < len(Text):
        if Text[Index] == "%":
            Match = PLACEHOLDER.match(Text, Index)
            if Match:
                Tokens.append(("placeholder", Match.group(0)))
                Index = Match.end()
                continue
        Tokens.append(("char", Text[Index]))
        Index += 1
    return Tokens


def FlipText(Text: str) -> str:
    Tokens = Tokenize(Text)
    Arguments = [Token for Token in Tokens if Token[0] == "placeholder" and Token[1] != "%%"]
    Numbered = len(Arguments) >= 2
    ArgumentIndex = 0
    Result = []
    for Kind, Value in Tokens:
        if Kind == "char":
            Result.append(("char", Value))
            continue
        if Value == "%%":
            Result.append(("placeholder", Value))
            continue
        if Numbered and not re.match(r"%\d+\$", Value):
            ArgumentIndex += 1
            Result.append(("placeholder", "%%%d$%s" % (ArgumentIndex, Value[-1])))
        else:
            Result.append(("placeholder", Value))
    Result.reverse()
    return "".join(Value if Kind == "placeholder" else FLIP_MAP.get(Value, Value) for Kind, Value in Result)


def FlipComponent(Component):
    if isinstance(Component, str):
        return FlipText(Component)
    if isinstance(Component, list):
        return [FlipComponent(Child) for Child in reversed(Component)]
    if isinstance(Component, dict):
        Style = {Key: Value for Key, Value in Component.items() if Key not in ("text", "extra")}
        Text = Component.get("text", "") if isinstance(Component.get("text"), str) else ""
        Extra = Component.get("extra")
        if isinstance(Extra, list) and Extra:
            Children = [FlipComponent(Child) for Child in reversed(Extra)]
            if Text:
                # 原组件里 text 在 extra 之前，反转后需要拆成「extra 在前、text 在后」的两段
                Head = dict(Style)
                Head["text"] = ""
                Head["extra"] = Children
                Tail = dict(Style)
                Tail["text"] = FlipText(Text)
                return [Head, Tail]
            Result = dict(Style)
            Result["text"] = ""
            Result["extra"] = Children
            return Result
        Result = dict(Style)
        if isinstance(Component.get("text"), str):
            Result["text"] = FlipText(Component["text"])
        return Result
    return Component


def Flatten(Component) -> str:
    if isinstance(Component, str):
        return Component
    if isinstance(Component, list):
        return "".join(Flatten(Child) for Child in Component)
    if isinstance(Component, dict):
        Text = Flatten(Component.get("text", ""))
        for Child in Component.get("extra", []) or []:
            Text += Flatten(Child)
        return Text
    return ""


def LoadJson(Path: str) -> dict:
    with open(Path, "r", encoding="utf-8-sig") as File:
        return json.load(File)


def SaveJson(Path: str, Data: dict) -> None:
    Text = json.dumps(Data, ensure_ascii=False, indent=2) + "\n"
    with open(Path, "w", encoding="utf-8", newline="") as File:
        File.write(Text.replace("\n", "\r\n"))


def BuildRichLang(BaseRichLang: dict) -> tuple[dict, list[str]]:
    Result = {}
    FallbackKeys = []
    for Key, Value in BaseRichLang.items():
        Target = FlipText(Flatten(Value))
        Flipped = FlipComponent(Value)
        if Flatten(Flipped) == Target:
            Result[Key] = Flipped
        else:
            # 极端结构（占位符跨组件等）退化为纯文本，保证 lang 与 rich_lang 一致
            FallbackKeys.append(Key)
            Result[Key] = Target
    return Result, FallbackKeys


def Process(BaseLangName: str, TargetLangName: str, CheckOnly: bool) -> int:
    BasePath = GetBasePath()
    RichLangDir = os.path.join(BasePath, "rich_lang")
    LangDir = os.path.join(BasePath, "lang")

    BaseRichLang = LoadJson(os.path.join(RichLangDir, BaseLangName))
    BaseLang = LoadJson(os.path.join(LangDir, BaseLangName))
    if set(BaseRichLang) != set(BaseLang):
        logging.error("Key set mismatch between rich_lang/%s and lang/%s", BaseLangName, BaseLangName)
        return 1

    RichLang, FallbackKeys = BuildRichLang(BaseRichLang)
    Lang = {Key: FlipText(Value) for Key, Value in BaseLang.items()}
    if FallbackKeys:
        logging.warning("%d keys fell back to plain text: %s", len(FallbackKeys), FallbackKeys[:5])
    for Key in BaseLang:
        if Flatten(RichLang[Key]) != Lang[Key]:
            logging.error("rich_lang/lang mismatch after flip: %s", Key)
            return 1

    logging.info("Generated %d keys from %s (fallback: %d)", len(Lang), BaseLangName, len(FallbackKeys))

    if CheckOnly:
        Diff = 0
        for Directory, Data, Name in ((RichLangDir, RichLang, "rich_lang"), (LangDir, Lang, "lang")):
            Path = os.path.join(Directory, TargetLangName)
            if not os.path.isfile(Path):
                logging.error("Missing %s/%s", Name, TargetLangName)
                return 1
            Existing = LoadJson(Path)
            Keys = set(Data) | set(Existing)
            for Key in Keys:
                if Data.get(Key) != Existing.get(Key):
                    Diff += 1
                    if Diff <= 5:
                        logging.warning("Diff in %s/%s: %s", Name, TargetLangName, Key)
        logging.info("Check done: %d differing keys", Diff)
        return 0 if Diff == 0 else 2

    SaveJson(os.path.join(RichLangDir, TargetLangName), RichLang)
    SaveJson(os.path.join(LangDir, TargetLangName), Lang)
    logging.info("Written rich_lang/%s and lang/%s", TargetLangName, TargetLangName)
    return 0


def main() -> int:
    Parser = argparse.ArgumentParser(description="Generate en_ud (upside-down English) from en_gb")
    Parser.add_argument("--base", default="en_gb.json", help="base language file name (default: en_gb.json)")
    Parser.add_argument("--target", default="en_ud.json", help="target language file name (default: en_ud.json)")
    Parser.add_argument("--check", action="store_true", help="only verify existing target files")
    Args = Parser.parse_args()
    logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S")
    return Process(Args.base, Args.target, Args.check)


if __name__ == "__main__":
    raise SystemExit(main())
