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
package com.b2international.commons.groovy.classloader;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.File;
import java.security.CodeSource;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.groovy.core.util.ReflectionUtils;

import com.b2international.commons.groovy.transform.ExtendScript;
import com.google.common.io.Files;

/**
 * Class loader for parsing and including additional scripts before the {@link CompilePhase#SEMANTIC_ANALYSIS semantic analysis} compiler phase. 
 *
 */
public class ScriptIncludingGroovyClassLoader extends GroovyClassLoader {

    /**
     * Creates a {@link ScriptIncludingGroovyClassLoader} using the given ClassLoader as parent.
     */
    public ScriptIncludingGroovyClassLoader(final ClassLoader loader) {
        super(loader);
    }

	@Override
	public Class<?> parseClass(final GroovyCodeSource codeSource, final boolean shouldCacheSource) throws CompilationFailedException {
		synchronized (sourceCache) {
			Class<?> answer = sourceCache.get(codeSource.getName());
			if (null != answer) {
				return answer;
			}
			answer = doParseClass(codeSource);
			if (shouldCacheSource) {
				sourceCache.put(codeSource.getName(), answer);
			}
			return answer;
		}
	}

	protected Class<?> doParseClass(final GroovyCodeSource codeSource) {
		validate(codeSource);
		Class<?> answer; // Was neither already loaded nor compiling, so compile and add to cache.
		final CompilationUnit compilationUnit = createCompilationUnit(getConfiguration(), getCodeSource(codeSource));
		SourceUnit sourceUnit = null;
		if (null == codeSource.getFile()) {
			sourceUnit = compilationUnit.addSource(codeSource.getName(), codeSource.getScriptText());
		} else {
			sourceUnit = compilationUnit.addSource(codeSource.getFile());
		}
		
		prepocessCompilationUnit(compilationUnit, sourceUnit);
		
		final ClassCollector collector = createCollector(compilationUnit, sourceUnit);
		compilationUnit.setClassgenCallback(collector);
		int goalPhase = Phases.CLASS_GENERATION;
		if (getConfiguration() != null && getConfiguration().getTargetDirectory() != null) {
			goalPhase = Phases.OUTPUT;
		}
		
		compilationUnit.compile(goalPhase);

		answer = getGeneratedClass(collector);
		final String mainClass = sourceUnit.getAST().getMainClassName();
		for (final Object o : collector.getLoadedClasses()) {
			final Class<?> clazz = (Class<?>) o;
			final String clazzName = clazz.getName();
			definePackage(clazzName);
			setClassCacheEntry(clazz);
			if (clazzName.equals(mainClass)) {
				answer = clazz;
			}
		}
		return answer;
	}

	protected void validate(final GroovyCodeSource codeSource) {
		if (null == codeSource.getFile()) {
			if (null == codeSource.getScriptText()) {
				throw new IllegalArgumentException("Script text to compile cannot be null.");
			}
		}
	}

	protected void definePackage(final String className) {
		final int i = className.lastIndexOf('.');
		if (-1 != i) {
			final String pkgName = className.substring(0, i);
			final java.lang.Package pkg = getPackage(pkgName);
			if (pkg == null) {
				definePackage(pkgName, null, null, null, null, null, null, null);
			}
		}
	}

	protected CompilerConfiguration getConfiguration() {
		return (CompilerConfiguration) ReflectionUtils.getPrivateField(GroovyClassLoader.class, "config", this);
	}

	protected CodeSource getCodeSource(final GroovyCodeSource codeSource) {
		return (CodeSource) ReflectionUtils.getPrivateField(GroovyCodeSource.class, "codeSource", codeSource);
	}

	protected Class<?> getGeneratedClass(final ClassCollector classCollector) {
		return (Class<?>) ReflectionUtils.getPrivateField(ClassCollector.class, "generatedClass", classCollector);
	}

	private void prepocessCompilationUnit(final CompilationUnit compilationUnit, final SourceUnit sourceUnit) {
		buildAST(compilationUnit);
		final CompileUnit ast = compilationUnit.getAST();
		for (final ModuleNode moduleNode : ast.getModules()) {
	
			final BlockStatement statementBlock = moduleNode.getStatementBlock();
			for (final Statement statement : statementBlock.getStatements()) {
				if (statement instanceof ExpressionStatement) {
					final ExpressionStatement expressionStatement = (ExpressionStatement) statement;
					final Expression expression = expressionStatement.getExpression();
					if (expression instanceof DeclarationExpression) {
						final DeclarationExpression declarationExpression = (DeclarationExpression) expression;
	
						for (final AnnotationNode annotationNode : declarationExpression.getAnnotations()) {
							final ClassNode annotationNodeClassType = annotationNode.getClassNode();
							final ImportNode annotationImportNode = moduleNode.getImport(annotationNodeClassType.getName());
							if (null != annotationImportNode) {
								if (ExtendScript.class.getName().equals(annotationImportNode.getClassName())) {
	
									final Expression leftExpression = declarationExpression.getLeftExpression();
									if (leftExpression instanceof VariableExpression) {
	
										final String absoluteFilePath = sourceUnit.getName();
										final ClassNode annotatedNodeType = leftExpression.getType();
										final String annotatedClassName = annotatedNodeType.getName();
										
										//here we can make sure that this is a Script instance, otherwise compiler already marked it
										final File scriptSourceFile = tryGetScriptSourceFile(moduleNode, absoluteFilePath, annotatedClassName);
										if (null != scriptSourceFile) {
											compilationUnit.addSource(scriptSourceFile);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private File tryGetScriptSourceFile(final ModuleNode moduleNode, final String absoluteFilePath, final String annotatedClassName) {
		File file = tryGetWorkspaceFile(moduleNode, absoluteFilePath, annotatedClassName);
		if (null != file) {
			return file;
		}
		//TODO add implementation that support server side script execution
		return null;
	}

	private File tryGetWorkspaceFile(final ModuleNode moduleNode, final String absoluteFilePath, final String annotatedClassName) {
		final File file = new File(absoluteFilePath);
		if (!file.isFile() || !file.canRead()) {
			return null;
		}
		final IPath path = Path.fromOSString(file.getAbsolutePath());
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource member = root.getFileForLocation(path);
		if (null != member) {
			final IProject project = member.getProject();
			if (null != project && project.isOpen()) {
				final IJavaProject javaProject = JavaCore.create(project);
				if (null != javaProject) {
					for (File scriptFile : Files.fileTreeTraverser().preOrderTraversal(project.getLocation().toFile())) {
						if (scriptFile.isFile() && scriptFile.getName().equals(annotatedClassName + ".groovy")) {
							return scriptFile;
						}
					}
					
				}
			}
		}
		
		return null;
	}

	
	private void buildAST(final CompilationUnit unit) {
		unit.compile(Phases.CONVERSION);
	}

}