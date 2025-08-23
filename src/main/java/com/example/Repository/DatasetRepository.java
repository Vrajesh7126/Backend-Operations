package com.example.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import com.example.Entity.DatasetRecord;

/**
 * Repository interface for DatasetRecord entity.
 * Provides methods to perform CRUD operations and custom queries on dataset
 * records.
 */
@Repository
public interface DatasetRepository extends JpaRepository<DatasetRecord, Long> {

    /**
     * Retrieves all records belonging to the specified dataset.
     *
     * @param datasetName the name of the dataset
     * @return a list of DatasetRecord objects
     */
    List<DatasetRecord> findByDatasetName(String datasetName);

    /**
     * Retrieves all records belonging to the specified dataset, sorted according to
     * the provided Sort object.
     *
     * @param datasetName the name of the dataset
     * @param sort        the sorting criteria
     * @return a sorted list of DatasetRecord objects
     */
    List<DatasetRecord> findByDatasetName(String datasetName, Sort sort);
}