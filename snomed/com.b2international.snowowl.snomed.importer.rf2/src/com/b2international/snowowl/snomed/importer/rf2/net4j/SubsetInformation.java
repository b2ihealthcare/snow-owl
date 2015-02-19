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
package com.b2international.snowowl.snomed.importer.rf2.net4j;

import java.text.ParseException;
import java.util.Date;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;

/**
 * Stores information about the reference set during the import at the server
 * side.
 * 
 * 
 */
public class SubsetInformation {

	private static final String NAMESPACE = "([0-9]){7}";
	private static final String EFFECTIVE_TIME = "([1-9]){1}([0-9]){3}((0{1}[1-9]{1})|(1{1}[0-2]{1})){1}(0[1-9]|[12][0-9]|3[01]){1}";

	private String fileName;
	private String subsetName;
	private String nameSpace;
	private Date effectiveTime;

	public SubsetInformation(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the following informations from the {@code filename}:
	 * <ul>
	 * <li>name of the subset,
	 * <li>namespace of the subset,
	 * <li>effective time of the subset.
	 * </ul>
	 * 
	 * @throws ParseException
	 *             if the {@code filename} cannot be parsed
	 */
	public void parse() throws ParseException {

		String term = fileName.substring(fileName.indexOf("sct1_Concepts_") + 14);
		String[] splitTerm = term.split("_|.txt|.xls|.xlsx|.csv");
		term = "";

		for (String terms : splitTerm) {
			if (terms.matches(NAMESPACE)) {
				setNameSpace(terms);
			} else if (terms.matches(EFFECTIVE_TIME)) {
				setEffectiveTime(EffectiveTimes.parse(terms, DateFormats.SHORT));
			} else {
				term = term + terms + " ";
			}
		}

		setSubsetName(term.trim());
	}

	public String getSubsetName() {
		return subsetName;
	}

	public void setSubsetName(String subsetName) {
		this.subsetName = subsetName;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
}