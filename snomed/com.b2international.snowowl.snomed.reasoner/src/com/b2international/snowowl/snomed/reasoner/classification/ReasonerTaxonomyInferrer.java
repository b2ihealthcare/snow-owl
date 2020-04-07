/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdEdges;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdMap;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalSctIdMultimap;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalSctIdSet;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Ordering;
import com.google.common.util.concurrent.RateLimiter;

/**
 * @since
 */
public final class ReasonerTaxonomyInferrer {

	// Report every ~5 minutes
	private static final RateLimiter LOG_LIMITER = RateLimiter.create(1.0 / 300.0);
	
	private static class LoggingProgressMonitor implements ReasonerProgressMonitor {

		private final Logger log;
		
		private String taskName = "<unknown task>";
		private float completionLevel = -1.0f;

		public LoggingProgressMonitor(final Logger log) {
			this.log = log;
		}

		private void reportTask() {
			log.info("--- Reasoner task: {} [{}]", taskName, completionLevel < 0.0f ? "--.-%" : String.format("%04.1f%%", completionLevel * 100.0f));
		}

		@Override
		public void reasonerTaskBusy() {
			if (LOG_LIMITER.tryAcquire()) {
				reportTask();
			}
		}

		@Override
		public void reasonerTaskProgressChanged(final int value, final int max) {
			if (value >= 0 && max > 0) {
				this.completionLevel = (float) value / max;
			}
			reasonerTaskBusy(); // Apply rate limiting
		}

		@Override
		public void reasonerTaskStarted(final String taskName) {
			this.taskName = (taskName == null) ? "<unknown task>" : taskName;
			this.completionLevel = -1.0f;
			reportTask(); // No rate limiting for started reasoner tasks
		}

		@Override
		public void reasonerTaskStopped() {
			// We are not interested in stopped reasoner tasks
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner-taxonomy-inferrer");

	public static final long DEPTH_CHANGE = -1L;
	
	private static final int EXPECTED_SIZE = 600_000;
	private static final NodeSet<OWLClass> EMPTY_NODE_SET = new OWLClassNodeSet();

	private static final String EXTENSION_POINT_ID = "org.protege.editor.owl.inference_reasonerfactory";
	private static final String CLASS_ELEMENT = "class";
	private static final String VALUE_ATTRIBUTE = "value";

	private static final String PRECOMPUTE_PROPERTY = "com.b2international.snowowl.snomed.reasoner.precomputeInferences";

	private final String reasonerId;
	private final DelegateOntology ontology;
	private final BranchContext branchContext;

	private OWLReasoner reasoner;
	// owl:Nothing should only be considered once
	private boolean nothingVisited = false;
	private LongSet processedConceptIds;
	private LongList iterationOrder; 

	private InternalIdEdges.Builder inferredAncestors;
	private InternalSctIdSet.Builder unsatisfiableConcepts;
	private InternalSctIdMultimap.Builder equivalentConcepts;

	private static OWLReasoner createReasoner(final String reasonerId, final OWLOntology owlOntology) {
		final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		final IExtension[] extensions = extensionPoint.getExtensions();

		for (final IExtension extension : extensions) {

			final IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			final String extensionId = extension.getUniqueIdentifier();

			if (reasonerId.equals(extensionId)) {
				final Optional<IConfigurationElement> classElement = Arrays.asList(configurationElements)
						.stream()
						.filter(e -> CLASS_ELEMENT.equals(e.getName()))
						.findFirst();

				if (!classElement.isPresent()) {
					throw new ReasonerApiException("Couldn't create reasoner info instance for extension '%s'.", reasonerId);
				}

				final ProtegeOWLReasonerInfo reasonerInfo;

				try {
					reasonerInfo = (ProtegeOWLReasonerInfo) classElement.get().createExecutableExtension(VALUE_ATTRIBUTE);
					reasonerInfo.initialise();
				} catch (final Exception e) {
					throw new ReasonerApiException("Couldn't create reasoner info instance for extension '%s'.", reasonerId, e);
				}

				final OWLReasonerFactory reasonerFactory = reasonerInfo.getReasonerFactory();
				final OWLReasonerConfiguration reasonerConfiguration = reasonerInfo.getConfiguration(new LoggingProgressMonitor(LOGGER));
				return reasonerFactory.createNonBufferingReasoner(owlOntology, reasonerConfiguration);
			}
		}

		throw new ReasonerApiException("Couldn't create reasoner info instance for extension '%s'.", reasonerId);
	}

	public ReasonerTaxonomyInferrer(final String reasonerId, final DelegateOntology ontology, final BranchContext branchContext) {
		this.reasonerId = reasonerId;
		this.ontology = ontology;
		this.branchContext = branchContext;
	}

	public ReasonerTaxonomy addInferences(final ReasonerTaxonomy taxonomy) {
		LOGGER.info(">>> Classification and inferred taxonomy extraction");

		try {
			final Stopwatch stopwatch = Stopwatch.createStarted();
			
			Deque<Node<OWLClass>> firstLayer = new LinkedList<Node<OWLClass>>();
			Deque<Node<OWLClass>> secondLayer = new LinkedList<Node<OWLClass>>();
			final Set<Node<OWLClass>> deferredNodes = newHashSet();
			
			reasoner = createReasoner(reasonerId, ontology);
			if (Boolean.getBoolean(PRECOMPUTE_PROPERTY)) {
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			}
			
			final NodeSet<OWLClass> initialSubClasses = reasoner.getSubClasses(ontology.getOWLThing(), true);
			final Set<Node<OWLClass>> initialNodes = initialSubClasses.getNodes();
			firstLayer.addAll(initialNodes);
			
			processedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(EXPECTED_SIZE);
			iterationOrder = PrimitiveLists.newLongArrayListWithExpectedSize(EXPECTED_SIZE);

			final InternalIdMap conceptMap = taxonomy.getConceptMap();
			inferredAncestors = InternalIdEdges.builder(conceptMap);
			unsatisfiableConcepts = InternalSctIdSet.builder(conceptMap);
			equivalentConcepts = InternalSctIdMultimap.builder(conceptMap);

			// Breadth-first walk through the class hierarchy
			while (!firstLayer.isEmpty()) {
				final Node<OWLClass> current = firstLayer.removeFirst();
				deferredNodes.remove(current);
				final NodeSet<OWLClass> nextNodeSet = processNode(current, deferredNodes);
				final Set<Node<OWLClass>> nextNodes = nextNodeSet.getNodes();
				secondLayer.addAll(nextNodes);
			
				if (firstLayer.isEmpty()) {
					// Indicate that the previous set of caches can be emptied
					if (deferredNodes.isEmpty()) {
						iterationOrder.add(DEPTH_CHANGE);
					}
			
					// Swap the role of the two layers
					if (!secondLayer.isEmpty()) {
						Deque<Node<OWLClass>> temp = firstLayer;
						firstLayer = secondLayer;
						secondLayer = temp;
					}
				}
			}

			processedConceptIds = null;

			LOGGER.info("<<< Classification and inferred taxonomy extraction [{}]", stopwatch.stop());

			return taxonomy.withInferences(inferredAncestors.build(), 
					unsatisfiableConcepts.build(), 
					equivalentConcepts.build(),
					iterationOrder);
			
		} finally {
			if (reasoner != null) {
				reasoner.dispose();
			}
		}
	}

	private NodeSet<OWLClass> processNode(final Node<OWLClass> node, Set<Node<OWLClass>> deferredNodes) {

		// Stop the walk if the node has already been visited
		if (isNodeProcessed(node)) {
			return EMPTY_NODE_SET;
		}

		// Check first if the current node is the "bottom" one, as all OWL classes are superclasses of owl:Nothing
		final LongSet conceptIds = collectConceptIds(node, PrimitiveSets.newLongOpenHashSet());

		// Stop the walk if we are at the bottom (we should only reach the bottom node once)
		if (node.isBottomNode()) {
			addUnsatisfiableConcepts(conceptIds);
			processedConceptIds.addAll(conceptIds);
			iterationOrder.addAll(conceptIds);
			return EMPTY_NODE_SET;
		}

		// All parents must be visited before this item can be processed; if not, the same node will appear later again, so stop walking
		final NodeSet<OWLClass> parentNodeSet = reasoner.getSuperClasses(node.getRepresentativeElement(), true);

		for (final Node<OWLClass> parentNode : parentNodeSet) {
			if (!isNodeProcessed(parentNode)) {
				deferredNodes.add(node);
				return EMPTY_NODE_SET;
			}
		}

		// Nodes that contain more than one concept are an indication of equivalence
		if (conceptIds.size() > 1) {
			addEquivalentConcepts(conceptIds);
		}

		// Extract parents and ancestors using the reasoner
		final LongSet parentConceptIds = PrimitiveSets.newLongOpenHashSet();
		for (final Node<OWLClass> parentNode : parentNodeSet) {
			collectConceptIds(parentNode, parentConceptIds);
		}

		addEdges(conceptIds, parentConceptIds);		
		processedConceptIds.addAll(conceptIds);
		iterationOrder.addAll(conceptIds);
		
		return computeNextNodeSet(node);
	}

	private void addUnsatisfiableConcepts(final LongSet conceptIds) {
		unsatisfiableConcepts.addAll(conceptIds);
	}

	private void addEquivalentConcepts(final LongSet conceptIds) {
		final Set<String> conceptIdsAsString = LongSets.toStringSet(conceptIds);

		/*
		 * Try to get the smallest SCTID (using natural ordering for Strings) that is
		 * already persisted; if no such item exists, we will use the smallest SCTID
		 * using the same ordering.
		 */
		final String representativeId = SnomedRequests.prepareSearchConcept()
				.one()
				.filterByIds(conceptIdsAsString)
				.setFields(SnomedConceptDocument.Fields.ID)
				.sortBy(SortField.ascending(RevisionDocument.Fields.ID))
				.build()
				.execute(branchContext)
				.first()
				.map(SnomedConcept::getId)
				.orElseGet(() -> Ordering.natural().min(conceptIdsAsString));

		conceptIdsAsString.remove(representativeId);
		equivalentConcepts.putAll(representativeId, conceptIdsAsString);
	}

	private void addEdges(final LongSet sourceIds, final LongSet destinationIds) {
		final List<String> sourceIdsAsString = LongSets.toStringList(sourceIds);
		final List<String> destinationIdsAsString = LongSets.toStringList(destinationIds);

		/*
		 * All sources should be linked with all destinations (in other words, we need a
		 * cartesian product here)
		 */
		for (final String source : sourceIdsAsString) {
			final List<String> repeatedSource = Collections.nCopies(destinationIdsAsString.size(), source);
			inferredAncestors.addEdges(repeatedSource, destinationIdsAsString);
		}
	}

	private boolean isNodeProcessed(final Node<OWLClass> node) {
		for (final OWLClass entity : node) {
			if (entity.isOWLThing()) {
				return true;
			}
			
			final long conceptId = ontology.getConceptId(entity);
			if (conceptId == -1L) { continue; }  // This OWL class does not correspond to a SNOMED CT concept
			if (!processedConceptIds.contains(conceptId)) { return false; }
		}

		return true;
	}

	private LongSet collectConceptIds(final Node<OWLClass> node, final LongSet conceptIds) {
		for (final OWLClass entity : node) {
			final long conceptId = ontology.getConceptId(entity);
			if (conceptId == -1L) { continue; } // This OWL class does not correspond to a SNOMED CT concept
			conceptIds.add(conceptId);
		}

		return conceptIds;
	}

	private NodeSet<OWLClass> computeNextNodeSet(final Node<OWLClass> node) {
		final NodeSet<OWLClass> subClasses = reasoner.getSubClasses(node.getRepresentativeElement(), true);

		if (!subClasses.isBottomSingleton()) {
			return subClasses;
		} else if (!nothingVisited) {
			nothingVisited = true;
			return subClasses;
		} else {
			return EMPTY_NODE_SET;
		}
	}
}
