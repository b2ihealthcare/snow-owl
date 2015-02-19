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
package com.b2international.snowowl.dsl.converter;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractValueConverter;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

public class ESCGConverterService extends DefaultTerminalConverters {
	
	public static class IntegerConverter extends AbstractValueConverter<Integer> {

		@Override
		public Integer toValue(String stringValue, INode node) throws ValueConverterException {
			
			Integer i = 0;
			try {
				i = stringValue == null ? 0 : Integer.valueOf(stringValue);
			} catch (NumberFormatException e) {
				throw new ValueConverterException("Could not convert "+stringValue+" to an Integer", node, e);
			}
			
			return i;
		}

		@Override
		public String toString(Integer value) throws ValueConverterException {
			return Integer.toString(value);
		}
		
	}
	
	@Inject
	private IntegerConverter integerConverter;
	
	@ValueConverter(rule = "Integer")
	public IValueConverter<Integer> Integer() {
		return integerConverter;
	}

}