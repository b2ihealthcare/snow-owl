/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.commons.json.Json;
import com.b2international.index.Searcher;
import com.b2international.index.migrate.DocumentMappingMigrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

/**
 * @since 9.0.0
 */
public class ResourceDocumentSchemaMigratorV9_0 implements DocumentMappingMigrator {

	@Override
	public void init(Searcher searcher) {
	}

	@Override
	public ObjectNode migrate(ObjectNode oldDocument, ObjectMapper mapper) {

		// additions
		oldDocument.put("hidden", false);

		// changes
		final List<Json> dependencies = newArrayList();

		final String extensionOf = oldDocument.path("extensionOf").textValue();

		if (!Strings.isNullOrEmpty(extensionOf)) {

			dependencies.add(Json.object("scope", "extensionOf", "uri", extensionOf));
			oldDocument.remove("extensionOf");

		}

		final String domain = oldDocument.path("settings").path("domain").textValue();

		if (!Strings.isNullOrEmpty(domain)) {

			dependencies.add(Json.object("scope", "domain", "uri", domain));
			((ObjectNode) oldDocument.get("settings")).remove("domain");

		}

		final String sourceDomain = oldDocument.path("settings").path("sourceDomain").textValue();

		if (!Strings.isNullOrEmpty(sourceDomain)) {

			dependencies.add(Json.object("scope", "source", "uri", sourceDomain));
			((ObjectNode) oldDocument.get("settings")).remove("sourceDomain");

		}

		final String targetDomain = oldDocument.path("settings").path("targetDomain").textValue();

		if (!Strings.isNullOrEmpty(targetDomain)) {

			dependencies.add(Json.object("scope", "target", "uri", targetDomain));
			((ObjectNode) oldDocument.get("settings")).remove("targetDomain");

		}

		if (!dependencies.isEmpty() && !oldDocument.hasNonNull("dependencies")) {
			oldDocument.set("dependencies", mapper.valueToTree(dependencies));
		}

		// removals

		if (oldDocument.path("settings").has("experimental")) {
			((ObjectNode) oldDocument.get("settings")).remove("experimental");
		}

		if (oldDocument.path("settings").has("defaultQueryTypeRefsetId")) {
			((ObjectNode) oldDocument.get("settings")).remove("defaultQueryTypeRefsetId");
		}

		if (oldDocument.path("settings").has("defaultRefsetModuleId")) {
			((ObjectNode) oldDocument.get("settings")).remove("defaultRefsetModuleId");
		}

		return oldDocument;

	}

}
