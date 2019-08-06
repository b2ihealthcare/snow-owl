/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;

/**
 * Contains utility methods for modifying incoming RF2 files. 
 */
public class Rf2FileModifier {

	private static final Splitter TAB_SPLITTER = Splitter.on('\t');

	/**
	 * Splits the given RF2 files into RF2 file segments based on distinct effective times. The result will be a map
	 * where keys are the effective times and values are the RF2 files. These files are being created to the temporary
	 * folder and will be cleaned up on graceful JVM shutdown.
	 * <p>
	 * The generated fragment files do not have an RF2 header.
	 * 
	 * @param toSplit the file to split
	 * @return a map of effective times and the corresponding RF2 file fragments
	 * @throws IOException if splitting fails for some reason
	 */
	public static SortedMap<String, File> split(final File toSplit) throws IOException {

		final SortedMap<String, File> fileFragments = new TreeMap<>();
		final SortedMap<String, PrintWriter> writers = new TreeMap<>();
		
		String line = null;
		boolean firstLine = true;
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(toSplit), Charsets.UTF_8))) {
			
			while ((line = reader.readLine()) != null) {
				
				if (firstLine) {
					firstLine = false;
					continue;
				}
				
				final List<String> values = TAB_SPLITTER.splitToList(line);
				final String effectiveTime = values.get(1);
				
				final PrintWriter effectiveTimeWriter;
				
				if (!fileFragments.containsKey(effectiveTime)) {
					final File effectiveTimeFile = createTempFile();
					effectiveTimeWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(effectiveTimeFile), Charsets.UTF_8)));
					
					fileFragments.put(effectiveTime, effectiveTimeFile);
					writers.put(effectiveTime, effectiveTimeWriter);
				} else {
					effectiveTimeWriter = writers.get(effectiveTime);
				}
				
				effectiveTimeWriter.print(line);
				effectiveTimeWriter.print("\r\n");
			}
			
		} finally {
			
			for (PrintWriter effectiveTimeWriter : writers.values()) {
				effectiveTimeWriter.close();
			}
		}
		
		return fileFragments;
	}
	
	private static File createTempFile() throws IOException {
		final File output = File.createTempFile(UUID.randomUUID().toString(), ".txt");
		output.deleteOnExit();
		return output;
	}
}
