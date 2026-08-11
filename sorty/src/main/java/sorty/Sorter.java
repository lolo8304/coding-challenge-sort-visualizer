package sorty;

import lombok.Getter;
import lombok.Setter;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.SorterProtocol;
import sorty.ui.NoOpUiDelegate;
import sorty.ui.NumbersAwareUiDelegate;

import java.util.Random;

@Getter
@Setter
public class Sorter implements SorterDelegate {
    private final int n;
    private final int from;
    private final int to;
    private final SortDirection direction;
    private final SortAlgorithm algorithm;
    private final Random random;
    private final DefaultSorter sorter;
    private SorterProtocol uiDelegate;
    private final int delayMillis;
    private Integer[] initialNumbers;

    public Sorter(int n, SortDirection direction) {
        this(n, 1, 100, direction, 0, SortAlgorithm.BUBBLE, 100, new NoOpUiDelegate());
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed) {
        this(n, from, to, direction, seed, SortAlgorithm.BUBBLE, 100, new NoOpUiDelegate());
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed, int delayMillis) {
        this(n, from, to, direction, seed, SortAlgorithm.BUBBLE, delayMillis, new NoOpUiDelegate());
    }

    public Sorter(
        int n,
        int from,
        int to,
        SortDirection direction,
        int seed,
        SortAlgorithm algorithm,
        int delayMillis
    ) {
        this(n, from, to, direction, seed, algorithm, delayMillis, new NoOpUiDelegate());
    }

    public Sorter(
        int n,
        int from,
        int to,
        SortDirection direction,
        int seed,
        SortAlgorithm algorithm,
        int delayMillis,
        SorterProtocol uiDelegate
    ) {
        this.n = n;
        this.from = from;
        this.to = to;
        this.direction = direction;
        this.algorithm = algorithm;
        this.random = seed > 0 ? new Random(seed) : new Random();
        this.sorter = algorithm.createSorter();
        this.uiDelegate = uiDelegate;
        this.delayMillis = delayMillis;
    }

    public Integer[] randomNumbers() {
        int range = Math.toIntExact((long) to - from + 1);
        return random.ints(n, 0, range)
            .map(value -> from + value)
            .boxed()
            .toArray(Integer[]::new);
    }

    @Override
    public Integer[] sort() {
        this.initialNumbers = randomNumbers();
        return sortFromInitialNumbers();
    }

    public Integer[] sort(Integer[] numbers) {
        this.initialNumbers = numbers.clone();
        return sortFromInitialNumbers();
    }

    private Integer[] sortFromInitialNumbers() {
        while (true) {
            try {
                this.sortNow(this.initialNumbers.clone());
                return sorter.getNumbers();
            } catch (SortRestartRequestedException exception) {
                // Restart from the original unsorted values.
            }
        }
    }

    private void sortNow(Integer[] numbers) {
        if (this.uiDelegate instanceof NumbersAwareUiDelegate numbersAwareUiDelegate) {
            numbersAwareUiDelegate.setNumbers(numbers);
        }
        this.sorter.setSorter(new EventHandler(1, this.uiDelegate, delayMillis));
        this.sorter.setNumbers(numbers);
        sorter.sort(this.direction);
    }

    @Override
    public Integer[] restart() {
        if (this.initialNumbers == null) {
            this.initialNumbers = randomNumbers();
        }
        return sortFromInitialNumbers();
    }
}
