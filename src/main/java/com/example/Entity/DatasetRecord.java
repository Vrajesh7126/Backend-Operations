package com.example.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Entity class representing a record in a dataset.
 * Maps to the "dataset_records" table in the database.
 */
@Entity
@Table(name = "dataset_records")
public class DatasetRecord {

    /**
     * Unique identifier for the dataset record.
     */
    @Id
    @Min(value = 0, message = "Id must be a positive integer")
    private Long id;

    /**
     * Name of the dataset this record belongs to.
     */
    private String datasetName;

    /**
     * Name of the person or entity.
     * Cannot be blank.
     * Can not contain numbers or special characters, only letters and spaces.
     */
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String name;

    /**
     * Age of the person or entity.
     * Cannot be null and must be a positive integer.
     */
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be a positive integer")
    private Integer age;

    /**
     * Department associated with the record.
     * Cannot be blank and must contain only letters and spaces.
     */
    @NotBlank(message = "Department is required")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Department must contain only letters and spaces")
    private String department;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
