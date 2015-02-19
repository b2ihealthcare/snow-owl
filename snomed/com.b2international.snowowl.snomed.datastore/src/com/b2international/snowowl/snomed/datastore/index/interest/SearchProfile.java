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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Represents a search profile, which is a named set of rules describing user
 * preferences of search result ordering and retrieval.
 * 
 */
@XStreamAlias("SearchProfile")
public class SearchProfile extends BeanPropertyChangeSupporter implements Serializable {

	private static final long serialVersionUID = -6405855333411775189L;
	
	public static final String PROP_NAME = "name";
	public static final String PROP_RULES = "rules";
	public static final String PROP_MUTABLE = "mutable";
	public static final String PROP_DIRTY = "dirty";

	private static void deepCopyRules(final Iterable<SearchProfileRule> fromRules, final LinkedHashSet<SearchProfileRule> toRules) {
		for (final SearchProfileRule fromRule : fromRules) {
			toRules.add(new SearchProfileRule(fromRule));
		}
	}
	
	private static Iterable<SearchProfileRule> getPreferencesRules(final Preferences profileNode) throws BackingStoreException {
		checkNotNull(profileNode, "profileNode");
		
		final ImmutableSet.Builder<SearchProfileRule> preferencesRulesBuilder = ImmutableSet.builder();
		
		for (final String childName : profileNode.childrenNames()) {
			preferencesRulesBuilder.add(new SearchProfileRule(profileNode.node(childName)));
		}
		
		return preferencesRulesBuilder.build();
	}

	private final String name;
	
	private final LinkedHashSet<SearchProfileRule> rules = Sets.newLinkedHashSet();
	
	private transient Set<SearchProfileRule> unmodifiableRules = Collections.unmodifiableSet(rules);
	
	private transient boolean mutable = true;
	
	private transient boolean dirty;
	
	public SearchProfile(final String name, final boolean mutable, final Iterable<SearchProfileRule> rules) {
		this.name = checkNotNull(name, "name");
		this.mutable = mutable;
		deepCopyRules(checkNotNull(rules, "rules"), this.rules);
		associateRules();
	}
	
	public SearchProfile(final String copyName, final SearchProfile source) {
		this(checkNotNull(copyName, "copyName"), 
				true, // a copy will always be mutable by definition
				checkNotNull(source, "source").getRules()); 
	}
	
	public SearchProfile(final Preferences profileNode) throws BackingStoreException {
		this(checkNotNull(profileNode, "profileNode").name(), 
				profileNode.getBoolean(PROP_MUTABLE, false),
				getPreferencesRules(profileNode));
	}

	public String getName() {
		return name;
	}
	
	public void addRule(final SearchProfileRule rule) {
		checkNotNull(rule, "rule");
		
		if (rules.add(rule)) {
			rule.setParent(this);
			firePropertyChange(PROP_RULES, null, null);
			setDirty(true);
		}
	}
	
	public void removeRule(final SearchProfileRule rule) {
		checkNotNull(rule, "rule");
		
		if (rules.remove(rule)) {
			rule.setParent(null);
			firePropertyChange(PROP_RULES, null, null);
			setDirty(true);
		}
	}
	
	public Set<SearchProfileRule> getRules() {
		return unmodifiableRules;
	}
	
	public boolean isMutable() {
		return mutable;
	}

	public void setMutable(final boolean mutable) {
		if (this.mutable != mutable) {
			final boolean oldMutable = this.mutable;
			this.mutable = mutable;
			firePropertyChange(PROP_MUTABLE, oldMutable, mutable);
		}
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(final boolean dirty) {
		if (this.dirty != dirty) {
			final boolean oldDirty = this.dirty;
			this.dirty = dirty;
			firePropertyChange(PROP_DIRTY, oldDirty, dirty);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final SearchProfile other = (SearchProfile) obj;
		return name.equals(other.name);
	}
	
	public void save(final Preferences profileParentNode) throws BackingStoreException {
		checkNotNull(profileParentNode, "profileParentNode");
		
		final Preferences profileNode = profileParentNode.node(name);
		
		profileNode.putBoolean(PROP_MUTABLE, mutable);
		
		for (final String child : profileNode.childrenNames()) {
			profileNode.node(child).removeNode();
		}
		
		final Iterator<SearchProfileRule> ruleIterator = rules.iterator();
		
		for (int i = 0; ruleIterator.hasNext(); i++) {
			ruleIterator.next().save(profileNode.node(Integer.toString(i)));
		}
		
		setDirty(false);
	}
	
	// Added to support de-serialization
	@Override
	protected Object readResolve() throws ObjectStreamException {
		super.readResolve();
		mutable = true;
		unmodifiableRules = Collections.unmodifiableSet(rules);
		associateRules();
		return this;
	}
	
	private void associateRules() {
		for (final SearchProfileRule rule : rules) {
			rule.setParent(this);
		}
	}
}