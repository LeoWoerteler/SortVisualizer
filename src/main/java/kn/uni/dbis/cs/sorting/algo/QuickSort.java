package kn.uni.dbis.cs.sorting.algo;

import java.util.Random;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * The Quick Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSort implements Sorter {

    /** Limit for 'small' ranges, e.g. for falling back to insertion sort. */
    public static final int SMALL_LIMIT = 7;

    /** Strategies for ending the recursive descent. */
    public enum RecursionEnd {

        /** Stops when there are fewer than two values in the range. */
        AT_MOST_ONE() {
            @Override
            boolean end(final DataModel model, final int start, final int end) throws InterruptedException {
                return end - start < 2;
            }
        },

        /** Sorts ranges of length at most {@link #SMALL_LIMIT} using Insertion Sort. */
        INSERTION_SORT() {
            @Override
            boolean end(final DataModel model, final int start, final int end) throws InterruptedException {
                if (end - start <= SMALL_LIMIT) {
                    InsertionSort.sort(model, start, end);
                    return true;
                }
                return false;
            }
        };

        /**
         * Determines whether the recursion should stop and takes care of sorting the remaining values.
         *
         * @param model data model
         * @param start start of the range to be sorted
         * @param end end of the range to be sorted (exclusive)
         * @return {@code true} if no more recursion should occur, {@code false} otherwise
         * @throws InterruptedException if the sorting thread was interrupted
         */
        abstract boolean end(DataModel model, int start, int end) throws InterruptedException;
    }

    /** Strategies for picking a pivot value. */
    public enum PivotStrategy {

        /** Picks the first value in the range. */
        FIRST() {
            @Override
            int calculatePivotPos(final Random rng, final DataModel model, final int start, final int end)
                    throws InterruptedException {
                return start;
            }
        },

        /** Picks a random value inside the range. */
        RANDOM() {
            @Override
            int calculatePivotPos(final Random rng, final DataModel model, final int start, final int end)
                    throws InterruptedException {
                return start + rng.nextInt(end - start);
            }
        },

        /** Picks the median of the first, middle, and last value of the range. */
        MEDIAN_OF_THREE() {
            @Override
            int calculatePivotPos(final Random rng, final DataModel model, final int start, final int end)
                    throws InterruptedException {
                final int a = start;
                final int b = start + (end - start) / 2;
                final int c = end - 1;
                return model.compare(a, b) < 0
                        ? (model.compare(b, c) < 0 ? b : (model.compare(c, a) < 0 ? a : c))
                        : (model.compare(b, c) > 0 ? b : (model.compare(c, a) > 0 ? a : c));
            }
        },

        /** Uses the <i>median-of-medians</i> strategy from Quick Select to find a good pivot. */
        MEDIAN_OF_MEDIANS() {
            @Override
            int calculatePivotPos(final Random rng, final DataModel model, final int start, final int end)
                    throws InterruptedException {
                final int n = end - start;
                if (n <= SMALL_LIMIT) {
                    InsertionSort.sort(model, start, end);
                    final int mid = start + (end - start) / 2;
                    model.setSpecial(mid);
                    return mid;
                }
                model.addArea(0, 0);
                model.addArea(0, 0);
                final int parts = (n + SMALL_LIMIT - 1) / SMALL_LIMIT;
                for (int i = 0; i < parts; i++) {
                    final int off = start + SMALL_LIMIT * i;
                    final int k = Math.min(SMALL_LIMIT, end - off);
                    model.changeArea(0, off, off + k);
                    final int pos = calculatePivotPos(rng, model, off, off + k);
                    model.changeArea(1, start, start + i + 1);
                    model.swap(start + i, pos);
                }
                model.removeArea();
                model.changeArea(0, start, start + parts);
                final int res = calculatePivotPos(rng, model, start, start + parts);
                model.setSpecial(res);
                model.removeArea();
                return res;
            }
        };


        /**
         * Chooses the position of the pivot to be used, potentially reordering the values in the process.
         *
         * @param rng random, number generator
         * @param model data model
         * @param start start of the range
         * @param end end of the range (exclusive)
         * @return position of the value chosen as median
         * @throws InterruptedException if the sorting thread was interrupted
         */
        abstract int calculatePivotPos(Random rng, DataModel model, int start, int end)
                throws InterruptedException;
    }

    /** Strategies for partitioning the values in a range relative to a pivot value. */
    public enum PartitionStrategy {

        /** Naïve partition strategy that does not treat values exactly equal to the pivot differently. */
        NAIVE() {
            @Override
            int[] partition(final DataModel model, final int start, final int end) throws InterruptedException {
                model.setSpecial(start);
                int l = start + 1;
                int r = end - 1;
                model.addArea(l, r + 1);
                for (;;) {
                    while (l <= r && model.compare(l, start) <= 0) {
                        l++;
                        model.changeArea(0, l, r + 1);
                    }
                    while (l <= r && model.compare(r, start) > 0) {
                        r--;
                        model.changeArea(0, l, r + 1);
                    }
                    if (l > r) {
                        break;
                    }
                    model.swap(l++, r--);
                    model.changeArea(0, l, r + 1);
                }
                model.swap(start, r);
                model.removeArea();
                model.setSpecial(-1);
                return new int[] { r, l };
            }
        },

        /** Always keeps the values equal to the pivot in the middle between the smaller and the bigger ones. */
        PIVOTS_MID() {
            @Override
            int[] partition(final DataModel model, final int start, final int end) throws InterruptedException {
                model.setSpecial(start);
                int l = start;
                int m = start + 1;
                int r = end;
                model.addArea(m, r);
                for (;;) {
                    while (m < r && model.compare(r - 1, m - 1) > 0) {
                        r--;
                        model.changeArea(0, m, r);
                    }
                    while (m < r) {
                        final int cmp = model.compare(m, m - 1);
                        if (cmp < 0) {
                            model.swap(l++, m++);
                        } else if (cmp == 0) {
                            m++;
                        } else {
                            break;
                        }
                        model.changeArea(0, m, r);
                    }
                    if (m >= r) {
                        break;
                    }
                    model.swap(m, --r);
                    model.changeArea(0, m, r);
                }
                model.removeArea();
                model.setSpecial(-1);
                return new int[] { l, r };
            }
        },

        /** First moves all values equal to the pivot to the start of the range and swaps them back later. */
        PIVOTS_LEFT() {
            @Override
            int[] partition(final DataModel model, final int start, final int end) throws InterruptedException {
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
                return new int[] { start + ltEnd - pivEnd, gtStart };
            }
        };

        /**
         * Partitions the values in the given range into three sub-ranges and returns the two positions between those
         * in an array {@code [pos1, pos2]}.
         * <ol>
         *   <li> Values smaller than the pivot in the range {@code [start, pos1)},
         *   <li> values equal to the pivot in the range {@code [pos1, pos2)},
         *   <li> and values greater than the pivot in the range {@code [pos2, end)}.
         * </ol>
         *
         * @param model data model
         * @param start start of the range to be partitioned
         * @param end end of the range to be partitioned (exclusive)
         * @return borders between the three sub-ranges
         * @throws InterruptedException if the sorting thread was interrupted
         */
        abstract int[] partition(DataModel model, int start, int end) throws InterruptedException;
    }

    /** Random number generator. */
    private final Random rng = new Random();

    /** Strategy for ending the recursion. */
    private final RecursionEnd endCondition;

    /** Strategy for choosing the pivot. */
    private final PivotStrategy median;

    /** Strategy for partitioning the values. */
    private final PartitionStrategy partition;

    /**
     * Constructs a parameterized Quick Sort algorithm.
     *
     * @param endCondition strategy for ending the recursion
     * @param median strategy for choosing the pivot
     * @param partition strategy for partitioning the values
     */
    public QuickSort(final RecursionEnd endCondition, final PivotStrategy median,
            final PartitionStrategy partition) {
        this.endCondition = endCondition;
        this.median = median;
        this.partition = partition;
    }

    @Override
    public final void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Determines whether the recursion should stop and takes care of sorting the remaining values.
     *
     * @param model data model
     * @param start start of the range to be sorted
     * @param end end of the range to be sorted (exclusive)
     * @return {@code true} if no more recursion should occur, {@code false} otherwise
     * @throws InterruptedException if the sorting thread was interrupted
     */
    final boolean end(final DataModel model, final int start, final int end) throws InterruptedException {
        return this.endCondition.end(model, start, end);
    }

    /**
     * Chooses the position of the pivot to be used, potentially reordering the values in the process.
     *
     * @param model data model
     * @param start start of the range
     * @param end end of the range (exclusive)
     * @return position of the value chosen as median
     * @throws InterruptedException if the sorting thread was interrupted
     */
    final int calculatePivotPos(final DataModel model, final int start, final int end) throws InterruptedException {
        return this.median.calculatePivotPos(rng, model, start, end);
    }

    /**
     * Partitions the values in the given range into three sub-ranges and returns the two positions between those
     * in an array {@code [pos1, pos2]}.
     * <ol>
     *   <li> Values smaller than the pivot in the range {@code [start, pos1)},
     *   <li> values equal to the pivot in the range {@code [pos1, pos2)},
     *   <li> and values greater than the pivot in the range {@code [pos2, end)}.
     * </ol>
     *
     * @param model data model
     * @param start start of the range to be partitioned
     * @param end end of the range to be partitioned (exclusive)
     * @return borders between the three sub-ranges
     * @throws InterruptedException if the sorting thread was interrupted
     */
    final int[] partition(final DataModel model, final int start, final int end) throws InterruptedException {
        return this.partition.partition(model, start, end);
    }

    /**
     * Returns this sorter's random number generator.
     *
     * @return the random number generator
     */
    final Random getRandom() {
        return this.rng;
    }

    /**
     * Sorts the given range of the given data model using Quick Sort.
     *
     * @param model data model
     * @param start start of the range to sort
     * @param end end of the range to sort (exclusive)
     * @throws InterruptedException if the sorting thread was interrupted
     */
    void sort(final DataModel model, final int start, final int end) throws InterruptedException {
        if (this.end(model, start, end)) {
            return;
        }
        model.addArea(start, end);
        model.swap(start, this.calculatePivotPos(model, start, end));
        final int[] lr = this.partition(model, start, end);
        sort(model, start, lr[0]);
        sort(model, lr[1], end);
        model.removeArea();
    }
}
