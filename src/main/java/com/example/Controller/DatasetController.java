package com.example.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Entity.DatasetRecord;
import com.example.Service.DatasetService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/dataset/")
public class DatasetController {
    private final DatasetService service;

    public DatasetController(DatasetService service) {
        this.service = service;
    }

    @PostMapping("{datasetName}/record")
    public ResponseEntity<Map<String, Object>> insertRecord(@Valid @RequestBody DatasetRecord datasetRecord,
            @PathVariable String datasetName) {

        // Save record (using service layer)
        DatasetRecord savedRecord = service.insertRecord(datasetName, datasetRecord);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Record added successfully");
        response.put("dataset", datasetName);
        response.put("recordId", savedRecord.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{datasetName}/query", params = "groupBy")
    public ResponseEntity<?> queryDatasetGrouped(
            @PathVariable String datasetName,
            @RequestParam String groupBy) {

        Map<String, List<DatasetRecord>> groupedRecords = service.groupByField(datasetName, groupBy);

        if (groupedRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(Map.of("groupedRecords", groupedRecords));
    }

    @GetMapping(value = "{datasetName}/query", params = { "sortBy", "order" })
    public ResponseEntity<?> queryDatasetSorted(
            @PathVariable String datasetName,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<DatasetRecord> sortedRecords = service.getSortedRecords(datasetName, sortBy, order);
        Map<String, Object> response = new HashMap<>();
        response.put("sortedRecords", sortedRecords);

        return ResponseEntity.ok(response);
    }
}
