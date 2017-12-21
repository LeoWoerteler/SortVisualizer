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
    /** Values to sort. */
    private final int[] values;
    /** Highlighted areas of interest. */
    private final List<int[]> areas = new ArrayList<>();
    /** Special value (row) to highlight. */
    private int specialValue = -1;

    /**
     * Creates a new data model.
     *
     * @param values values to sort
     * @param sleepTime waiting time between steps of the sorting algorithm
     */
    public DataModel(final int[] values, final AtomicInteger sleepTime) {
        this.values = values;
        this.sleepTime = sleepTime;
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
     * Returns the number of values to be sorted.
     *
     * @return number of values to be sorted
     */
    public int getLength() {
        return this.values.length;
    }

    /**
     * Swaps the values at positions {@code i} and {@code j}.
     * @param i index of the first value to swap
     * @param j index of the second value to swap
     */
    public synchronized void swap(final int i, final int j) {
        final int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    /**
     * Pauses the sorting algorithm.
     *
     * @throws InterruptedException if the thread was interrupted
     */
    public void pause() throws InterruptedException {
        checkStop();
        final long millis = (long) Math.ceil(Math.exp(this.sleepTime.get() / 13.155)) - 1;
        Thread.sleep(millis);
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
}
