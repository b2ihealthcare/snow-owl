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
package com.b2international.snowowl.snomed.core.mrcm.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.core.mrcm.ConceptModelComponentRenderer;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

/**
 * Exporter to create a delimiter separated file for the MRCM rules in the system.
 * 
 * @since 4.6
 */
public class CsvMrcmExporter {

	private static final Logger LOG = LoggerFactory.getLogger(CsvMrcmExporter.class);

	private static final Joiner TAB_JOINER = Joiner.on('\t').useForNull("");

	public void doExport(String user, OutputStream stream) {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final ConceptModelComponentRenderer renderer = new ConceptModelComponentRenderer(branch.getPath());

		try (SnomedEditingContext context = new SnomedEditingContext(branch)) {
			LogUtils.logExportActivity(LOG, user, branch, "Exporting MRCM rules to CSV...");

			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stream, Charsets.UTF_8)))) {
				writer.println(
						TAB_JOINER.join("uuid", "effectiveTime", "author", "strength", "description", "validationMessage", "form", "domain", "predicate"));

				for (ConstraintBase constraint : context.getConstraints()) {
					writer.print(TAB_JOINER.join(constraint.getUuid(), constraint.getEffectiveTime(), constraint.getAuthor(), constraint.getStrength(),
							constraint.getDescription(), constraint.getValidationMessage()));

					if (constraint instanceof AttributeConstraint) {
						final AttributeConstraint attributeConstraint = (AttributeConstraint) constraint;

						writer.print(TAB_JOINER.join(attributeConstraint.getForm(),
								renderer.getHumanReadableRendering(attributeConstraint.getDomain(), Integer.MAX_VALUE),
								renderer.getHumanReadableRendering(attributeConstraint.getPredicate(), Integer.MAX_VALUE)));
					}

					writer.println();
				}
			}

			LogUtils.logExportActivity(LOG, user, branch, "MRCM rule export to CSV successfully finished.");
		} catch (final Throwable t) {
			LogUtils.logExportActivity(LOG, user, branch, "Failed to export MRCM rules.");
			throw new SnowowlRuntimeException("Failed to export MRCM rules.", t);
		}
	}
}
