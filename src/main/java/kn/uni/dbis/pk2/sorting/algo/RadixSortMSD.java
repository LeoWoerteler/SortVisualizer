package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Radix Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class RadixSortMSD implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final int n = values.length;
        int ones = values[0];
        int zeroes = ~ones;
        boolean sorted = true;
        for (int i = 1; i < n; i++) {
            ones &= values[i];
            zeroes &= ~values[i];
            sorted &= values[i - 1] <= values[i];
        }
        if (sorted) {
            return;
        }

        final int changingBits = 32 - Integer.numberOfLeadingZeros(~(ones | zeroes));
        radixSort(model, 1 << (changingBits - 1), 0, n);
    }

    /**
     * Recursively sorts the given range of the given data model.
     *
     * @param model data model
     * @param mask bit mask
     * @param start start of the range to sort
     * @param end end of the range to sort
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private void radixSort(final DataModel model, final int mask, final int start, final int end)
            throws InterruptedException {
        if (start >= end) {
            return;
        }
        final int[] values = model.getValues();
        model.addArea(start, end);
        int l = start;
        int r = end;
        model.addArea(l, r);
        while (l < r) {
            model.setSpecial(l);
            model.pause(false);
            if ((values[l] & mask) == 0) {
                l++;
            } else {
                model.swap(l, --r);
            }
            model.changeArea(0, l, r);
        }
        model.setSpecial(-1);
        model.removeArea();
        final int lowerMask = mask >>> 1;
        if (lowerMask != 0) {
            radixSort(model, lowerMask, start, r);
            radixSort(model, lowerMask, r, end);
        }
        model.removeArea();
    }
}
