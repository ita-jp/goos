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
        var event = unpackEventFrom(message);
        var type = event.get("Event");
        if ("CLOSE".equals(type)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
            var price = Integer.parseInt(event.get("CurrentPrice"));
            var increment = Integer.parseInt(event.get("Increment"));
            listener.currentPrice(price, increment);
        }
    }

    private Map<String, String> unpackEventFrom(Message message) {
        return Arrays.stream(message.getBody().split(";"))
                .map(kv -> kv.split(":"))
                .collect(Collectors.toMap(kv -> kv[0].trim(), kv -> kv[1].trim()));
    }
}
