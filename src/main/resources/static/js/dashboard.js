// Dashboard charts. Data comes from <script type="application/json" id="dashboard-data">:
// { totals: { joy: 3, ... }, series: [{ createdAt, joy, sadness, ... }, ...] }
(() => {
  // Nothing to draw when the period has no data (the template shows a message instead).
  if (!document.getElementById("primaryTotalsChart")) return;

  const { totals, series } = JSON.parse(
    document.getElementById("dashboard-data").textContent,
  );

  const primary = Emotions.all.filter((emotion) => !emotion.compound);
  const compound = Emotions.all.filter((emotion) => emotion.compound);

  // Chart chrome: recessive hairline grid and axes, ink colors for text.
  const ACCENT = "#2a78d6";
  const CONTEXT_GRAY = "#c3c2b7";

  Chart.defaults.font.family = 'system-ui, -apple-system, "Segoe UI", sans-serif';
  Chart.defaults.color = "#52514e";
  Chart.defaults.borderColor = "#e1e0d9";

  const valueAxis = {
    beginAtZero: true,
    ticks: { precision: 0 },
    border: { display: false },
  };

  const xAxis = {
    grid: { display: false },
    border: { color: CONTEXT_GRAY },
  };

  const labels = series.map((day) => day.createdAt);

  // Totals: the bars are named on the axis, so one color is enough.
  function totalsChart(canvasId, emotions) {
    new Chart(document.getElementById(canvasId), {
      type: "bar",
      data: {
        labels: emotions.map((emotion) => emotion.label),
        datasets: [
          {
            label: "Emails",
            data: emotions.map((emotion) => totals[emotion.key] ?? 0),
            backgroundColor: ACCENT,
            borderRadius: 4,
            maxBarThickness: 32,
          },
        ],
      },
      options: {
        plugins: { legend: { display: false } },
        scales: { y: valueAxis, x: xAxis },
      },
    });
  }

  totalsChart("primaryTotalsChart", primary);
  totalsChart("compoundTotalsChart", compound);

  // Primary trend: five series, each with its fixed color from Emotion.java.
  new Chart(document.getElementById("primaryTrendChart"), {
    type: "line",
    data: {
      labels,
      datasets: primary.map((emotion) => ({
        label: emotion.label,
        data: series.map((day) => day[emotion.key]),
        borderColor: emotion.color,
        backgroundColor: emotion.color,
        borderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 4,
        tension: 0.3,
      })),
    },
    options: {
      interaction: { mode: "index", intersect: false },
      plugins: {
        legend: { position: "bottom", labels: { usePointStyle: true, pointStyle: "line" } },
      },
      scales: { y: valueAxis, x: xAxis },
    },
  });

  // Compound trend: ten series are too many to tell apart by color, so one is
  // highlighted and the rest stay gray for context.
  const highlightSelect = document.getElementById("compoundHighlight");

  const mostFrequent = compound.reduce((best, emotion) =>
    (totals[emotion.key] ?? 0) > (totals[best.key] ?? 0) ? emotion : best,
  );
  highlightSelect.value = mostFrequent.key;

  const compoundTrend = new Chart(document.getElementById("compoundTrendChart"), {
    type: "line",
    data: {
      labels,
      datasets: compound.map((emotion) => ({
        key: emotion.key,
        label: emotion.label,
        data: series.map((day) => day[emotion.key]),
        borderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 4,
        tension: 0.3,
      })),
    },
    options: {
      interaction: { mode: "index", intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          // Highlighted series first, then the rest by value
          itemSort: (a, b) =>
            (b.dataset.key === highlightSelect.value) - (a.dataset.key === highlightSelect.value) ||
            b.parsed.y - a.parsed.y,
        },
      },
      scales: { y: valueAxis, x: xAxis },
    },
  });

  function applyHighlight() {
    compoundTrend.data.datasets.forEach((dataset) => {
      const highlighted = dataset.key === highlightSelect.value;
      dataset.borderColor = highlighted ? ACCENT : CONTEXT_GRAY;
      dataset.backgroundColor = dataset.borderColor;
      dataset.borderWidth = highlighted ? 2.5 : 1;
      // Lower order is drawn last, so the highlighted line sits on top
      dataset.order = highlighted ? 0 : 1;
    });
    compoundTrend.update();
  }

  highlightSelect.addEventListener("change", applyHighlight);
  applyHighlight();
})();
