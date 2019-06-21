/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.converter;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractValueConverter;
import org.eclipse.xtext.nodemodel.INode;

import com.b2international.snowowl.snomed.ecl.Ecl;

/**
 * @since 5.4
 */
public final class CardinalityMaxValueConverter extends AbstractValueConverter<Integer> {

	@Override
	public Integer toValue(String string, INode node) throws ValueConverterException {
		if (Ecl.ANY.equals(string)) {
			return Ecl.MAX_CARDINALITY;
		} else {
			return Integer.valueOf(string);
		}
	}

	@Override
	public String toString(Integer value) throws ValueConverterException {
		if (value == Ecl.MAX_CARDINALITY) {
			return Ecl.ANY;
		} else {
			return value.toString();
		}
	}

}
