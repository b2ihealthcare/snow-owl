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

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.NullInputException;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.util.CSVContext;

/**
 * A cell processor that converts a string to a byte.
 * 
 * @see ParseInt
 * 
 * @author Kasper B. Graversen
 */
public class ParseByte extends CellProcessorAdaptor implements StringCellProcessor {

	public ParseByte() {
		super();
	}

	public ParseByte(final LongCellProcessor next) {
		super(next);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final Object value, final CSVContext context) throws SuperCSVException {
		
		if (value == null) {
			throw new NullInputException(MessageFormat.format("Input cannot be null on line {0} at column {1}",
					context.lineNumber, context.columnNumber), context,	this);
		}
		
		final Byte result;
		
		if (value instanceof Byte) {
			result = (Byte) value;
		} else if (value instanceof String) {
			
			try {
				result = Byte.parseByte((String) value);
			} catch (final NumberFormatException e) {
				throw new SuperCSVException("Parser error", context, this, e);
			}
			
		} else {
			throw new SuperCSVException(
					MessageFormat
							.format("Can''t convert ''{0}'' to a byte. Input is not of type Byte nor type String but of type {1}",
									value, value.getClass().getName()), context, this);
		}

		return next.execute(result, context);
	}
}
