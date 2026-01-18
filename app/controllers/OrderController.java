package controllers;

import play.mvc.*;
import play.Logger;
import models.Car;
import models.User;
import models.Order;
import com.google.gson.JsonObject;
import java.util.regex.Pattern;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

// Controlador del carrito y checkout
public class OrderController extends Controller {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String EMAIL_WEBHOOK_URL = "https://script.google.com/macros/s/AKfycbz_raa_RcZDqDiAQn3KtGNHRLqAftNBVqZdxWIwLzeOd7KTOFUJRPyNYGlZ-ywm5eeLow/exec";

    // Formatea precio como moneda
    private static String formatCurrency(Double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return formatter.format(amount) + " €";
    }

    // Envia email de confirmacion via webhook
    private static void enviarEmailConfirmacion(Order order) {
        try {
            // Formatear precio
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String precioFormateado = df.format(order.car.precio).replace(",", ".");

            // Formatear fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(order.fechaCreacion);

            // Construir JSON manualmente (sin dependencias externas)
            String jsonData = "{"
                + "\"email\":\"" + order.emailConfirmacion + "\","
                + "\"nombreCliente\":\"" + order.fullName + "\","
                + "\"numeroPedido\":\"" + order.id + "\","
                + "\"marcaCoche\":\"" + order.car.marca + "\","
                + "\"modeloCoche\":\"" + order.car.modelo + "\","
                + "\"precioCoche\":\"" + precioFormateado + "\","
                + "\"fechaCompra\":\"" + fechaFormateada + "\","
                + "\"direccion\":\"" + order.address.replace("\"", "'") + "\""
                + "}";

            // Crear conexion HTTP
            URL url = new URL(EMAIL_WEBHOOK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);  // 10 segundos timeout
            conn.setReadTimeout(10000);

            // Enviar datos
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Logger.info("Email de confirmacion enviado para pedido #%s a %s",
                           order.id, order.emailConfirmacion);
            } else {
                Logger.warn("Webhook respondio con codigo %d para pedido #%s",
                           responseCode, order.id);
            }

            conn.disconnect();

        } catch (Exception e) {
            // Log del error pero NO interrumpir el flujo de compra
            Logger.error("Error enviando email de confirmacion para pedido #%s: %s",
                        order.id, e.getMessage());
        }
    }

    // Obtiene el usuario de la sesion
    private static User getCurrentUser() {
        String userId = session.get("userId");
        if (userId != null) {
            try {
                return User.findById(Long.parseLong(userId));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Ver carrito
    public static void viewCart() {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión para ver tu carrito");
            UserController.login();
            return;
        }

        String cartCarId = session.get("cartCarId");
        Car car = null;

        if (cartCarId != null && !cartCarId.isEmpty()) {
            try {
                car = Car.findById(Long.parseLong(cartCarId));
                // Si el coche ya no existe en BD, limpiar sesión
                if (car == null) {
                    session.remove("cartCarId");
                    flash.error("El vehículo ya no está disponible");
                }
            } catch (NumberFormatException e) {
                session.remove("cartCarId");
            }
        }

        render(car);
    }

    // Añadir al carrito (AJAX)
    public static void addToCart(Long carId, Boolean force) {
        JsonObject response = new JsonObject();

        // Verificar sesión
        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("success", false);
            response.addProperty("error", "Debes iniciar sesión");
            response.addProperty("requireLogin", true);
            renderJSON(response.toString());
        }

        // Verificar que el coche existe
        if (carId == null) {
            response.addProperty("success", false);
            response.addProperty("error", "ID de vehículo no válido");
            renderJSON(response.toString());
        }

        Car car = Car.findById(carId);
        if (car == null) {
            response.addProperty("success", false);
            response.addProperty("error", "Vehículo no encontrado");
            renderJSON(response.toString());
        }

        // Verificar si ya hay un coche en el carrito
        String existingCarId = session.get("cartCarId");

        if (existingCarId != null && !existingCarId.isEmpty()) {
            // Si es el mismo coche
            if (existingCarId.equals(carId.toString())) {
                response.addProperty("success", false);
                response.addProperty("alreadyInCart", true);
                response.addProperty("message", "Este vehículo ya está en tu carrito");
                renderJSON(response.toString());
            }

            // Si es diferente y no se fuerza el reemplazo
            if (force == null || !force) {
                Car currentCar = Car.findById(Long.parseLong(existingCarId));
                response.addProperty("success", false);
                response.addProperty("needsConfirmation", true);
                response.addProperty("currentCarId", existingCarId);
                if (currentCar != null) {
                    response.addProperty("currentCarName", currentCar.marca + " " + currentCar.modelo);
                }
                response.addProperty("newCarName", car.marca + " " + car.modelo);
                renderJSON(response.toString());
            }
        }

        // Añadir al carrito
        session.put("cartCarId", carId.toString());

        response.addProperty("success", true);
        response.addProperty("message", "Añadido al carrito");
        response.addProperty("carName", car.marca + " " + car.modelo);
        renderJSON(response.toString());
    }

    // Quitar del carrito
    public static void removeFromCart() {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión");
            UserController.login();
            return;
        }

        session.remove("cartCarId");
        flash.success("Vehículo eliminado del carrito");
        viewCart();
    }

    // Comprobar si hay algo en el carrito (AJAX)
    public static void hasCart() {
        JsonObject response = new JsonObject();

        String cartCarId = session.get("cartCarId");
        boolean hasCart = cartCarId != null && !cartCarId.isEmpty();

        response.addProperty("hasCart", hasCart);

        if (hasCart) {
            try {
                Car car = Car.findById(Long.parseLong(cartCarId));
                if (car != null) {
                    response.addProperty("carId", car.id);
                    response.addProperty("carName", car.marca + " " + car.modelo);
                } else {
                    // Coche eliminado de BD
                    session.remove("cartCarId");
                    response.addProperty("hasCart", false);
                }
            } catch (NumberFormatException e) {
                session.remove("cartCarId");
                response.addProperty("hasCart", false);
            }
        }

        renderJSON(response.toString());
    }

    // Pagina de checkout
    public static void checkout() {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión para continuar");
            UserController.login();
            return;
        }

        String cartCarId = session.get("cartCarId");
        if (cartCarId == null || cartCarId.isEmpty()) {
            flash.error("Tu carrito está vacío");
            viewCart();
            return;
        }

        Car car = null;
        try {
            car = Car.findById(Long.parseLong(cartCarId));
        } catch (NumberFormatException e) {
            session.remove("cartCarId");
            flash.error("Error al cargar el vehículo");
            viewCart();
            return;
        }

        if (car == null) {
            session.remove("cartCarId");
            flash.error("El vehículo ya no está disponible");
            viewCart();
            return;
        }

        render(car, user);
    }

    // Procesar pago y crear orden
    public static void processPayment(String email, String fullName,
                                      String phone, String address,
                                      String cardNumber, String cardHolder,
                                      String cardExpiry, String cardCVV) {

        // Verificar sesión
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión para continuar");
            UserController.login();
            return;
        }

        // Verificar carrito
        String cartCarId = session.get("cartCarId");
        if (cartCarId == null || cartCarId.isEmpty()) {
            flash.error("Tu carrito está vacío");
            viewCart();
            return;
        }

        // Buscar coche
        Car car = null;
        try {
            car = Car.findById(Long.parseLong(cartCarId));
        } catch (NumberFormatException e) {
            session.remove("cartCarId");
            flash.error("Error al procesar el vehículo");
            viewCart();
            return;
        }

        if (car == null) {
            session.remove("cartCarId");
            flash.error("El vehículo ya no está disponible");
            viewCart();
            return;
        }

        // Validaciones de campos personales
        if (email == null || email.trim().isEmpty()) {
            flash.error("El email es obligatorio");
            render("@checkout", car, user);
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            flash.error("El formato del email no es válido");
            render("@checkout", car, user);
        }

        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() < 3) {
            flash.error("El nombre debe tener al menos 3 caracteres");
            render("@checkout", car, user);
        }

        if (phone == null || phone.trim().isEmpty()) {
            flash.error("El teléfono es obligatorio");
            render("@checkout", car, user);
        }

        if (address == null || address.trim().isEmpty()) {
            flash.error("La dirección es obligatoria");
            render("@checkout", car, user);
        }

        // Validaciones de campos de tarjeta
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            flash.error("Debes rellenar los datos de la tarjeta usando el botón automático");
            render("@checkout", car, user);
        }

        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            flash.error("El titular de la tarjeta es obligatorio");
            render("@checkout", car, user);
        }

        if (cardExpiry == null || cardExpiry.trim().isEmpty()) {
            flash.error("La fecha de expiración es obligatoria");
            render("@checkout", car, user);
        }

        if (cardCVV == null || cardCVV.trim().isEmpty()) {
            flash.error("El CVV es obligatorio");
            render("@checkout", car, user);
        }

        // Validar saldo suficiente
        Double precioCoche = car.precio;
        if (!user.tieneSaldoSuficiente(precioCoche)) {
            Double faltante = precioCoche - user.saldo;
            flash.error("Saldo insuficiente. Disponible: " + formatCurrency(user.saldo) +
                       ", Necesitas: " + formatCurrency(faltante) + " más");
            render("@checkout", car, user);
        }

        // Guardar saldo anterior para mostrar en confirmación
        Double saldoAnterior = user.saldo;

        // Crear orden
        try {
            Order order = new Order(user, car, email.trim(), fullName.trim(),
                                    phone.trim(), address.trim());
            order.save();

            // Descontar saldo del usuario
            user.descontarSaldo(precioCoche);

            // Cambiar estado a pagado
            order.estado = "pagado";
            order.save();

            // Enviar email de confirmacion via webhook (no bloquea si falla)
            enviarEmailConfirmacion(order);

            // Limpiar carrito
            session.remove("cartCarId");

            // Guardar saldo anterior en sesión para mostrar en confirmación
            session.put("saldoAnterior", saldoAnterior.toString());

            // Log del envío de email (placeholder)
            Logger.info("Orden #%d creada. Saldo descontado: %s. Nuevo saldo: %s. Email enviado a: %s",
                       order.id, formatCurrency(precioCoche), formatCurrency(user.saldo), email);

            // Redirigir a confirmación
            confirmation(order.id);

        } catch (Exception e) {
            Logger.error("Error al procesar el pago: %s", e.getMessage());
            flash.error("Se ha producido un error al procesar el pago. Inténtalo de nuevo.");
            render("@checkout", car, user);
        }
    }

    // Pagina de confirmacion de compra
    public static void confirmation(Long orderId) {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión");
            UserController.login();
            return;
        }

        if (orderId == null) {
            flash.error("Orden no encontrada");
            Application.index();
            return;
        }

        Order order = Order.findById(orderId);
        if (order == null) {
            flash.error("Orden no encontrada");
            Application.index();
            return;
        }

        // Verificar que la orden pertenece al usuario
        if (!order.user.id.equals(user.id)) {
            flash.error("No tienes permiso para ver esta orden");
            Application.index();
            return;
        }

        // Obtener saldo anterior de la sesión (si existe)
        String saldoAnteriorStr = session.get("saldoAnterior");
        Double saldoAnterior = null;
        if (saldoAnteriorStr != null) {
            try {
                saldoAnterior = Double.parseDouble(saldoAnteriorStr);
                session.remove("saldoAnterior");
            } catch (NumberFormatException e) {
                // Ignorar
            }
        }

        render(order, user, saldoAnterior);
    }

    // Historial de pedidos
    public static void history() {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión para ver tu historial");
            UserController.login();
            return;
        }

        List<Order> orders = Order.find("user = ?1 ORDER BY fechaCreacion DESC", user).fetch();
        render(orders, user);
    }

    // Ver detalle de un pedido
    public static void orderDetail(Long orderId) {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión");
            UserController.login();
            return;
        }

        if (orderId == null) {
            flash.error("Pedido no encontrado");
            history();
            return;
        }

        Order order = Order.findById(orderId);
        if (order == null) {
            flash.error("Pedido no encontrado");
            history();
            return;
        }

        // CRÍTICO: Verificar que el pedido pertenece al usuario
        if (!order.user.id.equals(user.id)) {
            flash.error("No tienes permiso para ver este pedido");
            history();
            return;
        }

        render(order, user);
    }

    // Confirmar recogida del vehiculo
    public static void confirmDelivery(Long orderId) {
        // Verificar que el usuario esté logueado
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesion");
            UserController.login();
            return;
        }

        // Buscar el pedido
        if (orderId == null) {
            flash.error("Pedido no encontrado");
            history();
            return;
        }

        Order order = Order.findById(orderId);
        if (order == null) {
            flash.error("Pedido no encontrado");
            history();
            return;
        }

        // SEGURIDAD: Verificar que el pedido pertenece al usuario logueado
        if (!order.user.id.equals(user.id)) {
            flash.error("No tienes permiso para modificar este pedido");
            history();
            return;
        }

        // VALIDACION: Solo se puede confirmar si el estado es 'pagado'
        if (!"pagado".equals(order.estado)) {
            flash.error("Solo puedes confirmar la recogida de pedidos pagados");
            history();
            return;
        }

        // Cambiar estado a 'entregado'
        order.estado = "entregado";
        order.save();

        Logger.info("Pedido #%d: Cliente %s confirmo la recogida del vehiculo %s %s",
                   order.id, user.userName, order.car.marca, order.car.modelo);

        flash.success("Has confirmado la recogida de tu " + order.car.marca + " " + order.car.modelo);
        history();
    }
}
