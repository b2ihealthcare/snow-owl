/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.text.MessageFormat;
import java.util.Deque;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;

/**
 * 
 */
public class ReasonerTaxonomyWalker {

	private static final NodeSet<OWLClass> EMPTY_NODE_SET = new OWLClassNodeSet();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReasonerTaxonomyWalker.class);
	
	private final OWLReasoner reasoner;
	
	private final ReasonerTaxonomy taxonomy;

	private LongSet processedConceptIds;
	
	private final DefaultPrefixManager pm;

	private boolean nothingProcessed;
	
	/**
	 * 
	 * @param branchPath
	 * @param reasoner
	 * @param data
	 */
	public ReasonerTaxonomyWalker(final IBranchPath branchPath, final OWLReasoner reasoner, final ReasonerTaxonomy changeSet) {
		this.reasoner = reasoner;
		this.taxonomy = changeSet;
		this.pm = SnomedOntologyUtils.createPrefixManager(SnomedOntologyUtils.BASE_IRI.resolve(branchPath.getPath()));
		this.processedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(600000);
	}

	/**
	 * 
	 */
	public void walk() {
		LOGGER.info(">>> Taxonomy extraction");
		
		final Stopwatch stopwatch = Stopwatch.createStarted();
		final Deque<Node<OWLClass>> nodesToProcess = new LinkedList<Node<OWLClass>>();
		nodesToProcess.add(reasoner.getTopClassNode());
		
		// Breadth-first walk through the class hierarchy
		while (!nodesToProcess.isEmpty()) {
			
			final Node<OWLClass> currentNode = nodesToProcess.removeFirst();
			final NodeSet<OWLClass> nextNodeSet = walk(currentNode);

			if (!EMPTY_NODE_SET.equals(nextNodeSet)) {

				nodesToProcess.addAll(nextNodeSet.getNodes());
				
			} 
			
		}
		
		processedConceptIds.clear();
		processedConceptIds = null;
		
		LOGGER.info(MessageFormat.format("<<< Taxonomy extraction [{0}]", stopwatch.stop().toString()));
	}

	private NodeSet<OWLClass> walk(final Node<OWLClass> node) {
		
 		if (isNodeProcessed(node)) {
 			return node.isTopNode() ? reasoner.getSubClasses(node.getRepresentativeElement(), true) : EMPTY_NODE_SET;
		}

 		// Check first if we are at the bottom node, as all OWL classes are superclasses of Nothing
 		final boolean unsatisfiable = node.isBottomNode();
 		final LongSet conceptIds = PrimitiveSets.newLongOpenHashSet();
 		collectConceptIds(node, conceptIds);

		if (unsatisfiable) {
			registerEquivalentConceptIds(conceptIds, unsatisfiable);
			processedConceptIds.addAll(conceptIds);
			return EMPTY_NODE_SET;
		}

 		// Check if all parents have already been visited earlier
 		final NodeSet<OWLClass> parentNodeSet = reasoner.getSuperClasses(node.getRepresentativeElement(), true);

		for (final Node<OWLClass> parentNode : parentNodeSet) {
			if (!isNodeProcessed(parentNode)) {
				return EMPTY_NODE_SET;
			}
		}
		
		if (conceptIds.size() > 1) {
			registerEquivalentConceptIds(conceptIds, unsatisfiable);
		}

		final LongSet parentConceptIds = PrimitiveSets.newLongOpenHashSet();
		
		for (final Node<OWLClass> parentNode : parentNodeSet) {
			
			// No parents if we found the Top node
			if (parentNode.isTopNode()) {
				break;
			}
			
			collectConceptIds(parentNode, parentConceptIds);
		}
		
		processedConceptIds.addAll(conceptIds);
		
		for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /* empty */) {
			registerParentConceptIds(itr.next(), parentConceptIds);
		}
		
		return computeNextNodeSet(node);
	}

	private NodeSet<OWLClass> computeNextNodeSet(final Node<OWLClass> node) {
		
		final NodeSet<OWLClass> subClasses = reasoner.getSubClasses(node.getRepresentativeElement(), true);
		
		if (!subClasses.isBottomSingleton()) {
			return subClasses;
		}
			
		if (nothingProcessed) {
			return EMPTY_NODE_SET;
		} else {
			nothingProcessed = true;
			return subClasses;
		}
	}

	private void registerParentConceptIds(final long child, final LongSet parents) {
		taxonomy.addEntry(new ReasonerTaxonomyEntry(child, parents));
	}

	private boolean isNodeProcessed(final Node<OWLClass> node) {
		for (final OWLClass owlClass : node) {
			if (!isConceptClass(owlClass)) {
				continue;
			}
			
			final long conceptId = getConceptId(owlClass);
			if (!processedConceptIds.contains(conceptId)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void collectConceptIds(final Node<OWLClass> node, final LongSet conceptIds) {
		for (final OWLClass owlClass : node) {
			if (!isConceptClass(owlClass)) {
				continue;
			}
			
			final long conceptId = getConceptId(owlClass);
			conceptIds.add(conceptId);
		}
	}

	private void registerEquivalentConceptIds(final LongSet conceptIds, final boolean unsatisfiable) {
		if (unsatisfiable) {
			taxonomy.getUnsatisfiableConceptIds().addAll(conceptIds);
		} else {
			taxonomy.addEquivalentConceptIds(conceptIds);
		}
	}

	private boolean isConceptClass(final OWLClass owlClass) {
		return hasPrefix(owlClass, SnomedOntologyUtils.PREFIX_CONCEPT);
	}

	private boolean hasPrefix(final OWLClass owlClass, final String prefix) {
		return pm.getShortForm(owlClass.getIRI()).startsWith(prefix);
	}
	
	private long getConceptId(final OWLClass owlClass) {
		final String strippedShortForm = pm.getShortForm(owlClass.getIRI()).substring(SnomedOntologyUtils.PREFIX_SNOMED.length());
		return Long.parseLong(Iterables.get(Splitter.on('_').split(strippedShortForm), 1));
	}
}