package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.fornecedor.CreateFornecedorRequest;
import com.gestaoiogurtes.services.FornecedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CriarFornecedorModalController {

    private FornecedorService service;

    @FXML private TextField txtNome;
    @FXML private TextField txtNif;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtMorada;
    @FXML private TextField txtCidade;
    @FXML private Label lblTipoNome;
    @FXML private Button btnSelecionarTipo;
    @FXML private Label lblErro;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    private String selectedTipoId;

    public static void show(
            FornecedorService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarFornecedorModalController.class
                    .getResource("/fxml/components/fornecedor/CriarFornecedorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Criar Fornecedor");
            stage.setScene(new Scene(root));

            CriarFornecedorModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.onSuccess = onSuccess;
            ctrl.service = service;

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleSelecionarTipo() {
        SelecionarFornecedorTipoModalController.show(
                service,
                dialogStage,
                null,
                selecao -> {
                    selectedTipoId = selecao.id;
                    lblTipoNome.setText(selecao.nome);
                    lblTipoNome.setVisible(true);
                    lblTipoNome.setManaged(true);
                    btnSelecionarTipo.setText("Alterar Tipo");
                }
        );
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String nif = txtNif.getText() == null ? "" : txtNif.getText().trim();
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String telefone = txtTelefone.getText() == null ? "" : txtTelefone.getText().trim();
        String morada = txtMorada.getText() == null ? "" : txtMorada.getText().trim();
        String cidade = txtCidade.getText() == null ? "" : txtCidade.getText().trim();

        if (nome.isEmpty() || nif.isEmpty() || telefone.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        if (selectedTipoId == null || selectedTipoId.isBlank()) {
            lblErro.setText("Por favor, selecione um tipo de fornecedor (*).");
            return;
        }

        var req = new CreateFornecedorRequest(nome, nif, email, telefone, morada, cidade, selectedTipoId, new ArrayList<>());

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);

        service.create(req, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Fornecedor \"" + req.nome + "\" criado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Fornecedor");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
