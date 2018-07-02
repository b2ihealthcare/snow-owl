/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.io.Serializable;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.0
 */
public final class ConcreteDomainChange implements Serializable {

	/**
	 * Enumerates expandable property keys.
	 */
	public static final class Expand {
		public static final String CONCRETE_DOMAIN_MEMBER = "concreteDomainMember";
	}
	
	private String classificationId;
	private ChangeNature changeNature;
	private SnomedReferenceSetMember concreteDomainMember;

	@JsonIgnore
	public String getClassificationId() {
		return classificationId;
	}

	public void setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
	}

	public ChangeNature getChangeNature() {
		return changeNature;
	}

	public void setChangeNature(final ChangeNature changeNature) {
		this.changeNature = changeNature;
	}

	public SnomedReferenceSetMember getConcreteDomainMember() {
		return concreteDomainMember;
	}

	public void setConcreteDomainMember(final SnomedReferenceSetMember concreteDomainMember) {
		this.concreteDomainMember = concreteDomainMember;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConcreteDomainChange [classificationId=");
		builder.append(classificationId);
		builder.append(", changeNature=");
		builder.append(changeNature);
		builder.append(", concreteDomainMember=");
		builder.append(concreteDomainMember);
		builder.append("]");
		return builder.toString();
	}
}
