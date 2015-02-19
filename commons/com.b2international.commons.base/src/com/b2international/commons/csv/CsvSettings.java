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
import com.b2international.commons.csv.CsvLexer.EOL;

/**
 * CSV setting class.
 */
public class CsvSettings {
	
	public static final CsvSettings STANDARD = new CsvSettings('"', ',', EOL.CRLF, true); 
	public static final CsvSettings EXCEL = new CsvSettings('"', ',', EOL.LF, true); 
	public static final CsvSettings EXCEL_EUROPE = new CsvSettings('"', ';', EOL.LF, true); 
	
	final char quote;
	final char delim;
	final EOL eol;
	final boolean trim;
	
	/**
	 * Constructor.
	 * @param quote the quote property.
	 * @param delim the delimiter property.
	 * @param eol the end of line enumeration type.
	 * @param trim the trim property.
	 */
	public CsvSettings(final char quote, final char delim, final EOL eol, final boolean trim) {
		this.quote = quote;
		this.delim = delim;
		this.eol = eol;
		this.trim = trim;
	}

	/**
	 * Returns with the quote.
	 * @return the quote attribute of the CSV setting.
	 */
	public char getQuote() {
		return quote;
	}

	/**
	 * Returns with the delimiter character.
	 * @return the delimiter character.
	 */
	public char getDelim() {
		return delim;
	}

	/**
	 * Returns with the end of line enumeration type.
	 * @return the {@link EOL} type.
	 */
	public EOL getEol() {
		return eol;
	}
	
	/**
	 * Returns the string representation of the EOL chars.
	 * @return
	 */
	public String getLineEndingString() {
		switch (eol) {
			case CR : return "\r";
			case LF : return "\n";
			case CRLF : return "\r\n";
			
			default: throw new IllegalArgumentException("Invalid End-Of-Line character");
		}
	}

	/**
	 * Returns with the <tt>boolean</tt> value of the trim attribute.
	 * @return <tt>true</tt> if trimming was enabled otherwise <tt>false</tt>.
	 */
	public boolean isTrim() {
		return trim;
	}
}