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
package com.b2international.snowowl.snomed.datastore.index.interest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;
import com.google.common.base.Objects;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Represents a single rule in a search profile.
 *
 */
@XStreamAlias("SearchProfileRule")
public class SearchProfileRule extends BeanPropertyChangeSupporter implements Serializable {

	private static final long serialVersionUID = -4970630361761869390L;
	
	public static final String PROP_DOMAIN = "domain";
	public static final String PROP_CONTEXT_ID = "contextId";
	public static final String PROP_INTEREST = "interest";
	
	private final SearchProfileDomain domain;
	private final String contextId;
	private SearchProfileInterest interest;
	
	private transient SearchProfile parent;
	
	public SearchProfileRule(final SearchProfileDomain domain, final String contextId, final SearchProfileInterest interest) {
		this.domain = checkNotNull(domain, "domain");
		this.contextId = checkNotNull(contextId, "contextId");
		this.interest = checkNotNull(interest, "interest");
	}
	
	public SearchProfileRule(final SearchProfileRule source) {
		this(checkNotNull(source, "source").getDomain(), source.getContextId(), source.getInterest());
	}
	
	public SearchProfileRule(final Preferences ruleNode) {
		this(SearchProfileDomain.valueOf(checkNotNull(ruleNode, "ruleNode").get(PROP_DOMAIN, null)),
			 ruleNode.get(PROP_CONTEXT_ID, null),
			 SearchProfileInterest.valueOf(ruleNode.get(PROP_INTEREST, null)));
	}
	
	public SearchProfileDomain getDomain() {
		return domain;
	}
	
	public String getContextId() {
		return contextId;
	}
	
	public SearchProfileInterest getInterest() {
		return interest;
	}
	
	public void setInterest(final SearchProfileInterest interest) {
		final SearchProfileInterest oldInterest = this.interest;
		this.interest = checkNotNull(interest, "interest");
		firePropertyChange(PROP_INTEREST, oldInterest, interest);
		
		if (null != parent && !oldInterest.equals(interest)) {
			parent.setDirty(true);
		}
	}
	
	public void setParent(final SearchProfile parent) {
		this.parent = parent;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(contextId, domain);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (!semanticEquals(obj)) {
			return false;
		}
		
		SearchProfileRule other = (SearchProfileRule) obj;
		
		if (parent == null) {
			
			if (other.parent != null) {
				return false;
			}
			
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		
		return true;
	}

	public boolean semanticEquals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		SearchProfileRule other = (SearchProfileRule) obj;
		
		if (contextId == null) {
			
			if (other.contextId != null) {
				return false;
			}
			
		} else if (!contextId.equals(other.contextId)) {
			return false;
		}
		
		if (domain != other.domain) {
			return false;
		}
		
		return true;
	}

	public void save(final Preferences ruleNode) throws BackingStoreException {
		
		checkNotNull(ruleNode, "ruleNode");

		ruleNode.clear();
		ruleNode.put(PROP_DOMAIN, domain.name());
		ruleNode.put(PROP_CONTEXT_ID, contextId);
		ruleNode.put(PROP_INTEREST, interest.name());
	}
	
	// Added to support de-serialization
	@Override
	protected Object readResolve() throws ObjectStreamException {
		super.readResolve();
		return this;
	}
}