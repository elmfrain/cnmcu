cmake_minimum_required(VERSION 3.13)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O2 -Wno-narrowing")
set(MOD_ID "cnmcu")

project("${MOD_ID}-natives")

find_package(Java REQUIRED)
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
find_package(JNI REQUIRED)

set(PROJECT_SOURCES 
  cnmcu/mos6502.cpp
  cnmcu/Nano.cpp

  bridge/com_elmfer_cnmcu_mcu_cpu_MOS6502.cpp
  bridge/com_elmfer_cnmcu_mcu_NanoMCU.cpp
  bridge/com_elmfer_cnmcu_mcu_modules_CNRAM.cpp
  bridge/com_elmfer_cnmcu_mcu_modules_CNROM.cpp
  bridge/cnmcuJava.cpp
)

set(PROJECT_INCLUDES
  bridge
  cnmcu
  ${JNI_INCLUDE_DIRS}
)

add_library(${PROJECT_NAME} SHARED ${PROJECT_SOURCES})

target_include_directories(${PROJECT_NAME} PRIVATE ${PROJECT_INCLUDES})

# force off-tree build
if(${CMAKE_SOURCE_DIR} STREQUAL ${CMAKE_BINARY_DIR})
  message(FATAL_ERROR "CMake generation is not allowed within the source directory!
  Remove the CMakeCache.txt file and try again from another folder, e.g.:
    mkdir build && cd build
    cmake ..
  ")
endif()

# default to Release build
if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release CACHE STRING
      "Choose the type of build, options are: None Debug Release RelWithDebInfo MinSizeRel."
      FORCE)
endif()

# Determine operating system and set NATIVES_OS
if(WIN32)
  set(NATIVES_OS "windows")
elseif(APPLE)
  set(NATIVES_OS "macos")
elseif(UNIX AND NOT APPLE)
  set(NATIVES_OS "linux")
else()
  message(FATAL_ERROR "Unsupported operating system")
endif()

# Determine target platform architecture and set NATIVES_PLATFORM
if(CMAKE_SYSTEM_PROCESSOR MATCHES "amd64|AMD64|x86_64|x86|X86|i386|i686")
  set(NATIVES_PLATFORM "x")
elseif(CMAKE_SYSTEM_PROCESSOR MATCHES "arm|ARM|aarch64|AARCH64")
  set(NATIVES_PLATFORM "arm")
else()
  message(FATAL_ERROR "Unsupported processor architecture")
endif()

# Determine architecture and set NATIVES_BITS
if(CMAKE_SIZEOF_VOID_P EQUAL 8)
  set(NATIVES_BITS "64")
elseif(CMAKE_SIZEOF_VOID_P EQUAL 4)
  set(NATIVES_BITS "32")
else()
  message(FATAL_ERROR "Unsupported architecture")
endif()
if(NATIVES_PLATFORM STREQUAL "x" AND NATIVES_BITS STREQUAL "32")
  set(NATIVES_BITS "86")
endif()

set_target_properties(${PROJECT_NAME} PROPERTIES
  PREFIX "lib"
  OUTPUT_NAME "${MOD_ID}-${NATIVES_OS}-${NATIVES_PLATFORM}${NATIVES_BITS}"
)