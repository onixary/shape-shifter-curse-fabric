#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)

from cryptography.hazmat.primitives.asymmetric import ed448
import typing
import os

import Init
import ScriptTypes
import Utils


def SubKeyUI(publicKey: typing.Optional[ed448.Ed448PublicKey], privateKey: typing.Optional[ed448.Ed448PrivateKey]):
	print("-- 创建子密钥 --")
	if (privateKey is None):
		print("无[根私钥] 无法签发子密钥")
		return
	Type = int(input("请输入密钥类型: "))
	Version = int(input("请输入密钥版本: "))
	UseMeltDown = input("是否使用熔断机制(Y/N): ").upper() != "N"
	AcceptedDataType = [int(i) for i in input("请输入可接受数据类型(多个数据类型请用空格隔开): ").split(" ")]
	try:
		subKeyBytes = Utils.createSubKey(privateKey, Type, Version, UseMeltDown, AcceptedDataType)
	except:
		print("创建子密钥失败")
		return
	if not os.path.isdir("./SUB_KEY"):
		os.mkdir("./SUB_KEY")
	with open(f"./SUB_KEY/{Type}.sub_key", "wb") as f:
		f.write(subKeyBytes)
	print(f"已创建子密钥: {Type}.sub_key")
	print("\n")


def SelectFile(folderPath: str, fileCondition: typing.Callable[[str], bool]) -> typing.Optional[str]:
	fileList = []
	for fileName in os.listdir(folderPath):
		if fileCondition(fileName):
			fileList.append(fileName)
	for i in range(len(fileList)):
		print(f"\t[{i}]> {fileList[i]}")
	select = input("请选择文件: ")
	if select.isdigit() and int(select) < len(fileList):
		return os.path.join(folderPath, fileList[int(select)])
	return None


def printAuthFileData(fileName: str, authFileBytes: bytes, publicKey: typing.Optional[ed448.Ed448PublicKey] = None):
	try:
		Utils.printAuthFileData(fileName, ScriptTypes.AuthFile.load(fileName, authFileBytes, publicKey))
	except Exception as e:
		print("验证文件读取失败")
		raise e


def ConsoleUI():
	publicKey, privateKey = Utils.loadRootKey()
	print(f"根密钥状态: 根公钥[{'√' if publicKey is not None else 'X'}] 根私钥[{'√' if privateKey is not None else 'X'}]")
	while True:
		print("离线验证文件签发系统")
		print("\t[0]> 创建子密钥")
		print("\t[1]> 签发所有数据")
		print("\t[2]> 读取指定验证文件")
		print("\t[3]> 读取所有验证文件")
		print("\t[4]> 退出")
		choice = int(input("请输入你的选择: "))
		match choice:
			case 0:
				SubKeyUI(publicKey, privateKey)
			case 1:
				print("-- 签发所有数据 --")
				subKeyType = int(input("请输入子密钥类型: "))
				try:
					subKey = Utils.loadSubKeySegment(subKeyType, publicKey)
				except Exception as e:
					print("加载子密钥失败")
					continue
				if subKey is None:
					print("未找到子密钥")
					continue
				try:
					Utils.SignAllData(subKey)
					print("签发数据成功")
				except Exception as e:
					print("签发数据失败")
					continue
				print("\n")
			case 2:
				print("-- 读取指定验证文件 --")
				filePath = SelectFile("./AUTH_FILE", lambda x: x.endswith(".auth"))
				if filePath is None:
					print("未选择文件")
					continue
				with open(filePath, "rb") as f:
					data = f.read()
				printAuthFileData(filePath, data, publicKey)
				print("\n")
			case 3:
				print("-- 读取所有验证文件 --")
				for fileName in os.listdir("./AUTH_FILE"):
					if not fileName.endswith(".auth"):
						continue
					FilePath = os.path.join("./AUTH_FILE", fileName)
					with open(FilePath, "rb") as f:
						Data = f.read()
					printAuthFileData(fileName, Data, publicKey)
				print("\n")
			case 4:
				break
			case _:
				print("无效选择")



if __name__ == "__main__":
	ConsoleUI()