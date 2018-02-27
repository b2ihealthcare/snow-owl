/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.net4j;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Represents a defect type of the release files.
 */
public class SnomedValidationDefect implements Serializable, Comparable<SnomedValidationDefect> {
	
	private static final long serialVersionUID = 1L;
	protected static final String TAB = "\t";
	protected static final String LE = System.getProperty("line.separator");
	
	private final String fileName;
	private final DefectType defectType;
	private final Collection<String> defects;
	
	public SnomedValidationDefect(final String fileName, final DefectType defectType, final Collection<String> defects) {
		this.fileName = fileName;
		this.defectType = checkNotNull(defectType, "defectType");
		checkArgument(!defects.isEmpty(), "At least one error message is required");
		this.defects = ImmutableList.copyOf(defects);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public final DefectType getDefectType() {
		return defectType;
	}
	
	public final Collection<String> getDefects() {
		return defects;
	}
	
	@Override
	public int compareTo(SnomedValidationDefect o) {
		return getDefectType().compareTo(o.getDefectType());
	}
	
}
