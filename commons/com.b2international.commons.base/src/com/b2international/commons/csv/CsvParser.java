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
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

/**
 * The CSV parser class.
 */
public class CsvParser {

	/**
	 * Lexer callback implementation.
	 */
	public class LexerCallback implements RecordLexerCallback {
		
		private final int fieldCount;
		private final List<String> line;

		/**
		 * Constructor.
		 * @param fieldCount number of columns in the source file.
		 */
		public LexerCallback(final int fieldCount) {
			this.fieldCount = fieldCount;
			line =  new ArrayList<String>(fieldCount);
		}

		/*
		 * (non-Javadoc)
		 * @see org.ihtsdo.lookup.parser.RecordLexerCallback#handleField(int, java.lang.StringBuilder)
		 */
		public void handleField(final int fieldCount, final StringBuilder field) {
			if(fieldCount > this.fieldCount) {
				throw new CsvParseException(String.format("Too many fields, got %d, expected %d", fieldCount, this.fieldCount), fileName);
			}
			line.add(field.toString());
		}

		/*
		 * (non-Javadoc)
		 * @see org.ihtsdo.lookup.parser.RecordLexerCallback#handleRecord(int)
		 */
		public void handleRecord(final int recordCount) {
			if(line.size() != this.fieldCount) {
				throw new CsvParseException(String.format("Incorrect number of fields at record %d, got %d, expected %d",
						lexer.getRecordCount(), line.size(), this.fieldCount), fileName);
			}
			recordCallback.handleRecord(recordCount, line);
			line.clear();
		}
	}
	
	protected final CsvLexer lexer;
	protected final LexerCallback lexerCallback;
	protected final RecordParserCallback<String> recordCallback;
	protected final Optional<String> fileName;

	/**
	 * Constructor.
	 * @param reader reader for the file to be parsed.
	 * @param settings CSV setting.
	 * @param recordCallback callback for the CSV parser.
	 * @param fieldCount the number of columns in the source file.
	 */
	public CsvParser(final Reader reader, final CsvSettings settings, final RecordParserCallback<String> recordCallback, final int fieldCount) {
		this(reader, Optional.<String>absent(), settings, recordCallback, fieldCount);
	}
	
	/**
	 * Constructor.
	 * @param reader reader for the file to be parsed.
	 * @param fileName the name of the parsed file. Can be {@code null}.
	 * @param settings CSV setting.
	 * @param recordCallback callback for the CSV parser.
	 * @param fieldCount the number of columns in the source file.
	 */
	public CsvParser(final Reader reader, final Optional<String> fileName, final CsvSettings settings, final RecordParserCallback<String> recordCallback, final int fieldCount) {
		this.fileName = fileName;
		this.lexerCallback = new LexerCallback(fieldCount);
		this.recordCallback = recordCallback;
		this.lexer = new CsvLexer(reader, settings, lexerCallback);
	}
	
	/**
	 * Performs the file parsing.
	 * @throws IOException if I/O exception occurred while parsing the file.
	 */
	public void parse() throws IOException {
		lexer.readAll();
	}
}