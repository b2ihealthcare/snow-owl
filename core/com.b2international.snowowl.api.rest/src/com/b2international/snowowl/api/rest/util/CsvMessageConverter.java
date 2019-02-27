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
package com.b2international.snowowl.api.rest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Charsets;

/**
 * @since 6.13
 */
public class CsvMessageConverter extends AbstractHttpMessageConverter<Collection<Object>> {
	
	private static final String ATTACHMENT = "attachment";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charsets.UTF_8);

	public CsvMessageConverter() {
		super(MEDIA_TYPE);
	}

	protected boolean supports(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}

	@Override
	protected void writeInternal(Collection<Object> items, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
		if (!items.isEmpty()) {
			output.getHeaders().setContentType(MEDIA_TYPE);
			output.getHeaders().set(CONTENT_DISPOSITION, ATTACHMENT);
			try (OutputStream out = output.getBody()) {
				final CsvMapper mapper = new CsvMapper();
				// XXX The mapper is auto closing the writer after the first write out for some reason
				mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
				
				CsvSchema schema = mapper.schemaFor(items.iterator().next().getClass()).withHeader().withColumnSeparator('\t');
				ObjectWriter writer = mapper.writer(schema);
				for (Object item : items) {
					writer.writeValue(out,  item);
				}
			}
		}
	}

	@Override
	protected Collection<Object> readInternal(Class<? extends Collection<Object>> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new NotImplementedException();
	}
	
}
