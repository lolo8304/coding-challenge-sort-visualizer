package sorty;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;
import sorty.algorithms.SorterProtocol;
import sorty.ui.ConsoleUiDelegate;
import sorty.ui.LanternaUiDelegate;

@Command(
    name = "sorty",
    mixinStandardHelpOptions = true,
    version = "sorty 0.1.0",
    description = "Sort integer values."
)
public class Sorty implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sorty.class);
    private static boolean VERBOSE = false;
    private static boolean VERBOSE2 = false;

    @Spec
    private CommandSpec spec;

    @Option(
        names = {"-d", "--descending"},
        description = "Sort values from largest to smallest."
    )
    private boolean descending;

    @Option(
        names = "-v",
        description = "Enable verbose output."
    )
    private boolean verbose;

    @Option(
        names = "-vv",
        description = "Enable extra verbose output."
    )
    private boolean verbose2;

    @Option(
        names = {"-c", "--console"},
        description = "Use the console UI log."
    )
    private boolean console;

    @Option(
        names = {"-l", "--lanterna"},
        description = "Use the fullscreen Lanterna text UI. This is the default."
    )
    private boolean lanterna;

    @Option(
        names = "--wait",
        description = "Keep the final Lanterna screen open until any key is pressed."
    )
    private boolean wait;

    @Option(
        names = {"-s", "--slow"},
        description = "Use slow visualization speed (100ms)."
    )
    private boolean slow;

    @Option(
        names = {"-m", "--medium"},
        description = "Use medium visualization speed (50ms)."
    )
    private boolean medium;

    @Option(
        names = {"-f", "--fast"},
        description = "Use fast visualization speed (25ms)."
    )
    private boolean fast;

    @Option(
        names = {"-n", "--number-count"},
        defaultValue = "20",
        paramLabel = "TOTAL",
        description = "Total unsorted numbers to sort."
    )
    private int totalNumbers;

    @Option(
        names = "--seed",
        defaultValue = "0",
        paramLabel = "SEED",
        description = "Random seed. Default: ${DEFAULT-VALUE}."
    )
    private int seed;

    @Option(
        names = {"-from", "--from"},
        defaultValue = "10",
        paramLabel = "MIN",
        description = "Smallest generated number. Default: ${DEFAULT-VALUE}."
    )
    private int from;

    @Option(
        names = {"-to", "--to"},
        defaultValue = "100",
        paramLabel = "MAX",
        description = "Largest generated number. Default: ${DEFAULT-VALUE}."
    )
    private int to;

    @Option(
        names = "--speed",
        defaultValue = "MEDIUM",
        paramLabel = "SPEED",
        description = "Sort visualization speed: FAST=25ms, MEDIUM=50ms, SLOW=100ms. Default: ${DEFAULT-VALUE}."
    )
    private SortSpeed speed;

    @Option(
        names = "--algorithm",
        defaultValue = "BUBBLE",
        paramLabel = "ALGORITHM",
        description = "Sorting algorithm: BUBBLE, INSERT, SELECTION, or MERGE. Default: ${DEFAULT-VALUE}."
    )
    private SortAlgorithm algorithm;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Sorty()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (totalNumbers < 1) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Option -n must be greater than 0."
            );
        }
        if (from > to) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Option -from must be less than or equal to -to."
            );
        }
        if ((long) to - from + 1 > Integer.MAX_VALUE) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "The range between -from and -to is too large."
            );
        }
        if (selectedStartupSpeeds() > 1) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Only one of --slow, --medium, or --fast can be selected."
            );
        }

        Sorty.VERBOSE = this.verbose || this.verbose2;
        Sorty.VERBOSE2 = this.verbose2;

        SortDirection direction = descending ? SortDirection.DESCENDING : SortDirection.ASCENDING;
        try {
            var sorter = new Sorter(totalNumbers, from, to, direction, seed, algorithm, selectedSpeed());
            var uiDelegate = this.uiDelegate();
            sorter.setUiDelegate(uiDelegate);
            var result = sorter.sort();
            this.formatResult(result);
            return 0;
        } catch (SortInterruptedException exception) {
            return 130;
        }
    }

    private int selectedStartupSpeeds() {
        int selected = 0;
        if (slow) {
            selected++;
        }
        if (medium) {
            selected++;
        }
        if (fast) {
            selected++;
        }
        return selected;
    }

    private SortSpeed selectedSpeed() {
        if (slow) {
            return SortSpeed.SLOW;
        }
        if (medium) {
            return SortSpeed.MEDIUM;
        }
        if (fast) {
            return SortSpeed.FAST;
        }
        return speed;
    }

    private SorterProtocol uiDelegate() {
        if (console) {
            return new ConsoleUiDelegate();
        }
        return new LanternaUiDelegate(wait);
    }

    private void formatResult(Integer[] numbers) {
        var builder = new StringBuilder();
        for (Integer number : numbers) {
            builder.append(number).append(" ");
        }
        System.out.print(builder.toString());
    }

    public static boolean verbose() {
        return VERBOSE;
    }

    public static boolean verbose2() {
        return VERBOSE2;
    }
}
