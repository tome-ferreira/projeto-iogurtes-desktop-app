package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.bll.model.User;
import com.gestaoiogurtes.services.ServiceLocator;
import com.gestaoiogurtes.services.interfaces.IUserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class DesativarUtilizadorModalController {

    private final IUserService userService = ServiceLocator.userService();

    @FXML private Label lblNome;
    @FXML private Label lblErro;

    private Stage dialogStage;
    private Runnable onSuccess;
    private User user;

    public static void show(User user, Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(DesativarUtilizadorModalController.class.getResource("/fxml/components/utilizadores/DesativarUtilizadorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Confirmar Desativação");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            DesativarUtilizadorModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setUser(user);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    private void setUser(User user) {
        this.user = user;
        lblNome.setText(user.getNome() + " (" + user.getEmail() + ")");
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleDesativar() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        try {
            userService.delete(user.getId());
            onSuccess.run();
            dialogStage.close();
        } catch (Exception e) {
            lblErro.setText(e.getMessage());
            lblErro.setVisible(true);
            lblErro.setManaged(true);
        }
    }
}
