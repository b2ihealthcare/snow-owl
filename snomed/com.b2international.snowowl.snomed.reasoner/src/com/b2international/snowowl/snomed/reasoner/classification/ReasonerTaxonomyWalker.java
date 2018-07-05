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

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerException;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.google.common.base.Stopwatch;

/**
 * @since
 */
public final class ReasonerTaxonomyWalker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReasonerTaxonomyWalker.class);

	private static final int EXPECTED_SIZE = 600_000;
	private static final NodeSet<OWLClass> EMPTY_NODE_SET = new OWLClassNodeSet();

	private static final String EXTENSION_POINT_ID = "org.protege.editor.owl.inference_reasonerfactory";
	private static final String CLASS_ELEMENT = "class";
	private static final String VALUE_ATTRIBUTE = "value";

	private final DelegateOntology ontology;
	private final OWLReasoner reasoner;
	private final BranchContext branchContext;
	private final TaxonomyCallback taxonomyCallback;
	private final EquivalenceCallback equivalenceCallback;

	private LongSet processedConceptIds;
	// owl:Nothing should only be considered once
	private boolean nothingVisited = false;

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
					throw new ReasonerException(String.format("Couldn't create reasoner info instance for extension '%s'.", reasonerId));
				}

				final ProtegeOWLReasonerInfo reasonerInfo;

				try {
					reasonerInfo = (ProtegeOWLReasonerInfo) classElement.get().createExecutableExtension(VALUE_ATTRIBUTE);
				} catch (final CoreException e) {
					throw new ReasonerException(String.format("Couldn't create reasoner info instance for extension '%s'.", reasonerId), e);
				}

				final OWLReasonerFactory reasonerFactory = reasonerInfo.getReasonerFactory();
				return reasonerFactory.createNonBufferingReasoner(owlOntology);
			}
		}

		throw new ReasonerException(String.format("Couldn't create reasoner info instance for extension '%s'.", reasonerId));
	}

	public ReasonerTaxonomyWalker(final String reasonerId, 
			final DelegateOntology ontology, 
			final BranchContext branchContext, 
			final TaxonomyCallback taxonomyCallback,
			final EquivalenceCallback equivalenceCallback) {

		this.ontology = ontology;
		this.reasoner = createReasoner(reasonerId, ontology);
		this.branchContext = branchContext;
		this.taxonomyCallback = taxonomyCallback;
		this.equivalenceCallback = equivalenceCallback;
	}

	public void walk() {
		LOGGER.info(">>> Taxonomy extraction");

		final Stopwatch stopwatch = Stopwatch.createStarted();
		final Deque<Node<OWLClass>> nodesToProcess = new LinkedList<Node<OWLClass>>();
		nodesToProcess.add(reasoner.getTopClassNode());

		processedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(EXPECTED_SIZE);

		// Breadth-first walk through the class hierarchy
		while (!nodesToProcess.isEmpty()) {
			final Node<OWLClass> current = nodesToProcess.removeFirst();
			final NodeSet<OWLClass> nextNodeSet = processNode(current);
			nodesToProcess.addAll(nextNodeSet.getNodes());
		}

		processedConceptIds = null;

		LOGGER.info("<<< Taxonomy extraction [{}]", stopwatch.stop());
	}

	private NodeSet<OWLClass> processNode(final Node<OWLClass> node) {

		// Stop the walk if the node has already been visited
		if (isNodeProcessed(node)) {
			return node.isTopNode() ? reasoner.getSubClasses(node.getRepresentativeElement(), true) : EMPTY_NODE_SET;
		}

		// Check first if the current node is the "bottom" one, as all OWL classes are superclasses of owl:Nothing
		final LongSet conceptIds = collectConceptIds(node, PrimitiveSets.newLongOpenHashSet());

		if (node.isBottomNode()) {
			notifyEquivalentConcepts(true, conceptIds);
			processedConceptIds.addAll(conceptIds);
			return EMPTY_NODE_SET;
		}

		// All parents must be visited before this item can be processed; if not, we will meet this concept again later
		final NodeSet<OWLClass> parentNodeSet = reasoner.getSuperClasses(node.getRepresentativeElement(), true);

		for (final Node<OWLClass> parentNode : parentNodeSet) {
			if (!isNodeProcessed(parentNode)) {
				return EMPTY_NODE_SET;
			}
		}

		// Nodes that contain more than one concept are an indication of equivalence
		if (conceptIds.size() > 1) {
			notifyEquivalentConcepts(false, conceptIds);
		}

		// Extract parents and ancestors using the reasoner
		final LongSet parentConceptIds = PrimitiveSets.newLongOpenHashSet();
		for (final Node<OWLClass> parentNode : parentNodeSet) {
			collectConceptIds(parentNode, parentConceptIds);
		}

		final LongSet ancestorConceptIds = getAncestors(node.getRepresentativeElement());		
		processedConceptIds.addAll(conceptIds);

		for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /* empty */) {
			taxonomyCallback.onConcept(itr.next(), parentConceptIds, ancestorConceptIds);
		}

		return computeNextNodeSet(node);
	}

	private void notifyEquivalentConcepts(final boolean unsatisfiable, final LongSet conceptIds) {

		// Try to get a representative element that is already persisted; if no such item exists, we will use the first element 
		final long representativeId = SnomedRequests.prepareSearchConcept()
				.one()
				.filterByIds(LongSets.toStringSet(conceptIds))
				.setFields(SnomedConceptDocument.Fields.ID, RevisionDocument.Fields.STORAGE_KEY)
				.sortBy(SortField.ascending(RevisionDocument.Fields.STORAGE_KEY))
				.build()
				.execute(branchContext)
				.first()
				.map(c -> Long.parseLong(c.getId()))
				.orElseGet(() -> conceptIds.iterator().next());

		final LongList sortedConceptIds = PrimitiveLists.newLongArrayListWithExpectedSize(conceptIds.size());
		sortedConceptIds.add(representativeId);
		conceptIds.remove(representativeId);
		sortedConceptIds.addAll(conceptIds);

		equivalenceCallback.onEquivalentSet(unsatisfiable, sortedConceptIds);
	}

	private boolean isNodeProcessed(final Node<OWLClass> node) {
		for (final OWLClass entity : node) {
			final long conceptId = ontology.getConceptId(entity);
			if (conceptId == -1L) { continue; }
			if (!processedConceptIds.contains(conceptId)) { return false; }
		}

		return true;
	}

	private LongSet collectConceptIds(final Node<OWLClass> node, final LongSet conceptIds) {
		for (final OWLClass entity : node) {
			final long conceptId = ontology.getConceptId(entity);
			if (conceptId == -1L) { continue; }
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

	public LongSet getAncestors(final long conceptId) {
		return getAncestors(ontology.getConceptClass(conceptId));
	}

	private LongSet getAncestors(final OWLClass conceptClass) {
		final NodeSet<OWLClass> ancestorNodeSet = reasoner.getSuperClasses(conceptClass, false);
		final LongSet ancestorConceptIds = PrimitiveSets.newLongOpenHashSet();
		for (final Node<OWLClass> ancestorNode : ancestorNodeSet) {
			collectConceptIds(ancestorNode, ancestorConceptIds);
		}
		return ancestorConceptIds;
	}
}
