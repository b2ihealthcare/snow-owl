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
package com.b2international.snowowl.internal.rpc;

import static org.eclipse.net4j.util.CheckUtil.checkNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * Enumerates supported serialization types of {@link ExtendedDataInput}, and
 * provides read/write methods for them, as well as a utility method for getting
 * the type of an arbitrary {@link Object}.
 * 
 */
public enum ValueType {
	
	BOOLEAN {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readBoolean();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeBoolean((Boolean) value);
		}

		@Override public Class<?> getValueClass() {
			return Boolean.TYPE;
		}
	},

	BYTE {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readByte();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeByte((Byte) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Byte.TYPE;
		}
	},

	BYTE_ARRAY {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readByteArray();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeByteArray((byte[]) value);
		}
		
		@Override public Class<?> getValueClass() {
			return byte[].class;
		}		
	},

	INT {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readInt();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeInt((Integer) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Integer.TYPE;
		}
	},

	LONG {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readLong();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeLong((Long) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Long.TYPE;
		}
	},

	FLOAT {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readFloat();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeFloat((Float) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Float.TYPE;
		}
	},

	DOUBLE {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readDouble();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeDouble((Double) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Double.TYPE;
		}
	},

	STRING {
		@Override public Object read(final ExtendedDataInput in) throws IOException {
			return in.readString();
		}

		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeString((String) value);
		}
		
		@Override public Class<?> getValueClass() {
			return String.class;
		}
	},
	
	SHORT {
		@Override
		public Object read(final ExtendedDataInput in) throws IOException {
			return in.readShort();
		}
		
		@Override
		public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeShort((Short) value);
		}
		
		@Override
		public Class<?> getValueClass() {
			return Short.TYPE;
		}
	},

	// Can't be read or written without wrapping and additional magic 
	PROGRESS_MONITOR(true) {
		@Override public Class<?> getValueClass() {
			return IProgressMonitor.class;
		}
	},
	
	INPUT(true) {
		@Override public Class<?> getValueClass() {
			return InputStream.class;
		}
	},
	
	OUTPUT(true) {
		@Override public Class<?> getValueClass() {
			return OutputStream.class;
		}
	},
	
	// Can't be read without a corresponding class loader to load the enum definition
	ENUM_VALUE {
		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeString(value.getClass().getName());
			out.writeEnum((Enum<?>) value);
		}
		
		@Override public Class<?> getValueClass() {
			return Enum.class;
		}
	},
	
	// Can't be read without a corresponding class loader to load the object class definition
	OBJECT {
		@Override public void write(final ExtendedDataOutput out, final Object value) throws IOException {
			out.writeObject(value);
		}
		
		@Override public Class<?> getValueClass() {
			return Object.class;
		}
	};

	public Object read(final ExtendedDataInput in) throws IOException {
		throw new UnsupportedOperationException("Reading values of this type is not supported through ValueType.");
	}

	public void write(final ExtendedDataOutput out, final Object value) throws IOException {
		throw new UnsupportedOperationException("Writing values of this type is not supported through ValueType.");
	}

	private final boolean proxied;
	
	private ValueType() {
		this(false);
	}
	
	private ValueType(final boolean proxied) {
		this.proxied = proxied;
	}
	
	/**
	 * @return
	 */
	public boolean isProxied() {
		return proxied;
	}

	/**
	 * @return
	 */
	public abstract Class<?> getValueClass();

	/**
	 * Returns the specified {@link Object}'s value type.
	 * 
	 * @param value the object to check (may not be {@code null})
	 * @return the value type of the object
	 */
	public static ValueType fromObject(final Object value) {
		checkNull(value, "value");
		final Class<?> valueClass = value.getClass();
		return fromClass(valueClass);
	}

	/**
	 * 
	 * @param valueClass
	 * @return
	 */
	public static ValueType fromClass(final Class<?> valueClass) {
		checkNull(valueClass, "valueClass");
		for (final ValueType candidate : values()) {
			if (candidate.getValueClass().isAssignableFrom(valueClass)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException("Object type should always match when iterating over possible value types. Parameter was: " + valueClass);
	}
}