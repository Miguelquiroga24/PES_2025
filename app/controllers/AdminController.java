package controllers;

import play.mvc.*;
import play.Logger;
import models.Car;
import models.User;
import models.Order;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

// Controlador del panel de administracion
public class AdminController extends Controller {

    // Verificar que sea admin antes de cada accion
    @Before
    static void checkAdmin() {
        String userId = session.get("userId");
        if (userId == null) {
            flash.error("Debes iniciar sesión");
            UserController.login();
        }

        try {
            User user = User.findById(Long.parseLong(userId));
            if (user == null || !user.isAdmin) {
                flash.error("No tienes permisos de administrador");
                Application.index();
            }
            // Pasar usuario a las vistas
            renderArgs.put("currentUser", user);
        } catch (NumberFormatException e) {
            flash.error("Sesión inválida");
            UserController.login();
        }
    }

    // Dashboard principal con estadisticas
    public static void dashboard() {
        // === ESTADÍSTICAS GENERALES ===
        Double totalVentas = calcularTotalVentas();
        long totalOrdenes = Order.count();
        long totalClientes = User.count("isAdmin = ?1", false);
        long totalCoches = Car.count();

        // === ÚLTIMAS 10 ÓRDENES ===
        List<Order> ultimasOrdenes = Order.find("ORDER BY fechaCreacion DESC").fetch(10);

        // === COCHES MÁS VENDIDOS (Top 5) ===
        List<Map<String, Object>> cochesMasVendidos = getCochesMasVendidos();

        // === DATOS PARA GRÁFICAS ===
        Map<String, Double> ventasPorMarca = getVentasPorMarca();
        Map<String, Double> ventasPorMes = getVentasPorMes();
        Map<String, Long> distribucionEstados = getDistribucionEstados();

        render(totalVentas, totalOrdenes, totalClientes, totalCoches,
               ultimasOrdenes, cochesMasVendidos,
               ventasPorMarca, ventasPorMes, distribucionEstados);
    }

    // Suma el precio de todas las ordenes pagadas
    private static Double calcularTotalVentas() {
        try {
            List<Order> ordenesPagadas = Order.find("estado = ?1", "pagado").fetch();
            Double total = 0.0;
            for (Order order : ordenesPagadas) {
                if (order.car != null) {
                    total += order.car.precio;
                }
            }
            return total;
        } catch (Exception e) {
            Logger.error("Error calculando total ventas: %s", e.getMessage());
            return 0.0;
        }
    }


    private static List<Map<String, Object>> getCochesMasVendidos() {
        List<Map<String, Object>> resultado = new ArrayList<>();

        try {
            // Obtener todas las órdenes pagadas
            List<Order> ordenes = Order.find("estado = ?1", "pagado").fetch();

            // Contar ventas por coche
            Map<Long, Integer> ventasPorCoche = new HashMap<>();
            Map<Long, Car> coches = new HashMap<>();

            for (Order order : ordenes) {
                if (order.car != null) {
                    Long carId = order.car.id;
                    ventasPorCoche.put(carId, ventasPorCoche.getOrDefault(carId, 0) + 1);
                    coches.put(carId, order.car);
                }
            }

            // Ordenar por ventas descendente
            List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(ventasPorCoche.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Tomar top 5
            int count = 0;
            for (Map.Entry<Long, Integer> entry : sorted) {
                if (count >= 5) break;

                Car car = coches.get(entry.getKey());
                if (car != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("car", car);
                    item.put("ventas", entry.getValue());
                    resultado.add(item);
                    count++;
                }
            }
        } catch (Exception e) {
            Logger.error("Error obteniendo coches más vendidos: %s", e.getMessage());
        }

        return resultado;
    }

    // Ventas agrupadas por marca
    private static Map<String, Double> getVentasPorMarca() {
        Map<String, Double> resultado = new LinkedHashMap<>();

        try {
            List<Order> ordenes = Order.find("estado = ?1", "pagado").fetch();

            for (Order order : ordenes) {
                if (order.car != null) {
                    String marca = order.car.marca;
                    Double precio = order.car.precio;
                    resultado.put(marca, resultado.getOrDefault(marca, 0.0) + precio);
                }
            }

            // Ordenar por valor descendente
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(resultado.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            resultado.clear();
            for (Map.Entry<String, Double> entry : sorted) {
                resultado.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            Logger.error("Error obteniendo ventas por marca: %s", e.getMessage());
        }

        return resultado;
    }

    // Ventas de los ultimos 6 meses
    private static Map<String, Double> getVentasPorMes() {
        Map<String, Double> resultado = new LinkedHashMap<>();

        try {
            // Nombres de meses en español
            String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                                     "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

            // Obtener fecha actual
            Calendar cal = Calendar.getInstance();

            // Inicializar últimos 6 meses con 0
            List<String> mesesOrdenados = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                Calendar temp = Calendar.getInstance();
                temp.add(Calendar.MONTH, -i);
                String key = nombresMeses[temp.get(Calendar.MONTH)] + " " + temp.get(Calendar.YEAR);
                mesesOrdenados.add(key);
                resultado.put(key, 0.0);
            }

            // Obtener órdenes pagadas
            List<Order> ordenes = Order.find("estado = ?1", "pagado").fetch();

            for (Order order : ordenes) {
                if (order.car != null && order.fechaCreacion != null) {
                    Calendar orderCal = Calendar.getInstance();
                    orderCal.setTime(order.fechaCreacion);
                    String key = nombresMeses[orderCal.get(Calendar.MONTH)] + " " + orderCal.get(Calendar.YEAR);

                    if (resultado.containsKey(key)) {
                        resultado.put(key, resultado.get(key) + order.car.precio);
                    }
                }
            }

            // Reordenar el LinkedHashMap
            Map<String, Double> ordenado = new LinkedHashMap<>();
            for (String mes : mesesOrdenados) {
                ordenado.put(mes, resultado.get(mes));
            }
            return ordenado;

        } catch (Exception e) {
            Logger.error("Error obteniendo ventas por mes: %s", e.getMessage());
        }

        return resultado;
    }

    // Cuantas ordenes hay en cada estado
    private static Map<String, Long> getDistribucionEstados() {
        Map<String, Long> resultado = new LinkedHashMap<>();

        try {
            // Estados posibles
            String[] estados = {"pendiente_pago", "pagado", "entregado", "cancelado"};
            String[] etiquetas = {"Pendiente", "Pagado", "Entregado", "Cancelado"};

            for (int i = 0; i < estados.length; i++) {
                long count = Order.count("estado = ?1", estados[i]);
                if (count > 0) {
                    resultado.put(etiquetas[i], count);
                }
            }

            // Si no hay órdenes, mostrar al menos "Pagado: 0"
            if (resultado.isEmpty()) {
                resultado.put("Pagado", 0L);
            }
        } catch (Exception e) {
            Logger.error("Error obteniendo distribución de estados: %s", e.getMessage());
        }

        return resultado;
    }

    // Lista de coches con filtros
    public static void manageCars(String filterMarca, String searchModelo, String sortBy) {
        // Obtener marcas únicas para el select
        List<Car> allCars = Car.findAll();
        Set<String> marcasSet = new TreeSet<>();
        for (Car c : allCars) {
            if (c.marca != null) marcasSet.add(c.marca);
        }
        List<String> marcas = new ArrayList<>(marcasSet);

        // Construir query con filtros
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filterMarca != null && !filterMarca.isEmpty()) {
            query.append(" AND marca = ?").append(paramIndex++);
            params.add(filterMarca);
        }

        if (searchModelo != null && !searchModelo.isEmpty()) {
            query.append(" AND LOWER(modelo) LIKE ?").append(paramIndex++);
            params.add("%" + searchModelo.toLowerCase() + "%");
        }

        // Ordenamiento
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "precio_asc": query.append(" ORDER BY precio ASC"); break;
                case "precio_desc": query.append(" ORDER BY precio DESC"); break;
                case "year_asc": query.append(" ORDER BY year ASC"); break;
                case "year_desc": query.append(" ORDER BY year DESC"); break;
                case "marca_az": query.append(" ORDER BY marca ASC, modelo ASC"); break;
                default: query.append(" ORDER BY marca ASC, modelo ASC");
            }
        } else {
            query.append(" ORDER BY marca ASC, modelo ASC");
        }

        List<Car> cars = Car.find(query.toString(), params.toArray()).fetch();
        render(cars, marcas, filterMarca, searchModelo, sortBy);
    }

    // Lista de pedidos con filtros
    public static void manageOrders(String filterEstado, String filterPeriodo) {
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filterEstado != null && !filterEstado.isEmpty()) {
            query.append(" AND estado = ?").append(paramIndex++);
            params.add(filterEstado);
        }

        if (filterPeriodo != null && !filterPeriodo.isEmpty()) {
            Date fechaDesde = null;
            Calendar cal = Calendar.getInstance();
            switch (filterPeriodo) {
                case "7dias":
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    fechaDesde = cal.getTime();
                    break;
                case "30dias":
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                    fechaDesde = cal.getTime();
                    break;
                case "1year":
                    cal.add(Calendar.YEAR, -1);
                    fechaDesde = cal.getTime();
                    break;
            }
            if (fechaDesde != null) {
                query.append(" AND fechaCreacion >= ?").append(paramIndex++);
                params.add(fechaDesde);
            }
        }

        query.append(" ORDER BY fechaCreacion DESC");
        List<Order> orders = Order.find(query.toString(), params.toArray()).fetch();
        render(orders, filterEstado, filterPeriodo);
    }

    // Lista de usuarios con filtros
    public static void manageUsers(String filterTipo, String search) {
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filterTipo != null && !filterTipo.isEmpty()) {
            if ("clientes".equals(filterTipo)) {
                query.append(" AND isAdmin = ?").append(paramIndex++);
                params.add(false);
            } else if ("admins".equals(filterTipo)) {
                query.append(" AND isAdmin = ?").append(paramIndex++);
                params.add(true);
            }
        }

        if (search != null && !search.isEmpty()) {
            query.append(" AND (LOWER(fullName) LIKE ?").append(paramIndex);
            query.append(" OR LOWER(email) LIKE ?").append(paramIndex + 1);
            query.append(" OR LOWER(userName) LIKE ?").append(paramIndex + 2).append(")");
            String searchLower = "%" + search.toLowerCase() + "%";
            params.add(searchLower);
            params.add(searchLower);
            params.add(searchLower);
        }

        query.append(" ORDER BY fullName ASC");
        List<User> users = User.find(query.toString(), params.toArray()).fetch();
        render(users, filterTipo, search);
    }

    // Formatea precio como moneda
    public static String formatCurrency(Double amount) {
        if (amount == null) return "0 €";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return formatter.format(amount) + " €";
    }

    // Formulario nuevo coche
    public static void newCar() {
        render();
    }

    // Crear coche nuevo
    public static void createCar(String marca, String modelo, String version,
                                 Integer year, Double precio, String color,
                                 String potencia, String combustible, Integer puertas,
                                 String transmision, String descripcion,
                                 String foto1, String foto2, String foto3) {

        // Validaciones
        if (marca == null || marca.trim().isEmpty()) {
            flash.error("La marca es obligatoria");
            newCar();
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            flash.error("El modelo es obligatorio");
            newCar();
        }

        if (year == null || year < 2000 || year > 2026) {
            flash.error("El año debe estar entre 2000 y 2026");
            newCar();
        }

        if (precio == null || precio <= 0) {
            flash.error("El precio debe ser mayor que 0");
            newCar();
        }

        if (potencia == null || potencia.trim().isEmpty()) {
            flash.error("La potencia es obligatoria");
            newCar();
        }

        if (foto1 == null || foto1.trim().isEmpty()) {
            flash.error("Al menos la foto principal es obligatoria");
            newCar();
        }

        // Crear el coche
        Car car = new Car(marca.trim(), modelo.trim(), year, precio, potencia.trim(), foto1.trim());
        car.version = version != null ? version.trim() : null;
        car.color = color != null ? color.trim() : null;
        car.combustible = combustible;
        car.puertas = puertas;
        car.transmision = transmision;
        car.descripcion = descripcion != null ? descripcion.trim() : null;
        car.foto2 = foto2 != null && !foto2.trim().isEmpty() ? foto2.trim() : null;
        car.foto3 = foto3 != null && !foto3.trim().isEmpty() ? foto3.trim() : null;

        car.save();

        flash.success("Coche creado exitosamente: " + marca + " " + modelo);
        manageCars(null, null, null);
    }

    // Formulario editar coche
    public static void editCar(Long id) {
        if (id == null) {
            flash.error("ID de coche no válido");
            manageCars(null, null, null);
        }

        Car car = Car.findById(id);
        if (car == null) {
            flash.error("Coche no encontrado");
            manageCars(null, null, null);
        }

        render(car);
    }

    // Actualizar coche existente
    public static void updateCar(Long id, String marca, String modelo, String version,
                                 Integer year, Double precio, String color,
                                 String potencia, String combustible, Integer puertas,
                                 String transmision, String descripcion,
                                 String foto1, String foto2, String foto3) {

        if (id == null) {
            flash.error("ID de coche no válido");
            manageCars(null, null, null);
        }

        Car car = Car.findById(id);
        if (car == null) {
            flash.error("Coche no encontrado");
            manageCars(null, null, null);
        }

        // Validaciones
        if (marca == null || marca.trim().isEmpty()) {
            flash.error("La marca es obligatoria");
            editCar(id);
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            flash.error("El modelo es obligatorio");
            editCar(id);
        }

        if (year == null || year < 2000 || year > 2026) {
            flash.error("El año debe estar entre 2000 y 2026");
            editCar(id);
        }

        if (precio == null || precio <= 0) {
            flash.error("El precio debe ser mayor que 0");
            editCar(id);
        }

        if (potencia == null || potencia.trim().isEmpty()) {
            flash.error("La potencia es obligatoria");
            editCar(id);
        }

        if (foto1 == null || foto1.trim().isEmpty()) {
            flash.error("Al menos la foto principal es obligatoria");
            editCar(id);
        }

        // Actualizar campos
        car.marca = marca.trim();
        car.modelo = modelo.trim();
        car.version = version != null ? version.trim() : null;
        car.year = year;
        car.precio = precio;
        car.color = color != null ? color.trim() : null;
        car.potencia = potencia.trim();
        car.combustible = combustible;
        car.puertas = puertas;
        car.transmision = transmision;
        car.descripcion = descripcion != null ? descripcion.trim() : null;
        car.foto1 = foto1.trim();
        car.foto2 = foto2 != null && !foto2.trim().isEmpty() ? foto2.trim() : null;
        car.foto3 = foto3 != null && !foto3.trim().isEmpty() ? foto3.trim() : null;

        car.save();

        flash.success("Coche actualizado exitosamente: " + marca + " " + modelo);
        manageCars(null, null, null);
    }

    // Eliminar coche (valida que no tenga pedidos)
    public static void deleteCar(Long id) {
        if (id == null) {
            flash.error("ID de coche no válido");
            manageCars(null, null, null);
        }

        Car car = Car.findById(id);
        if (car == null) {
            flash.error("Coche no encontrado");
            manageCars(null, null, null);
        }

        // CRÍTICO: Validar que no tenga pedidos asociados
        long ordersCount = Order.count("car = ?1", car);
        if (ordersCount > 0) {
            flash.error("No se puede eliminar el coche porque tiene " + ordersCount + " pedido(s) asociado(s)");
            manageCars(null, null, null);
        }

        // Eliminar el coche
        String carName = car.marca + " " + car.modelo;
        car.delete();

        flash.success("Coche eliminado exitosamente: " + carName);
        manageCars(null, null, null);
    }

    // Ver detalle de un pedido
    public static void orderDetail(Long orderId) {
        if (orderId == null) {
            flash.error("ID de pedido no válido");
            manageOrders(null, null);
        }

        Order order = Order.findById(orderId);
        if (order == null) {
            flash.error("Pedido no encontrado");
            manageOrders(null, null);
        }

        // Estados disponibles para el dropdown
        String[] estadosDisponibles = {"pendiente_pago", "pagado", "entregado", "cancelado"};
        String[] etiquetasEstados = {"Pendiente de Pago", "Pagado", "Entregado", "Cancelado"};

        render(order, estadosDisponibles, etiquetasEstados);
    }

    // Cambiar estado de un pedido
    public static void updateOrderStatus(Long orderId, String nuevoEstado) {
        if (orderId == null) {
            flash.error("ID de pedido no válido");
            manageOrders(null, null);
        }

        Order order = Order.findById(orderId);
        if (order == null) {
            flash.error("Pedido no encontrado");
            manageOrders(null, null);
        }

        // Validar que el estado sea válido
        String[] estadosValidos = {"pendiente_pago", "pagado", "entregado", "cancelado"};
        boolean estadoValido = false;
        for (String estado : estadosValidos) {
            if (estado.equals(nuevoEstado)) {
                estadoValido = true;
                break;
            }
        }

        if (!estadoValido) {
            flash.error("Estado no válido: " + nuevoEstado);
            orderDetail(orderId);
        }

        // Actualizar estado
        String estadoAnterior = order.estado;
        order.estado = nuevoEstado;
        order.save();

        Logger.info("Pedido #%d: Estado cambiado de '%s' a '%s'", orderId, estadoAnterior, nuevoEstado);
        flash.success("Estado del pedido #" + orderId + " actualizado a: " + getEtiquetaEstado(nuevoEstado));
        orderDetail(orderId);
    }

    // Convierte estado a texto legible
    private static String getEtiquetaEstado(String estado) {
        switch (estado) {
            case "pendiente_pago": return "Pendiente de Pago";
            case "pagado": return "Pagado";
            case "entregado": return "Entregado";
            case "cancelado": return "Cancelado";
            default: return estado;
        }
    }

    // Ver detalle de un usuario
    public static void userDetail(Long userId) {
        if (userId == null) {
            flash.error("ID de usuario no válido");
            manageUsers(null, null);
        }

        User user = User.findById(userId);
        if (user == null) {
            flash.error("Usuario no encontrado");
            manageUsers(null, null);
        }

        // Obtener todos los pedidos del usuario
        List<Order> orders = Order.find("user = ?1 ORDER BY fechaCreacion DESC", user).fetch();

        // Calcular estadísticas
        Double totalGastado = 0.0;
        int numeroPedidos = orders.size();
        Map<String, Integer> comprasPorMarca = new HashMap<>();

        for (Order order : orders) {
            if (order.car != null && ("pagado".equals(order.estado) || "entregado".equals(order.estado))) {
                totalGastado += order.car.precio;
                String marca = order.car.marca;
                comprasPorMarca.put(marca, comprasPorMarca.getOrDefault(marca, 0) + 1);
            }
        }

        // Encontrar marca favorita
        String marcaFavorita = null;
        int maxCompras = 0;
        for (Map.Entry<String, Integer> entry : comprasPorMarca.entrySet()) {
            if (entry.getValue() > maxCompras) {
                maxCompras = entry.getValue();
                marcaFavorita = entry.getKey();
            }
        }

        int comprasMarcaFavorita = maxCompras;

        render(user, orders, totalGastado, numeroPedidos, marcaFavorita, comprasMarcaFavorita);
    }
}
