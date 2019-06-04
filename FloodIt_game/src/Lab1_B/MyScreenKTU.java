package Lab1_B;

import studijosKTU.ScreenKTU;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MyScreenKTU extends ScreenKTU {
    private java.util.List<EventsListener> listeners = new ArrayList<>();

    public MyScreenKTU(int ch, int cw, int sh, int sw, Grid gr) {
        super(ch, cw, sh, sw, Fonts.plainA, gr);
    }

    @Override
    public void refresh() {
        repaint();
    }
    public JFrame getJFrame() {
        return mainFrame;
    }
    final public void lineBorder(int rowTop, int columnLeft, int countRow, int countColumn, int lineType, Color color) {
        setFontColor(color);
        lineBorder(rowTop, columnLeft, countRow, countColumn, lineType);
    }
	public void print(int row, int col, String s, Color bc) {
    	setBackColor(bc);
    	print(row, col, s);
	}
    public void addListener(EventsListener toAdd) {
        listeners.add(toAdd);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        int r = e.getY() / cellH; // row
        int c = e.getX() / cellW; // column

        for (EventsListener listener : listeners) {
            if (listener != null)
                listener.MouseClicked(c, r, e);
        }
    }
}
