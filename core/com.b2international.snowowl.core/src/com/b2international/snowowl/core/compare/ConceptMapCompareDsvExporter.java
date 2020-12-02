/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.ConceptMapMapping;

/**
 * @since 7.13
 */
public final class ConceptMapCompareDsvExporter {
	
	private static final String DELIMITER = ";";
	
	public static File export(final List<ConceptMapCompareResultItem> items, final Set<ConceptMapCompareChangeKind> changeKinds, final List<String> headers) throws IOException {
		final File file = File.createTempFile("conceptMapCompare-", ".txt");
		try (FileWriter writer = new FileWriter(file)) {
			
			if (headers != null && !headers.isEmpty()) {
				writer.write(header(headers));
				writer.write("\n");
			}
			
			final List<ConceptMapCompareResultItem> consideredItems = items.stream().filter(item -> changeKinds.contains(item.getChangeKind())).collect(Collectors.toList());
			
			for (final ConceptMapCompareResultItem item : consideredItems) {
				writer.write(line(item));
				writer.write("\n");
			}
		} catch (Exception e) {
			throw e;
		}
		
		return file;
	}
	
	private static String header(final List<String> headers) {
		return String.join(DELIMITER, headers);
	}
	
	public static String line(final ConceptMapCompareResultItem item) {
		final ConceptMapMapping mapping = item.getMapping();
		return String.join(DELIMITER,
				item.getChangeKind().toString(),
				ConceptMapCompareChangeKind.SAME.equals(item.getChangeKind()) ? "Both" : mapping.getContainerTerm(),
				mapping.getSourceComponentURI().codeSystem(),
				mapping.getSourceComponentURI().identifier(),
				mapping.getSourceTerm(),
				mapping.getTargetComponentURI().codeSystem(),
				mapping.getTargetComponentURI().identifier(),
				mapping.getTargetTerm());
	}
	
}
