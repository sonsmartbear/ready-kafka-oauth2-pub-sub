FROM openjdk:17
RUN mkdir /tmp/lib
COPY ./target/dependency/* /tmp/libs/
COPY ./target/kafka-oauth2-publisher-1.0-SNAPSHOT.jar /tmp/app.jar
WORKDIR /tmp
ARG OAUTH2_ACCESS_TOKEN=eyJraWQiOiJ5bGJTenZBb08tMC1SUzVUYXU1WHFHYzJLU0xKTFVVWUV6d015QXFxYVhrIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULjZuMFpnRWlqR1dpMXZ5X19FTGp2SVUxcjBsc01aQzl1cWdNY2NJZ0xZZmciLCJpc3MiOiJodHRwczovL2Rldi02ODgzMTc0My5va3RhLmNvbS9vYXV0aDIvYWFzb2t0YSIsImF1ZCI6ImFwaTovL2Fhc29rdGEiLCJpYXQiOjE2NjQyNTk3OTIsImV4cCI6MTY2NDI2MzM5MiwiY2lkIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDciLCJzY3AiOlsia2Fma2EiXSwic3ViIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDcifQ.Mw7WMeI476qga5b3sgoz7gGGsWTGChy2TSbOh4ulgWb6cBvIQZuLuSPTlRe-HNz3l08Tf2V-GN2emOev-aDN66_SXZ519Ap5tlhqYXspH3DpMcdZmj4FMa9-zkRRkHpNfwPCYouwWwVlF2gYwxwzuRSwkl03afhFc69isQRRK-tmfsWIP2SijRIASS3bAKxieRhGp7pNPFIWR8HiGL1GEv_pUFQlU1oahq90N9_kkpqKvfwsxPvi33EnJ_TqZUSol3CyBRyWLB6g3Ak75l0MHumrGZzeMtHRHsvOVfKkiFa25vE5DG5TJ6tOzYvtpZiqLcMeAUf6nZqk4y05Y5dd6A

#ENV JAVA_OPTS=-cp app.jar:/tmp/libs/*
ENTRYPOINT exec java -cp "app.jar:/tmp/libs/*" "com.smartbear.ready.kafka.oauth2.client.ProducerOAuth"


