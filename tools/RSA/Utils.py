#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan


import ScriptTypes
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes, serialization
import typing
import os


ROOT_KEY_PUBLIC_KEY_PATH = "./ROOT_KEY/ROOT_PUBLIC_KEY.pem"
ROOT_KEY_PRIVATE_KEY_PATH = "./ROOT_KEY/ROOT_PRIVATE_KEY.pem"


def createKeyPair() -> tuple[rsa.RSAPublicKey, rsa.RSAPrivateKey]:
	privateKey = rsa.generate_private_key(
		public_exponent=65537,
		key_size=4096,
		backend=default_backend()
	)
	publicKey = privateKey.public_key()
	return publicKey, privateKey


def createSubKey(RootPrivateKey: rsa.RSAPrivateKey, Type: int, Version: int, UseMeltDown: bool, AcceptedDataType: list[int]) -> bytes:
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
	return keySegment.save(RootPrivateKey, None)


def loadRootKey() -> tuple[typing.Optional[rsa.RSAPublicKey], typing.Optional[rsa.RSAPrivateKey]]:
	publicKey, privateKey = None, None
	if os.path.exists(ROOT_KEY_PUBLIC_KEY_PATH):
		with open(ROOT_KEY_PUBLIC_KEY_PATH, "rb") as f:
			publicKey = serialization.load_pem_public_key(f.read(), backend=default_backend())
	if os.path.exists(ROOT_KEY_PRIVATE_KEY_PATH):
		with open(ROOT_KEY_PRIVATE_KEY_PATH, "rb") as f:
			privateKey = serialization.load_pem_private_key(f.read(), password=None, backend=default_backend())
	return publicKey, privateKey


