package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        // Obtener 3 coches destacados aleatoriamente
        List<Car> featuredCars = Car.find("ORDER BY RANDOM()").fetch(3);

        // Obtener estadisticas
        long totalCars = Car.count();
        long totalBrands = Car.find("SELECT DISTINCT marca FROM Car").fetch().size();

        render(featuredCars, totalCars, totalBrands);
    }

    // Inicializa la BD con datos de prueba (solo si esta vacia)
    public static void initDB() {
        // Verificar si ya hay datos
        if (User.count() > 0) {
            renderText("La base de datos ya tiene datos. Total usuarios: " + User.count());
            return;
        }

        // Crear usuario administrador
        User admin = new User("admin", "admin@bycar.com", "admin123",
                              "Administrador BYCar", "600000000", "Calle Admin 1, Barcelona", true);
        admin.save();

        // Crear clientes de prueba
        User client1 = new User("maria", "maria@gmail.com", "maria123",
                                "Maria Cazorla", "611111111", "Calle Cliente 1, Barcelona", false);
        client1.save();

        User client2 = new User("miguel", "miguel@gmail.com", "miguel123",
                                "Miguel Quiroga", "622222222", "Calle Cliente 2, Barcelona", false);
        client2.save();

        // Crear coches de prueba - Tesla (2)
        Car tesla1 = new Car("Tesla", "Model S", 2024, 89990.00, "670 CV, eléctrico", "/public/images/cars/tesla_model_s_1.jpg");
        tesla1.version = "Long Range";
        tesla1.combustible = "Eléctrico";
        tesla1.color = "Rojo";
        tesla1.transmision = "Automática";
        tesla1.puertas = 4;
        tesla1.descripcion = "Sedán eléctrico de alto rendimiento con autonomía de hasta 650 km";
        tesla1.foto2 = "/public/images/cars/tesla_model_s_2.jpg";
        tesla1.save();

        Car tesla2 = new Car("Tesla", "Model 3", 2024, 45990.00, "283 CV, eléctrico", "/public/images/cars/tesla_model_3_1.jpg");
        tesla2.version = "Standard Range";
        tesla2.combustible = "Eléctrico";
        tesla2.color = "Blanco";
        tesla2.transmision = "Automática";
        tesla2.puertas = 4;
        tesla2.descripcion = "Sedán eléctrico compacto con tecnología de conducción autónoma";
        tesla2.foto2 = "/public/images/cars/tesla_model_3_2.jpg";
        tesla2.save();

        // Mercedes (2)
        Car mercedes1 = new Car("Mercedes", "Clase S", 2024, 115000.00, "435 CV, gasolina", "/public/images/cars/mercedes_s_class_1.jpg");
        mercedes1.version = "S 500 4MATIC";
        mercedes1.combustible = "Gasolina";
        mercedes1.color = "Negro";
        mercedes1.transmision = "Automática";
        mercedes1.puertas = 4;
        mercedes1.descripcion = "Lujo y confort en su máxima expresión con tecnología de vanguardia";
        mercedes1.foto2 = "/public/images/cars/mercedes_s_class_2.jpg";
        mercedes1.save();

        Car mercedes2 = new Car("Mercedes", "AMG GT", 2024, 165000.00, "585 CV, gasolina", "/public/images/cars/mercedes_amg_gt_1.jpg");
        mercedes2.version = "63 S";
        mercedes2.combustible = "Gasolina";
        mercedes2.color = "Gris";
        mercedes2.transmision = "Automática";
        mercedes2.puertas = 2;
        mercedes2.descripcion = "Deportivo de altas prestaciones con motor V8 biturbo";
        mercedes2.foto2 = "/public/images/cars/mercedes_amg_gt_2.jpg";
        mercedes2.save();

        // BMW (2)
        Car bmw1 = new Car("BMW", "Serie 3", 2024, 48500.00, "258 CV, diesel", "/public/images/cars/bmw_3_series_1.jpg");
        bmw1.version = "320d";
        bmw1.combustible = "Diesel";
        bmw1.color = "Azul";
        bmw1.transmision = "Automática";
        bmw1.puertas = 4;
        bmw1.descripcion = "Berlina deportiva con equilibrio perfecto entre confort y dinámica";
        bmw1.foto2 = "/public/images/cars/bmw_3_series_2.jpg";
        bmw1.save();

        Car bmw2 = new Car("BMW", "X5", 2024, 79900.00, "340 CV, híbrido", "/public/images/cars/bmw_x5_1.jpg");
        bmw2.version = "xDrive45e";
        bmw2.combustible = "Híbrido";
        bmw2.color = "Blanco";
        bmw2.transmision = "Automática";
        bmw2.puertas = 5;
        bmw2.descripcion = "SUV premium con tecnología híbrida enchufable y máxima versatilidad";
        bmw2.foto2 = "/public/images/cars/bmw_x5_2.jpg";
        bmw2.save();

        // Porsche (2)
        Car porsche1 = new Car("Porsche", "911", 2024, 135000.00, "450 CV, gasolina", "/public/images/cars/porsche_911_1.jpg");
        porsche1.version = "Carrera";
        porsche1.combustible = "Gasolina";
        porsche1.color = "Amarillo";
        porsche1.transmision = "Manual";
        porsche1.puertas = 2;
        porsche1.descripcion = "Icónico deportivo alemán con motor boxer de 6 cilindros";
        porsche1.foto2 = "/public/images/cars/porsche_911_2.jpg";
        porsche1.save();

        Car porsche2 = new Car("Porsche", "Cayenne", 2024, 95000.00, "340 CV, gasolina", "/public/images/cars/porsche_cayenne_1.jpg");
        porsche2.version = "Base";
        porsche2.combustible = "Gasolina";
        porsche2.color = "Negro";
        porsche2.transmision = "Automática";
        porsche2.puertas = 5;
        porsche2.descripcion = "SUV deportivo que combina lujo, rendimiento y practicidad";
        porsche2.foto2 = "/public/images/cars/porsche_cayenne_2.jpg";
        porsche2.save();

        // Aston Martin (2)
        Car aston1 = new Car("Aston Martin", "DB12", 2024, 245000.00, "680 CV, gasolina", "/public/images/cars/aston_martin_db12_1.jpg");
        aston1.version = "V8";
        aston1.combustible = "Gasolina";
        aston1.color = "Verde";
        aston1.transmision = "Automática";
        aston1.puertas = 2;
        aston1.descripcion = "GT británico de lujo con diseño elegante y motor V8 biturbo";
        aston1.foto2 = "/public/images/cars/aston_martin_db12_2.jpg";
        aston1.save();

        Car aston2 = new Car("Aston Martin", "DBX", 2024, 220000.00, "550 CV, gasolina", "/public/images/cars/aston_martin_dbx_1.jpg");
        aston2.version = "707";
        aston2.combustible = "Gasolina";
        aston2.color = "Gris";
        aston2.transmision = "Automática";
        aston2.puertas = 5;
        aston2.descripcion = "Primer SUV de Aston Martin, combina lujo británico con deportividad";
        aston2.foto2 = "/public/images/cars/aston_martin_dbx_2.jpg";
        aston2.save();

        // Maserati (2)
        Car maserati1 = new Car("Maserati", "MC20", 2024, 235000.00, "630 CV, gasolina", "/public/images/cars/maserati_mc20_1.jpg");
        maserati1.version = "Nettuno";
        maserati1.combustible = "Gasolina";
        maserati1.color = "Azul";
        maserati1.transmision = "Automática";
        maserati1.puertas = 2;
        maserati1.descripcion = "Superdeportivo italiano con motor V6 desarrollado por Maserati";
        maserati1.foto2 = "/public/images/cars/maserati_mc20_2.jpg";
        maserati1.save();

        Car maserati2 = new Car("Maserati", "Grecale", 2024, 85000.00, "330 CV, gasolina", "/public/images/cars/maserati_grecale_1.jpg");
        maserati2.version = "GT";
        maserati2.combustible = "Gasolina";
        maserati2.color = "Blanco";
        maserati2.transmision = "Automática";
        maserati2.puertas = 5;
        maserati2.descripcion = "SUV compacto de lujo con el alma deportiva característica de Maserati";
        maserati2.foto2 = "/public/images/cars/maserati_grecale_2.jpg";
        maserati2.save();

        // Audi (2)
        Car audi1 = new Car("Audi", "RS e-tron GT", 2024, 145000.00, "646 CV, eléctrico", "/public/images/cars/audi_rs_etron_gt_1.jpg");
        audi1.version = "Performance";
        audi1.combustible = "Eléctrico";
        audi1.color = "Rojo";
        audi1.transmision = "Automática";
        audi1.puertas = 4;
        audi1.descripcion = "Gran turismo eléctrico de altas prestaciones con hasta 472 km de autonomía";
        audi1.foto2 = "/public/images/cars/audi_rs_etron_gt_2.jpg";
        audi1.save();

        Car audi2 = new Car("Audi", "Q8", 2024, 89900.00, "340 CV, diesel", "/public/images/cars/audi_q8_1.jpg");
        audi2.version = "50 TDI";
        audi2.combustible = "Diesel";
        audi2.color = "Negro";
        audi2.transmision = "Automática";
        audi2.puertas = 5;
        audi2.descripcion = "SUV coupé de lujo con diseño distintivo y tecnología quattro";
        audi2.foto2 = "/public/images/cars/audi_q8_2.jpg";
        audi2.save();

        // Mostrar resumen
        renderText("Base de datos inicializada correctamente!\n\n" +
                   "Usuarios creados: " + User.count() + "\n" +
                   "Coches creados: " + Car.count() + "\n\n" +
                   "Usuarios:\n" +
                   "- admin / admin123 (ADMIN)\n" +
                   "- maria / maria123\n" +
                   "- miguel / miguel123\n\n" +
                   "Accede a http://localhost:9000/@db para ver la BD en el navegador");
    }

}