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
import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.AbstractIterator;

/**
 * The CSV lexer class.
 * 
 * */
public class CsvLexer implements Iterable<StringBuilder> {

	/**
	 * CSF field iterator class.
	 * 
	 * */
	public class CsvFieldIterator extends AbstractIterator<StringBuilder> {
		
		/*
		 * (non-Javadoc)
		 * @see com.google.common.collect.AbstractIterator#computeNext()
		 */
		@Override
		protected StringBuilder computeNext() {
			if(safeRead()) {
				return sb;
			} else {
				endOfData();
				return null;
			}
		}
	}
	
	public static final char CR = '\r';
	public static final char LF = '\n';
	
	
	/**
	 * Enumeration type of a processed character.
	 * */
	public static enum State {
		FIELDSTART,
		FIELD,
		FIELD_UNQUOTED,
		FIELD_ONEQUOTE,
		CR_BEFORE_LF,
		CR_BEFORE_LF_AFTER_QUOTED
	}
	
	/**
	 * Enumeration types of end of line. 
	 */
	public static enum EOL {
		CR,
		LF,
		CRLF
	}
	
	protected final Optional<String> fileName;
	
	protected final Reader reader;
	protected final CsvSettings csvSettings;
	protected final RecordLexerCallback callback;
	
	protected char[] buf;
	protected int bufStart;
	protected int bufEnd;
	
	protected StringBuilder sb = null;
	protected State state;
	
	/** length of the buffer up till the last non-space character, for optional trimming */
	protected int trimmedPos;
	protected int fieldCount;
	protected int recordCount;

	/**
	 * Public constructor.
	 * @param reader a reader instance associated with the source file to be parsed.
	 * @param csvSettings CSV setting.
	 * @param callback the callback of the lexer.
	 */
	public CsvLexer(final Reader reader, final CsvSettings csvSettings, final RecordLexerCallback callback) {
		this(reader, Optional.<String>absent(), csvSettings, callback);
	}

	public CsvLexer(final Reader reader, Optional<String> fileName, final CsvSettings csvSettings, final RecordLexerCallback callback) {
		
		this.reader = reader;
		this.csvSettings = csvSettings;
		this.callback = callback;
		this.fileName = fileName;

		buf = new char[1024];
		sb = new StringBuilder();
		state = State.FIELDSTART;
		sb.setLength(0);
		trimmedPos = 0;
		fieldCount = 0;
		recordCount = 0;
	}
	
	/**
	 * Starts reading the file associated with a {@link Reader} instance.
	 * @throws IOException if I/O exception occurred while reading the file.
	 */
	public void readAll() throws IOException {
		while(read());
	}
	
	/**
	 * Returns with the number of fields.
	 * @return the field count.
	 */
	public int getFieldCount() {
		return fieldCount;
	}
	
	/**
	 * Returns with the number of records.
	 * @return the record count.
	 */
	public int getRecordCount() {
		return recordCount;
	}
	
	/**
	 * Returns with the buffer.
	 * @return the buffer.
	 */
	public StringBuilder getBuffer() {
		return sb;
	}
	
	/**
	 * Closes the reader's stream.
	 * @throws IOException
	 */
	public void close() throws IOException {
		reader.close();
	}
	
	/**
	 * Returns with the file name of the processed CSV file.
	 * @return the CSV file name.
	 */
	public Optional<String> getFileName() {
		return fileName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<StringBuilder> iterator() {
		return new CsvFieldIterator();
	}

	/**
	 * Invokes the {@link CsvLexer#read()} method and closes the reader's stream.
	 * @return false if the parsing successfully finished.
	 */
	protected boolean safeRead() {
		try {
			return read();
		} catch (final IOException e) {
			if(reader != null) {
				try {
					reader.close();
				} catch (final IOException e2) {
					// ignore
				}
			}
			throw new CsvParseException("Error while reading CSV data: ", e, fileName);
		}
	}
	
	/**
	 * Performs a character reading process.
	 * @param c the passed in character.
	 * @return <tt>false</tt> if the passed in represents an end of the field, record or file. Otherwise returns <tt>true</tt>. 
	 * @throws IOException if I/O exception occurred while parsing.
	 */
	protected boolean process(final char c) throws IOException {

		switch(state) {
		case FIELDSTART:
			
			if(c == ' ' && csvSettings.trim) {
				// skip space
			
			} else if(c == csvSettings.quote) {
				state = State.FIELD;

			} else if(c == CR && csvSettings.eol == EOL.CRLF) {
				state = State.CR_BEFORE_LF;

			} else if(c == csvSettings.delim || isEol(c)) {
				fieldComplete();
				if(isEol(c)) {
					recordComplete();
				}
				return false;
			
			} else {
				sb.append((char) c);
				trimmedPos = sb.length();
				state = State.FIELD_UNQUOTED;
			}
			break;
			
		case FIELD:

			if(c == csvSettings.quote) {
				state = State.FIELD_ONEQUOTE;
			
			} else {
				sb.append((char) c);
				if(c != ' ') {
					trimmedPos = sb.length();
				}
			}
			
			break;
			
		case FIELD_UNQUOTED:

			if(c == csvSettings.quote) {
				illegalState(c);

			} else if(c == CR && csvSettings.eol == EOL.CRLF) {
				state = State.CR_BEFORE_LF;

			} else if(c == csvSettings.delim || isEol(c)) {
				fieldComplete();
				if(isEol(c)) {
					recordComplete();
				}
				return false;
			
			} else {
				sb.append((char) c);
				if(c != ' ') {
					trimmedPos = sb.length();
				}
				state = State.FIELD_UNQUOTED;
			}
			break;
			
		case FIELD_ONEQUOTE:

			if(c == csvSettings.quote) {
				sb.append((char) c);
				trimmedPos = sb.length();
				state = State.FIELD;
			
			} else if(c == CR && csvSettings.eol == EOL.CRLF) {
				state = State.CR_BEFORE_LF_AFTER_QUOTED;

			} else if(c == csvSettings.delim || isEol(c)) {
				// empty field may be delimited with two delimiters
				if(sb.length() == 1 && sb.charAt(0) == csvSettings.delim) {
					sb.setLength(0);
				}
				fieldComplete();
				if(isEol(c)) {
					recordComplete();
				}
				return false;
			
			} else {
				illegalState(c);
			}
			
			break;
			
		case CR_BEFORE_LF:
			
			if(c == LF) {
				fieldComplete();
				recordComplete();
				return false;
				
			} else {
				sb.append(CR);
				trimmedPos = sb.length();
				state = State.FIELD_UNQUOTED;
			}
			break;

		case CR_BEFORE_LF_AFTER_QUOTED:
			
			if(c == LF) {
				fieldComplete();
				recordComplete();
				return false;
				
			} else {
				illegalState(c);
			}
			break;

		default:
			illegalState(c);
		}
		
		return true;
	}
	
	/**
	 * Reads until the next end of the field, record or file.
	 * @param c the passed in character.
	 * @return false when end of file reached.
	 * @throws IOException if I/O exception occurred while parsing.
	 */
	protected boolean read() throws IOException {

		final char c = 0;
		for (;;) {

			if (bufStart >= bufEnd) {
				bufStart = 0;
				bufEnd = reader.read(buf, 0, buf.length);
			}

			// EOF
			if (bufStart >= bufEnd) {
				
				switch(state) {
				case FIELDSTART:
					break;
				case FIELD_UNQUOTED:
				case FIELD_ONEQUOTE:
					fieldComplete();
					recordComplete();
					break;
				default:
					illegalState(c);
				}
				
				return false;
			}
			
			while(bufStart < bufEnd) {
				if(process(buf[bufStart++])) {
					break;
				}
			}
		}
	}
	
	/**
	 * Returns true if the passed in character is an end of line character otherwise returns false.
	 * @param c the character to be checked.
	 * @return true if the input is an end of line character.
	 */
	protected boolean isEol(final int c) {
		return c == CR && csvSettings.eol == EOL.CR
		    || c == LF && csvSettings.eol == EOL.LF;
	}

	/**
	 * Performed when reading and parsing a filed has been completed.
	 */
	protected void fieldComplete() {
		callback.handleField(++fieldCount, sb);
		state = State.FIELDSTART;
		sb.setLength(0);
		trimmedPos = 0;
	}

	/**
	 * Performed when reading and parsing a record has been completed.
	 */
	protected void recordComplete() {
		callback.handleRecord(++recordCount);
		fieldCount = 0;
	}

	/**
	 * Throws a runtime {@link IllegalStateException} exception for a passed in character.
	 * @param c
	 */
	protected void illegalState(final int c) {
		throw new CsvParseException(String.format("Illegal state %s at char %s", state, c), fileName);
	}

}