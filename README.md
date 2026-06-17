# backend

 
Syntaze — Backend

Este repositório contém o backend da aplicação Syntaze, uma API REST desenvolvida em Java (Spring Boot) para ingestão, persistência e consulta de dados de stories do Instagram enviados por bots/scrapers, além de autenticação de usuários (magic link e senha) e gestão básica de bots.

Arquivos principais e documentação técnica
- Documentação técnica detalhada e análise do código: `GUIDELINES.md` (arquivo recém-criado neste repositório).

Sumário rápido
- Tecnologias: Java 17+, Spring Boot (Web, Security, Data JPA), JPA (jakarta.persistence), Auth0 Java JWT, SLF4J, OpenAPI/Swagger.
- Endpoints principais: `/auth/**`, `/bots/data`, `/stories`, `/stories/{id}`, `/stories/{id}/image`, `/stories/{id}/info`.
- Persistência: JPA (entidades para users, bots, instagram_profiles, instagram_stories). Configure `spring.datasource.*` para usar um banco relacional.
- Armazenamento de arquivos: implementação local (`LocalFileStorageService`) grava uploads em disco no diretório configurado por `app.uploads.dir` (default: `uploads`).
- Envio de e-mail: implementação padrão `ConsoleEmailService` apenas loga o link. Em produção troque por implementação SMTP ou serviço real de e-mail.

Problemas conhecidos / observações importantes
- Bug crítico detectado: `InstagramStoryEntity.toDomain()` não popula `profileId` no objeto de domínio. Consequência: respostas de endpoints podem retornar `profileId` nulo mesmo quando a relação existe no banco. Recomenda-se corrigir o mapeamento em `infra.repository.entity.InstagramStoryEntity` para preencher `profileId` quando `profile != null`.
- `application.properties` contém `jwt.secret` com valor padrão `replace-with-secure-secret`. Troque por um segredo seguro em produção.
- Não há refresh token implementado — avalie necessidade de fluxo de refresh se a aplicação exigir sessões longas.
- Não há deduplicação por `externalId` para stories — se necessário, implemente checagem na persistência.
- `spring.datasource.*` não está configurado no repositório; adicione as propriedades adequadas por ambiente (`application-{profile}.properties`) antes de executar com JPA habilitado.

Como executar localmente (modo rápido)
1) Configurar variáveis / properties essenciais (ex.: em `src/main/resources/application.properties` ou variáveis de ambiente):
   - `jwt.secret` (obrigatório em ambiente que usa JWT)
   - `jwt.expiration` (ms)
   - `jwt.magic.expiration` (ms)
   - `app.uploads.dir` (opcional, default: `uploads`)
   - `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `spring.datasource.driver-class-name` (se usar banco externo)

2) Build e execução (Windows PowerShell):

```powershell
# Compilar
.\mvnw.cmd -DskipTests clean package

# Executar
.\mvnw.cmd spring-boot:run
```

Observação: o projeto usa Maven wrapper (`mvnw.cmd`) para Windows.

Testes
- Existe um módulo de testes unitários básicos em `src/test/java`. Para executar os testes:

```powershell
.\mvnw.cmd test
```

Onde encontrar o código
- Controllers: `src/main/java/com/syntaze/backend/web/controller`
- Usecases / Services: `src/main/java/com/syntaze/backend/application/usecase`
- Repositórios de domínio: `src/main/java/com/syntaze/backend/domain/repository`
- Implementações JPA / entidades: `src/main/java/com/syntaze/backend/infra/repository` (inclui `entity` e `*Repository`)
- JWT / autenticação: `src/main/java/com/syntaze/backend/infra/jwt`
- Serviços externos (Email / FileStorage): `src/main/java/com/syntaze/backend/infra/external`
- Configurações e handlers de exceção: `src/main/java/com/syntaze/backend/infra/config`

Contribuição rápida
- Antes de abrir PR: execute `mvnw.cmd -DskipTests clean package` e garanta que não há erros de compilação.
- Siga as convenções do projeto (Java 17+, usage de records e JPA entities existentes).

Licença
- Não há arquivo LICENSE neste repositório. Adicione informação de licença se necessário.

Para documentação técnica completa, passos de deploy, variáveis de ambiente detalhadas e guidelines de contribuição e desenvolvimento, veja `GUIDELINES.md`.
