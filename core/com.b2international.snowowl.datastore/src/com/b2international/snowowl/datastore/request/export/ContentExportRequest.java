/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.FileUtils;
import com.b2international.scripting.api.ScriptEngine;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.3
 */
final class ContentExportRequest implements Request<RepositoryContext, UUID> {

	@NotEmpty
	@JsonProperty
	private UUID id;

	@NotNull
//	@JsonProperty
	private ContentEntry rootEntry;

	public void setId(final UUID id) {
		this.id = id;
	}

	public void setRootEntry(final ContentEntry rootEntry) {
		this.rootEntry = rootEntry;
	}

	@Override
	public UUID execute(final RepositoryContext context) {
		Path exportDirectory = null;
		try {
			exportDirectory = Files.createTempDirectory("export");
		} catch (final IOException e) {
			throw new ContentExportException("Failed to create working directory for content export.", e);
		}
		
		try {
			visit(context, exportDirectory, rootEntry);
	
			Path exportArchive = null;
			try {
				final String directoryName = exportDirectory.getFileName().toString();
				final String archiveName = directoryName + ".zip";
				exportArchive = exportDirectory.getParent().resolve(archiveName);
				FileUtils.createZipArchive(exportDirectory.toFile(), exportArchive.toFile());
			} catch (final IOException e) {
				throw new ContentExportException("Failed to package exported contents.", e);
			}
			
			try {
				context.service(FileRegistry.class).upload(id, Files.newInputStream(exportArchive, StandardOpenOption.READ));
			} catch (IOException e) {
				throw new ContentExportException("Failed to upload export archive to the file registry.", e);
			}
			
			try {
				Files.deleteIfExists(exportArchive);
			} catch (IOException ignored) {
				// If everything else went OK, there's no need to block the export here
			}
			
			return id;

		} finally {
			if (exportDirectory != null) {
				FileUtils.deleteDirectory(exportDirectory.toFile());
			}
		}
	}

	private void visit(final RepositoryContext context, final Path contextPath, final ContentEntry entry) {
		if (entry instanceof ContentFolder) {
			visit(context, contextPath, (ContentFolder) entry);
		} else {
			visit(context, contextPath, (ContentFile) entry);
		}
	}

	private void visit(final RepositoryContext context, final Path contextPath, final ContentFolder folder) {
		final Path folderPath = contextPath.resolve(folder.getName());
		try {
			Files.createDirectory(folderPath);
		} catch (IOException e) {
			throw new ContentExportException("Couldn't create export directory '" + folder.getName() + "'.", e);
		}

		for (final ContentEntry child : folder.getChildren()) {
			visit(context, folderPath, child);
		}
	}

	private void visit(final RepositoryContext context, final Path contextPath, final ContentFile file) {
		final Path filePath = contextPath.resolve(file.getName());
		final String script = file.getScript();
		final ObjectMapper objectMapper = new ObjectMapper();
		final CsvMapper csvMapper = new CsvMapper();
		final CsvSchema schema = CsvSchema.builder()
				.setNullValue("")
				.setColumnSeparator('\t')
				.setLineSeparator("\r\n")
				.addColumns(file.getColumns(), ColumnType.STRING)
				.build();

		try (final SequenceWriter csvWriter = csvMapper.writer(schema).writeValues(filePath.toFile())) {
			file.getRequestBuilderMap().forEach((branch, requestBuilder) -> {
				String scrollId = null;
				PageableCollectionResource<? extends IComponent> results = null;

				while (results == null || !results.isEmpty()) { 
					requestBuilder.setScrollId(scrollId);
					
					final RevisionIndexReadRequest<? extends PageableCollectionResource<? extends IComponent>> indexReadRequest = new RevisionIndexReadRequest<>(requestBuilder.build());
					final BranchRequest<? extends PageableCollectionResource<? extends IComponent>> branchRequest = new BranchRequest<>(branch, indexReadRequest);
					results = branchRequest.execute(context);

					final List<? extends IComponent> components = results.getItems();
					
					final List<Map<String, Object>> componentsAsMap = components.stream()
							.map(component -> objectMapper.convertValue(component, Map.class))
							.collect(Collectors.toList());
					
					final Map<String, Object> scriptParams = ImmutableMap.<String, Object>builder()
							.putAll(file.getScriptParams())
							.put("components", componentsAsMap)
							.build();
					
					final List<Map<String, String>> rowsAsMap = ScriptEngine.run("painless", null, script, scriptParams);

					try {
						csvWriter.writeAll(rowsAsMap);
					} catch (final IOException e) {
						throw new ContentExportException("Couldn't write rows to export file '" + file.getName() + "'.", e); 
					}

					scrollId = results.getScrollId();
				}
			});
		} catch (IOException e) {
			throw new ContentExportException("Couldn't writer for export file '" + file.getName() + "'.", e);
		}
	}
}
