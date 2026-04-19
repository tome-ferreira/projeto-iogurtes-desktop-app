# Architecture — Gestão de Iogurtes (Desktop App)

JavaFX 21 + AtlantaFX desktop application. All UI is constructed programmatically in Java — no FXML files are used.

---

## Project Structure

```
desktop_app/
├── pom.xml                          # Maven build (JavaFX 21, AtlantaFX 2.0.1, Ikonli)
├── docs/
│   └── ARCHITECTURE.md              # ← this file
└── src/main/java/com/gestaoiogurtes/
    │
    ├── GestaoIogurtes.java          # Application entry point, stage & navigation
    ├── PaginaLogin.java             # Login screen (no sidebar)
    │
    ├── model/                       # Plain data classes (view-models)
    │   └── IogurteVM.java
    │
    ├── layout/                      # Reusable layout wrappers
    │   ├── PaginaComSidebar.java    # BorderPane: sidebar left, content center
    │   └── Sidebar.java            # Navigation sidebar with theme switcher
    │
    ├── paginas/                     # One class per page/route
    │   ├── Dashboard.java
    │   └── Iogurtes.java           # Iogurtes list page (controller)
    │
    ├── components/                  # Reusable UI components
    │   └── iogurtes/               # Components scoped to the Iogurtes feature
    │       ├── CriarIogurteModal.java
    │       ├── DetalhesIogurteModal.java
    │       ├── EditarIogurteModal.java
    │       ├── ConfirmarApagarIogurteModal.java
    │       └── IogurteFormHelper.java   # package-private shared form builder
    │
    └── api/                         # Data / API service layer
        └── iogurtes/
            ├── IIogurtesApiService.java        # Interface (contract)
            ├── MockIogurtesApiService.java     # In-memory implementation
            ├── RealIogurtesApiService.java     # HTTP stub (TODO)
            └── IogurtesApiServiceFactory.java  # One-line toggle: mock ↔ real
```

| Folder | Purpose |
|---|---|
| `model/` | Data transfer / view-model objects. No JavaFX imports. |
| `layout/` | Structural wrappers used by every page. |
| `paginas/` | One class per navigable page. Extends `PaginaComSidebar`. |
| `components/<feature>/` | Self-contained UI components (modals, cards, etc.). |
| `api/<feature>/` | Service interface + mock + real implementations. |

---

## How to Create a New Page

### 1 — Create the page class

Create `src/main/java/com/gestaoiogurtes/paginas/MinhaNovaPage.java`:

```java
package com.gestaoiogurtes.paginas;

import atlantafx.base.theme.Styles;
import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.PaginaComSidebar;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MinhaNovaPage extends PaginaComSidebar {

    public MinhaNovaPage(GestaoIogurtes app) {
        super(app, criarConteudo());
    }

    private static VBox criarConteudo() {
        var titulo = new Label("Minha Nova Página");
        titulo.getStyleClass().add(Styles.TITLE_2);

        var area = new VBox(16, titulo);
        area.setPadding(new Insets(32));
        return area;
    }
}
```

### 2 — Register navigation in the Sidebar

In `Sidebar.java`, add a nav button inside the `navArea` block:

```java
var btnMinha = criarItem("Minha Página", new FontIcon(MaterialDesignX.SOME_ICON));
btnMinha.setOnAction(e -> app.getStage().getScene().setRoot(new MinhaNovaPage(app)));
```

Add it to the `navArea` VBox children list.

### 3 — (Optional) Add a top-level navigate method

In `GestaoIogurtes.java` you can add:

```java
public void navegarParaMinhaPage() {
    stage.getScene().setRoot(new MinhaNovaPage(this));
}
```

### Theme / styles

- The AtlantaFX theme is set globally in `GestaoIogurtes.start()` via `Application.setUserAgentStylesheet(...)`.
- The sidebar "Mudar tema" button cycles through all available AtlantaFX themes at runtime.
- Use `Styles.*` constants (from `atlantafx.base.theme.Styles`) for typography, colours, and button variants — never hardcode colour hex values.

---

## Component Conventions

### Reusable Modal Pattern

Each modal is a **plain Java class** (not a JavaFX control) with:
- A constructor that accepts the **data it needs** + the **API service** + a **`Runnable` callback** (fired on success).
- A single `public void show(Window owner)` method that builds and opens the dialog.

**Naming:** `<Action><Domain>Modal.java` — e.g. `CriarIogurteModal`, `EditarIogurteModal`.

**Location:** `src/main/java/com/gestaoiogurtes/components/<feature>/`

**Example skeleton:**

```java
package com.gestaoiogurtes.components.iogurtes;

import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.scene.control.*;
import javafx.stage.Window;

public class ExemploIogurteModal {

    private final IogurteVM iogurte;
    private final IIogurtesApiService api;
    private final Runnable onConcluido;

    public ExemploIogurteModal(IogurteVM iogurte, IIogurtesApiService api, Runnable onConcluido) {
        this.iogurte = iogurte;
        this.api = api;
        this.onConcluido = onConcluido;
    }

    public void show(Window owner) {
        var dialog = new Dialog<Void>();
        dialog.setTitle("Exemplo");
        dialog.initOwner(owner);

        var btnOk     = new ButtonType("OK",       ButtonBar.ButtonData.OK_DONE);
        var btnCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, btnCancel);

        dialog.setResultConverter(bt -> {
            if (bt == btnOk) {
                // perform action via api, then:
                onConcluido.run();
            }
            return null;
        });

        dialog.showAndWait();
    }
}
```

**Calling from the page controller:**

```java
private void abrirExemploModal(IogurteVM iogurte) {
    new ExemploIogurteModal(iogurte, api, this::renderizarTabela)
            .show(getScene().getWindow());
}
```

### Shared form helpers

If two modals (e.g. Criar and Editar) share form fields, extract them to a **package-private** helper class named `<Domain>FormHelper.java` in the same `components/<feature>/` package. See `IogurteFormHelper.java` as the reference example.

### Naming conventions

| Thing | Convention | Example |
|---|---|---|
| Page class | `PascalCase` noun | `Iogurtes`, `Dashboard` |
| Modal class | `<Verb><Domain>Modal` | `CriarIogurteModal` |
| Form helper | `<Domain>FormHelper` | `IogurteFormHelper` |
| Model class | `<Domain>VM` | `IogurteVM` |
| API interface | `I<Domain>ApiService` | `IIogurtesApiService` |
| Mock impl | `Mock<Domain>ApiService` | `MockIogurtesApiService` |
| Real impl | `Real<Domain>ApiService` | `RealIogurtesApiService` |
| Factory | `<Domain>ApiServiceFactory` | `IogurtesApiServiceFactory` |

---

## API Conventions

### Adding a new operation

1. **Declare it** in the interface (`IIogurtesApiService.java`):
   ```java
   void arquivar(IogurteVM iogurte);
   ```
2. **Implement it** in `MockIogurtesApiService`:
   ```java
   @Override
   public void arquivar(IogurteVM iogurte) {
       iogurte.visivelCliente = false; // or move to an archived list
   }
   ```
3. **Stub it** in `RealIogurtesApiService`:
   ```java
   @Override
   public void arquivar(IogurteVM iogurte) {
       // TODO: PATCH /api/iogurtes/{id}/arquivar
       throw new UnsupportedOperationException("Real API not implemented yet");
   }
   ```

### Switching mock → real

Open `IogurtesApiServiceFactory.java` and change **one line**:

```java
// Before (mock)
private static final boolean USE_MOCK = true;

// After (real API)
private static final boolean USE_MOCK = false;
```

### Adding a new domain (e.g. Encomendas)

1. Create `src/main/java/com/gestaoiogurtes/api/encomendas/`
2. Add `IEncomendasApiService.java`, `MockEncomendasApiService.java`, `RealEncomendasApiService.java`, `EncomendasApiServiceFactory.java`
3. Follow the same interface + factory pattern.
