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
public class QuickSorter extends DefaultSorter {

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Quick Sort " + direction, this.numbers.length);
        quickSort(0, this.numbers.length - 1, direction);
        this.getSorter().finish();
    }

    private void quickSort(int low, int high, SortDirection direction) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(low, high, direction);
        quickSort(low, pivotIndex - 1, direction);
        quickSort(pivotIndex + 1, high, direction);
    }

    private int partition(int low, int high, SortDirection direction) {
        int pivot = this.getAt(high);
        int boundary = low - 1;

        for (int current = low; current < high; current++) {
            int value = this.getAt(current);
            this.getSorter().compare(current, high);
            if (belongsBeforeOrAtPivot(value, pivot, direction)) {
                boundary++;
                swapIndexes(boundary, current);
            }
        }

        swapIndexes(boundary + 1, high);
        return boundary + 1;
    }

    private boolean belongsBeforeOrAtPivot(int value, int pivot, SortDirection direction) {
        return direction == SortDirection.ASCENDING ? value <= pivot : value >= pivot;
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
