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
package com.b2international.snowowl.semanticengine.normalform;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;


/**
 * Simple class to hold an attribute and its containing group, if it is grouped. 
 * If the attribute is ungrouped, the group will be {@link AttributeNameMatch#NO_GROUP}.
 * 
 */
public final class AttributeNameMatch {
	
	public static final Group NO_GROUP = ScgFactory.eINSTANCE.createGroup();
	
	private Group group;
	private final Attribute attribute;
	
	public AttributeNameMatch(Attribute attribute, Group group) {
		this.attribute = attribute;
		this.group = group;
	}

	public AttributeNameMatch(Attribute attribute) {
		this.attribute = attribute;
		this.group = NO_GROUP;
	}

	/**
	 * @return the name-matched attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @return the group containing the attribute, or {@link AttributeNameMatch#NO_GROUP} if ungrouped
	 */
	public Group getGroup() {
		return group;
	}
	
	@Override
	public String toString() {
		if (group != NO_GROUP)
			return attribute + " in " + group;
		else
			return attribute + " (ungrouped)";
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}