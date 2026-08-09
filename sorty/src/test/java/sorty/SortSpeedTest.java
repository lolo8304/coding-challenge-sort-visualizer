package sorty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SortSpeedTest {
    @Test
    void mapsSpeedsToFixedDelays() {
        assertEquals(50, SortSpeed.FAST.delayMillis());
        assertEquals(100, SortSpeed.MEDIUM.delayMillis());
        assertEquals(200, SortSpeed.SLOW.delayMillis());
    }
}
