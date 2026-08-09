package sorty.ui;

import java.io.IOException;
import java.util.Arrays;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.Screen.RefreshType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import sorty.algorithms.SorterProtocol;
import sorty.SortInterruptedException;
import sorty.SortRestartRequestedException;

public class LanternaUiDelegate implements SorterProtocol, NumbersAwareUiDelegate {
    private static final int CLOSE_DELAY_MILLIS = 500;
    private static final TextColor ORANGE = new TextColor.RGB(255, 165, 0);
    private static final TextColor DARK_GREEN = new TextColor.RGB(0, 100, 0);

    private Integer[] numbers = new Integer[0];
    private Screen screen;
    private String algorithm = "";
    private int total;
    private int compare;
    private int swap;
    private int access;
    private int write;
    private TerminalSize lastSize;
    private boolean completeRefreshRequired = true;
    private Thread shutdownHook;
    private boolean debugFrameByFrame = false;

    @Override
    public void setNumbers(Integer[] numbers) {
        this.numbers = numbers;
    }

    @Override
    public void start(String algorithm, int size) {
        this.algorithm = algorithm;
        this.total = 0;
        this.compare = 0;
        this.swap = 0;
        this.access = 0;
        this.write = 0;
        this.completeRefreshRequired = true;

        if (screen != null) {
            draw(-1, -1, "start");
            return;
        }

        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            this.screen = new TerminalScreen(terminal);
            this.screen.startScreen();
            this.screen.setCursorPosition(null);
            this.screen.clear();
            this.shutdownHook = new Thread(this::closeScreenQuietly);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            draw(-1, -1, "start");
        } catch (IOException exception) {
            throw new IllegalStateException("Could not start Lanterna UI.", exception);
        }
    }

    @Override
    public void finish() {
    }

    @Override
    public void finish(int total, int compare, int swap, int access) {
        this.total = total;
        this.compare = compare;
        this.swap = swap;
        this.access = access;
        draw(-1, -1, "finished");
        closeScreen();
    }

    @Override
    public void compare(int index1, int index2) {
        compare++;
        total++;
        draw(index1, index2, "compare");
    }

    @Override
    public void swap(int index1, int index2) {
        swap++;
        total++;
        draw(index1, index2, "swap");
    }

    @Override
    public int at(int index) {
        this.access++;
        total++;
        draw(index, -1, "at");
        return numbers[index];
    }

    @Override
    public int put(int index, int value) {
        this.write++;
        total++;
        numbers[index] = value;
        draw(index, -1, "put");
        return value;
    }

    private void draw(int index1, int index2, String action) {
        if (screen == null) {
            return;
        }

        try {
            abortIfRequested();
            TerminalSize resize = screen.doResizeIfNecessary();
            if (resize != null) {
                completeRefreshRequired = true;
            }

            TerminalSize size = screen.getTerminalSize();
            TextGraphics graphics = screen.newTextGraphics();
            if (!size.equals(lastSize)) {
                completeRefreshRequired = true;
                lastSize = size;
            }
            clearFrame(graphics, size);
            drawHeader(graphics, size, action);
            drawBars(graphics, size, index1, index2, action);

            if (completeRefreshRequired) {
                screen.refresh(RefreshType.COMPLETE);
                completeRefreshRequired = false;
            } else {
                screen.refresh(RefreshType.DELTA);
            }
            abortIfRequested();
        } catch (IOException exception) {
            closeScreen();
            throw new IllegalStateException("Could not render Lanterna UI.", exception);
        }
    }

    private void abortIfRequested() throws IOException {
        if (screen == null) {
            return;
        }

        if (this.debugFrameByFrame) {
            this.debugFrameByFrame = false;
            waitForResume();
        }

        KeyStroke keyStroke = screen.pollInput();
        while (keyStroke != null) {
            abortOrRestartIfRequested(keyStroke);
            if (isPause(keyStroke)) {
                waitForResume();
            }
            keyStroke = screen.pollInput();
        }
    }

    private void waitForResume() throws IOException {
        KeyStroke keyStroke = screen.readInput();
        while (keyStroke != null) {
            abortOrRestartIfRequested(keyStroke);
            if (isNext(keyStroke)) {
                this.debugFrameByFrame = true;
            }
            return;
        }
    }

    void abortOrRestartIfRequested(KeyStroke keyStroke) {
        if (isInterrupt(keyStroke)) {
            closeScreen();
            throw new SortInterruptedException();
        }
        if (isRestart(keyStroke)) {
            throw new SortRestartRequestedException();
        }
    }

    private boolean isInterrupt(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.EOF) {
            return true;
        }
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
            && keyStroke.isCtrlDown()
            && character != null
            && Character.toLowerCase(character) == 'c';
    }

    private boolean isPause(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
                && character != null
                && character == ' ';
    }

    private boolean isRestart(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
                && character != null
                && character == 's';
    }

    private boolean isNext(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
                && character != null
                && character == 'n';
    }

    private void drawHeader(TextGraphics graphics, TerminalSize size, String action) {
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(0, 0, fit("Sorty - " + algorithm + " - " + action, size.getColumns()));
        graphics.disableModifiers(SGR.BOLD);
        graphics.putString(
            0,
            1,
            fit(
                "n=" + numbers.length
                    + " total=" + total
                    + " compare=" + compare
                    + " swap=" + swap
                    + " access=" + access
                    + " write=" + write
                    + " (space pause, n step, s restart)",
                size.getColumns()
            )
        );
    }

    private void clearFrame(TextGraphics graphics, TerminalSize size) {
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.fillRectangle(new TerminalPosition(0, 0), size, ' ');
    }

    private void drawBars(TextGraphics graphics, TerminalSize size, int index1, int index2, String action) {
        if (numbers.length == 0 || size.getRows() <= 5 || size.getColumns() <= 0) {
            return;
        }

        int chartTop = 3;
        int chartHeight = size.getRows() - chartTop - 1;
        int columns = size.getColumns();
        int barWidth = Math.max(1, columns / numbers.length);
        int visibleBars = Math.min(numbers.length, Math.max(1, columns / barWidth));
        int min = Arrays.stream(numbers).mapToInt(Integer::intValue).min().orElse(0);
        int max = Arrays.stream(numbers).mapToInt(Integer::intValue).max().orElse(1);
        int range = Math.max(1, max - min);

        for (int i = 0; i < visibleBars; i++) {
            int value = numbers[i];
            int barHeight = Math.max(1, 1 + (int) Math.round(((double) (value - min) / range) * (chartHeight - 1)));
            TextColor color = barColor(i, index1, index2, action);
            int xStart = i * barWidth;
            int xEnd = Math.min(columns, xStart + barWidth);

            drawBarLabel(graphics, value, xStart, xEnd, size.getRows() - barHeight - 2, color);
            graphics.setBackgroundColor(color);
            for (int x = xStart; x < xEnd; x++) {
                for (int y = size.getRows() - 1; y >= size.getRows() - barHeight; y--) {
                    graphics.putString(new TerminalPosition(x, y), " ");
                }
            }
        }
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    private TextColor barColor(int index, int index1, int index2, String action) {
        if (index != index1 && index != index2) {
            return TextColor.ANSI.CYAN;
        }
        if ("swap".equals(action)) {
            return ORANGE;
        }
        if ("at".equals(action)) {
            return TextColor.ANSI.GREEN;
        }
        if ("put".equals(action)) {
            return DARK_GREEN;
        }
        return TextColor.ANSI.YELLOW;
    }

    private void drawBarLabel(TextGraphics graphics, int value, int xStart, int xEnd, int y, TextColor color) {
        String label = String.valueOf(value);
        int width = xEnd - xStart;
        if (width <= 0 || y < 0) {
            return;
        }

        String fittedLabel = label.length() <= width ? label : label.substring(0, width);
        int x = xStart + Math.max(0, (width - fittedLabel.length()) / 2);
        graphics.setForegroundColor(color);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.putString(x, y, fittedLabel);
    }

    private String fit(String value, int columns) {
        if (value.length() <= columns) {
            return value;
        }
        return value.substring(0, Math.max(0, columns));
    }

    private void closeScreen() {
        if (screen == null) {
            return;
        }
        try {
            pauseBeforeClose();
            screen.stopScreen();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not stop Lanterna UI.", exception);
        } finally {
            screen = null;
            removeShutdownHook();
        }
    }

    private void closeScreenQuietly() {
        if (screen == null) {
            return;
        }
        try {
            screen.stopScreen();
        } catch (IOException ignored) {
            // Best effort during JVM shutdown.
        } finally {
            screen = null;
        }
    }

    private void removeShutdownHook() {
        if (shutdownHook == null) {
            return;
        }
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException ignored) {
            // JVM is already shutting down.
        } finally {
            shutdownHook = null;
        }
    }

    private void pauseBeforeClose() {
        try {
            Thread.sleep(CLOSE_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SortInterruptedException();
        }
    }
}
