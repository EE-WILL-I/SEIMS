    function setRoundChart(key, lbl, lblKey, ctx, orgData) {
        const labels = new Array();
        const values = new Array();

        console.log(orgData['data']);
        for (const _key in orgData['data']) {
            labels.push(orgData['data'][_key][lblKey]);
            values.push(orgData['data'][_key][key]);
        }

        const data = {
            labels: labels,
            datasets: [{
                label: key,
                borderWidth: 0,
                fill: false,
                stepped: true,
                data: values
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
                        display: false,
                    },
                    title: {
                        display: true,
                        text: lbl,
                        font: {
                            size: 18
                        }
                    }
                }
            }
        });
    }


        function setBarChart(key, lbl, lblKey, ctx, orgData) {
            const labels = new Array();
            const values = new Array();

            console.log(orgData['data']);
            for (const _key in orgData['data']) {
                labels.push(orgData['data'][_key][lblKey]);
                values.push(orgData['data'][_key][key]);
            }

            const data = {
                labels: labels,
                datasets: [{
                    label: key,
                    fill: false,
                    data: values,
                    borderWidth: 3,
                    stepped: true,
                    data: values,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointHoverRadius: 8,
                    backgroundColor: '#25533b',
                    borderColor: '#25533b'
                }]
            }

            const workersChartCtx = document.getElementById(ctx);

            const workersChart = new Chart(workersChartCtx, {
                type: 'bar',
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
                            text: lbl,
                            font: {
                                size: 18
                            }
                        }
                    }
                }
            });
        }


        function setPolarChart(key, lbl, lblKey, ctx, orgData) {
                const labels = new Array();
                const values = new Array();

                console.log(orgData['data']);
                for (const _key in orgData['data']) {
                    labels.push(orgData['data'][_key][lblKey]);
                    values.push(orgData['data'][_key][key]);
                }

                const data = {
                    labels: labels,
                    datasets: [{
                        label: key,
                        borderWidth: 0,
                        fill: false,
                        stepped: true,
                        data: values
                    }]
                }

                const workersChartCtx = document.getElementById(ctx);

                const workersChart = new Chart(workersChartCtx, {
                    type: 'polarArea',
                    data: data,
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                display: false,
                            },
                            title: {
                                display: true,
                                text: lbl,
                                font: {
                                    size: 18
                                }
                            }
                        }
                    }
                });
            }