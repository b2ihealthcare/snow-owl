/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.b2international.snowowl.core.request.io.ImportDefect;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor;
import com.google.common.primitives.Ints;

/**
 * @since 7.0
 */
public final class Rf2ValidationIssueReporter {
	
	private final Map<String, ImportDefectAcceptor> defectsByFile = newHashMap();
	
	public ImportDefectAcceptor getDefectAcceptor(final String file) {
		return defectsByFile.computeIfAbsent(file, key -> new ImportDefectAcceptor(key));
	}
	
	public Stream<ImportDefect> allDefects() {
		return defectsByFile.values()
			.stream()
			.flatMap(acceptor -> acceptor.getDefects().stream());
	}
	
	private Stream<ImportDefect> allErrors() {
		return allDefects().filter(ImportDefect::isError);
	}

	private Stream<ImportDefect> allWarnings() {
		return allDefects().filter(ImportDefect::isWarning);
	}

	public int getNumberOfErrors() {
		return Ints.checkedCast(allErrors().count());
	}
	
	public int getNumberOfWarnings() {
		return Ints.checkedCast(allWarnings().count());
	}
	
	public List<ImportDefect> getErrors() {
		return allErrors().collect(Collectors.toUnmodifiableList());
	}
	
	public List<ImportDefect> getWarnings() {
		return allWarnings().collect(Collectors.toUnmodifiableList());
	}
	
	public List<ImportDefect> getDefects() {
		return allDefects().collect(Collectors.toUnmodifiableList());
	}

	public void logWarnings(Logger log) {
		allWarnings().forEachOrdered(defect -> log.warn(defect.getMessage()));
	}
	
	public void logErrors(Logger log) {
		allErrors().forEachOrdered(defect -> log.error(defect.getMessage()));
	}

	public boolean hasErrors() {
		return allErrors()
			.findAny()
			.isPresent();
	}
}
