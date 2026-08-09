package sorty;

import sorty.algorithms.BubbleSorter;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.SorterProtocol;
import sorty.ui.NoOpUiDelegate;
import sorty.ui.NumbersAwareUiDelegate;

import java.util.Random;

public class Sorter {
    private final int n;
    private final int from;
    private final int to;
    private final SortDirection direction;
    private final Random random;
    private final DefaultSorter sorter;
    private final SorterProtocol uiDelegate;
    private final SortSpeed speed;

    public Sorter(int n, SortDirection direction) {
        this(n, 1, 100, direction, 0, new NoOpUiDelegate(), SortSpeed.MEDIUM);
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed) {
        this(n, from, to, direction, seed, new NoOpUiDelegate(), SortSpeed.MEDIUM);
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed, SorterProtocol uiDelegate) {
        this(n, from, to, direction, seed, uiDelegate, SortSpeed.MEDIUM);
    }

    public Sorter(
        int n,
        int from,
        int to,
        SortDirection direction,
        int seed,
        SorterProtocol uiDelegate,
        SortSpeed speed
    ) {
        this.n = n;
        this.from = from;
        this.to = to;
        this.direction = direction;
        this.random = seed > 0 ? new Random(seed) : new Random();
        this.sorter = new BubbleSorter();
        this.uiDelegate = uiDelegate;
        this.speed = speed;
    }

    public Integer[] randomNumbers() {
        int range = Math.toIntExact((long) to - from + 1);
        return random.ints(n, 0, range)
            .map(value -> from + value)
            .boxed()
            .toArray(Integer[]::new);
    }

    public Integer[] sort() {
        Integer[] numbers = randomNumbers();
        if (this.uiDelegate instanceof NumbersAwareUiDelegate numbersAwareUiDelegate) {
            numbersAwareUiDelegate.setNumbers(numbers);
        }
        this.sorter.setSorter(new EventHandler(1, this.uiDelegate, speed));
        this.sorter.setNumbers(numbers);
        sorter.sort(this.direction);
        return sorter.getNumbers();
    }
}
