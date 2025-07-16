package com.code_mirror.code_service.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("CodingProblem")
public class CodingProblem implements Serializable {

    @Id
    private String id;
    private String title;
    private String description;
    private String language;
    private List<TestCase> exampleTestCases;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCase implements Serializable {
        private String input;
        private String expectedOutput;
    }
}