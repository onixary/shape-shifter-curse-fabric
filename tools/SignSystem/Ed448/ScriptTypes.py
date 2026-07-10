# !/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)

import os
import typing
import io
import abc
from cryptography.hazmat.primitives.asymmetric import ed448
from cryptography.hazmat.primitives import serialization
import Const


class SubKeyPublicSegment:
	PublicKey: ed448.Ed448PublicKey = None
	KeyType: int = -1
	KeyVersion: int = -1
	MeltDown: bool = True
	AcceptedDataType: list[int] = None
	Signature: bytes = None

	def __init__(self):
		self.AcceptedDataType = []

	@staticmethod
	def load(data: bytes, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None) -> "SubKeyPublicSegment":
		segment: SubKeyPublicSegment = SubKeyPublicSegment()
		dataIO: io.BytesIO = io.BytesIO(data)
		dataLength = int.from_bytes(dataIO.read(4), "little")
		if dataLength != len(data):
			raise Exception(f"Data length is not Current {len(data)} != {dataLength}")
		segment.KeyType = int.from_bytes(dataIO.read(4), "little")
		segment.KeyVersion = int.from_bytes(dataIO.read(4), "little")
		segment.MeltDown = bool(int.from_bytes(dataIO.read(1), "little"))
		AcceptedDataTypeCount = int.from_bytes(dataIO.read(4), "little")
		for i in range(AcceptedDataTypeCount):
			segment.AcceptedDataType.append(int.from_bytes(dataIO.read(4), "little"))
		segment.PublicKey = ed448.Ed448PublicKey.from_public_bytes(dataIO.read(57))
		signedEndIndex = dataIO.tell()
		segment.Signature = dataIO.read(114)
		if rootPublicKey is not None:
			dataIO.seek(0)
			signedData = dataIO.read(signedEndIndex)
			rootPublicKey.verify(segment.Signature, signedData)
		return segment

	def save(self, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None, rootPrivateKey: typing.Optional[ed448.Ed448PrivateKey] = None) -> bytes:
		dataIO: io.BytesIO = io.BytesIO()
		dataIO.write(self.KeyType.to_bytes(4, "little"))
		dataIO.write(self.KeyVersion.to_bytes(4, "little"))
		dataIO.write(int(self.MeltDown).to_bytes(1, "little"))
		dataIO.write(len(self.AcceptedDataType).to_bytes(4, "little"))
		for i in self.AcceptedDataType:
			dataIO.write(i.to_bytes(4, "little"))
		dataIO.write(self.PublicKey.public_bytes(serialization.Encoding.Raw, serialization.PublicFormat.Raw))
		signedData = dataIO.getvalue()
		dataLength = len(signedData) + 4 + 114
		signedData = dataLength.to_bytes(4, "little") + signedData
		dataIO = io.BytesIO(signedData)
		dataIO.seek(0, os.SEEK_END)
		if rootPrivateKey is not None:
			dataIO.write(rootPrivateKey.sign(signedData))
		else:
			if rootPublicKey is not None:
				try:
					rootPublicKey.verify(self.Signature, signedData)
				except:
					raise Exception("Stored Signature is invalid")
			else:
				raise Exception("Stored Signature is invalid")
			dataIO.write(self.Signature)
		return dataIO.getvalue()

	def verify(self, data: bytes, dataSignature: bytes) -> bool:
		try:
			self.PublicKey.verify(dataSignature, data)
			return True
		except Exception as e:
			return False


class SubKeySegment:
	PublicKeySegment: SubKeyPublicSegment = None
	PrivateKey: ed448.Ed448PrivateKey = None

	@staticmethod
	def load(data: bytes, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None) -> "SubKeySegment":
		segment: SubKeySegment = SubKeySegment()
		dataIO: io.BytesIO = io.BytesIO(data)
		publicKeySegmentLength = int.from_bytes(dataIO.read(4), "little")
		segment.PublicKeySegment = SubKeyPublicSegment.load(dataIO.read(publicKeySegmentLength), rootPublicKey)
		privateKeyLength = int.from_bytes(dataIO.read(4), "little")
		segment.PrivateKey = ed448.Ed448PrivateKey.from_private_bytes(dataIO.read(privateKeyLength))
		return segment

	def save(self, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None, rootPrivateKey: typing.Optional[ed448.Ed448PrivateKey] = None) -> bytes:
		SubKeyPublicSegmentBytes: bytes = self.PublicKeySegment.save(rootPublicKey, rootPrivateKey)
		SubKeyPrivateKeyBytes: bytes = self.PrivateKey.private_bytes(
			encoding=serialization.Encoding.Raw,
			format=serialization.PrivateFormat.Raw,
			encryption_algorithm=serialization.NoEncryption(),
		)
		dataIO: io.BytesIO = io.BytesIO()
		dataIO.write(len(SubKeyPublicSegmentBytes).to_bytes(4, "little"))
		dataIO.write(SubKeyPublicSegmentBytes)
		dataIO.write(len(SubKeyPrivateKeyBytes).to_bytes(4, "little"))
		dataIO.write(SubKeyPrivateKeyBytes)
		return dataIO.getvalue()

	def verify(self, data: bytes, dataSignature: bytes) -> bool:
		return self.PublicKeySegment.verify(data, dataSignature)

	def sign(self, data: bytes) -> bytes:
		return self.PrivateKey.sign(data)


class SubDataSegment:
	Type: int = -1
	Version: int = -1

	@abc.abstractmethod
	def save(self) -> bytes:
		# 包括Header的数据 为文档中的 "数据块"
		pass

	def getReadableData(self) -> str | dict | list | None:
		return None


class IDataSegment:
	SubSegments: list[SubDataSegment] = None

	def __init__(self):
		self.SubSegments = []

	# load 函数由于需要读取Plugin列表 所以不在ScriptTypes.py 中定义
	@staticmethod
	def load(fileName: str, data: bytes) -> "IDataSegment":
		raise NotImplementedError

	def save(self) -> bytes:
		dataIO = io.BytesIO()
		dataIO.write((0).to_bytes(4, "little"))  # 由后续添加长度
		dataIO.write(len(self.SubSegments).to_bytes(2, "little"))
		for subSegment in self.SubSegments:
			dataIO.write(subSegment.save())
		dataLength = dataIO.tell()
		dataIO.seek(0)
		dataIO.write(dataLength.to_bytes(4, "little"))
		return dataIO.getvalue()

	def saveWithSignature(self, privateKey: SubKeySegment | ed448.Ed448PrivateKey) -> tuple[bytes, bytes]:
		if isinstance(privateKey, SubKeySegment):
			privateKey = privateKey.PrivateKey
		data = self.save()
		return data, privateKey.sign(data)


class AuthFile:
	Version: int = -1
	KeySegment: SubKeyPublicSegment = None
	DataSegments: IDataSegment = None
	DataSignature: bytes = None

	@staticmethod
	def load(fileName: str, data: bytes, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None) -> "AuthFile":
		authFile: AuthFile = AuthFile()
		dataIO = io.BytesIO(data)
		MagicNumber = dataIO.read(Const.MAGIC_NUMBER_LENGTH)
		if MagicNumber != Const.MAGIC_NUMBER:
			raise Exception("Magic Number is invalid")
		authFile.Version = int.from_bytes(dataIO.read(4), "little")
		rollback = dataIO.tell()
		keySegmentSize = int.from_bytes(dataIO.read(4), "little")
		dataIO.seek(rollback)
		authFile.KeySegment = SubKeyPublicSegment.load(dataIO.read(keySegmentSize), rootPublicKey)
		rollback = dataIO.tell()
		dataSegmentSize = int.from_bytes(dataIO.read(4), "little")
		dataIO.seek(rollback)
		dataSegmentBytes = dataIO.read(dataSegmentSize)
		authFile.DataSegments = IDataSegment.load(fileName, dataSegmentBytes)
		authFile.DataSignature = dataIO.read(114)
		if not authFile.KeySegment.verify(dataSegmentBytes, authFile.DataSignature):
			raise Exception("AuthFile Cannot Pass Verify")
		return authFile

	def save(self, subKeySegment: SubKeySegment, rootPublicKey: typing.Optional[ed448.Ed448PublicKey] = None, rootPrivateKey: typing.Optional[ed448.Ed448PrivateKey] = None) -> bytes:
		if self.KeySegment.PublicKey != subKeySegment.PublicKeySegment.PublicKey:
			raise Exception("SubKeySegment is invalid")
		dataIO = io.BytesIO()
		dataIO.write(Const.MAGIC_NUMBER)
		dataIO.write(self.Version.to_bytes(4, "little"))
		dataIO.write(self.KeySegment.save(rootPublicKey, rootPrivateKey))
		dataSegmentBytes = self.DataSegments.save()
		dataIO.write(dataSegmentBytes)
		dataIO.write(subKeySegment.sign(dataSegmentBytes))
		return dataIO.getvalue()



dataDeserializer = typing.Callable[[str, bytes], SubDataSegment]  # FileName, Data -> Data(SubDataSegment)  // Data 包括 Header
loadAllDataFunction = typing.Callable[[], dict[str, list[SubDataSegment]]]  # None => {FileName(No Suffix): [Data, ...], ...}
dataDeserializerRegister = typing.Callable[[str, tuple[int, int], dataDeserializer], None]  # DeserializerName, (Type, Version(Can -1, Accept All Version)), Deserializer -> None
loadAllDataFunctionRegister = typing.Callable[[str, loadAllDataFunction], None]  # LoaderName, Loader -> None
pluginRegisterFunction = typing.Callable[[dataDeserializerRegister, loadAllDataFunctionRegister], None]