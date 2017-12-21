package kn.uni.dbis.pk2.sorting;

import java.util.function.Supplier;

import kn.uni.dbis.pk2.sorting.algo.BogoSort;
import kn.uni.dbis.pk2.sorting.algo.BubbleSort;
import kn.uni.dbis.pk2.sorting.algo.HeapSort;
import kn.uni.dbis.pk2.sorting.algo.InsertionSort;
import kn.uni.dbis.pk2.sorting.algo.MergeSort;
import kn.uni.dbis.pk2.sorting.algo.MergeSortNatural;
import kn.uni.dbis.pk2.sorting.algo.MergeSortNaturalIterative;
import kn.uni.dbis.pk2.sorting.algo.QuickSort3;
import kn.uni.dbis.pk2.sorting.algo.QuickSortHybrid;
import kn.uni.dbis.pk2.sorting.algo.QuickSortNaive;
import kn.uni.dbis.pk2.sorting.algo.QuickSortRandom;
import kn.uni.dbis.pk2.sorting.algo.RadixSortLSD;
import kn.uni.dbis.pk2.sorting.algo.RadixSortMSD;
import kn.uni.dbis.pk2.sorting.algo.SelectionSort;
import kn.uni.dbis.pk2.sorting.algo.ShakerSort;
import kn.uni.dbis.pk2.sorting.algo.ShakerSortDupl;
import kn.uni.dbis.pk2.sorting.algo.ShellSort;
import kn.uni.dbis.pk2.sorting.algo.SlowSort;
import kn.uni.dbis.pk2.sorting.algo.SmoothSort;

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
    SHAKERSORT_OPT("Shaker Sort (opt. for duplicates)", ShakerSortDupl::new, true),

    /** The Insertion Sort algorithm. */
    INSERTIONSORT("Insertion Sort", InsertionSort::new, true),

    /** The Insertion Sort algorithm. */
    SHELLSORT("Shell Sort", ShellSort::new, true),

    /** The naive Quick Sort algorithm. */
    QUICKSORT_NAIVE("Quick Sort (naïve)", QuickSortNaive::new, false),

    /** The Quick Sort algorithm. */
    QUICKSORT_RANDOM("Quick Sort (random pivot)", QuickSortRandom::new, false),

    /** The Quick Sort algorithm. */
    QUICKSORT("Quick Sort (median of 3)", QuickSort3::new, false),

    /** The Quick Sort algorithm. */
    QUICKSORT_INSERT("Quick Sort (falls back to insertion)", QuickSortHybrid::new, true),

    /** The Merge Sort algorithm. */
    MERGESORT("Merge Sort", MergeSort::new, false),

    /** The Merge Sort algorithm. */
    MERGESORT_NATURAL("Natural Merge Sort", MergeSortNatural::new, false),

    /** The Merge Sort algorithm. */
    MERGESORT_NATURAL_ITER("Iterative Natural Merge Sort", MergeSortNaturalIterative::new, true),

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
