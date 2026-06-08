package com.gestaoiogurtes.components.stock;

import com.gestaoiogurtes.models.loteProducao.LoteProducaoDetalheResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.services.LoteProducaoService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DetalhesLoteProducaoModalController {

    @FXML private TextField txtNumeroLote;
    @FXML private TextField txtProdutoNome;
    @FXML private TextField txtQuantidadeKg;
    @FXML private TextField txtStockAtualKg;
    @FXML private TextField txtDataProducao;
    @FXML private TextField txtDataValidade;
    @FXML private TextField txtEstado;
    @FXML private TextField txtCreatedAt;

    public Stage dialogStage;
    public LoteProducaoService service;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void show(LoteProducaoResponse loteBase, LoteProducaoService service, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesLoteProducaoModalController.class
                    .getResource("/fxml/components/stock/DetalhesLoteProducaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Lote de Produção");
            stage.setScene(new Scene(root));

            DetalhesLoteProducaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;

            ctrl.carregarDetalhes(loteBase.id.toString());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDetalhes(String id) {
        txtNumeroLote.setText("A carregar...");
        txtProdutoNome.setText("A carregar...");
        txtQuantidadeKg.setText("A carregar...");
        txtStockAtualKg.setText("A carregar...");
        txtDataProducao.setText("A carregar...");
        txtDataValidade.setText("A carregar...");
        txtEstado.setText("A carregar...");
        txtCreatedAt.setText("A carregar...");

        service.getById(id, state -> {
            if (state.isSuccess() && state.getData() != null) {
                preencherCampos(state.getData());
            } else if (state.isError()) {
                txtNumeroLote.setText("Erro ao carregar");
            }
        });
    }

    private void preencherCampos(LoteProducaoDetalheResponse lote) {
        txtNumeroLote.setText(lote.numeroLote != null ? lote.numeroLote : "—");
        txtProdutoNome.setText(lote.produtoNome != null ? lote.produtoNome : "—");

        txtQuantidadeKg.setText(lote.quantidadeKg != null ? String.format("%.2f", lote.quantidadeKg) : "0.00");
        txtStockAtualKg.setText(lote.stockAtualKg != null ? String.format("%.2f", lote.stockAtualKg) : "0.00");

        txtDataProducao.setText(lote.dataProducao != null ? lote.dataProducao.format(DATE_FORMATTER) : "—");
        txtDataValidade.setText(lote.dataValidade != null ? lote.dataValidade.format(DATE_FORMATTER) : "—");

        txtEstado.setText(lote.estado != null ? com.gestaoiogurtes.utils.EnumDisplayHelper.estadoLoteProducao(lote.estado) : "—");

        txtCreatedAt.setText(lote.createdAt != null ? lote.createdAt.format(DATETIME_FORMATTER) : "—");
    }

    @FXML
    private void handleFechar() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
