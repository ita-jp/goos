package com.example.goos.auctionsniper;

import com.example.goos.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class AuctionMessageTranslator implements MessageListener {

    private AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        var event = AuctionEvent.from(message.getBody());
        var eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
            listener.currentPrice(event.currentPrice(), event.increment());
        }
    }

    private static class AuctionEvent {

        private final Map<String, String> fields;

        private AuctionEvent(Map<String, String> fields) {
            this.fields = fields;
        }

        public String type() {
            return get("Event");
        }

        public int currentPrice() {
            return Integer.parseInt(get("CurrentPrice"));
        }

        public int increment() {
            return Integer.parseInt(get("Increment"));
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }

        static AuctionEvent from(String messageBody) {
            var map = Arrays.stream(messageBody.split(";"))
                    .map(kv -> kv.split(":"))
                    .collect(Collectors.toMap(kv -> kv[0].trim(), kv -> kv[1].trim()));
            return new AuctionEvent(map);
        }
    }
}
