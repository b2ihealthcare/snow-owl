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
package com.b2international.snowowl.snomed.mrcm.core.configuration;

import java.io.Serializable;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.preferences.AbstractEntrySetting;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.mrcm.mini.DataTypeConstants;
import com.b2international.snowowl.snomed.mrcm.mini.SectionType;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("attributeOrderConfiguration")
public final class AttributeOrderConfiguration extends AbstractEntrySetting implements Comparable<AttributeOrderConfiguration>, Serializable {
	
	public static AttributeOrderConfiguration create(final String refSetId, final int priority, final String id, final AttributeType attributeType, final SectionType sectionType) {
		return new AttributeOrderConfiguration(refSetId, priority, id, attributeType, sectionType);
	}
	
	public static AttributeOrderConfiguration create(final String refSetId, final int priority, final DataTypeConstants constant, final AttributeType attributeType, final SectionType sectionType) {
		return new AttributeOrderConfiguration(refSetId, priority, constant.getId(), attributeType, sectionType);
	}
	
	@XStreamAlias("priority")
	private int priority;
	@XStreamAlias("id")
	private String id;
	@XStreamAlias("attributeType")
	private AttributeType attributeType;
	@XStreamAlias("sectionType")
	private SectionType sectionType;

	/**
	 * Default constructor for serialization.
	 */
	protected AttributeOrderConfiguration() {
		super();
	}
	
	public AttributeOrderConfiguration(final String refSetId, final int priority, final String id, final AttributeType attributeType, final SectionType sectionType) {
		super(refSetId+"_"+id);
		this.priority = priority;
		this.id = id;
		this.attributeType = attributeType;
		this.sectionType = sectionType;
	}

	public int getPriority() {
		return priority;
	}

	public String getId() {
		return id;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public SectionType getSectionType() {
		return sectionType;
	}

	@Override
	public int compareTo(final AttributeOrderConfiguration o) {
		final int value = sectionType.getPriority() - o.getSectionType().getPriority();
		return 0 != value ? value : this.getPriority() - o.getPriority();
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String label; 
		if (AttributeType.CONCRETE_DATA_TYPE.equals(attributeType)) {
			label = DataTypeUtils.getDefaultDataTypeLabel(id);
		} else {
			label = getText(id); 
		}
		return sb.append(label).append(" [").append(priority).append("; ").append(attributeType).append("; ").append(sectionType).append("]").toString();
	}

	private String getText(String conceptId) {
		final String label = ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), conceptId);
		return label != null ? label : conceptId;
	}
}
