#!/bin/sh

java -jar collector.jar
echo "Collection complete. Keeping container running..."
tail -f /dev/null

