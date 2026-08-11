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

import sorty.SortInterruptedException;
import sorty.SortRestartRequestedException;
import sorty.algorithms.SorterProtocol;

public final class LanternaGridUiDelegate implements AutoCloseable {
    private static final int CLOSE_DELAY_MILLIS = 500;
    private static final TextColor ORANGE = new TextColor.RGB(255, 165, 0);
    private static final TextColor DARK_GREEN = new TextColor.RGB(0, 100, 0);

    private final Panel[] panels;
    private final boolean waitForKeyBeforeClose;
    private Screen screen;
    private TerminalSize lastSize;
    private boolean completeRefreshRequired = true;
    private Thread shutdownHook;
    private boolean debugFrameByFrame = false;

    public LanternaGridUiDelegate(int panelCount, boolean waitForKeyBeforeClose) {
        if (panelCount != 2 && panelCount != 4 && panelCount != 9 && panelCount != 16) {
            throw new IllegalArgumentException("Lanterna grid supports 2, 4, 9, or 16 panels.");
        }
        this.waitForKeyBeforeClose = waitForKeyBeforeClose;
        this.panels = new Panel[panelCount];
        for (int index = 0; index < panels.length; index++) {
            panels[index] = new Panel(index);
        }
        startScreen();
    }

    public SorterProtocol panel(int index) {
        return panels[index];
    }

    private void startScreen() {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            this.screen = new TerminalScreen(terminal);
            this.screen.startScreen();
            this.screen.setCursorPosition(null);
            this.screen.clear();
            this.shutdownHook = new Thread(this::closeScreenQuietly);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            draw();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not start Lanterna UI.", exception);
        }
    }

    @Override
    public void close() {
        closeScreen();
    }

    private synchronized void draw() {
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
            for (Panel panel : panels) {
                drawPanel(graphics, size, panel);
            }

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

    private void drawPanel(TextGraphics graphics, TerminalSize size, Panel panel) {
        Bounds bounds = panelBounds(size, panel.index);
        drawPanelBorder(graphics, bounds);
        drawHeader(graphics, bounds, panel);
        drawBars(graphics, bounds, panel);
    }

    private Bounds panelBounds(TerminalSize size, int panelIndex) {
        int gridColumns = gridColumns();
        int gridRows = panels.length / gridColumns;
        int column = panelIndex % gridColumns;
        int row = panelIndex / gridColumns;
        int x = size.getColumns() * column / gridColumns;
        int nextX = size.getColumns() * (column + 1) / gridColumns;
        int y = size.getRows() * row / gridRows;
        int nextY = size.getRows() * (row + 1) / gridRows;
        return new Bounds(x, y, nextX - x, nextY - y);
    }

    private int gridColumns() {
        if (panels.length == 2) {
            return 2;
        }
        return (int) Math.sqrt(panels.length);
    }

    private void drawPanelBorder(TextGraphics graphics, Bounds bounds) {
        if (bounds.width <= 1 || bounds.height <= 1) {
            return;
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
            graphics.putString(x, bounds.y, "-");
            graphics.putString(x, bounds.y + bounds.height - 1, "-");
        }
        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
            graphics.putString(bounds.x, y, "|");
            graphics.putString(bounds.x + bounds.width - 1, y, "|");
        }
        graphics.putString(bounds.x, bounds.y, "+");
        graphics.putString(bounds.x + bounds.width - 1, bounds.y, "+");
        graphics.putString(bounds.x, bounds.y + bounds.height - 1, "+");
        graphics.putString(bounds.x + bounds.width - 1, bounds.y + bounds.height - 1, "+");
    }

    private void drawHeader(TextGraphics graphics, Bounds bounds, Panel panel) {
        if (bounds.width <= 2 || bounds.height <= 2) {
            return;
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(bounds.x + 1, bounds.y + 1, fit(panel.title(), bounds.innerWidth()));
        graphics.disableModifiers(SGR.BOLD);
        if (bounds.height > 4) {
            graphics.putString(bounds.x + 1, bounds.y + 2, fit(panel.counters(), bounds.innerWidth()));
        }
    }

    private void drawBars(TextGraphics graphics, Bounds bounds, Panel panel) {
        if (panel.numbers.length == 0 || bounds.width <= 2 || bounds.height <= 6) {
            return;
        }

        int chartTop = bounds.y + 4;
        int chartBottom = bounds.y + bounds.height - 2;
        int chartHeight = chartBottom - chartTop + 1;
        int columns = bounds.innerWidth();
        int barWidth = Math.max(1, columns / panel.numbers.length);
        int visibleBars = Math.min(panel.numbers.length, Math.max(1, columns / barWidth));
        int min = Arrays.stream(panel.numbers).mapToInt(Integer::intValue).min().orElse(0);
        int max = Arrays.stream(panel.numbers).mapToInt(Integer::intValue).max().orElse(1);
        int range = Math.max(1, max - min);

        for (int i = 0; i < visibleBars; i++) {
            int value = panel.numbers[i];
            int barHeight = Math.max(1, 1 + (int) Math.round(((double) (value - min) / range) * (chartHeight - 1)));
            TextColor color = panel.barColor(i);
            int xStart = bounds.x + 1 + i * barWidth;
            int xEnd = Math.min(bounds.x + bounds.width - 1, xStart + barWidth);

            drawBarLabel(graphics, value, xStart, xEnd, chartBottom - barHeight, color);
            graphics.setBackgroundColor(color);
            for (int x = xStart; x < xEnd; x++) {
                for (int y = chartBottom; y > chartBottom - barHeight; y--) {
                    graphics.putString(new TerminalPosition(x, y), " ");
                }
            }
        }
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    private void drawBarLabel(TextGraphics graphics, int value, int xStart, int xEnd, int y, TextColor color) {
        String label = String.valueOf(value);
        int width = xEnd - xStart;
        if (width <= 0 || y < 0) {
            return;
        }

        String fittedLabel = fit(label, width);
        int x = xStart + Math.max(0, (width - fittedLabel.length()) / 2);
        graphics.setForegroundColor(color);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.putString(x, y, fittedLabel);
    }

    private void clearFrame(TextGraphics graphics, TerminalSize size) {
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.fillRectangle(new TerminalPosition(0, 0), size, ' ');
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

    private boolean isRestart(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
            && character != null
            && character == 's';
    }

    private boolean isPause(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
            && character != null
            && character == ' ';
    }

    private boolean isNext(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        return keyStroke.getKeyType() == KeyType.Character
            && character != null
            && character == 'n';
    }

    private synchronized void closeScreen() {
        if (screen == null) {
            return;
        }
        try {
            waitBeforeClose();
            screen.stopScreen();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not stop Lanterna UI.", exception);
        } finally {
            screen = null;
            removeShutdownHook();
        }
    }

    private synchronized void closeScreenQuietly() {
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

    private void waitBeforeClose() throws IOException {
        if (waitForKeyBeforeClose) {
            drainPendingInput();
            KeyStroke keyStroke = screen.readInput();
            while (keyStroke == null) {
                keyStroke = screen.readInput();
            }
            if (isInterrupt(keyStroke)) {
                throw new SortInterruptedException();
            }
            return;
        }

        try {
            Thread.sleep(CLOSE_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SortInterruptedException();
        }
    }

    private void drainPendingInput() throws IOException {
        while (screen.pollInput() != null) {
            // Ignore buffered input so --wait closes only on a key pressed after the final frame is visible.
        }
    }

    private String fit(String value, int columns) {
        if (value.length() <= columns) {
            return value;
        }
        return value.substring(0, Math.max(0, columns));
    }

    private record Bounds(int x, int y, int width, int height) {
        private int innerWidth() {
            return Math.max(0, width - 2);
        }
    }

    private class Panel implements SorterProtocol, NumbersAwareUiDelegate {
        private final int index;
        private Integer[] numbers = new Integer[0];
        private String algorithm = "";
        private String action = "waiting";
        private int total;
        private int compare;
        private int swap;
        private int access;
        private int write;
        private int index1 = -1;
        private int index2 = -1;

        private Panel(int index) {
            this.index = index;
        }

        @Override
        public void setNumbers(Integer[] numbers) {
            synchronized (LanternaGridUiDelegate.this) {
                this.numbers = numbers;
                draw();
            }
        }

        @Override
        public void start(String algorithm, int size) {
            synchronized (LanternaGridUiDelegate.this) {
                this.algorithm = algorithm;
                this.action = "start";
                this.total = 0;
                this.compare = 0;
                this.swap = 0;
                this.access = 0;
                this.write = 0;
                this.index1 = -1;
                this.index2 = -1;
                draw();
            }
        }

        @Override
        public void finish() {
        }

        @Override
        public void finish(int total, int compare, int swap, int access) {
            synchronized (LanternaGridUiDelegate.this) {
                this.total = total;
                this.compare = compare;
                this.swap = swap;
                this.access = access;
                this.action = "finished";
                this.index1 = -1;
                this.index2 = -1;
                draw();
            }
        }

        @Override
        public void compare(int index1, int index2) {
            synchronized (LanternaGridUiDelegate.this) {
                this.compare++;
                this.total++;
                mark(index1, index2, "compare");
            }
        }

        @Override
        public void swap(int index1, int index2) {
            synchronized (LanternaGridUiDelegate.this) {
                this.swap++;
                this.total++;
                mark(index1, index2, "swap");
            }
        }

        @Override
        public int at(int index) {
            synchronized (LanternaGridUiDelegate.this) {
                this.access++;
                this.total++;
                mark(index, -1, "at");
                return numbers[index];
            }
        }

        @Override
        public int put(int index, int value) {
            synchronized (LanternaGridUiDelegate.this) {
                this.write++;
                this.total++;
                this.numbers[index] = value;
                mark(index, -1, "put");
                return value;
            }
        }

        private void mark(int index1, int index2, String action) {
            this.index1 = index1;
            this.index2 = index2;
            this.action = action;
            draw();
        }

        private String title() {
            if (algorithm.isBlank()) {
                return "Sorty - waiting";
            }
            return "Sorty - " + algorithm + " - " + action;
        }

        private String counters() {
            return "n=" + numbers.length
                + " total=" + total
                + " compare=" + compare
                + " swap=" + swap
                + " access=" + access
                + " write=" + write
                + " (space pause, n step, s restart)";
        }

        private TextColor barColor(int index) {
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
    }
}
