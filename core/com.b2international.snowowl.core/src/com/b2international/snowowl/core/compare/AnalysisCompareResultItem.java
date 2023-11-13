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
package com.b2international.snowowl.core.compare;

/**
 * @since 9.0.0
 */
public record AnalysisCompareResultItem(String id, String label, String iconId, AnalysisCompareChangeKind changeKind, Boolean auxiliary) {

	// Short constructor for label and icon-less items
	public AnalysisCompareResultItem(final String id, final AnalysisCompareChangeKind changeKind) {
		this(id, null, null, changeKind, false);
	}

	// Short constructor for icon-less items
	public AnalysisCompareResultItem(final String id, final String label, final AnalysisCompareChangeKind changeKind) {
		this(id, label, null, changeKind, false);
	}
	
	// Short constructor for non-auxiliary items
	public AnalysisCompareResultItem(final String id, final String label, final String iconId, final AnalysisCompareChangeKind changeKind) {
		this(id, label, iconId, changeKind, false);
	}
	
}
