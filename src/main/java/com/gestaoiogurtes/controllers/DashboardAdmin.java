package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.services.UtilizadorService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import com.gestaoiogurtes.utils.NavigationHelper;
import com.gestaoiogurtes.utils.SessionManager;
import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.services.EncomendaService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.Cursor;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardAdmin implements AppAware {

    private final UtilizadorService utilizadorService = new UtilizadorService();
    private final EncomendaService encomendaService = new EncomendaService();
    private GestaoIogurtes app;

    @FXML private Sidebar sidebarController;
    @FXML private StackPane rootStack;
    @FXML private Label greetingLabel;

    @FXML private PieChart pieChartEncomendas;
    @FXML private ProgressIndicator loadingEncomendas;
    @FXML private VBox pieChartCard;

    @FXML private Label countAdmin;
    @FXML private ProgressIndicator loadingAdmin;

    @FXML private Label countGestor;
    @FXML private ProgressIndicator loadingGestor;

    @FXML private Label countFuncionarioMp;
    @FXML private ProgressIndicator loadingFuncionarioMp;

    @FXML private Label countFuncionarioOp;
    @FXML private ProgressIndicator loadingFuncionarioOp;

    @FXML private Label countCliente;
    @FXML private ProgressIndicator loadingCliente;

    @FXML
    public void initialize() {
        int hour = java.time.LocalTime.now().getHour();
        String saudacao = hour < 12 ? "Bom dia" : hour < 19 ? "Boa tarde" : "Boa noite";

        String nome = SessionManager.getInstance().getUserName();
        String role = SessionManager.getInstance().getUserRole();

        greetingLabel.setText(saudacao + ", " + nome + " — " + role);

        showLoading(true);

        utilizadorService.getAllActive(0, 1000, state -> {
            switch (state.getStatus()) {
                case SUCCESS -> {
                    showLoading(false);
                    var response = state.getData();
                    if (response != null && response.content != null) {
                        long cAdmin = 0, cGestor = 0, cFuncMp = 0, cFuncOp = 0, cCliente = 0;
                        for (UserResponse user : response.content) {
                            if (user.role == null) continue;
                            switch (user.role) {
                                case "ADMIN" -> cAdmin++;
                                case "GESTOR" -> cGestor++;
                                case "FUNCIONARIO_MP" -> cFuncMp++;
                                case "FUNCIONARIO_OP" -> cFuncOp++;
                                case "CLIENTE" -> cCliente++;
                            }
                        }
                        countAdmin.setText(String.valueOf(cAdmin));
                        countGestor.setText(String.valueOf(cGestor));
                        countFuncionarioMp.setText(String.valueOf(cFuncMp));
                        countFuncionarioOp.setText(String.valueOf(cFuncOp));
                        countCliente.setText(String.valueOf(cCliente));
                    } else {
                        setCountsToDash();
                    }
                }
                case ERROR -> {
                    showLoading(false);
                    setCountsToDash();
                    MessageHelper.mostrar(rootStack, "Erro ao carregar utilizadores ativos: " + state.getErrorMessage(), false);
                }
                default -> {}
            }
        });

        loadingEncomendas.setVisible(true);
        pieChartEncomendas.setVisible(false);

        encomendaService.getAll(0, 1000, state -> {
            if (state.isLoading()) return;
            loadingEncomendas.setVisible(false);
            if (state.isSuccess()) {
                pieChartEncomendas.setVisible(true);
                ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

                var response = state.getData();
                if (response != null && response.content != null) {
                    Map<String, Long> counts = response.content.stream()
                            .collect(Collectors.groupingBy(
                                    e -> e.estado != null ? e.estado : "DESCONHECIDO",
                                    Collectors.counting()
                            ));

                    // Fixed order to guarantee CSS classes (.default-color0, .default-color1, .default-color2)
                    String[] fixedStates = {"PENDENTE", "EXPEDIDA", "CANCELADA"};
                    for (String estado : fixedStates) {
                        data.add(new PieChart.Data(
                                EnumDisplayHelper.getEstadoEncomendaLabel(estado),
                                counts.getOrDefault(estado, 0L)
                        ));
                    }
                }
                pieChartEncomendas.setData(data);
            } else if (state.isError()) {
                MessageHelper.mostrar(rootStack, "Erro ao carregar encomendas: " + state.getErrorMessage(), false);
            }
        });

        pieChartCard.setCursor(Cursor.HAND);
    }

    private void showLoading(boolean loading) {
        countAdmin.setVisible(!loading);
        countAdmin.setManaged(!loading);
        loadingAdmin.setVisible(loading);
        loadingAdmin.setManaged(loading);

        countGestor.setVisible(!loading);
        countGestor.setManaged(!loading);
        loadingGestor.setVisible(loading);
        loadingGestor.setManaged(loading);

        countFuncionarioMp.setVisible(!loading);
        countFuncionarioMp.setManaged(!loading);
        loadingFuncionarioMp.setVisible(loading);
        loadingFuncionarioMp.setManaged(loading);

        countFuncionarioOp.setVisible(!loading);
        countFuncionarioOp.setManaged(!loading);
        loadingFuncionarioOp.setVisible(loading);
        loadingFuncionarioOp.setManaged(loading);

        countCliente.setVisible(!loading);
        countCliente.setManaged(!loading);
        loadingCliente.setVisible(loading);
        loadingCliente.setManaged(loading);
    }

    private void setCountsToDash() {
        countAdmin.setText("—");
        countGestor.setText("—");
        countFuncionarioMp.setText("—");
        countFuncionarioOp.setText("—");
        countCliente.setText("—");
    }

    @FXML
    private void handleNavigateToUtilizadores() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Utilizadores.fxml");
        }
    }

    @FXML
    private void handleNavigateToEncomendas() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Encomenda.fxml");
        }
    }

    @FXML
    private void handleNavigateToProdutosFinais() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/ProdutosFinais.fxml");
        }
    }

    @FXML
    private void handleNavigateToFornecedores() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Fornecedores.fxml");
        }
    }

    @FXML
    private void handleNavigateToMateriasPrimas() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/MateriasPrimas.fxml");
        }
    }

    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }
}
