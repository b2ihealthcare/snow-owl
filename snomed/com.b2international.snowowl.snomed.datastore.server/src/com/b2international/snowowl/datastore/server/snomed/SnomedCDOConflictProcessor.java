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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static java.lang.Boolean.parseBoolean;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.AlreadyReleasedConflictWrapper;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.ConflictingChange;
import com.b2international.snowowl.datastore.server.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Conflict processor for SNOMED&nbsp;CT ontology.
 *
 */
public class SnomedCDOConflictProcessor extends AbstractCDOConflictProcessor implements ICDOConflictProcessor {

	private static final Set<EClass> COMPONENT_CLASSES = ImmutableSet.of(
			SnomedPackage.Literals.CONCEPT,
			SnomedPackage.Literals.DESCRIPTION,
			SnomedPackage.Literals.RELATIONSHIP);

	@Override
	public ConflictWrapper checkConflictForNewObjects(final CDOChangeSetData targetChangeSet, final CDOIDAndVersion newInSource, final CDOView sourceView) {

		final ConflictWrapper conflictWrapper = super.checkConflictForNewObjects(targetChangeSet, newInSource, sourceView);

		if (null != conflictWrapper) {
			return conflictWrapper;
		}

		final Map<String, CDOID> newComponentIdsInTarget = Maps.newHashMap();

		for (final CDOIDAndVersion newInTarget : targetChangeSet.getNewObjects()) {
			final InternalCDORevision newRevisionInTarget = ClassUtils.checkAndCast(newInTarget, InternalCDORevision.class);

			if (!isComponentRevision(newRevisionInTarget)) {
				continue;
			}

			newComponentIdsInTarget.put(getComponentIdFromRevision(newRevisionInTarget), newInTarget.getID());
		}

		return checkDuplicateComponentIds(newComponentIdsInTarget, ClassUtils.checkAndCast(newInSource, InternalCDORevision.class), sourceView);
	}

	@Override
	public ConflictWrapper checkConflict(final Collection<CDOID> detachedIds, final CDOIDAndVersion newCdoIdAndVersion, final CDOView view) {

		CDOUtils.check(view);

		if (newCdoIdAndVersion instanceof InternalCDORevision) {

			final InternalCDORevision revision = (InternalCDORevision) newCdoIdAndVersion;
			final EClass eClass = revision.getEClass();

			if (SnomedPackage.eINSTANCE.getRelationship().equals(eClass)) {

				//source concept CDO ID
				CDOID cdoId = (CDOID) revision.getValue(SnomedPackage.eINSTANCE.getRelationship_Source());

				if (detachedIds.contains(cdoId)) {

					return creatConflictWrapper(cdoId, newCdoIdAndVersion);

				}

				//destination concept CDO ID
				cdoId = (CDOID) revision.getValue(SnomedPackage.eINSTANCE.getRelationship_Destination());

				if (detachedIds.contains(cdoId)) {

					return creatConflictWrapper(cdoId, newCdoIdAndVersion);

				}

				//type concept CDO ID
				cdoId = (CDOID) revision.getValue(SnomedPackage.eINSTANCE.getRelationship_Type());

				if (detachedIds.contains(cdoId)) {

					return creatConflictWrapper(cdoId, newCdoIdAndVersion);

				}

			} else if (SnomedPackage.eINSTANCE.getDescription().equals(eClass)) {

				//description type concept CDO ID
				final CDOID typeCdoId = (CDOID) revision.getValue(SnomedPackage.eINSTANCE.getDescription_Type());

				if (detachedIds.contains(typeCdoId)) {

					return creatConflictWrapper(typeCdoId, newCdoIdAndVersion);

				}

				//container concept CDO ID
				final CDOID containerCdoId = (CDOID) revision.getContainerID();

				if (detachedIds.contains(containerCdoId)) {

					return creatConflictWrapper(containerCdoId, newCdoIdAndVersion);

				}

			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {

				final String referencedComponentId = (String) revision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentId());

				CDOID cdoId = null;
				long storageKey = -1L;
				short type = -1;

				if (SnomedRefSetPackage.eINSTANCE.getSnomedQueryRefSetMember().equals(eClass)) {

					type = SnomedTerminologyComponentConstants.REFSET_NUMBER; //always referencing a simple type reference set

				} else {

					type = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(referencedComponentId);

				}

				if (-1 != type) {

					final IBranchPath branchPath = BranchPathUtils.createPath(view);

					switch (type) {

						case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:

							storageKey = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getStorageKey(branchPath, referencedComponentId);
							break;

						case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:

							storageKey = ApplicationContext.getInstance().getService(SnomedStatementBrowser.class).getStorageKey(branchPath, referencedComponentId);
							break;

						case SnomedTerminologyComponentConstants.REFSET_NUMBER:

							storageKey = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getStorageKey(branchPath, referencedComponentId);
							break;

						case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:

							storageKey = new SnomedDescriptionLookupService().getStorageKey(branchPath, referencedComponentId);
							break;

						default:

					}

				}

				//for e.g. mapping members referencing anything but SNOMED CT
				if (-1L == storageKey) {

					final SnomedRefSetMember member = (SnomedRefSetMember) view.getObject(newCdoIdAndVersion.getID());
					final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(member.getReferencedComponentType());
					final ILookupService<String, Object, Object> lookupService = CoreTerminologyBroker.getInstance().getLookupService(terminologyComponentId);
					final CDOObject component = (CDOObject) lookupService.getComponent(referencedComponentId, view);

					if (null != component) {

						cdoId = component.cdoID();

					}

				} else {

					cdoId = CDOIDUtil.createLong(storageKey);

				}

				if (detachedIds.contains(cdoId)) {

					return creatConflictWrapper(cdoId, newCdoIdAndVersion);

				}


			}


		} else {

			final CDOObject newObject = view.getObject(newCdoIdAndVersion.getID());

			if (newObject instanceof Relationship) {

				final Relationship relationship = (Relationship) newObject;

				//check source
				if (detachedIds.contains(relationship.getSource().cdoID())) {
					return creatConflictWrapper(relationship.getSource().cdoID(), newCdoIdAndVersion);
				}

				//check destination
				if (detachedIds.contains(relationship.getDestination().cdoID())) {
					return creatConflictWrapper(relationship.getDestination().cdoID(), newCdoIdAndVersion);
				}

				//check type
				if (detachedIds.contains(relationship.getType().cdoID())) {
					return creatConflictWrapper(relationship.getType().cdoID(), newCdoIdAndVersion);
				}

			} else if (newObject instanceof SnomedRefSetMember) {

				final SnomedRefSetMember member = (SnomedRefSetMember) newObject;
				final ILookupService<String, CDOObject, CDOView> lookupService = getLookupService(member);
				final long storageKey = lookupService.getStorageKey(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), member.getReferencedComponentId());

				CDOID cdoId = null;

				if (-1L == storageKey) {

					final CDOObject referencedComponent = lookupService.getComponent(member.getReferencedComponentId(), member.cdoView());
					cdoId = referencedComponent.cdoID();

				} else {

					cdoId = CDOIDUtil.createLong(storageKey);

				}

				if (detachedIds.contains(cdoId)) {
					return creatConflictWrapper(cdoId, newCdoIdAndVersion);
				}

			} else if (newObject instanceof Description) {

				final Description description = (Description) newObject;

				if (detachedIds.contains(description.getConcept().cdoID())) {
					return creatConflictWrapper(description.getConcept().cdoID(), newCdoIdAndVersion);
				}

			}
		}


		return null;
	}

	@Override
	public void detachConflictingObject(final CDOObject objectToRemove) {

		if (objectToRemove instanceof Relationship) {

			((Relationship) objectToRemove).setSource(null);
			((Relationship) objectToRemove).setDestination(null);

		} else if (objectToRemove instanceof SnomedRefSetMember) {

			final EObject container = objectToRemove.eContainer();
			final EStructuralFeature containingFeature = objectToRemove.eContainingFeature();

			((Collection<?>) container.eGet(containingFeature)).remove(objectToRemove);

		}

	}

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	@Override
	protected boolean shouldCheckReleasedFlag() {
		return true;
	}

	@Override
	protected ConflictWrapper checkReleasedForDetachedObjects(final InternalCDORevision changedTargetRevision) {

		final EClass eClass = changedTargetRevision.getEClass();
		final IBranchPath branchPath = createPath(changedTargetRevision.getBranch());

		boolean alreadyReleased = false;
		short terminologyComponentId = UNSPECIFIED_NUMBER_SHORT;
		String label = EMPTY_STRING;

		if (SnomedPackage.eINSTANCE.getComponent().isSuperTypeOf(eClass)) {
			alreadyReleased = parseBoolean(String.valueOf(changedTargetRevision.getValue(SnomedPackage.eINSTANCE.getComponent_Released())));
			final String id = String.valueOf(changedTargetRevision.getValue(SnomedPackage.eINSTANCE.getComponent_Id()));
			terminologyComponentId = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(id);
			final String terminologyComponentIdString = CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentId);
			final IComponentNameProvider nameProvider = CoreTerminologyBroker.getInstance().getNameProviderFactory(terminologyComponentIdString).getNameProvider();
			label = nameProvider.getComponentLabel(branchPath, id);
		} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
			alreadyReleased = parseBoolean(String.valueOf(changedTargetRevision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Released())));
			terminologyComponentId = SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
			String uuid = String.valueOf(changedTargetRevision.getValue(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Uuid()));
			label = getMemberLabel(branchPath, uuid);
		} else {
			return null;
		}

		return alreadyReleased ? new AlreadyReleasedConflictWrapper(changedTargetRevision.getID(), terminologyComponentId, label, branchPath) : null;

	}

	private String getMemberLabel(final IBranchPath branchPath, final String uuid) {
			final Pair<String, String> labelPair = getServiceForClass(ISnomedComponentService.class).getMemberLabel(branchPath, uuid);
			final StringBuffer sb = new StringBuffer();
			sb.append(labelPair.getA());
			if (!isEmpty(labelPair.getB())) {
				sb.append(" - ");
				sb.append(labelPair.getB());
			}
			return sb.toString();
	}

	private ConflictWrapper checkDuplicateComponentIds(final Map<String, CDOID> newComponentIdsInTarget, final InternalCDORevision newRevisionInSource, final CDOView sourceView) {

		if (!isComponentRevision(newRevisionInSource)) {
			return null;
		}

		final String newComponentIdInSource = getComponentIdFromRevision(newRevisionInSource);
		final CDOID conflictingNewInTarget = newComponentIdsInTarget.get(newComponentIdInSource);

		if (null != conflictingNewInTarget) {

			final ConflictingChange changeOnTarget = new ConflictingChange(ADDED, conflictingNewInTarget);
			final ConflictingChange changeOnSource = new ConflictingChange(ADDED, newRevisionInSource.getID());

			return new ConflictWrapper(changeOnTarget, changeOnSource);

		} else {
			return null;
		}
	}

	private boolean isComponentRevision(final InternalCDORevision revision) {
		return COMPONENT_CLASSES.contains(revision.getEClass());
	}

	private String getComponentIdFromRevision(final InternalCDORevision revision) {
		return (String) revision.getValue(SnomedPackage.Literals.COMPONENT__ID);
	}

	private ILookupService<String, CDOObject, CDOView> getLookupService(final SnomedRefSetMember member) {
		return CoreTerminologyBroker.getInstance().getLookupService(getTerminologyComponentType(member));
	}

	private String getTerminologyComponentType(final SnomedRefSetMember member) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(member.getReferencedComponentType());
	}

}