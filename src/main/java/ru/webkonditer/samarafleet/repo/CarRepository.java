package ru.webkonditer.samarafleet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webkonditer.samarafleet.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // Здесь можно добавить методы для специфичных запросов, если они потребуются
}
