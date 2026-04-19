package com.gestaoiogurtes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class IogurteVM {
    public UUID id;
    public String codigoSku;
    public String nome;
    public String descricao;
    public Integer validadeDias;
    public BigDecimal precoVenda;
    public BigDecimal precoPorKg;
    public Integer stockAtual;
    public Integer quantidadeLote;
    public Boolean visivelCliente;
    public LocalDateTime criadoEm;

    public IogurteVM(String sku, String nome, String descricao,
            int validade, BigDecimal preco, BigDecimal precoPorKg,
            int stock, int lote, boolean visivel) {
        this.id = UUID.randomUUID();
        this.codigoSku = sku;
        this.nome = nome;
        this.descricao = descricao;
        this.validadeDias = validade;
        this.precoVenda = preco;
        this.precoPorKg = precoPorKg;
        this.stockAtual = stock;
        this.quantidadeLote = lote;
        this.visivelCliente = visivel;
        this.criadoEm = LocalDateTime.now();
    }
}
