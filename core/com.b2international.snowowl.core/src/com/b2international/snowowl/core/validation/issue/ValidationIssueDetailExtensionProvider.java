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
package com.b2international.snowowl.core.validation.issue;

import java.util.Collection;

import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.snowowl.core.SnowOwl.InitializationException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public enum ValidationIssueDetailExtensionProvider {

	INSTANCE;
	
	private final Collection<ValidationIssueDetailExtension> extensions = Lists.newArrayList();
	
	private ValidationIssueDetailExtensionProvider() {
		ClassPathScanner.INSTANCE.getComponentsByInterface(ValidationIssueDetailExtension.class).forEach(this::addExtension);
	}
	
	public ValidationIssueDetailExtension getExtensions(String toolingId) {
		return extensions.stream()
			.filter(ext -> toolingId.equals(ext.getToolingId()))
			.findFirst()
			.orElseThrow(() -> new UnsupportedOperationException("Unsupported tooling id: " + toolingId));
	}
	
	public Collection<ValidationIssueDetailExtension> getExtensions() {
		return extensions;
	}
	
	/**
	 * Usage of this function is intended for testing purposes only
	 */
	public void addExtension(ValidationIssueDetailExtension extension) {
		if (Strings.isNullOrEmpty(extension.getToolingId())) {
			throw new InitializationException(extension.getClass().getName() + " must provide a known toolingID.");
		}
		extensions.add(extension);
	}
	
}