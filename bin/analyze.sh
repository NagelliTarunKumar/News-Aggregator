# #!/bin/sh

# java -jar analyzer.jar

java -jar analyzer.jar
echo "Analysis complete. Keeping container running..."
tail -f /dev/null
