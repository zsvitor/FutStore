$(document).ready(function () {
    $('form').on('submit', function (e) {
        const senha = $('#senha').val();
        const confirmarSenha = $('#confirmarSenha').val();
        if (senha !== confirmarSenha) {
            e.preventDefault();
            alert('As senhas não correspondem. Por favor, verifique.');
            $('#confirmarSenha').addClass('is-invalid');
            return false;
        }
        return true;
    });
    $('#buscarCep').on('click', function () {
        const cep = $('#enderecoFaturamento\\.cep').val().replace(/\D/g, '');

        if (cep.length !== 8) {
            alert('Por favor, informe um CEP válido com 8 dígitos.');
            return;
        }
        $(this).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Buscando...');
        $(this).prop('disabled', true);
        $.getJSON(`https://viacep.com.br/ws/${cep}/json/`, function (data) {
            if (!data.erro) {
                $('#enderecoFaturamento\\.logradouro').val(data.logradouro);
                $('#enderecoFaturamento\\.bairro').val(data.bairro);
                $('#enderecoFaturamento\\.cidade').val(data.localidade);
                $('#enderecoFaturamento\\.uf').val(data.uf);
                $('#enderecoFaturamento\\.numero').focus();
            } else {
                alert('CEP não encontrado. Verifique o número informado.');
            }
        }).fail(function () {
            alert('Erro ao buscar o CEP. Verifique sua conexão ou tente novamente mais tarde.');
        }).always(function () {
            $('#buscarCep').html('Buscar');
            $('#buscarCep').prop('disabled', false);
        });
    });
    $('#enderecoFaturamento\\.cep').on('input', function () {
        let value = $(this).val().replace(/\D/g, '');
        if (value.length > 5) {
            value = value.substring(0, 5) + '-' + value.substring(5, 8);
        }
        $(this).val(value);
    });
    $('#cpf').on('input', function () {
        let value = $(this).val().replace(/\D/g, '');
        if (value.length > 9) {
            value = value.substring(0, 3) + '.' + value.substring(3, 6) + '.' + value.substring(6, 9) + '-' + value.substring(9, 11);
        } else if (value.length > 6) {
            value = value.substring(0, 3) + '.' + value.substring(3, 6) + '.' + value.substring(6);
        } else if (value.length > 3) {
            value = value.substring(0, 3) + '.' + value.substring(3);
        }
        $(this).val(value);
    });
});