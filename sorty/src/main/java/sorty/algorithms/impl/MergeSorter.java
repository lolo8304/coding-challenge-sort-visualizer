package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class MergeSorter extends DefaultSorter {

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Merge Sort " + direction, this.numbers.length);
        this.sortRange(0, this.numbers.length, direction);
        this.getSorter().finish();
    }

    private void sortRange(int startInclusive, int endExclusive, SortDirection direction) {
        if (endExclusive - startInclusive <= 1) {
            return;
        }

        int middle = startInclusive + (endExclusive - startInclusive) / 2;
        sortRange(startInclusive, middle, direction);
        sortRange(middle, endExclusive, direction);
        merge(startInclusive, middle, endExclusive, direction);
    }

    private void merge(int startInclusive, int middle, int endExclusive, SortDirection direction) {
        Integer[] left = copyRange(startInclusive, middle);
        Integer[] right = copyRange(middle, endExclusive);
        int leftIndex = 0;
        int rightIndex = 0;
        int targetIndex = startInclusive;

        while (leftIndex < left.length && rightIndex < right.length) {
            this.getSorter().compare(startInclusive + leftIndex, middle + rightIndex);
            if (comesBeforeOrEqual(left[leftIndex], right[rightIndex], direction)) {
                this.putAt(targetIndex, left[leftIndex]);
                leftIndex++;
            } else {
                this.putAt(targetIndex, right[rightIndex]);
                rightIndex++;
            }
            targetIndex++;
        }

        while (leftIndex < left.length) {
            this.putAt(targetIndex, left[leftIndex]);
            leftIndex++;
            targetIndex++;
        }

        while (rightIndex < right.length) {
            this.putAt(targetIndex, right[rightIndex]);
            rightIndex++;
            targetIndex++;
        }
    }

    private Integer[] copyRange(int startInclusive, int endExclusive) {
        Integer[] copied = new Integer[endExclusive - startInclusive];
        for (int i = 0; i < copied.length; i++) {
            copied[i] = this.getAt(startInclusive + i);
            this.putAt(startInclusive + i, copied[i]);
        }
        return copied;
    }

    private boolean comesBeforeOrEqual(int left, int right, SortDirection direction) {
        return direction == SortDirection.ASCENDING ? left <= right : left >= right;
    }
}
