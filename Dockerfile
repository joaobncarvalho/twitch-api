# Usa uma imagem com Maven e Java 21 já instalados
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Compila a aplicação com Maven
RUN mvn package -DskipTests

# Usa uma nova imagem apenas para rodar o app
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

# Copia o build da fase anterior
COPY --from=build /app/target/quarkus-app/ /app/

# Define o comando de inicialização
CMD ["java", "-jar", "/app/quarkus-run.jar"]
