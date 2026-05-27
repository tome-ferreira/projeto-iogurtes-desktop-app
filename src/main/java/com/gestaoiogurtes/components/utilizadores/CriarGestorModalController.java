package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.CreateGestorRequest;
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

public class CriarGestorModalController {

    private UtilizadorService service;

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpDataAdmissao;
    @FXML private Label     lblErro;
    @FXML private Button    btnCriar;
    @FXML private Button    btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(UtilizadorService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarGestorModalController.class
                    .getResource("/fxml/components/utilizadores/CriarGestorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Criar Gestor");

            stage.setScene(new Scene(root));

            CriarGestorModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.onSuccess   = onSuccess;
            ctrl.service     = service;

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void initialize() {}

    @FXML private void handleCancelar() { dialogStage.close(); }

    @FXML
    private void handleCriar() {
        lblErro.setText("");
        String nome  = txtNome.getText()  == null ? "" : txtNome.getText().trim();
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String pass  = txtPassword.getText() == null ? "" : txtPassword.getText();
        String data  = dpDataAdmissao.getValue() == null ? "" : dpDataAdmissao.getValue().toString();

        if (nome.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }
        if (pass.length() < 8) {
            lblErro.setText("A palavra-passe deve ter pelo menos 8 caracteres.");
            return;
        }

        var req = new CreateGestorRequest(nome, email, pass, data.isEmpty() ? null : data);

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);

        service.createGestor(req, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Gestor \"" + req.nome + "\" criado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Gestor");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
