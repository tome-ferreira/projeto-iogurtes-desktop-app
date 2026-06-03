package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.produtoFinal.ProdutoMateriaResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.ProdutoFinalService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;

public class ComposicaoModalController {

    @FXML private Label lblTitulo;
    @FXML private HBox  hboxLoading;
    @FXML private VBox  listaContainer;

    private Stage dialogStage;
    private ProdutoFinalService produtoFinalService;
    private MateriaPrimaService materiaPrimaService;
    private String produtoId;
    private String produtoNome;

    public static void show(
            String produtoId,
            String produtoNome,
            ProdutoFinalService produtoFinalService,
            MateriaPrimaService materiaPrimaService,
            Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ComposicaoModalController.class
                            .getResource("/fxml/components/produtoFinal/ComposicaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Composição - " + (produtoNome != null ? produtoNome : ""));
            stage.setScene(new Scene(root));

            ComposicaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.produtoFinalService = produtoFinalService;
            ctrl.materiaPrimaService = materiaPrimaService;
            ctrl.produtoId = produtoId;
            ctrl.produtoNome = produtoNome;

            ctrl.lblTitulo.setText("Composição: " + (produtoNome != null ? produtoNome : "Produto"));
            ctrl.carregarDados();

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        produtoFinalService.getById(produtoId, state -> {
            if (state.isLoading()) {
                setLoadingVisible(true);
            } else if (state.isSuccess()) {
                setLoadingVisible(false);
                var produto = state.getData();
                if (produto != null && produto.composicao != null) {
                    renderizarLista(produto.composicao);
                } else {
                    renderizarLista(List.of());
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                Label lblErro = new Label("Erro ao carregar composição: " + state.getErrorMessage());
                lblErro.getStyleClass().add("cert-erro-msg");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    private void renderizarLista(List<ProdutoMateriaResponse> composicao) {
        listaContainer.getChildren().clear();

        if (composicao.isEmpty()) {
            VBox vazio = new VBox();
            vazio.getStyleClass().add("cert-vazio");
            vazio.setAlignment(javafx.geometry.Pos.CENTER);
            
            FontIcon icone = new FontIcon("mdi2p-playlist-remove");
            icone.getStyleClass().add("cert-vazio-icone");
            
            Label lbl = new Label("Sem matérias primas na composição.");
            lbl.getStyleClass().add("cert-vazio-texto");
            
            vazio.getChildren().addAll(icone, lbl);
            listaContainer.getChildren().add(vazio);
            return;
        }

        for (int i = 0; i < composicao.size(); i++) {
            ProdutoMateriaResponse item = composicao.get(i);
            
            HBox linha = new HBox();
            linha.getStyleClass().add("cert-linha");
            if (i == composicao.size() - 1) {
                linha.getStyleClass().add("cert-linha-ultima");
            }
            linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Nome
            Label lblNome = new Label(item.materiaNome != null ? item.materiaNome : "(sem nome)");
            lblNome.getStyleClass().add("cert-nome");
            lblNome.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lblNome, Priority.ALWAYS);
            
            // Quantidade
            Label lblQtd = new Label(item.quantidadePorUnidadeProduto + " " + 
                                     (item.materiaUnidade != null ? item.materiaUnidade : "un"));
            lblQtd.getStyleClass().add("cert-validade");
            lblQtd.setMinWidth(120);
            lblQtd.setPrefWidth(120);
            
            // Botão Eliminar
            Button btnRemover = new Button("Remover");
            btnRemover.getStyleClass().add("btn-linha-danger");
            btnRemover.setOnAction(e -> {
                EliminarComposicaoModalController.show(
                    produtoId, 
                    item.id.toString(), 
                    item.materiaNome, 
                    produtoFinalService, 
                    dialogStage, 
                    this::carregarDados
                );
            });

            HBox colAcoes = new HBox(btnRemover);
            colAcoes.setAlignment(javafx.geometry.Pos.CENTER);
            colAcoes.setMinWidth(90);
            colAcoes.setPrefWidth(90);
            
            linha.getChildren().addAll(lblNome, lblQtd, colAcoes);
            listaContainer.getChildren().add(linha);
        }
    }

    @FXML
    private void handleAdicionarNova() {
        AdicionarComposicaoModalController.show(
            produtoId,
            produtoFinalService,
            materiaPrimaService,
            dialogStage,
            this::carregarDados
        );
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
