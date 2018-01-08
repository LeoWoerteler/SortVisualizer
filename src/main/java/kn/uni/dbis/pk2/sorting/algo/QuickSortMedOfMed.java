package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * A naïve implementation of the Quick Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortMedOfMed implements Sorter {

    /** Size of the groups for the medians. */
    private static final int BATCH_SIZE = 7;

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts the given range of the given data model.
     *
     * @param model data model
     * @param start start of the range to sort
     * @param n length of the range
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private void sort(final DataModel model, final int start, final int n) throws InterruptedException {
        final int end = start + n;
        if (n <= BATCH_SIZE) {
            InsertionSort.sort(model, start, end);
            return;
        }

        final int medPos = medianOfMedians(model, start, end);
        model.swap(start, medPos);
        model.addArea(start, end);
        model.setSpecial(start);
        int pivEnd = start + 1;
        int ltEnd = start + 1;
        int gtStart = end;
        model.addArea(pivEnd, gtStart);
        for (;;) {
            while (ltEnd < gtStart && model.compare(gtStart - 1, start) > 0) {
                gtStart--;
                model.changeArea(0, ltEnd, gtStart);
            }
            while (ltEnd < gtStart) {
                final int cmp = model.compare(ltEnd, start);
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

    /**
     * Calculates the median of medians of groups of size {@link #BATCH_SIZE}.
     *
     * @param model data model
     * @param start start of the range
     * @param end end of the range (exclusive)
     * @return index of the median
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private int medianOfMedians(final DataModel model, final int start, final int end)
            throws InterruptedException {
        final int n = end - start;
        if (n <= BATCH_SIZE) {
            final int mid = median(model, start, end);
            model.setSpecial(mid);
            return mid;
        }

        model.addArea(0, 0);
        model.addArea(0, 0);
        final int parts = (n + BATCH_SIZE - 1) / BATCH_SIZE;
        for (int i = 0; i < parts; i++) {
            final int off = start + BATCH_SIZE * i;
            final int k = Math.min(BATCH_SIZE, end - off);
            model.changeArea(0, off, off + k);
            final int pos = medianOfMedians(model, off, off + k);
            model.changeArea(1, start, start + i + 1);
            model.swap(start + i, pos);
        }
        model.removeArea();
        model.changeArea(0, start, start + parts);
        final int res = medianOfMedians(model, start, start + parts);
        model.setSpecial(res);
        model.removeArea();
        return res;
    }

    /**
     * Calculates the median of a given range of values.
     *
     * @param model data model
     * @param start start of the range
     * @param end end of the range (exclusive)
     * @return index of the median
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static int median(final DataModel model, final int start, final int end)
            throws InterruptedException {
        InsertionSort.sort(model, start, end);
        return start + (end - start) / 2;
    }
}
