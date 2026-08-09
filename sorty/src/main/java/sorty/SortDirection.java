package sorty;

import lombok.Getter;

@Getter
public enum SortDirection {
    ASCENDING(1),
    DESCENDING(-1);

    private final int multiplier;

    SortDirection(int multiplier) {
        this.multiplier = multiplier;
    }
}
