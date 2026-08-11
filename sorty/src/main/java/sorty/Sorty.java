package sorty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
import sorty.ui.LanternaGridUiDelegate;
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
        names = "--delay",
        defaultValue = "100",
        paramLabel = "MS",
        description = "Delay between visualization events in milliseconds. Default: ${DEFAULT-VALUE}."
    )
    private int delayMillis;

    @Option(
        names = "--algorithm",
        defaultValue = "BUBBLE",
        paramLabel = "ALGORITHM",
        description = "Sorting algorithm list: comma-separated names or * for all. Default: ${DEFAULT-VALUE}."
    )
    private String algorithm;

    @Option(
        names = "-2",
        description = "Run selected algorithms in batches of 2 for split-screen comparison."
    )
    private boolean splitTwo;

    @Option(
        names = "-4",
        description = "Run selected algorithms in batches of 4 for split-screen comparison."
    )
    private boolean splitFour;

    @Option(
        names = "-9",
        description = "Run selected algorithms in batches of 9 for 3x3 split-screen comparison on large screens."
    )
    private boolean splitNine;

    @Option(
        names = "-16",
        description = "Run selected algorithms in batches of 16 for 4x4 split-screen comparison on large screens."
    )
    private boolean splitSixteen;

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
        if (delayMillis < 0) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Option --delay must be greater than or equal to 0."
            );
        }
        if (selectedSplitOptions() > 1) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Only one of -2, -4, -9, or -16 can be selected."
            );
        }

        Sorty.VERBOSE = this.verbose || this.verbose2;
        Sorty.VERBOSE2 = this.verbose2;

        SortDirection direction = descending ? SortDirection.DESCENDING : SortDirection.ASCENDING;
        try {
            List<SortAlgorithm> algorithms = selectedAlgorithms();
            Integer[] input = randomNumbers();
            int batchSize = selectedBatchSize();
            for (int start = 0; start < algorithms.size(); start += batchSize) {
                int end = Math.min(start + batchSize, algorithms.size());
                boolean isLastBatch = end == algorithms.size();
                runBatch(algorithms.subList(start, end), input, direction, algorithms.size() > 1, isLastBatch);
            }
            return 0;
        } catch (SortInterruptedException exception) {
            return 130;
        }
    }

    private void runBatch(
        List<SortAlgorithm> algorithms,
        Integer[] input,
        SortDirection direction,
        boolean includeAlgorithmName,
        boolean isLastBatch
    ) {
        int batchSize = selectedBatchSize();
        if (!console && batchSize > 1) {
            runLanternaGridBatch(algorithms, input, direction, includeAlgorithmName, batchSize, isLastBatch);
            return;
        }

        for (SortAlgorithm selectedAlgorithm : algorithms) {
            var sorter = new Sorter(totalNumbers, from, to, direction, seed, selectedAlgorithm, delayMillis);
            sorter.setUiDelegate(this.uiDelegate(isLastBatch));
            Integer[] result = sorter.sort(input);
            formatResult(selectedAlgorithm, result, includeAlgorithmName);
        }
    }

    private void runLanternaGridBatch(
        List<SortAlgorithm> algorithms,
        Integer[] input,
        SortDirection direction,
        boolean includeAlgorithmName,
        int panelCount,
        boolean isLastBatch
    ) {
        List<Integer[]> results = new ArrayList<>();
        try (LanternaGridUiDelegate grid = new LanternaGridUiDelegate(panelCount, wait && isLastBatch)) {
            for (int index = 0; index < algorithms.size(); index++) {
                SortAlgorithm selectedAlgorithm = algorithms.get(index);
                var sorter = new Sorter(totalNumbers, from, to, direction, seed, selectedAlgorithm, delayMillis);
                sorter.setUiDelegate(grid.panel(index));
                results.add(sorter.sort(input));
            }
        }
        for (int index = 0; index < algorithms.size(); index++) {
            formatResult(algorithms.get(index), results.get(index), includeAlgorithmName);
        }
    }

    private Integer[] randomNumbers() {
        int range = Math.toIntExact((long) to - from + 1);
        Random random = seed > 0 ? new Random(seed) : new Random();
        return random.ints(totalNumbers, 0, range)
            .map(value -> from + value)
            .boxed()
            .toArray(Integer[]::new);
    }

    private List<SortAlgorithm> selectedAlgorithms() {
        if ("*".equals(algorithm.trim())) {
            return Arrays.asList(SortAlgorithm.values());
        }

        List<SortAlgorithm> algorithms = new ArrayList<>();
        for (String value : algorithm.split(",")) {
            String name = value.trim();
            if (!name.isEmpty()) {
                try {
                    algorithms.add(SortAlgorithm.valueOf(name.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException exception) {
                    throw new CommandLine.ParameterException(
                        spec.commandLine(),
                        "Unknown algorithm: " + name
                    );
                }
            }
        }
        if (algorithms.isEmpty()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "At least one algorithm must be selected.");
        }
        return algorithms;
    }

    private int selectedBatchSize() {
        if (splitSixteen) {
            return 16;
        }
        if (splitNine) {
            return 9;
        }
        if (splitFour) {
            return 4;
        }
        if (splitTwo) {
            return 2;
        }
        return 1;
    }

    private int selectedSplitOptions() {
        int selected = 0;
        if (splitTwo) {
            selected++;
        }
        if (splitFour) {
            selected++;
        }
        if (splitNine) {
            selected++;
        }
        if (splitSixteen) {
            selected++;
        }
        return selected;
    }

    private SorterProtocol uiDelegate(boolean waitForKeyBeforeClose) {
        if (console) {
            return new ConsoleUiDelegate();
        }
        return new LanternaUiDelegate(wait && waitForKeyBeforeClose);
    }

    private void formatResult(SortAlgorithm algorithm, Integer[] numbers, boolean includeAlgorithmName) {
        var builder = new StringBuilder();
        if (includeAlgorithmName) {
            builder.append(algorithm).append(": ");
        }
        for (Integer number : numbers) {
            builder.append(number).append(" ");
        }
        if (includeAlgorithmName) {
            builder.append(System.lineSeparator());
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
