package ru.webkonditer.samarafleet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.ToString;

import java.util.List;

/**
 * Класс, представляющий владельца автомобиля (Owner).
 */
@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String email;

    @ManyToOne
    @JsonIgnore // Игнорируем поле при сериализации в JSON
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Car> cars;

    /**
     * Конструктор без аргументов (для JPA).
     */
    public Owner() {
    }

    /**
     * Конструктор с основными полями.
     *
     * @param fullName Полное имя владельца.
     * @param phone    Номер телефона владельца.
     * @param email    Электронная почта владельца.
     * @param dealer   Дилер, связанный с владельцем.
     * @param cars     Список автомобилей, принадлежащих владельцу.
     */
    public Owner(String fullName, String phone, String email, Dealer dealer, List<Car> cars) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.dealer = dealer;
        this.cars = cars;
    }

    /**
     * Метод для добавления машины владельцу.
     *
     * @param car Машина, которая будет добавлена владельцу.
     */
    public void addCar(Car car) {
        cars.add(car);
        car.setOwner(this);
    }

    /**
     * Метод для установки дилера владельцу.
     *
     * @param dealer Дилер, который будет установлен владельцу.
     */
    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    /**
     * Метод для установки идентификатора владельца.
     *
     * @param ownerId Идентификатор владельца.
     */
    public void setId(Long ownerId) {
        this.id = ownerId;
    }

    /**
     * Метод для получения идентификатора владельца.
     *
     * @return Идентификатор владельца.
     */
    public Long getId() {
        return id;
    }

    /**
     * Метод для получения полного имени владельца.
     *
     * @return Полное имя владельца.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Метод для установки полного имени владельца.
     *
     * @param fullName Полное имя владельца.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Метод для получения номера телефона владельца.
     *
     * @return Номер телефона владельца.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Метод для установки номера телефона владельца.
     *
     * @param phone Номер телефона владельца.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Метод для получения электронной почты владельца.
     *
     * @return Электронная почта владельца.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Метод для установки электронной почты владельца.
     *
     * @param email Электронная почта владельца.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Метод для получения дилера, связанного с владельцем.
     *
     * @return Дилер, связанный с владельцем.
     */
    public Dealer getDealer() {
        return dealer;
    }

    /**
     * Метод для получения списка автомобилей, принадлежащих владельцу.
     *
     * @return Список автомобилей, принадлежащих владельцу.
     */
    public List<Car> getCars() {
        return cars;
    }

    /**
     * Метод для установки списка автомобилей, принадлежащих владельцу.
     *
     * @param cars Список автомобилей, принадлежащих владельцу.
     */
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    /**
     * Метод для удаления машины у владельца.
     *
     * @param car Машина, которая будет удалена у владельца.
     */
    public void removeCar(Car car) {
        cars.remove(car);
        car.setOwner(null);
    }
}
