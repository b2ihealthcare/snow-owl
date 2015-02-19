package org.protege.editor.owl.ui.framelist;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import org.protege.editor.core.ui.list.MListButton;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 */
public class AxiomAnnotationButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);

    private static final String ANNOTATE_STRING = "@";

    private boolean annotationPresent = false;


    public AxiomAnnotationButton(ActionListener actionListener) {
        super("Annotations", ROLL_OVER_COLOR, actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        final Font font = g.getFont();

        int w = getBounds().width;
        int h = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;

        g.setFont(font.deriveFont(Font.BOLD).deriveFont(8));
        final Rectangle stringBounds = g.getFontMetrics().getStringBounds(ANNOTATE_STRING, g).getBounds();
        g.drawString(ANNOTATE_STRING,
                     getBounds().x + w / 2 - stringBounds.width / 2,
                     getBounds().y + h / 2 + stringBounds.height / 2 - 3);

        if (annotationPresent){
            g.drawOval(x + 2, y + 2, w - 4, h - 4);
        }

        g.setFont(font);
    }


    public void setAnnotationPresent(boolean annotationPresent) {
        this.annotationPresent = annotationPresent;
    }
}
