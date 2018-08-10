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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import java.util.Date;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.base.Strings;

/**
 * @since 7.0
 */
public class Rf2ModuleDependencyRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2ModuleDependencyRefSetRowValidator(Rf2ValidationResponseEntity validationEntity, String[] values) {
		super(validationEntity, values);
	}


	@Override
	protected void validate(String[] values) {
		super.validate(values);
		final String memberId = values[0];
		final String sourceEffectiveTime = values[6];
		final String targetEffectiveTime = values[7];
		validateSpecialFields(memberId, sourceEffectiveTime, targetEffectiveTime);
	}
	
	private void validateSpecialFields(String memberId, String sourceEffectiveTime, String targetEffectiveTime) {
		if (Strings.isNullOrEmpty(sourceEffectiveTime) || Strings.isNullOrEmpty(targetEffectiveTime)) {
			reportIssue(Rf2ValidationType.ERROR, String.format("Source or target effective time field was empty for '%s'", memberId));
			return;
		}
		
		try {
			final Date sourceDate = EffectiveTimes.parse(sourceEffectiveTime, DateFormats.SHORT);
			final Date targetDate = EffectiveTimes.parse(targetEffectiveTime, DateFormats.SHORT);
		} catch (SnowowlRuntimeException e) {
			reportIssue(Rf2ValidationType.ERROR, String.format("Source or target effective time field date type was in the incorrect format for '%s'", memberId));
		}
		
	}

}
