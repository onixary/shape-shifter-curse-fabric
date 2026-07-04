#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# Desc          : 生成子私钥

# 需要 pip install cryptography

from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.backends import default_backend


def CreateSubKey(Type: int, Version: int, UseMeltDown: bool, RootPrivateKey: bytes):
    private_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=4096,
    )
    public_key = private_key.public_key()
    n_bytes = public_key.public_numbers().n.to_bytes(512, 'big')
    private_pem = private_key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.PKCS8,
        encryption_algorithm=serialization.NoEncryption(),
    )
    type_bytes = Type.to_bytes(4, 'big')
    version_bytes = Version.to_bytes(4, 'big')
    meltdown_byte = bytes([1 if UseMeltDown else 0])
    data_to_sign = type_bytes + version_bytes + meltdown_byte + n_bytes
    try:
        root_priv = serialization.load_pem_private_key(RootPrivateKey, password=None, backend=default_backend())
    except:
        raise Exception("RootPrivateKey is invalid")
    signature = root_priv.sign(
        data_to_sign,
        padding.PKCS1v15(),
        hashes.SHA256()
    )
    if len(signature) != 512:
        raise Exception("Signature length is invalid")
    key_segment = type_bytes + version_bytes + meltdown_byte + n_bytes + signature
    return n_bytes, private_pem, key_segment


def ConsoleUI():
    with open("ROOT_PRIVATE_KEY.pem", "rb") as f:
        root_private_key = f.read()
    print("输入密钥类型(INT):")
    Type = int(input())
    print("输入密钥版本(INT):")
    Version = int(input())
    print("是否使用MeltDown(Y/N):")
    UseMeltDown = input() == "Y"
    n_bytes, private_pem, key_segment = CreateSubKey(Type, Version, UseMeltDown, root_private_key)
    with open(f"SubPrivateKey_{Type}_{Version}{'' if UseMeltDown else '_NM'}.pem", "wb") as f:
        f.write(private_pem)
    with open(f"SubKeySegment_{Type}_{Version}{'' if UseMeltDown else '_NM'}.bin", "wb") as f:
        f.write(key_segment)


if __name__ == "__main__":
    ConsoleUI()
    