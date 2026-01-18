/**
 * BYCar - Sistema de Carrito
 * Gestión del carrito de compras con AJAX
 */

// Estado del carrito
var cartState = {
    hasCart: false,
    carId: null,
    carName: null
};

/**
 * Inicializa el sistema de carrito
 */
function initCart() {
    updateCartBadge();
}

/**
 * Actualiza el badge del carrito en el navbar
 */
function updateCartBadge() {
    fetch('/order/hasCart')
        .then(response => response.json())
        .then(data => {
            cartState.hasCart = data.hasCart;
            cartState.carId = data.carId || null;
            cartState.carName = data.carName || null;

            var badge = document.querySelector('.cart-count-badge');
            if (badge) {
                if (data.hasCart) {
                    badge.textContent = '1';
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(error => {
            console.error('Error verificando carrito:', error);
        });
}

/**
 * Añade un coche al carrito
 * @param {number} carId - ID del coche
 * @param {boolean} force - Forzar reemplazo si ya hay otro coche
 */
function addToCart(carId, force) {
    var url = '/order/addToCart?carId=' + carId;
    if (force) {
        url += '&force=true';
    }

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Éxito - actualizar badge y mostrar toast
            updateCartBadge();
            showToast('Añadido al carrito', 'success');
        } else if (data.requireLogin) {
            // Necesita login
            showToast('Inicia sesión para añadir al carrito', 'warning');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } else if (data.alreadyInCart) {
            // Ya está en el carrito
            showToast('Este vehículo ya está en tu carrito', 'warning');
        } else if (data.needsConfirmation) {
            // Mostrar modal de confirmación
            showReplaceModal(carId, data.currentCarName, data.newCarName);
        } else {
            // Otro error
            showToast(data.error || 'Error al añadir al carrito', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    });
}

/**
 * Muestra el modal de confirmación para reemplazar coche
 */
function showReplaceModal(newCarId, currentCarName, newCarName) {
    // Verificar si existe el modal, si no, crearlo
    var modal = document.getElementById('replaceCartModal');
    if (!modal) {
        createReplaceModal();
        modal = document.getElementById('replaceCartModal');
    }

    // Actualizar contenido del modal
    var currentNameEl = modal.querySelector('.current-car-name');
    var newNameEl = modal.querySelector('.new-car-name');
    var confirmBtn = modal.querySelector('#confirmReplaceBtn');

    if (currentNameEl) currentNameEl.textContent = currentCarName || 'vehículo actual';
    if (newNameEl) newNameEl.textContent = newCarName || 'nuevo vehículo';

    // Configurar botón de confirmación
    if (confirmBtn) {
        confirmBtn.onclick = function() {
            // Cerrar modal
            var bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) bsModal.hide();

            // Añadir con force=true
            addToCart(newCarId, true);
        };
    }

    // Mostrar modal
    var bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

/**
 * Crea el modal de reemplazo dinámicamente
 */
function createReplaceModal() {
    var modalHtml = `
        <div class="modal fade" id="replaceCartModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Reemplazar vehículo</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <p>Ya tienes <strong class="current-car-name"></strong> en tu carrito.</p>
                        <p>¿Deseas reemplazarlo por <strong class="new-car-name"></strong>?</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" id="confirmReplaceBtn">Sí, reemplazar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

/**
 * Handler para botones de añadir al carrito
 */
function handleAddToCartClick(event) {
    event.preventDefault();
    var button = event.currentTarget;
    var carId = button.getAttribute('data-car-id');

    if (!carId) {
        showToast('Error: ID de vehículo no válido', 'error');
        return;
    }

    addToCart(parseInt(carId), false);
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    initCart();

    // Añadir event listeners a todos los botones de añadir al carrito
    var addToCartButtons = document.querySelectorAll('.btn-add-to-cart');
    addToCartButtons.forEach(function(button) {
        button.addEventListener('click', handleAddToCartClick);
    });
});
