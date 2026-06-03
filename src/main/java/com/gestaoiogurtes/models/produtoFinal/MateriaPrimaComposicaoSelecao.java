package com.gestaoiogurtes.models.produtoFinal;

/**
 * Resultado do callback do SelecionarMateriaPrimaComposicaoModal.
 * Guarda os dados da matéria prima seleccionada pelo utilizador,
 * incluindo a quantidade por unidade de produto.
 */
public class MateriaPrimaComposicaoSelecao {
    public final String id;
    public final String nome;
    public final double quantidade;

    public MateriaPrimaComposicaoSelecao(String id, String nome, double quantidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
    }
}
