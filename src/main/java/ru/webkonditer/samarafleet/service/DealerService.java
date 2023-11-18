package ru.webkonditer.samarafleet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.webkonditer.samarafleet.model.Car;
import ru.webkonditer.samarafleet.model.Dealer;
import ru.webkonditer.samarafleet.model.Owner;
import ru.webkonditer.samarafleet.repo.DealerRepository;
import ru.webkonditer.samarafleet.repo.OwnerRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с дилерами (Dealer).
 */
@Service
public class DealerService {

    private final DealerRepository dealerRepository;
    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;

    /**
     * Конструктор сервиса, использующий внедрение зависимости для доступа к репозиториям дилеров и владельцев.
     *
     * @param dealerRepository Репозиторий для работы с данными о дилерах.
     * @param ownerService     Сервис для работы с данными о владельцах.
     * @param ownerRepository  Репозиторий для работы с данными о владельцах.
     */
    @Autowired
    public DealerService(DealerRepository dealerRepository, OwnerService ownerService, OwnerRepository ownerRepository) {
        this.dealerRepository = dealerRepository;
        this.ownerService = ownerService;
        this.ownerRepository = ownerRepository;
    }

    /**
     * Получение списка всех дилеров.
     *
     * @return Список всех дилеров.
     */
    public List<Dealer> getAllDealers() {
        return dealerRepository.findAll();
    }

    /**
     * Получение информации о дилере по его идентификатору.
     *
     * @param dealerId Идентификатор дилера.
     * @return Информация о дилере в виде Optional.
     */
    public Optional<Dealer> getDealerById(Long dealerId) {
        return dealerRepository.findById(dealerId);
    }

    /**
     * Создание нового дилера.
     *
     * @param dealer Новый дилер.
     * @return Созданный дилер.
     */
    public Dealer createDealer(Dealer dealer) {
        // Удаляем id если есть
        if (dealer.getId() != null) {
            dealer.setId(null);
        }
        return dealerRepository.save(dealer);
    }

    /**
     * Обновление информации о дилере.
     *
     * @param dealerId      Идентификатор обновляемого дилера.
     * @param updatedDealer Обновленная информация о дилере.
     * @return Обновленный дилер или null, если дилер с заданным ID не найден.
     */
    public Dealer updateDealer(Long dealerId, Dealer updatedDealer) {
        // Проверка наличия дилера с заданным ID
        if (dealerRepository.existsById(dealerId)) {
            updatedDealer.setId(dealerId);
            return dealerRepository.save(updatedDealer);
        } else {
            // Можно обработать ситуацию, когда дилер с указанным ID не найден
            return null;
        }
    }

    /**
     * Удаление дилера по его идентификатору.
     *
     * @param dealerId Идентификатор удаляемого дилера.
     */
    public void deleteDealer(Long dealerId) {
        // Удаление дилера по ID
        dealerRepository.deleteById(dealerId);
    }

    /**
     * Добавление владельца к дилеру.
     *
     * @param dealerId Идентификатор дилера.
     * @param ownerId  Идентификатор владельца.
     * @return Ответ с информацией о дилере (успешно или с ошибкой).
     */
    public ResponseEntity<Dealer> addOwnerToDealer(Long dealerId, Long ownerId) {
        Optional<Dealer> dealerOptional = dealerRepository.findById(dealerId);
        Optional<Owner> ownerOptional = ownerService.getOwnerById(ownerId);

        if (ownerOptional.isEmpty() || dealerOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Dealer dealer = dealerOptional.get();
        Owner owner = ownerOptional.get();

        owner.setDealer(dealer);
        dealer.addOwner(owner);

        dealerRepository.save(dealer);

        return ResponseEntity.ok(dealer);
    }

    /**
     * Получение списка владельцев дилера по его идентификатору.
     *
     * @param dealerId Идентификатор дилера.
     * @return Список владельцев дилера.
     */
    public List<Owner> getAllDealerOwners(Long dealerId) {
        // Получаем диллера по идентификатору
        Optional<Dealer> dealer = dealerRepository.findById(dealerId);
        // Возвращаем список владельцев дилера или пустой список, если диллера нет
        return dealer.map(Dealer::getOwners).orElse(Collections.emptyList());
    }

    /**
     * Получение списка всех автомобилей дилера по его идентификатору.
     *
     * @param dealerId Идентификатор дилера.
     * @return Список всех автомобилей дилера.
     */
    public List<Car> getAllDealerCars(Long dealerId) {
        return dealerRepository.findById(dealerId)
                .map(dealer -> dealer.getOwners()
                        .stream()
                        .flatMap(owner -> owner.getCars().stream())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Открепление владельца от дилера.
     *
     * @param dealerId Идентификатор дилера.
     * @param ownerId  Идентификатор владельца.
     * @return Результат открепления (true - успешно, false - ошибка).
     */
    public boolean detachOwnerFromDealer(Long dealerId, Long ownerId) {
        Optional<Dealer> dealerOptional = dealerRepository.findById(dealerId);
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);

        if (dealerOptional.isPresent() && ownerOptional.isPresent()) {
            Dealer dealer = dealerOptional.get();
            Owner owner = ownerOptional.get();

            // Проверка, принадлежит ли владелец дилеру
            if (owner.getDealer() != null && owner.getDealer().getId().equals(dealerId)) {
                // Открепление владельца от дилера
                dealer.getOwners().remove(owner);
                owner.setDealer(null);

                // Сохранение изменений в базе данных
                dealerRepository.save(dealer);
                ownerRepository.save(owner);

                // Возвращаем true в случае успеха
                return true;
            }
        }

        // Возвращаем false в случае неудачи
        return false;
    }
}
