/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.List;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;

/**
 * @since 6.14
 */
public final class SnomedOWLExpressionConverterResult {

	public static final SnomedOWLExpressionConverterResult EMPTY = new SnomedOWLExpressionConverterResult(null, null);
	
	private final List<SnomedOWLRelationshipDocument> classAxiomRelationships;
	private final List<SnomedOWLRelationshipDocument> gciAxiomRelationships;

	public SnomedOWLExpressionConverterResult(List<SnomedOWLRelationshipDocument> classAxiomRelationships, List<SnomedOWLRelationshipDocument> gciAxiomRelationships) {
		this.classAxiomRelationships = classAxiomRelationships;
		this.gciAxiomRelationships = gciAxiomRelationships;
	}
	
	public List<SnomedOWLRelationshipDocument> getClassAxiomRelationships() {
		return classAxiomRelationships;
	}
	
	public List<SnomedOWLRelationshipDocument> getGciAxiomRelationships() {
		return gciAxiomRelationships;
	}
	
}
