@echo off
set SRC_DIR=%~dp0
set DST_DIR=D:\git\uuc-cp-git\src\main\java
set CPP_DIR=D:\git\uuc-cp-git\cpp
protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/user.proto
protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/base.proto
protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/msg.proto
protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/voip.proto