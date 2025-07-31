package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JTextFieldWithPlaceholder extends JTextField implements FocusListener {
    private String placeholder;

    public JTextFieldWithPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        addFocusListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = insets.left + 2;
            int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        repaint();
    }
}
