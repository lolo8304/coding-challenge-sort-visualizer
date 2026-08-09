package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class CombSorter extends DefaultSorter {
    private static final double SHRINK_FACTOR = 1.3;

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Comb Sort " + direction, this.numbers.length);
        int gap = this.numbers.length;
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = nextGap(gap);
            swapped = false;
            for (int i = 0; i + gap < this.numbers.length; i++) {
                swapped |= this.swapIfNeeded(i, i + gap, direction);
            }
        }
        this.getSorter().finish();
    }

    private int nextGap(int gap) {
        return Math.max(1, (int) (gap / SHRINK_FACTOR));
    }
}
