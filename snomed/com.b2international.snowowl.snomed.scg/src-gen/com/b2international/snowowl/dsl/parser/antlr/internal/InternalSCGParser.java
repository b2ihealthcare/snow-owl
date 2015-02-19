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
package com.b2international.snowowl.dsl.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.dsl.services.SCGGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalSCGParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_PLUS_SIGN", "RULE_COLON", "RULE_COMMA", "RULE_PIPE", "RULE_WS", "RULE_OPENING_CURLY_BRACKET", "RULE_CLOSING_CURLY_BRACKET", "RULE_EQUAL_SIGN", "RULE_OPENING_ROUND_BRACKET", "RULE_CLOSING_ROUND_BRACKET", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_LETTER", "RULE_OTHER_ALLOWED_TERM_CHARACTER", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_ZERO=15;
    public static final int RULE_OPENING_CURLY_BRACKET=9;
    public static final int RULE_EQUAL_SIGN=11;
    public static final int RULE_COMMA=6;
    public static final int RULE_DIGIT_NONZERO=14;
    public static final int RULE_OPENING_ROUND_BRACKET=12;
    public static final int RULE_OTHER_ALLOWED_TERM_CHARACTER=17;
    public static final int EOF=-1;
    public static final int RULE_SL_COMMENT=19;
    public static final int RULE_LETTER=16;
    public static final int RULE_ML_COMMENT=18;
    public static final int RULE_CLOSING_CURLY_BRACKET=10;
    public static final int RULE_COLON=5;
    public static final int RULE_CLOSING_ROUND_BRACKET=13;
    public static final int RULE_PIPE=7;
    public static final int RULE_PLUS_SIGN=4;
    public static final int RULE_WS=8;

    // delegates
    // delegators


        public InternalSCGParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalSCGParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalSCGParser.tokenNames; }
    public String getGrammarFileName() { return "../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */
     
     	private SCGGrammarAccess grammarAccess;
     	
        public InternalSCGParser(TokenStream input, SCGGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "Expression";	
       	}
       	
       	@Override
       	protected SCGGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleExpression"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:73:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
        	
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:77:2: (iv_ruleExpression= ruleExpression EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:78:2: iv_ruleExpression= ruleExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleExpression_in_entryRuleExpression87);
            iv_ruleExpression=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleExpression97); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleExpression"


    // $ANTLR start "ruleExpression"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:88:1: ruleExpression returns [EObject current=null] : ( ( (lv_concepts_0_0= ruleConcept ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )* (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )? ) ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        Token this_PLUS_SIGN_1=null;
        Token this_COLON_3=null;
        Token this_COMMA_5=null;
        EObject lv_concepts_0_0 = null;

        EObject lv_concepts_2_0 = null;

        EObject lv_attributes_4_0 = null;

        EObject lv_attributes_6_0 = null;

        EObject lv_groups_7_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:92:28: ( ( ( (lv_concepts_0_0= ruleConcept ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )* (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )? ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:93:1: ( ( (lv_concepts_0_0= ruleConcept ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )* (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )? )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:93:1: ( ( (lv_concepts_0_0= ruleConcept ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )* (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )? )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:93:2: ( (lv_concepts_0_0= ruleConcept ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )* (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )?
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:93:2: ( (lv_concepts_0_0= ruleConcept ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:94:1: (lv_concepts_0_0= ruleConcept )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:94:1: (lv_concepts_0_0= ruleConcept )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:95:3: lv_concepts_0_0= ruleConcept
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getExpressionAccess().getConceptsConceptParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConcept_in_ruleExpression147);
            lv_concepts_0_0=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getExpressionRule());
              	        }
                     		add(
                     			current, 
                     			"concepts",
                      		lv_concepts_0_0, 
                      		"Concept");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:111:2: (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==RULE_PLUS_SIGN) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:111:3: this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_concepts_2_0= ruleConcept ) )
            	    {
            	    this_PLUS_SIGN_1=(Token)match(input,RULE_PLUS_SIGN,FOLLOW_RULE_PLUS_SIGN_in_ruleExpression159); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_PLUS_SIGN_1, grammarAccess.getExpressionAccess().getPLUS_SIGNTerminalRuleCall_1_0()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:115:1: ( (lv_concepts_2_0= ruleConcept ) )
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:116:1: (lv_concepts_2_0= ruleConcept )
            	    {
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:116:1: (lv_concepts_2_0= ruleConcept )
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:117:3: lv_concepts_2_0= ruleConcept
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getExpressionAccess().getConceptsConceptParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleConcept_in_ruleExpression179);
            	    lv_concepts_2_0=ruleConcept();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getExpressionRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"concepts",
            	              		lv_concepts_2_0, 
            	              		"Concept");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:133:4: (this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )* )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_COLON) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:133:5: this_COLON_3= RULE_COLON ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )? ( (lv_groups_7_0= ruleGroup ) )*
                    {
                    this_COLON_3=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleExpression193); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COLON_3, grammarAccess.getExpressionAccess().getCOLONTerminalRuleCall_2_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:137:1: ( ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )* )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==RULE_DIGIT_NONZERO) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:137:2: ( (lv_attributes_4_0= ruleAttribute ) ) (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )*
                            {
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:137:2: ( (lv_attributes_4_0= ruleAttribute ) )
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:138:1: (lv_attributes_4_0= ruleAttribute )
                            {
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:138:1: (lv_attributes_4_0= ruleAttribute )
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:139:3: lv_attributes_4_0= ruleAttribute
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getExpressionAccess().getAttributesAttributeParserRuleCall_2_1_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleAttribute_in_ruleExpression214);
                            lv_attributes_4_0=ruleAttribute();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getExpressionRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"attributes",
                                      		lv_attributes_4_0, 
                                      		"Attribute");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:155:2: (this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) ) )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==RULE_COMMA) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:155:3: this_COMMA_5= RULE_COMMA ( (lv_attributes_6_0= ruleAttribute ) )
                            	    {
                            	    this_COMMA_5=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleExpression226); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {
                            	       
                            	          newLeafNode(this_COMMA_5, grammarAccess.getExpressionAccess().getCOMMATerminalRuleCall_2_1_1_0()); 
                            	          
                            	    }
                            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:159:1: ( (lv_attributes_6_0= ruleAttribute ) )
                            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:160:1: (lv_attributes_6_0= ruleAttribute )
                            	    {
                            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:160:1: (lv_attributes_6_0= ruleAttribute )
                            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:161:3: lv_attributes_6_0= ruleAttribute
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getExpressionAccess().getAttributesAttributeParserRuleCall_2_1_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FOLLOW_ruleAttribute_in_ruleExpression246);
                            	    lv_attributes_6_0=ruleAttribute();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getExpressionRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"attributes",
                            	              		lv_attributes_6_0, 
                            	              		"Attribute");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop2;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:177:6: ( (lv_groups_7_0= ruleGroup ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==RULE_OPENING_CURLY_BRACKET) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:178:1: (lv_groups_7_0= ruleGroup )
                    	    {
                    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:178:1: (lv_groups_7_0= ruleGroup )
                    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:179:3: lv_groups_7_0= ruleGroup
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getExpressionAccess().getGroupsGroupParserRuleCall_2_2_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FOLLOW_ruleGroup_in_ruleExpression271);
                    	    lv_groups_7_0=ruleGroup();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getExpressionRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"groups",
                    	              		lv_groups_7_0, 
                    	              		"Group");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleExpression"


    // $ANTLR start "entryRuleConcept"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:206:1: entryRuleConcept returns [EObject current=null] : iv_ruleConcept= ruleConcept EOF ;
    public final EObject entryRuleConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConcept = null;


        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:207:2: (iv_ruleConcept= ruleConcept EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:208:2: iv_ruleConcept= ruleConcept EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptRule()); 
            }
            pushFollow(FOLLOW_ruleConcept_in_entryRuleConcept314);
            iv_ruleConcept=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConcept; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConcept324); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConcept"


    // $ANTLR start "ruleConcept"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:215:1: ruleConcept returns [EObject current=null] : ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? ) ;
    public final EObject ruleConcept() throws RecognitionException {
        EObject current = null;

        Token this_PIPE_1=null;
        Token this_WS_2=null;
        Token this_WS_4=null;
        Token this_PIPE_5=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;

        AntlrDatatypeRuleToken lv_term_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:218:28: ( ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:219:1: ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:219:1: ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:219:2: ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:219:2: ( (lv_id_0_0= ruleConceptId ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:220:1: (lv_id_0_0= ruleConceptId )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:220:1: (lv_id_0_0= ruleConceptId )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:221:3: lv_id_0_0= ruleConceptId
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConceptAccess().getIdConceptIdParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConceptId_in_ruleConcept370);
            lv_id_0_0=ruleConceptId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConceptRule());
              	        }
                     		set(
                     			current, 
                     			"id",
                      		lv_id_0_0, 
                      		"ConceptId");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:237:2: (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_PIPE) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:237:3: this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConcept382); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PIPE_1, grammarAccess.getConceptAccess().getPIPETerminalRuleCall_1_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:241:1: (this_WS_2= RULE_WS )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==RULE_WS) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:241:2: this_WS_2= RULE_WS
                    	    {
                    	    this_WS_2=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleConcept393); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_WS_2, grammarAccess.getConceptAccess().getWSTerminalRuleCall_1_1()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:245:3: ( (lv_term_3_0= ruleTerm ) )
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:246:1: (lv_term_3_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:246:1: (lv_term_3_0= ruleTerm )
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:247:3: lv_term_3_0= ruleTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getConceptAccess().getTermTermParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleTerm_in_ruleConcept415);
                    lv_term_3_0=ruleTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getConceptRule());
                      	        }
                             		set(
                             			current, 
                             			"term",
                              		lv_term_3_0, 
                              		"Term");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:263:2: (this_WS_4= RULE_WS )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==RULE_WS) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:263:3: this_WS_4= RULE_WS
                    	    {
                    	    this_WS_4=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleConcept427); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_WS_4, grammarAccess.getConceptAccess().getWSTerminalRuleCall_1_3()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    this_PIPE_5=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConcept439); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PIPE_5, grammarAccess.getConceptAccess().getPIPETerminalRuleCall_1_4()); 
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConcept"


    // $ANTLR start "entryRuleGroup"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:279:1: entryRuleGroup returns [EObject current=null] : iv_ruleGroup= ruleGroup EOF ;
    public final EObject entryRuleGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGroup = null;


        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:280:2: (iv_ruleGroup= ruleGroup EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:281:2: iv_ruleGroup= ruleGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGroupRule()); 
            }
            pushFollow(FOLLOW_ruleGroup_in_entryRuleGroup476);
            iv_ruleGroup=ruleGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGroup; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGroup486); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGroup"


    // $ANTLR start "ruleGroup"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:288:1: ruleGroup returns [EObject current=null] : (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CLOSING_CURLY_BRACKET_4= RULE_CLOSING_CURLY_BRACKET ) ;
    public final EObject ruleGroup() throws RecognitionException {
        EObject current = null;

        Token this_OPENING_CURLY_BRACKET_0=null;
        Token this_COMMA_2=null;
        Token this_CLOSING_CURLY_BRACKET_4=null;
        EObject lv_attributes_1_0 = null;

        EObject lv_attributes_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:291:28: ( (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CLOSING_CURLY_BRACKET_4= RULE_CLOSING_CURLY_BRACKET ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:292:1: (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CLOSING_CURLY_BRACKET_4= RULE_CLOSING_CURLY_BRACKET )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:292:1: (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CLOSING_CURLY_BRACKET_4= RULE_CLOSING_CURLY_BRACKET )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:292:2: this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CLOSING_CURLY_BRACKET_4= RULE_CLOSING_CURLY_BRACKET
            {
            this_OPENING_CURLY_BRACKET_0=(Token)match(input,RULE_OPENING_CURLY_BRACKET,FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleGroup522); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_OPENING_CURLY_BRACKET_0, grammarAccess.getGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0()); 
                  
            }
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:296:1: ( (lv_attributes_1_0= ruleAttribute ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:297:1: (lv_attributes_1_0= ruleAttribute )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:297:1: (lv_attributes_1_0= ruleAttribute )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:298:3: lv_attributes_1_0= ruleAttribute
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getGroupAccess().getAttributesAttributeParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAttribute_in_ruleGroup542);
            lv_attributes_1_0=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getGroupRule());
              	        }
                     		add(
                     			current, 
                     			"attributes",
                      		lv_attributes_1_0, 
                      		"Attribute");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:314:2: (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:314:3: this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) )
            	    {
            	    this_COMMA_2=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleGroup554); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_COMMA_2, grammarAccess.getGroupAccess().getCOMMATerminalRuleCall_2_0()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:318:1: ( (lv_attributes_3_0= ruleAttribute ) )
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:319:1: (lv_attributes_3_0= ruleAttribute )
            	    {
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:319:1: (lv_attributes_3_0= ruleAttribute )
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:320:3: lv_attributes_3_0= ruleAttribute
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGroupAccess().getAttributesAttributeParserRuleCall_2_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttribute_in_ruleGroup574);
            	    lv_attributes_3_0=ruleAttribute();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGroupRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_3_0, 
            	              		"Attribute");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            this_CLOSING_CURLY_BRACKET_4=(Token)match(input,RULE_CLOSING_CURLY_BRACKET,FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleGroup587); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_CLOSING_CURLY_BRACKET_4, grammarAccess.getGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_3()); 
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGroup"


    // $ANTLR start "entryRuleAttribute"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:348:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:349:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:350:2: iv_ruleAttribute= ruleAttribute EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeRule()); 
            }
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute622);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttribute; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute632); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttribute"


    // $ANTLR start "ruleAttribute"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:357:1: ruleAttribute returns [EObject current=null] : ( ( (lv_name_0_0= ruleConcept ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleAttributeValue ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_SIGN_1=null;
        EObject lv_name_0_0 = null;

        EObject lv_value_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:360:28: ( ( ( (lv_name_0_0= ruleConcept ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleAttributeValue ) ) ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:361:1: ( ( (lv_name_0_0= ruleConcept ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleAttributeValue ) ) )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:361:1: ( ( (lv_name_0_0= ruleConcept ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleAttributeValue ) ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:361:2: ( (lv_name_0_0= ruleConcept ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleAttributeValue ) )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:361:2: ( (lv_name_0_0= ruleConcept ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:362:1: (lv_name_0_0= ruleConcept )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:362:1: (lv_name_0_0= ruleConcept )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:363:3: lv_name_0_0= ruleConcept
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAttributeAccess().getNameConceptParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConcept_in_ruleAttribute678);
            lv_name_0_0=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAttributeRule());
              	        }
                     		set(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"Concept");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_EQUAL_SIGN_1=(Token)match(input,RULE_EQUAL_SIGN,FOLLOW_RULE_EQUAL_SIGN_in_ruleAttribute689); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_EQUAL_SIGN_1, grammarAccess.getAttributeAccess().getEQUAL_SIGNTerminalRuleCall_1()); 
                  
            }
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:383:1: ( (lv_value_2_0= ruleAttributeValue ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:384:1: (lv_value_2_0= ruleAttributeValue )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:384:1: (lv_value_2_0= ruleAttributeValue )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:385:3: lv_value_2_0= ruleAttributeValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAttributeValue_in_ruleAttribute709);
            lv_value_2_0=ruleAttributeValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAttributeRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_2_0, 
                      		"AttributeValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleAttributeValue"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:409:1: entryRuleAttributeValue returns [EObject current=null] : iv_ruleAttributeValue= ruleAttributeValue EOF ;
    public final EObject entryRuleAttributeValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValue = null;


        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:410:2: (iv_ruleAttributeValue= ruleAttributeValue EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:411:2: iv_ruleAttributeValue= ruleAttributeValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeValueRule()); 
            }
            pushFollow(FOLLOW_ruleAttributeValue_in_entryRuleAttributeValue745);
            iv_ruleAttributeValue=ruleAttributeValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeValue; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeValue755); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttributeValue"


    // $ANTLR start "ruleAttributeValue"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:418:1: ruleAttributeValue returns [EObject current=null] : (this_Concept_0= ruleConcept | (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) ) ;
    public final EObject ruleAttributeValue() throws RecognitionException {
        EObject current = null;

        Token this_OPENING_ROUND_BRACKET_1=null;
        Token this_CLOSING_ROUND_BRACKET_3=null;
        EObject this_Concept_0 = null;

        EObject this_Expression_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:421:28: ( (this_Concept_0= ruleConcept | (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:422:1: (this_Concept_0= ruleConcept | (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:422:1: (this_Concept_0= ruleConcept | (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_DIGIT_NONZERO) ) {
                alt10=1;
            }
            else if ( (LA10_0==RULE_OPENING_ROUND_BRACKET) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:423:2: this_Concept_0= ruleConcept
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAttributeValueAccess().getConceptParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleConcept_in_ruleAttributeValue805);
                    this_Concept_0=ruleConcept();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Concept_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:435:6: (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET )
                    {
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:435:6: (this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET )
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:435:7: this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET this_Expression_2= ruleExpression this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET
                    {
                    this_OPENING_ROUND_BRACKET_1=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleAttributeValue822); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_ROUND_BRACKET_1, grammarAccess.getAttributeValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_1_0()); 
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAttributeValueAccess().getExpressionParserRuleCall_1_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleExpression_in_ruleAttributeValue846);
                    this_Expression_2=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Expression_2; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    this_CLOSING_ROUND_BRACKET_3=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleAttributeValue856); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_ROUND_BRACKET_3, grammarAccess.getAttributeValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_1_2()); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttributeValue"


    // $ANTLR start "entryRuleTerm"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:463:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:467:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:468:2: iv_ruleTerm= ruleTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTermRule()); 
            }
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm899);
            iv_ruleTerm=ruleTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerm.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm910); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleTerm"


    // $ANTLR start "ruleTerm"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:478:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:482:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:483:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:483:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:483:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:483:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=RULE_PLUS_SIGN && LA11_0<=RULE_COMMA)||(LA11_0>=RULE_OPENING_CURLY_BRACKET && LA11_0<=RULE_OTHER_ALLOWED_TERM_CHARACTER)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:484:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	              newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	          
            	    }
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm962);
            	    this_TermCharacter_0=ruleTermCharacter();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_TermCharacter_0);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	              afterParserOrEnumRuleCall();
            	          
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:494:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop14:
            do {
                int alt14=2;
                alt14 = dfa14.predict(input);
                switch (alt14) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:494:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:494:4: (this_WS_1= RULE_WS )+
            	    int cnt12=0;
            	    loop12:
            	    do {
            	        int alt12=2;
            	        int LA12_0 = input.LA(1);

            	        if ( (LA12_0==RULE_WS) ) {
            	            alt12=1;
            	        }


            	        switch (alt12) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:494:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm986); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      		current.merge(this_WS_1);
            	    	          
            	    	    }
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	          newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	          
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt12 >= 1 ) break loop12;
            	    	    if (state.backtracking>0) {state.failed=true; return current;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(12, input);
            	                throw eee;
            	        }
            	        cnt12++;
            	    } while (true);

            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:501:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt13=0;
            	    loop13:
            	    do {
            	        int alt13=2;
            	        int LA13_0 = input.LA(1);

            	        if ( ((LA13_0>=RULE_PLUS_SIGN && LA13_0<=RULE_COMMA)||(LA13_0>=RULE_OPENING_CURLY_BRACKET && LA13_0<=RULE_OTHER_ALLOWED_TERM_CHARACTER)) ) {
            	            alt13=1;
            	        }


            	        switch (alt13) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:502:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	              newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	          
            	    	    }
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm1016);
            	    	    this_TermCharacter_2=ruleTermCharacter();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      		current.merge(this_TermCharacter_2);
            	    	          
            	    	    }
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	              afterParserOrEnumRuleCall();
            	    	          
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt13 >= 1 ) break loop13;
            	    	    if (state.backtracking>0) {state.failed=true; return current;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(13, input);
            	                throw eee;
            	        }
            	        cnt13++;
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleTerm"


    // $ANTLR start "entryRuleConceptId"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:523:1: entryRuleConceptId returns [String current=null] : iv_ruleConceptId= ruleConceptId EOF ;
    public final String entryRuleConceptId() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleConceptId = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:527:2: (iv_ruleConceptId= ruleConceptId EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:528:2: iv_ruleConceptId= ruleConceptId EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptIdRule()); 
            }
            pushFollow(FOLLOW_ruleConceptId_in_entryRuleConceptId1076);
            iv_ruleConceptId=ruleConceptId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptId.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptId1087); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleConceptId"


    // $ANTLR start "ruleConceptId"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:538:1: ruleConceptId returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
    public final AntlrDatatypeRuleToken ruleConceptId() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DIGIT_NONZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_ZERO_2=null;
        Token this_DIGIT_NONZERO_3=null;
        Token this_ZERO_4=null;
        Token this_DIGIT_NONZERO_5=null;
        Token this_ZERO_6=null;
        Token this_DIGIT_NONZERO_7=null;
        Token this_ZERO_8=null;
        Token this_DIGIT_NONZERO_9=null;
        Token this_ZERO_10=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:542:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:543:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:543:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:543:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1131); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_DIGIT_NONZERO_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                  
            }
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:550:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_DIGIT_NONZERO) ) {
                alt15=1;
            }
            else if ( (LA15_0==RULE_ZERO) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:550:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1152); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:558:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId1178); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_2, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_1_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:565:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_DIGIT_NONZERO) ) {
                alt16=1;
            }
            else if ( (LA16_0==RULE_ZERO) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:565:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1200); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_3);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:573:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId1226); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_4, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_2_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:580:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==RULE_DIGIT_NONZERO) ) {
                alt17=1;
            }
            else if ( (LA17_0==RULE_ZERO) ) {
                alt17=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:580:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1248); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_5);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:588:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId1274); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_6);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_6, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_3_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:595:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_DIGIT_NONZERO) ) {
                alt18=1;
            }
            else if ( (LA18_0==RULE_ZERO) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:595:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1296); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_7);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:603:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId1322); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_8);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_8, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_4_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:610:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt19=0;
            loop19:
            do {
                int alt19=3;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==RULE_DIGIT_NONZERO) ) {
                    alt19=1;
                }
                else if ( (LA19_0==RULE_ZERO) ) {
                    alt19=2;
                }


                switch (alt19) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:610:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1344); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_DIGIT_NONZERO_9);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	          
            	    }

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:618:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId1370); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_ZERO_10);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_ZERO_10, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_5_1()); 
            	          
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleConceptId"


    // $ANTLR start "entryRuleTermCharacter"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:636:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:640:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:641:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTermCharacterRule()); 
            }
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter1428);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTermCharacter.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter1439); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRuleTermCharacter"


    // $ANTLR start "ruleTermCharacter"
    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:651:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_COMMA_3= RULE_COMMA | this_OPENING_CURLY_BRACKET_4= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_5= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_6= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_7= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_8= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_9= RULE_PLUS_SIGN | this_COLON_10= RULE_COLON | this_OTHER_ALLOWED_TERM_CHARACTER_11= RULE_OTHER_ALLOWED_TERM_CHARACTER ) ;
    public final AntlrDatatypeRuleToken ruleTermCharacter() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DIGIT_NONZERO_0=null;
        Token this_ZERO_1=null;
        Token this_LETTER_2=null;
        Token this_COMMA_3=null;
        Token this_OPENING_CURLY_BRACKET_4=null;
        Token this_CLOSING_CURLY_BRACKET_5=null;
        Token this_EQUAL_SIGN_6=null;
        Token this_OPENING_ROUND_BRACKET_7=null;
        Token this_CLOSING_ROUND_BRACKET_8=null;
        Token this_PLUS_SIGN_9=null;
        Token this_COLON_10=null;
        Token this_OTHER_ALLOWED_TERM_CHARACTER_11=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:655:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_COMMA_3= RULE_COMMA | this_OPENING_CURLY_BRACKET_4= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_5= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_6= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_7= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_8= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_9= RULE_PLUS_SIGN | this_COLON_10= RULE_COLON | this_OTHER_ALLOWED_TERM_CHARACTER_11= RULE_OTHER_ALLOWED_TERM_CHARACTER ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:656:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_COMMA_3= RULE_COMMA | this_OPENING_CURLY_BRACKET_4= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_5= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_6= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_7= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_8= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_9= RULE_PLUS_SIGN | this_COLON_10= RULE_COLON | this_OTHER_ALLOWED_TERM_CHARACTER_11= RULE_OTHER_ALLOWED_TERM_CHARACTER )
            {
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:656:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_COMMA_3= RULE_COMMA | this_OPENING_CURLY_BRACKET_4= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_5= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_6= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_7= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_8= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_9= RULE_PLUS_SIGN | this_COLON_10= RULE_COLON | this_OTHER_ALLOWED_TERM_CHARACTER_11= RULE_OTHER_ALLOWED_TERM_CHARACTER )
            int alt20=12;
            switch ( input.LA(1) ) {
            case RULE_DIGIT_NONZERO:
                {
                alt20=1;
                }
                break;
            case RULE_ZERO:
                {
                alt20=2;
                }
                break;
            case RULE_LETTER:
                {
                alt20=3;
                }
                break;
            case RULE_COMMA:
                {
                alt20=4;
                }
                break;
            case RULE_OPENING_CURLY_BRACKET:
                {
                alt20=5;
                }
                break;
            case RULE_CLOSING_CURLY_BRACKET:
                {
                alt20=6;
                }
                break;
            case RULE_EQUAL_SIGN:
                {
                alt20=7;
                }
                break;
            case RULE_OPENING_ROUND_BRACKET:
                {
                alt20=8;
                }
                break;
            case RULE_CLOSING_ROUND_BRACKET:
                {
                alt20=9;
                }
                break;
            case RULE_PLUS_SIGN:
                {
                alt20=10;
                }
                break;
            case RULE_COLON:
                {
                alt20=11;
                }
                break;
            case RULE_OTHER_ALLOWED_TERM_CHARACTER:
                {
                alt20=12;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:656:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1483); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_0);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:664:10: this_ZERO_1= RULE_ZERO
                    {
                    this_ZERO_1=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter1509); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_1, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:672:10: this_LETTER_2= RULE_LETTER
                    {
                    this_LETTER_2=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter1535); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_LETTER_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_LETTER_2, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:680:10: this_COMMA_3= RULE_COMMA
                    {
                    this_COMMA_3=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter1561); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_COMMA_3);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COMMA_3, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:688:10: this_OPENING_CURLY_BRACKET_4= RULE_OPENING_CURLY_BRACKET
                    {
                    this_OPENING_CURLY_BRACKET_4=(Token)match(input,RULE_OPENING_CURLY_BRACKET,FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleTermCharacter1587); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPENING_CURLY_BRACKET_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_CURLY_BRACKET_4, grammarAccess.getTermCharacterAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:696:10: this_CLOSING_CURLY_BRACKET_5= RULE_CLOSING_CURLY_BRACKET
                    {
                    this_CLOSING_CURLY_BRACKET_5=(Token)match(input,RULE_CLOSING_CURLY_BRACKET,FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleTermCharacter1613); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CLOSING_CURLY_BRACKET_5);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_CURLY_BRACKET_5, grammarAccess.getTermCharacterAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:704:10: this_EQUAL_SIGN_6= RULE_EQUAL_SIGN
                    {
                    this_EQUAL_SIGN_6=(Token)match(input,RULE_EQUAL_SIGN,FOLLOW_RULE_EQUAL_SIGN_in_ruleTermCharacter1639); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_EQUAL_SIGN_6);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_EQUAL_SIGN_6, grammarAccess.getTermCharacterAccess().getEQUAL_SIGNTerminalRuleCall_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:712:10: this_OPENING_ROUND_BRACKET_7= RULE_OPENING_ROUND_BRACKET
                    {
                    this_OPENING_ROUND_BRACKET_7=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTermCharacter1665); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPENING_ROUND_BRACKET_7);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_ROUND_BRACKET_7, grammarAccess.getTermCharacterAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:720:10: this_CLOSING_ROUND_BRACKET_8= RULE_CLOSING_ROUND_BRACKET
                    {
                    this_CLOSING_ROUND_BRACKET_8=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTermCharacter1691); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CLOSING_ROUND_BRACKET_8);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_ROUND_BRACKET_8, grammarAccess.getTermCharacterAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:728:10: this_PLUS_SIGN_9= RULE_PLUS_SIGN
                    {
                    this_PLUS_SIGN_9=(Token)match(input,RULE_PLUS_SIGN,FOLLOW_RULE_PLUS_SIGN_in_ruleTermCharacter1717); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_PLUS_SIGN_9);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PLUS_SIGN_9, grammarAccess.getTermCharacterAccess().getPLUS_SIGNTerminalRuleCall_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:736:10: this_COLON_10= RULE_COLON
                    {
                    this_COLON_10=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter1743); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_COLON_10);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COLON_10, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:744:10: this_OTHER_ALLOWED_TERM_CHARACTER_11= RULE_OTHER_ALLOWED_TERM_CHARACTER
                    {
                    this_OTHER_ALLOWED_TERM_CHARACTER_11=(Token)match(input,RULE_OTHER_ALLOWED_TERM_CHARACTER,FOLLOW_RULE_OTHER_ALLOWED_TERM_CHARACTER_in_ruleTermCharacter1769); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OTHER_ALLOWED_TERM_CHARACTER_11);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OTHER_ALLOWED_TERM_CHARACTER_11, grammarAccess.getTermCharacterAccess().getOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_11()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleTermCharacter"

    // Delegated rules


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\4\uffff";
    static final String DFA14_eofS =
        "\1\2\3\uffff";
    static final String DFA14_minS =
        "\1\7\1\4\2\uffff";
    static final String DFA14_maxS =
        "\1\10\1\21\2\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA14_specialS =
        "\4\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\2\1\1",
            "\3\3\1\2\1\1\11\3",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "()* loopback of 494:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*";
        }
    }
 

    public static final BitSet FOLLOW_ruleExpression_in_entryRuleExpression87 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpression97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleExpression147 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_RULE_PLUS_SIGN_in_ruleExpression159 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleExpression179 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleExpression193 = new BitSet(new long[]{0x0000000000004202L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleExpression214 = new BitSet(new long[]{0x0000000000000242L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleExpression226 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleExpression246 = new BitSet(new long[]{0x0000000000000242L});
    public static final BitSet FOLLOW_ruleGroup_in_ruleExpression271 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_ruleConcept_in_entryRuleConcept314 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConcept324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptId_in_ruleConcept370 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConcept382 = new BitSet(new long[]{0x000000000003FF70L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleConcept393 = new BitSet(new long[]{0x000000000003FF70L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConcept415 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleConcept427 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConcept439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGroup_in_entryRuleGroup476 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGroup486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleGroup522 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleGroup542 = new BitSet(new long[]{0x0000000000000440L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleGroup554 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleGroup574 = new BitSet(new long[]{0x0000000000000440L});
    public static final BitSet FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleGroup587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute622 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleAttribute678 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RULE_EQUAL_SIGN_in_ruleAttribute689 = new BitSet(new long[]{0x0000000000005000L});
    public static final BitSet FOLLOW_ruleAttributeValue_in_ruleAttribute709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeValue_in_entryRuleAttributeValue745 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeValue755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleAttributeValue805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleAttributeValue822 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAttributeValue846 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleAttributeValue856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm899 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm962 = new BitSet(new long[]{0x000000000003FF72L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm986 = new BitSet(new long[]{0x000000000003FF70L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm1016 = new BitSet(new long[]{0x000000000003FF72L});
    public static final BitSet FOLLOW_ruleConceptId_in_entryRuleConceptId1076 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptId1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1131 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1152 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId1178 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1200 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId1226 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1248 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId1274 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1296 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId1322 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId1344 = new BitSet(new long[]{0x000000000000C002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId1370 = new BitSet(new long[]{0x000000000000C002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter1428 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter1483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter1535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter1561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleTermCharacter1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleTermCharacter1613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_SIGN_in_ruleTermCharacter1639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTermCharacter1665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTermCharacter1691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_SIGN_in_ruleTermCharacter1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_ALLOWED_TERM_CHARACTER_in_ruleTermCharacter1769 = new BitSet(new long[]{0x0000000000000002L});

}