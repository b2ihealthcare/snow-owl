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
package com.b2international.snowowl.internal.eventbus.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @since 3.1
 */
public abstract class OM {
	
	public static final String BUNDLE_ID = "com.b2international.snowowl.eventbus";

	public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

	public static final OMLogger LOG = BUNDLE.logger();

	/**
	 * The {@link OSGiActivator OSGi bundle activator subclass} for this bundle. 
	 *
	 * @since 3.1
	 */
	public static final class Activator extends OSGiActivator {
		
		public Activator() {
			super(BUNDLE);
		}
		
		@Override
		protected void doStart() throws Exception {
		}
		
		@Override
		protected void doStop() throws Exception {
		}
		
	}
}