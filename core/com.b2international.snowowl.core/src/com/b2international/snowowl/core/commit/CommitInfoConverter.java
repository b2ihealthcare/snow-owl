/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.CommitDetail;
import com.b2international.snowowl.core.commit.CommitInfo.Builder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

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
	protected CommitInfos createCollectionResource(final List<CommitInfo> results, String searchAfter, final int limit, final int total) {
		return new CommitInfos(results, searchAfter, limit, total);
	}

	@Override
	protected CommitInfo toResource(final Commit doc) {
		final Builder builder = CommitInfo.builder(doc);
		
		// expand details if requested
		if (expand().containsKey(CommitInfo.Expand.DETAILS)) {
			final Options detailsExpandOptions = expand().get(CommitInfo.Expand.DETAILS, Options.class);
			final Collection<CommitDetail> commitDetails = getCommitDetails(doc, detailsExpandOptions);
			final List<CommitInfoDetail> commitInfoDetails = new ArrayList<>();
			
			// make sure we gc each commit info detail as soon as we can to free up memory faster
			for (CommitDetail info : Iterables.consumingIterable(commitDetails)) {
				toCommitInfoDetail(info).forEach(commitInfoDetails::add);
			}
			builder.details(new CommitInfoDetails(commitInfoDetails, null, commitInfoDetails.size(), commitInfoDetails.size()));
		}
		
		return builder.build();
	}

	private Collection<CommitDetail> getCommitDetails(final Commit commit, Options detailsExpandOptions) {
		// use the filter defined affectedComponentId if present
		final String affectedComponentId = getDetailsAffectedComponentId(filters, detailsExpandOptions);
		if (!Strings.isNullOrEmpty(affectedComponentId)) {
			return commit.getDetailsByObject(affectedComponentId);
		} else {
			return commit.getDetails();
		}
	}

	private String getDetailsAffectedComponentId(Options filters, Options expandOptions) {
		checkNotNull(filters, "At least one filter source must be defined");
		// prefer details() expand options first, then outer affectedComponentId filter
		if (expandOptions.containsKey("affectedComponentId")) {
			return expandOptions.getString("affectedComponentId");
		} else if (filters.containsKey(CommitInfoSearchRequest.OptionKey.AFFECTED_COMPONENT_ID.name())) {
			return filters.getString(CommitInfoSearchRequest.OptionKey.AFFECTED_COMPONENT_ID.name());
		} else {
			return null;
		}
	}

	private Stream<CommitInfoDetail> toCommitInfoDetail(CommitDetail detail) {

		final CommitInfoDetail.Builder info = CommitInfoDetail.builder()
				.changeKind(getChangeKind(detail))
				.property(detail.getProp())
				.objectType(detail.getObjectType());
		
		if (detail.isPropertyChange()) {
			info.fromValue(detail.getFrom())
				.value(detail.getTo());
			
			// for each changed object for this prop report a separate object
			return detail.getObjects()
					.stream()
					.map(object -> info.object(object).build());
		}
		
		final List<CommitInfoDetail> details = newArrayListWithExpectedSize(detail.getObjects().size());
		for (int i = 0; i < detail.getObjects().size(); i++) {
			final String object = detail.getObjects().get(i);
			info.objectType(detail.getObjectType());
			info.object(object); // set the object
			
			final Set<String> components = detail.getComponents().get(i);
			components.forEach(component -> details.add(info.valueType(detail.getComponentType())
					.value(component)
					.build())); // create change detail for each child ID
		}
		return details.stream();
	}

	private ChangeKind getChangeKind(CommitDetail indexDetail) {
		switch (indexDetail.getOp()) {
		case ADD: return ChangeKind.ADDED;
		case REMOVE: return ChangeKind.DELETED;
		case CHANGE: return ChangeKind.UPDATED;
		default: return ChangeKind.UNCHANGED;
		}
	}
}
