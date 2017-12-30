package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * Variant of the Radix Sort algorithm that sorts the least significant digit (i.e. bit) first.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class RadixSortLSD implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final int n = model.getLength();

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

        final int[] copy = values.clone();
        final int changingBits = 32 - Integer.numberOfLeadingZeros(~(ones | zeroes));
        for (int bit = 0; bit < changingBits; bit++) {
            final int mask = 1 << bit;
            int z = 0;
            int o = 0;
            for (int i = 0; i < n; i++) {
                if ((copy[i] & mask) == 0) {
                    o++;
                }
            }
            final int m = o;
            model.addArea(0, 0);
            model.addArea(0, 0);
            model.addArea(m, m);
            for (int i = 0; i < n; i++) {
                model.changeArea(2, i, i + 1);
                if ((copy[i] & mask) == 0) {
                    values[z++] = copy[i];
                    model.setSpecial(z - 1);
                    model.changeArea(1, 0, z);
                } else {
                    values[o++] = copy[i];
                    model.setSpecial(o - 1);
                    model.changeArea(0, m, o);
                }
                model.pause(true);
            }
            System.arraycopy(values, 0, copy, 0, n);
            model.removeArea();
            model.removeArea();
            model.removeArea();
            model.setSpecial(-1);
        }
    }
}
