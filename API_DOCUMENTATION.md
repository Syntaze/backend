# Syntaze Backend — API Reference e Fluxo de Autenticação

Este documento descreve os endpoints expostos pela API backend do Syntaze, o fluxo de autenticação esperado (magic link + JWT), cabeçalhos, formatos de request/response, exemplos curl e observações de segurança. Use-o como base para clientes (mobile, dashboards, bots) e para integração com outros projetos.

Versão: 1.0
Atualizado em: 2026-06-16

Sumário
- 1) Visão geral do auth
- 2) Cabeçalhos e propriedades comuns
- 3) Endpoints de autenticação
- 4) Endpoints para bots (ingestão)
- 5) Endpoints de stories (consumo)
- 6) Formatos de erro e códigos HTTP
- 7) Exemplo de fluxo completo (curl)
- 8) Observações operacionais e segurança

--------------------------------------------------------------------------------

1) Visão geral do fluxo de autenticação

O sistema suporta dois fluxos principais de autenticação para usuários humanos:

- Magic link
  1. Cliente chama POST /auth/magic/request com email.
  2. Backend gera um token "magic" (curta validade) e envia um link por e-mail (implementação atual apenas loga no console com `ConsoleEmailService`).
  3. Usuário clica no link (ou cliente consome o GET /auth/magic/confirm?token=<magicToken>). O backend valida o magic token.
  4. Se o e-mail já estiver associado a usuário com credenciais, é retornado um access token JWT. Se não, é retornado um register token (token de curta duração) para completar o cadastro.

- Login por email/senha
  - POST /auth/login retorna um JWT de acesso se credenciais estiverem corretas.

Tokens
- Access token: JWT assinado com `jwt.secret`. Cabeçalhos autenticados devem usar `Authorization: Bearer <token>`.
- Expiração do access token é definida por `jwt.expiration` (ms).
- Register token / Magic token: curto prazo, definido por `jwt.magic.expiration` (ms).

--------------------------------------------------------------------------------

2) Cabeçalhos e propriedades comuns

- Authorization: Bearer <token> — token JWT padrão para usuários.
- X-BOT-API-KEY: <bot-api-key> — header obrigatório para endpoints usados por bots (ex.: POST /bots/data).
- Content-Type: application/json ou multipart/form-data conforme o endpoint.
- Accept: application/json

Propriedades de configuração relevantes (variáveis de ambiente / application.properties):
- jwt.secret (obrigatório em produção)
- jwt.expiration (ms)
- jwt.magic.expiration (ms)
- app.uploads.dir — diretório de uploads locais

--------------------------------------------------------------------------------

3) Endpoints de autenticação

- POST /auth/magic/request
  - Descrição: solicita envio de magic link para o e-mail informado.
  - Auth: público
  - Request (application/json):
    {
      "email": "user@example.com"
    }
  - Response (200):
    { "message": "magic link sent" }
  - Observações: implementação atual apenas loga o link no console.

- GET /auth/magic/confirm?token=<magicToken>
  - Descrição: valida o magic token e retorna um access token ou register token.
  - Auth: público
  - Response (200) possível 1 - acesso concedido:
    {
      "accessToken": "eyJhbGci...",
      "expiresIn": 86400000
    }
  - Response (200) possível 2 - precisa completar cadastro:
    {
      "registerToken": "...",
      "expiresIn": 900000
    }

- POST /auth/register
  - Descrição: registra usuário com email e senha. Pode aceitar `registerToken` quando vindo de magic flow.
  - Auth: público
  - Request (application/json):
    {
      "email": "user@example.com",
      "password": "P@ssw0rd",
      "registerToken": "optional-register-token"
    }
  - Response (201):
    { "message": "user created" }
  - Observações: revisar política de criação de usuário se `registerToken` for obrigatório em seu ambiente.

- POST /auth/login
  - Descrição: login com email e senha.
  - Auth: público
  - Request (application/json):
    { "email": "user@example.com", "password": "P@ssw0rd" }
  - Response (200):
    { "accessToken": "eyJhbGci...", "expiresIn": 86400000 }

--------------------------------------------------------------------------------

4) Endpoints para bots (ingestão de stories)

Nota: Bots autenticam-se com `X-BOT-API-KEY`. Esse header é obrigatório para ingestão.

- POST /bots/data
  - Descrição: Recebe dados de story com screenshot (multipart/form-data).
  - Auth: header `X-BOT-API-KEY` obrigatório
  - Content-Type: multipart/form-data
  - Campos multipart esperados:
    - profileId (UUID, opcional) — id do `instagram_profile` no sistema
    - externalId (string, opcional) — id externa do story para deduplicação
    - likes (integer, opcional)
    - postedAt (string, opcional) — ISO-8601 datetime
    - screenshot (file, obrigatório) — imagem do screenshot
  - Exemplo curl (Linux/macOS):
    curl -X POST "https://api.example.com/bots/data" \
      -H "X-BOT-API-KEY: BOT_KEY_HERE" \
      -F "profileId=00000000-0000-0000-0000-000000000000" \
      -F "externalId=ig_story_123" \
      -F "likes=42" \
      -F "postedAt=2026-06-16T12:34:56Z" \
      -F "screenshot=@/path/to/screenshot.png"
  - Resposta (201):
    { "id": "<story-uuid>", "message": "story saved" }
  - Observações:
    - O backend armazena o arquivo em `app.uploads.dir` usando `LocalFileStorageService` por padrão.
    - Para produção, recomenda-se integração com S3/Blob e validação de quotas/tamanho do arquivo.

--------------------------------------------------------------------------------

5) Endpoints de stories (consumo)

Todos os endpoints abaixo exigem autenticação via `Authorization: Bearer <token>` (exceto se explicitado diferente).

- GET /stories
  - Descrição: retorna listagem paginada de stories.
  - Auth: Bearer JWT
  - Query params comuns: page (int), size (int), sort (ex.: createdAt,desc), profileId (UUID, opcional), externalId
  - Exemplo de resposta:
    {
      "content": [ { "id": "uuid", "profileId": "uuid", "externalId": "...", "screenshotLink": "/files/..", ... } ],
      "page": 0,
      "size": 20,
      "totalElements": 123
    }

- GET /stories/{id}
  - Descrição: obtém metadados do story (JSON)
  - Auth: Bearer JWT
  - Response (200): objeto JSON com os campos do story (id, profileId, externalId, likes, timestamps, screenshotLink)

- GET /stories/{id}/image
  - Descrição: baixa o arquivo de screenshot (ou redireciona para storage)
  - Auth: Bearer JWT
  - Response: Content-Type image/* com bytes da imagem ou um 302 para URL externa dependendo da implementação de FileStorageService

- GET /stories/{id}/info
  - Descrição: baixa JSON completo com o story (útil para consumo programático)
  - Auth: Bearer JWT

--------------------------------------------------------------------------------

6) Formatos de erro e códigos HTTP

Padrões gerais:
- 200 — OK (resposta com dados)
- 201 — Created (recurso criado)
- 400 — Bad Request (validação de campos inválidos)
- 401 — Unauthorized (token ausente/expirado/inválido)
- 403 — Forbidden (sem permissão / chave de bot inválida)
- 404 — Not Found
- 409 — Conflict (ex.: registro duplicado por externalId)
- 500 — Internal Server Error

Exemplo de corpo de erro (application/json):
{
  "timestamp": "2026-06-16T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "postedAt must be a valid ISO-8601 datetime",
  "path": "/bots/data"
}

--------------------------------------------------------------------------------

7) Exemplo de fluxo completo (curl)

1) Solicitar magic link:

```bash
curl -X POST "http://localhost:8080/auth/magic/request" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'
```

2) Confirmar magic token (exemplo retornando accessToken):

```bash
curl "http://localhost:8080/auth/magic/confirm?token=MAGIC_TOKEN"
```

3) Usar accessToken obtido para listar stories:

```bash
curl "http://localhost:8080/stories?page=0&size=20" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

4) Bot envia story com screenshot (Windows PowerShell example):

```powershell
curl -X POST "http://localhost:8080/bots/data" `
  -H "X-BOT-API-KEY: BOT_KEY_HERE" `
  -F "profileId=00000000-0000-0000-0000-000000000000" `
  -F "externalId=ig_story_123" `
  -F "likes=42" `
  -F "postedAt=2026-06-16T12:34:56Z" `
  -F "screenshot=@C:\path\to\screenshot.png"
```

--------------------------------------------------------------------------------

8) Observações operacionais e segurança

- Substitua `jwt.secret` por um segredo forte e diferente por ambiente antes de colocar em produção.
- Atualmente o envio de e-mails é feito por `ConsoleEmailService` (somente log). Em produção, implemente `SmtpEmailService` ou integre com serviços como SendGrid/Amazon SES.
- O armazenamento de arquivos é local (`LocalFileStorageService`). Para alta disponibilidade/escala, integre com S3/Blob storages e atualize `FileStorageService`.
- Considere implementar refresh tokens ou estratégia de renovação de sessão se o access token for de curta duração.
- Valide e aplique limites de tamanho/mimetypes para uploads (imagens) enviados por bots.
- Proteja endpoints sensíveis por RBAC se necessário (usar `@PreAuthorize` / roles configuradas).

--------------------------------------------------------------------------------

Contatos e próximos passos
- Se desejar, posso gerar uma collection Postman/OpenAPI a partir deste documento.
- Posso também criar exemplos de integração (pequeno SDK JS/Java) para acelerar adoção.

---
Arquivo gerado automaticamente; atualize quando endpoints forem adicionados/alterados.

