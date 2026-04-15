package ui;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private final Color backgroundColor;
    private final Color foregroundColor;
    private final int arc = 18;

    public RoundedButton(String text, Color backgroundColor, Color foregroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(foregroundColor);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, 42));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = getModel().isPressed() ? backgroundColor.darker()
                : getModel().isRollover() ? backgroundColor.brighter()
                : backgroundColor;

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        FontMetrics fm = g2.getFontMetrics();
        Rectangle r = new Rectangle(0, 0, getWidth(), getHeight());
        int x = r.x + (r.width - fm.stringWidth(getText())) / 2;
        int y = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(foregroundColor);
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}