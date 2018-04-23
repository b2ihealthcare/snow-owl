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
package com.b2international.snowowl.snomed.core.refset.automap;

import java.util.List;

import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * Interface for parsing table-like files (eg. xls).
 * Skipping empty lines and headers must be supported and can be controlled by the parser.
 * 
 */
public interface ITableParser {
	
	
	/**
	 * Execute parsing. Skipping empty lines and headers must be supported and can be controlled by the parser.
	 * 
	 * @throws SnowowlServiceException
	 */
	public void parse() throws SnowowlServiceException;
	
	/**
	 * Returns the parsed content. Members are the textual representation of each cell collected in a {@link List}.
	 * If skipping empty lines is supported but they has to be retained empty list must be created (<code>Collections.emptyList()}</code>). 
	 * If a cell is empty, empty string must be added to the list.
	 * 
	 * @return List<List<String>>
	 */
	public List<List<String>> getContent();
	
	/**
	 * Returns the header of the parsed content if there is. Empty list otherwise.
	 * 
	 */
	public List<String> getColumnHeader();
	
	/**
	 * Close resources.
	 */
	public void close();

}