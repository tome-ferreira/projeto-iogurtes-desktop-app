package com.gestaoiogurtes.components.encomendaMp;

import com.gestaoiogurtes.models.encomendaMp.EncomendaMpLinhaResponse;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;
import com.gestaoiogurtes.services.EncomendaMpService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class DetalhesEncomendaMpModalController {

    @FXML
    private Label lblFornecedor;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblData;
    @FXML
    private Label lblEntregaPrevista;
    @FXML
    private Label lblMoeda;
    @FXML
    private Label lblTotalSemIva;
    @FXML
    private Label lblTotalComIva;
    @FXML
    private Label lblObservacoes;
    @FXML
    private VBox linhasContainer;

    // Footer buttons
    @FXML
    private Button btnCancelarEncomenda;
    @FXML
    private Button btnAprovar;
    @FXML
    private Button btnRecebida;

    private Stage dialogStage;
    private EncomendaMpService service;
    private EncomendaMpResponse encomenda;

    private Consumer<String> onSuccess;

    public static void show(EncomendaMpResponse item,
            EncomendaMpService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesEncomendaMpModalController.class
                            .getResource("/fxml/components/encomendaMp/DetalhesEncomendaMpModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhe da Encomenda matéria prima");
            stage.setScene(new Scene(root));

            DetalhesEncomendaMpModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onSuccess = onSuccess;
            ctrl.preencherDados(item);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }

    @FXML
    private void handleAprovar() {
        ConfirmarAprovarEncomendaModalController.show(
                encomenda.id.toString(),
                service,
                dialogStage,
                mensagem -> {
                    dialogStage.close();
                    if (onSuccess != null)
                        onSuccess.accept(mensagem);
                });
    }

    @FXML
    private void handleCancelarEncomenda() {
        ConfirmarCancelarEncomendaModalController.show(
                encomenda.id.toString(),
                service,
                dialogStage,
                mensagem -> {
                    dialogStage.close();
                    if (onSuccess != null)
                        onSuccess.accept(mensagem);
                });
    }

    @FXML
    private void handleRecebida() {
        ConfirmarRecebidaEncomendaModalController.show(
                encomenda.id.toString(),
                service,
                dialogStage,
                mensagem -> {
                    dialogStage.close();
                    if (onSuccess != null)
                        onSuccess.accept(mensagem);
                });
    }

    private void preencherDados(EncomendaMpResponse item) {
        this.encomenda = item;

        lblFornecedor.setText(item.fornecedorNome != null ? item.fornecedorNome : "—");
        lblEstado.setText(EnumDisplayHelper.estadoEncomendaMp(item.estado));
        lblData.setText(formatarData(item.dataEncomenda));
        lblEntregaPrevista.setText(item.dataEntregaPrevista != null ? item.dataEntregaPrevista : "—");
        lblMoeda.setText(buildMoedaStr(item));
        lblTotalSemIva.setText(formatarEur(item.totalPrecoEurSemIva));
        lblTotalComIva.setText(formatarEur(item.totalPrecoEurComIva));
        lblObservacoes.setText(item.observacoes != null && !item.observacoes.isBlank()
                ? item.observacoes
                : "—");

        renderizarLinhas(item.linhas != null ? item.linhas : List.of());
        configurarBotoesFooter(item.estado);
    }

    private void configurarBotoesFooter(String estado) {
        boolean pendente = "PENDENTE".equals(estado);
        boolean encomendada = "ENCOMENDADA".equals(estado);
        boolean comAcao = pendente || encomendada;

        setButtonVisible(btnCancelarEncomenda, comAcao);
        setButtonVisible(btnAprovar, pendente);
        setButtonVisible(btnRecebida, encomendada);
    }

    private void setButtonVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    private void renderizarLinhas(List<EncomendaMpLinhaResponse> linhas) {
        linhasContainer.getChildren().clear();

        if (linhas.isEmpty()) {
            Label lblVazio = new Label("Sem linhas de encomenda.");
            lblVazio.getStyleClass().add("detalhe-sem-linhas");
            linhasContainer.getChildren().add(lblVazio);
            return;
        }

        // Cabeçalho das linhas
        HBox header = new HBox();
        header.getStyleClass().add("detalhe-linha-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(
                detalheHeaderCol("Matéria Prima", 200, true),
                detalheHeaderCol("Qtd.", 80, false),
                detalheHeaderCol("Preço Unit.", 100, false),
                detalheHeaderCol("Subtotal (€)", 100, false));
        linhasContainer.getChildren().add(header);

        for (EncomendaMpLinhaResponse linha : linhas) {
            HBox row = new HBox();
            row.getStyleClass().add("detalhe-linha-row");
            row.setAlignment(Pos.CENTER_LEFT);

            Label lblNome = new Label(linha.materiaNome != null ? linha.materiaNome : "—");
            lblNome.getStyleClass().add("detalhe-linha-nome");
            lblNome.setMinWidth(200);
            lblNome.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Label lblQtd = new Label(linha.quantidade != null
                    ? String.format("%.3f", linha.quantidade)
                    : "—");
            lblQtd.getStyleClass().add("detalhe-linha-dados");
            lblQtd.setPrefWidth(80);

            Label lblPreco = new Label(linha.precoUnitarioEur != null
                    ? String.format("%.2f €", linha.precoUnitarioEur)
                    : "—");
            lblPreco.getStyleClass().add("detalhe-linha-dados");
            lblPreco.setPrefWidth(100);

            Label lblSubtotal = new Label(linha.subtotalEur != null
                    ? String.format("%.2f €", linha.subtotalEur)
                    : "—");
            lblSubtotal.getStyleClass().add("detalhe-linha-dados");
            lblSubtotal.setPrefWidth(100);

            row.getChildren().addAll(lblNome, lblQtd, lblPreco, lblSubtotal);
            linhasContainer.getChildren().add(row);
        }
    }

    private Label detalheHeaderCol(String texto, double largura, boolean grow) {
        Label lbl = new Label(texto.toUpperCase());
        lbl.getStyleClass().add("detalhe-linha-header-label");
        lbl.setMinWidth(largura);
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setPrefWidth(largura);
        }
        return lbl;
    }

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isBlank())
            return "—";
        return dataIso.length() >= 10 ? dataIso.substring(0, 10) : dataIso;
    }

    private String formatarEur(Double valor) {
        return valor != null ? String.format("%.2f €", valor) : "—";
    }

    private String buildMoedaStr(EncomendaMpResponse item) {
        if (item.moedaCodigo == null)
            return "—";
        String str = item.moedaCodigo;
        if (item.moedaSimbolo != null)
            str += " (" + item.moedaSimbolo + ")";
        return str;
    }
}
