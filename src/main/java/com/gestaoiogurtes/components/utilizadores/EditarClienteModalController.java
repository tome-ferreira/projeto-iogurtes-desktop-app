package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.UpdateClienteRequest;
import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.services.EmpresaService;
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

public class EditarClienteModalController {

    private UtilizadorService service;
    private EmpresaService    empresaService;
    private UserResponse      utilizador;

    @FXML private TextField txtNome;
    @FXML private Label     lblEmpresaNome;
    @FXML private Button    btnSelecionarEmpresa;
    @FXML private Label     lblErro;
    @FXML private Button    btnGuardar;
    @FXML private Button    btnCancelar;

    private Stage            dialogStage;
    private Consumer<String> onSuccess;

    /** UUID da empresa actualmente seleccionada. */
    private String selectedEmpresaId;

    /* ── Abertura do modal ──────────────────────────────────────────────── */

    public static void show(
            UserResponse utilizador,
            UtilizadorService service,
            EmpresaService empresaService,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarClienteModalController.class
                    .getResource("/fxml/components/utilizadores/EditarClienteModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));

            EditarClienteModalController ctrl = loader.getController();
            ctrl.dialogStage    = stage;
            ctrl.onSuccess      = onSuccess;
            ctrl.service        = service;
            ctrl.empresaService = empresaService;
            ctrl.setUtilizador(utilizador);

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /* ── Pré-preenchimento ──────────────────────────────────────────────── */

    private void setUtilizador(UserResponse u) {
        this.utilizador = u;
        txtNome.setText(u.nome != null ? u.nome : "");

        // Se o cliente já tiver uma empresa, pré-seleccioná-la
        if (u.empresaId != null && !u.empresaId.isBlank()) {
            selectedEmpresaId = u.empresaId;
            // Mostrar o spinner / texto provisório enquanto carrega o nome
            lblEmpresaNome.setText("A carregar...");
            lblEmpresaNome.setVisible(true);
            lblEmpresaNome.setManaged(true);
            btnSelecionarEmpresa.setText("Alterar Empresa");

            // Buscar o nome da empresa via GET /empresas/{id}
            empresaService.getById(u.empresaId, state -> {
                if (state.isSuccess() && state.getData() != null) {
                    String nome = state.getData().nomeEmpresa;
                    lblEmpresaNome.setText(nome != null ? nome : u.empresaId);
                } else if (state.isError()) {
                    // Falha ao buscar — mostrar o id como fallback
                    lblEmpresaNome.setText(u.empresaId);
                }
                // isLoading é tratado com o texto "A carregar..." já mostrado
            });
        }
    }

    /* ── Inicialização ──────────────────────────────────────────────────── */

    @FXML public void initialize() {}

    /* ── Handlers ───────────────────────────────────────────────────────── */

    @FXML private void handleCancelar() { dialogStage.close(); }

    /**
     * Abre o {@link SelecionarEmpresaModalController} com a empresa actual
     * como pré-selecção. Se o utilizador fechar com X, a selecção existente
     * não é alterada.
     */
    @FXML
    private void handleSelecionarEmpresa() {
        SelecionarEmpresaModalController.show(
                empresaService,
                dialogStage,
                selectedEmpresaId,  // pré-selecção da empresa actual
                selecao -> {
                    selectedEmpresaId = selecao.id;
                    lblEmpresaNome.setText(selecao.nome);
                    lblEmpresaNome.setVisible(true);
                    lblEmpresaNome.setManaged(true);
                    btnSelecionarEmpresa.setText("Alterar Empresa");
                }
        );
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();

        if (nome.isEmpty()) {
            lblErro.setText("O nome é obrigatório (*).");
            return;
        }

        var req = new UpdateClienteRequest(
                nome,
                (selectedEmpresaId == null || selectedEmpresaId.isBlank()) ? null : selectedEmpresaId
        );

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);

        service.updateCliente(utilizador.id.toString(), req, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Cliente \"" + req.nome + "\" atualizado com sucesso.").replaceAll("\\R", " ").strip());
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
