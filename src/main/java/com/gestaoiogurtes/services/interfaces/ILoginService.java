//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.User;

/**
 * Interface espelho do {@code LoginService} do BLL Spring Boot.
 * <p>
 * <b>Nota:</b> o {@code LoginService} original não é um {@code @Service} Spring;
 * é instanciado manualmente. Esta interface mantém esse contrato.
 * </p>
 */
public interface ILoginService {

    /**
     * Autentica um utilizador por email e password.
     * <p>
     * Por razões de segurança, a mesma mensagem de erro é lançada para qualquer
     * falha (email inválido, utilizador inactivo, password errada) — conforme § 7.8.
     * </p>
     *
     * @param email    email do utilizador
     * @param password password em texto claro
     * @return {@link User} autenticado e activo
     * @throws IllegalArgumentException com mensagem {@code "Credenciais invalidas"} em qualquer falha
     */
    User execute(String email, String password);
}
