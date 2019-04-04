/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraph;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraphStatus;

/**
 * Taxonomy graph built from RF2 release files.
 */
public final class RF2TaxonomyGraph {
	
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	private static final String ACTIVE_STATUS = "1";
	private static final int EXPECTED_SIZE = 300000;
	
	private final TaxonomyGraph graph;
	private final String characteristicTypeId;
	private final SnomedOWLExpressionConverter owlExpressionConverter;
	
	public RF2TaxonomyGraph(BranchContext context, String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
		this.graph = new TaxonomyGraph(EXPECTED_SIZE, EXPECTED_SIZE);
		this.owlExpressionConverter = new SnomedOWLExpressionConverter(context);
	}
	
	public TaxonomyGraph getGraph() {
		return graph;
	}
	
	public void applyNodeChanges(final File conceptFile) {
		parseFile(conceptFile, 5, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final List<String> record) {
				if (ACTIVE_STATUS.equals(record.get(2))) {
					graph.addNode(record.get(0));
				} else {
					graph.removeNode(record.get(0));
				}
			}
		});
	}
	
	public void applyEdgeChanges(final File relationshipFile) {
		parseFile(relationshipFile, 10, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final List<String> record) {
				final String id = record.get(0);
				if (ACTIVE_STATUS.equals(record.get(2))) {
					final String typeId = record.get(7);
					final String rowCharacteristicTypeId = record.get(8);
					if (Concepts.IS_A.equals(typeId) && characteristicTypeId.equals(rowCharacteristicTypeId)) {
						final long sourceId = Long.parseLong(record.get(4));
						final long[] destinationIds = new long[] {Long.parseLong(record.get(5))};
						graph.addEdge(id, sourceId, destinationIds);
					}
				} else {
					graph.removeEdge(id);
				}
			}
		});
	}
	
	public void applyAxioms(final File owlExpressionFile) {
		parseFile(owlExpressionFile, SnomedRf2Headers.OWL_EXPRESSION_HEADER.length, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final List<String> record) {
				final String id = record.get(0);
				if (ACTIVE_STATUS.equals(record.get(2))) {
					String referencedComponentId = record.get(5);
					String owlExpression = record.get(6);
					SnomedOWLExpressionConverterResult result = owlExpressionConverter.toSnomedOWLRelationships(referencedComponentId, owlExpression);
					if (!CompareUtils.isEmpty(result.getClassAxiomRelationships())) {
						long[] destinationIds = result.getClassAxiomRelationships()
								.stream()
								.filter(classAxiom -> Concepts.IS_A.equals(classAxiom.getTypeId()))
								.map(SnomedOWLRelationshipDocument::getDestinationId)
								.mapToLong(Long::parseLong)
								.toArray();
						graph.addEdge(id, Long.parseLong(referencedComponentId), destinationIds);
					}
				} else {
					graph.removeEdge(id);
				}
			}
		});
	}
	
	private void parseFile(final File file, final int columnCount, final RecordParserCallback<String> callback) {
		
		if (null == file || file.getPath().isEmpty()) {
			return; //nothing to process
		}
		
		try (final Reader reader = new FileReader(file)) {
			new CsvParser(reader, CSV_SETTINGS, callback, columnCount).parse();
		} catch (final IOException e) {
			throw new SnowowlRuntimeException(MessageFormat.format("Exception caught while parsing file ''{0}''.", file));
		}
	}

	public TaxonomyGraphStatus update() {
		return graph.update();
	}

	public void init(LongCollection conceptIds, Iterable<Object[]> isaStatements) {
		// populate nodes
		LongIterator conceptIdsIt = conceptIds.iterator();
		while (conceptIdsIt.hasNext()) {
			long nodeId = conceptIdsIt.next();
			if (IComponent.ROOT_IDL == nodeId) {
				continue;
			}
			graph.addNode(nodeId);
		}
		
		// populate edges
		for (Object[] isaStatement : isaStatements) {
			graph.addEdge((String) isaStatement[0], (long) isaStatement[1], (long[]) isaStatement[2]);
		}
	}
	
}
