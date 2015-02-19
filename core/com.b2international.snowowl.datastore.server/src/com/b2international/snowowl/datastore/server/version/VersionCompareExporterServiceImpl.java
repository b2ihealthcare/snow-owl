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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.concurrent.ConcurrentCollectionUtils.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ComponentTypeNameCache;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.version.NodeTransformerService;
import com.b2international.snowowl.datastore.version.VersionCompareExporterService;
import com.google.common.base.Stopwatch;

/**
 * Version compare exporter service implementation.
 *
 */
public class VersionCompareExporterServiceImpl implements VersionCompareExporterService {

	private static final String CR_LF = "\n";
	private static final String HT = "\t";
	private static final String HEADER = "ID\tLabel\tChange\tComponent\tComponent type\tProperty\tFrom\tTo";
	private static final Logger LOGGER = getLogger(VersionCompareExporterServiceImpl.class);
	
	@Override
	public void export(final CompareResult result, final OutputStream os, final IProgressMonitor monitor) {
		
		try {

			final Stopwatch stopwatch = Stopwatch.createStarted();
			//100 units for the delta resolution (85%) + file writing (15%)
			//2 units for cheat
			final SubMonitor subMonitor = SubMonitor.convert(monitor, 102);
			
			final VersionCompareConfiguration configuration = result.getConfiguration();
			final String sourcePath = configuration.getSourcePath().getPath() + (configuration.isSourcePatched() ? "*" : "");
			final String targetPath = configuration.getTargetPath().getPath() + (configuration.isTargetPatched() ? "*" : "");
			LOGGER.info("Exporting changes between '" + sourcePath + "' and '" + targetPath + " for '" + configuration.getToolingName() + "'...");
			
			subMonitor.setTaskName("Initializing export...");
			subMonitor.worked(2); //cheat a bit
			
			final Object mutex = new Object();
			final PrintWriter writer = new PrintWriter(os, true);
			final int totalWork = result.size();
			final AtomicInteger currentPercent = new AtomicInteger();
			final AtomicInteger processedNodeCounter = new AtomicInteger();
			final Map<NodeDiff, String> diffToStringMap = newHashMap();
			
			final AtomicBoolean canceled = new AtomicBoolean();
			
			forEach(result, new Procedure<NodeDiff>() {
				protected void doApply(final NodeDiff diff) {
					if (!canceled.get()) {
						final NodeChange change = getNodeChange(configuration, diff);
						synchronized(mutex) {
							final int percent = (int) (processedNodeCounter.incrementAndGet() * 100.0f / (totalWork * 1.15f));
							if (percent > currentPercent.intValue()) {
								subMonitor.worked(1);
								currentPercent.incrementAndGet();
								if (monitor.isCanceled()) {
									if (canceled.compareAndSet(false, true)) {
										LOGGER.info("Version compare export was aborted by user.");
									}
								} else {
									subMonitor.setTaskName("Collecting change deltas for components... [" + currentPercent.intValue() + "%]");
								}
							}
							diffToStringMap.put(diff, VersionCompareExporterServiceImpl.this.toString(change));
						}
					}
				}
			});

			int modulo = (int) (totalWork / (100.0f - currentPercent.floatValue()));
			if (0 == modulo) { //total work is too few
				modulo = 1;
			}
			if (!canceled.get()) {
				writeFileHeader(writer, result);
				subMonitor.setTaskName("Generating output file... [" + currentPercent.intValue() + "%]");
			}
			int i = 0;
			for (final NodeDiff diff : result) {
				
				if (++i % modulo == 0) {
					subMonitor.worked(1);
					currentPercent.incrementAndGet();
					if (monitor.isCanceled()) {
						if (canceled.compareAndSet(false, true)) {
							LOGGER.info("Version compare export was aborted by user.");
						}
					} else {
						subMonitor.setTaskName("Generating output file... [" + currentPercent.intValue() + "%]");
					}
				}
				
				final String lines = diffToStringMap.get(diff);
				if (!isEmpty(lines)) {
					writer.println(lines);
				}
				
			}
				
			if (!canceled.get()) {
				LOGGER.info("Changes between '" + sourcePath + "' and '" + targetPath + "' for '" + configuration.getToolingName() + "' have been successfully exported. [" + stopwatch + "]");
			}
			
		} catch (final Exception e) {
			
			LOGGER.error("Failed to export compare result.", e);
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != monitor) {
				monitor.done();
			}
			
		}
		
	}

	private NodeChange getNodeChange(final VersionCompareConfiguration configuration, final NodeDiff diff) {
		return getServiceForClass(NodeTransformerService.class).transform(configuration, diff);
	}
	
	private void writeFileHeader(final PrintWriter writer, final CompareResult result) {
		writer.println(result.getConfiguration());
		writer.println(HEADER);
	}
	
	
	@Nullable private String toString(final NodeChange change) {
		if (!isEmpty(change.getDeltas())) {
			final StringBuilder sb = new StringBuilder();
			for (final NodeDelta delta : change.getDeltas()) {
				if (sb.length() > 0) {
					sb.append(CR_LF);
				}
				sb.append(toString(change, delta));
			}
			return sb.toString();
		}
		return null;
	}
	
	private String toString(final NodeChange change, final NodeDelta delta) {
		final StringBuilder sb = new StringBuilder();
		sb.append(change.getNodeId());
		sb.append(HT);
		sb.append(change.getLabel());
		sb.append(HT);
		sb.append(delta.getChange());
		sb.append(HT);
		sb.append(delta.getLabel());
		sb.append(HT);
		sb.append(ComponentTypeNameCache.INSTANCE.getComponentName(delta));
		sb.append(HT);
		sb.append(nullToEmpty(delta.getFeatureName()));
		sb.append(HT);
		sb.append(nullToEmpty(delta.getFromValue()));
		sb.append(HT);
		sb.append(nullToEmpty(delta.getToValue()));
		return sb.toString();
	}

}