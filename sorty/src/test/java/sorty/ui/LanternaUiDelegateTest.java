package sorty.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.googlecode.lanterna.input.KeyStroke;
import org.junit.jupiter.api.Test;
import sorty.SortRestartRequestedException;

class LanternaUiDelegateTest {
    @Test
    void readsValuesFromCurrentNumbers() {
        LanternaUiDelegate delegate = new LanternaUiDelegate();

        delegate.setNumbers(new Integer[] {4, 1, 3});

        assertEquals(1, delegate.at(1));
    }

    @Test
    void restartKeyRequestsRestartWhileWaiting() {
        LanternaUiDelegate delegate = new LanternaUiDelegate();
        KeyStroke restart = new KeyStroke('s', false, false);

        assertThrows(SortRestartRequestedException.class, () -> delegate.abortOrRestartIfRequested(restart));
    }
}
