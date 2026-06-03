package com.gestaoiogurtes.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para converter valores de enum da API em texto legível em
 * Português Europeu, para exibição na UI.
 *
 * <h3>Como usar</h3>
 * <pre>{@code
 * String label = EnumDisplayHelper.estadoFisico("LIQUIDO"); // → "Líquido"
 * }</pre>
 */
public final class EnumDisplayHelper {

    private EnumDisplayHelper() {}

    // ── Estado Físico (ProdutoFinal) ─────────────────────────────────────────
    private static final Map<String, String> ESTADO_FISICO = new HashMap<>();

    static {
        ESTADO_FISICO.put("LIQUIDO", "Líquido");
        ESTADO_FISICO.put("SOLIDO",  "Sólido");
    }

    /**
     * Devolve o rótulo em Português para um valor do enum EstadoFisico.
     *
     * @param valor valor da API (ex: "LIQUIDO")
     * @return rótulo legível (ex: "Líquido"), ou o próprio valor se não mapeado
     */
    public static String estadoFisico(String valor) {
        if (valor == null) return "—";
        return ESTADO_FISICO.getOrDefault(valor, valor);
    }

    /**
     * Devolve o valor da API para um rótulo em Português de EstadoFisico.
     * Útil para pré-seleccionar um ComboBox.
     *
     * @param label rótulo (ex: "Líquido")
     * @return valor da API (ex: "LIQUIDO"), ou null se não mapeado
     */
    public static String estadoFisicoParaApi(String label) {
        if (label == null) return null;
        return ESTADO_FISICO.entrySet().stream()
                .filter(e -> e.getValue().equals(label))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Devolve todos os rótulos de EstadoFisico para preencher ComboBox.
     */
    public static String[] estadoFisicoLabels() {
        return new String[]{"Líquido", "Sólido"};
    }

    // ── Estado Encomenda ─────────────────────────────────────────────────────
    private static final Map<String, String> ESTADO_ENCOMENDA = new HashMap<>();

    static {
        ESTADO_ENCOMENDA.put("PENDENTE",  "Pendente");
        ESTADO_ENCOMENDA.put("EXPEDIDA",  "Expedida");
        ESTADO_ENCOMENDA.put("CANCELADA", "Cancelada");
    }

    public static String estadoEncomenda(String valor) {
        if (valor == null) return "—";
        return ESTADO_ENCOMENDA.getOrDefault(valor, valor);
    }

    public static String estadoEncomendaParaApi(String label) {
        if (label == null) return null;
        return ESTADO_ENCOMENDA.entrySet().stream()
                .filter(e -> e.getValue().equals(label))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static String[] estadoEncomendaLabels() {
        return new String[]{"Pendente", "Expedida", "Cancelada"};
    }

    // ── Estado Encomenda MP ──────────────────────────────────────────────────
    private static final Map<String, String> ESTADO_ENCOMENDA_MP = new HashMap<>();

    static {
        ESTADO_ENCOMENDA_MP.put("PENDENTE",     "Pendente");
        ESTADO_ENCOMENDA_MP.put("ENCOMENDADA",  "Encomendada");
        ESTADO_ENCOMENDA_MP.put("RECEBIDA",     "Recebida");
        ESTADO_ENCOMENDA_MP.put("CANCELADA",    "Cancelada");
    }

    /**
     * Devolve o rótulo em Português para um valor do enum EstadoEncomendaMP.
     *
     * @param valor valor da API (ex: "ENCOMENDADA")
     * @return rótulo legível (ex: "Encomendada"), ou o próprio valor se não mapeado
     */
    public static String estadoEncomendaMp(String valor) {
        if (valor == null) return "—";
        return ESTADO_ENCOMENDA_MP.getOrDefault(valor, valor);
    }

    /**
     * Devolve o valor da API para um rótulo em Português de EstadoEncomendaMP.
     */
    public static String estadoEncomendaMpParaApi(String label) {
        if (label == null) return null;
        return ESTADO_ENCOMENDA_MP.entrySet().stream()
                .filter(e -> e.getValue().equals(label))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Devolve todos os rótulos de EstadoEncomendaMP para preencher ComboBox.
     */
    public static String[] estadoEncomendaMpLabels() {
        return new String[]{"Pendente", "Encomendada", "Recebida", "Cancelada"};
    }
}

