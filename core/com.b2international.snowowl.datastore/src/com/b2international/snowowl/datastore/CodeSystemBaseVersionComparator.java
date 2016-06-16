/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import java.util.Comparator;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;

/**
 * Comparator for ordering {@link ICodeSystem}s based on their base version,
 * where {@link ICodeSystem} with {@link IBranchPath} {@link IBranchPath#MAIN_BRANCH MAIN} (if present) is considered the biggest element and {@link ICodeSystem} based on the oldest {@link ICodeSystemVersion#getEffectiveDate()} is the smallest element.
 * 
 */
public class CodeSystemBaseVersionComparator implements Comparator<ICodeSystem> {
	
	
	@Override
	public int compare(ICodeSystem o1, ICodeSystem o2) {
		//Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		
		
		if (o1.getBranchPath().equals(o2.getBranchPath())) {
			return 0;
		} else if (BranchPathUtils.isMain(o1.getBranchPath())) {
			//firts one is MAIN thus it is the greater one.
			return 1; 
		} else if (BranchPathUtils.isMain(o2.getBranchPath())) {
			return -1;
		} else {
			
			// extension's parent should be always a date: MAIN/2016-01-31/SNOMEDCT-XY
			IBranchPath o1Base = BranchPathUtils.createPath(o1.getBranchPath()).getParent(); 
			IBranchPath o2Base = BranchPathUtils.createPath(o2.getBranchPath()).getParent();
			
			try {
				return Dates.parse(o1Base.lastSegment()).compareTo(Dates.parse(o2Base.lastSegment()));
			} catch (SnowowlRuntimeException e){
				return 0;
			}
		}
	}

}
