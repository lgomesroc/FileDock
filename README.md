# FileDock

Sistema de gerenciamento de documentos desenvolvido com Java e Spring Boot, com persistência em PostgreSQL, processamento em lote utilizando Spring Batch e execução em Apache Tomcat externo.

O projeto foi construído com foco em uma aplicação backend real, documentada e executável em ambiente Docker, utilizando uma arquitetura simples e organizada para facilitar manutenção, testes e evolução.

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

### Gerenciamento de documentos

* Cadastro de documentos
* Consulta de todos os documentos
* Consulta de documento por ID
* Atualização de documentos
* Exclusão de documentos
* Validação dos dados de entrada
* Tratamento de documentos inexistentes
* Controle de status de processamento
* Registro das datas de criação e atualização

### Armazenamento de arquivos

O FileDock possui uma camada específica para armazenamento de arquivos, separada da lógica de gerenciamento dos documentos.

Atualmente, o armazenamento local é implementado através de `LocalFileStorageService`.

A aplicação suporta:

* Upload de arquivos
* Armazenamento físico dos arquivos
* Geração de nomes únicos para os arquivos armazenados
* Registro do caminho de armazenamento no banco
* Download de arquivos
* Exclusão de arquivos
* Substituição de arquivos existentes
* Validação do caminho de armazenamento
* Persistência dos arquivos através de volume Docker

O arquivo original não é utilizado diretamente como nome físico de armazenamento. O sistema gera um identificador único utilizando `UUID`, preservando a extensão do arquivo.

Exemplo:

```text
Arquivo enviado:
contrato.pdf

Arquivo armazenado:
550e8400-e29b-41d4-a716-446655440000.pdf
```

O banco mantém o caminho do arquivo armazenado através do campo:

```text
storage_path
```

### Processamento

Os documentos cadastrados inicialmente recebem o status:

```text
PENDING
```

O Spring Batch identifica os documentos pendentes e realiza o processamento, alterando o status para:

```text
PROCESSED
```

Também é registrada a data de conclusão do processamento no campo:

```text
processedAt
```

Quando um arquivo é substituído, o documento volta para:

```text
PENDING
```

e o campo `processedAt` é reiniciado para `null`, permitindo que o novo arquivo passe novamente pelo processamento.

## API REST

A API principal está disponível através do contexto:

```text
/filedock
```

Endpoints disponíveis:

```text
GET    /api/documents
GET    /api/documents/{id}
POST   /api/documents
POST   /api/documents/upload
PUT    /api/documents/{id}
PUT    /api/documents/{id}/file
GET    /api/documents/{id}/download
DELETE /api/documents/{id}
```

### Cadastro por JSON

O endpoint:

```text
POST /api/documents
```

permite cadastrar um documento utilizando seus metadados.

### Upload

O endpoint:

```text
POST /api/documents/upload
```

recebe os metadados e o arquivo através de `multipart/form-data`.

Exemplo:

```text
title
description
file
```

### Download

O endpoint:

```text
GET /api/documents/{id}/download
```

retorna o arquivo físico associado ao documento.

A resposta utiliza `Content-Disposition` para disponibilizar o arquivo como download.

### Substituição de arquivo

O endpoint:

```text
PUT /api/documents/{id}/file
```

substitui o arquivo físico associado a um documento existente.

O fluxo executado é:

```text
Arquivo antigo
      ↓
Novo arquivo recebido
      ↓
Novo arquivo armazenado
      ↓
Documento atualizado
      ↓
Arquivo antigo removido
```

O documento permanece com o mesmo ID, enquanto os metadados relacionados ao arquivo são atualizados.

### Exclusão

O endpoint:

```text
DELETE /api/documents/{id}
```

remove o documento do banco e também remove o arquivo físico associado ao armazenamento.

## DTOs

A API utiliza DTOs para separar os modelos de entrada e saída da entidade de persistência.

Principais DTOs:

```text
CreateDocumentRequest
UpdateDocumentRequest
DocumentResponse
DocumentDownload
ErrorResponse
```

Essa separação evita expor diretamente a entidade JPA através da API e permite controlar os dados recebidos e retornados pelos endpoints.

## Validação

As requisições de criação e atualização utilizam Bean Validation para validar:

* Campos obrigatórios
* Tamanho máximo de campos
* Valores numéricos positivos
* Dados inválidos enviados pela API

As respostas de erro relacionadas à validação são padronizadas através do tratamento global de exceções.

Exemplo de resposta:

```json
{
  "error": "Dados inválidos",
  "fields": {
    "title": "must not be blank",
    "fileSize": "must be greater than 0"
  }
}
```

Documentos inexistentes retornam HTTP `404`.

## Documentação da API

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
* Operações de upload
* Operações de download
* Substituição de arquivos

## Banco de dados

O projeto utiliza PostgreSQL 17 para persistência dos documentos.

As alterações do banco são controladas pelo Flyway.

Migrações atualmente existentes:

```text
V1__create_documents_table.sql
V2__add_document_processing_status.sql
V3__add_document_storage_path.sql
```

### Estrutura da tabela `documents`

A tabela possui dados relacionados a:

* Identificação do documento
* Título
* Descrição
* Nome original do arquivo
* Tipo MIME
* Tamanho do arquivo
* Caminho de armazenamento
* Data de criação
* Data de atualização
* Status de processamento
* Data de processamento

O Hibernate utiliza:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Dessa forma, a estrutura do banco é controlada pelas migrações do Flyway, enquanto o Hibernate valida se o modelo JPA está compatível com o schema existente.

## Flyway

As migrações são executadas automaticamente durante a inicialização da aplicação.

Fluxo:

```text
Aplicação
    ↓
Flyway
    ↓
Verificação das migrações
    ↓
Execução das migrações pendentes
    ↓
PostgreSQL
```

As três migrações atuais foram executadas e validadas no banco PostgreSQL utilizado pelo ambiente Docker.

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
JpaItemWriter
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

O reader busca documentos com:

```text
ProcessingStatus.PENDING
```

Os documentos são processados em chunks de:

```text
10 itens
```

A configuração está centralizada em:

```text
DocumentBatchConfiguration
```

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

O WAR gerado pelo Maven é copiado para o diretório de aplicações do Tomcat:

```text
/usr/local/tomcat/webapps/filedock.war
```

A aplicação é acessada através de:

```text
http://localhost:8080/filedock
```

## Docker

O projeto possui Dockerfile para a aplicação e Docker Compose para orquestrar os serviços.

Serviços atuais:

```text
PostgreSQL 17
Tomcat 11 + FileDock
```

O Docker Compose configura:

* PostgreSQL
* Tomcat
* Rede entre os serviços
* Variáveis de ambiente
* Healthcheck do PostgreSQL
* Volume para dados do PostgreSQL
* Volume para armazenamento dos arquivos

### Volumes

O PostgreSQL utiliza o volume:

```text
filedock-postgres-data
```

para manter os dados do banco.

O FileDock utiliza o volume:

```text
filedock-storage
```

montado no container em:

```text
/app/uploads
```

Isso permite que os arquivos armazenados não dependam exclusivamente do filesystem efêmero do container.

## Executar o projeto

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

O arquivo `.env` é utilizado apenas localmente e não deve ser versionado.

### Linux

Compile o projeto:

```bash
./mvnw clean package
```

Execute os containers:

```bash
docker-compose up -d --build
```

### Windows

Compile o projeto:

```cmd
mvnw.cmd clean package
```

Execute os containers:

```cmd
docker-compose up -d --build
```

A aplicação ficará disponível em:

```text
http://localhost:8080/filedock
```

Swagger UI:

```text
http://localhost:8080/filedock/swagger-ui/index.html
```

## Verificar os containers

```bash
docker-compose ps
```

Exemplo de arquitetura em execução:

```text
filedock-tomcat
       │
       │ JDBC
       ↓
filedock-postgres

filedock-tomcat
       │
       │ arquivos
       ↓
filedock-storage
```

## Visualizar os logs

Para acompanhar os logs do Tomcat:

```bash
docker-compose logs -f tomcat
```

Para visualizar os logs do PostgreSQL:

```bash
docker-compose logs -f postgres
```

## Configuração

As configurações específicas do ambiente não são armazenadas diretamente no código-fonte.

O projeto utiliza variáveis de ambiente para a conexão com o PostgreSQL:

```text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
```

O local de armazenamento dos arquivos pode ser configurado através de:

```text
FILE_STORAGE_LOCATION
```

No ambiente Docker, o armazenamento é configurado como:

```text
/app/uploads
```

O arquivo:

```text
.env
```

é utilizado apenas localmente e está incluído no `.gitignore`.

O arquivo:

```text
.env.example
```

serve como referência para configuração do ambiente e é versionado no repositório.

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
│   │   │           │       ├── DocumentDownload.java
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
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── filedock/
│                   ├── FileDockApplicationTests.java
│                   └── document/
│                       ├── DocumentControllerTest.java
│                       └── DocumentServiceTest.java
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

O projeto possui testes automatizados utilizando JUnit, Mockito e recursos de teste do Spring.

Os testes atuais cobrem:

### Controller

* Consulta de documentos
* Consulta de documento por ID
* Criação de documentos
* Upload de arquivos
* Atualização de documentos
* Documento inexistente
* Validação de dados inválidos
* Download de arquivos
* Exclusão de documentos
* Substituição de arquivos

### Service

* Exclusão de documento e arquivo armazenado
* Falha na exclusão do arquivo
* Documento inexistente
* Substituição de arquivo
* Substituição de arquivo de documento inexistente
* Arquivo vazio
* Falha no armazenamento do novo arquivo

### Contexto

Também existe teste de carregamento do contexto da aplicação.

Executar os testes:

### Linux

```bash
./mvnw test
```

### Windows

```cmd
mvnw.cmd test
```

A suíte automatizada atual possui:

```text
18 testes
0 falhas
0 erros
0 testes ignorados
```

Resultado validado:

```text
BUILD SUCCESS
```

## Validação em ambiente real

Além dos testes automatizados, a aplicação foi validada executando efetivamente através do ambiente Docker.

Arquitetura utilizada:

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

Foram validados em ambiente real:

* Inicialização da aplicação
* Execução das migrações Flyway
* Comunicação com PostgreSQL
* Execução da API REST
* Cadastro de documentos
* Consulta de documentos
* Consulta por ID
* Atualização de documentos
* Validação de requisições
* Tratamento de documentos inexistentes
* Execução do Spring Batch
* Alteração de `PENDING` para `PROCESSED`
* Documentação OpenAPI
* Swagger UI
* Upload real de arquivo
* Persistência física do arquivo
* Persistência através de volume Docker
* Download do arquivo
* Exclusão do documento
* Exclusão do arquivo físico
* Substituição de arquivo
* Remoção do arquivo antigo após substituição
* Download do novo arquivo após substituição

### Fluxo real de armazenamento

O fluxo de upload validado foi:

```text
Cliente
   ↓
POST /api/documents/upload
   ↓
DocumentController
   ↓
DocumentService
   ↓
FileStorageService
   ↓
LocalFileStorageService
   ↓
/app/uploads
   ↓
Docker Volume
```

Os metadados do arquivo são persistidos no PostgreSQL enquanto o conteúdo físico permanece no armazenamento configurado.

### Fluxo real de download

```text
Cliente
   ↓
GET /api/documents/{id}/download
   ↓
DocumentService
   ↓
FileStorageService
   ↓
Arquivo físico
   ↓
HTTP Response
```

O download foi validado verificando:

* HTTP `200`
* `Content-Disposition`
* `Content-Type`
* `Content-Length`
* Conteúdo do arquivo retornado

### Fluxo real de substituição

A substituição de arquivo também foi validada ponta a ponta:

```text
Arquivo original
       ↓
Novo arquivo enviado
       ↓
Novo arquivo armazenado
       ↓
Registro atualizado no PostgreSQL
       ↓
Arquivo original removido
       ↓
Download do novo arquivo
```

Foi confirmado que, após a substituição, o armazenamento contém o novo arquivo e o download retorna o novo conteúdo.

## Próximas funcionalidades

* Tratamento específico das exceções de armazenamento na API
* Testes adicionais para cenários de erro do armazenamento
* Frontend Angular
* Melhorias de observabilidade e logs
* Evolução da infraestrutura para armazenamento externo
* Deploy em ambiente cloud
* Melhorias de segurança e configuração para ambiente de produção

## Objetivo do projeto

O FileDock é um projeto de portfólio desenvolvido para demonstrar, de forma prática, conhecimentos em desenvolvimento backend Java e tecnologias utilizadas em aplicações corporativas.

O projeto busca demonstrar não apenas a criação de endpoints, mas também aspectos relacionados a:

* Desenvolvimento de APIs REST
* Persistência relacional
* Spring Data JPA
* Migrações de banco de dados
* Processamento em lote com Spring Batch
* Validação de dados
* Tratamento de exceções
* Documentação de APIs
* Testes automatizados
* Upload e download de arquivos
* Gerenciamento do ciclo de vida dos arquivos
* Empacotamento WAR
* Apache Tomcat externo
* Docker
* Docker Compose
* Volumes persistentes
* Configuração por ambiente
* Separação de responsabilidades
* Organização e evolução de uma aplicação Spring Boot

O projeto foi estruturado para permitir a evolução gradual do backend para uma aplicação completa, com frontend Angular e posterior execução em ambiente cloud.
