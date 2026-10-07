# Stufe 1: mit vollem JDK bauen
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Erst nur die Gradle-Dateien kopieren: Abhängigkeiten werden so zwischengespeichert,
# solange sich build.gradle nicht ändert
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src src
# Tests laufen in GitHub Actions, hier nur das ausführbare JAR bauen
RUN ./gradlew bootJar --no-daemon

# Stufe 2: nur die Laufzeit (JRE) und das JAR, ohne Quellcode und Build-Werkzeuge
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Render Free hat 512 MB Speicher; die JVM soll höchstens 75 % davon für den Heap nehmen
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
