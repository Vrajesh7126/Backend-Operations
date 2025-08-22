package com.example.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.Entity.DatasetRecord;
import com.example.Exception.DatasetNotFoundException;
import com.example.Exception.InvalidFieldException;
import com.example.Repository.DatasetRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DatasetService {
    private final DatasetRepository repository;

    public DatasetService(DatasetRepository repository) {
        this.repository = repository;
    }

    public DatasetRecord insertRecord(String datasetName, DatasetRecord datasetRecord) {
        datasetRecord.setDatasetName(datasetName);
        return repository.save(datasetRecord);
    }

    public Map<String, List<DatasetRecord>> groupByField(String datasetName, String groupBy) {
        List<DatasetRecord> records = repository.findByDatasetName(datasetName);
        if (records.isEmpty()) {
            throw new DatasetNotFoundException("No records found for dataset: " + datasetName);
        }

        try {
            Field field = DatasetRecord.class.getDeclaredField(groupBy);
            field.setAccessible(true);
            return records.stream()
                    .collect(Collectors.groupingBy(record -> {
                        try {
                            Object value = field.get(record);
                            return value != null ? value.toString() : "null";
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Error accessing field: " + groupBy, e);
                        }
                    }));
        } catch (NoSuchFieldException e) {
            throw new InvalidFieldException("Unsupported groupBy field: " + groupBy);
        }
    }

    public List<DatasetRecord> getSortedRecords(String datasetName, String sortBy, String order) {
        List<DatasetRecord> records = repository.findByDatasetName(datasetName);
        if (records.isEmpty()) {
            throw new DatasetNotFoundException("No records found for dataset: " + datasetName);
        }

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        return repository.findByDatasetName(datasetName, sort);
    }
}
