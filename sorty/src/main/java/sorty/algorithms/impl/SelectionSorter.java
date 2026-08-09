package sorty.algorithms.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.State;

@Getter
@Setter
@AllArgsConstructor
public class SelectionSorter extends DefaultSorter {

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Selection Sort " + direction, this.numbers.length);
        for (int i = 0; i < this.numbers.length-1; i++) {
            this.selectAt(i, direction);
        }
        this.getSorter().finish();
    }

    private void selectAt(int slotIndex, SortDirection direction) {
        var curAt = this.getAt(slotIndex);
        var curIndex = slotIndex;
        var smallestAt = curAt;
        var smallestIndex = slotIndex;
        for (int i = slotIndex + 1; i < this.numbers.length; i++) {
            var nextAt = this.getAt(i);
            if (this.shouldSwapValues(smallestAt, nextAt, direction)) {
                smallestAt = nextAt;
                smallestIndex = i;
            }
        }
        if (curIndex != smallestIndex) {
            this.swap(new State(
                    curIndex, curAt, smallestIndex, smallestAt, true)
            );
        }
    }

}
