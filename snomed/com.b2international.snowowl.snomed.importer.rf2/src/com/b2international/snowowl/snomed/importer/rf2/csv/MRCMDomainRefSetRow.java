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
package com.b2international.snowowl.snomed.importer.rf2.csv;

/**
 * @since 5.10.19
 */
public class MRCMDomainRefSetRow extends RefSetRow {

	private String domainConstraint;
	private String parentDomain;
	private String proximalPrimitiveConstraint;
	private String proximalPrimitiveRefinement;
	private String domainTemplateForPrecoordination;
	private String domainTemplateForPostcoordination;
	private String editorialGuideReference;

	public String getDomainConstraint() {
		return domainConstraint;
	}

	public void setDomainConstraint(final String domainConstraint) {
		this.domainConstraint = domainConstraint;
	}

	public String getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(final String parentDomain) {
		this.parentDomain = parentDomain;
	}

	public String getProximalPrimitiveConstraint() {
		return proximalPrimitiveConstraint;
	}

	public void setProximalPrimitiveConstraint(final String proximalPrimitiveConstraint) {
		this.proximalPrimitiveConstraint = proximalPrimitiveConstraint;
	}

	public String getProximalPrimitiveRefinement() {
		return proximalPrimitiveRefinement;
	}

	public void setProximalPrimitiveRefinement(final String proximalPrimitiveRefinement) {
		this.proximalPrimitiveRefinement = proximalPrimitiveRefinement;
	}

	public String getDomainTemplateForPrecoordination() {
		return domainTemplateForPrecoordination;
	}

	public void setDomainTemplateForPrecoordination(final String domainTemplateForPrecoordination) {
		this.domainTemplateForPrecoordination = domainTemplateForPrecoordination;
	}

	public String getDomainTemplateForPostcoordination() {
		return domainTemplateForPostcoordination;
	}

	public void setDomainTemplateForPostcoordination(final String domainTemplateForPostcoordination) {
		this.domainTemplateForPostcoordination = domainTemplateForPostcoordination;
	}

	public String getEditorialGuideReference() {
		return editorialGuideReference;
	}

	public void setEditorialGuideReference(final String editorialGuideReference) {
		this.editorialGuideReference = editorialGuideReference;
	}

	@Override
	public String toString() {
		return String.format(
				"MRCMDomainRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, referencedComponentId=%s, domainConstraint=%s, "
						+ "parentDomain=%s, proximalPrimitiveConstraint=%s, proximalPrimitiveRefinement=%s, domainTemplateForPrecoordination=%s, "
						+ "domainTemplateForPostcoordination=%s, editorialGuideReference=%s ]",
						getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(), getReferencedComponentId(), getDomainConstraint(),
						getParentDomain(), getProximalPrimitiveConstraint(), getProximalPrimitiveRefinement(), getDomainTemplateForPrecoordination(),
						getDomainTemplateForPostcoordination(), getEditorialGuideReference());
	}

}
