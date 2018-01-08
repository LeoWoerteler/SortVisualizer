package kn.uni.dbis.pk2.sorting.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import kn.uni.dbis.pk2.sorting.DataModel;

/**
 * Panel displaying the sorting process.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SortPanel extends JPanel {
    /** Shade of gray for each background layer. */
    private static final Color TRANSPARENT_GRAY = new Color(0, 0, 0, 20);

    /** Data model to sort. */
    private DataModel model;

    /** Number of rows to show. */
    private final int numRows;

    /** Number of columns to show. */
    private final int numCols;

    /**
     * Constructor.
     *
     * @param model initial data model
     * @param height upper bound on the values
     */
    public SortPanel(final DataModel model, final int height) {
        this.model = model;
        final int[] values = model.getValues();
        this.numRows = height;
        this.numCols = values.length;
        this.setPreferredSize(new Dimension(numCols * Main.CELL_SIZE, numRows * Main.CELL_SIZE));
        setBackground(Color.WHITE);
    }

    /**
     * Changes the model to be shown.
     *
     * @param newModel new model
     */
    public void changeDataModel(final DataModel newModel) {
        synchronized (model) {
            model = newModel;
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        final double h = 1.0 * this.getHeight() / this.numRows;
        final double w = 1.0 * this.getWidth() / this.numCols;

        g2d.setColor(TRANSPARENT_GRAY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        synchronized (model) {
            for (final int[] area : model.getAreas()) {
                g2d.fill(new Rectangle2D.Double(area[0] * w, 0, (area[1] - area[0]) * w, getHeight()));
            }

            final int special = model.getSpecialValue();
            if (special >= 0) {
                final double top = getHeight() - (special + 1) * h;
                g2d.fill(new Rectangle2D.Double(0, top, getWidth(), h));
                for (final int[] area : model.getAreas()) {
                    g2d.setColor(Color.WHITE);
                    g2d.fill(new Rectangle2D.Double(area[0] * w, top, (area[1] - area[0]) * w, h));
                }
            }

            final int[] values = model.getValues();
            if (model.hasCopy()) {
                final int[] copy = model.getCopy();
                for (int i = 0; i < copy.length; i++) {
                    if (copy[i] != values[i]) {
                        g2d.setColor(copy[i] == special ? Color.RED : Color.GREEN);
                        g2d.fill(new Rectangle2D.Double(i * w, getHeight() - (copy[i] + 1) * h, w, h));
                    }
                }
            }

            for (int i = 0; i < values.length; i++) {
                g2d.setColor(values[i] == special ? Color.RED : Color.BLUE);
                g2d.fill(new Rectangle2D.Double(i * w, getHeight() - (values[i] + 1) * h, w, h));
            }
        }
    }
}
