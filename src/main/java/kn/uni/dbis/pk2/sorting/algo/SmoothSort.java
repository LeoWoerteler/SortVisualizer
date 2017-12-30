package kn.uni.dbis.pk2.sorting.algo;
import java.util.BitSet;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Smooth Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SmoothSort implements Sorter {

    /** Leonardo numbers that fit into an {@code int}. */
    private static final int[] L = new int[44];
    static {
        L[0] = 1;
        L[1] = 1;
        for (int i = 2; i < L.length; i++) {
            L[i] = L[i - 2] + L[i - 1] + 1;
        }
    }

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        if (model.getLength() < 2) {
            return;
        }
        final BitSet sizes = buildHeaps(model);
        sort(model, sizes);
    }

    /**
     * Initial phase that builds a sequence of heaps on the unsorted data.
     *
     * @param model data model
     * @return heap sizes
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static BitSet buildHeaps(final DataModel model) throws InterruptedException {
        final BitSet sizes = new BitSet();
        sizes.set(1);
        model.addArea(0, 2);
        int minSize = 1;
        for (int curr = 1, n = model.getLength(); curr < n; curr++) {
            if (sizes.get(minSize + 1)) {
                // build a heap from the two previous heaps and the current element
                sizes.clear(minSize, minSize + 2);
                model.removeArea();
                model.removeArea();
                minSize += 2;
            } else {
                // current element is singleton heap
                minSize = minSize == 1 ? 0 : 1;
            }
            sizes.set(minSize);
            model.addArea(0, curr + 1);

            // move new value into the correct heap
            restoreHeaps(model, sizes, minSize, curr);
        }
        return sizes;
    }

    /**
     * Sorts the data by successively extracting the maximal value from the sequence of heaps.
     *
     * @param model data model
     * @param sizes heap sizes
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void sort(final DataModel model, final BitSet sizes) throws InterruptedException {
        int min = sizes.nextSetBit(0);
        for (int i = model.getLength(); --i > 0;) {
            sizes.clear(min);
            model.removeArea();
            if (min < 2) {
                min = sizes.nextSetBit(min + 1);
                continue;
            }

            sizes.set(min - 1);
            model.addArea(0, i - L[min]);
            sizes.set(min - 2);
            model.addArea(0, i);
            min -= 2;

            restoreHeaps(model, sizes, min + 1, i - 1 - L[min]);
            restoreHeaps(model, sizes, min, i - 1);
        }
    }

    /**
     * Restores the heaps after the maximum was extracted.
     *
     * @param model data model
     * @param sizes heap sizes
     * @param initSizeL index of the initial heap's size
     * @param initPos initial position in the array
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void restoreHeaps(final DataModel model, final BitSet sizes, final int initSizeL, final int initPos)
            throws InterruptedException {
        model.setSpecial(initPos);
        int pos = initPos;
        int sizeL = initSizeL;
        for (;;) {
            final int nextL = sizes.nextSetBit(sizeL + 1);
            if (nextL < 0) {
                // we are in the first heap
                break;
            }
            final int size = L[sizeL];
            final int nextHead = pos - size;
            final int rightHead = pos - 1;
            if (model.compare(nextHead, pos) <= 0
                    || size > 1 && (model.compare(nextHead, rightHead) < 0
                            || model.compare(nextHead, rightHead - L[sizeL - 2]) < 0)) {
                // we have found the correct heap
                break;
            }
            model.swap(pos, nextHead);
            pos = nextHead;
            sizeL = nextL;
        }

        siftDown(model, sizeL, pos);
        model.setSpecial(-1);
    }

    /**
     * Sifts a value down into a single heap.
     *
     * @param model data model
     * @param initL index of the heap's size
     * @param headPos position of the heap's head
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void siftDown(final DataModel model, final int initL, final int headPos)
            throws InterruptedException {
        // sink the current value into its heap
        int sizeL = initL;
        int pos = headPos;
        while (sizeL > 1) {
            final int right = pos - 1;
            final int left = right - L[sizeL - 2];
            if (model.compare(left, right) <= 0) {
                // right child is bigger
                if (model.compare(pos, right) >= 0) {
                    break;
                }
                model.swap(pos, right);
                sizeL -= 2;
                pos = right;
            } else {
                // left child is bigger
                if (model.compare(pos, left) >= 0) {
                    break;
                }
                model.swap(pos, left);
                sizeL -= 1;
                pos = left;
            }
        }
    }
}
