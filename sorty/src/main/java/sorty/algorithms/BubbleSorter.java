package sorty.algorithms;

import lombok.*;
import sorty.SortDirection;

@Getter
@Setter
@AllArgsConstructor
public class BubbleSorter extends DefaultSorter {

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Bubble Sort " + direction, this.numbers.length);
        var i = this.numbers.length - 1;
        while (i > 0) {
            this.sortUntil(i--, direction);
        }
        this.getSorter().finish();
    }

    private void sortUntil(int index, SortDirection direction) {
        for (int i = 0; i < index; i++) {
            this.getSorter().compare(i, i+1);
            var state = this.shouldSwap(i, i + 1, direction);
            if (state.shouldSwap()) {
                this.sorter.swap(i, i+1);
                var value = state.val1();
                this.numbers[i] = state.val2();
                this.numbers[i+1] = value;
            }
        }
    }


}
