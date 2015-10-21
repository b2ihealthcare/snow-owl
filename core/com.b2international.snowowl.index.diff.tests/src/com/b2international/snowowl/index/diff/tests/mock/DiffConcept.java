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
package com.b2international.snowowl.index.diff.tests.mock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A minimal representation of an indexed concept, to be used in index-based diff tests.
 * 
 * @since 4.3
 */
public class DiffConcept {

	private final String id;
	private final String label;

	public DiffConcept(final String id, final String label) {
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return String.format("%s | %s |", id, label);
	}
}
