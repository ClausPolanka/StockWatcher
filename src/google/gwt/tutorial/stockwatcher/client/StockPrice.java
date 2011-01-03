package google.gwt.tutorial.stockwatcher.client;

import com.google.gwt.i18n.client.NumberFormat;

public class StockPrice {

    public final String symbol;
    public final double price;
    public final double change;

    public StockPrice(String symbol, double price, double change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    public double getChangePercent() {
        return 100.0 * change / price;
    }

    public String formatPrice(String formatPattern) {
        return NumberFormat.getFormat(formatPattern).format(this.price);
    }

    public String formatChange(String formatPattern) {
        NumberFormat changeFormat = NumberFormat.getFormat(formatPattern);
        return changeFormat.format(change);
    }

    public String formatChangePercent(String formatPattern) {
        NumberFormat changeFormat = NumberFormat.getFormat(formatPattern);
        return changeFormat.format(getChangePercent());
    }

}
