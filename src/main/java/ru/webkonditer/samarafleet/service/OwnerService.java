package ru.webkonditer.samarafleet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.webkonditer.samarafleet.model.Car;
import ru.webkonditer.samarafleet.model.Owner;
import ru.webkonditer.samarafleet.repo.CarRepository;
import ru.webkonditer.samarafleet.repo.OwnerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления владельцами и их автомобилями.
 */
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final CarRepository carRepository;
    private final CarService carService;

    @Autowired
    public OwnerService(OwnerRepository ownerRepository, CarRepository carRepository, CarService carService) {
        this.ownerRepository = ownerRepository;
        this.carRepository = carRepository;
        this.carService = carService;
    }

    /**
     * Получает список всех владельцев.
     *
     * @return Список всех владельцев.
     */
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    /**
     * Получает владельца по его идентификатору.
     *
     * @param ownerId Идентификатор владельца.
     * @return Владелец, если найден, иначе null.
     */
    public Optional<Owner> getOwnerById(Long ownerId) {
        return ownerRepository.findById(ownerId);
    }

    /**
     * Создает нового владельца.
     *
     * @param owner Владелец для создания.
     * @return Созданный владелец.
     */
    public Owner createOwner(Owner owner) {
        // Удаляем id если есть
        if (owner.getId() != null) {
            owner.setId(null);
        }
        return ownerRepository.save(owner);
    }

    /**
     * Обновляет данные владельца.
     *
     * @param ownerId      Идентификатор владельца, который будет обновлен.
     * @param updatedOwner Обновленные данные владельца.
     * @return Обновленный владелец, если найден, иначе null.
     */
    public Owner updateOwner(Long ownerId, Owner updatedOwner) {
        // Проверка наличия владельца с заданным ID
        if (ownerRepository.existsById(ownerId)) {
            updatedOwner.setId(ownerId);
            return ownerRepository.save(updatedOwner);
        } else {
            return null;
        }
    }

    /**
     * Удаляет владельца по его идентификатору.
     *
     * @param ownerId Идентификатор владельца.
     */
    public void deleteOwner(Long ownerId) {
        // Удаление владельца по ID
        ownerRepository.deleteById(ownerId);
    }

    /**
     * Проверяет существование владельца по его идентификатору.
     *
     * @param ownerId Идентификатор владельца.
     * @return true, если владелец существует, иначе false.
     */
    public boolean existsById(Long ownerId) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        return ownerOptional.isPresent();
    }

    /**
     * Получает список автомобилей владельца.
     *
     * @param ownerId Идентификатор владельца.
     * @return Список автомобилей владельца или null, если владелец не найден.
     */
    public List<Car> getAllOwnersCars(Long ownerId) {
        // Получаем владельца по идентификатору
        Optional<Owner> owner = ownerRepository.findById(ownerId);
        // Возвращаем список машин владельца или null, если владельца нет
        return owner.map(Owner::getCars).orElse(null);
    }

    /**
     * Добавляет автомобиль владельцу.
     *
     * @param ownerId Идентификатор владельца.
     * @param carId   Идентификатор автомобиля.
     * @return ResponseEntity с обновленным владельцем или ошибкой BadRequest.
     */
    public ResponseEntity<Owner> addCarToOwner(Long ownerId, Long carId) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Optional<Car> carOptional = carService.getCarById(carId);

        if (ownerOptional.isEmpty() || carOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Owner owner = ownerOptional.get();
        Car car = carOptional.get();

        car.setOwner(owner);
        owner.addCar(car);

        ownerRepository.save(owner);

        return ResponseEntity.ok(owner);
    }

    /**
     * Открепляет автомобиль от владельца.
     *
     * @param ownerId Идентификатор владельца.
     * @param carId   Идентификатор автомобиля.
     * @return true, если открепление успешно, иначе false.
     */
    public boolean detachCarFromOwner(Long ownerId, Long carId) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Optional<Car> carOptional = carRepository.findById(carId);

        if (ownerOptional.isPresent() && carOptional.isPresent()) {
            Owner owner = ownerOptional.get();
            Car car = carOptional.get();

            // Проверка, принадлежит ли машина данному владельцу
            if (owner.getCars().contains(car)) {
                // Открепление машины от владельца
                owner.removeCar(car);
                car.setOwner(null);

                // Сохранение изменений в базе данных
                ownerRepository.save(owner);
                carRepository.save(car);

                // Возвращаем true в случае успеха
                return true;
            }
        }

        // Возвращаем false в случае неудачи
        return false;
    }
}
