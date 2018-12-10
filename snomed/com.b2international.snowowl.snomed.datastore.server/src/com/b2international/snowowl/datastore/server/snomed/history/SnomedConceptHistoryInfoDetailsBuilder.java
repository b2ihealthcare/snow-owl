/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.history;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryInfoConstants.*;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.history.HistoryInfoDetails;
import com.b2international.snowowl.datastore.server.history.AbstractHistoryInfoDetailsBuilder;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Builder class for SNOMED CT specific detailed history information. 
 *
 */
public class SnomedConceptHistoryInfoDetailsBuilder extends AbstractHistoryInfoDetailsBuilder {

	private final LoadingCache<CDOObject, String> objectToLabelCache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<CDOObject, String>() {
		@Override
		public String load(final CDOObject object) throws Exception {
			if (object instanceof Concept) {
				return SnomedHistoryUtils.getLabelForConcept((Concept) object);
			} else if (object instanceof Description) {
				return SnomedHistoryUtils.getLabelForDescription((Description) object);
			} else if (object instanceof Relationship) {
				return SnomedHistoryUtils.getLabelForRelationship((Relationship) object);
			}
			throw new IllegalArgumentException("Unknown object type: " + object.getClass());
		}
	});
	
	private final LoadingCache<Pair<String, CDOView>, Concept> idToConceptCache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<Pair<String, CDOView>, Concept>() {
		@Override
		public Concept load(Pair<String, CDOView> pair) throws Exception {
			return SnomedHistoryUtils.getConcept(pair.getA(), pair.getB());
		}
	});
	
	private final LoadingCache<Pair<String, CDOView>, Description> idToDescriptionCache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<Pair<String, CDOView>, Description>() {
		@Override
		public Description load(Pair<String, CDOView> pair) throws Exception {
			return SnomedHistoryUtils.getDescription(pair.getA(), pair.getB());
		}
	});
	
	private final LoadingCache<Pair<String, CDOView>, Relationship> idToRelationshipCache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<Pair<String, CDOView>, Relationship>() {
		@Override
		public Relationship load(Pair<String, CDOView> pair) throws Exception {
			return SnomedHistoryUtils.getRelationship(pair.getA(), pair.getB());
		}
	});
	
	private static HashMap<String, String> map;

	private String getConceptLabel(Concept concept) {
		return objectToLabelCache.getUnchecked(concept);
	}
	
	private String getDescriptionLabel(Description description) {
		return objectToLabelCache.getUnchecked(description);
	}
	
	private String getRelationshipLabel(Relationship relationship) {
		return objectToLabelCache.getUnchecked(relationship);
	}
	
	private String getNewConceptLabel(Object value, CDOView view) {
		if (value instanceof CDOID) {
			CDOObject object = CDOUtils.getObjectIfExists(view, (CDOID) value);
			if (object != null && object instanceof Concept) {
				return getConceptLabel((Concept) object);
			}
		}
		return "";
	}
	
	private Concept getConcept(String id, CDOView view) {
		return idToConceptCache.getUnchecked(Pair.of(id, view));
	}
	
	private Description getDescription(String id, CDOView view) {
		return idToDescriptionCache.getUnchecked(Pair.of(id, view));
	}
	
	private Relationship getRelationship(String id, CDOView view) {
		return idToRelationshipCache.getUnchecked(Pair.of(id, view));
	}
	
	@Override
	protected Collection<? extends IHistoryInfoDetails> processNewObjects(final List<CDOIDAndVersion> newObjects, final CDOView beforeView, final CDOView currentView) {
		return processNewObjects(newObjects, beforeView, currentView, true);
	}
	
	@Override
	protected IHistoryInfoDetails generateInfoForNewObject(final CDOObject cdoObject, final CDOView beforeView, final CDOView currentView) {
		final String description = getDescription(cdoObject, beforeView, currentView, "New ", "added to ");
		if (isEmpty(description)) {
			return IHistoryInfoDetails.IGNORED_DETAILS;
		}
		return new HistoryInfoDetails(getComponent(cdoObject), description, ChangeKind.ADDED);
	}

	@Override
	protected IHistoryInfoDetails generateInfoForDetachedObject(final CDOObject cdoObject, final CDOView beforeView, final CDOView currentView) {
		final String description = getDescription(cdoObject, beforeView, currentView, "Detached ", "detached from ");
		if (isEmpty(description)) {
			return IHistoryInfoDetails.IGNORED_DETAILS;
		}
		return new HistoryInfoDetails(getComponent(cdoObject), description, ChangeKind.DELETED);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.history.AbstractHistoryInfoDetailsBuilder#generateInfoForChangedObject(org.eclipse.emf.cdo.CDOObject, org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta)
	 */
	@Override
	protected IHistoryInfoDetails generateInfoForChangedObject(final CDOObject cdoObject, final CDOView currentView, final CDOView beforeView, final CDOSetFeatureDelta featureDelta) {
		final String description = generateChangeDescription(cdoObject, currentView, beforeView, featureDelta);
		if (isEmpty(description)) {
			return IHistoryInfoDetails.IGNORED_DETAILS;
		}
		return new HistoryInfoDetails(getComponent(cdoObject), description, ChangeKind.UPDATED);
	}

	@Override
	public String getDescription(final CDOObject cdoObject, final CDOView beforeView, final CDOView currentView, final String change, final String refsetChange) {
		if (cdoObject instanceof Concept) {
			Concept concept = (Concept) cdoObject;
			return change + "concept: \"" + getConceptLabel(concept) + "\".";
		} else if (cdoObject instanceof Description) {
			Description description = (Description) cdoObject;
			return change + getConceptLabel(description.getType()) + ": \"" + description.getTerm() + "\".";
		} else if (cdoObject instanceof Relationship) {
			final Relationship relationship = (Relationship) cdoObject;
			if (null != relationship.getSource() && null != relationship.getType() && null != relationship.getDestination())
				return change + getConceptLabel(relationship.getCharacteristicType()).toLowerCase() + ": " + getRelationshipLabel(relationship) + ".";
		} else if (cdoObject instanceof SnomedConcreteDataTypeRefSetMember) {
			return change + "concrete domain element: \"" + getConcreteDataTypeItem((SnomedConcreteDataTypeRefSetMember) cdoObject) + "\".";
		} else if (cdoObject instanceof SnomedRefSetMember) {
			return getRefSetChangeDescription((SnomedRefSetMember) cdoObject, beforeView, currentView, refsetChange);
		} else if (cdoObject instanceof SnomedRefSet) {
			SnomedRefSet snomedRefset = (SnomedRefSet) cdoObject;
			return change + "reference set: \"" + getConceptLabel(getConcept(snomedRefset.getIdentifierId(), snomedRefset.cdoView())) + "\".";
		} 
		return null;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}

	public String generateChangeDescription(final CDOObject changedObject, final CDOView currentView, final CDOView beforeView, final CDOSetFeatureDelta featureDelta) {
	
		final EStructuralFeature feature = featureDelta.getFeature();
		final Object featureValue = featureDelta.getValue();
		final String featureName = feature.getName();
		final StringBuilder builder = new StringBuilder();
		
		if (changedObject instanceof Concept) {
			final Concept concept = (Concept) changedObject;
			if (SnomedHistoryInfoConstants.STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue), getConceptLabel(concept)).toString();
			} else if (SnomedHistoryInfoConstants.DEFINITION_STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), getConceptLabel(concept)).toString();
			} else if (SnomedHistoryInfoConstants.EXHAUSTIVE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue, "mutually disjoint", "non-disjoint"), getConceptLabel(concept)).toString();
			} else if (SnomedHistoryInfoConstants.EFFECTIVE_TIME_FEATURE_NAME.equals(featureName))  {
				return appendDescription(builder, getFeatureMapping().get(featureName), DateFormat.getDateInstance().format(featureValue), getConceptLabel(concept)).toString();
			} else if (MODULE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), getConceptLabel(concept)).toString();
			} else if (RELEASED_FEATURE_NAME.equals(featureName)) {
				return getPublishedChange(featureName, builder, getConceptLabel(concept), featureValue);
			}
		} else if (changedObject instanceof Description) {
			final Description description = (Description) changedObject;
			if (STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue), "\"" + description.getTerm() + "\"").toString();
			} else if (CASE_SIGNIFICANCE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), "\"" + description.getTerm() + "\"").toString();
			} else if (MODULE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView),  "\"" + description.getTerm() + "\"").toString();
			} else if (DESCRIPTION_TYPE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView),  "\"" + description.getTerm() + "\"").toString();
			} else if (EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), DateFormat.getDateInstance().format(featureValue), description.getTerm()).toString();
			} else if (DESCRIPTION_TERM_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName),	String.valueOf(featureValue), description.getTerm()).toString();
			} else if (RELEASED_FEATURE_NAME.equals(featureName)) {
				return getPublishedChange(featureName, builder, description.getTerm(), featureValue);
			}
		} else if (changedObject instanceof Relationship) {
			final Relationship relationship = (Relationship) changedObject;
			if (STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue), getRelationshipLabel(relationship)).toString();
			} else if (MODULE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), getRelationshipLabel(relationship)).toString();
			} else if (EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), DateFormat.getDateInstance().format(featureValue), getRelationshipLabel(relationship)).toString();
			} else if (GROUP_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), String.valueOf(featureValue), getRelationshipLabel(relationship)).toString();
			} else if (UNION_GROUP_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), String.valueOf(featureValue), getRelationshipLabel(relationship)).toString();
			} else if (CHARACTERISTIC_TYPE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), getRelationshipLabel(relationship)).toString();
			} else if (MODIFIER_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getNewConceptLabel(featureValue, currentView), getRelationshipLabel(relationship)).toString();
			} else if (RELEASED_FEATURE_NAME.equals(featureName)) {
				return getPublishedChange(featureName, builder, getRelationshipLabel(relationship), featureValue);
			} else if (RELATIONSHIP_TYPE_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, "relationship type" /*;( quite ugly but there is a collision with the description type feature name*/
						, getNewConceptLabel(featureValue, currentView), 
						getRelationshipLabel(relationship)).toString();
			}
		} else if (changedObject instanceof SnomedConcreteDataTypeRefSetMember) {
			if (STATUS_FEATURE_NAME.equals(featureName)) {
				final String status = getBooleanValue(featureValue);
				if ("active".equals(status)) {
					return getDescription(changedObject, beforeView, currentView, "New ", "added to ");
				} else if ("inactive".equals(status)) { //akitta: we agreed to indicate incativation as deletion. 
					return getDescription(changedObject, beforeView, currentView, "Detached ", "detached from ");
				} else {
					return "Unknown change on status feature for '" + changedObject + "'.";
				}
			}
		} else if (changedObject instanceof SnomedComplexMapRefSetMember) {
			if (CORRELATION_ID_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), String.valueOf(featureValue), 
						getReferencedComponentLabel((SnomedComplexMapRefSetMember) changedObject)).toString();
			} else if (STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue), 
						getReferencedComponentLabel((SnomedComplexMapRefSetMember) changedObject)).toString();
			} else if (EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						DateFormat.getDateInstance().format(featureValue),
						getReferencedComponentLabel((SnomedComplexMapRefSetMember) changedObject)).toString();
			} else if (MAP_GROUP_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), String.valueOf(featureValue), 
						getReferencedComponentLabel((SnomedComplexMapRefSetMember) changedObject)).toString();
			}
		} else if (changedObject instanceof SnomedRefSetMember) {
			if (DESCRIPTION_LENGTH_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), String.valueOf(featureValue), 
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (DESCRIPTION_FORMAT_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						getConceptLabel(getConcept(String.valueOf(featureValue), changedObject.cdoView())), 
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (STATUS_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), getBooleanValue(featureValue), 
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						DateFormat.getDateInstance().format(featureValue),
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (RELEASED_FEATURE_NAME.equals(featureName)) {
				return getPublishedChange(featureName, builder, getReferencedComponentLabel((SnomedRefSetMember) changedObject), featureValue);
			} else if (VALUE_ID_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						getConceptLabel(getConcept(String.valueOf(featureValue), changedObject.cdoView())), 
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (MODULE_ID_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						getConceptLabel(getConcept(String.valueOf(featureValue), changedObject.cdoView())), 
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (SOURCE_EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						DateFormat.getDateInstance().format(featureValue),
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();
			} else if (TARGET_EFFECTIVE_TIME_FEATURE_NAME.equals(featureName)) {
				return appendDescription(builder, getFeatureMapping().get(featureName), 
						DateFormat.getDateInstance().format(featureValue),
						getReferencedComponentLabel((SnomedRefSetMember) changedObject)).toString();

			} else if (ACCEPTABILITY_ID_FEATURE_NAME.equals(featureName)) {
				// IHTSDO is changing the acceptability of existing language members in case of PT changes on a concept. 
				// We have to show the change if it is changing to PREFERRED.
				if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(featureValue)) {
					return getRefSetChangeDescription((SnomedRefSetMember)changedObject, beforeView, currentView, "");
				} else {
					return null;
				}
			}
			
		} else if (changedObject instanceof SnomedMappingRefSet) {
			if (MAP_TARGET_TYPE_FEATURE_NAME.equals(featureName)) {
				final String label = getConceptLabel(getConcept(((SnomedMappingRefSet) changedObject).getIdentifierId(), changedObject.cdoView()));
				return appendDescription(builder, getFeatureMapping().get(featureName), getTerminologyComponentName(featureValue), label).toString();
			}
		} else if (changedObject instanceof SnomedRefSet) {
			if (RELEASED_FEATURE_NAME.equals(featureName)) {
				final String label = getConceptLabel(getConcept(((SnomedRefSet) changedObject).getIdentifierId(), changedObject.cdoView()));
				return getPublishedChange(featureName, builder, label, featureValue);
			}
		}
		return "Unexpected change on object: " + String.valueOf(changedObject) + " with the feature name of: '" + featureName + "'.";
	}

	private String getPublishedChange(final String featureName, final StringBuilder builder, final String label, final Object featureValue) {
		if ((boolean) featureValue) {
			return builder.append(label).append(" ").append(getFeatureMapping().get(featureName)).append(".").toString();
		} else {
			return null;
		}
	}

	private boolean isPtLanguageMember(final CDOObject member) {
		if (member instanceof SnomedLanguageRefSetMember) {
			final SnomedLanguageRefSetMember languageMember = (SnomedLanguageRefSetMember) member;
			if (isPreferred(languageMember)) {
				if (languageMember.eContainer() instanceof Description) {
					final Description description = (Description) languageMember.eContainer();
					if (canBePreferredDescription(description)) {
						return true;
					} 
				}
			}
		}
		return false;
	}

	private boolean canBePreferredDescription(final Description description) {
		final String typeId = description.getType().getId();
		return !Concepts.FULLY_SPECIFIED_NAME.equals(typeId) && !Concepts.TEXT_DEFINITION.equals(typeId);
	}
	
	private String getRefSetChangeDescription(final SnomedRefSetMember member, final CDOView beforeView, final CDOView currentView, final String change) {
		
		String referencedComponentLabel = getReferencedComponentLabel(member);
		
		if (isEmpty(referencedComponentLabel)) {
			referencedComponentLabel = member.getReferencedComponentId();
		}
		
		if (member instanceof SnomedSimpleMapRefSetMember) {
			return referencedComponentLabel + " " + change + getIdentifierConceptLabel(member) + "."; 
		} else if (member instanceof SnomedDescriptionTypeRefSetMember) {
			return referencedComponentLabel + " " + change + getIdentifierConceptLabel(member) + ".";
		} else if (member instanceof SnomedLanguageRefSetMember) {
			
			if (isPtLanguageMember(member)) {
				
				//ignore deletion
				if ("detached from ".equals(change)) {
					return null;
				}
				
				final SnomedLanguageRefSetMember languageMember = (SnomedLanguageRefSetMember) member;
				final Description description = (Description) languageMember.eContainer();
				final Concept concept = description.getConcept();
				final String refSetId = languageMember.getRefSetIdentifierId();
				final CDOObject beforeConcept = CDOUtils.getObjectIfExists(beforeView, concept.cdoID());
				final String previousPt = tryFindPreviousPtForLanguage(beforeConcept, refSetId);
				final String languageRefSetPt = getConceptLabel(getConcept(refSetId, member.cdoView()));

				if (null == previousPt) {
					return "New " + languageRefSetPt + " preferred term \"" + description.getTerm() + "\".";
				} else {
					return languageRefSetPt + " preferred term changed to \"" + description.getTerm() + "\" from \"" + previousPt + "\".";
				}
			}
			
			//intentionally null. we will ignore everything but the PT language changes
			return null;
		} else if (member instanceof SnomedAttributeValueRefSetMember) {	
			return referencedComponentLabel + " " + change + getIdentifierConceptLabel(member) + ".";
		} else if (member instanceof SnomedConcreteDataTypeRefSetMember) {
			throw new UnsupportedOperationException("Concrete domain members are not supported"); //XXX
		} else {
			return referencedComponentLabel + " " + change + getIdentifierConceptLabel(member) + ".";
		}
	}
	
	private String tryFindPreviousPtForLanguage(final CDOObject concept, final String refSetId) {

		if (concept instanceof Concept) {
			for (final Description description : ((Concept) concept).getDescriptions()) {
				if (description.isActive()) {
					if (canBePreferredDescription(description)) {
						for (final SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {
							if (refSetId.equals(member.getRefSetIdentifierId()) && member.isActive() && isPreferred(member)) {
								return description.getTerm();
							}
						}
					} 
				}
			}
		}
		
		return null;
	}

	private boolean isPreferred(final SnomedLanguageRefSetMember member) {
		return Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId());
	}

	private String getConcreteDataTypeItem(final SnomedConcreteDataTypeRefSetMember cdtMember) {
		return new StringBuilder()
			.append(getConceptLabel(getConcept(cdtMember.getTypeId(), cdtMember.cdoView())))
			.append(" ")
			.append(String.valueOf(cdtMember.getSerializedValue()))
			.toString();
	}
	
	private String getComponent(final CDOObject cdoObject) {
		if (isPtLanguageMember(cdoObject)) { //act as a concept change if the PT changed
			return CoreTerminologyBroker.getInstance().getComponentInformation(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).getName();
		}
		return CoreTerminologyBroker.getInstance().getComponentInformation(cdoObject).getName();
	}
	
	private String getTerminologyComponentName(final Object featureValue) {
		final int value = Integer.parseInt(String.valueOf(featureValue));
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER == value) 
			return "Unspecified";
		
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(value);
		return CoreTerminologyBroker.getInstance().getComponentInformation(terminologyComponentId).getName();
	}

	private String getBooleanValue(final Object featureValue) {
		return getBooleanValue(featureValue, "active", "inactive");
	}

	private String getBooleanValue(final Object featureValue, final String trueMessage, final String falseMessage) {
		return Boolean.valueOf(featureValue.toString()) ? trueMessage : falseMessage;
	}
	
	private StringBuilder appendDescription(final StringBuilder builder, final String attributeName, final String newValue, final String changedOn) {
		builder.append("Attribute '");
		builder.append(attributeName);
		builder.append("' changed to ");
		builder.append(newValue);
		builder.append(" on ");
		builder.append(changedOn);
		builder.append(".");
		return builder;
	}
	
	private Map<String, String> getFeatureMapping() {
		if (null == map) {
			//can happen when this info details builder used from task compare editor
			//and multiple thread accessing this static map
			/*
			 * Example:
			 * I created two subtasks, classified, and on
			 * Task 1: Set Intravenous Infusion Liquid to primitive
			 * Task 2: Set Intravenous Infusion Solution to primitive

			 * Marked tasks as FIXED.
			 * The compare tasks.

			 * But the first one says "Attribute 'null' changed to Primitive...
			 * (it should be 'definition status' rather than null.)
			 * */
			synchronized (SnomedConceptHistoryInfoDetailsBuilder.class) {
				if (null == map) {
					map = new HashMap<String, String>();
					map.put(CASE_SIGNIFICANCE_FEATURE_NAME, "case significance");
					map.put(STATUS_FEATURE_NAME, "status");
					map.put(VALUE_FEATURE_NAME, "has strength");
					map.put(OPERATOR_TYPE_FEATURE_NAME, "operator type");
					map.put(UNIT_TYPE_FEATURE_NAME, "unit type");
			 		map.put(DEFINITION_STATUS_FEATURE_NAME, "definition status");
					map.put(EFFECTIVE_TIME_FEATURE_NAME, "effective time");
					map.put(EXHAUSTIVE_FEATURE_NAME, "subclass definitions");
					map.put(MODULE_FEATURE_NAME, "module");
//					map.put(RELATIONSHIP_TYPE_FEATURE_NAME, "relationship type"); //XXX intentionally removed due to description type feature collision
					map.put(MODULE_ID_FEATURE_NAME, "module");
					map.put(MAP_TARGET_TYPE_FEATURE_NAME, "map target");
					map.put(DESCRIPTION_TYPE_FEATURE_NAME, "description type");
					map.put(DESCRIPTION_TERM_FEATURE_NAME, "description term");
					map.put(GROUP_FEATURE_NAME, "relationship group");
					map.put(UNION_GROUP_FEATURE_NAME, "relationship union group");
					map.put(CHARACTERISTIC_TYPE_FEATURE_NAME, "characteristic type");
					map.put(MODIFIER_FEATURE_NAME, "modifier");
					map.put(CORRELATION_ID_FEATURE_NAME, "correlation identifier");
					map.put(MAP_GROUP_FEATURE_NAME, "map group");
					map.put(RELEASED_FEATURE_NAME, "published");
					map.put(DESCRIPTION_LENGTH_FEATURE_NAME, "description length");
					map.put(DESCRIPTION_FORMAT_FEATURE_NAME, "description format");
					map.put(VALUE_ID_FEATURE_NAME, "value");
					map.put(SOURCE_EFFECTIVE_TIME_FEATURE_NAME, "source effective time");
					map.put(TARGET_EFFECTIVE_TIME_FEATURE_NAME, "target effective time");
				}
			}
		}
		return Collections.unmodifiableMap(map);
	}
	
	private String getReferencedComponentLabel(final SnomedRefSetMember member) {
		switch (member.getReferencedComponentType()) {
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: //$FALL-THROUGH$
			case SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER:
				return getConceptLabel(getConcept(member.getReferencedComponentId(), member.cdoView()));
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
				return getDescriptionLabel(getDescription(member.getReferencedComponentId(), member.cdoView()));
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				return getRelationshipLabel(getRelationship(member.getReferencedComponentId(), member.cdoView()));
			default:
				throw new IllegalArgumentException("Unexpected or unknown terminology component type: " + member.getReferencedComponentType());
		}
	}

	private String getIdentifierConceptLabel(final SnomedRefSetMember member) {
		final String refSetIdentifierId = member.getRefSetIdentifierId();
		final String label = getConceptLabel(getConcept(refSetIdentifierId, member.cdoView()));
		if (refSetIdentifierId.equals(label)) {
			return "deleted reference set " + label;
		}
		return label;
	}
}
