/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.rules.ExternalResource;

import com.b2international.index.es.EsIndexClientFactory;
import com.b2international.index.es.EsNode;

/**
 * @since 8.0
 */
public final class SynonymsRule extends ExternalResource {

	private final Path synonymsFile;
	private final List<String> synonyms;

	private List<String> linesToRestore;
	
	public SynonymsRule(String...synonyms) {
		this(List.of(synonyms));
	}
	
	public SynonymsRule(List<String> synonyms) {
		this(EsIndexClientFactory.DEFAULT_PATH.resolve(IndexClientFactory.DEFAULT_CLUSTER_NAME).resolve(EsNode.CONFIG_DIR).resolve(EsNode.SYNONYMS_FILE), synonyms);
	}
	
	public SynonymsRule(Path synonymsFile, List<String> synonyms) {
		this.synonymsFile = synonymsFile;
		this.synonyms = synonyms;
	}
	
	@Override
	protected void before() throws Throwable {
		linesToRestore = Files.exists(synonymsFile) ? Files.readAllLines(synonymsFile) : Collections.emptyList();
		Files.createDirectories(synonymsFile.getParent());
		Files.write(synonymsFile, synonyms);
	}
	
	@Override
	protected void after() {
		try {
			Files.write(synonymsFile, linesToRestore);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
