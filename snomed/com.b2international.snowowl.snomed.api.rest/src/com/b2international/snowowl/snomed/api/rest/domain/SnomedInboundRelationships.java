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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.List;

import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;

/**
 * @since 1.0
 */
public class SnomedInboundRelationships {

	private List<ISnomedRelationship> inboundRelationships;
	private int total;

	public List<ISnomedRelationship> getInboundRelationships() {
		return inboundRelationships;
	}

	public int getTotal() {
		return total;
	}

	public void setInboundRelationships(final List<ISnomedRelationship> inboundRelationships) {
		this.inboundRelationships = inboundRelationships;
	}

	public void setTotal(final int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedInboundRelationships [inboundRelationships=");
		builder.append(inboundRelationships);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}
}