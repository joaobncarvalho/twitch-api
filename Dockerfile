# Usa uma imagem com Maven e Java 21 já instalados para compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas o pom.xml para baixar dependências primeiro (otimiza cache)
COPY pom.xml .

# Baixa as dependências antes de copiar o código-fonte (melhora cache do build)
RUN mvn dependency:go-offline

# Agora copia o restante do código
COPY . .

# Compila a aplicação
RUN mvn package -DskipTests

# Usa uma nova imagem para rodar o app sem o Maven
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

# Copia o build da fase anterior
COPY --from=build /app/target/quarkus-app/ /app/

# Define o comando de inicialização
CMD ["java", "-jar", "/app/quarkus-run.jar"]
