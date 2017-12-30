package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * Hybrid version of the Quick Sort algorithm that falls back to Insertion Sort for small ranges.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortHybrid implements Sorter {

    /** Maximum length of subsequences that are sorted using Insertion Sort. */
    private static final int INSERTION_LIMIT = 16;

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts the given range of values.
     *
     * @param model data model
     * @param start start of the range
     * @param n number of values in the range
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void sort(final DataModel model, final int start, final int n) throws InterruptedException {
        final int end = start + n;
        if (n <= INSERTION_LIMIT) {
            InsertionSort.sort(model, start, end);
            return;
        }
        model.addArea(start, end);
        model.swap(start, QuickSort3.medianOfThree(model, start, start + n / 2, end - 1));
        model.setSpecial(start);
        int pivEnd = start + 1;
        int ltEnd = pivEnd;
        int gtStart = end;
        model.addArea(pivEnd, gtStart);
        for (;;) {
            while (ltEnd < gtStart && model.compare(gtStart - 1, pivEnd - 1) > 0) {
                gtStart--;
                model.changeArea(0, ltEnd, gtStart);
            }
            while (ltEnd < gtStart) {
                final int cmp = model.compare(ltEnd, pivEnd - 1);
                if (cmp == 0) {
                    model.swap(pivEnd++, ltEnd++);
                    model.changeArea(0, ltEnd, gtStart);
                } else if (cmp < 0) {
                    ltEnd++;
                    model.changeArea(0, ltEnd, gtStart);
                } else {
                    break;
                }
            }
            if (ltEnd >= gtStart) {
                break;
            }
            model.swap(ltEnd, --gtStart);
            model.changeArea(0, ltEnd, gtStart);
        }
        model.removeArea();

        final int move = Math.min(pivEnd - start, ltEnd - pivEnd);
        for (int i = 0; i < move; i++) {
            model.swap(start + i, ltEnd - 1 - i);
        }
        model.setSpecial(-1);
        sort(model, start, ltEnd - pivEnd);
        sort(model, gtStart, end - gtStart);
        model.removeArea();
    }
}
