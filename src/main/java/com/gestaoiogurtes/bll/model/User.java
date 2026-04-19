//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.TurnoTipo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilizador do sistema (funcionário, administrador ou representante de empresa).
 * <p>
 * <b>ATENÇÃO:</b> nunca expor {@code passwordHash} na UI.
 * </p>
 */
public class User extends BaseEntity {

    private Empresa empresa;
    private String nome;
    private String email;
    /** Hash BCrypt da password. Nunca expor na UI. */
    private String passwordHash;
    private TurnoTipo turno;
    private LocalDate dataAdmissao;
    private List<UserRole> roles = new ArrayList<>();

    public User() {}

    public User(Empresa empresa, String nome, String email,
                String passwordHash, TurnoTipo turno, LocalDate dataAdmissao) {
        this.empresa       = empresa;
        this.nome          = nome;
        this.email         = email;
        this.passwordHash  = passwordHash;
        this.turno         = turno;
        this.dataAdmissao  = dataAdmissao;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public Empresa    getEmpresa()                     { return empresa; }
    public void       setEmpresa(Empresa empresa)      { this.empresa = empresa; }

    public String     getNome()                        { return nome; }
    public void       setNome(String nome)             { this.nome = nome; }

    public String     getEmail()                       { return email; }
    public void       setEmail(String email)           { this.email = email; }

    public String     getPasswordHash()                { return passwordHash; }
    public void       setPasswordHash(String hash)     { this.passwordHash = hash; }

    public TurnoTipo  getTurno()                       { return turno; }
    public void       setTurno(TurnoTipo turno)        { this.turno = turno; }

    public LocalDate  getDataAdmissao()                { return dataAdmissao; }
    public void       setDataAdmissao(LocalDate d)     { this.dataAdmissao = d; }

    public List<UserRole> getRoles()                   { return roles; }
    public void       setRoles(List<UserRole> roles)   { this.roles = roles; }
}
