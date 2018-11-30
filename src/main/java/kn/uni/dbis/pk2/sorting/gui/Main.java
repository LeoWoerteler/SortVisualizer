package kn.uni.dbis.pk2.sorting.gui;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    public static void main(final String[] args) throws ClassNotFoundException, javax.swing.UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
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
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SortVisualizer");
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        final javax.swing.JFrame frame = new javax.swing.JFrame(makeTitle(algorithm.get(), ordering.get()));
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        final SortPanel sortPanel = new SortPanel(model, max);
        frame.add(sortPanel, BorderLayout.CENTER);
        final javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        final javax.swing.JMenu sortAlgorithms = new javax.swing.JMenu("Sort Algorithm");
        final javax.swing.ButtonGroup algos = new javax.swing.ButtonGroup();
        boolean newGroup = false;
        for (final SortingAlgorithm algo : SortingAlgorithm.values()) {
            if (newGroup) {
                sortAlgorithms.addSeparator();
            }
            final javax.swing.JRadioButtonMenuItem algoItem = new javax.swing.JRadioButtonMenuItem(algo.toString());
            algoItem.setSelected(algo == algorithm.get());
            algoItem.addActionListener(l -> {
                algorithm.set(algo);
                frame.setTitle(makeTitle(algo, ordering.get()));
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
                frame.setTitle(makeTitle(algorithm.get(), dOrd));
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
        slider.setToolTipText("Number of milliseconds to wait between operations.");
        slider.setPaintLabels(true);
        final Hashtable<Integer, JLabel> labels = new Hashtable<>();
        final JLabel slow = new JLabel("SLOW");
        slow.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        labels.put(0, slow);
        final JLabel fast = new JLabel("FAST");
        fast.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        labels.put(100, fast);
        slider.setLabelTable(labels);
        frame.add(slider, BorderLayout.SOUTH);

        final JSlider slider2 = new JSlider(JSlider.VERTICAL, 0, 100, sleepTime.get());
        slider2.addChangeListener(e -> {
            timeDistribution.set(slider2.getValue());
        });
        slider2.setToolTipText("Distribution of wait time between comparisons and swaps.");
        slider2.setPaintLabels(true);
        final Hashtable<Integer, JLabel> labels2 = new Hashtable<>();
        final JLabel swp = new JLabel("<html>SLOW<br/>SWAP</html>");
        swp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        labels2.put(0, swp);
        final JLabel cmp = new JLabel("<html>SLOW<br/>COMP</html>");
        cmp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        labels2.put(100, cmp);
        slider2.setLabelTable(labels2);
        frame.add(slider2, BorderLayout.EAST);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

        // start the sorting process
        setSorterThread(sorterThread, algorithm.get(), model);

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
        setSorterThread(sorterThread, algo, newModel);
    }

    /**
     * Returns a window title.
     *
     * @param algo sorting algorithm
     * @param ordering data ordering
     * @return window title
     */
    static String makeTitle(final SortingAlgorithm algo, final DataOrdering ordering) {
        return "Sort Algorithm Visualizer  —  " + algo + "  vs.  " + ordering;
    }

    /**
     * Sets a new sorter thread.
     *
     * @param sorterThread reference to the current sorter thread
     * @param algo new sorting algorithm to use
     * @param model data model to sort
     */
    private static void setSorterThread(final AtomicReference<Thread> sorterThread,
                                        final SortingAlgorithm algo, final DataModel model) {
        final Sorter sorter = algo.newInstance();
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
