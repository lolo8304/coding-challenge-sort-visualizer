package sorty.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import sorty.SortDirection;
import sorty.algorithms.impl.ShellSorter;

class ShellSorterTest {
    @Test
    void sortsAscending() {
        var input = new Integer[] {4, 1, 3, 1, -2};
        var sorter = new ShellSorter();
        var inspector = new ProtocolInspector();
        sorter.setSorter(inspector);
        sorter.setNumbers(input);

        sorter.sort(SortDirection.ASCENDING);

        assertEquals(Arrays.asList(-2, 1, 1, 3, 4), Arrays.asList(sorter.getNumbers()));
        assertTrue(inspector.compareEvents.size() > 0);
    }

    @Test
    void sortsDescending() {
        var input = new Integer[] {4, 1, 3, 1, -2};
        var sorter = new ShellSorter();
        var inspector = new ProtocolInspector();
        sorter.setSorter(inspector);
        sorter.setNumbers(input);

        sorter.sort(SortDirection.DESCENDING);

        assertEquals(Arrays.asList(4, 3, 1, 1, -2), Arrays.asList(sorter.getNumbers()));
        assertTrue(inspector.compareEvents.size() > 0);
    }

    @Test
    void sortsWithOptimizedGapSequence() {
        var input = new Integer[] {19, 3, 17, 1, 13, 5, 11, 7, 2, 23, 0, 29, 31, 4, 6};
        var sorter = new ShellSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.ASCENDING);

        assertEquals(
            Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 11, 13, 17, 19, 23, 29, 31),
            Arrays.asList(sorter.getNumbers())
        );
    }
}
