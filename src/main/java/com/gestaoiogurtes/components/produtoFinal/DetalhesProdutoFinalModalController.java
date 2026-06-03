package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.produtoFinal.ProdutoMateriaResponse;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

/**
 * Controller do modal de detalhes do Produto Final.
 * Apresenta todos os campos de ProdutoFinalResponse em modo só de leitura.
 * Os enums são mostrados com rótulos legíveis em Português via EnumDisplayHelper.
 */
public class DetalhesProdutoFinalModalController {

    @FXML private TextField txtCodigoSku;
    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtAbreviacaoSabor;
    @FXML private TextField txtEstadoFisico;
    @FXML private TextField txtValidadeDias;
    @FXML private TextField txtPrecoVenda;
    @FXML private TextField txtPrecoPorKg;
    @FXML private TextField txtTaxaIva;
    @FXML private TextField txtQuantidadeLote;
    @FXML private TextField txtVisivelCliente;
    @FXML private TextField txtCreatedAt;
    @FXML private VBox      composicaoContainer;

    private Stage dialogStage;

    public static void show(ProdutoFinalResponse produto, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesProdutoFinalModalController.class
                            .getResource("/fxml/components/produtoFinal/DetalhesProdutoFinalModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Produto Final");
            stage.setScene(new Scene(root));

            DetalhesProdutoFinalModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.preencherDados(produto);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void preencherDados(ProdutoFinalResponse p) {
        txtCodigoSku.setText(p.codigoSku != null ? p.codigoSku : "—");
        txtNome.setText(p.nome != null ? p.nome : "—");
        txtDescricao.setText(p.descricao != null ? p.descricao : "—");
        txtAbreviacaoSabor.setText(p.abreviacaoSabor != null ? p.abreviacaoSabor : "—");
        txtEstadoFisico.setText(EnumDisplayHelper.estadoFisico(p.estadoFisico));
        txtValidadeDias.setText(p.validadeDias != null ? p.validadeDias + " dias" : "—");
        txtPrecoVenda.setText(p.precoVenda != null ? String.format("%.2f €", p.precoVenda) : "—");
        txtPrecoPorKg.setText(p.precoPorKg != null ? String.format("%.2f €/kg", p.precoPorKg) : "—");
        txtTaxaIva.setText(p.taxaIva != null ? p.taxaIva + "%" : "—");
        txtQuantidadeLote.setText(p.quantidadeLote != null ? String.valueOf(p.quantidadeLote) : "—");
        txtVisivelCliente.setText(Boolean.TRUE.equals(p.visivelCliente) ? "Sim" : "Não");
        txtCreatedAt.setText(p.createdAt != null ? p.createdAt.toString().replace("T", " ").substring(0, 16) : "—");

        // Composição
        composicaoContainer.getChildren().clear();
        List<ProdutoMateriaResponse> composicao = p.composicao;
        if (composicao == null || composicao.isEmpty()) {
            Label lblVazio = new Label("Sem composição definida.");
            lblVazio.getStyleClass().add("composicao-vazia-label");
            composicaoContainer.getChildren().add(lblVazio);
        } else {
            for (ProdutoMateriaResponse item : composicao) {
                HBox row = new HBox();
                row.getStyleClass().add("composicao-row");
                row.setAlignment(Pos.CENTER_LEFT);

                String nome = item.materiaNome != null ? item.materiaNome : "—";
                String unidade = item.materiaUnidade != null ? item.materiaUnidade : "";
                String qtd = item.quantidadePorUnidadeProduto != null
                        ? String.format("%.3f %s", item.quantidadePorUnidadeProduto, unidade)
                        : "—";

                Label lblNome = new Label(nome);
                lblNome.getStyleClass().add("composicao-row-nome");
                HBox.setHgrow(lblNome, Priority.ALWAYS);

                Label lblQtd = new Label(qtd);
                lblQtd.getStyleClass().add("composicao-row-qtd");

                row.getChildren().addAll(lblNome, lblQtd);
                composicaoContainer.getChildren().add(row);
            }
        }
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
