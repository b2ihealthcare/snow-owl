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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.internal.file.InternalFileRegistry;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Stopwatch;

/**
 * @since 6.0.0
 */
public class SnomedRf2ImportRequest implements Request<BranchContext, Boolean> {

	private final UUID rf2ArchiveId;
	
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
		final DB db = createDb();
		
		read(rf2Archive, db);
		
		int total = 0;
		for (String name : db.getAllNames()) {
			total += db.<Map<String, SnomedConcept>>get(name).size();
		}
		System.err.println("Total number of concepts: " + total);
		
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
	}

	private void readFile(ZipEntry entry, final InputStream in, final ObjectReader oReader, DB db)
			throws IOException, JsonProcessingException {
		boolean header = true;
		Rf2ContentResolver resolver = null;
		
		final Map<String, Map<String, SnomedConcept>> effectiveTimeBatches = newHashMap();
		MappingIterator<String[]> mi = oReader.readValues(in);
		while (mi.hasNext()) {
			String[] line = mi.next();
			
			if (header) {
				if (Arrays.equals(SnomedRf2Headers.CONCEPT_HEADER, line)) {
					resolver = new Rf2ConceptContentResolver();
					header = false;
				} else {
					System.err.println("Unrecognized RF2 file: " + entry.getName());
					break;
				}
			} else {
				final String effectiveTime = line[1];
				if (!effectiveTimeBatches.containsKey(effectiveTime)) {
					effectiveTimeBatches.put(effectiveTime, newHashMapWithExpectedSize(5000));
				}
				Map<String, SnomedConcept> effectiveTimeBatch = effectiveTimeBatches.get(effectiveTime);
				resolver.resolve(line, effectiveTimeBatch);
				if (effectiveTimeBatch.size() >= 5000) {
					writeBatch(db, effectiveTime, effectiveTimeBatch);
					effectiveTimeBatch.clear();
				}
			}

		}
		
		// write the remaining items in the batches
		effectiveTimeBatches.forEach((effectiveTime, batch) -> writeBatch(db, effectiveTime, batch));
	}

	private void writeBatch(DB db, final String effectiveTime, Map<String, SnomedConcept> effectiveTimeBatch) {
		Map<String, SnomedConcept> effectiveTimeStore = db.get(effectiveTime);
		if (effectiveTimeStore == null) {
			effectiveTimeStore = db.hashMap(effectiveTime, Serializer.STRING, Serializer.JAVA).create();
		}
		effectiveTimeStore.putAll(effectiveTimeBatch);
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
					.allocateStartSize(1 * 1024*1024*1024)  // 1GB
				    .allocateIncrement(512 * 1024*1024)     // 512MB
					.make();
			
			// preload file content into disk cache
			db.getStore().fileLoad();
			return db;
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Couldn't create temporary db", e);
		}
	}
	
	interface Rf2ContentResolver {
		
		void resolve(String[] values, Map<String, SnomedConcept> conceptsById);
		
	}
	
	class Rf2ConceptContentResolver implements Rf2ContentResolver {
		
		@Override
		public void resolve(String[] values, Map<String, SnomedConcept> conceptsById) {
			final SnomedConcept concept = new SnomedConcept();
			concept.setId(values[0]);
			concept.setEffectiveTime(EffectiveTimes.parse(values[1], DateFormats.SHORT));
			concept.setActive("1".equals(values[2]));
			concept.setModuleId(values[3]);
			concept.setDefinitionStatus(Concepts.PRIMITIVE.equals(values[4]) ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
			conceptsById.put(concept.getId(), concept);
		}
		
	}

}
