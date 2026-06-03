package com.gestaoiogurtes.components.encomendaMp;

import com.gestaoiogurtes.models.encomendaMp.MateriaPrimaEncomendaSelecao;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaFornecedorResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal para seleccionar uma matéria prima ao criar uma encomenda MP.
 * Baseado em {@code SelecionarMateriaPrimaComposicaoModalController} — selecção + input de quantidade.
 *
 * <ul>
 *   <li>Carrega as matérias prima do fornecedor via {@code MateriaPrimaService.getFornecedores(materiaId)}
 *       mas, como a API agrupa por matéria (não por fornecedor), usa {@code GET /materias-primas/fornecedores}
 *       e filtra client-side pelo {@code fornecedorId} recebido.</li>
 *   <li>Mostra nome + preço unitário + símbolo de moeda por linha.</li>
 *   <li>"Adicionar" só activo quando: matéria seleccionada AND quantidade válida.</li>
 *   <li>Fechar com X: caller state inalterado.</li>
 * </ul>
 */
public class SelecionarMateriaEncomendaModalController {

    @FXML private Button    btnAnterior;
    @FXML private Button    btnProximo;
    @FXML private Label     lblPagina;
    @FXML private VBox      listaContainer;
    @FXML private HBox      hboxLoading;
    @FXML private Button    btnAdicionar;
    @FXML private TextField txtQuantidade;
    @FXML private Label     lblQuantidadeErro;

    private Stage                                     dialogStage;
    private MateriaPrimaService                       materiaPrimaService;
    private String                                    fornecedorId;
    private Consumer<MateriaPrimaEncomendaSelecao>    onConfirm;

    /** ID do registo MateriaPrimaFornecedor seleccionado (persiste entre páginas). */
    private String selectedMateriaFornecedorId;
    private String selectedNome;
    private Double selectedPrecoUnitario;
    private String selectedMoedaSimbolo;

    /** UUID da matéria prima (para o request de criação). */
    private String selectedMateriaId;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    /**
     * Abre o modal de selecção de matéria prima para encomenda MP.
     *
     * @param materiaPrimaService serviço de matéria prima
     * @param fornecedorId        UUID do fornecedor seleccionado
     * @param owner               janela pai
     * @param onConfirm           callback com a selecção confirmada
     */
    public static void show(
            MateriaPrimaService materiaPrimaService,
            String fornecedorId,
            Window owner,
            Consumer<MateriaPrimaEncomendaSelecao> onConfirm) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarMateriaEncomendaModalController.class
                            .getResource("/fxml/components/encomendaMp/SelecionarMateriaEncomendaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Adicionar Matéria Prima");
            stage.setScene(new Scene(root));

            SelecionarMateriaEncomendaModalController ctrl = loader.getController();
            ctrl.dialogStage          = stage;
            ctrl.materiaPrimaService  = materiaPrimaService;
            ctrl.fornecedorId         = fornecedorId;
            ctrl.onConfirm            = onConfirm;

            ctrl.carregarPagina(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        btnAdicionar.setDisable(true);
        lblQuantidadeErro.setText("");
        txtQuantidade.textProperty().addListener((obs, oldVal, newVal) -> validarEstadoBotao());
    }

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

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleAdicionar() {
        lblQuantidadeErro.setText("");

        if (selectedMateriaId == null || selectedMateriaId.isBlank()) {
            lblQuantidadeErro.setText("Selecione uma matéria prima.");
            return;
        }

        String qtdStr = txtQuantidade.getText() != null ? txtQuantidade.getText().trim() : "";
        if (qtdStr.isEmpty()) {
            lblQuantidadeErro.setText("A quantidade é obrigatória.");
            return;
        }

        double quantidade;
        try {
            quantidade = Double.parseDouble(qtdStr);
            if (quantidade <= 0) {
                lblQuantidadeErro.setText("A quantidade deve ser um número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            lblQuantidadeErro.setText("Quantidade inválida. Introduza um número válido.");
            return;
        }

        onConfirm.accept(new MateriaPrimaEncomendaSelecao(
                selectedMateriaId,
                selectedNome,
                selectedPrecoUnitario,
                selectedMoedaSimbolo,
                quantidade));
        dialogStage.close();
    }

    /**
     * Carrega a página de registos MateriaPrimaFornecedor e filtra pelo fornecedorId recebido.
     * A API não suporta filtro por fornecedor directamente em /materias-primas/fornecedores,
     * por isso carregamos uma página grande e filtramos client-side.
     */
    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        // Carrega todos os registos matéria-fornecedor e filtra client-side pelo fornecedorId
        materiaPrimaService.getAllFornecedores(page, PAGE_SIZE, state -> {
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

                    List<MateriaPrimaFornecedorResponse> itens =
                            resposta.content != null ? resposta.content : List.of();

                    // Filtrar pelo fornecedorId
                    List<MateriaPrimaFornecedorResponse> filtrados = itens.stream()
                            .filter(r -> r.fornecedorId != null
                                    && r.fornecedorId.toString().equals(fornecedorId)
                                    && Boolean.TRUE.equals(r.isActive))
                            .toList();

                    renderizarLista(filtrados);
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                Label lblErro = new Label("Erro ao carregar matérias primas: " + state.getErrorMessage());
                lblErro.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    private void renderizarLista(List<MateriaPrimaFornecedorResponse> itens) {
        listaContainer.getChildren().clear();

        if (itens.isEmpty()) {
            Label lblVazio = new Label("Sem matérias primas disponíveis para este fornecedor.");
            lblVazio.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lblVazio);
            return;
        }

        for (MateriaPrimaFornecedorResponse mp : itens) {
            String mfId       = mp.id             != null ? mp.id.toString()       : "";
            String materiaId  = mp.materiaId      != null ? mp.materiaId.toString() : "";
            String nome       = mp.materiaNome    != null ? mp.materiaNome          : "(sem nome)";
            Double preco      = mp.precoUnitario;
            String simbMoeda  = mp.moedaSimbolo   != null ? mp.moedaSimbolo         : "";
            String precoLabel = (preco != null ? String.format("%.2f", preco) : "—") + " " + simbMoeda;

            HBox linha = new HBox();
            linha.getStyleClass().add("selecionar-tipo-row");

            RadioButton radio = new RadioButton();
            radio.getStyleClass().add("selecionar-tipo-radio");
            radio.setSelected(mfId.equals(selectedMateriaFornecedorId));

            Label lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.NEVER);
            spacer.setPrefWidth(8);

            Label lblPreco = new Label(precoLabel);
            lblPreco.getStyleClass().add("selecionar-materia-preco");

            linha.getChildren().addAll(radio, lblNome, spacer, lblPreco);

            Runnable seleccionar = () -> {
                selectedMateriaFornecedorId = mfId;
                selectedMateriaId           = materiaId;
                selectedNome                = nome;
                selectedPrecoUnitario       = preco;
                selectedMoedaSimbolo        = simbMoeda;
                listaContainer.getChildren().forEach(node -> {
                    if (node instanceof HBox hb) {
                        hb.getChildren().stream()
                                .filter(c -> c instanceof RadioButton)
                                .map(c -> (RadioButton) c)
                                .forEach(rb -> rb.setSelected(false));
                    }
                });
                radio.setSelected(true);
                validarEstadoBotao();
            };

            radio.setOnAction(e -> seleccionar.run());
            linha.setOnMouseClicked(e -> seleccionar.run());

            listaContainer.getChildren().add(linha);
        }

        if (selectedMateriaFornecedorId != null && !selectedMateriaFornecedorId.isBlank()) {
            validarEstadoBotao();
        }
    }

    private void validarEstadoBotao() {
        boolean temSeleccao = selectedMateriaId != null && !selectedMateriaId.isBlank();
        String qtdStr = txtQuantidade.getText() != null ? txtQuantidade.getText().trim() : "";
        boolean quantidadeValida = false;
        if (!qtdStr.isEmpty()) {
            try {
                double v = Double.parseDouble(qtdStr);
                quantidadeValida = v > 0;
            } catch (NumberFormatException ignored) {}
        }
        btnAdicionar.setDisable(!(temSeleccao && quantidadeValida));
    }

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
