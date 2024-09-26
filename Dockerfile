FROM ubuntu:latest
LABEL authors="cmrom"

ENTRYPOINT ["top", "-b"]