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
package com.b2international.snowowl.snomed.core.refset.automap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.csv.CsvLexer;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordLexerCallback;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.Lists;

/**
 * CSV parser for supporting variable field size.
 * 
 *
 */
public class CsvVariableFieldCountParser implements ITableParser {
	
	private CsvLexer lexer;
	private VariableFieldSizeLexerCallback lexerCallback;
	private RecordParserCallback<String> recordCallback = new CsvRecordParserCallBack();
	
	private BufferedReader reader;
	
	private List<String> header;
	private List<List<String>> content;
	
	private int maxWidth;
	
	private boolean hasHeader;
	private boolean skipEmptyRows;

	public CsvVariableFieldCountParser(final File csvFile, final CsvSettings settings, boolean hasHeader, boolean skipEmptyRows) {
		try {
			this.header = Lists.newArrayList();
			
			this.content = new ArrayList<List<String>>();

			this.maxWidth = -1;
			
			this.hasHeader = hasHeader;
			
			this.skipEmptyRows = skipEmptyRows;
			
			reader = new BufferedReader(new FileReader(csvFile));
			this.lexerCallback = new VariableFieldSizeLexerCallback();
			this.lexer = new CsvLexer(reader, settings, lexerCallback);
		} catch (FileNotFoundException e) {
			ApplicationContext.handleException(SnomedDatastoreActivator.getContext().getBundle(), e, e.getMessage());
		}
	}
	
	/**
	 * Performs the file parsing.
	 */
	@Override
	public void parse() throws SnowowlServiceException {
		try {
			lexer.readAll();
		} catch (IOException e) {
			maxWidth = -1;
			throw new SnowowlServiceException(e);
		} catch (IllegalStateException e) {
			maxWidth = -1;
			throw new SnowowlServiceException(e);
		}
	}
	
	@Override
	public List<List<String>> getContent() {
		return content;
	}

	@Override
	public List<String> getColumnHeader() {
		if (maxWidth == -1) {
			return Collections.emptyList();
		}
		
		if (header.size() == 0) {
			return Collections.emptyList();
		}
		
		return header;
	}
	
	private class CsvRecordParserCallBack implements RecordParserCallback<String> {
		
		private boolean headerParsed = false;
		
		@Override
		public void handleRecord(int recordCount, List<String> record) {
			if (hasHeader && !headerParsed) {
				header.addAll(record);
				headerParsed = true;
			} else {
				if (record == null || (record.size() == 1 && record.get(0).equals(""))) {
					if (!skipEmptyRows) {
						String[] copiedArray = new String[record.size()];
						System.arraycopy(record.toArray(new String[] { }), 0, copiedArray, 0, record.size());
						
						content.add(Arrays.asList(copiedArray));
						
						return;
					}
				} else {
					if (record.size() > maxWidth) {
						maxWidth = record.size();
					}
					String[] copiedArray = new String[record.size()];
					System.arraycopy(record.toArray(new String[] { }), 0, copiedArray, 0, record.size());
					
					content.add(Arrays.asList(copiedArray));
				}
			}
			
		}
	}
	
	/**
	 * Lexer callback implementation.
	 */
	public class VariableFieldSizeLexerCallback implements RecordLexerCallback {
		
		private final List<String> line;

		public VariableFieldSizeLexerCallback() {
			line =  new ArrayList<String>();
		}

		/*
		 * (non-Javadoc)
		 * @see org.ihtsdo.lookup.parser.RecordLexerCallback#handleField(int)
		 */
		public void handleField(final int fieldCount, final StringBuilder field) {
			line.add(field.toString());
		}

		/*
		 * (non-Javadoc)
		 * @see org.ihtsdo.lookup.parser.RecordLexerCallback#handleRecord(int)
		 */
		public void handleRecord(final int recordCount) {
			recordCallback.handleRecord(recordCount, line);
			line.clear();
		}
	}
	
	
	
	public static void main(String[] args) throws IOException, SnowowlServiceException {
		CsvSettings csvSettings = new CsvSettings('\0', '\t', EOL.LF, true);
		
		CsvVariableFieldCountParser parser = new CsvVariableFieldCountParser(new File("/home/bvizer/test.csv"), csvSettings, false, true);
		
		parser.parse();
		
		List<List<String>> content = parser.getContent();
		
		for (List<String> list : content) {
			System.out.println(list);
		}
	}

	@Override
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				//	nothing to do
			}
		}
	}
}