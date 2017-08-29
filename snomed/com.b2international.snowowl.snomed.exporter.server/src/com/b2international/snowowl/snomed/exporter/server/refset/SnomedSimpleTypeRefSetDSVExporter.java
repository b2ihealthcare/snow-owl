/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.exporter.model.ComponentIdSnomedDsvExportItem;
import com.b2international.snowowl.snomed.exporter.model.DatatypeSnomedDsvExportItem;
import com.b2international.snowowl.snomed.exporter.model.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.exporter.model.SnomedRefSetDSVExportModel;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * This class implements the export process of the DSV export for simple type reference sets. Used by the SnomedSimpleTypeRefSetExportServerIndication
 * class.
 */
public class SnomedSimpleTypeRefSetDSVExporter implements IRefSetDSVExporter {

	/**
	 * This enumeration represents the different query types. It is used in the executeQuery method, to select the appropriate query string for the
	 * prepared statements.
	 */

	private enum QueryType {

		DESCRIPTION, RELATIONSHIP, DATATYPE;

		/**
		 * Returns the appropriate export query string.
		 * 
		 * @return
		 */
		public String getQueryString(boolean filterOnGroupId) {
			switch (this) {
				case DESCRIPTION:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DESCRIPTION_QUERY;
				case RELATIONSHIP:
					return SnomedRefSetExporterQueries.buildDSVExportRelationshipQuery(filterOnGroupId);
				case DATATYPE:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DATATYPE_QUERY;
				default:
					return null;
			}
		}

		/**
		 * Returns the string of a query which checks if the element has been removed on the branch.
		 * 
		 * @return
		 */
		public String getRemovedCheckerQueryString() {
			switch (this) {
				case DESCRIPTION:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_DESCRIPTION_QUERY;
				case RELATIONSHIP:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_RELATIONSHIP_QUERY;
				case DATATYPE:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REMOVED_DATATYPE_QUERY;
				default:
					return null;
			}
		}

		/**
		 * Returns the string of a query which checks if the element has been inactivated on the branch.
		 * 
		 * @return
		 */
		public String getInactivatedCheckerQueryString() {
			switch (this) {
				case DESCRIPTION:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_DESCRIPTION_QUERY;
				case RELATIONSHIP:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_RELATIONSHIP_QUERY;
				case DATATYPE:
					return SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_DATATYPE_QUERY;
				default:
					return null;
			}
		}
	}

	private String workingDir;
	private Connection connection;

	private String refSetId;
	private boolean descriptionIdExpected;
	private boolean relationshipTargetIdExpected;
	private Collection<AbstractSnomedDsvExportItem> exportItems;
	private Collection<AbstractSnomedDsvExportItem> groupedOnlyItems;
	private int branchId;
	private long branchBase;
	private IBranchPath branchPath;
	private Long languageConfiguration;
	private String delimiter;

	private Map<Long, String> conceptCDOAndSnomedIds;

	private Map<Integer, LinkedHashMap<String, Integer>> groupedRelationships;
	private Map<String, Integer> exportItemMaxOccurences;
	private Collection<String> headerList;
	private Collection<String> metaHeaderList;

	private IEventBus bus;
	private Set<String> synonymAndDescendantIds;
	private Map<String, ISnomedDescription> ptMap;
	private Map<String, ISnomedDescription> fsnMap;
	private Multimap<String, ISnomedDescription> otherDescriptions;

	/**
	 * Creates a new instance with the export parameters. Called by the SnomedSimpleTypeRefSetDSVExportServerIndication.
	 * 
	 * @param exportSetting
	 */

	public SnomedSimpleTypeRefSetDSVExporter(SnomedRefSetDSVExportModel exportSetting) {

		this.refSetId = exportSetting.getRefSetId();
		this.descriptionIdExpected = exportSetting.isDescriptionIdExpected();
		this.relationshipTargetIdExpected = exportSetting.isRelationshipTargetExpected();
		this.exportItems = exportSetting.getExportItems();
		this.languageConfiguration = exportSetting.getLanguageConfigurationId();
		this.delimiter = exportSetting.getDelimiter();
		this.branchId = exportSetting.getBranchID();
		this.branchBase = exportSetting.getBranchBase();
		this.branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());

		workingDir = exportSetting.getExportPath();
		groupedRelationships = Maps.newTreeMap();
		exportItemMaxOccurences = Maps.newHashMap();
		groupedOnlyItems = Lists.newArrayList();
		
		bus = ApplicationContext.getServiceForClass(IEventBus.class);
		
		synonymAndDescendantIds = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByEscg("<<" + Concepts.SYNONYM)
			.filterByActive(true)
			.build(branchPath.getPath())
			.execute(getBus())
			.then(new Function<SnomedConcepts, Set<String>>() {
				@Override
				public Set<String> apply(SnomedConcepts input) {
					return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
				}
			}).getSync();
		
		// set up the database connection
		connection = ApplicationContext.getInstance().getService(ICDORepositoryManager.class).get(SnomedPackage.eINSTANCE).getConnection();
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
		File file = new File(workingDir);
		
		try ( final DataOutputStream os = new DataOutputStream(new FileOutputStream(file)) ) {

			file.createNewFile();
			
			if (isDescriptionExportItemPresent()) {
				fetchDescriptions();
			}

			conceptCDOAndSnomedIds = getExportedMemberConceptCDOAndSnomedIds(refSetId);

			createHeaderList();

			// write the header to the file
			StringBuffer sb = new StringBuffer();
			for (String metaHeaderListElement : metaHeaderList) {
				if (sb.length() > 0) {
					sb.append(delimiter);
				}
				sb.append(metaHeaderListElement);
			}

			if (descriptionIdExpected || relationshipTargetIdExpected) {
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
			remainderMonitor.begin(conceptCDOAndSnomedIds.size());
			
			// write data to the file row by row
			for (Long conceptCDOId : conceptCDOAndSnomedIds.keySet()) {
				StringBuffer stringBuffer = new StringBuffer();

				for (AbstractSnomedDsvExportItem exportItem : exportItems) {
					if (stringBuffer.length() > 0) {
						stringBuffer.append(delimiter);
					}
					
					switch (exportItem.getType()) {
						
						//preferred term is a separate type!
						case DESCRIPTION:
							final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String descriptionTypeId = String.valueOf(descriptionItem.getComponentId());
							
							//FSN
							if (descriptionTypeId.equals(Concepts.FULLY_SPECIFIED_NAME)) {
								ISnomedDescription description = fsnMap.get(conceptCDOAndSnomedIds.get(conceptCDOId));
								Collection<String> descriptionResult = descriptionIdExpected ? singleton(description.getId() + delimiter + description.getTerm()) : singleton(description.getTerm()); 
								stringBuffer.append(joinResultsWithDelimiters(descriptionResult, 1, descriptionIdExpected));
							} else {
								
								Collection<ISnomedDescription> descriptions = otherDescriptions.get(conceptCDOAndSnomedIds.get(conceptCDOId));
								final Collection<String> results = FluentIterable.from(descriptions).filter(new Predicate<ISnomedDescription>() {
									@Override
									public boolean apply(ISnomedDescription input) {
										return input.getTypeId().equals(descriptionTypeId);
									}
								}).transform(new Function<ISnomedDescription, String>() {
									@Override
									public String apply(ISnomedDescription input) {
										return descriptionIdExpected ? input.getId() + delimiter + input.getTerm() : input.getTerm();
									}
								}).toList();
								
								stringBuffer.append(joinResultsWithDelimiters(results, exportItemMaxOccurences.get(descriptionTypeId), descriptionIdExpected));
							}
							
							break;

						case RELATIONSHIP:
							final ComponentIdSnomedDsvExportItem relationshipItem = (ComponentIdSnomedDsvExportItem) exportItem;
							final String relationshipTypeId = String.valueOf(relationshipItem.getComponentId());
							final int relationshipOccurrences = exportItemMaxOccurences.get(relationshipTypeId);
							
							final Collection<String> relationships = executeRelationshipOrDatatypeQuery(QueryType.RELATIONSHIP, conceptCDOId, relationshipTypeId, true, 0);
							stringBuffer.append(joinResultsWithDelimiters(relationships, relationshipOccurrences, relationshipTargetIdExpected));
							break;

						case DATAYPE:
							final DatatypeSnomedDsvExportItem datatypeItem = (DatatypeSnomedDsvExportItem) exportItem;
							final String datatypeName = exportItem.getDisplayName();
							final int datatypeOccurrences = exportItemMaxOccurences.get(datatypeName);
							final Collection<String> datatypes = executeRelationshipOrDatatypeQuery(QueryType.DATATYPE, conceptCDOId, datatypeName, false, null);
							final Collection<String> formattedDatatypeTerms = new ArrayList<String>();
							
							for (final String dataTypeTerm : datatypes) {
								
								if (!datatypeItem.isBooleanDatatype()) {
									formattedDatatypeTerms.add(dataTypeTerm);
								}
								
								if ("0".equals(dataTypeTerm)) {
									formattedDatatypeTerms.add("No");
								} else if ("1".equals(dataTypeTerm)) {
									formattedDatatypeTerms.add("Yes");
								} else {
									formattedDatatypeTerms.add(dataTypeTerm);
								}
							}

							stringBuffer.append(joinResultsWithDelimiters(formattedDatatypeTerms, datatypeOccurrences, false));
							break;

						case PREFERRED_TERM:
							ISnomedDescription pt = ptMap.get(conceptCDOAndSnomedIds.get(conceptCDOId));
							Collection<String> descriptionResult = descriptionIdExpected ? singleton(pt.getId() + delimiter + pt.getTerm()) : singleton(pt.getTerm()); 
							stringBuffer.append(joinResultsWithDelimiters(descriptionResult, 1, descriptionIdExpected));
							break;

						case CONCEPT_ID:
							stringBuffer.append(conceptCDOAndSnomedIds.get(conceptCDOId));
							break;

						case MODULE: 
							stringBuffer.append(getModule(conceptCDOId));
							break;

						case EFFECTIVE_TIME:
							stringBuffer.append(getEffectiveTime(conceptCDOId));
							break;

						case STATUS_LABEL:
							stringBuffer.append(isActive(conceptCDOId) ? "Active" : "Inactive");
							break;

						case DEFINITION_STATUS: 
							stringBuffer.append(getDefinitionStatus(conceptCDOId));
							break;

						default:
							break;
					}
				}
				
				for (Integer groupId : groupedRelationships.keySet()) {
					for (String relationshipId : groupedRelationships.get(groupId).keySet()) {
						stringBuffer.append(delimiter);
						TreeSet<String> relationships = Sets.newTreeSet();
						relationships.addAll(executeRelationshipOrDatatypeQuery(QueryType.RELATIONSHIP, conceptCDOId, relationshipId, true, groupId));
						stringBuffer
								.append(joinResultsWithDelimiters(relationships, groupedRelationships.get(groupId).get(relationshipId), relationshipTargetIdExpected));
					}
				}
				stringBuffer.append(System.getProperty("line.separator"));
				os.writeBytes(stringBuffer.toString());
				remainderMonitor.worked(1);
			}
			connection.close();
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
	
	private void fetchDescriptions() {
		
		final boolean isPreferredTermPresent = FluentIterable.from(exportItems).firstMatch(new Predicate<AbstractSnomedDsvExportItem>() {
			@Override
			public boolean apply(AbstractSnomedDsvExportItem input) {
				return SnomedDsvExportItemType.PREFERRED_TERM == input.getType();
			}
		}).isPresent();
		
		boolean isFsnPresent = FluentIterable.from(exportItems).filter(ComponentIdSnomedDsvExportItem.class).firstMatch(new Predicate<ComponentIdSnomedDsvExportItem>() {
			@Override
			public boolean apply(ComponentIdSnomedDsvExportItem input) {
				return SnomedDsvExportItemType.DESCRIPTION == input.getType() && String.valueOf(input.getComponentId()).equals(Concepts.FULLY_SPECIFIED_NAME);
			}
		}).isPresent();
		
		final Set<String> otherDescriptionTypeIds = FluentIterable.from(exportItems).filter(ComponentIdSnomedDsvExportItem.class).filter(new Predicate<ComponentIdSnomedDsvExportItem>() {
			@Override
			public boolean apply(ComponentIdSnomedDsvExportItem input) {
				return SnomedDsvExportItemType.DESCRIPTION == input.getType() && !String.valueOf(input.getComponentId()).equals(Concepts.FULLY_SPECIFIED_NAME);
			}
		}).transform(new Function<ComponentIdSnomedDsvExportItem, String>() {
			@Override
			public String apply(ComponentIdSnomedDsvExportItem input) {
				return String.valueOf(input.getComponentId());
			}
		}).toSet();
		
		Set<String> expandOptions = newHashSet();
		if (isPreferredTermPresent) {
			expandOptions.add("pt()");
		}
		
		if (isFsnPresent) {
			expandOptions.add("fsn()");
		}
		
		if (!otherDescriptionTypeIds.isEmpty()) {
			expandOptions.add("descriptions()");
		}
		
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByEscg("^" + refSetId)
			.filterByActive(true)
			.setExpand(Joiner.on(',').join(expandOptions))
			.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
			.build(branchPath.getPath())
			.executeSync(getBus());
		
		ptMap = newHashMap();
		fsnMap = newHashMap();
		otherDescriptions = ArrayListMultimap.create();
		
		for (final ISnomedConcept concept : concepts) {
			
			if (isPreferredTermPresent) {
				ptMap.put(concept.getId(), concept.getPt());
			}
			
			if (isFsnPresent) {
				fsnMap.put(concept.getId(), concept.getFsn());
			}
			
			if (!otherDescriptionTypeIds.isEmpty()) {
				
				List<ISnomedDescription> filteredDescriptions = FluentIterable.from(concept.getDescriptions())
						.filter(new Predicate<ISnomedDescription>() {
							@Override
							public boolean apply(ISnomedDescription input) {
								boolean condition = input.isActive() && otherDescriptionTypeIds.contains(input.getTypeId());
								return isPreferredTermPresent ? !input.getId().equals(concept.getPt().getId()) && condition : condition;
							}
				}).toList();
				
				for (ISnomedDescription description : filteredDescriptions) {
					otherDescriptions.put(concept.getId(), description);
				}
				
			}
			
		}
		
	}

	private boolean isDescriptionExportItemPresent() {
		return FluentIterable.from(exportItems).firstMatch(new Predicate<AbstractSnomedDsvExportItem>() {
			@Override
			public boolean apply(AbstractSnomedDsvExportItem exportItem) {
				return SnomedDsvExportItemType.DESCRIPTION == exportItem.getType() || SnomedDsvExportItemType.PREFERRED_TERM == exportItem.getType();
			}
		}).isPresent();
	}

	/**
	 * Searches for the maximum presence of the description, relationship or concrete data types. Separated grouped and ungrouped relationships, generates the header strings and
	 * initializes the row descriptor object.
	 * 
	 * @return with one or two header row in a collection. If there are two rows than the collection contains a new line character after the elements of the first row.
	 * @throws SQLException
	 */
	private void createHeaderList() throws SQLException {

		headerList = new ArrayList<String>();
		metaHeaderList = new ArrayList<String>();

		for (AbstractSnomedDsvExportItem exportItem : exportItems) {
			switch (exportItem.getType()) {
				
				case DESCRIPTION:
					final ComponentIdSnomedDsvExportItem descriptionItem = (ComponentIdSnomedDsvExportItem) exportItem;
					final String descriptionTypeId = String.valueOf(descriptionItem.getComponentId());
					int descriptionOccurrences;

					if (descriptionTypeId.equals(Concepts.FULLY_SPECIFIED_NAME)) {
						descriptionOccurrences = 1; // FIXME? single FSN?
					} else {
						descriptionOccurrences = getFilteredOtherDescriptions(descriptionTypeId);
					}
					
					final String descriptionDisplayName = descriptionItem.getDisplayName();
					
					exportItemMaxOccurences.put(descriptionTypeId, descriptionOccurrences);

					if (descriptionOccurrences < 2) {
						if (descriptionIdExpected) {
							metaHeaderList.add(descriptionDisplayName);
							metaHeaderList.add(descriptionDisplayName);
							headerList.add("descriptionId");
							headerList.add("term");
						} else {
							metaHeaderList.add(descriptionDisplayName);
							headerList.add("");
						}
					} else {
						for (int j = 1; j <= descriptionOccurrences; j++) {
							if (descriptionIdExpected) {
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
					if (sortToPropertyGroups(relationshipTypeId)) {
						
						final int relationshipOccurrences = getMaxOccurence(QueryType.RELATIONSHIP, relationshipTypeId);
						exportItemMaxOccurences.put(relationshipTypeId, relationshipOccurrences);
						
						if (relationshipOccurrences < 2) {
							if (relationshipTargetIdExpected) {
								metaHeaderList.add(relationshipDisplayName);
								metaHeaderList.add(relationshipDisplayName);
								headerList.add("Id");
								headerList.add("Name");
							} else {
								metaHeaderList.add(relationshipDisplayName);
								headerList.add("");
							}
						} else {
							for (int j = 1; j <= relationshipOccurrences; j++) {
								if (relationshipTargetIdExpected) {
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
					final int datatypeOccurrences = getMaxOccurence(QueryType.DATATYPE, datatypeDisplayName);
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
					if (descriptionIdExpected) {
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
					if (relationshipTargetIdExpected) {
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
						if (relationshipTargetIdExpected) {
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

	private int getFilteredOtherDescriptions(final String descriptionTypeId) {
		int max = 0;
		for (Entry<String, Collection<ISnomedDescription>> entry : otherDescriptions.asMap().entrySet()) {
			
			int numberOfTypes = FluentIterable.from(entry.getValue()).filter(new Predicate<ISnomedDescription>() {
				@Override
				public boolean apply(ISnomedDescription input) {
					return input.getTypeId().equals(descriptionTypeId);
				}
			}).size();
			
			if (numberOfTypes > max) {
				max = numberOfTypes;
			}
		}
		return max;
	}

	private ISnomedConceptNameProvider getNameProvider() {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class);
	}

	/**
	 * Sorts the relationship to property groups.
	 * 
	 * @param relationshipCDOId
	 * @return true if the relationship occurs also as an ungrouped relationship.
	 * @throws SQLException
	 */

	private boolean sortToPropertyGroups(String relationshipCDOId) throws SQLException {

		boolean occoursAsUngrouped = false;
		boolean noOccurance = false;

		HashMap<Integer, Integer> groupsAndCounts = Maps.newHashMap();
		PreparedStatement statement = connection.prepareStatement(QueryType.RELATIONSHIP.getQueryString(false));
		statement.setString(2, relationshipCDOId);
		statement.setInt(3, branchId);
		statement.setInt(4, branchId);
		statement.setLong(5, branchBase);
		statement.setLong(6, branchBase);

		PreparedStatement removedCheckerStatement = connection.prepareStatement(QueryType.RELATIONSHIP.getRemovedCheckerQueryString());
		removedCheckerStatement.setInt(2, branchId);

		for (Long conceptCDOId : conceptCDOAndSnomedIds.keySet()) {
			statement.setLong(1, conceptCDOId);
			ResultSet resultSet = statement.executeQuery();
			int i = 1;
			while (resultSet.absolute(i)) {
				long CDOId = resultSet.getLong(1);
				Integer group = resultSet.getInt(4);
				long resultBranch = resultSet.getLong(3);
				// if the result comes from the main branch, it is possible that
				// it
				// has been removed on the actual branch.
				if (0 == resultBranch) {
					removedCheckerStatement.setLong(1, CDOId);
					ResultSet removedCheckerResultSet = removedCheckerStatement.executeQuery();
					// if the result is on the branch with negative version,
					// than it
					// has been removed.
					if (removedCheckerResultSet.absolute(1) && removedCheckerResultSet.getLong(1) < 0) {
						i++;
						continue;
					}
				}
				if (0 == group) {
					occoursAsUngrouped = true;
					i++;
					continue;
				}
				if (groupsAndCounts.get(group) != null) {
					Integer count = groupsAndCounts.get(group);
					groupsAndCounts.put(group, count++);
				} else {
					groupsAndCounts.put(group, 1);
				}
				i++;
			}
			if (1 == i) {
				noOccurance = true;
			}
			for (Integer groupId : groupsAndCounts.keySet()) {
				// if there is no item with the actual group id
				if (null == groupedRelationships.get(groupId)) {
					groupedRelationships.put(groupId, new LinkedHashMap<String, Integer>());
					groupedRelationships.get(groupId).put(relationshipCDOId, groupsAndCounts.get(groupId));
					// if there is no item with the given relationship id
				} else if (null == groupedRelationships.get(groupId).get(relationshipCDOId)) {
					groupedRelationships.get(groupId).put(relationshipCDOId, groupsAndCounts.get(groupId));
				} else if (groupsAndCounts.get(groupId) > groupedRelationships.get(groupId).get(relationshipCDOId)) {
					groupedRelationships.get(groupId).put(relationshipCDOId, groupsAndCounts.get(groupId));
				} else {
					continue;
				}
			}

		}
		return (occoursAsUngrouped || noOccurance);
	}

	/**
	 * This method implements a maximum search. It founds the maximum presence of a given description, relationship or concrete datatype.
	 * 
	 * @param queryType
	 *            - type of the query can be description, relationship and data type.
	 * @param typeString
	 *            - type string of the description or relationship or data type.
	 * @return
	 * @throws SQLException
	 */
	private int getMaxOccurence(QueryType queryType, String typeString) throws SQLException {
		int max = -1;
		int size;
		for (Long conceptCDOId : conceptCDOAndSnomedIds.keySet()) {
			
			if (QueryType.RELATIONSHIP.equals(queryType)) {
				size = executeRelationshipOrDatatypeQuery(queryType, conceptCDOId, typeString, true, 0).size();
			} else {
				size = executeRelationshipOrDatatypeQuery(queryType, conceptCDOId, typeString, false, null).size();
			}
			
			if (size > max) {
				max = size;
			}
		}
		return max;
	}

	/**
	 * Returns with the results of the query.
	 * 
	 * @param queryType
	 *            - type of the query can be RELATIONSHIP or DATATYPE.
	 * @param conceptCDOId
	 *            - CDO ID of the exported concept.
	 * @param typeId
	 *            - type string of the description or relationship or data type.
	 * @param filterOnGroupId
	 *            - denotes if the results are filtered on a certain property group. It is interpreted only on relationships.
	 * @param groupId
	 *            - property group id of the relationship.
	 * @return the results of the query in a collection.
	 * @throws SQLException
	 */

	private Collection<String> executeRelationshipOrDatatypeQuery(QueryType queryType, long conceptCDOId, String typeId, boolean filterOnGroupId, Integer groupId)
			throws SQLException {

		Preconditions.checkArgument((QueryType.RELATIONSHIP.equals(queryType) || QueryType.DATATYPE.equals(queryType)), "Query type is not RELATIONSHIP or DATATYPE but "
				+ queryType.toString());
		// sorted map is used to order relationship target names alphabetically
		Map<String, String> resultCollection = new TreeMap<String, String>();

		PreparedStatement statement = connection.prepareStatement(queryType.getQueryString(filterOnGroupId));
		int paramIndex = 1;

		statement.setLong(paramIndex++, conceptCDOId);
		statement.setString(paramIndex++, typeId);
		statement.setInt(paramIndex++, branchId);
		statement.setInt(paramIndex++, branchId);
		statement.setLong(paramIndex++, branchBase);
		statement.setLong(paramIndex++, branchBase);
		if (QueryType.RELATIONSHIP.equals(queryType) && filterOnGroupId) {
			statement.setInt(paramIndex++, groupId);
		}
		ResultSet resultSet = statement.executeQuery();

		PreparedStatement removedCheckerStatement = connection.prepareStatement(queryType.getRemovedCheckerQueryString());
		removedCheckerStatement.setInt(2, branchId);

		PreparedStatement inactivatedCheckerStatement = connection.prepareStatement(queryType.getInactivatedCheckerQueryString());

		int i = 1;

		while (resultSet.absolute(i)) {
			long CDOId = resultSet.getLong(1);
			long resultBranch = resultSet.getLong(3);
			// if the result comes from the main branch, it is possible that is
			// has been removed on the actual branch.
			if (0 == resultBranch) {
				removedCheckerStatement.setLong(1, CDOId);
				ResultSet removedCheckerResultSet = removedCheckerStatement.executeQuery();
				// if the result is on the branch with negative version, than it
				// has been removed.
				if (removedCheckerResultSet.absolute(1) && removedCheckerResultSet.getLong(1) < 0) {
					i++;
					continue;
				}
			}
			// remove the deleted and already published relationships or concrete datatype elements
			inactivatedCheckerStatement.setLong(1, CDOId);
			inactivatedCheckerStatement.setInt(2, branchId);
			ResultSet inactivatedCheckerResultSet = inactivatedCheckerStatement.executeQuery();
			// if the latest version on the branch is inactive, then it has been removed.
			if (inactivatedCheckerResultSet.absolute(1) && !inactivatedCheckerResultSet.getBoolean(1)) {
				i++;
				continue;
			}
			if (QueryType.RELATIONSHIP.equals(queryType)) {
				if (relationshipTargetIdExpected) {
					// relationship with target id
					resultCollection.put(getPreferredTerm(resultSet.getLong(5), false), resultSet.getString(2) + delimiter + getPreferredTerm(resultSet.getLong(5), false));
				} else {
					// relationship without target id
					resultCollection.put(getPreferredTerm(resultSet.getLong(5), false), getPreferredTerm(resultSet.getLong(5), false));
				}
			} else {
				// datatype
				resultCollection.put(resultSet.getString(2), resultSet.getString(2));
			}
			i++;
		}

		return resultCollection.values();
	}
	
	/**
	 * Get the preferred term of a concept.
	 * 
	 * @param conceptCDOId
	 * @return A {@link SnomedConceptIndexEntry} object containing the label and storage key of the preferred term.
	 * @throws SQLException
	 */

	private String getPreferredTerm(long conceptCDOId, boolean descriptionIdExpected) throws SQLException {
		ResultSet resultSet = null;

		int i = 1;

		final PreparedStatement preferredTermStatement = connection.prepareStatement(SnomedRefSetExporterQueries.buildPreferredTermQuery(synonymAndDescendantIds.size()));
		preferredTermStatement.setString(i++, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);

		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setLong(i++, branchBase);
		preferredTermStatement.setLong(i++, branchBase);

		preferredTermStatement.setLong(i++, conceptCDOId);

		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setLong(i++, branchBase);
		preferredTermStatement.setLong(i++, branchBase);

		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection cdoConnection = connectionManager.get(SnomedPackage.eINSTANCE);
		final CDOBranch branch = cdoConnection.getSession().getBranchManager().getBranch(branchId);
		final CDOBranchPoint branchPoint = branch.getHead();
		
		for (final String typeId : synonymAndDescendantIds) {
			
			final int j = i++;
			
			CDOUtils.apply(new CDOViewFunction<Void, CDOView>(branchPoint) {
				@Override protected Void apply(final CDOView view) {

					final CDOID cdoId = new SnomedConceptLookupService().getComponent(typeId, view).cdoID();
					final long storageKey = CDOIDUtils.asLong(cdoId);
					
					try {
						preferredTermStatement.setLong(j, storageKey);
					} catch (final SQLException e) {
						throw new SnowowlRuntimeException(e);
					}
					
					return null;
				}
			});
			
		}

		preferredTermStatement.setString(i++, String.valueOf(languageConfiguration));

		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setInt(i++, branchId);
		preferredTermStatement.setLong(i++, branchBase);
		preferredTermStatement.setLong(i++, branchBase);

		resultSet = preferredTermStatement.executeQuery();
		resultSet.absolute(1);
		String result;
		if (resultSet.isFirst()) {
			result = resultSet.getString(1);
			if (descriptionIdExpected) {
				result = resultSet.getString(3) + delimiter + result;
			}
		} else {
			result = "";
			if (descriptionIdExpected) {
				result += delimiter;
			}
		}
		return result;
	}

	/**
	 * Returns the effective time of the exported concept.
	 * 
	 * @param conceptCDOId
	 * @return
	 * @throws SQLException
	 */

	private String getEffectiveTime(long conceptCDOId) throws SQLException {
		PreparedStatement effectiveTimeStatement = connection.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_EFFECTIVE_TIME_QUERY);
		effectiveTimeStatement.setLong(1, conceptCDOId);
		effectiveTimeStatement.setInt(2, branchId);
		effectiveTimeStatement.setInt(3, branchId);
		effectiveTimeStatement.setLong(4, branchBase);
		effectiveTimeStatement.setLong(5, branchBase);
		ResultSet resultSet = effectiveTimeStatement.executeQuery();
		if (resultSet.absolute(1)) {
			Date effectiveTime = resultSet.getDate(1);
			if (null == effectiveTime) {
				return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL;
			}
			return effectiveTime.toString();
		} else {
			return "";
		}

	}

	private String joinResultsWithDelimiters(Collection<String> results, int max, boolean idExpected) {
		StringBuffer sb = new StringBuffer();
		sb.append(Joiner.on(delimiter).join(results));

		if (max == 0) {
			// when result is empty the delimiter separating export items acts as the first necessary delimeter
			if (idExpected) {
				sb.append(delimiter);
			}
		} else {

			int start;
			if (results.isEmpty()) {
				start = 1; // since the first delimeter has been already appended
			} else {
				start = idExpected ? results.size() * 2 : results.size();
			}
			
			max = idExpected ? max * 2 : max;

			// fill the remaining slots with delimiters
			for (int j = start; j < max; j++) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	private Map<Long, String> getExportedMemberConceptCDOAndSnomedIds(String refSetId) throws SQLException {
		Map<Long, String> conceptCDOIds = Maps.newHashMap();
		PreparedStatement inactivatedCheckerStatement = connection
				.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_INACTIVATED_REFSET_MEMBER_CONCEPT_QUERY);
		PreparedStatement statement = connection.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_REFSET_MEMBER_QUERY);
		statement.setLong(1, Long.valueOf(refSetId));
		statement.setInt(2, branchId);
		statement.setInt(3, branchId);
		statement.setLong(4, branchBase);
		statement.setLong(5, branchBase);
		statement.setInt(6, branchId);
		statement.setInt(7, branchId);
		statement.setLong(8, branchBase);
		statement.setLong(9, branchBase);
		ResultSet resultSet = statement.executeQuery();
		int i = 1;
		while (resultSet.absolute(i)) {
			Long CDOId = resultSet.getLong(1);
			inactivatedCheckerStatement.setLong(1, CDOId);
			inactivatedCheckerStatement.setInt(2, branchId);
			ResultSet inactivatedCheckerResultSet = inactivatedCheckerStatement.executeQuery();
			/*
			 * Active refset members are skipped if the referenced concept is inactive!
			 */
			if (inactivatedCheckerResultSet.absolute(1) && !inactivatedCheckerResultSet.getBoolean(1)) {
				i++;
				continue;
			}
			conceptCDOIds.put(resultSet.getLong(1), resultSet.getString(2));
			i++;
		}
		return conceptCDOIds;
	}

	private boolean isActive(Long conceptCDOId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_IS_ACTIVE_CONCEPT_QUERY);
		statement.setLong(1, conceptCDOId);
		statement.setInt(2, branchId);
		statement.setInt(3, branchId);
		statement.setLong(4, branchBase);
		statement.setLong(5, branchBase);
		ResultSet resultSet = statement.executeQuery();

		if (resultSet.absolute(1)) {
			return resultSet.getBoolean(1);
		} else {
			return false;
		}

	}

	private String getDefinitionStatus(Long conceptCDOId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_DEFINITION_STATUS_QUERY);
		statement.setLong(1, conceptCDOId);
		statement.setInt(2, branchId);
		statement.setInt(3, branchId);
		statement.setLong(4, branchBase);
		statement.setLong(5, branchBase);
		ResultSet resultSet = statement.executeQuery();
		resultSet.absolute(1);
		return getPreferredTerm(resultSet.getLong(1), false);
	}

	private String getModule(Long conceptCDOId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SnomedRefSetExporterQueries.SQL_SIMPLE_TYPE_REFSET_DSV_EXPORT_MODULE_QUERY);
		statement.setLong(1, conceptCDOId);
		statement.setInt(2, branchId);
		statement.setInt(3, branchId);
		statement.setLong(4, branchBase);
		statement.setLong(5, branchBase);
		ResultSet resultSet = statement.executeQuery();
		resultSet.absolute(1);
		return getPreferredTerm(resultSet.getLong(1), false);
	}
	
	private IEventBus getBus() {
		return bus;
	}
}