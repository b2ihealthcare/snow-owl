package org.protege.editor.owl.model.find;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityFinder {

    // exact matches

    OWLClass getOWLClass(String rendering);


    OWLObjectProperty getOWLObjectProperty(String rendering);


    OWLDataProperty getOWLDataProperty(String rendering);


    OWLAnnotationProperty getOWLAnnotationProperty(String rendering);


    OWLNamedIndividual getOWLIndividual(String rendering);


    OWLDatatype getOWLDatatype(String rendering);


    OWLEntity getOWLEntity(String rendering);


    Set<String> getOWLEntityRenderings();


    // pattern matches

    Set<OWLClass> getMatchingOWLClasses(String match);

    Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp);
    
    Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp, int flags);


    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match);

    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp);
    
    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp, int flags);


    Set<OWLDataProperty> getMatchingOWLDataProperties(String match);

    Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp);
    
    Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp, int flags);


    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match);

    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp);
    
    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp, int flags);


    Set<OWLDatatype> getMatchingOWLDatatypes(String match);

    Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp);
    
    Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp, int flags);


    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match);

    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp);
    
    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp, int flags);


    Set<OWLEntity> getMatchingOWLEntities(String match);

    Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp);
    
    Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp, int flags);


    // IRI

    Set<OWLEntity> getEntities(IRI iri);
}
