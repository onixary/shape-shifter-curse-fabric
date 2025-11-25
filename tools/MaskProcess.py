#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# 需要安装的库: Pillow
# pip install Pillow

import typing
import os

from PIL import Image

RenderLayerCondition = typing.Callable[[int, int, int, int], bool]  # (R, G, B, A) => Bool
RenderLayerApply = typing.Callable[[], tuple[int, int, int, int]]  # () => (R, G, B, A)
RenderLayer = typing.NamedTuple("RenderLayer", [("ID", str), ("Condition", RenderLayerCondition), ("ApplyLayer", RenderLayerApply)])
RenderLayerList: list[RenderLayer] = []  # 越往后优先级越高
LayerColor = (255, 0, 0, 255)  # (R, G, B, A)
EmptyColor = (0, 0, 0, 255)  # (R, G, B, A)

RenderLayerList.append(RenderLayer("primaryColor", lambda R, G, B, A: R == 255, lambda: (255, 0, 0, 255)))
RenderLayerList.append(RenderLayer("accentColor1", lambda R, G, B, A: G == 255, lambda: (0, 255, 0, 255)))
RenderLayerList.append(RenderLayer("accentColor2", lambda R, G, B, A: B == 255, lambda: (0, 0, 255, 255)))
RenderLayerList.append(RenderLayer("eyeColorA", lambda R, G, B, A: A == 1, lambda: (0, 0, 0, 1)))
RenderLayerList.append(RenderLayer("eyeColorB", lambda R, G, B, A: A == 2, lambda: (0, 0, 0, 2)))


if not os.path.isdir("./LayerData"):
	os.makedirs("./LayerData")
if not os.path.isdir("./Image"):
	os.makedirs("./Image")


def ReadImage(Path: str) -> Image.Image:
	ImageData = Image.open(Path, "r", ["png"])
	if ImageData.mode != "RGBA":
		print("[ERROR] %s 的图片格式不是 RGBA" % Path)
		ImageData = ImageData.convert("RGBA")
	return ImageData


def GetLayerPath(ImageName: str, Layer: RenderLayer) -> str:
	ImageNameRaw = ImageName.removesuffix(".png")
	if not os.path.isdir("./LayerData/%s" % ImageNameRaw):
		os.makedirs("./LayerData/%s" % ImageNameRaw)
	return os.path.join("./LayerData/%s/" % ImageNameRaw, Layer.ID + ".png")


def GetImagePath(ImageName: str) -> str:
	return os.path.join("./Image/", ImageName)


def SplitLayer(ImageData: Image.Image, ImageName: str, Only1Layer: bool = True) -> None:
	Layers = RenderLayerList.copy()
	Layers.reverse()
	LayersData: dict[str, Image.Image] = {Layer.ID: Image.new("RGBA", ImageData.size, EmptyColor) for Layer in Layers}
	for X in range(ImageData.width):
		for Y in range(ImageData.height):
			Pixel = ImageData.getpixel((X, Y))
			for Layer in Layers:
				if Layer.Condition(*Pixel):
					LayersData[Layer.ID].putpixel((X, Y), LayerColor)
					if Only1Layer:
						break
	for Layer in Layers:
		LayersData[Layer.ID].save(GetLayerPath(ImageName, Layer))
	print("已处理 %s" % ImageName)


def MergeLayer(ImageName: str, ImageSize: tuple[int, int]) -> None:
	ResultImage = Image.new("RGBA", ImageSize, EmptyColor)
	Layers = RenderLayerList.copy()
	LayersData: dict[str, Image.Image] = {}
	for Layer in Layers:
		ImageData = Image.open(GetLayerPath(ImageName, Layer))
		LayersData[Layer.ID] = ImageData
	for X in range(ResultImage.width):
		for Y in range(ResultImage.height):
			for Layer in Layers:
				Pixel = LayersData[Layer.ID].getpixel((X, Y))
				if Pixel[0] == LayerColor[0] and Pixel[1] == LayerColor[1] and Pixel[2] == LayerColor[2]:
					ResultImage.putpixel((X, Y), Layer.ApplyLayer())
	ResultImage.save(GetImagePath(ImageName))
	print("已处理 %s" % ImageName)


def SplitLayerAll() -> None:
	for ImageName in os.listdir("./Image"):
		if ImageName.endswith(".png"):
			SplitLayer(ReadImage(GetImagePath(ImageName)), ImageName)


def MergeLayerAll() -> None:
	for ImageDirName in os.listdir("./LayerData"):
		if not os.path.isdir("./LayerData/%s" % ImageDirName):
			continue
		ImageName = ImageDirName + ".png"
		MergeLayer(ImageName, ReadImage(GetLayerPath(ImageName, RenderLayerList[0])).size)


def OpenConsoleMenu() -> None:
	Option = typing.NamedTuple("Option", [("Name", str), ("Function", typing.Callable[[], None])])
	Options: list[Option] = []
	Options.append(Option("分离Layer", SplitLayerAll))
	Options.append(Option("合并Layer", MergeLayerAll))
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
	OpenConsoleMenu()
