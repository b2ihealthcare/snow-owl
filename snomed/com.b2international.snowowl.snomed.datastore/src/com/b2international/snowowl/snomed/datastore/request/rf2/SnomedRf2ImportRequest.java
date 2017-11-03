/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.internal.file.InternalFileRegistry;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;

/**
 * @since 6.0.0
 */
public class SnomedRf2ImportRequest implements Request<BranchContext, Boolean> {

	private final UUID rf2ArchiveId;
	
	private Map<String/*effectiveTimeKey*/, EffectiveTimeSlice> effectiveTimeSlices = newHashMap();
	
	SnomedRf2ImportRequest(UUID rf2ArchiveId) {
		this.rf2ArchiveId = rf2ArchiveId;
	}
	
	@Override
	public Boolean execute(BranchContext context) {
		final InternalFileRegistry fileReg = (InternalFileRegistry) context.service(FileRegistry.class);
		final File rf2Archive = fileReg.getFile(rf2ArchiveId);

		doImport(rf2Archive);
		
		return Boolean.TRUE;
	}

	void doImport(final File rf2Archive) {
		try (final DB db = createDb()) {
			Stopwatch w = Stopwatch.createStarted();
			read(rf2Archive, db);
			System.err.println("Read RF2 took: " + w);
			w.reset().start();

			
			for (String effectiveTime : Ordering.natural().immutableSortedCopy(effectiveTimeSlices.keySet())) {
				final Multiset<Class<?>> numberOfComponentRevisions = HashMultiset.create();
				final EffectiveTimeSlice slice = effectiveTimeSlices.get(effectiveTime);
				for (SnomedComponent concept : slice.getComponentsByContainer(IComponent.ROOT_ID)) {
					ImmutableList.Builder<SnomedComponent> conceptAndRelatedComponents = ImmutableList.builder();
					conceptAndRelatedComponents.add(concept);
					// add concept descriptions, relationships, members
					for (SnomedComponent relatedComponent : slice.getComponentsByContainer(concept.getId())) {
						conceptAndRelatedComponents.add(relatedComponent);
						// add description and relationship members
						conceptAndRelatedComponents.addAll(slice.getComponentsByContainer(relatedComponent.getId()));
					}
					conceptAndRelatedComponents.build().stream()
						.map(Object::getClass)
						.forEach(numberOfComponentRevisions::add);
				}
				
				System.err.println(effectiveTime);
				for (com.google.common.collect.Multiset.Entry<Class<?>> entry : numberOfComponentRevisions.entrySet()) {
					System.err.println("\tTotal number of "+ entry.getElement().getSimpleName() +" revisions: " + entry.getCount());
				}
			}
			
			System.err.println("Read RF2.db took: " + w);
		}
	}

	private void read(File rf2Archive, DB db) {
		final CsvMapper csvMapper = new CsvMapper();
		csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		final CsvSchema schema = CsvSchema.emptySchema()
				.withoutQuoteChar()
				.withColumnSeparator('\t')
				.withLineSeparator("\r\n");
		final ObjectReader oReader = csvMapper.readerFor(String[].class).with(schema);

		Stopwatch w = Stopwatch.createStarted();
		try (final ZipFile zip = new ZipFile(rf2Archive)) {
			for (ZipEntry entry : Collections.list(zip.entries())) {
				if (entry.getName().contains("Full/") && entry.getName().contains(".txt")) {
					w.reset().start();
					System.err.println(entry.getName());
					try (final InputStream in = zip.getInputStream(entry)) {
						readFile(entry, in, oReader, db);
					}
					System.err.println(entry.getName() + " - " + w);
				}
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		// flush any tmp maps to disk
		effectiveTimeSlices.values().forEach(EffectiveTimeSlice::flush);
	}

	private void readFile(ZipEntry entry, final InputStream in, final ObjectReader oReader, DB db)
			throws IOException, JsonProcessingException {
		boolean header = true;
		Rf2ContentType<?> resolver = null;
		
		MappingIterator<String[]> mi = oReader.readValues(in);
		while (mi.hasNext()) {
			String[] line = mi.next();
			
			if (header) {
				for (Rf2ContentType<?> contentType : Rf2Format.getContentTypes()) {
					if (contentType.canResolve(line)) {
						resolver = contentType;
						break;
					}
				}

				if (resolver == null) {
					System.err.println("Unrecognized RF2 file: " + entry.getName());
					break;
				}
				
				header = false;
			} else {
				final String effectiveTime = line[1];
				if (!effectiveTimeSlices.containsKey(effectiveTime)) {
					 effectiveTimeSlices.put(effectiveTime, new EffectiveTimeSlice(db, effectiveTime));
				}
				resolver.register(line, effectiveTimeSlices.get(effectiveTime));
			}

		}

	}

	private DB createDb() {
		try {
			DB db = DBMaker
					.fileDB(Files.createTempDirectory(rf2ArchiveId.toString()).resolve("rf2-import.db").toFile())
					.fileDeleteAfterClose()
					.fileMmapEnable()
					.fileMmapPreclearDisable()
					// Unmap (release resources) file when its closed.
					// That can cause JVM crash if file is accessed after it was unmapped
					// (there is possible race condition).
					.cleanerHackEnable()
					.allocateStartSize(256 * 1024*1024)  // 256MB
				    .allocateIncrement(128 * 1024*1024)  // 128MB
					.make();
			
			// preload file content into disk cache
			db.getStore().fileLoad();
			return db;
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Couldn't create temporary db", e);
		}
	}
	
}
