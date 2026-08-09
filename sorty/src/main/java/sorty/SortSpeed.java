package sorty;

public enum SortSpeed {
    FAST(25),
    MEDIUM(50),
    SLOW(100);

    private final int delayMillis;

    SortSpeed(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public int delayMillis() {
        return delayMillis;
    }
}
