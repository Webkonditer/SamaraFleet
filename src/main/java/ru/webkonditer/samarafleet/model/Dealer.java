package ru.webkonditer.samarafleet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

/**
 * Класс, представляющий дилера (Dealer).
 */
@Entity
@Getter // Аннотация Lombok для генерации геттеров, сеттеров, equals, hashCode и toString
//@Setter // Аннотация Lombok для генерации сеттеров (закомментирована, так как не используется)
@Table(name = "dealers")
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Column(name = "representative_name")
    private String representativeName;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL)
    @JsonIgnore // Игнорируем поле при сериализации в JSON
    private List<Owner> owners;

    /**
     * Конструктор без аргументов (для JPA).
     */
    public Dealer() {
    }

    /**
     * Конструктор с основными полями.
     *
     * @param name               Название дилера.
     * @param email              Электронная почта дилера.
     * @param representativeName ФИО представителя дилера.
     */
    public Dealer(String name, String email, String representativeName) {
        this.name = name;
        this.email = email;
        this.representativeName = representativeName;
    }

    /**
     * Добавляет владельца к дилеру.
     *
     * @param owner Владелец, который будет добавлен к дилеру.
     */
    public void addOwner(Owner owner) {
        owners.add(owner);
        owner.setDealer(this);
    }

    /**
     * Устанавливает идентификатор дилера.
     *
     * @param id Идентификатор дилера.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Устанавливает название дилера.
     *
     * @param name Название дилера.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает электронную почту дилера.
     *
     * @param email Электронная почта дилера.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Устанавливает ФИО представителя дилера.
     *
     * @param representativeName ФИО представителя дилера.
     */
    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }
}
