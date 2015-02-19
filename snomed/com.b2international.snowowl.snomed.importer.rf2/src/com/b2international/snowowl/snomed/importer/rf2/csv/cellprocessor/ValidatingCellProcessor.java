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
package com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.util.CSVContext;

/**
 * Represents a cell processor that forwards the received value immediately to
 * the next processor in the chain, then throws away the result and returns the
 * original value. The validating behavior is achieved by the follower
 * processors throwing {@link SuperCSVException} in case of an error.
 * 
 */
public class ValidatingCellProcessor extends CellProcessorAdaptor implements
		BoolCellProcessor, DateCellProcessor, DoubleCellProcessor,
		LongCellProcessor, StringCellProcessor {

	/**
	 * Replaces possible {@code null} references with a {@link NullObjectPattern null processor}.
	 * 
	 * @param next the {@link CellProcessor} to wrap
	 * @return {@link NullObjectPattern#INSTANCE} if {@code next} is {@code null}, {@code next} otherwise
	 */
	private static CellProcessor getWrappedProcessor(final CellProcessor next) {
		return (next == null) ? NullObjectPattern.INSTANCE : next;
	}

	public ValidatingCellProcessor(final CellProcessor next) {
		super(getWrappedProcessor(next));
	}

	@Override
	public Object execute(final Object value, final CSVContext context) {
		next.execute(value, context);
		return value;
	}
}