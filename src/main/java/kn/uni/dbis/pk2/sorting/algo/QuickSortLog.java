package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * Hybrid version of the Quick Sort algorithm that falls back to Insertion Sort for small ranges.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortLog implements Sorter {

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
        int from = start;
        int len = n;
        while (len > INSERTION_LIMIT) {
            final int end = from + len;
            model.addArea(from, end);
            model.swap(from, QuickSort3.medianOfThree(model, from, from + len / 2, end - 1));
            model.setSpecial(from);
            int pivEnd = from + 1;
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

            final int move = Math.min(pivEnd - from, ltEnd - pivEnd);
            for (int i = 0; i < move; i++) {
                model.swap(from + i, ltEnd - 1 - i);
            }
            model.setSpecial(-1);
            final int l = ltEnd - pivEnd;
            final int r = end - gtStart;
            if (l >= r) {
                sort(model, from, l);
                from = gtStart;
            } else {
                sort(model, gtStart, r);
                len = ltEnd - pivEnd;
            }
            model.removeArea();
        }
        InsertionSort.sort(model, from, from + len);
    }
}
