package com.gestaoiogurtes.models.materiaPrima;

/**
 * POJO de retorno do selector de fornecedor para matéria prima.
 * Transporta apenas id e nome — suficiente para pré-preencher o formulário.
 */
public class FornecedorMateriaPrimaSelecao {
    public final String id;
    public final String nome;

    public FornecedorMateriaPrimaSelecao(String id, String nome) {
        this.id   = id;
        this.nome = nome;
    }
}
