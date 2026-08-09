package sorty;

public class SortInterruptedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SortInterruptedException() {
        super("Sort interrupted.");
    }
}
