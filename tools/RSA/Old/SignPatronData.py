#!/usr/bin/env python
# -*- coding: UTF-8 -*-
# Author        : XuHaoNan
# Desc          : 签名赞助者数据

# 需要 pip install cryptography

import os
import time
from typing import Any
import struct
import base64
import json

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes, serialization


def BuildPatronDataBytes(PatronJson: dict[str, Any], Timestamp: int | None = None) -> tuple[str, bytes]:
	Timestamp = int(time.time()) if Timestamp is None else Timestamp
	UUIDString = PatronJson["UUID"]
	UUIDBytes = bytes.fromhex(UUIDString)
	ExpiresIn = PatronJson["ExpiresIn"]
	PermissionLevel = PatronJson["PermissionLevel"]
	ExtraData = PatronJson["ExtraData"]
	ExtraDataItems = []
	for Key, ValueB64 in ExtraData.items():
		KeyBytes = Key.encode("utf-8")
		ValueBytes = base64.b64decode(ValueB64)
		ExtraDataItems.append((KeyBytes, ValueBytes))
	ExtraDataCount = len(ExtraDataItems)
	Content = bytearray()
	Content.extend(UUIDBytes)
	Content.extend(struct.pack(">Q", Timestamp))
	Content.extend(struct.pack(">Q", ExpiresIn))
	Content.extend(struct.pack(">i", PermissionLevel))
	Content.extend(struct.pack(">I", ExtraDataCount))
	for KeyBytes, ValueBytes in ExtraDataItems:
		Content.extend(struct.pack(">I", len(KeyBytes)))
		Content.extend(KeyBytes)
		Content.extend(struct.pack(">I", len(ValueBytes)))
		Content.extend(ValueBytes)
	DataLength = len(Content)
	if DataLength > 16 * 512:
		raise ValueError(f"Data length {DataLength} exceeds maximum 8192 bytes")
	Result = struct.pack(">I", DataLength) + bytes(Content)
	return UUIDString.upper(), Result


# ./PatronData
def ListPatronData() -> list[bytes]:
	PatronJson = [
		os.path.join("./PatronData", f) for f in os.listdir("./PatronData")
		if f.endswith(".json") and os.path.isfile(os.path.join("./PatronData", f))
	]
	PatronData = []
	for PatronFilePath in PatronJson:
		with open(PatronFilePath, "r", encoding="utf-8") as f:
			PatronJson = json.load(f)
		try:
			PatronData.append(BuildPatronDataBytes(PatronJson))
		except Exception as e:
			print(f"Error building patron data for {PatronFilePath}: {e}")
			continue
	return PatronData


# ./SubKeys
def ListSubKeys() -> dict[str, str]:
	SubKeys = [
		os.path.join("./SubKeys", f) for f in os.listdir("./SubKeys")
		if f.endswith(".bin") and os.path.isfile(os.path.join("./SubKeys", f))
	]
	Result = {os.path.basename(FilePath).removesuffix(".bin"): FilePath for FilePath in SubKeys}
	return Result


def ReadKey(FilePath: str) -> tuple[bytes, rsa.RSAPublicKey, rsa.RSAPrivateKey]:
	with open(FilePath, "rb") as f:
		Data = f.read()
	if len(Data) < 4:
		raise ValueError("File too small")
	SksSize = int.from_bytes(Data[:4], 'big')
	if len(Data) < 4 + SksSize + 4:
		raise ValueError("File too small for segment")
	SksStart = 4
	SksEnd = SksStart + SksSize
	SubKeySegment = Data[SksStart:SksEnd]

	if len(SubKeySegment) < 4 + 4 + 1 + 512 + 512:
		raise ValueError("SubKeySegment too short")
	TypeBytes = SubKeySegment[0:4]
	VersionBytes = SubKeySegment[4:8]
	MeltdownByte = SubKeySegment[8:9]
	NBytes = SubKeySegment[9:9 + 512]
	Signature = SubKeySegment[9 + 512:9 + 512 + 512]
	SpkStart = SksEnd
	if len(Data) < SpkStart + 4:
		raise ValueError("Missing SPK size")
	SpkSize = int.from_bytes(Data[SpkStart:SpkStart + 4], 'big')
	SpkEnd = SpkStart + 4 + SpkSize
	if len(Data) < SpkEnd:
		raise ValueError("Missing private key bytes")
	PrivateDer = Data[SpkStart + 4:SpkEnd]

	NInt = int.from_bytes(NBytes, 'big')
	PublicNumbers = rsa.RSAPublicNumbers(e=65537, n=NInt)
	PublicKey = PublicNumbers.public_key(backend=default_backend())
	PrivateKey = serialization.load_der_private_key(PrivateDer, password=None, backend=default_backend())
	return SubKeySegment, PublicKey, PrivateKey


def SignPatronData(PatronData: bytes, SubKey: tuple[bytes, rsa.RSAPublicKey, rsa.RSAPrivateKey]) -> bytes:
	KeySegment = SubKey[0]
	PrivateKey = SubKey[2]
	DataSignature = PrivateKey.sign(
		PatronData,
		padding.PKCS1v15(),
		hashes.SHA256()
	)
	if len(DataSignature) != 512:
		raise ValueError("Data signature length invalid")
	VersionBytes = (0).to_bytes(4, 'big')
	return VersionBytes + KeySegment + PatronData + DataSignature


def SignAllPatronData(SubKeys: tuple[bytes, rsa.RSAPublicKey, rsa.RSAPrivateKey]) -> None:
	if not os.path.exists("./SignedPatronData"):
		os.mkdir("./SignedPatronData")
	for UUID, PatronData in ListPatronData():
		try:
			SignedPatronData = SignPatronData(PatronData, SubKeys)
			print(f"Signed {UUID}")
		except Exception as e:
			print(f"Error signing {UUID}: {e}")
			continue
		with open(f"SignedPatronData/{UUID}.key", "wb") as f:
			f.write(SignedPatronData)


def PrintKeyInfo(Key: tuple[bytes, rsa.RSAPublicKey, rsa.RSAPrivateKey]):
	SubKeySegment = Key[0]
	TypeBytes = SubKeySegment[0:4]
	VersionBytes = SubKeySegment[4:8]
	MeltdownByte = SubKeySegment[8:9]
	Type = int.from_bytes(TypeBytes, 'big')
	Version = int.from_bytes(VersionBytes, 'big')
	Meltdown = MeltdownByte[0] != 0
	print(f"密钥类型: {Type}")
	print(f"密钥版本: {Version}")
	print(f"使用熔断机制: {Meltdown}")


def ConsoleUI() -> None:
	Keys = ListSubKeys()
	KeyList = []
	NowKeyIndex = 0
	while True:
		print("可用子密钥:")
		for KeyName, KeyPath in Keys.items():
			print(f"{NowKeyIndex}> {KeyName}")
			KeyList.append(KeyPath)
		Select = int(input("选择密钥: "))
		if Select >= len(KeyList):
			print("无效选择")
			continue
		TargetKeyPath = KeyList[Select]
		KeyData = ReadKey(TargetKeyPath)
		print("密钥信息:")
		PrintKeyInfo(KeyData)
		if input("是否继续? (Y/N): ") == "Y":
			break
	SignAllPatronData(KeyData)


if __name__ == "__main__":
	ConsoleUI()