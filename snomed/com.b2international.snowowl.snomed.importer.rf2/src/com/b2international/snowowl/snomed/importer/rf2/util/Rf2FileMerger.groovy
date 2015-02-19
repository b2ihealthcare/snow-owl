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
package com.b2international.snowowl.snomed.importer.rf2.util

import com.google.common.collect.Iterables


/**
 * RF2 file merger.
 */
class Rf2FileMerger {

	private final def otherFiles

	public Rf2FileMerger(final def fileUriOrPathName) {
		otherFiles = [ new File(fileUriOrPathName) ] as Set
	}
	
	public Rf2FileMerger add(final def fileUriOrPathName) {
		def file = new File(fileUriOrPathName)
		checkFileHeader(file)
		otherFiles << file
		return this
	}
	
	public File merge() {
		def output = File.createTempFile("${UUID.randomUUID()}.txt", null)
		output.deleteOnExit()
		merge(output)
		return output
	}
	
	public File merge(final def output) {
		output.withWriterAppend { writer ->
			otherFiles.eachWithIndex { file, index ->
				file.eachLine { line, number ->
					if (0 != index && 1 == number) {
						return
					}
					writer << "${line}\r\n"
				}
			}
		}
		return output
	}
	
	private def checkFileHeader(final def file) {
		compareHeaders(getFileHeader(file), getFileHeader(Iterables.get(otherFiles, 0)))
	}
	
	private def compareHeaders(final def leftHeader, final def rightHeader) {
		def lowerCaseLeftHeader = leftHeader.collect { it.toLowerCase() }
		def lowerCaseRightHeader = rightHeader.collect { it.toLowerCase() }
		def diff = (lowerCaseLeftHeader - lowerCaseRightHeader) + (lowerCaseRightHeader - lowerCaseLeftHeader)
		if (0 != diff.size()) {
			throw new IllegalStateException("Mismatching file headers.\n[$leftHeader]\n[$rightHeader]\nDiff: $diff")
		}
	}
	
	private def getFileHeader(final def file) {
		def line
		file.withReader { line = it.readLine() }
		return line.split("\t")
	}
	
}
