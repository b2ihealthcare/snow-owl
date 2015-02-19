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

import java.awt.Label;

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
 * Visitor interface for traversing the nodes of a {@link ClamlModel ClaML model}.
 * 
 */
public interface ClamlModelVisitor {

	/**
	 * Visit the {@link ClamlModel}.
	 * @param clamlModel
	 */
	public void visit(ClamlModel clamlModel);

	/**
	 * Visit the {@link ClamlClass}.
	 * @param clamlClass
	 */
	public void visit(ClamlClass clamlClass);
	
	/**
	 * Visit the {@link Modifier}.
	 * @param modifier
	 */
	public void visit(Modifier modifier);
	
	/**
	 * Visit the {@link ModifierClass}.
	 * @param modifierClass
	 */
	public void visit(ModifierClass modifierClass);
	
	/**
	 * Visit the {@link ModifiedBy}.
	 * @param modifiedBy
	 */
	public void visit(ModifiedBy modifiedBy);
	
	/**
	 * Visit the {@link ExcludeModifier}.
	 * @param excludeModifier
	 */
	public void visit(ExcludeModifier excludeModifier);
	
	/**
	 * Visit the {@link Rubric}.
	 * @param rubric
	 */
	public void visit(Rubric rubric);
	
	/**
	 * Visit the {@link Label}.
	 * @param label
	 */
	public void visit(FlatLabel label);

	/**
	 * Visit the {@link SubClass}.
	 * @param subClass
	 */
	public void visit(SubClass subClass);

	/**
	 * Visit the {@link SuperClass}.
	 * @param superClass
	 */
	public void visit(SuperClass superClass);
}