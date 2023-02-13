    function setChart(key, lbl, lblKey, ctx, orgData) {
        const labels = new Array();
        const values = new Array();

        for (const _key in orgData['data']) {
            labels.push(orgData['data'][_key][lblKey]);
            values.push(orgData['data'][_key][key]);
        }

        const data = {
            labels: labels,
            datasets: [{
                label: key,
                backgroundColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)'
                ],
                borderWidth: 5,
                fill: false,
                stepped: true,
                data: values,
            }]
        }

        const workersChartCtx = document.getElementById(ctx);

        const workersChart = new Chart(workersChartCtx, {
            type: 'doughnut',
            data: data,
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        display: false
                    },
                    title: {
                        display: true,
                        text: lbl
                    }
                }
            }
        });
    }