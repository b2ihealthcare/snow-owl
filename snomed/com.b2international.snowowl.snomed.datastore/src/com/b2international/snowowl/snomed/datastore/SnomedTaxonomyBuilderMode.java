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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.commons.Pair.IdenticalPair.identicalPairOf;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.b2international.commons.Pair;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;

/**
 * Enumeration of {@link ISnomedTaxonomyBuilder} modes.
 *
 */
public enum SnomedTaxonomyBuilderMode {
	
	/**
	 * Default taxonomy builder mode. When the taxonomy is running in this mode
	 * it operates in a fail-fast mode. It means a runtime exception is
	 * thrown during the build phase if the taxonomy is incomplete.
	 */
	DEFAULT {
		@Override
		public Pair<String, String> handleMissingSource(final long sourceId, final long destinationId) {
			final String msg = "Cannot find source concept with ID: '" + sourceId + "'. Destination concept ID was: '" + destinationId + "'.";
			LOGGER.error(msg);
			throw new IllegalStateException(msg);
		}
		
		@Override
		public Pair<String, String> handleMissingDestination(long sourceId, long destinationId) {
			final String msg = "Cannot find destination concept with ID: '" + destinationId + "'. Source concept ID was: '" + sourceId + "'.";
			LOGGER.error(msg);
			throw new IllegalStateException(msg);
		}
	},
	
	/**
	 * Validation mode means that the taxonomy builder will not get build if the 
	 * taxonomy is incomplete. Instead a runtime exception will be thrown 
	 * at the very end of the processing and before the build phase containing
	 * all erroneous nodes and edges.
	 */
	VALIDATE {
		@Override
		public Pair<String, String> handleMissingSource(final long sourceId, final long destinationId) {
			return identicalPairOf(Long.toString(sourceId), Long.toString(destinationId));
		}
		
		@Override
		public Pair<String, String> handleMissingDestination(final long sourceId, final long destinationId) {
			return identicalPairOf(Long.toString(sourceId), Long.toString(destinationId));
		}
	};
	
	private static final Logger LOGGER = getLogger(SnomedTaxonomyBuilderMode.class); 
	
	/**
	 * Handles the case when the source concept is missing from the taxonomy.
	 * @param sourceId the source concept ID.
	 * @param destinationId the destination concept ID.
	 * @return a pair of IDs between the conflicting nodes. The left value is the source the right is the
	 * destination. 
	 */
	public abstract Pair<String, String> handleMissingSource(final long sourceId, final long destinationId);
	
	/**
	 * Handles the case when the destination concept is missing from the taxonomy.
	 * @param sourceId the source concept ID.
	 * @param destinationId the destination concept ID.
	 * @return a pair of IDs between the conflicting nodes. The left value is the source the right is the
	 * destination. 
	 */
	public abstract Pair<String, String> handleMissingDestination(final long sourceId, final long destinationId);
	
}