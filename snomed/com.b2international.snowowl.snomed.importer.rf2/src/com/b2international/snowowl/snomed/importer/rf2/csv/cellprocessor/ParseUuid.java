/**
 * Copyright 2007 Kasper B. Graversen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor;

import java.text.MessageFormat;
import java.util.UUID;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.NullInputException;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.util.CSVContext;

/**
 * A {@link StringCellProcessor} that parses UUIDs from their String
 * representation.
 *
 * @see ParseInt
 * 
 */
public class ParseUuid extends CellProcessorAdaptor implements StringCellProcessor {

	public ParseUuid() {
		super();
	}

	@Override
	public Object execute(final Object value, final CSVContext context) throws SuperCSVException {

		if (null == value) { 
			throw new NullInputException(MessageFormat.format("Input cannot be null on line {0} at column {1}",
					context.lineNumber, context.columnNumber), context, this); 
		}

		final UUID result;

		if (value instanceof String) {

			try {
				result = UUID.fromString((String) value);
			} catch (final IllegalArgumentException e) {
				throw new SuperCSVException("Parser error", context, this, e);
			}

		} else {

			throw new SuperCSVException(MessageFormat.format("Can''t convert ''{0}'' to UUID. Input is not of type String but of type {1}",
					value, value.getClass().getName()),	context, this);
		}

		return next.execute(result, context);
	}
}
