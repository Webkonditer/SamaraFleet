package ru.webkonditer.samarafleet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.webkonditer.samarafleet.model.Car;
import ru.webkonditer.samarafleet.repo.CarRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с сущностью "Автомобиль" (Car).
 */
@Service
public class CarService {

    private final CarRepository carRepository;

    /**
     * Конструктор сервиса, использующий внедрение зависимости для доступа к репозиторию машин.
     *
     * @param carRepository Репозиторий для работы с данными об автомобилях.
     */
    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * Получение списка всех автомобилей.
     *
     * @return Список всех автомобилей.
     */
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    /**
     * Получение информации об автомобиле по его идентификатору.
     *
     * @param carId Идентификатор автомобиля.
     * @return Информация об автомобиле в виде Optional.
     */
    public Optional<Car> getCarById(Long carId) {
        return carRepository.findById(carId);
    }

    /**
     * Создание нового автомобиля.
     *
     * @param car Новый автомобиль.
     * @return Созданный автомобиль.
     */
    public Car createCar(Car car) {
        // Удаляем id если есть
        if (car.getId() != null) {
            car.setId(null);
        }
        // Сохраняем новую машину
        return carRepository.save(car);
    }

    /**
     * Обновление информации об автомобиле.
     *
     * @param carId      Идентификатор обновляемого автомобиля.
     * @param updatedCar Обновленная информация об автомобиле.
     * @return Обновленный автомобиль или null, если автомобиль с заданным ID не найден.
     */
    public Car updateCar(Long carId, Car updatedCar) {
        // Проверка наличия машины с заданным ID
        if (carRepository.existsById(carId)) {
            updatedCar.setId(carId);
            return carRepository.save(updatedCar);
        } else {
            return null;
        }
    }

    /**
     * Удаление автомобиля по его идентификатору.
     *
     * @param carId Идентификатор удаляемого автомобиля.
     */
    public void deleteCar(Long carId) {
        // Удаление машины по ID
        carRepository.deleteById(carId);
    }
}
