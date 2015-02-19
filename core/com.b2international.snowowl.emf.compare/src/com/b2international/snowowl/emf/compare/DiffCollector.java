/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.emf.compare;

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.ChangeKind.DELETED;
import static com.b2international.commons.ChangeKind.UNCHANGED;
import static com.b2international.commons.ChangeKind.UPDATED;
import static com.b2international.snowowl.emf.compare.internal.EmfCompare.createComparison;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.compare.DifferenceKind.ADD;
import static org.eclipse.emf.compare.DifferenceKind.CHANGE;
import static org.eclipse.emf.compare.DifferenceKind.DELETE;
import static org.eclipse.emf.compare.DifferenceKind.MOVE;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Change;
import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.AttributeDiffImpl;
import com.b2international.snowowl.emf.compare.diff.Diff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiffImpl;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiffImpl;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiffImpl;
import com.b2international.snowowl.emf.compare.diff.processor.DiffProcessor;
import com.google.common.base.Stopwatch;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

/**
 * Basic {@link Diff difference} collector implementation.
 *
 */
public abstract class DiffCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiffCollector.class);
	private static final String LOG_TEMPLATE = "Differences has been successfully processed. Found {0} difference{1}. [{2}]";
	
	private static final BiMap<Change, DifferenceKind> CHANGE_TO_DIFFERENCE_KIND_MAP = ImmutableBiMap.<Change, DifferenceKind>of(
			ADDED, ADD,
			DELETED, DELETE,
			UPDATED, CHANGE
		);
	
	private static final Map<Change, Change> INVERSE_CHANGE_MAP = ImmutableMap.<Change, Change>of(
			ADDED, DELETED, 
			DELETED, ADDED, 
			UPDATED, UPDATED, 
			UNCHANGED, UNCHANGED); 
	
	/**
	 * Compares the given {@link Notifier notifiers} and produces a collection of output with the processor
	 * representing all the differences between the given inputs.
	 * @param left object to compare with the {@code right}.
	 * @param right object to compare with the {@code left} one.
	 * @param processor the processor to use for the output gathering.
	 * @return a collection of arbitrary objects representing the differences.
	 */
	public static <E> Collection<E> compare(final Notifier left, final Notifier right, final DiffProcessor<E> processor) {
		
		checkNotNull(left, "left");
		checkNotNull(right, "right");
		checkNotNull(processor, "processor");
		
		final Stopwatch stopwatch = Stopwatch.createStarted();
		trace("Calculating differences between '" + left + "' and '" + right + "'. Building object graphs for differences...");
		final Comparison comparison = createComparison(left, right, processor.getExcludedFeatures(), processor.getNsUriProvider());
		trace("Difference calculation successfully finished. [" + stopwatch + "]");
		
		stopwatch.reset();
		stopwatch.start();
		
		final Collection<E> deltas = newLinkedHashSet();
		trace("Processing differences between '" + left + "' and '" + right + "'...");
		
		for (final org.eclipse.emf.compare.Diff diff : comparison.getDifferences()) {

			if (isMove(diff)) {
				continue;
			}
			
			if (diff instanceof ReferenceChange) {
				
				final ReferenceChange referenceChange = (ReferenceChange) diff;
				final EReference reference = referenceChange.getReference();
				final Change change = getChange(referenceChange.getKind());
				
				final EObject leftMatch = referenceChange.getMatch().getLeft();
				final EObject rightMatch = referenceChange.getMatch().getRight();

				if (reference.isMany()) {

					/* 
					 * XXX: Assuming the source is always the left side, as the documentation says for two-way compare operations.
					 * 
					 * Change is not yet inverted, so if it says that something was _added_ to the left object, we will find the parent object on the left,
					 * otherwise it is on the right. 
					 */
					final EObject target = change.isNew() ? leftMatch : rightMatch;
					final EObject value = referenceChange.getValue();
					
					final ReferenceDiff referenceDiff = createAttributeDiff(reference, value, target, invert(change));
					deltas.add(processor.processManyValueReferenceChange(referenceDiff));
					
				} else {

					if (null == leftMatch || null == rightMatch) {
						continue;
					}
					
					final EObject fromValue = (EObject) leftMatch.eGet(reference);
					final EObject toValue = (EObject) rightMatch.eGet(reference);
					final SingleValueReferenceDiff referenceDiff = createSingleValueAttributeDiff(reference, fromValue, toValue, rightMatch, change);
					deltas.add(processor.processSingleValueReferenceChange(referenceDiff));
				}
				
			} else if (diff instanceof AttributeChange) {
				
				final AttributeChange attributeChange = (AttributeChange) diff; 
				final EAttribute attribute = attributeChange.getAttribute();
				final Change change = getChange(attributeChange.getKind());
				
				final EObject source = attributeChange.getMatch().getLeft();
				final EObject target = attributeChange.getMatch().getRight();
				
				if (null == source || null == target) {
					continue;
				}
				
				if (attribute.isMany()) {

					final Object value = attributeChange.getValue();
					final AttributeDiff attributeDiff = createReferenceDiff(attribute, value, target, invert(change));
					deltas.add(processor.processManyValueAttributeChange(attributeDiff));
					
				} else {
					
					final Object fromValue = source.eGet(attribute);
					final Object toValue = target.eGet(attribute);
					
					final SingleValueAttributeDiff attributeDiff = createSingleValueReferenceDiff(attribute, fromValue, toValue, target, change);
					deltas.add(processor.processSingleValueAttributeChange(attributeDiff));
				}
			}
		}
		
		final int deltaSize = deltas.size();
		trace(format(LOG_TEMPLATE, deltaSize, deltaSize == 1 ? "" : "s", stopwatch));
		
		return unmodifiableCollection(deltas);
	}
	
	private static Change getChange(final DifferenceKind kind) {
		return checkNotNull(CHANGE_TO_DIFFERENCE_KIND_MAP.inverse().get(checkNotNull(kind, "kind")), "Cannot associated change for difference kind: " + kind);
	}
	
	private static boolean isMove(final org.eclipse.emf.compare.Diff diff) {
		return MOVE.equals(checkNotNull(diff, "diff").getKind());
	}
	
	private static SingleValueAttributeDiff createSingleValueReferenceDiff(final EAttribute attribute, final Object fromValue, final Object toValue, final EObject target, final Change change) {
		return new SingleValueAttributeDiffImpl(attribute, toValue, target, change, fromValue);
	}

	private static AttributeDiff createReferenceDiff(final EAttribute attribute, final Object value, final EObject target, final Change change) {
		return new AttributeDiffImpl(attribute, value, target, change);
	}

	private static SingleValueReferenceDiff createSingleValueAttributeDiff(final EReference reference, final EObject fromValue, final EObject toValue, final EObject target, final Change change) {
		return new SingleValueReferenceDiffImpl(reference, toValue, target, change, fromValue);
	}

	private static ReferenceDiff createAttributeDiff(final EReference reference, final EObject value, final EObject target, final Change change) {
		return new ReferenceDiffImpl(reference, value, target, change);
	}
	
	private static Change invert(final Change change) {
		return INVERSE_CHANGE_MAP.get(checkNotNull(change, "change"));
	}
	
	private static void trace(final String msg) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(msg);
		}
	}
}