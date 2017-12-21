package kn.uni.dbis.pk2.sorting.algo;

import java.util.Random;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * Bogo Sort sorts a sequence of values by repeatedly shuffling it until the result is sorted.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class BogoSort implements Sorter {
    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final Random rng = new Random();
        for (;;) {
            boolean sorted = true;
            model.addArea(0, values.length);
            for (int i = 1; i < values.length; i++) {
                model.changeArea(0, i, values.length);
                model.setSpecialValue(values[i]);
                model.pause();
                if (values[i - 1] > values[i]) {
                    sorted = false;
                    break;
                }
            }
            model.removeArea();
            model.setSpecialValue(-1);

            if (sorted) {
                return;
            }

            model.addArea(0, 0);
            for (int i = 1; i < values.length; i++) {
                model.changeArea(0, 0, i);
                model.setSpecialValue(values[i]);
                model.pause();
                final int j = rng.nextInt(i + 1);
                if (j != i) {
                    model.swap(i, j);
                }
            }
            model.removeArea();
            model.setSpecialValue(-1);
        }
    }
}
