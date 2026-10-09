// Dashboard charts. Data comes from <script type="application/json" id="dashboard-data">:
// { totals: { joy: 3, ... }, series: [{ createdAt, joy, sadness, ... }, ...] }
(() => {
  const { totals, series } = JSON.parse(
    document.getElementById("dashboard-data").textContent,
  );
  const emotions = Emotions.all;

  new Chart(document.getElementById("myChart"), {
    type: "bar",
    data: {
      labels: emotions.map((emotion) => emotion.label),
      datasets: [
        {
          data: emotions.map((emotion) => totals[emotion.key] ?? 0),
          backgroundColor: emotions.map((emotion) => emotion.color),
          borderColor: emotions.map((emotion) => emotion.borderColor),
          borderWidth: 1,
        },
      ],
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });

  new Chart(document.getElementById("myChart2"), {
    type: "line",
    data: {
      labels: series.map((day) => day.createdAt),
      datasets: emotions.map((emotion) => ({
        label: emotion.label,
        data: series.map((day) => day[emotion.key]),
        borderColor: emotion.borderColor,
        tension: 0.3,
      })),
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });
})();
