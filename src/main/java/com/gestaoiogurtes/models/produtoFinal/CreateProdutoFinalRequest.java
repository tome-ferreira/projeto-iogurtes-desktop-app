package com.gestaoiogurtes.models.produtoFinal;

import java.util.List;

public class CreateProdutoFinalRequest {
    public String nome;
    public String descricao;
    public String abreviacaoSabor;
    public String estadoFisico;
    public Integer validadeDias;
    public Double precoVenda;
    public Double precoPorKg;
    public Double taxaIva;
    public Boolean visivelCliente;
    public Integer quantidadeLote;
    public List<ComposicaoItem> composicao;

    public CreateProdutoFinalRequest() {
    }
}
