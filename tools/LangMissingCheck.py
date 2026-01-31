#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan


import json


DEFAULT_LANG = "zh_cn.json"
OTHER_LANGS = [
	"en_us.json",
	"ru_ru.json",
]


def getLangPath(LangName: str) -> str:
	return "../src/main/resources/assets/shape-shifter-curse/lang/%s" % LangName


def getRichLangPath(LangName: str) -> str:
	return "../src/main/resources/assets/shape-shifter-curse/rich_lang/%s" % LangName


def getLangAllKeys(LangJson: dict[str, any]) -> set[str]:
	return set(LangJson.keys())


def getDifference(BaseLangKeys: set[str], LangKeys: set[str]) -> tuple[set[str], set[str]]:
	return LangKeys - BaseLangKeys, BaseLangKeys - LangKeys


def LogDifference(TargetLangType: str, TargetLangName: str, ADD: set[str], DELETE: set[str]) -> None:
	print(f"{TargetLangType} {TargetLangName}:")
	if len(ADD) == 0 and len(DELETE) == 0:
		print("No difference\n")
		return None
	print(f"ADD: {len(ADD)}")
	for key in ADD:
		print(f"  {key}")
	print(f"DELETE: {len(DELETE)}")
	for key in DELETE:
		print(f"  {key}")
	print("")
	return None


def ReadLangJson(LangJsonPath: str) -> dict[str, any]:
	with open(LangJsonPath, "rb") as File:
		return json.load(File)


if __name__ == "__main__":
	BaseLangKeys = getLangAllKeys(ReadLangJson(getLangPath(DEFAULT_LANG)))
	for LangName in OTHER_LANGS:
		LangKeys = getLangAllKeys(ReadLangJson(getLangPath(LangName)))
		ADD, DELETE = getDifference(BaseLangKeys, LangKeys)
		LogDifference("Lang", LangName, ADD, DELETE)
	BaseRichLangKeys = getLangAllKeys(ReadLangJson(getRichLangPath(DEFAULT_LANG)))
	for LangName in OTHER_LANGS:
		LangKeys = getLangAllKeys(ReadLangJson(getRichLangPath(LangName)))
		ADD, DELETE = getDifference(BaseRichLangKeys, LangKeys)
		LogDifference("RichLang", LangName, ADD, DELETE)
	input("Press Enter to exit")
