package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class ShellSorter extends DefaultSorter {
    private static final int[] SPALTEN = {
        2147483647, 1131376761, 410151271, 157840433,
        58548857, 21521774, 8810089, 3501671, 1355339, 543749, 213331,
        84801, 27901, 11969, 4711, 1968, 815, 271, 111, 41, 13, 4, 1
    };

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Shell Sort " + direction, this.numbers.length);
        for (int gap : SPALTEN) {
            if (gap < this.numbers.length) {
                sortGap(gap, direction);
            }
        }
        this.getSorter().finish();
    }

    private void sortGap(int gap, SortDirection direction) {
        for (int i = gap; i < this.numbers.length; i++) {
            int value = this.getAt(i);
            int j = i;

            while (j >= gap) {
                int previous = this.getAt(j - gap);
                this.getSorter().compare(j - gap, j);
                if (!this.shouldSwapValues(previous, value, direction)) {
                    break;
                }

                this.putAt(j, previous);
                j -= gap;
            }

            this.putAt(j, value);
        }
    }
}
