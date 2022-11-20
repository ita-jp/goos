package com.example.goos;

import com.example.goos.auctionsniper.ui.MainWindow;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FakeAuctionServer {

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private final SingleMessageListener messageListener = new SingleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public String getItemId() {
        return itemId;
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener((chat, createdLocally) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
        receivedAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivedAMessageMatching(sniperId, equalTo(format(Main.BID_COMMAND_FORMAT, bid)));
    }

    private void receivedAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        var format = "SOL Version: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;";
        var message = String.format(format, price, increment, bidder);
        currentChat.sendMessage(message);
    }

    public class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        @Override
        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        @SuppressWarnings("unchecked")
        public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
            final var message = messages.poll(5, TimeUnit.SECONDS);
            assertThat("Message", message, is(notNullValue()));
            assertThat(message.getBody(), messageMatcher);
        }
    }
}
