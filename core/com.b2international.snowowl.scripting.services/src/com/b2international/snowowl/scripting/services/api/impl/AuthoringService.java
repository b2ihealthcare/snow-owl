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
package com.b2international.snowowl.scripting.services.api.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nonnull;

import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.b2international.snowowl.scripting.services.api.IAuthoringService;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Preconditions;
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

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addConcreteDomainDataTypeToConcept(long, java.lang.String, com.b2international.snowowl.scripting.services.api.ConcreteDomainDataType, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void addConcreteDomainDataTypeToConcept(final long conceptId, final String concreteDomainAttributeName,
			final DataType concreteDomainAttributeType, final Object value, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToComponent(conceptId, concreteDomainAttributeName, concreteDomainAttributeType, 
				value, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addBooleanConcreteDomainTypeToConcept(long, java.lang.String, boolean, java.lang.String, java.lang.String)
	 */
	@Override
	public void addBooleanConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName,
			final boolean booleanValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToConcept(conceptId, concreteDomainAttributeName, 
				DataType.BOOLEAN, booleanValue, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addIntegerConcreteDomainTypeToConcept(long, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void addIntegerConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, final int intValue,
			final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToConcept(conceptId, concreteDomainAttributeName, 
				DataType.INTEGER, intValue, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addFloatConcreteDomainTypeToConcept(long, java.lang.String, float, java.lang.String, java.lang.String)
	 */
	@Override
	public void addFloatConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, final float floatValue,
			final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToConcept(conceptId, concreteDomainAttributeName, 
				DataType.DECIMAL, floatValue, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addDateConcreteDomainTypeToConcept(long, java.lang.String, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDateConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, final Date dateValue,
			final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToConcept(conceptId, concreteDomainAttributeName, 
				DataType.DATE, dateValue, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addStringDomainDateTypeToConcept(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addStringDomainDateTypeToConcept(final long conceptId, final String concreteDomainAttributeName, final String stringValue,
			final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToConcept(conceptId, concreteDomainAttributeName, 
				DataType.STRING, stringValue, characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addConcreteDomainDataTypeToRelationship(long, java.lang.String, com.b2international.snowowl.scripting.services.api.ConcreteDomainDataType, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void addConcreteDomainDataTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final DataType concreteDomainAttributeType, final Object value, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToComponent(relationshipId, concreteDomainAttributeName, 
				concreteDomainAttributeType, value, characteristicTypeId, taskId);

	}

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
		final SnomedConcreteDataTypeRefSet refSet = getRefSet(context.getTransaction(), identifierConceptId);

		final ComponentType componentType = ComponentType.RELATIONSHIP;

		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				componentType.getComponentIdentifierPair(Long.valueOf(relationship.getId())), 
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
		final SnomedConcreteDataTypeRefSet refSet = getRefSet(context.getTransaction(), identifierConceptId);
		
		final ComponentType componentType = ComponentType.RELATIONSHIP;
		
		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				componentType.getComponentIdentifierPair(Long.valueOf(relationship.getId())),
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

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addBooleanConcreteDomainTypeToRelationship(long, java.lang.String, boolean, java.lang.String, java.lang.String)
	 */
	@Override
	public void addBooleanConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final boolean booleanValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToRelationship(relationshipId, concreteDomainAttributeName, DataType.BOOLEAN, booleanValue,
				characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addIntegerConcreteDomainTypeToRelationship(long, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void addIntegerConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final int intValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToRelationship(relationshipId, concreteDomainAttributeName, DataType.INTEGER, intValue,
				characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addFloatConcreteDomainTypeToRelationship(long, java.lang.String, float, java.lang.String, java.lang.String)
	 */
	@Override
	public void addFloatConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final float floatValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToRelationship(relationshipId, concreteDomainAttributeName, DataType.DECIMAL, floatValue,
				characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addDateConcreteDomainTypeToRelationship(long, java.lang.String, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDateConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final Date dateValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToRelationship(relationshipId, concreteDomainAttributeName, DataType.DATE, dateValue, 
				characteristicTypeId, taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IAuthoringService#addStringDomainDateTypeToRelationship(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addStringDomainDateTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName,
			final String stringValue, final String characteristicTypeId, final String taskId) {
		
		addConcreteDomainDataTypeToRelationship(relationshipId, concreteDomainAttributeName, DataType.STRING, stringValue,
				characteristicTypeId, taskId);
	}

	/*
	 * creates a concrete domain data type and adds it to the associated SNOMED
	 * CT component identified by its unique component ID
	 */
	private void addConcreteDomainDataTypeToComponent(final long componentId, final String concreteDomainAttributeName,
			final DataType concreteDomainAttributeType, final Object value, 
			final String characteristicTypeId,
			final String taskId) {
		
		SnomedRefSetEditingContext context = null;
		
		try {
			context = SnomedRefSetEditingContext.createInstance(getOrCreateBranch(taskId));
			final String identifierConceptId = getIdentifierConceptId(concreteDomainAttributeType);
			final SnomedConcreteDataTypeRefSet refSet = getRefSet(context.getTransaction(), identifierConceptId);
			final ComponentType componentType = ComponentType.getComponentType(componentId);
			final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
					componentType.getComponentIdentifierPair(componentId), concreteDomainAttributeType, value, 
					characteristicTypeId,
					concreteDomainAttributeName,
					getModuleId(context), refSet), "Error while creating concrete domain for " + componentType + ": " + componentId
					+ "on task " + taskId + " with value " + value);
			componentType.getComponent(context.getTransaction(), componentId).getConcreteDomainRefSetMembers().add(member);
			context.commit("", new ConsoleProgressMonitor());
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while getting CDO root resource for SNOMED CT reference set editing context", e);
			throw new RuntimeException(e);
		} finally {
			if (null != context) {
				context.close();
			}
		}
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
		final SnomedConcreteDataTypeRefSet refSet = getRefSet(context.getTransaction(), identifierConceptId);

		final ComponentType componentType = ComponentType.CONCEPT;

		final SnomedConcreteDataTypeRefSetMember member = checkNotNull(context.createConcreteDataTypeRefSetMember(
				componentType.getComponentIdentifierPair(Long.valueOf(concept.getId())), concreteDomainAttributeType, value, 
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
		return checkNotNull(SnomedRefSetUtil.DATATYPE_TO_REFSET_MAP.get(dataType), "Error while getting identifier concept ID for concrete domain reference set. Type: " + dataType);
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

	/*returns with the task manager service. never null.*/
	@Nonnull
	private TaskManager getTaskManager() {
		return Preconditions.checkNotNull(ApplicationContext.getInstance().getService(TaskManager.class), "Task manager service was null.");
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

	/*
	 * returns with the concrete domain reference set identified by the
	 * unique identifier concept ID.
	 */
	@Nonnull
	private SnomedConcreteDataTypeRefSet getRefSet(final CDOView view, final String identifierConcepetId) {
		final SnomedRefSet refSet = getRefSetLookupService().getComponent(identifierConcepetId, checkView(view));
		checkNotNull(refSet, "SNOMED CT reference set was null. Identifier concept ID: " + identifierConcepetId);
		if (refSet instanceof SnomedConcreteDataTypeRefSet) {
			return (SnomedConcreteDataTypeRefSet) refSet;
		} else {
			throw new IllegalStateException("SNOMED CT reference set was not a concrete domain reference set but "
					+ refSet.getClass().getName());
		}
	}

	/*
	 * returns with a service for looking up SNOMED CT reference sets based on
	 * the identifier concept IDs
	 */
	private SnomedRefSetLookupService getRefSetLookupService() {
		return new SnomedRefSetLookupService();
	}

	/*
	 * returns with the CDO branch identified by the specified unique branch ID.
	 * if the branch does not exist, the branch will be created.NOTE: the branch
	 * ID is not equal with the CDO Branch ID. It is rather means e.g.: the
	 * bugzilla task ID.
	 */
	@Nonnull
	private IBranchPath getOrCreateBranch(final String taskId) {
		final IBranchPathMap taskBranchPathMap = ApplicationContext.getInstance().getService(ITaskStateManager.class).getTaskBranchPathMap(taskId);
		final IBranchPath snomedBranchPath = taskBranchPathMap.getBranchPath(getConnection().getUuid());
		return snomedBranchPath;
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
			public ComponentIdentifierPair<String> getComponentIdentifierPair(final long componentId) {
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
			public ComponentIdentifierPair<String> getComponentIdentifierPair(final long componentId) {
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

		/*
		 * returns with the component type specified by the unique ID of the
		 * SNOMED CT component.
		 */
		private static ComponentType getComponentType(final long componentId) {
			switch (SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(String.valueOf(componentId))) {
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
				return CONCEPT;
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				return RELATIONSHIP;
			default:
				throw new RuntimeException("SNOMED CT component type cannot be specified from the specified ID: " + componentId);
			}
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
		ComponentIdentifierPair<String> getComponentIdentifierPair(final long componentId);
	}

}