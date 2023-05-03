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
package com.b2international.commons.test.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.junit.After;
import org.junit.Test;

import com.b2international.commons.io.PathUtils;
import com.google.common.io.Resources;

/**
 * @since 7.22.0
 */
public class PathUtilsTests {

	private Path tempDirectory;
	private Path tempDirectory2;
	private Path tempDirectory3;

	@After
	public void after() throws IOException {

		if (tempDirectory != null && Files.exists(tempDirectory)) {
			PathUtils.deleteDirectory(tempDirectory);
		}

		if (tempDirectory2 != null && Files.exists(tempDirectory2)) {
			PathUtils.deleteDirectory(tempDirectory2);
		}
		
		if (tempDirectory3 != null && Files.exists(tempDirectory3)) {
			PathUtils.deleteDirectory(tempDirectory3);
		}

	}

	@Test
	public void testDeleteEmptyDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		assertTrue(Files.exists(tempDirectory));

		PathUtils.deleteDirectory(tempDirectory);
		assertFalse(Files.exists(tempDirectory));

	}

	@Test
	public void testDeleteDirectoryWithContent() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.isRegularFile(tempFile));

		PathUtils.deleteDirectory(tempDirectory);
		assertFalse(Files.exists(tempDirectory));
		assertFalse(Files.exists(tempFile));

	}
	
	@Test
	public void testDeleteDirectoryWithNestedContent() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		tempDirectory3 = Files.createDirectory(tempDirectory.resolve("inner-dir2"));
		
		final Path tempFile = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory3.resolve("test-file.zip"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.isRegularFile(tempFile));
		assertTrue(Files.exists(tempFile2));
		assertTrue(Files.isRegularFile(tempFile2));

		PathUtils.deleteDirectory(tempDirectory);
		
		// assert everything is deleted including root directory and all sub-directories
		assertFalse(Files.exists(tempFile));
		assertFalse(Files.exists(tempDirectory2));
		
		assertFalse(Files.exists(tempFile2));
		assertFalse(Files.exists(tempDirectory3));
		
		assertFalse(Files.exists(tempDirectory));

	}

	@Test
	public void testCleanDirectoryWithContent() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory2.resolve("test-file.zip"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.isRegularFile(tempFile));
		assertTrue(Files.exists(tempFile2));
		assertTrue(Files.isRegularFile(tempFile2));

		PathUtils.cleanDirectory(tempDirectory);
		
		assertTrue(Files.exists(tempDirectory));
		
		assertFalse(Files.exists(tempFile));
		assertFalse(Files.exists(tempFile2));
		assertFalse(Files.exists(tempDirectory2));

	}

	@Test
	public void testDeleteDirectoryWithFilter() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.exists(tempFile2));
		assertTrue(Files.isRegularFile(tempFile2));

		PathUtils.deleteDirectory(tempDirectory, Set.of(tempFile));
		assertTrue(Files.exists(tempDirectory));
		assertTrue(Files.exists(tempFile));
		assertFalse(Files.exists(tempFile2));

	}
	
	@Test
	public void testDeleteDirectoryWithNestedFilter() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory2.resolve("test-file.zip"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.isRegularFile(tempFile));
		assertTrue(Files.exists(tempFile2));
		assertTrue(Files.isRegularFile(tempFile2));

		PathUtils.deleteDirectory(tempDirectory, Set.of(tempFile2));
		
		assertTrue(Files.exists(tempDirectory));
		assertTrue(Files.exists(tempDirectory2));
		
		assertFalse(Files.exists(tempFile));
		assertTrue(Files.exists(tempFile2));

	}
	
	@Test
	public void testDeleteDirectoryWithNestedFilter2() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		
		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		tempDirectory3 = Files.createDirectory(tempDirectory.resolve("inner-dir2"));
		
		final Path tempFile = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory3.resolve("test-file.zip"));

		assertTrue(Files.exists(tempFile));
		assertTrue(Files.isRegularFile(tempFile));
		assertTrue(Files.exists(tempFile2));
		assertTrue(Files.isRegularFile(tempFile2));

		PathUtils.deleteDirectory(tempDirectory, Set.of(tempFile2));
		
		// assert root dir exist
		assertTrue(Files.exists(tempDirectory));
		
		// assert parent dir of excluded file exists 
		assertTrue(Files.exists(tempDirectory3));
		
		// assert excluded file exists
		assertTrue(Files.exists(tempFile2));
		
		// assert everything else is deleted
		assertFalse(Files.exists(tempFile));
		assertFalse(Files.exists(tempDirectory2));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateZipArchiveFail() throws IOException {
		PathUtils.createZipArchive(Paths.get("test"), Paths.get("test.zip"));
	}

	@Test
	public void testCreateAndUnzipArchive() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		Files.write(tempFile, List.of("first line"), StandardOpenOption.APPEND);

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);

		assertTrue(Files.exists(archive));

		tempDirectory2 = PathUtils.unzipArchive(archive);

		final Path unzippedTempFile = tempDirectory2.resolve(tempFile.getFileName());
		final Path unzippedTempFile2 = tempDirectory2.resolve(tempFile2.getFileName());

		assertTrue(Files.exists(unzippedTempFile));
		assertTrue(Files.lines(unzippedTempFile).findFirst().get().equals("first line"));

		assertTrue(Files.exists(unzippedTempFile2));

	}

	@Test
	public void testCreateAndUnzipArchiveWithFilter() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		Files.write(tempFile, List.of("first line"), StandardOpenOption.APPEND);

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive, "*.zip");

		assertTrue(Files.exists(archive));

		tempDirectory2 = Files.createTempDirectory("commons-test");

		PathUtils.unzipArchive(archive, tempDirectory2);

		final Path unzippedTempFile = tempDirectory2.resolve(tempFile.getFileName());
		final Path unzippedTempFile2 = tempDirectory2.resolve(tempFile2.getFileName());

		assertTrue(Files.exists(unzippedTempFile));
		assertTrue(Files.lines(unzippedTempFile).findFirst().get().equals("first line"));

		assertFalse(Files.exists(unzippedTempFile2));

	}

	@Test
	public void testCreateAndUnzipArchiveWithFolders() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");

		final Path subDirectory = Files.createDirectory(tempDirectory.resolve("subdir"));

		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(subDirectory.resolve("test-file.zip"));

		Files.write(tempFile, List.of("first line"), StandardOpenOption.APPEND);

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);

		assertTrue(Files.exists(archive));

		tempDirectory2 = PathUtils.unzipArchive(archive);

		final Path unzippedTempFile = tempDirectory2.resolve(tempFile.getFileName());

		final Path unzippedSubDirectory = tempDirectory2.resolve("subdir");
		final Path unzippedTempFile2 = unzippedSubDirectory.resolve(tempFile2.getFileName());

		assertTrue(Files.exists(unzippedTempFile));
		assertTrue(Files.isRegularFile(unzippedTempFile));
		assertTrue(Files.lines(unzippedTempFile).findFirst().get().equals("first line"));

		assertTrue(Files.exists(unzippedSubDirectory));
		assertTrue(Files.isDirectory(unzippedSubDirectory));

		assertTrue(Files.exists(unzippedTempFile2));
		assertTrue(Files.isRegularFile(unzippedTempFile2));

	}

	@Test
	public void testUnzipArchiveWithoutFolders() throws Exception {

		/*
		 * This zip archive does not contain directory entries, just the following:
		 *  subdir/test-file.zip
		 *  test-file.txt
		 */
		final Path archive = Paths.get(FileLocator.toFileURL(Resources.getResource(PathUtilsTests.class, "test_wo_folders.zip")).toURI());

		tempDirectory = PathUtils.unzipArchive(archive);

		final Path unzippedTempFile = tempDirectory.resolve("test-file.txt");

		final Path subDirectory = tempDirectory.resolve("subdir");
		final Path unzippedTempFile2 = subDirectory.resolve("test-file.zip");

		assertTrue(Files.exists(unzippedTempFile));
		assertTrue(Files.isRegularFile(unzippedTempFile));

		assertTrue(Files.lines(unzippedTempFile).findFirst().get().equals("line"));

		assertTrue(Files.exists(subDirectory));
		assertTrue(Files.isDirectory(subDirectory));

		assertTrue(Files.isRegularFile(unzippedTempFile2));
		assertTrue(Files.exists(unzippedTempFile2));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnzipArchiveFailWithInvalidFile() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		PathUtils.unzipArchive(Paths.get("test"), tempDirectory);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnzipArchiveFailWithInvalidDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path archive = tempDirectory.resolve("tests.zip");

		PathUtils.unzipArchive(archive, Paths.get("test"));

	}

	@Test
	public void testGetFileExtension() throws IOException {
		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		assertEquals("txt", PathUtils.getFileExtension(tempFile));
	}

	@Test
	public void testGetFileExtensionWithMultipleDots() throws IOException {
		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test.file.txt"));
		assertEquals("txt", PathUtils.getFileExtension(tempFile));
	}

	@Test
	public void testGetFileNameWithoutExtension() throws IOException {
		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		assertEquals("test-file", PathUtils.getFileNameWithoutExtension(tempFile));
	}

	@Test
	public void testGetFileNameWithoutExtensionWithMultipleDots() throws IOException {
		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test.file.txt"));
		assertEquals("test.file", PathUtils.getFileNameWithoutExtension(tempFile));
	}

	@Test
	public void testFindSingleFileInRootDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Optional<Path> file = PathUtils.findFile(tempDirectory, "*.txt");

		assertTrue(file.isPresent());
		assertEquals(tempFile, file.get());

	}

	@Test
	public void testFindMultipleFilesInRootDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Collection<Path> files = PathUtils.findFiles(tempDirectory, "*test-file*");

		assertEquals(2, files.size());
		assertTrue(files.stream().anyMatch(path -> path.equals(tempFile)));
		assertTrue(files.stream().anyMatch(path -> path.equals(tempFile2)));

	}

	@Test
	public void testFindSingleFileInDirectoryTree() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		Files.createFile(tempDirectory.resolve("test-file.txt"));
		Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-inner-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Optional<Path> file = PathUtils.findFileRecursive(tempDirectory, "*inner*.txt");

		assertTrue(file.isPresent());
		assertEquals(tempFile3, file.get());

	}

	@Test
	public void testFindMultipleFilesInDirectoryTree() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Collection<Path> file = PathUtils.findFilesRecursive(tempDirectory, "*test-file*");

		assertEquals(3, file.size());
		assertTrue(file.stream().anyMatch(path -> path.equals(tempFile)));
		assertTrue(file.stream().anyMatch(path -> path.equals(tempFile2)));
		assertTrue(file.stream().anyMatch(path -> path.equals(tempFile3)));

	}

	@Test
	public void testFindSingleFileInZipRootDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		Files.createFile(tempDirectory2.resolve("test-file.txt"));

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);
		assertTrue(Files.isRegularFile(archive));

		final Optional<Path> file = PathUtils.findFile(archive, "*.txt");

		assertTrue(file.isPresent());
		assertEquals("/" + tempFile.getFileName().toString(), file.get().toString());

	}

	@Test
	public void testFindMultipleFilesInZipRootDirectory() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);
		assertTrue(Files.isRegularFile(archive));

		final Collection<Path> files = PathUtils.findFiles(archive, "*test-file*");

		assertEquals(2, files.size());

		assertTrue(files.stream().anyMatch(path -> path.toString().equals("/" + tempFile.getFileName().toString())));
		assertTrue(files.stream().anyMatch(path -> path.toString().equals("/" + tempFile2.getFileName().toString())));

	}

	@Test
	public void testFindSingleFileInZipDirectoryTree() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		Files.createFile(tempDirectory.resolve("test-file.txt"));
		Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-inner-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);
		assertTrue(Files.isRegularFile(archive));

		final Optional<Path> file = PathUtils.findFileRecursive(archive, "*inner*.txt");

		assertTrue(file.isPresent());
		assertEquals("/" + tempFile3.getParent().getFileName() + "/" + tempFile3.getFileName(), file.get().toString());

	}

	@Test
	public void testFindMultipleFilesInZipDirectoryTree() throws IOException {

		tempDirectory = Files.createTempDirectory("commons-test");
		final Path tempFile = Files.createFile(tempDirectory.resolve("test-file.txt"));
		final Path tempFile2 = Files.createFile(tempDirectory.resolve("test-file.zip"));

		tempDirectory2 = Files.createDirectory(tempDirectory.resolve("inner-dir"));
		assertTrue(Files.isDirectory(tempDirectory2));
		final Path tempFile3 = Files.createFile(tempDirectory2.resolve("test-file.txt"));
		assertTrue(Files.exists(tempFile3));

		final Path archive = tempDirectory.resolve("archive.zip");

		PathUtils.createZipArchive(tempDirectory, archive);
		assertTrue(Files.isRegularFile(archive));

		final Collection<Path> file = PathUtils.findFilesRecursive(archive, "*test-file*");

		assertEquals(3, file.size());
		assertTrue(file.stream().anyMatch(path -> path.toString().equals("/" + tempFile.getFileName().toString())));
		assertTrue(file.stream().anyMatch(path -> path.toString().equals("/" + tempFile2.getFileName().toString())));
		assertTrue(file.stream().anyMatch(path -> path.toString().equals("/" + tempFile3.getParent().getFileName().toString() + "/" + tempFile3.getFileName().toString())));

	}

}
