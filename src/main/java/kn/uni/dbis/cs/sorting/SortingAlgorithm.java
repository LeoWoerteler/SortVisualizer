package kn.uni.dbis.cs.sorting;

import java.util.function.Supplier;

import kn.uni.dbis.cs.sorting.algo.BogoSort;
import kn.uni.dbis.cs.sorting.algo.BubbleSort;
import kn.uni.dbis.cs.sorting.algo.CombSort;
import kn.uni.dbis.cs.sorting.algo.HeapSort;
import kn.uni.dbis.cs.sorting.algo.InsertionSort;
import kn.uni.dbis.cs.sorting.algo.MergeSort;
import kn.uni.dbis.cs.sorting.algo.MergeSortNatural;
import kn.uni.dbis.cs.sorting.algo.MergeSortNaturalExtendedRuns;
import kn.uni.dbis.cs.sorting.algo.QuickSort;
import kn.uni.dbis.cs.sorting.algo.QuickSortIterative;
import kn.uni.dbis.cs.sorting.algo.RadixSortLSD;
import kn.uni.dbis.cs.sorting.algo.RadixSortMSD;
import kn.uni.dbis.cs.sorting.algo.SelectionSort;
import kn.uni.dbis.cs.sorting.algo.ShakerSort;
import kn.uni.dbis.cs.sorting.algo.ShakerSortDupl;
import kn.uni.dbis.cs.sorting.algo.ShellSort;
import kn.uni.dbis.cs.sorting.algo.SlowSort;
import kn.uni.dbis.cs.sorting.algo.SmoothSort;

/**
 * Enumeration of all supported sorting algorithms.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public enum SortingAlgorithm {
    /** The Selection Sort algorithm. */
    SELECTIONSORT("Selection Sort", SelectionSort::new, true),

    /** The Bubble Sort algorithm. */
    BUBBLESORT("Bubble Sort", BubbleSort::new, false),

    /** The Shaker Sort algorithm. */
    SHAKERSORT("Shaker Sort", ShakerSort::new, false),

    /** Variant of the Shaker Sort algorithm which moves blocks of equal values. */
    SHAKERSORT_OPT("Shaker Sort (opt. for duplicates)", ShakerSortDupl::new, false),

    /** The Comb Sort algorithm. */
    COMBSORT("Comb Sort", CombSort::new, true),

    /** The Insertion Sort algorithm. */
    INSERTIONSORT("Insertion Sort", InsertionSort::new, false),

    /** The Insertion Sort algorithm. */
    SHELLSORT("Shell Sort", ShellSort::new, true),

    /** The naive Quick Sort algorithm. */
    QUICKSORT_NAIVE("Quick Sort",
            () -> new QuickSort(QuickSort.RecursionEnd.AT_MOST_ONE,
                    QuickSort.PivotStrategy.FIRST, QuickSort.PartitionStrategy.NAIVE), false),

    /** The Quick Sort algorithm. */
    QUICKSORT_LOG("Quick Sort (log space)", () -> new QuickSortIterative(QuickSort.RecursionEnd.INSERTION_SORT,
            QuickSort.PivotStrategy.MEDIAN_OF_THREE, QuickSort.PartitionStrategy.PIVOTS_LEFT), false),

    /** The Quick Sort algorithm. */
    QUICKSORT_RANDOM("Quick Sort (random pivot)",
            () -> new QuickSort(QuickSort.RecursionEnd.AT_MOST_ONE,
                    QuickSort.PivotStrategy.RANDOM, QuickSort.PartitionStrategy.PIVOTS_LEFT), false),

    /** The Quick Sort algorithm. */
    QUICKSORT_MED3("Quick Sort (median of 3)",
            () -> new QuickSort(QuickSort.RecursionEnd.AT_MOST_ONE,
                    QuickSort.PivotStrategy.MEDIAN_OF_THREE, QuickSort.PartitionStrategy.PIVOTS_MID), false),

    /** The Quick Sort algorithm. */
    QUICKSORT_INSERT("Quick Sort (falls back to insertion)",
            () -> new QuickSort(QuickSort.RecursionEnd.INSERTION_SORT,
                    QuickSort.PivotStrategy.MEDIAN_OF_THREE, QuickSort.PartitionStrategy.PIVOTS_LEFT), false),

    /** The Quick Sort algorithm. */
    QUICKSORT_MEDMED("Quick Sort (median of medians)",
            () -> new QuickSort(QuickSort.RecursionEnd.INSERTION_SORT,
                    QuickSort.PivotStrategy.MEDIAN_OF_MEDIANS, QuickSort.PartitionStrategy.PIVOTS_LEFT), true),

    /** The Merge Sort algorithm. */
    MERGESORT("Merge Sort", MergeSort::new, false),

    /** The Merge Sort algorithm. */
    MERGESORT_NATURAL("Natural Merge Sort (up/down)", MergeSortNatural::new, false),

    /** The Merge Sort algorithm. */
    MERGESORT_NATURAL2("Natural Merge Sort (extends runs)", MergeSortNaturalExtendedRuns::new, true),

    /** The Heap Sort algorithm. */
    HEAPSORT("Heap Sort", HeapSort::new, false),

    /** The Smooth Sort algorithm, reverse Heap Sort that takes advantage of (partially) sorted input. */
    SMOOTHSORT("Smooth Sort", SmoothSort::new, true),

    /** The Radix Sort Algorithm. */
    RADIXSORT("Radix Sort", RadixSortMSD::new, false),

    /** The Radix Sort Algorithm. */
    RADIXSORT_LSD("Radix Sort (least significant bit)", RadixSortLSD::new, true),

    /** The Slow Sort algorithm. */
    SLOWSORT("Slow Sort", SlowSort::new, false),

    /** The Bogo Sort algorithm. */
    BOGOSORT("Bogo Sort", BogoSort::new, true);

    /** Name of the algorithm. */
    private final String name;

    /** Supplier for sorter instances. */
    private final Supplier<Sorter> supplier;

    /** Marker for algorithms at the end of their groups. */
    private final boolean endOfGroup;

    /**
     * Constructor.
     *
     * @param name name of the algorithm
     * @param supplier for constructing sorter instances
     * @param endOfGroup flag marking the end of a group of algorithms
     */
    SortingAlgorithm(final String name, final Supplier<Sorter> supplier, final boolean endOfGroup) {
        this.name = name;
        this.supplier = supplier;
        this.endOfGroup = endOfGroup;
    }

    /**
     * Instantiates an instance of the sorting algorithm.
     *
     * @return runnable instance of the sorting algorithm
     */
    public Sorter newInstance() {
        return supplier.get();
    }

    /**
     * Checks if this algorithm is at the end of his group.
     *
     * @return result of check
     */
    public boolean isEndOfGroup() {
        return this.endOfGroup;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
