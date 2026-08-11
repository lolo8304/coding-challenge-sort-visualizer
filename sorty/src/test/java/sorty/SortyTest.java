package sorty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

class SortyTest {
    @Test
    void consoleFlagUsesConsoleUiLog() {
        String output = execute("--console", "-vv", "-n", "3", "--seed", "1");

        assertTrue(output.contains("Starting Sorter"));
        assertTrue(output.contains("Compare"));
        assertTrue(output.contains("access:"));
    }

    @Test
    void shortConsoleFlagUsesConsoleUiLog() {
        String output = execute("-c", "-vv", "-n", "3", "--seed", "1");

        assertTrue(output.contains("Starting Sorter"));
        assertTrue(output.contains("Compare"));
    }

    @Test
    void lanternaFlagIsRecognized() {
        CommandLine commandLine = new CommandLine(new Sorty());

        assertTrue(commandLine.getCommandSpec().findOption("--lanterna").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-l").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--wait").isOption());
    }

    @Test
    void delayFlagIsRecognized() {
        CommandLine commandLine = new CommandLine(new Sorty());

        assertTrue(commandLine.getCommandSpec().findOption("--delay").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--seed").isOption());
    }

    @Test
    void algorithmFlagIsRecognized() {
        CommandLine commandLine = new CommandLine(new Sorty());

        assertTrue(commandLine.getCommandSpec().findOption("--algorithm").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-2").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-4").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-9").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-16").isOption());
    }

    @Test
    void insertAlgorithmSortsConsoleOutput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "INSERT");

        assertEquals("14 45 49 95 ", output);
    }

    @Test
    void mergeAlgorithmSortsConsoleOutput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "MERGE");

        assertEquals("14 45 49 95 ", output);
    }

    @Test
    void quickAlgorithmSortsConsoleOutput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "QUICK");

        assertEquals("14 45 49 95 ", output);
    }

    @Test
    void heapAlgorithmIsAccepted() {
        CommandLine commandLine = new CommandLine(new Sorty());

        assertEquals(0, commandLine.execute("--console", "-n", "4", "--seed", "1", "--algorithm", "HEAP"));
    }

    @Test
    void shellAlgorithmSortsConsoleOutput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "SHELL");

        assertEquals("14 45 49 95 ", output);
    }

    @Test
    void radixAlgorithmSortsConsoleOutput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "RADIX");

        assertEquals("14 45 49 95 ", output);
    }

    @Test
    void additionalAlgorithmsSortConsoleOutput() {
        for (String algorithm : List.of("COCKTAIL", "COMB", "GNOME", "TIM", "INTRO", "BOGO")) {
            String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", algorithm);

            assertEquals("14 45 49 95 ", output);
        }
    }

    @Test
    void commaSeparatedAlgorithmsRunAgainstSameInput() {
        String output = execute("--console", "-n", "4", "--seed", "1", "--algorithm", "BUBBLE,INSERT");

        assertEquals(
            "BUBBLE: 14 45 49 95 " + System.lineSeparator()
                + "INSERT: 14 45 49 95 " + System.lineSeparator(),
            output
        );
    }

    @Test
    void starRunsAllAlgorithms() {
        String output = execute("--console", "-n", "2", "--seed", "1", "--algorithm", "*", "-4");

        assertTrue(output.contains("BUBBLE: "));
        assertTrue(output.contains("BOGO: "));
    }

    @Test
    void commandRejectsConflictingSplitOptions() {
        int exitCode = new CommandLine(new Sorty()).execute("-n", "3", "-9", "-16");

        assertEquals(2, exitCode);
    }

    @Test
    void commandRejectsNegativeDelay() {
        int exitCode = new CommandLine(new Sorty()).execute("-n", "3", "--delay", "-1");

        assertEquals(2, exitCode);
    }

    @Test
    void commandRejectsInvalidRange() {
        int exitCode = new CommandLine(new Sorty()).execute("-n", "3", "-from", "10", "-to", "9");

        assertEquals(2, exitCode);
    }

    private String execute(String... args) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String[] commandArgs = new String[args.length + 2];
        commandArgs[0] = "--delay";
        commandArgs[1] = "0";
        System.arraycopy(args, 0, commandArgs, 2, args.length);
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            int exitCode = new CommandLine(new Sorty()).execute(commandArgs);
            assertEquals(0, exitCode);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }
}
