cmake_minimum_required(VERSION 3.28)
project(MaskProcessV2_C)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static -static-libgcc -static-libstdc++")

find_package(Stb REQUIRED)

add_executable(MaskProcessV2_C dataStruct.h maskProcessV2.cpp)

target_include_directories(MaskProcessV2_C PRIVATE ${Stb_INCLUDE_DIR})