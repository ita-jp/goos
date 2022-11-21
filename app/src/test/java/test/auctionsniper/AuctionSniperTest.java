package test.auctionsniper;

import com.example.goos.Auction;
import com.example.goos.AuctionSniper;
import com.example.goos.SniperListener;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {

    @RegisterExtension
    private final JUnit5Mockery context = new JUnit5Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener listener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, listener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            oneOf(listener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportBiddingWhenNewPriceArrives() {
        var price = 1001;
        var increment = 25;

        context.checking(new Expectations(){{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(listener).sniperBidding();
        }});

        sniper.currentPrice(price, increment);
    }
}
