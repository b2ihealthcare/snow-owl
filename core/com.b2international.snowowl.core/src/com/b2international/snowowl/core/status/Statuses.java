/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.status;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import com.b2international.snowowl.core.CoreActivator;

/**
 * Common methods and functions to work with {@link IStatus},
 * {@link SerializableStatus} and {@link SerializableMultiStatus} instances much
 * more easily.
 * 
 * @since 3.2
 */
public final class Statuses {

	private Statuses() {
	}

	/**
	 * Returns the {@link Status#CANCEL_STATUS} instance.
	 * 
	 * @return
	 */
	public static IStatus cancel() {
		return Status.CANCEL_STATUS;
	}

	/**
	 * Returns the {@link Status#OK_STATUS} instance.
	 * 
	 * @return
	 */
	public static IStatus ok() {
		return Status.OK_STATUS;
	}
	
	/**
	 * Returns an {@link IStatus} instance representing pending status.
	 * @return
	 */
	public static IStatus pending() {
		return info("Pending...");
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#ERROR}
	 * severity, {@link CommonsActivator#PLUGIN_ID} as pluginId, and with the
	 * given message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus error(final String message) {
		return new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID, message);
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#WARNING}
	 * severity, {@link CommonsActivator#PLUGIN_ID} as pluginId, and with the
	 * given message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus warn(final String message) {
		return new Status(IStatus.WARNING, CoreActivator.PLUGIN_ID, message);
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#INFO}
	 * severity, {@link CommonsActivator#PLUGIN_ID} as pluginId, and with the
	 * given message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus info(final String message) {
		return new Status(IStatus.INFO, CoreActivator.PLUGIN_ID, message);
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#ERROR}
	 * severity, and with the given pluginId and message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus error(final String pluginId, final String message) {
		return new Status(IStatus.ERROR, pluginId, message);
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#WARNING}
	 * severity, and with the given pluginId and message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus warn(final String pluginId, final String message) {
		return new Status(IStatus.WARNING, pluginId, message);
	}

	/**
	 * Returns a new {@link IStatus} instance with {@link IStatus#INFO}
	 * severity, and with the given pluginId and message.
	 * 
	 * @param message
	 * @return
	 */
	public static IStatus info(final String pluginId, final String message) {
		return new Status(IStatus.INFO, pluginId, message);
	}

	/**
	 * Returns anew {@link IStatus} instanceof with {@link IStatus#ERROR}
	 * severity, and with the given pluginId, message, a throwable.
	 * 
	 * @param pluginId
	 * @param message
	 * @param t
	 * @return
	 */
	public static IStatus error(final String pluginId, final String message, final Throwable t) {
		return new Status(IStatus.ERROR, pluginId, message, t);
	}

	/**
	 * Returns a serializable {@link IStatus} implementation with
	 * {@link IStatus#OK} severity.
	 * 
	 * @return
	 */
	public static IStatus serializableOk() {
		return SerializableStatus.OK_STATUS;
	}

	/**
	 * Returns a serializable {@link IStatus} implementation with
	 * {@link IStatus#CANCEL} severity.
	 * 
	 * @return
	 */
	public static IStatus serializableCancel() {
		return SerializableStatus.CANCEL_STATUS;
	}

	/**
	 * Wraps the given {@link IStatus} instance as serializable {@link IStatus}.
	 * 
	 * @param status
	 * @return
	 */
	public static SerializableStatus toSerializable(final IStatus status) {
		return new SerializableStatus(status);
	}

	/**
	 * Adds the content of the given {@link IStatus} to the specified {@link SerializableMultiStatus}.
	 * 
	 * @param multiStatus
	 * @param status
	 */
	public static void mergeInto(final SerializableMultiStatus multiStatus, final IStatus status) {
		if (status instanceof SerializableStatus) {
			multiStatus.merge((SerializableStatus) status);
		} else {
			multiStatus.merge(toSerializable(status));
		}
	}
	
	/**
	 * Returns with a copy of the status argument.
	 * 
	 * @param status
	 *            the status to copy.
	 * @return the cloned status.
	 */
	public static IStatus copyOf(final IStatus status) {
		final IStatus copy;
		if (isMultiStatus(status)) {
			copy = new MultiStatus(status.getPlugin(), status.getCode(), status.getMessage(), status.getException());
			for (final IStatus childStatus : ((MultiStatus) status).getChildren()) {
				((MultiStatus) copy).add(copyOf(childStatus));
			}
		} else {
			copy = new Status(status.getSeverity(), status.getPlugin(), status.getCode(), status.getMessage(),
					status.getException());
		}
		return status instanceof Serializable ? toSerializable(copy) : copy;
	}

	private static boolean isMultiStatus(final IStatus status) {
		return checkNotNull(status, "status").isMultiStatus() && status instanceof MultiStatus;
	}

}