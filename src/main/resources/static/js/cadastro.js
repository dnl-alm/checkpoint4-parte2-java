document.getElementById('form-cadastro').addEventListener('submit', function (event) {
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;
    const erroEl = document.getElementById('erro-js');

    if (senha.length < 6) {
        event.preventDefault();
        erroEl.textContent = 'A senha precisa ter pelo menos 6 caracteres.';
        erroEl.style.display = 'block';
        return;
    }

    if (senha !== confirmarSenha) {
        event.preventDefault();
        erroEl.textContent = 'As senhas não coincidem.';
        erroEl.style.display = 'block';
        return;
    }

    erroEl.style.display = 'none';
});