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
package com.b2international.snowowl.scripting.services.api.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.scripting.services.api.IAuthoringService;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.google.common.base.Strings;

/**
 * Authoring service singleton implementation.
 * 
 * @see IAuthoringService
 */
public enum AuthoringService implements IAuthoringService {

	/**
	 * The authoring service singleton.
	 */
	INSTANCE;

	/** Logger instance for the {@link AuthoringService authoring service}. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthoringService.class);

	@Override
	public void addConcreteDomainDataTypeToRelationship(final SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String characteristicTypeId) {
		addConcreteDomainDataTypeToRelationship(editingContext, relationship, concreteDomainAttributeName, concreteDomainAttributeType, value,
				getModuleId(editingContext), characteristicTypeId);
	}
	@Override
	public void addConcreteDomainDataTypeToRelationship(final SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String moduleId, final String characteristicTypeId) {
		checkModuleId(moduleId);
		final SnomedRefSetEditingContext context = editingContext.getRefSetEditingContext();
		final String identifierConceptId = getIdentifierConceptId(concreteDomainAttributeType);
		final SnomedConcreteDataTypeRefSet refSet = context.lookup(identifierConceptId, SnomedConcreteDataTypeRefSet.class);

		final ComponentType componentType = ComponentType.RELATIONSHIP;

		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				relationship.getId(), 
				concreteDomainAttributeType, 
				value,
				characteristicTypeId,
				concreteDomainAttributeName, 
				moduleId, 
				refSet), "Error while creating concrete domain for "
				+ componentType + ": " + relationship.getId() + " with value " + value);

		// attach it to the relationship
		relationship.getConcreteDomainRefSetMembers().add(member);
	}
	
	@Override
	public void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, Relationship relationship,
			String concreteDomainAttributeName, DataType concreteDomainAttributeType, Object value, String uomId, String operatorId,
			String characteristicTypeId) {
		addConcreteDomainDataTypeToRelationship(editingContext, relationship, concreteDomainAttributeName, concreteDomainAttributeType, value, uomId,
				operatorId, getModuleId(editingContext), characteristicTypeId);
	}
	
	@Override
	public void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, Relationship relationship,
			String concreteDomainAttributeName, DataType concreteDomainAttributeType, Object value, String uomId, String operatorId,
			String moduleId, String characteristicTypeId) {
		checkModuleId(moduleId);
		final SnomedRefSetEditingContext context = editingContext.getRefSetEditingContext();
		final String identifierConceptId = getIdentifierConceptId(concreteDomainAttributeType);
		final SnomedConcreteDataTypeRefSet refSet = context.lookup(identifierConceptId, SnomedConcreteDataTypeRefSet.class);
		
		final ComponentType componentType = ComponentType.RELATIONSHIP;
		
		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				relationship.getId(),
				uomId, 
				operatorId,
				value,
				characteristicTypeId,
				concreteDomainAttributeName,
				moduleId,
				refSet), "Error while creating concrete domain for " + componentType + ": " + relationship.getId() + " with value " + value);
		
		// attach it to the relationship
		relationship.getConcreteDomainRefSetMembers().add(member);
	}

	private void checkModuleId(final String moduleId) {
		checkArgument(!Strings.isNullOrEmpty(moduleId), "ModuleId cannot be null or empty");
	}

	@Override
	public void addConcreteDomainDataTypeToConcept(final SnomedEditingContext editingContext, final Concept concept,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String characteristicTypeId) {
		addConcreteDomainDataTypeToConcept(editingContext, concept, concreteDomainAttributeName, concreteDomainAttributeType, value, getModuleId(editingContext), characteristicTypeId);
	}
	
	@Override
	public void addConcreteDomainDataTypeToConcept(final SnomedEditingContext editingContext, final Concept concept,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String moduleId, final String characteristicTypeId) {
		checkModuleId(moduleId);
		final SnomedRefSetEditingContext context = editingContext.getRefSetEditingContext();
		final String identifierConceptId = getIdentifierConceptId(concreteDomainAttributeType);
		final SnomedConcreteDataTypeRefSet refSet = context.lookup(identifierConceptId, SnomedConcreteDataTypeRefSet.class);

		final ComponentType componentType = ComponentType.CONCEPT;

		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				concept.getId(), 
				concreteDomainAttributeType, 
				value, 
				characteristicTypeId, concreteDomainAttributeName,
				moduleId, refSet), "Error while creating concrete domain for " + componentType + ": " + concept.getId()
				+ " with value " + value);

		// attach it to the concept
		concept.getConcreteDomainRefSetMembers().add(member);
	}

	/*
	 * returns with the proper identifier concept ID of the concrete domain
	 * reference set based on the specified data type.
	 */
	@Nonnull
	private String getIdentifierConceptId(final DataType dataType) {
		return checkNotNull(SnomedRefSetUtil.getConcreteDomainRefSetMap().get(dataType), "Error while getting identifier concept ID for concrete domain reference set. Type: " + dataType);
	}

	/* returns with the unique ID of the default SNOMED CT module concept. */
	@Nonnull
	private String getModuleId(final SnomedRefSetEditingContext context) {
		checkNotNull(context, "SNOMED CT reference set editing context argument cannot be null.");
		return checkString(checkNotNull(context.getSnomedEditingContext().getDefaultModuleConcept(), "Default SNOMED CT module concept was null.").getId());
	}
	
	/* returns with the unique ID of the default SNOMED CT module concept. */
	@Nonnull
	private String getModuleId(final SnomedEditingContext context) {
		checkNotNull(context, "SNOMED CT editing context argument cannot be null.");
		return checkString(checkNotNull(context.getDefaultModuleConcept(), "Default SNOMED CT module concept was null.").getId());
	}

	/*
	 * checks the specified CDO view instance. throw runtime exception if the
	 * specified CDO view cannot be referenced.returns with the view if it can
	 * be used safely.
	 */
	@Nonnull
	private CDOView checkView(final CDOView view) {
		if (null == view) {
			throw new NullPointerException("CDO view argument cannot be null.");
		}
		if (view.isClosed()) {
			throw new IllegalStateException("CDO view was closed. View ID: " + view.getViewID());
		}
		if (view.hasConflict()) {
			throw new IllegalStateException("CDO view has conflicts. View ID: " + view.getViewID());
		}
		return view;
	}

	/*
	 * checks whether the specified string instance is neither null nor empty.
	 * returns with the specified string instance.
	 */
	@Nonnull
	private String checkString(final String s) {
		if (StringUtils.isEmpty(s)) {
			throw new IllegalStateException("String argument cannot be null or empty: " + s);
		}
		return s;
	}

	/* returns with the branch manager service. */
	@Nonnull
	private ICDOConnection getConnection() {
		return checkNotNull(ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE), "CDO connection service was null.");
	}

	/**
	 * Enumeration implementation of the {@link IComponentProvider} and
	 * {@link IComponentIdentifierPairProvider} interfaces.
	 * 
	 * @see IComponentProvider
	 * @see IComponentIdentifierPairProvider
	 */
	private static enum ComponentType implements IComponentProvider, IComponentIdentifierPairProvider {
		CONCEPT("SNOMED C concept") {
			@Nonnull
			@Override
			public Annotatable getComponent(final CDOView view, final long componentId) {
				return checkNotNull(new SnomedConceptLookupService().getComponent(String.valueOf(componentId), view),
						"SNOMED CT concept cannot be found with ID: " + componentId);
			}

			@Nonnull
			@Override
			public ComponentIdentifier getComponentIdentifierPair(final long componentId) {
				return checkNotNull(SnomedRefSetEditingContext.createConceptTypePair(String.valueOf(componentId)),
						"SNOMED CT concept identifier pair was null. ID: " + componentId);
			}
		},
		RELATIONSHIP("SNOMED CT relationship") {
			@Nonnull
			@Override
			public Annotatable getComponent(final CDOView view, final long componentId) {
				return checkNotNull(new SnomedRelationshipLookupService().getComponent(String.valueOf(componentId), view),
						"SNOMED CT relationship cannot be found with ID: " + componentId);
			}

			@Nonnull
			@Override
			public ComponentIdentifier getComponentIdentifierPair(final long componentId) {
				return checkNotNull(SnomedRefSetEditingContext.createRelationshipTypePair(String.valueOf(componentId)),
						"SNOMED CT relationship identifier pair was null. ID: " + componentId);
			}
		};
		private final String name;

		private ComponentType(final String name) {
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return name;
		}

	}

	private static interface IComponentProvider {
		/**
		 * Returns with a SNOMED&nbsp;CT component identified by its unique ID.<br>
		 * The component is capable to store concrete domain data types.
		 * 
		 * @param view
		 *            the CDO view where the lookup should be performed.
		 * @param componentId
		 *            the unique ID of the SNOMED CT&nbsp;component.
		 * @return the SNOMED CT component.
		 */
		Annotatable getComponent(final CDOView view, final long componentId);
	}

	private static interface IComponentIdentifierPairProvider {
		/**
		 * Creates and returns with a component identifier pair based on the
		 * specified unique SNOMED&nbsp;CT component ID.
		 * 
		 * @param componentId
		 *            the unique ID of the SNOMED&nbsp;CT component.
		 * @return the identifier pair instance.
		 */
		ComponentIdentifier getComponentIdentifierPair(final long componentId);
	}

}