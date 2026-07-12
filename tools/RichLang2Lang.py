#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Project       : RichLangToLang
# File          : RichLang2Lang.py
# Author        : XuHaoNan
# Version       : 1.1.0 (Modified for auto path detection)

import json
import logging
import os
import sys

# Support Minecraft Version [ < 1.20.3 - 23w40a ]


logging.basicConfig(level=logging.DEBUG, format="%(asctime)s - %(levelname)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S", handlers=[logging.FileHandler("Log.log", "w", encoding="utf-8"), logging.StreamHandler(sys.stdout)])


def GetBasePath() -> str:
    """
    获取基础工作路径。
    如果脚本位于 tools 目录下，则返回指向 src/main/resources/assets/shape-shifter-curse 的相对路径。
    否则返回当前工作目录（保持向后兼容）。
    """
    ScriptDir = os.path.dirname(os.path.abspath(__file__))
    ScriptFolderName = os.path.basename(ScriptDir)

    # 如果脚本在 tools 目录下，自动定位到资源目录
    if ScriptFolderName.lower() == "tools":
        # 假设结构: project_root/tools/RichLang2Lang.py -> project_root/src/main/resources/assets/shape-shifter-curse
        BasePath = os.path.join(ScriptDir, "..", "src", "main", "resources", "assets", "shape-shifter-curse")
        BasePath = os.path.normpath(BasePath)  # 规范化路径
        if os.path.isdir(BasePath):
            logging.info(f"Auto-detected tools directory. Using resource path: {BasePath}")
            return BasePath
        else:
            logging.warning(f"Expected resource directory not found: {BasePath}")

    # 默认使用当前工作目录（向后兼容：手动移动到目标目录运行的情况）
    CurrentDir = os.getcwd()
    logging.info(f"Using current working directory: {CurrentDir}")
    return CurrentDir


def GetString(JsonElement: dict[str, any] | list[any] | str) -> str:
    FinalString = ""
    if isinstance(JsonElement, dict):
        if "text" in JsonElement:
            FinalString += JsonElement["text"]
        elif "translatable" in JsonElement:
            # translatable 需要读取其他lang文件暂时不支持
            logging.warning("Translatable is not supported. Skipped")
        elif "score" in JsonElement or "nbt" in JsonElement or "selector" in JsonElement or "keybind" in JsonElement:
            # score nbt selector keybind 为动态值，无法支持
            logging.warning("Score, NBT, Selector, Keybind is not supported. Skipped")
        if "extra" in JsonElement:
            FinalString += GetString(JsonElement["extra"])
    elif isinstance(JsonElement, list):
        for i in JsonElement:
            FinalString += GetString(i)
    elif isinstance(JsonElement, str):
        FinalString = JsonElement
    else:
        FinalString = ""
    return FinalString


def ProcessRichLang(JsonData: dict[str, any]) -> dict[str, str]:
    FinalLang = {}
    for Key, Value in JsonData.items():
        FinalLang[Key] = GetString(Value)
    return FinalLang


def ProcessRichLangFile(FilePath: str, OutputPath: str) -> bool:
    try:
        with open(FilePath, "rb") as File:
            JsonData = json.load(File)
        FinalLang = ProcessRichLang(JsonData)
        with open(OutputPath, "w", encoding="utf-8") as File:
            json.dump(FinalLang, File, ensure_ascii=False, indent=4)
        return True
    except Exception as e:
        logging.error(f"Error Processing {FilePath}: {e}")
        return False


if __name__ == "__main__":
    # 获取基础路径（自动检测或手动模式）
    BasePath = GetBasePath()

    # 构建 rich_lang 和 lang 的完整路径
    RichTextPath = os.path.join(BasePath, "rich_lang")
    OutputPath = os.path.join(BasePath, "lang")

    if not os.path.isdir(RichTextPath):
        logging.error(f"{RichTextPath} Not Found Or Is Not A Folder.")
        sys.exit(1)
    if not os.path.isdir(OutputPath):
        os.makedirs(OutputPath)
        logging.info(f"Created output directory: {OutputPath}")

    SuccessCount = 0
    FailCount = 0

    for RichLangFileName in os.listdir(RichTextPath):
        # 只处理 .json 文件
        if not RichLangFileName.endswith(".json"):
            continue

        InputFile = os.path.join(RichTextPath, RichLangFileName)
        OutputFile = os.path.join(OutputPath, RichLangFileName)

        IsSuccess = ProcessRichLangFile(InputFile, OutputFile)
        if IsSuccess:
            logging.info(f"Processed {RichLangFileName}")
            SuccessCount += 1
        else:
            FailCount += 1

    logging.info(f"Conversion complete. Success: {SuccessCount}, Failed: {FailCount}")
