package kn.uni.dbis.pk2.sorting.algo;

import java.util.Random;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Quick Sort algorithm, using a randomly chosen value as pivot.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortRandom implements Sorter {

    /** Random number generator. */
    private final Random rng = new Random();

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
    private void sort(final DataModel model, final int start, final int n) throws InterruptedException {
        final int end = start + n;
        if (n < 2) {
            return;
        }
        model.addArea(start, end);
        final int[] array = model.getValues();
        final int pivot = array[start + rng.nextInt(n)];
        model.setSpecialValue(pivot);
        int pivEnd = start;
        int ltEnd = start;
        int gtStart = end;
        model.addArea(pivEnd, gtStart);
        for (;;) {
            while (ltEnd < gtStart && array[gtStart - 1] > pivot) {
                gtStart--;
                model.changeArea(0, ltEnd, gtStart);
                model.pause();
            }
            while (ltEnd < gtStart) {
                if (array[ltEnd] == pivot) {
                    model.swap(pivEnd++, ltEnd++);
                    model.changeArea(0, ltEnd, gtStart);
                    model.pause();
                } else if (array[ltEnd] < pivot) {
                    ltEnd++;
                    model.changeArea(0, ltEnd, gtStart);
                    model.pause();
                } else {
                    break;
                }
            }
            if (ltEnd >= gtStart) {
                break;
            }
            model.swap(ltEnd, --gtStart);
            model.changeArea(0, ltEnd, gtStart);
            model.pause();
        }
        model.removeArea();

        final int move = Math.min(pivEnd - start, ltEnd - pivEnd);
        for (int i = 0; i < move; i++) {
            model.swap(start + i, ltEnd - 1 - i);
            model.pause();
        }
        model.setSpecialValue(-1);
        sort(model, start, ltEnd - pivEnd);
        sort(model, gtStart, end - gtStart);
        model.removeArea();
    }
}
