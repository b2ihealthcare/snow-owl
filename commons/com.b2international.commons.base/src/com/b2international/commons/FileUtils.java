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
package com.b2international.commons;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.base.CharMatcher;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public final class FileUtils {

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";
	
	public static final char[] INVALID_RESOURCE_CHARACTERS = { '\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0' };
	
	public static final CharMatcher INVALID_RESOURCE_MATCHER = CharMatcher.whitespace()
			.or(CharMatcher.anyOf(String.valueOf(INVALID_RESOURCE_CHARACTERS)))
			.or(CharMatcher.javaIsoControl())
			.precomputed();

	public static boolean deleteDirectory(final File path) {
		cleanDirectory(path);
		return (path.delete());
	}


	public static void cleanDirectory(final File path) {
		if (!path.isDirectory()) {
			throw new IllegalArgumentException(path.getAbsolutePath() + " is not a directory.");
		}

		final File[] files = path.listFiles();
		if (!isEmpty(files)) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
	}
	
	/**
	 * Creates a zip archive with the filtered contents of the given root directory.
	 * If no filter is specified (null), all content is compressed 
	 * @param rootDirectory the directory with the content to be compressed
	 * @param archiveFile the zip file to create
	 * @param filter file filter to filter the content compressed
	 * @return the archive file
	 * @throws IOException
	 */
	public static File createZipArchive(final File rootDirectory, final File archiveFile, final FileFilter filter) throws IOException {
		
		checkArgument(rootDirectory != null
				&& rootDirectory.isDirectory() 
				&& rootDirectory.canWrite(), "The given directory %s is not found or it's read-only", rootDirectory);
		
		checkNotNull(archiveFile, "zipFile");

		try (FileOutputStream fos = new FileOutputStream(archiveFile)) {
			try (ZipOutputStream zos = new ZipOutputStream(fos)) {

				final Deque<File> queue = new LinkedList<File>();
				queue.push(rootDirectory);

				while (!queue.isEmpty()) {
					final File first = queue.pop();
					final File[] content = first.listFiles(filter);
					if (content != null) {
						for (final File file : content) {
							final String relativeName = getRelativeName(rootDirectory, file);
							if (file.isDirectory()) {
								zos.putNextEntry(new ZipEntry(relativeName));
								queue.push(file);
							} else {
								zos.putNextEntry(new ZipEntry(relativeName));
								try (FileInputStream fis = new FileInputStream(file)) {
									copy(fis, zos);
								}
							}
							zos.closeEntry();
						}
					}
					// TODO include empty dirs???
				}
			}
		}
		
		return archiveFile; 
	}

	/**
	 * Creates a zip archive with the contents of the given root directory.
	 * @param rootDirectory the directory with the content to be compressed
	 * @param archiveFile the zip file to create
	 * @return the archive file
	 * @throws IOException
	 */
	public static File createZipArchive(final File rootDirectory, final File archiveFile) throws IOException {
		return createZipArchive(rootDirectory, archiveFile, null);
		
	}
	
	/**
	 * Returns the name of all ZipEntries in a zip archive. Inclusion of directories can be set by the includeDirectories parameter.
	 * 
	 * @param archive
	 *            - the zip file
	 * @param includeDirectories
	 *            - include directories or not
	 * @return A list of zip entry names
	 * @throws IOException
	 */
	public static Collection<String> getZipEntries(final File archive, final boolean includeDirectories) throws IOException {
		final ArrayList<String> entries = newArrayList();
		try (ZipFile zipFile = new ZipFile(archive)) {
			final ArrayList<? extends ZipEntry> zipEntries = Collections.list(zipFile.entries());
			for (final ZipEntry zipEntry : zipEntries) {
				if (!includeDirectories) {
					if (!zipEntry.isDirectory()) {
						entries.add(zipEntry.getName());
					}
				} else {
					entries.add(zipEntry.getName());
				}
			}
		}
		return entries;
	}

	/**
	 * Takes a zip file and decompress it to the given root directory.
	 * If the directory where to put the contents of the zip file is not exist it creates it.
	 *
	 */
	public static void decompressZipArchive(final File zipFile, final File rootDirectoryToUnZip) throws IOException {
		
		final int BUFFER = 2048;

		if (rootDirectoryToUnZip.exists() && rootDirectoryToUnZip.isFile()) {
			throw new IllegalArgumentException("Passed argument points to an existing file not a directory.");
		}

		if (!rootDirectoryToUnZip.exists()) {
			rootDirectoryToUnZip.mkdir();
		}

		try (
			final FileInputStream fis = new FileInputStream(zipFile);
			final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
			
			ZipEntry entry = zis.getNextEntry();
			
			while (entry != null) {
				
				File newFile = new File(rootDirectoryToUnZip, entry.getName());
				if (!newFile.toPath().normalize().startsWith(rootDirectoryToUnZip.toPath())) {
					throw new IOException("Bad zip entry");
				}
				
				if (entry.isDirectory()) {
					newFile.mkdir();
				} else {
					
					File parentFile = newFile.getParentFile();
					if (parentFile != null && !parentFile.exists()) {
						parentFile.mkdirs();
					}
					
					int count;
					final byte data[] = new byte[BUFFER];
					
					final FileOutputStream fos = new FileOutputStream(newFile);
					final BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
					
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					
					dest.flush();
					fos.flush();
					dest.close();
					fos.close();
				}
				
				entry = zis.getNextEntry();
			}
		}
		
	}

	private static void copy(final InputStream is, final OutputStream os) throws IOException {
		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
	}

	private static String getRelativeName(final File directory, final File file) {
		final String name = directory.toURI().relativize(file.toURI()).getPath();
		if (file.isDirectory()) {
			// URI always uses forward slashes (http://www.ietf.org/rfc/rfc1738.txt), regardless of platform, therefore the OS specific file separator
			// on windows ('\' character) is not working in this case
			return name.endsWith("/") ? name : name + "/";
		}
		return name;
	}

	private FileUtils() {
		// Prevent instantiation
	}

	/**
	 * Collects the occurences into the passed Set (recursively)
	 */
	public static void search(final File rootPath, final String fileName, final Set<File> occuranceCollectorSet) {
		if (!rootPath.exists()) {
			return;
		}

		if (rootPath.isFile()) {
			if (rootPath.getName().equals(fileName)) {
				occuranceCollectorSet.add(rootPath);
			}

			return;
		}

		final File[] dirContent = rootPath.listFiles();

		for (final File entry : dirContent) {
			if (entry.isDirectory()) {
				search(entry, fileName, occuranceCollectorSet);
			}

			if (entry.getName().equals(fileName)) {
				occuranceCollectorSet.add(entry);
			}
		}
	}
	
	/**
	 * Returns a file path from the given directory that matches the path matcher expression.
	 * It is expected to have a single file in the directory that matches the expression.
	 * @param workDir to find the file within
	 * @param pathMatcherExpression
	 * @return path for the matched file.
	 */
	public static Path getFileFromWorkFolder(String workDir, String pathMatcherExpression) throws IOException {
		
		Path workDirPath = Paths.get(workDir);
		final PathMatcher filter = workDirPath.getFileSystem().getPathMatcher("glob:**/" + pathMatcherExpression);
		
		try (final Stream<Path> stream = java.nio.file.Files.list(workDirPath)) {
			Set<Path> filesFromWorkFolder = stream.filter(filter::matches).collect(Collectors.toSet());
			if (filesFromWorkFolder.isEmpty()) {
				throw new RuntimeException("Could not find file in the work folder " +workDir + " for the path expression: " + pathMatcherExpression);
			} else if (filesFromWorkFolder.size() > 1) {
				throw new RuntimeException("Found more than one file in work folder for the path expression: " + pathMatcherExpression);
			}
			return filesFromWorkFolder.iterator().next();
		}
	}
}