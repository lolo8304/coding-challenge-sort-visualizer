package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class CocktailSorter extends DefaultSorter {
    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Cocktail Sort " + direction, this.numbers.length);
        boolean swapped = true;
        int start = 0;
        int end = this.numbers.length - 1;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; i++) {
                swapped |= this.swapIfNeeded(i, i + 1, direction);
            }
            if (!swapped) {
                break;
            }
            swapped = false;
            end--;
            for (int i = end - 1; i >= start; i--) {
                swapped |= this.swapIfNeeded(i, i + 1, direction);
            }
            start++;
        }
        this.getSorter().finish();
    }
}
