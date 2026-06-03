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
}
