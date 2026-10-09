// Email detail page: sandboxed body sizing and the emotion editor.
// Reads its settings from data-* attributes on #email-editor.
(() => {
  const editor = document.getElementById("email-editor");
  const { updateUrl, currentPrimary, currentSecondary } = editor.dataset;

  const emailFrame = document.getElementById("email_content");

  function resizeEmailFrame() {
    const doc = emailFrame.contentDocument;
    if (doc && doc.documentElement) {
      emailFrame.style.height = doc.documentElement.scrollHeight + "px";
    }
  }

  emailFrame.addEventListener("load", resizeEmailFrame);
  if (emailFrame.contentDocument?.readyState === "complete") {
    resizeEmailFrame();
  }

  const primarySelect = document.getElementById("primary_emotion");
  const secondarySelect = document.getElementById("secondary_emotion");
  const compoundLabel = document.getElementById("compound_emotion");
  const saveButton = document.getElementById("update_emotion");

  function showCompound() {
    const key = Emotions.compoundOf(primarySelect.value, secondarySelect.value);
    compoundLabel.innerText = key ? Emotions.byKey[key].label : "—";
  }

  if (currentPrimary) primarySelect.value = currentPrimary;
  if (currentSecondary) secondarySelect.value = currentSecondary;
  if (currentPrimary && currentSecondary) showCompound();

  primarySelect.addEventListener("change", showCompound);
  secondarySelect.addEventListener("change", showCompound);

  saveButton.addEventListener("click", () => {
    const primaryEmotion = primarySelect.value;
    const secondaryEmotion = secondarySelect.value;

    if (primaryEmotion == secondaryEmotion) {
      Swal.fire({
        title: "Nothing was saved",
        text: "Primary and secondary emotions must be different.",
        icon: "warning",
      });

      return;
    }

    const body = {
      primaryEmotion,
      secondaryEmotion,
      compoundEmotion: Emotions.compoundOf(primaryEmotion, secondaryEmotion),
    };

    saveButton.disabled = true;
    saveButton.textContent = "Saving…";

    fetch(updateUrl, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    })
      .then((response) => {
        if (!response.ok) throw new Error("HTTP " + response.status);

        Swal.fire({
          title: "Saved",
          text: "The email's emotions have been updated.",
          icon: "success",
        }).then((result) => {
          window.location.reload();
        });
      })
      .catch((error) => {
        saveButton.disabled = false;
        saveButton.textContent = "Save";

        Swal.fire({
          title: "Error",
          text: "The email could not be updated. Please try again.",
          icon: "error",
        });
      });
  });
})();
