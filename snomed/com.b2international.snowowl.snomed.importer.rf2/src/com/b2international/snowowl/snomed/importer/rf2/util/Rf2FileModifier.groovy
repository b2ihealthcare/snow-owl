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

/**
 * Class for modifying RF2 files. 
 */
class Rf2FileModifier {

	/**
	 * Splits the given RF2 files into RF2 file segments based on distinct effective times.
	 * The result will be a map where keys are the effective times and values are the RF2 files.
	 * These files are being created to the temporary folder and will be cleaned up on graceful 
	 * JVM shutdown. It is important to note, that the generated fragment files does not have 
	 * an RF2 header.
	 * @param toSplit the file to split.
	 * @return a map of effective times and the corresponding RF2 file fragments.
	 */
	public Map<String, File> split(final File toSplit) {
		
		def fileFragments = [:]
		toSplit.eachLine { line, number ->
			if (1 != number) {
				def values = line.split("\t")
				def effectiveTime = values[1]
				if (!fileFragments.containsKey(effectiveTime)) {
					fileFragments << [(effectiveTime) : createTempFile()]
				}
				fileFragments.get(effectiveTime).withWriterAppend { writer ->
					writer.println(line)
				}
			}
		}
		
		return fileFragments
		
	} 
	
	/**
	 * Cuts the very fist line of the file and returns with the modified file.
	 * The modified file
	 * @param toCutHeader
	 * @return
	 */
	public File cutHeader(final File toCutHeader) {
		def output = createTempFile()
		toCutHeader.eachLine { line, number ->
			if (1 != number) {
				output.withWriterAppend { writer ->
					writer.println(line)
				}
			}
		}
		return output
	}
	
	private def createTempFile() {
		def output = File.createTempFile("${UUID.randomUUID()}.txt", null)
		output.deleteOnExit()
		return output;
	}
	
}
