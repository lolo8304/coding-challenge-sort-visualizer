package sorty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
    }

    @Test
    void speedFlagIsRecognized() {
        CommandLine commandLine = new CommandLine(new Sorty());

        assertTrue(commandLine.getCommandSpec().findOption("--speed").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--slow").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-s").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--medium").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-m").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--fast").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("-f").isOption());
        assertTrue(commandLine.getCommandSpec().findOption("--seed").isOption());
    }

    @Test
    void commandRejectsMultipleStartupSpeeds() {
        int exitCode = new CommandLine(new Sorty()).execute("-n", "3", "--slow", "--fast");

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
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            int exitCode = new CommandLine(new Sorty()).execute(args);
            assertEquals(0, exitCode);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }
}
