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
package com.b2international.commons.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CommonsActivator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * SerializableStatus implementation from the Eclipse ECF project.
 * 
 * @since 3.0
 */

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.CLASS,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@class")
public class SerializableStatus implements IStatus, Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(SerializableStatus.class);
	private static final long serialVersionUID = -1874392357776889683L;

	public static final IStatus OK_STATUS = new SerializableStatus(OK, CommonsActivator.PLUGIN_ID, OK, "ok", null); //$NON-NLS-1$
	public static final IStatus CANCEL_STATUS = new SerializableStatus(CANCEL, CommonsActivator.PLUGIN_ID, 1, "", null); //$NON-NLS-1$

	/**
	 * The severity. One of
	 * <ul>
	 * <li><code>CANCEL</code></li>
	 * <li><code>ERROR</code></li>
	 * <li><code>WARNING</code></li>
	 * <li><code>INFO</code></li>
	 * <li>or <code>OK</code> (0)</li>
	 * </ul>
	 */
	private int severity = OK;

	/**
	 * Unique identifier of plug-in.
	 */
	private String pluginId;

	/**
	 * Plug-in-specific status code.
	 */
	private int code;

	/**
	 * Message, localized to the current locale.
	 */
	private String message;

	/**
	 * Wrapped exception, or <code>null</code> if none.
	 */
	private Throwable exception = null;

	/**
	 * Constant to avoid generating garbage.
	 */
	private static final SerializableStatus[] EMPTY_ARRAY = new SerializableStatus[0];

	public SerializableStatus(IStatus status) {
		setSeverity(status.getSeverity());
		setPlugin(status.getPlugin());
		setCode(status.getCode());
		setMessage(status.getMessage());
		setException(status.getException());
	}

	public SerializableStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		setSeverity(severity);
		setPlugin(pluginId);
		setCode(code);
		setMessage(message);
		setException(exception);
	}
	
	@JsonCreator
	public SerializableStatus(@JsonProperty ("severity") int severity, 
			@JsonProperty ("plugin") String plugin, 
			@JsonProperty ("code") int code,
			@JsonProperty ("message") String message) {
		setSeverity(severity);
		setPlugin(plugin);
		setCode(code);
		setMessage(message);
	}

	public SerializableStatus(int severity, String pluginId, String message, Throwable exception) {
		setSeverity(severity);
		setPlugin(pluginId);
		setMessage(message);
		setException(exception);
		setCode(OK);
	}

	public SerializableStatus(int severity, String pluginId, String message) {
		setSeverity(severity);
		setPlugin(pluginId);
		setMessage(message);
		setCode(OK);
		setException(null);
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	public SerializableStatus[] getChildren() {
		return EMPTY_ARRAY;
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
	@JsonIgnore
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
	@JsonIgnore
	public boolean isMultiStatus() {
		return false;
	}

	/*
	 * (Intentionally not javadoc'd) Implements the corresponding method on
	 * <code>IStatus</code>.
	 */
	@JsonIgnore
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
	protected void setCode(int code) {
		this.code = code;
	}

	/**
	 * Sets the exception.
	 * 
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable
	 */
	protected void setException(Throwable exception) {
		this.exception = checkForSerializable(exception);
	}

	private Throwable checkForSerializable(Throwable exception2) {
		if (exception2 == null) return null;
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new ByteArrayOutputStream());
			oos.writeObject(exception2);
		} catch (IOException e) {
			LOG.warn(String.format("Exception %s could not be serialized for SerializableStatus", exception2), e); //$NON-NLS-1$
			// In this case, we'll create a new exception that can be serialized
			return createNewExceptionFor(exception2);
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		return exception2;
	}

	private Throwable createNewExceptionFor(Throwable exception2) {
		Exception re = new Exception(exception2.getMessage());
		// setStackTrace not in Foundation 1.1 as per
		// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=261781
		// [mczotter] we don't use Foundation 1.1 Exec Env, so we can use setStackTrace
		re.setStackTrace(exception2.getStackTrace());
		return re;
	}

	/**
	 * Sets the message. If null is passed, message is set to an empty string.
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale
	 */
	protected void setMessage(String message) {
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
	protected void setPlugin(String pluginId) {
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
	protected void setSeverity(int severity) {
		Assert.isLegal(severity == OK || severity == ERROR || severity == WARNING || severity == INFO || severity == CANCEL);
		this.severity = severity;
	}
	
	protected void setChildren(SerializableStatus [] children) {
	}

	/**
	 * Returns a string representation of the status, suitable for debugging
	 * purposes only.
	 * @return String
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("SerializableStatus "); //$NON-NLS-1$
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