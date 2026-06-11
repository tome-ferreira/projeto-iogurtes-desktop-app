package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.services.OrdemProducaoService;
import com.gestaoiogurtes.services.LoteProducaoService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import com.gestaoiogurtes.utils.NavigationHelper;
import com.gestaoiogurtes.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardFuncionarioOp implements AppAware {

    private final OrdemProducaoService ordemProducaoService = new OrdemProducaoService();
    private final LoteProducaoService loteProducaoService = new LoteProducaoService();
    private GestaoIogurtes app;

    @FXML private Sidebar sidebarController;
    @FXML private StackPane rootStack;
    @FXML private Label greetingLabel;

    @FXML private VBox cardAguardaAprovacao;
    @FXML private Label countAguardaAprovacao;
    @FXML private ProgressIndicator loadingAguardaAprovacao;

    @FXML private VBox cardEmProducao;
    @FXML private Label countEmProducao;
    @FXML private ProgressIndicator loadingEmProducao;

    @FXML private VBox cardDesperdicio;
    @FXML private Label countDesperdicio;
    @FXML private ProgressIndicator loadingDesperdicio;

    @FXML private VBox lineChartCard;
    @FXML private LineChart<String, Number> lineChartOrdens;
    @FXML private ProgressIndicator loadingLineChart;

    @FXML
    public void initialize() {
        int hour = java.time.LocalTime.now().getHour();
        String saudacao = hour < 12 ? "Bom dia" : hour < 19 ? "Boa tarde" : "Boa noite";

        String nome = SessionManager.getInstance().getUserName();
        String role = SessionManager.getInstance().getUserRole();

        greetingLabel.setText(saudacao + ", " + nome + " — " + role);

        cardAguardaAprovacao.setOnMouseClicked(e -> handleNavigateToOrdens());
        cardAguardaAprovacao.setCursor(Cursor.HAND);

        cardEmProducao.setOnMouseClicked(e -> handleNavigateToOrdens());
        cardEmProducao.setCursor(Cursor.HAND);

        cardDesperdicio.setOnMouseClicked(e -> handleNavigateToStock());
        cardDesperdicio.setCursor(Cursor.HAND);

        lineChartCard.setOnMouseClicked(e -> handleNavigateToOrdens());
        lineChartCard.setCursor(Cursor.HAND);

        loadOrdensProducao();
        loadLotesProducao();
    }

    private void loadOrdensProducao() {
        setOrdensLoading(true);
        loadingLineChart.setVisible(true);
        loadingLineChart.setManaged(true);
        lineChartOrdens.setVisible(false);
        lineChartOrdens.setManaged(false);

        ordemProducaoService.getAll(0, 1000, state -> {
            if (state.isLoading()) return;
            setOrdensLoading(false);
            loadingLineChart.setVisible(false);
            loadingLineChart.setManaged(false);

            if (state.isSuccess()) {
                lineChartOrdens.setVisible(true);
                lineChartOrdens.setManaged(true);

                var response = state.getData();
                if (response != null && response.content != null) {
                    long aguardaAprovacaoCount = 0;
                    long emProducaoCount = 0;

                    for (OrdemProducaoResponse ordem : response.content) {
                        if ("AGUARDA_APROVACAO".equals(ordem.estado)) {
                            aguardaAprovacaoCount++;
                        } else if ("EM_PRODUCAO".equals(ordem.estado)) {
                            emProducaoCount++;
                        }
                    }

                    countAguardaAprovacao.setText(String.valueOf(aguardaAprovacaoCount));
                    countEmProducao.setText(String.valueOf(emProducaoCount));

                    List<OrdemProducaoResponse> ordensConcluidas = response.content.stream()
                            .filter(o -> "CONCLUIDA".equals(o.estado) && o.dataFim != null)
                            .toList();

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

                    for (int i = 4; i >= 0; i--) {
                        LocalDate dia = LocalDate.now().minusDays(i);
                        long count = ordensConcluidas.stream()
                                .filter(o -> o.dataFim.toLocalDate().equals(dia))
                                .count();
                        series.getData().add(new XYChart.Data<>(dia.format(fmt), count));
                    }
                    lineChartOrdens.getData().clear();
                    lineChartOrdens.getData().add(series);

                } else {
                    setOrdensCountsToDash();
                }
            } else if (state.isError()) {
                setOrdensCountsToDash();
                lineChartOrdens.setVisible(false);
                lineChartOrdens.setManaged(false);
                MessageHelper.mostrar(rootStack, "Erro ao carregar ordens de produção: " + state.getErrorMessage(), false);
            }
        });
    }

    private void loadLotesProducao() {
        setLotesLoading(true);

        loteProducaoService.getAll(0, 1000, state -> {
            if (state.isLoading()) return;
            setLotesLoading(false);
            if (state.isSuccess()) {
                var response = state.getData();
                if (response != null && response.content != null) {
                    long desperdicioCount = 0;
                    for (LoteProducaoResponse lote : response.content) {
                        if ("DESPERDICIO".equals(lote.estado)) {
                            desperdicioCount++;
                        }
                    }
                    countDesperdicio.setText(String.valueOf(desperdicioCount));
                } else {
                    countDesperdicio.setText("—");
                }
            } else if (state.isError()) {
                countDesperdicio.setText("—");
                MessageHelper.mostrar(rootStack, "Erro ao carregar lotes de produção: " + state.getErrorMessage(), false);
            }
        });
    }

    private void setOrdensLoading(boolean loading) {
        countAguardaAprovacao.setVisible(!loading);
        countAguardaAprovacao.setManaged(!loading);
        loadingAguardaAprovacao.setVisible(loading);
        loadingAguardaAprovacao.setManaged(loading);

        countEmProducao.setVisible(!loading);
        countEmProducao.setManaged(!loading);
        loadingEmProducao.setVisible(loading);
        loadingEmProducao.setManaged(loading);
    }

    private void setLotesLoading(boolean loading) {
        countDesperdicio.setVisible(!loading);
        countDesperdicio.setManaged(!loading);
        loadingDesperdicio.setVisible(loading);
        loadingDesperdicio.setManaged(loading);
    }

    private void setOrdensCountsToDash() {
        countAguardaAprovacao.setText("—");
        countEmProducao.setText("—");
    }

    @FXML
    private void handleNavigateToOrdens() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/OrdensProducao.fxml");
        }
    }

    @FXML
    private void handleNavigateToStock() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Stock.fxml");
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
