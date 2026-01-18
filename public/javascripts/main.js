/* ============================================
   BYCAR - JAVASCRIPT INTERACTIVO PREMIUM
   Efectos visuales estilo Vercel
   ============================================ */

(function() {
    'use strict';

    // ============================================
    // 1. INICIALIZACIÓN
    // ============================================
    document.addEventListener('DOMContentLoaded', function() {
        initNavbarScroll();
        initMobileMenu();
        initCursor3DEffect();
        initAnimationsOnScroll();
        initParticleBackground();
        initSmoothScrolling();
    });

    // ============================================
    // 2. NAVBAR SCROLL EFFECT
    // ============================================
    function initNavbarScroll() {
        const navbar = document.querySelector('.navbar-premium');
        if (!navbar) return;

        let lastScroll = 0;
        const scrollThreshold = 50;

        function handleScroll() {
            const currentScroll = window.pageYOffset;

            // Añadir clase scrolled cuando se hace scroll
            if (currentScroll > scrollThreshold) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }

            lastScroll = currentScroll;
        }

        // Throttle para mejor rendimiento
        let ticking = false;
        window.addEventListener('scroll', function() {
            if (!ticking) {
                window.requestAnimationFrame(function() {
                    handleScroll();
                    ticking = false;
                });
                ticking = true;
            }
        });

        // Check inicial
        handleScroll();
    }

    // ============================================
    // 3. MOBILE MENU TOGGLE
    // ============================================
    function initMobileMenu() {
        const menuToggle = document.querySelector('.menu-toggle');
        const navLinks = document.querySelector('.nav-links');

        if (!menuToggle || !navLinks) return;

        menuToggle.addEventListener('click', function() {
            navLinks.classList.toggle('active');
            menuToggle.classList.toggle('active');
        });

        // Cerrar menú al hacer click en un link
        const links = navLinks.querySelectorAll('a');
        links.forEach(function(link) {
            link.addEventListener('click', function() {
                navLinks.classList.remove('active');
                menuToggle.classList.remove('active');
            });
        });

        // Cerrar al hacer click fuera
        document.addEventListener('click', function(e) {
            if (!menuToggle.contains(e.target) && !navLinks.contains(e.target)) {
                navLinks.classList.remove('active');
                menuToggle.classList.remove('active');
            }
        });
    }

    // ============================================
    // 4. EFECTO 3D CON CURSOR
    // ============================================
    function initCursor3DEffect() {
        const cards = document.querySelectorAll('.premium-card, .card-3d, .profile-section');

        cards.forEach(function(card) {
            card.addEventListener('mousemove', function(e) {
                const rect = card.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;

                const centerX = rect.width / 2;
                const centerY = rect.height / 2;

                const rotateX = (y - centerY) / 20;
                const rotateY = (centerX - x) / 20;

                card.style.transform =
                    'perspective(1000px) rotateX(' + (-rotateX) + 'deg) rotateY(' + (-rotateY) + 'deg) translateY(-4px)';
            });

            card.addEventListener('mouseleave', function() {
                card.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) translateY(0)';
            });
        });
    }

    // ============================================
    // 5. ANIMACIONES AL HACER SCROLL
    // ============================================
    function initAnimationsOnScroll() {
        const animatedElements = document.querySelectorAll('.animate-on-scroll');

        if (!animatedElements.length) return;

        const observerOptions = {
            root: null,
            rootMargin: '0px',
            threshold: 0.1
        };

        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animated');
                    observer.unobserve(entry.target);
                }
            });
        }, observerOptions);

        animatedElements.forEach(function(el) {
            observer.observe(el);
        });
    }

    // ============================================
    // 6. PARTÍCULAS DE FONDO (Canvas)
    // ============================================
    function initParticleBackground() {
        const canvas = document.getElementById('bgCanvas');
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        let particles = [];
        let animationId;

        // Configuración
        const config = {
            particleCount: 50,
            particleSize: 2,
            lineDistance: 150,
            speed: 0.5,
            color: '0, 112, 243' // RGB del accent-primary
        };

        // Resize canvas
        function resizeCanvas() {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        }

        // Crear partícula
        function createParticle() {
            return {
                x: Math.random() * canvas.width,
                y: Math.random() * canvas.height,
                vx: (Math.random() - 0.5) * config.speed,
                vy: (Math.random() - 0.5) * config.speed,
                size: Math.random() * config.particleSize + 1
            };
        }

        // Inicializar partículas
        function initParticles() {
            particles = [];
            for (var i = 0; i < config.particleCount; i++) {
                particles.push(createParticle());
            }
        }

        // Dibujar partículas y líneas
        function draw() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // Dibujar partículas
            particles.forEach(function(particle, i) {
                // Actualizar posición
                particle.x += particle.vx;
                particle.y += particle.vy;

                // Rebote en bordes
                if (particle.x < 0 || particle.x > canvas.width) particle.vx *= -1;
                if (particle.y < 0 || particle.y > canvas.height) particle.vy *= -1;

                // Dibujar partícula
                ctx.beginPath();
                ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(' + config.color + ', 0.5)';
                ctx.fill();

                // Dibujar líneas entre partículas cercanas
                for (var j = i + 1; j < particles.length; j++) {
                    var dx = particles[j].x - particle.x;
                    var dy = particles[j].y - particle.y;
                    var distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < config.lineDistance) {
                        var opacity = 1 - (distance / config.lineDistance);
                        ctx.beginPath();
                        ctx.moveTo(particle.x, particle.y);
                        ctx.lineTo(particles[j].x, particles[j].y);
                        ctx.strokeStyle = 'rgba(' + config.color + ', ' + (opacity * 0.2) + ')';
                        ctx.lineWidth = 1;
                        ctx.stroke();
                    }
                }
            });

            animationId = requestAnimationFrame(draw);
        }

        // Event listeners
        window.addEventListener('resize', function() {
            resizeCanvas();
            initParticles();
        });

        // Iniciar
        resizeCanvas();
        initParticles();
        draw();

        // Pausar cuando no está visible
        document.addEventListener('visibilitychange', function() {
            if (document.hidden) {
                cancelAnimationFrame(animationId);
            } else {
                draw();
            }
        });
    }

    // ============================================
    // 7. SMOOTH SCROLLING
    // ============================================
    function initSmoothScrolling() {
        document.querySelectorAll('a[href^="#"]').forEach(function(anchor) {
            anchor.addEventListener('click', function(e) {
                var targetId = this.getAttribute('href');
                if (targetId === '#') return;

                var target = document.querySelector(targetId);
                if (target) {
                    e.preventDefault();
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    }

    // ============================================
    // 8. UTILIDADES GLOBALES
    // ============================================

    // Función para añadir efecto ripple a botones
    window.addRippleEffect = function(selector) {
        var buttons = document.querySelectorAll(selector);

        buttons.forEach(function(button) {
            button.addEventListener('click', function(e) {
                var rect = button.getBoundingClientRect();
                var x = e.clientX - rect.left;
                var y = e.clientY - rect.top;

                var ripple = document.createElement('span');
                ripple.className = 'ripple-effect';
                ripple.style.left = x + 'px';
                ripple.style.top = y + 'px';

                button.appendChild(ripple);

                setTimeout(function() {
                    ripple.remove();
                }, 600);
            });
        });
    };

    // Función para mostrar notificaciones toast
    window.showToast = function(message, type) {
        type = type || 'info';

        var toast = document.createElement('div');
        toast.className = 'toast-notification toast-' + type;
        toast.textContent = message;

        document.body.appendChild(toast);

        // Trigger animation
        setTimeout(function() {
            toast.classList.add('show');
        }, 10);

        // Remove after delay
        setTimeout(function() {
            toast.classList.remove('show');
            setTimeout(function() {
                toast.remove();
            }, 300);
        }, 3000);
    };

    // ============================================
    // 9. FORM ENHANCEMENTS
    // ============================================

    // Auto-focus en primer input de formularios
    var firstInput = document.querySelector('.premium-card input:not([type="hidden"])');
    if (firstInput) {
        setTimeout(function() {
            firstInput.focus();
        }, 500);
    }

    // Validación visual en tiempo real
    var inputs = document.querySelectorAll('.premium-input, .form-control');
    inputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            if (this.value.trim() !== '') {
                this.classList.add('has-value');
            } else {
                this.classList.remove('has-value');
            }
        });

        // Check inicial
        if (input.value.trim() !== '') {
            input.classList.add('has-value');
        }
    });

})();
