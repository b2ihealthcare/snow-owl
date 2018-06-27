package com.b2international.snowowl.snomed.reasoner.ontology;

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @since 7.0
 */
public final class OntologyBuilder {

	private static final String CONCEPT_MODEL_OBJECT_ATTRIBUTE = "762705008";
	private static final String CONCEPT_MODEL_DATA_ATTRIBUTE = "762706009";
	
	private static final String ROLE_GROUP_ID = "609096000";
	private static final Set<String> NEVER_GROUPED_TYPE_IDS = ImmutableSet.of(Concepts.PART_OF, 
			Concepts.LATERALITY, 
			Concepts.HAS_DOSE_FORM, 
			Concepts.HAS_ACTIVE_INGREDIENT);

	// The prefix used for SNOMED CT identifiers (also the default)
	private static final String PREFIX_SCT = "sct:";
	private static final String NAMESPACE_SCT = "http://snomed.info/id/";
	
	// The prefix used for SNOMED CT module ontology identifiers
	private static final String PREFIX_SCTM = "sctm:";
	private static final String NAMESPACE_SCTM = "http://snomed.info/sct/";
	
	private final OWLOntology ontology;
	private final DefaultPrefixManager prefixManager;
	
	/**
	 * Creates a {@link DefaultPrefixManager} instance for the specified ontology.
	 * Prefixes will be configured in accordance with the OWL refset specification (draft)
	 * 
	 * @return the created prefix manager
	 */
	private static DefaultPrefixManager createPrefixManager() {
		final DefaultPrefixManager prefixManager = new DefaultPrefixManager();
		prefixManager.setDefaultPrefix(NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCT, NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCTM, NAMESPACE_SCTM);
		return prefixManager;
	}
	
	public OntologyBuilder(String ontologyModuleId, String ontologyVersionId) throws OWLOntologyCreationException {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		
		Optional<IRI> ontologyIRI = Optional.of(IRI.create(
				String.format("%s%s", NAMESPACE_SCTM, ontologyModuleId)));
		Optional<IRI> versionIRI = Optional.fromNullable(ontologyVersionId)
				.transform(v -> String.format("%s%s/version/%s", NAMESPACE_SCTM, ontologyModuleId, v))
				.transform(IRI::create);
				
		OWLOntologyID ontologyID = new OWLOntologyID(ontologyIRI, versionIRI);
		
		this.ontology = ontologyManager.createOntology(ontologyID);
		this.prefixManager = createPrefixManager();
	}
	
	public void build(BranchContext context, IProgressMonitor monitor) {
		addOWLClassAxioms(context, CONCEPT_MODEL_OBJECT_ATTRIBUTE, CONCEPT_MODEL_DATA_ATTRIBUTE);
		addOWLObjectPropertyAxioms(context, CONCEPT_MODEL_OBJECT_ATTRIBUTE);
		addOWLObjectPropertyAxioms(context, CONCEPT_MODEL_DATA_ATTRIBUTE);
	}

	private void addOWLClassAxioms(BranchContext context, String... propertyRootIds) {
		SnomedConceptSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.setExpand(String.format("relationships(active:true,characteristicType:\"%s\")", Concepts.STATED_RELATIONSHIP))
			.setScroll("5m")
			.setLimit(10_000);
		
		SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> searchIterator = new SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts>(
				requestBuilder, b -> b.build().execute(context));
		
		searchIterator.forEachRemaining(chunk -> {
			for (SnomedConcept concept : chunk) {
				if (isProperty(concept, propertyRootIds)) {
					continue;
				}
				
				Set<OWLClassExpression> intersection = Sets.newHashSet();
				
				concept.getRelationships()
					.stream()
					.filter(r -> isParentRelationship(r))
					.map(SnomedRelationship::getDestinationId)
					.forEachOrdered(parentId -> {
						OWLClass parentClass = getOWLClass(parentId);
						intersection.add(parentClass);
					});
			
				concept.getRelationships()
					.stream()
					.filter(r -> isNeverGroupedRelationship(r))
					.forEachOrdered(r -> {
						OWLClassExpression relationshipExpression = getRelationshipExpression(r.getTypeId(), r.getDestinationId());
						intersection.add(relationshipExpression);
					});
			
				concept.getRelationships()
					.stream()
					.filter(r -> isGroupedRelationship(r) && r.getGroup() == 0)
					.forEachOrdered(r -> {
						OWLClassExpression relationshipExpression = getRelationshipExpression(r.getTypeId(), r.getDestinationId());
						OWLClassExpression singleGroupExpression = getRoleGroupExpression(relationshipExpression);
						intersection.add(singleGroupExpression);
					});
				
				concept.getRelationships()
					.stream()
					.filter(r -> isGroupedRelationship(r) && r.getGroup() > 0)
					.collect(Collectors.groupingBy(SnomedRelationship::getGroup))
					.entrySet()
					.stream()
					.forEachOrdered(e -> {
						int group = e.getKey();
						Set<OWLClassExpression> groupIntersection = e.getValue()
							.stream()
							.map(gr -> getRelationshipExpression(gr.getTypeId(), gr.getDestinationId()))
							.collect(Collectors.toSet());
						
						OWLClassExpression groupExpression = getOWLObjectIntersectionOf(groupIntersection);
						intersection.add(groupExpression);
					});

				OWLClass conceptClass = getOWLClass(concept.getId());
				addAxiom(getOWLDeclarationAxiom(conceptClass));
				
				if (concept.getDefinitionStatus().isPrimitive()) {
					addAxiom(getOWLSubClassOfAxiom(conceptClass, getOWLObjectIntersectionOf(intersection)));
				} else {
					addAxiom(getOWLEquivalentClassesAxiom(conceptClass, getOWLObjectIntersectionOf(intersection)));
				}
			}
		});
	}

	private boolean isParentRelationship(SnomedRelationship r) {
		return Concepts.IS_A.equals(r.getTypeId());
	}

	private boolean isNeverGroupedRelationship(SnomedRelationship r) {
		return NEVER_GROUPED_TYPE_IDS.contains(r.getTypeId()) && r.getGroup() == 0;
	}

	private boolean isGroupedRelationship(SnomedRelationship r) {
		return !Concepts.IS_A.equals(r.getTypeId()) && !NEVER_GROUPED_TYPE_IDS.contains(r.getTypeId());
	}

	private boolean isProperty(SnomedConcept concept, String... propertyRootIds) {
		LongSet ancestorSet = PrimitiveSets.newLongOpenHashSet(concept.getStatedAncestorIds());
		LongSet parentSet = PrimitiveSets.newLongOpenHashSet(concept.getStatedParentIds());
		
		for (String propertyRootId : propertyRootIds) {
			if (concept.getId().equals(propertyRootId)) { return true; }
			
			long longRootId = Long.parseLong(propertyRootId);
			if (parentSet.contains(longRootId)) { return true; }
			if (ancestorSet.contains(longRootId)) { return true; }
		}

		return false;
	}

	private void addOWLObjectPropertyAxioms(BranchContext context, String propertyAncestorId) {
		SnomedConcepts propertyConcepts = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByStatedAncestor(propertyAncestorId)
			.build()
			.execute(context);
		
		propertyConcepts.forEach(child -> {
			OWLObjectProperty childProperty = getOWLObjectProperty(child.getId());
			addAxiom(getOWLDeclarationAxiom(childProperty));
			
			long[] parentIds = child.getStatedParentIds();
			for (long parentId : parentIds) {
				OWLObjectProperty parentProperty = getOWLObjectProperty(Long.toString(parentId));
				addAxiom(getOWLDeclarationAxiom(parentProperty));
				addAxiom(getOWLSubObjectPropertyOfAxiom(childProperty, parentProperty));
			}
		});
	}
	
	private OWLClass getOWLClass(String conceptId) {
		return getDataFactory().getOWLClass(":" + conceptId, prefixManager);
	}
	
	private OWLObjectProperty getOWLObjectProperty(String typeId) {
		return getDataFactory().getOWLObjectProperty(":" + typeId, prefixManager);
	}
	
	private OWLObjectSomeValuesFrom getRelationshipExpression(String typeId, String destinationId) {
		OWLObjectProperty property = getOWLObjectProperty(typeId);
		OWLClass filler = getOWLClass(destinationId);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}
	
	private OWLObjectSomeValuesFrom getRoleGroupExpression(OWLClassExpression filler) {
		OWLObjectProperty property = getOWLObjectProperty(ROLE_GROUP_ID);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}
	
	private OWLDeclarationAxiom getOWLDeclarationAxiom(OWLEntity e) {
		return getDataFactory().getOWLDeclarationAxiom(e);
	}
	
	private OWLSubClassOfAxiom getOWLSubClassOfAxiom(OWLClassExpression child, OWLClassExpression parent) {
		return getDataFactory().getOWLSubClassOfAxiom(child, parent);
	}
	
	private OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(OWLClassExpression... expressions) {
		return getDataFactory().getOWLEquivalentClassesAxiom(expressions);
	}
	
	private OWLSubObjectPropertyOfAxiom getOWLSubObjectPropertyOfAxiom(OWLObjectPropertyExpression child, OWLObjectPropertyExpression parent) {
		return getDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
	}
	
	private OWLClassExpression getOWLObjectIntersectionOf(Set<OWLClassExpression> conjuncts) {
		if (conjuncts.size() > 1) {
			return getDataFactory().getOWLObjectIntersectionOf(conjuncts);
		} else {
			return Iterables.getOnlyElement(conjuncts);
		}
	}
	
	private void addAxiom(OWLAxiom axiom) {
		getOntologyManager().addAxiom(ontology, axiom);
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}

	public OWLOntologyManager getOntologyManager() {
		return getOntology().getOWLOntologyManager();
	}
	
	public OWLDataFactory getDataFactory() {
		return getOntologyManager().getOWLDataFactory();
	}
}
