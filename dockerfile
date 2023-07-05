FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY ./build/libs/sFL-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-Dcom.amazonaws.sdk.disableEc2Metadata=true", "-jar", "app.jar"]




# FROM adoptopenjdk:11-jdk-hotspot
#
# WORKDIR /app
#
# COPY ./build/libs/sFL-0.0.1-SNAPSHOT.jar app.jar
#
# CMD ["java", "-jar", "app.jar"]
#
# EXPOSE 9000