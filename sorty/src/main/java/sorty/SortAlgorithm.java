package sorty;

import sorty.algorithms.BubbleSorter;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.InsertSorter;

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
    };

    public abstract DefaultSorter createSorter();
}
