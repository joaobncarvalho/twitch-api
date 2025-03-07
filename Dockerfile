# Usa uma imagem base do OpenJDK para rodar aplicações Java
FROM eclipse-temurin:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven/Gradle
COPY target/quarkus-app /app/

# Comando para executar a aplicação
CMD ["java", "-jar", "/app/quarkus-run.jar"]
