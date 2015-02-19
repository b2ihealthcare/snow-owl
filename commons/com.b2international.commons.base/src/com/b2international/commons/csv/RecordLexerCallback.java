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

/**
 * Interface for the lexer callback.
 * */
public interface RecordLexerCallback {

	/**
	 * Handles some operation on a parsed field based on the passed in field count and the field represented as a {@link StringBuilder}.
	 * @param fieldCount the current field count.
	 * @param field the field represented as a StringBuilder.
	 */
	void handleField(int fieldCount, StringBuilder field);
	
	/**
	 * Handles operation on a parsed record based on the passed in record count. 
	 * @param recordCount the number of records.
	 */
	void handleRecord(int recordCount);
}