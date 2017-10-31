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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.base.Ascii;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

/**
 * Representation of an exporter for SNOMED&nbsp;CT ontology.
 *
 */
public interface SnomedExporter {
	
	/**Horizontal tab.*/
	String HT = new String(new byte [] { Ascii.HT }, Charsets.US_ASCII);
	String CR_LF = new String(new byte[] {Ascii.CR, Ascii.LF}, Charsets.US_ASCII);
	
	String METADATA = "Metadata";
	String LANGUAGE = "Language";
	String CROSSMAP = "Maps";
	String MAP = "Map";
	String TERMINOLOGY = "Terminology";
	String CONTENT = "Content";
	String SUBSETS = "Subsets";
	String RF1_ROOT_FOLDER = "RF1Release";
	String RF2_ROOT_FOLDER = "RF2Release";
	String RF2_REFERENCE_SET_RELATIVE_ROOT_DIR = RF2_ROOT_FOLDER + File.separatorChar + "Refset";

	String RF1_CORE_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + TERMINOLOGY + File.separatorChar + CONTENT;
	String RF1_SUBSET_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + SUBSETS;
	String RF1_CROSSMAP_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + CROSSMAP;
	String RF2_CORE_RELATIVE_DIRECTORY = RF2_ROOT_FOLDER + File.separatorChar + TERMINOLOGY;
	String RF2_CONTENT_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + CONTENT;
	String RF2_MAP_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + MAP;
	String RF2_LANGUAGE_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + LANGUAGE;
	String RF2_METADATA_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + METADATA;
	
	String getRelativeDirectory();
	
	String getFileName();
	
	ComponentExportType getType();
	
	String[] getColumnHeaders();
	
	SnomedExportContext getExportContext();
	
	default void execute() throws IOException {
		Path workingDirPath = getExportContext().getReleaseRootPath().resolve(getRelativeDirectory());
		
		if (Files.notExists(workingDirPath)) {
			Files.createDirectories(workingDirPath);
		}
		
		Path filePath = workingDirPath.resolve(getFileName());
		
		if (Files.notExists(filePath)) {
			Files.createFile(filePath);
		}

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw")) {
			try (FileChannel fileChannel = randomAccessFile.getChannel()) {
					
				if (randomAccessFile.length() == 0L) {
					fileChannel.write(ByteBuffer.wrap(Joiner.on("\t").join(getColumnHeaders()).trim().getBytes(UTF_8)));
					fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
				}
				
				fileChannel.position(fileChannel.size());

				writeLines(line -> {
					try {
						fileChannel.write(ByteBuffer.wrap(line.getBytes(UTF_8)));
						fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
					} catch (IOException e) {
						throw new SnowowlRuntimeException(e);
					}
				});
			}
		}
	}

	void writeLines(Consumer<String> lineProcessor) throws IOException;
	
}