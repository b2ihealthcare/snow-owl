/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;

/**
 * @since 1.0
 */
public class SnomedOutboundRelationships {

	private List<SnomedRelationship> outboundRelationships;
	private int total;

	public List<SnomedRelationship> getOutboundRelationships() {
		return outboundRelationships;
	}

	public int getTotal() {
		return total;
	}

	public void setOutboundRelationships(final List<SnomedRelationship> outboundRelationships) {
		this.outboundRelationships = outboundRelationships;
	}

	public void setTotal(final int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedOutboundRelationships [outboundRelationships=");
		builder.append(outboundRelationships);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}
}