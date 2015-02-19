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
package com.b2international.snowowl.datastore.editor.notification;

import java.io.Serializable;

/**
 * @since 2.8
 */
public class NotificationMessage<D extends Serializable> implements Serializable {
	
	private static final long serialVersionUID = -8463839970821100320L;
	
	private final D data;
	private final String message;
	
	public NotificationMessage(D data, String message) {
		this.data = data;
		this.message = message;
	}

	public D getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationMessage<?> other = (NotificationMessage<?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [data=");
		builder.append(data);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
	
}