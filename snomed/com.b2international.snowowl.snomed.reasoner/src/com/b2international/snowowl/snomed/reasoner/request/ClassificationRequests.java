/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

/**
 * The main entry point of the classification API; contains factory methods for
 * request builders related to classification and OWL ontology export.
 * 
 * @since 5.7
 */
public abstract class ClassificationRequests {

	public static ClassificationCreateRequestBuilder prepareCreateClassification() {
		return new ClassificationCreateRequestBuilder();
	}

	public static ClassificationGetRequestBuilder prepareGetClassification(final String classificationId) {
		return new ClassificationGetRequestBuilder(classificationId);
	}

	public static ClassificationSearchRequestBuilder prepareSearchClassification() {
		return new ClassificationSearchRequestBuilder();
	}

	public static ClassificationSaveRequestBuilder prepareSaveClassification(final String classificationId) {
		return new ClassificationSaveRequestBuilder(classificationId);
	}

	public static ClassificationDeleteRequestBuilder prepareDeleteClassification(final String classificationId) {
		return new ClassificationDeleteRequestBuilder(classificationId);
	}

	public static EquivalentConceptSetSearchRequestBuilder prepareSearchEquivalentConceptSet() {
		return new EquivalentConceptSetSearchRequestBuilder();
	}

	public static ConcreteDomainChangeSearchRequestBuilder prepareSearchConcreteDomainChange() {
		return new ConcreteDomainChangeSearchRequestBuilder();
	}

	public static RelationshipChangeSearchRequestBuilder prepareSearchRelationshipChange() {
		return new RelationshipChangeSearchRequestBuilder();
	}

	public static OntologyExportRequestBuilder prepareExportOntology() {
		return new OntologyExportRequestBuilder();
	}

	public static ReasonerExtensionSearchRequestBuilder prepareSearchReasonerExtensions() {
		return new ReasonerExtensionSearchRequestBuilder();
	}

	private ClassificationRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
