/*******************************************************************************
 * Copyright (c) 2022 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.request.version;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 8.8
 */
public class VersionConverter extends BaseResourceConverter<VersionDocument, Version, Versions> {

	protected VersionConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected RepositoryContext context() {
		return (RepositoryContext) super.context();
	}

	@Override
	protected Versions createCollectionResource(final List<Version> results, final String searchAfter, final int limit, final int total) {
		return new Versions(results, searchAfter, limit, total);
	}

	@Override
	protected Version toResource(final VersionDocument doc) {
		final Version version = new Version();
		version.setId(doc.getId());
		version.setVersion(doc.getVersion());
		version.setDescription(doc.getDescription());
		version.setEffectiveTime(doc.getEffectiveTimeAsLocalDate());
		version.setResource(doc.getResource());
		version.setBranchPath(doc.getBranchPath());
		version.setCreatedAt(doc.getCreatedAt());
		version.setAuthor(doc.getAuthor());
		version.setUrl(doc.getUrl());
		return version;
	}

	@Override
	public void expand(final List<Version> results) {

		if (results.isEmpty() || expand().isEmpty()) {
			return;
		}

		expandUpdatedAtCommit(results);

	}

	private void expandUpdatedAtCommit(final List<Version> results) {
		if (expand().containsKey(Version.Expand.UPDATED_AT_COMMIT)) {
			results.stream()
				.forEach(res -> {
					RepositoryRequests.commitInfos().prepareSearchCommitInfo()
						.one()
						.filterByBranch(Branch.MAIN_PATH) // all resource commits go to the main branch
						.filterByAffectedComponent(res.getId())
						.setFields(CommitInfo.Fields.DEAFULT_FIELD_SELECTION)
						.sortBy("timestamp:desc")
						.build()
						.execute(context())
						.first()
						.ifPresent(res::setUpdatedAtCommit);
				});
		}
	}

}
