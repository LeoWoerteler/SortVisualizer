package kn.uni.dbis.cs.sorting.algo;

import java.util.Random;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * Bogo Sort sorts a sequence of values by repeatedly shuffling it until the result is sorted.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class BogoSort implements Sorter {

    /** Random number generator. */
    private final Random rng = new Random();

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        for (;;) {
            boolean sorted = true;
            model.addArea(0, n);
            for (int i = 1; i < n; i++) {
                model.changeArea(0, i, n);
                model.setSpecial(i);
                if (model.compare(i - 1, i) > 0) {
                    sorted = false;
                    break;
                }
            }
            model.removeArea();
            model.setSpecial(-1);

            if (sorted) {
                return;
            }

            model.addArea(0, 0);
            for (int i = 1; i < n; i++) {
                model.changeArea(0, 0, i);
                model.setSpecial(i);
                final int j = rng.nextInt(i + 1);
                if (j != i) {
                    model.swap(i, j);
                }
            }
            model.removeArea();
            model.setSpecial(-1);
        }
    }
}
