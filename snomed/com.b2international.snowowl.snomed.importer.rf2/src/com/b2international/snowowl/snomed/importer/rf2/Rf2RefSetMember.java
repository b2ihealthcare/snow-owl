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
package com.b2international.snowowl.snomed.importer.rf2;

import java.util.Date;
import java.util.List;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class Rf2RefSetMember extends UnsupportedRf2RefSetMember {

	private List<String> record;
	private SnomedRefSet refSet;

	public Rf2RefSetMember(List<String> rf2Row, SnomedRefSet refSet, long storageKey) {
		super(storageKey);
		this.record = rf2Row;
		this.refSet = refSet;
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
	public byte getMapGroup() {
		return Byte.parseByte(record.get(6));
	}

	@Override
	public byte getMapPriority() {
		return Byte.parseByte(record.get(7));
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

	private Date getEffectiveTime(int index) {
		final String effectiveTime = record.get(index);
		if (!Strings.isNullOrEmpty(effectiveTime)) {
			return EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
		} else {
			return null;
		}
	}

}
