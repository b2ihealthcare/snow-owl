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

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.3
 */
public abstract class UnsupportedRf2RefSetMember extends FakeEObjectImpl implements SnomedRefSetMember, SnomedAssociationRefSetMember, SnomedAttributeValueRefSetMember, SnomedComplexMapRefSetMember, SnomedDescriptionTypeRefSetMember, SnomedLanguageRefSetMember, SnomedModuleDependencyRefSetMember, SnomedConcreteDataTypeRefSetMember {

	public UnsupportedRf2RefSetMember(long storageKey) {
		super(storageKey);
	}

	@Override
	public void setEffectiveTime(Date value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setActive(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRefSet(SnomedRefSet value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReleased(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReferencedComponentId(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModuleId(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUuid(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTargetComponentId(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValueId(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMapTargetComponentId(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMapTargetComponentDescription(String value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setMapGroup(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMapPriority(int value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setMapRule(String value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setMapAdvice(String value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setCorrelationId(String value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setMapCategoryId(String value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void unsetEffectiveTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getTargetComponentType() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setDescriptionFormat(String value) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public void setDescriptionLength(int value) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public void setAcceptabilityId(String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setSourceEffectiveTime(Date value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setTargetEffectiveTime(Date value) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public void setCharacteristicTypeId(String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setLabel(String value) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public void setOperatorComponentId(String value) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public void setSerializedValue(String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setUomComponentId(String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getLabel() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataType getDataType() {
		throw new UnsupportedOperationException();
	}

}