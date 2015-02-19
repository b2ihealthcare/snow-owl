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
package com.b2international.snowowl.semanticengine.utils;

import java.text.MessageFormat;
import java.util.Collections;

import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.google.common.base.Preconditions;

/**
 * Ensures that an SCG {@link Expression} has a complete context wrapper.
 */
public class ContextWrapperBuilder {

	/**
	 * Ensures the completeness of the context wrapper of the expression passed in.
	 * <em>Note: it modifies and returns the original expression.</em>
	 * @param contextWrapperExpression the expression to process
	 * @return the processed expression
	 */
	public Expression ensureCompleteContextWrapper(Expression contextWrapperExpression) {
		Preconditions.checkNotNull(contextWrapperExpression, "Expression cannot be null.");
		Preconditions.checkArgument(hasSomeContextWrapper(contextWrapperExpression), 
				"The expression %s doesn't have a context wrapper.", contextWrapperExpression.toString());
		Group contextWrapperAttributeGroup = contextWrapperExpression.getGroups().get(0);
		
		// finding / procedure context
		if (hasAttribute(contextWrapperExpression, SemanticUtils.ASSOCIATED_FINDING_ID)) {	// finding
			if (hasAttribute(contextWrapperExpression, SemanticUtils.PROCEDURE_CONTEXT_ID))
				throw new IllegalArgumentException(MessageFormat.format("The expression {0} is inconsistent, it has both " +
						"an associated finding and a procedure context.", contextWrapperExpression.toString()));
			if (!hasAttribute(contextWrapperExpression, SemanticUtils.FINDING_CONTEXT_ID)) {
				Attribute findingContextAttribute = buildDefaultFindingContextAttribute();
				contextWrapperAttributeGroup.getAttributes().add(findingContextAttribute);
			}
		} else if (hasAttribute(contextWrapperExpression, SemanticUtils.ASSOCIATED_PROCEDURE_ID)) {	// procedure
			if (hasAttribute(contextWrapperExpression, SemanticUtils.FINDING_CONTEXT_ID))
				throw new IllegalArgumentException(MessageFormat.format("The expression {0} is inconsistent, it has both " +
						"an associated procedure and a finding context.", contextWrapperExpression.toString()));
			if (!hasAttribute(contextWrapperExpression, SemanticUtils.PROCEDURE_CONTEXT_ID)) {
				Attribute procedureContextAttribute = buildDefaultProcedureContextAttribute();
				contextWrapperAttributeGroup.getAttributes().add(procedureContextAttribute);
			}
		}
		
		// temporal context
		if (!hasAttribute(contextWrapperExpression, SemanticUtils.TEMPORAL_CONTEXT_ID)) {
			contextWrapperAttributeGroup.getAttributes().add(buildDefaultTemporalContextAttribute());
		}
		
		// subject relationship context
		if (!hasAttribute(contextWrapperExpression, SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID)) {
			contextWrapperAttributeGroup.getAttributes().add(buildDefaultSubjectRelationshipContextAttribute());
		}
		
		return contextWrapperExpression;
	}
	
	private static boolean hasAttribute(Expression expression, String nameConceptId) {
		AttributePresenceCheckingVisitor attributeValueExtractor = new AttributePresenceCheckingVisitor(nameConceptId);
		EObjectWalker walker = EObjectWalker.createContainmentWalker(attributeValueExtractor);
		walker.walk(expression);
		return attributeValueExtractor.isPresent();
	}

	/**
	 * @param expression
	 * @return true if the expression passed in has at least an incomplete context wrapper, false otherwise
	 */
	public static boolean hasSomeContextWrapper(Expression expression) {
		boolean focusConceptPresent = expression.getConcepts().size() == 1 && expression.getConcepts().get(0) != null
			&& SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID.equals(expression.getConcepts().get(0).getId());
		boolean hasOneAttributeGroup = expression.getGroups().size() == 1;
		return focusConceptPresent && hasOneAttributeGroup;
	}
	
	/**
	 * @param expression
	 * @return true if the expression passed in already has a complete context wrapper, false otherwise
	 */
	public static boolean hasCompleteContextWrapper(Expression expression) {
		Preconditions.checkNotNull(expression, "Expression cannot be null.");
		boolean incompleteContextWrapperPresent = hasSomeContextWrapper(expression);
		boolean clinicalKernelPresent = hasAttribute(expression, SemanticUtils.ASSOCIATED_FINDING_ID) 
			|| hasAttribute(expression, SemanticUtils.ASSOCIATED_PROCEDURE_ID);
		boolean contextPresent = hasAttribute(expression, SemanticUtils.FINDING_CONTEXT_ID) 
			|| hasAttribute(expression, SemanticUtils.PROCEDURE_CONTEXT_ID);
		boolean temporalContextPresent = hasAttribute(expression, SemanticUtils.TEMPORAL_CONTEXT_ID);
		boolean subjectRelationshipContextPresent = hasAttribute(expression, SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID);
		return incompleteContextWrapperPresent && clinicalKernelPresent && contextPresent && temporalContextPresent 
			&& subjectRelationshipContextPresent;
	}
	
	public Expression buildAssociatedFindingContextWrapper(Expression clinicalKernelExpression) {
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Group group = ScgBuilderUtils.buildGroup(buildAssociatedFindingAttribute(clinicalKernelExpression), 
				buildDefaultFindingContextAttribute(),
				buildDefaultTemporalContextAttribute(), 
				buildDefaultSubjectRelationshipContextAttribute());
		Expression expression = ScgBuilderUtils.buildExpression(Collections.singletonList(situationWithExplicitContextConcept), 
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		return expression;
	}
	
	public Expression buildAssociatedProcedureContextWrapper(Expression clinicalKernelExpression) {
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Group group = ScgBuilderUtils.buildGroup(buildAssociatedProcedureAttribute(clinicalKernelExpression), 
				buildDefaultProcedureContextAttribute(),
				buildDefaultTemporalContextAttribute(), 
				buildDefaultSubjectRelationshipContextAttribute());
		Expression expression = ScgBuilderUtils.buildExpression(Collections.singletonList(situationWithExplicitContextConcept), 
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		return expression;
	}
	
	public Attribute buildAssociatedFindingAttribute(Expression clinicalKernelExpression) {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.ASSOCIATED_FINDING_ID, clinicalKernelExpression);
	}
	
	public Attribute buildAssociatedProcedureAttribute(Expression clinicalKernelExpression) {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.ASSOCIATED_PROCEDURE_ID, clinicalKernelExpression);
	}
	
	public Attribute buildDefaultTemporalContextAttribute() {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.TEMPORAL_CONTEXT_ID, SemanticUtils.CURRENT_ID);
	}
	
	public Attribute buildDefaultSubjectRelationshipContextAttribute() {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID, SemanticUtils.SUBJECT_OF_RECORD_ID);
	}
	
	public Attribute buildDefaultFindingContextAttribute() {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.FINDING_CONTEXT_ID, SemanticUtils.KNOWN_PRESENT_ID);
	}
	
	public Attribute buildDefaultProcedureContextAttribute() {
		return ScgBuilderUtils.buildAttribute(SemanticUtils.PROCEDURE_CONTEXT_ID, SemanticUtils.PERFORMED_ID);
	}
}