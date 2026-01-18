package models;

import play.db.jpa.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order extends Model {

    @ManyToOne
    @JoinColumn(nullable = false)
    public User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Car car;

    @Column(nullable = false, length = 50)
    public String estado; // pendiente_pago, pagado, entregado, cancelado

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date fechaCreacion;

    @Column(nullable = false, length = 100)
    public String emailConfirmacion;

    @Column(nullable = false, length = 100)
    public String fullName;

    @Column(nullable = false, length = 20)
    public String phone;

    @Column(nullable = false, length = 255)
    public String address;

    // Constructor vacío
    public Order() {
    }

    // Constructor con parámetros
    public Order(User user, Car car, String emailConfirmacion,
                 String fullName, String phone, String address) {
        this.user = user;
        this.car = car;
        this.estado = "pendiente_pago";
        this.fechaCreacion = new Date();
        this.emailConfirmacion = emailConfirmacion;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Order #" + id + " - " + car + " (" + estado + ")";
    }
}
