#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)


import os
import importlib
import typing

import ScriptTypes


dataDeserializerRegistry: dict[int, dict[int, list[tuple[str, ScriptTypes.dataDeserializer]]]] = {}
dataDeserializerRegistryAllVersion: dict[int, list[tuple[str, ScriptTypes.dataDeserializer]]] = {}
loadAllDataFunctionRegistry: dict[str, ScriptTypes.loadAllDataFunction] = {}


def DataDeserializerRegister(DeserializerName: str, TVTuple: tuple[int, int], Deserializer: ScriptTypes.dataDeserializer):
	if TVTuple[1] == -1:
		if TVTuple[0] not in dataDeserializerRegistryAllVersion:
			dataDeserializerRegistryAllVersion[TVTuple[0]] = []
		dataDeserializerRegistryAllVersion[TVTuple[0]].append((DeserializerName, Deserializer))
	else:
		if TVTuple[0] not in dataDeserializerRegistry:
			dataDeserializerRegistry[TVTuple[0]] = {}
		if TVTuple[1] not in dataDeserializerRegistry[TVTuple[0]]:
			dataDeserializerRegistry[TVTuple[0]][TVTuple[1]] = []
		dataDeserializerRegistry[TVTuple[0]][TVTuple[1]].append((DeserializerName, Deserializer))


def LoadAllDataFunctionRegister(LoaderName: str, Loader: ScriptTypes.loadAllDataFunction):
	loadAllDataFunctionRegistry[LoaderName] = Loader


def loadPlugins(pluginName: str) -> bool:
	try:
		module = importlib.import_module(f"Plugins.{pluginName}")
		module.registerPlugin(DataDeserializerRegister, LoadAllDataFunctionRegister)
		return True
	except Exception as e:
		print(f"加载插件 {pluginName} 失败")
		print(e)
		return False


def loadAllPlugins() -> None:
	for pluginName in os.listdir("Plugins"):
		if pluginName.endswith(".py"):
			loadPlugins(pluginName[:-3])


def getDataDeserializer(Type: int, Version: int) -> typing.Optional[ScriptTypes.dataDeserializer]:
	if Type in dataDeserializerRegistry and Version in dataDeserializerRegistry[Type]:
		return dataDeserializerRegistry[Type][Version][0][1]
	if Type in dataDeserializerRegistryAllVersion:
		return dataDeserializerRegistryAllVersion[Type][0][1]
	return None


def readSubSegment(fileName: str, Type: int, Version: int, data: bytes) -> typing.Optional[ScriptTypes.SubDataSegment]:
	dataDeserializer = getDataDeserializer(Type, Version)
	if dataDeserializer:
		return dataDeserializer(fileName, data)
	return None


def getLoadAllDataFunction() -> dict[str, ScriptTypes.loadAllDataFunction]:
	return loadAllDataFunctionRegistry
