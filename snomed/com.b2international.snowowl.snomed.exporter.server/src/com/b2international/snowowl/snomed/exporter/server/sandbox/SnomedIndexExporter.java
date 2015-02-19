/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Representation of a SNOMED&nbsp;CT exporter working on index directories.
 *
 */
public interface SnomedIndexExporter extends SnomedExporter {

	/**
	 * Returns with a set of fields that has to be load for the document.
	 * <p>Could be {@code null}. If {@code null} then all the fields will be
	 * loaded.
	 * @return a set of document field names or {@code null}.
	 */
	@Nullable Set<String> getFieldsToLoad();
	
	/**
	 * Returns with the query for the for the branch path argument that
	 * has to be run to get back the documents for the export process.
	 * @param branchPath the branch path.
	 * @return the query for the export.
	 */
	Query getExportQuery(final IBranchPath branchPath);
	
	/**
	 * Transforms the document argument into a serialized line of 
	 * attributes.
	 * @param doc the document to transform.
	 * @return a string as a serialized line in the export file.
	 */
	String transform(final Document doc);
	
}