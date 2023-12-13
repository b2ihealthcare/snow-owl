/*
 * Copyright 2019-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation;

import java.util.List;

import com.b2international.snowowl.core.ComponentIdentifier;

/**
 * @since 6.16
 */
public class ValidationIssueDetails {

	/** Detail key for highlighting information */
	public static final String DETAIL_HIGHLIGHT = "highlightDetails";

	/** Detail key for suggested action */
	public static final String DETAIL_ACTION = "suggestedAction";

	/** Detail key for components related to the suggested action */
	public static final String DETAIL_ACTION_COMPONENTS = "suggestedActionComponents";

	private final ComponentIdentifier affectedComponentId;

	private List<StylingDetail> stylingDetails;
	private SuggestedAction suggestedAction;
	private List<SuggestedComponent> suggestedComponents;

	public ValidationIssueDetails(final ComponentIdentifier affectedComponentId) {
		this.affectedComponentId = affectedComponentId;
	}

	public ComponentIdentifier getAffectedComponentId() {
		return affectedComponentId;
	}

	public List<StylingDetail> getStylingDetails() {
		return stylingDetails;
	}

	public void setStylingDetails(final List<StylingDetail> stylingDetails) {
		this.stylingDetails = stylingDetails;
	}

	public SuggestedAction getSuggestedAction() {
		return suggestedAction;
	}

	public void setSuggestedAction(final SuggestedAction suggestedAction) {
		this.suggestedAction = suggestedAction;
	}

	public List<SuggestedComponent> getSuggestedComponents() {
		return suggestedComponents;
	}

	public void setSuggestedComponents(final List<SuggestedComponent> suggestedComponents) {
		this.suggestedComponents = suggestedComponents;
	}
}
