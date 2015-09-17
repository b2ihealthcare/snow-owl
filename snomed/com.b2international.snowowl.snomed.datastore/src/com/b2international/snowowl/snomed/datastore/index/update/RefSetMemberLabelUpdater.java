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
package com.b2international.snowowl.snomed.datastore.index.update;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.getTerminologyComponentIdValue;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.SortKeyMode;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;

/**
 * @since 4.3
 */
public class RefSetMemberLabelUpdater extends ComponentLabelUpdater<SnomedDocumentBuilder> {

	private SnomedRefSetMember member;
	private ComponentLabelProvider labelProvider;

	public RefSetMemberLabelUpdater(SnomedRefSetMember member, String label, ComponentLabelProvider labelProvider) {
		super(member.getUuid(), label);
		this.member = member;
		this.labelProvider = labelProvider;
	}
	
	private boolean isConcreteDomainMember(SnomedRefSetMember member) {
		return SnomedRefSetType.CONCRETE_DATA_TYPE.equals(member.getRefSet().getType());
	}
	
	@Override
	protected void updateLabelFields(SnomedDocumentBuilder doc, String label) {
		super.updateLabelFields(doc, label);
		if (isConcreteDomainMember(member) && label != null) {
			SortKeyMode.INSTANCE.update(doc, label);
		}
	}

	@Override
	protected String getLabel() {
		// recompute label if not defined for member
		String label = super.getLabel();
		if (null == label) {
			if (!isConcreteDomainMember(member)) {
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
				if (isConcreteDomainMember(member)) {
					final SnomedConcreteDataTypeRefSetMember dataTypeMember = (SnomedConcreteDataTypeRefSetMember) member;
					label = dataTypeMember.getLabel();
					if (null == label) {
						label = extractAttributeLabel(dataTypeMember);
					}
				} else {
					final ComponentIdentifierPair<String> identifierPair = createPair(getReferencedComponentType(member), member.getReferencedComponentId());
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
		return label;
	}
	
	/*returns with the short value of the passed in unique terminology component identifier*/
	private String getTerminologyComponentId(final short terminologyComponentIdValue) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentIdValue);
	}
	
	/*returns with the referenced component type value*/
	private short getReferencedComponentType(final SnomedRefSetMember member) {
		return null != member.getRefSet() ? member.getReferencedComponentType() : member.getRefSet().getReferencedComponentType();
	}

	/*creates a component identifier pair*/
	private ComponentIdentifierPair<String> createPair(final short terminologyComponentIdValue, final String componentId) {
		return ComponentIdentifierPair.<String>create(getTerminologyComponentId(terminologyComponentIdValue), componentId);
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
					return labelProvider.getComponentLabel(String.valueOf(statement.getAttributeId()));
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

}
