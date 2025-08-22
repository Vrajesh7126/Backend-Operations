package com.example.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Entity.DatasetRecord;

import org.springframework.data.domain.Sort;

@Repository
public interface DatasetRepository extends JpaRepository<DatasetRecord, Long> {
    List<DatasetRecord> findByDatasetName(String datasetName);

    List<DatasetRecord> findByDatasetName(String datasetName, Sort sort);
}