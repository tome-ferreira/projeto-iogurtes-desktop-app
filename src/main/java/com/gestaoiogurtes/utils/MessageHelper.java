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


public final class MessageHelper {

    
    private static final int DURACAO_SEGUNDOS = 4;

    private MessageHelper() {
    }

    public static void mostrar(StackPane rootStack, String descricao, boolean sucesso) {
        if (rootStack == null || descricao == null)
            return;

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

        // setOnClose o botão x aparece e permite fechar imediatamente
        message.setOnClose(e -> rootStack.getChildren().remove(message));

        // Auto-fechar após DURACAO_SEGUNDOS
        var timer = new PauseTransition(Duration.seconds(DURACAO_SEGUNDOS));
        timer.setOnFinished(e -> rootStack.getChildren().remove(message));
        timer.play();

        // Posicionar no canto inferior direito sem bloquear a interação com o conteúdo
        StackPane.setAlignment(message, Pos.TOP_RIGHT);
        StackPane.setMargin(message, new Insets(16, 16, 0, 0));

        rootStack.getChildren().add(message);
    }
}
