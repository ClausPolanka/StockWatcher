package google.gwt.tutorial.stockwatcher.client;

import com.google.gwt.junit.client.GWTTestCase;

public class StockWatcherTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "google.gwt.tutorial.stockwatcher.StockWatcher";
    }

    public void test_Verify_that_instance_fields_in_StockPrice_are_set_correctly() {
        String symbol = "XYZ";
        double price = 70.0;
        double change = 2.0;
        double changePercent = 100.0 * change / price;

        StockPrice sp = new StockPrice(symbol, price, change);
        assertNotNull(sp);
        assertEquals(symbol, sp.symbol);
        assertEquals(price, sp.price, 0.001);
        assertEquals(change, sp.change, 0.001);
        assertEquals(changePercent, sp.getChangePercent(), 0.001);
    }

}