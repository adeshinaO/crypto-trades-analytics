package io.github.abdulwahabo.extractor;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class BlockchainApiListener implements WebSocket.Listener {

    // TODO: Read Javadocs for the class.

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        return null;
    }

    @Override
    public void onOpen(WebSocket webSocket) {

    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {

    }
}
