package org.protege.editor.owl.ui.ontology.wizard.move.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.core.ui.util.CheckList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public class SignatureDependentSelectionPreviewPanel extends MoveAxiomsKitConfigurationPanel {

    private SignatureSelection signatureSelection;

    private OWLObjectList previewList;

    private JLabel previewLabel;

    private RemovableObjectList<OWLEntity> signatureList;

    private CheckList list;

    private Timer previewTimer;


    public SignatureDependentSelectionPreviewPanel(SignatureSelection signatureSelection) {
        this.signatureSelection = signatureSelection;
        previewLabel = new JLabel("Axioms: ");
    }


    public void initialise() {
        setLayout(new BorderLayout(7, 7));


        JPanel previewPanel = new JPanel(new BorderLayout(3, 3));
        previewLabel = new JLabel("Axioms: Computing... ");
        previewPanel.add(previewLabel, BorderLayout.NORTH);
        previewList = new OWLObjectList(getEditorKit());
        previewPanel.add(new JScrollPane(previewList));
        previewPanel.setBorder(ComponentFactory.createTitledBorder("Preview"));

        add(previewPanel);
        OWLCellRenderer cellRenderer = new OWLCellRenderer(getEditorKit());
        cellRenderer.setWrap(false);
        cellRenderer.setHighlightKeywords(true);
        previewList.setCellRenderer(cellRenderer);


        signatureList = new RemovableObjectList<OWLEntity>();
        signatureList.setCellRenderer(cellRenderer);

        signatureList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                }
            }
        });
        signatureList.setPreferredSize(new Dimension(300, 300));
        JPanel signatureListPanel = new JPanel(new BorderLayout());
        list = new CheckList(signatureList);
        list.addCheckListListener(new CheckList.CheckListListener() {

            public void itemChecked(Object item) {
                updatePreview();
            }


            public void itemUnchecked(Object item) {
                updatePreview();
            }
        });
        signatureListPanel.add(new JScrollPane(list));
        signatureListPanel.setBorder(ComponentFactory.createTitledBorder("Signature"));
        add(signatureListPanel, BorderLayout.WEST);

        signatureList.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                updateSignature();
            }
        });

        previewTimer = new Timer(1200, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doPreviewUpdate();
            }
        });
        previewTimer.setRepeats(false);
    }


    public void updateSignature() {
        Set<OWLEntity> sig = signatureSelection.getSignature();
        Set<OWLEntity> newSig = new HashSet<OWLEntity>(getCheckedEntities());
        if (!sig.equals(newSig)) {
            signatureSelection.setSignature(newSig);
            updatePreview();
        }
    }


    public String getInstructions() {
        return "Confirm the signature for computing the module";
    }


    public void updatePreview() {
        // Slight delay
        previewTimer.restart();
    }


    private void doPreviewUpdate() {
        previewLabel.setText("Axioms: Computing... ");
        previewLabel.repaint();
        previewList.setListData(new Object[0]);
        final Set<OWLEntity> entities = getCheckedEntities();

        final Set<OWLOntology> sourceOntologies = getModel().getSourceOntologies();

        Runnable runnable = new Runnable() {
            public void run() {
                final Set<OWLAxiom> axioms = signatureSelection.getAxioms(sourceOntologies, entities);
                final java.util.List<OWLAxiom> axs = new ArrayList<OWLAxiom>(new TreeSet<OWLAxiom>(axioms));
                final int upperBound = 500 > axs.size() ? axs.size() : 500;
//                System.out.println("Updating for sig: " + entities);
                Runnable runnable = new Runnable() {
                    public void run() {
//                        System.out.println("Filling list");
                        previewLabel.setText("Axioms (showing " + upperBound + " out of " + axioms.size() + " in module)");
                    }
                };


                SwingUtilities.invokeLater(runnable);

//                System.out.println(axioms.size());

                Runnable runnable2 = new Runnable() {
                    public void run() {

                        previewList.setListData(axs.subList(0, upperBound).toArray());
//                        System.out.println("Done");
                    }
                };
                SwingUtilities.invokeLater(runnable2);
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }


    private Set<OWLEntity> getCheckedEntities() {
        final Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (Object o : list.getCheckedItems()) {
            RemovableObjectList<OWLEntity>.RemovableObjectListItem item = (RemovableObjectList.RemovableObjectListItem) o;
            entities.add(item.getObject());
        }
        return entities;
    }


    public void dispose() {
    }


    public String getID() {
        return "modularity.selectlocalitytype";
    }


    public String getTitle() {
        return "Module preview page";
    }


    public void update() {
        Set<OWLEntity> sig = signatureSelection.getSignature();
        signatureList.setListData(sig.toArray());
        updatePreview();
    }


    public void commit() {
        signatureSelection.setSignature(getCheckedEntities());
    }
}