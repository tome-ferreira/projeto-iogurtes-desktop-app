package com.gestaoiogurtes.components.empresas;

import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.services.EmpresaService;
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

public class EliminarEmpresaModalController {

    private EmpresaService service;
    private EmpresaResponse empresa;

    @FXML private Label lblAviso;
    @FXML private Label lblErro;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(EmpresaResponse empresa, EmpresaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarEmpresaModalController.class.getResource("/fxml/components/empresas/EliminarEmpresaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Confirmar Eliminação");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarEmpresaModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);
            controller.setEmpresa(empresa);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setOnSuccess(Consumer<String> onSuccess) {
        this.onSuccess = onSuccess;
    }

    private void setService(EmpresaService service) {
        this.service = service;
    }

    private void setEmpresa(EmpresaResponse empresa) {
        this.empresa = empresa;
        lblAviso.setText("Esta acção irá eliminar permanentemente a empresa\n\"" +
                empresa.nomeEmpresa + "\" e todos os dados associados.\n\n" +
                "Esta operação não pode ser revertida.");
    }

    @FXML
    public void initialize() {
        // Any specific initialization here
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleEliminar() {
        btnEliminar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        service.delete(empresa.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setText("A eliminar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Empresa \"" + empresa.nomeEmpresa + "\" eliminada com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnEliminar.setDisable(false);
                btnCancelar.setDisable(false);
                btnEliminar.setText("Eliminar");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
