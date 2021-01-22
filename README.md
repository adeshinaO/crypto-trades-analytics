# BTC/USD Analytics

A data pipeline that collects data for trades of BTC/USD executed on 
[exchange.blockchain.com](https://exchange.blockchain.com) and feeds a dashboard showing the quanity of
BTC/USD sold/bought per minute.

TODO: GIF of dashboard.

# Pipeline Architecture

![](crypto-anly.png)

The first component of the pipeline is `Extractor`, A command line Java program written with JDK 11. It opens a 
persistent WebSocket connection to the [Blockchain WebSocket API](https://exchange.blockchain.com/api/#websocket-api). 
As new trades are executed on the exchange, `Extractor` receives their data and writes them to a Kafka topic.  

Next component of the pipeline is a stream processing program built with the Kafka Streams library. It continously