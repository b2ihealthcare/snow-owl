/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests.dsl


/**
 * Injects domain specific language into the given target class.
 *
 */
class DslInjector {

	def injectDslInto(final target) {
		def classLoader = new GroovyClassLoader(target.class.classLoader)
		new File('.').eachFileRecurse { file ->
			if ("MockIndexServiceDsl.groovy" == file.name) {
				classLoader.parseClass(file).init()
			}
		}
	}
		
}


