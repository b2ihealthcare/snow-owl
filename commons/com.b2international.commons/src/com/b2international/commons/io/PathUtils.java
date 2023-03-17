/*
 * Copyright 2023 B2i Healthcare, http://b2ihealthcare.com
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
package com.b2international.commons.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.base.Strings;

/**
 * @since 7.22.0
 */
public class PathUtils {

	/**
	 * Delete the specified directory and all files recursively
	 */
	public static void deleteDirectory(final Path directory) throws IOException {
		deleteDirectory(directory, Set.of());
	}

	/**
	 * Clean the specified directory but keep the directory itself
	 */
	public static void cleanDirectory(final Path directory) throws IOException {
		deleteDirectory(directory, Set.of(directory.toString()));
	}

	/**
	 * Delete all files within the specified directory except the exclusions specified. If there are none, the directory is deleted as well.
	 * The exclusions must be declared as full paths.
	 */
	public static void deleteDirectory(final Path directory, Collection<String> exclusions) throws IOException {

		checkArgument(Files.isDirectory(directory), "The specified path is not a directory");

		final List<Path> filteredPaths = Files.walk(directory)
				.sorted(Comparator.reverseOrder())
				.filter(path -> exclusions.stream().noneMatch(p -> p.equals(path.toString())))
				.collect(toList());

		for (final Path path : filteredPaths) {

			// do not try to delete the containing directory when there are excluded files present inside
			if (path.equals(directory) && Files.list(directory).findFirst().isPresent()) {
				continue;
			}

			Files.delete(path);
		}

	}

	/**
	 * Create a zip archive from the specified directory using the archive path specified.
	 */
	public static Path createZipArchive(final Path directory, final Path archive) throws IOException {
		return createZipArchive(directory, archive, null);
	}

	/**
	 * Create a zip archive from the specified directory using the archive path specified. Exclusion filters can be specified using the PathMatcher format e.g.: *.zip
	 */
	public static Path createZipArchive(final Path directory, final Path archive, final String exclusionPathMatcherExpression) throws IOException {

		checkArgument(Files.isDirectory(directory), "The specified directory is not a directory");

		final URI uri = URI.create("jar:" + archive.toUri().toString());

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, Map.of("create", "true"))) {

			List<Path> sortedPaths = Files.walk(directory)
					.sorted() // directories are encountered first
					.collect(toList());

			if (!Strings.isNullOrEmpty(exclusionPathMatcherExpression)) {

				final PathMatcher pathMatcher = directory.getFileSystem().getPathMatcher("glob:**/" + exclusionPathMatcherExpression);
				sortedPaths = sortedPaths.stream().filter(path -> !pathMatcher.matches(path)).collect(toList());

			}

			for (final Path path : sortedPaths) {

				final Path relativePath = directory.relativize(path);
				final Path zipPath = zipfs.getPath(relativePath.toString());

				if (Files.isDirectory(path)) {
					Files.createDirectories(zipPath);
				} else {
					Files.copy(path, zipPath, StandardCopyOption.REPLACE_EXISTING);
				}

			}

		}


		return archive;

	}

	/**
	 * Unzip a zip archive to a temporary directory. The returned path is the temporary directory's path
	 */
	public static Path unzipArchive(Path sourceFile) throws IOException {
		final Path tempDir = Files.createTempDirectory("");
		unzipArchive(sourceFile, tempDir);
		return tempDir;
	}

	/**
	 * Unzip a zip archive to the specified directory
	 */
	public static void unzipArchive(Path sourceFile, Path targetDirectory) throws IOException {

		checkArgument(Files.isRegularFile(sourceFile), "Source file is not a regular file");
		checkArgument(Files.isDirectory(targetDirectory), "Target directory is not a directory");

		try (final FileInputStream fis = new FileInputStream(sourceFile.toFile()); ZipInputStream zis = new ZipInputStream(fis)) {

			ZipEntry entry = zis.getNextEntry();

			while (entry != null) {

				final Path normalizedPath = normalizeZipEntryPath(entry, targetDirectory);

				// Files.isDirectory(normalizedPath) won't work here, because ZipFileSystem cannot determine attributes properly.
				// ZipEntry.isDirectory() however uses a path based check, which works as expected.
				if (entry.isDirectory()) {
					Files.createDirectories(normalizedPath);
				} else {

					if (normalizedPath.getParent() != null && Files.notExists(normalizedPath.getParent())) {
						Files.createDirectories(normalizedPath.getParent());
					}

					Files.copy(zis, normalizedPath, StandardCopyOption.REPLACE_EXISTING);

				}

				entry = zis.getNextEntry();

			}

		}

	}

	/**
	 * Get the extension of the file specified
	 */
	public static String getFileExtension(Path path) {
		checkNotNull(path);
		final String fileName = path.getFileName().toString();
		final int dotIndex = fileName.lastIndexOf('.');
		return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
	}

	/**
	 * Get the name of the file without the extension
	 */
	public static String getFileNameWithoutExtension(Path path) {
		checkNotNull(path);
		final String fileName = path.getFileName().toString();
		final int dotIndex = fileName.lastIndexOf('.');
		return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
	}

	/**
	 * Find a single file in either the root of a folder or the root of a zip archive using the filter expression
	 */
	public static Optional<Path> findFile(Path source, String pathMatcherExpression) throws IOException {
		return findFiles(source, pathMatcherExpression, false).stream().findFirst();
	}

	/**
	 * Find all files in either the root of a folder or the root of a zip archive using the filter expression
	 */
	public static Collection<Path> findFiles(Path source, String pathMatcherExpression) throws IOException {
		return findFiles(source, pathMatcherExpression, false);
	}

	/**
	 * Find a single file recursively in either a folder or a zip archive using the filter expression
	 */
	public static Optional<Path> findFileRecursive(Path source, String pathMatcherExpression) throws IOException {
		return findFiles(source, pathMatcherExpression, true).stream().findFirst();
	}

	/**
	 * Find all files recursively in either a folder or a zip archive using the filter expression
	 */
	public static Collection<Path> findFilesRecursive(Path source, String pathMatcherExpression) throws IOException {
		return findFiles(source, pathMatcherExpression, true);
	}

	private static Collection<Path> findFiles(Path source, String pathMatcherExpression, boolean recursive) throws IOException {

		if (Files.isDirectory(source)) {
			return findFiles(source.getFileSystem(), Set.of(source), pathMatcherExpression, recursive);
		} else if (getFileExtension(source).equals("zip")) {
			try (FileSystem zipFileSystem = FileSystems.newFileSystem(source, null)) {
				return findFiles(zipFileSystem, zipFileSystem.getRootDirectories(), pathMatcherExpression, recursive);
			}
		}

		throw new UnsupportedOperationException("Cannot find file matches if source is not a directory or an archive");

	}

	private static Collection<Path> findFiles(FileSystem fileSystem, Iterable<Path> directoriesToIterate, String pathMatcherExpression, boolean recursive) throws IOException {

		final Set<Path> matches = newHashSet();

		final PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:**/" + pathMatcherExpression);

		for (final Path directory : directoriesToIterate) {
			if (recursive) {
				Files.walk(directory).filter(path -> pathMatcher.matches(path)).forEach(matches::add);
			} else {
				Files.list(directory).filter(path -> pathMatcher.matches(path)).forEach(matches::add);
			}
		}

		return matches;

	}

	/**
	 * Protection against zip slip attack, see https://security.snyk.io/research/zip-slip-vulnerability
	 */
	private static Path normalizeZipEntryPath(ZipEntry zipEntry, Path targetDirectory) throws IOException {

		final Path normalizedPath = targetDirectory.resolve(zipEntry.getName()).normalize();

		if (!normalizedPath.startsWith(targetDirectory)) {
			throw new IOException("Bad zip entry: " + zipEntry.getName());
		}

		return normalizedPath;

	}

	private PathUtils() {}

}
