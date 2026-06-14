package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class EliminarMateriaPrimaModalController {

    @FXML private Label  lblMensagem;
    @FXML private Label  lblErro;
    @FXML private Button btnEliminar;

    public Stage dialogStage;
    public MateriaPrimaService service;
    public Consumer<String> onSuccess;
    public String idToEliminar;

    public static void show(MateriaPrimaResponse entidade, MateriaPrimaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EliminarMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/EliminarMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Matéria Prima");
            stage.setScene(new Scene(root));

            EliminarMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onSuccess = onSuccess;
            
            ctrl.setEntidade(entidade);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEntidade(MateriaPrimaResponse p) {
        this.idToEliminar = p.id != null ? p.id.toString() : null;
        String nome = p.nome != null ? p.nome : "esta matéria prima";
        lblMensagem.setText("Tem a certeza que deseja eliminar " + nome + "?");
    }

    @FXML
    private void handleEliminar() {
        if (idToEliminar == null) return;

        btnEliminar.setDisable(true);
        btnEliminar.setText("A eliminar...");
        lblErro.setText("");

        service.delete(idToEliminar, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Matéria Prima eliminada com sucesso.");
                }
            } else if (state.isError()) {
                btnEliminar.setDisable(false);
                btnEliminar.setText("Eliminar");
                lblErro.setText("Erro: " + state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
