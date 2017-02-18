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
package com.b2international.snowowl.datastore.history;

import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
import static java.util.Collections.emptyList;

import java.util.Collection;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;

/**
 * Representation of a historical information details builder for terminology
 * and content independent components.
 *
 */
public interface HistoryInfoDetailsBuilder extends TerminologyComponentIdProvider {

	/**
	 * Builds detailed history information from a passed in {@link CDOCommitInfo} instance.
	 * @param currentView for dirty and new objects.
	 * @param beforeView for detached objects. 
	 * @param commitInfo the passed in CDO commit information.
	 * @param configuration 
	 * @return the detailed history informations.
	 */
	Collection<IHistoryInfoDetails> buildDetails(final CDOView currentView, final CDOView beforeView, final CDOCommitInfo commitInfo, HistoryInfoConfiguration configuration);
	
	/**
	 * No-operation instance. Does nothing.
	 */
	HistoryInfoDetailsBuilder NOOP = new HistoryInfoDetailsBuilder() {
		
		@Override
		public Collection<IHistoryInfoDetails> buildDetails(final CDOView currentView, 
				final CDOView beforeView, final CDOCommitInfo commitInfo, HistoryInfoConfiguration configuration) {
			
			return emptyList();
		};
		
		@Override
		public short getTerminologyComponentId() {
			return UNSPECIFIED_NUMBER_SHORT;
		};
	};
	
}