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
- Spring Web MVC para a camada web
- Spring Data JPA para persistência
- PostgreSQL como banco de dados
- Flyway para versionamento do schema
- Spring Batch para processamento de documentos
- WAR para empacotamento da aplicação
- Apache Tomcat externo como servidor de aplicação
- Docker para padronização do ambiente

## Estrutura do projeto

```
FileDock/
│
├── docs/
│   ├── architecture/
│   ├── api/
│   ├── database/
│   └── deployment/
│
├── src/
│   ├── main/
│   │   ├── java/com/filedock/
│   │   │   ├── FileDockApplication.java
│   │   │   ├── ServletInitializer.java
│   │   │   ├── document/
│   │   │   │   ├── Document.java
│   │   │   │   ├── DocumentController.java
│   │   │   │   ├── DocumentRepository.java
│   │   │   │   ├── DocumentService.java
│   │   │   │   ├── ProcessingStatus.java
│   │   │   │   └── dto/
│   │   │   ├── batch/
│   │   │   └── exception/
│   │   │
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/filedock/
│
├── frontend/
│
├── .github/
├── .mvn/
├── .env.example
├── .gitattributes
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Banco de dados

O PostgreSQL é executado através do Docker Compose.

A configuração utiliza variáveis de ambiente para banco, usuário e senha.

O schema do banco é versionado através do Flyway.

As migrations atuais são:

- `V1__create_documents_table.sql` — criação da tabela `documents`
- `V2__add_document_processing_status.sql` — adição do status e da data de processamento dos documentos

A tabela `documents` possui, entre outros dados, o status de processamento do documento:

- `PENDING`
- `PROCESSED`

## API REST

O projeto possui uma API REST para gerenciamento de documentos.

Endpoint base:

```text
/api/documents
```

Endpoints atualmente implementados:

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/documents` | Lista os documentos |
| GET | `/api/documents/{id}` | Busca um documento pelo ID |
| POST | `/api/documents` | Cria um novo documento |

A API utiliza uma camada de serviço para concentrar as regras relacionadas aos documentos e o Spring Data JPA para acesso ao banco de dados.

### Tratamento de recursos não encontrados

Quando um documento não é encontrado pelo ID, a aplicação lança uma exceção específica:

```text
ResourceNotFoundException
```

O tratamento é realizado globalmente através de:

```text
GlobalExceptionHandler
```

Nesse caso, a API retorna HTTP `404 Not Found`.

Exemplo:

```json
{
  "error": "Documento não encontrado: 999"
}
```

## Spring Batch

O projeto utiliza Spring Batch para estruturar o processamento dos documentos pendentes.

O fluxo implementado possui:

- `RepositoryItemReader` para buscar documentos com status `PENDING`
- `ItemProcessor` para atualizar o status do documento
- `JpaItemWriter` para persistir as alterações
- `Job` para representar o processamento
- `Step` para definir o fluxo de processamento

Durante o processamento, o documento passa de `PENDING` para `PROCESSED` e recebe a data de processamento.

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

### Executando os testes

No Windows:

```powershell
.\mvnw.cmd clean test
```

No Linux:

```bash
./mvnw clean test
```

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
- [x] Spring Web MVC
- [x] Spring Data JPA
- [x] Flyway
- [x] Migration da tabela `documents`
- [x] Migration de status de processamento
- [x] Entidade `Document`
- [x] `DocumentRepository`
- [x] Spring Batch
- [x] `RepositoryItemReader`
- [x] `ItemProcessor`
- [x] `JpaItemWriter`
- [x] Job de processamento de documentos
- [x] Step de processamento de documentos
- [x] API REST de documentos
- [x] Endpoint para listagem de documentos
- [x] Endpoint para consulta de documento por ID
- [x] Endpoint para criação de documentos
- [x] `DocumentService`
- [x] `CreateDocumentRequest`
- [x] `ResourceNotFoundException`
- [x] Tratamento global de exceções
- [x] Empacotamento WAR
- [x] `SpringBootServletInitializer`
- [x] Apache Tomcat externo
- [x] Docker
- [x] Docker Compose
- [x] Deploy do WAR no Tomcat
- [x] Comunicação entre aplicação e PostgreSQL
- [x] Testes automatizados do contexto da aplicação

### Próximas etapas

- [ ] Atualização de documentos
- [ ] Definição da estratégia de remoção/arquivamento de documentos
- [ ] Validação de dados
- [ ] Tratamento de erros de validação
- [ ] DTOs de resposta
- [ ] Testes unitários e de integração
- [ ] Documentação da API
- [ ] Frontend Angular
- [ ] Refinamento do ambiente Docker
- [ ] Deploy da aplicação

## Objetivo

O FileDock é um projeto de portfólio voltado à prática de desenvolvimento backend com Java e Spring, incluindo desenvolvimento de APIs, persistência de dados, processamento em lote, uso de servidor de aplicação externo e ambientes reproduzíveis com Docker.