package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.CreateClienteRequest;
import com.gestaoiogurtes.services.EmpresaService;
import com.gestaoiogurtes.services.UtilizadorService;
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
import java.util.function.Consumer;

public class CriarClienteModalController {

    private UtilizadorService service;
    private EmpresaService empresaService;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblEmpresaNome;
    @FXML
    private Button btnSelecionarEmpresa;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnCriar;
    @FXML
    private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    private String selectedEmpresaId;

    public static void show(
            UtilizadorService service,
            EmpresaService empresaService,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarClienteModalController.class
                    .getResource("/fxml/components/utilizadores/CriarClienteModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Criar Cliente");
            stage.setScene(new Scene(root));

            CriarClienteModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.onSuccess = onSuccess;
            ctrl.service = service;
            ctrl.empresaService = empresaService;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleSelecionarEmpresa() {
        SelecionarEmpresaModalController.show(
                empresaService,
                dialogStage,
                null, // sem pré-selecção
                selecao -> {
                    selectedEmpresaId = selecao.id;
                    lblEmpresaNome.setText(selecao.nome);
                    lblEmpresaNome.setVisible(true);
                    lblEmpresaNome.setManaged(true);
                    btnSelecionarEmpresa.setText("Alterar Empresa");
                });
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String pass = txtPassword.getText() == null ? "" : txtPassword.getText();

        if (nome.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }
        if (pass.length() < 8) {
            lblErro.setText("A palavra-passe deve ter pelo menos 8 caracteres.");
            return;
        }
        if (selectedEmpresaId == null || selectedEmpresaId.isBlank()) {
            lblErro.setText("Por favor, selecione uma empresa (*).");
            return;
        }

        var req = new CreateClienteRequest(nome, email, pass, selectedEmpresaId);

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);

        service.createCliente(req, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Cliente \"" + req.nome + "\" criado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Cliente");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
