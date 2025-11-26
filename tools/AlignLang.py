#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# File          : AlignLang.py
# Author        : XuHaoNan

# Description   : 让lang对齐

# 由于rich_lang里有换行 虽然删除换行不会出现问题 但会大幅度降低后续修改可读性
# 如果将脚本移动时 请修改LangFolderPath

import os
import json

TargetNameSpace = "shape-shifter-curse"
LangFolderPath = f"../src/main/resources/assets/{TargetNameSpace}/lang"


# 目前所有的Lang ["zh_cn.json", "en_us.json", "ru_ru.json"]
BaseAlignLang = "zh_cn.json"  # 基础语言
AlignTargetLang = ["en_us.json"]  # 需要对齐的目标语言 由于俄语需要其他人手动翻译 所以只对英文进行对齐


def get_lang_list() -> list[str]:
	return [file for file in os.listdir(LangFolderPath) if file.endswith(".json")]


def get_lang_path(lang_name: str) -> str:
	return os.path.join(LangFolderPath, lang_name).replace("\\", "/")


def get_lang_dict(lang_name: str) -> dict[str, str]:
	with open(get_lang_path(lang_name), "rb") as f:
		return json.load(f)


def save_lang(lang_name: str, lang_dict: dict[str, str]):
	with open(get_lang_path(lang_name), "w", encoding="utf-8") as f:
		json.dump(lang_dict, f, ensure_ascii=False, indent=2)


def align_lang():
	base_lang = get_lang_dict(BaseAlignLang)
	for lang_name in get_lang_list():
		if lang_name not in AlignTargetLang:
			continue
		lang = get_lang_dict(lang_name)
		final_lang = {}
		for key, value in base_lang.items():
			if key not in lang:
				lang[key] = value
				print(f"{lang_name} Missing {key}")  # 输出缺失的key 需要后续手动修改
			final_lang[key] = lang[key]
		save_lang(lang_name, final_lang)
		print(f"{lang_name} Align Success")


if __name__ == "__main__":
	align_lang()