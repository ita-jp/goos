package test.auctionsniper;

import com.example.goos.AuctionSniper;
import com.example.goos.SniperListener;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {

    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final SniperListener listener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(listener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            oneOf(listener).sniperLost();
        }});

        sniper.auctionClosed();
    }
}
