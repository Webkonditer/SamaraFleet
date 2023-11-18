package ru.webkonditer.samarafleet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс, представляющий автомобиль (Car).
 */
@Entity
@Table(name = "cars")
@Data // Аннотация Lombok для генерации геттеров, сеттеров, equals, hashCode и toString
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "manufacture_date")
    private String manufactureDate;

    @ManyToOne
    @JsonIgnore // Игнорируем поле при сериализации в JSON
    @JoinColumn(name = "owner_id")
    private Owner owner;

    /**
     * Конструктор без аргументов (для JPA).
     */
    public Car() {
    }

    /**
     * Конструктор с основными полями.
     *
     * @param registrationNumber Номер регистрации автомобиля.
     * @param manufactureDate   Дата производства автомобиля.
     */
    public Car(String registrationNumber, String manufactureDate) {
        this.registrationNumber = registrationNumber;
        this.manufactureDate = manufactureDate;
    }

    /**
     * Устанавливает владельца автомобиля.
     *
     * @param owner Владелец автомобиля.
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Устанавливает идентификатор автомобиля.
     *
     * @param carId Идентификатор автомобиля.
     */
    public void setId(Long carId) {
        this.id = carId;
    }

    /**
     * Получает идентификатор автомобиля.
     *
     * @return Идентификатор автомобиля.
     */
    public Long getId() {
        return id;
    }

    /**
     * Получает номер регистрации автомобиля.
     *
     * @return Номер регистрации автомобиля.
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /**
     * Устанавливает номер регистрации автомобиля.
     *
     * @param registrationNumber Номер регистрации автомобиля.
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    /**
     * Получает дату производства автомобиля.
     *
     * @return Дата производства автомобиля.
     */
    public String getManufactureDate() {
        return manufactureDate;
    }

    /**
     * Устанавливает дату производства автомобиля.
     *
     * @param manufactureDate Дата производства автомобиля.
     */
    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    /**
     * Получает владельца автомобиля.
     *
     * @return Владелец автомобиля.
     */
    public Owner getOwner() {
        return owner;
    }

}
