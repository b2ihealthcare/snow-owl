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
package org.easyb.junit;

import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RunWith(EasybJUnitRunner.class)
public abstract class EasybSuite {

	private final Set<String> behaviorFilenameExtensions = new HashSet<String>();

	protected EasybSuite() {
		behaviorFilenameExtensions.add("story");
		behaviorFilenameExtensions.add("behavior");
	}

	protected File baseDir() {
		return new File("spec");
	}

	protected boolean generateReports(){
       return false;
    }

    protected File reportsDir(){
       return new File("reports");
    }

   protected File searchDir() {
      String path = getClass().getName();
      path = path.substring(0, path.lastIndexOf('.'));
      path = path.replace('.', '/');
      return new File(baseDir(), path);
   }

   protected String description() {
      return getClass().getName();
   }

   protected boolean trackTime() {
      return false;
   }

	/**
	 *
	 * @param extension without leading '.'
	 */
	public void addBehaviorFilenameExtension(final String extension) {
		behaviorFilenameExtensions.add(extension);
	}

	protected boolean isBehavior(final String aFilenameExtension) {
		return behaviorFilenameExtensions.contains(aFilenameExtension);
	}
}