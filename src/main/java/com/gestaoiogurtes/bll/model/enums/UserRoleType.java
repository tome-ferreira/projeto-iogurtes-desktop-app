//v1.0
package com.gestaoiogurtes.bll.model.enums;

/**
 * Papel (role) de um utilizador no sistema.
 */
public enum UserRoleType {
    /** Administrador do sistema; acesso total. */
    ADMIN,
    /** Funcionário de fábrica; acesso operacional. */
    FUNCIONARIO,
    /** Representante da empresa cliente; acesso ao catálogo e encomendas. */
    EMPRESA
}
