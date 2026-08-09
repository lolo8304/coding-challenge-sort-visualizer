package sorty;

import sorty.ui.NoOpUiDelegate;

import java.util.Arrays;
import java.util.List;

public class TestSorter extends Sorter {

    private Integer[] numbers;

    public TestSorter(int n, int from, int to, SortDirection direction, int seed) {
        super(n, from, to, direction, seed, SortSpeed.FAST, new NoOpUiDelegate());
    }
    public TestSorter(List<Integer> numbers, SortDirection direction) {
        this(numbers.toArray(Integer[]::new), direction);
    }

    public TestSorter(Integer[] numbers, SortDirection direction) {
        super(numbers.length, 1, 100, direction, 0, SortSpeed.FAST, new NoOpUiDelegate());
        this.numbers = Arrays.stream(numbers).toArray(Integer[]::new);
    }

    @Override
    public Integer[] sort() {
        this.numbers = super.sort();
        return this.numbers;
    }

    public Integer[] getNumbers() {
        return numbers;
    }
    public List<Integer> getNumbersAsList() {
        return Arrays.asList(numbers);
    }

    @Override
    public Integer[] randomNumbers() {
        return this.numbers;
    }
}
