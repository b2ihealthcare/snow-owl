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
package com.b2international.snowowl.snomed.importer.rf2;

import java.util.Date;
import java.util.List;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class Rf2RefSetMember extends UnsupportedRf2RefSetMember {

	private List<String> record;
	private SnomedRefSet refSet;
	private EClass eClass;

	public Rf2RefSetMember(List<String> rf2Row, SnomedRefSet refSet, long storageKey) {
		super(storageKey);
		this.record = rf2Row;
		this.refSet = refSet;
		this.eClass = getMemberClass(refSet);
	}
	
	private static EClass getMemberClass(SnomedRefSet refSet) {
		switch (refSet.getType()) {
		case ASSOCIATION: return SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER;
		case EXTENDED_MAP:
		case COMPLEX_MAP: return SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER;
		case ATTRIBUTE_VALUE: return SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER;
		case CONCRETE_DATA_TYPE: return SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER;
		case DESCRIPTION_TYPE: return SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER;
		case LANGUAGE: return SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER;
		case MODULE_DEPENDENCY: return SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER;
		case QUERY: return SnomedRefSetPackage.Literals.SNOMED_QUERY_REF_SET_MEMBER;
		case SIMPLE: return SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER;
		case SIMPLE_MAP: return SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER;
		case OWL_AXIOM: return SnomedRefSetPackage.Literals.SNOMED_ANNOTATION_REF_SET_MEMBER;
		default: throw new UnsupportedOperationException("Unknown refset: " + refSet.getType());
		}
	}

	@Override
	public EClass eClass() {
		return eClass;
	}
	
	@Override
	public short getReferencedComponentType() {
		return SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(getReferencedComponentId());
	}

	@Override
	public Date getEffectiveTime() {
		return getEffectiveTime(1);
	}

	@Override
	public boolean isSetEffectiveTime() {
		return !Strings.isNullOrEmpty(record.get(1));
	}

	@Override
	public boolean isActive() {
		return "1".equals(record.get(2));
	}

	@Override
	public SnomedRefSet getRefSet() {
		return refSet;
	}

	@Override
	public boolean isReleased() {
		return true;
	}

	@Override
	public String getReferencedComponentId() {
		return record.get(5);
	}

	@Override
	public String getModuleId() {
		return record.get(3);
	}

	@Override
	public String getRefSetIdentifierId() {
		return record.get(4);
	}

	@Override
	public String getUuid() {
		return record.get(0);
	}

	@Override
	public String getTargetComponentId() {
		return record.get(6);
	}

	@Override
	public String getValueId() {
		return record.get(6);
	}
	
	@Override
	public String getMapTargetComponentId() {
		switch (getRefSet().getType()) {
		case SIMPLE_MAP:
			return record.get(6);
		case EXTENDED_MAP:
		case COMPLEX_MAP:
			return record.get(10);
		default: throw new UnsupportedOperationException();
		}
	}

	@Override
	public short getMapTargetComponentType() {
		return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
	}

	@Override
	public String getMapTargetComponentDescription() {
		if (record.size() > 7) {
			return record.get(7);
		}
		return null;
	}

	@Override
	public int getMapGroup() {
		return Integer.parseInt(record.get(6));
	}

	@Override
	public int getMapPriority() {
		return Integer.parseInt(record.get(7));
	}

	@Override
	public String getMapRule() {
		return record.get(8);
	}

	@Override
	public String getMapAdvice() {
		return record.get(9);
	}

	@Override
	public String getCorrelationId() {
		return record.get(11);
	}

	@Override
	public String getMapCategoryId() {
		if (record.size() > 12) {
			return record.get(12);
		}
		return null;
	}

	@Override
	public String getDescriptionFormat() {
		return record.get(6);
	}
	
	@Override
	public int getDescriptionLength() {
		return Integer.parseInt(record.get(7));
	}
	
	@Override
	public String getAcceptabilityId() {
		return record.get(6);
	}
	
	@Override
	public Date getSourceEffectiveTime() {
		return getEffectiveTime(6);
	}
	
	@Override
	public Date getTargetEffectiveTime() {
		return getEffectiveTime(7);
	}
	
	@Override
	public String getCharacteristicTypeId() {
		if (record.size() > 10) {
			return record.get(10);
		}
		return null;
	}
	
	@Override
	public String getUomComponentId() {
		return record.get(6);
	}
	
	@Override
	public String getOperatorComponentId() {
		return record.get(7);
	}
	
	@Override
	public String getSerializedValue() {
		return record.size() > 10 ? record.get(9) : record.get(8);
	}
	
	@Override
	public DataType getDataType() {
		return SnomedRefSetUtil.getDataType(getRefSetIdentifierId());
	}
	
	@Override
	public String getLabel() {
		return record.get(8);
	}

	private Date getEffectiveTime(int index) {
		final String effectiveTime = record.get(index);
		if (!Strings.isNullOrEmpty(effectiveTime)) {
			return EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
		} else {
			return null;
		}
	}

}
