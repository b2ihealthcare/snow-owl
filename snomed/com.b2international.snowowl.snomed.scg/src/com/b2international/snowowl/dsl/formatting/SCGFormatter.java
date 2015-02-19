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
package com.b2international.snowowl.dsl.formatting;

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.b2international.snowowl.dsl.services.SCGGrammarAccess;

/**
 * This class contains custom formatting settings for an SCG expression.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormatter} as an example
 * 
 */
public class SCGFormatter extends AbstractDeclarativeFormatter {
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter#configureFormatting(org.eclipse.xtext.formatting.impl.FormattingConfig)
	 */
	protected void configureFormatting(FormattingConfig cfg) {
		
		SCGGrammarAccess ga =  (SCGGrammarAccess) getGrammarAccess();
		
		//COMMENT
		cfg.setLinewrap(0, 1, 2).before(ga.getSL_COMMENTRule());
		cfg.setLinewrap(0, 1, 2).before(ga.getML_COMMENTRule());
		cfg.setLinewrap(0, 1, 1).after(ga.getML_COMMENTRule());
		
		//no space between pipe, pipe and concept id, space after closing pipe
		cfg.setNoSpace().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());
		cfg.setNoSpace().after(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());
		cfg.setNoSpace().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_4());
		
		//line wrap before and after {} plus indentation
		cfg.setLinewrap().before(ga.getGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0());
		cfg.setLinewrap().after(ga.getGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0());
		cfg.setLinewrap().before(ga.getGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_3());
		cfg.setLinewrap().after(ga.getGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_3());
		cfg.setIndentation(ga.getGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0(), ga.getGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_3());
		
		//line wrap before ,
		cfg.setLinewrap().before(ga.getExpressionAccess().getCOMMATerminalRuleCall_2_1_1_0());
		
		//setnospace after ( and before )
		cfg.setNoSpace().after(ga.getAttributeValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_1_0());
		cfg.setNoSpace().before(ga.getAttributeValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_1_2());
		
		//do not break after |
		cfg.setNoLinewrap().after(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());

		//do not break before |
		cfg.setNoLinewrap().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_4());
	}

}