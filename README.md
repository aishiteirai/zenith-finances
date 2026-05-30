# Zenith Finances

> Plataforma de simulação e gestão de custódia de ativos financeiros.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen?style=flat-square&logo=springboot)
![H2](https://img.shields.io/badge/H2-Persistente-blue?style=flat-square)
![License](https://img.shields.io/badge/Licença-MIT-lightgrey?style=flat-square)

---

## Visão Geral

O **Zenith Finances** é um sistema web de gerenciamento de ativos financeiros desenvolvido em Java com Spring Boot. Ele permite que usuários criem múltiplas carteiras isoladas, executem ordens de compra e venda de ativos (ações, criptos, FIIs e renda fixa), e acompanhem a evolução patrimonial por meio de relatórios e gráficos interativos.

O sistema adota o conceito de **Inércia vs. Custódia**: o saldo global do investidor (cofre) é mantido separado do capital alocado em cada carteira, permitindo controle granular da liquidez.

---

## Funcionalidades

### Investidor
- Registro e autenticação com Spring Security
- Dashboard com visão consolidada do patrimônio
- Criação de múltiplas carteiras com aportes iniciais deduzidos do saldo global
- Depósito de fundos no cofre global
- Aporte e resgate entre o cofre e cada carteira

### Boleta de Negociação
- Ordens de **Compra** e **Venda** por carteira
- Cálculo automático de **preço médio** de aquisição
- **Mark-to-Market**: preços atualizados via API da [BrAPI](https://brapi.dev) a cada 5 minutos pelo `CotacaoScheduler`
- Validação de saldo antes da execução de cada ordem

### Extrato e Relatórios
- Histórico completo de transações (compras, vendas, aportes e resgates)
- Evolução patrimonial dos últimos 6 meses (Chart.js)
- Gráfico de alocação por categoria (Ações, Cripto, FII, Renda Fixa)
- Gráfico de distribuição por carteira
- Ranking dos 5 ativos com maior taxa de rendimento estimada

### Painel Administrativo (`/admin`)
- Visão consolidada do AUM global (saldos + caixa + ativos custodiados)
- Listagem e bloqueio/desbloqueio de investidores
- Gestão do catálogo de ativos (cadastro e ocultação via soft delete)
- Auditoria completa de todas as transações da plataforma

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3.0 |
| Segurança | Spring Security 6 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de Dados | H2 (modo arquivo local) |
| Templates | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Validação | Spring Validation (Jakarta) |
| Utilitários | Lombok |
| Build | Maven (Maven Wrapper incluído) |
| CSS | Tailwind CSS (CDN) |
| Gráficos | Chart.js (CDN) |
| Fontes | JetBrains Mono + Material Symbols (Google Fonts) |
| API de Cotações | BrAPI (`brapi.dev`) |

---

## Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+** (ou use o wrapper `./mvnw` incluído no projeto)
- Conexão com a internet (para Google Fonts, Tailwind CDN e BrAPI)

---

## Como Executar

**1. Clone o repositório**
```bash
git clone https://github.com/aishiteirai/zenith-finances.git
cd zenith-finances
```

**2. Execute a aplicação**
```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**3. Acesse no navegador**
```
http://localhost:8085
```

O banco de dados H2 é criado automaticamente em `./data/zenithdb.mv.db` na primeira execução. Os dados de seed (usuários, ativos e carteiras de exemplo) são inseridos automaticamente se o banco estiver vazio.

---

## Credenciais Padrão

> ⚠️ Altere estas senhas antes de qualquer uso em ambiente não-local.

| Perfil | E-mail | Senha |
|---|---|---|
| Usuário principal | `user@zenith.node` | `SenhaForte1234` |
| Usuário secundário | `elena@zenith.node` | `senha123` |
| Administrador | `admin@zenith.node` | `admin1234` |

---

## Configuração (`application.properties`)

```properties
server.port=8085

# Banco de dados H2 persistente em arquivo local
spring.datasource.url=jdbc:h2:file:./data/zenithdb;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=password

# Console H2 (disponível em http://localhost:8085/h2-console)
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=false

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> O arquivo `./data/` e `*.mv.db` estão no `.gitignore` — o banco nunca é versionado.

---

## Estrutura do Projeto

```
src/
└── main/
    ├── java/com/example/zenith/
    │   ├── config/           # Segurança, inicialização de dados, UserDetailsService
    │   ├── controller/       # Controllers MVC e REST
    │   ├── dto/              # Objetos de transferência de dados (RegistroDTO, TransacaoRequestDTO)
    │   ├── exception/        # Exceções customizadas (SaldoInsuficienteException)
    │   ├── model/            # Entidades JPA (Investidor, Carteira, Ativo, Posicao, Transacao)
    │   ├── repository/       # Interfaces Spring Data JPA
    │   ├── scheduler/        # CotacaoScheduler (atualização de preços via BrAPI)
    │   └── service/          # Lógica de negócio (CarteiraService, TransacaoService, etc.)
    └── resources/
        ├── static/
        │   ├── css/          # style.css (customizações globais)
        │   └── js/           # tailwind-config.js (tema customizado)
        ├── templates/
        │   ├── admin/        # dashboard, usuarios, ativos, transacoes
        │   ├── fragments/    # shared.html (header e assets reutilizáveis)
        │   ├── home.html
        │   ├── carteira-detalhes.html
        │   ├── extrato.html
        │   ├── relatorios.html
        │   ├── perfil.html
        │   ├── login.html
        │   ├── register.html
        │   └── index.html
        └── application.properties
```

---

## Segurança

- Autenticação via formulário com Spring Security
- Senhas armazenadas com **BCrypt**
- Proteção **CSRF** em todos os formulários e requisições AJAX
- Proteção contra **Clickjacking** com `X-Frame-Options: SAMEORIGIN`
- Proteção contra **IDOR**: operações de compra/venda validam a ownership da carteira pelo e-mail do usuário autenticado
- Controle de acesso baseado em roles (`ROLE_USER`, `ROLE_ADMIN`)
- Usuários bloqueados pelo admin têm a sessão negada no carregamento do `UserDetails`

---

## Atualização de Cotações

O `CotacaoScheduler` consulta a API pública da [BrAPI](https://brapi.dev) a cada **5 minutos** para atualizar o campo `precoAtual` dos ativos cadastrados. Quando disponível, esse preço é utilizado como referência nas ordens (Mark-to-Market). Caso a API esteja indisponível, o sistema utiliza o preço informado manualmente na boleta.

> A BrAPI pode exigir um token de autenticação para uso em produção. Configure a variável correspondente em `application.properties` se necessário.

---

## Testes

O projeto inclui testes automatizados com JUnit 5 e Mockito:

```bash
./mvnw test
```

| Arquivo de Teste | Cobertura |
|---|---|
| `RegistroControllerTest` | Fluxos de registro (sucesso, validação, e-mail duplicado) |
| `InvestidorServiceTest` | Registro de investidor (sucesso e e-mail existente) |
| `InvestidorRepositoryTest` | Consulta por e-mail no banco |

---

## Limitações Conhecidas

- O banco H2 não é recomendado para ambientes de produção. Para deploy real, substitua por PostgreSQL ou MySQL e adicione Flyway para controle de migrations.
- As senhas dos usuários de seed estão hardcoded no `DataInitializer`. Em produção, use variáveis de ambiente.
- As métricas de risco na tela de Analytics (Índice Sharpe, Beta) são valores estáticos e não calculados.
- O letreiro de cotações no cabeçalho exibe preços fixos e não reflete os dados do `CotacaoScheduler`.

---

## Licença

Distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para mais informações.
