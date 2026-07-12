#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)

import ScriptTypes
import PluginUtils
import Init
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import ed448
from cryptography.hazmat.primitives import serialization
import typing
import os


ROOT_KEY_PUBLIC_KEY_PATH = "./ROOT_KEY/ROOT_PUBLIC_KEY.pem"
ROOT_KEY_PRIVATE_KEY_PATH = "./ROOT_KEY/ROOT_PRIVATE_KEY.pem"


def createKeyPair() -> tuple[ed448.Ed448PublicKey, ed448.Ed448PrivateKey]:
	privateKey = ed448.Ed448PrivateKey.generate()
	publicKey = privateKey.public_key()
	return publicKey, privateKey


def createSubKey(RootPrivateKey: ed448.Ed448PrivateKey, Type: int, Version: int, UseMeltDown: bool, AcceptedDataType: list[int]) -> bytes:
	publicKey, privateKey = createKeyPair()
	publicKeySegment = ScriptTypes.SubKeyPublicSegment()
	publicKeySegment.PublicKey = publicKey
	publicKeySegment.KeyType = Type
	publicKeySegment.KeyVersion = Version
	publicKeySegment.MeltDown = UseMeltDown
	publicKeySegment.AcceptedDataType = AcceptedDataType
	keySegment = ScriptTypes.SubKeySegment()
	keySegment.PrivateKey = privateKey
	keySegment.PublicKeySegment = publicKeySegment
	return keySegment.save(None, RootPrivateKey)


def loadRootKey() -> tuple[typing.Optional[ed448.Ed448PublicKey], typing.Optional[ed448.Ed448PrivateKey]]:
	publicKey, privateKey = None, None
	if os.path.exists(ROOT_KEY_PUBLIC_KEY_PATH):
		with open(ROOT_KEY_PUBLIC_KEY_PATH, "rb") as f:
			publicKey = serialization.load_pem_public_key(f.read(), backend=default_backend())
	if os.path.exists(ROOT_KEY_PRIVATE_KEY_PATH):
		with open(ROOT_KEY_PRIVATE_KEY_PATH, "rb") as f:
			privateKey = serialization.load_pem_private_key(f.read(), password=None, backend=default_backend())
	return publicKey, privateKey


def loadSubKeySegment(subKeyType: int, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None) -> typing.Optional[ScriptTypes.SubKeySegment]:
	if not os.path.isfile(f"./SUB_KEY/{subKeyType}.sub_key"):
		return None
	with open(f"./SUB_KEY/{subKeyType}.sub_key", "rb") as f:
		return ScriptTypes.SubKeySegment.load(f.read(), rootPublicKey)
	return None


def isKeySegmentValid(keySegment: ScriptTypes.SubKeyPublicSegment) -> bool:
	publicKey, _ = loadRootKey()
	try:
		keySegment.save(publicKey, None)
		return True
	except Exception as e:
		return False


def isDataSegmentValid(dataSegment: ScriptTypes.IDataSegment, keySegment: ScriptTypes.SubKeyPublicSegment, dataSignature: bytes) -> bool:
	publicKey = keySegment.PublicKey
	try:
		publicKey.verify(dataSignature, dataSegment.save())
		return True
	except Exception as e:
		return False


def printAuthFileData(fileName: str, authFile: ScriptTypes.AuthFile):
	print(f"离线认证文件 - {fileName}")
	print(f"\t密钥段:")
	print(f"\t\t密钥类型: {authFile.KeySegment.KeyType}")
	print(f"\t\t密钥版本: {authFile.KeySegment.KeyVersion}")
	print(f"\t\t是否使用熔断机制: {authFile.KeySegment.MeltDown}")
	print(f"\t\t可接受数据类型: {authFile.KeySegment.AcceptedDataType}")
	print(f"\t\t是否验证成功: {isKeySegmentValid(authFile.KeySegment)}")
	print(f"\t数据段:")
	print(f"\t\t数据段数量: {len(authFile.DataSegments.SubSegments)}")
	nowIndex = 0
	for subSegment in authFile.DataSegments.SubSegments:
		print(f"\t\t数据段[{nowIndex}]:")
		print(f"\t\t\t数据段类型: {subSegment.Type}")
		print(f"\t\t\t数据段版本: {subSegment.Version}")
		print(f"\t\t\t数据段内容: {subSegment.getReadableData()}")
		nowIndex += 1
	print(f"\t是否验证成功: {isDataSegmentValid(authFile.DataSegments, authFile.KeySegment, authFile.DataSignature)}")


def SignAllData(subKeySegment: ScriptTypes.SubKeySegment):
	publicKey, privateKey = loadRootKey()
	subSegmentMap: dict[str, list[ScriptTypes.SubDataSegment]] = {}
	for ADFName, ADF in PluginUtils.getLoadAllDataFunction().items():
		try:
			result: dict[str, list[ScriptTypes.SubDataSegment]] = ADF()
			for key, value in result.items():
				if key not in subSegmentMap:
					subSegmentMap[key] = []
				for subSegment in value:
					subSegmentMap[key].append(subSegment)
		except Exception as e:
			print(f"插件 {ADFName} 获取数据失败")
	if not os.path.isdir("./AUTH_FILE"):
		os.mkdir("./AUTH_FILE")
	for authFileName, subSegments in subSegmentMap.items():
		authFile = ScriptTypes.AuthFile()
		authFile.Version = 0
		authFile.KeySegment = subKeySegment.PublicKeySegment
		dataSegment = ScriptTypes.IDataSegment()
		subSegmentsList = []
		for subSegment in subSegments:
			if (subSegment.Type in subKeySegment.PublicKeySegment.AcceptedDataType) or (-1 in subKeySegment.PublicKeySegment.AcceptedDataType):
				subSegmentsList.append(subSegment)
			else:
				print(f"数据段 {subSegment.Type} 未被当前子密钥允许")
				print(f"数据段内容: {subSegment.getReadableData()}")
		if len(subSegmentsList) == 0:
			print(f"文件 {authFileName} 没有数据段被当前子密钥允许 跳过此文件")
			continue
		dataSegment.SubSegments = subSegmentsList
		authFile.DataSegments = dataSegment
		authFileBytes = authFile.save(subKeySegment, publicKey, privateKey)
		with open(f"./AUTH_FILE/{authFileName}.auth", "wb") as f:
			f.write(authFileBytes)

