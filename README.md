# 📬 EventMail

> Um estudo prático de **arquitetura de microsserviços** com comunicação assíncrona via **RabbitMQ**, simulando um fluxo real de cadastro de usuário com envio de e-mail de boas-vindas.

![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![CloudAMQP](https://img.shields.io/badge/CloudAMQP-FF6600?style=flat)

---

## 🎯 Sobre o projeto

O **EventMail** nasceu como um projeto de revisão e consolidação de conceitos de **microsserviços desacoplados via mensageria**. A ideia é simples na superfície, mas rica em detalhes de implementação: um usuário se cadastra, e esse cadastro **dispara automaticamente** um e-mail de boas-vindas sem que o serviço de cadastro precise saber nada sobre como o e-mail é enviado.

Esse desacoplamento é o coração do projeto, e é resolvido com **RabbitMQ** rodando na nuvem via **CloudAMQP**.

---

## 🧱 Arquitetura

![Fluxo da arquitetura](./architecture-flow.png)

O fluxo funciona assim:

1. **Usuário se cadastra** através de uma requisição à API do `user-microservice`.
2. O `user-microservice` **persiste os dados do usuário** no seu próprio banco PostgreSQL.
3. Após salvar, ele **publica uma mensagem** no RabbitMQ (via CloudAMQP) contendo as informações necessárias para o envio do e-mail.
4. O `email-microservice`, que está **escutando a fila** (`@RabbitListener`), recebe a mensagem.
5. O `email-microservice` **salva o registro do e-mail** no seu próprio banco PostgreSQL (independente do banco do `user-microservice`).
6. Por fim, o `email-microservice` **envia o e-mail de boas-vindas** de fato, via `JavaMailSender` (SMTP/Gmail).

> 💡 O ponto-chave aqui: os dois microsserviços **não se conhecem diretamente**. Toda a comunicação acontece de forma assíncrona através do broker. Se o `email-microservice` estiver fora do ar no momento do cadastro, a mensagem continua na fila esperando para ser processada — nada se perde.

---

## 🗂️ Estrutura dos microsserviços

| Serviço | Responsabilidade | Banco de dados |
|---|---|---|
| `user-microservice` | Cadastro de usuários, exposição da API REST, publicação de eventos | PostgreSQL próprio |
| `email-microservice` | Consumo de eventos, persistência do e-mail, envio via SMTP | PostgreSQL próprio |

Cada serviço é **independente**, com seu próprio banco de dados — reforçando um dos princípios centrais de microsserviços: **autonomia de dados**.

---

## 🛠️ Tecnologias utilizadas

- **Java** + **Spring Boot**
- **Spring AMQP** (integração com RabbitMQ)
- **RabbitMQ** via **CloudAMQP** (broker na nuvem)
- **PostgreSQL** (um banco por serviço)
- **Spring Data JPA / Hibernate**
- **JavaMailSender** (envio de e-mail via SMTP/Gmail)
- **Maven**

---

## ▶️ Como executar o projeto

### Pré-requisitos
- Java 17+
- Maven
- Uma instância do CloudAMQP (existe um [plano gratuito](https://www.cloudamqp.com/) — *Little Lemur* — suficiente para testes)
- PostgreSQL (uma instância para cada microsserviço, ou dois schemas/bancos separados)
- Uma conta de e-mail com senha de app habilitada (caso use Gmail)

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/EventMail.git
cd EventMail
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` (ou configure as variáveis no seu sistema/IDE) com:

```bash
DB_USER=postgres
DB_PASSWORD=sua_senha
ADDRESS_RABBITMQ=amqps://usuario:senha@host.rmq.cloudamqp.com/vhost
EMAIL=seu-email@gmail.com
PASSWORD_EMAIL_APP=sua-senha-de-app-do-gmail
```

> ⚠️ Nunca commite o `.env` ou credenciais reais. Adicione `.env` ao `.gitignore`.

**`user-microservice/src/main/resources/application.properties`**
```properties
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/ms-user
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

spring.rabbitmq.addresses=${ADDRESS_RABBITMQ}
broker.queue.email.name=default.email
```

**`email-microservice/src/main/resources/application.properties`**
```properties
server.port=8082

spring.datasource.url=jdbc:postgresql://localhost:5432/ms-email
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

spring.rabbitmq.addresses=${ADDRESS_RABBITMQ}
broker.queue.email.name=default.email

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL}
spring.mail.password=${PASSWORD_EMAIL_APP}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. Rode os serviços
```bash
# Em terminais separados
cd user-microservice && ./mvnw spring-boot:run
cd email-microservice && ./mvnw spring-boot:run
```

### 4. Teste o fluxo completo
Envie uma requisição `POST` para o `user-microservice`:

```http
POST http://localhost:8081/users
Content-Type: application/json

{
  "name": "Igor Rafael",
  "email": "igor@gmail.com"
}
```

Resposta esperada:
```json
{
  "userId": "2abd54d4-3d44-4460-9d2a-667fceced361",
  "name": "Igor Rafael",
  "email": "igor@gmail.com"
}
```

Logo em seguida, o `email-microservice` consome a mensagem da fila, persiste o registro e dispara o e-mail de boas-vindas automaticamente. 🎉

---

## 📚 O que esse projeto me ajudou a fixar

- Comunicação assíncrona entre serviços via **producer/consumer**
- Configuração de exchanges, filas e routing keys no RabbitMQ
- Uso do **CloudAMQP** como alternativa gerenciada ao RabbitMQ local
- Separação real de responsabilidades e dados entre microsserviços
- Tratamento de eventos com `@RabbitListener` e `@Payload`
- Envio de e-mails via SMTP integrado a um fluxo orientado a eventos

---

## 🚧 Próximos passos

- [ ] Adicionar Dead Letter Queue (DLQ) para mensagens com falha de processamento
- [ ] Implementar retry automático no consumer
- [ ] Adicionar testes de integração com Testcontainers
- [ ] Dockerizar os dois microsserviços
- [ ] Adicionar documentação da API com Swagger/OpenAPI

---

## 👤 Autor

**Igor Rafael Silva Coelho**
Full Stack Software Engineer (Java/Angular)

---

⭐ Se esse projeto te ajudou a entender melhor microsserviços e mensageria, deixa uma estrela no repositório!

