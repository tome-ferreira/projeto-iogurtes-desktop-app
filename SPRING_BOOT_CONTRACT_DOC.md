# SPRINGBOOT-API-CONTRACT — Gestão de Iogurtes

> **Versão do documento:** 1.0 — Gerada a partir do código-fonte real do projecto.  
> **Camada consumida pela app JavaFX:** BLL (Business Logic Layer) — serviços em `com.empresa.iogurtes.gestaoiogurtes.core.service`.  
> **Linguagem:** descrições em Português Europeu · todo o código em inglês.

---

## Índice

1. [Visão Geral do Sistema](#1-visão-geral-do-sistema)
2. [Todos os Modelos de Dados](#2-todos-os-modelos-de-dados)
3. [Interfaces dos Serviços BLL](#3-interfaces-dos-serviços-bll)
4. [Guia de Implementação Mock](#4-guia-de-implementação-mock)
5. [Guia de Substituição Mock → Real](#5-guia-de-substituição-mock--real)
6. [Configuração do Projecto JavaFX](#6-configuração-do-projecto-javafx)
7. [Constrangimentos e Armadilhas Conhecidas](#7-constrangimentos-e-armadilhas-conhecidas)

---

## 1. Visão Geral do Sistema

### O que o sistema faz

Sistema de gestão de produção e stocks de iogurtes. Permite:

- Gerir **empresas clientes** e os seus **utilizadores** com papéis de acesso.
- Gerir **fornecedores** com certificações de qualidade.
- Gerir **matérias-primas** (leite, açúcar, aromas, etc.) e controlar o seu **stock** através de movimentos.
- Definir **produtos finais** (iogurtes) com listas de ingredientes e preços.
- Criar e aprovar **ordens de produção**, consumindo matérias-primas e acrescentando stock de produto final.
- Registar **encomendas** de pallets, com expedição automática se houver stock ou criação de ordens de produção pendentes caso contrário.
- Controlar os **movimentos de stock do produto final** (produção, expedição, ajuste, devolução).

### Estrutura de camadas

```
┌─────────────────────────────────────┐
│          JavaFX Desktop App         │
│  (chama serviços BLL directamente)  │
└─────────────┬───────────────────────┘
              │ depende de
              ▼
┌─────────────────────────────────────┐
│  BLL — Business Logic Layer         │
│  package: core.service              │
│  EmpresaService, UserService,       │
│  FornecedorService,                 │
│  MateriaPrimaService,               │
│  MovimentoStockMPService,           │
│  ProdutoFinalService,               │
│  MovimentoStockPFService,           │
│  OrdemProducaoService,              │
│  EncomendaService,                  │
│  PalletTipoService,                 │
│  LoginService                       │
└─────────────┬───────────────────────┘
              │ depende de
              ▼
┌─────────────────────────────────────┐
│  DAL — Data Access Layer            │
│  package: core.repository           │
│  (Spring Data JPA / Hibernate)      │
│  base de dados: PostgreSQL          │
└─────────────────────────────────────┘
```

### Regra principal

**A app JavaFX nunca acede ao DAL directamente.** Toda a lógica passa pelo BLL. A app JavaFX instancia (ou injeta) serviços e chama os seus métodos públicos.

---

## 2. Todos os Modelos de Dados

### 2.1 `BaseEntity` — Superclasse de todas as entidades

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`

Todos os modelos estendem `BaseEntity`. Campos herdados presentes em **todos** os modelos:

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `id` | `UUID` | Não | Identificador único gerado automaticamente |
| `isActive` | `boolean` | Não | `true` enquanto o registo está activo; `false` após soft-delete |
| `deletedAt` | `LocalDateTime` | Sim | Data/hora do soft-delete; `null` enquanto activo |
| `createdAt` | `LocalDateTime` | Não | Preenchido automaticamente no `@PrePersist` |
| `updatedAt` | `LocalDateTime` | Não | Actualizado automaticamente no `@PreUpdate` |

**Atenção:** o sistema usa **soft-delete** — `delete()` nos serviços não apaga da BD; marca `isActive = false`. Os métodos `getAll()` retornam apenas registos activos.

---

### 2.2 `Empresa`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `empresas`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `nomeEmpresa` | `String` | Não | Nome comercial da empresa (máx. 150 char) |
| `nipc` | `String` | Não | NIPC único (máx. 20 char) |
| `telefone` | `String` | Sim | Número de telefone normalizado via libphonenumber |
| `morada` | `String` | Sim | Morada completa (máx. 200 char) |
| `codigoPostal` | `String` | Sim | Código postal (máx. 20 char) |
| `cidade` | `String` | Sim | Cidade (máx. 100 char) |

---

### 2.3 `User`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `users`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `empresa` | `Empresa` | Sim | Empresa associada (pode ser null para ADMIN global) |
| `nome` | `String` | Não | Nome completo (máx. 100 char) |
| `email` | `String` | Não | Email único (máx. 150 char) |
| `passwordHash` | `String` | Não | Hash BCrypt da password; **nunca expor na UI** |
| `turno` | `TurnoTipo` | Sim | Turno de trabalho (enum) |
| `dataAdmissao` | `LocalDate` | Sim | Data de admissão |
| `roles` | `List<UserRole>` | Não | Lista de papéis do utilizador (cascade ALL) |

---

### 2.4 `UserRole`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `user_roles`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `user` | `User` | Não | Utilizador proprietário |
| `role` | `UserRoleType` | Não | Papel atribuído (enum) |

---

### 2.5 `Fornecedor`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `fornecedores`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `nome` | `String` | Não | Nome do fornecedor (máx. 150 char) |
| `nif` | `String` | Sim | NIF único (máx. 20 char) |
| `email` | `String` | Sim | Email de contacto (máx. 150 char) |
| `telefone` | `String` | Sim | Telefone normalizado (máx. 20 char) |
| `morada` | `String` | Sim | Morada (máx. 200 char) |
| `certificacoes` | `List<FornecedorCertificacao>` | Não | Certificações (cascade ALL) |

---

### 2.6 `FornecedorCertificacao`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `fornecedor_certificacoes`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `fornecedor` | `Fornecedor` | Sim | Fornecedor proprietário |
| `tipo` | `TipoCertificacao` | Não | Tipo de certificação (enum) |
| `descricao` | `String` | Sim | Descrição livre (máx. 120 char) |
| `validade` | `LocalDate` | Sim | Data de validade da certificação |

---

### 2.7 `MateriaPrima`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `materias_primas`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `nome` | `String` | Não | Nome da matéria-prima (máx. 120 char) |
| `tipo` | `TipoMateriaPrima` | Não | Categoria (enum) |
| `unidade` | `String` | Sim | Unidade de medida, ex: "kg", "L" (máx. 10 char) |
| `stockAtual` | `BigDecimal` | Sim | Stock actual em unidades (precision 12, scale 3) |
| `stockMinimo` | `BigDecimal` | Sim | Stock mínimo de alerta (precision 12, scale 3) |
| `precoUnitario` | `BigDecimal` | Não | Preço por unidade (precision 10, scale 2) |
| `fornecedor` | `Fornecedor` | Não | Fornecedor associado |

---

### 2.8 `MovimentoStockMP`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `movimentos_stock_mp`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `user` | `User` | Não | Utilizador que registou o movimento |
| `materia` | `MateriaPrima` | Não | Matéria-prima afectada |
| `tipo` | `TipoMovimentoMP` | Não | Tipo de movimento (enum) |
| `quantidade` | `BigDecimal` | Não | Quantidade movimentada (precision 12, scale 3) |
| `observacao` | `String` | Sim | Nota livre (máx. 200 char) |

---

### 2.9 `ProdutoFinal`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `produtos_finais`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `codigoSku` | `String` | Não | Código SKU único (máx. 50 char) |
| `nome` | `String` | Não | Nome do produto (máx. 120 char) |
| `descricao` | `String` | Sim | Descrição longa |
| `validadeDias` | `Integer` | Sim | Prazo de validade em dias |
| `precoVenda` | `BigDecimal` | Sim | Preço de venda (precision 10, scale 2) |
| `precoPorKg` | `BigDecimal` | Sim | Preço por kg (precision 10, scale 2) |
| `visivelCliente` | `Boolean` | Sim | Se aparece no catálogo do cliente |
| `stockAtual` | `Integer` | Sim | Stock actual em unidades inteiras |
| `quantidadeLote` | `Integer` | Não | Unidades por lote de produção |
| `materias` | `List<ProdutoMateria>` | Não | Ingredientes (cascade ALL) |

---

### 2.10 `ProdutoMateria`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `produto_materias`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `produto` | `ProdutoFinal` | Não | Produto final que usa este ingrediente |
| `materia` | `MateriaPrima` | Não | Matéria-prima usada |
| `quantidadePorUnidadeProduto` | `BigDecimal` | Não | Quantidade de matéria-prima por unidade de produto (precision 12, scale 3) |

---

### 2.11 `MovimentoStockPF`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `movimentos_stock_pf`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `produto` | `ProdutoFinal` | Não | Produto afectado |
| `ordem` | `OrdemProducao` | Sim | Ordem de produção associada (pode ser null em expedição manual) |
| `tipo` | `TipoMovimentoPF` | Não | Tipo de movimento (enum) |
| `quantidadeKg` | `Integer` | Não | Quantidade em unidades inteiras |
| `observacao` | `String` | Sim | Nota livre (máx. 200 char) |

---

### 2.12 `OrdemProducao`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `ordens_producao`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `estado` | `EstadoOrdem` | Sim | Estado actual (enum); default `EM_PRODUCAO` |
| `dataInicio` | `LocalDateTime` | Sim | Data/hora de início |
| `dataFim` | `LocalDateTime` | Sim | Data/hora de conclusão prevista |
| `user` | `User` | Sim | Utilizador que criou a ordem |
| `aprovadoEm` | `LocalDateTime` | Sim | Data/hora de aprovação; null até aprovada |
| `observacoes` | `String` | Sim | Notas livres |
| `produtos` | `List<OrdemProducaoProduto>` | Sim | Linhas de produto (cascade ALL) |
| `consumos` | `List<ConsumoProducao>` | Sim | Consumos de matérias-primas (cascade ALL) |

---

### 2.13 `OrdemProducaoProduto`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `ordem_producao_produtos`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `ordem` | `OrdemProducao` | Não | Ordem de produção pai |
| `produto` | `ProdutoFinal` | Não | Produto a produzir |
| `quantidadeKg` | `BigDecimal` | Não | Quantidade a produzir em kg (precision 12, scale 3) |

---

### 2.14 `ConsumoProducao`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `consumos_producao`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `ordem` | `OrdemProducao` | Não | Ordem de produção pai |
| `materia` | `MateriaPrima` | Não | Matéria-prima consumida |
| `quantidadeKg` | `BigDecimal` | Não | Quantidade consumida em kg (precision 12, scale 3) |

---

### 2.15 `PalletTipo`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `pallet_tipos`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `nome` | `String` | Não | Nome do tipo de pallet (máx. 80 char) |
| `capacidadeKg` | `BigDecimal` | Não | Capacidade máxima em kg (precision 10, scale 3) |

---

### 2.16 `Encomenda`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `encomendas`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `user` | `User` | Sim | Utilizador que criou a encomenda |
| `estado` | `EstadoEncomenda` | Sim | Estado (enum); default `pendente` |
| `dataEncomenda` | `LocalDateTime` | Sim | Data/hora de criação; preenchida no construtor |
| `totalPreco` | `BigDecimal` | Sim | Total calculado automaticamente (precision 12, scale 2) |
| `pallets` | `List<EncomendaPallet>` | Sim | Linhas de pallet (cascade ALL) |

---

### 2.17 `EncomendaPallet`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `encomenda_pallets`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `encomenda` | `Encomenda` | Não | Encomenda pai |
| `produto` | `ProdutoFinal` | Não | Produto encomendado |
| `palletTipo` | `PalletTipo` | Não | Tipo de pallet |
| `quantidadePallets` | `Integer` | Não | Número de pallets |
| `precoPorPallet` | `BigDecimal` | Não | Preço por pallet (precision 10, scale 2) |
| `ordens` | `List<EncomendaOrdem>` | Sim | Ordens de produção associadas (cascade ALL) |

---

### 2.18 `EncomendaOrdem`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.model`  
**Tabela:** `encomenda_ordens`

| Campo | Tipo | Nullable | Descrição |
|-------|------|----------|-----------|
| `ordem` | `OrdemProducao` | Não | Ordem de produção |
| `encomendaPallet` | `EncomendaPallet` | Não | Linha de pallet associada |
| `quantidadePallets` | `Integer` | Não | Quantidade de pallets desta linha |
| `estado` | `EstadoEncomendaOrdem` | Não | Estado da relação (enum) |

---

### 2.19 Enums

#### `EstadoEncomenda` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `pendente` | Encomenda criada mas com stock insuficiente; aguarda ordens de produção |
| `confirmada` | Stock total disponível; encomenda expedida imediatamente |
| `expedida` | Mercadoria enviada |
| `entregue` | Encomenda recebida pelo cliente |
| `cancelada` | Encomenda cancelada |

#### `EstadoEncomendaOrdem` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `pendente` | Ordem ainda não produzida |
| `produzido` | Produção concluída |
| `expedido` | Produto expedido |

#### `EstadoOrdem` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `AGUARDA_APROVACAO` | Criada automaticamente por encomenda; aguarda aprovação manual |
| `EM_PRODUCAO` | Em curso (estado default na criação manual) |
| `CONCLUIDA` | Produção finalizada |
| `CANCELADA` | Ordem cancelada; consumos de MP revertidos |

#### `TipoCertificacao` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `ISO` | Norma ISO |
| `BIO` | Certificação biológica |
| `HACCP` | Controlo de segurança alimentar |
| `OUTRA` | Outro tipo |

#### `TipoMateriaPrima` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `BASES` | Bases lácteas ou equivalentes |
| `ADOCANTES` | Açúcares e adoçantes |
| `SABOR` | Aromas e frutos |
| `OUTRO` | Outros ingredientes |

#### `TipoMovimentoMP` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `ENTRADA` | Recepção de matéria-prima; adiciona ao stock |
| `SAIDA` | Consumo; subtrai ao stock |
| `AJUSTE` | Ajuste manual; define o stock directamente |

#### `TipoMovimentoPF` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `PRODUCAO` | Produto acabado de produzir; adiciona ao stock |
| `EXPEDICAO` | Produto expedido; subtrai do stock |
| `AJUSTE` | Ajuste manual; define o stock directamente |
| `DEVOLUCAO` | Devolução de cliente; adiciona ao stock |

#### `TurnoTipo` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `MANHA` | Turno da manhã |
| `TARDE` | Turno da tarde |
| `NOITE` | Turno da noite |

#### `UserRoleType` — `com.empresa.iogurtes.gestaoiogurtes.core.model.enums`

| Valor | Descrição |
|-------|-----------|
| `ADMIN` | Administrador do sistema; acesso total |
| `FUNCIONARIO` | Funcionário de fábrica; acesso operacional |
| `EMPRESA` | Representante da empresa cliente; acesso ao catálogo e encomendas |

---

## 3. Interfaces dos Serviços BLL

> **Nota:** o projecto Spring Boot não define interfaces Java para os serviços — são classes `@Service` concretas. Para a app JavaFX usar mocks intercambiáveis, o developer **deve** criar interfaces Java no projecto JavaFX que espelhem os métodos públicos de cada serviço. O [Capítulo 6](#6-configuração-do-projecto-javafx) explica a estrutura recomendada.

---

### 3.1 `EmpresaService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createEmpresa`

```java
public Empresa createEmpresa(
    String nomeEmpresa,
    String nipc,
    String telefone,
    String morada,
    String codigoPostal,
    String cidade
)
```

**O que faz:** Valida os dados, normaliza o telefone via libphonenumber, cria e persiste uma nova `Empresa`.  
**Parâmetros:**

| Parâmetro | Descrição |
|-----------|-----------|
| `nomeEmpresa` | Nome da empresa; obrigatório |
| `nipc` | NIPC único; obrigatório; validado contra duplicados |
| `telefone` | Telefone; pode ser null; normalizado automaticamente |
| `morada` | Morada; obrigatória |
| `codigoPostal` | Código postal; obrigatório |
| `cidade` | Cidade; obrigatória |

**Retorna:** `Empresa` com `id` preenchido.  
**Lança:** `IllegalArgumentException` se validação falhar (NIPC duplicado, campos obrigatórios em falta, etc.).

```java
// Exemplo
Empresa e = empresaService.createEmpresa(
    "Laticínios do Norte SA",
    "501234567",
    "+351 22 000 0001",
    "Rua do Leite, 1",
    "4000-001",
    "Porto"
);
System.out.println(e.getId()); // UUID gerado
```

---

#### `getById`

```java
public Empresa getById(UUID id)
```

**O que faz:** Recupera uma empresa pelo ID. Inclui registos inactivos no lookup (usa `findById`).  
**Parâmetros:** `id` — UUID da empresa.  
**Retorna:** `Empresa` correspondente.  
**Lança:** `IllegalArgumentException("Empresa não encontrada!")` se não existir.

```java
Empresa e = empresaService.getById(UUID.fromString("11111111-0000-0000-0000-000000000001"));
```

---

#### `getAll`

```java
public List<Empresa> getAll()
```

**O que faz:** Retorna todas as empresas **activas** (`isActive = true`).  
**Retorna:** `List<Empresa>` (pode ser vazia).  
**Lança:** Nada.

```java
List<Empresa> empresas = empresaService.getAll();
```

---

#### `getAllIncludingInactive`

```java
public List<Empresa> getAllIncludingInactive()
```

**O que faz:** Retorna **todos** os registos, incluindo soft-deleted.  
**Retorna:** `List<Empresa>`.

```java
List<Empresa> todas = empresaService.getAllIncludingInactive();
```

---

#### `update`

```java
public Empresa update(
    UUID id,
    String nomeEmpresa,
    String nipc,
    String telefone,
    String morada,
    String codigoPostal,
    String cidade
)
```

**O que faz:** Valida, normaliza telefone, actualiza e persiste a empresa.  
**Parâmetros:** `id` — empresa a actualizar; restantes campos idênticos ao `createEmpresa`.  
**Retorna:** `Empresa` actualizada.  
**Lança:** `IllegalArgumentException` se não existir ou validação falhar.

```java
Empresa updated = empresaService.update(
    existingId,
    "Laticínios do Norte Lda",
    "501234567",
    "+351 22 000 0099",
    "Rua do Leite, 5",
    "4000-001",
    "Porto"
);
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete da empresa **e** de todos os utilizadores associados (em cascata via `UserService.delete`).  
**Lança:** `IllegalArgumentException("Empresa não encontrada!")` se não existir.

```java
empresaService.delete(empresaId);
```

---

### 3.2 `UserService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createUser`

```java
public User createUser(
    String nome,
    String email,
    String password,
    String turno,
    LocalDate dataAdmissao,
    List<String> roles,
    UUID empresaId
)
```

**O que faz:** Valida e converte `turno` (String → `TurnoTipo`) e `roles` (List\<String\> → `List<UserRole>`), faz hash BCrypt da password, cria e persiste o `User` com os papéis em cascata.

**Parâmetros:**

| Parâmetro | Descrição |
|-----------|-----------|
| `nome` | Nome completo; obrigatório |
| `email` | Email único; obrigatório |
| `password` | Password em texto claro; será imediatamente cifrada |
| `turno` | String do enum `TurnoTipo` (ex: `"MANHA"`) |
| `dataAdmissao` | Data de admissão; pode ser null |
| `roles` | Lista de strings de `UserRoleType` (ex: `["ADMIN", "FUNCIONARIO"]`) |
| `empresaId` | UUID da empresa; pode ser null para ADMIN global |

**Retorna:** `User` persistido.  
**Lança:** `IllegalArgumentException` se email duplicado, turno inválido, role inválida, etc.

```java
User u = userService.createUser(
    "Ana Silva",
    "ana@laticinios.pt",
    "S3gur@nca!",
    "MANHA",
    LocalDate.of(2024, 3, 1),
    List.of("FUNCIONARIO"),
    empresaId
);
```

---

#### `updateUser`

```java
public User updateUser(UUID id, String nome, String turno, List<String> roles)
```

**O que faz:** Actualiza nome, turno e papéis do utilizador. Os papéis existentes são **substituídos** (clear + add).  
**Lança:** `IllegalArgumentException("Utilizador não encontrado")` ou erros de validação.

```java
User updated = userService.updateUser(userId, "Ana M. Silva", "TARDE", List.of("FUNCIONARIO"));
```

---

#### `getById`

```java
public User getById(UUID id)
```

**Lança:** `IllegalArgumentException("Utilizador não encontrado")`.

```java
User u = userService.getById(userId);
```

---

#### `getAll`

```java
public List<User> getAll()
```

Retorna utilizadores activos.

---

#### `getAllIncludingInactive`

```java
public List<User> getAllIncludingInactive()
```

---

#### `changePassword`

```java
public void changePassword(UUID id, String newPassword)
```

**O que faz:** Valida que o utilizador existe e está activo, que a nova password é diferente da actual, e actualiza o hash.  
**Lança:** `IllegalArgumentException` se utilizador inactivo, password igual à actual, ou não passar validação.

```java
userService.changePassword(userId, "NovaS3gur@nca!");
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: ordens de produção, encomendas, movimentos de stock MP e papéis do utilizador.  
**Lança:** `IllegalArgumentException("Utilizador não encontrado")`.

---

### 3.3 `LoginService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`  
**Nota:** Esta classe **não é `@Service`** — não é gerida pelo Spring; deve ser instanciada manualmente ou adaptada.

#### `execute`

```java
public User execute(String email, String password)
```

**O que faz:** Autentica o utilizador por email e password. Verifica `isActive`.  
**Parâmetros:** `email`, `password` em texto claro.  
**Retorna:** `User` autenticado.  
**Lança:** `IllegalArgumentException("Credenciais invalidas")` em qualquer falha (email inválido, utilizador inactivo, password errada).

```java
// Nota: "Credenciais invalidas" é a mesma mensagem para todos os erros (por segurança)
try {
    User loggedIn = loginService.execute("ana@laticinios.pt", "S3gur@nca!");
} catch (IllegalArgumentException e) {
    // mostrar "Email ou password incorrectos" na UI
}
```

---

### 3.4 `FornecedorService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createFornecedor`

```java
public Fornecedor createFornecedor(
    String nome,
    String nif,
    String email,
    String telefone,
    String morada,
    List<FornecedorCertificacao> certificacoes
)
```

**O que faz:** Valida, normaliza telefone, cria `Fornecedor` com as certificações associadas em cascata.  
**Parâmetros:**

| Parâmetro | Descrição |
|-----------|-----------|
| `nome` | Nome; obrigatório |
| `nif` | NIF único; pode ser null |
| `email` | Email; validado formato; pode ser null |
| `telefone` | Telefone; normalizado; pode ser null |
| `morada` | Morada; obrigatória |
| `certificacoes` | Lista de `FornecedorCertificacao` já construídas (sem `fornecedor` definido — é definido internamente) |

**Retorna:** `Fornecedor` persistido.  
**Lança:** `IllegalArgumentException` se validação falhar.

```java
FornecedorCertificacao cert = new FornecedorCertificacao();
cert.setTipo(TipoCertificacao.HACCP);
cert.setDescricao("HACCP 2024");
cert.setValidade(LocalDate.of(2025, 12, 31));

Fornecedor f = fornecedorService.createFornecedor(
    "Leite do Campo SA",
    "507654321",
    "geral@leitecampo.pt",
    "+351 253 000 111",
    "Rua do Pasto, 10, Braga",
    List.of(cert)
);
```

---

#### `updateFornecedor`

```java
public Fornecedor updateFornecedor(UUID id, String nome, String nif, String email,
                                   String telefone, String morada)
```

**Nota:** Não actualiza certificações; apenas os campos escalares.  
**Lança:** `IllegalArgumentException("Fornecedor não encontrado")`.

---

#### `getById`

```java
public Fornecedor getById(UUID id)
```

**Lança:** `IllegalArgumentException("Fornecedor não encontrado")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<Fornecedor> getAll()
public List<Fornecedor> getAllIncludingInactive()
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: todas as matérias-primas do fornecedor e todas as certificações.

---

### 3.5 `MateriaPrimaService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createMateriaPrima`

```java
public MateriaPrima createMateriaPrima(
    String nome,
    String unidade,
    TipoMateriaPrima tipo,
    BigDecimal stockAtual,
    BigDecimal stockMinimo,
    BigDecimal precoUnitario,
    UUID fornecedorId
)
```

**O que faz:** Valida, obtém referência ao fornecedor por ID (sem carregar entidade completa), cria e persiste.  
**Lança:** `IllegalArgumentException` se validação falhar.

```java
MateriaPrima mp = materiaPrimaService.createMateriaPrima(
    "Leite gordo pasteurizado",
    "L",
    TipoMateriaPrima.BASES,
    new BigDecimal("5000.000"),
    new BigDecimal("500.000"),
    new BigDecimal("0.85"),
    fornecedorId
);
```

---

#### `updateMateriaPrima`

```java
public MateriaPrima updateMateriaPrima(
    UUID id,
    String nome,
    String unidade,
    TipoMateriaPrima tipo,
    BigDecimal stockMinimo,
    BigDecimal precoUnitario,
    UUID fornecedorId
)
```

**Nota:** `stockAtual` **não** é actualizável por este método; usa-se `MovimentoStockMPService.registarMovimento`.  
**Lança:** `IllegalArgumentException("Matéria prima não encontrada")`.

---

#### `getById`

```java
public MateriaPrima getById(UUID id)
```

**Lança:** `IllegalArgumentException("Matéria prima não encontrada")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<MateriaPrima> getAll()
public List<MateriaPrima> getAllIncludingInactive()
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: `ProdutoMateria` e `MovimentoStockMP` relacionados.

---

### 3.6 `MovimentoStockMPService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `registarMovimento`

```java
public MovimentoStockMP registarMovimento(
    UUID userId,
    UUID materiaId,
    TipoMovimentoMP tipo,
    BigDecimal quantidade,
    String observacao
)
```

**O que faz:** Valida, carrega a `MateriaPrima`, atualiza `stockAtual` consoante o tipo:
- `ENTRADA` → `stockAtual += quantidade`
- `SAIDA` → `stockAtual -= quantidade`
- `AJUSTE` → `stockAtual = quantidade`

Guarda a matéria-prima actualizada e cria um registo de movimento.  
**Esta é uma operação `@Transactional`.**  
**Lança:** `IllegalArgumentException("Matéria prima não encontrada!")`.

```java
MovimentoStockMP mov = movimentoStockMPService.registarMovimento(
    userId,
    mpId,
    TipoMovimentoMP.ENTRADA,
    new BigDecimal("200.000"),
    "Recepção de encomenda #REF-20240101"
);
```

---

#### `getByMateria`

```java
public List<MovimentoStockMP> getByMateria(UUID materiaId)
```

Retorna todos os movimentos (incluindo inactivos) para uma matéria-prima.

---

#### `getByUser`

```java
public List<MovimentoStockMP> getByUser(UUID userId)
```

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<MovimentoStockMP> getAll()
public List<MovimentoStockMP> getAllIncludingInactive()
```

---

### 3.7 `ProdutoFinalService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createProduto`

```java
public ProdutoFinal createProduto(
    String codigoSku,
    String nome,
    String descricao,
    Integer validadeDias,
    BigDecimal precoVenda,
    BigDecimal precoPorKg,
    Integer quantidadeLote,
    List<ProdutoMateria> materias
)
```

**O que faz:** Valida SKU único e nome único, cria `ProdutoFinal` e associa as linhas de ingredientes.  
**Nota:** cada `ProdutoMateria` deve ter `materia` definida (com ID válido); o campo `produto` é preenchido internamente.

```java
MateriaPrima leite = materiaPrimaService.getById(leiteId);
MateriaPrima acucar = materiaPrimaService.getById(acucarId);

ProdutoMateria pm1 = new ProdutoMateria();
pm1.setMateria(leite);
pm1.setQuantidadePorUnidadeProduto(new BigDecimal("0.180"));

ProdutoMateria pm2 = new ProdutoMateria();
pm2.setMateria(acucar);
pm2.setQuantidadePorUnidadeProduto(new BigDecimal("0.020"));

ProdutoFinal iogurte = produtoFinalService.createProduto(
    "IGT-NAT-125",
    "Iogurte Natural 125g",
    "Iogurte natural sem adição de açúcar",
    21,
    new BigDecimal("0.65"),
    new BigDecimal("5.20"),
    1000,
    List.of(pm1, pm2)
);
```

---

#### `updateProduto`

```java
public ProdutoFinal updateProduto(
    UUID id,
    String nome,
    String descricao,
    Integer validadeDias,
    BigDecimal precoVenda,
    BigDecimal precoPorKg,
    Integer quantidadeLote,
    Boolean visivelCliente
)
```

**Nota importante:** a lista de ingredientes **não é actualizável** por este método. O SKU também não muda.  
**Lança:** `IllegalArgumentException("Produto não encontrado!")`.

---

#### `getById`

```java
public ProdutoFinal getById(UUID id)
```

**Lança:** `IllegalArgumentException("Produto não encontrado!")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<ProdutoFinal> getAll()
public List<ProdutoFinal> getAllIncludingInactive()
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: `ProdutoMateria`, `MovimentoStockPF`, `EncomendaPallet` e `EncomendaOrdem` relacionados.

---

### 3.8 `MovimentoStockPFService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `registarMovimento`

```java
public MovimentoStockPF registarMovimento(
    ProdutoFinal produto,
    OrdemProducao ordem,
    TipoMovimentoPF tipo,
    Integer quantidade,
    String observacao
)
```

**O que faz:** Actualiza `stockAtual` do produto:
- `PRODUCAO` ou `DEVOLUCAO` → `stockAtual += quantidade`
- `EXPEDICAO` → `stockAtual -= quantidade` (lança excepção se ficar negativo)
- `AJUSTE` → `stockAtual = quantidade`

**Parâmetros:** `produto` e `ordem` são **objectos completos** (não IDs). `ordem` pode ser null.  
**Lança:** `IllegalStateException("Stock insuficiente para o produto: …")` em `EXPEDICAO` com quantidade insuficiente.

```java
ProdutoFinal produto = produtoFinalService.getById(produtoId);
MovimentoStockPF mov = movimentoStockPFService.registarMovimento(
    produto,
    null,
    TipoMovimentoPF.AJUSTE,
    500,
    "Ajuste de inventário anual"
);
```

---

#### `getById`

```java
public MovimentoStockPF getById(UUID id)
```

**Lança:** `IllegalArgumentException("Movimento não encontrado")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<MovimentoStockPF> getAll()
public List<MovimentoStockPF> getAllIncludingInactive()
```

---

#### `getByProduto`

```java
public List<MovimentoStockPF> getByProduto(UUID produtoId)
```

---

#### `getByOrdem`

```java
public List<MovimentoStockPF> getByOrdem(UUID ordemId)
```

---

### 3.9 `OrdemProducaoService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createOrdem`

```java
public OrdemProducao createOrdem(
    UUID userId,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    String observacoes,
    List<OrdemProducaoProduto> produtos
)
```

**O que faz:** Cria a ordem, calcula consumos de matérias-primas por produto (usando `ProdutoMateria.quantidadePorUnidadeProduto × quantidadeKg`), e chama `MovimentoStockMPService.registarMovimento(SAIDA)` para cada ingrediente.  
**Estado inicial:** `EM_PRODUCAO`.

```java
OrdemProducaoProduto opp = new OrdemProducaoProduto();
opp.setProduto(produtoFinal);   // ou usar o construtor com UUID
opp.setQuantidadeKg(new BigDecimal("500.000"));

OrdemProducao ordem = ordemProducaoService.createOrdem(
    userId,
    LocalDateTime.now(),
    LocalDateTime.now().plusHours(8),
    "Produção turno manhã",
    List.of(opp)
);
```

---

#### `getById`

```java
public OrdemProducao getById(UUID id)
```

**Lança:** `IllegalArgumentException("Ordem não encontrada")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<OrdemProducao> getAll()
public List<OrdemProducao> getAllIncludingInactive()
```

---

#### `updateOrdem`

```java
public OrdemProducao updateOrdem(
    UUID id,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    String observacoes
)
```

**Nota:** campos `null` são ignorados (não sobrescrevem o valor existente).  
**Lança:** excepções de validação (ex: data de início posterior à data de fim).

---

#### `cancelarOrdem`

```java
public OrdemProducao cancelarOrdem(UUID id, UUID userId)
```

**O que faz:** Muda estado para `CANCELADA` e **reverte** todos os consumos de matérias-primas através de movimentos `ENTRADA`.  
**Lança:** `IllegalArgumentException` se ordem não existir.

```java
OrdemProducao cancelada = ordemProducaoService.cancelarOrdem(ordemId, userId);
```

---

#### `aprovarOrdem`

```java
public OrdemProducao aprovarOrdem(UUID ordemId)
```

**O que faz:** Apenas válido para ordens em `AGUARDA_APROVACAO`. Calcula e regista consumos de MP (`SAIDA`), muda estado para `EM_PRODUCAO`, preenche `aprovadoEm`.  
**Lança:** `IllegalStateException("Ordem não está em estado de aprovação")` se estado incorrecto.

```java
OrdemProducao aprovada = ordemProducaoService.aprovarOrdem(ordemId);
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: produtos da ordem, consumos, `EncomendaOrdem` e `MovimentoStockPF` associados.

---

### 3.10 `EncomendaService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `createEncomenda`

```java
public Encomenda createEncomenda(UUID userId, List<EncomendaPallet> pallets)
```

**O que faz:** Processo complexo:
1. Calcula `totalPreco` automaticamente.
2. Para cada pallet com stock suficiente → regista movimento `EXPEDICAO` imediatamente.
3. Para pallets sem stock → cria uma `OrdemProducao` em estado `AGUARDA_APROVACAO`.
4. Se **todos** têm stock → estado final `confirmada`; caso contrário → `pendente`.

**Parâmetros:** cada `EncomendaPallet` deve ter `produto` (com ID), `palletTipo` (com ID), `quantidadePallets` e `precoPorPallet` definidos.  
**Lança:** `IllegalArgumentException` se utilizador, produto ou tipo de pallet não existirem.

```java
EncomendaPallet ep = new EncomendaPallet();
ProdutoFinal prodRef = new ProdutoFinal();
prodRef.setId(produtoId);
PalletTipo palletRef = new PalletTipo();
palletRef.setId(palletTipoId);

ep.setProduto(prodRef);
ep.setPalletTipo(palletRef);
ep.setQuantidadePallets(5);
ep.setPrecoPorPallet(new BigDecimal("250.00"));

Encomenda enc = encomendaService.createEncomenda(userId, List.of(ep));
```

---

#### `getById`

```java
public Encomenda getById(UUID id)
```

**Lança:** `IllegalArgumentException("Encomenda não encontrada")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<Encomenda> getAll()
public List<Encomenda> getAllIncludingInactive()
```

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: `EncomendaPallet`, `EncomendaOrdem` e ordens de produção associadas.

---

### 3.11 `PalletTipoService`

**Package:** `com.empresa.iogurtes.gestaoiogurtes.core.service`

#### `create`

```java
public PalletTipo create(String nome, BigDecimal capacidadeKg)
```

**Lança:** `IllegalArgumentException` se validação falhar.

```java
PalletTipo pt = palletTipoService.create("Euro Pallet 1200kg", new BigDecimal("1200.000"));
```

---

#### `getById`

```java
public PalletTipo getById(UUID id)
```

**Lança:** `IllegalArgumentException("Tipo de pallet não encontrado")`.

---

#### `getAll` / `getAllIncludingInactive`

```java
public List<PalletTipo> getAll()
public List<PalletTipo> getAllIncludingInactive()
```

---

#### `update`

```java
public PalletTipo update(UUID id, String nome, BigDecimal capacidadeKg)
```

**Nota:** campos `null` são ignorados.

---

#### `delete`

```java
public void delete(UUID id)
```

**O que faz:** Soft-delete em cascata: `EncomendaPallet` e `EncomendaOrdem` associadas.

---

## 4. Guia de Implementação Mock

> Cada classe mock implementa a interface espelho definida no projecto JavaFX (ver [Secção 6](#6-configuração-do-projecto-javafx)). Usa dados em memória: `List` e `Map`.

---

### 4.1 `MockEmpresaService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.Empresa;
import com.iogurtes.javafx.service.IEmpresaService;

import java.time.LocalDateTime;
import java.util.*;

public class MockEmpresaService implements IEmpresaService {

    private final Map<UUID, Empresa> store = new LinkedHashMap<>();

    public MockEmpresaService() {
        // Dados de amostra
        Empresa e1 = new Empresa("Laticínios do Norte SA", "501234567",
                "+351220000001", "Rua do Leite, 1", "4000-001", "Porto");
        e1.setId(UUID.fromString("11111111-0000-0000-0000-000000000001"));
        store.put(e1.getId(), e1);

        Empresa e2 = new Empresa("Queijaria do Sul Lda", "502345678",
                "+351289000003", "Estrada do Queijo, 7", "8000-001", "Faro");
        e2.setId(UUID.fromString("11111111-0000-0000-0000-000000000002"));
        store.put(e2.getId(), e2);
    }

    @Override
    public Empresa createEmpresa(String nomeEmpresa, String nipc, String telefone,
                                  String morada, String codigoPostal, String cidade) {
        if (nomeEmpresa == null || nomeEmpresa.isBlank())
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        if (nipc == null || nipc.isBlank())
            throw new IllegalArgumentException("NIPC é obrigatório");
        boolean nipcDuplicado = store.values().stream()
                .anyMatch(e -> e.getNipc().equals(nipc) && e.isActive());
        if (nipcDuplicado)
            throw new IllegalArgumentException("NIPC já existe: " + nipc);

        Empresa empresa = new Empresa(nomeEmpresa, nipc, telefone, morada, codigoPostal, cidade);
        empresa.setId(UUID.randomUUID());
        store.put(empresa.getId(), empresa);
        return empresa;
    }

    @Override
    public Empresa getById(UUID id) {
        Empresa e = store.get(id);
        if (e == null)
            throw new IllegalArgumentException("Empresa não encontrada!");
        return e;
    }

    @Override
    public List<Empresa> getAll() {
        return store.values().stream()
                .filter(Empresa::isActive)
                .toList();
    }

    @Override
    public List<Empresa> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Empresa update(UUID id, String nomeEmpresa, String nipc, String telefone,
                           String morada, String codigoPostal, String cidade) {
        Empresa empresa = getById(id);
        if (nomeEmpresa == null || nomeEmpresa.isBlank())
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        empresa.setNomeEmpresa(nomeEmpresa);
        empresa.setNipc(nipc);
        empresa.setTelefone(telefone);
        empresa.setMorada(morada);
        empresa.setCodigoPostal(codigoPostal);
        empresa.setCidade(cidade);
        return empresa;
    }

    @Override
    public void delete(UUID id) {
        Empresa empresa = getById(id);
        empresa.softDelete();
    }
}
```

---

### 4.2 `MockUserService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.User;
import com.empresa.iogurtes.gestaoiogurtes.core.model.UserRole;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TurnoTipo;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.UserRoleType;
import com.iogurtes.javafx.service.IUserService;

import java.time.LocalDate;
import java.util.*;

public class MockUserService implements IUserService {

    private final Map<UUID, User> store = new LinkedHashMap<>();

    public MockUserService() {
        // Admin de sistema
        User admin = new User(null, "Admin Sistema", "admin@sistema.pt",
                "$2a$HASH_PLACEHOLDER", TurnoTipo.MANHA, LocalDate.of(2023, 1, 1));
        admin.setId(UUID.fromString("22222222-0000-0000-0000-000000000001"));
        UserRole roleAdmin = new UserRole();
        roleAdmin.setId(UUID.randomUUID());
        roleAdmin.setRole(UserRoleType.ADMIN);
        roleAdmin.setUser(admin);
        admin.setRoles(List.of(roleAdmin));
        store.put(admin.getId(), admin);

        // Funcionário
        User func = new User(null, "Ana Silva", "ana@laticinios.pt",
                "$2a$HASH_PLACEHOLDER", TurnoTipo.MANHA, LocalDate.of(2024, 3, 1));
        func.setId(UUID.fromString("22222222-0000-0000-0000-000000000002"));
        UserRole roleFuncionario = new UserRole();
        roleFuncionario.setId(UUID.randomUUID());
        roleFuncionario.setRole(UserRoleType.FUNCIONARIO);
        roleFuncionario.setUser(func);
        func.setRoles(List.of(roleFuncionario));
        store.put(func.getId(), func);
    }

    @Override
    public User createUser(String nome, String email, String password, String turno,
                            LocalDate dataAdmissao, List<String> roles, UUID empresaId) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Email inválido");
        boolean emailDuplicado = store.values().stream()
                .anyMatch(u -> u.getEmail().equals(email) && u.isActive());
        if (emailDuplicado)
            throw new IllegalArgumentException("Email já existe: " + email);

        TurnoTipo turnoTipo = TurnoTipo.valueOf(turno);

        User user = new User(null, nome, email, "HASHED_" + password, turnoTipo, dataAdmissao);
        user.setId(UUID.randomUUID());

        List<UserRole> userRoles = new ArrayList<>();
        for (String roleStr : roles) {
            UserRole ur = new UserRole();
            ur.setId(UUID.randomUUID());
            ur.setRole(UserRoleType.valueOf(roleStr));
            ur.setUser(user);
            userRoles.add(ur);
        }
        user.setRoles(userRoles);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(UUID id, String nome, String turno, List<String> roles) {
        User user = getById(id);
        user.setNome(nome);
        user.setTurno(TurnoTipo.valueOf(turno));

        List<UserRole> newRoles = new ArrayList<>();
        for (String roleStr : roles) {
            UserRole ur = new UserRole();
            ur.setId(UUID.randomUUID());
            ur.setRole(UserRoleType.valueOf(roleStr));
            ur.setUser(user);
            newRoles.add(ur);
        }
        user.getRoles().clear();
        user.getRoles().addAll(newRoles);
        return user;
    }

    @Override
    public User getById(UUID id) {
        User u = store.get(id);
        if (u == null)
            throw new IllegalArgumentException("Utilizador não encontrado");
        return u;
    }

    @Override
    public List<User> getAll() {
        return store.values().stream().filter(User::isActive).toList();
    }

    @Override
    public List<User> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void changePassword(UUID id, String newPassword) {
        User user = getById(id);
        if (!user.isActive())
            throw new IllegalArgumentException("Utilizador inativo");
        if (user.getPasswordHash().equals("HASHED_" + newPassword))
            throw new IllegalArgumentException("A nova password deve ser diferente da atual");
        user.setPasswordHash("HASHED_" + newPassword);
    }

    @Override
    public void delete(UUID id) {
        User user = getById(id);
        user.getRoles().forEach(r -> r.softDelete());
        user.softDelete();
    }
}
```

---

### 4.3 `MockLoginService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.User;
import com.iogurtes.javafx.service.ILoginService;

import java.util.Map;

public class MockLoginService implements ILoginService {

    private final Map<String, User> usersByEmail;

    /** Injeta o mock de utilizadores para validar credenciais */
    public MockLoginService(MockUserService userService) {
        this.usersByEmail = new java.util.HashMap<>();
        userService.getAllIncludingInactive()
                .forEach(u -> usersByEmail.put(u.getEmail(), u));
    }

    @Override
    public User execute(String email, String password) {
        if (email == null || !email.contains("@") || password == null || password.isBlank())
            throw new IllegalArgumentException("Credenciais invalidas");

        User user = usersByEmail.get(email);
        if (user == null || !user.isActive())
            throw new IllegalArgumentException("Credenciais invalidas");

        // Mock: verifica hash simplificado "HASHED_<password>"
        if (!user.getPasswordHash().equals("HASHED_" + password))
            throw new IllegalArgumentException("Credenciais invalidas");

        return user;
    }
}
```

---

### 4.4 `MockFornecedorService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.Fornecedor;
import com.empresa.iogurtes.gestaoiogurtes.core.model.FornecedorCertificacao;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TipoCertificacao;
import com.iogurtes.javafx.service.IFornecedorService;

import java.time.LocalDate;
import java.util.*;

public class MockFornecedorService implements IFornecedorService {

    private final Map<UUID, Fornecedor> store = new LinkedHashMap<>();

    public MockFornecedorService() {
        FornecedorCertificacao cert = new FornecedorCertificacao();
        cert.setId(UUID.randomUUID());
        cert.setTipo(TipoCertificacao.HACCP);
        cert.setDescricao("HACCP 2025");
        cert.setValidade(LocalDate.of(2025, 12, 31));

        Fornecedor f = new Fornecedor("Leite do Campo SA", "507654321",
                "geral@leitecampo.pt", "+351253000111", "Rua do Pasto, 10, Braga");
        f.setId(UUID.fromString("33333333-0000-0000-0000-000000000001"));
        cert.setFornecedor(f);
        f.setCertificacoes(new ArrayList<>(List.of(cert)));
        store.put(f.getId(), f);
    }

    @Override
    public Fornecedor createFornecedor(String nome, String nif, String email,
                                        String telefone, String morada,
                                        List<FornecedorCertificacao> certificacoes) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do fornecedor é obrigatório");

        Fornecedor f = new Fornecedor(nome, nif, email, telefone, morada);
        f.setId(UUID.randomUUID());
        List<FornecedorCertificacao> certs = new ArrayList<>(certificacoes);
        certs.forEach(c -> {
            c.setId(UUID.randomUUID());
            c.setFornecedor(f);
        });
        f.setCertificacoes(certs);
        store.put(f.getId(), f);
        return f;
    }

    @Override
    public Fornecedor updateFornecedor(UUID id, String nome, String nif, String email,
                                        String telefone, String morada) {
        Fornecedor f = getById(id);
        f.setNome(nome);
        f.setNif(nif);
        f.setEmail(email);
        f.setTelefone(telefone);
        f.setMorada(morada);
        return f;
    }

    @Override
    public Fornecedor getById(UUID id) {
        Fornecedor f = store.get(id);
        if (f == null)
            throw new IllegalArgumentException("Fornecedor não encontrado");
        return f;
    }

    @Override
    public List<Fornecedor> getAll() {
        return store.values().stream().filter(Fornecedor::isActive).toList();
    }

    @Override
    public List<Fornecedor> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        Fornecedor f = getById(id);
        f.getCertificacoes().forEach(FornecedorCertificacao::softDelete);
        f.softDelete();
    }
}
```

---

### 4.5 `MockMateriaPrimaService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.Fornecedor;
import com.empresa.iogurtes.gestaoiogurtes.core.model.MateriaPrima;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TipoMateriaPrima;
import com.iogurtes.javafx.service.IMateriaPrimaService;

import java.math.BigDecimal;
import java.util.*;

public class MockMateriaPrimaService implements IMateriaPrimaService {

    private final Map<UUID, MateriaPrima> store = new LinkedHashMap<>();
    private final MockFornecedorService fornecedorService;

    public MockMateriaPrimaService(MockFornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;

        Fornecedor f = fornecedorService.getAll().get(0);

        MateriaPrima leite = new MateriaPrima("Leite gordo pasteurizado", TipoMateriaPrima.BASES,
                "L", new BigDecimal("5000.000"), new BigDecimal("500.000"),
                new BigDecimal("0.85"), f);
        leite.setId(UUID.fromString("44444444-0000-0000-0000-000000000001"));
        store.put(leite.getId(), leite);

        MateriaPrima acucar = new MateriaPrima("Açúcar refinado", TipoMateriaPrima.ADOCANTES,
                "kg", new BigDecimal("2000.000"), new BigDecimal("200.000"),
                new BigDecimal("0.90"), f);
        acucar.setId(UUID.fromString("44444444-0000-0000-0000-000000000002"));
        store.put(acucar.getId(), acucar);
    }

    @Override
    public MateriaPrima createMateriaPrima(String nome, String unidade, TipoMateriaPrima tipo,
                                            BigDecimal stockAtual, BigDecimal stockMinimo,
                                            BigDecimal precoUnitario, UUID fornecedorId) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        Fornecedor f = fornecedorService.getById(fornecedorId);
        MateriaPrima mp = new MateriaPrima(nome, tipo, unidade, stockAtual, stockMinimo, precoUnitario, f);
        mp.setId(UUID.randomUUID());
        store.put(mp.getId(), mp);
        return mp;
    }

    @Override
    public MateriaPrima updateMateriaPrima(UUID id, String nome, String unidade, TipoMateriaPrima tipo,
                                            BigDecimal stockMinimo, BigDecimal precoUnitario,
                                            UUID fornecedorId) {
        MateriaPrima mp = getById(id);
        Fornecedor f = fornecedorService.getById(fornecedorId);
        mp.setNome(nome);
        mp.setUnidade(unidade);
        mp.setTipo(tipo);
        mp.setStockMinimo(stockMinimo);
        mp.setPrecoUnitario(precoUnitario);
        mp.setFornecedor(f);
        return mp;
    }

    @Override
    public MateriaPrima getById(UUID id) {
        MateriaPrima mp = store.get(id);
        if (mp == null)
            throw new IllegalArgumentException("Matéria prima não encontrada");
        return mp;
    }

    @Override
    public List<MateriaPrima> getAll() {
        return store.values().stream().filter(MateriaPrima::isActive).toList();
    }

    @Override
    public List<MateriaPrima> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        MateriaPrima mp = getById(id);
        mp.softDelete();
    }
}
```

---

### 4.6 `MockMovimentoStockMPService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.MateriaPrima;
import com.empresa.iogurtes.gestaoiogurtes.core.model.MovimentoStockMP;
import com.empresa.iogurtes.gestaoiogurtes.core.model.User;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TipoMovimentoMP;
import com.iogurtes.javafx.service.IMovimentoStockMPService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class MockMovimentoStockMPService implements IMovimentoStockMPService {

    private final Map<UUID, MovimentoStockMP> store = new LinkedHashMap<>();
    private final MockMateriaPrimaService materiaPrimaService;
    private final MockUserService userService;

    public MockMovimentoStockMPService(MockMateriaPrimaService materiaPrimaService,
                                        MockUserService userService) {
        this.materiaPrimaService = materiaPrimaService;
        this.userService = userService;
    }

    @Override
    public MovimentoStockMP registarMovimento(UUID userId, UUID materiaId,
                                               TipoMovimentoMP tipo, BigDecimal quantidade,
                                               String observacao) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantidade deve ser positiva");

        User user = userService.getById(userId);
        MateriaPrima materia = materiaPrimaService.getById(materiaId);

        switch (tipo) {
            case ENTRADA -> materia.setStockAtual(materia.getStockAtual().add(quantidade));
            case SAIDA   -> materia.setStockAtual(materia.getStockAtual().subtract(quantidade));
            case AJUSTE  -> materia.setStockAtual(quantidade);
        }

        MovimentoStockMP mov = new MovimentoStockMP(user, materia, tipo, quantidade, observacao);
        mov.setId(UUID.randomUUID());
        store.put(mov.getId(), mov);
        return mov;
    }

    @Override
    public List<MovimentoStockMP> getByMateria(UUID materiaId) {
        return store.values().stream()
                .filter(m -> m.getMateria().getId().equals(materiaId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentoStockMP> getByUser(UUID userId) {
        return store.values().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentoStockMP> getAll() {
        return store.values().stream().filter(MovimentoStockMP::isActive).toList();
    }

    @Override
    public List<MovimentoStockMP> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }
}
```

---

### 4.7 `MockProdutoFinalService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.ProdutoFinal;
import com.empresa.iogurtes.gestaoiogurtes.core.model.ProdutoMateria;
import com.iogurtes.javafx.service.IProdutoFinalService;

import java.math.BigDecimal;
import java.util.*;

public class MockProdutoFinalService implements IProdutoFinalService {

    private final Map<UUID, ProdutoFinal> store = new LinkedHashMap<>();

    public MockProdutoFinalService(MockMateriaPrimaService mpService) {
        var leite = mpService.getAll().get(0);
        var acucar = mpService.getAll().get(1);

        ProdutoMateria pm1 = new ProdutoMateria();
        pm1.setId(UUID.randomUUID());
        pm1.setMateria(leite);
        pm1.setQuantidadePorUnidadeProduto(new BigDecimal("0.180"));

        ProdutoMateria pm2 = new ProdutoMateria();
        pm2.setId(UUID.randomUUID());
        pm2.setMateria(acucar);
        pm2.setQuantidadePorUnidadeProduto(new BigDecimal("0.020"));

        ProdutoFinal p = new ProdutoFinal("IGT-NAT-125", "Iogurte Natural 125g",
                "Iogurte natural sem açúcar", 21,
                new BigDecimal("0.65"), new BigDecimal("5.20"), 1000);
        p.setId(UUID.fromString("55555555-0000-0000-0000-000000000001"));
        p.setStockAtual(2000);
        p.setVisivelCliente(true);
        pm1.setProduto(p);
        pm2.setProduto(p);
        p.setMaterias(new ArrayList<>(List.of(pm1, pm2)));
        store.put(p.getId(), p);
    }

    @Override
    public ProdutoFinal createProduto(String codigoSku, String nome, String descricao,
                                       Integer validadeDias, BigDecimal precoVenda,
                                       BigDecimal precoPorKg, Integer quantidadeLote,
                                       List<ProdutoMateria> materias) {
        if (codigoSku == null || codigoSku.isBlank())
            throw new IllegalArgumentException("SKU é obrigatório");
        boolean skuDup = store.values().stream()
                .anyMatch(p -> p.getCodigoSku().equals(codigoSku) && p.isActive());
        if (skuDup)
            throw new IllegalArgumentException("SKU já existe: " + codigoSku);

        ProdutoFinal produto = new ProdutoFinal(codigoSku, nome, descricao,
                validadeDias, precoVenda, precoPorKg, quantidadeLote);
        produto.setId(UUID.randomUUID());
        produto.setStockAtual(0);
        produto.setVisivelCliente(false);

        List<ProdutoMateria> mats = new ArrayList<>(materias);
        mats.forEach(m -> {
            m.setId(UUID.randomUUID());
            m.setProduto(produto);
        });
        produto.setMaterias(mats);
        store.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public ProdutoFinal updateProduto(UUID id, String nome, String descricao,
                                       Integer validadeDias, BigDecimal precoVenda,
                                       BigDecimal precoPorKg, Integer quantidadeLote,
                                       Boolean visivelCliente) {
        ProdutoFinal p = getById(id);
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setValidadeDias(validadeDias);
        p.setPrecoVenda(precoVenda);
        p.setPrecoPorKg(precoPorKg);
        p.setQuantidadeLote(quantidadeLote);
        p.setVisivelCliente(visivelCliente);
        return p;
    }

    @Override
    public ProdutoFinal getById(UUID id) {
        ProdutoFinal p = store.get(id);
        if (p == null)
            throw new IllegalArgumentException("Produto não encontrado!");
        return p;
    }

    @Override
    public List<ProdutoFinal> getAll() {
        return store.values().stream().filter(ProdutoFinal::isActive).toList();
    }

    @Override
    public List<ProdutoFinal> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        ProdutoFinal p = getById(id);
        p.getMaterias().forEach(pm -> pm.softDelete());
        p.softDelete();
    }
}
```

---

### 4.8 `MockPalletTipoService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.PalletTipo;
import com.iogurtes.javafx.service.IPalletTipoService;

import java.math.BigDecimal;
import java.util.*;

public class MockPalletTipoService implements IPalletTipoService {

    private final Map<UUID, PalletTipo> store = new LinkedHashMap<>();

    public MockPalletTipoService() {
        PalletTipo euro = new PalletTipo("Euro Pallet 1200kg", new BigDecimal("1200.000"));
        euro.setId(UUID.fromString("66666666-0000-0000-0000-000000000001"));
        store.put(euro.getId(), euro);

        PalletTipo meio = new PalletTipo("Meio Pallet 600kg", new BigDecimal("600.000"));
        meio.setId(UUID.fromString("66666666-0000-0000-0000-000000000002"));
        store.put(meio.getId(), meio);
    }

    @Override
    public PalletTipo create(String nome, BigDecimal capacidadeKg) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        if (capacidadeKg == null || capacidadeKg.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Capacidade deve ser positiva");
        PalletTipo pt = new PalletTipo(nome, capacidadeKg);
        pt.setId(UUID.randomUUID());
        store.put(pt.getId(), pt);
        return pt;
    }

    @Override
    public PalletTipo getById(UUID id) {
        PalletTipo pt = store.get(id);
        if (pt == null)
            throw new IllegalArgumentException("Tipo de pallet não encontrado");
        return pt;
    }

    @Override
    public List<PalletTipo> getAll() {
        return store.values().stream().filter(PalletTipo::isActive).toList();
    }

    @Override
    public List<PalletTipo> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public PalletTipo update(UUID id, String nome, BigDecimal capacidadeKg) {
        PalletTipo pt = getById(id);
        if (nome != null) pt.setNome(nome);
        if (capacidadeKg != null) pt.setCapacidadeKg(capacidadeKg);
        return pt;
    }

    @Override
    public void delete(UUID id) {
        PalletTipo pt = getById(id);
        pt.softDelete();
    }
}
```

---

### 4.9 `MockOrdemProducaoService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.*;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.EstadoOrdem;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TipoMovimentoMP;
import com.iogurtes.javafx.service.IOrdemProducaoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class MockOrdemProducaoService implements IOrdemProducaoService {

    private final Map<UUID, OrdemProducao> store = new LinkedHashMap<>();
    private final MockUserService userService;
    private final MockProdutoFinalService produtoService;
    private final MockMovimentoStockMPService stockMPService;

    public MockOrdemProducaoService(MockUserService userService,
                                     MockProdutoFinalService produtoService,
                                     MockMovimentoStockMPService stockMPService) {
        this.userService = userService;
        this.produtoService = produtoService;
        this.stockMPService = stockMPService;
    }

    @Override
    public OrdemProducao createOrdem(UUID userId, LocalDateTime dataInicio,
                                      LocalDateTime dataFim, String observacoes,
                                      List<OrdemProducaoProduto> produtos) {
        User user = userService.getById(userId);

        OrdemProducao ordem = new OrdemProducao(user, dataInicio, dataFim, observacoes);
        ordem.setId(UUID.randomUUID());
        ordem.setEstado(EstadoOrdem.EM_PRODUCAO);

        List<ConsumoProducao> todosConsumos = new ArrayList<>();
        List<OrdemProducaoProduto> produtosMutaveis = new ArrayList<>(produtos);

        for (OrdemProducaoProduto opp : produtosMutaveis) {
            opp.setId(UUID.randomUUID());
            opp.setOrdem(ordem);

            ProdutoFinal produto = produtoService.getById(opp.getProduto().getId());

            for (ProdutoMateria pm : produto.getMaterias()) {
                BigDecimal consumoTotal = pm.getQuantidadePorUnidadeProduto()
                        .multiply(opp.getQuantidadeKg());
                UUID materiaId = pm.getMateria().getId();

                todosConsumos.stream()
                        .filter(c -> c.getMateria().getId().equals(materiaId))
                        .findFirst()
                        .ifPresentOrElse(
                                ex -> ex.setQuantidadeKg(ex.getQuantidadeKg().add(consumoTotal)),
                                () -> todosConsumos.add(new ConsumoProducao(ordem, pm.getMateria(), consumoTotal))
                        );

                stockMPService.registarMovimento(
                        userId, materiaId, TipoMovimentoMP.SAIDA, consumoTotal,
                        "Consumo para produção via ordem " + ordem.getId()
                );
            }
        }

        ordem.setProdutos(produtosMutaveis);
        ordem.setConsumos(todosConsumos);
        store.put(ordem.getId(), ordem);
        return ordem;
    }

    @Override
    public OrdemProducao getById(UUID id) {
        OrdemProducao o = store.get(id);
        if (o == null)
            throw new IllegalArgumentException("Ordem não encontrada");
        return o;
    }

    @Override
    public List<OrdemProducao> getAll() {
        return store.values().stream().filter(OrdemProducao::isActive).toList();
    }

    @Override
    public List<OrdemProducao> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public OrdemProducao updateOrdem(UUID id, LocalDateTime dataInicio, LocalDateTime dataFim,
                                      String observacoes) {
        OrdemProducao ordem = getById(id);
        if (dataInicio != null) ordem.setDataInicio(dataInicio);
        if (dataFim != null) ordem.setDataFim(dataFim);
        if (observacoes != null) ordem.setObservacoes(observacoes);
        return ordem;
    }

    @Override
    public OrdemProducao cancelarOrdem(UUID id, UUID userId) {
        OrdemProducao ordem = getById(id);
        for (ConsumoProducao consumo : ordem.getConsumos()) {
            stockMPService.registarMovimento(
                    userId, consumo.getMateria().getId(),
                    TipoMovimentoMP.ENTRADA, consumo.getQuantidadeKg(),
                    "Reversão por cancelamento da ordem " + ordem.getId()
            );
        }
        ordem.setEstado(EstadoOrdem.CANCELADA);
        return ordem;
    }

    @Override
    public OrdemProducao aprovarOrdem(UUID ordemId) {
        OrdemProducao ordem = getById(ordemId);
        if (ordem.getEstado() != EstadoOrdem.AGUARDA_APROVACAO)
            throw new IllegalStateException("Ordem não está em estado de aprovação");
        ordem.setEstado(EstadoOrdem.EM_PRODUCAO);
        ordem.setAprovadoEm(LocalDateTime.now());
        return ordem;
    }

    @Override
    public void delete(UUID id) {
        OrdemProducao ordem = getById(id);
        ordem.getProdutos().forEach(p -> p.softDelete());
        ordem.getConsumos().forEach(c -> c.softDelete());
        ordem.softDelete();
    }
}
```

---

### 4.10 `MockEncomendaService`

```java
package com.iogurtes.javafx.service.mock;

import com.empresa.iogurtes.gestaoiogurtes.core.model.*;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.EstadoEncomenda;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.EstadoOrdem;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TipoMovimentoPF;
import com.iogurtes.javafx.service.IEncomendaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class MockEncomendaService implements IEncomendaService {

    private final Map<UUID, Encomenda> store = new LinkedHashMap<>();
    private final MockUserService userService;
    private final MockProdutoFinalService produtoService;
    private final MockPalletTipoService palletTipoService;
    private final MockOrdemProducaoService ordemService;

    public MockEncomendaService(MockUserService userService,
                                 MockProdutoFinalService produtoService,
                                 MockPalletTipoService palletTipoService,
                                 MockOrdemProducaoService ordemService) {
        this.userService = userService;
        this.produtoService = produtoService;
        this.palletTipoService = palletTipoService;
        this.ordemService = ordemService;
    }

    @Override
    public Encomenda createEncomenda(UUID userId, List<EncomendaPallet> pallets) {
        User user = userService.getById(userId);

        BigDecimal totalPreco = pallets.stream()
                .map(p -> p.getPrecoPorPallet().multiply(BigDecimal.valueOf(p.getQuantidadePallets())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Encomenda encomenda = new Encomenda(user, totalPreco);
        encomenda.setId(UUID.randomUUID());

        boolean todosComStock = true;
        List<EncomendaPallet> palletsMutaveis = new ArrayList<>(pallets);

        for (EncomendaPallet ep : palletsMutaveis) {
            ep.setId(UUID.randomUUID());
            ep.setEncomenda(encomenda);

            ProdutoFinal produto = produtoService.getById(ep.getProduto().getId());
            PalletTipo palletTipo = palletTipoService.getById(ep.getPalletTipo().getId());

            int kgNecessarios = palletTipo.getCapacidadeKg()
                    .multiply(BigDecimal.valueOf(ep.getQuantidadePallets())).intValue();

            if (produto.getStockAtual() >= kgNecessarios) {
                produto.setStockAtual(produto.getStockAtual() - kgNecessarios);
            } else {
                todosComStock = false;
                // cria ordem pendente sem consumo imediato de MP
                OrdemProducao ordemPendente = new OrdemProducao(
                        user, LocalDateTime.now(), LocalDateTime.now().plusHours(8),
                        "Ordem gerada automaticamente para encomenda " + encomenda.getId()
                );
                ordemPendente.setId(UUID.randomUUID());
                ordemPendente.setEstado(EstadoOrdem.AGUARDA_APROVACAO);
                ordemPendente.setProdutos(new ArrayList<>());
                ordemPendente.setConsumos(new ArrayList<>());
                ordemService.getAll(); // assegura referência sem quebrar mock

                EncomendaOrdem eo = new EncomendaOrdem(ordemPendente, ep, ep.getQuantidadePallets());
                eo.setId(UUID.randomUUID());
                ep.setOrdens(new ArrayList<>(List.of(eo)));
            }
        }

        encomenda.setPallets(palletsMutaveis);
        encomenda.setEstado(todosComStock ? EstadoEncomenda.confirmada : EstadoEncomenda.pendente);
        store.put(encomenda.getId(), encomenda);
        return encomenda;
    }

    @Override
    public Encomenda getById(UUID id) {
        Encomenda e = store.get(id);
        if (e == null)
            throw new IllegalArgumentException("Encomenda não encontrada");
        return e;
    }

    @Override
    public List<Encomenda> getAll() {
        return store.values().stream().filter(Encomenda::isActive).toList();
    }

    @Override
    public List<Encomenda> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        Encomenda e = getById(id);
        if (e.getPallets() != null) {
            e.getPallets().forEach(pallet -> {
                if (pallet.getOrdens() != null)
                    pallet.getOrdens().forEach(eo -> eo.softDelete());
                pallet.softDelete();
            });
        }
        e.softDelete();
    }
}
```

---

## 5. Guia de Substituição Mock → Real

### 5.1 Dependência Maven a adicionar

```xml
<!-- pom.xml do projecto JavaFX -->
<dependency>
    <groupId>com.empresa.iogurtes</groupId>
    <artifactId>gestao-iogurtes</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <!-- instalar localmente: mvn install -DskipTests no projecto Spring Boot -->
</dependency>

<!-- Para BCrypt (necessário para LoginService real) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
    <version>6.x.x</version>
</dependency>

<!-- Spring Context (para @Transactional e injecção de dependências) -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.x.x</version>
</dependency>

<!-- Spring Data JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>4.0.3</version>
</dependency>

<!-- Driver PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.x</version>
</dependency>
```

### 5.2 Mudanças de imports

Os modelos e enums vêm do JAR; os imports continuam iguais:

```java
// Antes (mock) — mesmo import
import com.empresa.iogurtes.gestaoiogurtes.core.model.Empresa;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TurnoTipo;

// Depois (real) — exactamente o mesmo import; sem alteração
import com.empresa.iogurtes.gestaoiogurtes.core.model.Empresa;
import com.empresa.iogurtes.gestaoiogurtes.core.model.enums.TurnoTipo;
```

A única mudança é na linha que instancia o serviço.

### 5.3 Ficheiro único a alterar: `ServiceLocator.java`

Crie um ficheiro central `ServiceLocator` (ver [Secção 6](#6-configuração-do-projecto-javafx)):

```java
// --- MODO MOCK ---
public class ServiceLocator {
    public static IEmpresaService empresaService() {
        return new MockEmpresaService();
    }
    // ...
}

// --- MODO REAL (substituir acima) ---
// Requer Spring ApplicationContext configurado com DataSource PostgreSQL
public class ServiceLocator {
    private static ApplicationContext ctx = SpringApplicationBuilder.create();

    public static IEmpresaService empresaService() {
        return ctx.getBean(EmpresaService.class);
    }
    // ...
}
```

Alternativamente, usando uma variável de ambiente:

```java
public class ServiceLocator {
    private static final boolean USE_MOCK =
            Boolean.parseBoolean(System.getProperty("app.useMock", "true"));

    public static IEmpresaService empresaService() {
        return USE_MOCK
            ? new MockEmpresaService()
            : SpringContext.getBean(EmpresaService.class);
    }
}
```

Lançar com `-Dapp.useMock=false` para usar o JAR real.

### 5.4 O que testar após a substituição

| Área a testar | O que verificar |
|---------------|-----------------|
| Ligação à base de dados | `EmpresaService.getAll()` retorna dados reais |
| Autenticação | `LoginService.execute()` verifica hash BCrypt real |
| Transacções | `MovimentoStockMPService.registarMovimento()` altera `stockAtual` atomicamente |
| Soft-delete | `delete()` não apaga da BD; `getAll()` filtra correctamente |
| Datas | `createdAt` e `updatedAt` são preenchidos pelo JPA (`@PrePersist`) |
| Lazy loading | Colecções como `User.roles` e `ProdutoFinal.materias` carregam fora de sessão JPA? Ver secção 7 |

---

## 6. Configuração do Projecto JavaFX

### 6.1 Estrutura de pastas recomendada

```
javafx-iogurtes/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/iogurtes/javafx/
                ├── App.java                     ← ponto de entrada JavaFX
                ├── ServiceLocator.java          ← único ficheiro a alterar no swap
                ├── service/                     ← interfaces espelho do BLL
                │   ├── IEmpresaService.java
                │   ├── IUserService.java
                │   ├── ILoginService.java
                │   ├── IFornecedorService.java
                │   ├── IMateriaPrimaService.java
                │   ├── IMovimentoStockMPService.java
                │   ├── IProdutoFinalService.java
                │   ├── IMovimentoStockPFService.java
                │   ├── IOrdemProducaoService.java
                │   ├── IEncomendaService.java
                │   └── IPalletTipoService.java
                ├── service/mock/                ← mocks em memória
                │   ├── MockEmpresaService.java
                │   ├── MockUserService.java
                │   ├── MockLoginService.java
                │   ├── MockFornecedorService.java
                │   ├── MockMateriaPrimaService.java
                │   ├── MockMovimentoStockMPService.java
                │   ├── MockProdutoFinalService.java
                │   ├── MockMovimentoStockPFService.java
                │   ├── MockOrdemProducaoService.java
                │   ├── MockEncomendaService.java
                │   └── MockPalletTipoService.java
                ├── controller/                  ← controllers JavaFX (FXML)
                │   ├── LoginController.java
                │   ├── DashboardController.java
                │   ├── EmpresasController.java
                │   └── ...
                └── view/                        ← ficheiros .fxml
                    ├── login.fxml
                    ├── dashboard.fxml
                    └── ...
```

### 6.2 Interfaces espelho (exemplo para `IEmpresaService`)

```java
package com.iogurtes.javafx.service;

import com.empresa.iogurtes.gestaoiogurtes.core.model.Empresa;
import java.util.List;
import java.util.UUID;

public interface IEmpresaService {
    Empresa createEmpresa(String nomeEmpresa, String nipc, String telefone,
                           String morada, String codigoPostal, String cidade);
    Empresa getById(UUID id);
    List<Empresa> getAll();
    List<Empresa> getAllIncludingInactive();
    Empresa update(UUID id, String nomeEmpresa, String nipc, String telefone,
                   String morada, String codigoPostal, String cidade);
    void delete(UUID id);
}
```

Criar uma interface equivalente para cada serviço, espelhando os métodos públicos documentados na [Secção 3](#3-interfaces-dos-serviços-bll).

### 6.3 `ServiceLocator` — ponto central de configuração

```java
package com.iogurtes.javafx;

import com.iogurtes.javafx.service.*;
import com.iogurtes.javafx.service.mock.*;

/**
 * Único ponto de instanciação dos serviços.
 * Para trocar de mock para real, substituir este ficheiro.
 */
public class ServiceLocator {

    // --- Mocks com dependências injectadas manualmente ---
    private static final MockFornecedorService fornecedorService =
            new MockFornecedorService();
    private static final MockMateriaPrimaService materiaPrimaService =
            new MockMateriaPrimaService(fornecedorService);
    private static final MockUserService userService =
            new MockUserService();
    private static final MockMovimentoStockMPService movimentoMPService =
            new MockMovimentoStockMPService(materiaPrimaService, userService);
    private static final MockProdutoFinalService produtoFinalService =
            new MockProdutoFinalService(materiaPrimaService);
    private static final MockPalletTipoService palletTipoService =
            new MockPalletTipoService();
    private static final MockOrdemProducaoService ordemProducaoService =
            new MockOrdemProducaoService(userService, produtoFinalService, movimentoMPService);
    private static final MockEncomendaService encomendaService =
            new MockEncomendaService(userService, produtoFinalService,
                    palletTipoService, ordemProducaoService);
    private static final MockEmpresaService empresaService =
            new MockEmpresaService();
    private static final MockLoginService loginService =
            new MockLoginService(userService);

    public static IEmpresaService empresaService()         { return empresaService; }
    public static IUserService userService()               { return userService; }
    public static ILoginService loginService()             { return loginService; }
    public static IFornecedorService fornecedorService()   { return fornecedorService; }
    public static IMateriaPrimaService materiaPrimaService(){ return materiaPrimaService; }
    public static IMovimentoStockMPService stockMPService() { return movimentoMPService; }
    public static IProdutoFinalService produtoFinalService(){ return produtoFinalService; }
    public static IOrdemProducaoService ordemService()     { return ordemProducaoService; }
    public static IEncomendaService encomendaService()     { return encomendaService; }
    public static IPalletTipoService palletTipoService()   { return palletTipoService; }
}
```

### 6.4 Como usar nos controllers JavaFX

```java
public class EmpresasController {

    // Obtém o serviço do ServiceLocator — sem acoplamento à implementação
    private final IEmpresaService empresaService = ServiceLocator.empresaService();

    @FXML
    private TableView<Empresa> tabelaEmpresas;

    @FXML
    public void initialize() {
        // NUNCA chamar serviços directamente no FX thread se for lento
        // Para mock é seguro; para real usar Task<>
        List<Empresa> empresas = empresaService.getAll();
        tabelaEmpresas.getItems().setAll(empresas);
    }

    @FXML
    private void onCriarEmpresa() {
        try {
            Empresa nova = empresaService.createEmpresa(
                nomeField.getText(), nipcField.getText(), telefoneField.getText(),
                moradaField.getText(), codigoPostalField.getText(), cidadeField.getText()
            );
            tabelaEmpresas.getItems().add(nova);
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage()); // mostrar alerta ao utilizador
        }
    }
}
```

---

## 7. Constrangimentos e Armadilhas Conhecidas

### 7.1 Thread safety e o FX Thread

**Problema:** O JavaFX exige que actualizações de UI ocorram no Application Thread (FX thread). Chamadas a serviços com base de dados real podem bloquear a UI.

**Regra:**
- Com **mocks**: as chamadas são rápidas e em memória → podem correr no FX thread sem problema prático.
- Com **JAR real**: **NUNCA** chamar serviços no FX thread. Usar `Task<T>` ou `Service<T>` do JavaFX.

```java
// Padrão correcto para o JAR real
Task<List<Empresa>> task = new Task<>() {
    @Override
    protected List<Empresa> call() {
        return ServiceLocator.empresaService().getAll();
    }
};
task.setOnSucceeded(e -> tabelaEmpresas.getItems().setAll(task.getValue()));
task.setOnFailed(e -> mostrarErro(task.getException().getMessage()));
new Thread(task).start();
```

### 7.2 Lazy loading de colecções JPA

**Problema crítico:** Com o JAR real, colecções como `User.roles`, `ProdutoFinal.materias`, `Fornecedor.certificacoes`, `OrdemProducao.consumos` e `OrdemProducao.produtos` são `@OneToMany` com fetch type `LAZY` (default JPA). Se a sessão JPA fechar antes de aceder à colecção, ocorre `LazyInitializationException`.

**Sintomas:** A colecção aparece correctamente nos mocks, mas lança excepção com o JAR real.

**Soluções:**
1. Aceder às colecções **dentro** da chamada ao serviço (ainda dentro da transacção).
2. As coleções retornadas pelos serviços já estavem inicializadas se acedidas dentro da `@Transactional`.
3. Garantir que a chamada ao serviço completa antes de fechar a ligação; com `Task<>` isto é automático.

```java
// Correcto: aceder dentro do Task, na mesma transacção
Task<List<ProdutoFinal>> task = new Task<>() {
    @Override
    protected List<ProdutoFinal> call() {
        List<ProdutoFinal> produtos = ServiceLocator.produtoFinalService().getAll();
        // Forçar inicialização aqui, dentro da sessão
        produtos.forEach(p -> p.getMaterias().size());
        return produtos;
    }
};
```

### 7.3 Transaccionalidade — comportamento diferente entre mock e real

| Comportamento | Mock | JAR Real |
|---------------|------|----------|
| `registarMovimento` actualiza `stockAtual` atomicamente | Sim (em memória) | Sim (`@Transactional`) |
| Falha a meio não desfaz alterações | Não (estado corrompido) | Sim (rollback automático) |
| `createEncomenda` com erro num pallet | Pallets anteriores ficam criados | Rollback total |

**Consequência:** Os mocks não replicam rollback. Testar cenários de erro com o JAR real.

### 7.4 Soft-delete vs. delete físico

O sistema **nunca apaga registos da base de dados**. `getAll()` retorna apenas `isActive = true`. `getAllIncludingInactive()` retorna tudo.

- Na UI: mostrar apenas activos por defeito.
- Para operações de auditoria: usar `getAllIncludingInactive()`.
- Ao mostrar relações (ex: encomenda com pallets inactivos): filtrar `isActive` manualmente se necessário.

### 7.5 `LoginService` não é `@Service`

`LoginService` no projecto Spring Boot **não tem** `@Service` — não é gerido pelo Spring. Para usar com o JAR real, é necessário instanciá-lo manualmente com as dependências correctas, ou criar um wrapper `@Service` no projecto Spring Boot.

```java
// Para usar com o JAR real: instanciação manual
UserRepository userRepo = springContext.getBean(UserRepository.class);
PasswordHasher hasher = springContext.getBean(PasswordHasher.class);
LoginService loginService = new LoginService(userRepo, hasher);
```

### 7.6 `MovimentoStockPFService.registarMovimento` aceita objectos, não IDs

Ao contrário de `MovimentoStockMPService`, o serviço de movimentos de produto final recebe **objectos completos** (`ProdutoFinal produto`, `OrdemProducao ordem`), não UUIDs. É necessário carregar os objectos antes de chamar o método.

```java
// Incorrecto (não compila)
movimentoStockPFService.registarMovimento(produtoId, ordemId, TipoMovimentoPF.AJUSTE, 100, "nota");

// Correcto
ProdutoFinal produto = produtoFinalService.getById(produtoId);
movimentoStockPFService.registarMovimento(produto, null, TipoMovimentoPF.AJUSTE, 100, "nota");
```

### 7.7 `updateProduto` não actualiza ingredientes

A lista `materias` de um `ProdutoFinal` **não pode ser alterada** por `updateProduto`. Para mudar ingredientes, seria necessário eliminar e recriar o produto (ou implementar lógica adicional não presente no BLL actual).

### 7.8 Mensagens de erro genéricas na autenticação

`LoginService` lança sempre `"Credenciais invalidas"` independentemente do motivo (utilizador não existe, password errada, conta inactiva). **Não mostrar** mensagens mais específicas na UI — é comportamento intencional de segurança.

### 7.9 Enum `EstadoEncomenda` usa minúsculas

```java
// EstadoEncomenda usa valores em minúsculas!
EstadoEncomenda.pendente    // ← minúscula
EstadoEncomenda.confirmada  // ← minúscula

// EstadoOrdem usa MAIÚSCULAS:
EstadoOrdem.EM_PRODUCAO     // ← maiúscula
EstadoOrdem.CANCELADA       // ← maiúscula
```

Isto afecta comparações e serialização. **Não alterar** — é o comportamento do código fonte real.

---

*Documento gerado a 18 de Abril de 2026 a partir do código-fonte do projecto `gestao-iogurtes` versão `0.0.1-SNAPSHOT`.*
