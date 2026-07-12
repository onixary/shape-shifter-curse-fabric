#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan
# LICENSE       : All Rights Reserved (XuHaoNan)

import io
import PluginUtils
PluginUtils.loadAllPlugins()
import ScriptTypes
import Const


def loadDataSegments(fileName: str, data: bytes) -> ScriptTypes.IDataSegment:
	dataSegment = ScriptTypes.IDataSegment()
	dataIO = io.BytesIO(data)
	dataSegmentSize = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
	if dataSegmentSize != len(data):
		raise Exception(f"DataSegment Size is not equal to Data Size {len(data)} != {dataSegmentSize}")
	subSegmentCount = int.from_bytes(dataIO.read(2), Const.INT_BYTE_TYPE)
	for i in range(subSegmentCount):
		rollBack = dataIO.tell()
		subSegmentType = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		subSegmentVersion = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		subSegmentSize = int.from_bytes(dataIO.read(4), Const.INT_BYTE_TYPE)
		dataIO.seek(rollBack)
		subSegmentData = dataIO.read(subSegmentSize)
		subSegment = PluginUtils.readSubSegment(fileName, subSegmentType, subSegmentVersion, subSegmentData)
		dataSegment.SubSegments.append(subSegment)
	return dataSegment


ScriptTypes.IDataSegment.load = loadDataSegments


