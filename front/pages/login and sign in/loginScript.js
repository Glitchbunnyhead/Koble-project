document.addEventListener("DOMContentLoaded", () => {
    const isProfessor = document.getElementById("checkTeacher");
    const birthDateInput = document.getElementById("birthDateInput");
    const isCompany = document.getElementById("checkCompany");
    const cnpjInput = document.getElementById("cnpjInput");
    const loginForm = document.getElementById("LForm");

    // ===== Função auxiliar para animação =====
    function toggleAnimated(element, show) {
        if (!element) return;
        element.classList.remove("visibleField", "hiddenField");
        void element.offsetWidth; // reinicia a animação
        element.classList.add(show ? "visibleField" : "hiddenField");
        setTimeout(() => {
            element.style.display = show ? "block" : "none";
        }, show ? 0 : 300);
    }

    // ===== Institucional: esconder/mostrar data de nascimento =====
    if (isProfessor && birthDateInput) {
        toggleAnimated(birthDateInput, true);
        isProfessor.addEventListener("change", () => {
            toggleAnimated(birthDateInput, !isProfessor.checked);
        });
    }

    // ===== Externo: esconder/mostrar CNPJ =====
    if (isCompany && cnpjInput) {
        toggleAnimated(cnpjInput, false);
        isCompany.addEventListener("change", () => {
            toggleAnimated(cnpjInput, isCompany.checked);
        });
    }

    // ===== Redirecionar após login =====
    loginForm.addEventListener('submit', function(){
        window.open("../../index.html");
    })
});
