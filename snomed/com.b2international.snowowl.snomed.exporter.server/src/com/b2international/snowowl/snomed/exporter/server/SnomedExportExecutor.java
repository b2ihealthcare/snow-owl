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
package com.b2international.snowowl.snomed.exporter.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;
import com.google.common.base.Joiner;


/**
 * This class executes the corresponding SNOMED&nbsp;CT specific exporter and writes the result to the given temporary working directory.
 * The subsequent path and filename is got by the specified importer.
 *
 */
public class SnomedExportExecutor {
	
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final String RELEASE_BASE_DIRECTORY = "SnomedCT_Release_";
	
	private final String temporaryWorkingDirectory;
	private final File baseReleaseDir;
	private File releaseFilePath;
	private final SnomedExporter snomedExporter;
	private final String clientNamespace;
	
	public SnomedExportExecutor(final SnomedExporter snomedExporter, final String workingDirectory, final String clientNamespace) {
		this.snomedExporter = snomedExporter;
		this.clientNamespace = clientNamespace;
		
		baseReleaseDir = new File(workingDirectory + File.separatorChar + RELEASE_BASE_DIRECTORY + clientNamespace);
		if (!baseReleaseDir.exists()) {
			baseReleaseDir.mkdirs();
		}
		
		this.temporaryWorkingDirectory = workingDirectory;
	}
	
	public void execute(boolean append) throws IOException {
		releaseFilePath = write(append);
	}

	public File getTemporaryFile() {
		if (releaseFilePath == null) {
			throw new IllegalStateException("The exported file is not ready, this may sign that you are trying to get the file from a different thread or (more likely) you have not called the executor yet.");
		}
		return releaseFilePath;
	}
	
	public void deleteTemporaryFile() {
		if (releaseFilePath != null && releaseFilePath.exists()) {
			releaseFilePath.delete();
		}
	}
	
	public File writeExtendedDescriptionTypeExplanation() throws IOException {
		final File extendedDescriptionTypePath = new File(
				temporaryWorkingDirectory + 
				File.separatorChar + 
				RELEASE_BASE_DIRECTORY + 
				clientNamespace + 
				File.separatorChar + 
				snomedExporter.getRelativeDirectory() + 
				File.separatorChar + 
				"Extended_Description_Type_Explanation.txt");
		
		if (extendedDescriptionTypePath.getParentFile() != null) {
			extendedDescriptionTypePath.getParentFile().mkdirs();
		}
		
		extendedDescriptionTypePath.createNewFile();
		
		final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extendedDescriptionTypePath), "UTF-8"));
		writer.write(getExtendedDescriptionTypeExplanationFileFormat());
		writer.close();
		
		return extendedDescriptionTypePath;
	}
	
	private File write(boolean append) throws IOException {
		final File releaseFileRelativePath = new File(
				temporaryWorkingDirectory + 
				File.separatorChar + 
				RELEASE_BASE_DIRECTORY + 
				clientNamespace + 
				File.separatorChar + 
				snomedExporter.getRelativeDirectory() + 
				File.separatorChar + 
				snomedExporter.getFileName());
		
		if (releaseFileRelativePath.getParentFile() != null) {
			releaseFileRelativePath.getParentFile().mkdirs();
		}
		
		releaseFileRelativePath.createNewFile();
		
		FileChannel fileChannel = null;
		RandomAccessFile randomAccessFile = null;

		try {
			randomAccessFile = new RandomAccessFile(releaseFileRelativePath, "rw");
			fileChannel = randomAccessFile.getChannel();
			
			//if appended, set the position and omit the header
			if (append) {
				final long fileSize = fileChannel.size();
				fileChannel.position(fileSize);
			} else {
				writeFileHeader(fileChannel, snomedExporter.getColumnHeaders());
			}
			
			for (final String line : snomedExporter) {
				fileChannel.write(ByteBuffer.wrap(line.getBytes(UTF_8)));
				fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
			}
			
		} finally {
			
			if (null != randomAccessFile) {
				randomAccessFile.close();
			}
			
			if (null != fileChannel) {
				fileChannel.close();
			}
			
			if (null != snomedExporter) {
				try {
					snomedExporter.close();
				} catch (final Exception e) {
					try {
						snomedExporter.close();
					} catch (final Exception e1) {
						e.addSuppressed(e1);
					}
					throw new SnowowlRuntimeException("Error while releasing exporter.", e);
				}
			}
		}
		return releaseFileRelativePath;
	}
	
	private void writeFileHeader(final FileChannel fileChannel, final String[] columnHeaders) throws IOException {
		fileChannel.write(ByteBuffer.wrap(Joiner.on("\t").join(columnHeaders).trim().getBytes(UTF_8)));
		fileChannel.write(ByteBuffer.wrap(SnomedExporter.CR_LF.getBytes(UTF_8)));
	}

	private String getExtendedDescriptionTypeExplanationFileFormat() {
		return new StringBuilder("TypeID\tDescription type\n")
		.append(" 1\tPreferred term\n")
		.append(" 2\tSynonym\n")
		.append(" 3\tFully specified name\n")
		.append(" 4\tFull name\n")
		.append(" 5\tAbbreviation\n")
		.append(" 6\tProduct term\n")
		.append(" 7\tShort name\n")
		.append(" 8\tPreferred plural\n")
		.append(" 9\tNote\n")
		.append("10\tSearch term\n")
		.append("11\tAbbreviation plural\n")
		.append("12\tProduct term plural\n")
		.toString();
	}
}