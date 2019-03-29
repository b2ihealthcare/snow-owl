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
package com.b2international.snowowl.core.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.ecore.EPackage;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**Wrapper for the namespace URI of an {@link EPackage}. <br>This class has an overridden {@link #equals(Object)} and {@link #hashCode()} based on the wrapped
 * namespace URI string.
 *@see #NULL_IMPL*/
public final class NsUri {
	
	/**Function for transforming an {@link NsUri} instance into string.*/
	public static final Function<NsUri, String> TO_STRING_FUNCTION = new Function<NsUri, String>() {
		@Override public String apply(final NsUri nsUri) {
			return checkNotNull(nsUri, "nsUri").getNsUri();
		}
	};
	
	/**Null implementation as a workaround for issue that LoadingCache does not allow returning with {@code null}. Represents nothing.*/
	public static final NsUri NULL_IMPL = new NsUri(NsUri.class.getSimpleName());
	
	private final String nsUri;
	public NsUri(final String nsUri) { this.nsUri = Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null."); }
	
	public String getNsUri() { return nsUri; }
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nsUri == null) ? 0 : nsUri.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NsUri))
			return false;
		final NsUri other = (NsUri) obj;
		if (nsUri == null) {
			if (other.nsUri != null)
				return false;
		} else if (!nsUri.equals(other.nsUri))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("NS URI", nsUri).toString();
	}
	
	/**Returns {@code true} if the given {@link NsUri} instance is either {@code null} or the {@link #NULL_IMPL NULL} instance.*/
	public static boolean isNull(final NsUri nsUri) {
		return null == nsUri || NULL_IMPL.equals(nsUri);
	}
	
}