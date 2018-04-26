/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.dsv;

import static java.util.Optional.ofNullable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.commons.FileUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.ComponentIdSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.DatatypeSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * This class implements the export process of the DSV export for simple type reference sets. 
 * Used by the SnomedSimpleTypeRefSetExportServerIndication class.
 * 
 * 
 */

public class SnomedSimpleTypeRefSetDSVExporter implements IRefSetDSVExporter {

	private String refSetId;
	private boolean includeDescriptionId;
	private boolean includeRelationshipId;
	private boolean includeInactiveMembers;
	private Collection<AbstractSnomedDsvExportItem> exportItems;
	private Collection<AbstractSnomedDsvExportItem> groupedOnlyItems;
	private IBranchPath branchPath;
	private String delimiter;

	private Map<Integer, LinkedHashMap<String, Integer>> groupedRelationships;
	private Map<String, Integer> exportItemOccurences;
	private Collection<String> headerList;
	private Collection<String> metaHeaderList;

	private List<ExtendedLocale> locales;
	private String exportPath;

	/**
	 * Creates a new instance with the export parameters. Called by the SnomedSimpleTypeRefSetDSVExportServerIndication.
	 * 
	 * @param exportSetting
	 */

	public SnomedSimpleTypeRefSetDSVExporter(final SnomedRefSetDSVExportModel exportSetting) {

		this.refSetId = exportSetting.getRefSetId();
		this.includeDescriptionId = exportSetting.includeDescriptionId();
		this.includeRelationshipId = exportSetting.includeRelationshipTargetId();
		this.includeInactiveMembers = exportSetting.includeInactiveMembers();
		this.exportItems = exportSetting.getExportItems();
		this.locales = exportSetting.getLocales();
		this.delimiter = exportSetting.getDelimiter();
		this.branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
		exportPath = exportSetting.getExportPath();
		groupedRelationships = Maps.newTreeMap();
		groupedOnlyItems = Lists.newArrayList();
	}

	/**
	 * Executes the export to delimiter separated values.
	 * 
	 * @param monitor
	 * @return The file with the exported values.
	 * @throws SnowowlServiceException
	 */
	@Override
	public File executeDSVExport(OMMonitor monitor) throws SnowowlServiceException, IOException {

		monitor.begin(100);
		Async async = monitor.forkAsync(80);
		OMMonitor remainderMonitor = null;
		File file = new File(exportPath, refSetId + ".csv");
		try (final DataOutputStream os = new DataOutputStream(new FileOutputStream(file)) ) {

			file.createNewFile();

			SnomedConcepts referencedComponents = getReferencedComponentConcepts(refSetId,includeInactiveMembers);
			createHeaderList(referencedComponents);

			// write the header to the file
			StringBuffer sb = new StringBuffer();
			for (String metaHeaderListElement : metaHeaderList) {
				if (sb.length() > 0) {
					sb.append(delimiter);
				}
				sb.append(metaHeaderListElement);
			}

			if (includeDescriptionId || includeRelationshipId || includeInactiveMembers) {
				sb.append(System.getProperty("line.separator"));
				// sb.length > 0 works not, because the first element of the meta header can be empty string.
				boolean fistElement = true;

				for (String headerListElement : headerList) {
					if (fistElement) {
						fistElement = false;
					} else {
						sb.append(delimiter);
					}
					sb.append(headerListElement);
				}
			}
			sb.append(System.getProperty("line.separator"));
			os.writeBytes(sb.toString());

			async.stop();
			async = null;
			remainderMonitor = monitor.fork(20);
			remainderMonitor.begin(referencedComponents.getTotal());
			// write data to the file row by row
			LinkedHashMap<String, Integer> groupZeroCounts = groupedRelationships.getOrDefault(0, new LinkedHashMap<>());
			
			for (SnomedConcept referencedComponent : referencedComponents) {
				StringBuffer stringBuffer = new StringBuffer();

				for (AbstractSnomedDsvExportItem exportItem : exportItems) {
					if (stringBuffer.length() > 0) {
						stringBuffer.append(delimiter);
					}
					
					switch (exportItem.getType()) {
						
						case DESCRIPTION:
							final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String descriptionTypeId = descriptionItem.getComponentId();
							final int descriptionOccurrences = exportItemOccurences.get(descriptionTypeId);
							final Collection<String> descriptions = getDescriptionTokens(referencedComponent, descriptionTypeId);
							stringBuffer.append(joinResultsWithDelimiters(descriptions, descriptionOccurrences, delimiter, includeDescriptionId));
							break;

						case RELATIONSHIP:
							final ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String relationshipTypeId = String.valueOf(relationshipItem.getComponentId());
							// Use groupZeroCounts instead of exportItemOccurences here
							int relationshipOccurrences = groupZeroCounts.get(relationshipTypeId);
							final Collection<String> relationships = getRelationshipTokens(referencedComponent, relationshipTypeId, 0);
							stringBuffer.append(joinResultsWithDelimiters(relationships, relationshipOccurrences, delimiter, includeRelationshipId));
							break;

						case DATAYPE:
							final DatatypeSnomedDsvExportItem datatypeItem = (DatatypeSnomedDsvExportItem) exportItem;
							final String datatypeName = exportItem.getDisplayName();
							final int datatypeOccurrences = exportItemOccurences.get(datatypeName);
							final Collection<String> datatypes = getDatatypeTokens(referencedComponent, datatypeName);
							final Collection<String> formattedDatatypeTerms = new ArrayList<String>();
							
							for (final String dataTypeTerm : datatypes) {
								
								if (!datatypeItem.isBooleanDatatype()) {
									formattedDatatypeTerms.add(dataTypeTerm);
									// XXX: add continue here? or else it could be added twice to the list 
								}
								
								if ("0".equals(dataTypeTerm)) {
									formattedDatatypeTerms.add("No");
								} else if ("1".equals(dataTypeTerm)) {
									formattedDatatypeTerms.add("Yes");
								} else {
									formattedDatatypeTerms.add(dataTypeTerm);
								}
							}

							stringBuffer.append(joinResultsWithDelimiters(formattedDatatypeTerms, datatypeOccurrences, delimiter, false));
							break;

						case PREFERRED_TERM:
							stringBuffer.append(getPreferredTerm(referencedComponent, includeDescriptionId));
							break;

						case CONCEPT_ID:
							stringBuffer.append(referencedComponent.getId());
							break;

						case MODULE: 
							stringBuffer.append(referencedComponent.getModuleId());
							break;

						case EFFECTIVE_TIME:
							stringBuffer.append(Dates.formatByGmt(referencedComponent.getEffectiveTime()));
							break;

						case STATUS_LABEL:
							stringBuffer.append(referencedComponent.isActive() ? "Active" : "Inactive");
							break;

						case DEFINITION_STATUS: 
							stringBuffer.append(referencedComponent.getDefinitionStatus());
							break;

						default:
							break;
					}
				}
				
				for (Integer groupId : groupedRelationships.keySet()) {
					if (groupId == 0) {
						continue;
					}
					
					for (String relationshipId : groupedRelationships.get(groupId).keySet()) {
						stringBuffer.append(delimiter);
						TreeSet<String> relationships = Sets.newTreeSet();
						relationships.addAll(getRelationshipTokens(referencedComponent, relationshipId, groupId));
						stringBuffer.append(joinResultsWithDelimiters(relationships, groupedRelationships.get(groupId).get(relationshipId), delimiter, includeRelationshipId));
					}
				}
				stringBuffer.append(System.getProperty("line.separator"));
				os.writeBytes(stringBuffer.toString());
				remainderMonitor.worked(1);
			}
			File zipFile = FileUtils.createZipArchive(file.getParentFile(), Files.createTempFile("export", ".zip").toFile());
			return zipFile;
		} catch (Exception e) {
			throw new SnowowlServiceException(e);
		} finally {
			if (file != null) {
				file.delete();
			}
			if (null != async) {
				async.stop();
			}
			if (null != remainderMonitor) {
				remainderMonitor.done();
			}
			if (null != monitor) {
				monitor.done();
			}
		}
	}

	private String getPreferredTerm(SnomedConcept concept, boolean includeDescriptionId) {
		SnomedDescription pt = concept.getPt();
		if (pt == null) {
			return includeDescriptionId ? "" + delimiter + "" : "";
		} else {
			return includeDescriptionId ? pt.getId() + delimiter + pt.getTerm() : pt.getTerm();
		}
	}

	private Collection<String> getDatatypeTokens(SnomedConcept referencedComponent, String datatypeName) {
		Collection<String> result = Lists.newArrayList();
		List<SnomedRelationship> items = referencedComponent.getRelationships().getItems();
		for (SnomedRelationship snomedRelationship : items) {
			List<SnomedReferenceSetMember> cdMembers = snomedRelationship.getMembers().getItems();
			for (SnomedReferenceSetMember cdMember : cdMembers) {
				Map<String, Object> properties = cdMember.getProperties();
				if (properties.containsKey(Fields.ATTRIBUTE_NAME) && properties.get(Fields.ATTRIBUTE_NAME).equals(datatypeName)) {
					result.add(properties.get(SnomedRf2Headers.FIELD_VALUE).toString());
				}
			}
		}
		
		return result;
	}

	private List<String> getRelationshipTokens(SnomedConcept referencedComponent, String relationshipTypeId, int groupNumber) {
		List<String> result = Lists.newArrayList();
		List<SnomedRelationship> relationships = referencedComponent.getRelationships().getItems();
		relationships.stream()
						.filter(relationship -> relationship.getTypeId().equals(relationshipTypeId) )
						.filter(relationship -> relationship.getGroup().equals(groupNumber))
						.forEach(relationship -> {
							SnomedConcept destination = relationship.getDestination();
							StringBuilder sb = new StringBuilder();
							if (includeRelationshipId) {
								sb.append(destination.getId()).append(delimiter);
							} 
							SnomedDescription pt = destination.getPt();
							result.add(sb.append( (pt == null) ? "" : pt.getTerm()).toString());
						});
		return result;
	}

	private List<String> getDescriptionTokens(SnomedConcept referencedComponent, String descriptionTypeId) {
		List<String> result = Lists.newArrayList();
		List<SnomedDescription> descriptions = referencedComponent.getDescriptions().getItems();
		descriptions.stream()
			.filter(description -> description.getTypeId().equals(descriptionTypeId))
			.forEach(description -> {
				StringBuilder sb = new StringBuilder();
				if (includeDescriptionId) {
					sb.append(description.getId()).append(delimiter);
				}
				result.add(sb.append(description.getTerm()).toString());
			});
		return result;
	}

	/**
	 * Searches for the maximum presence of the description, relationship or concrete data types. 
	 * Separated grouped and ungrouped relationships, generates the header strings and
	 * initializes the row descriptor object.
	 * @param refset 
	 * 
	 * @return with one or two header row in a collection. If there are two rows than the collection contains a new line character after the elements of the first row.
	 */
	private void createHeaderList(SnomedConcepts referencedComponents) {

		headerList = new ArrayList<String>();
		metaHeaderList = new ArrayList<String>();

		Map<String, String> descriptionTypeIdToTermMap = SnomedRequests.prepareSearchConcept()
			.all()
			.setLocales(locales)
			.filterByAncestor(Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT)
			.setExpand("pt()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getEventBus())
			.then(concepts -> toTypeIdTermMap(concepts))
			.getSync();
		
		Map<String, String> relationsshipTypeIdToTermMap = SnomedRequests.prepareSearchConcept()
				.all()
				.setLocales(locales)
				.filterByAncestor(Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.setExpand("pt()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getEventBus())
				.then(concepts -> toTypeIdTermMap(concepts))
				.getSync();
		
		
		exportItemOccurences = initOccurrenceMap(referencedComponents, exportItems);
		
		for (AbstractSnomedDsvExportItem exportItem : exportItems) {
			switch (exportItem.getType()) {
				
				case DESCRIPTION:
					final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
					final String descriptionTypeId = descriptionItem.getComponentId();
					int descriptionOccurrences = exportItemOccurences.get(descriptionTypeId);
					final String descriptionDisplayName = descriptionTypeIdToTermMap.getOrDefault(descriptionTypeId, descriptionItem.getDisplayName());
					
					// only one result
					if (2 > descriptionOccurrences) {
						if (includeDescriptionId) {
							metaHeaderList.add(descriptionDisplayName);
							metaHeaderList.add(descriptionDisplayName);
							headerList.add("descriptionId");
							headerList.add("term");
						} else {
							metaHeaderList.add(descriptionDisplayName);
							headerList.add("");
						}
						// zero or more than one result
					} else {
						for (int j = 1; j <= descriptionOccurrences; j++) {
							if (includeDescriptionId) {
								metaHeaderList.add(descriptionDisplayName + " (" + j + ")");
								metaHeaderList.add(descriptionDisplayName + " (" + j + ")");
								headerList.add("descriptionId");
								headerList.add("term");
							} else {
								metaHeaderList.add(descriptionDisplayName + " (" + j + ")");
								headerList.add("");
							}

						}
					}
					break;
					
				case RELATIONSHIP:
					final ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
					final String relationshipTypeId = String.valueOf(relationshipItem.getComponentId());
					final String relationshipDisplayName = relationsshipTypeIdToTermMap.getOrDefault(relationshipTypeId, relationshipItem.getDisplayName());
					
					// if the relationship occurs as an ungrouped relationship.
					sortToRelationshipGroups(referencedComponents, relationshipTypeId);
					
					LinkedHashMap<String, Integer> groupZeroCounts = groupedRelationships.getOrDefault(0, new LinkedHashMap<>());
					final int relationshipOccurrences = groupZeroCounts.getOrDefault(relationshipTypeId, 0);
					if (relationshipOccurrences > 0) {
						
						// only one result
						if (2 > relationshipOccurrences) {
							if (includeRelationshipId) {
								metaHeaderList.add(relationshipDisplayName);
								metaHeaderList.add(relationshipDisplayName);
								headerList.add("Id");
								headerList.add("Name");
							} else {
								metaHeaderList.add(relationshipDisplayName);
								headerList.add("");
							}
							// zero or more than one result
						} else {
							for (int j = 1; j <= relationshipOccurrences; j++) {
								if (includeRelationshipId) {
									metaHeaderList.add(relationshipDisplayName + " (" + j + ")");
									metaHeaderList.add(relationshipDisplayName + " (" + j + ")");
									headerList.add("Id");
									headerList.add("Name");
								} else {
									metaHeaderList.add(relationshipDisplayName + " (" + j + ")");
									headerList.add("");
								}
							}
						}
						// if the relationship occurs only as a grouped
						// relationship, collect it in an other collection.
					} else {
						groupedOnlyItems.add(exportItem);
					}
					
					break;
				
				case DATAYPE:
					final DatatypeSnomedDsvExportItem datatypeItem = (DatatypeSnomedDsvExportItem) exportItem;
					final String datatypeDisplayName = datatypeItem.getDisplayName();
					final int datatypeOccurrences = exportItemOccurences.get(datatypeDisplayName);

					// only one result
					if (2 > datatypeOccurrences) {
						metaHeaderList.add(datatypeDisplayName);
						headerList.add("");
						// zero or more than one result
					} else {
						for (int j = 1; j <= datatypeOccurrences; j++) {
							metaHeaderList.add(datatypeDisplayName + " (" + j + ")");
							headerList.add("");
						}
					}
					
					break;

				case PREFERRED_TERM:
					if (includeDescriptionId) {
						metaHeaderList.add(exportItem.getDisplayName());
						metaHeaderList.add(exportItem.getDisplayName());
						headerList.add("descriptionId");
						headerList.add("term");
					} else {
						metaHeaderList.add(exportItem.getDisplayName());
						headerList.add("");
					}
					
					break;

				default:
					metaHeaderList.add(exportItem.getDisplayName());
					headerList.add("");
			}
		}
		
		// add the property group columns to the header
		for (Integer groupId : groupedRelationships.keySet()) {
			if (groupId == 0) {
				continue;
			}
			
			for (String relationshipTypeId : groupedRelationships.get(groupId).keySet()) {
				String relationshipName = relationsshipTypeIdToTermMap.get(relationshipTypeId);
				if (1 == groupedRelationships.get(groupId).get(relationshipTypeId)) {
					if (includeRelationshipId) {
						metaHeaderList.add(relationshipName + " (AG" + groupId + ")");
						metaHeaderList.add(relationshipName + " (AG" + groupId + ")");
						headerList.add("Id");
						headerList.add("Name");
					} else {
						metaHeaderList.add(relationshipName + " (AG" + groupId + ")");
						headerList.add("");
					}
				} else {
					for (int j = 1; j <= groupedRelationships.get(groupId).get(relationshipTypeId); j++) {
						if (includeRelationshipId) {
							metaHeaderList.add(relationshipName + " (" + j + ") " + " (AG" + groupId + ")");
							metaHeaderList.add(relationshipName + " (" + j + ") " + " (AG" + groupId + ")");
							headerList.add("Id");
							headerList.add("Name");
						} else {
							metaHeaderList.add(relationshipName + " (" + j + ") " + " (AG" + groupId + ")");
							headerList.add("");
						}
					}
				}
			}
		}
		// remove the only grouped relationships from the export items, because
		// they will be exported particularly by groups.
		exportItems.removeAll(groupedOnlyItems);
	}

	private Map<String, String> toTypeIdTermMap(SnomedConcepts concepts) {
		return concepts.getItems()
					.stream()
					.collect(Collectors.toMap(concept -> concept.getId(), concept -> {
							SnomedDescription pt = concept.getPt();
							return pt == null ? concept.getId() : pt.getTerm();
						}));
	}

	private void sortToRelationshipGroups(SnomedConcepts referencedComponents, String relationshipTypeId) {
		
		Map<Integer, Integer> groupedRelationshipsForConcept = Maps.newHashMap();
		for (SnomedConcept concept : referencedComponents) {
			groupedRelationshipsForConcept.clear();
			concept.getRelationships().getItems().stream()
				.filter(relationship -> relationship.getType().getId().equals(relationshipTypeId))
				.forEach(relationship -> {
					groupedRelationshipsForConcept.merge(relationship.getGroup(), 1, (oldValue, newValue) -> oldValue + newValue);
				});
				
		groupedRelationshipsForConcept.entrySet().stream().forEach(entry -> {
			groupedRelationships.compute(entry.getKey(), (key, oldValue) -> {
				LinkedHashMap<String, Integer> relationshipTypeOccurence = ofNullable(oldValue).orElseGet(LinkedHashMap::new);
				relationshipTypeOccurence.merge(relationshipTypeId, entry.getValue(), Math::max);
				return relationshipTypeOccurence;
			});
		});
		}
	}

	private Map<String, Integer> initOccurrenceMap(SnomedConcepts referencedComponents, Collection<AbstractSnomedDsvExportItem> exportColumns) {
		Map<String, Integer> result = Maps.newHashMap();
		for (AbstractSnomedDsvExportItem column : exportColumns) {
			for (SnomedConcept concept : referencedComponents.getItems()) {
				switch (column.getType()) {
				case DESCRIPTION: {
					ComponentIdSnomedDsvExportItem item = (ComponentIdSnomedDsvExportItem) column;
					Integer count = (int) concept.getDescriptions()
							.getItems().stream()
							.filter(description -> description.getTypeId().equals(item.getComponentId()))
							.count();
					result.merge(item.getComponentId(), count,  Math::max);
					break;
				}
				case RELATIONSHIP: {
					// Will keep track of occurrences by group and relationship type ID in groupedRelationships (including group 0)
					break;
				}
				case DATAYPE: {
					DatatypeSnomedDsvExportItem item = (DatatypeSnomedDsvExportItem) column;
					
					Integer count = (int) concept.getRelationships()
									.getItems().stream()
									.flatMap(relationship -> relationship.getMembers().getItems().stream())
									.filter(cdMember -> cdMember.getProperties().get(Fields.ATTRIBUTE_NAME).toString().contains(item.getDisplayName()))
									.count();
					result.merge(item.getDisplayName(), count, Math::max);
					break;
				}
				default: break;
				}
			}
		}

		return result;
	}

	private String joinResultsWithDelimiters(Collection<String> results, int max, String delimiter, boolean includeComponentId) {
		StringBuffer sb = new StringBuffer();
		sb.append(Joiner.on(delimiter).join(results));

		int start = 0;

		if (sb.length() == 0) {
			if (includeComponentId) {
				sb.append(delimiter);
			}
			start = results.size() + 1;
		} else {
			start = results.size();
		}

		// fill the remaining slots with delimiters
		for (int j = start; j < max; j++) {
			sb.append(delimiter);
			if (includeComponentId) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	/*
	 * Fetch members of the specified refset identifier.
	 * Don't include members that are active BUT their identified component are inactive.
	 * @param refSetId
	 * @param includeInactive
	 * @return
	 */
	private SnomedConcepts getReferencedComponentConcepts(String refSetId, boolean includeInactive) {
		
		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember()
			.all()
			.filterByRefSet(refSetId);
		
		if(!includeInactive) {
			requestBuilder.filterByActive(true);
		}
		
		return requestBuilder
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getEventBus())
			.then(members ->  members.getItems().stream().map(m -> m.getReferencedComponent().getId()).collect(Collectors.toSet()))
			.then(this::getReferencedConcepts)
			.getSync();
	}
	
	private SnomedConcepts getReferencedConcepts(Set<String> conceptIds) {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(conceptIds)
				.filterByActive(true)
				.setExpand("pt()")
				.setLocales(locales)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getEventBus())
				.then(concepts -> expandDescriptions(concepts))
				.then(concepts -> expandRelationships(concepts))
				.getSync();
	}
	
	private SnomedConcepts expandRelationships(SnomedConcepts concepts) {
		SnomedRequests.prepareSearchRelationship()
						.all()
						.filterByActive(true)
						.filterBySource(conceptIds(concepts))
						.filterByCharacteristicTypes(Sets.newHashSet(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP))
						.setExpand("source(),destination(expand(pt())),type(expand(pt())),members(expand(pt()))")
						.setLocales(locales)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
						.execute(getEventBus())
						.then(relationships -> mapToSourceConcepts(relationships, concepts))
						.getSync();
		
		return concepts;
	}

	private SnomedRelationships mapToSourceConcepts(SnomedRelationships relationships, SnomedConcepts concepts) {
		ImmutableListMultimap<String, SnomedRelationship> relationshipsBySourceId = Multimaps.index(relationships.getItems(), (relationship) -> relationship.getSourceId());
		concepts.forEach(concept -> {
			List<SnomedRelationship> collection = relationshipsBySourceId.get(concept.getId());
			concept.setRelationships(new SnomedRelationships(collection, null, null, collection.size(), collection.size()));
		});
		return relationships;
	}

	private List<String> conceptIds(SnomedConcepts concepts) {
		return Lists.transform(concepts.getItems(), IComponent.ID_FUNCTION);
	}

	private SnomedConcepts expandDescriptions(SnomedConcepts concepts) {
		SnomedRequests.prepareSearchDescription()
						.all()
						.filterByActive(true)
						.filterByConceptId(conceptIds(concepts))
						.setLocales(locales)
						.setExpand("concept(),type(expand(pt())),members(expand(pt()))"/* expanding each description's language refset members */)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
						.execute(getEventBus())
						.then(resultDescriptions -> mapToConcepts(concepts, resultDescriptions))
						.getSync();
		
		return concepts;
	}

	private SnomedDescriptions mapToConcepts(SnomedConcepts concepts, SnomedDescriptions resultDescriptions) {
		ImmutableListMultimap<String, SnomedDescription> descriptionsByOwnerConceptId = Multimaps.index(resultDescriptions.getItems(), description -> description.getConceptId());
		
		concepts.forEach(concept -> {
			List<SnomedDescription> descriptions = descriptionsByOwnerConceptId.get(concept.getId());
			concept.setDescriptions(new SnomedDescriptions(descriptions, null, null, descriptions.size(), descriptions.size()));
		});
		
		return resultDescriptions;
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}