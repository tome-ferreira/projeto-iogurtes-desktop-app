//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code UserService} do BLL Spring Boot.
 */
public interface IUserService {

    /**
     * Cria e persiste um novo utilizador com os papéis especificados.
     *
     * @param nome         nome completo; obrigatório
     * @param email        email único; obrigatório
     * @param password     password em texto claro; será imediatamente cifrada
     * @param turno        valor em String do enum {@code TurnoTipo} (ex: {@code "MANHA"})
     * @param dataAdmissao data de admissão; pode ser {@code null}
     * @param roles        lista de Strings de {@code UserRoleType} (ex: {@code ["ADMIN", "FUNCIONARIO"]})
     * @param empresaId    UUID da empresa associada; pode ser {@code null} para ADMIN global
     * @return {@link User} persistido
     * @throws IllegalArgumentException se email duplicado, turno inválido, role inválida, etc.
     */
    User createUser(String nome, String email, String password, String turno,
                    LocalDate dataAdmissao, List<String> roles, UUID empresaId);

    /**
     * Actualiza nome, turno e papéis de um utilizador. Os papéis existentes são substituídos.
     *
     * @param id    UUID do utilizador
     * @param nome  novo nome completo
     * @param turno novo turno (String do enum)
     * @param roles nova lista de papéis (substitui a existente)
     * @return {@link User} actualizado
     * @throws IllegalArgumentException se utilizador não encontrado ou validação falhar
     */
    User updateUser(UUID id, String nome, String turno, List<String> roles);

    /**
     * Obtém um utilizador pelo seu identificador único.
     *
     * @param id UUID do utilizador
     * @return {@link User} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Utilizador não encontrado"}
     */
    User getById(UUID id);

    /**
     * Retorna todos os utilizadores activos ({@code isActive = true}).
     *
     * @return lista de utilizadores activos
     */
    List<User> getAll();

    /**
     * Retorna todos os registos de utilizadores, incluindo os eliminados (soft-deleted).
     *
     * @return lista completa de utilizadores
     */
    List<User> getAllIncludingInactive();

    /**
     * Altera a password de um utilizador activo.
     *
     * @param id          UUID do utilizador
     * @param newPassword nova password em texto claro
     * @throws IllegalArgumentException se utilizador inactivo, password igual à actual, ou validação falhar
     */
    void changePassword(UUID id, String newPassword);

    /**
     * Executa soft-delete em cascata: ordens de produção, encomendas, movimentos de stock MP e papéis.
     *
     * @param id UUID do utilizador
     * @throws IllegalArgumentException com mensagem {@code "Utilizador não encontrado"}
     */
    void delete(UUID id);
}
