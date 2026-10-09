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

  if (currentPrimary) primarySelect.value = currentPrimary;
  if (currentSecondary) secondarySelect.value = currentSecondary;

  function showCompound() {
    compoundLabel.innerText = Emotions.compoundOf(
      primarySelect.value,
      secondarySelect.value,
    );
  }

  primarySelect.addEventListener("change", showCompound);
  secondarySelect.addEventListener("change", showCompound);

  document.getElementById("update_emotion").addEventListener("click", () => {
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
          title: "Updated!",
          text: "The email has been updated.",
          icon: "success",
        }).then((result) => {
          window.location.reload();
        });
      })
      .catch((error) => {
        Swal.fire({
          title: "Error",
          text: "The email could not be updated. Please try again.",
          icon: "error",
        });
      });
  });
})();
