//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.*;
import com.gestaoiogurtes.bll.model.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Fábrica central de dados de teste em memória.
 * <p>
 * Todas as implementações mock obtêm os seus dados pré-populados a partir desta classe.
 * Para adicionar ou editar registos de teste, basta alterar este ficheiro.
 * </p>
 * <p>
 * Dados em Português Europeu com entidades realistas do sector de laticínios.
 * </p>
 */
public final class MockDataFactory {

    // ── UUIDs fixos para referências cruzadas entre entidades ──────

    public static final UUID ID_EMPRESA_1   = UUID.fromString("11111111-0000-0000-0000-000000000001");
    public static final UUID ID_EMPRESA_2   = UUID.fromString("11111111-0000-0000-0000-000000000002");
    public static final UUID ID_EMPRESA_3   = UUID.fromString("11111111-0000-0000-0000-000000000003");
    public static final UUID ID_EMPRESA_4   = UUID.fromString("11111111-0000-0000-0000-000000000004");
    public static final UUID ID_EMPRESA_5   = UUID.fromString("11111111-0000-0000-0000-000000000005");

    public static final UUID ID_USER_ADMIN  = UUID.fromString("22222222-0000-0000-0000-000000000001");
    public static final UUID ID_USER_FUNC1  = UUID.fromString("22222222-0000-0000-0000-000000000002");
    public static final UUID ID_USER_FUNC2  = UUID.fromString("22222222-0000-0000-0000-000000000003");
    public static final UUID ID_USER_EMP1   = UUID.fromString("22222222-0000-0000-0000-000000000004");
    public static final UUID ID_USER_EMP2   = UUID.fromString("22222222-0000-0000-0000-000000000005");

    public static final UUID ID_FORN_1      = UUID.fromString("33333333-0000-0000-0000-000000000001");
    public static final UUID ID_FORN_2      = UUID.fromString("33333333-0000-0000-0000-000000000002");
    public static final UUID ID_FORN_3      = UUID.fromString("33333333-0000-0000-0000-000000000003");
    public static final UUID ID_FORN_4      = UUID.fromString("33333333-0000-0000-0000-000000000004");
    public static final UUID ID_FORN_5      = UUID.fromString("33333333-0000-0000-0000-000000000005");

    public static final UUID ID_MP_LEITE    = UUID.fromString("44444444-0000-0000-0000-000000000001");
    public static final UUID ID_MP_ACUCAR   = UUID.fromString("44444444-0000-0000-0000-000000000002");
    public static final UUID ID_MP_MORANGO  = UUID.fromString("44444444-0000-0000-0000-000000000003");
    public static final UUID ID_MP_NATA     = UUID.fromString("44444444-0000-0000-0000-000000000004");
    public static final UUID ID_MP_BAUNILHA = UUID.fromString("44444444-0000-0000-0000-000000000005");

    public static final UUID ID_PROD_NAT    = UUID.fromString("55555555-0000-0000-0000-000000000001");
    public static final UUID ID_PROD_MOR    = UUID.fromString("55555555-0000-0000-0000-000000000002");
    public static final UUID ID_PROD_GREGO  = UUID.fromString("55555555-0000-0000-0000-000000000003");
    public static final UUID ID_PROD_BAU    = UUID.fromString("55555555-0000-0000-0000-000000000004");
    public static final UUID ID_PROD_LIQUIDO= UUID.fromString("55555555-0000-0000-0000-000000000005");

    public static final UUID ID_PALLET_EURO = UUID.fromString("66666666-0000-0000-0000-000000000001");
    public static final UUID ID_PALLET_MEIO = UUID.fromString("66666666-0000-0000-0000-000000000002");
    public static final UUID ID_PALLET_QUART= UUID.fromString("66666666-0000-0000-0000-000000000003");

    // ── Empresas ───────────────────────────────────────────────────

    /**
     * Retorna um mapa ordenado de empresas pré-populadas.
     *
     * @return mapa de {@code UUID → Empresa}
     */
    public static Map<UUID, Empresa> criarEmpresas() {
        Map<UUID, Empresa> store = new LinkedHashMap<>();

        Empresa e1 = new Empresa("Laticínios do Norte SA", "501234567",
                "+351220000001", "Rua do Leite, 1", "4000-001", "Porto");
        e1.setId(ID_EMPRESA_1);
        store.put(e1.getId(), e1);

        Empresa e2 = new Empresa("Queijaria do Sul Lda", "502345678",
                "+351289000003", "Estrada do Queijo, 7", "8000-001", "Faro");
        e2.setId(ID_EMPRESA_2);
        store.put(e2.getId(), e2);

        Empresa e3 = new Empresa("Produtos Lácteos do Alentejo Lda", "503456789",
                "+351266000022", "Herdade do Leite, s/n", "7000-001", "Évora");
        e3.setId(ID_EMPRESA_3);
        store.put(e3.getId(), e3);

        Empresa e4 = new Empresa("Iogurteria Lisboa SA", "504567890",
                "+351213000050", "Avenida da República, 42", "1050-191", "Lisboa");
        e4.setId(ID_EMPRESA_4);
        store.put(e4.getId(), e4);

        Empresa e5 = new Empresa("Cooperativa dos Açores Agrícola", "505678901",
                "+351295000100", "Rua das Furnas, 3", "9500-001", "Ponta Delgada");
        e5.setId(ID_EMPRESA_5);
        store.put(e5.getId(), e5);

        return store;
    }

    // ── Fornecedores ───────────────────────────────────────────────

    /**
     * Retorna um mapa ordenado de fornecedores pré-populados com certificações.
     *
     * @return mapa de {@code UUID → Fornecedor}
     */
    public static Map<UUID, Fornecedor> criarFornecedores() {
        Map<UUID, Fornecedor> store = new LinkedHashMap<>();

        // Fornecedor 1 — Leite gordo
        FornecedorCertificacao cert1a = new FornecedorCertificacao();
        cert1a.setId(UUID.randomUUID());
        cert1a.setTipo(TipoCertificacao.HACCP);
        cert1a.setDescricao("HACCP Certificado 2025");
        cert1a.setValidade(LocalDate.of(2025, 12, 31));

        FornecedorCertificacao cert1b = new FornecedorCertificacao();
        cert1b.setId(UUID.randomUUID());
        cert1b.setTipo(TipoCertificacao.ISO);
        cert1b.setDescricao("ISO 9001:2015");
        cert1b.setValidade(LocalDate.of(2026, 6, 30));

        Fornecedor f1 = new Fornecedor("Leite do Campo SA", "507654321",
                "geral@leitecampo.pt", "+351253000111", "Rua do Pasto, 10, Braga");
        f1.setId(ID_FORN_1);
        cert1a.setFornecedor(f1); cert1b.setFornecedor(f1);
        f1.setCertificacoes(new ArrayList<>(List.of(cert1a, cert1b)));
        store.put(f1.getId(), f1);

        // Fornecedor 2 — Açúcar
        FornecedorCertificacao cert2 = new FornecedorCertificacao();
        cert2.setId(UUID.randomUUID());
        cert2.setTipo(TipoCertificacao.BIO);
        cert2.setDescricao("Certificação Biológica PT-BIO-03");
        cert2.setValidade(LocalDate.of(2026, 3, 31));

        Fornecedor f2 = new Fornecedor("Açucareira Nacional Lda", "508765432",
                "comercial@acucareira.pt", "+351239500200", "Zona Industrial de Coimbra, Lote 5 - 3030-101 Coimbra");
        f2.setId(ID_FORN_2);
        cert2.setFornecedor(f2);
        f2.setCertificacoes(new ArrayList<>(List.of(cert2)));
        store.put(f2.getId(), f2);

        // Fornecedor 3 — Frutos/polpas
        FornecedorCertificacao cert3 = new FornecedorCertificacao();
        cert3.setId(UUID.randomUUID());
        cert3.setTipo(TipoCertificacao.HACCP);
        cert3.setDescricao("HACCP Frutas Frescas");
        cert3.setValidade(LocalDate.of(2025, 9, 30));

        Fornecedor f3 = new Fornecedor("Frutos do Vale Lda", "509876543",
                "encomendas@frutosvale.pt", "+351269000300", "Estrada Nacional 2, Km 45 - 7800-001 Beja");
        f3.setId(ID_FORN_3);
        cert3.setFornecedor(f3);
        f3.setCertificacoes(new ArrayList<>(List.of(cert3)));
        store.put(f3.getId(), f3);

        // Fornecedor 4 — Natas e derivados
        Fornecedor f4 = new Fornecedor("Natas da Serra SA", "510987654",
                null, "+351275300400", "Serra da Estrela, Edifício Industrial B - 6260-001 Manteigas");
        f4.setId(ID_FORN_4);
        f4.setCertificacoes(new ArrayList<>());
        store.put(f4.getId(), f4);

        // Fornecedor 5 — Aromas e extratos
        FornecedorCertificacao cert5 = new FornecedorCertificacao();
        cert5.setId(UUID.randomUUID());
        cert5.setTipo(TipoCertificacao.OUTRA);
        cert5.setDescricao("Certificado de Qualidade Alimentar APED");
        cert5.setValidade(LocalDate.of(2025, 12, 15));

        Fornecedor f5 = new Fornecedor("Aromas Ibéricos SA", "511098765",
                "info@aromasibericos.pt", "+351214500500", "Parque Empresarial de Sintra, Rua A - 2710-001 Sintra");
        f5.setId(ID_FORN_5);
        cert5.setFornecedor(f5);
        f5.setCertificacoes(new ArrayList<>(List.of(cert5)));
        store.put(f5.getId(), f5);

        return store;
    }

    // ── Matérias-primas ────────────────────────────────────────────

    /**
     * Retorna um mapa de matérias-primas pré-populadas.
     * Requer o mapa de fornecedores para associar referências.
     *
     * @param fornecedores mapa de fornecedores já criados
     * @return mapa de {@code UUID → MateriaPrima}
     */
    public static Map<UUID, MateriaPrima> criarMateriasPrimas(Map<UUID, Fornecedor> fornecedores) {
        Map<UUID, MateriaPrima> store = new LinkedHashMap<>();
        Fornecedor fLeite    = fornecedores.get(ID_FORN_1);
        Fornecedor fAcucar   = fornecedores.get(ID_FORN_2);
        Fornecedor fFrutas   = fornecedores.get(ID_FORN_3);
        Fornecedor fNatas    = fornecedores.get(ID_FORN_4);
        Fornecedor fAromas   = fornecedores.get(ID_FORN_5);

        MateriaPrima leite = new MateriaPrima("Leite gordo pasteurizado", TipoMateriaPrima.BASES,
                "L", new BigDecimal("8500.000"), new BigDecimal("500.000"), new BigDecimal("0.85"), fLeite);
        leite.setId(ID_MP_LEITE);
        store.put(leite.getId(), leite);

        MateriaPrima acucar = new MateriaPrima("Açúcar refinado", TipoMateriaPrima.ADOCANTES,
                "kg", new BigDecimal("3200.000"), new BigDecimal("200.000"), new BigDecimal("0.90"), fAcucar);
        acucar.setId(ID_MP_ACUCAR);
        store.put(acucar.getId(), acucar);

        MateriaPrima morango = new MateriaPrima("Polpa de morango", TipoMateriaPrima.SABOR,
                "kg", new BigDecimal("1200.000"), new BigDecimal("100.000"), new BigDecimal("2.30"), fFrutas);
        morango.setId(ID_MP_MORANGO);
        store.put(morango.getId(), morango);

        MateriaPrima nata = new MateriaPrima("Nata UHT pasteurizada", TipoMateriaPrima.BASES,
                "L", new BigDecimal("2000.000"), new BigDecimal("150.000"), new BigDecimal("1.20"), fNatas);
        nata.setId(ID_MP_NATA);
        store.put(nata.getId(), nata);

        MateriaPrima baunilha = new MateriaPrima("Extrato de baunilha bourbon", TipoMateriaPrima.SABOR,
                "L", new BigDecimal("80.000"), new BigDecimal("10.000"), new BigDecimal("45.00"), fAromas);
        baunilha.setId(ID_MP_BAUNILHA);
        store.put(baunilha.getId(), baunilha);

        return store;
    }

    // ── Utilizadores ───────────────────────────────────────────────

    /**
     * Retorna um mapa de utilizadores pré-populados.
     * Requer o mapa de empresas para associar referências.
     *
     * @param empresas mapa de empresas já criadas
     * @return mapa de {@code UUID → User}
     */
    public static Map<UUID, User> criarUtilizadores(Map<UUID, Empresa> empresas) {
        Map<UUID, User> store = new LinkedHashMap<>();

        // Admin global (sem empresa)
        User admin = new User(null, "Administrador Sistema", "admin@gestao.pt",
                "HASHED_admin123", TurnoTipo.MANHA, LocalDate.of(2023, 1, 10));
        admin.setId(ID_USER_ADMIN);
        admin.setRoles(criarRoles(admin, List.of(UserRoleType.ADMIN)));
        store.put(admin.getId(), admin);

        // Funcionário turno manhã
        User func1 = new User(null, "Ana Silva", "ana.silva@gestao.pt",
                "HASHED_func123", TurnoTipo.MANHA, LocalDate.of(2024, 3, 1));
        func1.setId(ID_USER_FUNC1);
        func1.setRoles(criarRoles(func1, List.of(UserRoleType.FUNCIONARIO)));
        store.put(func1.getId(), func1);

        // Funcionário turno tarde
        User func2 = new User(null, "João Ferreira", "joao.ferreira@gestao.pt",
                "HASHED_func456", TurnoTipo.TARDE, LocalDate.of(2024, 6, 15));
        func2.setId(ID_USER_FUNC2);
        func2.setRoles(criarRoles(func2, List.of(UserRoleType.FUNCIONARIO)));
        store.put(func2.getId(), func2);

        // Representante de empresa 1
        Empresa emp1 = empresas.get(ID_EMPRESA_1);
        User emp1User = new User(emp1, "Rui Costa", "rui.costa@laticiniosnorte.pt",
                "HASHED_emp123", TurnoTipo.MANHA, LocalDate.of(2023, 9, 5));
        emp1User.setId(ID_USER_EMP1);
        emp1User.setRoles(criarRoles(emp1User, List.of(UserRoleType.EMPRESA)));
        store.put(emp1User.getId(), emp1User);

        // Representante de empresa 2
        Empresa emp2 = empresas.get(ID_EMPRESA_2);
        User emp2User = new User(emp2, "Maria Santos", "maria.santos@queijariasul.pt",
                "HASHED_emp456", TurnoTipo.MANHA, LocalDate.of(2024, 1, 20));
        emp2User.setId(ID_USER_EMP2);
        emp2User.setRoles(criarRoles(emp2User, List.of(UserRoleType.EMPRESA)));
        store.put(emp2User.getId(), emp2User);

        return store;
    }

    // ── Produtos Finais ────────────────────────────────────────────

    /**
     * Retorna um mapa de produtos finais pré-populados com ingredientes.
     * Requer o mapa de matérias-primas para associar referências.
     *
     * @param materias mapa de matérias-primas já criadas
     * @return mapa de {@code UUID → ProdutoFinal}
     */
    public static Map<UUID, ProdutoFinal> criarProdutos(Map<UUID, MateriaPrima> materias) {
        Map<UUID, ProdutoFinal> store = new LinkedHashMap<>();

        MateriaPrima leite    = materias.get(ID_MP_LEITE);
        MateriaPrima acucar   = materias.get(ID_MP_ACUCAR);
        MateriaPrima morango  = materias.get(ID_MP_MORANGO);
        MateriaPrima nata     = materias.get(ID_MP_NATA);
        MateriaPrima baunilha = materias.get(ID_MP_BAUNILHA);

        // Iogurte Natural
        ProdutoFinal nat = new ProdutoFinal("IGT-NAT-125", "Iogurte Natural 125g",
                "Iogurte natural sem adição de açúcar, 125g", 21,
                new BigDecimal("0.65"), new BigDecimal("5.20"), 1000);
        nat.setId(ID_PROD_NAT);
        nat.setStockAtual(3500);
        nat.setVisivelCliente(true);
        nat.setMaterias(new ArrayList<>(List.of(
                criarPM(nat, leite, "0.110"),
                criarPM(nat, nata,  "0.015")
        )));
        store.put(nat.getId(), nat);

        // Iogurte Morango
        ProdutoFinal mor = new ProdutoFinal("IGT-MOR-125", "Iogurte Morango 125g",
                "Iogurte com polpa de morango, 125g", 18,
                new BigDecimal("0.79"), new BigDecimal("6.32"), 1000);
        mor.setId(ID_PROD_MOR);
        mor.setStockAtual(2100);
        mor.setVisivelCliente(true);
        mor.setMaterias(new ArrayList<>(List.of(
                criarPM(mor, leite,   "0.090"),
                criarPM(mor, acucar,  "0.012"),
                criarPM(mor, morango, "0.025")
        )));
        store.put(mor.getId(), mor);

        // Iogurte Grego
        ProdutoFinal grego = new ProdutoFinal("IGT-GRE-170", "Iogurte Grego Proteico 170g",
                "Iogurte grego proteico com baixo teor de gordura, 170g", 28,
                new BigDecimal("1.29"), new BigDecimal("7.59"), 500);
        grego.setId(ID_PROD_GREGO);
        grego.setStockAtual(850);
        grego.setVisivelCliente(true);
        grego.setMaterias(new ArrayList<>(List.of(
                criarPM(grego, leite, "0.150"),
                criarPM(grego, nata,  "0.020")
        )));
        store.put(grego.getId(), grego);

        // Iogurte Baunilha
        ProdutoFinal bau = new ProdutoFinal("IGT-BAU-125", "Iogurte Baunilha 125g",
                "Iogurte com extrato de baunilha bourbon, 125g", 21,
                new BigDecimal("0.89"), new BigDecimal("7.12"), 1000);
        bau.setId(ID_PROD_BAU);
        bau.setStockAtual(560);
        bau.setVisivelCliente(true);
        bau.setMaterias(new ArrayList<>(List.of(
                criarPM(bau, leite,    "0.100"),
                criarPM(bau, acucar,   "0.015"),
                criarPM(bau, baunilha, "0.002")
        )));
        store.put(bau.getId(), bau);

        // Iogurte Líquido
        ProdutoFinal liq = new ProdutoFinal("IGT-LIQ-200", "Iogurte Líquido Natural 200ml",
                "Iogurte líquido para beber, natural, 200ml", 14,
                new BigDecimal("0.59"), new BigDecimal("2.95"), 2000);
        liq.setId(ID_PROD_LIQUIDO);
        liq.setStockAtual(4200);
        liq.setVisivelCliente(true);
        liq.setMaterias(new ArrayList<>(List.of(
                criarPM(liq, leite,  "0.195"),
                criarPM(liq, acucar, "0.008")
        )));
        store.put(liq.getId(), liq);

        return store;
    }

    // ── Tipos de pallet ────────────────────────────────────────────

    /**
     * Retorna um mapa de tipos de pallet pré-populados.
     *
     * @return mapa de {@code UUID → PalletTipo}
     */
    public static Map<UUID, PalletTipo> criarPalletTipos() {
        Map<UUID, PalletTipo> store = new LinkedHashMap<>();

        PalletTipo euro = new PalletTipo("Euro Pallet 1200kg", new BigDecimal("1200.000"));
        euro.setId(ID_PALLET_EURO);
        store.put(euro.getId(), euro);

        PalletTipo meio = new PalletTipo("Meio Pallet 600kg", new BigDecimal("600.000"));
        meio.setId(ID_PALLET_MEIO);
        store.put(meio.getId(), meio);

        PalletTipo quart = new PalletTipo("Quarto Pallet 300kg", new BigDecimal("300.000"));
        quart.setId(ID_PALLET_QUART);
        store.put(quart.getId(), quart);

        return store;
    }

    // ── Helpers internos ───────────────────────────────────────────

    private static List<UserRole> criarRoles(User user, List<UserRoleType> tipos) {
        List<UserRole> roles = new ArrayList<>();
        for (UserRoleType tipo : tipos) {
            UserRole ur = new UserRole();
            ur.setId(UUID.randomUUID());
            ur.setRole(tipo);
            ur.setUser(user);
            roles.add(ur);
        }
        return roles;
    }

    private static ProdutoMateria criarPM(ProdutoFinal produto, MateriaPrima materia, String qtd) {
        ProdutoMateria pm = new ProdutoMateria();
        pm.setId(UUID.randomUUID());
        pm.setProduto(produto);
        pm.setMateria(materia);
        pm.setQuantidadePorUnidadeProduto(new BigDecimal(qtd));
        return pm;
    }

    private MockDataFactory() {}
}
