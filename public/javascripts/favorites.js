/**
 * BYCar - Sistema de Favoritos
 * Gestión de favoritos con AJAX
 */

// Set global de IDs favoritos (para evitar llamadas repetidas)
var userFavoriteIds = new Set();
var isUserLoggedIn = false;

/**
 * Inicializa el sistema de favoritos al cargar la página
 */
function initFavorites() {
    // Cargar IDs de favoritos del usuario
    loadFavoriteIds();

    // Actualizar contador del badge
    updateFavoriteCount();
}

/**
 * Carga los IDs de favoritos del usuario actual
 */
function loadFavoriteIds() {
    fetch('/favorites/ids')
        .then(response => response.json())
        .then(data => {
            isUserLoggedIn = data.loggedIn;
            userFavoriteIds.clear();

            if (data.ids && Array.isArray(data.ids)) {
                data.ids.forEach(id => userFavoriteIds.add(id));
            }

            // Actualizar estado visual de todos los botones
            updateAllFavoriteButtons();
        })
        .catch(error => {
            console.error('Error cargando favoritos:', error);
        });
}

/**
 * Actualiza el estado visual de todos los botones de favorito en la página
 */
function updateAllFavoriteButtons() {
    // Botones en cards del catálogo y favoritos
    var buttons = document.querySelectorAll('.btn-favorite');
    buttons.forEach(function(button) {
        var carId = parseInt(button.getAttribute('data-car-id'));
        if (userFavoriteIds.has(carId)) {
            button.classList.add('active');
            button.title = 'Quitar de favoritos';
        } else {
            button.classList.remove('active');
            button.title = 'Añadir a favoritos';
        }
    });

    // Botón en página de detalle
    var detailButtons = document.querySelectorAll('.btn-favorite-detail');
    detailButtons.forEach(function(button) {
        var carId = parseInt(button.getAttribute('data-car-id'));
        if (userFavoriteIds.has(carId)) {
            button.classList.add('active');
            button.title = 'Quitar de favoritos';
            var textSpan = button.querySelector('.favorite-text');
            if (textSpan) textSpan.textContent = 'Guardado';
        } else {
            button.classList.remove('active');
            button.title = 'Añadir a favoritos';
            var textSpan = button.querySelector('.favorite-text');
            if (textSpan) textSpan.textContent = 'Favorito';
        }
    });
}

/**
 * Alterna el estado de favorito de un coche
 * @param {number} carId - ID del coche
 * @param {HTMLElement} button - Elemento botón clickeado
 */
function toggleFavorite(carId, button) {
    if (!isUserLoggedIn) {
        showToast('Inicia sesión para guardar favoritos', 'warning');
        // Opcional: redirigir a login
        // window.location.href = '/login';
        return;
    }

    var isCurrentlyFavorite = button.classList.contains('active');
    var endpoint = isCurrentlyFavorite ? '/favorites/remove' : '/favorites/add';

    // Actualización optimista de la UI
    button.classList.toggle('active');
    button.classList.add('pulse');

    // Actualizar Set local
    if (isCurrentlyFavorite) {
        userFavoriteIds.delete(carId);
    } else {
        userFavoriteIds.add(carId);
    }

    fetch(endpoint + '?carId=' + carId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        button.classList.remove('pulse');

        if (data.success) {
            // Actualizar badge
            updateFavoriteCountDisplay(data.count);

            // Mostrar toast
            var message = isCurrentlyFavorite ? 'Eliminado de favoritos' : 'Añadido a favoritos';
            var toastType = isCurrentlyFavorite ? 'removed' : 'success';
            showToast(message, toastType);

            // Actualizar título del botón
            button.title = isCurrentlyFavorite ? 'Añadir a favoritos' : 'Quitar de favoritos';

            // Actualizar texto si es botón de detalle
            var textSpan = button.querySelector('.favorite-text');
            if (textSpan) {
                textSpan.textContent = isCurrentlyFavorite ? 'Favorito' : 'Guardado';
            }
        } else {
            // Revertir cambio optimista
            button.classList.toggle('active');
            if (isCurrentlyFavorite) {
                userFavoriteIds.add(carId);
            } else {
                userFavoriteIds.delete(carId);
            }

            if (data.requireLogin) {
                showToast('Inicia sesión para guardar favoritos', 'warning');
            } else {
                showToast(data.error || 'Error al procesar', 'error');
            }
        }
    })
    .catch(error => {
        button.classList.remove('pulse');

        // Revertir cambio optimista
        button.classList.toggle('active');
        if (isCurrentlyFavorite) {
            userFavoriteIds.add(carId);
        } else {
            userFavoriteIds.delete(carId);
        }

        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    });
}

/**
 * Actualiza el contador de favoritos en el badge del navbar
 */
function updateFavoriteCount() {
    fetch('/favorites/count')
        .then(response => response.json())
        .then(data => {
            isUserLoggedIn = data.loggedIn;
            updateFavoriteCountDisplay(data.count);
        })
        .catch(error => {
            console.error('Error obteniendo contador:', error);
        });
}

/**
 * Actualiza visualmente el badge del contador
 * @param {number} count - Número de favoritos
 */
function updateFavoriteCountDisplay(count) {
    var badge = document.querySelector('.favorite-count-badge');
    if (badge) {
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }
}

/**
 * Muestra una notificación toast elegante
 * @param {string} message - Mensaje a mostrar
 * @param {string} type - Tipo: 'success', 'error', 'warning'
 */
function showToast(message, type) {
    // Remover toast existente si hay uno
    var existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }

    // Iconos SVG para cada tipo
    var icons = {
        success: '<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6L9 17l-5-5"/></svg>',
        removed: '<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5"/></svg>',
        error: '<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
        warning: '<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>'
    };

    // Crear nuevo toast
    var toast = document.createElement('div');
    toast.className = 'toast-notification toast-' + type;
    toast.innerHTML = (icons[type] || icons.success) + '<span class="toast-message">' + message + '</span>';

    document.body.appendChild(toast);

    // Animar entrada
    setTimeout(function() {
        toast.classList.add('show');
    }, 10);

    // Auto-remover después de 2.5 segundos
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() {
            toast.remove();
        }, 400);
    }, 2500);
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', initFavorites);
