// Survey template editor: builds the questions JSON into the hidden #template input on submit.
const container = document.getElementById("questionsContainer");
const addQuestionButton = document.getElementById("addQuestion");
const form = document.getElementById("surveyTemplateForm");
const templateInput = document.getElementById("template");

let questionCounter = 0;
const existingTemplateElement =
  document.getElementById("existingTemplate");

let existingTemplate = {
  questions: [],
};

try {
  existingTemplate = JSON.parse(existingTemplateElement.textContent);
} catch (error) {
  console.error("Could not parse survey template", error);
}

if (existingTemplate.questions) {
  existingTemplate.questions.forEach((question) =>
    addQuestion(question),
  );
}

addQuestionButton.addEventListener("click", () => {
  addQuestion({
    type: "rating",
    label: "",
    required: false,
    min: 1,
    max: 5,
  });
});

function addQuestion(question = {}) {
  questionCounter++;

  const questionId = question.id || `q${questionCounter}`;
  const numericId = parseInt(questionId.replace("q", ""));

  if (!isNaN(numericId)) {
    questionCounter = Math.max(questionCounter, numericId);
  }

  const wrapper = document.createElement("div");

  wrapper.classList.add("card", "mb-3", "question-item");
  wrapper.dataset.questionId = questionId;
  wrapper.innerHTML = `
    <div class="card-body">
        <div
            class="d-flex
            justify-content-between
            align-items-center
            mb-3"
        >
            <strong>
                Question
                <span class="question-number"></span>
            </strong>

            <button type="button" class="btn btn-danger btn-sm remove-question">
                Remove
            </button>

        </div>


        <div class="mb-3">
            <label>Question</label>

            <input
                type="text"
                class="form-control question-label"
                required
            />
        </div>


        <div class="mb-3">
            <label> Type </label>

            <select class="form-select question-type">
                <option value="rating">
                    Rating
                </option>

                <option value="textarea">
                    Text Area
                </option>

                <option value="multiple_choice">
                    Multiple Choice
                </option>
            </select>
        </div>

        <div class="form-check mb-3">
            <input
            type="checkbox"
            class="form-check-input question-required"
            />

            <label class="form-check-label"> Required </label>
        </div>
        <div class="question-options"></div>
    </div>`;

  container.appendChild(wrapper);

  const labelInput = wrapper.querySelector(".question-label");
  const typeSelect = wrapper.querySelector(".question-type");
  const requiredInput = wrapper.querySelector(".question-required");

  labelInput.value = question.label || "";
  typeSelect.value = question.type || "rating";
  requiredInput.checked = question.required === true;

  renderQuestionOptions(wrapper, question);

  typeSelect.addEventListener("change", () => {
    renderQuestionOptions(wrapper, {});
  });

  wrapper
    .querySelector(".remove-question")
    .addEventListener("click", () => {
      wrapper.remove();
      updateQuestionNumbers();
    });

  updateQuestionNumbers();
}

function renderQuestionOptions(wrapper, question) {
  const type = wrapper.querySelector(".question-type").value;
  const optionsContainer = wrapper.querySelector(".question-options");
  optionsContainer.innerHTML = "";

  if (type === "rating") {
    optionsContainer.innerHTML = `
    <div class="row">
        <div class="col-md-6 mb-3">
            <label> Minimum </label>
            <input
                type="number"
                class="form-control rating-min"
                value="${question.min ?? 1}"
                required
            />
        </div>
        <div class="col-md-6 mb-3">
            <label>
                Maximum
            </label>

            <input
                type="number"
                class="form-control rating-max"
                value="${question.max ?? 5}"
                required
            />
        </div>
    </div>`;
  }

  if (type === "textarea") {
    optionsContainer.innerHTML = `
    <div class="mb-3">
        <label>Maximum characters</label>

        <input
            type="number"
            class="form-control textarea-max-length"
            value="${question.maxLength ?? 2000}"
            min="1"
        />
    </div>`;
  }

  if (type === "multiple_choice") {
    optionsContainer.innerHTML = `
        <label>
            Options
        </label>

        <div class="choice-options"></div>

        <button
            type="button"
            class="btn btn-outline-primary btn-sm add-option mt-2"
        >
           + Add option
        </button>`;

    const choices = optionsContainer.querySelector(".choice-options");

    const existingOptions = question.options || [];

    existingOptions.forEach((option) => {
      addChoiceOption(choices, option);
    });

    if (existingOptions.length === 0) {
      addChoiceOption(choices, "");

      addChoiceOption(choices, "");
    }

    optionsContainer
      .querySelector(".add-option")
      .addEventListener("click", () => {
        addChoiceOption(choices, "");
      });
  }
}

function addChoiceOption(container, value) {
  const row = document.createElement("div");
  row.classList.add("input-group", "mb-2");
  row.innerHTML = `
    <input
        type="text"
        class="form-control choice-value"
        placeholder="Option"
    />

    <button
        type="button"
        class="btn btn-outline-danger remove-option"
    >
        Remove
    </button>
`;

  row.querySelector(".choice-value").value = value;
  row.querySelector(".remove-option").addEventListener("click", () => {
    row.remove();
  });
  container.appendChild(row);
}

function updateQuestionNumbers() {
  const questions = container.querySelectorAll(".question-item");

  questions.forEach((question, index) => {
    question.querySelector(".question-number").textContent = index + 1;
  });
}

function buildTemplate() {
  const questions = [];

  container
    .querySelectorAll(".question-item")
    .forEach((questionElement) => {
      const id = questionElement.dataset.questionId;

      const label = questionElement
        .querySelector(".question-label")
        .value.trim();

      const type =
        questionElement.querySelector(".question-type").value;

      const required =
        questionElement.querySelector(".question-required").checked;

      const question = {
        id,
        type,
        label,
        required,
      };

      if (type === "rating") {
        question.min = Number(
          questionElement.querySelector(".rating-min").value,
        );

        question.max = Number(
          questionElement.querySelector(".rating-max").value,
        );
      }

      if (type === "textarea") {
        const maxLength = questionElement.querySelector(
          ".textarea-max-length",
        );

        if (maxLength && maxLength.value) {
          question.maxLength = Number(maxLength.value);
        }
      }

      if (type === "multiple_choice") {
        question.options = Array.from(
          questionElement.querySelectorAll(".choice-value"),
        )
          .map((input) => input.value.trim())
          .filter((value) => value.length > 0);
      }

      questions.push(question);
    });

  return {
    questions,
  };
}

function showValidationError(text) {
  Swal.fire({ title: "Can't save yet", text, icon: "warning" });
}

form.addEventListener("submit", (event) => {
  const template = buildTemplate();

  if (template.questions.length === 0) {
    event.preventDefault();

    showValidationError("The survey must contain at least one question.");

    return;
  }

  for (const question of template.questions) {
    if (question.type === "rating" && question.min >= question.max) {
      event.preventDefault();

      showValidationError(
        `"${question.label}" must have a maximum greater than its minimum.`,
      );

      return;
    }

    if (
      question.type === "multiple_choice" &&
      question.options.length < 2
    ) {
      event.preventDefault();

      showValidationError(`"${question.label}" must have at least two options.`);

      return;
    }
  }

  templateInput.value = JSON.stringify(template);
});
