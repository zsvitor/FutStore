document.addEventListener('DOMContentLoaded', function () {
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', function (e) {
            e.preventDefault();
            const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
            logoutModal.show();
        });
    }
    document.querySelector('#alterarSenha form').addEventListener('submit', function (e) {
        const novaSenha = document.getElementById('novaSenha').value;
        const confirmarNovaSenha = document.getElementById('confirmarNovaSenha').value;
        if (novaSenha !== confirmarNovaSenha) {
            e.preventDefault();
            alert('As senhas não correspondem. Por favor, verifique.');
            return false;
        }
        return true;
    });
    $('#buscarCepNovo').on('click', function () {
        const cep = $('#cep').val().replace(/\D/g, '');
        if (cep.length !== 8) {
            alert('Por favor, informe um CEP válido com 8 dígitos.');
            return;
        }
        $(this).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Buscando...');
        $(this).prop('disabled', true);
        $.getJSON(`https://viacep.com.br/ws/${cep}/json/`, function (data) {
            if (!data.erro) {
                $('#logradouro').val(data.logradouro);
                $('#bairro').val(data.bairro);
                $('#cidade').val(data.localidade);
                $('#uf').val(data.uf);
                $('#numero').focus();
            } else {
                alert('CEP não encontrado. Verifique o número informado.');
            }
        }).fail(function () {
            alert('Erro ao buscar o CEP. Verifique sua conexão ou tente novamente mais tarde.');
        }).always(function () {
            $('#buscarCepNovo').html('Buscar');
            $('#buscarCepNovo').prop('disabled', false);
        });
    });
    $('#cep').on('input', function () {
        let value = $(this).val().replace(/\D/g, '');
        if (value.length > 5) {
            value = value.substring(0, 5) + '-' + value.substring(5, 8);
        }
        $(this).val(value);
    });
    const hash = window.location.hash;
    if (hash) {
        $('.list-group-item[href="' + hash + '"]').tab('show');
    }
    $('a[data-bs-toggle="list"]').on('click', function (e) {
        window.location.hash = $(this).attr('href');
    });
});