/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @since 6.4
 */
public class CsvMessageConverter extends AbstractHttpMessageConverter<CollectionResource> {
	public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

	public CsvMessageConverter() {
		super(MEDIA_TYPE);
	}

	protected boolean supports(Class<?> clazz) {
		return CollectionResource.class.isAssignableFrom(clazz);
	}

	protected void writeInternal(CollectionResource response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
		final Collection<Object> items = response.getItems();
		if (!items.isEmpty()) {
			output.getHeaders().setContentType(MEDIA_TYPE);
			output.getHeaders().set("Content-Disposition", "attachment");
			try (OutputStream out = output.getBody()) {
				final CsvMapper mapper = new CsvMapper();
				CsvSchema schema = mapper.schemaFor(items.iterator().next().getClass()).withHeader();
				ObjectWriter writer = mapper.writer(schema);
				for (Object item : items) {
					writer.writeValue(out,  item);
				}
			}
		}
	}
	
	@Override
	protected CollectionResource readInternal(
			Class<? extends CollectionResource> arg0, HttpInputMessage arg1)
			throws IOException, HttpMessageNotReadableException {
		throw new NotImplementedException();
	}

}
