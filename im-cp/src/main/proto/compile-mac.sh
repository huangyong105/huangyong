SRC_DIR="/Users/huit/git/uuc-cp-git/src/main/proto"
DST_DIR="/Users/huit/git/uuc-cp-git/src/main/java"
#protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/user.proto
#protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/base.proto
#protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/msg.proto
protoc msg.proto --java_out=/Users/huit/git/uuc-cp-git/src/main/java/
#protoc -I=/Users/huit/git/uuc-cp-git/src/main/proto --java_out=/Users/huit/git/uuc-cp-git/src/main/java /Users/huit/git/uuc-cp-git/src/main/proto/voip.proto