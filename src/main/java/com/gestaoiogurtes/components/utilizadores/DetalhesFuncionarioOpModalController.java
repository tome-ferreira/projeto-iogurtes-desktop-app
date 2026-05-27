package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.UserResponse;
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

public class DetalhesFuncionarioOpModalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTurno;
    @FXML private TextField txtDataAdmissao;
    @FXML private Label     lblInativo;
    @FXML private HBox      hboxInativo;

    public static void show(UserResponse u, boolean inativo, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesFuncionarioOpModalController.class
                    .getResource("/fxml/components/utilizadores/DetalhesFuncionarioOpModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Funcionário OP");
            stage.setScene(new Scene(root));

            DetalhesFuncionarioOpModalController ctrl = loader.getController();
            ctrl.preencherDados(u, inativo);

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void preencherDados(UserResponse u, boolean inativo) {
        txtNome.setText(u.nome != null ? u.nome : "—");
        txtEmail.setText(u.email != null ? u.email : "—");
        txtTurno.setText(u.turno != null ? u.turno : "—");
        txtDataAdmissao.setText(u.dataAdmissao != null ? u.dataAdmissao : "—");
        txtNome.setEditable(false);
        txtEmail.setEditable(false);
        txtTurno.setEditable(false);
        txtDataAdmissao.setEditable(false);
        if (hboxInativo != null) {
            hboxInativo.setVisible(inativo);
            hboxInativo.setManaged(inativo);
        }
    }

    @FXML
    private void handleFechar() {
        ((Stage) txtNome.getScene().getWindow()).close();
    }
}
