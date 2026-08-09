package sorty.algorithms;

import lombok.*;
import sorty.SortDirection;

import java.util.Arrays;
import java.util.Comparator;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultSorter {
    protected SorterProtocol sorter;
    protected Integer[] numbers;

    public void sort(SortDirection direction) {
        this.numbers = Arrays.stream(this.numbers).sorted(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        if (direction == SortDirection.ASCENDING) {
                            return o1.compareTo(o2);
                        } else {
                            return o2.compareTo(o1);
                        }
                    }
                }
        ).toArray(Integer[]::new);
    }

    protected State shouldSwap(int index1, int index2, SortDirection direction) {
        var at1 = this.getAt(index1);
        var at2 = this.getAt(index2);
        this.getSorter().compare(index1, index2);
        var shouldSwap = direction == SortDirection.ASCENDING ? at1 > at2 : at1 < at2;
        return new State(index1, at1, index2, at2, shouldSwap);
    }

    protected boolean shouldSwapValues(int at1, int at2, SortDirection direction) {
        return direction == SortDirection.ASCENDING ? at1 > at2 : at1 < at2;
    }


    protected void swap(State state) {
        state.swap(this.getSorter(), numbers);
    }

    protected boolean swapIfNeeded(int index1, int index2, SortDirection direction) {
        var state = this.shouldSwap(index1, index2, direction);
        var needsSwap = state.shouldSwap();
        if (needsSwap) {
            this.swap(state);
        }
        return needsSwap;
    }

    protected int getAt(int index) {
        this.sorter.at(index);
        return this.numbers[index];
    }
    protected void putAt(int index, int value) {
        this.numbers[index] = value;
        this.sorter.put(index, value);
    }
}
