FROM eclipse-temurin:21-alpine

WORKDIR /apps

COPY ./applications/app/build/libs/app.jar .
COPY ./applications/analyzer/build/libs/analyzer.jar .
COPY ./applications/collector/build/libs/collector.jar .

COPY ./bin/app.sh ./app.sh
COPY ./bin/analyze.sh ./analyze.sh
COPY ./bin/collect.sh ./collect.sh

ENTRYPOINT [ "./app.sh" ]
