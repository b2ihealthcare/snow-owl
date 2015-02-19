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
package com.b2international.snowowl.datastore.cdo


/**
 * CDO query utility class extension to support missing CDO query execution feature with CDO server views
 * on the server-side.
 */
class CDOQueryUtilsExtension {

	static def getResults(def Object cdoQuery, def Object query) {
		
		def view = cdoQuery.view
		
		def session = view.session
		def repository = session.repository
		def queryManager = repository.queryManager

		def serverSession = session.internalSession
		def serverView = serverSession.getView(view.getViewID())
		def result = queryManager.execute(serverView, query.getQueryInfo())
		def queryId = result.getQueryID()
		
		query.setQueryID(queryId)
		
		def resultQueue = query.getQueue()
		
		try {
			
			while (result.hasNext()) {
				
				def object = result.next()
				
				if (null != object) {
					resultQueue.add(object)
				}
				
			}
			
		} catch (final Throwable t) {
			
			resultQueue.setException(new RuntimeException(t.message, t))
		
		} finally {
		
			resultQueue.close()
		
		}
		
	}
	
}
