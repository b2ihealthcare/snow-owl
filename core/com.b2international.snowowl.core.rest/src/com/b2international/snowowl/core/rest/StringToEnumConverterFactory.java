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
package com.b2international.snowowl.core.rest;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * {@link ConverterFactory} for converting String to Enums (case-insensitive).
 * 
 * @see ConverterFactory
 * @since 7.5
 */
@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

	private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

		private Class<T> enumType;

		public StringToEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}

		public T convert(String source) {
			return (T) Enum.valueOf(this.enumType, source.trim().toUpperCase());
		}
	}

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnumConverter(targetType);
	}
}
