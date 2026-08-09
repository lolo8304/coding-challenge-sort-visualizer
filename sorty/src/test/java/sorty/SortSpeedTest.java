package sorty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SortSpeedTest {
    @Test
    void mapsSpeedsToFixedDelays() {
        assertEquals(25, SortSpeed.FAST.delayMillis());
        assertEquals(50, SortSpeed.MEDIUM.delayMillis());
        assertEquals(100, SortSpeed.SLOW.delayMillis());
    }
}
