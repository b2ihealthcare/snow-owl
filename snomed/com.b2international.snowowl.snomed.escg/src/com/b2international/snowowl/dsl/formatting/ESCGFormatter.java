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

import com.b2international.snowowl.dsl.services.ESCGGrammarAccess;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class ESCGFormatter extends AbstractDeclarativeFormatter {
	
	@Override
	protected void configureFormatting(FormattingConfig cfg) {

		ESCGGrammarAccess ga =  (ESCGGrammarAccess) getGrammarAccess();
		
		cfg.setAutoLinewrap(100);
		//COMMENT
		cfg.setLinewrap(0, 1, 2).before(ga.getSL_COMMENTRule());
		cfg.setLinewrap(0, 1, 2).before(ga.getML_COMMENTRule());
		cfg.setLinewrap(0, 1, 1).after(ga.getML_COMMENTRule());
		
		//no space between pipe, pipe and concept id, space after closing pipe
		cfg.setNoSpace().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());
		cfg.setNoSpace().after(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());
		cfg.setNoSpace().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_4());
		cfg.setNoSpace().before(ga.getRefSetAccess().getPIPETerminalRuleCall_3_0());
		cfg.setNoSpace().after(ga.getRefSetAccess().getPIPETerminalRuleCall_3_0());
		cfg.setNoSpace().before(ga.getRefSetAccess().getPIPETerminalRuleCall_3_2());

		//line wrap after = + indentation
		cfg.setLinewrap().after(ga.getEQUAL_SIGNRule());
		cfg.setIndentationIncrement().after(ga.getEQUAL_SIGNRule());
		
		//line wrap before AND/OR
		cfg.setLinewrap().before(ga.getOrAccess().getOR_TOKENTerminalRuleCall_1_1());
		cfg.setLinewrap().before(ga.getAndAccess().getAND_TOKENTerminalRuleCall_1_1());
		
		//line wrap before and after {} + indentation
		cfg.setLinewrap().before(ga.getAttributeGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0());
		cfg.setLinewrap().after(ga.getAttributeGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0());
		cfg.setLinewrap().before(ga.getAttributeGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_2());
		cfg.setLinewrap().after(ga.getAttributeGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_2());
		cfg.setIndentation(ga.getAttributeGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0(), ga.getAttributeGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_2());
		
		//line wrap before and after () + indentation
		cfg.setLinewrap().before(ga.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0());
		cfg.setLinewrap().after(ga.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0());
		cfg.setLinewrap().before(ga.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2());
		cfg.setLinewrap().after(ga.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2());
		cfg.setIndentation(ga.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0(), ga.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2());
		
		//setnospace after ^ ! < << ~
		cfg.setNoSpace().after(ga.getConceptGroupAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0());
		cfg.setNoSpace().after(ga.getConceptGroupAccess().getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1());
		cfg.setNoSpace().after(ga.getConceptGroupAccess().getConstraintSUBTYPETerminalRuleCall_1_0_0());
		cfg.setNoSpace().after(ga.getRefSetAccess().getCARETTerminalRuleCall_1());
		cfg.setNoSpace().after(ga.getRefSetAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0());
		cfg.setNoSpace().after(ga.getAttributeAccess().getOptionalOPTIONALTerminalRuleCall_0_0());
		
		//line wrap before ,
		cfg.setLinewrap().before(ga.getAttributeSetAccess().getCOMMATerminalRuleCall_1_0());
		
		//setnospace after ( and before )XXX commented out since, line wrap before and after () + indentation
//		cfg.setNoSpace().after(ga.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0());
//		cfg.setNoSpace().before(ga.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2());
		
		//do not break after the modifiers
		cfg.setNoLinewrap().after(ga.getAttributeAccess().getOptionalOPTIONALTerminalRuleCall_0_0());
		cfg.setNoLinewrap().after(ga.getConceptGroupAccess().getConstraintSUBTYPETerminalRuleCall_1_0_0());
		cfg.setNoLinewrap().after(ga.getConceptGroupAccess().getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1());
		cfg.setNoLinewrap().after(ga.getNegatableSubExpressionAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0());
		cfg.setNoLinewrap().after(ga.getRefSetAccess().getCARETTerminalRuleCall_1());
		
		//do not break after |
		cfg.setNoLinewrap().after(ga.getConceptAccess().getPIPETerminalRuleCall_1_0());

		//do not break before |
		cfg.setNoLinewrap().before(ga.getConceptAccess().getPIPETerminalRuleCall_1_4());
		
	}
}