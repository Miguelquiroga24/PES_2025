package models;

import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "cars")
public class Car extends Model {

    @Column(nullable = false, length = 50)
    public String marca;

    @Column(nullable = false, length = 100)
    public String modelo;

    @Column(length = 100)
    public String version;

    @Column(nullable = false)
    public int year;

    @Column(nullable = false)
    public double precio;

    @Column(length = 50)
    public String color;

    @Column(nullable = false, length = 100)
    public String potencia;

    @Column(length = 50)
    public String combustible;

    public Integer puertas;

    @Column(length = 50)
    public String transmision;

    @Column(length = 1000)
    public String descripcion;

    @Column(nullable = false, length = 255)
    public String foto1;

    @Column(length = 255)
    public String foto2;

    @Column(length = 255)
    public String foto3;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    public List<Order> orders;

    // Constructor vacío
    public Car() {
    }

    // Constructor con campos obligatorios
    public Car(String marca, String modelo, int year, double precio,
               String potencia, String foto1) {
        this.marca = marca;
        this.modelo = modelo;
        this.year = year;
        this.precio = precio;
        this.potencia = potencia;
        this.foto1 = foto1;
    }

    @Override
    public String toString() {
        return marca + " " + modelo;
    }
}
