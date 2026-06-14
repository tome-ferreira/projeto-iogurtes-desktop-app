package com.gestaoiogurtes.components.tipoPallet;

import com.gestaoiogurtes.models.tipoPallet.TipoPalletResponse;
import com.gestaoiogurtes.services.TipoPalletService;
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

public class EliminarTipoPalletModalController {

    @FXML private Label lblMensagem;
    @FXML private Label lblErro;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private TipoPalletResponse tipoPallet;
    private final TipoPalletService service = new TipoPalletService();
    private Consumer<String> onSucesso;
    private Consumer<String> onErro;

    public static void show(TipoPalletResponse tipoPallet, Window owner, Consumer<String> onSucesso, Consumer<String> onErro) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarTipoPalletModalController.class.getResource("/fxml/components/tipoPallet/EliminarTipoPalletModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Tipo de Pallet");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarTipoPalletModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setTipoPallet(tipoPallet);
            controller.setCallbacks(onSucesso, onErro);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setTipoPallet(TipoPalletResponse tipoPallet) {
        this.tipoPallet = tipoPallet;
        if (tipoPallet != null && tipoPallet.nome != null) {
            lblMensagem.setText("Tem a certeza que pretende eliminar o tipo de pallet '" + tipoPallet.nome + "'?");
        }
    }

    private void setCallbacks(Consumer<String> onSucesso, Consumer<String> onErro) {
        this.onSucesso = onSucesso;
        this.onErro = onErro;
    }

    @FXML
    public void initialize() {
        lblErro.setText("");
    }

    @FXML
    private void handleEliminar() {
        lblErro.setText("");
        service.delete(tipoPallet.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setDisable(true);
                btnEliminar.setText("A eliminar...");
                lblErro.setText("");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSucesso != null) onSucesso.accept("Tipo de Pallet eliminado com sucesso.");
            } else if (state.isError()) {
                btnEliminar.setDisable(false);
                btnEliminar.setText("Eliminar");
                lblErro.setText(state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
