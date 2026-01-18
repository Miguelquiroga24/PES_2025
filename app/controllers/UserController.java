package controllers;

import play.mvc.*;
import play.Logger;
import models.User;
import models.Order;
import java.util.regex.Pattern;
import java.util.List;

// Controlador de usuarios (registro, login, perfil)
public class UserController extends Controller {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Formulario de registro
    public static void register() {
        render();
    }

    // Crear nuevo usuario
    public static void createUser(String userName, String email, String password,
                                   String fullName, String phone, String address) {

        // Validar que no estén vacíos
        if (userName == null || userName.trim().isEmpty()) {
            flash.error("El nombre de usuario es obligatorio");
            params.flash();
            register();
        }

        if (email == null || email.trim().isEmpty()) {
            flash.error("El email es obligatorio");
            params.flash();
            register();
        }

        if (password == null || password.trim().isEmpty()) {
            flash.error("La contraseña es obligatoria");
            params.flash();
            register();
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            flash.error("El nombre completo es obligatorio");
            params.flash();
            register();
        }

        if (phone == null || phone.trim().isEmpty()) {
            flash.error("El teléfono es obligatorio");
            params.flash();
            register();
        }

        if (address == null || address.trim().isEmpty()) {
            flash.error("La dirección es obligatoria");
            params.flash();
            register();
        }

        // Validar longitud mínima de userName
        if (userName.trim().length() < 3) {
            flash.error("El nombre de usuario debe tener al menos 3 caracteres");
            params.flash();
            register();
        }

        // Validar formato de email
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            flash.error("El formato del email no es válido");
            params.flash();
            register();
        }

        // Validar longitud de contraseña
        if (password.length() < 8) {
            flash.error("La contraseña debe tener al menos 8 caracteres");
            params.flash();
            register();
        }

        // Verificar que userName sea único
        User existingUserName = User.find("byUserName", userName).first();
        if (existingUserName != null) {
            flash.error("Este nombre de usuario ya está en uso");
            params.flash();
            register();
        }

        // Verificar que email sea único
        User existingEmail = User.find("byEmail", email).first();
        if (existingEmail != null) {
            flash.error("Este email ya está registrado");
            params.flash();
            register();
        }

        // Si todo está OK, crear el usuario
        User newUser = new User(userName, email, password, fullName, phone, address, false);
        newUser.save();

        // Login automático después del registro
        session.put("userId", newUser.id);
        session.put("userName", newUser.userName);
        session.put("isAdmin", "false");

        flash.success("¡Registro exitoso! Bienvenido a BYCar, " + newUser.fullName);
        Application.index();
    }

    // Formulario de login
    public static void login() {
        render();
    }

    // Procesar login
    public static void authenticate(String email, String password) {

        // Validar que no estén vacíos
        if (email == null || email.trim().isEmpty()) {
            flash.error("El email es obligatorio");
            login();
        }

        if (password == null || password.trim().isEmpty()) {
            flash.error("La contraseña es obligatoria");
            login();
        }

        // Buscar usuario por email
        User user = User.find("byEmail", email).first();

        // Verificar que existe
        if (user == null) {
            flash.error("Email o contraseña incorrectos");
            login();
        }

        // Verificar contraseña
        if (!user.password.equals(password)) {
            flash.error("Email o contraseña incorrectos");
            login();
        }

        // Login exitoso - guardar en sesión
        session.put("userId", user.id);
        session.put("userName", user.userName);
        session.put("isAdmin", String.valueOf(user.isAdmin));

        flash.success("Bienvenido de nuevo, " + user.fullName);

        // Redirigir según el tipo de usuario
        if (user.isAdmin) {
            // TODO: Redirigir a AdminController.dashboard() cuando exista
            Application.index();
        } else {
            // TODO: Redirigir a CarController.catalog() cuando exista
            Application.index();
        }
    }

    // Cerrar sesion
    public static void logout() {
        session.clear();
        flash.success("Has cerrado sesión correctamente");
        Application.index();
    }

    // Ver perfil
    public static void profile() {

        // Verificar que el usuario esté logueado
        if (session.get("userId") == null) {
            flash.error("Debes iniciar sesión para ver tu perfil");
            login();
        }

        // Obtener el usuario de la BD
        Long userId = Long.parseLong(session.get("userId"));
        User user = User.findById(userId);

        if (user == null) {
            flash.error("Usuario no encontrado");
            session.clear();
            login();
        }

        render(user);
    }

    // Actualizar perfil (nombre, telefono, direccion)
    public static void updateProfile(String fullName, String phone, String address) {

        // Verificar que el usuario esté logueado
        if (session.get("userId") == null) {
            flash.error("Debes iniciar sesión");
            login();
        }

        // Validar que no estén vacíos
        if (fullName == null || fullName.trim().isEmpty()) {
            flash.error("El nombre completo es obligatorio");
            profile();
        }

        if (phone == null || phone.trim().isEmpty()) {
            flash.error("El teléfono es obligatorio");
            profile();
        }

        if (address == null || address.trim().isEmpty()) {
            flash.error("La dirección es obligatoria");
            profile();
        }

        // Obtener el usuario
        Long userId = Long.parseLong(session.get("userId"));
        User user = User.findById(userId);

        if (user == null) {
            flash.error("Usuario no encontrado");
            session.clear();
            login();
        }

        // Actualizar solo los campos permitidos
        user.fullName = fullName.trim();
        user.phone = phone.trim();
        user.address = address.trim();
        user.save();

        flash.success("Perfil actualizado correctamente");
        profile();
    }

    // Pagina de recarga de saldo
    public static void recargarSaldo() {
        // Verificar que el usuario esté logueado
        if (session.get("userId") == null) {
            flash.error("Debes iniciar sesión para recargar saldo");
            login();
            return;
        }

        // Obtener el usuario de la BD
        Long userId = Long.parseLong(session.get("userId"));
        User user = User.findById(userId);

        if (user == null) {
            flash.error("Usuario no encontrado");
            session.clear();
            login();
            return;
        }

        render(user);
    }

    // Procesar recarga de saldo
    public static void processRecargarSaldo(Double monto) {
        // Verificar que el usuario esté logueado
        if (session.get("userId") == null) {
            flash.error("Debes iniciar sesión");
            login();
            return;
        }

        // Obtener el usuario
        Long userId = Long.parseLong(session.get("userId"));
        User user = User.findById(userId);

        if (user == null) {
            flash.error("Usuario no encontrado");
            session.clear();
            login();
            return;
        }

        // Validar monto
        if (monto == null || monto <= 0) {
            flash.error("El monto debe ser mayor a 0");
            recargarSaldo();
            return;
        }

        if (monto > 1000000) {
            flash.error("El monto máximo por recarga es 1.000.000 €");
            recargarSaldo();
            return;
        }

        // Recargar saldo
        user.recargarSaldo(monto);

        // Formatear montos para mensaje
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.GERMAN);
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("###,###", symbols);

        flash.success("Saldo recargado: +" + formatter.format(monto) + " €. Nuevo saldo: " + formatter.format(user.saldo) + " €");
        profile();
    }

    // Eliminar cuenta de usuario
    public static void deleteAccount(String confirmText) {
        // Verificar que el usuario este logueado
        if (session.get("userId") == null) {
            flash.error("Debes iniciar sesion");
            login();
            return;
        }

        // Obtener el usuario
        Long userId = Long.parseLong(session.get("userId"));
        User user = User.findById(userId);

        if (user == null) {
            flash.error("Usuario no encontrado");
            session.clear();
            login();
            return;
        }

        // SEGURIDAD: Los administradores no pueden eliminar su cuenta
        if (user.isAdmin) {
            flash.error("Los administradores no pueden eliminar su cuenta");
            profile();
            return;
        }

        // Verificar que el texto de confirmacion sea exactamente "ELIMINAR"
        if (confirmText == null || !confirmText.equals("ELIMINAR")) {
            flash.error("Debes escribir ELIMINAR para confirmar");
            profile();
            return;
        }

        // Guardar datos para el log antes de eliminar
        String userName = user.userName;
        String email = user.email;

        try {
            // Eliminar todos los pedidos del usuario
            List<Order> userOrders = Order.find("user = ?1", user).fetch();
            for (Order order : userOrders) {
                order.delete();
            }

            // Eliminar el usuario
            user.delete();

            // Limpiar sesion
            session.clear();

            Logger.info("Cuenta eliminada: usuario=%s, email=%s", userName, email);

            flash.success("Tu cuenta ha sido eliminada permanentemente. Lamentamos verte partir.");
            Application.index();

        } catch (Exception e) {
            Logger.error("Error al eliminar cuenta de usuario %s: %s", userName, e.getMessage());
            flash.error("Error al eliminar la cuenta. Intentalo de nuevo.");
            profile();
        }
    }
}
