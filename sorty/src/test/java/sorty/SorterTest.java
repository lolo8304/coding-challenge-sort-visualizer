package sorty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class SorterTest {
    @Test
    void sortsAscendingWithoutMutatingInput() {
        List<Integer> input = List.of(4, 1, 3, 1, -2);

        var sorted = new TestSorter(input, SortDirection.ASCENDING);
        sorted.sort();

        assertEquals(List.of(-2, 1, 1, 3, 4), sorted.getNumbersAsList());
        assertEquals(List.of(4, 1, 3, 1, -2), input);
    }

    @Test
    void sortsDescending() {
        var sorted = new TestSorter(List.of(4, 1, 3, 1, -2), SortDirection.DESCENDING);
        sorted.sort();

        assertEquals(List.of(4, 3, 1, 1, -2), sorted.getNumbersAsList());
    }

    @Test
    void constructsInsertionSorter() {
        var sorter = new Sorter(3, 1, 10, SortDirection.ASCENDING, 1, SortAlgorithm.INSERT, SortSpeed.FAST);

        assertEquals(SortAlgorithm.INSERT, sorter.getAlgorithm());
        assertEquals("InsertionSorter", sorter.getSorter().getClass().getSimpleName());
    }

    @Test
    void constructsMergeSorter() {
        var sorter = new Sorter(3, 1, 10, SortDirection.ASCENDING, 1, SortAlgorithm.MERGE, SortSpeed.FAST);

        assertEquals(SortAlgorithm.MERGE, sorter.getAlgorithm());
        assertEquals("MergeSorter", sorter.getSorter().getClass().getSimpleName());
    }

    @Test
    void constructsQuickSorter() {
        var sorter = new Sorter(3, 1, 10, SortDirection.ASCENDING, 1, SortAlgorithm.QUICK, SortSpeed.FAST);

        assertEquals(SortAlgorithm.QUICK, sorter.getAlgorithm());
        assertEquals("QuickSorter", sorter.getSorter().getClass().getSimpleName());
    }

    @Test
    void constructsHeapSorter() {
        var sorter = new Sorter(3, 1, 10, SortDirection.ASCENDING, 1, SortAlgorithm.HEAP, SortSpeed.FAST);

        assertEquals(SortAlgorithm.HEAP, sorter.getAlgorithm());
        assertEquals("HeapSorter", sorter.getSorter().getClass().getSimpleName());
    }

    @Test
    void constructsShellSorter() {
        var sorter = new Sorter(3, 1, 10, SortDirection.ASCENDING, 1, SortAlgorithm.SHELL, SortSpeed.FAST);

        assertEquals(SortAlgorithm.SHELL, sorter.getAlgorithm());
        assertEquals("ShellSorter", sorter.getSorter().getClass().getSimpleName());
    }
}
