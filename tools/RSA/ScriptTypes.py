# !/usr/bin/env python
# -*- coding: UTF-8 -*-
# Author        : XuHaoNan

import typing
import io
import abc
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes, serialization
import Const


class SubKeyPublicSegment:
	PublicKey: rsa.RSAPublicKey = None
	KeyType: int = -1
	KeyVersion: int = -1
	MeltDown: bool = True
	AcceptedDataType: list[int] = []
	Signature: bytes = None

	@staticmethod
	def load(data: bytes, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None) -> "SubKeyPublicSegment":
		segment: SubKeyPublicSegment = SubKeyPublicSegment()
		dataIO: io.BytesIO = io.BytesIO(data)
		dataLength = dataIO.read(4)
		if (dataLength != len(bytes)):
			raise Exception("Data length is not Current")
		segment.KeyType = int.from_bytes(dataIO.read(4), "big")
		segment.KeyVersion = int.from_bytes(dataIO.read(4), "big")
		segment.MeltDown = bool(int.from_bytes(dataIO.read(1), "big"))
		AcceptedDataTypeCount = int.from_bytes(dataIO.read(4), "big")
		for i in range(AcceptedDataTypeCount):
			segment.AcceptedDataType.append(int.from_bytes(dataIO.read(4), "big"))
		segment.PublicKey = rsa.RSAPublicNumbers(
			e=65537,
			n=int.from_bytes(dataIO.read(512), "big")
		).public_key(backend=default_backend())
		signedEndIndex = dataIO.tell()
		segment.Signature = dataIO.read(512)
		if rootPublicKey is not None:
			dataIO.seek(0)
			signedData = dataIO.read(signedEndIndex)
			rootPublicKey.verify(segment.Signature, signedData, padding.PKCS1v15(), hashes.SHA256())
		return segment

	def save(self, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None, rootPrivateKey: typing.Optional[rsa.RSAPrivateKey] = None) -> bytes:
		dataIO: io.BytesIO = io.BytesIO()
		dataIO.write(self.KeyType.to_bytes(4, "big"))
		dataIO.write(self.KeyVersion.to_bytes(4, "big"))
		dataIO.write(int(self.MeltDown).to_bytes(1, "big"))
		dataIO.write(len(self.AcceptedDataType).to_bytes(4, "big"))
		for i in self.AcceptedDataType:
			dataIO.write(i.to_bytes(4, "big"))
		dataIO.write(self.PublicKey.public_numbers().n.to_bytes(512, "big"))
		signedData = dataIO.getvalue()
		dataLength = len(signedData) + 4 + 512
		signedData = dataLength.to_bytes(4, "big") + signedData
		dataIO = io.BytesIO(signedData)
		if rootPrivateKey is not None:
			dataIO.write(rootPrivateKey.sign(signedData, padding.PKCS1v15(), hashes.SHA256()))
		else:
			if rootPublicKey is not None:
				try:
					rootPublicKey.verify(self.Signature, signedData, padding.PKCS1v15(), hashes.SHA256())
				except:
					raise Exception("Stored Signature is invalid")
			else:
				raise Exception("Stored Signature is invalid")
			dataIO.write(self.Signature)
		return dataIO.getvalue()

	def verify(self, data: bytes, dataSignature: bytes) -> bool:
		try:
			self.PublicKey.verify(dataSignature, data, padding.PKCS1v15(), hashes.SHA256())
			return True
		except:
			return False


class SubKeySegment:
	PublicKeySegment: SubKeyPublicSegment = None
	PrivateKey: rsa.RSAPrivateKey = None

	@staticmethod
	def load(data: bytes, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None) -> "SubKeySegment":
		segment: SubKeySegment = SubKeySegment()
		dataIO: io.BytesIO = io.BytesIO(data)
		publicKeySegmentLength = int.from_bytes(dataIO.read(4), "big")
		segment.PublicKeySegment = SubKeyPublicSegment.load(dataIO.read(publicKeySegmentLength), rootPublicKey)
		privateKeyLength = int.from_bytes(dataIO.read(4), "big")
		segment.PrivateKey = serialization.load_der_private_key(dataIO.read(privateKeyLength), None, default_backend())
		return segment

	def save(self, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None, rootPrivateKey: typing.Optional[rsa.RSAPrivateKey] = None) -> bytes:
		SubKeyPublicSegmentBytes: bytes = self.PublicKeySegment.save(rootPublicKey, rootPrivateKey)
		SubKeyPrivateKeyBytes: bytes = self.PrivateKey.private_bytes(
			encoding=serialization.Encoding.DER,
			format=serialization.PrivateFormat.PKCS8,
			encryption_algorithm=serialization.NoEncryption(),
		)
		dataIO: io.BytesIO = io.BytesIO()
		dataIO.write(len(SubKeyPublicSegmentBytes).to_bytes(4, "big"))
		dataIO.write(SubKeyPublicSegmentBytes)
		dataIO.write(len(SubKeyPrivateKeyBytes).to_bytes(4, "big"))
		dataIO.write(SubKeyPrivateKeyBytes)
		return dataIO.getvalue()

	def verify(self, data: bytes, dataSignature: bytes) -> bool:
		return self.PublicKeySegment.verify(data, dataSignature)

	def sign(self, data: bytes) -> bytes:
		return self.PrivateKey.sign(data, padding.PKCS1v15(), hashes.SHA256())


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
	SubSegments: list[SubDataSegment] = []

	# load 函数由于需要读取Plugin列表 所以不在ScriptTypes.py 中定义
	@staticmethod
	def load(data: bytes) -> "IDataSegment":
		raise NotImplementedError

	def save(self) -> bytes:
		dataIO = io.BytesIO()
		dataIO.write(len(self.SubSegments).to_bytes(2, "big"))
		for subSegment in self.SubSegments:
			dataIO.write(subSegment.save())
		return dataIO.getvalue()

	def saveWithSignature(self, privateKey: SubKeySegment | rsa.RSAPrivateKey) -> tuple[bytes, bytes]:
		if isinstance(privateKey, SubKeySegment):
			privateKey = privateKey.PrivateKey
		data = self.save()
		return data, privateKey.sign(data, padding.PKCS1v15(), hashes.SHA256())


class AuthFile:
	Version: int = -1
	KeySegment: SubKeyPublicSegment = None
	DataSegments: IDataSegment
	DataSignature: bytes = None

	@staticmethod
	def load(data: bytes, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None) -> "AuthFile":
		authFile: AuthFile = AuthFile()
		dataIO = io.BytesIO(data)
		MagicNumber = dataIO.read(Const.MAGIC_NUMBER_LENGTH)
		if MagicNumber != Const.MAGIC_NUMBER:
			raise Exception("Magic Number is invalid")
		authFile.Version = int.from_bytes(dataIO.read(4), "big")
		rollback = dataIO.tell()
		keySegmentSize = int.from_bytes(dataIO.read(4), "big")
		dataIO.seek(rollback)
		authFile.KeySegment = SubKeyPublicSegment.load(dataIO.read(keySegmentSize), rootPublicKey)
		rollback = dataIO.tell()
		dataSegmentSize = int.from_bytes(dataIO.read(4), "big")
		dataIO.seek(rollback)
		dataSegmentBytes = dataIO.read(dataSegmentSize)
		authFile.DataSegments = IDataSegment.load(dataSegmentBytes)
		authFile.DataSignature = dataIO.read(512)
		if not authFile.KeySegment.verify(dataSegmentBytes, authFile.DataSignature):
			raise Exception("AuthFile Cannot Pass Verify")
		return authFile

	def save(self, subKeySegment: SubKeySegment, rootPublicKey: typing.Optional[rsa.RSAPublicKey] = None, rootPrivateKey: typing.Optional[rsa.RSAPrivateKey] = None) -> bytes:
		if self.KeySegment.PublicKey != subKeySegment.PublicKeySegment.PublicKey:
			raise Exception("SubKeySegment is invalid")
		dataIO = io.BytesIO()
		dataIO.write(Const.MAGIC_NUMBER)
		dataIO.write(self.Version.to_bytes(4, "big"))
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