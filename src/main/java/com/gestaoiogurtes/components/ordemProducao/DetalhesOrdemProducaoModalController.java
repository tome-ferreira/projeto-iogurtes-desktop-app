package com.gestaoiogurtes.components.ordemProducao;

import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoProdutoResponse;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse;
import com.gestaoiogurtes.services.OrdemProducaoService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.application.Platform;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class DetalhesOrdemProducaoModalController {

    @FXML
    private VBox conteudoContainer;
    @FXML
    private VBox loadingOverlay;

    @FXML
    private Label lblUtilizador;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblDataInicio;
    @FXML
    private Label lblDataFim;
    // @FXML private Label lblAprovadoEm;
    @FXML
    private Label lblObservacoes;

    @FXML
    private VBox produtosContainer;
    @FXML
    private VBox linhasProdutosList;

    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnAprovar;
    @FXML
    private Button btnConcluir;
    @FXML
    private Button btnFechar;

    private Stage dialogStage;
    private OrdemProducaoService service;
    private String ordemId;

    private Consumer<String> onMutacaoBemSucedida;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void show(
            OrdemProducaoResponse resumo,
            OrdemProducaoService service,
            Window owner,
            Consumer<String> onMutacaoBemSucedida) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesOrdemProducaoModalController.class
                            .getResource("/fxml/components/ordemProducao/DetalhesOrdemProducaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes da Ordem de Produção");
            stage.setScene(new Scene(root));

            DetalhesOrdemProducaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.ordemId = resumo.id.toString();
            ctrl.onMutacaoBemSucedida = onMutacaoBemSucedida;

            stage.show();
            ctrl.carregarDetalhes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDetalhes() {
        setLoading(true);

        service.getById(ordemId, state -> {
            if (state.isSuccess() && state.getData() != null) {
                setLoading(false);
                preencherDados(state.getData());
                configureFooterButtons(state.getData().estado);
            } else if (state.isError()) {
                setLoading(false);
                mostrarErro(state.getErrorMessage());
            }
        });
    }

    private void preencherDados(OrdemProducaoResponse dados) {
        lblUtilizador.setText(dados.userNome != null ? dados.userNome : "—");

        lblEstado.setText(EnumDisplayHelper.estadoOrdemProducao(dados.estado));
        lblEstado.getStyleClass().setAll("pill-ordem-estado", "pill-estado-" + estadoApiParaCss(dados.estado));

        lblDataInicio.setText(formatarData(dados.dataInicio));
        lblDataFim.setText(formatarData(dados.dataFim));
        // lblAprovadoEm.setText(formatarData(dados.aprovadoEm));

        if (dados.observacoes != null && !dados.observacoes.isBlank()) {
            lblObservacoes.setText(dados.observacoes);
        } else {
            lblObservacoes.setText("Sem observações.");
            lblObservacoes.getStyleClass().add("text-muted");
        }

        linhasProdutosList.getChildren().clear();
        List<OrdemProducaoProdutoResponse> prods = dados.produtos;
        if (prods == null || prods.isEmpty()) {
            Label vazia = new Label("Nenhum produto associado.");
            vazia.getStyleClass().add("detalhe-sem-linhas");
            linhasProdutosList.getChildren().add(vazia);
        } else {
            for (int i = 0; i < prods.size(); i++) {
                var p = prods.get(i);
                HBox row = new HBox();
                row.getStyleClass().add("detalhe-linha-row");
                row.setAlignment(Pos.CENTER_LEFT);

                Label lblNome = new Label(p.produtoNome != null ? p.produtoNome : "—");
                lblNome.getStyleClass().add("detalhe-linha-nome");
                lblNome.setPrefWidth(250);
                HBox.setHgrow(lblNome, Priority.ALWAYS);

                Label lblSku = new Label(p.produtoSku != null ? p.produtoSku : "—");
                lblSku.getStyleClass().add("detalhe-linha-dados");
                lblSku.setPrefWidth(120);

                Label lblQtd = new Label(p.quantidadeKg != null ? String.format("%.3f kg", p.quantidadeKg) : "—");
                lblQtd.getStyleClass().add("detalhe-linha-dados");
                lblQtd.setPrefWidth(100);

                row.getChildren().addAll(lblNome, lblSku, lblQtd);
                linhasProdutosList.getChildren().add(row);
            }
        }
    }

    private void mostrarErro(String erro) {
        conteudoContainer.getChildren().clear();
        Label errLabel = new Label("Erro ao carregar detalhes: " + erro);
        errLabel.setStyle("-fx-text-fill: -color-danger-fg; -fx-padding: 32; -fx-font-size: 14px;");
        conteudoContainer.getChildren().add(errLabel);
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }

    @FXML
    private void handleAprovar() {
        ConfirmarAprovarOrdemModalController.show(
                ordemId,
                service,
                dialogStage.getOwner(),
                mensagem -> {
                    dialogStage.close();
                    if (onMutacaoBemSucedida != null) {
                        onMutacaoBemSucedida.accept(mensagem);
                    }
                });
    }

    @FXML
    private void handleCancelar() {
        ConfirmarCancelarOrdemModalController.show(
                ordemId,
                service,
                dialogStage.getOwner(),
                mensagem -> {
                    dialogStage.close();
                    if (onMutacaoBemSucedida != null) {
                        onMutacaoBemSucedida.accept(mensagem);
                    }
                });
    }

    @FXML
    private void handleConcluir() {
        ConfirmarConcluirOrdemModalController.show(
                ordemId,
                service,
                dialogStage.getOwner(),
                mensagem -> {
                    dialogStage.close();
                    if (onMutacaoBemSucedida != null) {
                        onMutacaoBemSucedida.accept(mensagem);
                    }
                });
    }

    private void configureFooterButtons(String estado) {
        btnAprovar.setVisible(false);
        btnAprovar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
        btnConcluir.setVisible(false);
        btnConcluir.setManaged(false);
        btnFechar.setVisible(true);
        btnFechar.setManaged(true);

        if ("AGUARDA_APROVACAO".equals(estado)) {
            btnAprovar.setVisible(true);
            btnAprovar.setManaged(true);
            btnCancelar.setVisible(true);
            btnCancelar.setManaged(true);
        } else if ("EM_PRODUCAO".equals(estado)) {
            btnConcluir.setVisible(true);
            btnConcluir.setManaged(true);
            btnCancelar.setVisible(true);
            btnCancelar.setManaged(true);
        }
    }

    private String formatarData(LocalDateTime dt) {
        if (dt == null)
            return "—";
        return dt.format(FORMATTER);
    }

    private String estadoApiParaCss(String estado) {
        if (estado == null)
            return "desconhecido";
        return estado.toLowerCase().replace("_", "-");
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
        conteudoContainer.setVisible(!loading);
    }
}
