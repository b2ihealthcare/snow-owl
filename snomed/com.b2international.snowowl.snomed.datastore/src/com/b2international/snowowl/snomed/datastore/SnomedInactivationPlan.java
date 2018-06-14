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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * A DTO about the projected outcome of a inactivation operation for SNOMED&nbsp;CT components.
 * @see InactivationReason
 */
public class SnomedInactivationPlan {

	/**
	 * NILL implementation of the {@link SnomedInactivationPlan}. Does nothing.
	 */
	public static final SnomedInactivationPlan NULL_IMPL = new SnomedInactivationPlan() {
		@Override public void markForInactivation(final CDOObject... components) { /*does nothing*/ };
		@Override public void performInactivation(final InactivationReason reason, final String targetComponentId) {
			/*does nothing*/
		}
	};
	
	/**
	 * Returns {@code true} if the specified {@link SnomedInactivationPlan inactivation plan} instance is {@code null} 
	 * or equals with the {@link #NULL_IMPL} or the set of inactivated components is either empty or {@code null}. Otherwise returns with {@code false}. 
	 * @param plan the inactivation plan to check.
	 * @return {@code false} if the inactivation plan can be referenced. Otherwise returns with {@code true}.
	 */
	public static boolean isNull(final SnomedInactivationPlan plan) {
		return null == plan || NULL_IMPL.equals(plan) || CompareUtils.isEmpty(plan.getInactivatedComponents());
	}
	
	/**
	 * Enumeration representing the reason of the SNOMED&nbsp;CT concept inactivation.
	 * <p>The following reasons are available:
	 * <ul>
	 * <li>{@link #DUPLICATE <em>Duplicate</em>}</li>
	 * <li>{@link #OUTDATED <em>Outdated</em>}</li>
	 * <li>{@link #AMBIGUOUS <em>Ambiguous</em>}</li>
	 * <li>{@link #ERRONEOUS <em>Erroneous</em>}</li>
	 * <li>{@link #LIMITED <em>Limited</em>}</li>
	 * <li>{@link #MOVED_ELSEWEHERE <em>Moved elsewhere</em>}</li>
	 * <li>{@link #PENDING_MOVE <em>Pending move</em>}</li>
	 * <li>{@link #RETIRED <em>Retired</em>}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Note that "Pending move" is present for completeness, however the process requires the concept to stay active, and so can not be 
	 * handled by {@code SnomedInactivationPlan} correctly at this time.
	 * </p>
	 * @see SnomedInactivationPlan
	 */
	public static enum InactivationReason {

		/**
		 * Duplicate. <br>SNOMED&nbsp;CT reference set: SAME AS (ID: 900000000000527005)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: DUPLICATE (ID: 900000000000482003)
		 * @see InactivationReason
		 */
		DUPLICATE("Duplicate", Concepts.REFSET_SAME_AS_ASSOCIATION, Concepts.DUPLICATE), //SAME AS
		
		/**
		 * Outdated. <br>SNOMED&nbsp;CT reference set: REPLACED BY (ID: 900000000000526001)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: OUTDATED (ID: 900000000000483008)
		 * @see InactivationReason
		 */
		OUTDATED("Outdated", Concepts.REFSET_REPLACED_BY_ASSOCIATION, Concepts.OUTDATED), // REPLACED BY
		
		/**
		 * Ambiguous. <br>SNOMED&nbsp;CT reference set: POSSIBLY EQUIVALENT TO (ID: 900000000000523009)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: AMBIGUOUS (ID: 900000000000484002)
		 * @see InactivationReason
		 */
		AMBIGUOUS("Ambiguous", Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, Concepts.AMBIGUOUS), //POSSIBLY EQUIVALENT TO
		
		/**
		 * Erroneous. <br>SNOMED&nbsp;CT reference set: REPLACED BY (ID: 900000000000526001)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: ERRONEOUS (ID: 900000000000485001)
		 * @see InactivationReason
		 */
		ERRONEOUS("Erroneous", Concepts.REFSET_REPLACED_BY_ASSOCIATION, Concepts.ERRONEOUS), //REPLACED BY

		/**
		 * Limited. <br>SNOMED&nbsp;CT reference set: WAS A (ID: 900000000000528000)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: LIMITED (ID: 900000000000486000)
		 * @see InactivationReason
		 */
		LIMITED("Limited", Concepts.REFSET_WAS_A_ASSOCIATION, Concepts.LIMITED), // WAS A (may not be applicable universally)
		
		/**
		 * Moved elsewhere. <br>SNOMED&nbsp;CT reference set: MOVED TO (ID: 900000000000524003)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: MOVED_ELSEWHERE (ID: 900000000000487009)
		 * @see InactivationReason
		 */
		MOVED_ELSEWEHERE("Moved elsewhere", Concepts.REFSET_MOVED_TO_ASSOCIATION, Concepts.MOVED_ELSEWHERE),  //MOVED TO
		
		/**
		 * Pending move. <br>SNOMED&nbsp;CT reference set: MOVED TO (ID: 900000000000524003)
		 * <br>SNOMED&nbsp;CT concept of the inactivation reason: PENDING_MOVE (ID: 900000000000492006)
		 * @see InactivationReason 
		 */
		PENDING_MOVE("Pending move", Concepts.REFSET_MOVED_TO_ASSOCIATION, Concepts.PENDING_MOVE), // MOVED TO
		
		/**
		 * Retired ("inactive with no reason given for inactivation"). Neither a historical association reference set member 
		 * nor a component inactivation reference set member is required.
		 * @see InactivationReason
		 */
		RETIRED("Retired", "", "");
		
		private final String name;
		private final String associatedRefSetId;
		private final String inactivationReasonConceptId;
		
		private InactivationReason(final String name, final String associatedRefSetId, final String inactivationReasonConceptId) {
			this.name = name;
			this.associatedRefSetId = associatedRefSetId;
			this.inactivationReasonConceptId = inactivationReasonConceptId;
		}
		
		/**
		 * Returns with the human readable name of the inactivation reason.
		 * @return the human readable reason.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Returns with the associated SNOMED&nbsp;CT reference set concept identifier.
		 * <p><b>Note:</b> can be empty string if the inactivation reason is {@link #RETIRED retired}.
		 * @return the associated reference set concept identifier.
		 */
		public String getAssociatedRefSetId() {
			return associatedRefSetId;
		}
		
		/**
		 * Returns with the ID of the inactivation reason SNOMED&nbsp;CT concept.
		 * <p><b>Note:</b> can be empty string if the inactivation reason is {@link #RETIRED retired}.
		 * @return the ID of the inactivation reason.
		 */
		public String getInactivationReasonConceptId() {
			return inactivationReasonConceptId;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return getName();
		}
		
	}
	
	private final SnomedEditingContext context;
	private final Set<CDOObject> inactivatedComponents;
	private final String moduleId;
	
	/**
	 * Empty constructor for the {@link #NULL_IMPL null object}.
	 */
	private SnomedInactivationPlan() {
		this.context = null;
		inactivatedComponents = Collections.emptySet();
		moduleId = null;
	}
	
	/**
	 * Creates a new plan instance for the SNOMED&nbsp;CT inactivation process.
	 * @param context the editing context for the SNOMED&nbsp;CT terminology.
	 */
	public SnomedInactivationPlan(final SnomedEditingContext context) {
		this.context = Preconditions.checkNotNull(context, "SNOMED CT editing context argument cannot be null.");
		inactivatedComponents = Sets.newTreeSet(ComponentUtils2.CDO_OBJECT_COMPARATOR);
		moduleId = Preconditions.checkNotNull(this.context.getDefaultModuleConcept(), "SNOMED CT module concept cannot be null.").getId();
	}
	
	/**
	 * Returns with a copy of the SNOMED&nbsp;CT components marked for inactivation.
	 * @return the copy if of the components marked for inactivation.
	 */
	public Set<CDOObject> getInactivatedComponents() {
		return Collections.unmodifiableSet(inactivatedComponents);
	}
	
	/**
	 * Marks the specified SNOMED&nbsp;CT component for inactivation.
	 * @param components the SNOMED&nbsp;CT component. Can be {@code null}.
	 */
	public void markForInactivation(@Nullable final CDOObject... components) {

		if (null == components) {
			return;
		}
		
		for (final CDOObject component : components) {
			
			if (component instanceof SnomedRefSetMember) {
				
				if (!((SnomedRefSetMember) component).isActive()) { //skip inactive reference set members
					continue;
				}
				
				((SnomedRefSetMember) component).setActive(false);
				((SnomedRefSetMember) component).unsetEffectiveTime();
				
			} else if (component instanceof Component) {
				
				if (!((Component) component).isActive()) { //skip inactive SNOMED CT components
					continue;
				}
				
				if (!(component instanceof Description)) { //skip descriptions 
					//their status will not change but a 'non-current' description inactivation indicator reference set member will be created 
					((Component) component).setActive(false);
					((Component) component).unsetEffectiveTime();
				}
			}
			
			inactivatedComponents.add(component);
		}
	}

	/**
	 * Marks the specified storageKeys for inactivations.
	 * @param storageKeys
	 */
	public void markForInactivation(@Nullable Collection<Long> storageKeys) {
		if (storageKeys == null) return;
		markForInactivation(toArray(transform(storageKeys, new Function<Long, CDOObject>() {
			@Override
			public CDOObject apply(Long input) {
				return (CDOObject) context.lookupIfExists(input);
			}
		}), CDOObject.class));
	}
	
	/**
	 * Performs the inactivation process, but it does not commits changes to the backend. After this method call {@link #commitPlan(String, IProgressMonitor)} to actually save the plan's content.
	 * 
	 * @param reason inactivation reason for the process.
	 * @param targetComponentId the SNOMED&nbsp;CT concept ID which is the association target. Can be {@code null}. If {@code null} no historical association reference set will be created.
	 */
	public void performInactivation(final InactivationReason reason, @Nonnull final String targetComponentId) {
		for (final CDOObject component : inactivatedComponents) {

			if (component instanceof Concept) {

				final Concept concept = (Concept) component;

				// neither concept inactivation reference set member nor association reference set member have to be created
				if (isRetired(reason)) {
					continue;
				}

				// create historical association reference set member only and if only it is specified
				if (!StringUtils.isEmpty(targetComponentId)) {
					concept.getAssociationRefSetMembers().add(createAssociationMember(component, targetComponentId, reason));
				}

				concept.getInactivationIndicatorRefSetMembers().add(createConceptInactivationMember(component, reason));

			} else if (component instanceof Description) {

				// "concept non current" members for descriptions are always created regardless of reason
				final Description description = (Description) component;
				description.getInactivationIndicatorRefSetMembers().add(createDescriptionInactivationMember(component));
			}
		}
	}

	/**
	 * Commits the plan's actual state to the backend.
	 * 
	 * @param commitComment
	 *            the commit comment. Can be {@code null}.
	 * @param monitor
	 *            the progress monitor for the process. Can be {@code null}.
	 * @throws SnowowlServiceException
	 *             if error occurred while committing transaction.
	 */
	public void commitPlan(@Nullable final String commitComment, @Nullable IProgressMonitor monitor) throws SnowowlServiceException {
		if (context.isDirty()) {
			context.commit(null == commitComment ? "" : commitComment, null == monitor ? new NullProgressMonitor() : monitor);
		}
	}

	/*creates and returns with the description inactivation indicator reference set member*/
	private SnomedAttributeValueRefSetMember createDescriptionInactivationMember(final CDOObject component) {
		
		return context.getRefSetEditingContext().createAttributeValueRefSetMember(
				getReferencedComponentId(component), 
				Concepts.CONCEPT_NON_CURRENT, 
				moduleId,
				context.lookup(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR, SnomedRefSet.class));
	}

	/*creates and returns with the concept inactivation indicator reference set member*/
	private SnomedAttributeValueRefSetMember createConceptInactivationMember(final CDOObject component, final InactivationReason reason) {
		
		return context.getRefSetEditingContext().createAttributeValueRefSetMember(
				getReferencedComponentId(component), 
				reason.getInactivationReasonConceptId(), 
				moduleId,
				context.lookup(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR, SnomedRefSet.class));
	}

	/*returns true if the inactivation reason is retired*/
	private boolean isRetired(final InactivationReason reason) {
		return InactivationReason.RETIRED.equals(reason);
	}
	
	/*returns with the SNOMED CT historical association reference set associated with the current inactivation plan.*/
	private SnomedStructuralRefSet getHistoricalRefSet(final InactivationReason reason) {
		return context.lookup(reason.getAssociatedRefSetId(), SnomedStructuralRefSet.class);
	}

	/*creates the historical association reference set member based on the specified component*/
	private SnomedAssociationRefSetMember createAssociationMember(final CDOObject component, final String targetComponentId, final InactivationReason reason) {

		return context.getRefSetEditingContext().createAssociationRefSetMember(
				getReferencedComponentId(component), 
				targetComponentId, 
				moduleId, 
				getHistoricalRefSet(reason));
	}
	
	/*creates a SNOMED CT concept type of description type component identifier pair based on the specified argument.*/
	private String getReferencedComponentId(final CDOObject component) {
		
		if (component instanceof Concept) {
			return ((Concept) component).getId();
		} else if (component instanceof Description) {
			return ((Description) component).getId();	
		}
		
		throw new IllegalArgumentException("Component argument must be either a SNOMED CT concept or a SNOMED CT description. Was a " + component);
	}

}