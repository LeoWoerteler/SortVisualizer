package kn.uni.dbis.pk2.sorting.gui;

import java.awt.BorderLayout;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.DataOrdering;
import kn.uni.dbis.pk2.sorting.Sorter;
import kn.uni.dbis.pk2.sorting.SortingAlgorithm;

/**
 * Entry point for the sorting visualizer.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class Main {
    /** Edge length of the squares representing values. */
    static final int CELL_SIZE = 5;

    /** Update frequency of the GUI in frames per second. */
    private static final int FPS = 60;

    /** Number of values to sort. */
    private static final int NUM_VALUES = 300;

    /** Upper limit on the values to be sorted. */
    private static final int MAX = 200;

    /** Unused. */
    private Main() {
    }

    /**
     * 
     * @param args unused
     */
    public static void main(final String[] args) {
        final int numValues;
        final int max;
        if (args.length > 0) {
            if (args.length != 2) {
                System.err.println("Call either with no arguments or with two integers "
                        + "(number of values, and upper bound)");
                return;
            }
            try {
                numValues = Integer.parseInt(args[0]);
            } catch (final NumberFormatException e) {
                System.err.println("Call either with no arguments or with two integers "
                        + "(number of values, and upper bound)\nnot an integer: " + args[0]);
                return;
            }
            try {
                max = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                System.err.println("Call either with no arguments or with two integers "
                        + "(number of values, and upper bound)\nnot an integer: " + args[1]);
                return;
            }
            if (numValues <= 0) {
                System.err.println("Number of values too small: " + numValues + ", must be greater than 0");
                return;
            }
            if (max <= 0) {
                System.err.println("Upper bound too small: " + max + ", must be greater than 0");
                return;
            }
        } else {
            numValues = NUM_VALUES;
            max = MAX;
        }

        // fill an array with random values
        final Random rng = new Random();
        final AtomicInteger sleepTime = new AtomicInteger(50);
        final AtomicInteger timeDistribution = new AtomicInteger(50);
        final AtomicReference<Thread> sorterThread = new AtomicReference<>();
        final AtomicReference<DataOrdering> ordering = new AtomicReference<>(DataOrdering.RANDOM);
        final AtomicReference<SortingAlgorithm> algorithm = new AtomicReference<>(SortingAlgorithm.INSERTIONSORT);
        final DataModel model = makeModel(rng, numValues, max, ordering.get(), sleepTime, timeDistribution);

        // initialize the GUI
        final JFrame frame = new JFrame("Sort Algorithm Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final SortPanel sortPanel = new SortPanel(model, max);
        frame.add(sortPanel, BorderLayout.CENTER);
        final JMenuBar menuBar = new JMenuBar();
        final JMenu sortAlgorithms = new JMenu("Sort Algorithm");
        final ButtonGroup algos = new ButtonGroup();
        boolean newGroup = false;
        for (final SortingAlgorithm algo : SortingAlgorithm.values()) {
            if (newGroup) {
                sortAlgorithms.addSeparator();
            }
            final JRadioButtonMenuItem algoItem = new JRadioButtonMenuItem(algo.toString());
            algoItem.setSelected(algo == algorithm.get());
            algoItem.addActionListener(l -> {
                algorithm.set(algo);
                final DataModel newModel = makeModel(rng, numValues, max, ordering.get(), sleepTime, timeDistribution);
                restartSorting(sorterThread, sortPanel, algo, newModel);
            });
            algos.add(algoItem);
            sortAlgorithms.add(algoItem);
            newGroup = algo.isEndOfGroup();
        }
        menuBar.add(sortAlgorithms);


        final JMenu dataOrder = new JMenu("Data Ordering");
        final ButtonGroup orders = new ButtonGroup();
        for (final DataOrdering dOrd : DataOrdering.values()) {
            final JRadioButtonMenuItem dOrdItem = new JRadioButtonMenuItem(dOrd.toString());
            dOrdItem.setSelected(dOrd == ordering.get());
            dOrdItem.addActionListener(l -> {
                ordering.set(dOrd);
                final DataModel newModel = makeModel(rng, numValues, max, dOrd, sleepTime, timeDistribution);
                restartSorting(sorterThread, sortPanel, algorithm.get(), newModel);
            });
            orders.add(dOrdItem);
            dataOrder.add(dOrdItem);
        }
        menuBar.add(dataOrder);

        frame.setJMenuBar(menuBar);
        final JSlider slider = new JSlider(0, 100, sleepTime.get());
        slider.addChangeListener(e -> {
            sleepTime.set(100 - slider.getValue());
        });
        frame.add(slider, BorderLayout.SOUTH);
        final JSlider slider2 = new JSlider(0, 100, sleepTime.get());
        slider2.setOrientation(SwingConstants.VERTICAL);
        slider2.addChangeListener(e -> {
            timeDistribution.set(slider2.getValue());
        });
        frame.add(slider2, BorderLayout.EAST);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

        // start the sorting process
        setSorterThread(sorterThread, algorithm.get().newInstance(), model);

        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1000 / FPS);
                sortPanel.repaint();
            } catch (final InterruptedException e) {
                final Thread sort = sorterThread.get();
                if (sort != null) {
                    sort.interrupt();
                }
                break;
            }
        }
    }

    /**
     * Makes a new data model to be sorted.
     *
     * @param rng random number generator
     * @param numValues number of values to sort
     * @param max upper bound for the values
     * @param ordering data ordering
     * @param sleepTime reference to the sleeping time
     * @param timeDistribution reference to the proportion between the speed of comparisons and swaps
     * @return data model
     */
    private static DataModel makeModel(final Random rng, final int numValues, final int max,
            final DataOrdering ordering, final AtomicInteger sleepTime, final AtomicInteger timeDistribution) {
        final int[] newArray = new int[numValues];
        ordering.fill(newArray, max, rng);
        return new DataModel(newArray, sleepTime, timeDistribution);
    }

    /**
     * Restarts the sorting process with the given configuration.
     *
     * @param sorterThread reference to the current sorter thread
     * @param sortPanel sort panel
     * @param algo sorting algorithm
     * @param newModel new data model to sort
     */
    private static void restartSorting(final AtomicReference<Thread> sorterThread,
            final SortPanel sortPanel, final SortingAlgorithm algo, final DataModel newModel) {
        final Thread old = sorterThread.get();
        if (old != null) {
            old.interrupt();
        }
        sortPanel.changeDataModel(newModel);
        setSorterThread(sorterThread, algo.newInstance(), newModel);
    }

    /**
     * Sets a new sorter thread.
     *
     * @param sorterThread reference to the current sorter thread
     * @param sorter ne wsorter to use
     * @param model data model to sort
     */
    private static void setSorterThread(final AtomicReference<Thread> sorterThread,
            final Sorter sorter, final DataModel model) {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sorter.sort(model);
                } catch (final InterruptedException e) {
                    // let the thread die
                }
            }
        };
        thread.start();
        final Thread old = sorterThread.getAndSet(thread);
        if (old != null && old.isAlive()) {
            old.interrupt();
        }
    }
}
