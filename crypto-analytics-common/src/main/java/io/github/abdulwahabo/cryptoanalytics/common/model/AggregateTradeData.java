package io.github.abdulwahabo.cryptoanalytics.common.model;

public class AggregateTradeData {

    private String time;
    private double aggregateSales;
    private double aggregateBuys;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAggregateSales() {
        return aggregateSales;
    }

    public void setAggregateSales(double aggregateSales) {
        this.aggregateSales = aggregateSales;
    }

    public double getAggregateBuys() {
        return aggregateBuys;
    }

    public void setAggregateBuys(double aggregateBuys) {
        this.aggregateBuys = aggregateBuys;
    }
}
