#!/bin/bash

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR_PATH="$BASE_DIR/austin-stream/target/austin-stream-0.0.1-SNAPSHOT.jar"
if [[ ! -f "$JAR_PATH" ]]; then
    echo "jar 不存在: $JAR_PATH" >&2
        exit 1
fi

docker cp "$JAR_PATH" austin-jobmanager-1:/opt/austin-stream-0.0.1-SNAPSHOT.jar
docker exec -ti austin-jobmanager-1 flink run /opt/austin-stream-0.0.1-SNAPSHOT.jar

# stream local test
# docker cp ./austin-stream-0.0.1-SNAPSHOT.jar austin_jobmanager_1:/opt/austin-stream-test-0.0.1-SNAPSHOT.jar
# docker exec -ti austin_jobmanager_1 flink run /opt/austin-stream-test-0.0.1-SNAPSHOT.jar


# data-house
# ./flink run austin-data-house-0.0.1-SNAPSHOT.jar

