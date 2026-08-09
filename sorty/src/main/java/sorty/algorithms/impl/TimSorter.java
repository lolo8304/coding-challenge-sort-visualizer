package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class TimSorter extends DefaultSorter {
    private static final int RUN = 32;

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Tim Sort " + direction, this.numbers.length);
        for (int start = 0; start < this.numbers.length; start += RUN) {
            insertionSort(start, Math.min(start + RUN, this.numbers.length), direction);
        }
        for (int size = RUN; size < this.numbers.length; size *= 2) {
            for (int left = 0; left < this.numbers.length; left += 2 * size) {
                int middle = Math.min(left + size, this.numbers.length);
                int right = Math.min(left + 2 * size, this.numbers.length);
                if (middle < right) {
                    merge(left, middle, right, direction);
                }
            }
        }
        this.getSorter().finish();
    }

    private void insertionSort(int start, int end, SortDirection direction) {
        for (int i = start + 1; i < end; i++) {
            int value = this.getAt(i);
            int j = i;
            while (j > start) {
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

    private void merge(int start, int middle, int end, SortDirection direction) {
        Integer[] left = copyRange(start, middle);
        Integer[] right = copyRange(middle, end);
        int leftIndex = 0;
        int rightIndex = 0;
        int target = start;
        while (leftIndex < left.length && rightIndex < right.length) {
            this.getSorter().compare(start + leftIndex, middle + rightIndex);
            if (comesBeforeOrEqual(left[leftIndex], right[rightIndex], direction)) {
                this.putAt(target++, left[leftIndex++]);
            } else {
                this.putAt(target++, right[rightIndex++]);
            }
        }
        while (leftIndex < left.length) {
            this.putAt(target++, left[leftIndex++]);
        }
        while (rightIndex < right.length) {
            this.putAt(target++, right[rightIndex++]);
        }
    }

    private Integer[] copyRange(int start, int end) {
        Integer[] copied = new Integer[end - start];
        for (int i = 0; i < copied.length; i++) {
            copied[i] = this.getAt(start + i);
        }
        return copied;
    }

    private boolean comesBeforeOrEqual(int left, int right, SortDirection direction) {
        return direction == SortDirection.ASCENDING ? left <= right : left >= right;
    }
}
