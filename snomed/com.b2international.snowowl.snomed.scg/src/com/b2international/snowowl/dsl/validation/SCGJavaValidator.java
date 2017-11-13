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
package com.b2international.snowowl.dsl.validation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.ScgPackage;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
 
/**
 * Java validator to register custom validation rules to be checked.
 * 
 *
 */
public class SCGJavaValidator extends AbstractSCGJavaValidator {
	
	public static final String INVALID_CONCEPT_ID_LENGTH = "invalidConceptId";
	public static final String NON_EXISTING_CONCEPT_ID = "nonexistentConcept";
	public static final String NON_MATCHING_TERM = "nomMatchingTerm";
	public static final String INACTIVE_CONCEPT = "inactiveConcept";
	
	private final Provider<IEventBus> bus;

	@Inject
	public SCGJavaValidator(Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	protected boolean isResponsible(Map<Object, Object> context, EObject eObject) {
		// context must have the associated activeBranch key, otherwise we cannot validate the target
		return super.isResponsible(context, eObject) && context.containsKey("activeBranch");
	}
	
	/**
	 * Check if the concept id length is between 6 and 18.
	 * @param concept
	 */
	@Check(CheckType.NORMAL)
	public void checkSnomedConceptId(Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		if (concept.getId().length() < 6 || concept.getId().length() > 18) {
			error("Concept ID length should be between 6 and 18 characters", ScgPackage.eINSTANCE.getConcept_Id(), INACTIVE_CONCEPT);
		}
	}
	
	/**
	 * Check if the concept is in the database based on the given concept id
	 * 
	 * @param concept
	 */
	@Check(CheckType.NORMAL)
	public void checkSnomedConceptValidity(Concept concept) {
		if (concept.getId() == null)
			return;
		
		try {
			SnomedConcept resolvedConcept = getConcept(concept.getId());

			// Concept id is not valid if the concept id length is less then 6 or longer then 18 -> should't be existed at all -> don't show 2 error messages
			if (concept.getId().length() < 6 || concept.getId().length() > 18) {
				return;
			}

			if (resolvedConcept == null) {
				error("Concept ID does not exist in the database", ScgPackage.eINSTANCE.getConcept_Id(), NON_EXISTING_CONCEPT_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Check(CheckType.NORMAL)
//	public void checkMRCMValidity(Expression expression) {
//		if (expression == null || expression.eAllContents().hasNext() == false)
//			return;
//		
//		SCGExpressionExtractor extractor = new SCGExpressionExtractor(expression);
//		NormalFormWrapper normalForm = new NormalFormWrapper(extractor.getFocusConceptIdList(), wrapRelationshipGroups(extractor.getGroupConcepts()));
//		
//		IBranchPath branchPath = BranchPathUtils.createPath(getBranch());
//		try (SnomedEditingContext editingContext = new SnomedEditingContext(branchPath)) {
//			com.b2international.snowowl.snomed.Concept concept = editingContext.buildDraftConceptFromNormalForm(normalForm);
//			concept.eAdapters().add(new ConceptParentAdapter(extractor.getFocusConceptIdList()));
//			IWidgetModelProvider widgetModelProvider = ApplicationContext.getInstance().getService(IWidgetModelProvider.class);
//			ConceptWidgetModel conceptWidgetModel = widgetModelProvider.createConceptWidgetModel(branchPath, extractor.getFocusConceptIdList(), null);
//			IWidgetBeanProvider widgetBeanProvider = ApplicationContext.getServiceForClass(IWidgetBeanProvider.class);
//			ConceptWidgetBean conceptWidgetBean = widgetBeanProvider.createConceptWidgetBean(branchPath, concept.getId(), conceptWidgetModel, null, true, false, new NullProgressMonitor());
//			IDiagnostic diagnostic = new MrcmConceptWidgetBeanValidator().validate(conceptWidgetBean);
//			Set<Attribute> markedAttributes = Sets.newHashSet();
//			for (IDiagnostic childDiagnostic : diagnostic.getChildren()) {
//				DiagnosticSeverity severity = childDiagnostic.getProblemMarkerSeverity();
//				switch (severity) {
//				case ERROR:
//					// find exact location for the error
//					WidgetBeanValidationDiagnostic widgetBeanDiagnostic = (WidgetBeanValidationDiagnostic) childDiagnostic;
//					ModeledWidgetBean widgetBean = widgetBeanDiagnostic.getWidgetBean();
//					if (widgetBean instanceof RelationshipWidgetBean) {
//						RelationshipWidgetBean relationshipWidgetBean = (RelationshipWidgetBean) widgetBean;
//						ScgAttributeFinderVisitor<SnomedConceptDocument> attributeExtractingVisitor =	
//								new ScgAttributeFinderVisitor<SnomedConceptDocument>(relationshipWidgetBean.getSelectedType().getId(), 
//										relationshipWidgetBean.getSelectedValue().getId(), Integer.MAX_VALUE, markedAttributes);
//						EObjectWalker extractorWalker = EObjectWalker.createContainmentWalker(attributeExtractingVisitor);
//						extractorWalker.walk(expression);
//						List<Attribute> matchingAttributes = attributeExtractingVisitor.getMatchingAttributes();
//						if (!CompareUtils.isEmpty(matchingAttributes)) {
//							final Attribute matchingAttribute = matchingAttributes.get(0);
//							if (matchingAttribute.eContainer() instanceof Expression) {
//								Expression containingExpression = (Expression) matchingAttribute.eContainer();
//								int index = containingExpression.getAttributes().indexOf(matchingAttribute);
//								error(childDiagnostic.getMessage(), containingExpression, ScgPackage.eINSTANCE.getExpression_Attributes(), index);
//							} else if (matchingAttribute.eContainer() instanceof Group) {
//								Group containingGroup = (Group) matchingAttribute.eContainer();
//								int index = containingGroup.getAttributes().indexOf(matchingAttribute);
//								error(childDiagnostic.getMessage(), containingGroup, ScgPackage.eINSTANCE.getGroup_Attributes(), index);
//							} else {
//								throw new IllegalStateException("Unexpected attribute container: " + matchingAttribute.eContainer());
//							}
//							markedAttributes.add(matchingAttribute);
//						}
//					}
//					break;
//					
//				default:
//					break;
//				}
//			}
//		}
//	}
//
//	private Collection<AttributeConceptGroupWrapper> wrapRelationshipGroups(final Collection<ExtractedSCGAttributeGroup> groupConcepts) {
//		final Set<AttributeConceptGroupWrapper> attributeConceptGroupWrappers = Sets.newHashSet();
//		for (ExtractedSCGAttributeGroup group : groupConcepts) {
//			attributeConceptGroupWrappers.add(new AttributeConceptGroupWrapper(group.getAttributeConceptIdMap(), group.getGroupId()));
//		}
//		return attributeConceptGroupWrappers;
//	}
	
	/**
	 * Check if the concept id matches the subsequent term declaration. 
	 * Shows no error if it matches the preferred term, a warning if the term is a synonym, error otherwise.
	 * 
	 * @param concept
	 */
	@Check(CheckType.NORMAL)
	public void checkNonMatchingTerm(Concept concept) {
		if (concept != null && concept.getId() != null) {
			checkNonMatchingTerm(concept.getId(), concept.getTerm(), ScgPackage.Literals.CONCEPT__TERM);
		}
	}
	
	private void checkNonMatchingTerm(String id, String term, EAttribute termAttribute) {
		
		final SnomedConcept concept = SnomedRequests.prepareGetConcept(id)
				.setExpand("descriptions(),pt()")
				.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		
		if (concept.getPt() != null && concept.getPt().getTerm().equals(term)) {
			return;
		}
		
		for (SnomedDescription description : concept.getDescriptions()) {
			if (description.getTerm().equals(term)) {
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
					warning("This is the fully specified name, not the preferred term.", termAttribute, NON_MATCHING_TERM);
					return;
				} else if (Concepts.SYNONYM.equals(description.getTypeId())) {
					warning("This is a synonym, not the preferred term.", termAttribute, NON_MATCHING_TERM);
					return;
				}
			}
		}
		
		error("This term is not a description for the specified component ID.", termAttribute, NON_MATCHING_TERM);
	}
	
	/**
	 * Concept should be regarded as inactive if its status is: CURRENT/LIMITED/PENDING_MOVE.
	 * 
	 * @param concept
	 */
	@Check(CheckType.NORMAL) 
	public void checkNonActiveConcepts(Concept concept) {
		if (null == concept || concept.getId() == null) {
			return;
		}
		
		final SnomedConcept entry = getConcept(concept.getId());
		if (entry != null && !entry.isActive()) {
			warning("Concept is inactive", ScgPackage.eINSTANCE.getConcept_Id(), INACTIVE_CONCEPT);
		}
	}

	private SnomedConcept getConcept(String id) {
		return Iterables.getOnlyElement(SnomedRequests.prepareSearchConcept()
				.setLimit(1)
				.filterById(id)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(bus.get()).getSync(), null);
	}

	private String getBranch() {
		checkArgument(getContext().containsKey("activeBranch"), "Active branch scope is required to execute this validator");
		return (String) getContext().get("activeBranch");
	}

}