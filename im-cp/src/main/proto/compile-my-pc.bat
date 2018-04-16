@echo off
set SRC_DIR=F:\src\protoc-3.0.2-win32
set DST_DIR=F:\src\protoc-3.0.2-win32\java
set CPP_DIR=F:\src\protoc-3.0.2-win32\cpp
#protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/user.proto
protoc -I=. --java_out=%DST_DIR% %SRC_DIR%/base.proto
protoc -I=. --java_out=%DST_DIR% %SRC_DIR%/msg.proto
#protoc -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/voip.proto