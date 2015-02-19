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

package com.b2international.commons.groovy.transform;

import groovy.lang.Script;
import groovyjarjarasm.asm.Opcodes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * Handles transformation for the @Service annotation.
 *
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ServiceASTTransformation extends ClassCodeExpressionTransformer implements ASTTransformation, Opcodes {

	private static final Class<?> CLASS = Service.class;
	private static final ClassNode TYPE = ClassHelper.make(CLASS);
	private static final String TYPE_NAME = "@" + TYPE.getNameWithoutPackage();
	private static final ClassNode AST_TRANSFORMATION_TYPE = ClassHelper.make(GroovyASTTransformationClass.class);
	private SourceUnit sourceUnit;
	private DeclarationExpression candidate;
	private boolean insideScriptBody;
	private String variableName;
	private FieldNode serviceFieldNode;
	private ClosureExpression currentClosure;

	public void visit(final ASTNode[] nodes, final SourceUnit source) {
		sourceUnit = source;
		if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
			throw new GroovyBugError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
		}

		final AnnotatedNode parent = (AnnotatedNode) nodes[1];
		final AnnotationNode node = (AnnotationNode) nodes[0];
		if (!TYPE.equals(node.getClassNode()))
			return;

		if (parent instanceof DeclarationExpression) {
			final DeclarationExpression de = (DeclarationExpression) parent;
			final ClassNode cNode = de.getDeclaringClass();
			if (!cNode.isScript()) {
				addError("Error: annotation " + TYPE_NAME + " can only be used within a Script.", parent);
				return;
			}
			
			final ClassNode injectedType = de.getVariableExpression().getType().getPlainNodeReference();
			
            if(injectedType.isScript()){
                addError("Error: declared type " + injectedType + " should not extend " + Script.class.getName() + " class.", parent);
                return;
            }
			
			candidate = de;
			if (de.isMultipleAssignmentDeclaration()) {
				addError("Error: annotation " + TYPE_NAME + " not supported with multiple assignment notation.", parent);
				return;
			}
			final VariableExpression ve = de.getVariableExpression();
			variableName = ve.getName();

			final ConstructorCallExpression initializerExpression = new ConstructorCallExpression(ve.getType(), MethodCallExpression.NO_ARGUMENTS);

			//set owner null here, it will be updated by addField
			serviceFieldNode = new FieldNode(variableName, ve.getModifiers(), ve.getType(), null, initializerExpression);
			serviceFieldNode.setSourcePosition(de);
			cNode.addField(serviceFieldNode);

			//annotations that are not Groovy transforms should be transferred to the generated field
			final List<AnnotationNode> annotations = de.getAnnotations();
			for (final AnnotationNode annotation : annotations) {
				final ClassNode annotationClassNode = annotation.getClassNode();
				if (annotationClassNode.getAnnotations(AST_TRANSFORMATION_TYPE).isEmpty()) {
					serviceFieldNode.addAnnotation(annotation);
				}
			}

			super.visitClass(cNode);
			//so that Closures can see newly added fields
			//(not super efficient for a very large class with many @InjectScript but we chose simplicity
			//and understandability of this solution over more complex but efficient alternatives)
			final VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
			scopeVisitor.visitClass(cNode);
		}
	}

	@Override
	public Expression transform(final Expression expr) {
		if (expr == null) {
			return null;
		}
		if (expr instanceof DeclarationExpression) {
			final DeclarationExpression de = (DeclarationExpression) expr;
			if (de.getLeftExpression() == candidate.getLeftExpression()) {
				if (insideScriptBody) {
					//TODO make EmptyExpression work partially works but not if only thing in script
					//return EmptyExpression.INSTANCE;
					return new ConstantExpression(null);
				}
				addError("Error: annotation " + TYPE_NAME + " can only be used within a Script body.", expr);
				return expr;
			}
		} else if (insideScriptBody && expr instanceof VariableExpression && currentClosure != null) {
			final VariableExpression ve = (VariableExpression) expr;
			if (ve.getName().equals(variableName)) {
				//we may only check the variable name because the Groovy compiler
				//already fails if a variable with the same name already exists in the scope.
				//this means that a closure cannot shadow a class variable
				ve.setAccessedVariable(serviceFieldNode);
				final VariableScope variableScope = currentClosure.getVariableScope();
				final Iterator<Variable> iterator = variableScope.getReferencedLocalVariablesIterator();
				while (iterator.hasNext()) {
					final Variable next = iterator.next();
					if (next.getName().equals(variableName))
						iterator.remove();
				}
				variableScope.putReferencedClassVariable(serviceFieldNode);
				return ve;
			}
		}
		return expr.transformExpression(this);
	}

	@Override
	public void visitClosureExpression(final ClosureExpression expression) {
		final ClosureExpression old = currentClosure;
		currentClosure = expression;
		super.visitClosureExpression(expression);
		currentClosure = old;
	}

	@Override
	public void visitMethod(final MethodNode node) {
		final Boolean oldInsideScriptBody = insideScriptBody;
		if (node.isScriptBody()) {
			insideScriptBody = true;
		}
		super.visitMethod(node);
		insideScriptBody = oldInsideScriptBody;
	}

	@Override
	public void visitExpressionStatement(final ExpressionStatement es) {
		final Expression exp = es.getExpression();
		if (exp instanceof BinaryExpression) {
			exp.visit(this);
		}
		super.visitExpressionStatement(es);
	}

	@Override
	protected SourceUnit getSourceUnit() {
		return sourceUnit;
	}
}