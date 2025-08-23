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

/**
 * Service class for handling dataset operations such as insert, group, and
 * sort.
 */
@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;

    /**
     * Constructor for DatasetService.
     *
     * @param datasetRepository the repository for dataset records
     */
    public DatasetService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    /**
     * Inserts a new record into the specified dataset.
     *
     * @param datasetName   the name of the dataset
     * @param datasetRecord the record to insert
     * @return the saved DatasetRecord
     */
    public DatasetRecord insertRecord(String datasetName, DatasetRecord datasetRecord) {
        datasetRecord.setDatasetName(datasetName);
        return datasetRepository.save(datasetRecord);
    }

    /**
     * Groups records of a dataset by the specified field.
     *
     * @param datasetName  the name of the dataset
     * @param groupByField the field to group by
     * @return a map where the key is the field value and the value is the list of
     *         records
     * @throws DatasetNotFoundException if no records are found for the dataset
     * @throws InvalidFieldException    if the groupBy field is invalid
     */
    public Map<String, List<DatasetRecord>> groupByField(String datasetName, String groupByField) {
        List<DatasetRecord> records = datasetRepository.findByDatasetName(datasetName);
        if (records.isEmpty()) {
            throw new DatasetNotFoundException("No records found for dataset: " + datasetName);
        }

        // Validate that the groupByField exists in DatasetRecord
        Field field;
        try {
            field = DatasetRecord.class.getDeclaredField(groupByField);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InvalidFieldException("Unsupported groupBy field: " + groupByField);
        }

        // Group records by the specified field
        return records.stream()
                .collect(Collectors.groupingBy(record -> {
                    try {
                        Object value = field.get(record);
                        return value != null ? value.toString() : "null";
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field: " + groupByField, e);
                    }
                }));
    }

    /**
     * Retrieves sorted records from the specified dataset.
     *
     * @param datasetName the name of the dataset
     * @param sortByField the field to sort by
     * @param sortOrder   the sort order ("asc" or "desc")
     * @return a list of sorted DatasetRecord objects
     * @throws DatasetNotFoundException if no records are found for the dataset
     */
    public List<DatasetRecord> getSortedRecords(String datasetName, String sortByField, String sortOrder) {
        // Validate sortByField
        try {
            DatasetRecord.class.getDeclaredField(sortByField);
        } catch (NoSuchFieldException e) {
            throw new InvalidFieldException("Unsupported sortBy field: " + sortByField);
        }

        // Validate sortOrder
        Sort.Direction direction;
        if (sortOrder.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        } else if (sortOrder.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Invalid sort order: " + sortOrder + ". Use 'asc' or 'desc'.");
        }

        Sort sort = Sort.by(direction, sortByField);
        List<DatasetRecord> records = datasetRepository.findByDatasetName(datasetName, sort);
        if (records.isEmpty()) {
            throw new DatasetNotFoundException("No records found for dataset: " + datasetName);
        }
        return records;
    }

    /**
     * Checks if a record with the given ID exists in the dataset repository.
     *
     * @param id the ID of the record to check
     * @return true if a record with the given ID exists, false otherwise
     */
    public boolean existsById(Long id) {
        return datasetRepository.existsById(id);
    }
}
