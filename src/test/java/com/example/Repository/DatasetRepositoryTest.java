package com.example.Repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.Entity.DatasetRecord;

@DataJpaTest
@EntityScan(basePackages = "com.example.Entity")
public class DatasetRepositoryTest {

    @Autowired
    private DatasetRepository repository;

    @Test
    void testFindByDatasetName() {
        DatasetRecord rec1 = new DatasetRecord();
        rec1.setId(1L);
        rec1.setDatasetName("TestDataset");
        rec1.setName("myTestName");
        rec1.setAge(30);
        rec1.setDepartment("Engineering");
        repository.save(rec1);

        DatasetRecord rec2 = new DatasetRecord();
        rec2.setId(2L);
        rec2.setDatasetName("TestDataset");
        rec2.setName("myNewTestName");
        rec2.setAge(15);
        rec2.setDepartment("BCA");
        repository.save(rec2);

        /* Find by exist dataset name */
        List<DatasetRecord> results = repository.findByDatasetName("TestDataset");
        assertThat(results).hasSize(2);

        /* Find by non-exist dataset name */
        results = repository.findByDatasetName("NonExistentDataset");
        assertThat(results).isEmpty();

        /* Find by null or empty dataset name */
        results = repository.findByDatasetName(null);
        assertThat(results).isEmpty();

        results = repository.findByDatasetName("");
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByDatasetNameWithSorting() {
        DatasetRecord rec1 = new DatasetRecord();
        rec1.setId(1L);
        rec1.setDatasetName("TestDataset");
        rec1.setName("myTestName");
        rec1.setAge(30);
        rec1.setDepartment("Engineering");
        repository.save(rec1);

        DatasetRecord rec2 = new DatasetRecord();
        rec2.setId(2L);
        rec2.setDatasetName("TestDataset");
        rec2.setName("myNewTestName");
        rec2.setAge(15);
        rec2.setDepartment("BCA");
        repository.save(rec2);

        /* Find by exist dataset name with sorting */
        List<DatasetRecord> results = repository.findByDatasetName("TestDataset",
                org.springframework.data.domain.Sort.by("age").ascending());
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getAge()).isEqualTo(15);
        assertThat(results.get(1).getAge()).isEqualTo(30);

        /* Find by non-exist dataset name with sorting */
        results = repository.findByDatasetName("NonExistentDataset",
                org.springframework.data.domain.Sort.by("age").ascending());
        assertThat(results).isEmpty();

        /* Find by null or empty dataset name with sorting */
        results = repository.findByDatasetName(null, org.springframework.data.domain.Sort.by("age").ascending());
        assertThat(results).isEmpty();

        results = repository.findByDatasetName("", org.springframework.data.domain.Sort.by("age").ascending());
        assertThat(results).isEmpty();

        results = repository.findByDatasetName("TestDataset", null);
        assertThat(results).hasSize(2);

        results = repository.findByDatasetName("TestDataset",
                org.springframework.data.domain.Sort.by("id").descending());
        assertThat(results).hasSize(2);
        assertThat(results.get(1).getId()).isEqualTo(1L);
        assertThat(results.get(0).getId()).isEqualTo(2L);

        results = repository.findByDatasetName("TestDataset",
                org.springframework.data.domain.Sort.by("name").ascending());
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("myNewTestName");
        assertThat(results.get(1).getName()).isEqualTo("myTestName");

        results = repository.findByDatasetName("TestDataset",
                org.springframework.data.domain.Sort.by("department").descending());
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDepartment()).isEqualTo("Engineering");
        assertThat(results.get(1).getDepartment()).isEqualTo("BCA");

        results = repository.findByDatasetName("TestDataset",
                org.springframework.data.domain.Sort.by("datasetName").descending());
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDatasetName()).isEqualTo("TestDataset");
        assertThat(results.get(1).getDatasetName()).isEqualTo("TestDataset");
    }

    @Test
    void testFindByDatasetNameWithInvalidSort() {
        assertThrows(
                org.springframework.data.mapping.PropertyReferenceException.class,
                () -> repository.findByDatasetName(
                        "TestDataset",
                        org.springframework.data.domain.Sort.by("unknownField").descending()));
    }
}
