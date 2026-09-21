# FileDock

Sistema de gerenciamento de documentos desenvolvido com Java e Spring Boot.

O projeto tem como objetivo praticar uma aplicação web corporativa utilizando Spring, PostgreSQL, Spring Batch e servidor de aplicação Tomcat externo, mantendo o ambiente reproduzível com Docker.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Batch
- PostgreSQL 17
- Flyway
- Apache Tomcat 11
- Maven
- Docker
- Docker Compose
- JUnit

## Estrutura atual

O FileDock utiliza:

- Spring Boot para a aplicação
- Spring Data JPA para persistência
- PostgreSQL como banco de dados
- Flyway para versionamento do schema
- WAR para empacotamento da aplicação
- Apache Tomcat externo como servidor de aplicação
- Docker para padronização do ambiente

## Banco de dados

O PostgreSQL é executado através do Docker Compose.

A configuração utiliza variáveis de ambiente para banco, usuário e senha.

O schema do banco é versionado através do Flyway.

A primeira migration cria a tabela `documents`.

## Tomcat externo

A aplicação é empacotada como um arquivo WAR e implantada em um Apache Tomcat externo.

O projeto utiliza `SpringBootServletInitializer` para permitir a execução da aplicação em um servidor de aplicação externo.

O Tomcat é executado através de um container Docker e realiza o deploy do arquivo:

```text
filedock.war
```

A aplicação é disponibilizada na porta `8080`.

O deploy foi validado através dos logs do Tomcat, incluindo:

- carregamento do arquivo WAR;
- inicialização do Spring Boot;
- conexão com PostgreSQL;
- execução e validação das migrations do Flyway;
- inicialização do Spring MVC;
- conclusão do deploy da aplicação.

## Executando o projeto

### Pré-requisitos

- Java 21
- Docker
- Docker Compose

### Variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
POSTGRES_DB=filedock
POSTGRES_USER=filedock
POSTGRES_PASSWORD=filedock_dev
```

### Gerando o WAR

No Windows:

```powershell
.\mvnw.cmd clean package
```

No Linux:

```bash
./mvnw clean package
```

### Iniciando o ambiente

Com o Docker em execução:

```bash
docker-compose up -d --build
```

O Docker Compose inicia:

- PostgreSQL
- Tomcat

O Tomcat utiliza o WAR gerado pelo Maven e se conecta ao PostgreSQL através da rede interna do Docker Compose.

### Parando o ambiente

```bash
docker-compose down
```

O volume do PostgreSQL é mantido para preservar os dados.

## Status do projeto

O projeto está em desenvolvimento.

### Implementado

- [x] Projeto Spring Boot
- [x] Java 21
- [x] PostgreSQL
- [x] Spring Data JPA
- [x] Flyway
- [x] Empacotamento WAR
- [x] SpringBootServletInitializer
- [x] Apache Tomcat externo
- [x] Docker
- [x] Docker Compose
- [x] Deploy do WAR no Tomcat
- [x] Comunicação entre aplicação e PostgreSQL

### Próximas etapas

- [ ] API REST de documentos
- [ ] Validação de dados
- [ ] Tratamento global de exceções
- [ ] Testes unitários e de integração
- [ ] Spring Batch
- [ ] Documentação da API
- [ ] Frontend Angular
- [ ] Deploy da aplicação

## Objetivo

O FileDock é um projeto de portfólio voltado à prática de desenvolvimento backend com Java e Spring, incluindo o uso de um servidor de aplicação externo e ambientes reproduzíveis com Docker.