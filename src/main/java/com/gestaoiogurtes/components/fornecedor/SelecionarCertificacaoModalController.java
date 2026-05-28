package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.models.fornecedor.AddCertificacaoRequest;
import com.gestaoiogurtes.services.FornecedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modal para seleccionar uma certificação e adicionar ao fornecedor.
 * Baseado directamente em SelecionarEmpresaModalController.
 */
public class SelecionarCertificacaoModalController {

    @FXML private Button    btnAnterior;
    @FXML private Button    btnProximo;
    @FXML private Label     lblPagina;
    @FXML private VBox      listaContainer;
    @FXML private HBox      hboxLoading;
    @FXML private Button    btnAdicionar;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;
    @FXML private Label     lblErro;

    private Stage            dialogStage;
    private FornecedorService service;
    private String           fornecedorId;
    private Runnable         onSuccess;

    private String selectedId;
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String fornecedorId, FornecedorService service,
                            Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarCertificacaoModalController.class
                            .getResource("/fxml/components/fornecedor/SelecionarCertificacaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Adicionar Certificação");
            stage.setScene(new Scene(root));

            SelecionarCertificacaoModalController ctrl = loader.getController();
            ctrl.dialogStage  = stage;
            ctrl.service      = service;
            ctrl.fornecedorId = fornecedorId;
            ctrl.onSuccess    = onSuccess;

            ctrl.carregarPagina(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Inicialização ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        btnAdicionar.setDisable(true);
        lblErro.setText("");

        // Recalcular estado do botão sempre que qualquer data mudar.
        // Assim se o utilizador seleccionar a certificação primeiro e só depois
        // preencher as datas, o botão activa-se correctamente.
        dpDataInicio.valueProperty().addListener((obs, o, n) -> actualizarBotaoAdicionar());
        dpDataFim.valueProperty().addListener((obs, o, n)    -> actualizarBotaoAdicionar());
    }

    // ── Handlers de paginação ───────────────────────────────────────────────

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) carregarPagina(currentPage - 1);
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1) carregarPagina(currentPage + 1);
    }

    // ── Handlers do footer ──────────────────────────────────────────────────

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleAdicionar() {
        lblErro.setText("");

        // Validação
        if (selectedId == null || selectedId.isBlank()) {
            lblErro.setText("Selecione uma certificação.");
            return;
        }
        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim    = dpDataFim.getValue();
        if (inicio == null || fim == null) {
            lblErro.setText("Preencha ambas as datas.");
            return;
        }
        if (!fim.isAfter(inicio)) {
            lblErro.setText("A data de fim deve ser posterior à data de início.");
            return;
        }

        btnAdicionar.setDisable(true);
        btnAdicionar.setText("A adicionar...");

        var req = new AddCertificacaoRequest(selectedId, inicio.format(FMT), fim.format(FMT));
        service.addCertificacao(fornecedorId, req, state -> {
            if (state.isLoading()) {
                // já desactivado acima
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnAdicionar.setDisable(false);
                btnAdicionar.setText("Adicionar");
                lblErro.setText("Erro: " + (state.getErrorMessage() != null ? state.getErrorMessage() : "desconhecido"));
            }
        });
    }

    // ── Carregamento da lista ───────────────────────────────────────────────

    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        service.getAllCertificacoesDisponiveis(page, PAGE_SIZE, state -> {
            if (state.isLoading()) {
                setLoadingVisible(true);
            } else if (state.isSuccess()) {
                setLoadingVisible(false);
                var resposta = state.getData();
                if (resposta != null) {
                    currentPage = page;
                    totalPages  = Math.max(1, resposta.totalPages);
                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);
                    renderizarLista(resposta.content != null ? resposta.content : List.of());
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                var lbl = new Label("Erro ao carregar: " + state.getErrorMessage());
                lbl.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lbl);
            }
        });
    }

    private void renderizarLista(List<CertificacaoResponse> certs) {
        listaContainer.getChildren().clear();

        if (certs.isEmpty()) {
            var lbl = new Label("Sem certificações disponíveis.");
            lbl.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lbl);
            return;
        }

        for (CertificacaoResponse cert : certs) {
            String id   = cert.id   != null ? cert.id.toString() : "";
            String nome = cert.nome != null ? cert.nome : "(sem nome)";

            var linha = new HBox();
            linha.getStyleClass().add("selecionar-tipo-row");

            var radio = new RadioButton();
            radio.getStyleClass().add("selecionar-tipo-radio");
            radio.setSelected(id.equals(selectedId));

            var lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");

            linha.getChildren().addAll(radio, lblNome);

            Runnable seleccionar = () -> {
                selectedId   = id;
                selectedNome = nome;
                actualizarBotaoAdicionar();
                listaContainer.getChildren().forEach(node -> {
                    if (node instanceof HBox hb) {
                        hb.getChildren().stream()
                                .filter(c -> c instanceof RadioButton)
                                .map(c -> (RadioButton) c)
                                .forEach(rb -> rb.setSelected(false));
                    }
                });
                radio.setSelected(true);
            };

            radio.setOnAction(e -> seleccionar.run());
            linha.setOnMouseClicked(e -> seleccionar.run());

            listaContainer.getChildren().add(linha);
        }

        if (selectedId != null && !selectedId.isBlank()) {
            actualizarBotaoAdicionar();
        }
    }

    private void actualizarBotaoAdicionar() {
        // Fica activo só quando há selecção E ambas as datas preenchidas E fim > inicio
        boolean selOk = selectedId != null && !selectedId.isBlank();
        LocalDate ini = dpDataInicio.getValue();
        LocalDate fim = dpDataFim.getValue();
        boolean datasOk = ini != null && fim != null && fim.isAfter(ini);
        btnAdicionar.setDisable(!(selOk && datasOk));
    }

    // ── Helper de loading ────────────────────────────────────────────────────

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
