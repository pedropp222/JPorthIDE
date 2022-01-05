package main.editor;


import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PCaret extends DefaultCaret
{
    public PCaret()
    {
        setBlinkRate(0);
    }

    protected synchronized void damage(Rectangle r) {
        if (r == null)
            return;
        // give values to x,y,width,height (inherited from java.awt.Rectangle)
        x = r.x;
        y = r.y + (r.height * 4 / 5 - 3);
        width = 10;
        height = 10;
        repaint(); // calls getComponent().repaint(x, y, width, height)
    }

    public void paint(Graphics g) {
        JTextComponent comp = getComponent();
        if (comp == null)
            return;

        int dot = getDot();
        Rectangle2D r = null;
        try {
            r = comp.modelToView2D(dot);
        } catch (BadLocationException e) {
            return;
        }
        if (r == null)
            return;

        int dist = (int) (r.getHeight() * 4 / 5 - 3); // will be distance from r.y to top



        if ((x != r.getX()) || (y != r.getY() + dist)) {
            // paint() has been called directly, without a previous call to
            // damage(), so do some cleanup. (This happens, for example, when
            // the
            // text component is resized.)
            repaint(); // erase previous location of caret
            x = (int) r.getX(); // set new values for x,y,width,height
            y = (int) (r.getY() + dist);
            width = 5;
            height = 5;
        }

        if (isVisible()) {
            g.setColor(comp.getCaretColor());

            Font f = comp.getFont();

            //g.drawRect(r.x - comp.getFontMetrics(f).charWidth('c') ,r.y - r.height,comp.getFontMetrics(f).charWidth('c'),comp.getFontMetrics(f).getHeight());

            g.drawOval((int)r.getX(), (int)r.getY() + dist - 5, width,10);

            //g.drawLine(r.x, r.y + dist, r.x, r.y + dist + 4); // 5 vertical
            // pixels
            //g.drawLine(r.x, r.y + dist + 4, r.x + 4, r.y + dist + 4); // 5 horiz
            // px
        }
    }
}