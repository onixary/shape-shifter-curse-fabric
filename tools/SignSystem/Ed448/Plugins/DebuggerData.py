#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)

import importlib.util
import os
import typing
from typing import TYPE_CHECKING
import base64
import time
import io
import json

if TYPE_CHECKING:
	import ScriptTypes
	import Const

if not TYPE_CHECKING:
	currentDir = os.path.dirname(os.path.abspath(__file__))
	parentDir = os.path.dirname(currentDir)
	moduleName = "ScriptTypes"
	moduleFilePath = os.path.join(parentDir, f"{moduleName}.py")
	spec = importlib.util.spec_from_file_location(moduleName, moduleFilePath)
	ScriptTypes = importlib.util.module_from_spec(spec)
	spec.loader.exec_module(ScriptTypes)
	moduleName = "Const"
	moduleFilePath = os.path.join(parentDir, f"{moduleName}.py")
	spec = importlib.util.spec_from_file_location(moduleName, moduleFilePath)
	Const = importlib.util.module_from_spec(spec)
	spec.loader.exec_module(Const)


def FormatDuration(Seconds: int) -> str:
	if Seconds == 0:
		return "0s"
	Years = Seconds // (365 * 24 * 3600)
	Seconds %= (365 * 24 * 3600)
	Days = Seconds // (24 * 3600)
	Seconds %= (24 * 3600)
	Hours = Seconds // 3600
	Seconds %= 3600
	Minutes = Seconds // 60
	Seconds %= 60
	Parts = []
	if Years:
		Parts.append(f"{Years}y")
	if Days:
		Parts.append(f"{Days}d")
	if Hours:
		Parts.append(f"{Hours}h")
	if Minutes:
		Parts.append(f"{Minutes}m")
	if Seconds:
		Parts.append(f"{Seconds}s")
	return "".join(Parts) if Parts else "0s"


class DebuggerDataSegment(ScriptTypes.SubDataSegment):
	UUID: bytes = None
	DebugLevel: int = 0
	Timestamp: int = 0
	ExpiresIn: int = 0

	@staticmethod
	def load(data: bytes) -> "DebuggerDataSegment":
		dataIO = io.BytesIO(data)
		segment = DebuggerDataSegment()
		segment.Type = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		segment.Version = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		length = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		if length != len(data):
			raise Exception("Data length is not Current")
		segment.UUID = dataIO.read(16)
		segment.DebugLevel = int.from_bytes(dataIO.read(2), Const.INT_BYTE_TYPE)
		segment.Timestamp = int.from_bytes(dataIO.read(8), Const.INT_BYTE_TYPE)
		segment.ExpiresIn = int.from_bytes(dataIO.read(8), Const.INT_BYTE_TYPE)
		return segment

	def save(self) -> bytes:
		dataIO = io.BytesIO()
		dataIO.write(self.Type.to_bytes(4, Const.INT_BYTE_TYPE))
		dataIO.write(self.Version.to_bytes(4, Const.INT_BYTE_TYPE))
		dataIO.write((0).to_bytes(4, Const.INT_BYTE_TYPE))  # 先用0填充 之后填充数据长度
		dataIO.write(self.UUID)
		dataIO.write(self.DebugLevel.to_bytes(2, Const.INT_BYTE_TYPE))
		dataIO.write(self.Timestamp.to_bytes(8, Const.INT_BYTE_TYPE))
		dataIO.write(self.ExpiresIn.to_bytes(8, Const.INT_BYTE_TYPE))
		length = dataIO.tell()
		dataIO.seek(8)
		dataIO.write(length.to_bytes(4, Const.INT_BYTE_TYPE))
		return dataIO.getvalue()

	@staticmethod
	def fromJson(jsonData: dict, Timestamp: typing.Optional[int] = None) -> "PatronDataSegment":
		Timestamp = int(time.time()) if Timestamp is None else Timestamp
		segment = DebuggerDataSegment()
		segment.Type = 2
		segment.Version = 0
		segment.UUID = bytes.fromhex(jsonData["UUID"])
		segment.DebugLevel = jsonData["DebugLevel"]
		segment.Timestamp = Timestamp
		segment.ExpiresIn = jsonData["ExpiresIn"]
		return segment

	def getReadableData(self) -> str | dict | list | None:
		return {
			"数据类型": self.Type,
			"数据版本": self.Version,
			"UUID": self.UUID.hex(),
			"调试等级": self.DebugLevel,
			"签发时间": time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(self.Timestamp)),
			"失效时间": time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(self.Timestamp + self.ExpiresIn)),
			"有效期": FormatDuration(self.ExpiresIn)
		}


def loadAllData() -> dict[str, list[ScriptTypes.SubDataSegment]]:
	DebuggerJsons = [
		os.path.join("./DebuggerData", f) for f in os.listdir("./DebuggerData")
		if f.endswith(".json") and os.path.isfile(os.path.join("./DebuggerData", f))
	]
	DebuggerData = {}
	for DebuggerFilePath in DebuggerJsons:
		with open(DebuggerFilePath, "r", encoding="utf-8") as f:
			DebuggerJson = json.load(f)
		try:
			dataSegment = DebuggerDataSegment.fromJson(DebuggerJson)
			fileName = dataSegment.UUID.hex().upper()
			dataList = DebuggerData.get(fileName, None)
			if dataList is None:
				dataList = []
			dataList.append(dataSegment)
			DebuggerData[fileName] = dataList
		except Exception as e:
			print(f"Error building patron data for {DebuggerFilePath}: {e}")
			continue
	return DebuggerData


def registerPlugin(dataSerializerRegister: ScriptTypes.dataDeserializerRegister, loaderRegister: ScriptTypes.loadAllDataFunctionRegister):
	dataSerializerRegister("Debugger Data Reader V0", (2, 0), lambda fileName, data: DebuggerDataSegment.load(data))
	loaderRegister("Debugger Data", loadAllData)