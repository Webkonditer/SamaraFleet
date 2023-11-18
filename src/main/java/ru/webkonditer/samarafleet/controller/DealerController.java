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
import ru.webkonditer.samarafleet.model.Dealer;
import ru.webkonditer.samarafleet.model.Owner;
import ru.webkonditer.samarafleet.service.DealerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dealers")
public class DealerController {

    private final DealerService dealerService;

    @Autowired
    public DealerController(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    @GetMapping
    @Operation(summary = "Get all dealers", description = "Get a list of all dealers")
    public List<Dealer> getAllDealers() {
        return dealerService.getAllDealers();
    }

    @GetMapping("/{dealerId}")
    @Operation(
            summary = "Get dealer by ID",
            description = "Get detailed information about a dealer by its ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Dealer found",
            content = @Content(schema = @Schema(implementation = Dealer.class))
    )
    @ApiResponse(responseCode = "404", description = "Dealer not found")
    public ResponseEntity<Dealer> getDealerById(
            @Parameter(description = "ID of the dealer to be retrieved") @PathVariable Long dealerId
    ) {
        return dealerService.getDealerById(dealerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new dealer", description = "Create a new dealer with the provided details")
    @ApiResponse(
            responseCode = "201",
            description = "Dealer created",
            content = @Content(schema = @Schema(implementation = Dealer.class))
    )
    public ResponseEntity<Dealer> createDealer(
            @RequestBody Dealer dealer
    ) {
        Dealer createdDealer = dealerService.createDealer(dealer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDealer);
    }

    @PutMapping("/{dealerId}")
    @Operation(summary = "Update dealer details", description = "Update the details of an existing dealer")
    @ApiResponse(
            responseCode = "200",
            description = "Dealer updated",
            content = @Content(schema = @Schema(implementation = Dealer.class))
    )
    @ApiResponse(responseCode = "404", description = "Dealer not found")
    public ResponseEntity<Dealer> updateDealer(
            @Parameter(description = "ID of the dealer to be updated") @PathVariable Long dealerId,
            @RequestBody Dealer updatedDealer
    ) {
        Dealer updated = dealerService.updateDealer(dealerId, updatedDealer);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{dealerId}")
    @Operation(summary = "Delete a dealer", description = "Delete a dealer by its ID")
    @ApiResponse(responseCode = "204", description = "Dealer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Dealer not found")
    public ResponseEntity<Void> deleteDealer(
            @Parameter(description = "ID of the dealer to be deleted") @PathVariable Long dealerId
    ) {
        dealerService.deleteDealer(dealerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{dealerId}/addowner/{ownerId}")
    @Operation(
            summary = "Add owner to dealer",
            description = "Add an owner to the specified dealer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Owner added to the dealer",
            content = @Content(schema = @Schema(implementation = Dealer.class))
    )
    @ApiResponse(responseCode = "400", description = "Bad request, dealer or owner not found")
    public ResponseEntity<Dealer> addCarToOwner(
            @Parameter(description = "ID of the dealer to which the owner is to be added") @PathVariable Long dealerId,
            @Parameter(description = "ID of the owner to be added to the dealer") @PathVariable Long ownerId
    ) {
        ResponseEntity<Dealer> responseEntity = dealerService.addOwnerToDealer(dealerId, ownerId);

        // Check for the presence of the dealer and owner
        if (responseEntity.getStatusCode().is4xxClientError()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(responseEntity.getBody());
    }

    @GetMapping("/{dealerId}/owners")
    @Operation(
            summary = "Get all owners of a dealer",
            description = "Get a list of all owners associated with the specified dealer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of owners",
            content = @Content(schema = @Schema(implementation = Owner.class))
    )
    public List<Owner> getAllDealerOwners(
            @Parameter(description = "ID of the dealer for which owners are to be retrieved") @PathVariable Long dealerId
    ) {
        return dealerService.getAllDealerOwners(dealerId);
    }

    @GetMapping("/{dealerId}/cars")
    @Operation(
            summary = "Get all cars of a dealer",
            description = "Get a list of all cars associated with the specified dealer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of cars",
            content = @Content(schema = @Schema(implementation = Car.class))
    )
    public List<Car> getAllDealerCars(
            @Parameter(description = "ID of the dealer for which cars are to be retrieved") @PathVariable Long dealerId
    ) {
        return dealerService.getAllDealerCars(dealerId);
    }

    @DeleteMapping("/{dealerId}/owners/{ownerId}")
    @Operation(
            summary = "Detach owner from dealer",
            description = "Detach an owner from the specified dealer"
    )
    @ApiResponse(responseCode = "204", description = "Owner detached successfully")
    @ApiResponse(responseCode = "400", description = "Bad request, dealer or owner not found")
    public ResponseEntity<Void> detachOwnerFromDealer(
            @Parameter(description = "ID of the dealer from which the owner is to be detached") @PathVariable Long dealerId,
            @Parameter(description = "ID of the owner to be detached from the dealer") @PathVariable Long ownerId
    ) {
        return dealerService.detachOwnerFromDealer(dealerId, ownerId) ?
                ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
