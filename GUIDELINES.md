 # GUIDELINES — Documentação Técnica e Operacional (Syntaze Backend)

 Este arquivo reúne informações práticas para desenvolvedores e operadores: como configurar, executar, variáveis de ambiente necessárias, endpoints principais, problemas conhecidos e passos para deploy.

1. Visão geral
- Projeto: Syntaze — backend (API REST)
- Stack: Java 17+, Spring Boot, Spring Security, Spring Data JPA, Auth0 Java JWT
- Objetivo: receber stories (com screenshot) enviados por bots, persistir dados e fornecer endpoints para consumo por clientes (mobile, dashboards).

2. Como rodar localmente
Pré-requisitos:
- JDK 17+
- Maven (ou usar wrapper `mvnw.cmd` incluído)
- Banco de dados relacional (ex.: Postgres, MySQL) ou H2 para testes

Configuração mínima (exemplo usando Postgres):
- No `src/main/resources/application-dev.properties` (ou variáveis de ambiente):
```
spring.datasource.url=jdbc:postgresql://localhost:5432/syntaze
spring.datasource.username=synuser
spring.datasource.password=synpass
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```
- Configurar `jwt.secret` e demais propriedades:
```
jwt.secret=uma-chave-secreta-muito-segura
jwt.expiration=86400000
jwt.magic.expiration=900000
app.uploads.dir=uploads
```

Build e execução (Windows PowerShell):
```powershell
# Compilar
.\mvnw.cmd -DskipTests clean package

# Executar
.\mvnw.cmd spring-boot:run
```

3. Variáveis de ambiente / propriedades importantes
- `jwt.secret` (obrigatório em produção): segredo para assinar tokens JWT
- `jwt.expiration` (ms): tempo de vida do token de acesso
- `jwt.magic.expiration` (ms): tempo de vida do magic token
- `app.uploads.dir`: diretório onde screenshots são armazenados (default: `uploads`)
- `spring.datasource.*`: URL, usuário, senha, driver

4. Endpoints principais (resumo)
- POST /auth/magic/request — solicita magic link (body: {"email": "..."})
- GET /auth/magic/confirm?token=... — confirma token mágico (retorna token de acesso ou register token)
- POST /auth/register — registra usuário com senha (body: {email,password,registerToken?})
- POST /auth/login — login por email/senha (body: {email,password})
- POST /bots/data — ingestão de story (multipart/form-data). Header: `X-BOT-API-KEY` obrigatório
  Campos multipart esperados: `profileId` (opcional), `externalId` (opcional), `likes` (opcional), `postedAt` (opcional ISO-8601), `screenshot` (arquivo obrigatório)
- GET /stories — listagem paginada (autenticada)
- GET /stories/{id} — metadados do story (autenticada)
- GET /stories/{id}/image — download do screenshot (autenticada)
- GET /stories/{id}/info — download JSON do story (autenticada)

5. Armazenamento de arquivos
- Implementação atual: `LocalFileStorageService`, grava arquivos no disco em `app.uploads.dir`.
- Em produção, considere integrar com S3/Blob storage e atualizar `FileStorageService` (criar nova implementação e registrar via Spring).

6. Email
- Implementação atual: `ConsoleEmailService` (apenas loga links). Para produção, implementar `SmtpEmailService` usando `JavaMailSender` ou integrar com provedores (SendGrid, SES, etc.).

7. Banco de dados
- Entidades: `users`, `bots`, `instagram_profiles`, `instagram_stories`.
- `spring.jpa.hibernate.ddl-auto` pode ser `validate`/`update`/`none` conforme política. Em produção, favor usar migrações (Flyway/Liquibase) — não presente no projeto atual.

8. Problemas conhecidos e correções recomendadas
- InstagramStoryEntity.toDomain não popula profileId
  - Local: `src/main/java/com/syntaze/backend/infra/repository/entity/InstagramStoryEntity.java`
  - Correção sugerida no método `toDomain()`:
    - Se `this.profile != null` então setar `domain.setProfileId(this.profile.getId())` (ou equivalente com o builder/record usado).
- `jwt.secret` default está em `application.properties` com valor `replace-with-secure-secret` — troque antes de uso em produção.
- Política de criação de usuário: o endpoint `/auth/register` permite criar usuário sem `registerToken` (verificar requisito de segurança).
- Sem refresh tokens — implementar se necessário.

9. Segurança
- Spring Security configurado com JWT filter (`JwtFilter`) que valida header `Authorization: Bearer <token>`.
- Endpoints públicos: `/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/actuator/**`.
- Outros endpoints requerem autenticação. Se precisar restringir por role, configure `ConfigSecurity`/`@PreAuthorize`.

10. Boas práticas para contribuições
- Sempre rode `mvnw.cmd -DskipTests clean package` antes de abrir PR.
- Adicione testes unitários para novas regras de negócio.
- Documente alterações relevantes em `GUIDELINES.md` ou em documentação / tickets.

11. Checklist de preparação para produção
- [ ] Substituir `jwt.secret` por segredo seguro e diferente por ambiente
- [ ] Migrar `ConsoleEmailService` para serviço real de e-mail
- [ ] Configurar armazenamento de arquivos (S3/Blob) ou garantir redundância/backup do diretório de uploads
- [ ] Configurar banco de dados com backups e aplicar migrações (usar Flyway/Liquibase)
- [ ] Revisar política de criação de usuário e deduplicação de stories (externalId)
- [ ] Implementar refresh token ou outra estratégia de renovação de sessão, se necessário

12. Contatos e próximos passos
- Se desejar, posso:
  - Gerar exemplos curl/postman collection para todos os endpoints
  - Criar tarefa/PR para corrigir `InstagramStoryEntity.toDomain()`
  - Extrair `pom.xml` e listar dependências e versões

---
Arquivo gerado automaticamente a partir da análise do código-fonte. Se algo estiver incorreto ou incompleto, atualize este documento ou solicite que eu aplique as correções necessárias.

