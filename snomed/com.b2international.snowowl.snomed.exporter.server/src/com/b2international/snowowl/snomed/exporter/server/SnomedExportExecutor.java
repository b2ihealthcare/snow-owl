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
package com.b2international.snowowl.snomed.exporter.server;

import static com.google.common.base.Charsets.UTF_8;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;
import com.google.common.base.Joiner;


/**
 * This class executes the corresponding SNOMED CT specific exporter and writes the result to the given temporary working directory.
 *
 */
public class SnomedExportExecutor {

	private final SnomedExporter exporter;
	
	public SnomedExportExecutor(final SnomedExporter exporter) {
		this.exporter = exporter;
	}

	public void execute() throws IOException {
		
		Path workingDirPath = Paths.get(exporter.getExportContext().getReleaseRootPath().toString(), exporter.getRelativeDirectory());
		
		if (Files.notExists(workingDirPath)) {
			Files.createDirectories(workingDirPath);
		}
		
		Path filePath = Paths.get(workingDirPath.toString(), exporter.getFileName());
		
		if (Files.notExists(filePath)) {
			Files.createFile(filePath);
		}

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw")) {
			try (FileChannel fileChannel = randomAccessFile.getChannel()) {
					
				if (randomAccessFile.length() == 0L) {
					writeFileHeader(fileChannel, exporter.getColumnHeaders());
				}
				
				fileChannel.position(fileChannel.size());
				
				for (final String line : exporter) {
					fileChannel.write(ByteBuffer.wrap(line.getBytes(UTF_8)));
					fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
				}
			}
		}
		
	}
	
	private void writeFileHeader(final FileChannel fileChannel, final String[] columnHeaders) throws IOException {
		fileChannel.write(ByteBuffer.wrap(Joiner.on("\t").join(columnHeaders).trim().getBytes(UTF_8)));
		fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
	}
	
}