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
package com.b2international.snowowl.snomed.exporter.server.exporter;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * Representation of a SNOMED&nbsp;CT exporter hiding Lucene index store.
 *
 */
public interface SnomedIndexExporter<T extends SnomedDocument> extends SnomedExporter {

	/**
	 * Returns with the query expression for the for the branch path argument that
	 * has to be run to get back the documents for the export process.
	 * @param branchPath the branch path.
	 * @return the query expression for the export.
	 */
	Expression getExportQuery(final IBranchPath branchPath);
	
	/**
	 * Transforms the SNOMED CT document index representation argument into a serialized line of 
	 * attributes.
	 * @param the SNOMED CT document to transform.
	 * @return a string as a serialized line in the export file.
	 */
	String transform(final T snomedDocument);
	
}