package com.gestaoiogurtes.components.encomenda;

import com.gestaoiogurtes.models.encomenda.EncomendaDetalheResponse;
import com.gestaoiogurtes.models.encomenda.EncomendaPalletResponse;
import com.gestaoiogurtes.models.encomenda.EncomendaResponse;
import com.gestaoiogurtes.services.EncomendaService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.function.Consumer;

public class DetalhesEncomendaModalController {

    public Stage dialogStage;
    public EncomendaService service;
    private EncomendaDetalheResponse encomendaDetalhe;
    private Consumer<String> onSuccess;

    @FXML private Label lblId;
    @FXML private Label lblUtilizador;
    @FXML private Label lblData;
    @FXML private Label lblEstado;
    @FXML private Label lblMoeda;
    @FXML private Label lblTotalBase;
    @FXML private Label lblTotalEur;
    @FXML private VBox palletsContainer;

    @FXML private Button btnCancelarEncomenda;
    @FXML private Button btnConfirmar;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void show(EncomendaResponse encomenda, EncomendaService service, Window owner, Consumer<String> onSuccess) {
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
            ctrl.onSuccess = onSuccess;
            
            ctrl.carregarDetalhes(encomenda.id.toString());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDetalhes(String id) {
        service.getById(id, state -> {
            if (state.isSuccess() && state.getData() != null) {
                encomendaDetalhe = state.getData();
                preencherCampos(encomendaDetalhe);
                configurarBotoesFooter(encomendaDetalhe.estado);
            } else if (state.isError()) {
                lblUtilizador.setText("Erro ao carregar os detalhes.");
            }
        });
    }

    private void preencherCampos(EncomendaDetalheResponse e) {
        lblId.setText(e.id != null ? e.id.toString() : "—");
        lblUtilizador.setText(e.userNome != null ? e.userNome : "—");
        lblData.setText(e.dataEncomenda != null ? e.dataEncomenda.format(formatter) : "—");
        lblEstado.setText(EnumDisplayHelper.estadoEncomenda(e.estado));
        
        String moeda = (e.moedaCodigo != null ? e.moedaCodigo : "") + " " + (e.moedaSimbolo != null ? "(" + e.moedaSimbolo + ")" : "");
        lblMoeda.setText(!moeda.trim().isEmpty() ? moeda.trim() : "—");
        
        String precoBase = e.totalPreco != null ? String.format("%.2f %s", e.totalPreco, (e.moedaSimbolo != null ? e.moedaSimbolo : "")) : "—";
        lblTotalBase.setText(precoBase);

        String precoEur = e.totalPrecoEur != null ? String.format("%.2f €", e.totalPrecoEur) : "—";
        lblTotalEur.setText(precoEur);
        
        renderizarPallets(e);
    }

    private void configurarBotoesFooter(String estado) {
        boolean pendente = "PENDENTE".equals(estado);
        boolean expedida = "EXPEDIDA".equals(estado);

        setButtonVisible(btnConfirmar, pendente);
        setButtonVisible(btnCancelarEncomenda, pendente || expedida);
    }

    private void setButtonVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    private void renderizarPallets(EncomendaDetalheResponse e) {
        palletsContainer.getChildren().clear();
        
        if (e.pallets == null || e.pallets.isEmpty()) {
            var lblEmpty = new Label("Nenhuma pallet associada a esta encomenda.");
            lblEmpty.getStyleClass().add("form-label");
            lblEmpty.setStyle("-fx-text-fill: -color-fg-muted;");
            palletsContainer.getChildren().add(lblEmpty);
            return;
        }

        var header = new GridPane();
        header.setHgap(10);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 4 4 0 0; -fx-border-color: transparent transparent -color-border-subtle transparent; -fx-border-width: 0 0 1 0;");
        
        Label h1 = new Label("PRODUTO");
        Label h2 = new Label("TIPO PALLET");
        Label h3 = new Label("QTD");
        
        h1.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        h2.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        h3.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -color-fg-muted;");
        
        GridPane.setHgrow(h1, Priority.ALWAYS);
        GridPane.setHgrow(h2, Priority.ALWAYS);
        GridPane.setHgrow(h3, Priority.NEVER);
        
        header.add(h1, 0, 0);
        header.add(h2, 1, 0);
        header.add(h3, 2, 0);
        
        palletsContainer.getChildren().add(header);

        for (EncomendaPalletResponse p : e.pallets) {
            var row = new GridPane();
            row.setHgap(10);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-border-color: transparent transparent -color-border-subtle transparent; -fx-border-width: 0 0 1 0;");
            
            Label l1 = new Label(p.produtoNome != null ? p.produtoNome : "—");
            Label l2 = new Label(p.palletTipoNome != null ? p.palletTipoNome : "—");
            Label l3 = new Label(p.quantidadePallets != null ? p.quantidadePallets.toString() : "0");
            
            l1.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            l2.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            l3.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
            
            GridPane.setHgrow(l1, Priority.ALWAYS);
            GridPane.setHgrow(l2, Priority.ALWAYS);
            GridPane.setHgrow(l3, Priority.NEVER);
            
            row.add(l1, 0, 0);
            row.add(l2, 1, 0);
            row.add(l3, 2, 0);
            
            palletsContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleConfirmar() {
        if (encomendaDetalhe == null) return;
        ConfirmarConfirmarEncomendaModalController.show(
                encomendaDetalhe.id.toString(),
                service,
                dialogStage,
                () -> {
                    dialogStage.close();
                    if (onSuccess != null) {
                        onSuccess.accept("Encomenda confirmada com sucesso.");
                    }
                });
    }

    @FXML
    private void handleCancelarEncomenda() {
        if (encomendaDetalhe == null) return;
        ConfirmarCancelarEncomendaClienteModalController.show(
                encomendaDetalhe.id.toString(),
                service,
                dialogStage,
                () -> {
                    dialogStage.close();
                    if (onSuccess != null) {
                        onSuccess.accept("Encomenda cancelada com sucesso.");
                    }
                });
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
