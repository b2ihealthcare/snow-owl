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
package com.b2international.snowowl.core.claml;

import com.b2international.snowowl.core.claml.ClamlModel.ClamlClass;
import com.b2international.snowowl.core.claml.ClamlModel.ExcludeModifier;
import com.b2international.snowowl.core.claml.ClamlModel.FlatLabel;
import com.b2international.snowowl.core.claml.ClamlModel.ModifiedBy;
import com.b2international.snowowl.core.claml.ClamlModel.Modifier;
import com.b2international.snowowl.core.claml.ClamlModel.ModifierClass;
import com.b2international.snowowl.core.claml.ClamlModel.Rubric;
import com.b2international.snowowl.core.claml.ClamlModel.SubClass;
import com.b2international.snowowl.core.claml.ClamlModel.SuperClass;

/**
 * Empty implementation of the {@link ClamlModelVisitor} interface.
 * Subclasses only need to override methods for the model elements they are interested in.
 * 
 */
public class ClamlModelVisitorAdapter implements ClamlModelVisitor {

	public void visit(ClamlModel clamlModel) {
	}
	
	public void visit(ClamlClass clamlClass) {
	}
	
	public void visit(Modifier modifier) {
	}
	
	public void visit(ModifierClass modifierClass) {
	}
	
	public void visit(ModifiedBy modifiedBy) {
	}
	
	public void visit(ExcludeModifier excludeModifier) {
	}
	
	public void visit(Rubric rubric) {
	}
	
	public void visit(FlatLabel label) {
	}

	public void visit(SubClass subClass) {
	}

	public void visit(SuperClass superClass) {
	}
}