package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.services.EmpresaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class DetalhesClienteModalController {

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private Label lblEmpresaNome;
    @FXML
    private ProgressIndicator loadingEmpresa;
    @FXML
    private Label lblInativo;
    @FXML
    private HBox hboxInativo;

    public static void show(UserResponse u, boolean inativo, Window owner, EmpresaService empresaService) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesClienteModalController.class
                    .getResource("/fxml/components/utilizadores/DetalhesClienteModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Cliente");
            stage.setScene(new Scene(root));

            DetalhesClienteModalController ctrl = loader.getController();
            ctrl.preencherDados(u, inativo, empresaService);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preencherDados(UserResponse u, boolean inativo, EmpresaService empresaService) {
        txtNome.setText(u.nome != null ? u.nome : "—");
        txtEmail.setText(u.email != null ? u.email : "—");
        txtNome.setEditable(false);
        txtEmail.setEditable(false);

        if (hboxInativo != null) {
            hboxInativo.setVisible(inativo);
            hboxInativo.setManaged(inativo);
        }

        // Resolução do nome da empresa
        if (u.empresaId != null && !u.empresaId.isBlank()) {
            // Mostrar spinner enquanto carrega
            loadingEmpresa.setVisible(true);
            loadingEmpresa.setManaged(true);
            lblEmpresaNome.setText("A carregar...");

            empresaService.getById(u.empresaId, state -> {
                if (state.isLoading()) {
                    // estado de transição — já tratado acima
                } else if (state.isSuccess() && state.getData() != null) {
                    loadingEmpresa.setVisible(false);
                    loadingEmpresa.setManaged(false);
                    String nome = state.getData().nomeEmpresa;
                    lblEmpresaNome.setText(nome != null ? nome : "Sem empresa associada");
                } else {
                    // erro ou body nulo
                    loadingEmpresa.setVisible(false);
                    loadingEmpresa.setManaged(false);
                    lblEmpresaNome.setText("Sem empresa associada");
                }
            });
        } else {
            // Sem empresaId — mostrar imediatamente
            loadingEmpresa.setVisible(false);
            loadingEmpresa.setManaged(false);
            lblEmpresaNome.setText("Sem empresa associada");
        }
    }

    @FXML
    private void handleFechar() {
        ((Stage) txtNome.getScene().getWindow()).close();
    }
}
