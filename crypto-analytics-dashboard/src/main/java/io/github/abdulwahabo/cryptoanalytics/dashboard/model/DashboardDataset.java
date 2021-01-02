package io.github.abdulwahabo.cryptoanalytics.dashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DashboardDataset {

    // todo: Data for the barchart:
    //      An array of elements in the format.
    //      {
    //          t: new Date(), or "2020-2-2T09:03:00"
    //          y: 43.r
    //      }

    private List<Data> sell;
    private List<Data> buy;

    public List<Data> getSell() {
        return sell;
    }

    public void setSell(List<Data> sell) {
        this.sell = sell;
    }

    public List<Data> getBuy() {
        return buy;
    }

    public void setBuy(List<Data> buy) {
        this.buy = buy;
    }

    public static class Data {

        @JsonProperty("x")
        private String time;

        @JsonProperty("y")
        private double value;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
