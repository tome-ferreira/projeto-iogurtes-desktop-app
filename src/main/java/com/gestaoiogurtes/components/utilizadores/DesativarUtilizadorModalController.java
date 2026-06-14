package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.UserResponse;
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

public class DesativarUtilizadorModalController {

    private UtilizadorService service;
    private UserResponse utilizador;

    @FXML
    private Label lblMensagem;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnDesativar;
    @FXML
    private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(UserResponse utilizador, UtilizadorService service, Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(DesativarUtilizadorModalController.class
                    .getResource("/fxml/components/utilizadores/DesativarUtilizadorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Desativar Utilizador");
            stage.setScene(new Scene(root));

            DesativarUtilizadorModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.onSuccess = onSuccess;
            ctrl.service = service;
            ctrl.setUtilizador(utilizador);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUtilizador(UserResponse u) {
        this.utilizador = u;
        lblMensagem.setText(
                "Tem a certeza que deseja desativar o utilizador \"" + u.nome + "\"?\n" +
                        "O utilizador ficará inativo e não poderá aceder ao sistema.");
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleDesativar() {
        btnDesativar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.deactivate(utilizador.id.toString(), state -> {
            if (state.isLoading()) {
                btnDesativar.setText("A desativar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Utilizador \"" + utilizador.nome + "\" desativado com sucesso.")
                        .replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnDesativar.setDisable(false);
                btnCancelar.setDisable(false);
                btnDesativar.setText("Desativar");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
