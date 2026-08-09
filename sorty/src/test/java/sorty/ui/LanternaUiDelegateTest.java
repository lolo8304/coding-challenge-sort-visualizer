package sorty.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LanternaUiDelegateTest {
    @Test
    void readsValuesFromCurrentNumbers() {
        LanternaUiDelegate delegate = new LanternaUiDelegate();

        delegate.setNumbers(new Integer[] {4, 1, 3});

        assertEquals(1, delegate.at(1));
    }
}
