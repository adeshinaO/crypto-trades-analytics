var chart_options = {
    scales: {
        xAxes: [{
            type: 'time',
            time: {
                unit: 'minute'
            }
        }],
        yAxes: [{
            scaleLabel: {
                display: true,
                labelString: 'Quantity of ETH'
            },
            ticks: {
                beginAtZero: true
            }
        }]
    }
};

var sell_chart_ctx = document.getElementById("sell_chart").getContext("2d");
var buy_chart_ctx = document.getElementById("buy_chart").getContext("2d");

var sell_chart = new Chart(sell_chart_ctx, {
    type: "bar",
    datasets: [{
        backgroundColor: "#ad3d3c",
        borderColor: "#ad3d3c",
        borderWidth: 0,
        data: []
    }],
    options: chart_options
});

var buy_chart = new Chart(buy_chart_ctx, {
    type: "bar",
    datasets: [{      
        backgroundColor: "#2e8b57",
        borderColor: "#2e8b57",
        borderWidth: 0,
        data: []
    }],  
    options: chart_options
});

var api = document.getElementById("data-api-url").value;

var refreshDataset = function () {
    fetch(api).then(response => {
        if (response.status == 200) {
            response.json().then(dataset => {
                buy_chart.datasets[0].data = dataset.buy;
                sell_chart.datasets[0].data = dataset.sell;
            })
        } else {
            console.warn("STATUS CODE: " + response.status);
        }
    }).catch(console.error);
}

refreshDataset();
setInterval(refreshDataset, 20000);
