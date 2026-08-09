package sorty.algorithms.impl;

import java.util.Random;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.State;

@Getter
@Setter
@NoArgsConstructor
public class BogoSorter extends DefaultSorter {
    private static final int MAX_SHUFFLE_SIZE = 8;
    private static final int MAX_ATTEMPTS = 100_000;
    private final Random random = new Random(0);

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Bogo Sort " + direction, this.numbers.length);
        if (this.numbers.length <= MAX_SHUFFLE_SIZE) {
            int attempts = 0;
            while (!isSorted(direction) && attempts < MAX_ATTEMPTS) {
                shuffle();
                attempts++;
            }
        }
        if (!isSorted(direction)) {
            fallbackSort(direction);
        }
        this.getSorter().finish();
    }

    private void shuffle() {
        for (int i = this.numbers.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swapIndexes(i, j);
        }
    }

    private boolean isSorted(SortDirection direction) {
        for (int i = 1; i < this.numbers.length; i++) {
            this.getSorter().compare(i - 1, i);
            int previous = this.getAt(i - 1);
            int current = this.getAt(i);
            if (this.shouldSwapValues(previous, current, direction)) {
                return false;
            }
        }
        return true;
    }

    private void fallbackSort(SortDirection direction) {
        for (int i = 1; i < this.numbers.length; i++) {
            int value = this.getAt(i);
            int j = i;
            while (j > 0) {
                int previous = this.getAt(j - 1);
                this.getSorter().compare(j - 1, j);
                if (!this.shouldSwapValues(previous, value, direction)) {
                    break;
                }
                this.putAt(j, previous);
                j--;
            }
            this.putAt(j, value);
        }
    }

    private void swapIndexes(int index1, int index2) {
        if (index1 == index2) {
            return;
        }
        int value1 = this.getAt(index1);
        int value2 = this.getAt(index2);
        this.swap(new State(index1, value1, index2, value2, true));
    }
}
