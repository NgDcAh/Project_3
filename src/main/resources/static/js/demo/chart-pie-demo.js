// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

// Pie Chart Example
var ctx = document.getElementById("myPieChart");


$.ajax({
	url: '/admin/get-percentage-per-category',
	type: 'GET',
	dataType: 'json',
	success: function(data) {
		// Kiểm tra xem dữ liệu có tồn tại hay không
		if (data) {
			// Lấy danh sách mặt hàng và giá trị từ dữ liệu Ajax
			var labels = Object.keys(data);
			var dataValues = Object.values(data);

			var dynamicColors = generateDynamicColors(labels.length);

			// Tạo biểu đồ với dữ liệu từ Ajax
			var myPieChart = new Chart(ctx, {
				type: 'doughnut',
				data: {
					labels: labels,
					datasets: [{
						data: dataValues,
						backgroundColor: dynamicColors,
						hoverBackgroundColor: dynamicColors.map(color => shadeColor(color, -10)),
						hoverBorderColor: "rgba(234, 236, 244, 1)",
					}],
				},
				options: {
					maintainAspectRatio: false,
					tooltips: {
						backgroundColor: "rgb(255,255,255)",
						bodyFontColor: "#858796",
						borderColor: '#dddfeb',
						borderWidth: 1,
						xPadding: 15,
						yPadding: 15,
						displayColors: false,
						caretPadding: 10,
					},
					legend: {
						display: false
					},
					cutoutPercentage: 80,
				},
			});
			var legend = document.getElementById('chart-legend');
			var labels = myPieChart.data.labels;
			var colors = myPieChart.data.datasets[0].backgroundColor;

			for (var i = 0; i < labels.length; i++) {
				var label = labels[i];
				var color = colors[i];

				var legendItem = document.createElement('span');
				legendItem.innerHTML = `
    <span class="mr-2">
      <i class="fas fa-circle" style="color: ${color};"></i> ${label}
    </span>
  `;
				legend.appendChild(legendItem);
			}

		} else {
			console.error('Invalid data format from the server');
		}
	},
	error: function(error) {
		console.error('Error fetching data:', error);
	}
});

function generateDynamicColors(numColors) {
	var dynamicColors = [];
	for (var i = 0; i < numColors; i++) {
		var hue = (i * (360 / numColors)) % 360;
		dynamicColors.push('hsl(' + hue + ', 70%, 60%)');
	}
	return dynamicColors;
}

// Hàm điều chỉ màu sắc
function shadeColor(color, percent) {
	var num = parseInt(color.slice(1), 16);
	var amt = Math.round(2.55 * percent);
	var R = (num >> 16) + amt;
	var G = (num >> 8 & 0x00FF) + amt;
	var B = (num & 0x0000FF) + amt;
	return "#" + (0x1000000 + (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 + (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 + (B < 255 ? (B < 1 ? 0 : B) : 255)).toString(16).slice(1);
}







