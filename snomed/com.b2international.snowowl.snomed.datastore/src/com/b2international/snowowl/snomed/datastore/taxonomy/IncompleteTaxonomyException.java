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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.b2international.commons.Pair;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode;

/**
 * Runtime exception that has to be thrown when the underlying taxonomy being build
 * by the {@link ISnomedTaxonomyBuilder taxonomy builder} and 
 * the builder is in {@link SnomedTaxonomyBuilderMode#VALIDATE validate} mode.
 *
 */
public class IncompleteTaxonomyException extends RuntimeException {

	private static final long serialVersionUID = -8805505705355299796L;
	
	private Collection<Pair<String, String>> incompleteNodePairs;
	
	/**
	 * Creates a new instance with a collection of erroneous node pairs.
	 */
	public IncompleteTaxonomyException(final Collection<Pair<String, String>> incompleteNodePairs) {
		this.incompleteNodePairs = newHashSet(checkNotNull(incompleteNodePairs, "incompleteNodePairs"));
	}
	
	/**
	 * Returns with a collection erroneous node pairs. 
	 */
	public Collection<Pair<String, String>> getIncompleteNodePairs() {
		return incompleteNodePairs;
	}

}