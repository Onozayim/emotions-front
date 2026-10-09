// Emotion definitions rendered by the server (see Emotion.java) into
// <script type="application/json" id="emotions-data">.
window.Emotions = (() => {
  const all = JSON.parse(document.getElementById("emotions-data").textContent);
  const byKey = Object.fromEntries(all.map((emotion) => [emotion.key, emotion]));

  // Returns the key of the compound emotion made of the two primaries, or "".
  function compoundOf(first, second) {
    const match = all.find(
      (emotion) =>
        emotion.compound &&
        emotion.components.includes(first) &&
        emotion.components.includes(second) &&
        first !== second,
    );

    return match ? match.key : "";
  }

  return { all, byKey, compoundOf };
})();
