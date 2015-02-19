package org.protege.editor.owl.ui.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.UIUtil;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 11, 2008<br><br>
 */
public class SourcePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(SourcePanel.class);

    private JEditorPane sourceConsole;

    public SourcePanel(final URL loc) {
        setLayout(new BorderLayout(7, 7));

        try {
            sourceConsole = new JEditorPane(loc);
            sourceConsole.setSelectionColor(Color.RED);
            final JButton saveButton = new JButton(new AbstractAction("Save as...") {
                public void actionPerformed(ActionEvent event) {
                    saveContent(loc);
                }
            });
            Box sourceHeader = new Box(BoxLayout.LINE_AXIS);
            sourceHeader.add(new JLabel("Source: "));
            final JLabel fileLabel = new JLabel(loc.toString());
            fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD));
            sourceHeader.add(fileLabel);
            sourceHeader.add(Box.createHorizontalGlue());
            sourceHeader.add(saveButton);
            add(sourceHeader, BorderLayout.NORTH);
            add(new JScrollPane(sourceConsole), BorderLayout.CENTER);
        }
        catch (Exception e) {
            logger.warn(e);
        }
    }

    public void highlight(final int line, int col){
        if (line >= 0){
            final int colCorrected = Math.max(col, 0);
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    int caretPos = 0;
                    final Document document = sourceConsole.getDocument();
                    if (document instanceof PlainDocument){
                        PlainDocument doc = (PlainDocument) document;
                        caretPos = doc.getDefaultRootElement().getElement(line).getStartOffset() + colCorrected;
                    }
                    sourceConsole.setCaretPosition(caretPos);
                    sourceConsole.setSelectionStart(caretPos);
                    sourceConsole.setSelectionEnd(caretPos+1);
                    sourceConsole.requestFocus();
                }
            });
        }
    }

    private void saveContent(URL url) {
        File source = confirmFile(url);
        if (source != null){
            try {
                FileWriter fw = new FileWriter(source);
                BufferedWriter writer = new BufferedWriter(fw, 1024);
                PlainDocument doc = (PlainDocument)sourceConsole.getDocument();
                String content = doc.getText(0, doc.getLength()-1);
                writer.write(content);
                writer.flush();
                fw.flush();
                fw.close();
                logger.info("Saved source to: " + source);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    private File confirmFile(URL url) {

        JDialog f = (JDialog) SwingUtilities.getAncestorOfClass(Window.class, this);
        FileDialog fileDialog = new FileDialog(f, "Save source", FileDialog.SAVE);

        try {
            final File file = new File(url.toURI());
            fileDialog.setDirectory(file.getPath());
            fileDialog.setFile(file.getName());
        }
        catch (URISyntaxException e) {
            fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
            fileDialog.setFile(url.getFile());
        }

        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        if (fileName != null) {
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }
}
