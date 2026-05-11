package com.gestaoiogurtes.utils;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;

/**
 * Utilitário partilhado para apresentar mensagens AtlantaFX ({@link Message})
 * em qualquer página CRUD do sistema.
 *
 * <h3>Como usar</h3>
 * <pre>{@code
 * // No controller da página (ex: EmpresasController):
 * MessageHelper.mostrar(rootStack, "Empresa criada com sucesso!", true);
 * MessageHelper.mostrar(rootStack, "Erro: " + mensagem, false);
 * }</pre>
 *
 * <h3>Como funciona</h3>
 * <ol>
 *   <li>Cria um {@link Message} (Node AtlantaFX com título + descrição + ícone)</li>
 *   <li>Aplica o estilo SUCCESS ou DANGER conforme o tipo de mensagem</li>
 *   <li>Define {@code setOnClose} para que o botão ✕ apareça e permita fechar manualmente</li>
 *   <li>Adiciona ao {@code StackPane} raiz com alinhamento BOTTOM_RIGHT</li>
 *   <li>Auto-fecha após {@value #DURACAO_SEGUNDOS} segundos via {@link PauseTransition}</li>
 * </ol>
 *
 * <h3>Pré-requisito</h3>
 * O {@code StackPane} raiz (fx:id="rootStack") deve ser o nó contentor da página.
 */
public final class MessageHelper {

    /** Duração em segundos antes de a mensagem fechar automaticamente. */
    private static final int DURACAO_SEGUNDOS = 4;



    private MessageHelper() {}

    /**
     * Apresenta uma mensagem no canto inferior direito do {@code rootStack} fornecido.
     *
     * @param rootStack StackPane raiz da página (fx:id="rootStack")
     * @param descricao texto descritivo a apresentar
     * @param sucesso   {@code true} → estilo verde (SUCCESS); {@code false} → estilo vermelho (DANGER)
     */
    public static void mostrar(StackPane rootStack, String descricao, boolean sucesso) {
        if (rootStack == null || descricao == null) return;

        String titulo = sucesso ? "Sucesso" : "Erro";
        String textoLimpo = descricao.replaceAll("\\R", " ").strip();

        var icone = new FontIcon(sucesso
                ? MaterialDesignD.DATABASE_CHECK_OUTLINE
                : MaterialDesignA.ALERT_CIRCLE_OUTLINE);

        var message = new Message(titulo, textoLimpo, icone);
        message.getStyleClass().add(sucesso ? Styles.SUCCESS : Styles.DANGER);
        message.setMinWidth(200);
        message.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        message.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        // setOnClose → o botão ✕ aparece e permite fechar imediatamente
        message.setOnClose(e -> rootStack.getChildren().remove(message));

        // Auto-fechar após DURACAO_SEGUNDOS
        var timer = new PauseTransition(Duration.seconds(DURACAO_SEGUNDOS));
        timer.setOnFinished(e -> rootStack.getChildren().remove(message));
        timer.play();

        // Posicionar no canto inferior direito sem bloquear a interação com o conteúdo
        StackPane.setAlignment(message, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(message, new Insets(0, 16, 16, 0));

        rootStack.getChildren().add(message);
    }
}
