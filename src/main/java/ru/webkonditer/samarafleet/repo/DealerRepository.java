package ru.webkonditer.samarafleet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webkonditer.samarafleet.model.Dealer;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    // Здесь можно добавить методы для специфичных запросов, если они потребуются
}
