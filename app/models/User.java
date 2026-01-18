package models;

import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User extends Model {

    @Column(unique = true, nullable = false, length = 50)
    public String userName;

    @Column(unique = true, nullable = false, length = 100)
    public String email;

    @Column(nullable = false, length = 255)
    public String password;

    @Column(nullable = false, length = 100)
    public String fullName;

    @Column(nullable = false, length = 20)
    public String phone;

    @Column(nullable = false, length = 255)
    public String address;

    @Column(nullable = false)
    public boolean isAdmin = false;

    // IDs de coches favoritos separados por comas (ej: "1,5,8")
    @Column(length = 1000)
    public String favoriteCarIds = "";

    // Saldo Wallet BYCar (por defecto 500,000 €)
    @Column(nullable = false)
    public Double saldo = 500000.0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Order> orders;

    // Constructor vacío (requerido por JPA)
    public User() {
        this.saldo = 500000.0;
    }

    // Constructor con parámetros
    public User(String userName, String email, String password,
                String fullName, String phone, String address, boolean isAdmin) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin;
        this.favoriteCarIds = "";
        this.saldo = 500000.0;
    }

    // Comprobar si tiene saldo suficiente
    public boolean tieneSaldoSuficiente(Double monto) {
        return this.saldo >= monto;
    }

    // Descontar saldo
    public void descontarSaldo(Double monto) {
        if (tieneSaldoSuficiente(monto)) {
            this.saldo -= monto;
            this.save();
        }
    }

    // Recargar saldo
    public void recargarSaldo(Double monto) {
        if (monto > 0) {
            this.saldo += monto;
            this.save();
        }
    }

    @Override
    public String toString() {
        return userName;
    }
}
