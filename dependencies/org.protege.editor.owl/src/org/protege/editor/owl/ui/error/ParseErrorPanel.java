package org.protege.editor.owl.ui.error;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.error.ErrorExplainer;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 11, 2008<br><br>
 */
public class ParseErrorPanel<O extends Throwable> extends ErrorPanel<O>{

    private static final Logger logger = Logger.getLogger(ParseErrorPanel.class);

    private SourcePanel source;

    private JSplitPane splitter;


    public ParseErrorPanel(final ErrorExplainer.ErrorExplanation<O> oErrorExplanation, URI loc, SourcePanel sourcePanel) {
        super(oErrorExplanation, loc);
        if (sourcePanel != null){
            this.source = sourcePanel;

            removeComponentFromParent(getTabs());
            splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getTabs(), null);
            splitter.setBorder(new EmptyBorder(0, 0, 0, 0));
            add(splitter, BorderLayout.CENTER);

            addComponentListener(new ComponentAdapter(){
                public void componentShown(ComponentEvent event) {
                    if (oErrorExplanation instanceof ErrorExplainer.ParseErrorExplanation){
                        ErrorExplainer.ParseErrorExplanation expl = (ErrorExplainer.ParseErrorExplanation) oErrorExplanation;
                        source.highlight(expl.getLine(), expl.getColumn());
                    }

                    removeComponentFromParent(source);
                    splitter.setBottomComponent(source);
                    splitter.setDividerLocation(0.5);
                    splitter.repaint();
                }
            });
        }
    }


    protected static void removeComponentFromParent(JComponent component) {
        if (component.getParent() != null){
            component.getParent().remove(component);
        }
    }

}

