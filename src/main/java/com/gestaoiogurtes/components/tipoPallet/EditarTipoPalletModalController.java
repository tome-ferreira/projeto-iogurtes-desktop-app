package com.gestaoiogurtes.components.tipoPallet;

import com.gestaoiogurtes.models.tipoPallet.TipoPalletResponse;
import com.gestaoiogurtes.models.tipoPallet.UpdateTipoPalletRequest;
import com.gestaoiogurtes.services.TipoPalletService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class EditarTipoPalletModalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCapacidade;
    @FXML private Label lblErro;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private TipoPalletResponse tipoPallet;
    private final TipoPalletService service = new TipoPalletService();
    private Consumer<String> onSucesso;
    private Consumer<String> onErro;

    public static void show(TipoPalletResponse tipoPallet, Window owner, Consumer<String> onSucesso, Consumer<String> onErro) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarTipoPalletModalController.class.getResource("/fxml/components/tipoPallet/EditarTipoPalletModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Tipo de Pallet");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarTipoPalletModalController controller = loader.getController();
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
        if (tipoPallet != null) {
            txtNome.setText(tipoPallet.nome != null ? tipoPallet.nome : "");
            txtCapacidade.setText(tipoPallet.capacidadeKg != null ? tipoPallet.capacidadeKg.toString() : "");
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
    private void handleGuardar() {
        lblErro.setText("");
        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String capacidadeStr = txtCapacidade.getText() != null ? txtCapacidade.getText().trim() : "";

        if (nome.isEmpty()) {
            mostrarErro("O nome é obrigatório.");
            return;
        }

        Double capacidade;
        try {
            capacidade = Double.parseDouble(capacidadeStr.replace(",", "."));
            if (capacidade <= 0) {
                mostrarErro("A capacidade deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Capacidade inválida.");
            return;
        }

        UpdateTipoPalletRequest req = new UpdateTipoPalletRequest(nome, capacidade);

        service.update(tipoPallet.id.toString(), req, state -> {
            if (state.isLoading()) {
                btnGuardar.setDisable(true);
                btnGuardar.setText("A guardar...");
                lblErro.setText("");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSucesso != null) onSucesso.accept("Tipo de Pallet atualizado com sucesso.");
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnGuardar.setText("Guardar");
                mostrarErro(state.getErrorMessage());
            }
        });
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
