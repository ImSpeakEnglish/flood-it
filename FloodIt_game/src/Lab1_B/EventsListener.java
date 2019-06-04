package Lab1_B;

import java.awt.event.MouseEvent;

public interface EventsListener {
    /**
     * Mouse clicked on cell at coordinates x*y.
     * @param e e.getButton() == 1 ? left button : right button;
     */
    void MouseClicked(int x, int y, MouseEvent e);
}
