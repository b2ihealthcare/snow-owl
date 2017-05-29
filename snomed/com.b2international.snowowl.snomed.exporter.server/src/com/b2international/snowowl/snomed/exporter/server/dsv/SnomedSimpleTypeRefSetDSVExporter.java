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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.ComponentIdSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.DatatypeSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class implements the export process of the DSV export for simple type reference sets. 
 * Used by the SnomedSimpleTypeRefSetExportServerIndication class.
 * 
 * 
 */

public class SnomedSimpleTypeRefSetDSVExporter implements IRefSetDSVExporter {

	/**
	 * This enumeration represents the different query types. It is used in the executeQuery method, to select the appropriate query string for the prepared statements.
	 * 
	 * 
	 */

	private enum QueryType {
		DESCRIPTION, RELATIONSHIP, DATATYPE;
	}

	/**
	 * Directory of the temporary file on the server side.
	 */
	private static String TEMPORARY_WORKING_DIRECTORY;

	private String refSetId;
	private boolean includeDescriptionId;
	private boolean includeRelationshipId;
	private Collection<AbstractSnomedDsvExportItem> exportItems;
	private Collection<AbstractSnomedDsvExportItem> groupedOnlyItems;
	private IBranchPath branchPath;
	private String delimiter;

	private Map<Integer, LinkedHashMap<String, Integer>> groupedRelationships;
	private Map<String, Integer> exportItemMaxOccurences;
	private Collection<String> headerList;
	private Collection<String> metaHeaderList;

	private List<ExtendedLocale> locales;

	/**
	 * Creates a new instance with the export parameters. Called by the SnomedSimpleTypeRefSetDSVExportServerIndication.
	 * 
	 * @param exportSetting
	 */

	public SnomedSimpleTypeRefSetDSVExporter(final SnomedRefSetDSVExportModel exportSetting) {

		this.refSetId = exportSetting.getRefSetId();
		this.includeDescriptionId = exportSetting.includeDescriptionId();
		this.includeRelationshipId = exportSetting.includeRelationshipTargetId();
		this.exportItems = exportSetting.getExportItems();
		this.locales = exportSetting.getLocales();
		this.delimiter = exportSetting.getDelimiter();
		this.branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());

		SnomedSimpleTypeRefSetDSVExporter.TEMPORARY_WORKING_DIRECTORY = exportSetting.getExportPath();
		groupedRelationships = Maps.newTreeMap();
		exportItemMaxOccurences = Maps.newHashMap();
		groupedOnlyItems = Lists.newArrayList();
//		synonymAndDescendants = SnomedRequests.prepareGetSynonyms()
//				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
//				.execute(getEventBus())
//				.then(concepts -> FluentIterable.from(concepts).transform(IComponent.ID_FUNCTION).toSet())
//				.getSync();
	}

	/**
	 * Executes the export to delimiter separated values.
	 * 
	 * @param monitor
	 * @return The file with the exported values.
	 * @throws SnowowlServiceException
	 */
	@Override
	public File executeDSVExport(OMMonitor monitor) throws SnowowlServiceException {

		monitor.begin(100);
		Async async = monitor.forkAsync(80);
		OMMonitor remainderMonitor = null;
		File file = new File(TEMPORARY_WORKING_DIRECTORY);
		try ( final DataOutputStream os = new DataOutputStream(new FileOutputStream(file)) ) {

			file.createNewFile();

			SnomedReferenceSet refset = getRefset(refSetId);
			createHeaderList(refset);

			// write the header to the file
			StringBuffer sb = new StringBuffer();
			for (String metaHeaderListElement : metaHeaderList) {
				if (sb.length() > 0) {
					sb.append(delimiter);
				}
				sb.append(metaHeaderListElement);
			}

			if (includeDescriptionId || includeRelationshipId) {
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
			remainderMonitor.begin(refset.getMembers().getTotal());
			// write data to the file row by row
			for (SnomedReferenceSetMember refsetMember : refset.getMembers().getItems()) {
				StringBuffer stringBuffer = new StringBuffer();

				for (AbstractSnomedDsvExportItem exportItem : exportItems) {
					if (stringBuffer.length() > 0) {
						stringBuffer.append(delimiter);
					}
					
					switch (exportItem.getType()) {
						
						case DESCRIPTION:
							final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String descriptionTypeId = descriptionItem.getComponentId();
							final int descriptionOccurrences = exportItemMaxOccurences.get(descriptionTypeId);
							
							final Collection<String> descriptions = getDescriptionTokens(QueryType.DESCRIPTION, refsetMember, descriptionTypeId);
							stringBuffer.append(joinResultsWithDelimiters(descriptions, descriptionOccurrences, delimiter, includeDescriptionId));
							break;

						case RELATIONSHIP:
							final ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String relationshipTypeId = String.valueOf(relationshipItem.getComponentId());
							final int relationshipOccurrences = exportItemMaxOccurences.get(relationshipTypeId);
							
							final Collection<String> relationships = getRelationshipTokens(QueryType.RELATIONSHIP, refsetMember, relationshipTypeId, true, 0);
							stringBuffer.append(joinResultsWithDelimiters(relationships, relationshipOccurrences, delimiter, includeRelationshipId));
							break;

						case DATAYPE:
							final DatatypeSnomedDsvExportItem datatypeItem = (DatatypeSnomedDsvExportItem) exportItem;
							final String datatypeName = exportItem.getDisplayName();
							final int datatypeOccurrences = exportItemMaxOccurences.get(datatypeName);
							final Collection<String> datatypes = getDatatypeTokens(QueryType.DATATYPE, refsetMember, datatypeName);
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
							stringBuffer.append(getPreferredTerm(refsetMember, includeDescriptionId));
							break;

						case CONCEPT_ID:
							stringBuffer.append(refsetMember.getReferencedComponent().getId());
							break;

						case MODULE: 
							stringBuffer.append(refsetMember.getReferencedComponent().getModuleId());
							break;

						case EFFECTIVE_TIME:
							stringBuffer.append(Dates.formatByGmt(refsetMember.getReferencedComponent().getEffectiveTime()));
							break;

						case STATUS_LABEL:
							stringBuffer.append(refsetMember.getReferencedComponent().isActive() ? "Active" : "Inactive");
							break;

						case DEFINITION_STATUS: 
							stringBuffer.append(toConcept(refsetMember).getDefinitionStatus());
							break;

						default:
							break;
					}
				}
				
				for (Integer groupId : groupedRelationships.keySet()) {
					for (String relationshipId : groupedRelationships.get(groupId).keySet()) {
						stringBuffer.append(delimiter);
						TreeSet<String> relationships = Sets.newTreeSet();
						relationships.addAll(getRelationshipTokens(QueryType.RELATIONSHIP, refsetMember, relationshipId, true, groupId));
						stringBuffer.append(joinResultsWithDelimiters(relationships, groupedRelationships.get(groupId).get(relationshipId), delimiter, includeRelationshipId));
					}
				}
				stringBuffer.append(System.getProperty("line.separator"));
				os.writeBytes(stringBuffer.toString());
				remainderMonitor.worked(1);
			}
		} catch (Exception e) {
			throw new SnowowlServiceException(e);
		} finally {
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
		return file;
	}

	private String getPreferredTerm(SnomedReferenceSetMember refsetMember, boolean includeDescriptionId) {
		SnomedDescription pt = toConcept(refsetMember).getPt();
		return includeDescriptionId ? pt.getId() + delimiter + pt.getTerm() : pt.getTerm();
	}

	private Collection<String> getDatatypeTokens(QueryType datatype, SnomedReferenceSetMember refsetMember, String datatypeName) {
		Collection<String> result = Lists.newArrayList();
		SnomedConcept concept = toConcept(refsetMember);
		List<SnomedRelationship> items = concept.getRelationships().getItems();
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

	private SnomedConcept toConcept(SnomedReferenceSetMember refsetMember) {
		return (SnomedConcept) refsetMember.getReferencedComponent();
	}

	private Collection<String> getRelationshipTokens(QueryType relationship, SnomedReferenceSetMember refsetMember,String relationshipTypeId, boolean filterByGroupNumber, int groupNumber) {
		return null;
	}

	private Collection<String> getDescriptionTokens(QueryType description, SnomedReferenceSetMember refsetMember, String descriptionTypeId) {
		return null;
	}

	/**
	 * Searches for the maximum presence of the description, relationship or concrete data types. 
	 * Separated grouped and ungrouped relationships, generates the header strings and
	 * initializes the row descriptor object.
	 * @param refset 
	 * 
	 * @return with one or two header row in a collection. If there are two rows than the collection contains a new line character after the elements of the first row.
	 */
	private void createHeaderList(SnomedReferenceSet refset) {

		headerList = new ArrayList<String>();
		metaHeaderList = new ArrayList<String>();

		List<SnomedReferenceSetMember> members = refset.getMembers().getItems();
		
		Map<String, Integer> occurenceByTypeId = initOccurrenceMap(members, exportItems);
		
		for (AbstractSnomedDsvExportItem exportItem : exportItems) {
			switch (exportItem.getType()) {
				
				case DESCRIPTION:
					final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
					final String descriptionTypeId = descriptionItem.getComponentId();
					final int descriptionOccurrences = getMaxOccurence(occurenceByTypeId, descriptionTypeId);
					final String descriptionDisplayName = descriptionItem.getDisplayName();
					
					exportItemMaxOccurences.put(descriptionTypeId, descriptionOccurrences);
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
					final String relationshipDisplayName = relationshipItem.getDisplayName();
					
					// if the relationship occurs as an ungrouped relationship.
					if (sortToRelationshipGroups(members, relationshipTypeId)) {
						
						final int relationshipOccurrences = getMaxOccurence(occurenceByTypeId, relationshipTypeId);
						exportItemMaxOccurences.put(relationshipTypeId, relationshipOccurrences);
						
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
					final int datatypeOccurrences = getMaxOccurence(occurenceByTypeId, datatypeDisplayName);
					exportItemMaxOccurences.put(datatypeDisplayName, datatypeOccurrences);

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
			for (String relationshipTypeId : groupedRelationships.get(groupId).keySet()) {
				if (1 == groupedRelationships.get(groupId).get(relationshipTypeId)) {
					if (includeRelationshipId) {
						metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (AG" + groupId + ")");
						metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (AG" + groupId + ")");
						headerList.add("Id");
						headerList.add("Name");
					} else {
						metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (AG" + groupId + ")");
						headerList.add("");
					}
				} else {
					for (int j = 1; j <= groupedRelationships.get(groupId).get(relationshipTypeId); j++) {
						if (includeRelationshipId) {
							metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (" + j + ") " + " (AG" + groupId + ")");
							metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (" + j + ") " + " (AG" + groupId + ")");
							headerList.add("Id");
							headerList.add("Name");
						} else {
							metaHeaderList.add(getNameProvider().getComponentLabel(branchPath, relationshipTypeId) + " (" + j + ") " + " (AG" + groupId + ")");
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

	private boolean sortToRelationshipGroups(List<SnomedReferenceSetMember> members, String relationshipTypeId) {
		
		Map<Integer, Integer> groupsAndCounts = Maps.newHashMap();

		AtomicBoolean occoursAsUngrouped =  new AtomicBoolean(false);
		AtomicBoolean noOccurance = new AtomicBoolean(false);
		
		asConceptStream(members)
			.forEach(concept -> {
					AtomicInteger i = new AtomicInteger();
					
					concept.getRelationships().getItems().stream()
						.filter(relationship -> relationship.getType().getId().equals(relationshipTypeId))
						.forEach(relationship -> {

							if (relationship.getGroup() == 0) {
								occoursAsUngrouped.set(true);
							} else {
								groupsAndCounts.compute(relationship.getGroup(), (key, oldValue) -> ofNullable(oldValue).orElse(0) + 1 );
							}
							i.incrementAndGet();
					});
					
					if (i.get() == 1) {
						noOccurance.set(true);
					}
					
					groupsAndCounts.entrySet().stream().forEach(entry -> {
						groupedRelationships.compute(entry.getKey(), (key, oldValue) -> {
								LinkedHashMap<String, Integer> relationshipTypeOccurence = ofNullable(oldValue).orElseGet(LinkedHashMap::new);
								relationshipTypeOccurence.compute(relationshipTypeId, (innerKey, innerOldValue) -> Math.max(ofNullable(innerOldValue).orElse(0), entry.getValue()));
								return relationshipTypeOccurence;
							});
					});
					
				} 
			);
		
		return occoursAsUngrouped.get() || noOccurance.get();
	}

	private Map<String, Integer> initOccurrenceMap(List<SnomedReferenceSetMember> members, Collection<AbstractSnomedDsvExportItem> exportColumns) {
		Map<String, Integer> result = Maps.newHashMap();
		
		asConceptStream(members).forEach(concept -> {

			exportColumns.stream().forEach(column -> {

				switch (column.getType()) {
					case DESCRIPTION: {
						ComponentIdSnomedDsvExportItem item = (ComponentIdSnomedDsvExportItem) column;
						int count = (int) concept.getDescriptions()
								.getItems().stream()
								.filter(description -> description.getTypeId().equals(item.getComponentId()))
								.count();
						result.compute(item.getComponentId(), (key, oldCountValue) -> Math.max(oldCountValue, count));
						break;
					}
					case RELATIONSHIP: {
						ComponentIdSnomedDsvExportItem item = (ComponentIdSnomedDsvExportItem) column;
						int count = (int) concept.getRelationships()
								.getItems().stream()
								.filter(relationship -> relationship.getTypeId().equals(item.getComponentId()))
								.count();
						result.compute(item.getComponentId(), (key, oldCountValue) -> Math.max(oldCountValue, count));
						break;
					}
					case DATAYPE: {
						DatatypeSnomedDsvExportItem item = (DatatypeSnomedDsvExportItem) column;
						
						int count = (int) concept.getRelationships()
								.getItems().stream()
								.flatMap(relationship -> relationship.getMembers().getItems().stream())
								.filter(cdMember -> cdMember.getProperties().get(Fields.ATTRIBUTE_NAME).toString().contains(item.getDisplayName()))
								.count();
						result.compute(item.getDisplayName(), (key, oldCountValue) -> Math.max(oldCountValue, count));
						break;
					}
					default: break;
				}
			});
		});
		
		return result;
	}

	private ISnomedConceptNameProvider getNameProvider() {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class);
	}


	/**
	 * This method implements a maximum search. It founds the maximum presence of a given description, relationship or concrete datatype.
	 * @param occurenceByTypeId 
	 * @param members 
	 * 
	 * @param queryType
	 *            - type of the query can be description, relationship and data type.
	 * @param typeId
	 *            - type id of the description or relationship or data type.
	 * @return
	 */
	private int getMaxOccurence(Map<String, Integer> occurenceByTypeId, String typeId) {
		return occurenceByTypeId.get(typeId);
	}

	private Stream<SnomedConcept> asConceptStream(List<SnomedReferenceSetMember> members) {
		return members.stream()
			.map(member -> member.getReferencedComponent())
			.map(SnomedConcept.class::cast);
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

	
	/**
	 * Fetch members of the specified refset identifier.
	 * Don't include inactive members or members that are active BUT their identified component are inactive.
	 * @param refSetId
	 * @return
	 */
	private SnomedReferenceSet getRefset(String refSetId) {
		String expand = MessageFormat.format(""
				+ "members("
				+ "			limit:{0},"
				+ "			active:true,"
				+ "			expand("
				+ "					referencedComponent(" //referenced components are concept in DSV export
				+ "										active:true,"
				+ "										expand("
				+ "												pt(),"
				+ "												descriptions("
				+ "															limit:{0},"
				+ "															active:true,"
				+ "															type(expand(pt())),"
				+ "															expand("
				+ "																	members(" //expanding each description's language refset members
				+ "																			limit:{0},"
				+ "																			expand(pt())"
				+ "																	)"
				+ "															)"
				+ "												),"
				+ "												relationships("
				+ "															limit:{0},"
				+ "															active:true,"
				+ "															expand(members(limit:{0},expand(pt())))," // expanding each relationships's concrete domain members 
				+ "															destination(expand(pt())),"
				+ "															type(expand(pt()))"
				+ "												)"
				+ "										)"
				+ "					)"
				+ "			)"
				+ ")", String.valueOf(Integer.MAX_VALUE));
		SnomedReferenceSet refset = 
			SnomedRequests.prepareGetReferenceSet(refSetId)
				.setExpand(expand)
				.setLocales(locales)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getEventBus())
			.getSync();
		return refset;
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}