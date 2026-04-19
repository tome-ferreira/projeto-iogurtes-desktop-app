package com.gestaoiogurtes.utils;

import com.gestaoiogurtes.bll.model.enums.TurnoTipo;
import com.gestaoiogurtes.bll.model.enums.UserRoleType;

/**
 * Utilitário para conversão de Enums em amigáveis nomes de exibição (Português Europeu).
 */
public final class EnumDisplayHelper {

    private EnumDisplayHelper() {}

    /**
     * Retorna o nome amigável de um tipo de role do utilizador.
     */
    public static String getRoleDisplayName(UserRoleType role) {
        if (role == null) return "Sem Papel";
        return switch (role) {
            case ADMIN -> "Administrador";
            case FUNCIONARIO -> "Funcionário";
            case EMPRESA -> "Empresa Cliente";
        };
    }

    /**
     * Retorna o nome amigável de um turno de trabalho.
     */
    public static String getTurnoDisplayName(TurnoTipo turno) {
        if (turno == null) return "Sem Turno";
        return switch (turno) {
            case MANHA -> "Manhã";
            case TARDE -> "Tarde";
            case NOITE -> "Noite";
        };
    }

}
