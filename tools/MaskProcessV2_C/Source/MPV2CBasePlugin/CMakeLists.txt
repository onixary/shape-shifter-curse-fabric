cmake_minimum_required(VERSION 3.28)
project(MPV2CBasePlugin)

set(CMAKE_CXX_STANDARD 20)

add_library(MPV2CBasePlugin SHARED library.cpp dataStruct.h)
