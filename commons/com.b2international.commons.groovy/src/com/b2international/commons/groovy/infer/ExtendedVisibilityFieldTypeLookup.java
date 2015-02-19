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
package com.b2international.commons.groovy.infer;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;
import groovy.transform.Field;

import java.util.Collection;
import java.util.Map;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.jdt.groovy.core.util.ReflectionUtils;
import org.eclipse.jdt.groovy.search.AbstractSimplifiedTypeLookup;
import org.eclipse.jdt.groovy.search.ITypeLookup;
import org.eclipse.jdt.groovy.search.VariableScope;

import com.b2international.commons.groovy.transform.ExtendScript;
import com.b2international.commons.groovy.transform.Service;

/**
 * Type lookup to infer types which visibility has been extended to a field one.
 * Variables with {@link Field}, {@link Service} or {@link ExtendScript}. 
 *
 */
public class ExtendedVisibilityFieldTypeLookup extends AbstractSimplifiedTypeLookup implements ITypeLookup {

	private static final Collection<String> ACCEPTED_ANNOTATIONS = unmodifiableCollection(newHashSet(
			Service.class.getName(),
			ExtendScript.class.getName(),
			Field.class.getName()
		)); 
	
	private final Map<String, ClassNode> typesAndDeclarations = newHashMap(); 
	
	@Override
	public void initialize(final GroovyCompilationUnit compilationUnit, final VariableScope variableScope) {
		final ModuleNode moduleNode = (ModuleNode) ReflectionUtils.getPrivateField(VariableScope.class, "scopeNode", variableScope);
		final BlockStatement statementBlock = moduleNode.getStatementBlock();
		for (final Statement statement : statementBlock.getStatements()) {
			if (statement instanceof ExpressionStatement) {
				final ExpressionStatement expressionStatement = (ExpressionStatement) statement;
				final Expression expression = expressionStatement.getExpression();
				if (expression instanceof DeclarationExpression) {
					final DeclarationExpression declarationExpression = (DeclarationExpression) expression;
					for (final AnnotationNode annotationNode : declarationExpression.getAnnotations()) {
						final String name = String.valueOf(annotationNode.getClassNode().redirect());
						if (null != annotationNode.getClassNode() 
								&& ACCEPTED_ANNOTATIONS.contains(name)) {
							
							final Expression leftExpression = ((DeclarationExpression) expression).getLeftExpression();
							typesAndDeclarations.put(leftExpression.getText(), leftExpression.getType());
							
						}
					}
				}
			}
		}
		
	}

	@Override
	protected TypeAndDeclaration lookupTypeAndDeclaration(final ClassNode classNode, final String name, final VariableScope scope) {
		final ClassNode typeClassNode = typesAndDeclarations.get(name);
		if (null != typeClassNode) {
			return new TypeAndDeclaration(typeClassNode, typeClassNode);
		}
		return null;
	}
	



}