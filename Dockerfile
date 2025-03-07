# Etapa 1: Construção com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o arquivo pom.xml primeiro para otimizar cache
COPY pom.xml ./

# Verifica se o POM.XML realmente foi copiado
RUN ls -l pom.xml || (echo "❌ ERRO: pom.xml NÃO ENCONTRADO!" && exit 1)

# Baixa as dependências do Maven antes de copiar o código-fonte
RUN mvn dependency:go-offline

# Copia o restante do código para o container
COPY src ./src

# Compila o projeto
RUN mvn package -DskipTests

# Etapa 2: Criar a imagem final para rodar a aplicação
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

# Copia o build gerado na etapa anterior
COPY --from=build /app/target/quarkus-app/ /app/

# Comando para rodar a aplicação
CMD ["java", "-jar", "/app/quarkus-run.jar"]
