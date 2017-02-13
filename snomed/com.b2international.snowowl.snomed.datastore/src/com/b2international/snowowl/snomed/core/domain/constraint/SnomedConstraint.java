/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import com.b2international.snowowl.core.domain.BaseComponent;

/**
 * @since 5.7
 */
public abstract class SnomedConstraint extends BaseComponent {

	private String domain;
	private int minCardinality;
	private int maxCardinality;
	
	public final String getDomain() {
		return domain;
	}
	
	public final int getMinCardinality() {
		return minCardinality;
	}
	
	public final int getMaxCardinality() {
		return maxCardinality;
	}
	
	public final void setDomain(String domain) {
		this.domain = domain;
	}
	
	public final void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}
	
	public final void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}
	
}
