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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
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

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public final class FileUtils {

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";

	public static String readToString(final File file) throws IOException {
		return readToString(new FileReader(file));
	}

	public static String readToString(final InputStream is) throws IOException {
		return readToString(new InputStreamReader(is));
	}

	public static String readToString(final Reader reader) throws IOException {

		final StringBuilder builder = new StringBuilder();

		final char[] buf = new char[1024];
		int i = 0;
		while((i = reader.read(buf)) >= 0) {
			builder.append(buf, 0, i);
		}

		return builder.toString();
	}

	/**
	 * Creates a temporary file by copying the content of the original file given
	 * with the URL argument. The temporary file will be deleted on graceful JVM halt.
	 * <p>May return with {@code null} if the file cannot be created.
	 * @param url the URL pointing to the original file to copy.
	 * @return the temporary copy file. Or {@code null} if the copy failed.
	 * @see File#deleteOnExit()
	 */
	public static File copyContentToTempFile(final URL url) {
		checkNotNull(url, "url");

		try (final InputStream is = url.openStream()) {
			return copyContentToTempFile(is, getFileName(url));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a temporary file by copying the content of the original file given
	 * as an input stream and the file name. The temporary file will be deleted on graceful JVM halt.
	 * <p>May return with {@code null} if the file cannot be created.
	 * <p>Callers are responsible for closing the input stream.
	 * @param is the input stream to the file.
	 * @param the file name. Can be null. If {@code null} a random UUID will be assigned as the temporary file name.
	 * @return the temporary copy file. Or {@code null} if the copy failed.
	 * @see File#deleteOnExit()
	 */
	public static File copyContentToTempFile(final InputStream is, final String fileName) {
		checkNotNull(is, "is");

		try {
			final File tmpDirectory = com.google.common.io.Files.createTempDir();
			final File tmpFile = new File(tmpDirectory, isEmpty(fileName) ? randomUUID().toString() : fileName);
			tmpFile.deleteOnExit();

			new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return is;
				}
			}.copyTo(Files.asByteSink(tmpFile));
			return tmpFile;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns with the file name (with the extension) extracted from the URL.
	 * <br>May throw runtime exception if the URL is invalid or does not point to
	 * a file that has extension.
	 * @param url the URL pointing to a file.
	 * @return the name of the file.
	 */
	public static final String getFileName(final URL url) {
		final String urlString = url.toString();
		return urlString.substring(urlString.lastIndexOf('/') + 1, urlString.length());
	}

	public static void copy(final File sourceLocation, final File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			final String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copy(new File(sourceLocation, children[i]),
						new File(targetLocation, children[i]));
			}
		} else {

			final InputStream in = new FileInputStream(sourceLocation);
			final OutputStream out = new FileOutputStream(targetLocation);

			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

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

		final FileInputStream fis = new FileInputStream(zipFile);
		final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

		ZipEntry entry = zis.getNextEntry();
		
		while (entry != null) {

			File newFile = new File(rootDirectoryToUnZip, entry.getName());
			
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

		zis.close();
		fis.close();
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
	 * @param directory to find the file within
	 * @param path matcher expression
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