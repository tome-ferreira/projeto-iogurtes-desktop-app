package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;
import com.gestaoiogurtes.services.EncomendaMpService;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import com.gestaoiogurtes.utils.NavigationHelper;
import com.gestaoiogurtes.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.stream.Collectors;

public class DashboardFuncionarioMp implements AppAware {

    private final EncomendaMpService encomendaMpService = new EncomendaMpService();
    private final MateriaPrimaService materiaPrimaService = new MateriaPrimaService();
    private GestaoIogurtes app;

    @FXML private Sidebar sidebarController;
    @FXML private StackPane rootStack;
    @FXML private Label greetingLabel;

    @FXML private VBox cardPendentes;
    @FXML private Label countPendentes;
    @FXML private ProgressIndicator loadingPendentes;

    @FXML private VBox cardEmTransito;
    @FXML private Label countEmTransito;
    @FXML private ProgressIndicator loadingEmTransito;

    @FXML private VBox cardAlertas;
    @FXML private Label countAlertas;
    @FXML private ProgressIndicator loadingAlertas;

    @FXML private VBox pieChartCard;
    @FXML private PieChart pieChartMaterias;
    @FXML private ProgressIndicator loadingPieChart;

    @FXML
    public void initialize() {
        int hour = java.time.LocalTime.now().getHour();
        String saudacao = hour < 12 ? "Bom dia" : hour < 19 ? "Boa tarde" : "Boa noite";

        String nome = SessionManager.getInstance().getUserName();
        String role = SessionManager.getInstance().getUserRole();

        greetingLabel.setText(saudacao + ", " + nome + " — " + role);

        cardPendentes.setOnMouseClicked(e -> handleNavigateToEncomendaMp());
        cardPendentes.setCursor(Cursor.HAND);

        cardEmTransito.setOnMouseClicked(e -> handleNavigateToEncomendaMp());
        cardEmTransito.setCursor(Cursor.HAND);

        cardAlertas.setOnMouseClicked(e -> handleNavigateToMateriasPrimas());
        cardAlertas.setCursor(Cursor.HAND);

        pieChartCard.setOnMouseClicked(e -> handleNavigateToPieChart());
        pieChartCard.setCursor(Cursor.HAND);

        loadEncomendas();
        loadMateriasPrimas();
    }

    private void loadEncomendas() {
        setEncomendasLoading(true);

        encomendaMpService.getAll(0, 1000, state -> {
            if (state.isLoading()) return;
            setEncomendasLoading(false);
            if (state.isSuccess()) {
                var response = state.getData();
                if (response != null && response.content != null) {
                    long cPendentes = 0, cEmTransito = 0;
                    for (EncomendaMpResponse enc : response.content) {
                        if (enc.estado == null) continue;
                        if (enc.estado.equals("PENDENTE")) {
                            cPendentes++;
                        } else if (enc.estado.equals("ENCOMENDADA")) {
                            cEmTransito++;
                        }
                    }
                    countPendentes.setText(String.valueOf(cPendentes));
                    countEmTransito.setText(String.valueOf(cEmTransito));
                } else {
                    setEncomendasCountsToDash();
                }
            } else if (state.isError()) {
                setEncomendasCountsToDash();
                MessageHelper.mostrar(rootStack, "Erro ao carregar encomendas MP: " + state.getErrorMessage(), false);
            }
        });
    }

    private void setEncomendasLoading(boolean loading) {
        countPendentes.setVisible(!loading);
        countPendentes.setManaged(!loading);
        loadingPendentes.setVisible(loading);
        loadingPendentes.setManaged(loading);

        countEmTransito.setVisible(!loading);
        countEmTransito.setManaged(!loading);
        loadingEmTransito.setVisible(loading);
        loadingEmTransito.setManaged(loading);
    }

    private void setEncomendasCountsToDash() {
        countPendentes.setText("—");
        countEmTransito.setText("—");
    }

    private void loadMateriasPrimas() {
        setMateriasLoading(true);
        loadingPieChart.setVisible(true);
        loadingPieChart.setManaged(true);
        pieChartMaterias.setVisible(false);
        pieChartMaterias.setManaged(false);

        materiaPrimaService.getAll(0, 1000, state -> {
            if (state.isLoading()) return;
            setMateriasLoading(false);
            loadingPieChart.setVisible(false);
            loadingPieChart.setManaged(false);

            if (state.isSuccess()) {
                var response = state.getData();
                if (response != null && response.content != null) {
                    long cAlertas = response.content.stream()
                            .filter(mp -> mp.stockAtual != null && mp.stockMinimo != null && mp.stockAtual <= mp.stockMinimo)
                            .count();
                    countAlertas.setText(String.valueOf(cAlertas));

                    pieChartMaterias.setVisible(true);
                    pieChartMaterias.setManaged(true);

                    Map<String, Long> countPorTipo = response.content.stream()
                            .collect(Collectors.groupingBy(
                                    mp -> (mp.tipo != null && mp.tipo.nome != null) ? mp.tipo.nome : "Desconhecido",
                                    Collectors.counting()
                            ));

                    ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                    countPorTipo.forEach((tipo, count) -> {
                        pieData.add(new PieChart.Data(tipo, count));
                    });
                    pieChartMaterias.setData(pieData);
                } else {
                    countAlertas.setText("—");
                }
            } else if (state.isError()) {
                countAlertas.setText("—");
                MessageHelper.mostrar(rootStack, "Erro ao carregar matérias primas: " + state.getErrorMessage(), false);
            }
        });
    }

    private void setMateriasLoading(boolean loading) {
        countAlertas.setVisible(!loading);
        countAlertas.setManaged(!loading);
        loadingAlertas.setVisible(loading);
        loadingAlertas.setManaged(loading);
    }

    @FXML
    private void handleNavigateToEncomendaMp() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/EncomendaMp.fxml");
        }
    }

    @FXML
    private void handleNavigateToMateriasPrimas() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/MateriasPrimas.fxml");
        }
    }

    @FXML
    private void handleNavigateToFornecedores() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Fornecedores.fxml");
        }
    }

    @FXML
    private void handleNavigateToStock() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Stock.fxml");
        }
    }

    @FXML
    private void handleNavigateToPieChart() {
        handleNavigateToMateriasPrimas();
    }

    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }
}
