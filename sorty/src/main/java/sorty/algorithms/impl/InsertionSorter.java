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
public class InsertionSorter extends DefaultSorter {

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Insert Sort " + direction, this.numbers.length);
        for (int i = 1; i < this.numbers.length; i++) {
            var state = this.shouldSwap(i-1, i, direction);
            // the max until now has the highest value
            if (state.shouldSwap()) {
                insertInSorted(state, direction);
            }
        }
        this.getSorter().finish();
    }

    private void insertInSorted(State state, SortDirection direction) {
        var indexCur = state.index2();
        var atCur = state.at2();

        var indexPrev = state.index1();
        var atPrev = state.at1();

        this.putAt(indexCur, atPrev); //
        indexPrev--;
        indexCur--;
        while (indexPrev >= 0) {
            atPrev = this.getAt(indexPrev);
            var needsSwap = this.shouldSwapValues(atPrev, atCur, direction);
            if (needsSwap) {
                this.putAt(indexCur, atPrev); //
                indexPrev--;
                indexCur--;
            } else  {
                indexPrev--;
                break;
            }
        }
        this.putAt(indexCur, atCur);
    }


}
