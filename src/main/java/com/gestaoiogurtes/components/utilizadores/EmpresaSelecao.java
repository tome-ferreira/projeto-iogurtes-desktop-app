package com.gestaoiogurtes.components.utilizadores;

/**
 * Payload devolvido pelo {@link SelecionarEmpresaModalController} quando o
 * utilizador confirma uma selecção de empresa.
 *
 * <p>Contém apenas os dois campos necessários para o ecrã chamador:</p>
 * <ul>
 *   <li>{@code id}   — UUID da empresa (String) para enviar à API</li>
 *   <li>{@code nome} — nome legível para apresentar na UI</li>
 * </ul>
 */
public class EmpresaSelecao {

    /** UUID da empresa seleccionada. */
    public final String id;

    /** Nome da empresa seleccionada ({@code nomeEmpresa} da API). */
    public final String nome;

    public EmpresaSelecao(String id, String nome) {
        this.id   = id;
        this.nome = nome;
    }
}
