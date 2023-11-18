package ru.webkonditer.samarafleet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.webkonditer.samarafleet.model.Car;
import ru.webkonditer.samarafleet.service.CarService;
import ru.webkonditer.samarafleet.service.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;
    private final OwnerService ownerService;

    @Autowired
    public CarController(CarService carService, OwnerService ownerService) {
        this.carService = carService;
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Get a list of all cars")
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{carId}")
    @Operation(
            summary = "Get car by ID",
            description = "Get detailed information about a car by its ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Car found",
            content = @Content(schema = @Schema(implementation = Car.class))
    )
    @ApiResponse(responseCode = "404", description = "Car not found")
    public ResponseEntity<Car> getCarById(
            @Parameter(description = "ID of the car to be retrieved") @PathVariable Long carId
    ) {
        return carService.getCarById(carId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new car", description = "Create a new car with the provided details")
    @ApiResponse(
            responseCode = "201",
            description = "Car created",
            content = @Content(schema = @Schema(implementation = Car.class))
    )
    @ApiResponse(responseCode = "400", description = "Bad request, owner not found")
    public ResponseEntity<Car> createCar(
            @RequestBody Car car
    ) {
        // Check if the owner with the specified ID exists
        if (car.getOwner() != null && car.getOwner().getId() != null) {
            if (!ownerService.existsById(car.getOwner().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        Car createdCar = carService.createCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    @PutMapping("/{carId}")
    @Operation(summary = "Update car details", description = "Update the details of an existing car")
    @ApiResponse(
            responseCode = "200",
            description = "Car updated",
            content = @Content(schema = @Schema(implementation = Car.class))
    )
    @ApiResponse(responseCode = "404", description = "Car not found")
    public ResponseEntity<Car> updateCar(
            @Parameter(description = "ID of the car to be updated") @PathVariable Long carId,
            @RequestBody Car updatedCar
    ) {
        updatedCar.setId(carId);
        Car updated = carService.updateCar(carId, updatedCar);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{carId}")
    @Operation(summary = "Delete a car", description = "Delete a car by its ID")
    @ApiResponse(responseCode = "204", description = "Car deleted successfully")
    @ApiResponse(responseCode = "404", description = "Car not found")
    public ResponseEntity<Void> deleteCar(
            @Parameter(description = "ID of the car to be deleted") @PathVariable Long carId
    ) {
        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }
}
