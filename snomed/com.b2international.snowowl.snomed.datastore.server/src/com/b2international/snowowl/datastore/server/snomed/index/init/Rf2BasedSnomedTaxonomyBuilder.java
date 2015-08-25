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

import static com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode.DEFAULT;
import static com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode.VALIDATE;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;

import bak.pcj.map.LongKeyMap;

import com.b2international.commons.arrays.Arrays2;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.IsAStatementWithId;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyBuilderMode;
import com.b2international.snowowl.snomed.datastore.index.StatementMap;
import com.b2international.snowowl.snomed.datastore.taxonomy.AbstractSnomedTaxonomyBuilder;
import com.google.common.base.Preconditions;

/**
 * Taxonomy builder backed by RF2 release files.
 */
public class Rf2BasedSnomedTaxonomyBuilder extends AbstractSnomedTaxonomyBuilder  {
	
	private static final CsvSettings CSV_SETTINGS = new CsvSettings('\0', '\t', EOL.LF, true);
	private static final String ACTIVE_STATUS = "1";
	private static final int EXPECTED_SIZE = 300000;
	
	/**
	 * Returns with a new taxonomy builder instance after replicating the specified one.
	 * @param builder the builder to replicate.
	 * @return the new builder instance.
	 */
	public static Rf2BasedSnomedTaxonomyBuilder newInstance(final Rf2BasedSnomedTaxonomyBuilder builder) {
		return newInstance(builder, builder.getMode(), builder.conceptFilePath, builder.relationshipFilePath);
	}
	
	public static Rf2BasedSnomedTaxonomyBuilder newInstance(final AbstractSnomedTaxonomyBuilder builder, 
			final String conceptFilePath, final String relationshipFilePath) {
		return newInstance(builder, DEFAULT, conceptFilePath, relationshipFilePath);
		
	}
	
	public static Rf2BasedSnomedTaxonomyBuilder newValidationInstance(final AbstractSnomedTaxonomyBuilder builder, 
			final String conceptFilePath, final String relationshipFilePath) {
		return newInstance(builder, VALIDATE, conceptFilePath, relationshipFilePath);
		
	}
	
	private static Rf2BasedSnomedTaxonomyBuilder newInstance(final AbstractSnomedTaxonomyBuilder builder, final SnomedTaxonomyBuilderMode mode, 
			final String conceptFilePath, final String relationshipFilePath) {
		
		Preconditions.checkNotNull(builder, "Builder argument cannot be null.");
		
		final Rf2BasedSnomedTaxonomyBuilder $ = new Rf2BasedSnomedTaxonomyBuilder(mode);
		$.nodes = new LongBidiMapWithInternalId(builder.getNodes());
		$.edges = (StatementMap) ((StatementMap) builder.getEdges()).clone();
		$.setDirty(builder.isDirty());
		$.descendants = Arrays2.copy(builder.getDescendants());
		$.ancestors = Arrays2.copy(builder.getAncestors());
		$.conceptFilePath = conceptFilePath;
		$.relationshipFilePath = relationshipFilePath;
		
		return $;
	}
	
	private final Object edgeMutex = new Object();
	private final Object nodeMutex = new Object();
	private String conceptFilePath;
	private String relationshipFilePath;
	
	/**
	 * Bi-directional map for storing SNOMED CT concept IDs. 
	 */
	private LongBidiMapWithInternalId nodes;

	/**
	 * Map for storing active IS_A type SNOMED CT relationship representations. Keys are the unique relationship identifiers.
	 * <br>For values see: {@link IsAStatementWithId}.
	 */
	private LongKeyMap edges;
	private SnomedTaxonomyBuilderMode mode;
	
	public Rf2BasedSnomedTaxonomyBuilder(final String conceptFilePath, final String relationshipFilePath) {
		this.conceptFilePath = conceptFilePath;
		this.relationshipFilePath = relationshipFilePath;
	}
	
	private Rf2BasedSnomedTaxonomyBuilder(final SnomedTaxonomyBuilderMode mode) {
		this.mode = mode;
	}

	@Override
	protected SnomedTaxonomyBuilderMode getMode() {
		return mode;
	}
	
	@Override
	public LongBidiMapWithInternalId getNodes() {
		
		if (null == nodes) {
			
			synchronized (nodeMutex) {
				
				if (null == nodes) {
					
					nodes = new LongBidiMapWithInternalId(EXPECTED_SIZE);
					parseFile(conceptFilePath, 5, new RecordParserCallback<String>() {
						@Override public void handleRecord(final int recordCount, final List<String> record) {
							final long id = Long.parseLong(record.get(0));
							nodes.put(id, id);
						}
					});
		
					
				}
				
			}
			
		}
		
		return nodes;
	}

	@Override
	public LongKeyMap getEdges() {
		
		if (null == edges) {
			
			synchronized (edgeMutex) {
				
				if (null == edges) {
					
					edges = new StatementMap();
					parseFile(relationshipFilePath, 10, new RecordParserCallback<String>() {
						@Override public void handleRecord(final int recordCount, final List<String> record) {
							
							if (ACTIVE_STATUS.equals(record.get(2)) && Concepts.IS_A.equals(record.get(7))) {
								
								edges.put(Long.parseLong(record.get(0)), 
										new long[] { 
											Long.parseLong(record.get(5)), 
											Long.parseLong(record.get(4)) });
							}
						}
					});
					
				}
				
			}
			
		}
		
		return edges;
		
	}
	
	public void applyNodeChanges(final String conceptFilePath) {
		parseFile(conceptFilePath, 5, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final List<String> record) {
				
				final TaxonomyNode node = new TaxonomyNode() {
					@Override public boolean isCurrent() { return ACTIVE_STATUS.equals(record.get(2)); }
					@Override public String getId() { return record.get(0); }
				};
				
				if (node.isCurrent()) {
					addNode(node);
				} else {
					removeNode(node);
				}
				
			}
		});
	}
	
	public void applyEdgeChanges(final String relationshipFilePath) {
		parseFile(relationshipFilePath, 10, new RecordParserCallback<String>() {
			@Override public void handleRecord(final int recordCount, final List<String> record) {
				
				addEdge(new TaxonomyEdge() {
					@Override public boolean isCurrent() {
						return ACTIVE_STATUS.equals(record.get(2));
					}
					@Override public String getId() {
						return record.get(0);
					}
					@Override public boolean isValid() {
						return Concepts.IS_A.equals(record.get(7));
					}
					@Override public String getSoureId() {
						return record.get(4);
					}
					@Override public String getDestinationId() {
						return record.get(5);
					}
				});
				
			}
		});
	}
	
	private void parseFile(final String filePath, final int columnCount, final RecordParserCallback<String> callback) {
		
		if (null == filePath) {
			return; //nothing to process
		}
		
		try (final Reader reader = new FileReader(new File(filePath))) {
			new CsvParser(reader, CSV_SETTINGS, callback, columnCount).parse();
		} catch (final IOException e) {
			throw new SnowowlRuntimeException(MessageFormat.format("Exception caught while parsing file ''{0}''.", filePath));
		}
	}
	
	/**
	 * Specialized taxonomy builder wrapping any arbitrary {@link AbstractSnomedTaxonomyBuilder} instance and delegating into it.
	 * {@link #applyEdgeChanges(String)} and {@link #applyNodeChanges(String)} does nothing for any instance of the current class.
	 */
	public static final class ReducedRf2BasedSnomedTaxonomyBuilder extends Rf2BasedSnomedTaxonomyBuilder {
	
		private final AbstractSnomedTaxonomyBuilder delegate;

		public ReducedRf2BasedSnomedTaxonomyBuilder(final AbstractSnomedTaxonomyBuilder delegate) {
			super(DEFAULT);
			this.delegate = delegate;
			this.setDirty(delegate.isDirty());
			this.descendants = Arrays2.copy(delegate.getDescendants());
			this.ancestors = Arrays2.copy(delegate.getAncestors());
		}

		@Override
		public LongKeyMap getEdges() {
			return delegate.getEdges();
		}
		
		@Override
		public LongBidiMapWithInternalId getNodes() {
			return delegate.getNodes();
		}
		
		@Override
		public AbstractSnomedTaxonomyBuilder build() {
			return delegate.build();
		}
		
		public static Rf2BasedSnomedTaxonomyBuilder createNewReeducedInstance(final AbstractSnomedTaxonomyBuilder builder) {
			return new ReducedRf2BasedSnomedTaxonomyBuilder(builder);
		}
		
		@Override public final void applyNodeChanges(String conceptFilePath) {
			//does nothing
		}
		
		@Override public final void applyEdgeChanges(String relationshipFilePath) {
			//intentionally does nothing
		}
		
	}
	
	
}
