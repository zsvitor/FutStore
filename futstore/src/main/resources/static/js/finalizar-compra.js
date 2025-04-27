document.addEventListener('DOMContentLoaded', function () {
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', function (e) {
            e.preventDefault();
            const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
            logoutModal.show();
        });
    }
    const shippingSection = document.getElementById('shipping-section');
    const paymentSection = document.getElementById('payment-section');
    const reviewSection = document.getElementById('review-section');
    const stepShipping = document.getElementById('step-shipping');
    const stepPayment = document.getElementById('step-payment');
    const stepReview = document.getElementById('step-review');
    const btnToPayment = document.getElementById('btn-to-payment');
    const btnBackToShipping = document.getElementById('btn-back-to-shipping');
    const btnToReview = document.getElementById('btn-to-review');
    const btnBackToPayment = document.getElementById('btn-back-to-payment');
    const addressCards = document.querySelectorAll('.address-card');
    const enderecoRadios = document.querySelectorAll('.endereco-radio');
    enderecoRadios.forEach(radio => {
        radio.addEventListener('change', function () {
            addressCards.forEach(card => {
                card.classList.remove('selected');
            });
            if (this.checked) {
                this.closest('.address-card').classList.add('selected');
            }
        });
    });
    const paymentCards = document.querySelectorAll('.payment-method-card');
    const paymentRadios = document.querySelectorAll('.payment-method-radio');
    const cardDetails = document.getElementById('card-details');
    paymentRadios.forEach(radio => {
        radio.addEventListener('change', function () {
            paymentCards.forEach(card => {
                card.classList.remove('selected');
            });
            if (this.checked) {
                this.closest('.payment-method-card').classList.add('selected');
                if (this.value === 'CARTAO') {
                    cardDetails.style.display = 'block';
                } else {
                    cardDetails.style.display = 'none';
                }
            }
        });
    });
    btnToPayment.addEventListener('click', function () {
        let valid = false;
        const enderecoSelecionado = document.querySelector('input[name="enderecoEntregaId"]:checked');

        if (enderecoSelecionado) {
            valid = true;
            document.querySelector('.endereco-feedback').style.display = 'none';
        } else {
            document.querySelector('.endereco-feedback').style.display = 'block';
        }

        if (valid) {
            shippingSection.style.display = 'none';
            paymentSection.style.display = 'block';
            reviewSection.style.display = 'none';
            stepShipping.classList.remove('active');
            stepShipping.classList.add('completed');
            stepPayment.classList.add('active');
            updateAddressReview();
        }
    });
    btnBackToShipping.addEventListener('click', function () {
        shippingSection.style.display = 'block';
        paymentSection.style.display = 'none';
        reviewSection.style.display = 'none';
        stepPayment.classList.remove('active');
        stepShipping.classList.add('active');
        stepShipping.classList.remove('completed');
    });
    btnToReview.addEventListener('click', function () {
        let valid = false;
        const formaPagamentoSelecionada = document.querySelector('input[name="formaPagamento"]:checked');
        if (formaPagamentoSelecionada) {
            valid = true;
            document.querySelector('.payment-method-feedback').style.display = 'none';
            if (formaPagamentoSelecionada.value === 'CARTAO') {
                const cardFields = document.querySelectorAll('#card-details input');

                cardFields.forEach(field => {
                    if (!field.value) {
                        field.classList.add('is-invalid');
                        valid = false;
                    } else {
                        field.classList.remove('is-invalid');
                    }
                });
            }
        } else {
            document.querySelector('.payment-method-feedback').style.display = 'block';
        }
        if (valid) {
            shippingSection.style.display = 'none';
            paymentSection.style.display = 'none';
            reviewSection.style.display = 'block';
            stepPayment.classList.remove('active');
            stepPayment.classList.add('completed');
            stepReview.classList.add('active');
            updatePaymentReview();
        }
    });
    btnBackToPayment.addEventListener('click', function () {
        shippingSection.style.display = 'none';
        paymentSection.style.display = 'block';
        reviewSection.style.display = 'none';

        stepReview.classList.remove('active');
        stepPayment.classList.add('active');
        stepPayment.classList.remove('completed');
    });
    function updateAddressReview() {
        const selectedAddressDisplay = document.getElementById('selected-address-display');
        const enderecoSelecionado = document.querySelector('input[name="enderecoEntregaId"]:checked');
        if (enderecoSelecionado) {
            const addressCard = enderecoSelecionado.closest('.address-card').cloneNode(true);
            const radioInput = addressCard.querySelector('input[type="radio"]');
            if (radioInput) radioInput.remove();
            selectedAddressDisplay.innerHTML = '';
            selectedAddressDisplay.appendChild(addressCard);
        } else {
            selectedAddressDisplay.innerHTML = '<p>Nenhum endereço selecionado.</p>';
        }
    }
    function updatePaymentReview() {
        const selectedPaymentDisplay = document.getElementById('selected-payment-display');
        const formaPagamentoSelecionada = document.querySelector('input[name="formaPagamento"]:checked');
        if (formaPagamentoSelecionada) {
            if (formaPagamentoSelecionada.value === 'BOLETO') {
                selectedPaymentDisplay.innerHTML = `
            <div class="payment-method-card selected">
                <i class="fas fa-barcode me-2"></i>
                <strong>Boleto Bancário</strong><br>
                <small class="text-muted">O pedido será confirmado após o pagamento do boleto.</small>
            </div>
        `;
            } else if (formaPagamentoSelecionada.value === 'CARTAO') {
                const cardNumber = document.getElementById('cardNumber').value;
                const cardName = document.getElementById('cardName').value;
                const cardExpiry = document.getElementById('cardExpiry').value;
                const cardInstallments = document.getElementById('cardInstallments');
                const installmentsText = cardInstallments.options[cardInstallments.selectedIndex].text;
                selectedPaymentDisplay.innerHTML = `
            <div class="payment-method-card selected">
                <i class="fas fa-credit-card me-2"></i>
                <strong>Cartão de Crédito</strong><br>
                <small class="text-muted">Cartão com final ${cardNumber.substring(cardNumber.length - 4)}</small><br>
                <small class="text-muted">Titular: ${cardName}</small><br>
                <small class="text-muted">Validade: ${cardExpiry}</small><br>
                <small class="text-muted">Parcelas: ${installmentsText}</small>
            </div>
        `;
            }
        }
    }
    if (document.getElementById('cardNumber')) {
        document.getElementById('cardNumber').addEventListener('input', function (e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 16) value = value.substring(0, 16);
            let formattedValue = '';
            for (let i = 0; i < value.length; i++) {
                if (i > 0 && i % 4 === 0) formattedValue += ' ';
                formattedValue += value[i];
            }
            e.target.value = formattedValue;
        });
    }
    if (document.getElementById('cardExpiry')) {
        document.getElementById('cardExpiry').addEventListener('input', function (e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 4) value = value.substring(0, 4);
            if (value.length > 2) {
                e.target.value = value.substring(0, 2) + '/' + value.substring(2);
            } else {
                e.target.value = value;
            }
        });
    }
    if (document.getElementById('cardCVV')) {
        document.getElementById('cardCVV').addEventListener('input', function (e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 3) value = value.substring(0, 3);
            e.target.value = value;
        });
    }

    const checkoutForm = document.getElementById('checkoutForm');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function (e) {
            e.preventDefault();
            if (reviewSection.style.display === 'block') {
                let enderecoValid = false;
                const enderecoSelecionado = document.querySelector('input[name="enderecoEntregaId"]:checked');

                if (enderecoSelecionado) {
                    enderecoValid = true;
                }

                const formaPagamentoSelecionada = document.querySelector('input[name="formaPagamento"]:checked');
                let pagamentoValid = false;
                if (formaPagamentoSelecionada) {
                    pagamentoValid = true;
                    if (formaPagamentoSelecionada.value === 'CARTAO') {
                        const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');
                        const cardName = document.getElementById('cardName').value.trim();
                        const cardExpiry = document.getElementById('cardExpiry').value;
                        const cardCVV = document.getElementById('cardCVV').value;
                        if (cardNumber.length !== 16 || !/^\d{16}$/.test(cardNumber)) {
                            document.getElementById('cardNumber').classList.add('is-invalid');
                            pagamentoValid = false;
                        } else {
                            document.getElementById('cardNumber').classList.remove('is-invalid');
                        }
                        if (cardName.length < 3) {
                            document.getElementById('cardName').classList.add('is-invalid');
                            pagamentoValid = false;
                        } else {
                            document.getElementById('cardName').classList.remove('is-invalid');
                        }
                        if (!cardExpiry.match(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)) {
                            document.getElementById('cardExpiry').classList.add('is-invalid');
                            pagamentoValid = false;
                        } else {
                            document.getElementById('cardExpiry').classList.remove('is-invalid');
                        }
                        if (!cardCVV.match(/^\d{3,4}$/)) {
                            document.getElementById('cardCVV').classList.add('is-invalid');
                            pagamentoValid = false;
                        } else {
                            document.getElementById('cardCVV').classList.remove('is-invalid');
                        }
                    }
                }
                if (enderecoValid && pagamentoValid) {
                    const loadingOverlay = document.createElement('div');
                    loadingOverlay.className = 'loading-overlay';
                    loadingOverlay.innerHTML = `
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Processando...</span>
                        </div>
                        <p class="mt-2">Processando seu pedido...</p>
                    `;
                    document.body.appendChild(loadingOverlay);
                    this.submit();
                } else {
                    let errorMessage = 'Por favor, verifique se todos os campos estão preenchidos corretamente.';

                    if (!enderecoValid) {
                        errorMessage = 'Por favor, selecione um endereço de entrega válido.';
                        btnBackToShipping.click();
                    } else if (!pagamentoValid) {
                        errorMessage = 'Por favor, selecione uma forma de pagamento e preencha todos os dados necessários.';
                        btnBackToPayment.click();
                    }
                    const alertDiv = document.createElement('div');
                    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
                    alertDiv.setAttribute('role', 'alert');
                    alertDiv.innerHTML = `
                        ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    `;
                    const container = document.querySelector('.container');
                    container.insertBefore(alertDiv, container.firstChild.nextSibling);
                }
            } else {
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-danger alert-dismissible fade show';
                alertDiv.setAttribute('role', 'alert');
                alertDiv.innerHTML = `
                    Por favor, complete todas as etapas antes de confirmar o pedido.
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                `;
                const container = document.querySelector('.container');
                container.insertBefore(alertDiv, container.firstChild.nextSibling);
            }
        });
    }
});