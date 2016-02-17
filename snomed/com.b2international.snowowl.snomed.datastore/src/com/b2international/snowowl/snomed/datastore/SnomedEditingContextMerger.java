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
package com.b2international.snowowl.snomed.datastore;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.AbstractCDOEditingContextMerger;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.exception.MergeFailedException;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 */
public class SnomedEditingContextMerger extends AbstractCDOEditingContextMerger<SnomedEditingContext> {

	/**
	 * Private logger instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedEditingContextMerger.class);

	//supplier for the language type reference set identifier concept ID
	//its content will be initialized on the very first access
	private final Supplier<String> languageRefSetIdSupplier = Suppliers.memoize(new Supplier<String>() {
		@Override public String get() {
			return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
		}
	});

	/**Stores reference set CDO objects identified by their reference set identifier concept IDs.*/
	private final Map<String, SnomedRefSet> refSetCache = Maps.newHashMap();

	/**Stores concept CDO objects identified by their concept IDs.*/
	private final Map<String, Concept> conceptCache = Maps.newHashMap();

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOEditingContextMerger#mergeEditingContext(com.b2international.snowowl.datastore.CDOEditingContext, com.b2international.snowowl.datastore.CDOEditingContext)
	 */
	@Override
	public void mergeEditingContext(final SnomedEditingContext dirtyEditingContext, final SnomedEditingContext newEditingContext) throws MergeFailedException {
		//TODO restore state check after development cycle.
//		checkState(!newEditingContext.isDirty(), "The underlying audit CDO view was dirty for the new editing context.");

		try {

			//process new object from the transaction
			Set<CDOObject> newObjects = Sets.newHashSet(ComponentUtils2.getNewObjects(dirtyEditingContext.getTransaction(), CDOObject.class));
			for (final CDOObject newObject : newObjects) {

				if (!(hasDirectCDOResource(newObject))) {
					continue; //we will care about them later
				}

				if (SnomedPackage.eINSTANCE.getConcept().equals(newObject.eClass())) {

					//detach the object from the old transaction first.
					dirtyEditingContext.getContents().remove(newObject);

					//then add the new object to the brand new transaction
					newEditingContext.add(newObject);

					//finally visit all the references of the objects and update and reload all CDO object references with permanent CDO ID from the new transaction
					visitReferences(newObject, newEditingContext.getTransaction());

				} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().equals(newObject.eClass())) {

					//detach the object from the old transaction first.
					dirtyEditingContext.getRefSetEditingContext().getContents().remove(newObject);

					//then add the new object to the brand new transaction
					newEditingContext.getRefSetEditingContext().add(newObject);

					//finally visit all the references of the objects and update and reload all CDO object references with permanent CDO ID from the new transaction
					visitReferences(newObject, newEditingContext.getTransaction());

				}

			}

			//this point all CDO root resource related attache changes has been removed from the dirty context
			//we go through all the new changes and again and apply the changes on the new editing context
			//probably we have to add object to their container. XXX if no we have to log a warning.

			// Grab Descriptions and Relationships first to avoid looking for eg. a new Description on the new
			// editing context
			newObjects = ImmutableSet.<CDOObject>builder()
					.addAll(ComponentUtils2.getNewObjects(dirtyEditingContext.getTransaction(), Description.class))
					.addAll(ComponentUtils2.getNewObjects(dirtyEditingContext.getTransaction(), Relationship.class))
					.addAll(ComponentUtils2.getNewObjects(dirtyEditingContext.getTransaction(), CDOObject.class))
					.build();

			newObjectProcessing: for (final CDOObject newObject : newObjects) {

				if (newObject instanceof Relationship) {

					final Relationship relationship = (Relationship) newObject;
					relationship.setSource((Concept) getObjectFromTransaction(relationship.getSource().cdoID(), newEditingContext));
					relationship.setDestination((Concept) getObjectFromTransaction(relationship.getDestination().cdoID(), newEditingContext));

				} else if (newObject instanceof Description) {

					final Description description = (Description) newObject;
					description.setConcept((Concept) getObjectFromTransaction(description.getConcept().cdoID(), newEditingContext));

				} else if (newObject instanceof SnomedRefSetMember) {

					final CDOObject container = CDOUtil.getCDOObject(newObject.eContainer());
					final CDOObject reloadedContainer = getObjectFromTransaction(container.cdoID(), newEditingContext);
					final SnomedRefSetMember newMember = (SnomedRefSetMember) newObject;

					final Object containingValue = reloadedContainer.eGet(newObject.eContainingFeature());
					@SuppressWarnings("unchecked") final EList<SnomedRefSetMember> containingList = (EList<SnomedRefSetMember>) containingValue;
					containingList.add(newMember);

					newMember.setRefSet(getRefSetById(newMember.getRefSetIdentifierId(), newEditingContext));

					//language reference set members needs additional processing
					//if there is a new language type reference set member representing a preferred term
					//we have to either deactivate the other one or delete it
					//this could happen when some changed the preferred term of a concept like we did
					if (newMember instanceof SnomedLanguageRefSetMember) {

						if (!newMember.isActive()) {

							continue; //ignore inactive members

						}

						//we only care about preferred acceptability
						if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(((SnomedLanguageRefSetMember) newMember).getAcceptabilityId())) {

							//we have to eliminate (retire/delete) all other preferred language members except for FSN ones

							if (newMember.eContainer() instanceof Description) {

								final Description description = (Description) newMember.eContainer();
								final Description reloadedDescription = (Description) getObjectFromTransaction(description.cdoID(), newEditingContext);

								if (Concepts.FULLY_SPECIFIED_NAME.equals(reloadedDescription.getType().getId())) {

									continue; //ignore FSNs

								}

								final Concept concept = reloadedDescription.getConcept();

								if (CDOState.NEW.equals(concept.cdoState())) {

									continue; //concept is a new one, preferred term could not change from outside (from another user)

								}

								for (final Description otherDescriptions : concept.getDescriptions()) {

									if (otherDescriptions.cdoID().equals(description.cdoID())) {

										continue; //this

									}

									if (!otherDescriptions.isActive()) {

										continue; //ignore inactive descriptions.

									}

									if (Concepts.FULLY_SPECIFIED_NAME.equals(otherDescriptions.getType().getId())) {

										continue; //ignore FSNs

									}

									for (final SnomedLanguageRefSetMember otherLanguageMember : otherDescriptions.getLanguageRefSetMembers()) {

										if (!otherLanguageMember.isActive()) {

											continue; //skip retired members

										}

										if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE.equals(otherLanguageMember.getAcceptabilityId())) {

											continue; //ignore acceptable language members

										}

										if (languageRefSetIdSupplier.get().equals(otherLanguageMember.getRefSetIdentifierId())) {

											if (otherLanguageMember.isReleased()) {

												//retire
												otherLanguageMember.unsetEffectiveTime();
												otherLanguageMember.setActive(false);


											} else {

												//delete
												EcoreUtil.remove(otherLanguageMember);

											}

											//create an acceptable one add it to the description as well.
											final SnomedLanguageRefSetMember replacementMember = newEditingContext.getRefSetEditingContext().createLanguageRefSetMember(
													SnomedRefSetEditingContext.createDescriptionTypePair(otherDescriptions.getId()),
													SnomedRefSetEditingContext.createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE),
													otherLanguageMember.getModuleId(),
													(SnomedStructuralRefSet) getObjectFromTransaction(otherLanguageMember.getRefSet().cdoID(), newEditingContext));
											otherDescriptions.getLanguageRefSetMembers().add(replacementMember);

											//as we processed the other language member for PT we can process another new object if an
											break newObjectProcessing;

										}



									}


								}


							}


						}

					}

				}

				//XXX what about query type members? probably they'll be handled in the final case
			}

			//process detached objects
			final Set<CDOObject> detachedOnDirtyTransaction = Sets.newHashSet(ComponentUtils2.getDetachedObjects(dirtyEditingContext.getTransaction(), CDOObject.class));

			//here we just detach the object that direct CDO resource is a CDO resource. it will automatically detach all the CDO object references
			//then we collect the detached objects from the new transaction and compare with the detached ones from the dirty transaction
			//finally we get the intersection of the detached objects and remove them from their container one by one
			for (final CDOObject detachedObject : detachedOnDirtyTransaction) {

				if (hasDirectCDOResource(detachedObject)) { //we only care about objects with direct CDO resource container
					//as the concept or reference set has been detached from the containing resource we lookup the component by its ID from the
					//new transaction and remove it again

					//reference set removal could be different but as referenced concept always deleted
					//when reference set is removed it is handled automatically
					if (SnomedPackage.eINSTANCE.getConcept().equals(detachedObject.eClass())) {

						final String conceptId = CDOUtils.getAttribute(detachedObject, SnomedPackage.eINSTANCE.getComponent_Id(), String.class);
						Preconditions.checkNotNull(conceptId, "SNOMED CT concept ID was null for " + detachedObject);

						//XXX maybe we should optimize concept removal. what could happen if huge number of concept is removed as a subtree of the ontology
						final Concept conceptToDetache = getConceptById(conceptId, newEditingContext);

						//remove concept via editing context and deletion plan. that is bit more optimized.
						newEditingContext.delete(conceptToDetache);

					}

				//check for reference set member removal
				} else {

					//not a reference set member, will be processed later
					if (!(detachedObject instanceof SnomedRefSetMember)) {
						continue;
					}

					final CDOObject containerRefSet = CDOUtils.getAttribute(detachedObject, SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_RefSet(), CDOObject.class);

					//reference set member has been detached from a regular reference set
					if (containerRefSet instanceof SnomedRegularRefSet) {

						//if reference set not deleted
						if (!CDOState.TRANSIENT.equals(containerRefSet.cdoState())) {

							//load reference set in the new transaction and remove member from the reference set
							final List<SnomedRefSetMember> members = ((SnomedRegularRefSet) getObjectFromTransaction(containerRefSet.cdoID(), newEditingContext)).getMembers();

							//detached member has to be reloaded from the new context otherwise deletion won't happen
							//but as detached member does not have CDO ID anymore, we check existence in index first

							//if the component does not exist anymore we do not have to do anything
							//otherwise we get it's storage key
							final String uuid = CDOUtils.getAttribute(detachedObject, SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Uuid(), String.class);
							final long storageKey = new SnomedRefSetMemberLookupService().getStorageKey(BranchPathUtils.createActivePath(SnomedRefSetPackage.eINSTANCE), uuid);

							//if component exists but we deleted it
							if (-1L != storageKey) {
								//apply deletion again to merge changes
								members.remove(getObjectFromTransaction(CDOIDUtil.createLong(storageKey), newEditingContext));

							}

						}

					}

				}

			}

			//now we get the detached object in the new transaction
			final Set<CDOObject> detachedOnNewTransaction = Sets.newHashSet(ComponentUtils2.getDetachedObjects(newEditingContext.getTransaction(), CDOObject.class));

			//this point we cannot rely on the CDO IDs as all the objects are detached, we are rather building component identifier pairs
			//so we get it somehow
			final Map<ComponentIdentifierPair<String>, CDOObject> detachedObjectsOnNewContext = Maps.uniqueIndex(detachedOnNewTransaction, new Function<CDOObject, ComponentIdentifierPair<String>>() {
				@Override public ComponentIdentifierPair<String> apply(final CDOObject cdoObject) {
					return createComponentIdentifierPair(cdoObject);
				}
			});

			//this point we have to go all the detached objects from the "old" transaction and check if they have been processed for the new one
			for (final Iterator<CDOObject> itr = detachedOnDirtyTransaction.iterator(); itr.hasNext();) {
				final ComponentIdentifierPair<String> componentIdentifierPair = createComponentIdentifierPair(itr.next());
				if (!detachedObjectsOnNewContext.containsKey(componentIdentifierPair)) { //changes are not in the new transaction -> not processed yet
					//we reload the unprocessed object and remove it
					final CDOObject object = loadObject(componentIdentifierPair, newEditingContext);
					if (object instanceof Description) {
						((Description) object).getConcept().getDescriptions().remove(object);
					} else if (object instanceof Relationship) {
						((Relationship) object).setSource(null);
						((Relationship) object).setDestination(null);
					} else if (object instanceof SnomedRefSet) {
						//TODO implement me
					} else if (object instanceof SnomedLanguageRefSetMember) {
						//nothing to do as we removed (or will be removed) the reference set member when deleting the description
					} else if (object instanceof SnomedConcreteDataTypeRefSetMember) {
						//same as for language reference set members. but this time the container is the relationship
					} else if (object instanceof SnomedAssociationRefSetMember) {
						//still same as language reference set members but related to relationship
					}
				}
			}

			//process 'dirty' changes on feature and reference level
			for (final CDORevisionKey revisionKey : getChangeSetData(dirtyEditingContext).getChangedObjects()) {
				if (revisionKey instanceof InternalCDORevisionDelta) {
					final CDOObject changedObject = getObjectFromTransaction(revisionKey.getID(), newEditingContext);
					for (final Entry<EStructuralFeature, CDOFeatureDelta> entry : ((InternalCDORevisionDelta) revisionKey).getFeatureDeltaMap().entrySet()) {
						if (entry.getKey() instanceof EAttribute) {
							final EAttribute feature = (EAttribute) entry.getKey();
							if (entry.getValue() instanceof CDOSetFeatureDelta) {
								final CDOSetFeatureDelta featureDelta = (CDOSetFeatureDelta) entry.getValue();
								changedObject.eSet(feature, featureDelta.getValue());
							} else if (entry.getValue() instanceof CDOUnsetFeatureDelta) {
								changedObject.eUnset(feature);
							} else {
								LOGGER.warn("Unprocessed attribute changes. Revisions: " + revisionKey + "\nFeature with delta: " + entry + "\n");
							}
						} else if (entry.getKey() instanceof EReference) {
							final EReference feature = (EReference) entry.getKey();
							if (entry.getValue() instanceof CDOSetFeatureDelta) {
								final CDOSetFeatureDelta featureDelta = (CDOSetFeatureDelta) entry.getValue();
								final Object newValue = featureDelta.getValue();
								if (newValue instanceof CDOObject) {
									final CDOObject newCdoObjectValue = getObjectFromTransaction(((CDOObject) newValue).cdoID(), newEditingContext);
									changedObject.eSet(feature, newCdoObjectValue);
								} else if (newValue instanceof CDOID) {
									final CDOObject newCdoObjectValue = getObjectFromTransaction(((CDOID) newValue), newEditingContext);
									changedObject.eSet(feature, newCdoObjectValue);
								} else {
									if (null == newValue && featureDelta.getOldValue() instanceof SnomedStructuralRefSet) {
										//ignore as we merged the preferred language member
									} else {
										LOGGER.warn("Unknown value type: " + newValue);
									}
								}
							} else {
								LOGGER.warn("Unprocessed reference changes. Revisions: " + revisionKey + "\nFeature with delta: " + entry + "\n");
							}
						}
					}
				}
			}

		} catch (final ObjectNotFoundException e) {
			handleObjectNotFoundException(newEditingContext, e);
		} catch (final Exception e) {
			throw new MergeFailedException(e);
		} finally {

			if (null != dirtyEditingContext) {
				dirtyEditingContext.close();
			}
		}

	}

	/**
	 * Returns {@code true} if the specified CDO object is either a {@link Concept SNOMED&nbsp;CT concept} or a {@link SnomedRefSet SNOMED&nbsp;CT reference set}.
	 * Otherwise it returns {@code false}.
	 */
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.AbstractCDOEditingContextMerger#hasDirectCDOResource(org.eclipse.emf.cdo.CDOObject)
	 */
	@Override
	protected boolean hasDirectCDOResource(final CDOObject object) {
		return object instanceof Concept || object instanceof SnomedRefSet;
	}

	/*loads a SNOMED CT component (concept, reference set, relationship, description or reference set member (maybe etc.) specified by the component identifier par on the given transaction)*/
	private CDOObject loadObject(final ComponentIdentifierPair<String> componentIdentifierPair, final SnomedEditingContext newEditingContext) {
		final CDOTransaction view = newEditingContext.getTransaction();
		final String componentId = componentIdentifierPair.getComponentId();
		final String terminologyComponentId = componentIdentifierPair.getTerminologyComponentId();
		return (CDOObject) CoreTerminologyBroker.getInstance().getLookupService(terminologyComponentId).getComponent(componentId, view);
	}

	/*returns with the SNOMED CT concept based on its unique ID looked up in the specified editing context instance*/
	private Concept getConceptById(final String conceptId, final SnomedEditingContext context) {

		Concept concept = conceptCache.get(conceptId);

		if (null == concept) {

			concept = new SnomedConceptLookupService().getComponent(conceptId, context.getTransaction());
			conceptCache.put(conceptId, concept);

		}

		return concept;
	}

	/*returns with the SNOMED CT reference set based on its unique ID looked up in the specified editing context instance*/
	private SnomedRefSet getRefSetById(final String refSetId, final SnomedEditingContext context) {

		SnomedRefSet refSet = refSetCache.get(refSetId);

		if (null == refSet) {

			refSet = new SnomedRefSetLookupService().getComponent(refSetId, context.getTransaction());
			refSetCache.put(refSetId, refSet);

		}

		return refSet;
	}

	/*creates a terminology component ID based on the specified CDO object.*/
	private ComponentIdentifierPair<String> createComponentIdentifierPair(final CDOObject object) {
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(object);
		if (object instanceof Component) {
			final String componentId = CDOUtils.getAttribute(object, SnomedPackage.eINSTANCE.getComponent_Id(), String.class);
			return ComponentIdentifierPair.<String>create(terminologyComponentId, componentId);
		} else if (object instanceof SnomedRefSet) {
			final String componentId = CDOUtils.getAttribute(object, SnomedRefSetPackage.eINSTANCE.getSnomedRefSet_IdentifierId(), String.class);
			return ComponentIdentifierPair.<String>create(terminologyComponentId, componentId);
		} else if (object instanceof SnomedRefSetMember) {
			final String componentId = CDOUtils.getAttribute(object, SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Uuid(), String.class);
			return ComponentIdentifierPair.<String>create(terminologyComponentId, componentId);
		} else {
			throw new IllegalArgumentException("Unregistered class as terminology component: " + object.getClass());
		}
	}

}