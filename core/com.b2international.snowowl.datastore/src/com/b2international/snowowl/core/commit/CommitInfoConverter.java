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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.CommitDetail;
import com.b2international.snowowl.core.commit.CommitInfo.Builder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.google.common.base.Strings;

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
			final Collection<CommitDetail> changes = Strings.isNullOrEmpty(affectedComponentId) ? doc.getDetails() : doc.getDetailsByObject(affectedComponentId);
			final List<CommitInfoDetail> details = changes.stream()
					.flatMap(info -> toCommitInfoDetail(info))
					.collect(Collectors.toList());
			builder.details(new CommitInfoDetails(details, null, null, details.size(), details.size()));
		}
		
		return builder.build();
	}

	private Stream<CommitInfoDetail> toCommitInfoDetail(CommitDetail detail) {
		// for each object report a different change detail object
		final CommitInfoDetail.Builder info = CommitInfoDetail.builder()
				.changeKind(getChangeKind(detail));
		if (detail.isPropertyChange()) {
			info
				.property(detail.getProp())
				.fromValue(detail.getFrom())
				.value(detail.getTo());
			// for each changed object for this prop report a separate object
			return detail.getObjects()
					.stream()
					.map(object -> info.object(object).build());
		} else {
			info.property(CommitInfoDetail.COMPONENT);
			final List<CommitInfoDetail> details = newArrayListWithExpectedSize(detail.getObjects().size());
			for (int i = 0; i < detail.getObjects().size(); i++) {
				final String object = detail.getObjects().get(i);
				final Set<String> children = detail.getChildren().get(i);
				info.object(object); // set the object
				children.forEach(child -> details.add(info.value(child).build())); // create change detail for each child ID
			}
			return details.stream();
		}
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
