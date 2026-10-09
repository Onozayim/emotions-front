// Dashboard charts. Data comes from <script type="application/json" id="dashboard-data">:
// { totals: { joy: 3, ... }, series: [{ createdAt, joy, sadness, ... }, ...], from, to }
(() => {
  // Nothing to draw when the period has no data (the template shows a message instead).
  if (!document.getElementById("primaryTotalsChart")) return;

  const { totals, series, from, to } = JSON.parse(
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

  function countAxis() {
    return { beginAtZero: true, ticks: { precision: 0 }, border: { display: false } };
  }

  function percentAxis() {
    return {
      beginAtZero: true,
      ticks: { callback: (value) => value + "%" },
      border: { display: false },
    };
  }

  function xAxis() {
    return { grid: { display: false }, border: { color: CONTEXT_GRAY } };
  }

  // ---- Color mixing (in OKLab, so blends stay as bright as their parents) ----

  const srgbToLinear = (c) => (c <= 0.04045 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4);
  const linearToSrgb = (c) => (c <= 0.0031308 ? 12.92 * c : 1.055 * c ** (1 / 2.4) - 0.055);

  function hexToOklab(hex) {
    const [r, g, b] = [1, 3, 5].map((i) => srgbToLinear(parseInt(hex.slice(i, i + 2), 16) / 255));
    const l = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b);
    const m = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b);
    const s = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b);
    return [
      0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s,
      1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s,
      0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s,
    ];
  }

  function oklabToHex([L, a, b]) {
    const l = (L + 0.3963377774 * a + 0.2158037573 * b) ** 3;
    const m = (L - 0.1055613458 * a - 0.0638541728 * b) ** 3;
    const s = (L - 0.0894841775 * a - 1.291485548 * b) ** 3;
    const rgb = [
      4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
      -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
      -0.0041960863 * l - 0.7034186147 * m + 1.707614701 * s,
    ];
    return (
      "#" +
      rgb
        .map((c) => Math.round(Math.min(1, Math.max(0, linearToSrgb(c))) * 255))
        .map((c) => c.toString(16).padStart(2, "0"))
        .join("")
    );
  }

  function mixColors(first, second) {
    const [a, b] = [hexToOklab(first), hexToOklab(second)];
    return oklabToHex(a.map((value, i) => (value + b[i]) / 2));
  }

  // Primary emotions use their own color; compound emotions blend their two components.
  function colorOf(emotion) {
    if (!emotion.compound) return emotion.color;
    const [first, second] = emotion.components.map((key) => Emotions.byKey[key].color);
    return mixColors(first, second);
  }

  // ---- Totals ----

  function totalsChart(canvasId, emotions) {
    new Chart(document.getElementById(canvasId), {
      type: "bar",
      data: {
        labels: emotions.map((emotion) => emotion.label),
        datasets: [
          {
            label: "Emails",
            data: emotions.map((emotion) => totals[emotion.key] ?? 0),
            backgroundColor: emotions.map(colorOf),
            borderRadius: 4,
            maxBarThickness: 32,
          },
        ],
      },
      options: {
        plugins: { legend: { display: false } },
        scales: { y: countAxis(), x: xAxis() },
      },
    });
  }

  totalsChart("primaryTotalsChart", primary);
  totalsChart("compoundTotalsChart", compound);

  // ---- Time series: one entry per calendar day, days without a row count as 0 ----

  const DAY_MS = 24 * 60 * 60 * 1000;

  // Dates arrive as "2026-10-08" or, depending on the JSON serializer, as [2026, 10, 8].
  function isoDate(value) {
    if (value == null) return null;
    if (Array.isArray(value)) {
      const [year, month, day] = value;
      return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
    }
    return String(value).slice(0, 10);
  }

  const toUtcMs = (day) => Date.parse(day + "T00:00:00Z");
  const fromUtcMs = (ms) => new Date(ms).toISOString().slice(0, 10);

  const rowsByDay = new Map(series.map((row) => [isoDate(row.createdAt), row]));
  const dataDays = [...rowsByDay.keys()].sort();
  const today = new Date().toLocaleDateString("sv"); // local date as YYYY-MM-DD

  // Range: the filter dates when given, otherwise the first/last day with data.
  // An open-ended "To" in the future stops at today.
  const firstDay = isoDate(from) ?? dataDays[0];
  const lastDataDay = dataDays[dataDays.length - 1];
  let lastDay = isoDate(to) ?? lastDataDay;
  const latestAllowed = today > lastDataDay ? today : lastDataDay;
  if (lastDay > latestAllowed) lastDay = latestAllowed;

  const days = [];
  for (let ms = toUtcMs(firstDay); ms <= toUtcMs(lastDay); ms += DAY_MS) {
    const day = fromUtcMs(ms);
    const row = rowsByDay.get(day);
    const counts = Object.fromEntries(
      Emotions.all.map((emotion) => [emotion.key, row?.[emotion.key] ?? 0]),
    );
    days.push({ day, counts });
  }

  // ---- Grouping ----

  function bucketKey(day, group) {
    if (group === "month") return day.slice(0, 7);
    if (group === "week") {
      // Weeks start on Monday
      const ms = toUtcMs(day);
      const daysSinceMonday = (new Date(ms).getUTCDay() + 6) % 7;
      return fromUtcMs(ms - daysSinceMonday * DAY_MS);
    }
    return day;
  }

  function bucketLabel(key, group) {
    if (group === "month") {
      return new Date(key + "-01T00:00:00Z").toLocaleDateString("en", {
        month: "short",
        year: "numeric",
        timeZone: "UTC",
      });
    }
    if (group === "week") return "Week of " + key;
    return key;
  }

  function groupDays(group) {
    const buckets = new Map();

    for (const { day, counts } of days) {
      const key = bucketKey(day, group);
      if (!buckets.has(key)) {
        buckets.set(key, {
          label: bucketLabel(key, group),
          counts: Object.fromEntries(Emotions.all.map((emotion) => [emotion.key, 0])),
        });
      }
      const bucket = buckets.get(key);
      for (const emotion of Emotions.all) bucket.counts[emotion.key] += counts[emotion.key];
    }

    // Every email has exactly one primary emotion, so their sum is the email count.
    for (const bucket of buckets.values()) {
      bucket.emails = primary.reduce((sum, emotion) => sum + bucket.counts[emotion.key], 0);
    }

    return [...buckets.values()];
  }

  function valueOf(bucket, key, mode) {
    if (mode === "percent") {
      // No emails means no share at all: leave a gap rather than drawing 0%
      return bucket.emails ? (bucket.counts[key] / bucket.emails) * 100 : null;
    }
    return bucket.counts[key];
  }

  // ---- Line charts ----

  function lineDataset(emotion, color) {
    return {
      key: emotion.key,
      label: emotion.label,
      data: [],
      borderColor: color,
      backgroundColor: color,
      borderWidth: 2,
      pointRadius: 0,
      pointHoverRadius: 4,
      // Smooth, but never overshoots the real values (e.g. below zero)
      cubicInterpolationMode: "monotone",
    };
  }

  function tooltipLabel(context) {
    const count = context.dataset.counts[context.dataIndex];
    if (currentMode() === "percent") {
      return `${context.dataset.label}: ${context.parsed.y.toFixed(1)}% (${count})`;
    }
    return `${context.dataset.label}: ${count}`;
  }

  // Primary trend: five series, each with its fixed color from Emotion.java.
  const primaryTrend = new Chart(document.getElementById("primaryTrendChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: primary.map((emotion) => lineDataset(emotion, emotion.color)),
    },
    options: {
      interaction: { mode: "index", intersect: false },
      plugins: {
        legend: { position: "bottom", labels: { usePointStyle: true, pointStyle: "line" } },
        tooltip: { callbacks: { label: tooltipLabel } },
      },
      scales: { y: countAxis(), x: xAxis() },
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
      labels: [],
      datasets: compound.map((emotion) => lineDataset(emotion, CONTEXT_GRAY)),
    },
    options: {
      interaction: { mode: "index", intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: { label: tooltipLabel },
          // Highlighted series first, then the rest by value
          itemSort: (a, b) =>
            (b.dataset.key === highlightSelect.value) - (a.dataset.key === highlightSelect.value) ||
            (b.parsed.y ?? 0) - (a.parsed.y ?? 0),
        },
      },
      scales: { y: countAxis(), x: xAxis() },
    },
  });

  function applyHighlight() {
    compoundTrend.data.datasets.forEach((dataset) => {
      const highlighted = dataset.key === highlightSelect.value;
      // Same blended color as the emotion's bar in "Compound emotions"
      dataset.borderColor = highlighted ? colorOf(Emotions.byKey[dataset.key]) : CONTEXT_GRAY;
      dataset.backgroundColor = dataset.borderColor;
      dataset.borderWidth = highlighted ? 2.5 : 1;
      // Lower order is drawn last, so the highlighted line sits on top
      dataset.order = highlighted ? 0 : 1;
    });
    compoundTrend.update();
  }

  // ---- Controls: Count / % of emails and Day / Week / Month ----

  const currentMode = () => document.querySelector('input[name="trendMode"]:checked').value;
  const currentGroup = () => document.querySelector('input[name="trendGroup"]:checked').value;

  function renderTrends() {
    const mode = currentMode();
    const buckets = groupDays(currentGroup());
    // With few points a bare line hides where the values are, so show markers
    const pointRadius = buckets.length <= 12 ? 3 : 0;

    for (const chart of [primaryTrend, compoundTrend]) {
      chart.data.labels = buckets.map((bucket) => bucket.label);
      chart.data.datasets.forEach((dataset) => {
        dataset.data = buckets.map((bucket) => valueOf(bucket, dataset.key, mode));
        dataset.counts = buckets.map((bucket) => bucket.counts[dataset.key]);
        dataset.pointRadius = pointRadius;
      });
      chart.options.scales.y = mode === "percent" ? percentAxis() : countAxis();
      chart.update();
    }
  }

  document
    .querySelectorAll('input[name="trendMode"], input[name="trendGroup"]')
    .forEach((input) => input.addEventListener("change", renderTrends));
  highlightSelect.addEventListener("change", applyHighlight);

  renderTrends();
  applyHighlight();
})();
