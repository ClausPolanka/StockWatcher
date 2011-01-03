package google.gwt.tutorial.stockwatcher.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StockWatcher implements EntryPoint {

    private static final int REFRESH_INTERVAL_IN_MILLISECONDS = 5000;

    private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable stocksFlexTable = new FlexTable();
    private HorizontalPanel addPanel = new HorizontalPanel();
    private TextBox newSymbolTextBox = new TextBox();
    private Button addStockButton = new Button("Add");
    private Label lastUpdatedLabel = new Label();
    private ArrayList<String> stocks = new ArrayList<String>();

    @Override
    public void onModuleLoad() {
        createTableForStockData();
        addStylesToElementsInStockListTable();
        assemblePanelToAddAStock();
        assembleMainPanel();
        associateMainPanelWithTheHtmlHostPage();
        moveCursorFocusToInputBox();
        setupTimerToRefreshStockListAutomatically();
        listenForMouseEventsOnAddButton();
        listenForKeyboardEventsInInputBox();
    }

    private void createTableForStockData() {
        stocksFlexTable.setText(0, 0, "Symbol");
        stocksFlexTable.setText(0, 1, "Price");
        stocksFlexTable.setText(0, 2, "Change");
        stocksFlexTable.setText(0, 3, "Remove");
    }

    private void addStylesToElementsInStockListTable() {
        stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
        stocksFlexTable.addStyleName("watchList");
        stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");
    }

    private void assemblePanelToAddAStock() {
        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);
        addPanel.addStyleName("addPanel");
    }

    private void assembleMainPanel() {
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);
    }

    private void associateMainPanelWithTheHtmlHostPage() {
        RootPanel.get("stockList").add(mainPanel);
    }

    private void moveCursorFocusToInputBox() {
        newSymbolTextBox.setFocus(true);
    }

    private void setupTimerToRefreshStockListAutomatically() {
        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                refreshWatchList();
                displayTimestampShowingLastRefresh();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL_IN_MILLISECONDS);
    }

    private void refreshWatchList() {
        StockPrice[] prices = new StockPrice[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            prices[i] = createNewRandomStockPriceFor(stocks.get(i));
        }
        updateTable(prices);
    }

    private StockPrice createNewRandomStockPriceFor(String stockPrice) {
        final double MAX_PRICE = 100.0; // $100.00
        final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

        double price = Random.nextDouble() * MAX_PRICE;
        double change = price * MAX_PRICE_CHANGE * (Random.nextDouble() * 2.0 - 1.0);

        return new StockPrice(stockPrice, price, change);
    }

    private void updateTable(StockPrice[] prices) {
        for (int i = 0; i < prices.length; i++) {
            updateTable(prices[i]);
        }
    }

    private void displayTimestampShowingLastRefresh() {
        lastUpdatedLabel.setText("Last update : " + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
    }

    private void updateTable(StockPrice price) {
        if (!stocks.contains(price.symbol)) {
            return;
        }
        int row = stocks.indexOf(price.symbol) + 1;

        // Populate the Price and Change fields with new data.
        stocksFlexTable.setText(row, 1, price.formatPrice("#,##0.00"));

        Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
        changeWidget.setText(price.formatChange("+#,##0.00;-#,##0.00") + " (" + price.formatChangePercent("+#,##0.00;-#,##0.00") + "%)");

        changeColorOfTextInChangeFieldBasedOnItsValue(price, changeWidget);
        applyTableStylingTo(row);
    }

    private void changeColorOfTextInChangeFieldBasedOnItsValue(StockPrice price, Label changeWidget) {
        String changeStyleName = "noChange";
        if (price.getChangePercent() < -0.1f) {
            changeStyleName = "negativeChange";
        } else if (price.getChangePercent() > 0.1f) {
            changeStyleName = "positiveChange";
        }
        changeWidget.setStyleName(changeStyleName);
    }

    private void applyTableStylingTo(int row) {
        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
    }

    private void listenForMouseEventsOnAddButton() {
        addStockButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                addStockToFlexTable();
            }
        });
    }

    private void addStockToFlexTable() {
        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
        newSymbolTextBox.setFocus(true);
        if (notBetweenOneAndTenCharactersThatAreNumbersLettersOrDots(symbol)) {
            Window.alert("'" + symbol + "' is not valid symbol");
            newSymbolTextBox.selectAll();
            return;
        }
        newSymbolTextBox.setText("");

        if (stocks.contains(symbol)) {
            return;
        }
        addToTable(symbol);
        addButtonToRemoveThis(symbol);
        refreshWatchList();
        displayTimestampShowingLastRefresh();
    }

    private boolean notBetweenOneAndTenCharactersThatAreNumbersLettersOrDots(String symbol) {
        return !symbol.matches("^[0-9A-Z\\.]{1,10}$");
    }

    private void addToTable(final String symbol) {
        stocks.add(symbol);
        stocksFlexTable.setText(stocksFlexTable.getRowCount(), 0, symbol);
        stocksFlexTable.setWidget(stocksFlexTable.getRowCount() - 1, 2, new Label());
    }

    private void addButtonToRemoveThis(final String symbol) {
        Button removeStockButton = new Button("x");
        removeStockButton.addStyleDependentName("remove");
        removeStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeFromTable(symbol);
            }
        });
        stocksFlexTable.setWidget(stocksFlexTable.getRowCount() - 1, 3, removeStockButton);
    }

    private void removeFromTable(final String symbol) {
        int removedIndex = stocks.indexOf(symbol);
        stocks.remove(removedIndex);
        stocksFlexTable.removeRow(removedIndex + 1);
    }

    private void listenForKeyboardEventsInInputBox() {
        newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                    addStockToFlexTable();
                }
            }
        });
    }
}
