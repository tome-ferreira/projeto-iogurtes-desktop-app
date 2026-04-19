//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.User;
import com.gestaoiogurtes.bll.model.UserRole;
import com.gestaoiogurtes.bll.model.enums.TurnoTipo;
import com.gestaoiogurtes.bll.model.enums.UserRoleType;
import com.gestaoiogurtes.services.interfaces.IUserService;

import java.time.LocalDate;
import java.util.*;

/**
 * Implementação mock em memória de {@link IUserService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockUserService implements IUserService {

    private final Map<UUID, User> store;

    public MockUserService(MockEmpresaService empresaService) {
        this.store = MockDataFactory.criarUtilizadores(
                MockDataFactory.criarEmpresas()
        );
    }

    @Override
    public User createUser(String nome, String email, String password, String turno,
                            LocalDate dataAdmissao, List<String> roles, UUID empresaId) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Email inválido");
        boolean emailDuplicado = store.values().stream()
                .anyMatch(u -> u.getEmail().equals(email) && u.isActive());
        if (emailDuplicado)
            throw new IllegalArgumentException("Email já existe: " + email);

        TurnoTipo turnoTipo = TurnoTipo.valueOf(turno);

        User user = new User(null, nome, email, "HASHED_" + password, turnoTipo, dataAdmissao);

        List<UserRole> userRoles = new ArrayList<>();
        for (String roleStr : roles) {
            UserRole ur = new UserRole();
            ur.setId(UUID.randomUUID());
            ur.setRole(UserRoleType.valueOf(roleStr));
            ur.setUser(user);
            userRoles.add(ur);
        }
        user.setRoles(userRoles);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(UUID id, String nome, String turno, List<String> roles) {
        User user = getById(id);
        user.setNome(nome);
        user.setTurno(TurnoTipo.valueOf(turno));

        List<UserRole> newRoles = new ArrayList<>();
        for (String roleStr : roles) {
            UserRole ur = new UserRole();
            ur.setId(UUID.randomUUID());
            ur.setRole(UserRoleType.valueOf(roleStr));
            ur.setUser(user);
            newRoles.add(ur);
        }
        user.getRoles().clear();
        user.getRoles().addAll(newRoles);
        return user;
    }

    @Override
    public User getById(UUID id) {
        User u = store.get(id);
        if (u == null)
            throw new IllegalArgumentException("Utilizador não encontrado");
        return u;
    }

    @Override
    public List<User> getAll() {
        return store.values().stream()
                .filter(User::isActive)
                .toList();
    }

    @Override
    public List<User> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void changePassword(UUID id, String newPassword) {
        User user = getById(id);
        if (!user.isActive())
            throw new IllegalArgumentException("Utilizador inativo");
        if (user.getPasswordHash().equals("HASHED_" + newPassword))
            throw new IllegalArgumentException("A nova password deve ser diferente da atual");
        user.setPasswordHash("HASHED_" + newPassword);
    }

    @Override
    public void delete(UUID id) {
        User user = getById(id);
        user.getRoles().forEach(UserRole::softDelete);
        user.softDelete();
    }
}
