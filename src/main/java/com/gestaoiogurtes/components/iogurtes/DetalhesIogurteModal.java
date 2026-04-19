package com.gestaoiogurtes.components.iogurtes;

import atlantafx.base.theme.Styles;
import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Self-contained modal showing the full details of an iogurte,
 * with actions to edit or delete it.
 *
 * <pre>
 *   new DetalhesIogurteModal(iogurte, api, this::renderizarTabela).show(getScene().getWindow());
 * </pre>
 */
public class DetalhesIogurteModal {

    private final IogurteVM iogurte;
    private final IIogurtesApiService api;
    private final Runnable onAtualizado;

    public DetalhesIogurteModal(IogurteVM iogurte, IIogurtesApiService api, Runnable onAtualizado) {
        this.iogurte = iogurte;
        this.api = api;
        this.onAtualizado = onAtualizado;
    }

    public void show(Window owner) {
        var dialog = new Dialog<Void>();
        dialog.setTitle("Detalhes — " + iogurte.nome);
        dialog.initOwner(owner);

        var btnEditar = new ButtonType("Editar",  ButtonBar.ButtonData.OTHER);
        var btnApagar = new ButtonType("Apagar",  ButtonBar.ButtonData.OTHER);
        var btnFechar = new ButtonType("Fechar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEditar, btnApagar, btnFechar);

        // style the danger button
        var nodeBtnApagar = (Button) dialog.getDialogPane().lookupButton(btnApagar);
        nodeBtnApagar.getStyleClass().add(Styles.DANGER);

        dialog.getDialogPane().setContent(criarConteudoDetalhes(iogurte));
        dialog.getDialogPane().setPrefWidth(480);

        dialog.setResultConverter(bt -> {
            if (bt == btnEditar) {
                new EditarIogurteModal(iogurte, api, onAtualizado).show(owner);
            }
            if (bt == btnApagar) {
                new ConfirmarApagarIogurteModal(iogurte, api, onAtualizado).show(owner);
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ── Details content ───────────────────────────────────────────────────────

    private static VBox criarConteudoDetalhes(IogurteVM i) {
        var grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 0, 8, 0));

        int row = 0;
        adicionarDetalhe(grid, row++, "SKU",            i.codigoSku);
        adicionarDetalhe(grid, row++, "Nome",           i.nome);
        adicionarDetalhe(grid, row++, "Descrição",      i.descricao != null ? i.descricao : "—");
        adicionarDetalhe(grid, row++, "Validade",       i.validadeDias + " dias");
        adicionarDetalhe(grid, row++, "Preço venda",    String.format("%.2f €", i.precoVenda));
        adicionarDetalhe(grid, row++, "Preço por kg",   String.format("%.2f €/kg", i.precoPorKg));
        adicionarDetalhe(grid, row++, "Stock atual",    String.valueOf(i.stockAtual));
        adicionarDetalhe(grid, row++, "Qtd. por lote",  String.valueOf(i.quantidadeLote));
        adicionarDetalhe(grid, row,   "Visível cliente", i.visivelCliente ? "Sim" : "Não");

        return new VBox(grid);
    }

    private static void adicionarDetalhe(GridPane grid, int row, String label, String valor) {
        var lbl = new Label(label);
        lbl.getStyleClass().addAll(Styles.TEXT_BOLD, Styles.TEXT_SMALL);
        lbl.setMinWidth(130);

        var val = new Label(valor);
        val.getStyleClass().add(Styles.TEXT_MUTED);
        val.setWrapText(true);

        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}
