package kn.uni.dbis.cs.sorting.algo;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * The Merge Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        model.createCopy();
        this.mergeSort(model, true, 0, model.getLength());
        model.destroyCopy();
    }

    /**
     * Sorts the values in the given range using merge sort.
     *
     * @param model data model
     * @param intoValues flag indicating if the primary array is the target
     * @param start start of the current range
     * @param n length of the range to sort
     * @throws InterruptedException if the thread was interrupted
     */
    private void mergeSort(final DataModel model, final boolean intoValues, final int start, final int n)
            throws InterruptedException {
        if (n < 2) {
            return;
        }

        // split phase
        if (n != model.getLength()) {
            model.addArea(start, start + n);
        }
        final int k = n / 2;
        final int mid = start + k;
        final int end = start + n - 1;
        this.mergeSort(model, !intoValues, start, k);
        this.mergeSort(model, !intoValues, mid, n - k);

        // merge phase
        int i = start;
        int j = start + k;
        final int[] from = intoValues ? model.getCopy() : model.getValues();
        final int[] to = intoValues ? model.getValues() : model.getCopy();
        model.addArea(j, end + 1);
        model.addArea(i, start + k);
        for (int o = start; o <= end; o++) {
            final int next;
            if (j > end || i < mid && model.compare(from, i, j) <= 0) {
                model.changeArea(0, i, start + k);
                next = i++;
            } else {
                model.changeArea(1, j, end + 1);
                next = j++;
            }
            final int val = from[next];
            from[next] = -1;
            model.setSpecialValue(val);
            model.setValue(to, o, val);
        }
        model.removeArea();
        model.removeArea();
        model.setSpecial(-1);
        if (n != model.getLength()) {
            model.removeArea();
        }
    }
}
