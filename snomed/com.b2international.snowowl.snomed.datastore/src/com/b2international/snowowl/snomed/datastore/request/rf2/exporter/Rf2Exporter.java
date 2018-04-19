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
package com.b2international.snowowl.snomed.datastore.request.rf2.exporter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequestBuilder;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

/**
 * @since 6.3
 */
public abstract class Rf2Exporter<B extends SnomedSearchRequestBuilder<B, R>, R extends PageableCollectionResource<C>, C extends SnomedComponent> {

	private static final Joiner TAB_JOINER = Joiner.on('\t');
	
	private static final String CR_LF = "\r\n";

	private static final int BATCH_SIZE = 10000;
	
	// Parameters used for file name calculations
	protected final Rf2ReleaseType releaseType;
	protected final String countryNamespaceElement;
	protected final String namespaceFilter;
	protected final String archiveEffectiveTime;
	protected final boolean includePreReleaseContent;

	private final String transientEffectiveTime;
	private final Collection<String> modules;

	public Rf2Exporter(final Rf2ReleaseType releaseType, 
			final String countryNamespaceElement,
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules) {

		this.releaseType = releaseType;
		this.countryNamespaceElement = countryNamespaceElement;
		this.namespaceFilter = namespaceFilter;
		this.transientEffectiveTime = transientEffectiveTime;
		this.archiveEffectiveTime = archiveEffectiveTime;
		this.includePreReleaseContent = includePreReleaseContent;
		this.modules = modules;
	}

	protected abstract Path getRelativeDirectory();

	protected abstract Path getFileName();

	protected abstract String[] getHeader();

	protected abstract B createSearchRequestBuilder();

	protected abstract Stream<List<String>> getMappedStream(R results, RepositoryContext context, String branch);

	protected final String getEffectiveTime(final SnomedComponent component) {
		if (component.getEffectiveTime() == null) {
			// FIXME: Should we add a test for unexpected encounters of unversioned content here?
			return transientEffectiveTime;
		} else {
			return EffectiveTimes.format(component.getEffectiveTime(), DateFormats.SHORT); 
		}
	}

	protected final String getActive(final SnomedComponent component) {
		return component.isActive() ? "1" : "0";
	}

	public final void exportBranch(final Path releaseDirectory, final RepositoryContext context, final String branch, final long effectiveTimeStart, final long effectiveTimeEnd) throws IOException {
		// Ensure that the path leading to the export file exists
		final Path exportFileDirectory = releaseDirectory.resolve(getRelativeDirectory());
		Files.createDirectories(exportFileDirectory);

		final Path exportFile = exportFileDirectory.resolve(getFileName());
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(exportFile.toFile(), "rw")) {
			try (FileChannel fileChannel = randomAccessFile.getChannel()) {

				// Add a header if the file is empty
				if (randomAccessFile.length() == 0L) {
					fileChannel.write(toByteBuffer(TAB_JOINER.join(getHeader())));
					fileChannel.write(toByteBuffer(CR_LF));
				}

				// We want to append rows, if the file already exists, so jump to the end
				fileChannel.position(fileChannel.size());

				/* 
				 * XXX: createSearchRequestBuilder() should handle namespace/language code filtering, if applicable;
				 * we will only handle the effective time and module filters here.
				 */
				final B requestBuilder = createSearchRequestBuilder()
						.filterByModules(modules) // null value will be ignored
						.setLimit(BATCH_SIZE)
						.setScroll("15m");
				
				if (effectiveTimeStart != 0 || effectiveTimeEnd != Long.MAX_VALUE) {
					requestBuilder.filterByEffectiveTime(effectiveTimeStart, effectiveTimeEnd);
				}
				
				final SearchResourceRequestIterator<B, R> iterator = new SearchResourceRequestIterator<>(requestBuilder, scrolledBuilder -> {
					final Request<BranchContext, R> scrolledRequest = scrolledBuilder.build();
					final RevisionIndexReadRequest<R> indexReadRequest = new RevisionIndexReadRequest<>(scrolledRequest);
					final BranchRequest<R> branchRequest = new BranchRequest<R>(branch, indexReadRequest);
					return branchRequest.execute(context);
				});
				
				while (iterator.hasNext()) {
					final R hits = iterator.next();
					
					getMappedStream(hits, context, branch).forEachOrdered(row -> {
						try {
							fileChannel.write(toByteBuffer(TAB_JOINER.join(row)));
							fileChannel.write(toByteBuffer(CR_LF));
						} catch (final IOException e) {
							throw new SnowowlRuntimeException("Failed to write contents for file '" + exportFile.getFileName() + "'.");
						}
					});
				}
			}
		}
	}

	private static ByteBuffer toByteBuffer(final String s) {
		return ByteBuffer.wrap(s.getBytes(Charsets.UTF_8));
	}
}
