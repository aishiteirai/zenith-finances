# 🌌 Zenith Finances | Master Node

![Zenith Finances Banner](src/main/resources/static/images/Imagem-Zenith_index.png)

O **Zenith Finances** é um sistema avançado de gestão de custódia e alocação de ativos. Desenvolvido com uma arquitetura baseada em "Nós" (Nodes), o sistema permite aos utilizadores separar o seu capital líquido (*Inércia*) dos seus investimentos (*Capital Alocado*), oferecendo um ambiente seguro para simulação e acompanhamento de carteiras financeiras.

## 🚀 Funcionalidades Principais

* **Arquitetura Multi-Carteira:** Crie múltiplos ambientes isolados (ex: Reserva de Emergência, Ações de Longo Prazo, Cold Wallet Cripto) numa única conta.
* **Motor de Transações (Boleta Operacional):** Execução de ordens de **Compra** e **Venda** com validação estrita de saldo e quantidade.
* **Marcação a Mercado (Mark-to-Market):** O sistema calcula o *valuation* atual da carteira com base nas cotações de mercado atualizadas, permitindo realizar lucros na venda de ativos valorizados.
* **Gestão de Inércia:** Sistema integrado de aportes e resgates globais ou por carteira.
* **Painel Administrativo (Admin Node):** Área restrita para gestão de utilizadores globais, supervisão do AUM (Assets Under Management), listagem da auditoria de transações e gestão do catálogo de ativos do sistema.
* **Segurança Reforçada:** Proteção nativa contra ataques **CSRF**, **Clickjacking** (bloqueio de iframes), e falhas de **IDOR** (Insecure Direct Object Reference) nas operações financeiras.

## 🛠️ Stack Tecnológico

**Back-end:**
* Java 17+
* Spring Boot 3.3.0
* Spring Security (Autenticação, Controlo de Acessos e Encriptação de Senhas)
* Spring Data JPA (Hibernate)
* Lombok (Redução de boilerplate)
* Maven

**Front-end:**
* Thymeleaf (Server-side rendering)
* Tailwind CSS (Carregado via CDN para prototipagem rápida)
* Fontes: JetBrains Mono e Material Symbols (Google Fonts)

**Base de Dados:**
* H2 Database (Modo persistente em ficheiro local)

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos
* **Java 17** ou superior instalado.
* **Maven** (Opcional, pois o projeto inclui o *Maven Wrapper* `mvnw`).
* Uma IDE compatível (IntelliJ IDEA, Eclipse, VS Code).

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/seu-usuario/zenith-finances.git](https://github.com/seu-usuario/zenith-finances.git)
   cd zenith-finances
