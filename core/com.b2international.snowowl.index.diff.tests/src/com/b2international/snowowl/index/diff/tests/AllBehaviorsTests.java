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
package com.b2international.snowowl.index.diff.tests;
import org.easyb.junit.EasybSuite;

import java.io.File;

/**
 * Test suite to run all easyb stories.
 *
 */
public class AllBehaviorsTests extends EasybSuite {

	/*
	 * (non-Javadoc)
	 * @see org.easyb.junit.EasybSuite#baseDir()
	 */
    @Override
    protected File baseDir() {
        return new File("src/com/b2international/snowowl/index/diff/tests");
    }

    /*
     * (non-Javadoc)
     * @see org.easyb.junit.EasybSuite#generateReports()
     */
    @Override
    protected boolean generateReports() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.easyb.junit.EasybSuite#searchDir()
     */
    @Override
    protected File searchDir() {
    	return new File("src/com/b2international/snowowl/index/diff/tests");
    }

    protected File withReports() {
        return new File("reports");
    }
}