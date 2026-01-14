#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Project       : RichLangToLang
# File          : RichLang2Lang.py
# Author        : XuHaoNan
# Version       : 1.0.0

import json
import logging
import os
import sys

# Support Minecraft Version [ < 1.20.3 - 23w40a ]


logging.basicConfig(level=logging.DEBUG, format="%(asctime)s - %(levelname)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S", handlers=[logging.FileHandler("Log.log", "w"), logging.StreamHandler(sys.stdout)], encoding="utf-8")


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
	RichTextPath = "./rich_lang"
	OutputPath = "./lang"
	if not os.path.isdir(RichTextPath):
		logging.error(f"{RichTextPath} Not Found Or Is Not A Folder.")
		sys.exit()
	if not os.path.isdir(OutputPath):
		os.makedirs(OutputPath)
	for RichLangFileName in os.listdir(RichTextPath):
		IsSuccess = ProcessRichLangFile(os.path.join(RichTextPath, RichLangFileName), os.path.join(OutputPath, RichLangFileName))
		if IsSuccess:
			logging.info(f"Processed {RichLangFileName}")
