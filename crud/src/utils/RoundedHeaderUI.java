package utils;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import java.awt.*;
import java.awt.geom.Path2D;

public class RoundedHeaderUI extends BasicTableHeaderUI {
    private final int radius;

    public RoundedHeaderUI(int radius) {
        this.radius = radius;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        int w = c.getWidth();
        int h = c.getHeight();
        int r = radius;

        Path2D path = new Path2D.Float();
        path.moveTo(0, h);
        path.lineTo(0, r);
        path.quadTo(0, 0, r, 0);
        path.lineTo(w - r, 0);
        path.quadTo(w, 0, w, r);
        path.lineTo(w, h);
        path.closePath();

        g2.setClip(path);

        super.paint(g2, c);

        g2.dispose();
    }
}
