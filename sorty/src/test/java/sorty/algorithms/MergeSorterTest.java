package sorty.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import sorty.SortDirection;
import sorty.algorithms.impl.MergeSorter;

class MergeSorterTest {
    @Test
    void sortsAscending() {
        var input = new Integer[] {4, 1, 3, 1, -2};
        var sorter = new MergeSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.ASCENDING);

        assertEquals(Arrays.asList(-2, 1, 1, 3, 4), Arrays.asList(sorter.getNumbers()));
    }

    @Test
    void sortsDescending() {
        var input = new Integer[] {4, 1, 3, 1, -2};
        var sorter = new MergeSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.DESCENDING);

        assertEquals(Arrays.asList(4, 3, 1, 1, -2), Arrays.asList(sorter.getNumbers()));
    }
}
