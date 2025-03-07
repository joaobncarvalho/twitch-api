# Usa a imagem do Maven e Java 21 para compilar a aplicação
FROM eclipse-temurin:21-jdk AS build

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
