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
package com.b2international.snowowl.snomed.core.refset;

/**
 * Model for simple type refset to simple type refset derivation.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SimpleTypeRefSetDerivationModel {

	public enum DeriveType {
		DESCRIPTION("Create simple type reference set with referenced component type description."),
		RELATIONSHIP("Create simple type reference set with referenced component type relationship."),
		BOTH("Create duo simple type reference sets.");

		private final String label;

		private DeriveType(final String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	private String refSetName;
	private DeriveType deriveType;

	public SimpleTypeRefSetDerivationModel() {
		refSetName = "";
		deriveType = DeriveType.DESCRIPTION;
	}

	public String getRefSetName() {
		return refSetName;
	}

	public void setRefSetName(String refSetName) {
		this.refSetName = refSetName;
	}

	public DeriveType getDeriveType() {
		return deriveType;
	}

	public void setDeriveType(DeriveType deriveType) {
		this.deriveType = deriveType;
	}
}