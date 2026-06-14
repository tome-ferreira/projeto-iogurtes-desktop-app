package com.gestaoiogurtes.components.encomendaMp;

import com.gestaoiogurtes.models.encomendaMp.CreateEncomendaMpRequest;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpLinhaItem;
import com.gestaoiogurtes.models.encomendaMp.FornecedorEncomendaSelecao;
import com.gestaoiogurtes.models.encomendaMp.MateriaPrimaEncomendaSelecao;
import com.gestaoiogurtes.services.EncomendaMpService;
import com.gestaoiogurtes.services.FornecedorService;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CriarEncomendaMpModalController {

    @FXML
    private Label lblFornecedorNome;
    @FXML
    private Button btnSelecionarFornecedor;
    @FXML
    private TextArea campoObservacoes;
    @FXML
    private Tab tabMateriasPrimas;
    @FXML
    private Button btnAdicionarMateria;
    @FXML
    private VBox linhasContainer;
    @FXML
    private Label lblLinhasAviso;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnCriar;
    private Stage dialogStage;
    private EncomendaMpService service;
    private FornecedorService fornecedorService;
    private MateriaPrimaService materiaPrimaService;
    private Consumer<String> onSuccess;

    private String selectedFornecedorId;

    /** ObservableList das linhas de matéria prima adicionadas. */
    private final ObservableList<MateriaPrimaEncomendaSelecao> linhas = FXCollections.observableArrayList();

    public static void show(
            EncomendaMpService service,
            FornecedorService fornecedorService,
            MateriaPrimaService materiaPrimaService,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CriarEncomendaMpModalController.class
                            .getResource("/fxml/components/encomendaMp/CriarEncomendaMpModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Fazer Encomenda de Matéria Prima");
            stage.setScene(new Scene(root));

            CriarEncomendaMpModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.fornecedorService = fornecedorService;
            ctrl.materiaPrimaService = materiaPrimaService;
            ctrl.onSuccess = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");
        lblFornecedorNome.setVisible(false);
        lblFornecedorNome.setManaged(false);
        tabMateriasPrimas.setDisable(true);
        btnAdicionarMateria.setDisable(true);
        lblLinhasAviso.setVisible(false);
        lblLinhasAviso.setManaged(false);
        renderizarLinhas();
    }

    @FXML
    private void handleSelecionarFornecedor() {
        SelecionarFornecedorEncomendaModalController.show(
                fornecedorService,
                dialogStage,
                selecao -> {
                    javafx.application.Platform.runLater(() -> {
                        selectedFornecedorId = selecao.id;
                        lblFornecedorNome.setText(selecao.nome);
                        lblFornecedorNome.setVisible(true);
                        lblFornecedorNome.setManaged(true);
                        btnSelecionarFornecedor.setText("Alterar Fornecedor");

                        // Ao mudar fornecedor, limpar linhas
                        linhas.clear();
                        renderizarLinhas();

                        // Activar Tab 2
                        tabMateriasPrimas.setDisable(false);
                        btnAdicionarMateria.setDisable(false);
                    });
                });
    }

    @FXML
    private void handleAdicionarMateria() {
        if (selectedFornecedorId == null || selectedFornecedorId.isBlank())
            return;

        SelecionarMateriaEncomendaModalController.show(
                materiaPrimaService,
                selectedFornecedorId,
                dialogStage,
                selecao -> {
                    javafx.application.Platform.runLater(() -> {
                        // Verificar duplicado pelo materiaId
                        boolean duplicado = linhas.stream()
                                .anyMatch(item -> item.id.equals(selecao.id));
                        if (duplicado) {
                            lblLinhasAviso.setText(
                                    "A matéria prima \"" + selecao.nome + "\" já foi adicionada.");
                            lblLinhasAviso.setVisible(true);
                            lblLinhasAviso.setManaged(true);
                            return;
                        }
                        lblLinhasAviso.setVisible(false);
                        lblLinhasAviso.setManaged(false);
                        linhas.add(selecao);
                        renderizarLinhas();
                    });
                });
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");

        // Validação
        if (selectedFornecedorId == null || selectedFornecedorId.isBlank()) {
            mostrarErro("Selecione um fornecedor.");
            return;
        }
        if (linhas.isEmpty()) {
            mostrarErro("Adicione pelo menos uma matéria prima.");
            return;
        }

        // Construir linhas do request
        List<EncomendaMpLinhaItem> linhasReq = new ArrayList<>();
        for (MateriaPrimaEncomendaSelecao sel : linhas) {
            linhasReq.add(new EncomendaMpLinhaItem(
                    UUID.fromString(sel.id),
                    sel.quantidade));
        }

        // Construir request
        CreateEncomendaMpRequest request = new CreateEncomendaMpRequest();
        request.userId = SessionManager.getInstance().getUserId();
        request.fornecedorId = UUID.fromString(selectedFornecedorId);
        request.linhas = linhasReq;

        // Campo observações — opcional
        String obs = campoObservacoes != null && campoObservacoes.getText() != null
                ? campoObservacoes.getText().trim()
                : "";
        if (!obs.isEmpty()) {
            request.observacoes = obs;
        }

        btnCriar.setDisable(true);
        btnCriar.setText("A criar...");

        service.create(request, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Encomenda de matéria prima criada com sucesso.");
                }
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCriar.setText("Criar Encomenda");
                mostrarErro(state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void renderizarLinhas() {
        linhasContainer.getChildren().clear();

        if (linhas.isEmpty()) {
            Label lblVazio = new Label("Nenhuma matéria prima adicionada.");
            lblVazio.getStyleClass().add("linhas-vazia-label");
            linhasContainer.getChildren().add(lblVazio);
            return;
        }

        for (int i = 0; i < linhas.size(); i++) {
            MateriaPrimaEncomendaSelecao item = linhas.get(i);
            final int idx = i;

            HBox row = new HBox();
            row.getStyleClass().add("linha-row");
            row.setAlignment(Pos.CENTER_LEFT);

            Label lblNome = new Label(item.nome);
            lblNome.getStyleClass().add("linha-row-nome");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Label lblQtd = new Label(String.format("%.3f", item.quantidade));
            lblQtd.getStyleClass().add("linha-row-qtd");

            Label lblPreco = new Label(
                    (item.precoUnitario != null ? String.format("%.2f", item.precoUnitario) : "—")
                            + " " + (item.moedaSimbolo != null ? item.moedaSimbolo : ""));
            lblPreco.getStyleClass().add("linha-row-preco");

            Region spacer1 = new Region();
            HBox.setHgrow(spacer1, Priority.NEVER);
            spacer1.setPrefWidth(8);

            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.NEVER);
            spacer2.setPrefWidth(8);

            Button btnRemover = new Button("Remover");
            btnRemover.getStyleClass().add("btn-remover-linha");
            btnRemover.setOnAction(e -> {
                linhas.remove(idx);
                renderizarLinhas();
            });

            row.getChildren().addAll(lblNome, lblQtd, spacer1, lblPreco, spacer2, btnRemover);
            linhasContainer.getChildren().add(row);
        }
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg != null ? msg : "Ocorreu um erro desconhecido.");
    }
}
