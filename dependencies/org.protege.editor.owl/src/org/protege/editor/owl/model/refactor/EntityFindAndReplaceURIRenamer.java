package org.protege.editor.owl.model.refactor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 3, 2008<br><br>
 *
 * Cannot use an OWLEntityRenamer directly because multiple entities may be referenced by the same axiom
 */
public class EntityFindAndReplaceURIRenamer {

    private Logger logger = Logger.getLogger(EntityFindAndReplaceURIRenamer.class);


    private OWLOntologyManager mngr;

    private Collection<OWLEntity> entities;
    private Set<OWLOntology> ontologies;
    private String pattern;
    private String newText;

    private Map<OWLEntity, IRI> entity2IRIMap = new HashMap<OWLEntity, IRI>();

    private Map<OWLEntity, String> errorMap = new HashMap<OWLEntity, String>();

    public EntityFindAndReplaceURIRenamer(OWLOntologyManager mngr, Collection<OWLEntity> entities, Set<OWLOntology> ontologies, String pattern, String newText) {
        this.mngr = mngr;
        this.entities = entities;
        this.ontologies = ontologies;
        this.pattern = pattern;
        this.newText = newText;

        generateNameMap();
    }

    
        private void generateNameMap() {
        for (OWLEntity entity : entities){
            String newURIStr = entity.getIRI().toString().replaceAll("(?i)" + pattern, newText);
            try {
                URI newURI = new URI(newURIStr);
                if (!newURI.isAbsolute()){
                    throw new URISyntaxException(newURIStr, "IRI must be absolute");
                }
                entity2IRIMap.put(entity, IRI.create(newURI));
            }
            catch (URISyntaxException e) {
                errorMap.put(entity, newURIStr);
            }
        }
    }


    public boolean hasErrors(){
        return !errorMap.isEmpty();
    }


    public Map<OWLEntity, String> getErrors(){
        return Collections.unmodifiableMap(errorMap);
    }


    public List<OWLOntologyChange> getChanges(){
        OWLEntityRenamer renamer = new OWLEntityRenamer(mngr, ontologies);
        return renamer.changeIRI(entity2IRIMap);
    }
}
