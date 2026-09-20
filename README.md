# FileDock

Sistema web para gerenciamento de documentos corporativos, desenvolvido com Java e Spring Boot.

O projeto tem como objetivo construir uma aplicação de gerenciamento de documentos com API REST, persistência de dados, validações, testes automatizados e execução em diferentes ambientes.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- PostgreSQL 17
- Flyway
- Apache Tomcat
- Maven
- JUnit
- Mockito
- Docker
- Docker Compose

## Banco de dados

O PostgreSQL é executado localmente através do Docker Compose.

As alterações de estrutura do banco são controladas pelo Flyway através de migrations versionadas.

A aplicação utiliza o Hibernate apenas para validar o mapeamento entre as entidades Java e o banco de dados. A criação e alteração do schema são responsabilidade do Flyway.

## Configuração

O projeto utiliza variáveis de ambiente para as configurações do banco de dados.

Crie um arquivo `.env` a partir do `.env.example`:

```env
POSTGRES_DB=filedock
POSTGRES_USER=filedock
POSTGRES_PASSWORD=filedock_dev