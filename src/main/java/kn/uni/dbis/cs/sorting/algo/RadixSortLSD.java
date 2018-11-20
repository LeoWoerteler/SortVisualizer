package kn.uni.dbis.cs.sorting.algo;

import java.util.Arrays;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

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

        final int changingBits = 32 - Integer.numberOfLeadingZeros(~(ones | zeroes));
        int[] in;
        int[] out;
        if (changingBits % 2 == 0) {
            in = values;
            out = model.createCopy();
        } else {
            in = model.createCopy();
            out = values;
        }
        Arrays.fill(out, -1);

        for (int bit = 0; bit < changingBits; bit++) {
            final int mask = 1 << bit;
            int z = 0;
            int o = 0;
            for (int i = 0; i < n; i++) {
                if ((in[i] & mask) == 0) {
                    o++;
                }
            }
            final int m = o;
            model.addArea(0, 0);
            model.addArea(0, 0);
            model.addArea(m, m);
            for (int i = 0; i < n; i++) {
                model.changeArea(2, i, i + 1);
                final int val = in[i];
                in[i] = -1;
                final int pos;
                if ((val & mask) == 0) {
                    pos = z++;
                    model.changeArea(1, 0, z);
                } else {
                    pos = o++;
                    model.changeArea(0, m, o);
                }
                model.setSpecialValue(val);
                model.setValue(out, pos, val);
            }
            model.removeArea();
            model.removeArea();
            model.removeArea();
            model.setSpecial(-1);
            final int [] temp = in;
            in = out;
            out = temp;
        }
        model.destroyCopy();
    }
}
