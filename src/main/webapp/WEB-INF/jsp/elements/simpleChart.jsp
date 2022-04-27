<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class = "chart">
    <script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script type="text/javascript">
        const labels = [
            'January',
            'February',
            'March',
            'April',
            'May',
            'June',
        ];

        const data = {
            labels: labels,
            datasets: [{
                label: 'sample_dataset',
                backgroundColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 0,
                data: [0, 10, 5, 2, 20, 30, 45],
            }]
        };
    </script>
    <div class="chart-div" style="height: 300px; width: 300px;">
        <canvas id="myChart" style = "display: block; box-sizing: border-box; height: 300px; width: 300px;"></canvas>
    </div>
    <script src="${pageContext.request.contextPath}/JS/charts/doughnutChart.js"  type="text/javascript"></script>
</div>