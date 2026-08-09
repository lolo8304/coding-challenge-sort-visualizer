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
        var at1 = this.sorter.at(index1);
        var at2 = this.sorter.at(index2);
        var shouldSwap = direction == SortDirection.ASCENDING ? at1 > at2 : at1 < at2;
        return new State(at1, at2, shouldSwap);
    }
}
