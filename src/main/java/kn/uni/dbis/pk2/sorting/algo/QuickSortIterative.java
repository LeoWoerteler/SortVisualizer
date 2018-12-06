package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;

/**
 * Hybrid version of the Quick Sort algorithm that falls back to Insertion Sort for small ranges.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortIterative extends QuickSort {

    /**
     * Constructs a parameterized iterative Quick Sort.
     *
     * @param endCondition strategy for ending the recursion
     * @param median strategy for choosing the pivot
     * @param partition strategy for partitioning the values
     */
    public QuickSortIterative(final RecursionEnd endCondition, final PivotStrategy median,
            final PartitionStrategy partition) {
        super(endCondition, median, partition);
    }

    @Override
    void sort(final DataModel model, final int start, final int end, final int depth) throws InterruptedException {
        model.addArea(start, end);
        int from = start;
        int to = end;
        while (!this.end(model, from, to, depth)) {
            model.swap(from, this.calculatePivotPos(model, from, to));
            final int[] lr = this.partition(model, from, to);
            // sort the smaller part recursively
            if (lr[0] - from <= to - lr[1]) {
                sort(model, from, lr[0], depth + 1);
                from = lr[1];
            } else {
                sort(model, lr[1], to, depth + 1);
                to = lr[0];
            }
            model.changeArea(0, from, to);
        }
        model.removeArea();
        InsertionSort.sort(model, from, to);
    }
}
