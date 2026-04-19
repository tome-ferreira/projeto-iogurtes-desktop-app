//v1.0
package com.gestaoiogurtes.services;

import com.gestaoiogurtes.services.interfaces.*;
import com.gestaoiogurtes.services.mock.*;

/**
 * Ponto central de instanciação e acesso a todos os serviços da aplicação.
 *
 * <p>
 * <b>Para trocar de mock para real, alterar a constante {@code USE_MOCK}:</b>
 * <pre>
 *   private static final boolean USE_MOCK = false; // ← usar JAR Spring Boot real
 * </pre>
 * </p>
 *
 * <p><b>Uso nos controllers:</b></p>
 * <pre>
 *   IEmpresaService empresaService = ServiceLocator.empresaService();
 *   List&lt;Empresa&gt; empresas = empresaService.getAll();
 * </pre>
 *
 * <p>
 * Quando {@code USE_MOCK = true}: retorna implementações em memória sem dependências externas.<br>
 * Quando {@code USE_MOCK = false}: retorna stubs que devem ser substituídos pelas
 * implementações reais do JAR Spring Boot (ver SPRING_BOOT_CONTRACT_DOC.md § 5).
 * </p>
 */
public final class ServiceLocator {

    /**
     * Alterar para {@code false} para usar o JAR Spring Boot real.
     * Com {@code true}, a aplicação funciona completamente standalone sem base de dados.
     */
    private static final boolean USE_MOCK = true;

    // ── Instâncias mock (criadas uma única vez, com dependências injectadas) ──

    private static final MockFornecedorService        mockFornecedor;
    private static final MockMateriaPrimaService      mockMateriaPrima;
    private static final MockEmpresaService           mockEmpresa;
    private static final MockUserService              mockUser;
    private static final MockMovimentoStockMPService  mockMovimentoMP;
    private static final MockProdutoFinalService      mockProduto;
    private static final MockPalletTipoService        mockPalletTipo;
    private static final MockOrdemProducaoService     mockOrdem;
    private static final MockEncomendaService         mockEncomenda;
    private static final MockLoginService             mockLogin;

    static {
        mockFornecedor   = new MockFornecedorService();
        mockMateriaPrima = new MockMateriaPrimaService(mockFornecedor);
        mockEmpresa      = new MockEmpresaService();
        mockUser         = new MockUserService(mockEmpresa);
        mockMovimentoMP  = new MockMovimentoStockMPService(mockMateriaPrima, mockUser);
        mockProduto      = new MockProdutoFinalService(mockMateriaPrima);
        mockPalletTipo   = new MockPalletTipoService();
        mockOrdem        = new MockOrdemProducaoService(mockUser, mockProduto, mockMovimentoMP);
        mockEncomenda    = new MockEncomendaService(mockUser, mockProduto, mockPalletTipo, mockOrdem);
        mockLogin        = new MockLoginService(mockUser);
    }

    // ── Accessors ──────────────────────────────────────────────────

    /**
     * Retorna o serviço de empresas.
     *
     * @return implementação mock ou real de {@link IEmpresaService}
     */
    public static IEmpresaService empresaService() {
        return USE_MOCK ? mockEmpresa : stubReal("IEmpresaService");
    }

    /**
     * Retorna o serviço de utilizadores.
     *
     * @return implementação mock ou real de {@link IUserService}
     */
    public static IUserService userService() {
        return USE_MOCK ? mockUser : stubReal("IUserService");
    }

    /**
     * Retorna o serviço de login.
     *
     * @return implementação mock ou real de {@link ILoginService}
     */
    public static ILoginService loginService() {
        return USE_MOCK ? mockLogin : stubReal("ILoginService");
    }

    /**
     * Retorna o serviço de fornecedores.
     *
     * @return implementação mock ou real de {@link IFornecedorService}
     */
    public static IFornecedorService fornecedorService() {
        return USE_MOCK ? mockFornecedor : stubReal("IFornecedorService");
    }

    /**
     * Retorna o serviço de matérias-primas.
     *
     * @return implementação mock ou real de {@link IMateriaPrimaService}
     */
    public static IMateriaPrimaService materiaPrimaService() {
        return USE_MOCK ? mockMateriaPrima : stubReal("IMateriaPrimaService");
    }

    /**
     * Retorna o serviço de movimentos de stock de matéria-prima.
     *
     * @return implementação mock ou real de {@link IMovimentoStockMPService}
     */
    public static IMovimentoStockMPService stockMPService() {
        return USE_MOCK ? mockMovimentoMP : stubReal("IMovimentoStockMPService");
    }

    /**
     * Retorna o serviço de produtos finais.
     *
     * @return implementação mock ou real de {@link IProdutoFinalService}
     */
    public static IProdutoFinalService produtoFinalService() {
        return USE_MOCK ? mockProduto : stubReal("IProdutoFinalService");
    }

    /**
     * Retorna o serviço de movimentos de stock de produto final.
     * <p>
     * <b>Nota (§ 7.6):</b> este serviço recebe objectos completos, não IDs.
     * </p>
     *
     * @return implementação mock ou real de {@link IMovimentoStockPFService}
     */
    public static IMovimentoStockPFService stockPFService() {
        return USE_MOCK ? new MockMovimentoStockPFService() : stubReal("IMovimentoStockPFService");
    }

    /**
     * Retorna o serviço de ordens de produção.
     *
     * @return implementação mock ou real de {@link IOrdemProducaoService}
     */
    public static IOrdemProducaoService ordemService() {
        return USE_MOCK ? mockOrdem : stubReal("IOrdemProducaoService");
    }

    /**
     * Retorna o serviço de encomendas.
     *
     * @return implementação mock ou real de {@link IEncomendaService}
     */
    public static IEncomendaService encomendaService() {
        return USE_MOCK ? mockEncomenda : stubReal("IEncomendaService");
    }

    /**
     * Retorna o serviço de tipos de pallet.
     *
     * @return implementação mock ou real de {@link IPalletTipoService}
     */
    public static IPalletTipoService palletTipoService() {
        return USE_MOCK ? mockPalletTipo : stubReal("IPalletTipoService");
    }

    // ── Helpers ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private static <T> T stubReal(String serviceName) {
        throw new UnsupportedOperationException(
                "Implementação real de " + serviceName + " ainda não configurada. " +
                "Consulte SPRING_BOOT_CONTRACT_DOC.md § 5 para instruções de substituição.");
    }

    private ServiceLocator() {}
}
