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
package com.b2international.snowowl.core.validation;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
import static com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity.CANCEL;
import static com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity.INFO;
import static com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity.OK;
import static com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity.valueOf;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.primitives.Ints.max;
import static com.google.common.primitives.Ints.toArray;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProvider;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * {@link IDiagnostic} implementation to be used with the component validation service.
 * 
 */
public class ComponentValidationDiagnosticImpl implements ComponentValidationDiagnostic {

	private static final long serialVersionUID = -9215897306106050093L;

	private static final ComponentValidationDiagnosticImpl CANCEL_DIAGNOSTIC = 
			new ComponentValidationDiagnosticImpl(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, UNSPECIFIED_NUMBER_SHORT, CANCEL);

	private static DiagnosticSeverity getMaxSeverity(final Iterable<? extends IDiagnostic> children) {
		if (isEmpty(children)) {
			return INFO;
		}
		return valueOf(max(toArray(transform(newHashSet(children), new Function<IDiagnostic, Integer>() {
			public Integer apply(final IDiagnostic diagnostic) {
				return diagnostic.getProblemMarkerSeverity().getErrorCode();
			}
		}))));
	}
	
	private static DiagnosticSeverity getMaxSeverity(final DiagnosticSeverity left, final DiagnosticSeverity right) {
		return checkNotNull(left, "left").compareTo(checkNotNull(right, "right")) < 1 ? left : right;
	}

	private final String componentId;
	private final String message;
	private final String sourceId;
	private final short terminologyComponentId;
	private final List<IDiagnostic> children;
	private DiagnosticSeverity severity;

	public static ComponentValidationDiagnostic createOk(final String componentId, final String sourceId, final short terminologyComponentId) {
		return new ComponentValidationDiagnosticImpl(componentId, EMPTY_STRING, sourceId, terminologyComponentId, OK);
	}

	public static ComponentValidationDiagnostic createOk(final IdAndTerminologyComponentIdProvider provider, final String sourceId) {
		return new ComponentValidationDiagnosticImpl(checkNotNull(provider, "provider").getId(), EMPTY_STRING, sourceId, provider.getTerminologyComponentId(), OK);
	}
	
	public static ComponentValidationDiagnostic createCancel() {
		return CANCEL_DIAGNOSTIC;
	}
	
	public ComponentValidationDiagnosticImpl(final IdAndTerminologyComponentIdProvider provider, final String message, final String sourceId, 
			final DiagnosticSeverity severity) {
		
		this(checkNotNull(provider, "provider").getId(), message, sourceId, provider.getTerminologyComponentId(), severity, Collections.<IDiagnostic>emptyList());
	}
	
	public ComponentValidationDiagnosticImpl(final String componentId, final String message, final String sourceId, 
			final short terminologyComponentId, final DiagnosticSeverity severity) {
		
		this(componentId, message, sourceId, terminologyComponentId, severity, Collections.<IDiagnostic>emptyList());
	}
	
	public ComponentValidationDiagnosticImpl(final String componentId, final String sourceId, 
			final short terminologyComponentId, final Collection<? extends IDiagnostic> children) {
		
		this(componentId, EMPTY_STRING, sourceId, UNSPECIFIED_NUMBER_SHORT, getMaxSeverity(children), children);
		
	}
	
	public ComponentValidationDiagnosticImpl(final String componentId, final String message, final String sourceId, 
			final short terminologyComponentId, final DiagnosticSeverity severity, final Collection<? extends IDiagnostic> children) {
		
		this.sourceId = checkNotNull(sourceId, "sourceId");
		this.terminologyComponentId = terminologyComponentId;
		this.severity = severity;
		this.componentId = checkNotNull(componentId, "componentId");
		this.message = nullToEmpty(message);
		this.children = newArrayList(children);
		this.severity = isEmpty(this.children) ? severity : getMaxSeverity(severity, getMaxSeverity(this.children));
	}
	
	@Override
	public boolean isOk() {
		return OK.equals(severity);
	}

	@Override
	public DiagnosticSeverity getProblemMarkerSeverity() {
		return severity;
	}
	
	@Override
	public List<IDiagnostic> getChildren() {
		return copyOf(children);
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String getSource() {
		return sourceId;
	}
	
	@Override
	public String getId() {
		return componentId;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("componentId", componentId).add("severity", severity).add("message", message).toString();
	}
}