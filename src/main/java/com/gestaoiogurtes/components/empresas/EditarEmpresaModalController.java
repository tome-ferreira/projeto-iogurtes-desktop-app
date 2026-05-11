package com.gestaoiogurtes.components.empresas;

import com.gestaoiogurtes.model.EmpresaResponse;
import com.gestaoiogurtes.model.UpdateEmpresaRequest;
import com.gestaoiogurtes.services.interfaces.IEmpresaApiService;
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

public class EditarEmpresaModalController {

    private IEmpresaApiService service;
    private EmpresaResponse empresa;

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    @FXML private TextField txtNipc;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtMorada;
    @FXML private TextField txtCp;
    @FXML private TextField txtCidade;
    @FXML private Label lblErro;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(EmpresaResponse empresa, IEmpresaApiService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarEmpresaModalController.class.getResource("/fxml/components/empresas/EditarEmpresaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Empresa");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarEmpresaModalController controller = loader.getController();
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

    private void setService(IEmpresaApiService service) {
        this.service = service;
    }

    private void setEmpresa(EmpresaResponse empresa) {
        this.empresa = empresa;
        lblTitulo.setText("Editar — " + empresa.nomeEmpresa);
        txtNome.setText(empresa.nomeEmpresa != null ? empresa.nomeEmpresa : "");
        txtNipc.setText(empresa.nipc != null ? empresa.nipc : "");
        txtTelefone.setText(empresa.telefone != null ? empresa.telefone : "");
        txtMorada.setText(empresa.morada != null ? empresa.morada : "");
        txtCp.setText(empresa.codigoPostal != null ? empresa.codigoPostal : "");
        txtCidade.setText(empresa.cidade != null ? empresa.cidade : "");
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
    private void handleGuardar() {
        lblErro.setText("");

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String nipc = txtNipc.getText() == null ? "" : txtNipc.getText().trim();
        String telefone = txtTelefone.getText() == null ? "" : txtTelefone.getText().trim();
        String morada = txtMorada.getText() == null ? "" : txtMorada.getText().trim();
        String cp = txtCp.getText() == null ? "" : txtCp.getText().trim();
        String cidade = txtCidade.getText() == null ? "" : txtCidade.getText().trim();

        if (nome.isEmpty() || nipc.isEmpty() || morada.isEmpty() || cp.isEmpty() || cidade.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        var request = new UpdateEmpresaRequest(
                nome,
                nipc,
                telefone.isEmpty() ? null : telefone,
                morada,
                cp,
                cidade
        );

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.update(empresa.id.toString(), request, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Empresa \"" + request.nomeEmpresa + "\" actualizada com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnCancelar.setDisable(false);
                btnGuardar.setText("Guardar Alterações");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
