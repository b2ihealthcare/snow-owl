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

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;

/**
 * Serializable {@link IStatus} implementation, to be used by the validation
 * service.
 * 
 */
public class ComponentValidationStatus implements IStatus, Serializable {

	private static final long serialVersionUID = -5998842027165848868L;

	private static final String UNKNOWN_PLUGIN_ID = "unknown"; //$NON-NLS-1$
	public static final ComponentValidationStatus OK_STATUS = new ComponentValidationStatus(OK, UNKNOWN_PLUGIN_ID, OK, "", null); //$NON-NLS-1$
	public static final ComponentValidationStatus CANCEL_STATUS = new ComponentValidationStatus(CANCEL, UNKNOWN_PLUGIN_ID, 1, "", null); //$NON-NLS-1$

	private ComponentValidationStatus[] children = new ComponentValidationStatus[0];
	private int severity = OK;
	private String pluginId;
	private int code;
	private String message;
	private Throwable exception = null;

	/**
	 * Creates a new status object. The created status has no children.
	 * 
	 * @param severity
	 *            the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *            <code>INFO</code>, <code>WARNING</code>, or
	 *            <code>CANCEL</code>
	 * @param pluginId
	 *            the unique identifier of the relevant plug-in
	 * @param code
	 *            the plug-in-specific status code, or <code>OK</code>
	 * @param message
	 *            a human-readable message, localized to the current locale
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable
	 */
	public ComponentValidationStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		setSeverity(severity);
		setPlugin(pluginId);
		setCode(code);
		setMessage(message);
		setException(exception);
	}

	/**
	 * Simplified constructor of a new status object; assumes that code is
	 * <code>OK</code>. The created status has no children.
	 * 
	 * @param severity
	 *            the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *            <code>INFO</code>, <code>WARNING</code>, or
	 *            <code>CANCEL</code>
	 * @param pluginId
	 *            the unique identifier of the relevant plug-in
	 * @param message
	 *            a human-readable message, localized to the current locale
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable
	 * 
	 * @since org.eclipse.equinox.common 3.3
	 */
	public ComponentValidationStatus(int severity, String pluginId, String message, Throwable exception) {
		setSeverity(severity);
		setPlugin(pluginId);
		setMessage(message);
		setException(exception);
		setCode(OK);
	}

	/**
	 * Simplified constructor of a new status object; assumes that code is
	 * <code>OK</code> and exception is <code>null</code>. The created status
	 * has no children.
	 * 
	 * @param severity
	 *            the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *            <code>INFO</code>, <code>WARNING</code>, or
	 *            <code>CANCEL</code>
	 * @param pluginId
	 *            the unique identifier of the relevant plug-in
	 * @param message
	 *            a human-readable message, localized to the current locale
	 * 
	 * @since org.eclipse.equinox.common 3.3
	 */
	public ComponentValidationStatus(int severity, String pluginId, String message) {
		setSeverity(severity);
		setPlugin(pluginId);
		setMessage(message);
		setCode(OK);
		setException(null);
	}
	
	public ComponentValidationStatus(String pluginId, String message, Collection<ComponentValidationStatus> children) {
		this(getSeverity(children), pluginId, message);
		this.children = children.toArray(new ComponentValidationStatus[0]);
	}
	
	public ComponentValidationStatus() {
	}

	private static int getSeverity(Collection<ComponentValidationStatus> children) {
		int maxSeverity = OK;
		for (ComponentValidationStatus childStatus : children) {
			int severity = childStatus.getSeverity();
			if (severity > maxSeverity)
				maxSeverity = severity;
		}
		return maxSeverity;
	}

	public ComponentValidationStatus[] getChildren() {
		return children;
	}

	@Override
	public boolean isMultiStatus() {
		return children.length > 0;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public int getCode() {
		return code;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public Throwable getException() {
		return exception;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public String getPlugin() {
		return pluginId;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public int getSeverity() {
		return severity;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public boolean isOK() {
		return severity == OK;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public boolean matches(int severityMask) {
		return (severity & severityMask) != 0;
	}

	/**
	 * Sets the status code.
	 * 
	 * @param code
	 *            the plug-in-specific status code, or <code>OK</code>
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Sets the exception.
	 * 
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Sets the message. If null is passed, message is set to an empty string.
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale
	 */
	public void setMessage(String message) {
		if (message == null)
			this.message = ""; //$NON-NLS-1$
		else
			this.message = message;
	}

	/**
	 * Sets the plug-in id.
	 * 
	 * @param pluginId
	 *            the unique identifier of the relevant plug-in
	 */
	public void setPlugin(String pluginId) {
		Assert.isLegal(pluginId != null && pluginId.length() > 0);
		this.pluginId = pluginId;
	}

	/**
	 * Sets the severity.
	 * 
	 * @param severity
	 *            the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *            <code>INFO</code>, <code>WARNING</code>, or
	 *            <code>CANCEL</code>
	 */
	public void setSeverity(int severity) {
		Assert.isLegal(severity == OK || severity == ERROR || severity == WARNING || severity == INFO || severity == CANCEL);
		this.severity = severity;
	}

	public void setChildren(ComponentValidationStatus[] children) {
		this.children = children;
	}
	
	/**
	 * Returns a string representation of the status, suitable for debugging
	 * purposes only.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Status "); //$NON-NLS-1$
		if (severity == OK) {
			buf.append("OK"); //$NON-NLS-1$
		} else if (severity == ERROR) {
			buf.append("ERROR"); //$NON-NLS-1$
		} else if (severity == WARNING) {
			buf.append("WARNING"); //$NON-NLS-1$
		} else if (severity == INFO) {
			buf.append("INFO"); //$NON-NLS-1$
		} else if (severity == CANCEL) {
			buf.append("CANCEL"); //$NON-NLS-1$
		} else {
			buf.append("severity="); //$NON-NLS-1$
			buf.append(severity);
		}
		buf.append(": "); //$NON-NLS-1$
		buf.append(pluginId);
		buf.append(" code="); //$NON-NLS-1$
		buf.append(code);
		buf.append(' ');
		buf.append(message);
		buf.append(' ');
		buf.append(exception);
		return buf.toString();
	}
}