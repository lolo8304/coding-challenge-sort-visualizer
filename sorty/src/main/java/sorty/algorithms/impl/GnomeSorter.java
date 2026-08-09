package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class GnomeSorter extends DefaultSorter {
    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Gnome Sort " + direction, this.numbers.length);
        int index = 1;
        while (index < this.numbers.length) {
            if (index == 0 || !this.swapIfNeeded(index - 1, index, direction)) {
                index++;
            } else {
                index--;
            }
        }
        this.getSorter().finish();
    }
}
