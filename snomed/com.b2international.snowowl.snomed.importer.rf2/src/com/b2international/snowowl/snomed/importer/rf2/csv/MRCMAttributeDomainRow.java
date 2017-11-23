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
public class MRCMAttributeDomainRow extends RefSetRow {

	private String domainId;
	private boolean grouped;
	private String attributeCardinality;
	private String attributeInGroupCardinality;
	private String ruleStrengthId;
	private String contentTypeId;

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(final String domainId) {
		this.domainId = domainId;
	}

	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(final boolean grouped) {
		this.grouped = grouped;
	}

	public String getAttributeCardinality() {
		return attributeCardinality;
	}

	public void setAttributeCardinality(final String attributeCardinality) {
		this.attributeCardinality = attributeCardinality;
	}

	public String getAttributeInGroupCardinality() {
		return attributeInGroupCardinality;
	}

	public void setAttributeInGroupCardinality(final String attributeInGroupCardinality) {
		this.attributeInGroupCardinality = attributeInGroupCardinality;
	}

	public String getRuleStrengthId() {
		return ruleStrengthId;
	}

	public void setRuleStrengthId(final String ruleStrengthId) {
		this.ruleStrengthId = ruleStrengthId;
	}

	public String getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(final String contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	@Override
	public String toString() {
		return String.format(
				"MRCMAttributeDomainRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, referencedComponentId=%s, domainId=%s, "
						+ "grouped=%s, attributeCardinality=%s, attributeInGroupCardinality=%s, ruleStrengthId=%s, contentTypeId=%s ]",
						getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(), getReferencedComponentId(), getDomainId(), isGrouped(),
						getAttributeCardinality(), getAttributeInGroupCardinality(), getRuleStrengthId(), getContentTypeId());
	}

}
