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
import ru.webkonditer.samarafleet.model.Owner;
import ru.webkonditer.samarafleet.service.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(
            summary = "Get all owners",
            description = "Retrieve a list of all owners"
    )
    public List<Owner> getAllOwners() {
        return ownerService.getAllOwners();
    }

    @GetMapping("/{ownerId}")
    @Operation(
            summary = "Get owner by ID",
            description = "Retrieve an owner by their ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Owner found successfully",
            content = @Content(schema = @Schema(implementation = Owner.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Owner not found"
    )
    public ResponseEntity<Owner> getOwnerById(
            @Parameter(
                    description = "ID of the owner to be retrieved",
                    required = true
            ) @PathVariable Long ownerId) {
        return ownerService.getOwnerById(ownerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create owner",
            description = "Create a new owner"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Owner created successfully",
            content = @Content(schema = @Schema(implementation = Owner.class))
    )
    public ResponseEntity<Owner> createOwner(
            @Parameter(
                    description = "Owner object to be created",
                    required = true
            ) @RequestBody Owner owner) {
        Owner createdOwner = ownerService.createOwner(owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOwner);
    }

    @PutMapping("/{ownerId}")
    @Operation(
            summary = "Update owner",
            description = "Update an existing owner"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Owner updated successfully",
            content = @Content(schema = @Schema(implementation = Owner.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Owner not found"
    )
    public ResponseEntity<Owner> updateOwner(
            @Parameter(
                    description = "ID of the owner to be updated",
                    required = true
            ) @PathVariable Long ownerId,
            @Parameter(
                    description = "Updated owner object",
                    required = true
            ) @RequestBody Owner updatedOwner) {
        Owner updated = ownerService.updateOwner(ownerId, updatedOwner);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{ownerId}")
    @Operation(
            summary = "Delete owner",
            description = "Delete an owner by ID"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Owner deleted successfully"
    )
    public ResponseEntity<Void> deleteOwner(
            @Parameter(
                    description = "ID of the owner to be deleted",
                    required = true
            ) @PathVariable Long ownerId) {
        ownerService.deleteOwner(ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ownerId}/cars")
    @Operation(
            summary = "Get all cars of an owner",
            description = "Retrieve a list of all cars owned by a specific owner"
    )
    public List<Car> getAllOwnersCars(
            @Parameter(
                    description = "ID of the owner",
                    required = true
            ) @PathVariable Long ownerId) {
        return ownerService.getAllOwnersCars(ownerId);
    }

    @PatchMapping("/{ownerId}/addcar/{carId}")
    @Operation(
            summary = "Add car to owner",
            description = "Add a car to the owner's list of cars"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Car added to owner successfully",
            content = @Content(schema = @Schema(implementation = Owner.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request, error adding car to owner"
    )
    public ResponseEntity<Owner> addCarToOwner(
            @Parameter(
                    description = "ID of the owner",
                    required = true
            ) @PathVariable Long ownerId,
            @Parameter(
                    description = "ID of the car to be added",
                    required = true
            ) @PathVariable Long carId) {

        ResponseEntity<Owner> responseEntity = ownerService.addCarToOwner(ownerId, carId);

        // Проверка наличия владельца и автомобиля
        if (responseEntity.getStatusCode().is4xxClientError()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(responseEntity.getBody());
    }

    // Эндпоинт для открепления машины от владельца
    @DeleteMapping("/{ownerId}/cars/{carId}")
    @Operation(
            summary = "Detach car from owner",
            description = "Detach a car from the owner's list of cars"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Car detached from owner successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request, error detaching car from owner"
    )
    public ResponseEntity<Void> detachCarFromOwner(
            @Parameter(
                    description = "ID of the owner",
                    required = true
            ) @PathVariable Long ownerId,
            @Parameter(
                    description = "ID of the car to be detached",
                    required = true
            ) @PathVariable Long carId) {
        return ownerService.detachCarFromOwner(ownerId, carId) ?
                ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
