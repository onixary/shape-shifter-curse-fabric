#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# 需要安装的库: Pillow
# pip install Pillow

import os
import typing
import importlib
from PIL import Image

RGBA = typing.NamedTuple("RGBA", [("r", int), ("g", int), ("b", int), ("a", int)])
POS = typing.NamedTuple("POS", [("x", int), ("y", int)])
PROGRAM_IMAGE_VARIABLE = dict[str, any]
MASK_CONDITION = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_APPLY = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_PROGRAM_LINE = typing.NamedTuple("MASK_PROGRAM_LINE", [("LineName", str), ("MaskCondition", str), ("MaskApply", str)])
MASK_PROGRAM = typing.NamedTuple("MASK_PROGRAM", [("ProgramVariable", typing.Callable[[], PROGRAM_IMAGE_VARIABLE]), ("Program", list[MASK_PROGRAM_LINE]), ("AllowSplit", bool), ("AllowMerge", bool)])
MaskProgramRegistry = tuple[dict[str, MASK_CONDITION], dict[str, MASK_APPLY], dict[str, MASK_PROGRAM]]

MaskConditionRegistry: dict[str, MASK_CONDITION] = {}
MaskApplyRegistry: dict[str, MASK_APPLY] = {}
AllMaskProgramRegistry: dict[str, MASK_PROGRAM] = {}

LayerEmptyColor = RGBA(0, 0, 0, 0)  # (R, G, B, A)
MaskEmptyColor = RGBA(0, 0, 0, 255)  # (R, G, B, A)


def checkFolder() -> None:
	if not os.path.isdir("./LayerData"):
		os.makedirs("./LayerData")
	if not os.path.isdir("./Image"):
		os.makedirs("./Image")


def loadPlugin(pluginFile: str) -> MaskProgramRegistry:
	try:
		plugin = importlib.import_module(pluginFile)
		return plugin.registryAllMaskProgram()
	except Exception as e:
		print(f"Error loading plugin {pluginFile}: {e}")
		return {}, {}, {}


def loadAllPlugins() -> None:
	allPluginFiles = [file for file in os.listdir("Plugins") if file.endswith(".py")]
	allPluginRegistry = [loadPlugin(f"Plugins.{file[:-3]}") for file in allPluginFiles]
	for pluginRegistry in allPluginRegistry:
		MaskConditionRegistry.update(pluginRegistry[0])
		MaskApplyRegistry.update(pluginRegistry[1])
		AllMaskProgramRegistry.update(pluginRegistry[2])
	for MaskProgram in AllMaskProgramRegistry.values():
		for P_ID, C_ID, A_ID in MaskProgram.Program:
			if MaskProgram.AllowSplit and MaskConditionRegistry.get(C_ID, None) is None:
				print(f"Mask condition |{C_ID}| not found")
				return None
			if MaskProgram.AllowMerge and MaskApplyRegistry.get(A_ID, None) is None:
				print(f"Mask apply |{A_ID}| not found")
				return None


def readImage(Path: str) -> Image.Image:
	ImageData = Image.open(Path, "r", ["png"])
	if ImageData.mode != "RGBA":
		print("[ERROR] %s 的图片格式不是 RGBA" % Path)
		ImageData = ImageData.convert("RGBA")
	return ImageData


def getLayerPath(ImageName: str, ProgramLineName: str) -> str:
	ImageNameRaw = ImageName.removesuffix(".png")
	if not os.path.isdir("./LayerData/%s" % ImageNameRaw):
		os.makedirs("./LayerData/%s" % ImageNameRaw)
	return os.path.join("./LayerData/%s/" % ImageNameRaw, ProgramLineName + ".png")


def getImagePath(ImageName: str) -> str:
	return os.path.join("./Image/", ImageName + ".png")


def executeMaskCondition(Color: RGBA | tuple[int, int, int, int], PixelPos: POS, MaskCondition: MASK_CONDITION, ProgramData: PROGRAM_IMAGE_VARIABLE) -> typing.Optional[RGBA]:
	if not isinstance(Color, RGBA):
		Color = RGBA(*Color)
	return MaskCondition(Color, PixelPos, ProgramData)


def executeMaskApply(Color: RGBA, PixelPos: POS, MaskApply: MASK_APPLY, ProgramData: PROGRAM_IMAGE_VARIABLE) -> typing.Optional[RGBA]:
	if not isinstance(Color, RGBA):
		Color = RGBA(*Color)
	return MaskApply(Color, PixelPos, ProgramData)


def executeMaskProgram_SplitLayer(imageData: Image.Image, MaskProgramID: str) -> typing.Optional[dict[str, Image.Image]]:
	if MaskProgramID not in AllMaskProgramRegistry:
		print(f"Mask program {MaskProgramID} not found")
		return None
	MaskProgram = AllMaskProgramRegistry[MaskProgramID]
	if not MaskProgram.AllowSplit:
		print(f"Mask program {MaskProgramID} does not allow splitting")
		return None
	MaskProgramList = MaskProgram.Program.copy()
	MaskProgramList.reverse()
	ProgramVariable = MaskProgram.ProgramVariable()
	LayersData: dict[str, Image.Image] = {Program.LineName: Image.new("RGBA", imageData.size, LayerEmptyColor) for Program in MaskProgramList}

	for y in range(imageData.height):
		for x in range(imageData.width):
			ImageColor = imageData.getpixel((x, y))
			if len(ImageColor) != 4:
				print(f"Invalid color format {ImageColor}")
				return None
			for P_ID, C_ID, _ in MaskProgramList:
				Color = executeMaskCondition(ImageColor, POS(x, y), MaskConditionRegistry[C_ID], ProgramVariable)
				if Color is not None:
					LayersData[P_ID].putpixel((x, y), Color)
					break
	return LayersData


def readAllNeedLayersData(ImageName: str, MaskProgramID: str) -> typing.Optional[dict[str, Image.Image]]:
	LayersData = {}
	for P_ID, _, _ in AllMaskProgramRegistry[MaskProgramID].Program:
		LayerPath = getLayerPath(ImageName, P_ID)
		if not os.path.exists(LayerPath):
			print(f"Layer {LayerPath} not found")
			return None
		LayersData[P_ID] = readImage(LayerPath)
	return LayersData


def executeMaskProgram_MergeLayer(ImageName: str, LayersData: typing.Optional[dict[str, Image.Image]], MaskProgramID: str) -> typing.Optional[Image.Image]:
	if LayersData is None:
		LayersData = readAllNeedLayersData(ImageName, MaskProgramID)
	if LayersData is None:
		print(f"Error reading layers data for {ImageName}")
		return None
	MaskProgram = AllMaskProgramRegistry[MaskProgramID]
	if not MaskProgram.AllowMerge:
		print(f"Mask program {MaskProgramID} does not allow merging")
		return None
	MaskProgramList = MaskProgram.Program.copy()
	MaskProgramList.reverse()
	ProgramVariable = MaskProgram.ProgramVariable()
	ResultImage = Image.new("RGBA", LayersData.get(MaskProgramList[0].LineName).size, MaskEmptyColor)
	for y in range(ResultImage.height):
		for x in range(ResultImage.width):
			Color = None
			for P_ID, _, A_ID in MaskProgramList:
				Color = LayersData[P_ID].getpixel((x, y))
				Color = executeMaskApply(Color, POS(x, y), MaskApplyRegistry[A_ID], ProgramVariable)
				if Color is not None:
					break
			if Color is not None:
				ResultImage.putpixel((x, y), Color)
	return ResultImage


def splitLayer(ImageName: str, MaskProgramID: str) -> bool:
	try:
		ImageData = readImage(getImagePath(ImageName))
		for LayerName, LayersData in executeMaskProgram_SplitLayer(ImageData, MaskProgramID).items():
			LayersData.save(getLayerPath(ImageName, LayerName))
		return True
	except Exception as e:
		print(f"Error splitting layers for {ImageName}: {e}")
		return False


def selectMaskProgram(CanSplit: bool = True, CanMerge: bool = True) -> typing.Optional[str]:
	CanSelectableMaskProgram = [MaskProgram for MaskProgram in AllMaskProgramRegistry.keys() if (AllMaskProgramRegistry[MaskProgram].AllowSplit or not CanSplit) and (AllMaskProgramRegistry[MaskProgram].AllowMerge or not CanMerge)]
	for Index, MaskProgram in enumerate(CanSelectableMaskProgram):
		print("[%d]: %s" % (Index, MaskProgram))
	Select = int(input("请选择: "))
	if Select < len(AllMaskProgramRegistry):
		return list(AllMaskProgramRegistry.keys())[Select]
	else:
		return None


def splitLayerAll_Console() -> None:
	MaskProgramID = selectMaskProgram(True, False)
	if MaskProgramID is None:
		return
	SuccessCount = 0
	FailCount = 0
	for ImageName in os.listdir("./Image"):
		if ImageName.endswith(".png"):
			if splitLayer(ImageName.removesuffix(".png"), MaskProgramID):
				print(f"[√]分离成功 {ImageName}")
				SuccessCount += 1
			else:
				print(f"[X]分离失败 {ImageName}")
				FailCount += 1
	TotalCount = SuccessCount + FailCount
	print("\n成功%d/%d, 失败%d/%d" % (SuccessCount, TotalCount, FailCount, TotalCount))
	return None


def mergeLayer(ImageName: str, MaskProgramID: str) -> bool:
	try:
		ImageData = executeMaskProgram_MergeLayer(ImageName, None, MaskProgramID)
		ImageData.save(getImagePath(ImageName))
		return True
	except Exception as e:
		print(f"Error merging layers for {ImageName}: {e}")
		return False


def mergeLayerAll_Console() -> None:
	MaskProgramID = selectMaskProgram(False, True)
	if MaskProgramID is None:
		return
	SuccessCount = 0
	FailCount = 0
	for ImageName in os.listdir("./LayerData"):
		if os.path.isdir("./LayerData/%s" % ImageName):
			if mergeLayer(ImageName, MaskProgramID):
				print(f"[√]合并成功 {ImageName}")
				SuccessCount += 1
			else:
				print(f"[X]合并失败 {ImageName}")
				FailCount += 1
	TotalCount = SuccessCount + FailCount
	print("\n成功%d/%d, 失败%d/%d" % (SuccessCount, TotalCount, FailCount, TotalCount))


def OpenConsoleMenu() -> None:
	checkFolder()
	Option = typing.NamedTuple("Option", [("Name", str), ("Function", typing.Callable[[], None])])
	Options: list[Option] = []
	Options.append(Option("分离Layer", splitLayerAll_Console))
	Options.append(Option("合并Layer", mergeLayerAll_Console))
	Options.append(Option("退出", lambda: exit()))
	while True:
		print("-" * 20)
		for Index, Option in enumerate(Options):
			print("[%d]: %s" % (Index, Option.Name))
		Select = int(input("请选择: "))
		if Select < len(Options):
			Options[Select].Function()
		print("\n\n\n\n")


if __name__ == "__main__":
	loadAllPlugins()
	OpenConsoleMenu()
