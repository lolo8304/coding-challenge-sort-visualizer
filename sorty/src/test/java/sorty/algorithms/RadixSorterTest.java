package sorty.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import sorty.SortDirection;
import sorty.algorithms.impl.RadixSorter;

class RadixSorterTest {
    @Test
    void sortsAscending() {
        var input = new Integer[] {170, 45, 75, 90, 802, 24, 2, 66};
        var sorter = new RadixSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.ASCENDING);

        assertEquals(Arrays.asList(2, 24, 45, 66, 75, 90, 170, 802), Arrays.asList(sorter.getNumbers()));
    }

    @Test
    void sortsDescending() {
        var input = new Integer[] {170, 45, 75, 90, 802, 24, 2, 66};
        var sorter = new RadixSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.DESCENDING);

        assertEquals(Arrays.asList(802, 170, 90, 75, 66, 45, 24, 2), Arrays.asList(sorter.getNumbers()));
    }

    @Test
    void sortsSignedValuesAndDuplicates() {
        var input = new Integer[] {4, -10, 3, 0, -10, Integer.MAX_VALUE, Integer.MIN_VALUE, 4};
        var sorter = new RadixSorter();
        sorter.setSorter(new ProtocolInspector());
        sorter.setNumbers(input);

        sorter.sort(SortDirection.ASCENDING);

        assertEquals(
            Arrays.asList(Integer.MIN_VALUE, -10, -10, 0, 3, 4, 4, Integer.MAX_VALUE),
            Arrays.asList(sorter.getNumbers())
        );
    }
}
