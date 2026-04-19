//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.User;
import com.gestaoiogurtes.services.interfaces.ILoginService;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementação mock em memória de {@link ILoginService}.
 * <p>
 * Verifica a password usando o esquema simplificado {@code "HASHED_<password>"}
 * empregue em todos os mocks.
 * </p>
 * <p>
 * <b>Credenciais de teste pré-configuradas:</b><br>
 * <ul>
 *   <li>admin@gestao.pt / admin123</li>
 *   <li>ana.silva@gestao.pt / func123</li>
 *   <li>joao.ferreira@gestao.pt / func456</li>
 *   <li>rui.costa@laticiniosnorte.pt / emp123</li>
 *   <li>maria.santos@queijariasul.pt / emp456</li>
 * </ul>
 * </p>
 */
public class MockLoginService implements ILoginService {

    private final Map<String, User> usersByEmail;

    /**
     * Constrói o serviço de login a partir do mock de utilizadores.
     *
     * @param userService serviço de utilizadores mock já instanciado
     */
    public MockLoginService(MockUserService userService) {
        this.usersByEmail = new HashMap<>();
        userService.getAllIncludingInactive()
                .forEach(u -> usersByEmail.put(u.getEmail(), u));
    }

    @Override
    public User execute(String email, String password) {
        if (email == null || !email.contains("@") || password == null || password.isBlank())
            throw new IllegalArgumentException("Credenciais invalidas");

        User user = usersByEmail.get(email);
        if (user == null || !user.isActive())
            throw new IllegalArgumentException("Credenciais invalidas");

        // Hash simplificado para mock: "HASHED_<password>"
        if (!user.getPasswordHash().equals("HASHED_" + password))
            throw new IllegalArgumentException("Credenciais invalidas");

        return user;
    }
}
