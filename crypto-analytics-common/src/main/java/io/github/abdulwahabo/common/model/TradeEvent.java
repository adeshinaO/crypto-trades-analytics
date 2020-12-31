package io.github.abdulwahabo.common.model;

public class TradeEvent {

    private String seqnum;
    private String event;
    private String symbol;

    // TODO: Create a datetime without seconds and USE AS KAFKA PRODUCER KEY
    private String timestamp;

    private String side;
    private String qty;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {

        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {


        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getSeqnum() {
        return seqnum;
    }

    public void setSeqnum(String seqnum) {
        this.seqnum = seqnum;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getEvent() {

        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
