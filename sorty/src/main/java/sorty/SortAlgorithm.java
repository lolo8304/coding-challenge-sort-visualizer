package sorty;

import sorty.algorithms.BubbleSorter;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.InsertSorter;
import sorty.algorithms.SelectionSorter;

public enum SortAlgorithm {
    BUBBLE {
        @Override
        public DefaultSorter createSorter() {
            return new BubbleSorter();
        }
    },
    INSERT {
        @Override
        public DefaultSorter createSorter() {
            return new InsertSorter();
        }
    },
    SELECTION {
        @Override
        public DefaultSorter createSorter() {
            return new SelectionSorter();
        }
    };

    public abstract DefaultSorter createSorter();
}
