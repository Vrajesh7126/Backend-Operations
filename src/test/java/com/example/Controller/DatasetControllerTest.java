package com.example.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.Entity.DatasetRecord;
import com.example.Exception.GlobalExceptionHandler;
import com.example.Exception.InvalidFieldException;
import com.example.Service.DatasetService;
import org.springframework.http.MediaType;

@WebMvcTest(DatasetController.class)
@Import(GlobalExceptionHandler.class)
public class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetService datasetService;

    private DatasetRecord record;

    @BeforeEach
    void setup() {
        record = new DatasetRecord();
        record.setId(1L);
        record.setDatasetName("TestDS");
        record.setName("Alice");
        record.setAge(25);
        record.setDepartment("Engineering");
    }

    @Test
    void testAddRecordSuccess() throws Exception {
        when(datasetService.existsById(1L)).thenReturn(false);
        when(datasetService.insertRecord(eq("TestDS"), any(DatasetRecord.class))).thenReturn(record);

        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.dataset").value("TestDS"))
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void testAddRecordDuplicateId() throws Exception {
        when(datasetService.existsById(1L)).thenReturn(true);

        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Record with this ID already exists"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testAddWithInvalidJson() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":"twenty five",
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithissingId() throws Exception {
        String json = """
                    {
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithMissingName() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithMissgingAge() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithMissingDepartment() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithInvalidName() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice123",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithInvalidAge() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":-5,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithInvalidDepartment() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25,
                        "department":"Eng123"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithNegativeId() throws Exception {
        String json = """
                    {
                        "id":-1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithNegativeAge() throws Exception {
        String json = """
                    {
                        "id":1,
                        "datasetName":"TestDS",
                        "name":"Alice",
                        "age":-25,
                        "department":"Engineering"
                    }
                """;

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithEmptyRequestBody() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/dataset/TestDS/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithgetGroupRecords() throws Exception {
        when(datasetService.groupByField(eq("TestDS"), eq("department")))
                .thenReturn(Map.of("Engineering", List.of(record)));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "department")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("Alice"));
    }

    @Test
    void testWithgetGroupRecordsNoContent() throws Exception {
        when(datasetService.groupByField(eq("TestDS"), eq("department")))
                .thenReturn(Map.of());

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "department")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testWithGroupById() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(30);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(28);
        record3.setDepartment("HR");

        // Mock service response with multiple groups
        when(datasetService.groupByField(eq("TestDS"), eq("id")))
                .thenReturn(Map.of(
                        "1", List.of(record),
                        "2", List.of(record2),
                        "3", List.of(record3)));
        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.1[0].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.2[0].name").value("Bob"))
                .andExpect(jsonPath("$.groupedRecords.3[0].name").value("Charlie"));
    }

    @Test
    void testWithGroupByNameMultipleEntries() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Alice"); // same name to test multiple entries under the same key
        record2.setAge(30);
        record2.setDepartment("HR");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Bob"); // different name
        record3.setAge(28);
        record3.setDepartment("Engineering");

        when(datasetService.groupByField(eq("TestDS"), eq("name")))
                .thenReturn(Map.of(
                        "Alice", List.of(record, record2),
                        "Bob", List.of(record3)));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "name")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Alice[0].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.Alice[1].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.Bob[0].name").value("Bob"));
    }

    @Test
    void testWithGroupByAgeMultipleEntries() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(25); // same age as record
        record2.setDepartment("HR");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(30); // different age
        record3.setDepartment("Engineering");

        when(datasetService.groupByField(eq("TestDS"), eq("age")))
                .thenReturn(Map.of(
                        "25", List.of(record, record2),
                        "30", List.of(record3)));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "age")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.25[0].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.25[1].name").value("Bob"))
                .andExpect(jsonPath("$.groupedRecords.30[0].name").value("Charlie"));
    }

    @Test
    void testWithGroupByDepartmentMultipleEntries() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering"); // same department

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(30);
        record3.setDepartment("HR"); // different department

        when(datasetService.groupByField(eq("TestDS"), eq("department")))
                .thenReturn(Map.of(
                        "Engineering", List.of(record, record2),
                        "HR", List.of(record3)));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "department")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.Engineering[1].name").value("Bob"))
                .andExpect(jsonPath("$.groupedRecords.HR[0].name").value("Charlie"));
    }

    @Test
    void testWithInvalidGroupByField() throws Exception {
        when(datasetService.groupByField(eq("TestDS"), eq("invalidField")))
                .thenThrow(new InvalidFieldException("Unsupported groupBy field: invalidField"));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "invalidField")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unsupported groupBy field: invalidField"));
    }

    @Test
    void testWithEmptyGroupByField() throws Exception {
        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBasicSortByMultipleEntries() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(30);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(28);
        record3.setDepartment("HR");

        // Mock service response with multiple groups
        when(datasetService.groupByField(eq("TestDS"), eq("department")))
                .thenReturn(Map.of(
                        "Engineering", List.of(record, record2),
                        "HR", List.of(record3)));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("groupBy", "department")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("Alice"))
                .andExpect(jsonPath("$.groupedRecords.Engineering[1].name").value("Bob"))
                .andExpect(jsonPath("$.groupedRecords.HR[0].name").value("Charlie"));
    }

    @Test
    void testGetSortedRecordsMultipleEntries() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        DatasetRecord record4 = new DatasetRecord();
        record4.setId(4L);
        record4.setDatasetName("TestDS");
        record4.setName("David");
        record4.setAge(30);
        record4.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("age"), eq("asc")))
                .thenReturn(List.of(record3, record, record2, record4));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "age")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Charlie"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Bob"))
                .andExpect(jsonPath("$.sortedRecords[3].name").value("David"));
    }

    @Test
    void testGetSortedRecordsDescOrder() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        DatasetRecord record4 = new DatasetRecord();
        record4.setId(4L);
        record4.setDatasetName("TestDS");
        record4.setName("David");
        record4.setAge(30);
        record4.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("age"), eq("desc")))
                .thenReturn(List.of(record4, record2, record, record3));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "age")
                .param("order", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("David"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Bob"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[3].name").value("Charlie"));
    }

    @Test
    void testGetSortedRecordsDefaultOrder() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        List<DatasetRecord> sortedList = List.of(record3, record, record2);

        when(datasetService.getSortedRecords(anyString(), anyString(), anyString()))
                .thenReturn(sortedList);

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "age")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Charlie"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Bob"));
    }

    @Test
    void testSortedWithId() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("id"), eq("asc")))
                .thenReturn(List.of(record, record2, record3));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "id")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Bob"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Charlie"));
    }

    @Test
    void testSortedWithName() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("name"), eq("asc")))
                .thenReturn(List.of(record, record2, record3));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "name")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Bob"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Charlie"));
    }

    @Test
    void testSortedWithDepartment() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("department"), eq("asc")))
                .thenReturn(List.of(record3, record, record2));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "department")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Charlie"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Bob"));
    }

    @Test
    void testSortedWithAge() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("age"), eq("asc")))
                .thenReturn(List.of(record3, record, record2));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "age")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Charlie"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Bob"));
    }

    @Test
    void testSortedWithDatasetName() throws Exception {
        DatasetRecord record2 = new DatasetRecord();
        record2.setId(2L);
        record2.setDatasetName("TestDS");
        record2.setName("Bob");
        record2.setAge(28);
        record2.setDepartment("Engineering");

        DatasetRecord record3 = new DatasetRecord();
        record3.setId(3L);
        record3.setDatasetName("TestDS");
        record3.setName("Charlie");
        record3.setAge(22);
        record3.setDepartment("BCA");

        when(datasetService.getSortedRecords(eq("TestDS"), eq("datasetName"), eq("asc")))
                .thenReturn(List.of(record, record2, record3));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "datasetName")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Alice"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Bob"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("Charlie"));
    }

    @Test
    void testGetSortedRecordsInvalidField() throws Exception {
        when(datasetService.getSortedRecords(eq("TestDS"), eq("invalidField"), eq("asc")))
                .thenThrow(new InvalidFieldException("Unsupported sortBy field: invalidField"));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "invalidField")
                .param("order", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unsupported sortBy field: invalidField"));
    }

    @Test
    void testGetSortedRecordsInvalidOrder() throws Exception {
        when(datasetService.getSortedRecords(eq("TestDS"), eq("age"), eq("invalidOrder")))
                .thenThrow(new IllegalArgumentException("Invalid sort order: invalidOrder. Use 'asc' or 'desc'."));

        mockMvc.perform(get("/api/dataset/TestDS/query")
                .param("sortBy", "age")
                .param("order", "invalidOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid sort order: invalidOrder. Use 'asc' or 'desc'."));
    }

}
