package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.services.EmpresaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal para seleccionar uma empresa de uma lista paginada.
 *
 * <h3>Comportamento</h3>
 * <ul>
 *   <li>Carrega 10 empresas por página via {@link EmpresaService#getAll}.</li>
 *   <li>A selecção persiste entre mudanças de página (armazenada em memória).</li>
 *   <li>O botão "Continuar" só fica activo quando uma empresa está seleccionada.</li>
 *   <li>{@code onConfirm} é chamado <strong>apenas</strong> ao clicar "Continuar"
 *       — fechar com o X não dispara o callback.</li>
 * </ul>
 */
public class SelecionarEmpresaModalController {

    /* ── Injecções FXML ────────────────────────────────────────────────── */
    @FXML private Button           btnAnterior;
    @FXML private Button           btnProximo;
    @FXML private Label            lblPagina;
    @FXML private VBox             listaContainer;
    @FXML private HBox             hboxLoading;
    @FXML private Button           btnContinuar;

    /* ── Estado do modal ───────────────────────────────────────────────── */
    private Stage            dialogStage;
    private EmpresaService   empresaService;
    private Consumer<EmpresaSelecao> onConfirm;

    /** ID da empresa actualmente seleccionada (persiste entre páginas). */
    private String selectedId;
    /** Nome da empresa actualmente seleccionada. */
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages  = 1;

    private static final int PAGE_SIZE = 10;

    /* ── Método estático de abertura ───────────────────────────────────── */

    /**
     * Abre o modal de selecção de empresa.
     *
     * @param empresaService  serviço para obter as empresas
     * @param owner           janela proprietária (para centering)
     * @param preSelectedId   UUID pré-seleccionado (pode ser {@code null})
     * @param onConfirm       callback invocado ao clicar "Continuar"; nunca
     *                        chamado ao fechar com o X
     */
    public static void show(
            EmpresaService empresaService,
            Window owner,
            String preSelectedId,
            Consumer<EmpresaSelecao> onConfirm) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarEmpresaModalController.class
                            .getResource("/fxml/components/utilizadores/SelecionarEmpresaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Selecionar Empresa");
            stage.setScene(new Scene(root));

            SelecionarEmpresaModalController ctrl = loader.getController();
            ctrl.dialogStage    = stage;
            ctrl.empresaService = empresaService;
            ctrl.onConfirm      = onConfirm;

            if (preSelectedId != null && !preSelectedId.isBlank()) {
                ctrl.selectedId = preSelectedId;
                // nome será preenchido assim que a linha aparecer na lista
            }

            ctrl.carregarPagina(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ── Initialização FXML ────────────────────────────────────────────── */

    @FXML
    public void initialize() {
        // Continuar desactivado até haver uma selecção
        btnContinuar.setDisable(true);
    }

    /* ── Handlers de navegação ─────────────────────────────────────────── */

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) {
            carregarPagina(currentPage - 1);
        }
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1) {
            carregarPagina(currentPage + 1);
        }
    }

    /* ── Handler do footer ─────────────────────────────────────────────── */

    @FXML
    private void handleContinuar() {
        if (selectedId != null && !selectedId.isBlank()) {
            onConfirm.accept(new EmpresaSelecao(selectedId, selectedNome));
        }
        dialogStage.close();
    }

    /* ── Lógica de carregamento ────────────────────────────────────────── */

    /**
     * Carrega a página solicitada e re-renderiza a lista.
     */
    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        empresaService.getAll(page, PAGE_SIZE, state -> {
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
                // Mostra mensagem de erro inline na lista
                Label lblErro = new Label("Erro ao carregar empresas: " + state.getErrorMessage());
                lblErro.getStyleClass().add("selecionar-empresa-erro");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    /**
     * Constrói uma linha por empresa e adiciona-a ao {@code listaContainer}.
     */
    private void renderizarLista(List<EmpresaResponse> empresas) {
        listaContainer.getChildren().clear();

        if (empresas.isEmpty()) {
            Label lblVazio = new Label("Sem empresas disponíveis.");
            lblVazio.getStyleClass().add("selecionar-empresa-vazio");
            listaContainer.getChildren().add(lblVazio);
            return;
        }

        for (EmpresaResponse empresa : empresas) {
            String id   = empresa.id   != null ? empresa.id.toString() : "";
            String nome = empresa.nomeEmpresa != null ? empresa.nomeEmpresa : "(sem nome)";

            // Construir a linha
            HBox linha = new HBox();
            linha.getStyleClass().add("selecionar-empresa-row");

            RadioButton radio = new RadioButton();
            radio.getStyleClass().add("selecionar-empresa-radio");
            // Marcar se este for o ID pré-seleccionado / actualmente seleccionado
            radio.setSelected(id.equals(selectedId));

            Label lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-empresa-nome");

            linha.getChildren().addAll(radio, lblNome);

            // Actualizar selecção ao clicar na linha inteira ou no radio
            Runnable seleccionar = () -> {
                selectedId   = id;
                selectedNome = nome;
                btnContinuar.setDisable(false);
                // Desmarcar todos os outros radios nesta página
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

        // Se o ID seleccionado não estiver nesta página, garantir que o botão
        // Continuar fica activo (a selecção persiste em memória)
        if (selectedId != null && !selectedId.isBlank()) {
            btnContinuar.setDisable(false);
        }
    }

    /* ── Helpers de UI ─────────────────────────────────────────────────── */

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
