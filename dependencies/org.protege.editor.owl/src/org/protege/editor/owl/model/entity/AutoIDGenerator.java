package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 25, 2008<br><br>
 */
public interface AutoIDGenerator {
    
    String getNextID(Class<? extends OWLEntity> type) throws AutoIDException;
}
