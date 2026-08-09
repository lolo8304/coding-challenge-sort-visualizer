package sorty;

public enum SortSpeed {
    FAST(50),
    MEDIUM(100),
    SLOW(200);

    private final int delayMillis;

    SortSpeed(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public int delayMillis() {
        return delayMillis;
    }
}
