# 🚗 BYCAR - INSTRUCCIONES MAESTRAS PARA EL AGENTE DE IA
## Concesionario Virtual - Proyecto de Ingeniería del Software

**Versión del documento:** 1.0  
**Última actualización:** 30 Noviembre 2025  
**Framework:** Play Framework 1.5.3  
**IDE:** IntelliJ IDEA 2025.2.5 Ultimate Edition  
**Java:** JDK 8 Update 471  

---

## 📋 ÍNDICE DE CONTENIDOS

1. [Visión General del Proyecto](#vision-general)
2. [Configuración del Entorno](#configuracion-entorno)
3. [Arquitectura y Estructura](#arquitectura)
4. [Modelos de Base de Datos](#modelos)
5. [Funcionalidades Principales](#funcionalidades)
6. [Diseño y UX/UI](#diseno)
7. [Sistema de Compra y Pago](#compra)
8. [Panel de Administración](#admin)
9. [Testing y Calidad](#testing)
10. [Reglas de Código](#reglas-codigo)
11. [Plan de Desarrollo](#plan-desarrollo)
12. [Criterios de Éxito](#criterios-exito)

---

## 1. VISIÓN GENERAL DEL PROYECTO {#vision-general}

### 🎯 Objetivo Principal
Desarrollar una aplicación web completa que simule un concesionario de vehículos de lujo en línea, superando en calidad, funcionalidad y diseño al proyecto de referencia WebPelis.

### 📊 Alcance del Proyecto
- **Tipo:** Aplicación Web Full-Stack
- **Usuarios:** Clientes (registro libre) + Administradores (creación manual)
- **Funcionalidades Core:**
  - Catálogo dinámico de vehículos de lujo
  - Sistema de compra con carrito (máximo 1 vehículo)
  - Pasarela de pago ficticia con autofill
  - Confirmación por email (HTML)
  - Historial de pedidos con estados
  - Panel de administración avanzado

### 🏆 Nivel de Calidad Esperado
**Superior al proyecto de referencia en todos los aspectos:**
- ✅ Código más limpio y organizado
- ✅ Diseño visual moderno y profesional
- ✅ Más funcionalidades (favoritos, comparador, filtros, modo oscuro)
- ✅ Mejor manejo de errores y validaciones
- ✅ Tests con 85-90% de cobertura
- ✅ Documentación completa pero natural (no parecer IA)

---

## 2. CONFIGURACIÓN DEL ENTORNO {#configuracion-entorno}

### 🔧 Stack Tecnológico

**Backend:**
- Play Framework 1.5.3
- Java 8 (JDK 8 Update 471)
- H2 Database (modo archivo)
- JPA/Hibernate

**Frontend:**
- HTML5 + CSS3 moderno
- JavaScript vanilla
- Framework CSS: **Bootstrap 5** (responsive, componentes profesionales)
- Animaciones suaves con CSS transitions/animations
- Diseño responsive de alta calidad

**Herramientas:**
- IntelliJ IDEA 2025.2.5 Ultimate Edition
- Git para control de versiones (opcional pero recomendado)

### 📦 Plugins de IntelliJ REQUERIDOS

**IMPORTANTE - INSTALAR ANTES DE EMPEZAR:**

1. **Play Framework Support**
   - Ir a: File → Settings → Plugins
   - Buscar: "Play Framework"
   - Instalar y reiniciar

2. **Scala** (necesario para Play 1.5.3)
   - Ir a: File → Settings → Plugins
   - Buscar: "Scala"
   - Instalar y reiniciar

3. **Play 2 Routes** (autocompletado en routes)
   - Ir a: File → Settings → Plugins
   - Buscar: "Play 2 Routes"
   - Instalar y reiniciar

**Verificación:**
- Después de instalar, reiniciar IntelliJ
- Abrir el proyecto BYCar
- Verificar que los archivos .java, .html y routes tengan syntax highlighting

### 📁 Estructura de Carpetas del Proyecto

```
BYCar/
├── app/
│   ├── controllers/
│   │   ├── Application.java          # Controlador principal
│   │   ├── UserController.java       # Gestión de usuarios
│   │   ├── CarController.java        # Gestión de coches
│   │   ├── OrderController.java      # Gestión de pedidos
│   │   └── AdminController.java      # Panel de administración
│   ├── models/
│   │   ├── User.java                 # Modelo de usuario
│   │   ├── Car.java                  # Modelo de coche
│   │   └── Order.java                # Modelo de pedido
│   └── views/
│       ├── main.html                 # Template principal
│       ├── Application/
│       │   └── index.html            # Página de inicio
│       ├── User/
│       │   ├── login.html            # Login
│       │   ├── register.html         # Registro
│       │   └── profile.html          # Perfil de usuario
│       ├── Car/
│       │   ├── catalog.html          # Catálogo completo
│       │   ├── detail.html           # Detalle de coche
│       │   └── favorites.html        # Favoritos
│       ├── Order/
│       │   ├── cart.html             # Carrito
│       │   ├── checkout.html         # Checkout
│       │   └── history.html          # Historial
│       └── Admin/
│           ├── dashboard.html        # Dashboard con estadísticas
│           ├── manageCars.html       # Gestión de coches
│           ├── manageOrders.html     # Gestión de pedidos
│           └── manageUsers.html      # Gestión de usuarios
├── conf/
│   ├── application.conf              # Configuración principal
│   ├── routes                        # Rutas de la aplicación
│   └── dependencies.yml              # Dependencias
├── data/
│   └── db/                           # Base de datos H2
├── public/
│   ├── images/
│   │   ├── logo/
│   │   │   └── bycar-logo.png        # Logo creado
│   │   └── cars/
│   │       ├── tesla_model_s_1.jpg
│   │       ├── tesla_model_s_2.jpg
│   │       ├── mercedes_s_class_1.jpg
│   │       ├── ... (resto de imágenes)
│   ├── javascripts/
│   │   ├── main.js                   # JavaScript principal
│   │   ├── cart.js                   # Lógica del carrito
│   │   ├── filters.js                # Filtros del catálogo
│   │   └── admin-charts.js           # Gráficas del admin
│   └── stylesheets/
│       ├── main.css                  # Estilos principales
│       ├── catalog.css               # Estilos del catálogo
│       ├── admin.css                 # Estilos del admin
│       └── dark-mode.css             # Estilos modo oscuro
├── test/
│   ├── BasicTest.java                # Tests unitarios básicos
│   ├── UserControllerTest.java       # Tests de usuarios
│   ├── CarControllerTest.java        # Tests de coches
│   ├── OrderControllerTest.java      # Tests de pedidos
│   └── ApplicationTest.java          # Tests funcionales
├── documentation/
│   ├── README.md                     # Instrucciones de instalación
│   └── MANUAL_USUARIO.md             # Manual de usuario
└── instruccions_road_map.md          # Este documento
```

---

## 3. ARQUITECTURA Y ESTRUCTURA {#arquitectura}

### 🏗️ Patrón MVC (Modelo-Vista-Controlador)

**ESTRICTAMENTE SEGUIR ESTE PATRÓN:**

#### Modelos (app/models/)
- **Responsabilidad:** Representar datos y lógica de negocio
- **Tecnología:** JPA/Hibernate con anotaciones
- **Reglas:**
  - Extends Model de Play Framework
  - Anotaciones: @Entity, @OneToMany, @ManyToOne, etc.
  - Getters y Setters para todos los campos
  - Constructores: vacío (JPA) + con parámetros

#### Vistas (app/views/)
- **Responsabilidad:** Presentación e interfaz de usuario
- **Tecnología:** HTML + Template Engine de Play
- **Reglas:**
  - Usar #{extends /} para heredar templates
  - Usar #{form @Controller.method()} para formularios
  - Variables: ${variable}
  - Loops: #{list items:lista, as:'item'}
  - Bootstrap 5 para componentes

#### Controladores (app/controllers/)
- **Responsabilidad:** Lógica de aplicación y flujo
- **Tecnología:** Java con Play Framework
- **Reglas:**
  - Extends Controller
  - Métodos static void
  - render() para vistas
  - renderJSON() para APIs
  - Validaciones antes de guardar datos

### 🔗 Separación de Responsabilidades

**UserController:**
- register()
- login()
- logout()
- profile()
- updateProfile()
- addToFavorites()
- removeFromFavorites()

**CarController:**
- listAll()
- detail()
- search()
- filter()
- compare()
- getFavorites()

**OrderController:**
- viewCart()
- addToCart()
- removeFromCart()
- checkout()
- processPayment()
- getHistory()
- viewOrderDetail()

**AdminController:**
- dashboard()
- manageCars()
- createCar()
- editCar()
- deleteCar()
- manageOrders()
- updateOrderStatus()
- manageUsers()
- getStatistics()

---

## 4. MODELOS DE BASE DE DATOS {#modelos}

### 📊 Diagrama ER

```
User (Cliente/Admin)
├── id: Long (PK, auto)
├── userName: String (UNIQUE, NOT NULL)
├── email: String (UNIQUE, NOT NULL)
├── password: String (NOT NULL)
├── fullName: String (NOT NULL)
├── phone: String (NOT NULL)
├── address: String (NOT NULL)
├── isAdmin: boolean (DEFAULT false)
└── favoriteCarIds: String (lista de IDs separados por coma)

Car (Vehículo)
├── id: Long (PK, auto)
├── marca: String (NOT NULL)
├── modelo: String (NOT NULL)
├── version: String
├── year: int (NOT NULL)
├── precio: double (NOT NULL)
├── color: String
├── potencia: String (NOT NULL)
├── combustible: String
├── puertas: int
├── transmision: String
├── descripcion: String (TEXT)
├── foto1: String (NOT NULL)
├── foto2: String
└── foto3: String

Order (Pedido)
├── id: Long (PK, auto)
├── user: User (FK, NOT NULL)
├── car: Car (FK, NOT NULL)
├── estado: String (NOT NULL)
│   └── Valores: 'pendiente_pago', 'pagado', 'entregado', 'cancelado'
├── fechaCreacion: Date (NOT NULL, auto)
├── emailConfirmacion: String (NOT NULL)
├── fullName: String (NOT NULL)
├── phone: String (NOT NULL)
└── address: String (NOT NULL)
```

### 🔑 Relaciones

- User → Order: **1:N** (Un usuario puede tener muchos pedidos)
- Car → Order: **1:N** (Un coche puede estar en muchos pedidos)
- User → Car (Favoritos): **N:M** (Implementado como String de IDs)

### ✅ Validaciones Obligatorias

**User:**
- userName: mínimo 3 caracteres, único
- email: formato válido, único
- password: mínimo 8 caracteres
- fullName: no vacío
- phone: formato válido (puede ser simple)
- address: no vacío

**Car:**
- marca: no vacío
- modelo: no vacío
- year: entre 2000 y 2026
- precio: mayor que 0
- potencia: no vacío
- foto1: URL válida

**Order:**
- user: no null
- car: no null
- estado: solo valores permitidos
- email: formato válido
- fullName, phone, address: no vacíos

---

## 5. FUNCIONALIDADES PRINCIPALES {#funcionalidades}

### 🔐 Sistema de Autenticación

**Registro (libre para clientes):**
1. Formulario con: userName, email, password, fullName, phone, address
2. Validar que userName y email sean únicos
3. Encriptar contraseña (opcional: BCrypt, o en texto plano para simplicidad)
4. Guardar con isAdmin = false
5. Login automático después del registro

**Login:**
1. Formulario con: email (o userName) + password
2. Buscar usuario en BD
3. Validar contraseña
4. Guardar en sesión: session.put("userId", user.id)
5. Redirigir a catálogo o perfil

**Logout:**
1. Limpiar sesión: session.clear()
2. Redirigir a inicio

### 🚗 Catálogo de Vehículos

**Listado completo:**
- Grid responsive (3 columnas en desktop, 2 en tablet, 1 en móvil)
- Card por cada coche: imagen, marca, modelo, precio, "Ver detalle"
- Paginación: 9 coches por página
- Botón "Añadir a favoritos" (⭐)

**Detalle de vehículo:**
- Galería de imágenes (1-3 fotos, navegable)
- Toda la información del coche
- Botón "Añadir al carrito"
- Botón "Añadir a favoritos"
- Botón "Comparar" (si está activo el modo comparación)

**Búsqueda y Filtros:**
- Barra de búsqueda por marca/modelo
- Filtros laterales:
  - Marca (checkboxes)
  - Precio (rango con slider)
  - Año (rango)
  - Combustible (checkboxes)
  - Transmisión (checkboxes)
- Ordenar por: Precio (asc/desc), Año (asc/desc), Marca (A-Z)

**Favoritos:**
- Guardar IDs de coches favoritos en User.favoriteCarIds
- Página de favoritos muestra solo los coches guardados
- Botón para quitar de favoritos

**Comparador:**
- Seleccionar 2-3 coches
- Vista lado a lado con todas las especificaciones
- Resaltar diferencias

### 🛒 Sistema de Compra

**Carrito (máximo 1 coche):**
- Si hay un coche y se añade otro, preguntar: "¿Reemplazar coche actual?"
- Vista del carrito: imagen, detalles, precio, botón "Eliminar"
- Botón "Proceder al checkout"

**Checkout:**
- Resumen del coche a comprar
- Formulario:
  - Email (prellenado del usuario)
  - Nombre completo (prellenado)
  - Teléfono (prellenado)
  - Dirección (prellenada)
- Sección de pago:
  - Campos de tarjeta: Número (16 dígitos), Titular, Fecha (MM/YY), CVV (3 dígitos)
  - Botón "Rellenar automáticamente"
  - NO permitir entrada manual en campos de tarjeta
- Botón "Confirmar compra"

**Procesamiento del pago:**
1. Validar que todos los campos estén llenos
2. Crear Order con estado = "pendiente_pago"
3. Cambiar estado a "pagado"
4. Enviar email de confirmación
5. Mostrar página de confirmación
6. Vaciar carrito

### 📧 Sistema de Email (PENDIENTE DE WEBHOOK)

**Nota:** Dejar preparado para integrar Google Apps Script Webhook más adelante.

**Estructura del email (HTML):**
```html
<!DOCTYPE html>
<html>
<head>
    <style>
        /* Estilos inline para email */
    </style>
</head>
<body>
    <h1>¡Gracias por tu compra en BYCar!</h1>
    <p>Hola [NOMBRE],</p>
    <p>Tu pedido #[NUMERO_PEDIDO] ha sido confirmado.</p>
    
    <h2>Detalles del vehículo:</h2>
    <ul>
        <li>Marca: [MARCA]</li>
        <li>Modelo: [MODELO]</li>
        <li>Año: [AÑO]</li>
        <li>Precio: [PRECIO]€</li>
    </ul>
    
    <h2>Instrucciones de recogida:</h2>
    <p>Puedes pasar a buscar tu vehículo al concesionario <strong>[MARCA]</strong> más cercano.</p>
    <p>Fecha estimada de recogida: [FECHA + 7 días]</p>
    
    <p>Gracias por confiar en BYCar.</p>
</body>
</html>
```

**Método en OrderController:**
```java
private static void sendConfirmationEmail(Order order) {
    // TODO: Integrar con Google Apps Script Webhook
    // Por ahora, solo registrar en logs
    Logger.info("Email enviado a: " + order.emailConfirmacion);
}
```

### 📜 Historial de Pedidos

**Para cada pedido mostrar:**
- Número de pedido
- Imagen del coche
- Marca y modelo
- Precio pagado
- Fecha de compra
- Estado (con color: verde=pagado, azul=entregado, rojo=cancelado)
- Botón "Ver detalle"

**Detalle de pedido:**
- Toda la información del pedido
- Toda la información del coche
- Línea de tiempo del estado
- Botón "Descargar factura" (PDF simple)

---

## 6. DISEÑO Y UX/UI {#diseno}

### 🎨 Identidad Visual

**Logo:**
- Crear un logo simple pero profesional para BYCar
- Formato: SVG o PNG transparente
- Colores: Negro/Gris oscuro + Acento (azul, rojo o dorado)
- Usar en navbar y favicon

**Paleta de Colores:**
```css
:root {
    --primary-color: #1e3a8a;      /* Azul oscuro elegante */
    --secondary-color: #dc2626;    /* Rojo acento */
    --accent-color: #f59e0b;       /* Dorado/Ámbar */
    --dark-bg: #111827;            /* Fondo oscuro */
    --light-bg: #f9fafb;           /* Fondo claro */
    --text-dark: #111827;          /* Texto oscuro */
    --text-light: #f9fafb;         /* Texto claro */
    --border-color: #e5e7eb;       /* Bordes */
    --success: #10b981;            /* Verde éxito */
    --warning: #f59e0b;            /* Amarillo advertencia */
    --error: #ef4444;              /* Rojo error */
}
```

**Modo Oscuro:**
```css
[data-theme="dark"] {
    --primary-color: #3b82f6;      /* Azul más claro */
    --dark-bg: #f9fafb;            /* Invertir fondos */
    --light-bg: #111827;
    --text-dark: #f9fafb;          /* Invertir textos */
    --text-light: #111827;
    --border-color: #374151;
}
```

### 📱 Responsive Design

**Breakpoints:**
- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

**Grid del catálogo:**
- Desktop: 3 columnas
- Tablet: 2 columnas
- Mobile: 1 columna

### ✨ Animaciones y Transiciones

**Aplicar con moderación y elegancia:**

```css
/* Hover en cards */
.car-card {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.car-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

/* Fade in al cargar */
.fade-in {
    animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

/* Smooth scroll */
html {
    scroll-behavior: smooth;
}
```

### 🧭 Navegación

**Navbar (siempre visible):**
- Logo (clickeable → inicio)
- Catálogo
- Mis Favoritos (solo si está logueado)
- Mi Perfil (dropdown: Ver perfil, Historial, Logout)
- Admin (solo si isAdmin=true)
- Carrito (con badge del número de items)
- Toggle modo oscuro (☀️/🌙)

**Footer:**
- Links: Sobre nosotros, Contacto, Términos y condiciones
- Redes sociales (iconos)
- © 2025 BYCar - Todos los derechos reservados

---

## 7. SISTEMA DE COMPRA Y PAGO {#compra}

### 💳 Pasarela de Pago Ficticia

**Implementación del AutoFill:**

```javascript
function autoFillCardData() {
    // Generar datos ficticios realistas
    const cardTypes = [
        { name: 'Visa', prefix: '4', length: 16 },
        { name: 'Mastercard', prefix: '5', length: 16 },
        { name: 'Amex', prefix: '37', length: 15 }
    ];
    
    // Seleccionar tipo aleatorio
    const cardType = cardTypes[Math.floor(Math.random() * cardTypes.length)];
    
    // Generar número
    let cardNumber = cardType.prefix;
    while (cardNumber.length < cardType.length) {
        cardNumber += Math.floor(Math.random() * 10);
    }
    
    // Formatear: XXXX XXXX XXXX XXXX
    const formatted = cardNumber.match(/.{1,4}/g).join(' ');
    
    // Generar titular (nombre ficticio)
    const names = ['JUAN PEREZ', 'MARIA GARCIA', 'CARLOS LOPEZ', 'ANA MARTINEZ'];
    const holder = names[Math.floor(Math.random() * names.length)];
    
    // Generar fecha (MM/YY) - siempre en el futuro
    const month = String(Math.floor(Math.random() * 12) + 1).padStart(2, '0');
    const year = String(new Date().getFullYear() + Math.floor(Math.random() * 5) + 1).slice(-2);
    const expiry = month + '/' + year;
    
    // Generar CVV (3 dígitos)
    const cvv = String(Math.floor(Math.random() * 900) + 100);
    
    // Rellenar campos
    document.getElementById('cardNumber').value = formatted;
    document.getElementById('cardHolder').value = holder;
    document.getElementById('cardExpiry').value = expiry;
    document.getElementById('cardCVV').value = cvv;
    
    // Deshabilitar campos para evitar edición manual
    document.getElementById('cardNumber').disabled = true;
    document.getElementById('cardHolder').disabled = true;
    document.getElementById('cardExpiry').disabled = true;
    document.getElementById('cardCVV').disabled = true;
}
```

**HTML del checkout:**
```html
<div class="payment-section">
    <h3>Información de Pago</h3>
    <button type="button" onclick="autoFillCardData()" class="btn btn-primary mb-3">
        🎲 Rellenar Automáticamente
    </button>
    
    <div class="form-group">
        <label>Número de Tarjeta</label>
        <input type="text" id="cardNumber" class="form-control" 
               placeholder="XXXX XXXX XXXX XXXX" readonly>
    </div>
    
    <div class="form-group">
        <label>Titular</label>
        <input type="text" id="cardHolder" class="form-control" 
               placeholder="NOMBRE APELLIDO" readonly>
    </div>
    
    <div class="row">
        <div class="col-md-6">
            <label>Fecha de Expiración</label>
            <input type="text" id="cardExpiry" class="form-control" 
                   placeholder="MM/YY" readonly>
        </div>
        <div class="col-md-6">
            <label>CVV</label>
            <input type="text" id="cardCVV" class="form-control" 
                   placeholder="XXX" readonly>
        </div>
    </div>
</div>
```

### ✅ Flujo Completo de Compra

1. Usuario añade coche al carrito
2. Va a "Ver carrito"
3. Click en "Proceder al checkout"
4. Verifica/edita datos personales
5. Click en "Rellenar automáticamente"
6. Revisa que todo esté correcto
7. Click en "Confirmar compra"
8. Backend crea Order con estado="pendiente_pago"
9. Backend cambia estado a "pagado"
10. Backend intenta enviar email (o registra en logs)
11. Mostrar página de confirmación
12. Vaciar carrito

---

## 8. PANEL DE ADMINISTRACIÓN {#admin}

### 📊 Dashboard (Página Principal del Admin)

**Estadísticas en Cards:**
- Total de ventas (suma de precios de órdenes pagadas)
- Número total de órdenes
- Número de clientes registrados
- Número de coches en catálogo

**Gráficas (Chart.js):**
1. **Gráfica de barras:** Ventas por marca
2. **Gráfica de líneas:** Ventas por mes (últimos 6 meses)
3. **Gráfica de pastel:** Distribución de estados de órdenes

**Tablas:**
- Últimas 10 órdenes (con estado, cliente, coche, precio)
- Coches más vendidos (top 5)

### 🚗 Gestión de Coches

**Listado:**
- Tabla con: ID, Imagen, Marca, Modelo, Año, Precio, Acciones
- Botón "Crear nuevo coche"
- Botones por fila: Editar, Eliminar

**Crear/Editar coche:**
- Formulario con todos los campos
- Validaciones en frontend y backend
- Previsualización de imágenes
- Botón "Guardar" / "Actualizar"

**Eliminar coche:**
- Modal de confirmación: "¿Estás seguro?"
- Si tiene órdenes asociadas: "No se puede eliminar, tiene X pedidos"

### 📦 Gestión de Órdenes

**Listado:**
- Tabla con: ID, Cliente, Coche, Fecha, Estado, Total, Acciones
- Filtros: Por estado, por fecha
- Botón "Ver detalle" por fila
- Dropdown para cambiar estado

**Detalle de orden:**
- Toda la información de la orden
- Información del cliente
- Información del coche
- Línea de tiempo de estados
- Botón "Cambiar estado"

### 👥 Gestión de Usuarios

**Listado:**
- Tabla con: ID, Username, Email, Nombre, Es Admin, Fecha Registro, Acciones
- Filtros: Clientes / Admins
- Botón "Ver pedidos del usuario"
- Botón "Activar/Desactivar" (opcional)

---

## 9. TESTING Y CALIDAD {#testing}

### 🧪 Estrategia de Testing

**Objetivo:** 85-90% de cobertura de código

**Prioridad 1 (Crítico):**
1. Login y Registro de usuarios
2. Proceso completo de compra (añadir al carrito → checkout → pago)
3. CRUD de coches (admin)
4. Cambio de estados de órdenes

**Prioridad 2 (Importante):**
1. Búsqueda y filtros de coches
2. Sistema de favoritos
3. Comparador de coches
4. Estadísticas del dashboard

**Prioridad 3 (Bueno tener):**
1. Modo oscuro (toggle)
2. Paginación
3. Validaciones de formularios

### 📝 Tipos de Tests

**Tests Unitarios (app/test/BasicTest.java):**
```java
@Test
public void testUserCreation() {
    User user = new User("testuser", "test@test.com", "password123", 
                         "Test User", "123456789", "Test Address", false);
    user.save();
    
    User found = User.find("byEmail", "test@test.com").first();
    assertNotNull(found);
    assertEquals("testuser", found.userName);
}

@Test
public void testCarValidation() {
    Car car = new Car("Tesla", "Model S", 2023, 85000.0, 
                      "670 CV, eléctrico", "/images/tesla.jpg");
    assertTrue(car.precio > 0);
    assertTrue(car.year >= 2000 && car.year <= 2026);
}
```

**Tests Funcionales (app/test/ApplicationTest.java):**
```java
@Test
public void testLoginFlow() {
    // Crear usuario de prueba
    User user = new User("testuser", "test@test.com", "password123",
                         "Test User", "123456789", "Test Address", false);
    user.save();
    
    // Intentar login
    Map<String, String> params = new HashMap<>();
    params.put("email", "test@test.com");
    params.put("password", "password123");
    
    Response response = POST("/UserController/login", params);
    assertIsOk(response);
    assertHeaderEquals("Location", "/Car/catalog", response);
}

@Test
public void testCheckoutProcess() {
    // Crear usuario y coche
    User user = createTestUser();
    Car car = createTestCar();
    
    // Simular añadir al carrito
    Map<String, String> params = new HashMap<>();
    params.put("carId", car.id.toString());
    
    Response response = POST("/OrderController/addToCart", params);
    assertIsOk(response);
    
    // Verificar que está en el carrito
    // ... (continuar con el flujo)
}
```

**Herramienta de Cobertura:**
- Usar Cobertura plugin de Play Framework
- Ejecutar: `play test` y luego `play cobertura:report`
- Verificar que la cobertura sea >= 85%

---

## 10. REGLAS DE CÓDIGO {#reglas-codigo}

### 📜 Estándares de Java

**Nomenclatura:**
- Clases: PascalCase (ej: `UserController`, `Car`)
- Métodos: camelCase (ej: `getUserById`, `processPurchase`)
- Variables: camelCase (ej: `userName`, `totalPrice`)
- Constantes: UPPER_SNAKE_CASE (ej: `MAX_CART_SIZE`)

**Comentarios JavaDoc:**
- **SOLO en métodos críticos** (no en todos)
- **Escribir como estudiantes**, no como IA
- **Ejemplos de buenos comentarios:**

```java
/**
 * Valida que el email tenga formato correcto
 * @param email El email a validar
 * @return true si es válido, false si no
 */
public static boolean isValidEmail(String email) {
    // Patrón regex simple para validar email
    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
    return email.matches(regex);
}

/**
 * Añade un coche al carrito del usuario
 * Si ya hay un coche, lo reemplaza
 */
public static void addToCart(Long carId) {
    // Obtener usuario de la sesión
    Long userId = Long.parseLong(session.get("userId"));
    User user = User.findById(userId);
    
    // Buscar el coche
    Car car = Car.findById(carId);
    if (car == null) {
        renderText("Coche no encontrado");
        return;
    }
    
    // Guardar en sesión (carrito simple)
    session.put("cartCarId", carId.toString());
    viewCart();
}
```

**❌ Comentarios a EVITAR (parecen de IA):**
```java
/**
 * This method implements a sophisticated algorithm to...
 * It leverages the power of...
 * Returns: A comprehensive object containing...
 */
```

**✅ Comentarios BUENOS (parecen de estudiantes):**
```java
/**
 * Busca un coche por su ID
 * Devuelve null si no lo encuentra
 */
```

### 🎯 Manejo de Errores

**Siempre validar antes de guardar:**
```java
public static void register(String userName, String email, String password, 
                           String fullName, String phone, String address) {
    // Validar que no estén vacíos
    if (userName == null || userName.trim().isEmpty()) {
        flash.error("El nombre de usuario es obligatorio");
        render("@register");
    }
    
    // Validar email único
    User existing = User.find("byEmail", email).first();
    if (existing != null) {
        flash.error("Este email ya está registrado");
        render("@register");
    }
    
    // Validar longitud de contraseña
    if (password.length() < 8) {
        flash.error("La contraseña debe tener al menos 8 caracteres");
        render("@register");
    }
    
    // Si todo OK, crear usuario
    User user = new User(userName, email, password, fullName, phone, address, false);
    user.save();
    
    flash.success("Registro exitoso. Bienvenido a BYCar!");
    login(email, password);  // Login automático
}
```

**Try-Catch para operaciones críticas:**
```java
public static void processPayment(Long orderId) {
    try {
        Order order = Order.findById(orderId);
        if (order == null) {
            renderJSON("{\"error\": \"Pedido no encontrado\"}");
            return;
        }
        
        // Cambiar estado
        order.estado = "pagado";
        order.save();
        
        // Intentar enviar email
        sendConfirmationEmail(order);
        
        renderJSON("{\"success\": true, \"orderId\": " + orderId + "}");
        
    } catch (Exception e) {
        Logger.error("Error procesando pago: " + e.getMessage());
        renderJSON("{\"error\": \"Error al procesar el pago\"}");
    }
}
```

### 🔒 Seguridad Básica

**Validar sesión en métodos protegidos:**
```java
public static void profile() {
    // Verificar que el usuario esté logueado
    if (session.get("userId") == null) {
        flash.error("Debes iniciar sesión");
        UserController.login();
        return;
    }
    
    Long userId = Long.parseLong(session.get("userId"));
    User user = User.findById(userId);
    render(user);
}
```

**Verificar permisos de admin:**
```java
public static void dashboard() {
    // Verificar login
    if (session.get("userId") == null) {
        UserController.login();
        return;
    }
    
    // Verificar que sea admin
    Long userId = Long.parseLong(session.get("userId"));
    User user = User.findById(userId);
    
    if (!user.isAdmin) {
        flash.error("No tienes permisos de administrador");
        Application.index();
        return;
    }
    
    // Continuar con la lógica del dashboard
    // ...
}
```

---

## 11. PLAN DE DESARROLLO {#plan-desarrollo}

### 📅 Enfoque: Desarrollo Mixto (Iterativo + Por Capas)

**Fase 0: Configuración (0.5-1 día)**
- [x] Instalar plugins de IntelliJ
- [ ] Configurar application.conf
- [ ] Crear estructura de carpetas
- [ ] Configurar routes básicas

**Fase 1: Modelos y BD (1 día)**
- [ ] Crear User.java
- [ ] Crear Car.java
- [ ] Crear Order.java
- [ ] Método initDB() con 15 coches (2 por marca)
- [ ] Verificar que se crea la BD correctamente

**Fase 2: Autenticación (1-2 días)**
- [ ] UserController: register, login, logout
- [ ] Vistas: login.html, register.html
- [ ] Validaciones de formularios
- [ ] Manejo de sesión
- [ ] Tests de login/registro

**Fase 3: Catálogo Básico (1-2 días)**
- [ ] CarController: listAll, detail
- [ ] Vistas: catalog.html, detail.html
- [ ] Diseño con Bootstrap
- [ ] Responsive design
- [ ] Tests de visualización

**Fase 4: Búsqueda y Filtros (1 día)**
- [ ] CarController: search, filter
- [ ] JavaScript para filtros dinámicos
- [ ] Ordenamiento (precio, año, marca)
- [ ] Paginación (9 coches por página)

**Fase 5: Sistema de Favoritos (0.5 día)**
- [ ] UserController: addToFavorites, removeFromFavorites
- [ ] CarController: getFavorites
- [ ] Vista: favorites.html
- [ ] JavaScript para toggle de favoritos

**Fase 6: Carrito y Compra (2 días)**
- [ ] OrderController: addToCart, viewCart, removeFromCart
- [ ] OrderController: checkout, processPayment
- [ ] Vistas: cart.html, checkout.html, confirmation.html
- [ ] JavaScript de autofill de tarjeta
- [ ] Tests del flujo completo de compra

**Fase 7: Historial de Pedidos (0.5 día)**
- [ ] OrderController: getHistory, viewOrderDetail
- [ ] Vistas: history.html, order-detail.html
- [ ] Línea de tiempo de estados

**Fase 8: Panel de Administración (2-3 días)**
- [ ] AdminController: dashboard
- [ ] AdminController: manageCars (CRUD completo)
- [ ] AdminController: manageOrders (ver, cambiar estado)
- [ ] AdminController: manageUsers (listar, ver)
- [ ] AdminController: getStatistics
- [ ] Vistas de admin (dashboard, manage*)
- [ ] Chart.js para gráficas
- [ ] Tests de funcionalidades admin

**Fase 9: Extras (1-2 días)**
- [ ] Comparador de coches
- [ ] Modo oscuro (toggle)
- [ ] Logo de BYCar
- [ ] Animaciones CSS
- [ ] Pulir diseño general

**Fase 10: Testing Completo (1-2 días)**
- [ ] Escribir todos los tests faltantes
- [ ] Ejecutar Cobertura
- [ ] Llegar a 85-90% de cobertura
- [ ] Corregir bugs encontrados

**Fase 11: Documentación (0.5-1 día)**
- [ ] README.md con instrucciones de instalación
- [ ] MANUAL_USUARIO.md con capturas de pantalla
- [ ] Comentarios JavaDoc en métodos críticos
- [ ] Revisar que no parezcan de IA

**Fase 12: Integración de Email (0.5 día)**
- [ ] Crear webhook en Google Apps Script
- [ ] Integrar en OrderController.sendConfirmationEmail()
- [ ] Probar envío de emails

**Fase 13: Revisión Final y Entrega (0.5 día)**
- [ ] Verificar que todo funciona
- [ ] Ejecutar todos los tests
- [ ] Verificar responsive en móvil/tablet
- [ ] Limpiar código innecesario
- [ ] Preparar para entrega

**TOTAL ESTIMADO: 12-16 días de trabajo**

---

## 12. CRITERIOS DE ÉXITO {#criterios-exito}

### ✅ Checklist de Funcionalidades

**Autenticación:**
- [ ] Registro de clientes funciona
- [ ] Login con email + password funciona
- [ ] Logout funciona
- [ ] Sesión se mantiene entre páginas

**Catálogo:**
- [ ] Se muestran todos los coches
- [ ] Paginación funciona (9 por página)
- [ ] Diseño responsive (móvil, tablet, desktop)
- [ ] Detalle de coche muestra toda la info
- [ ] Imágenes se cargan correctamente

**Búsqueda y Filtros:**
- [ ] Búsqueda por marca/modelo funciona
- [ ] Filtros por precio funcionan
- [ ] Filtros por año funcionan
- [ ] Filtros por combustible funcionan
- [ ] Ordenar por precio/año/marca funciona

**Favoritos:**
- [ ] Añadir a favoritos funciona
- [ ] Quitar de favoritos funciona
- [ ] Ver favoritos muestra solo los guardados
- [ ] Icono de favorito se actualiza (⭐ llena/vacía)

**Compra:**
- [ ] Añadir al carrito funciona (máx 1)
- [ ] Ver carrito muestra el coche
- [ ] Checkout muestra formulario completo
- [ ] Botón "Rellenar automáticamente" genera datos realistas
- [ ] No se puede editar manualmente los campos de tarjeta
- [ ] Confirmar compra crea el pedido
- [ ] Estado cambia a "pagado"
- [ ] Se muestra confirmación
- [ ] Carrito se vacía después de comprar

**Historial:**
- [ ] Se muestran todos los pedidos del usuario
- [ ] Estados tienen colores diferentes
- [ ] Ver detalle muestra toda la info

**Admin - Coches:**
- [ ] Listar coches funciona
- [ ] Crear coche funciona
- [ ] Editar coche funciona
- [ ] Eliminar coche funciona (con validación)

**Admin - Pedidos:**
- [ ] Listar pedidos funciona
- [ ] Cambiar estado de pedido funciona
- [ ] Ver detalle de pedido funciona

**Admin - Dashboard:**
- [ ] Estadísticas se calculan correctamente
- [ ] Gráficas se muestran (Chart.js)
- [ ] Tabla de últimos pedidos funciona

**Extras:**
- [ ] Comparador de coches funciona
- [ ] Modo oscuro funciona (toggle)
- [ ] Logo de BYCar creado y visible

**Calidad:**
- [ ] Tests cubren 85-90% del código
- [ ] No hay errores en consola
- [ ] Código está limpio y organizado
- [ ] Comentarios no parecen de IA
- [ ] README y manual de usuario completos

---

## 📝 NOTAS FINALES PARA EL AGENTE

### 🎯 Tu Misión
Eres un desarrollador full-stack senior con 20 años de experiencia. Tu objetivo es crear una aplicación web profesional, robusta y bien diseñada que supere las expectativas del proyecto de referencia WebPelis.

### 🚀 Mentalidad de Trabajo
- **Calidad sobre rapidez:** Es mejor hacer las cosas bien que rápido
- **Código limpio:** El siguiente desarrollador debe entender tu código fácilmente
- **Probar frecuentemente:** Después de cada funcionalidad, ejecutar y probar
- **Documentar lo crítico:** Solo comentar lo que no es obvio

### 💡 Si encuentras problemas:
1. Leer la documentación de Play Framework 1.5.3
2. Revisar el proyecto WebPelis de referencia
3. Buscar en Stack Overflow
4. Informar al coordinador (vía el intermediario)

### 📞 Comunicación con el Coordinador
- Cada vez que completes una fase, informar
- Si hay dudas sobre una funcionalidad, preguntar
- Si encuentras un bug, reportar con detalle
- Si tienes una sugerencia de mejora, proponerla

---

## 🏁 ESTADO ACTUAL DEL PROYECTO

**Última actualización:** [FECHA]

**Fase actual:** [FASE]

**Completado:**
- [ ] Fase 0: Configuración
- [ ] Fase 1: Modelos y BD
- [ ] Fase 2: Autenticación
- [ ] Fase 3: Catálogo Básico
- [ ] Fase 4: Búsqueda y Filtros
- [ ] Fase 5: Favoritos
- [ ] Fase 6: Carrito y Compra
- [ ] Fase 7: Historial
- [ ] Fase 8: Admin
- [ ] Fase 9: Extras
- [ ] Fase 10: Testing
- [ ] Fase 11: Documentación
- [ ] Fase 12: Email
- [ ] Fase 13: Entrega

**Próximo paso:** [DESCRIPCIÓN]

**Pendiente resolver:** [ISSUES]

---

**FIN DEL DOCUMENTO**

---

Este documento es la **FUENTE ÚNICA DE VERDAD** para el proyecto BYCar. Cualquier duda sobre funcionalidades, diseño, arquitectura o implementación debe responderse consultando este documento primero.

**Versión:** 1.0  
**Creado por:** Project Manager - Coordinador Técnico  
**Para:** Agente de IA - Desarrollador Full-Stack Senior  
**Proyecto:** BYCar - Concesionario Virtual  
**Asignatura:** Projecte d'Enginyeria del Software 2024-2025
