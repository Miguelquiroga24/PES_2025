package controllers;

import play.mvc.*;
import models.Car;
import models.User;
import java.util.*;
import com.google.gson.JsonObject;

// Controlador de favoritos (AJAX/JSON)
public class FavoriteController extends Controller {

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

    // Parsea string de favoritos a Set de IDs
    private static Set<String> parseFavorites(String favoriteCarIds) {
        Set<String> favorites = new LinkedHashSet<>();
        if (favoriteCarIds != null && !favoriteCarIds.trim().isEmpty()) {
            String[] ids = favoriteCarIds.split(",");
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    favorites.add(id.trim());
                }
            }
        }
        return favorites;
    }

    // Convierte Set a string separado por comas
    private static String joinFavorites(Set<String> favorites) {
        return String.join(",", favorites);
    }

    // Pagina principal de favoritos
    public static void index() {
        User user = getCurrentUser();
        if (user == null) {
            flash.error("Debes iniciar sesión para ver tus favoritos");
            UserController.login();
            return; // Nunca se alcanza pero evita warning del compilador
        }

        List<Car> favoriteCars = new ArrayList<>();
        Set<String> favoriteIds = parseFavorites(user.favoriteCarIds);

        for (String idStr : favoriteIds) {
            try {
                Long carId = Long.parseLong(idStr);
                Car car = Car.findById(carId);
                if (car != null) {
                    favoriteCars.add(car);
                }
            } catch (NumberFormatException e) {
                // Ignorar IDs inválidos
            }
        }

        int totalFavorites = favoriteCars.size();
        render(favoriteCars, totalFavorites);
    }

    // Añadir a favoritos (AJAX)
    public static void add(Long carId) {
        JsonObject response = new JsonObject();

        // Verificar sesión
        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("success", false);
            response.addProperty("error", "Sesión no válida");
            response.addProperty("requireLogin", true);
            renderJSON(response.toString());
        }

        // Verificar que el coche existe
        if (carId == null) {
            response.addProperty("success", false);
            response.addProperty("error", "ID de coche no válido");
            renderJSON(response.toString());
        }

        Car car = Car.findById(carId);
        if (car == null) {
            response.addProperty("success", false);
            response.addProperty("error", "Coche no encontrado");
            renderJSON(response.toString());
        }

        try {
            // Parsear favoritos actuales
            Set<String> favorites = parseFavorites(user.favoriteCarIds);

            // Añadir nuevo favorito
            favorites.add(carId.toString());

            // Guardar en BD
            user.favoriteCarIds = joinFavorites(favorites);
            user.save();

            response.addProperty("success", true);
            response.addProperty("count", favorites.size());
            response.addProperty("message", "Añadido a favoritos");
        } catch (Exception e) {
            response.addProperty("success", false);
            response.addProperty("error", "Error al guardar favorito");
        }

        renderJSON(response.toString());
    }

    // Quitar de favoritos (AJAX)
    public static void remove(Long carId) {
        JsonObject response = new JsonObject();

        // Verificar sesión
        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("success", false);
            response.addProperty("error", "Sesión no válida");
            response.addProperty("requireLogin", true);
            renderJSON(response.toString());
        }

        // Verificar ID válido
        if (carId == null) {
            response.addProperty("success", false);
            response.addProperty("error", "ID de coche no válido");
            renderJSON(response.toString());
        }

        try {
            // Parsear favoritos actuales
            Set<String> favorites = parseFavorites(user.favoriteCarIds);

            // Quitar favorito
            favorites.remove(carId.toString());

            // Guardar en BD
            user.favoriteCarIds = joinFavorites(favorites);
            user.save();

            response.addProperty("success", true);
            response.addProperty("count", favorites.size());
            response.addProperty("message", "Eliminado de favoritos");
        } catch (Exception e) {
            response.addProperty("success", false);
            response.addProperty("error", "Error al eliminar favorito");
        }

        renderJSON(response.toString());
    }

    // Comprobar si es favorito (AJAX)
    public static void isFavorite(Long carId) {
        JsonObject response = new JsonObject();

        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("isFavorite", false);
            response.addProperty("loggedIn", false);
            renderJSON(response.toString());
        }

        if (carId == null) {
            response.addProperty("isFavorite", false);
            response.addProperty("loggedIn", true);
            renderJSON(response.toString());
        }

        Set<String> favorites = parseFavorites(user.favoriteCarIds);
        boolean isFav = favorites.contains(carId.toString());

        response.addProperty("isFavorite", isFav);
        response.addProperty("loggedIn", true);
        renderJSON(response.toString());
    }

    // Contar favoritos (AJAX)
    public static void count() {
        JsonObject response = new JsonObject();

        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("count", 0);
            response.addProperty("loggedIn", false);
            renderJSON(response.toString());
        }

        Set<String> favorites = parseFavorites(user.favoriteCarIds);
        response.addProperty("count", favorites.size());
        response.addProperty("loggedIn", true);
        renderJSON(response.toString());
    }

    // Obtener IDs de favoritos (AJAX)
    public static void ids() {
        JsonObject response = new JsonObject();

        User user = getCurrentUser();
        if (user == null) {
            response.addProperty("loggedIn", false);
            response.add("ids", new com.google.gson.JsonArray());
            renderJSON(response.toString());
        }

        Set<String> favorites = parseFavorites(user.favoriteCarIds);
        com.google.gson.JsonArray idsArray = new com.google.gson.JsonArray();
        for (String id : favorites) {
            try {
                idsArray.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                // Ignorar IDs inválidos
            }
        }

        response.addProperty("loggedIn", true);
        response.add("ids", idsArray);
        renderJSON(response.toString());
    }
}
