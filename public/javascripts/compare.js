/* ============================================
   BYCAR - SISTEMA DE COMPARACION DE VEHICULOS
   Permite seleccionar 2-3 coches para comparar
   ============================================ */

(function() {
    'use strict';

    var STORAGE_KEY = 'bycar_compare_ids';
    var MAX_COMPARE = 3;
    var MIN_COMPARE = 2;

    // ============================================
    // INICIALIZACION
    // ============================================
    document.addEventListener('DOMContentLoaded', function() {
        initCompareSystem();
        updateCompareUI();
        initCompareCheckboxes();
        initCompareButtons();
    });

    // ============================================
    // GESTION DE IDs EN LOCALSTORAGE
    // ============================================

    // Obtener IDs seleccionados
    function getCompareIds() {
        var stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
            try {
                var ids = JSON.parse(stored);
                if (Array.isArray(ids)) {
                    return ids;
                }
            } catch (e) {
                // Si hay error, limpiar
                localStorage.removeItem(STORAGE_KEY);
            }
        }
        return [];
    }

    // Guardar IDs seleccionados
    function saveCompareIds(ids) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));
    }

    // Verificar si un ID esta seleccionado
    function isInCompare(carId) {
        var ids = getCompareIds();
        return ids.indexOf(carId.toString()) !== -1;
    }

    // Añadir a comparacion
    function addToCompare(carId) {
        var ids = getCompareIds();
        var carIdStr = carId.toString();

        if (ids.indexOf(carIdStr) !== -1) {
            return false; // Ya existe
        }

        if (ids.length >= MAX_COMPARE) {
            showCompareToast('Máximo ' + MAX_COMPARE + ' vehículos para comparar', 'warning');
            return false;
        }

        ids.push(carIdStr);
        saveCompareIds(ids);
        updateCompareUI();
        return true;
    }

    // Quitar de comparacion
    function removeFromCompare(carId) {
        var ids = getCompareIds();
        var carIdStr = carId.toString();
        var index = ids.indexOf(carIdStr);

        if (index !== -1) {
            ids.splice(index, 1);
            saveCompareIds(ids);
            updateCompareUI();
            return true;
        }
        return false;
    }

    // Toggle comparacion
    function toggleCompare(carId) {
        if (isInCompare(carId)) {
            removeFromCompare(carId);
            return false;
        } else {
            return addToCompare(carId);
        }
    }

    // Limpiar seleccion
    window.clearCompareSelection = function() {
        localStorage.removeItem(STORAGE_KEY);
        updateCompareUI();
        showCompareToast('Selección limpiada', 'info');
    };

    // ============================================
    // ACTUALIZACION DE UI
    // ============================================

    function updateCompareUI() {
        var ids = getCompareIds();
        var count = ids.length;

        // Actualizar checkboxes
        var checkboxes = document.querySelectorAll('.compare-checkbox');
        checkboxes.forEach(function(checkbox) {
            var carId = checkbox.getAttribute('data-car-id');
            checkbox.checked = isInCompare(carId);
        });

        // Actualizar botones de detalle
        var detailBtns = document.querySelectorAll('.btn-compare-toggle');
        detailBtns.forEach(function(btn) {
            var carId = btn.getAttribute('data-car-id');
            if (isInCompare(carId)) {
                btn.classList.add('active');
                btn.querySelector('.compare-btn-text').textContent = 'En comparación';
            } else {
                btn.classList.remove('active');
                btn.querySelector('.compare-btn-text').textContent = 'Añadir a comparación';
            }
        });

        // Actualizar barra flotante
        updateFloatingBar(count, ids);

        // Actualizar contador en navbar si existe
        var navCounter = document.querySelector('.compare-nav-count');
        if (navCounter) {
            navCounter.textContent = count;
            navCounter.style.display = count > 0 ? 'flex' : 'none';
        }
    }

    function updateFloatingBar(count, ids) {
        var floatingBar = document.getElementById('compareFloatingBar');
        if (!floatingBar) return;

        var countEl = floatingBar.querySelector('.compare-count');
        var compareBtn = floatingBar.querySelector('.btn-go-compare');
        var previewContainer = floatingBar.querySelector('.compare-preview-cars');

        // Actualizar contador
        if (countEl) {
            countEl.textContent = count + ' / ' + MAX_COMPARE;
        }

        // Mostrar/ocultar barra
        if (count > 0) {
            floatingBar.classList.add('visible');
        } else {
            floatingBar.classList.remove('visible');
        }

        // Habilitar/deshabilitar boton
        if (compareBtn) {
            if (count >= MIN_COMPARE) {
                compareBtn.classList.remove('disabled');
                compareBtn.removeAttribute('disabled');
            } else {
                compareBtn.classList.add('disabled');
                compareBtn.setAttribute('disabled', 'disabled');
            }
        }

        // Actualizar preview de coches (si existe el contenedor)
        if (previewContainer) {
            updatePreviewCars(previewContainer, ids);
        }
    }

    function updatePreviewCars(container, ids) {
        container.innerHTML = '';

        ids.forEach(function(id) {
            // Buscar datos del coche en el DOM
            var carCard = document.querySelector('.car-card[data-car-id="' + id + '"]');
            if (carCard) {
                var img = carCard.querySelector('.car-image img');
                var brand = carCard.querySelector('.car-brand');
                var model = carCard.querySelector('.car-model');

                if (img) {
                    var preview = document.createElement('div');
                    preview.className = 'compare-preview-item';
                    preview.innerHTML = '<img src="' + img.src + '" alt="' + (brand ? brand.textContent : '') + '">' +
                                       '<button class="compare-preview-remove" data-car-id="' + id + '">&times;</button>';
                    container.appendChild(preview);

                    // Event para quitar
                    preview.querySelector('.compare-preview-remove').addEventListener('click', function(e) {
                        e.stopPropagation();
                        removeFromCompare(id);
                    });
                }
            }
        });
    }

    // ============================================
    // INICIALIZACION DE ELEMENTOS
    // ============================================

    function initCompareSystem() {
        // Crear barra flotante si no existe
        if (!document.getElementById('compareFloatingBar') && document.querySelector('.catalog-container')) {
            createFloatingBar();
        }
    }

    function createFloatingBar() {
        var bar = document.createElement('div');
        bar.id = 'compareFloatingBar';
        bar.className = 'compare-floating-bar';
        bar.innerHTML =
            '<div class="compare-floating-content">' +
                '<div class="compare-floating-info">' +
                    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="compare-icon">' +
                        '<rect x="3" y="3" width="7" height="7"></rect>' +
                        '<rect x="14" y="3" width="7" height="7"></rect>' +
                        '<rect x="14" y="14" width="7" height="7"></rect>' +
                        '<rect x="3" y="14" width="7" height="7"></rect>' +
                    '</svg>' +
                    '<span class="compare-label">Comparar</span>' +
                    '<span class="compare-count">0 / ' + MAX_COMPARE + '</span>' +
                '</div>' +
                '<div class="compare-preview-cars"></div>' +
                '<div class="compare-floating-actions">' +
                    '<button class="btn-clear-selection" onclick="clearCompareSelection()">' +
                        'Limpiar' +
                    '</button>' +
                    '<button class="btn-go-compare disabled" disabled onclick="goToCompare()">' +
                        'Comparar Ahora' +
                    '</button>' +
                '</div>' +
            '</div>';

        document.body.appendChild(bar);
    }

    function initCompareCheckboxes() {
        var checkboxes = document.querySelectorAll('.compare-checkbox');
        checkboxes.forEach(function(checkbox) {
            checkbox.addEventListener('change', function() {
                var carId = this.getAttribute('data-car-id');
                if (this.checked) {
                    if (!addToCompare(carId)) {
                        this.checked = false; // Revertir si fallo
                    } else {
                        showCompareToast('Añadido a comparación', 'success');
                    }
                } else {
                    removeFromCompare(carId);
                }
            });
        });
    }

    function initCompareButtons() {
        // Botones toggle en detalle
        var toggleBtns = document.querySelectorAll('.btn-compare-toggle');
        toggleBtns.forEach(function(btn) {
            btn.addEventListener('click', function() {
                var carId = this.getAttribute('data-car-id');
                var added = toggleCompare(carId);
                if (added) {
                    showCompareToast('Añadido a comparación', 'success');
                }
            });
        });
    }

    // ============================================
    // NAVEGACION
    // ============================================

    window.goToCompare = function() {
        var ids = getCompareIds();
        if (ids.length < MIN_COMPARE) {
            showCompareToast('Selecciona al menos ' + MIN_COMPARE + ' vehículos', 'warning');
            return;
        }

        var url = '/car/compare?carIds=' + ids.join(',');
        window.location.href = url;
    };

    // ============================================
    // NOTIFICACIONES
    // ============================================

    function showCompareToast(message, type) {
        // Usar el sistema de toast existente si esta disponible
        if (typeof window.showToast === 'function') {
            window.showToast(message, type);
            return;
        }

        // Fallback simple
        var toast = document.createElement('div');
        toast.className = 'compare-toast compare-toast-' + type;
        toast.textContent = message;
        document.body.appendChild(toast);

        setTimeout(function() {
            toast.classList.add('show');
        }, 10);

        setTimeout(function() {
            toast.classList.remove('show');
            setTimeout(function() {
                toast.remove();
            }, 300);
        }, 2500);
    }

    // ============================================
    // EXPORTAR FUNCIONES GLOBALES
    // ============================================
    window.BYCarCompare = {
        getIds: getCompareIds,
        add: addToCompare,
        remove: removeFromCompare,
        toggle: toggleCompare,
        isSelected: isInCompare,
        clear: clearCompareSelection,
        goToCompare: goToCompare
    };

})();
