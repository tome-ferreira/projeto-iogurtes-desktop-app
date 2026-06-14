package com.gestaoiogurtes.components.ordemProducao;

import com.gestaoiogurtes.models.ordemProducao.CreateOrdemProducaoRequest;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoLinhaItem;
import com.gestaoiogurtes.models.ordemProducao.ProdutoOrdemSelecao;
import com.gestaoiogurtes.services.OrdemProducaoService;
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
import javafx.scene.control.ScrollPane;
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

public class CriarOrdemProducaoModalController {

    @FXML private TextArea campoObservacoes;
    @FXML private Button btnAdicionarProduto;
    @FXML private VBox linhasContainer;
    @FXML private Label lblLinhasAviso;
    @FXML private Label lblErro;
    @FXML private ScrollPane scrollErro;
    @FXML private Button btnCriar;

    private Stage dialogStage;
    private OrdemProducaoService service;
    private Consumer<String> onSuccess;

    private final ObservableList<ProdutoOrdemSelecao> linhas = FXCollections.observableArrayList();

    public static void show(
            OrdemProducaoService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CriarOrdemProducaoModalController.class
                            .getResource("/fxml/components/ordemProducao/CriarOrdemProducaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Nova Ordem de Produção");
            stage.setScene(new Scene(root));

            CriarOrdemProducaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onSuccess = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        mostrarErro(null);
        lblLinhasAviso.setVisible(false);
        lblLinhasAviso.setManaged(false);
        renderizarLinhas();
    }

    @FXML
    private void handleAdicionarProduto() {
        SelecionarProdutoOrdemModalController.show(
                service,
                dialogStage,
                selecao -> {
                    javafx.application.Platform.runLater(() -> {
                        boolean duplicado = linhas.stream()
                                .anyMatch(item -> item.id.equals(selecao.id));
                        if (duplicado) {
                            lblLinhasAviso.setText(
                                    "O produto \"" + selecao.nome + "\" já foi adicionado.");
                            lblLinhasAviso.setVisible(true);
                            lblLinhasAviso.setManaged(true);
                            return;
                        }
                        lblLinhasAviso.setVisible(false);
                        lblLinhasAviso.setManaged(false);
                        linhas.add(selecao);
                        renderizarLinhas();
                    });
                }
        );
    }

    @FXML
    private void handleCriar() {
        mostrarErro(null);

        if (linhas.isEmpty()) {
            mostrarErro("Adicione pelo menos um produto.");
            return;
        }

        List<OrdemProducaoLinhaItem> linhasReq = new ArrayList<>();
        for (ProdutoOrdemSelecao sel : linhas) {
            linhasReq.add(new OrdemProducaoLinhaItem(
                    UUID.fromString(sel.id),
                    sel.quantidade));
        }

        CreateOrdemProducaoRequest request = new CreateOrdemProducaoRequest();
        request.userId = SessionManager.getInstance().getUserId();
        request.produtos = linhasReq;

        String obs = campoObservacoes != null && campoObservacoes.getText() != null
                ? campoObservacoes.getText().trim() : "";
        if (!obs.isEmpty()) {
            request.observacoes = obs;
        }

        btnCriar.setDisable(true);
        btnCriar.setText("A criar...");

        service.create(request, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Ordem de produção criada com sucesso.");
                }
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCriar.setText("Criar Ordem");
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
            Label lblVazio = new Label("Nenhum produto adicionado.");
            lblVazio.getStyleClass().add("linhas-vazia-label");
            linhasContainer.getChildren().add(lblVazio);
            return;
        }

        for (int i = 0; i < linhas.size(); i++) {
            ProdutoOrdemSelecao item = linhas.get(i);
            final int idx = i;

            HBox row = new HBox();
            row.getStyleClass().add("linha-row");
            row.setAlignment(Pos.CENTER_LEFT);

            Label lblNome = new Label(item.nome);
            lblNome.getStyleClass().add("linha-row-nome");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Label lblQtd = new Label(String.format("%.3f kg", item.quantidade));
            lblQtd.getStyleClass().add("linha-row-qtd");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.NEVER);
            spacer.setPrefWidth(8);

            Button btnRemover = new Button("Remover");
            btnRemover.getStyleClass().add("btn-remover-linha");
            btnRemover.setOnAction(e -> {
                linhas.remove(idx);
                renderizarLinhas();
            });

            row.getChildren().addAll(lblNome, lblQtd, spacer, btnRemover);
            linhasContainer.getChildren().add(row);
        }
    }

    private void mostrarErro(String msg) {
        boolean hasError = msg != null && !msg.trim().isEmpty();
        lblErro.setText(hasError ? msg : "");
        if (scrollErro != null) {
            scrollErro.setVisible(hasError);
            scrollErro.setManaged(hasError);
        }
    }
}
