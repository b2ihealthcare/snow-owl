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

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;

/**
 * @since 5.2
 */
public final class CommitInfoConverter extends BaseResourceConverter<Commit, CommitInfo, CommitInfos> {

	public CommitInfoConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected CommitInfos createCollectionResource(final List<CommitInfo> results, final String scrollId, Object[] searchAfter, final int limit, final int total) {
		return new CommitInfos(results, context().id(), scrollId, searchAfter, limit, total);
	}

	@Override
	protected CommitInfo toResource(final Commit doc) {
		return CommitInfo.builder(doc).build();
	}
	
}
