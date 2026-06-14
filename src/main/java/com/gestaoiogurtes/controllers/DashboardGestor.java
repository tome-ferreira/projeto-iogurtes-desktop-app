package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.services.EncomendaService;
import com.gestaoiogurtes.services.LoteProducaoService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import com.gestaoiogurtes.utils.NavigationHelper;
import com.gestaoiogurtes.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.OrdemProducaoService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardGestor implements AppAware {

    private final LoteProducaoService loteProducaoService = new LoteProducaoService();
    private final EncomendaService encomendaService = new EncomendaService();
    private final MateriaPrimaService materiaPrimaService = new MateriaPrimaService();
    private final OrdemProducaoService ordemProducaoService = new OrdemProducaoService();
    private GestaoIogurtes app;

    @FXML
    private Sidebar sidebarController;
    @FXML
    private StackPane rootStack;
    @FXML
    private Label greetingLabel;

    @FXML
    private VBox cardGasto;
    @FXML
    private Label countGasto;
    @FXML
    private ProgressIndicator loadingLotes1;

    @FXML
    private VBox cardDisponivel;
    @FXML
    private Label countDisponivel;
    @FXML
    private ProgressIndicator loadingLotes2;

    @FXML
    private VBox cardDesperdicio;
    @FXML
    private Label countDesperdicio;
    @FXML
    private ProgressIndicator loadingLotes3;

    @FXML
    private VBox pieChartCard;
    @FXML
    private PieChart pieChartEncomendas;
    @FXML
    private ProgressIndicator loadingEncomendas;

    @FXML
    private VBox pieChartMateriasCard;
    @FXML
    private PieChart pieChartMaterias;
    @FXML
    private ProgressIndicator loadingPieChartMaterias;

    @FXML
    private VBox lineChartOrdensCard;
    @FXML
    private LineChart<String, Number> lineChartOrdens;
    @FXML
    private ProgressIndicator loadingLineChartOrdens;

    @FXML
    private VBox barChartCard;
    @FXML
    private BarChart<String, Number> barChartProdutos;
    @FXML
    private ProgressIndicator loadingBarChart;

    @FXML
    public void initialize() {
        int hour = java.time.LocalTime.now().getHour();
        String saudacao = hour < 12 ? "Bom dia" : hour < 19 ? "Boa tarde" : "Boa noite";

        String nome = SessionManager.getInstance().getUserName();
        String role = SessionManager.getInstance().getUserRole();

        greetingLabel.setText(saudacao + ", " + nome);

        cardGasto.setOnMouseClicked(e -> handleNavigateToStock());
        cardGasto.setCursor(Cursor.HAND);

        cardDisponivel.setOnMouseClicked(e -> handleNavigateToStock());
        cardDisponivel.setCursor(Cursor.HAND);

        cardDesperdicio.setOnMouseClicked(e -> handleNavigateToStock());
        cardDesperdicio.setCursor(Cursor.HAND);

        pieChartCard.setOnMouseClicked(e -> handleNavigateToEncomendas());
        pieChartCard.setCursor(Cursor.HAND);

        barChartCard.setOnMouseClicked(e -> handleNavigateToEncomendas());
        barChartCard.setCursor(Cursor.HAND);

        pieChartMateriasCard.setOnMouseClicked(e -> handleNavigateToMateriasPrimas());
        pieChartMateriasCard.setCursor(Cursor.HAND);

        lineChartOrdensCard.setOnMouseClicked(e -> handleNavigateToOrdens());
        lineChartOrdensCard.setCursor(Cursor.HAND);

        loadLotes();
        loadPieChartEncomendas();
        loadBarChart();
        loadPieChartMaterias();
        loadLineChartOrdens();
    }

    private void loadLotes() {
        setLotesLoading(true);

        loteProducaoService.getAll(0, 1000, state -> {
            if (state.isLoading())
                return;
            setLotesLoading(false);
            if (state.isSuccess()) {
                var response = state.getData();
                if (response != null && response.content != null) {
                    long cGasto = 0, cDisponivel = 0, cDesperdicio = 0;
                    for (LoteProducaoResponse lote : response.content) {
                        if (lote.estado == null)
                            continue;
                        switch (lote.estado) {
                            case "GASTO" -> cGasto++;
                            case "DISPONIVEL" -> cDisponivel++;
                            case "DESPERDICIO" -> cDesperdicio++;
                        }
                    }
                    countGasto.setText(String.valueOf(cGasto));
                    countDisponivel.setText(String.valueOf(cDisponivel));
                    countDesperdicio.setText(String.valueOf(cDesperdicio));
                } else {
                    setLotesCountsToDash();
                }
            } else if (state.isError()) {
                setLotesCountsToDash();
                MessageHelper.mostrar(rootStack, "Erro ao carregar lotes de produção: " + state.getErrorMessage(),
                        false);
            }
        });
    }

    private void setLotesLoading(boolean loading) {
        countGasto.setVisible(!loading);
        countGasto.setManaged(!loading);
        loadingLotes1.setVisible(loading);
        loadingLotes1.setManaged(loading);

        countDisponivel.setVisible(!loading);
        countDisponivel.setManaged(!loading);
        loadingLotes2.setVisible(loading);
        loadingLotes2.setManaged(loading);

        countDesperdicio.setVisible(!loading);
        countDesperdicio.setManaged(!loading);
        loadingLotes3.setVisible(loading);
        loadingLotes3.setManaged(loading);
    }

    private void setLotesCountsToDash() {
        countGasto.setText("—");
        countDisponivel.setText("—");
        countDesperdicio.setText("—");
    }

    private void loadBarChart() {
        loadingBarChart.setVisible(true);
        loadingBarChart.setManaged(true);
        barChartProdutos.setVisible(false);
        barChartProdutos.setManaged(false);

        encomendaService.getByEstado("EXPEDIDA", 0, 1000, state -> {
            if (state.isLoading())
                return;
            loadingBarChart.setVisible(false);
            loadingBarChart.setManaged(false);

            if (state.isSuccess()) {
                barChartProdutos.setVisible(true);
                barChartProdutos.setManaged(true);

                var response = state.getData();
                if (response != null && response.content != null) {
                    Map<String, Long> produtoCounts = response.content.stream()
                            .filter(e -> e.pallets != null)
                            .flatMap(e -> e.pallets.stream()
                                    .map(p -> p.produtoNome != null ? p.produtoNome : "Desconhecido")
                                    .distinct())
                            .collect(Collectors.groupingBy(
                                    nome -> nome,
                                    Collectors.counting()));

                    Map<String, Long> top5Produtos = produtoCounts.entrySet().stream()
                            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                            .limit(5)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new));

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    top5Produtos.forEach((nome, count) -> series.getData().add(new XYChart.Data<>(nome, count)));

                    barChartProdutos.getData().clear();
                    barChartProdutos.getData().add(series);

                    long maxCount = top5Produtos.values().stream().max(Long::compareTo).orElse(0L);
                    javafx.scene.chart.NumberAxis yAxis = (javafx.scene.chart.NumberAxis) barChartProdutos.getYAxis();
                    yAxis.setAutoRanging(false);
                    yAxis.setLowerBound(0);
                    yAxis.setUpperBound(Math.max(5, maxCount + (maxCount < 10 ? 1 : 2)));
                    yAxis.setTickUnit(1);
                    yAxis.setMinorTickVisible(false);
                    yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
                        @Override
                        public String toString(Number object) {
                            return object.intValue() == object.doubleValue() ? String.valueOf(object.intValue()) : "";
                        }

                        @Override
                        public Number fromString(String string) {
                            return null;
                        }
                    });
                }
            } else if (state.isError()) {
                MessageHelper.mostrar(rootStack, "Erro ao carregar encomendas: " + state.getErrorMessage(), false);
            }
        });
    }

    private void loadPieChartEncomendas() {
        loadingEncomendas.setVisible(true);
        pieChartEncomendas.setVisible(false);

        encomendaService.getAll(0, 1000, state -> {
            if (state.isLoading())
                return;
            loadingEncomendas.setVisible(false);
            if (state.isSuccess()) {
                pieChartEncomendas.setVisible(true);
                ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

                var response = state.getData();
                if (response != null && response.content != null) {
                    Map<String, Long> counts = response.content.stream()
                            .collect(Collectors.groupingBy(
                                    e -> e.estado != null ? e.estado : "DESCONHECIDO",
                                    Collectors.counting()));

                    String[] fixedStates = { "PENDENTE", "EXPEDIDA", "CANCELADA" };
                    for (String estado : fixedStates) {
                        data.add(new PieChart.Data(
                                EnumDisplayHelper.getEstadoEncomendaLabel(estado),
                                counts.getOrDefault(estado, 0L)));
                    }
                }
                pieChartEncomendas.setData(data);
            } else if (state.isError()) {
                MessageHelper.mostrar(rootStack, "Erro ao carregar encomendas: " + state.getErrorMessage(), false);
            }
        });
    }

    private void loadPieChartMaterias() {
        loadingPieChartMaterias.setVisible(true);
        loadingPieChartMaterias.setManaged(true);
        pieChartMaterias.setVisible(false);
        pieChartMaterias.setManaged(false);

        materiaPrimaService.getAll(0, 1000, state -> {
            if (state.isLoading())
                return;
            loadingPieChartMaterias.setVisible(false);
            loadingPieChartMaterias.setManaged(false);

            if (state.isSuccess()) {
                var response = state.getData();
                if (response != null && response.content != null) {
                    pieChartMaterias.setVisible(true);
                    pieChartMaterias.setManaged(true);

                    Map<String, Long> countPorTipo = response.content.stream()
                            .collect(Collectors.groupingBy(
                                    mp -> (mp.tipo != null && mp.tipo.nome != null) ? mp.tipo.nome : "Desconhecido",
                                    Collectors.counting()));

                    ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                    countPorTipo.forEach((tipo, count) -> {
                        pieData.add(new PieChart.Data(tipo, count));
                    });
                    pieChartMaterias.setData(pieData);
                }
            } else if (state.isError()) {
                MessageHelper.mostrar(rootStack, "Erro ao carregar matérias primas: " + state.getErrorMessage(), false);
            }
        });
    }

    private void loadLineChartOrdens() {
        loadingLineChartOrdens.setVisible(true);
        loadingLineChartOrdens.setManaged(true);
        lineChartOrdens.setVisible(false);
        lineChartOrdens.setManaged(false);

        ordemProducaoService.getAll(0, 1000, state -> {
            if (state.isLoading())
                return;
            loadingLineChartOrdens.setVisible(false);
            loadingLineChartOrdens.setManaged(false);

            if (state.isSuccess()) {
                lineChartOrdens.setVisible(true);
                lineChartOrdens.setManaged(true);

                var response = state.getData();
                if (response != null && response.content != null) {
                    java.util.List<com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse> ordensConcluidas = response.content
                            .stream()
                            .filter(o -> "CONCLUIDA".equals(o.estado) && o.dataFim != null)
                            .toList();

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");

                    for (int i = 4; i >= 0; i--) {
                        java.time.LocalDate dia = java.time.LocalDate.now().minusDays(i);
                        long count = ordensConcluidas.stream()
                                .filter(o -> o.dataFim.toLocalDate().equals(dia))
                                .count();
                        series.getData().add(new XYChart.Data<>(dia.format(fmt), count));
                    }
                    lineChartOrdens.getData().clear();
                    lineChartOrdens.getData().add(series);
                }
            } else if (state.isError()) {
                MessageHelper.mostrar(rootStack, "Erro ao carregar ordens de produção: " + state.getErrorMessage(),
                        false);
            }
        });
    }

    @FXML
    private void handleNavigateToOrdens() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/OrdensProducao.fxml");
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

    @FXML
    private void handleNavigateToStock() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Stock.fxml");
        }
    }

    @FXML
    private void handleNavigateToEncomendas() {
        if (app != null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Encomenda.fxml");
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
