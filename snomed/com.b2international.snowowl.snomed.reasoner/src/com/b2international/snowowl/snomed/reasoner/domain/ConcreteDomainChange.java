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

/**
 * @since 7.0
 */
public final class ConcreteDomainChange implements Serializable {

	private ChangeNature changeNature;
	private SnomedReferenceSetMember concreteDomainMember;

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
		builder.append("ConcreteDomainChange [changeNature=");
		builder.append(changeNature);
		builder.append(", concreteDomainMember=");
		builder.append(concreteDomainMember);
		builder.append("]");
		return builder.toString();
	}
}
