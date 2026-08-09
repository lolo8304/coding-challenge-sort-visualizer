package sorty;

import lombok.Getter;
import lombok.Setter;
import sorty.algorithms.BubbleSorter;
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
    private final Random random;
    private final DefaultSorter sorter;
    private SorterProtocol uiDelegate;
    private final SortSpeed speed;
    private Integer[] initialNumbers;

    public Sorter(int n, SortDirection direction) {
        this(n, 1, 100, direction, 0, SortSpeed.MEDIUM, new NoOpUiDelegate());
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed) {
        this(n, from, to, direction, seed, SortSpeed.MEDIUM, new NoOpUiDelegate());
    }

    public Sorter(int n, int from, int to, SortDirection direction, int seed, SortSpeed speed) {
        this(n, from, to, direction, seed, speed, new NoOpUiDelegate());
    }

    public Sorter(
        int n,
        int from,
        int to,
        SortDirection direction,
        int seed,
        SortSpeed speed,
        SorterProtocol uiDelegate
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

    @Override
    public Integer[] sort() {
        this.initialNumbers = randomNumbers();
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
        this.sorter.setSorter(new EventHandler(1, this.uiDelegate, speed));
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
