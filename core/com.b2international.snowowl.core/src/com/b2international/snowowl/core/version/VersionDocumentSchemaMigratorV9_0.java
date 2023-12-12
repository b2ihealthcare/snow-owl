/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.version;

import java.io.IOException;

import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.migrate.DocumentMappingMigrator;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

/**
 * @since 9.0.0
 */
public class VersionDocumentSchemaMigratorV9_0 implements DocumentMappingMigrator {

	private Searcher searcher;
	private RevisionBranch mainBranch;

	@Override
	public void init(Searcher searcher) {
		this.searcher = searcher;
		try {
			mainBranch = searcher.get(RevisionBranch.class, RevisionBranch.MAIN_PATH);
		} catch (final IOException e) {
			throw new IndexException("Failed to retrieve revision branch during schema migration", e);
		}
	}

	@Override
	public ObjectNode migrate(ObjectNode source, ObjectMapper mapper) {

		final long createdAt = source.get("createdAt").longValue();
		final String resourceId = source.get("resourceId").textValue();

		final JsonNode dependencies = Query.select(JsonNode.class)
			.from(ResourceDocument.class)
			.where(Expressions.bool()
				.filter(Revision.Expressions.id(resourceId))
				.filter(mainBranch.ref().restrictTo(createdAt).toRevisionFilter())
				.build()
			)
			.limit(1)
			.build()
			.search(searcher)
			.first()
			.get("dependencies");

		if (dependencies != null) {
			source.set("dependencies", dependencies);
		}

		// removals

		final String domain = source.path("settings").path("domain").textValue();

		if (!Strings.isNullOrEmpty(domain)) {
			((ObjectNode) source.get("settings")).remove("domain");
		}

		final String sourceDomain = source.path("settings").path("sourceDomain").textValue();

		if (!Strings.isNullOrEmpty(sourceDomain)) {
			((ObjectNode) source.get("settings")).remove("sourceDomain");
		}

		final String targetDomain = source.path("settings").path("targetDomain").textValue();

		if (!Strings.isNullOrEmpty(targetDomain)) {
			((ObjectNode) source.get("settings")).remove("targetDomain");
		}

		if (source.path("settings").has("experimental")) {
			((ObjectNode) source.get("settings")).remove("experimental");
		}

		return source;

	}

}
