package ru.webkonditer.samarafleet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webkonditer.samarafleet.model.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    // Здесь можно добавить методы для специфичных запросов, если они потребуются
}
