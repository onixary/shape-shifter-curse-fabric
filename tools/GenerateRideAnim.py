#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# File          : GenerateRideAnim.py
# Author        : XuHaoNan

# Description   : 生成骑乘动画

# 由于原版骑乘动画是悬空的 并且悬空多少和实体高度有关 所以需要根据实体高度生成

import os
import json
import typing
import math

FormRideAnimationMap = {
	"shape-shifter-curse:form_bat_2": "shape-shifter-curse:bat_1_sneak_idle",
	"shape-shifter-curse:form_bat_3": "shape-shifter-curse:bat_1_sneak_idle",
	"shape-shifter-curse:form_ocelot_2": "shape-shifter-curse:ocelot_2_sneak_idle",
	"shape-shifter-curse:form_ocelot_3": "shape-shifter-curse:form_feral_common_sneak_idle",
	"shape-shifter-curse:form_familiar_fox_2": "shape-shifter-curse:ocelot_2_sneak_idle",
	"shape-shifter-curse:form_familiar_fox_3": "shape-shifter-curse:form_feral_common_sneak_idle",
	"shape-shifter-curse:form_snow_fox_2": "shape-shifter-curse:ocelot_2_sneak_idle",
	"shape-shifter-curse:form_snow_fox_3": "shape-shifter-curse:form_feral_common_sneak_idle",
	"shape-shifter-curse:form_feral_cat_sp": "shape-shifter-curse:form_feral_common_sneak_idle"
}


def getPower(powerID: str) -> dict[str, any]:
	NameSpace, Path = powerID.split(":", 1)
	PowerFilePath = f"../src/main/resources/data/{NameSpace}/powers/{Path}.json"
	if os.path.exists(PowerFilePath):
		with open(PowerFilePath, "rb") as f:
			return json.load(f)
	else:
		raise FileNotFoundError(f"PowerFilePath {PowerFilePath} not found")


def getOrigins(originID: str) -> dict[str, any]:
	NameSpace, Path = originID.split(":", 1)
	OriginFilePath = f"../src/main/resources/data/{NameSpace}/origins/{Path}.json"
	if os.path.exists(OriginFilePath):
		with open(OriginFilePath, "rb") as f:
			return json.load(f)
	else:
		raise FileNotFoundError(f"OriginFilePath {OriginFilePath} not found")


def getOriginsScale(originID: str) -> tuple[float, float]:
	Origins = getOrigins(originID)
	Powers = Origins["powers"]
	for PowerID in Powers:
		Power = getPower(PowerID)
		if Power["type"] == "shape-shifter-curse:scale":
			return Power.get("scale", 1.0), Power.get("eye_scale", 1.0)
	raise ValueError(f"Origin {originID} not found scale")


def getAllFormScale() -> dict[str, tuple[float, float]]:
	AllFormScale = {}
	for file in os.listdir("../src/main/resources/data/shape-shifter-curse/origins"):
		if file.endswith(".json"):
			OriginPath = file[:-5]
			OriginID = f"shape-shifter-curse:{OriginPath}"
			AllFormScale[OriginID] = getOriginsScale(OriginID)
	return AllFormScale


def getAnimation(animationID: str) -> dict[str, any]:
	NameSpace, Path = animationID.split(":", 1)
	AnimationFileFolder = f"../src/main/resources/assets/{NameSpace}/player_animation"
	for file in os.listdir(AnimationFileFolder):
		if file.endswith(".json"):
			AnimationFilePath = os.path.join(AnimationFileFolder, file)
			with open(AnimationFilePath, "rb") as f:
				Animation = json.load(f).get("animations", {})
				if Path in Animation:
					return Animation[Path]
	raise RuntimeError(f"Animation {animationID} not found")


def writeAnimation(animationData: dict[str, dict[str, any]], animationFilePath: str) -> None:
	# animationData -> { AnimPath: AnimData }
	AnimationFileData = {
		"format_version": "1.8.0",
		"animations": animationData
	}
	with open(animationFilePath, "w", encoding="utf-8") as f:
		json.dump(AnimationFileData, f, ensure_ascii=False, indent=None, separators=(',', ':'))
	print(f"Animation {animationFilePath} write success")


def getFormRideAnimation(originID: str) -> typing.Optional[str]:
	return FormRideAnimationMap.get(originID, None)


def generateRideAnimation(baseRideAnimation: dict[str, any], scale: tuple[float, float]) -> dict[str, any]:
	animationLength = baseRideAnimation.get("animation_length", -1.0)
	FixedY = max(((0.9 * (scale[0] * scale[1])) - 0.25) * 16, 1.0)
	for boneName in baseRideAnimation["bones"]:
		BoneData = baseRideAnimation["bones"][boneName]
		if "position" not in BoneData:
			if animationLength <= 0:
				BoneData["position"] = {
					"vector": [0, FixedY, 0]
				}
			else:
				BoneData["position"] = {
					"0.0": {
						"vector": [0, FixedY, 0]
					},
					str(animationLength): {
						"vector": [0, FixedY, 0]
					}
				}
		else:
			if animationLength <= 0:
				BoneData["position"]["vector"][1] += FixedY
			else:
				for time, data in BoneData["position"].items():
					data["vector"][1] += FixedY
	return baseRideAnimation


def generateAllRideAnimation() -> dict[str, dict[str, any]]:
	AllFormScale = getAllFormScale()
	FormRideAnimation = {}
	for originsID, animationID in FormRideAnimationMap.items():
		originsPath = originsID.split(":", 1)[1]
		FormRideAnimation[f"{originsPath.removeprefix('form_')}_riding"] = generateRideAnimation(
			getAnimation(animationID),
			AllFormScale[originsID]
		)
	return FormRideAnimation


if __name__ == "__main__":
	writeAnimation(generateAllRideAnimation(), "./form_riding_animation.json")