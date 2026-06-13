# SportFlow - Sistema de Gestão Desportiva de Elite

## 1. Relatório Final

### 1.1 Descrição das Funcionalidades Implementadas
O **SportFlow** é uma plataforma integrada para gestão de torneios, focada em três perfis de utilizador:

*   **Criação da Conta Organizador:** Fluxo de registo especializado que exige a aprovação manual de um administrador. Os dados (nome, email, papel) são persistidos tanto no Supabase Auth como na tabela pública de perfis.
*   **Autenticação Baseada em Papéis (RBAC):** Sistema que valida se o utilizador está a tentar entrar no portal correto (Atleta, Organizador ou Admin), impedindo acessos indevidos.
*   **Fluxo de Criação do Evento:** Interface dedicada para Organizadores (com estado 'ATIVO') criarem torneios, definindo modalidade, data, localização, capacidade e preço.
*   **Perfil Dinâmico:** Sincronização em tempo real com a base de dados, permitindo a visualização e gestão de dados da conta.

### 1.2 Funcionalidades Extra Desenvolvidas
*   **Sistema de Aprovação Admin:** Secção exclusiva no perfil do Administrador para gerir pedidos pendentes de novos organizadores.
*   **Custom Branding Feedbacks:** Implementação de `Snackbars` personalizados que incluem o logo da SportFlow e seguem o design system da marca para todas as confirmações de sucesso ou erro.
*   **Arquitetura Baseada em Funções (RPC):** Uso de funções SQL (Stored Procedures) no servidor para garantir a segurança em operações críticas de escrita, contornando limitações de RLS (Row Level Security).
*   **Triggers de Automação:** Backend inteligente que cria perfis automaticamente após o registo no Auth.

### 1.3 Descrição de Testes e Validações
*   **Testes de Integração:** Validação da comunicação entre o frontend Kotlin e as APIs do Supabase (Auth e Postgrest).
*   **Validação de Regras de Negócio:** Testes manuais para garantir que um Organizador pendente não consegue fazer login e que um Atleta não consegue aceder a funções administrativas.
*   **Testes de RLS:** Verificação de que um utilizador comum não consegue alterar dados de outros perfis na base de dados.

### 1.4 Instruções de Instalação, Execução e Utilização
1.  **Clonar o Repositório:** `git clone [link-do-repositório]`
2.  **Configurar Credenciais:** No ficheiro `local.properties` na raiz do projeto, adicione:
    ```properties
    SUPABASE_URL=https://gjlbllzruxlvypcngtda.supabase.co
    SUPABASE_PUBLISHABLE_KEY=sb_publishable_R4Y8NPzM8CQsYwdLoWU8TQ_psbdoM2U
    ```
3.  **Compilar:** Abrir no Android Studio (Ladybug ou superior) e sincronizar o Gradle.
4.  **Execução:** Correr num dispositivo com API 26 (Android 8.0) ou superior.
5.  **Utilização:** Selecione o papel desejado no ecrã de login/registo para navegar entre as diferentes interfaces da plataforma.

### 1.5 Principais Dificuldades e Resoluções
*   **Infinite Recursion em RLS:** As políticas do Postgres tentavam ler a tabela `perfis` para validar permissões de leitura da própria tabela.
    *   *Resolução:* Migração para a verificação de cargos através de metadados no token JWT (`auth.jwt()`), eliminando a consulta recursiva.
*   **Perda de Sessão:** Desincronização entre o estado da app e o Supabase.
    *   *Resolução:* Implementação de ecrãs de erro resilientes com botões de recuperação ("Voltar ao Login") que limpam as preferências locais.
*   **Execução de Updates:** Algumas atualizações não eram processadas pelo servidor.
    *   *Resolução:* Implementação de chamadas RPC com `security definer` para garantir a execução com privilégios elevados.

### 1.6 Links do Projeto
*   **Gestão de Projeto:** [Inserir Link para Trello/Jira/ClickUp]
*   **Repositório Git:** [Inserir Link para GitHub/GitLab]
*   **Mockups e Protótipos:** [Inserir Link para Figma]

### 1.7 Justificação das Alterações (vs. Entrega Intermédia)
Relativamente à entrega intermédia, a principal alteração foi a transição de um sistema de dados estáticos (mocks) para uma **integração total e funcional com o Supabase**. O design foi refinado para suportar estados de carregamento, erros de rede e feedback visual premium (Snackbars com logo).

---

## 2. Projeto Android Studio

### 2.1 Tecnologias e Bibliotecas
*   **Linguagem:** Kotlin 1.9+
*   **UI:** Jetpack Compose (Material 3)
*   **Backend:** Supabase (Postgrest, Auth)
*   **Imagens:** Coil
*   **Arquitetura:** MVVM (Model-View-ViewModel) com Repositories.

### 2.2 Arquitetura Final
O projeto segue a separação de responsabilidades:
*   `com.sportflow.app.data`: Gestão de dados remotos e repositórios.
*   `com.sportflow.app.ui.screens`: Componentes visuais organizados por fluxo.
*   `com.sportflow.app.ui.viewmodel`: Gestão de estado e lógica de UI.
*   `com.sportflow.app.model`: Definição de modelos de dados de domínio.

---

## 3. Base de Dados

### 3.1 Tecnologias de Persistência
*   **Remota:** PostgreSQL alojado no Supabase para dados globais.
*   **Local:** SharedPreferences para cache de sessão e preferências de utilizador.

### 3.2 Estrutura de Tabelas (Modelo Final)

```sql
-- Tabela de Perfis
CREATE TABLE public.perfis (
    id UUID REFERENCES auth.users ON DELETE CASCADE PRIMARY KEY,
    nome TEXT NOT NULL,
    email TEXT NOT NULL,
    papel TEXT NOT NULL CHECK (papel IN ('ADMIN', 'ORGANIZADOR', 'JOGADOR')),
    estado TEXT NOT NULL DEFAULT 'ATIVO' CHECK (estado IN ('ATIVO', 'PENDENTE', 'REJEITADO')),
    metodo_pagamento TEXT,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Tabela de Torneios
CREATE TABLE public.torneios (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nome TEXT NOT NULL,
    data_inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    estado TEXT NOT NULL DEFAULT 'ABERTO',
    organizador_id UUID REFERENCES public.perfis(id),
    modalidade TEXT,
    localizacao TEXT,
    capacidade_maxima INT,
    preco DECIMAL
);
```

### 3.3 Funções RPC (Scripts de Criação)
```sql
CREATE OR REPLACE FUNCTION aprovar_utilizador(user_id UUID)
RETURNS VOID AS $$
BEGIN
  UPDATE public.perfis SET estado = 'ATIVO' WHERE id = user_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

---
*Documento gerado como suporte técnico para o projeto SportFlow.*
