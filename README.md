# FileDock

Sistema de gerenciamento de documentos desenvolvido com Java e Spring Boot, com persistência em PostgreSQL, processamento assíncrono utilizando Spring Batch e execução em Apache Tomcat externo.

O projeto foi construído com foco em uma aplicação backend real, documentada e executável em ambiente Docker, utilizando uma arquitetura simples e organizada para facilitar manutenção e evolução.

## Tecnologias

* Java 21
* Spring Boot 4.1.1
* Spring Web MVC
* Spring Data JPA
* Spring Batch
* PostgreSQL 17
* Flyway
* Apache Tomcat 11
* Maven
* Docker
* Docker Compose
* JUnit
* Mockito
* OpenAPI
* Swagger UI

## Funcionalidades

### Documentos

* Cadastro de documentos
* Consulta de todos os documentos
* Consulta de documento por ID
* Atualização de documentos
* Validação dos dados de entrada
* Tratamento de documentos inexistentes
* Controle de status de processamento

### Processamento

Os documentos cadastrados inicialmente recebem o status:

```text
PENDING
```

O Spring Batch identifica os documentos pendentes e realiza o processamento, alterando o status para:

```text
PROCESSED
```

Também é registrada a data de conclusão do processamento.

### API REST

Endpoints disponíveis:

```text
GET    /api/documents
GET    /api/documents/{id}
POST   /api/documents
PUT    /api/documents/{id}
```

A API utiliza DTOs para separar os modelos de entrada e saída da entidade de persistência.

### Validação

As requisições de criação e atualização utilizam Bean Validation para validar:

* Campos obrigatórios
* Tamanho máximo de campos
* Valores numéricos positivos
* Dados inválidos enviados pela API

As respostas de erro são padronizadas através de um tratamento global de exceções.

### Documentação da API

A API possui documentação OpenAPI disponibilizada através do Swagger UI.

Durante a execução local:

```text
http://localhost:8080/filedock/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/filedock/v3/api-docs
```

A documentação apresenta:

* Endpoints disponíveis
* Parâmetros
* Corpo das requisições
* Respostas HTTP
* Schemas dos DTOs
* Exemplos de dados
* Respostas de erro
* Status de processamento

## Banco de dados

O projeto utiliza PostgreSQL para persistência dos documentos.

As alterações do banco são controladas pelo Flyway.

Migrações atualmente existentes:

```text
V1__create_documents_table.sql
V2__add_document_processing_status.sql
V3__add_document_storage_path.sql
```

A tabela `documents` possui dados relacionados a:

* Identificação do documento
* Título
* Descrição
* Nome do arquivo
* Tipo MIME
* Tamanho
* Caminho de armazenamento
* Data de criação
* Data de atualização
* Status de processamento
* Data de processamento

O Hibernate utiliza:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Dessa forma, a estrutura do banco é controlada pelas migrações do Flyway.

## Spring Batch

O processamento dos documentos utiliza Spring Batch.

Fluxo atual:

```text
Documento criado
       ↓
PENDING
       ↓
RepositoryItemReader
       ↓
DocumentProcessor
       ↓
PROCESSED
       ↓
processedAt
```

O processamento utiliza:

* `RepositoryItemReader`
* `ItemProcessor`
* `JpaItemWriter`
* `Job`
* `Step`
* processamento em chunks

O processamento foi validado utilizando a aplicação executando em Tomcat externo e PostgreSQL.

## Apache Tomcat

A aplicação é empacotada como arquivo WAR e executada em Apache Tomcat externo.

O projeto não depende exclusivamente do servidor embutido do Spring Boot para execução.

O fluxo utilizado é:

```text
Maven
  ↓
WAR
  ↓
Docker Image
  ↓
Apache Tomcat 11
  ↓
FileDock
```

O `Dockerfile` utiliza:

```text
tomcat:11.0-jre21
```

O WAR gerado pelo Maven é copiado para o diretório de aplicações do Tomcat.

## Docker

O projeto possui Dockerfile para a aplicação e Docker Compose para orquestrar os serviços.

Serviços atuais:

```text
PostgreSQL
Tomcat + FileDock
```

O PostgreSQL utiliza volume Docker para persistência dos dados.

### Executar o projeto

Crie o arquivo `.env` a partir do exemplo:

```text
.env.example
```

Configure as variáveis:

```env
POSTGRES_DB=filedock
POSTGRES_USER=filedock
POSTGRES_PASSWORD=filedock_dev
```

Compile o projeto:

```bash
./mvnw clean package
```

No Windows:

```cmd
mvnw.cmd clean package
```

Depois execute:

```bash
docker-compose up -d --build
```

A aplicação ficará disponível em:

```text
http://localhost:8080/filedock
```

### Verificar os containers

```bash
docker-compose ps
```

### Visualizar os logs

```bash
docker-compose logs -f tomcat
```

## Configuração

As configurações sensíveis não são armazenadas diretamente no código-fonte.

O projeto utiliza variáveis de ambiente para a conexão com o PostgreSQL.

O arquivo:

```text
.env
```

é utilizado apenas localmente e não deve ser versionado.

O arquivo:

```text
.env.example
```

serve como referência para configuração do ambiente.

## Estrutura do projeto

```text
FileDock/
├── docs/
│   ├── architecture/
│   ├── api/
│   ├── database/
│   └── deployment/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── filedock/
│   │   │           ├── FileDockApplication.java
│   │   │           ├── ServletInitializer.java
│   │   │           │
│   │   │           ├── document/
│   │   │           │   ├── Document.java
│   │   │           │   ├── DocumentController.java
│   │   │           │   ├── DocumentRepository.java
│   │   │           │   ├── DocumentService.java
│   │   │           │   ├── ProcessingStatus.java
│   │   │           │   └── dto/
│   │   │           │       ├── CreateDocumentRequest.java
│   │   │           │       ├── DocumentResponse.java
│   │   │           │       └── UpdateDocumentRequest.java
│   │   │           │
│   │   │           ├── batch/
│   │   │           │   └── DocumentBatchConfiguration.java
│   │   │           │
│   │   │           ├── exception/
│   │   │           │   ├── ErrorResponse.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── ResourceNotFoundException.java
│   │   │           │
│   │   │           └── storage/
│   │   │               ├── FileStorageService.java
│   │   │               └── LocalFileStorageService.java
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__create_documents_table.sql
│   │       │       ├── V2__add_document_processing_status.sql
│   │       │       └── V3__add_document_storage_path.sql
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── filedock/
│                   ├── FileDockApplicationTests.java
│                   └── document/
│                       └── DocumentControllerTest.java
│
├── frontend/
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

## Testes

O projeto possui testes automatizados utilizando JUnit, Mockito e Spring Boot Test.

Os testes atuais cobrem:

* Consulta de documentos
* Consulta por ID
* Criação de documentos
* Atualização de documentos
* Documento inexistente
* Validação de dados inválidos
* Carregamento do contexto da aplicação

Executar os testes:

```bash
./mvnw test
```

No Windows:

```cmd
mvnw.cmd test
```

A suíte atual possui:

```text
7 testes
0 falhas
0 erros
```

## Validação em ambiente real

Além dos testes automatizados, a aplicação foi validada executando efetivamente através do ambiente Docker:

```text
FileDock
   ↓
Apache Tomcat 11
   ↓
Spring Boot
   ↓
Spring Batch
   ↓
PostgreSQL 17
```

Foram validados:

* Inicialização da aplicação
* Execução das migrações Flyway
* Comunicação com PostgreSQL
* Execução da API REST
* Cadastro de documentos
* Consulta de documentos
* Atualização de documentos
* Validação de requisições
* Tratamento de erros
* Execução do Spring Batch
* Alteração de `PENDING` para `PROCESSED`
* Documentação OpenAPI
* Swagger UI

## Próximas funcionalidades

* Upload real de arquivos
* Persistência do arquivo no armazenamento configurado
* Download de arquivos
* Exclusão de arquivos
* Substituição de arquivos existentes
* Testes específicos para armazenamento de arquivos
* Frontend Angular
* Melhorias de observabilidade e logs
* Evolução da infraestrutura para armazenamento externo
* Deploy em ambiente cloud

## Objetivo do projeto

O FileDock é um projeto de portfólio desenvolvido para demonstrar, de forma prática, conhecimentos em desenvolvimento backend Java e tecnologias utilizadas em aplicações corporativas.

O projeto busca demonstrar não apenas a criação de endpoints, mas também aspectos relacionados a:

* Persistência relacional
* Migrações de banco
* Processamento em lote
* Validação
* Tratamento de exceções
* Documentação de APIs
* Testes automatizados
* Empacotamento WAR
* Apache Tomcat externo
* Docker
* Docker Compose
* Configuração por ambiente
* Organização e evolução de uma aplicação Spring Boot
