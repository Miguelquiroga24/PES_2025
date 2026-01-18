/**
 * BYCar - Checkout
 * Funcionalidad de autofill para datos de tarjeta (simulación)
 */

/**
 * Rellena automáticamente los datos de la tarjeta
 * El titular se obtiene del nombre del usuario logueado
 */
function autoFillCardData() {
    // Obtener nombre del usuario desde el data attribute
    var cardHolderInput = document.getElementById('cardHolder');
    var rawName = (cardHolderInput.dataset.userFullname || '').trim();

    // Usar nombre del usuario o fallback genérico
    var generatedName = rawName !== '' ? rawName.toUpperCase() : 'CLIENTE BYCAR';

    // Tipos de tarjeta ficticios
    var cardTypes = [
        { name: 'Visa', prefix: '4', length: 16 },
        { name: 'Mastercard', prefix: '5', length: 16 },
        { name: 'Amex', prefix: '37', length: 15 }
    ];

    var cardType = cardTypes[Math.floor(Math.random() * cardTypes.length)];

    // Generar número de tarjeta
    var cardNumber = cardType.prefix;
    while (cardNumber.length < cardType.length) {
        cardNumber += Math.floor(Math.random() * 10);
    }
    var formattedNumber = cardNumber.match(/.{1,4}/g).join(' ');

    // Generar fecha de expiración futura (MM/YY)
    var month = String(Math.floor(Math.random() * 12) + 1).padStart(2, '0');
    var now = new Date();
    var year = String(now.getFullYear() + Math.floor(Math.random() * 5) + 1).slice(-2);
    var expiry = month + '/' + year;

    // Generar CVV (3 dígitos)
    var cvv = String(Math.floor(Math.random() * 900) + 100);

    // Rellenar campos
    document.getElementById('cardNumber').value = formattedNumber;
    cardHolderInput.value = generatedName;
    document.getElementById('cardExpiry').value = expiry;
    document.getElementById('cardCVV').value = cvv;

    // Marcar visualmente como llenos
    document.querySelectorAll('.card-input').forEach(function(input) {
        input.classList.add('filled');
    });

    // Mostrar toast si existe la función
    if (typeof showToast === 'function') {
        showToast('Datos de tarjeta generados', 'success');
    }
}

/**
 * Validación del formulario antes de enviar
 */
function validateCheckoutForm() {
    var cardNumber = document.getElementById('cardNumber').value;
    var cardHolder = document.getElementById('cardHolder').value;
    var cardExpiry = document.getElementById('cardExpiry').value;
    var cardCVV = document.getElementById('cardCVV').value;

    if (!cardNumber || !cardHolder || !cardExpiry || !cardCVV) {
        if (typeof showToast === 'function') {
            showToast('Debes rellenar los datos de la tarjeta', 'warning');
        } else {
            alert('Debes rellenar los datos de la tarjeta usando el botón "Rellenar Automáticamente"');
        }
        return false;
    }

    return true;
}

// Añadir validación al formulario
document.addEventListener('DOMContentLoaded', function() {
    var form = document.getElementById('checkoutForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateCheckoutForm()) {
                e.preventDefault();
            }
        });
    }
});
