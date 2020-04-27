/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * @since 7.6
 */
public class TermStringConverter extends AbstractNullSafeConverter<String> {

	@Override
	protected String internalToString(String value) {
		String val = value.trim();
		if (!val.startsWith("|")) {
			val = "|".concat(val);
		}
		if (!val.endsWith("|")) {
			val = val.concat("|");
		}
		return val;
	}
	
	@Override
	protected String internalToValue(String string, INode node) throws ValueConverterException {
		return string.replaceAll("\\|", "");
	}

}
