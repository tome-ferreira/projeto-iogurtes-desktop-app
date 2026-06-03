package com.gestaoiogurtes.components.encomenda;

import com.gestaoiogurtes.models.encomenda.EncomendaResponse;
import com.gestaoiogurtes.models.encomenda.EncomendaPalletResponse;
import com.gestaoiogurtes.services.EncomendaService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DetalhesEncomendaModalController {

    public Stage dialogStage;
    public EncomendaService service;

    @FXML private TextField txtId;
    @FXML private TextField txtUtilizador;
    @FXML private TextField txtData;
    @FXML private TextField txtEstado;
    @FXML private TextField txtMoeda;
    @FXML private TextField txtTotal;
    @FXML private TextField txtCriadoEm;
    @FXML private VBox palletsContainer;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void show(EncomendaResponse encomenda, EncomendaService service, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesEncomendaModalController.class.getResource("/fxml/components/encomenda/DetalhesEncomendaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes da Encomenda");
            stage.setScene(new Scene(root));

            DetalhesEncomendaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            
            // fetch detailed info
            ctrl.carregarDetalhes(encomenda.id.toString());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDetalhes(String id) {
        service.getById(id, state -> {
            if (state.isSuccess() && state.getData() != null) {
                preencherCampos(state.getData());
            } else if (state.isError()) {
                txtUtilizador.setText("Erro ao carregar os detalhes.");
            }
        });
    }

    private void preencherCampos(com.gestaoiogurtes.models.encomenda.EncomendaDetalheResponse e) {
        txtId.setText(e.id != null ? e.id.toString() : "—");
        txtUtilizador.setText(e.userNome != null ? e.userNome : "—");
        txtData.setText(e.dataEncomenda != null ? e.dataEncomenda.format(formatter) : "—");
        txtEstado.setText(EnumDisplayHelper.estadoEncomenda(e.estado));
        
        String moeda = (e.moedaCodigo != null ? e.moedaCodigo : "") + " " + (e.moedaSimbolo != null ? "(" + e.moedaSimbolo + ")" : "");
        txtMoeda.setText(!moeda.trim().isEmpty() ? moeda.trim() : "—");
        
        String precoEur = e.totalPrecoEur != null ? String.format("%.2f €", e.totalPrecoEur) : "—";
        txtTotal.setText(precoEur);
        
        txtCriadoEm.setText(e.createdAt != null ? e.createdAt.format(formatter) : "—");

        renderizarPallets(e);
    }

    private void renderizarPallets(com.gestaoiogurtes.models.encomenda.EncomendaDetalheResponse e) {
        palletsContainer.getChildren().clear();
        
        if (e.pallets == null || e.pallets.isEmpty()) {
            var lblEmpty = new Label("Nenhuma pallet associada a esta encomenda.");
            lblEmpty.getStyleClass().add("form-label");
            lblEmpty.setStyle("-fx-text-fill: -color-fg-muted;");
            palletsContainer.getChildren().add(lblEmpty);
            return;
        }

        // Tabela cabeçalho
        var header = new GridPane();
        header.setHgap(10);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 4 4 0 0; -fx-border-color: transparent transparent -color-border-subtle transparent; -fx-border-width: 0 0 1 0;");
        
        Label h1 = new Label("PRODUTO");
        Label h2 = new Label("TIPO PALLET");
        Label h3 = new Label("QTD");
        Label h4 = new Label("SUBTOTAL");
        
        h1.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        h2.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        h3.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        h4.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        
        GridPane.setHgrow(h1, Priority.ALWAYS);
        GridPane.setHgrow(h2, Priority.ALWAYS);
        GridPane.setHgrow(h3, Priority.NEVER);
        GridPane.setHgrow(h4, Priority.NEVER);
        
        header.add(h1, 0, 0);
        header.add(h2, 1, 0);
        header.add(h3, 2, 0);
        header.add(h4, 3, 0);
        
        palletsContainer.getChildren().add(header);

        // Tabela linhas
        for (EncomendaPalletResponse p : e.pallets) {
            var row = new GridPane();
            row.setHgap(10);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-border-color: transparent transparent -color-border-subtle transparent; -fx-border-width: 0 0 1 0;");
            
            Label l1 = new Label(p.produtoNome != null ? p.produtoNome : "—");
            Label l2 = new Label(p.palletTipoNome != null ? p.palletTipoNome : "—");
            Label l3 = new Label(p.quantidadePallets != null ? p.quantidadePallets.toString() : "0");
            Label l4 = new Label(p.subtotalComIvaEur != null ? String.format("%.2f €", p.subtotalComIvaEur) : "—");
            
            l1.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            l2.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            l3.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            l4.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            
            GridPane.setHgrow(l1, Priority.ALWAYS);
            GridPane.setHgrow(l2, Priority.ALWAYS);
            GridPane.setHgrow(l3, Priority.NEVER);
            GridPane.setHgrow(l4, Priority.NEVER);
            
            row.add(l1, 0, 0);
            row.add(l2, 1, 0);
            row.add(l3, 2, 0);
            row.add(l4, 3, 0);
            
            palletsContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
