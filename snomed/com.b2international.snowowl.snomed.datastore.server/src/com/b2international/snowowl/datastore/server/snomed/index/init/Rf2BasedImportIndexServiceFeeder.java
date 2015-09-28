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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.TermType;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * {@link ImportIndexServerService Import index service} content initializer. This class uses RF2 files for the content initialization.
 */
public class Rf2BasedImportIndexServiceFeeder implements IImportIndexServiceFeeder {

	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	
	private final Set<String> descriptionFilePaths;
	private final Set<String> languageRefSetFilePaths;
	private final Set<String> synonymAndDescendantIds;
	
	public Rf2BasedImportIndexServiceFeeder(final Set<String> descriptionFilePaths, final Set<String> languageRefSetFilePaths, final Set<String> synonymAndDescendantIds, final IBranchPath branchPath) {
		this.descriptionFilePaths = descriptionFilePaths;
		this.languageRefSetFilePaths = languageRefSetFilePaths;
		this.synonymAndDescendantIds = synonymAndDescendantIds;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.snomed.index.init.IImportIndexServiceFeeder#initContent(com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService, com.b2international.snowowl.core.api.IBranchPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initContent(final ImportIndexServerService service, final IBranchPath branchPath, final IProgressMonitor monitor) throws SnowowlServiceException {

		final AtomicInteger i = new AtomicInteger();

		for (final String descriptionFilePath : descriptionFilePaths) {
			try (final FileReader reader = new FileReader(new File(descriptionFilePath))) {
	
				new CsvParser(reader, CSV_SETTINGS, new RecordParserCallback<String>() {
	
					@Override
					public void handleRecord(final int recordCount, final List<String> record) {
	
						final boolean active = "1".equals(record.get(2));
						final TermType termType = getTermType(record.get(6));
						
						service.registerDescription(record.get(0), record.get(4), record.get(7), termType, active);
						if (0 == i.incrementAndGet() % 10000) {
							service.commit();
						}
	
					}

				}, 9).parse();
	
			} catch (final IOException e) {
				throw new SnowowlRuntimeException("Error while parsing description RF2 file. File: " + descriptionFilePath, e);
			} finally {
				service.commit();
			}
		}
		
		i.set(0);

		for (final String languageRefSetFile : languageRefSetFilePaths) {
			try (final FileReader reader = new FileReader(new File(languageRefSetFile))) {
				new CsvParser(reader, CSV_SETTINGS, new RecordParserCallback<String>() {
	
					@Override
					public void handleRecord(final int recordCount, final List<String> record) {

						final String memberId = record.get(0);
						final String refSetId = record.get(4);
						final String descriptionId = record.get(5);
						
						final boolean active = "1".equals(record.get(2));
						final boolean preferred = Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(record.get(6));
						
						service.registerAcceptability(descriptionId, refSetId, memberId, preferred, active);
						
						if (0 == i.incrementAndGet() % 10000) {
							service.commit();
						}
					}
				}, 7).parse();
	
			} catch (final IOException e) {
				throw new SnowowlRuntimeException("Error while parsing language reference set RF2 file. File: " + languageRefSetFile, e);
			} finally {
				service.commit();
			}
		}
	}
	
	private TermType getTermType(final String typeId) {
		
		if (Concepts.FULLY_SPECIFIED_NAME.equals(typeId)) {
			return TermType.FSN;
		} else if (synonymAndDescendantIds.contains(typeId)) {
			return TermType.SYNONYM_AND_DESCENDANTS;
		} else {
			return TermType.OTHER;
		}
	}
}
