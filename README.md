# mycoworking

## Visao geral
Aplicacao para gestao de coworking, com API REST em Spring Boot e persistencia via Spring Data JPA. O foco e clareza de responsabilidades, separacao de camadas e integracao simples com banco de dados externo. O deploy e planejado para o App Platform da DigitalOcean.

## Tech Stack
- Java 17
- Spring Boot (Web, Validation)
- Spring Data JPA + Hibernate
- PostgreSQL (banco externo)
- Swagger/OpenAPI para documentacao de API
- Docker Compose para ambiente local
- DigitalOcean App Platform para deploy

## Arquitetura
A solucao adota arquitetura em camadas (controller, service, repository), com controllers expondo endpoints REST e services concentrando regras de negocio. A persistencia usa JPA, com entidades mapeadas no dominio e repositorios para o acesso aos dados.

O banco de dados e externo: PostgreSQL, configurado por variaveis de ambiente no arquivo [src/main/resources/application.properties](src/main/resources/application.properties) (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD). Essa abordagem facilita a implantacao em ambientes distintos e desacopla a aplicacao do ambiente local.

## Arquitetura de pastas
A estrutura de pastas separa responsabilidades e torna a manutencao previsivel:

- [src/main/java/com/mycoworking/app/controller](src/main/java/com/mycoworking/app/controller): endpoints REST.
- [src/main/java/com/mycoworking/app/service](src/main/java/com/mycoworking/app/service): regras de negocio.
- [src/main/java/com/mycoworking/app/repository](src/main/java/com/mycoworking/app/repository): acesso a dados (JPA).
- [src/main/java/com/mycoworking/app/model](src/main/java/com/mycoworking/app/model): entidades de dominio.
- [src/main/java/com/mycoworking/app/dto](src/main/java/com/mycoworking/app/dto): objetos de transferencia.
- [src/main/java/com/mycoworking/app/config](src/main/java/com/mycoworking/app/config): configuracoes da aplicacao.
- [src/main/java/com/mycoworking/app/helpers](src/main/java/com/mycoworking/app/helpers) e [src/main/java/com/mycoworking/app/utils](src/main/java/com/mycoworking/app/utils): utilitarios e apoio.
- [src/main/resources](src/main/resources): configuracoes e recursos estaticos/templates.
- [src/test](src/test): testes automatizados e recursos de teste.

## Como rodar localmente
Existem duas formas principais de subir a aplicacao:

### Com Docker (recomendado)
1. Copie o arquivo [.env.example](.env.example) para [.env](.env) e ajuste as variaveis se necessario.
2. Suba os containers com `docker compose up -d` usando o arquivo [docker-compose.yml](docker-compose.yml).
3. A API estara disponivel em http://localhost:8080 .

### Com Maven
1. Configure as variaveis de ambiente de banco (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD).
2. Execute `./mvnw spring-boot:run`.
3. A API estara disponivel em http://localhost:8080 .

## Deploy (DigitalOcean App Platform)
1. Crie um app na DigitalOcean e conecte este repositorio.
2. Selecione o modo de build via Dockerfile (arquivo [Dockerfile](Dockerfile)) ou buildpack Java.
3. Defina as variaveis de ambiente de producao abaixo.
4. Garanta que a aplicacao exponha a porta 8080.
5. Efetue o deploy e acompanhe os logs pelo painel do App Platform.

### Variaveis de ambiente (producao)
Configure as variaveis abaixo no App Platform para apontar para o banco externo:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

## Endpoints principais
Alguns endpoints para demonstracao no Swagger:

- `GET /api/rooms/`: lista salas.
- `GET /api/rooms/{id}`: detalha uma sala.
- `POST /api/rooms/`: cria uma sala.
- `PUT /api/rooms/`: atualiza uma sala.
- `DELETE /api/rooms/{id}`: remove uma sala.
- `GET /api/rooms/free-slots?date=dd/MM/aaaa&roomId=<opcional>`: consulta horarios livres.

- `GET /api/reserves/`: lista reservas.
- `GET /api/reserves/{id}`: detalha uma reserva.
- `POST /api/reserves/`: cria uma reserva.
- `PUT /api/reserves/`: atualiza uma reserva.
- `DELETE /api/reserves/{id}`: remove uma reserva.

## Testar via Swagger
1. Suba a aplicacao localmente.
2. Abra o Swagger UI em http://localhost:8080/swagger-ui/index.html .
3. Escolha um endpoint, clique em "Try it out" e execute as requisicoes para validar o comportamento da API.

### Fluxo de demonstracao (exemplo)
1. Crie uma sala:

```json
POST /api/rooms/
{
	"name": "Sala Ocean",
	"kind": "MEETING",
	"description": "Sala com projetor e quadro branco"
}
```

2. Crie uma reserva para a sala criada (substitua o id retornado):

```json
POST /api/reserves/
{
	"room": {
		"id": "<ID_DA_SALA>"
	},
	"startTime": "2026-06-10T10:00:00-03:00",
	"endTime": "2026-06-10T11:00:00-03:00"
}
```

3. Verifique horarios livres:

```
GET /api/rooms/free-slots?date=10/06/2026&roomId=<ID_DA_SALA>
```
