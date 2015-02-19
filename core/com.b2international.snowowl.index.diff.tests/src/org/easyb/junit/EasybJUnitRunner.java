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
package org.easyb.junit;

import org.easyb.Configuration;
import org.easyb.domain.Behavior;
import org.easyb.listener.ExecutionListener;
import org.easyb.listener.ListenerBuilder;
import org.easyb.listener.ListenerFactory;
import org.easyb.listener.ResultsAmalgamator;
import org.easyb.report.HtmlReportWriter;
import org.easyb.report.ReportWriter;
import org.easyb.report.TxtSpecificationReportWriter;
import org.easyb.report.TxtStoryReportWriter;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.easyb.BehaviorRunner.getBehaviors;
import static org.easyb.junit.RunProperties.isEclipse;
import static org.easyb.junit.RunProperties.isIDEA;
import static org.junit.runner.Description.createSuiteDescription;

public class EasybJUnitRunner extends Runner {
	private final RunNotifierReplay runNotifierReplay = new RunNotifierReplay(); //needed for timing within the ide
	private final DescriptionCreator descriptionCreator;
	private Description description;
	private List<Behavior> behaviors;
	private final EasybSuite suite;
	private JunitExecutionListenerRegistry listenerRegistry;
	private Configuration configuration;

	public EasybJUnitRunner(final Class<? extends EasybSuite> testClass) throws Exception {
		suite = testClass.newInstance();
		descriptionCreator = new DescriptionCreator(suite.baseDir());
		listenerRegistry = new JunitExecutionListenerRegistry();
		configuration = new Configuration(getFilePaths(), getReports(suite));
		ListenerFactory.registerBuilder(new ListenerBuilder() {
			public ExecutionListener get() {
				return listenerRegistry;
			}
		});
	}

	public Description getDescription() {
		if (description == null) {
			description = createSuiteDescription(suite.description());
			if (isEclipse() || isIDEA())
				executeBehaviors(runNotifierReplay);
		}
		return description;
	}

	private void executeBehaviors(final RunNotifier notifier) {
		for (final Behavior behavior : behaviors()) {
			final Description behaviorDescription = descriptionCreator.create(behavior);
			getDescription().addChild(behaviorDescription);
			executeBehavior(behavior, behaviorDescription, notifier);
		}
		ListenerFactory.notifyTestingCompleted();
		final ResultsAmalgamator resultsAmalgamator = new ResultsAmalgamator(behaviors);
		for (final ReportWriter report : configuration.getConfiguredReports()) {
			report.writeReport(resultsAmalgamator);
		}
	}

	private void executeBehavior(final Behavior behavior, final Description behaviorDescription, final RunNotifier notifier) {
		final JUnitExecutionListener listener = new JUnitExecutionListener(behaviorDescription, notifier);
		listenerRegistry.registerListener(listener);
		try {
			behavior.execute();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			listenerRegistry.unregisterListener(listener);
		}
	}

	public void run(final RunNotifier notifier) {
		if (isEclipse() || isIDEA())
			runNotifierReplay.replay(notifier, suite.trackTime());
		else
			executeBehaviors(notifier);
	}

	private List<Behavior> behaviors() {
		return behaviors != null ? behaviors : (behaviors = getBehaviors(getFilePaths()));
	}

	private String[] getFilePaths() {
		final List<String> filePaths = new ArrayList<String>();
		listFiles(suite.searchDir(), filePaths);
		return filePaths.toArray(new String[filePaths.size()]);
	}

	private void listFiles(final File dir, final List<String> files) {
		//check whether this is a valid directory at all
		final File[] filesInDirectory = dir.listFiles();
		if (filesInDirectory != null) {
			for (final File file : filesInDirectory) {
				if (file.isDirectory()) {
					listFiles(file, files);
				} else if (isBehavior(file)) {
					files.add(file.getAbsolutePath());
				}
			}
		} else {
			System.err.println("Could not find any behaviour/story files in " + dir + ", maybe IDE prefix (working dir) is wrong?");
		}
	}

	private boolean isBehavior(final File file) {
		return suite.isBehavior(filenameExtension(file.getName()));
	}

	private static String filenameExtension(final String fileName) {
		final int idx = fileName.lastIndexOf(".");
		return fileName.substring(idx + 1, fileName.length());
	}

	private List<ReportWriter> getReports(final EasybSuite suite) {
		if (!suite.generateReports()) {
			return new ArrayList<ReportWriter>();
		}
		final String reportsDir = suite.reportsDir().getPath();

		final File html = new File(reportsDir, "html");
		html.mkdirs();
		final File plain = new File(reportsDir, "plain");
		plain.mkdirs();

		final List<ReportWriter> reports = new ArrayList<ReportWriter>();
		reports.add(new HtmlReportWriter(html.getAbsolutePath() + "/easyb.html"));
		reports.add(new TxtStoryReportWriter(plain.getAbsolutePath() + "/easyb-stories.txt"));
		reports.add(new TxtSpecificationReportWriter(plain.getAbsolutePath() + "/easyb-specifications.txt"));
		return reports;
	}
}