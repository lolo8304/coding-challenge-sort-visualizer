package sorty.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import sorty.SortDirection;
import sorty.algorithms.impl.BogoSorter;
import sorty.algorithms.impl.CocktailSorter;
import sorty.algorithms.impl.CombSorter;
import sorty.algorithms.impl.GnomeSorter;
import sorty.algorithms.impl.IntroSorter;
import sorty.algorithms.impl.TimSorter;

class AdditionalSorterTest {
    @Test
    void sortsAscending() {
        for (var sorter : sorters()) {
            sorter.setSorter(new ProtocolInspector());
            sorter.setNumbers(new Integer[] {4, 1, 3, 1, -2});

            sorter.sort(SortDirection.ASCENDING);

            assertEquals(Arrays.asList(-2, 1, 1, 3, 4), Arrays.asList(sorter.getNumbers()));
        }
    }

    @Test
    void sortsDescending() {
        for (var sorter : sorters()) {
            sorter.setSorter(new ProtocolInspector());
            sorter.setNumbers(new Integer[] {4, 1, 3, 1, -2});

            sorter.sort(SortDirection.DESCENDING);

            assertEquals(Arrays.asList(4, 3, 1, 1, -2), Arrays.asList(sorter.getNumbers()));
        }
    }

    private List<DefaultSorter> sorters() {
        return Stream.of(
            new CocktailSorter(),
            new CombSorter(),
            new GnomeSorter(),
            new TimSorter(),
            new IntroSorter(),
            new BogoSorter()
        ).toList();
    }
}
