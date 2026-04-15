package ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int arc;
    private final Color backgroundColor;

    public RoundedPanel(LayoutManager layout, Color backgroundColor, int arc) {
        super(layout);
        this.backgroundColor = backgroundColor;
        this.arc = arc;
        setOpaque(false);
    }

    public RoundedPanel(Color backgroundColor, int arc) {
        this(new BorderLayout(), backgroundColor, arc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}