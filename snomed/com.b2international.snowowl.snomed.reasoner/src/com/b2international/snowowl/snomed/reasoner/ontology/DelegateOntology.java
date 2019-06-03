/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.reasoner.ontology;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.net4j.util.StringUtil;
import org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdMap;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdMultimap;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * @since
 */
public final class DelegateOntology extends DelegateOntologyStub implements OWLMutableOntology {

	// The prefix used for SNOMED CT identifiers (also the default namespace for ":" prefixes)
	private static final String PREFIX_SCT = ":";
	private static final String PREFIX_SCT_LONG = "sct:";
	private static final String NAMESPACE_SCT = "http://snomed.info/id/";

	// The prefix used for SNOMED CT module ontology identifiers
	private static final String PREFIX_SCTM = "sctm:";
	public static final String NAMESPACE_SCTM = "http://snomed.info/sct/";

	// The prefix used for on-the-fly generated OWL classes for CD member values
	private static final String PREFIX_SO = "so:";
	private static final String NAMESPACE_SO = "http://b2international.com/so/";
	
	// Post 2018-01 INT datasets use different concepts for object and data attribute roots; only one out of the two set of concepts can be used
	private static final long POST_2018_OBJECT_ATTRIBUTE = Long.parseLong(Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE);
	private static final long POST_2018_DATA_ATTRIBUTE = Long.parseLong(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE);
	
	private static final long PRE_2018_OBJECT_ATTRIBUTE = Long.parseLong(Concepts.CONCEPT_MODEL_ATTRIBUTE);
	private static final long PRE_2018_DATA_ATTRIBUTE = Long.parseLong(Concepts.SG_CONCRETE_DOMAIN_ATTRIBUTE);

	private static final long ROLE_GROUP = Long.parseLong(Concepts.ROLE_GROUP);

	private static final long PART_OF = 123005000L;
	private static final long LATERALITY = 272741003L;
	private static final long HAS_DOSE_FORM = 411116001L;
	private static final long HAS_ACTIVE_INGREDIENT = 127489000L;

	// Post 2018-01 INT datasets provide never grouped type IDs as part of the MRCM attribute reference set
	private static final LongSet PRE_2018_NEVER_GROUPED_TYPE_IDS = PrimitiveSets.newLongOpenHashSet(
			PART_OF, 
			LATERALITY, 
			HAS_DOSE_FORM, 
			HAS_ACTIVE_INGREDIENT);

	private static final Joiner NEWLINE_JOINER = Joiner.on('\n');
	
	private static final String PARSED_ONTOLOGY_START = NEWLINE_JOINER.join(
			"Prefix(:=<http://snomed.info/id/>)",
			"Prefix(sct:=<http://snomed.info/id/>)",
			"Prefix(sctm:=<http://snomed.info/sct/>)",
			"Prefix(so:=<http://b2international.com/so/>)",
			"Ontology(");
	
	private static final String PARSED_ONTOLOGY_END = ")";
	
	private static final Logger LOGGER = LoggerFactory.getLogger("ontology");
	
	private final class EntityDeclarationAxiomIterator extends AbstractIterator<OWLDeclarationAxiom> {
		private final LongIterator idIterator;
		private final LongFunction<OWLEntity> entityFactory;

		public EntityDeclarationAxiomIterator(final LongIterator idIterator, final LongFunction<OWLEntity> entityFactory) {
			this.idIterator = idIterator;
			this.entityFactory = entityFactory;
		}

		@Override
		protected OWLDeclarationAxiom computeNext() {
			if (!idIterator.hasNext()) {
				return endOfData();
			}

			final long conceptId = idIterator.next();
			final OWLEntity entity = entityFactory.apply(conceptId);
			return getOWLDeclarationAxiom(entity);
		}
	}

	private final class ConceptAxiomIterator<T extends OWLClassAxiom> extends AbstractIterator<T> {
		private final LongIterator idIterator;
		private final LongPredicate idPredicate;
		private final BiFunction<OWLClassExpression, OWLClassExpression, T> classAxiomFactory;
		private final InternalIdMultimap<StatementFragment> statements;

		public ConceptAxiomIterator(final LongIterator idIterator,
				final LongPredicate idPredicate, 
				final BiFunction<OWLClassExpression, OWLClassExpression, T> classAxiomFactory,
				final InternalIdMultimap<StatementFragment> statements) {

			this.idIterator = idIterator;
			this.idPredicate = idPredicate;
			this.classAxiomFactory = classAxiomFactory;
			this.statements = statements;
		}

		@Override
		protected T computeNext() {
			final long conceptId = nextApplicableConceptId();
			if (conceptId == -1L) {
				return endOfData();
			}

			final OWLClass conceptClass = getConceptClass(conceptId);
			final Set<OWLClassExpression> conceptDefinitionExpression = getConceptDefinitionExpression(conceptId, statements);
			
			if (!conceptDefinitionExpression.isEmpty()) {
				return classAxiomFactory.apply(conceptClass, getOWLObjectIntersectionOf(conceptDefinitionExpression));
			} else {
				// TODO: Filter concepts that don't produce a superclass expression in advance (SNOMED CT Root is a known one)
				return classAxiomFactory.apply(conceptClass, getOWLThing());
			}
		}

		private long nextApplicableConceptId() {
			while (idIterator.hasNext()) {
				final long conceptId = idIterator.next();
				if (idPredicate.test(conceptId)) {
					return conceptId;
				}
			}

			return -1L;
		}
	}

	private final class SubPropertyAxiomIterator<R extends OWLPropertyRange, P extends OWLPropertyExpression, A extends OWLSubPropertyAxiom<P>> extends AbstractIterator<A> {
		private final long attributeRootId;
		private final LongIterator childIterator;
		private final LongFunction<P> propertyFactory;
		private final BiFunction<P, P, A> subPropertyAxiomFactory;

		private long childId = -1L;
		private LongIterator parentIterator;

		public SubPropertyAxiomIterator(final long attributeRootId,
				final LongIterator childIterator,
				final LongFunction<P> propertyFactory,
				final BiFunction<P, P, A> subPropertyAxiomFactory) {

			this.attributeRootId = attributeRootId;
			this.childIterator = childIterator;
			this.propertyFactory = propertyFactory;
			this.subPropertyAxiomFactory = subPropertyAxiomFactory;
		}

		@Override
		protected A computeNext() {
			final long parentId = nextApplicableParentId(); 
			if (parentId == -1L) {
				return endOfData();
			}

			final P childProperty = propertyFactory.apply(childId);
			final P parentProperty = propertyFactory.apply(parentId);
			return subPropertyAxiomFactory.apply(childProperty, parentProperty);	
		}

		private long nextApplicableParentId() {
			// Check if there are more parents to return for the current value of "childId"
			if (parentIterator != null) {
				while (parentIterator.hasNext()) {
					final long parentId = parentIterator.next();
					if (parentId != attributeRootId) {
						return parentId;
					}
				}
			}

			// Otherwise, look for the next child which has parents
			while (childIterator.hasNext()) {
				childId = childIterator.next();
				parentIterator = taxonomy.getStatedAncestors()
						.getDestinations(childId, true)
						.iterator();

				while (parentIterator.hasNext()) {
					final long parentId = parentIterator.next();
					if (parentId != attributeRootId) {
						return parentId;
					}
				}
			}

			return -1L;
		}
	}

	private final class DisjointUnionAxiomIterator extends AbstractIterator<OWLDisjointUnionAxiom> {
		private final LongIterator idIterator;

		public DisjointUnionAxiomIterator(final LongIterator idIterator) {
			this.idIterator = idIterator;
		}

		@Override
		protected OWLDisjointUnionAxiom computeNext() {
			if (!idIterator.hasNext()) {
				return endOfData();
			}

			final long parentId = idIterator.next();
			final OWLClass parentClass = getConceptClass(parentId);
			final LongSet directChildIds = taxonomy.getStatedDescendants()
					.getDestinations(parentId, true);

			final Set<OWLClass> childClasses = newHashSet();

			for (final LongIterator itr = directChildIds.iterator(); itr.hasNext(); /*empty*/) {
				final long childId = itr.next();
				final OWLClass childClass = getConceptClass(childId);
				childClasses.add(childClass);
			}

			return getOWLDisjointUnionAxiom(parentClass, childClasses);
		}
	}

	private final class FunctionalSyntaxAxiomIterator extends AbstractIterator<OWLLogicalAxiom> {
		
		private final Iterator<String> axiomIterator;
		private final OWLOntologyLoaderConfiguration configuration;
		private final SingleAxiomOwlOntology singleAxiomOntology;
		
		public FunctionalSyntaxAxiomIterator(final Stream<String> axiomStream) {
			this.axiomIterator = axiomStream.iterator();
			this.configuration = new OWLOntologyLoaderConfiguration();
			this.singleAxiomOntology = new SingleAxiomOwlOntology(getOWLOntologyManager());
		}

		@Override
		protected OWLLogicalAxiom computeNext() {
			if (!axiomIterator.hasNext()) {
				return endOfData();
			}
			
			final String axiomString = axiomIterator.next();
			
			try {
				
				final OWLOntologyDocumentSource singleAxiomOntologySource = new StringDocumentSource(PARSED_ONTOLOGY_START + axiomString + PARSED_ONTOLOGY_END);
				new OWLFunctionalSyntaxOWLParser().parse(singleAxiomOntologySource, singleAxiomOntology, configuration);
				final Set<OWLAxiom> logicalAxioms = singleAxiomOntology.getAxioms();
				return (OWLLogicalAxiom) logicalAxioms.iterator().next();
				
			} catch (final NoSuchElementException e) {
				LOGGER.warn("Encountered non-logical OWL axiom '{}'", axiomString, e);
				return getOWLSubClassOfAxiom(getOWLNothing(), getOWLThing()); // No-op axiom, just to match the expected axiom count
			} catch (final IOException e) {
				LOGGER.warn("Couldn't parse OWL axiom '{}'", axiomString, e);
				return getOWLSubClassOfAxiom(getOWLNothing(), getOWLThing()); // No-op axiom, just to match the expected axiom count
			}
		}
	}

	/**
	 * Creates a {@link DefaultPrefixManager} instance for the specified ontology.
	 * Prefixes will be configured in accordance with the OWL reference set
	 * specification, with the exception of the default prefix.
	 * 
	 * @see <a href="https://docs.google.com/document/d/1jn9jiWe2cKNA5WrbQZ2MGWV_VoKU6Una6ZpQLskeRXA/">OWL
	 *      Reference Sets Specification (draft)</a>
	 * @return the created prefix manager
	 */
	private static DefaultPrefixManager createPrefixManager() {
		final DefaultPrefixManager prefixManager = new DefaultPrefixManager();
		prefixManager.setDefaultPrefix(NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCT_LONG, NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCTM, NAMESPACE_SCTM);
		prefixManager.setPrefix(PREFIX_SO, NAMESPACE_SO);
		return prefixManager;
	}

	private final OWLOntologyManager manager;
	private final OWLOntologyID ontologyID;
	private final ReasonerTaxonomy taxonomy;

	private final DefaultPrefixManager prefixManager;
	
	private final long objectAttributeId;
	private final long dataAttributeId;
	private final LongSet neverGroupedIds;

	public DelegateOntology(final OWLOntologyManager manager, 
			final OWLOntologyID ontologyID, 
			final ReasonerTaxonomy taxonomy) {

		this.manager = manager;
		this.ontologyID = ontologyID;
		this.taxonomy = taxonomy;

		this.prefixManager = createPrefixManager();
		
		if (taxonomy.getConceptMap().getInternalId(POST_2018_OBJECT_ATTRIBUTE) != -1) {
			objectAttributeId = POST_2018_OBJECT_ATTRIBUTE;
		} else {
			objectAttributeId = PRE_2018_OBJECT_ATTRIBUTE;
		}
		
		if (taxonomy.getConceptMap().getInternalId(POST_2018_DATA_ATTRIBUTE) != -1) {
			dataAttributeId = POST_2018_DATA_ATTRIBUTE;
		} else {
			dataAttributeId = PRE_2018_DATA_ATTRIBUTE;
		}
		
		if (!taxonomy.getNeverGroupedTypeIds().isEmpty()) {
			this.neverGroupedIds = taxonomy.getNeverGroupedTypeIds();
		} else {
			this.neverGroupedIds = PRE_2018_NEVER_GROUPED_TYPE_IDS;
		}
	}
	
	@Override
	public ChangeApplied addAxiom(OWLAxiom axiom) {
		return ChangeApplied.UNSUCCESSFULLY;
	}
	
	@Override
	public ChangeApplied addAxioms(Set<? extends OWLAxiom> axioms) {
		return ChangeApplied.UNSUCCESSFULLY;
	}
	
	@Override
	public ChangeApplied applyChange(OWLOntologyChange change) {
		return change instanceof SetOntologyID ? ChangeApplied.SUCCESSFULLY : ChangeApplied.UNSUCCESSFULLY;
	}
	
	@Override
	public ChangeApplied applyChanges(List<? extends OWLOntologyChange> changes) {
		return changes.stream().map(this::applyChange).reduce(ChangeApplied.SUCCESSFULLY, (result, next) -> {
			return next == ChangeApplied.UNSUCCESSFULLY ? next : result;
		});
	}
	
	@Override
	public ChangeDetails applyChangesAndGetDetails(List<? extends OWLOntologyChange> changes) {
		return new ChangeDetails(applyChanges(changes), changes);
	}
	
	@Override
	public final Set<OWLImportsDeclaration> getImportsDeclarations() {
		return Collections.emptySet();
	}
	
	@Override
	public final Set<OWLAnnotation> getAnnotations() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<OWLEntity> getSignature() {
		Set<OWLEntity> toReturn = newHashSet();
		OWLEntityCollector collector = new OWLEntityCollector(toReturn);
		getLogicalAxioms().forEach(ax -> ax.accept(collector));
		return toReturn;
	}
	
	@Override
	public final Set<IRI> getPunnedIRIs(Imports includeImportsClosure) {
		return Collections.emptySet();
	}
	
	@Override
	public final Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject entity) {
		return Collections.emptySet();
	}
	
	@Override
	protected int index() {
		return OWLObjectTypeIndexProvider.ONTOLOGY;
	}

	@Override
	protected int compareObjectOfSameType(final OWLObject object) {
		if (object == this) {
			return 0;
		}
		final OWLOntology other = (OWLOntology) object;
		return ontologyID.compareTo(other.getOntologyID());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("DelegateOntology(");
		sb.append(ontologyID);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}

	@Override
	public OWLOntologyID getOntologyID() {
		return ontologyID;
	}

	@Override
	public boolean isAnonymous() {
		return ontologyID.isAnonymous();
	}

	@Override
	public Set<OWLOntology> getImportsClosure() {
		return newHashSet(this);
	}

	@Override
	public final void accept(final OWLNamedObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public final <O> O accept(final OWLNamedObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public final void accept(final OWLObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public final <O> O accept(final OWLObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		return new AbstractSet<OWLLogicalAxiom>() {
			@Override
			@SuppressWarnings("unchecked")
			public Iterator<OWLLogicalAxiom> iterator() {
				return Iterators.concat(
						owlReferenceSetAxioms(),
						conceptSubClassOfAxioms(),
						conceptEquivalentClassesAxioms(),
						objectAttributeSubPropertyOfAxioms(),
						dataAttributeSubPropertyOfAxioms(),
						disjointUnionAxioms());
			}

			@Override
			public int size() {
				return getLogicalAxiomCount();
			}
		};
	}
	
	@Override
	public int getLogicalAxiomCount() {
		return owlReferenceSetAdditionalAxiomCount()	// owlReferenceSetAxioms()
				+ conceptCount()			// conceptSubClassOfAxioms() + conceptEquivalentClassesAxioms()
				+ objectHierarchyCount()	// objectAttributeSubPropertyOfAxioms()
				+ dataHierarchyCount()		// dataAttributeSubPropertyOfAxioms()
				+ disjointUnionCount();		// disjointUnionAxioms()
	}
	
	@Override
	public Set<OWLAxiom> getAxioms() {
		return new AbstractSet<OWLAxiom>() {
			@Override
			@SuppressWarnings("unchecked")
			public Iterator<OWLAxiom> iterator() {
				return Iterators.concat(
//						conceptDeclarationAxioms(),
//						objectAttributeDeclarationAxioms(),
//						dataAttributeDeclarationAxioms(),
						owlReferenceSetAxioms(),
						conceptSubClassOfAxioms(),
						conceptEquivalentClassesAxioms(),
						objectAttributeSubPropertyOfAxioms(),
						dataAttributeSubPropertyOfAxioms(),
						disjointUnionAxioms());
			}

			@Override
			public int size() {
				return getAxiomCount();
			}
		};
	}
	
	@Override
	public int getAxiomCount() {
		return 1 * conceptCount()				// conceptDeclarationAxioms() + conceptSubClassOfAxioms() + conceptEquivalentClassesAxioms()
//				+ objectAttributeCount()		// objectAttributeDeclarationAxioms()
				+ objectHierarchyCount()		// objectAttributeSubPropertyOfAxioms()
//				+ dataAttributeCount()			// dataAttributeDeclarationAxioms()
				+ dataHierarchyCount()			// dataAttributeSubPropertyOfAxioms()
				+ disjointUnionCount()			// disjointUnionAxioms()
				+ owlReferenceSetAdditionalAxiomCount();	// owlReferenceSetAxioms()
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
		if (AxiomType.DECLARATION.equals(axiomType)) {
			// Minimal implementation for FaCT++ which is only interested in declaration axioms
			return Collections.emptySet();
		} else {
			return super.getAxioms(axiomType);
		}
	}
	
	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
		if (AxiomType.DECLARATION.equals(axiomType)) {
			// Minimal implementation for FaCT++ which is only interested in declaration axioms
			return 0;
		} else {
			return super.getAxiomCount(axiomType);
		}
	}

	////////////////////////
	// Declaration axioms
	////////////////////////
	
	private Iterator<OWLDeclarationAxiom> conceptDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(conceptIdIterator(), this::getConceptClass);
	}

	private Iterator<OWLDeclarationAxiom> objectAttributeDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(objectAttributeIdIterator(), this::getConceptObjectProperty);
	}

//	private Iterator<OWLDeclarationAxiom> dataAttributeDeclarationAxioms() {
//		return new EntityDeclarationAxiomIterator(dataAttributeIdIterator(), this::getConceptDataProperty);
//	}
	
	private Iterator<OWLDeclarationAxiom> dataAttributeDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(dataAttributeIdIterator(), this::getConceptObjectProperty);
	}

	///////////////////////////////
	// Concept definition axioms
	///////////////////////////////
	
	private Iterator<OWLSubClassOfAxiom> conceptSubClassOfAxioms() {
		final LongPredicate primitiveConcept = conceptId -> {
			final boolean primitive = !taxonomy.getDefinedConceptMap().containsKey(conceptId);
			final boolean hasStatedIsa = !taxonomy.getStatedAncestors().getDestinations(conceptId, true).isEmpty();
			final boolean hasStatedNonIsa = !taxonomy.getSubclassOfStatements().get(conceptId).isEmpty();
			final boolean noEqStatements = taxonomy.getEquivalentStatements().get(conceptId).isEmpty();
			return (hasStatedIsa && noEqStatements && primitive) || hasStatedNonIsa;
		}; 
		return new ConceptAxiomIterator<>(conceptIdIterator(), primitiveConcept, this::getOWLSubClassOfAxiom, taxonomy.getSubclassOfStatements());
	}

	private Iterator<OWLEquivalentClassesAxiom> conceptEquivalentClassesAxioms() {
		final LongPredicate fullyDefinedConcept = conceptId -> {
			final boolean defined = taxonomy.getDefinedConceptMap().containsKey(conceptId);
			final boolean hasStatedIsa = !taxonomy.getStatedAncestors().getDestinations(conceptId, true).isEmpty();
			final boolean hasStatedNonIsa = !taxonomy.getEquivalentStatements().get(conceptId).isEmpty();
			final boolean noSubClassOfStatements = taxonomy.getSubclassOfStatements().get(conceptId).isEmpty();
			return (hasStatedIsa && noSubClassOfStatements && defined) || hasStatedNonIsa;
		};
		return new ConceptAxiomIterator<>(conceptIdIterator(), fullyDefinedConcept, this::getOWLEquivalentClassesAxiom, taxonomy.getEquivalentStatements());
	}

	////////////////////////////////////////////
	// "Sub <object|data> property of" axioms
	////////////////////////////////////////////
	
	private Iterator<OWLSubObjectPropertyOfAxiom> objectAttributeSubPropertyOfAxioms() {
		return new SubPropertyAxiomIterator<>(objectAttributeId, 
				objectAttributeIdIterator(), 
				this::getConceptObjectProperty, 
				this::getOWLSubObjectPropertyOfAxiom);
	}

//	private Iterator<OWLSubDataPropertyOfAxiom> dataAttributeSubPropertyOfAxioms() {
//		return new SubPropertyAxiomIterator<>(dataAttributeId, 
//				dataAttributeIdIterator(), 
//				this::getConceptDataProperty, 
//				this::getOWLSubDataPropertyOfAxiom);
//	}
	
	private Iterator<OWLSubObjectPropertyOfAxiom> dataAttributeSubPropertyOfAxioms() {
		return new SubPropertyAxiomIterator<>(dataAttributeId, 
				dataAttributeIdIterator(), 
				this::getConceptObjectProperty, 
				this::getOWLSubObjectPropertyOfAxiom);
	}

	///////////////////////////////////////////////////
	// Disjoint union axioms for exhaustive concepts
	///////////////////////////////////////////////////
	
	private Iterator<OWLDisjointUnionAxiom> disjointUnionAxioms() {
		return new DisjointUnionAxiomIterator(exhaustiveIdIterator());
	}

	///////////////////////////////////////////////////////////
	// Anything else declared as an OWL reference set member
	///////////////////////////////////////////////////////////
	
	private Iterator<OWLLogicalAxiom> owlReferenceSetAxioms() {
		return new FunctionalSyntaxAxiomIterator(taxonomy.getStatedAdditionalAxioms().valueStream());
	}
	
	private LongIterator conceptIdIterator() {
		return taxonomy.getConceptMap().getSctIds();
	}

	private LongIterator objectAttributeIdIterator() {
		return getAllSubTypes(objectAttributeId).iterator();
	}

	private LongIterator dataAttributeIdIterator() {
		return getAllSubTypes(dataAttributeId).iterator();
	}

	private LongIterator exhaustiveIdIterator() {
		return taxonomy.getExhaustiveConcepts().iterator();
	}

	private int conceptCount() {
		return taxonomy.getConceptMap().size();
	}

	private int objectAttributeCount() {
		return getAllSubTypesCount(objectAttributeId);
	}

	private int objectHierarchyCount() {
		return hierarchyCount(objectAttributeId);
	}

	private int dataAttributeCount() {
		return getAllSubTypesCount(dataAttributeId);
	}

	private int dataHierarchyCount() {
		return hierarchyCount(dataAttributeId);
	}

	private int disjointUnionCount() {
		return taxonomy.getExhaustiveConcepts().size();
	}

	private int owlReferenceSetAdditionalAxiomCount() {
		return (int) taxonomy.getStatedAdditionalAxioms().valueStream().count();
	}

	private int getAllSubTypesCount(final long ancestorId) {
		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			return taxonomy.getStatedDescendants()
					.getDestinations(ancestorId, false)
					.size();
		} else {
			return 0;
		}
	}

	private LongSet getAllSubTypes(final long ancestorId) {
		final LongSet allSubTypes = PrimitiveSets.newLongOpenHashSet();

		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			allSubTypes.addAll(taxonomy.getStatedDescendants()
					.getDestinations(ancestorId, false));
		}

		return allSubTypes;
	}
	
	private LongSet getSubTypes(final long parentId) {
		final LongSet subTypes = PrimitiveSets.newLongOpenHashSet();
		
		if (taxonomy.getConceptMap().getInternalId(parentId) != InternalIdMap.NO_INTERNAL_ID) {
			subTypes.addAll(taxonomy.getStatedDescendants()
					.getDestinations(parentId, true));
		}
		
		return subTypes;
	}

	private int hierarchyCount(final long ancestorId) {
		int parentsCount = 0;

		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			// Get the direct children of the attribute root concept
			for (final LongIterator directItr = getSubTypes(ancestorId).iterator(); directItr.hasNext(); /* empty */) {
				// Descendants of each direct child will contribute as many "SubPropertyOf" axioms as they have stated (direct) parents
				for (final LongIterator descendantItr = getAllSubTypes(directItr.next()).iterator(); descendantItr.hasNext(); /* empty */) {
					final int parents = taxonomy.getStatedAncestors()
							.getDestinations(descendantItr.next(), true)
							.size();

					parentsCount += parents;
				}
			}
		}

		return parentsCount;
	}

	public long getConceptId(final OWLClass conceptClass) {
		final String iri = conceptClass.getIRI().toString();
		if (iri.startsWith(NAMESPACE_SCT)) {
			return Long.parseLong(iri.substring(NAMESPACE_SCT.length())); 
		} else {
			return -1L;
		}
	}

	public OWLClass getConceptClass(final long conceptId) {
		return getOWLClass(PREFIX_SCT + conceptId);
	}

	private OWLObjectProperty getConceptObjectProperty(final long conceptId) {
		return getOWLObjectProperty(PREFIX_SCT + conceptId);
	}

//	private OWLDataProperty getConceptDataProperty(final long conceptId) {
//		return getOWLDataProperty(PREFIX_SCT + conceptId);
//	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final long typeId, final long destinationId, final boolean destinationNegated, final boolean universal) {
		final OWLClass destinationClass = getConceptClass(destinationId);
		final OWLClassExpression filler = destinationNegated 
				? getOWLObjectComplementOf(destinationClass) 
				: destinationClass;

		return getRelationshipExpression(typeId, filler, universal);
	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final long typeId, final OWLClassExpression filler, final boolean universal) {
		final OWLObjectProperty property = getConceptObjectProperty(typeId);

		if (universal) {
			return getOWLObjectAllValuesFrom(property, filler);
		} else {
			return getOWLObjectSomeValuesFrom(property, filler);
		}
	}

	private OWLObjectSomeValuesFrom getRoleGroupExpression(final OWLClassExpression filler) {
		final OWLObjectProperty property = getConceptObjectProperty(ROLE_GROUP);
		return getOWLObjectSomeValuesFrom(property, filler);
	}

	private Set<OWLClassExpression> getConceptDefinitionExpression(final long conceptId, final InternalIdMultimap<StatementFragment> statements) {
		final Set<OWLClassExpression> intersection = Sets.newHashSet();
		final LongSet superTypeIds = taxonomy.getStatedAncestors()
				.getDestinations(conceptId, true);
		
		for (final LongIterator itr = superTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			addParent(parentId, intersection);
		}

		final Collection<StatementFragment> statedNonIsAFragments = statements.get(conceptId);

		final Collection<ConcreteDomainFragment> statedConcreteDomainMembers = taxonomy
				.getStatedConcreteDomainMembers()
				.get(Long.toString(conceptId));

		// "Never grouped" relationships are added directly to the OWL object intersection
		statedNonIsAFragments.stream()
			.filter(r -> isNeverGrouped(r))
			.collect(Collectors.groupingBy(StatementFragment::getUnionGroup))
			.entrySet()
			.stream()
			.forEachOrdered(ug -> addUnionGroup(ug, intersection));

		// Ungrouped concept concrete domain members are also added directly
		statedConcreteDomainMembers.stream()
			.filter(c -> c.getGroup() == 0)
			.forEachOrdered(c -> addConcreteDomainMember(c, intersection));

		// Remaining stated relationships are wrapped in roleGroups
		statedNonIsAFragments.stream()
			.filter(r -> !isNeverGrouped(r))
			.collect(Collectors.groupingBy(StatementFragment::getGroup))
			.entrySet()
			.stream()
			.forEachOrdered(g -> addGroup(conceptId, g, intersection));

		return intersection;
	}

	private void addParent(final long parentId, final Set<OWLClassExpression> intersection) {
		final OWLClass parentClass = getConceptClass(parentId);
		intersection.add(parentClass);
	}

	private boolean isNeverGrouped(final StatementFragment r) {
		return neverGroupedIds.contains(r.getTypeId()) && r.getGroup() == 0;
	}

	private void addUnionGroup(final Entry<Integer, List<StatementFragment>> unionGroup, final Set<OWLClassExpression> intersection) {
		if (unionGroup.getKey() > 0) {
			final long commonTypeId = unionGroup.getValue().get(0).getTypeId();
			final boolean isUniversal = unionGroup.getValue().get(0).isUniversal();

			checkState(unionGroup.getValue()
					.stream()
					.allMatch(r -> commonTypeId == r.getTypeId() && isUniversal == r.isUniversal()));

			final Set<OWLClassExpression> unionGroupDisjuncts = unionGroup.getValue()
					.stream()
					.map(ugr -> getConceptClass(ugr.getDestinationId()))
					.collect(Collectors.toSet());

			final OWLClassExpression unionGroupExpression = getOWLObjectUnionOf(unionGroupDisjuncts);
			final OWLQuantifiedObjectRestriction relationshipExpression = getRelationshipExpression(commonTypeId, unionGroupExpression, isUniversal);
			intersection.add(relationshipExpression);
		} else {
			unionGroup.getValue()
				.stream()
				.map(ugr -> getRelationshipExpression(ugr.getTypeId(), 
						ugr.getDestinationId(), 
						ugr.isDestinationNegated(), 
						ugr.isUniversal()))
				.forEachOrdered(intersection::add);
		}
	}

	private void addGroup(final long conceptId, final Entry<Integer, List<StatementFragment>> group, final Set<OWLClassExpression> intersection) {
		final Set<OWLClassExpression> groupIntersection = Sets.newHashSet();

		group.getValue()
			.stream()
			.collect(Collectors.groupingBy(StatementFragment::getUnionGroup))
			.entrySet()
			.stream()
			.forEachOrdered(ug -> addUnionGroup(ug, groupIntersection));

		if (group.getKey() > 0) {
			
			// CD members should only be considered in non-zero groups
			taxonomy.getStatedConcreteDomainMembers()
				.get(Long.toString(conceptId))
				.stream()
				.filter(c -> c.getGroup() == group.getKey())
				.forEachOrdered(c -> addConcreteDomainMember(c, groupIntersection));
			
			intersection.add(getRoleGroupExpression(getOWLObjectIntersectionOf(groupIntersection)));
			
		} else {
			groupIntersection.forEach(ce -> {
				final OWLClassExpression singleGroupExpression = getRoleGroupExpression(ce);
				intersection.add(singleGroupExpression);
			});
		}
	}

	private static final String[] FIND = new String[] {"%", "(", ")" };
	private static final String[] REPLACE = new String[] { "%25", "%28", "%29" };
	
	private void addConcreteDomainMember(final ConcreteDomainFragment member, final Set<OWLClassExpression> intersection) {
		final long typeId = member.getTypeId();
		final String serializedValue = member.getSerializedValue();
		final DataType sctDataType = SnomedRefSetUtil.getDataType(Long.toString(member.getRefSetId()));
		final OWL2Datatype owl2Datatype = getOWL2Datatype(sctDataType);

		String encodedValue;
		
		try {
			encodedValue = URLEncoder.encode(serializedValue, Charsets.UTF_8.name());
			encodedValue = StringUtil.replace(encodedValue, FIND, REPLACE);
			encodedValue = String.format("label_%s_%s", encodedValue, owl2Datatype.getShortForm());
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Cannot encode literal: '" + serializedValue + "'.", e);
		}

		final OWLClass valueConcept = getOWLClass(PREFIX_SO + encodedValue);
		final OWLQuantifiedObjectRestriction valueExpression = getRelationshipExpression(typeId, valueConcept, false);
		
//		final OWLDataProperty dataProperty = getConceptDataProperty(typeId);
//		final OWLLiteral valueLiteral = getDataFactory().getOWLLiteral(serializedValue, owl2Datatype);
//		final OWLDataHasValue dataExpression = getDataFactory().getOWLDataHasValue(dataProperty, valueLiteral);

		intersection.add(valueExpression);
	}
	
	/////////////////////////////////////////////////////
	// Low-level building blocks for OWL 2 ontologies
	/////////////////////////////////////////////////////
	
	private OWLDataFactory getDataFactory() {
		return manager.getOWLDataFactory();
	}

	public OWLClassExpression getOWLThing() {
		return getDataFactory().getOWLThing();
	}
	
	private OWLClassExpression getOWLNothing() {
		return getDataFactory().getOWLNothing();
	}

	private OWL2Datatype getOWL2Datatype(final DataType dataType) {
		switch (dataType) {
			case BOOLEAN: return OWL2Datatype.XSD_BOOLEAN;
			case DATE: return OWL2Datatype.XSD_DATE_TIME;
			case DECIMAL: return OWL2Datatype.XSD_DECIMAL;
			case INTEGER: return OWL2Datatype.XSD_INT;
			case STRING: return OWL2Datatype.RDF_PLAIN_LITERAL;
			default: throw new IllegalStateException(MessageFormat.format("Unhandled datatype enum ''{0}''.", dataType));
		}
	}

	private OWLClass getOWLClass(final String abbreviatedIRI) {
		return getDataFactory().getOWLClass(abbreviatedIRI, prefixManager);
	}

	private OWLObjectProperty getOWLObjectProperty(final String abbreviatedIRI) {
		return getDataFactory().getOWLObjectProperty(abbreviatedIRI, prefixManager);
	}

//	private OWLDataProperty getOWLDataProperty(final String abbreviatedIRI) {
//		return getDataFactory().getOWLDataProperty(abbreviatedIRI, prefixManager);
//	}

	private OWLClassExpression getOWLObjectIntersectionOf(final Set<OWLClassExpression> conjuncts) {
		if (conjuncts.size() > 1) {
			return getDataFactory().getOWLObjectIntersectionOf(conjuncts);
		} else {
			return Iterables.getOnlyElement(conjuncts);
		}
	}

	private OWLClassExpression getOWLObjectUnionOf(final Set<OWLClassExpression> disjuncts) {
		if (disjuncts.size() > 1) {
			return getDataFactory().getOWLObjectUnionOf(disjuncts);
		} else {
			return Iterables.getOnlyElement(disjuncts);
		}
	}

	private OWLClassExpression getOWLObjectComplementOf(final OWLClass owlClass) {
		return getDataFactory().getOWLObjectComplementOf(owlClass);
	}
	
	private OWLObjectSomeValuesFrom getOWLObjectSomeValuesFrom(final OWLObjectProperty property, final OWLClassExpression filler) {
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}
	
	private OWLObjectAllValuesFrom getOWLObjectAllValuesFrom(final OWLObjectProperty property, final OWLClassExpression filler) {
		return getDataFactory().getOWLObjectAllValuesFrom(property, filler);
	}

	private OWLDeclarationAxiom getOWLDeclarationAxiom(final OWLEntity e) {
		return getDataFactory().getOWLDeclarationAxiom(e);
	}

	private OWLSubClassOfAxiom getOWLSubClassOfAxiom(final OWLClassExpression child, final OWLClassExpression parent) {
		return getDataFactory().getOWLSubClassOfAxiom(child, parent);
	}

	private OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(final OWLClassExpression... expressions) {
		return getDataFactory().getOWLEquivalentClassesAxiom(expressions);
	}

	private OWLDisjointUnionAxiom getOWLDisjointUnionAxiom(final OWLClass parent, final Set<OWLClass> children) {
		return getDataFactory().getOWLDisjointUnionAxiom(parent, children);
	}

	private OWLSubObjectPropertyOfAxiom getOWLSubObjectPropertyOfAxiom(final OWLObjectPropertyExpression child, final OWLObjectPropertyExpression parent) {
		return getDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
	}

//	private OWLSubDataPropertyOfAxiom getOWLSubDataPropertyOfAxiom(final OWLDataPropertyExpression child, final OWLDataPropertyExpression parent) {
//		return getDataFactory().getOWLSubDataPropertyOfAxiom(child, parent);
//	}
}
