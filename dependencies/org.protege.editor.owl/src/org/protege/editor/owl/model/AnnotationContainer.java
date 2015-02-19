package org.protege.editor.owl.model;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 8, 2009<br><br>
 *
 * @TODO remove when added to the OWL API
 */
public interface AnnotationContainer {

    Set<OWLAnnotation> getAnnotations();

}
