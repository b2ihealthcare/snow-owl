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
package com.b2international.snowowl.scripting.core;

import static com.b2international.commons.collections.Collections3.forEach;
import static com.b2international.commons.exceptions.Exceptions.extractCause;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.slf4j.Logger;

import com.b2international.commons.collections.Procedure;
import com.b2international.commons.groovy.classloader.ScriptIncludingGroovyClassLoader;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Groovy scripting related helper methods
 * 
 */
public abstract class GroovyScriptHelper {

	private static final Logger LOGGER = getLogger(GroovyScriptHelper.class);
	
	private static final String ORG_CODEHAUS_GROOVY = "org.codehaus.groovy";
	
	public static final Supplier<Collection<URL>> ADDITIONAL_LIBS_URL_SUPPLIER = // 
			memoize(new Supplier<Collection<URL>>() {
				public Collection<URL> get() {
					return collectAdditionalLibraryUrls();
				}
			});

	/**
	 * Takes a Groovy file (actually any text file can do regardless of file extension), uses <b>this</b> class classloader to resolve classes used by the script
	 * (that is why this is not static method) and use reflection to execute Groovy script (calling <code>main</code> method. If there is no such thing as
	 * main method in the passed script file, Groovy executor takes care of it so <code>main</code> is optional).
	 * 
	 * @param groovySourceFilePath absolute path of the Groovy script file to be executed
	 * @throws Exception see groovy.lang.GroovyClassLoader.parseClass(File) and java.lang.Class.newInstance()
	 */
	public static void reflectiveGroovyScriptExecutor(final String groovySourceFilePath) throws Exception {
		reflectiveGroovyScriptExecutor(groovySourceFilePath, new NullProgressMonitor());
	}
	
	/**
	 * Executes a Groovy script after reading it from a file given by the absolute path on  the current class loader.
	 * @param groovySourceFilePath the absolute path to the script.
	 * @param monitor progress monitor for the progress.
	 * @throws Exception see java.lang.Class.newInstance() and groovy.lang.GroovyClassLoader.parseClass()
	 */
	public static void reflectiveGroovyScriptExecutor(final String groovySourceFilePath, final IProgressMonitor monitor) throws Exception {
		
		GroovyClassLoader groovyLoader = null;
		
		try {
			
			groovyLoader = new ScriptIncludingGroovyClassLoader(ScriptingCoreActivator.class.getClassLoader());
			final File groovyScriptFile = new File(groovySourceFilePath);
			
			if (!groovyScriptFile.exists() || groovyScriptFile.isDirectory()) {
				throw new IllegalArgumentException(groovySourceFilePath + " is not exist or directory.");
			}
			
			final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
			compilerConfiguration.addCompilationCustomizers(new ASTTransformationCustomizer(groovy.transform.ThreadInterrupt.class));
			injectGroovyAllToClassPath(groovyLoader);
			
			final GroovyShell shell = new GroovyShell(groovyLoader, new ScriptingBinding(), compilerConfiguration);
//			setField(GroovyShell.class, shell, "loader", groovyLoader);
			final Script script = shell.parse(groovyScriptFile);
			
			ScriptExecutionObserverJob observerJob = null; //job for observing the script execution and interrupting it on request
			ScriptRunnerClosure closure = null; //closure as a runnable
			
			try {
				
				closure = new ScriptRunnerClosure(script, observerJob);
				final Thread thread = DefaultGroovyStaticMethods.start(
						null /*according to javadoc this is just a placeholder variable used by Groovy categories; ignored for default static methods*/, 
						closure);

				if (null != closure) {
					if (null == closure.cause) { //not needed if the script cannot be executed
						
						observerJob = new ScriptExecutionObserverJob(thread, monitor);
						closure.observerJob = observerJob;
						observerJob.schedule();
						observerJob.join();
						
					}
				}
				
			} finally {
				
				if (null != observerJob) {
					observerJob.cancel();
					observerJob.join();
				}
				
				if (null != closure) { //notify client if error occurred
					
					Thread.sleep(500L); 
					
					if (null != closure.cause) {
						throw closure.cause;
					}
				}
			}
		
			
		} finally {
			
			if (null != groovyLoader) {
				groovyLoader.close();
			}
			
		}
		
	}

	private static void injectGroovyAllToClassPath(final GroovyClassLoader groovyLoader) {
		checkNotNull(groovyLoader, "groovyLoader");
		forEach(ADDITIONAL_LIBS_URL_SUPPLIER.get(), new Procedure<URL>() {
			protected void doApply(final URL url) {
				System.out.println("Injecting classpath: " + url);
				groovyLoader.addURL(url);
			}
		});
	}

	private static Collection<URL> collectAdditionalLibraryUrls() {
		final List<URL> urls = newArrayList();
		final BundleContext context = FrameworkUtil.getBundle(GroovyScriptHelper.class).getBundleContext();
		for (final Bundle bundle : context.getBundles()) {
			if (bundle.getSymbolicName().contains(ORG_CODEHAUS_GROOVY)) {
				final Version version = bundle.getVersion();
				if (is207Version(version)) {
					final Enumeration<URL> findEntries = bundle.findEntries("", "*.jar", true);
					urls.addAll(Collections.list(findEntries));
					break;
				}
			}
		}
		
		return unmodifiableList(urls);
	}
	
	private static boolean is207Version(final Version version) {
		return null != version 
			&& version.getMajor() == 2 
			&& version.getMinor() == 0 
			&& version.getMicro() == 7;
	}
	
	private GroovyScriptHelper() { /*suppress instantiation*/ }
	
	/**
	 * Closure for executing the {@code main} method of a Groovy script.
	 * @see Closure
	 * @see ScriptInterruptedException
	 */
	private static final class ScriptRunnerClosure extends Closure<Void> implements Serializable {

		private static final long serialVersionUID = 8722895774630497469L;

		private final Script groovyObject;
		private ScriptExecutionObserverJob observerJob;
		private Exception cause;

		/**
		 * Creates a new closure for executing a script.
		 * @param script the Groovy script to execute. Cannot be {@code null}.
		 * @param observerJob 
		 */
		ScriptRunnerClosure(final Script script, final ScriptExecutionObserverJob observerJob) {
			super(new Object() /*owner object that is not used in this context especially outer class is a static helper*/);
			this.observerJob = observerJob;
			this.groovyObject = Preconditions.checkNotNull(script, "Groovy object argument cannot be null.");
		}
		
		/* (non-Javadoc)
		 * @see groovy.lang.Closure#call()
		 */
		@Override
		public Void call() {
			try {
				groovyObject.run();//invokeMethod(MAIN_METHOD, new Object[] { /*empty array as args*/});
			} catch (final Exception e) { //other script execution related defects. e.g.: missing property, compile error
				final InterruptedException interruptedException = extractCause(e, GroovyScriptHelper.class.getClassLoader(), InterruptedException.class);
				cause = null != interruptedException ? new ScriptInterruptedException() : e;
			} finally {
				if (null != observerJob) { //immediately cancel associated observer job
					observerJob.cancel();
					try {
						observerJob.join();
					} catch (final InterruptedException e) {
						//not much we can do. fall through. 
					}
				}
			}
			return null; //only way to return with a void
		}
		
	}
	
}