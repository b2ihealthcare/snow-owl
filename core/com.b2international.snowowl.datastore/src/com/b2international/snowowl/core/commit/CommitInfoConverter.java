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
package com.b2international.snowowl.core.commit;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.CommitChange;
import com.b2international.snowowl.core.commit.CommitInfo.Builder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.2
 */
final class CommitInfoConverter extends BaseResourceConverter<Commit, CommitInfo, CommitInfos> {

	private final Options filters;

	public CommitInfoConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales, Options filters) {
		super(context, expand, locales);
		this.filters = filters;
	}

	@Override
	protected CommitInfos createCollectionResource(final List<CommitInfo> results, final String scrollId, Object[] searchAfter, final int limit, final int total) {
		return new CommitInfos(results, context().id(), scrollId, searchAfter, limit, total);
	}

	@Override
	protected CommitInfo toResource(final Commit doc) {
		final Builder builder = CommitInfo.builder(doc);
		
		// expand details if requested
		if (expand().containsKey(CommitInfo.Expand.DETAILS)) {
			final String affectedComponentId = filters.containsKey(CommitInfoSearchRequest.OptionKey.AFFECTED_COMPONENT) ? filters.getString(CommitInfoSearchRequest.OptionKey.AFFECTED_COMPONENT.name()) : ""; 
			final Collection<CommitChange> changes = Strings.isNullOrEmpty(affectedComponentId) ? doc.getChangesByContainer().values() : ImmutableList.of(doc.getChangesByContainer(affectedComponentId));
			// TODO traverse change tree and add all related CommitChanges
			final List<CommitInfoDetail> details = changes.stream()
					.map(change -> new CommitInfoDetail()) // TODO fill out detail with actual changes
					.collect(Collectors.toList());
			builder.details(new CommitInfoDetails(details, null, null, details.size(), details.size()));
		}
		
		return builder.build();
	}
	
}
