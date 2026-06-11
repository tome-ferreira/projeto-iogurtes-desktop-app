# Controlo de Acessos Baseado em Cargos (RBAC) na Interface de Utilizador (UI)

Este documento explica como o Controlo de Acessos Baseado em Cargos (RBAC - *Role-Based Access Control*) foi implementado na interface da aplicação e serve como guia para a implementação futura de lógicas de visualização condicional para novos componentes da UI.

## Como foi Implementado (Exemplo: Menu Lateral / Sidebar)

A lógica de permissões baseia-se na identificação do cargo (Role) do utilizador autenticado, utilizando a classe `SessionManager`. 

No caso do menu lateral (`Sidebar`), a estrutura completa de todas as secções (Dashboards, Produtos, Encomendas, Matérias Primas, Fornecedores e Gestão) encontra-se declarada no `Sidebar.fxml`. No entanto, nem todos os utilizadores devem ver todos os botões ou secções. 

Para resolver isto:
1. **Identificadores FXML:** Atribuímos um `fx:id` não apenas aos botões, mas também às *Labels* de secção (ex: `lblSectionDashboards`, `lblSectionProdutos`).
2. **Obtenção do Cargo:** No método `initialize()` do controlador (`Sidebar.java`), invocamos o `SessionManager` para saber o cargo do utilizador:
   ```java
   String role = SessionManager.getInstance().getUserRole();
   ```
3. **Controlo de Visibilidade:** Criámos um método utilitário chamado `hideNode(Node... nodes)` que esconde completamente os nós da interface. Ocultar nós em JavaFX requer desativar ambas as propriedades `visible` e `managed` (caso contrário o elemento fica invisível, mas continua a ocupar espaço físico no ecrã).
   ```java
   private void hideNode(Node... nodes) {
       for (Node n : nodes) {
           if (n != null) {
               n.setVisible(false);
               n.setManaged(false);
           }
       }
   }
   ```
4. **Aplicação de Regras:** Consoante o valor do `role` (`ADMIN`, `GESTOR`, `FUNCIONARIO_MP`, `FUNCIONARIO_OP`), escondemos iterativamente os botões e os rótulos de categorias que esse utilizador não tem autorização para visualizar.

## Boas Práticas para o Futuro

Sempre que for necessário ocultar e/ou renderizar botões, secções ou ecrãs inteiros na UI com base nos Cargos do Utilizador, siga esta abordagem:

1. **Desenhe o FXML para o Nível Mais Alto (ADMIN):** Ao criar novos FXMLs, desenhe a interface considerando que **todos** os elementos estão visíveis. O ecrã no construtor visual (Scene Builder) deve mostrar a interface no seu estado mais completo.
2. **Defina IDs para Tudo o que pode ser escondido:** Certifique-se de que os contentores (VBox, HBox) e elementos (Button, Label) que poderão estar ocultos para certos cargos possuem um `fx:id` definido.
3. **Esconda via Java, não FXML:** Deixe os elementos visíveis por defeito no FXML e use o método `initialize()` do seu `Controller` para esconder ativamente os nós que o utilizador autenticado não deve ver, validando o Cargo com o `SessionManager`.
4. **Use `setManaged(false)`:** Nunca se esqueça que `setVisible(false)` apenas torna o componente transparente. Deve usar também `setManaged(false)` para que o `LayoutManager` do JavaFX feche o espaço do componente na interface gráfica de forma fluída.
