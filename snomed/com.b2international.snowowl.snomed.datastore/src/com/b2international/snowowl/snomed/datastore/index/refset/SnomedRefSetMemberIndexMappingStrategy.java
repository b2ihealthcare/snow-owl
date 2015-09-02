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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.datastore.BranchPathUtils.createActivePath;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.getTerminologyComponentIdValue;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION_SORT_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.util.BytesRef;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.INameProviderFactory;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.SortKeyMode;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.base.Preconditions;

public class SnomedRefSetMemberIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final SnomedRefSetMember member;
	private String label;
	private Map<String, String> idLabelCache;
	
	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember) {
		this(refSetMember, null, Collections.<String, String>emptyMap());
	}

	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember, @Nullable final Map<String, String> idLabelCache) {
		this(refSetMember, null, idLabelCache);
	}
	
	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember, @Nullable final String label) {
		this(refSetMember, label, Collections.<String, String>emptyMap());
	}

	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember, @Nullable final String label, @Nullable final Map<String, String> idLabelCache) {
		this.label = label;
		this.member = Preconditions.checkNotNull(refSetMember, "Reference set member cannot be null.");
		this.idLabelCache = null == idLabelCache ? Collections.<String, String>emptyMap() : idLabelCache;
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	public Document createDocument() {
		final long storageKey = getStorageKey();
		final SnomedDocumentBuilder doc = SnomedMappings.doc()
				.storageKey(storageKey)
				.active(member.isActive())
				.memberRefSetType(member.getRefSet().getType())
				.memberReferencedComponentType((int) member.getReferencedComponentType())
				.memberReferencedComponentId(member.getReferencedComponentId())
				.module(member.getModuleId())
				.memberRefSetId(member.getRefSetIdentifierId())
				.field(REFERENCE_SET_MEMBER_UUID, member.getUuid())
				.field(REFERENCE_SET_MEMBER_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(member.getEffectiveTime()))
				.storedOnly(COMPONENT_RELEASED, member.isReleased() ? 1 : 0);

		if (null == label) {
		
			if (!SnomedRefSetType.CONCRETE_DATA_TYPE.equals(member.getRefSet().getType())) {
				
				final String terminologyComponentId = getTerminologyComponentId(member.getReferencedComponentType());
				if (member instanceof SnomedQueryRefSetMember) {
					
					label = CoreTerminologyBroker.getInstance().getNameProviderFactory(terminologyComponentId).getNameProvider().getText(member);
					
				} else {
					
					switch (member.getReferencedComponentType()) {
						
						case SnomedTerminologyComponentConstants.REFSET_NUMBER: //$FALL-THROUGH$
						case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
							
							label = CoreTerminologyBroker.getInstance().getNameProviderFactory(terminologyComponentId).getNameProvider().getText(member);
							break;
							
							
						case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
							
							final SnomedDescriptionIndexEntry description = new SnomedDescriptionLookupService().getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE),
									member.getReferencedComponentId());
							
							//new descriptions in the transaction
							if (null == description) {
								
								final Description component = new SnomedDescriptionLookupService().getComponent(member.getReferencedComponentId(), member.cdoView());
								
								if (null != component) {
									
									label = component.getTerm();
												
								}	
								
								
							} else { //existing description
								
								label = description.getLabel();
								
							}
							
							break;
							
						case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
							
							final SnomedRelationshipIndexEntry relationship = ApplicationContext.getInstance().getService(SnomedStatementBrowser.class)
								.getStatement(BranchPathUtils.createPath(member.cdoView()), member.getReferencedComponentId());
							
							//new relationship in the transaction
							if (null == relationship) {
								
								final Relationship component = new SnomedRelationshipLookupService().getComponent(member.getReferencedComponentId(), member.cdoView());
								
								if (null != component) {
									
									label = CoreTerminologyBroker.getInstance().adapt(component).getLabel(); 
									
								}
								
								
							} else {
								
								label = relationship.getLabel();
								
							}
							
							break;
						
					}
					
				}
			}
			
			if (null == label) {
				
				if (SnomedRefSetType.CONCRETE_DATA_TYPE.equals(member.getRefSet().getType())) {
				
					final SnomedConcreteDataTypeRefSetMember dataTypeMember = (SnomedConcreteDataTypeRefSetMember) member;
					label = dataTypeMember.getLabel();
					
					if (null == label) {
						label = extractAttributeLabel(dataTypeMember);
					}
				
				} else {
				
					final ComponentIdentifierPair<String> identifierPair = createPair(getReferencedComponentType(member, member.getRefSet()), member.getReferencedComponentId());
					IComponent<?> component = CoreTerminologyBroker.getInstance().getComponent(identifierPair);
					
					if (null == component) {
						final Object cdoObject = CoreTerminologyBroker.getInstance().getLookupService(identifierPair.getTerminologyComponentId()).getComponent(identifierPair.getComponentId(), member.cdoView());
						if (null == cdoObject) {
							component = new IComponent<Object>() {
								private static final long serialVersionUID = 1L;
								@Override public Object getId() {
									return member.getReferencedComponentId();
								}
								@Override public String getLabel() {
									return member.getReferencedComponentId() + " (unresolved)";
								}
							};
						} else {
							component = CoreTerminologyBroker.getInstance().adapt(cdoObject);
						}
					}
					
					Preconditions.checkNotNull(component, "Unable to resolve component for " + member.getReferencedComponentId() + "(Type: " + member.getReferencedComponentType());
					label = component.getLabel();
				}
				
			}
			
		}
		doc.label(label);
		
		switch (member.getRefSet().getType()) {
			
			case SIMPLE : 
				//nothing else to do
				return doc.build();
				
			case ASSOCIATION:
				//set the target component ID. It's always a SNOMED CT concept
				final SnomedAssociationRefSetMember associationMember = (SnomedAssociationRefSetMember) member;
				return doc.field(REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID, associationMember.getTargetComponentId()).build();
			case ATTRIBUTE_VALUE:
				//set the member value ID. Again, it's always a SNOMED CT concept
				final SnomedAttributeValueRefSetMember attributeValueMember = (SnomedAttributeValueRefSetMember) member;
				return doc.field(REFERENCE_SET_MEMBER_VALUE_ID, attributeValueMember.getValueId()).build();
			case QUERY:
				//set the ESCG query from the member
				final SnomedQueryRefSetMember queryMember = (SnomedQueryRefSetMember) member;
				return doc.field(REFERENCE_SET_MEMBER_QUERY, queryMember.getQuery().trim()).build();
				
			case EXTENDED_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP:
				//cast member to complex map and set complex map properties to the document
				final SnomedComplexMapRefSetMember complexMember = (SnomedComplexMapRefSetMember) member;
				doc.storedOnly(REFERENCE_SET_MEMBER_MAP_GROUP, complexMember.getMapGroup());
				doc.storedOnly(REFERENCE_SET_MEMBER_MAP_PRIORITY, complexMember.getMapPriority());
				if (null != complexMember.getMapRule()) {
					doc.field(REFERENCE_SET_MEMBER_MAP_RULE, complexMember.getMapRule());
				}
				if (null != complexMember.getMapAdvice()) {
					doc.field(REFERENCE_SET_MEMBER_MAP_ADVICE, complexMember.getMapAdvice());
				}
				if (null != complexMember.getMapCategoryId()) {
					doc.field(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID, Long.valueOf(complexMember.getMapCategoryId()));
				}
				doc.field(REFERENCE_SET_MEMBER_CORRELATION_ID, Long.valueOf(complexMember.getCorrelationId()));
				
				final String complexMapTargetComponentId = complexMember.getMapTargetComponentId();
				final short complexMapTargetComponentType = complexMember.getMapTargetComponentType();
				
				doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, complexMapTargetComponentId);
				doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, (int) complexMapTargetComponentType);
				
				if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == complexMapTargetComponentType) {
					
					doc.storedOnly(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, complexMapTargetComponentId); //unknown map target
				
				} else {
					
					final INameProviderFactory nameProviderFactory = CoreTerminologyBroker.getInstance().getNameProviderFactory(getTerminologyComponentId(complexMapTargetComponentType));
					final String mapTargetLabel = nameProviderFactory.getNameProvider().getText(complexMapTargetComponentId);
					doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, mapTargetLabel);
				}
				return doc.build();
				
			case DESCRIPTION_TYPE:
				//set description type ID, label and description length
				final SnomedDescriptionTypeRefSetMember descriptionMember = (SnomedDescriptionTypeRefSetMember) member;
				doc.field(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID, Long.valueOf(descriptionMember.getDescriptionFormat()));
				doc.storedOnly(REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH, descriptionMember.getDescriptionLength());
				//description type must be a SNOMED CT concept
				final String descriptionFormatLabel = getCoreComponentLabel(descriptionMember.getDescriptionFormat(), descriptionMember.cdoView());
				doc.field(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_LABEL, descriptionFormatLabel);
				return doc.build();
				
			case LANGUAGE:
				//set description acceptability label and ID
				final SnomedLanguageRefSetMember languageMember = (SnomedLanguageRefSetMember) member;
				doc.field(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, Long.valueOf(languageMember.getAcceptabilityId()));
				//acceptability ID always represents a SNOMED CT concept
				final String acceptabilityLabel = getCoreComponentLabel(languageMember.getAcceptabilityId(), languageMember.cdoView());
				doc.field(REFERENCE_SET_MEMBER_ACCEPTABILITY_LABEL, acceptabilityLabel);
				return doc.build();
				
			case CONCRETE_DATA_TYPE:
				
				//set operator ID, serialized value, UOM ID (if any) and characteristic type ID
				final SnomedConcreteDataTypeRefSetMember dataTypeMember = (SnomedConcreteDataTypeRefSetMember) member;
				doc.field(REFERENCE_SET_MEMBER_OPERATOR_ID, Long.valueOf(dataTypeMember.getOperatorComponentId()));
				doc.field(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, dataTypeMember.getSerializedValue());
				if (null != dataTypeMember.getUomComponentId()) {
					doc.field(REFERENCE_SET_MEMBER_UOM_ID, Long.valueOf(dataTypeMember.getUomComponentId()));
				}
				
				if (null != dataTypeMember.getCharacteristicTypeId()) {
					doc.field(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID, Long.valueOf(dataTypeMember.getCharacteristicTypeId()));
				}
				
				final Document document = doc.build();
				if (dataTypeMember.getUomComponentId() != null) {
					document.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_UOM_ID, Long.parseLong(dataTypeMember.getUomComponentId())));
				}
				
				final DataType dataType = SnomedRefSetUtil.getDataType(member.getRefSetIdentifierId());
				document.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_DATA_TYPE_VALUE, (byte) dataType.ordinal()));
				document.add(new BinaryDocValuesField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE, new BytesRef(dataTypeMember.getSerializedValue())));
				
				if (null != label) {
					SortKeyMode.SEARCH_ONLY.add(document, label);
				}
				
				if (member.eContainer() instanceof Component) {
					final String containerModuleId = ((Component) member.eContainer()).getModule().getId();
					document.add(new NumericDocValuesField(REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID, Long.valueOf(containerModuleId)));	
				}
				return document;
				
			case SIMPLE_MAP:
				//set map target ID, type and label
				final SnomedSimpleMapRefSetMember mapMember = (SnomedSimpleMapRefSetMember) member;
				final String simpleMapTargetComponentId = mapMember.getMapTargetComponentId();
				final short simpleMapTargetComponentType = mapMember.getMapTargetComponentType();
				
				doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, simpleMapTargetComponentId);
				doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, (int) simpleMapTargetComponentType);
				
				if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == simpleMapTargetComponentType) {
					
					doc.storedOnly(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, simpleMapTargetComponentId); //unknown map target
				
				} else {
					
					final CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
					final String terminologyComponentId = getTerminologyComponentId(simpleMapTargetComponentType);
					final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(terminologyComponentId);
					final INameProviderFactory nameProviderFactory = terminologyBroker.getNameProviderFactory(terminologyComponentId);
					String mapTargetLabel = nameProviderFactory.getNameProvider().getComponentLabel(createActivePath(repositoryUuid), simpleMapTargetComponentId);
					if (isEmpty(mapTargetLabel)) {
						mapTargetLabel = simpleMapTargetComponentId;
					}
					doc.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL, mapTargetLabel);
				}
				
				final String componentDescription = mapMember.getMapTargetComponentDescription();
				if (null != componentDescription) {
					doc.tokenizedField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION, componentDescription);
					doc.searchOnlyField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION_SORT_KEY, IndexUtils.getSortKey(componentDescription));
				}
				
				return doc.build();
				
			case MODULE_DEPENDENCY:
				final SnomedModuleDependencyRefSetMember dependencyMember = (SnomedModuleDependencyRefSetMember) member;
				
				doc.field(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(dependencyMember.getSourceEffectiveTime()));
				doc.field(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(dependencyMember.getTargetEffectiveTime()));
				
				return doc.build();
			
			default:
			
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + member.getRefSet().getType());
		}
		
	}

	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(member.cdoID());
	}
	
	/*returns with the referenced component type value*/
	private short getReferencedComponentType(final SnomedRefSetMember member, final SnomedRefSet refSet) {
		return null != member.getRefSet() ? member.getReferencedComponentType() : refSet.getReferencedComponentType();
	}

	/*retrieves the attribute label at all cost. throw illegal argument exception if the referenced component type is not supported.*/
	private String extractAttributeLabel(final SnomedConcreteDataTypeRefSetMember member) {
		final String referencedComponentId = member.getReferencedComponentId();
		String conceptId = null;
		switch (getTerminologyComponentIdValue(referencedComponentId)) {
			case RELATIONSHIP_NUMBER:
				final ILookupService<String, Relationship, CDOView> service = CoreTerminologyBroker.getInstance().getLookupService(RELATIONSHIP);
				final IComponent<String> component = service.getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), referencedComponentId);
				//look into the lightweight store
				if (component instanceof IStatement) {
					final IStatement<?> statement = (IStatement<?>) component;
					return getCoreComponentLabel(String.valueOf(statement.getAttributeId()), member.cdoView());
					//if we found the relationship type's preferred term
				}
				//else look into the CDO
				Relationship relationship = service.getComponent(referencedComponentId, member.cdoView());
				//get it from the underlying CDO view
				if (null == relationship)
					for (final Relationship newRelationship : ComponentUtils2.getNewObjects(member.cdoView(), Relationship.class)) {
						if (referencedComponentId.equals(newRelationship.getId())) {
							relationship = newRelationship;
							break;
						}
					}
				
				//we found the relationship type concept -> found the concept's PT
				conceptId = relationship.getType().getId();
			case CONCEPT_NUMBER:
				//the referenced component was a concept
				if (null == conceptId)
					conceptId = referencedComponentId;
				
				return SnomedConceptNameProvider.INSTANCE.getText(conceptId, member.cdoView());
			default:
				throw new IllegalArgumentException("Illegal referenced component identifier. ID: " + referencedComponentId);
		}
	}
	
	/*creates a component identifier pair*/
	private ComponentIdentifierPair<String> createPair(final short terminologyComponentIdValue, final String componentId) {
		return ComponentIdentifierPair.<String>create(getTerminologyComponentId(terminologyComponentIdValue), componentId);
	}
	
	/*returns with the short value of the passed in unique terminology component identifier*/
	private String getTerminologyComponentId(final short terminologyComponentIdValue) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentIdValue);
	}

	/*returns with the label of the SNOMED CT core component.*/
	private String getCoreComponentLabel(final String componentId, final CDOView view) {

		String label = idLabelCache.get(componentId);
		
		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		label = componentService.getLabels(BranchPathUtils.createPath(view), componentId)[0];

		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = SnomedConceptNameProvider.INSTANCE.getText(componentId);
		
		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = SnomedConceptNameProvider.INSTANCE.getText(componentId, view);
		
		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = componentId;
		
		return label;
	}
}
