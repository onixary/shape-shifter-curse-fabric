#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan

import typing

RGBA = typing.NamedTuple("RGBA", [("r", int), ("g", int), ("b", int), ("a", int)])
POS = typing.NamedTuple("POS", [("x", int), ("y", int)])
PROGRAM_IMAGE_VARIABLE = dict[str, any]
MASK_CONDITION = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_APPLY = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_PROGRAM_LINE = typing.NamedTuple("MASK_PROGRAM_LINE", [("LineName", str), ("MaskCondition", str), ("MaskApply", str)])
MASK_PROGRAM = typing.NamedTuple("MASK_PROGRAM", [("ProgramVariable", typing.Callable[[], PROGRAM_IMAGE_VARIABLE]), ("Program", list[MASK_PROGRAM_LINE]), ("AllowSplit", bool), ("AllowMerge", bool)])
MaskProgramRegistry = tuple[dict[str, MASK_CONDITION], dict[str, MASK_APPLY], dict[str, MASK_PROGRAM]]

LayerColor = RGBA(255, 0, 0, 255)  # (R, G, B, A)


def IsNearPixel(Pos1: POS, Pos2: POS) -> bool:
	if Pos1.x == Pos2.x and Pos1.y == Pos2.y:
		return True
	elif Pos1.x == Pos2.x and abs(Pos1.y - Pos2.y) <= 1:
		return True
	elif Pos1.y == Pos2.y and abs(Pos1.x - Pos2.x) <= 1:
		return True
	else:
		return False


def EyeColorAFunc(Color: RGBA, PixelPos: POS, ProgramData: PROGRAM_IMAGE_VARIABLE) -> typing.Optional[RGBA]:
	if Color.a > 16:  # 我记得最早的版本<=16
		return None
	EyeData = ProgramData.get("EyeData", None)
	if EyeData is None:
		ProgramData["EyeData"] = {"EyeAPos": [], "HasEyeA": False}
		EyeData = ProgramData["EyeData"]
	if not EyeData["HasEyeA"]:
		EyeData["HasEyeA"] = True
		EyeData["EyeAPos"].append(PixelPos)
		return LayerColor
	if any(IsNearPixel(PixelPos, EyePos) for EyePos in EyeData["EyeAPos"]):
		EyeData["EyeAPos"].append(PixelPos)
		return LayerColor
	return None


def EyeColorBFunc(Color: RGBA, PixelPos: POS, ProgramData: PROGRAM_IMAGE_VARIABLE) -> typing.Optional[RGBA]:
	if Color.a > 16:  # 我记得最早的版本<=16
		return None
	EyeData = ProgramData.get("EyeData", None)
	if EyeData is None:
		ProgramData["EyeData"] = {"EyeAPos": [], "HasEyeA": False}
		EyeData = ProgramData["EyeData"]
	if EyeData["HasEyeA"]:
		if not any(IsNearPixel(PixelPos, EyePos) for EyePos in EyeData["EyeAPos"]):
			return LayerColor
	return None


eyeColorFixACondition = EyeColorAFunc
eyeColorFixBCondition = EyeColorBFunc


eyeFixProgram: MASK_PROGRAM = MASK_PROGRAM(
	lambda: {},
	[
		MASK_PROGRAM_LINE("primaryColor", "default:primaryColor", ""),
		MASK_PROGRAM_LINE("accentColor1", "default:accentColor1", ""),
		MASK_PROGRAM_LINE("accentColor2", "default:accentColor2", ""),
		MASK_PROGRAM_LINE("eyeColorA", "eyeFix:eyeColorFixA", ""),
		MASK_PROGRAM_LINE("eyeColorB", "eyeFix:eyeColorFixB", "")
	],
	True, False
)


def registryAllMaskProgram() -> MaskProgramRegistry:
	C, A, P = {}, {}, {}
	C["eyeFix:eyeColorFixA"] = eyeColorFixACondition
	C["eyeFix:eyeColorFixB"] = eyeColorFixBCondition
	P["eyeFix:eyeColorFix"] = eyeFixProgram
	return C, A, P
