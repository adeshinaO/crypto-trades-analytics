# BTC/USD Analytics

A data pipeline that collects data for trades of BTC/USD executed on 
[exchange.blockchain.com](https://exchange.blockchain.com) and feeds a dashboard showing the quanity of
BTC/USD sold/bought per minute.

TODO: GIF of dashboard.

## Pipeline Architecture

![](crypto-anly.png)

The first component of the pipeline is `Extractor`, A command line Java program written with JDK 11. It opens a 
persistent WebSocket connection to the [Blockchain WebSocket API](https://exchange.blockchain.com/api/#websocket-api). 
As new trades are executed on the exchange, `Extractor` receives their data and writes them to the Kafka `input topic`.  

Next component of the pipeline is a stream processing program built with the Kafka Streams library. It continuously 
polls the Kafka `input topic`, aggregates the data and writes to the `output topic`.

The final component of the pipeline is a Spring Boot application that serves the frontend assets for the web UI, and 
a provides a REST endpoint for fetching data that is displayed on charts. The UI consists of bar charts drawn using 
the ChartJS library. The frontend calls the data endpoint every 10 seconds and this causes the application to poll 
the Kafka `output topic`, package and return any available data.

## Tech Stack

* Java 11
* Apache Kafka
* Spring Boot
* Bulma CSS
