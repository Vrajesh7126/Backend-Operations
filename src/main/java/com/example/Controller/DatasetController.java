package com.example.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

import com.example.Entity.DatasetRecord;
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
 * Provides endpoints for inserting, grouping, and sorting dataset records.
 */
@RestController
@RequestMapping("/api/dataset/")
public class DatasetController {

    private final DatasetService datasetService;

    /**
     * Constructor for DatasetController.
     * 
     * @param datasetService the service layer for dataset operations
     */
    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    /**
     * Inserts a new record into the specified dataset.
     *
     * @param datasetName   the name of the dataset
     * @param datasetRecord the record to insert
     * @return ResponseEntity with operation status and record ID
     */
    @PostMapping("{datasetName}/record")
    public ResponseEntity<Map<String, Object>> addRecordToDataset(
            @PathVariable String datasetName,
            @Valid @RequestBody DatasetRecord datasetRecord) {

        if (datasetRecord.getId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID is required", "status", 400));
        }

        // Check if a record with the same ID already exists
        boolean exists = datasetService.existsById(datasetRecord.getId());
        if (exists) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Record with this ID already exists");
            errorResponse.put("status", 400);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        DatasetRecord savedRecord = datasetService.insertRecord(datasetName, datasetRecord);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Record added successfully");
        response.put("dataset", datasetName);
        response.put("recordId", savedRecord.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves grouped records from the specified dataset by a given field.
     *
     * @param datasetName the name of the dataset
     * @param groupBy     the field to group by
     * @return ResponseEntity with grouped records or no content if empty
     */
    @GetMapping(value = "{datasetName}/query", params = "groupBy")
    public ResponseEntity<?> getGroupedRecords(
            @PathVariable String datasetName,
            @RequestParam String groupBy) {

        if (groupBy == null || groupBy.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "groupBy field cannot be empty"));
        }

        Map<String, List<DatasetRecord>> groupedRecords = datasetService.groupByField(datasetName, groupBy);

        if (groupedRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(Map.of("groupedRecords", groupedRecords));
    }

    /**
     * Retrieves sorted records from the specified dataset.
     *
     * @param datasetName the name of the dataset
     * @param sortBy      the field to sort by
     * @param order       the sort order (asc or desc)
     * @return ResponseEntity with sorted records
     */
    @GetMapping(value = "{datasetName}/query", params = { "sortBy" })
    public ResponseEntity<Map<String, List<DatasetRecord>>> getSortedRecords(
            @PathVariable String datasetName,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<DatasetRecord> sortedRecords = datasetService.getSortedRecords(datasetName, sortBy, order);

        return ResponseEntity.ok(Map.of("sortedRecords", sortedRecords));
    }
}
