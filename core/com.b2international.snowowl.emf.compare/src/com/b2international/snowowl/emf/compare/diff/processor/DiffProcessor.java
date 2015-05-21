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
package com.b2international.snowowl.emf.compare.diff.processor;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.emf.NsUriProvider;
import com.b2international.snowowl.emf.compare.diff.AttributeDiff;
import com.b2international.snowowl.emf.compare.diff.Diff;
import com.b2international.snowowl.emf.compare.diff.ReferenceDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueAttributeDiff;
import com.b2international.snowowl.emf.compare.diff.SingleValueReferenceDiff;

/**
 * Difference processor representation. The processor is reposnsible to
 * consume a {@link Diff difference} and transform it any arbitrary object with 
 * a type of {@code E}. 
 * @param <E> the type of produced output.
 */
public interface DiffProcessor<E> {

	Collection<EStructuralFeature> getExcludedFeatures();
	
	/**
	 * Transform a {@link SingleValueAttributeDiff single valued attribute difference} into the desired format. 
	 * @param diff the difference to process.
	 * @return the output.
	 */
	E processSingleValueAttributeChange(final SingleValueAttributeDiff diff);

	/**
	 * Transform a {@link AttributeDiff attribute difference} into the desired format. 
	 * @param diff the difference to process.
	 * @return the output.
	 */
	E processManyValueAttributeChange(final AttributeDiff diff);

	/**
	 * Transform a {@link SingleValueReferenceDiff single valued reference difference} into the desired format. 
	 * @param diff the difference to process.
	 * @return the output.
	 */
	E processSingleValueReferenceChange(final SingleValueReferenceDiff diff);

	/**
	 * Transform a {@link ReferenceDiff reference difference} into the desired format. 
	 * @param diff the difference to process.
	 * @return the output.
	 */
	E processManyValueReferenceChange(final ReferenceDiff diff);
	
	/**
	 * Returns with a human readable representation of the {@link Notifier} argument.
	 * @param notifier the notifier.
	 * @return the human readable representation of the notifier.
	 */
	String toString(final Notifier notifier);
	
	/**
	 * Returns with the namespace URI provider for the processor.
	 * @return the namespace URI provider.
	 */
	NsUriProvider getNsUriProvider();

}