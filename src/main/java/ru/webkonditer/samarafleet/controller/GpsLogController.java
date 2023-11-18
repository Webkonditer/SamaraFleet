package ru.webkonditer.samarafleet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.webkonditer.samarafleet.service.GpsLogService;

@Controller
public class GpsLogController {

    private final GpsLogService gpsLogService;

    @Autowired
    public GpsLogController(GpsLogService gpsLogService) {
        this.gpsLogService = gpsLogService;
    }

    @PostMapping("/upload-gps-log")
    @Operation(
            summary = "Upload GPS log file",
            description = "Upload a GPS log file for processing"
    )
    @ApiResponse(
            responseCode = "200",
            description = "GPS log processed successfully",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request, error processing GPS log file",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<String> uploadGpsLog(
            @Parameter(
                    description = "GPS log file to be uploaded",
                    required = true
            ) @RequestParam("file") MultipartFile file
    ) {
        try {
            String result = gpsLogService.processGpsLog(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing GPS log file: " + e.getMessage());
        }
    }
}
