/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.commons.options.Options;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.commitinfo.CommitInfo;
import com.b2international.snowowl.datastore.commitinfo.CommitInfoConverter;
import com.b2international.snowowl.datastore.commitinfo.CommitInfoDocument;

/**
 * @since 5.2
 */
public class CommitInfoGetRequest extends GetRequest<CommitInfo, CommitInfoDocument> {

	private static final long serialVersionUID = 1L;

	protected CommitInfoGetRequest() {
		super("Commit Info");
	}

	@Override
	protected Class<CommitInfoDocument> getDocType() {
		return CommitInfoDocument.class;
	}
	
	@Override
	protected String getIdField() {
		return DocumentMapping._ID;
	}

	@Override
	protected CommitInfo process(final RepositoryContext context, final CommitInfoDocument doc, final Options expand) {
		return new CommitInfoConverter(context, expand(), locales()).convert(doc);
	}

}
