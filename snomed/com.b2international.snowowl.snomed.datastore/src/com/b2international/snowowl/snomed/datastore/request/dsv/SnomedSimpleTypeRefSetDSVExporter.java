/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.dsv;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Optional.ofNullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.ComponentIdSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.DatatypeSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;

/**
 * Implements the export process of the DSV export for simple type reference sets. 
 */
public class SnomedSimpleTypeRefSetDSVExporter implements IRefSetDSVExporter {

	private static final String HEADER_EXPAND = "descriptions(active:true),"
			+ "relationships(active:true),"
			+ "members()";
	
	private static final String DATA_EXPAND = "pt(),"
			+ "descriptions(active:true),"
			+ "relationships(active:true,expand(destination(expand(pt())))),"
			+ "members()";

	private static final Map<String, Integer> NO_OCCURRENCES = ImmutableMap.of();
	
	private final BranchContext context;
	
	private String refSetId;
	private boolean includeDescriptionId;
	private boolean includeRelationshipId;
	private boolean includeInactiveMembers;
	private List<AbstractSnomedDsvExportItem> exportItems;
	private List<ExtendedLocale> locales;
	private Joiner joiner;
	private String lineSeparator;
	
	private Map<String, Integer> descriptionCount; // maximum number of descriptions by type
	private Map<Integer, Map<String, Integer>> propertyCountByGroup; // maximum number of properties by group and type


	/**
	 * Creates a new instance with the export parameters.
	 * 
	 * @param exportSetting
	 */
	public SnomedSimpleTypeRefSetDSVExporter(final BranchContext context, final SnomedRefSetDSVExportModel exportSetting) {
		this.refSetId = exportSetting.getRefSetId();
		this.includeDescriptionId = exportSetting.includeDescriptionId();
		this.includeRelationshipId = exportSetting.includeRelationshipTargetId();
		this.includeInactiveMembers = exportSetting.includeInactiveMembers();
		this.exportItems = exportSetting.getExportItems();
		this.locales = exportSetting.getLocales();
		this.context = context;
		this.joiner = Joiner.on(exportSetting.getDelimiter());
		this.lineSeparator = System.lineSeparator();
	}

	/**
	 * Executes the export to delimiter separated values.
	 * 
	 * @param monitor
	 * @return The file with the exported values.
	 */
	@Override
	public File executeDSVExport(IProgressMonitor monitor) throws IOException {
		monitor.beginTask("Export RefSet to DSV...", 100);
		Path exportPath = Files.createTempFile("dsv-export-" + refSetId + Dates.now(), ".csv");
		try {
			try (BufferedWriter writer = Files.newBufferedWriter(exportPath, Charsets.UTF_8)) {
				computeHeader();
				writeHeader(writer);
				writeValues(monitor, writer);
			}
			return exportPath.toFile();
		} finally {
			if (null != monitor) { 
				monitor.done(); 
			}
		}
	}

	/*
	 * Fetches members of the specified reference set
	 */
	private SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> getMemberConceptIterator(String expand) {
		
		SnomedConceptSearchRequestBuilder builder = SnomedRequests.prepareSearchConcept()
			.setLocales(locales)
			.setExpand(expand)
			.sortBy(SortField.ascending(SnomedConceptDocument.Fields.ID))
			.setLimit(10_000);
		
		if (includeInactiveMembers) {
			builder.isMemberOf(refSetId);
		} else {
			builder.isActiveMemberOf(refSetId);
		}
		
		return new SearchResourceRequestIterator<>(builder, b -> b.build().execute(context));
	}

	/*
	 * Finds the maximum number of occurrences for each description, relationship and concrete data type; generates headers. 
	 */
	private void computeHeader() {
		descriptionCount = newHashMap();
		propertyCountByGroup = newHashMap();
		
		SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> conceptIterator = getMemberConceptIterator(HEADER_EXPAND);
		while (conceptIterator.hasNext()) {
			computeHeader(conceptIterator.next());
		}
	}
	
	private void computeHeader(SnomedConcepts chunk) {
		for (SnomedConcept concept : chunk) {
			for (AbstractSnomedDsvExportItem exportItem : exportItems) {
				switch (exportItem.getType()) {
					case DESCRIPTION:
						ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
						String descriptionTypeId = descriptionItem.getComponentId();
						Integer matchingDescriptions = concept.getDescriptions()
								.stream()
								.filter(d -> descriptionTypeId.equals(d.getTypeId()))
								.collect(Collectors.reducing(0, description -> 1, Integer::sum));
						
						descriptionCount.merge(descriptionTypeId, matchingDescriptions, Math::max);
						break;
					case RELATIONSHIP:
						ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
						String relationshipTypeId = relationshipItem.getComponentId();
						
						Map<Integer, Integer> matchingRelationships = concept.getRelationships()
								.stream()
								.filter(r -> relationshipTypeId.equals(r.getTypeId()) 
										&& (Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()) 
										|| Concepts.ADDITIONAL_RELATIONSHIP.equals(r.getCharacteristicTypeId())))
								.collect(Collectors.groupingBy(
										SnomedRelationship::getGroup,
										Collectors.reducing(0, relationship -> 1, Integer::sum)));

						matchingRelationships.entrySet()
								.stream()
								.forEach(entry -> {
									propertyCountByGroup.compute(entry.getKey(), (key, oldValue) -> {
										Map<String, Integer> propertyCountForGroup = ofNullable(oldValue).orElseGet(HashMap::new);
										propertyCountForGroup.merge(relationshipTypeId, entry.getValue(), Math::max);
										return propertyCountForGroup;
									});
								});
						break;
					case DATAYPE:
						ComponentIdSnomedDsvExportItem dataTypeItem = (ComponentIdSnomedDsvExportItem) exportItem;
						String dataTypeId = dataTypeItem.getComponentId();
						
						Map<Integer, Integer> matchingMembers = concept.getMembers()
								.stream()
								.filter(m -> SnomedRefSetType.CONCRETE_DATA_TYPE.equals(m.type())
										&& m.isActive()
										&& dataTypeId.equals(m.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID)) 
										&& (Concepts.INFERRED_RELATIONSHIP.equals(m.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)) 
										|| Concepts.ADDITIONAL_RELATIONSHIP.equals(m.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))))
								.collect(Collectors.groupingBy(
										m -> (Integer) m.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP),
										Collectors.reducing(0, relationship -> 1, Integer::sum)));

						matchingMembers.entrySet()
								.stream()
								.forEach(entry -> {
									propertyCountByGroup.compute(entry.getKey(), (key, oldValue) -> {
										Map<String, Integer> propertyCountForGroup = ofNullable(oldValue).orElseGet(HashMap::new);
										propertyCountForGroup.merge(dataTypeId, entry.getValue(), Math::max);
										return propertyCountForGroup;
									});
								});
						break;
					default:
						// Single-use fields don't need to be counted in advance
						break;
				}
			}
		}
	}
	
	private void writeHeader(BufferedWriter writer) throws IOException {
		Map<String, String> descriptionTypeIdMap = createTypeIdMap(Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT);
		Map<String, String> propertyTypeIdMap = createTypeIdMap(Concepts.CONCEPT_MODEL_ATTRIBUTE); // includes object and data attributes
		List<String> propertyHeader = newArrayList();
		List<String> detailHeader = newArrayList();
		
		for (AbstractSnomedDsvExportItem exportItem : exportItems) {
			switch (exportItem.getType()) {
				case DESCRIPTION: {
					ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
					String typeId = descriptionItem.getComponentId();
					String displayName = descriptionTypeIdMap.getOrDefault(typeId, descriptionItem.getDisplayName());
					int occurrences = descriptionCount.get(typeId);
					
					if (occurrences < 2) {
						if (includeDescriptionId) {
							propertyHeader.add(displayName);
							propertyHeader.add(displayName);
							detailHeader.add("ID");
							detailHeader.add("Term");
						} else {
							propertyHeader.add(displayName);
							detailHeader.add("");
						}
					} else {
						for (int j = 1; j <= occurrences; j++) {
							String numberedDisplayName = String.format("%s (%s)", displayName, j);
							if (includeDescriptionId) {
								propertyHeader.add(numberedDisplayName);
								propertyHeader.add(numberedDisplayName);
								detailHeader.add("ID");
								detailHeader.add("Term");
							} else {
								propertyHeader.add(numberedDisplayName);
								detailHeader.add("");
							}
						}
					}
					break;
				}
					
				case RELATIONSHIP: {
					ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
					String typeId = relationshipItem.getComponentId();
					String displayName = propertyTypeIdMap.getOrDefault(typeId, relationshipItem.getDisplayName());
					
					for (Integer group : propertyCountByGroup.keySet() ) {
						Map<String, Integer> occurrencesByType = propertyCountByGroup.getOrDefault(group, NO_OCCURRENCES);
						int occurrences = occurrencesByType.getOrDefault(typeId, 0);
						if (occurrences < 1) {
							continue;
						}
							
						String groupTag = group == 0 ? "" : String.format(" (AG%s)", group);
						if (occurrences < 2) {
							if (includeRelationshipId) {
								String groupedDisplayName = displayName + groupTag;
								propertyHeader.add(groupedDisplayName);
								propertyHeader.add(groupedDisplayName);
								detailHeader.add("ID");
								detailHeader.add("Destination");
							} else {
								propertyHeader.add(displayName);
								detailHeader.add("");
							}
						} else {
							for (int j = 1; j <= occurrences; j++) {
								String numberedDisplayName = String.format("%s (%s)%s", displayName, j, groupTag);
								if (includeRelationshipId) {
									propertyHeader.add(numberedDisplayName);
									propertyHeader.add(numberedDisplayName);
									detailHeader.add("ID");
									detailHeader.add("Destination");
								} else {
									propertyHeader.add(numberedDisplayName);
									detailHeader.add("");
								}
							}
						}
					}
					
					break;
				}
				
				case DATAYPE: {
					ComponentIdSnomedDsvExportItem dataTypeItem = (ComponentIdSnomedDsvExportItem) exportItem;
					String typeId = dataTypeItem.getComponentId();
					String displayName = propertyTypeIdMap.getOrDefault(typeId, dataTypeItem.getDisplayName());

					for (Integer groupId : propertyCountByGroup.keySet() ) {
						Map<String, Integer> occurrencesByType = propertyCountByGroup.getOrDefault(groupId, NO_OCCURRENCES);
						int occurrences = occurrencesByType.getOrDefault(typeId, 0);
						if (occurrences < 1) {
							continue;
						}
						
						String groupTag = groupId == 0 ? "" : String.format(" (AG%s)", groupId);
						if (occurrences < 2) {
							String groupedDisplayName = displayName + groupTag;
							propertyHeader.add(groupedDisplayName);
							detailHeader.add("");
						} else {
							for (int j = 1; j <= occurrences; j++) {
								String numberedDisplayName = String.format("%s (%s)%s", displayName, j, groupTag);
								propertyHeader.add(numberedDisplayName);
								detailHeader.add("");
							}
						}						
					}
					break;
				}
				
				case PREFERRED_TERM:
					if (includeDescriptionId) {
						propertyHeader.add(exportItem.getDisplayName());
						propertyHeader.add(exportItem.getDisplayName());
						detailHeader.add("ID");
						detailHeader.add("Term");
					} else {
						propertyHeader.add(exportItem.getDisplayName());
						detailHeader.add("");
					}
					break;
	
				default:
					propertyHeader.add(exportItem.getDisplayName());
					detailHeader.add("");
					break;
			}
		}
		
		// write the header to the file
		writer.write(joiner.join(propertyHeader));
		writer.write(lineSeparator);
		
		if (includeDescriptionId || includeRelationshipId) {
			writer.write(joiner.join(detailHeader));
			writer.write(lineSeparator);
		}
	}

	private Map<String, String> createTypeIdMap(String ancestorId) {
		return createTypeIdMap(SnomedRequests.prepareSearchConcept()
			.all()
			.setLocales(locales)
			.filterByAncestor(ancestorId)
			.setExpand("pt()")
			.build()
			.execute(context));
	}

	private Map<String, String> createTypeIdMap(SnomedConcepts concepts) {
		return concepts.stream()
			.collect(Collectors.toMap(
				SnomedConcept::getId, 
				c -> getPreferredTerm(c)));
	}

	private void writeValues(IProgressMonitor monitor, BufferedWriter writer) throws IOException {
		SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> conceptIterator = getMemberConceptIterator(DATA_EXPAND);
		
		while (conceptIterator.hasNext()) {
			SnomedConcepts chunk = conceptIterator.next();
			writeValues(writer, chunk);
			monitor.worked(chunk.getItems().size());
		}
	}
		
	private void writeValues(BufferedWriter writer, SnomedConcepts chunk) throws IOException {
		List<String> dataRow = newArrayList();
		
		for (SnomedConcept concept : chunk) {
			dataRow.clear();

			for (AbstractSnomedDsvExportItem exportItem : exportItems) {
				switch (exportItem.getType()) {
					case DESCRIPTION: {
						final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
						final String typeId = descriptionItem.getComponentId();
						int occurrences = descriptionCount.get(typeId);
						
						final Map<String, String> termsById = concept.getDescriptions()
								.stream()
								.filter(d -> typeId.equals(d.getTypeId()))
								.collect(Collectors.toMap(
										SnomedDescription::getId, 
										SnomedDescription::getTerm));
						
						addCells(dataRow, occurrences, includeDescriptionId, termsById);
						break;
					}

					case RELATIONSHIP: {
						final ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
						for (Integer propertyGroup : propertyCountByGroup.keySet()) {
							final String typeId = relationshipItem.getComponentId();
							final Map<String, Integer> groupOccurrences = propertyCountByGroup.getOrDefault(propertyGroup, NO_OCCURRENCES);
							
							final int occurrences = groupOccurrences.getOrDefault(typeId, 0);
							concept.getRelationships()
									.stream()
									.filter(r -> typeId.equals(r.getTypeId())
											&& Objects.equals(r.getGroup(), propertyGroup) 
											&& (Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()) 
													|| Concepts.ADDITIONAL_RELATIONSHIP.equals(r.getCharacteristicTypeId())))
									.forEach(relationship -> {
										addCells(dataRow, occurrences, includeRelationshipId, ImmutableMap.of(relationship.getDestinationId(), getPreferredTerm(relationship.getDestination())));
									});
							
						}
						break;
					}

					case DATAYPE: {
						final DatatypeSnomedDsvExportItem datatypeItem = (DatatypeSnomedDsvExportItem) exportItem;
						for (Integer propertyGroup : propertyCountByGroup.keySet()) {
							Map<String, Integer> groupedOccurrences = propertyCountByGroup.getOrDefault(propertyGroup, NO_OCCURRENCES);
							final String typeId = datatypeItem.getComponentId();
							int occurrences = groupedOccurrences.getOrDefault(typeId, 0);
							
							if (occurrences < 1) {
								break;
							}
							
							final List<String> properties = concept.getMembers()
									.stream()
									.filter(m -> SnomedRefSetType.CONCRETE_DATA_TYPE.equals(m.type())
											&& m.isActive()
											&& typeId.equals(m.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID))
											&& Objects.equals(m.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP), propertyGroup) 
											&& (Concepts.INFERRED_RELATIONSHIP.equals(m.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)) 
													|| Concepts.ADDITIONAL_RELATIONSHIP.equals(m.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))))
									.map(m -> m.getProperties().get(SnomedRf2Headers.FIELD_VALUE))
									.map(p -> {
										if (datatypeItem.isBooleanDatatype()) {
											return "1".equals(p) ? "Yes" : "No";
										} else {
											return p.toString();
										}
									})
									.sorted()
									.collect(Collectors.toList());
							
							for (String value : properties) {
								dataRow.add(value);
								occurrences--;
							}
							while (occurrences > 0) {
								dataRow.add("");
								occurrences--;
							}
						}
						break;
					}
					
					case PREFERRED_TERM:
						if (includeDescriptionId) {
							dataRow.add(getPreferredTermId(concept));
							dataRow.add(getPreferredTerm(concept));
						} else {
							dataRow.add(getPreferredTerm(concept));
						}
						break;

					case CONCEPT_ID:
						dataRow.add(concept.getId());
						break;

					case MODULE: 
						dataRow.add(concept.getModuleId());
						break;

					case EFFECTIVE_TIME:
						dataRow.add(EffectiveTimes.format(concept.getEffectiveTime()));
						break;

					case STATUS_LABEL:
						dataRow.add(concept.isActive() ? "Active" : "Inactive");
						break;

					case DEFINITION_STATUS: 
						dataRow.add(concept.getDefinitionStatusId());
						break;

					default:
						break;
				}
			}
			
			writer.write(joiner.join(dataRow));
			writer.write(lineSeparator);
		}
	}

	private void addCells(List<String> dataRow, int occurrences, boolean includeIds, Map<String, String> idValuePairs) {
		if (includeIds) {
			SortedSet<String> sortedIds = ImmutableSortedSet.copyOf(idValuePairs.keySet());
			for (String id : sortedIds) {
				dataRow.add(id);
				dataRow.add(idValuePairs.get(id));
				occurrences--;
			}
			while (occurrences > 0) {
				dataRow.add("");
				dataRow.add("");
				occurrences--;
			}
		} else {
			List<String> sortedValues = Ordering.natural().sortedCopy(idValuePairs.values());
			for (String value : sortedValues) {
				dataRow.add(value);
				occurrences--;
			}
			while (occurrences > 0) {
				dataRow.add("");
				occurrences--;
			}
		}
	}

	private String getPreferredTerm(SnomedConcept concept) {
		return (concept.getPt() == null) ? "" : concept.getPt().getTerm();
	}
	
	private String getPreferredTermId(SnomedConcept concept) {
		return (concept.getPt() == null) ? "" : concept.getPt().getId();
	}

}
