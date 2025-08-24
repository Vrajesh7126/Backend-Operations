package com.example.Controller;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

import com.example.Entity.DatasetRecord;
import com.example.Exception.DatasetNotFoundException;
import com.example.Exception.InvalidFieldException;
import com.example.Service.DatasetService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for managing dataset records.
 */
@RestController
@RequestMapping("/api/dataset/")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("{datasetName}/record")
    public ResponseEntity<Map<String, Object>> addRecordToDataset(
            @PathVariable String datasetName,
            @Valid @RequestBody DatasetRecord datasetRecord) {

        if (datasetRecord.getId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID is required"));
        }

        if (datasetService.existsById(datasetRecord.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Record with this ID already exists"));
        }

        DatasetRecord savedRecord = datasetService.insertRecord(datasetName, datasetRecord);

        return ResponseEntity.ok(Map.of(
                "message", "Record added successfully",
                "dataset", datasetName,
                "recordId", savedRecord.getId()));
    }

    @GetMapping(value = "{datasetName}/query", params = "groupBy")
    public ResponseEntity<?> getGroupedRecords(
            @PathVariable String datasetName,
            @RequestParam String groupBy) {

        if (groupBy == null || groupBy.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "groupBy field cannot be empty"));
        }

        try {
            Map<String, List<DatasetRecord>> groupedRecords = datasetService.groupByField(datasetName, groupBy);

            if (groupedRecords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(Map.of("groupedRecords", groupedRecords));

        } catch (DatasetNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", ex.getMessage()));

        } catch (InvalidFieldException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping(value = "{datasetName}/query", params = { "sortBy" })
    public ResponseEntity<?> getSortedRecords(
            @PathVariable String datasetName,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        try {
            List<DatasetRecord> sortedRecords = datasetService.getSortedRecords(datasetName, sortBy, order);

            if (sortedRecords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(Map.of("sortedRecords", sortedRecords));

        } catch (DatasetNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", ex.getMessage()));

        } catch (InvalidFieldException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
