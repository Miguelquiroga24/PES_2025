package controllers;

import play.mvc.*;
import models.Car;
import java.util.List;
import java.util.ArrayList;

// Controlador del catalogo de coches
public class CarController extends Controller {

    private static final int PAGE_SIZE = 9;

    // Catalogo con paginacion, busqueda y filtros
    public static void catalog(Integer page,
                               String search,
                               String marca,
                               Double precioMin,
                               Double precioMax,
                               Integer year,
                               String combustible,
                               String transmision,
                               String sortBy,
                               String sortOrder) {

        // Validar pagina
        if (page == null || page < 1) {
            page = 1;
        }

        // Construir query dinamicamente con parametros posicionales
        StringBuilder whereClause = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        // Busqueda por texto (marca o modelo)
        if (search != null && !search.trim().isEmpty()) {
            whereClause.append(" AND (LOWER(marca) LIKE ?").append(paramIndex)
                       .append(" OR LOWER(modelo) LIKE ?").append(paramIndex + 1).append(")");
            String searchPattern = "%" + search.toLowerCase().trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            paramIndex += 2;
        }

        // Filtro por marca (puede ser múltiple, separado por comas)
        String[] marcasSeleccionadas = null;
        if (marca != null && !marca.trim().isEmpty()) {
            marcasSeleccionadas = marca.split(",");
            if (marcasSeleccionadas.length == 1) {
                whereClause.append(" AND marca = ?").append(paramIndex);
                params.add(marcasSeleccionadas[0]);
                paramIndex++;
            } else {
                whereClause.append(" AND marca IN (");
                for (int i = 0; i < marcasSeleccionadas.length; i++) {
                    if (i > 0) whereClause.append(", ");
                    whereClause.append("?").append(paramIndex);
                    params.add(marcasSeleccionadas[i]);
                    paramIndex++;
                }
                whereClause.append(")");
            }
        }

        // Filtro por rango de precio
        if (precioMin != null) {
            whereClause.append(" AND precio >= ?").append(paramIndex);
            params.add(precioMin);
            paramIndex++;
        }
        if (precioMax != null) {
            whereClause.append(" AND precio <= ?").append(paramIndex);
            params.add(precioMax);
            paramIndex++;
        }

        // Filtro por ano
        if (year != null) {
            whereClause.append(" AND year = ?").append(paramIndex);
            params.add(year);
            paramIndex++;
        }

        // Filtro por combustible
        if (combustible != null && !combustible.trim().isEmpty()) {
            whereClause.append(" AND combustible = ?").append(paramIndex);
            params.add(combustible);
            paramIndex++;
        }

        // Filtro por transmision
        if (transmision != null && !transmision.trim().isEmpty()) {
            whereClause.append(" AND transmision = ?").append(paramIndex);
            params.add(transmision);
            paramIndex++;
        }

        // Ordenamiento
        String orderBy = "id DESC";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String order = (sortOrder != null && sortOrder.equalsIgnoreCase("asc")) ? "ASC" : "DESC";
            switch (sortBy) {
                case "precio":
                    orderBy = "precio " + order;
                    break;
                case "year":
                    orderBy = "year " + order;
                    break;
                case "marca":
                    orderBy = "marca " + order + ", modelo " + order;
                    break;
                default:
                    orderBy = "id DESC";
            }
        }

        // Contar total de resultados filtrados
        long totalCars = Car.count(whereClause.toString(), params.toArray());

        // Calcular paginacion
        int totalPages = (int) Math.ceil((double) totalCars / PAGE_SIZE);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        // Obtener coches filtrados y paginados
        int offset = (page - 1) * PAGE_SIZE;
        String fullQuery = whereClause.toString() + " ORDER BY " + orderBy;
        List<Car> cars = Car.find(fullQuery, params.toArray()).from(offset).fetch(PAGE_SIZE);

        // Obtener valores unicos para los filtros (sin filtrar)
        List<String> marcasDisponibles = Car.find("SELECT DISTINCT c.marca FROM Car c ORDER BY c.marca").fetch();
        List<Integer> yearsDisponibles = Car.find("SELECT DISTINCT c.year FROM Car c ORDER BY c.year DESC").fetch();
        List<String> combustiblesDisponibles = Car.find("SELECT DISTINCT c.combustible FROM Car c WHERE c.combustible IS NOT NULL ORDER BY c.combustible").fetch();
        List<String> transmisionesDisponibles = Car.find("SELECT DISTINCT c.transmision FROM Car c WHERE c.transmision IS NOT NULL ORDER BY c.transmision").fetch();

        // Obtener precio minimo y maximo del catalogo completo
        Double precioMinCatalogo = Car.find("SELECT MIN(c.precio) FROM Car c").first();
        Double precioMaxCatalogo = Car.find("SELECT MAX(c.precio) FROM Car c").first();

        // Renderizar con todos los datos
        render(cars, page, totalPages, totalCars,
               search, marca, precioMin, precioMax, year, combustible, transmision, sortBy, sortOrder,
               marcasDisponibles, yearsDisponibles, combustiblesDisponibles, transmisionesDisponibles,
               precioMinCatalogo, precioMaxCatalogo);
    }

    // Comparador de coches (2-3 vehiculos lado a lado)
    public static void compare(String carIds) {
        // Validar que se recibieron IDs
        if (carIds == null || carIds.trim().isEmpty()) {
            flash.error("Selecciona al menos 2 vehículos para comparar");
            catalog(1, null, null, null, null, null, null, null, null, null);
            return;
        }

        // Parsear IDs separados por coma
        String[] idStrings = carIds.split(",");

        // Validar cantidad (minimo 2, maximo 3)
        if (idStrings.length < 2) {
            flash.error("Selecciona al menos 2 vehículos para comparar");
            catalog(1, null, null, null, null, null, null, null, null, null);
            return;
        }
        if (idStrings.length > 3) {
            flash.error("Máximo 3 vehículos para comparar");
            catalog(1, null, null, null, null, null, null, null, null, null);
            return;
        }

        // Buscar los coches por ID
        List<Car> carsToCompare = new ArrayList<>();
        for (String idStr : idStrings) {
            try {
                Long carId = Long.parseLong(idStr.trim());
                Car car = Car.findById(carId);
                if (car != null) {
                    carsToCompare.add(car);
                }
            } catch (NumberFormatException e) {
                // Ignorar IDs invalidos
            }
        }

        // Validar que se encontraron suficientes coches
        if (carsToCompare.size() < 2) {
            flash.error("No se encontraron suficientes vehículos para comparar");
            catalog(1, null, null, null, null, null, null, null, null, null);
            return;
        }

        // Calcular precios para resaltar diferencias
        Double precioMin = carsToCompare.stream().mapToDouble(c -> c.precio).min().orElse(0);
        Double precioMax = carsToCompare.stream().mapToDouble(c -> c.precio).max().orElse(0);

        render(carsToCompare, precioMin, precioMax, carIds);
    }

    // Detalle de un coche
    public static void detail(Long id, Integer page) {
        // Si page es null, usar 1 por defecto
        if (page == null || page < 1) {
            page = 1;
        }

        // Validar que el id no sea null
        if (id == null) {
            flash.error("ID de vehiculo no valido");
            catalog(page, null, null, null, null, null, null, null, null, null);
        }

        // Buscar el coche por ID
        Car car = Car.findById(id);

        // Si no existe, mostrar error y volver al catalogo
        if (car == null) {
            flash.error("Vehiculo no encontrado");
            catalog(page, null, null, null, null, null, null, null, null, null);
        }

        // Renderizar la vista de detalle con la pagina
        render(car, page);
    }
}
