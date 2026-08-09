package sorty.algorithms;

import org.junit.jupiter.api.Test;
import sorty.SortDirection;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertSorterTest {

    @Test
    public void sort_3_2_ok() {
        // Arrange
        var input = new Integer[]{3, 2};
        var expected = new Integer[]{2, 3};
        var inspector = new ProtocolInspector();

        var sorter = new InsertSorter();
        sorter.setSorter(inspector);
        sorter.setNumbers(input);

        // Act
        sorter.sort(SortDirection.ASCENDING);

        // Assert
        assertEquals(Arrays.asList(expected), Arrays.asList(sorter.getNumbers()));
        assertEquals(1, inspector.compareEvents.size());
        assertEquals(0, inspector.swapEvents.size());
    }


    @Test
    public void sort_3_2_1_ok() {
        // Arrange
        var input = new Integer[]{3, 2, 1};
        var expected = new Integer[]{1, 2, 3};
        var inspector = new ProtocolInspector();

        var sorter = new InsertSorter();
        sorter.setSorter(inspector);
        sorter.setNumbers(input);

        // Act
        sorter.sort(SortDirection.ASCENDING);

        // Assert
        assertEquals(Arrays.asList(expected), Arrays.asList(sorter.getNumbers()));
        assertEquals(2, inspector.compareEvents.size());
        assertEquals(0, inspector.swapEvents.size());
    }


}
