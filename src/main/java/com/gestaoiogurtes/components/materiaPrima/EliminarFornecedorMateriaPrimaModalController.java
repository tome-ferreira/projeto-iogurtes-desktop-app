package com.gestaoiogurtes.components.materiaPrima;

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

/**
 * Modal de confirmação de remoção de um fornecedor de matéria prima.
 * DELETE /materias-primas/fornecedores/{id}
 */
public class EliminarFornecedorMateriaPrimaModalController {

    @FXML private Label  lblMensagem;
    @FXML private Label  lblErro;
    @FXML private Button btnConfirmar;

    private Stage               dialogStage;
    private MateriaPrimaService service;
    private String              fornecedorLinkId;
    private Runnable            onSuccess;

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String fornecedorLinkId, String fornecedorNome,
                            MateriaPrimaService service, Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EliminarFornecedorMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/EliminarFornecedorMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Remover Fornecedor");
            stage.setScene(new Scene(root));

            EliminarFornecedorMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage       = stage;
            ctrl.service           = service;
            ctrl.fornecedorLinkId  = fornecedorLinkId;
            ctrl.onSuccess         = onSuccess;

            String nome = fornecedorNome != null ? fornecedorNome : "este fornecedor";
            ctrl.lblMensagem.setText("Tem a certeza que pretende remover o fornecedor \"" + nome + "\" desta matéria prima?");

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Inicialização ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        lblErro.setText("");
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    // ── Handlers FXML ───────────────────────────────────────────────────────

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleConfirmar() {
        mostrarErro("");
        btnConfirmar.setDisable(true);
        btnConfirmar.setText("A remover...");

        service.deleteFornecedor(fornecedorLinkId, state -> {
            if (state.isLoading()) {
                // já desactivado acima
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnConfirmar.setDisable(false);
                btnConfirmar.setText("Confirmar");
                mostrarErro("Erro: " + state.getErrorMessage());
            }
        });
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
        boolean visivel = msg != null && !msg.isBlank();
        lblErro.setVisible(visivel);
        lblErro.setManaged(visivel);
    }
}
