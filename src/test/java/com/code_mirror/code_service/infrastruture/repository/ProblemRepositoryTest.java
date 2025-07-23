package com.code_mirror.code_service.infrastruture.repository;

import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import com.code_mirror.code_service.infrastructure.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProblemRepositoryTest {

    private ProblemRepository problemRepository;

    @BeforeEach
    void setUp() {
        problemRepository = Mockito.mock(ProblemRepository.class);
    }

    @Test
    void testSaveProblem() {
        CodingProblem problem = new CodingProblem("1", "Sumar dos números", "Suma dos enteros", "Java", null);

        when(problemRepository.save(problem)).thenReturn(problem);

        CodingProblem savedProblem = problemRepository.save(problem);

        assertThat(savedProblem).isNotNull();
        assertThat(savedProblem.getId()).isEqualTo("1");
        verify(problemRepository, times(1)).save(problem);
    }

    @Test
    void testFindById() {
        CodingProblem problem = new CodingProblem("2", "Multiplicar", "Multiplica dos números", "Python", null);

        when(problemRepository.findById("2")).thenReturn(Optional.of(problem));

        Optional<CodingProblem> result = problemRepository.findById("2");

        assertThat(result).isPresent();
        assertThat(result.get().getLanguage()).isEqualTo("Python");
        verify(problemRepository).findById("2");
    }

    @Test
    void testDeleteById() {
        doNothing().when(problemRepository).deleteById("3");

        problemRepository.deleteById("3");

        verify(problemRepository, times(1)).deleteById("3");
    }
}
