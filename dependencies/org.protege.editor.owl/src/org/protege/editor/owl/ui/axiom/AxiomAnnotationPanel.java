package org.protege.editor.owl.ui.axiom;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 3, 2008<br><br>
 */
public class AxiomAnnotationPanel extends JComponent {

    private AxiomAnnotationsList annotationsComponent;

    private DefaultListModel model;


    public AxiomAnnotationPanel(OWLEditorKit eKit) {
        setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(500, 300));

        // we need to use the OWLCellRenderer, so create a singleton JList
        final OWLCellRenderer ren = new OWLCellRenderer(eKit);
        ren.setHighlightKeywords(true);

        model = new DefaultListModel();
        JList label = new JList(model);
        label.setBackground(getBackground());
        label.setEnabled(false);
        label.setOpaque(true);
        label.setCellRenderer(ren);

        annotationsComponent = new AxiomAnnotationsList(eKit);

        final JScrollPane scroller = new JScrollPane(annotationsComponent);

        add(label, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);

        setVisible(true);
    }


    public void setAxiomInstance(OWLAxiomInstance axiomInstance){
        model.clear();
        if (axiomInstance != null){
            model.addElement(axiomInstance.getAxiom());
            annotationsComponent.setRootObject(axiomInstance);
        }
        else{
            annotationsComponent.setRootObject(null);
        }
    }


    public OWLAxiomInstance getAxiom(){
        return annotationsComponent.getRoot();
    }


    public void dispose(){
        annotationsComponent.dispose();
    }
}
