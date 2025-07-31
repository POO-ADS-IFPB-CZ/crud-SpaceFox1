package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedScrollPane extends JScrollPane {
    private final int radius;

    public RoundedScrollPane(Component view, int radius) {
        super(view);
        this.radius = radius;
        setOpaque(false);

        JViewport viewport = getViewport();
        viewport.setOpaque(false);
        viewport = new JViewport() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setClip(clip);

                g2.setColor(getBackground());
                g2.fill(clip);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        viewport.setView(view);
        viewport.setOpaque(false);
        setViewport(viewport);

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setClip(clip);

        g2.setColor(getBackground());
        g2.fill(clip);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
    }
}
