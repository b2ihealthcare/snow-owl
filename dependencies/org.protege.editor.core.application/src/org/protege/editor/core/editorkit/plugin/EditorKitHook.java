package org.protege.editor.core.editorkit.plugin;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePluginInstance;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 *
 * A plugin that gets initialised when the editor kit has been created.
 * Can be used to customise the editor kit, workspace or model manager
 * without having to add a UI component.
 *
 */
public abstract class EditorKitHook implements ProtegePluginInstance {

    private EditorKit editorKit;

    protected void setup(EditorKit editorKit) {
        this.editorKit = editorKit;
    }

    protected EditorKit getEditorKit() {
        return editorKit;
    }
}
	