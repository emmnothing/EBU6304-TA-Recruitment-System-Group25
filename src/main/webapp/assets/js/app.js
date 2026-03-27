(function () {
  function bindDigitsOnly() {
    const inputs = document.querySelectorAll("[data-digits-only='true']");
    inputs.forEach((input) => {
      input.addEventListener("input", () => {
        input.value = input.value.replace(/\D/g, "").slice(0, 11);
      });
    });
  }

  function setupRegisterForm() {
    const registerForm = document.getElementById("registerForm");
    if (!registerForm) {
      return;
    }

    const emailInput = document.getElementById("emailInput");
    const phoneInput = document.getElementById("phoneInput");
    const passwordInput = document.getElementById("passwordInput");
    const confirmPasswordInput = document.getElementById("confirmPasswordInput");
    const verificationAnswerInput = document.getElementById("verificationAnswerInput");
    const captchaQuestion = document.getElementById("captchaQuestion");
    const registerClientError = document.getElementById("registerClientError");
    let captchaResult = 0;

    function generateCaptcha() {
      const num1 = Math.floor(Math.random() * 9) + 1;
      const num2 = Math.floor(Math.random() * 9) + 1;
      const operators = ["+", "-", "x"];
      const operator = operators[Math.floor(Math.random() * operators.length)];

      if (operator === "+") {
        captchaResult = num1 + num2;
        captchaQuestion.textContent = num1 + " + " + num2 + " = ?";
      } else if (operator === "-") {
        const bigger = Math.max(num1, num2);
        const smaller = Math.min(num1, num2);
        captchaResult = bigger - smaller;
        captchaQuestion.textContent = bigger + " - " + smaller + " = ?";
      } else {
        captchaResult = num1 * num2;
        captchaQuestion.textContent = num1 + " x " + num2 + " = ?";
      }
    }

    registerForm.addEventListener("submit", function (event) {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      const phonePattern = /^\d{11}$/;
      let message = "";

      if (!emailPattern.test(emailInput.value.trim())) {
        message = "Please enter a valid email address.";
      } else if (!phonePattern.test(phoneInput.value.trim())) {
        message = "Phone number must be exactly 11 digits.";
      } else if (passwordInput.value !== confirmPasswordInput.value || passwordInput.value.trim() === "") {
        message = "The two passwords do not match.";
      } else if (Number(verificationAnswerInput.value.trim()) !== captchaResult) {
        message = "Incorrect verification answer. Please try again.";
      }

      if (message) {
        event.preventDefault();
        registerClientError.textContent = message;
        registerClientError.style.display = "block";
        generateCaptcha();
      }
    });

    generateCaptcha();
  }

  function setupForgotForm() {
    const forgotForm = document.getElementById("forgotForm");
    if (!forgotForm) {
      return;
    }

    const emailInput = document.getElementById("emailInput");
    const phoneInput = document.getElementById("phoneInput");
    const newPasswordInput = document.getElementById("newPasswordInput");
    const confirmPasswordInput = document.getElementById("confirmPasswordInput");
    const forgotClientError = document.getElementById("forgotClientError");

    forgotForm.addEventListener("submit", function (event) {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      const phonePattern = /^\d{11}$/;
      let message = "";

      if (!phonePattern.test(phoneInput.value.trim())) {
        message = "Phone number must be exactly 11 digits.";
      } else if (!emailPattern.test(emailInput.value.trim())) {
        message = "Please enter a valid email address.";
      } else if (newPasswordInput.value !== confirmPasswordInput.value || newPasswordInput.value.trim() === "") {
        message = "The two passwords do not match.";
      }

      if (message) {
        event.preventDefault();
        forgotClientError.textContent = message;
        forgotClientError.style.display = "block";
      }
    });
  }

  bindDigitsOnly();
  setupRegisterForm();
  setupForgotForm();
})();
