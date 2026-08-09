package sorty.algorithms.impl;

import lombok.*;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

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
            this.swapIfNeeded(i, i + 1, direction);
        }
    }


}
