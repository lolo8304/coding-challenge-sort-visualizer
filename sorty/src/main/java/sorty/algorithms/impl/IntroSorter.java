package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.State;

@Getter
@Setter
@NoArgsConstructor
public class IntroSorter extends DefaultSorter {
    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Intro Sort " + direction, this.numbers.length);
        int depthLimit = 2 * floorLog2(Math.max(1, this.numbers.length));
        introSort(0, this.numbers.length - 1, depthLimit, direction);
        this.getSorter().finish();
    }

    private void introSort(int low, int high, int depthLimit, SortDirection direction) {
        if (low >= high) {
            return;
        }
        if (depthLimit == 0) {
            heapSortRange(low, high, direction);
            return;
        }
        int pivotIndex = partition(low, high, direction);
        introSort(low, pivotIndex - 1, depthLimit - 1, direction);
        introSort(pivotIndex + 1, high, depthLimit - 1, direction);
    }

    private int partition(int low, int high, SortDirection direction) {
        int pivot = this.getAt(high);
        int boundary = low - 1;
        for (int current = low; current < high; current++) {
            int value = this.getAt(current);
            this.getSorter().compare(current, high);
            if (direction == SortDirection.ASCENDING ? value <= pivot : value >= pivot) {
                boundary++;
                swapIndexes(boundary, current);
            }
        }
        swapIndexes(boundary + 1, high);
        return boundary + 1;
    }

    private void heapSortRange(int low, int high, SortDirection direction) {
        for (int end = high; end > low; end--) {
            int selected = low;
            for (int i = low + 1; i <= end; i++) {
                this.getSorter().compare(i, selected);
                int current = this.getAt(i);
                int best = this.getAt(selected);
                if (direction == SortDirection.ASCENDING ? current > best : current < best) {
                    selected = i;
                }
            }
            swapIndexes(selected, end);
        }
    }

    private int floorLog2(int value) {
        return 31 - Integer.numberOfLeadingZeros(value);
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
