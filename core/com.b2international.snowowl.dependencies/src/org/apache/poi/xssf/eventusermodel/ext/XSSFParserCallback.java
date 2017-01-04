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
package org.apache.poi.xssf.eventusermodel.ext;

import java.util.Collection;
import java.util.List;

/**
 * Callback for parsing a XSSF workbook file. 
 *
 */
public interface XSSFParserCallback {

	/**
	 * Inclusive, zero based maximum column number that has to be parsed.
	 * @return the maximum, relevant column count.
	 */
	int getRelevantColumnCount();
	
	/**
	 * Fired when a line of cells are processed during the XSSF parsing.
	 * The ordered list contains the string values of each individual cells.
	 * @param rowNumber the (zero based) number of row being processed by the parser.
	 * @param cellValues the cell values as an ordered list of strings.
	 */
	void handleNewLine(int rowNumber, final List<String> cellValues);
	
	/**
	 * Returns with a collection of unique workbook sheet names that has to be parsed.
	 * Others are ignored.
	 * @return a collection sheet names that has to be parsed.
	 */
	Collection<String> getSheetNamesToProcess();
	
}