#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan

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

currentDir = os.path.dirname(os.path.abspath(__file__))
parentDir = os.path.dirname(currentDir)
moduleName = "ScriptTypes"  # 假设要导入的模块名
moduleFilePath = os.path.join(parentDir, f"{moduleName}.py")
spec = importlib.util.spec_from_file_location(moduleName, moduleFilePath)
if not TYPE_CHECKING:
	ScriptTypes = importlib.util.module_from_spec(spec)
	spec.loader.exec_module(ScriptTypes)


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


class PatronDataSegment(ScriptTypes.SubDataSegment):
	UUID: bytes = None
	PermissionLevel: int = 0
	Timestamp: int = 0
	ExpiresIn: int = 0
	ExtraData: dict[str, bytes] = None

	def __init__(self):
		self.ExtraData = {}

	@staticmethod
	def load(data: bytes) -> "PatronDataSegment":
		dataIO = io.BytesIO(data)
		segment = PatronDataSegment()
		segment.Type = int.from_bytes(dataIO.read(4), "big")
		segment.Version = int.from_bytes(dataIO.read(4), "big")
		length = int.from_bytes(dataIO.read(4), "big")
		if length != len(data):
			raise Exception("Data length is not Current")
		segment.UUID = dataIO.read(16)
		segment.PermissionLevel = int.from_bytes(dataIO.read(4), "big")
		segment.Timestamp = int.from_bytes(dataIO.read(8), "big")
		segment.ExpiresIn = int.from_bytes(dataIO.read(8), "big")
		extraDataCount = int.from_bytes(dataIO.read(4), "big")
		for i in range(extraDataCount):
			keyLength = int.from_bytes(dataIO.read(4), "big")
			key = dataIO.read(keyLength).decode("utf-8")
			valueLength = int.from_bytes(dataIO.read(4), "big")
			value = dataIO.read(valueLength)
			segment.ExtraData[key] = value
		return segment

	def save(self) -> bytes:
		dataIO = io.BytesIO()
		dataIO.write(self.Type.to_bytes(4, "big"))
		dataIO.write(self.Version.to_bytes(4, "big"))
		dataIO.write((0).to_bytes(4, "big"))  # 先用0填充 之后填充数据长度
		dataIO.write(self.UUID)
		dataIO.write(self.PermissionLevel.to_bytes(4, "big"))
		dataIO.write(self.Timestamp.to_bytes(8, "big"))
		dataIO.write(self.ExpiresIn.to_bytes(8, "big"))
		dataIO.write(len(self.ExtraData).to_bytes(4, "big"))
		for key, value in self.ExtraData.items():
			dataIO.write(len(key).to_bytes(4, "big"))
			dataIO.write(key.encode("utf-8"))
			dataIO.write(len(value).to_bytes(4, "big"))
			dataIO.write(value)
		length = dataIO.tell()
		dataIO.seek(8)
		dataIO.write(length.to_bytes(4, "big"))
		return dataIO.getvalue()

	@staticmethod
	def fromJson(jsonData: dict, Timestamp: typing.Optional[int] = None) -> "PatronDataSegment":
		Timestamp = int(time.time()) if Timestamp is None else Timestamp
		segment = PatronDataSegment()
		segment.Type = 1
		segment.Version = 0
		segment.UUID = bytes.fromhex(jsonData["UUID"])
		segment.PermissionLevel = jsonData["PermissionLevel"]
		segment.Timestamp = Timestamp
		segment.ExpiresIn = jsonData["ExpiresIn"]
		extraData = jsonData.get("ExtraData", {})
		for key, value in extraData.items():
			segment.ExtraData[key] = base64.b64decode(value)
		return segment

	def getReadableData(self) -> str | dict | list | None:
		return {
			"数据类型": self.Type,
			"数据版本": self.Version,
			"UUID": self.UUID.hex(),
			"权限等级": self.PermissionLevel,
			"签发时间": time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(self.Timestamp)),
			"失效时间": time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(self.Timestamp + self.ExpiresIn)),
			"有效期": FormatDuration(self.ExpiresIn),
			"额外数据": {
				key: f"0x{value.hex()}" for key, value in self.ExtraData.items()
			}
		}


def loadAllData() -> dict[str, list[ScriptTypes.SubDataSegment]]:
	PatronJson = [
		os.path.join("./PatronData", f) for f in os.listdir("./PatronData")
		if f.endswith(".json") and os.path.isfile(os.path.join("./PatronData", f))
	]
	PatronData = {}
	for PatronFilePath in PatronJson:
		with open(PatronFilePath, "r", encoding="utf-8") as f:
			PatronJson = json.load(f)
		try:
			dataSegment = PatronDataSegment.fromJson(PatronJson)
			fileName = dataSegment.UUID.hex().upper()
			dataList = PatronData.get(fileName, None)
			if dataList is None:
				dataList = []
			dataList.append(dataSegment)
			PatronData[fileName] = dataList
		except Exception as e:
			print(f"Error building patron data for {PatronFilePath}: {e}")
			continue
	return PatronData


def registerPlugin(dataSerializerRegister: ScriptTypes.dataDeserializerRegister, loaderRegister: ScriptTypes.loadAllDataFunctionRegister):
	dataSerializerRegister("Patron Data Reader V0", (1, 0), lambda fileName, data: PatronDataSegment.load(data))
	loaderRegister("Patron Data", loadAllData)