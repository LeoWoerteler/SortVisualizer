package kn.uni.dbis.pk2.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data model for sorting algorithms, contains the values to sort and
 * additional highlighting information for the visualization.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class DataModel {

    /** Number of milliseconds to wait between operations. */
    private final AtomicInteger sleepTime;

    /** Distribution of wait time between comparisons and swaps. */
    private final AtomicInteger distribution;

    /** Values to sort. */
    private final int[] values;

    /** Copy of values to sort. */
    private int[] copy;

    /** Highlighted areas of interest. */
    private final List<int[]> areas = new ArrayList<>();

    /** Special value (row) to highlight. */
    private int specialValue = -1;

    /**
     * Creates a new data model.
     *
     * @param values values to sort
     * @param sleepTime waiting time between steps of the sorting algorithm
     * @param timeDistribution sleeping time distribution
     */
    public DataModel(final int[] values, final AtomicInteger sleepTime, final AtomicInteger timeDistribution) {
        this.values = values;
        this.sleepTime = sleepTime;
        this.distribution = timeDistribution;
    }

    /**
     * Adds an area of interest.
     * @param start start of the area
     * @param end end of the area
     */
    public synchronized void addArea(final int start, final int end) {
        areas.add(new int[] { start, end });
    }

    /**
     * Changes the last added area.
     *
     * @param stackPos position of the area on the stack
     * @param start new start position
     * @param end new end position
     */
    public synchronized void changeArea(final int stackPos, final int start, final int end) {
        final int[] last = areas.get(areas.size() - 1 - stackPos);
        last[0] = start;
        last[1] = end;
    }

    /**
     * Removes the last added area of interest.
     */
    public synchronized void removeArea() {
        areas.remove(areas.size() - 1);
    }

    /**
     * Sets a new highlighted special value, {@code -1} means none.
     *
     * @param newValue new highlighted value
     */
    public synchronized void setSpecialValue(final int newValue) {
        this.specialValue = newValue;
    }

    /**
     * Sets a new highlighted special index, {@code -1} means none.
     *
     * @param index index of new highlighted value
     */
    public synchronized void setSpecial(final int index) {
        this.specialValue = index < 0 ? -1 : values[index];
    }

    /**
     * Returns the current special value.
     *
     * @return current highlighted special value, or {@code -1} if none is set
     */
    public int getSpecialValue() {
        return this.specialValue;
    }

    /**
     * Returns the current areas of interest, as two-element int arrays {@code int[]{ start, end }}.
     *
     * @return areas of interest
     */
    public Iterable<int[]> getAreas() {
        return this.areas;
    }

    /**
     * Returns the array of values to be sorted.
     *
     * @return values to be sorted
     */
    public int[] getValues() {
        return this.values;
    }

    /**
     * Creates a copy of the data array.
     *
     * @return the copy
     * @throws IllegalStateException if there already exists a copy
     */
    public synchronized int[] createCopy() {
        if (this.copy != null) {
            throw new IllegalStateException("copy already exists");
        }
        this.copy = this.values.clone();
        return this.copy;
    }

    /**
     * Checks if a copy has been created.
     *
     * @return {@code true} if a copy has been created, {@code false} otherwise
     */
    public synchronized boolean hasCopy() {
        return this.copy != null;
    }

    /**
     * Returns the copy of the data array.
     *
     * @return copy of the value array
     * @throws IllegalStateException if no copy exists
     */
    public synchronized int[] getCopy() {
        if (this.copy == null) {
            throw new IllegalStateException("no copy was created");
        }
        return this.copy;
    }

    /**
     * Destroys the copy.
     *
     * @throws IllegalStateException if no copy exists
     */
    public synchronized void destroyCopy() {
        if (this.copy == null) {
            throw new IllegalStateException("no copy exists");
        }
        this.copy = null;
    }

    /**
     * Returns the number of values to be sorted.
     *
     * @return number of values to be sorted
     */
    public int getLength() {
        return this.values.length;
    }

    /**
     * Compares two values.
     *
     * @param i index of the first value
     * @param j index of the second value
     * @return see {@link Integer#compare(int, int)}
     * @throws InterruptedException if the thread was interrupted
     */
    public int compare(final int i, final int j) throws InterruptedException {
        return compare(values, i, j);
    }

    /**
     * Compares two values in the given array.
     *
     * @param array the array
     * @param i index of the first value
     * @param j index of the second value
     * @return see {@link Integer#compare(int, int)}
     * @throws InterruptedException if the thread was interrupted
     */
    public int compare(final int[] array, final int i, final int j) throws InterruptedException {
        pause(false);
        return Integer.compare(array[i], array[j]);
    }

    /**
     * Swaps the values at positions {@code i} and {@code j}.
     *
     * @param i index of the first value to swap
     * @param j index of the second value to swap
     * @throws InterruptedException if the thread was interrupted
     */
    public synchronized void swap(final int i, final int j) throws InterruptedException {
        swap(values, i, j);
    }

    /**
     * Swaps two values in the given array.
     *
     * @param array array to swap the values in
     * @param i index of the first value to swap
     * @param j index of the second value to swap
     * @throws InterruptedException if the thread was interrupted
     */
    public synchronized void swap(final int[] array, final int i, final int j)
            throws InterruptedException {
        if (i != j) {
            final int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            pause(true);
        }
    }

    /**
     * Pauses the sorting algorithm.
     *
     * @param swap pause for a swap instead of a comparison
     * @throws InterruptedException if the thread was interrupted
     */
    public void pause(final boolean swap) throws InterruptedException {
        checkStop();
        final double millis = Math.ceil(Math.exp(this.sleepTime.get() / 13.155)) - 1;
        final int comp = distribution.get();
        final double factor;
        if (swap) {
            factor = comp <= 50 ? 1 : (100.0 - comp) / comp;
        } else {
            factor = comp >= 50 ? 1 : comp / (100.0 - comp);
        }
        Thread.sleep((long) Math.round(factor * millis));
    }

    /**
     * Checks if the thread was interrupted.
     *
     * @throws InterruptedException if the thread was interrupted
     */
    private static void checkStop() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * Sets a value in this data model.
     *
     * @param pos position of the value to set
     * @param value new value
     * @throws InterruptedException if the sorting thread was interrupted
     */
    public void setValue(final int pos, final int value) throws InterruptedException {
        setValue(this.values, pos, value);
    }

    /**
     * Sets a value in the given array.
     *
     * @param array array to set the value in
     * @param pos position of the value to set
     * @param value new value
     * @throws InterruptedException if the sorting thread was interrupted
     */
    public void setValue(final int[] array, final int pos, final int value)
            throws InterruptedException {
        array[pos] = value;
        pause(true);
    }
}
