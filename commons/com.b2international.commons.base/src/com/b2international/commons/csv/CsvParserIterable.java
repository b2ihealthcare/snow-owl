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
package com.b2international.commons.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;

public class CsvParserIterable<T> implements Iterable<T> {

	protected final CsvLexer csvReader;
	protected final Function<List<String>, T> adapter;
	private final int fieldCount;
	
	public CsvParserIterable(CsvLexer csvReader, Function<List<String>, T> adapter, int fieldCount) {
		this.csvReader = csvReader;
		this.adapter = adapter;
		this.fieldCount = fieldCount;
	}

	public List<String> getHeader() {
		List<String> line = new ArrayList<String>(fieldCount);
		readLine(line);
		return line;
	}
	
	public void close() throws IOException {
		csvReader.close();
	}
	
	protected void tryClose() {
		try {
			close();
		} catch (IOException e2) {
			// ignore
		}
	}
	
	protected boolean readLine(List<String> line) {

		int recordCount = csvReader.getRecordCount();
		
		int i = 0;
		boolean hasNext = false;
		for(; i < fieldCount; i++) {
			hasNext = csvReader.safeRead();
			line.add(csvReader.getBuffer().toString());
		}
		
		if(recordCount != csvReader.getRecordCount() - 1) {
			throw new CsvParseException(String.format("CSV has more records than %d in reader", i), csvReader.getFileName());
		}
		
		return hasNext;
	}
	
	public Iterator<T> iterator() {
		return new AbstractIterator<T>() {
			
			List<String> line = new ArrayList<String>(fieldCount);
			
			@Override
			protected T computeNext() {
				if(readLine(line)) {
					return adapter.apply(line);
				} else {
					endOfData();
					return null;
				}
			}
		};
	}
}