#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# Desc          : 没什么用的函数 比如验签什么的 一般不用在Python端验签

# 需要 pip install cryptography

from cryptography.hazmat.primitives.asymmetric import padding, rsa
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.backends import default_backend
import os
import time


def VerifySign(Data: bytes, PublicKey: bytes) -> bool:
	try:
		RootPublic = serialization.load_pem_public_key(PublicKey, backend=default_backend())
	except:
		RootPublic = serialization.load_der_public_key(PublicKey, backend=default_backend())

	if len(Data) < 4 + 1033 + 512:
		return False

	VersionBytes = Data[0:4]
	KeySegment = Data[4:4 + 1033]
	TypeBytes = KeySegment[0:4]
	VersionBytesKey = KeySegment[4:8]
	MeltdownByte = KeySegment[8:9]
	NBytes = KeySegment[9:9 + 512]
	KeySignature = KeySegment[9 + 512:9 + 512 + 512]

	SignedData = TypeBytes + VersionBytesKey + MeltdownByte + NBytes
	try:
		RootPublic.verify(KeySignature, SignedData, padding.PKCS1v15(), hashes.SHA256())
	except:
		return False

	NInt = int.from_bytes(NBytes, 'big')
	SubPublicNumbers = rsa.RSAPublicNumbers(e=65537, n=NInt)
	SubPublicKey = SubPublicNumbers.public_key(backend=default_backend())

	PatronStart = 4 + 1033
	PatronData = Data[PatronStart:-512]
	DataSignature = Data[-512:]

	try:
		SubPublicKey.verify(DataSignature, PatronData, padding.PKCS1v15(), hashes.SHA256())
		return True
	except:
		return False


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


def PrintSignedFileInfo(Data: bytes, PublicKey: bytes, Prefix: str = "") -> None:
	IsValid = VerifySign(Data, PublicKey)

	KeySegment = Data[4:4 + 1033]
	Type = int.from_bytes(KeySegment[0:4], 'big')
	Version = int.from_bytes(KeySegment[4:8], 'big')
	Meltdown = KeySegment[8:9][0] != 0

	PatronStart = 4 + 1033
	PatronData = Data[PatronStart:-512]
	DataLen = int.from_bytes(PatronData[0:4], 'big')
	UUIDBytes = PatronData[4:20]
	Timestamp = int.from_bytes(PatronData[20:28], 'big')
	ExpiresIn = int.from_bytes(PatronData[28:36], 'big')
	PermissionLevel = int.from_bytes(PatronData[36:40], 'big')
	ExtraCount = int.from_bytes(PatronData[40:44], 'big')
	UUIDHex = UUIDBytes.hex()
	TimestampStr = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(Timestamp))
	ExpireTime = Timestamp + ExpiresIn
	ExpireTimeStr = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(ExpireTime))
	DurationStr = FormatDuration(ExpiresIn)
	print(Prefix + "密钥类型:", Type)
	print(Prefix + "密钥版本:", Version)
	print(Prefix + "使用熔断机制:", Meltdown)
	print(Prefix + "玩家UUID:", UUIDHex)
	print(Prefix + "权限等级:", PermissionLevel)
	print(Prefix + "有效期:", DurationStr)
	print(Prefix + "签发时间戳:", TimestampStr)
	print(Prefix + "过期时间:", ExpireTimeStr)
	print(Prefix + "额外数据数量:", ExtraCount)
	Offset = 44
	for i in range(ExtraCount):
		if Offset + 4 > len(PatronData):
			break
		KeyLen = int.from_bytes(PatronData[Offset:Offset + 4], 'big')
		Offset += 4
		KeyBytes = PatronData[Offset:Offset + KeyLen]
		Offset += KeyLen
		if Offset + 4 > len(PatronData):
			break
		ValueLen = int.from_bytes(PatronData[Offset:Offset + 4], 'big')
		Offset += 4
		ValueBytes = PatronData[Offset:Offset + ValueLen]
		Offset += ValueLen
		try:
			KeyStr = KeyBytes.decode('utf-8')
		except UnicodeDecodeError:
			KeyStr = KeyBytes.hex()
		print(Prefix + f"\t额外数据[{i}]: key: {KeyStr}, value: 0x{ValueBytes.hex()}")
	print(Prefix + "文件有效:", IsValid)


def PrintAllSignedFileInfo(PublicKey: bytes) -> None:
	for FileName in os.listdir("./SignedPatronData"):
		if not FileName.endswith(".key"):
			continue
		FilePath = os.path.join("./SignedPatronData", FileName)
		with open(FilePath, "rb") as f:
			Data = f.read()
		print(f"File -> {os.path.basename(FilePath)}")
		PrintSignedFileInfo(Data, PublicKey, "\t")


if __name__ == "__main__":
	print("正确公钥:")
	with open("./ROOT_PUBLIC_KEY.pem", "rb") as f:
		PublicKey = f.read()
	PrintAllSignedFileInfo(PublicKey)
	print("错误公钥:")
	with open("./PublicKey2.pem", "rb") as f:
		PublicKey = f.read()
	PrintAllSignedFileInfo(PublicKey)
