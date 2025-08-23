package com.example.Service;

import com.example.Entity.DatasetRecord;
import com.example.Exception.DatasetNotFoundException;
import com.example.Repository.DatasetRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.example.Entity")
class DatasetControllerTest {
        private DatasetService datasetService;

        @Autowired
        private DatasetRepository repository;

        @BeforeEach
        void setup() {
                datasetService = new DatasetService(repository);
        }

        private DatasetRecord createRecord(Long id, String datasetName, String name, int age, String dept) {
                DatasetRecord rec = new DatasetRecord();
                rec.setId(id);
                rec.setDatasetName(datasetName);
                rec.setName(name);
                rec.setAge(age);
                rec.setDepartment(dept);
                return rec;
        }

        @Test
        void testInsertRecord() {
                DatasetRecord rec = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord saved = datasetService.insertRecord("TestDataset", rec);
                assertThat(saved.getId()).isEqualTo(1L);
                assertThat(saved.getDatasetName()).isEqualTo("TestDataset");
                assertThat(saved.getName()).isEqualTo("Alice");
                assertThat(saved.getAge()).isEqualTo(25);
                assertThat(saved.getDepartment()).isEqualTo("Engineering");
        }

        @Test
        void testDatasetNotFound() {
                // Make sure the dataset repository is empty for this dataset
                String nonExistentDataset = "NonExistent";

                // Expect DatasetNotFoundException when calling groupByField
                DatasetNotFoundException ex = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField(nonExistentDataset, "age");
                });

                // Optional: assert the exception message
                assertThat(ex.getMessage()).isEqualTo("No records found for dataset: " + nonExistentDataset);
        }

        @Test
        void testDatasetEmpty() {
                // Make sure the dataset repository is empty for this dataset
                String emptyDataset = "";

                // Expect DatasetNotFoundException when calling groupByField
                DatasetNotFoundException ex = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField(emptyDataset, "age");
                });

                // Optional: assert the exception message
                assertThat(ex.getMessage()).isEqualTo("No records found for dataset: " + emptyDataset);
        }

        @Test
        void testGroupById() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Charlie", 25, "Engineering");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var grouped = datasetService.groupByField("TestDataset", "id");
                assertThat(grouped).hasSize(3);
                assertThat(grouped.get("1")).hasSize(1);
                assertThat(grouped.get("2")).hasSize(1);
                assertThat(grouped.get("3")).hasSize(1);
        }

        @Test
        void testByDataset() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset1", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset2", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset1", "Charlie", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var grouped = datasetService.groupByField("TestDataset1", "name");
                assertThat(grouped).hasSize(2);
                assertThat(grouped.get("Alice")).hasSize(1);
                assertThat(grouped.get("Charlie")).hasSize(1);

                grouped = datasetService.groupByField("TestDataset2", "name");
                assertThat(grouped).hasSize(1);
                assertThat(grouped.get("Bob")).hasSize(1);
        }

        @Test
        void testGroupByName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Alice", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var grouped = datasetService.groupByField("TestDataset", "name");
                assertThat(grouped).hasSize(2);
                assertThat(grouped.get("Alice")).hasSize(2);
                assertThat(grouped.get("Bob")).hasSize(1);
        }

        @Test
        void testGroupByAge() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Charlie", 25, "Engineering");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var grouped = datasetService.groupByField("TestDataset", "age");
                assertThat(grouped).hasSize(2);
                assertThat(grouped.get("25")).hasSize(2);
                assertThat(grouped.get("30")).hasSize(1);
        }

        @Test
        void testGroupByDepartment() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Charlie", 25, "Engineering");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var grouped = datasetService.groupByField("TestDataset", "department");
                assertThat(grouped).hasSize(2);
                assertThat(grouped.get("Engineering")).hasSize(2);
                assertThat(grouped.get("HR")).hasSize(1);
        }

        @Test
        void testInvalidGroupByField() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                repository.save(rec1);

                // Expect InvalidFieldException when calling groupByField with invalid field
                Exception ex = assertThrows(Exception.class, () -> {
                        datasetService.groupByField("TestDataset", "invalidField");
                });

                // Optional: assert the exception message
                assertThat(ex.getMessage()).isEqualTo("Unsupported groupBy field: invalidField");
        }

        @Test
        void testInvalidDatasetName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                repository.save(rec1);

                // Expect DatasetNotFoundException when calling groupByField with null dataset
                // name
                DatasetNotFoundException ex1 = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField(null, "age");
                });
                assertThat(ex1.getMessage()).isEqualTo("No records found for dataset: null");

                // Expect DatasetNotFoundException when calling groupByField with empty dataset
                // name
                DatasetNotFoundException ex2 = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField("", "age");
                });
                assertThat(ex2.getMessage()).isEqualTo("No records found for dataset: ");
        }

        @Test
        void testEmptyDatasetName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                repository.save(rec1);

                // Expect DatasetNotFoundException when calling groupByField with empty dataset
                // name
                DatasetNotFoundException ex = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField("", "age");
                });
                assertThat(ex.getMessage()).isEqualTo("No records found for dataset: ");
        }

        @Test
        void testNullDatasetName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                repository.save(rec1);

                // Expect DatasetNotFoundException when calling groupByField with null dataset
                // name
                DatasetNotFoundException ex = assertThrows(DatasetNotFoundException.class, () -> {
                        datasetService.groupByField(null, "age");
                });
                assertThat(ex.getMessage()).isEqualTo("No records found for dataset: null");
        }

        @Test
        void testSortedById() {
                DatasetRecord rec1 = createRecord(3L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(1L, "TestDataset", "Charlie", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var results = repository.findByDatasetName("TestDataset",
                                org.springframework.data.domain.Sort.by("id").ascending());
                assertThat(results).hasSize(3);
                assertThat(results.get(0).getId()).isEqualTo(1L);
                assertThat(results.get(1).getId()).isEqualTo(2L);
                assertThat(results.get(2).getId()).isEqualTo(3L);
        }

        @Test
        void testSortedByDatasetName() {
                DatasetRecord rec1 = createRecord(1L, "B_Dataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "C_Dataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "A_Dataset", "Charlie", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var results = repository.findByDatasetName("A_Dataset",
                                org.springframework.data.domain.Sort.by("datasetName").ascending());
                assertThat(results).hasSize(1);
                assertThat(results.get(0).getDatasetName()).isEqualTo("A_Dataset");

                results = repository.findByDatasetName("B_Dataset",
                                org.springframework.data.domain.Sort.by("datasetName").ascending());
                assertThat(results).hasSize(1);
                assertThat(results.get(0).getDatasetName()).isEqualTo("B_Dataset");

                results = repository.findByDatasetName("C_Dataset",
                                org.springframework.data.domain.Sort.by("datasetName").ascending());
                assertThat(results).hasSize(1);
                assertThat(results.get(0).getDatasetName()).isEqualTo("C_Dataset");
        }

        @Test
        void testSortedByName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Charlie", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Alice", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Bob", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var results = repository.findByDatasetName("TestDataset",
                                org.springframework.data.domain.Sort.by("name").ascending());
                assertThat(results).hasSize(3);
                assertThat(results.get(0).getName()).isEqualTo("Alice");
                assertThat(results.get(1).getName()).isEqualTo("Bob");
                assertThat(results.get(2).getName()).isEqualTo("Charlie");
        }

        @Test
        void testBasicgetSortedAge() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "HR");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Charlie", 28, "Marketing");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var results = repository.findByDatasetName("TestDataset",
                                org.springframework.data.domain.Sort.by("age").ascending());
                assertThat(results).hasSize(3);
                assertThat(results.get(0).getAge()).isEqualTo(25);
                assertThat(results.get(1).getAge()).isEqualTo(28);
                assertThat(results.get(2).getAge()).isEqualTo(30);
        }

        @Test
        void testSortedByDepartment() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Marketing");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "Engineering");
                DatasetRecord rec3 = createRecord(3L, "TestDataset", "Charlie", 28, "HR");
                repository.save(rec1);
                repository.save(rec2);
                repository.save(rec3);

                var results = repository.findByDatasetName("TestDataset",
                                org.springframework.data.domain.Sort.by("department").ascending());
                assertThat(results).hasSize(3);
                assertThat(results.get(0).getDepartment()).isEqualTo("Engineering");
                assertThat(results.get(1).getDepartment()).isEqualTo("HR");
                assertThat(results.get(2).getDepartment()).isEqualTo("Marketing");
        }

        @Test
        void testFindByDatasetNameWithInvalidSort() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                repository.save(rec1);

                // Expect PropertyReferenceException when calling findByDatasetName with invalid
                // sort field
                assertThrows(
                                org.springframework.data.mapping.PropertyReferenceException.class,
                                () -> repository.findByDatasetName(
                                                "TestDataset",
                                                org.springframework.data.domain.Sort.by("unknownField").descending()));
        }

        @Test
        void testFindByEmptyOrNullDatasetName() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "Alice", 25, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "Bob", 30, "BCA");
                repository.save(rec1);
                repository.save(rec2);

                /* Find by null or empty dataset name without sorting */
                var results = repository.findByDatasetName(null, null);
                assertThat(results).isEmpty();

                results = repository.findByDatasetName("", null);
                assertThat(results).isEmpty();
        }

        @Test
        void testFIndByInvalidSortFieldId() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "myTestName", 30, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "myNewTestName", 15, "BCA");
                repository.save(rec1);
                repository.save(rec2);

                assertThrows(
                                org.springframework.data.mapping.PropertyReferenceException.class,
                                () -> repository.findByDatasetName(
                                                "TestDataset",
                                                org.springframework.data.domain.Sort.by("unknownField").descending()));
        }

        @Test
        void testExistById() {
                DatasetRecord rec1 = createRecord(1L, "TestDataset", "myTestName", 30, "Engineering");
                DatasetRecord rec2 = createRecord(2L, "TestDataset", "myNewTestName", 15, "BCA");
                repository.save(rec1);
                repository.save(rec2);

                /* Exist by exist id */
                boolean exists = repository.existsById(1L);
                assertThat(exists).isTrue();

                exists = repository.existsById(2L);
                assertThat(exists).isTrue();

                /* Exist by non-exist id */
                exists = repository.existsById(3L);
                assertThat(exists).isFalse();
        }
}
