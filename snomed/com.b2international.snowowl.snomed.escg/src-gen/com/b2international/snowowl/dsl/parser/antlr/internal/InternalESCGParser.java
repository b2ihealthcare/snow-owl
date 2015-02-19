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
import com.b2international.snowowl.dsl.services.ESCGGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalESCGParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_UNION_TOKEN", "RULE_PLUS_SIGN", "RULE_COLON", "RULE_NOT_TOKEN", "RULE_CARET", "RULE_PIPE", "RULE_SUBTYPE", "RULE_INCLUSIVE_SUBTYPE", "RULE_WS", "RULE_OPENING_CURLY_BRACKET", "RULE_CLOSING_CURLY_BRACKET", "RULE_COMMA", "RULE_OPTIONAL", "RULE_EQUAL_SIGN", "RULE_OPENING_SQUARE_BRACKET", "RULE_CLOSING_SQUARE_BRACKET", "RULE_OR_TOKEN", "RULE_AND_TOKEN", "RULE_OPENING_ROUND_BRACKET", "RULE_CLOSING_ROUND_BRACKET", "RULE_DIGIT_NONZERO", "RULE_ZERO", "RULE_LETTER", "RULE_PERIOD", "RULE_OTHER_ALLOWED_TERM_CHARACTER", "RULE_EQUALS_OPERATOR", "RULE_GREATER_THAN_OPERATOR", "RULE_LESS_EQUALS_OPERATOR", "RULE_GREATER_EQUALS_OPERATOR", "RULE_NOT_EQUALS_OPERATOR", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "'mg'"
    };
    public static final int RULE_EQUALS_OPERATOR=29;
    public static final int RULE_ZERO=25;
    public static final int RULE_NOT_TOKEN=7;
    public static final int RULE_CARET=8;
    public static final int RULE_PERIOD=27;
    public static final int RULE_SUBTYPE=10;
    public static final int RULE_OPENING_CURLY_BRACKET=13;
    public static final int RULE_EQUAL_SIGN=17;
    public static final int RULE_COMMA=15;
    public static final int RULE_GREATER_THAN_OPERATOR=30;
    public static final int RULE_DIGIT_NONZERO=24;
    public static final int RULE_AND_TOKEN=21;
    public static final int RULE_OTHER_ALLOWED_TERM_CHARACTER=28;
    public static final int RULE_OPENING_ROUND_BRACKET=22;
    public static final int RULE_OR_TOKEN=20;
    public static final int RULE_OPTIONAL=16;
    public static final int RULE_SL_COMMENT=35;
    public static final int EOF=-1;
    public static final int RULE_GREATER_EQUALS_OPERATOR=32;
    public static final int RULE_NOT_EQUALS_OPERATOR=33;
    public static final int RULE_LESS_EQUALS_OPERATOR=31;
    public static final int RULE_LETTER=26;
    public static final int RULE_ML_COMMENT=34;
    public static final int RULE_CLOSING_CURLY_BRACKET=14;
    public static final int RULE_CLOSING_ROUND_BRACKET=23;
    public static final int RULE_COLON=6;
    public static final int T__36=36;
    public static final int RULE_PIPE=9;
    public static final int RULE_CLOSING_SQUARE_BRACKET=19;
    public static final int RULE_INCLUSIVE_SUBTYPE=11;
    public static final int RULE_OPENING_SQUARE_BRACKET=18;
    public static final int RULE_PLUS_SIGN=5;
    public static final int RULE_WS=12;
    public static final int RULE_UNION_TOKEN=4;

    // delegates
    // delegators


        public InternalESCGParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalESCGParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalESCGParser.tokenNames; }
    public String getGrammarFileName() { return "../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */
     
     	private ESCGGrammarAccess grammarAccess;
     	
        public InternalESCGParser(TokenStream input, ESCGGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "Expression";	
       	}
       	
       	@Override
       	protected ESCGGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleExpression"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:73:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:77:2: (iv_ruleExpression= ruleExpression EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:78:2: iv_ruleExpression= ruleExpression EOF
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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:88:1: ruleExpression returns [EObject current=null] : ( ( (lv_subExpression_0_0= ruleSubExpression ) ) (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )* )? ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        Token this_UNION_TOKEN_1=null;
        EObject lv_subExpression_0_0 = null;

        EObject lv_subExpression_2_0 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:92:28: ( ( ( (lv_subExpression_0_0= ruleSubExpression ) ) (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )* )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:93:1: ( ( (lv_subExpression_0_0= ruleSubExpression ) ) (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )* )?
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:93:1: ( ( (lv_subExpression_0_0= ruleSubExpression ) ) (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )* )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=RULE_NOT_TOKEN && LA2_0<=RULE_CARET)||(LA2_0>=RULE_SUBTYPE && LA2_0<=RULE_INCLUSIVE_SUBTYPE)||LA2_0==RULE_DIGIT_NONZERO) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:93:2: ( (lv_subExpression_0_0= ruleSubExpression ) ) (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )*
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:93:2: ( (lv_subExpression_0_0= ruleSubExpression ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:94:1: (lv_subExpression_0_0= ruleSubExpression )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:94:1: (lv_subExpression_0_0= ruleSubExpression )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:95:3: lv_subExpression_0_0= ruleSubExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getExpressionAccess().getSubExpressionSubExpressionParserRuleCall_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleSubExpression_in_ruleExpression147);
                    lv_subExpression_0_0=ruleSubExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getExpressionRule());
                      	        }
                             		add(
                             			current, 
                             			"subExpression",
                              		lv_subExpression_0_0, 
                              		"SubExpression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:111:2: (this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==RULE_UNION_TOKEN) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:111:3: this_UNION_TOKEN_1= RULE_UNION_TOKEN ( (lv_subExpression_2_0= ruleSubExpression ) )
                    	    {
                    	    this_UNION_TOKEN_1=(Token)match(input,RULE_UNION_TOKEN,FOLLOW_RULE_UNION_TOKEN_in_ruleExpression159); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_UNION_TOKEN_1, grammarAccess.getExpressionAccess().getUNION_TOKENTerminalRuleCall_1_0()); 
                    	          
                    	    }
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:115:1: ( (lv_subExpression_2_0= ruleSubExpression ) )
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:116:1: (lv_subExpression_2_0= ruleSubExpression )
                    	    {
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:116:1: (lv_subExpression_2_0= ruleSubExpression )
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:117:3: lv_subExpression_2_0= ruleSubExpression
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getExpressionAccess().getSubExpressionSubExpressionParserRuleCall_1_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FOLLOW_ruleSubExpression_in_ruleExpression179);
                    	    lv_subExpression_2_0=ruleSubExpression();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getExpressionRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"subExpression",
                    	              		lv_subExpression_2_0, 
                    	              		"SubExpression");
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
    // $ANTLR end "ruleExpression"


    // $ANTLR start "entryRuleSubExpression"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:144:1: entryRuleSubExpression returns [EObject current=null] : iv_ruleSubExpression= ruleSubExpression EOF ;
    public final EObject entryRuleSubExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubExpression = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:145:2: (iv_ruleSubExpression= ruleSubExpression EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:146:2: iv_ruleSubExpression= ruleSubExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleSubExpression_in_entryRuleSubExpression222);
            iv_ruleSubExpression=ruleSubExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleSubExpression232); if (state.failed) return current;

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
    // $ANTLR end "entryRuleSubExpression"


    // $ANTLR start "ruleSubExpression"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:153:1: ruleSubExpression returns [EObject current=null] : ( ( (lv_lValues_0_0= ruleLValue ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )? ) ;
    public final EObject ruleSubExpression() throws RecognitionException {
        EObject current = null;

        Token this_PLUS_SIGN_1=null;
        Token this_COLON_3=null;
        EObject lv_lValues_0_0 = null;

        EObject lv_lValues_2_0 = null;

        EObject lv_refinements_4_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:156:28: ( ( ( (lv_lValues_0_0= ruleLValue ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )? ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:157:1: ( ( (lv_lValues_0_0= ruleLValue ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )? )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:157:1: ( ( (lv_lValues_0_0= ruleLValue ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:157:2: ( (lv_lValues_0_0= ruleLValue ) ) (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )?
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:157:2: ( (lv_lValues_0_0= ruleLValue ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:158:1: (lv_lValues_0_0= ruleLValue )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:158:1: (lv_lValues_0_0= ruleLValue )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:159:3: lv_lValues_0_0= ruleLValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getSubExpressionAccess().getLValuesLValueParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleLValue_in_ruleSubExpression278);
            lv_lValues_0_0=ruleLValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getSubExpressionRule());
              	        }
                     		add(
                     			current, 
                     			"lValues",
                      		lv_lValues_0_0, 
                      		"LValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:175:2: (this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==RULE_PLUS_SIGN) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:175:3: this_PLUS_SIGN_1= RULE_PLUS_SIGN ( (lv_lValues_2_0= ruleLValue ) )
            	    {
            	    this_PLUS_SIGN_1=(Token)match(input,RULE_PLUS_SIGN,FOLLOW_RULE_PLUS_SIGN_in_ruleSubExpression290); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_PLUS_SIGN_1, grammarAccess.getSubExpressionAccess().getPLUS_SIGNTerminalRuleCall_1_0()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:179:1: ( (lv_lValues_2_0= ruleLValue ) )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:180:1: (lv_lValues_2_0= ruleLValue )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:180:1: (lv_lValues_2_0= ruleLValue )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:181:3: lv_lValues_2_0= ruleLValue
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getSubExpressionAccess().getLValuesLValueParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleLValue_in_ruleSubExpression310);
            	    lv_lValues_2_0=ruleLValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getSubExpressionRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"lValues",
            	              		lv_lValues_2_0, 
            	              		"LValue");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:197:4: (this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_COLON) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:197:5: this_COLON_3= RULE_COLON ( (lv_refinements_4_0= ruleRefinements ) )
                    {
                    this_COLON_3=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleSubExpression324); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COLON_3, grammarAccess.getSubExpressionAccess().getCOLONTerminalRuleCall_2_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:201:1: ( (lv_refinements_4_0= ruleRefinements ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:202:1: (lv_refinements_4_0= ruleRefinements )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:202:1: (lv_refinements_4_0= ruleRefinements )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:203:3: lv_refinements_4_0= ruleRefinements
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getSubExpressionAccess().getRefinementsRefinementsParserRuleCall_2_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleRefinements_in_ruleSubExpression344);
                    lv_refinements_4_0=ruleRefinements();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getSubExpressionRule());
                      	        }
                             		set(
                             			current, 
                             			"refinements",
                              		lv_refinements_4_0, 
                              		"Refinements");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


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
    // $ANTLR end "ruleSubExpression"


    // $ANTLR start "entryRuleLValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:227:1: entryRuleLValue returns [EObject current=null] : iv_ruleLValue= ruleLValue EOF ;
    public final EObject entryRuleLValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLValue = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:228:2: (iv_ruleLValue= ruleLValue EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:229:2: iv_ruleLValue= ruleLValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getLValueRule()); 
            }
            pushFollow(FOLLOW_ruleLValue_in_entryRuleLValue382);
            iv_ruleLValue=ruleLValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleLValue; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleLValue392); if (state.failed) return current;

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
    // $ANTLR end "entryRuleLValue"


    // $ANTLR start "ruleLValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:236:1: ruleLValue returns [EObject current=null] : (this_ConceptGroup_0= ruleConceptGroup | this_RefSet_1= ruleRefSet ) ;
    public final EObject ruleLValue() throws RecognitionException {
        EObject current = null;

        EObject this_ConceptGroup_0 = null;

        EObject this_RefSet_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:239:28: ( (this_ConceptGroup_0= ruleConceptGroup | this_RefSet_1= ruleRefSet ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:240:1: (this_ConceptGroup_0= ruleConceptGroup | this_RefSet_1= ruleRefSet )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:240:1: (this_ConceptGroup_0= ruleConceptGroup | this_RefSet_1= ruleRefSet )
            int alt5=2;
            switch ( input.LA(1) ) {
            case RULE_NOT_TOKEN:
                {
                int LA5_1 = input.LA(2);

                if ( ((LA5_1>=RULE_SUBTYPE && LA5_1<=RULE_INCLUSIVE_SUBTYPE)||LA5_1==RULE_DIGIT_NONZERO) ) {
                    alt5=1;
                }
                else if ( (LA5_1==RULE_CARET) ) {
                    alt5=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
                }
                break;
            case RULE_SUBTYPE:
            case RULE_INCLUSIVE_SUBTYPE:
            case RULE_DIGIT_NONZERO:
                {
                alt5=1;
                }
                break;
            case RULE_CARET:
                {
                alt5=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:241:2: this_ConceptGroup_0= ruleConceptGroup
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getLValueAccess().getConceptGroupParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleConceptGroup_in_ruleLValue442);
                    this_ConceptGroup_0=ruleConceptGroup();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ConceptGroup_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:254:2: this_RefSet_1= ruleRefSet
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getLValueAccess().getRefSetParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleRefSet_in_ruleLValue472);
                    this_RefSet_1=ruleRefSet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_RefSet_1; 
                              afterParserOrEnumRuleCall();
                          
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
    // $ANTLR end "ruleLValue"


    // $ANTLR start "entryRuleRefSet"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:273:1: entryRuleRefSet returns [EObject current=null] : iv_ruleRefSet= ruleRefSet EOF ;
    public final EObject entryRuleRefSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefSet = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:274:2: (iv_ruleRefSet= ruleRefSet EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:275:2: iv_ruleRefSet= ruleRefSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRefSetRule()); 
            }
            pushFollow(FOLLOW_ruleRefSet_in_entryRuleRefSet507);
            iv_ruleRefSet=ruleRefSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRefSet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefSet517); if (state.failed) return current;

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
    // $ANTLR end "entryRuleRefSet"


    // $ANTLR start "ruleRefSet"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:282:1: ruleRefSet returns [EObject current=null] : ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_CARET_1= RULE_CARET ( (lv_id_2_0= ruleConceptId ) ) (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )? ) ;
    public final EObject ruleRefSet() throws RecognitionException {
        EObject current = null;

        Token lv_negated_0_0=null;
        Token this_CARET_1=null;
        Token this_PIPE_3=null;
        Token this_PIPE_5=null;
        AntlrDatatypeRuleToken lv_id_2_0 = null;

        AntlrDatatypeRuleToken lv_term_4_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:285:28: ( ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_CARET_1= RULE_CARET ( (lv_id_2_0= ruleConceptId ) ) (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:286:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_CARET_1= RULE_CARET ( (lv_id_2_0= ruleConceptId ) ) (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:286:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_CARET_1= RULE_CARET ( (lv_id_2_0= ruleConceptId ) ) (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:286:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_CARET_1= RULE_CARET ( (lv_id_2_0= ruleConceptId ) ) (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:286:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_NOT_TOKEN) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:287:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:287:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:288:3: lv_negated_0_0= RULE_NOT_TOKEN
                    {
                    lv_negated_0_0=(Token)match(input,RULE_NOT_TOKEN,FOLLOW_RULE_NOT_TOKEN_in_ruleRefSet559); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_negated_0_0, grammarAccess.getRefSetAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getRefSetRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"negated",
                              		true, 
                              		"NOT_TOKEN");
                      	    
                    }

                    }


                    }
                    break;

            }

            this_CARET_1=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleRefSet576); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_CARET_1, grammarAccess.getRefSetAccess().getCARETTerminalRuleCall_1()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:308:1: ( (lv_id_2_0= ruleConceptId ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:309:1: (lv_id_2_0= ruleConceptId )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:309:1: (lv_id_2_0= ruleConceptId )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:310:3: lv_id_2_0= ruleConceptId
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getRefSetAccess().getIdConceptIdParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConceptId_in_ruleRefSet596);
            lv_id_2_0=ruleConceptId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getRefSetRule());
              	        }
                     		set(
                     			current, 
                     			"id",
                      		lv_id_2_0, 
                      		"ConceptId");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:326:2: (this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_PIPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:326:3: this_PIPE_3= RULE_PIPE ( (lv_term_4_0= ruleTerm ) ) this_PIPE_5= RULE_PIPE
                    {
                    this_PIPE_3=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleRefSet608); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PIPE_3, grammarAccess.getRefSetAccess().getPIPETerminalRuleCall_3_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:330:1: ( (lv_term_4_0= ruleTerm ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:331:1: (lv_term_4_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:331:1: (lv_term_4_0= ruleTerm )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:332:3: lv_term_4_0= ruleTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRefSetAccess().getTermTermParserRuleCall_3_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleTerm_in_ruleRefSet628);
                    lv_term_4_0=ruleTerm();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getRefSetRule());
                      	        }
                             		set(
                             			current, 
                             			"term",
                              		lv_term_4_0, 
                              		"Term");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    this_PIPE_5=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleRefSet639); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PIPE_5, grammarAccess.getRefSetAccess().getPIPETerminalRuleCall_3_2()); 
                          
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
    // $ANTLR end "ruleRefSet"


    // $ANTLR start "entryRuleConceptGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:360:1: entryRuleConceptGroup returns [EObject current=null] : iv_ruleConceptGroup= ruleConceptGroup EOF ;
    public final EObject entryRuleConceptGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptGroup = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:361:2: (iv_ruleConceptGroup= ruleConceptGroup EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:362:2: iv_ruleConceptGroup= ruleConceptGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptGroupRule()); 
            }
            pushFollow(FOLLOW_ruleConceptGroup_in_entryRuleConceptGroup676);
            iv_ruleConceptGroup=ruleConceptGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptGroup; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptGroup686); if (state.failed) return current;

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
    // $ANTLR end "entryRuleConceptGroup"


    // $ANTLR start "ruleConceptGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:369:1: ruleConceptGroup returns [EObject current=null] : ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )? ( (lv_concept_2_0= ruleConcept ) ) ) ;
    public final EObject ruleConceptGroup() throws RecognitionException {
        EObject current = null;

        Token lv_negated_0_0=null;
        Token lv_constraint_1_1=null;
        Token lv_constraint_1_2=null;
        EObject lv_concept_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:372:28: ( ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )? ( (lv_concept_2_0= ruleConcept ) ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:373:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )? ( (lv_concept_2_0= ruleConcept ) ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:373:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )? ( (lv_concept_2_0= ruleConcept ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:373:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )? ( (lv_concept_2_0= ruleConcept ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:373:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_NOT_TOKEN) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:374:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:374:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:375:3: lv_negated_0_0= RULE_NOT_TOKEN
                    {
                    lv_negated_0_0=(Token)match(input,RULE_NOT_TOKEN,FOLLOW_RULE_NOT_TOKEN_in_ruleConceptGroup728); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_negated_0_0, grammarAccess.getConceptGroupAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getConceptGroupRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"negated",
                              		true, 
                              		"NOT_TOKEN");
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:391:3: ( ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=RULE_SUBTYPE && LA10_0<=RULE_INCLUSIVE_SUBTYPE)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:392:1: ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:392:1: ( (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:393:1: (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:393:1: (lv_constraint_1_1= RULE_SUBTYPE | lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==RULE_SUBTYPE) ) {
                        alt9=1;
                    }
                    else if ( (LA9_0==RULE_INCLUSIVE_SUBTYPE) ) {
                        alt9=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:394:3: lv_constraint_1_1= RULE_SUBTYPE
                            {
                            lv_constraint_1_1=(Token)match(input,RULE_SUBTYPE,FOLLOW_RULE_SUBTYPE_in_ruleConceptGroup753); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_constraint_1_1, grammarAccess.getConceptGroupAccess().getConstraintSUBTYPETerminalRuleCall_1_0_0()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getConceptGroupRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"constraint",
                                      		lv_constraint_1_1, 
                                      		"SUBTYPE");
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:409:8: lv_constraint_1_2= RULE_INCLUSIVE_SUBTYPE
                            {
                            lv_constraint_1_2=(Token)match(input,RULE_INCLUSIVE_SUBTYPE,FOLLOW_RULE_INCLUSIVE_SUBTYPE_in_ruleConceptGroup773); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_constraint_1_2, grammarAccess.getConceptGroupAccess().getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getConceptGroupRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"constraint",
                                      		lv_constraint_1_2, 
                                      		"INCLUSIVE_SUBTYPE");
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:427:3: ( (lv_concept_2_0= ruleConcept ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:428:1: (lv_concept_2_0= ruleConcept )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:428:1: (lv_concept_2_0= ruleConcept )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:429:3: lv_concept_2_0= ruleConcept
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConceptGroupAccess().getConceptConceptParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConcept_in_ruleConceptGroup803);
            lv_concept_2_0=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConceptGroupRule());
              	        }
                     		set(
                     			current, 
                     			"concept",
                      		lv_concept_2_0, 
                      		"Concept");
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
    // $ANTLR end "ruleConceptGroup"


    // $ANTLR start "entryRuleConcept"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:453:1: entryRuleConcept returns [EObject current=null] : iv_ruleConcept= ruleConcept EOF ;
    public final EObject entryRuleConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConcept = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:454:2: (iv_ruleConcept= ruleConcept EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:455:2: iv_ruleConcept= ruleConcept EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptRule()); 
            }
            pushFollow(FOLLOW_ruleConcept_in_entryRuleConcept839);
            iv_ruleConcept=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConcept; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConcept849); if (state.failed) return current;

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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:462:1: ruleConcept returns [EObject current=null] : ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? ) ;
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
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:465:28: ( ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:466:1: ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:466:1: ( ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:466:2: ( (lv_id_0_0= ruleConceptId ) ) (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )?
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:466:2: ( (lv_id_0_0= ruleConceptId ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:467:1: (lv_id_0_0= ruleConceptId )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:467:1: (lv_id_0_0= ruleConceptId )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:468:3: lv_id_0_0= ruleConceptId
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConceptAccess().getIdConceptIdParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConceptId_in_ruleConcept895);
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

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:484:2: (this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_PIPE) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:484:3: this_PIPE_1= RULE_PIPE (this_WS_2= RULE_WS )* ( (lv_term_3_0= ruleTerm ) ) (this_WS_4= RULE_WS )* this_PIPE_5= RULE_PIPE
                    {
                    this_PIPE_1=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConcept907); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PIPE_1, grammarAccess.getConceptAccess().getPIPETerminalRuleCall_1_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:488:1: (this_WS_2= RULE_WS )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==RULE_WS) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:488:2: this_WS_2= RULE_WS
                    	    {
                    	    this_WS_2=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleConcept918); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_WS_2, grammarAccess.getConceptAccess().getWSTerminalRuleCall_1_1()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:492:3: ( (lv_term_3_0= ruleTerm ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:493:1: (lv_term_3_0= ruleTerm )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:493:1: (lv_term_3_0= ruleTerm )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:494:3: lv_term_3_0= ruleTerm
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getConceptAccess().getTermTermParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleTerm_in_ruleConcept940);
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

                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:510:2: (this_WS_4= RULE_WS )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==RULE_WS) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:510:3: this_WS_4= RULE_WS
                    	    {
                    	    this_WS_4=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleConcept952); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_WS_4, grammarAccess.getConceptAccess().getWSTerminalRuleCall_1_3()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    this_PIPE_5=(Token)match(input,RULE_PIPE,FOLLOW_RULE_PIPE_in_ruleConcept964); if (state.failed) return current;
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


    // $ANTLR start "entryRuleRefinements"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:526:1: entryRuleRefinements returns [EObject current=null] : iv_ruleRefinements= ruleRefinements EOF ;
    public final EObject entryRuleRefinements() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinements = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:527:2: (iv_ruleRefinements= ruleRefinements EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:528:2: iv_ruleRefinements= ruleRefinements EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRefinementsRule()); 
            }
            pushFollow(FOLLOW_ruleRefinements_in_entryRuleRefinements1001);
            iv_ruleRefinements=ruleRefinements();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRefinements; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRefinements1011); if (state.failed) return current;

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
    // $ANTLR end "entryRuleRefinements"


    // $ANTLR start "ruleRefinements"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:535:1: ruleRefinements returns [EObject current=null] : ( ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* ) | ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+ ) ;
    public final EObject ruleRefinements() throws RecognitionException {
        EObject current = null;

        EObject lv_attributeSet_0_0 = null;

        EObject lv_attributeGroups_1_0 = null;

        EObject lv_attributeGroups_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:538:28: ( ( ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* ) | ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+ ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:1: ( ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* ) | ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+ )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:1: ( ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* ) | ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+ )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( ((LA16_0>=RULE_NOT_TOKEN && LA16_0<=RULE_CARET)||(LA16_0>=RULE_SUBTYPE && LA16_0<=RULE_INCLUSIVE_SUBTYPE)||LA16_0==RULE_OPTIONAL||LA16_0==RULE_OPENING_SQUARE_BRACKET||LA16_0==RULE_DIGIT_NONZERO) ) {
                alt16=1;
            }
            else if ( (LA16_0==RULE_OPENING_CURLY_BRACKET) ) {
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
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:2: ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:2: ( ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )* )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:3: ( (lv_attributeSet_0_0= ruleAttributeSet ) ) ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )*
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:539:3: ( (lv_attributeSet_0_0= ruleAttributeSet ) )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:540:1: (lv_attributeSet_0_0= ruleAttributeSet )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:540:1: (lv_attributeSet_0_0= ruleAttributeSet )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:541:3: lv_attributeSet_0_0= ruleAttributeSet
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRefinementsAccess().getAttributeSetAttributeSetParserRuleCall_0_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleAttributeSet_in_ruleRefinements1058);
                    lv_attributeSet_0_0=ruleAttributeSet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getRefinementsRule());
                      	        }
                             		set(
                             			current, 
                             			"attributeSet",
                              		lv_attributeSet_0_0, 
                              		"AttributeSet");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:557:2: ( (lv_attributeGroups_1_0= ruleAttributeGroup ) )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==RULE_OPENING_CURLY_BRACKET) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:558:1: (lv_attributeGroups_1_0= ruleAttributeGroup )
                    	    {
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:558:1: (lv_attributeGroups_1_0= ruleAttributeGroup )
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:559:3: lv_attributeGroups_1_0= ruleAttributeGroup
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getRefinementsAccess().getAttributeGroupsAttributeGroupParserRuleCall_0_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FOLLOW_ruleAttributeGroup_in_ruleRefinements1079);
                    	    lv_attributeGroups_1_0=ruleAttributeGroup();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getRefinementsRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"attributeGroups",
                    	              		lv_attributeGroups_1_0, 
                    	              		"AttributeGroup");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:576:6: ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:576:6: ( (lv_attributeGroups_2_0= ruleAttributeGroup ) )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==RULE_OPENING_CURLY_BRACKET) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:577:1: (lv_attributeGroups_2_0= ruleAttributeGroup )
                    	    {
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:577:1: (lv_attributeGroups_2_0= ruleAttributeGroup )
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:578:3: lv_attributeGroups_2_0= ruleAttributeGroup
                    	    {
                    	    if ( state.backtracking==0 ) {
                    	       
                    	      	        newCompositeNode(grammarAccess.getRefinementsAccess().getAttributeGroupsAttributeGroupParserRuleCall_1_0()); 
                    	      	    
                    	    }
                    	    pushFollow(FOLLOW_ruleAttributeGroup_in_ruleRefinements1108);
                    	    lv_attributeGroups_2_0=ruleAttributeGroup();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      	        if (current==null) {
                    	      	            current = createModelElementForParent(grammarAccess.getRefinementsRule());
                    	      	        }
                    	             		add(
                    	             			current, 
                    	             			"attributeGroups",
                    	              		lv_attributeGroups_2_0, 
                    	              		"AttributeGroup");
                    	      	        afterParserOrEnumRuleCall();
                    	      	    
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                    	    if (state.backtracking>0) {state.failed=true; return current;}
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);


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
    // $ANTLR end "ruleRefinements"


    // $ANTLR start "entryRuleAttributeGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:602:1: entryRuleAttributeGroup returns [EObject current=null] : iv_ruleAttributeGroup= ruleAttributeGroup EOF ;
    public final EObject entryRuleAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeGroup = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:603:2: (iv_ruleAttributeGroup= ruleAttributeGroup EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:604:2: iv_ruleAttributeGroup= ruleAttributeGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeGroupRule()); 
            }
            pushFollow(FOLLOW_ruleAttributeGroup_in_entryRuleAttributeGroup1145);
            iv_ruleAttributeGroup=ruleAttributeGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeGroup; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeGroup1155); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAttributeGroup"


    // $ANTLR start "ruleAttributeGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:611:1: ruleAttributeGroup returns [EObject current=null] : (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET this_AttributeSet_1= ruleAttributeSet this_CLOSING_CURLY_BRACKET_2= RULE_CLOSING_CURLY_BRACKET ) ;
    public final EObject ruleAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_OPENING_CURLY_BRACKET_0=null;
        Token this_CLOSING_CURLY_BRACKET_2=null;
        EObject this_AttributeSet_1 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:614:28: ( (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET this_AttributeSet_1= ruleAttributeSet this_CLOSING_CURLY_BRACKET_2= RULE_CLOSING_CURLY_BRACKET ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:615:1: (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET this_AttributeSet_1= ruleAttributeSet this_CLOSING_CURLY_BRACKET_2= RULE_CLOSING_CURLY_BRACKET )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:615:1: (this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET this_AttributeSet_1= ruleAttributeSet this_CLOSING_CURLY_BRACKET_2= RULE_CLOSING_CURLY_BRACKET )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:615:2: this_OPENING_CURLY_BRACKET_0= RULE_OPENING_CURLY_BRACKET this_AttributeSet_1= ruleAttributeSet this_CLOSING_CURLY_BRACKET_2= RULE_CLOSING_CURLY_BRACKET
            {
            this_OPENING_CURLY_BRACKET_0=(Token)match(input,RULE_OPENING_CURLY_BRACKET,FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleAttributeGroup1191); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_OPENING_CURLY_BRACKET_0, grammarAccess.getAttributeGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0()); 
                  
            }
            if ( state.backtracking==0 ) {
               
              	  /* */ 
              	
            }
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributeSetParserRuleCall_1()); 
                  
            }
            pushFollow(FOLLOW_ruleAttributeSet_in_ruleAttributeGroup1215);
            this_AttributeSet_1=ruleAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_AttributeSet_1; 
                      afterParserOrEnumRuleCall();
                  
            }
            this_CLOSING_CURLY_BRACKET_2=(Token)match(input,RULE_CLOSING_CURLY_BRACKET,FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleAttributeGroup1225); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_CLOSING_CURLY_BRACKET_2, grammarAccess.getAttributeGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_2()); 
                  
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
    // $ANTLR end "ruleAttributeGroup"


    // $ANTLR start "entryRuleAttributeSet"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:643:1: entryRuleAttributeSet returns [EObject current=null] : iv_ruleAttributeSet= ruleAttributeSet EOF ;
    public final EObject entryRuleAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeSet = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:644:2: (iv_ruleAttributeSet= ruleAttributeSet EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:645:2: iv_ruleAttributeSet= ruleAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeSetRule()); 
            }
            pushFollow(FOLLOW_ruleAttributeSet_in_entryRuleAttributeSet1260);
            iv_ruleAttributeSet=ruleAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeSet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeSet1270); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAttributeSet"


    // $ANTLR start "ruleAttributeSet"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:652:1: ruleAttributeSet returns [EObject current=null] : ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) ;
    public final EObject ruleAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_COMMA_1=null;
        EObject lv_attributes_0_0 = null;

        EObject lv_attributes_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:655:28: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:656:1: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:656:1: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:656:2: ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:656:2: ( (lv_attributes_0_0= ruleAttribute ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:657:1: (lv_attributes_0_0= ruleAttribute )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:657:1: (lv_attributes_0_0= ruleAttribute )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:658:3: lv_attributes_0_0= ruleAttribute
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAttributeSetAccess().getAttributesAttributeParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAttribute_in_ruleAttributeSet1316);
            lv_attributes_0_0=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAttributeSetRule());
              	        }
                     		add(
                     			current, 
                     			"attributes",
                      		lv_attributes_0_0, 
                      		"Attribute");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:674:2: (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==RULE_COMMA) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:674:3: this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) )
            	    {
            	    this_COMMA_1=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleAttributeSet1328); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_COMMA_1, grammarAccess.getAttributeSetAccess().getCOMMATerminalRuleCall_1_0()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:678:1: ( (lv_attributes_2_0= ruleAttribute ) )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:679:1: (lv_attributes_2_0= ruleAttribute )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:679:1: (lv_attributes_2_0= ruleAttribute )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:680:3: lv_attributes_2_0= ruleAttribute
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAttributeSetAccess().getAttributesAttributeParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttribute_in_ruleAttributeSet1348);
            	    lv_attributes_2_0=ruleAttribute();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAttributeSetRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_2_0, 
            	              		"Attribute");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
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
        }
        return current;
    }
    // $ANTLR end "ruleAttributeSet"


    // $ANTLR start "entryRuleAttribute"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:704:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:705:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:706:2: iv_ruleAttribute= ruleAttribute EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeRule()); 
            }
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute1386);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttribute; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute1396); if (state.failed) return current;

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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:713:1: ruleAttribute returns [EObject current=null] : ( ( (lv_optional_0_0= RULE_OPTIONAL ) )? ( (lv_assignment_1_0= ruleAttributeAssignment ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token lv_optional_0_0=null;
        EObject lv_assignment_1_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:716:28: ( ( ( (lv_optional_0_0= RULE_OPTIONAL ) )? ( (lv_assignment_1_0= ruleAttributeAssignment ) ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:717:1: ( ( (lv_optional_0_0= RULE_OPTIONAL ) )? ( (lv_assignment_1_0= ruleAttributeAssignment ) ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:717:1: ( ( (lv_optional_0_0= RULE_OPTIONAL ) )? ( (lv_assignment_1_0= ruleAttributeAssignment ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:717:2: ( (lv_optional_0_0= RULE_OPTIONAL ) )? ( (lv_assignment_1_0= ruleAttributeAssignment ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:717:2: ( (lv_optional_0_0= RULE_OPTIONAL ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_OPTIONAL) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:718:1: (lv_optional_0_0= RULE_OPTIONAL )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:718:1: (lv_optional_0_0= RULE_OPTIONAL )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:719:3: lv_optional_0_0= RULE_OPTIONAL
                    {
                    lv_optional_0_0=(Token)match(input,RULE_OPTIONAL,FOLLOW_RULE_OPTIONAL_in_ruleAttribute1438); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_optional_0_0, grammarAccess.getAttributeAccess().getOptionalOPTIONALTerminalRuleCall_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAttributeRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"optional",
                              		true, 
                              		"OPTIONAL");
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:735:3: ( (lv_assignment_1_0= ruleAttributeAssignment ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:736:1: (lv_assignment_1_0= ruleAttributeAssignment )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:736:1: (lv_assignment_1_0= ruleAttributeAssignment )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:737:3: lv_assignment_1_0= ruleAttributeAssignment
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAttributeAccess().getAssignmentAttributeAssignmentParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAttributeAssignment_in_ruleAttribute1465);
            lv_assignment_1_0=ruleAttributeAssignment();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAttributeRule());
              	        }
                     		set(
                     			current, 
                     			"assignment",
                      		lv_assignment_1_0, 
                      		"AttributeAssignment");
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


    // $ANTLR start "entryRuleAttributeAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:761:1: entryRuleAttributeAssignment returns [EObject current=null] : iv_ruleAttributeAssignment= ruleAttributeAssignment EOF ;
    public final EObject entryRuleAttributeAssignment() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeAssignment = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:762:2: (iv_ruleAttributeAssignment= ruleAttributeAssignment EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:763:2: iv_ruleAttributeAssignment= ruleAttributeAssignment EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeAssignmentRule()); 
            }
            pushFollow(FOLLOW_ruleAttributeAssignment_in_entryRuleAttributeAssignment1501);
            iv_ruleAttributeAssignment=ruleAttributeAssignment();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeAssignment; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeAssignment1511); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAttributeAssignment"


    // $ANTLR start "ruleAttributeAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:770:1: ruleAttributeAssignment returns [EObject current=null] : (this_ConceptAssignment_0= ruleConceptAssignment | this_NumericalAssignment_1= ruleNumericalAssignment | this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup ) ;
    public final EObject ruleAttributeAssignment() throws RecognitionException {
        EObject current = null;

        EObject this_ConceptAssignment_0 = null;

        EObject this_NumericalAssignment_1 = null;

        EObject this_NumericalAssignmentGroup_2 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:773:28: ( (this_ConceptAssignment_0= ruleConceptAssignment | this_NumericalAssignment_1= ruleNumericalAssignment | this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:774:1: (this_ConceptAssignment_0= ruleConceptAssignment | this_NumericalAssignment_1= ruleNumericalAssignment | this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:774:1: (this_ConceptAssignment_0= ruleConceptAssignment | this_NumericalAssignment_1= ruleNumericalAssignment | this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup )
            int alt19=3;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:775:2: this_ConceptAssignment_0= ruleConceptAssignment
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAttributeAssignmentAccess().getConceptAssignmentParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleConceptAssignment_in_ruleAttributeAssignment1561);
                    this_ConceptAssignment_0=ruleConceptAssignment();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ConceptAssignment_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:788:2: this_NumericalAssignment_1= ruleNumericalAssignment
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAttributeAssignmentAccess().getNumericalAssignmentParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleNumericalAssignment_in_ruleAttributeAssignment1591);
                    this_NumericalAssignment_1=ruleNumericalAssignment();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_NumericalAssignment_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:801:2: this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAttributeAssignmentAccess().getNumericalAssignmentGroupParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleNumericalAssignmentGroup_in_ruleAttributeAssignment1621);
                    this_NumericalAssignmentGroup_2=ruleNumericalAssignmentGroup();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_NumericalAssignmentGroup_2; 
                              afterParserOrEnumRuleCall();
                          
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
    // $ANTLR end "ruleAttributeAssignment"


    // $ANTLR start "entryRuleConceptAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:820:1: entryRuleConceptAssignment returns [EObject current=null] : iv_ruleConceptAssignment= ruleConceptAssignment EOF ;
    public final EObject entryRuleConceptAssignment() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptAssignment = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:821:2: (iv_ruleConceptAssignment= ruleConceptAssignment EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:822:2: iv_ruleConceptAssignment= ruleConceptAssignment EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptAssignmentRule()); 
            }
            pushFollow(FOLLOW_ruleConceptAssignment_in_entryRuleConceptAssignment1656);
            iv_ruleConceptAssignment=ruleConceptAssignment();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptAssignment; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptAssignment1666); if (state.failed) return current;

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
    // $ANTLR end "entryRuleConceptAssignment"


    // $ANTLR start "ruleConceptAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:829:1: ruleConceptAssignment returns [EObject current=null] : ( ( (lv_name_0_0= ruleLValue ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleRValue ) ) ) ;
    public final EObject ruleConceptAssignment() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_SIGN_1=null;
        EObject lv_name_0_0 = null;

        EObject lv_value_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:832:28: ( ( ( (lv_name_0_0= ruleLValue ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleRValue ) ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:833:1: ( ( (lv_name_0_0= ruleLValue ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleRValue ) ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:833:1: ( ( (lv_name_0_0= ruleLValue ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleRValue ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:833:2: ( (lv_name_0_0= ruleLValue ) ) this_EQUAL_SIGN_1= RULE_EQUAL_SIGN ( (lv_value_2_0= ruleRValue ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:833:2: ( (lv_name_0_0= ruleLValue ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:834:1: (lv_name_0_0= ruleLValue )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:834:1: (lv_name_0_0= ruleLValue )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:835:3: lv_name_0_0= ruleLValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConceptAssignmentAccess().getNameLValueParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleLValue_in_ruleConceptAssignment1712);
            lv_name_0_0=ruleLValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConceptAssignmentRule());
              	        }
                     		set(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"LValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_EQUAL_SIGN_1=(Token)match(input,RULE_EQUAL_SIGN,FOLLOW_RULE_EQUAL_SIGN_in_ruleConceptAssignment1723); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_EQUAL_SIGN_1, grammarAccess.getConceptAssignmentAccess().getEQUAL_SIGNTerminalRuleCall_1()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:855:1: ( (lv_value_2_0= ruleRValue ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:856:1: (lv_value_2_0= ruleRValue )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:856:1: (lv_value_2_0= ruleRValue )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:857:3: lv_value_2_0= ruleRValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getConceptAssignmentAccess().getValueRValueParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleRValue_in_ruleConceptAssignment1743);
            lv_value_2_0=ruleRValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getConceptAssignmentRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_2_0, 
                      		"RValue");
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
    // $ANTLR end "ruleConceptAssignment"


    // $ANTLR start "entryRuleNumericalAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:881:1: entryRuleNumericalAssignment returns [EObject current=null] : iv_ruleNumericalAssignment= ruleNumericalAssignment EOF ;
    public final EObject entryRuleNumericalAssignment() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumericalAssignment = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:882:2: (iv_ruleNumericalAssignment= ruleNumericalAssignment EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:883:2: iv_ruleNumericalAssignment= ruleNumericalAssignment EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNumericalAssignmentRule()); 
            }
            pushFollow(FOLLOW_ruleNumericalAssignment_in_entryRuleNumericalAssignment1779);
            iv_ruleNumericalAssignment=ruleNumericalAssignment();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNumericalAssignment; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNumericalAssignment1789); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNumericalAssignment"


    // $ANTLR start "ruleNumericalAssignment"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:890:1: ruleNumericalAssignment returns [EObject current=null] : ( ( (lv_name_0_0= ruleConcept ) ) ( (lv_operator_1_0= ruleOperator ) ) ( (lv_value_2_0= ruleDecimalNumber ) ) ( (lv_unit_3_0= ruleUnitType ) ) ) ;
    public final EObject ruleNumericalAssignment() throws RecognitionException {
        EObject current = null;

        EObject lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_operator_1_0 = null;

        AntlrDatatypeRuleToken lv_value_2_0 = null;

        AntlrDatatypeRuleToken lv_unit_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:893:28: ( ( ( (lv_name_0_0= ruleConcept ) ) ( (lv_operator_1_0= ruleOperator ) ) ( (lv_value_2_0= ruleDecimalNumber ) ) ( (lv_unit_3_0= ruleUnitType ) ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:894:1: ( ( (lv_name_0_0= ruleConcept ) ) ( (lv_operator_1_0= ruleOperator ) ) ( (lv_value_2_0= ruleDecimalNumber ) ) ( (lv_unit_3_0= ruleUnitType ) ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:894:1: ( ( (lv_name_0_0= ruleConcept ) ) ( (lv_operator_1_0= ruleOperator ) ) ( (lv_value_2_0= ruleDecimalNumber ) ) ( (lv_unit_3_0= ruleUnitType ) ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:894:2: ( (lv_name_0_0= ruleConcept ) ) ( (lv_operator_1_0= ruleOperator ) ) ( (lv_value_2_0= ruleDecimalNumber ) ) ( (lv_unit_3_0= ruleUnitType ) )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:894:2: ( (lv_name_0_0= ruleConcept ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:895:1: (lv_name_0_0= ruleConcept )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:895:1: (lv_name_0_0= ruleConcept )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:896:3: lv_name_0_0= ruleConcept
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentAccess().getNameConceptParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConcept_in_ruleNumericalAssignment1835);
            lv_name_0_0=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentRule());
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

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:912:2: ( (lv_operator_1_0= ruleOperator ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:913:1: (lv_operator_1_0= ruleOperator )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:913:1: (lv_operator_1_0= ruleOperator )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:914:3: lv_operator_1_0= ruleOperator
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentAccess().getOperatorOperatorParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleOperator_in_ruleNumericalAssignment1856);
            lv_operator_1_0=ruleOperator();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentRule());
              	        }
                     		set(
                     			current, 
                     			"operator",
                      		lv_operator_1_0, 
                      		"Operator");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:930:2: ( (lv_value_2_0= ruleDecimalNumber ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:931:1: (lv_value_2_0= ruleDecimalNumber )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:931:1: (lv_value_2_0= ruleDecimalNumber )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:932:3: lv_value_2_0= ruleDecimalNumber
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentAccess().getValueDecimalNumberParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleDecimalNumber_in_ruleNumericalAssignment1877);
            lv_value_2_0=ruleDecimalNumber();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_2_0, 
                      		"DecimalNumber");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:948:2: ( (lv_unit_3_0= ruleUnitType ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:949:1: (lv_unit_3_0= ruleUnitType )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:949:1: (lv_unit_3_0= ruleUnitType )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:950:3: lv_unit_3_0= ruleUnitType
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentAccess().getUnitUnitTypeParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleUnitType_in_ruleNumericalAssignment1898);
            lv_unit_3_0=ruleUnitType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentRule());
              	        }
                     		set(
                     			current, 
                     			"unit",
                      		lv_unit_3_0, 
                      		"UnitType");
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
    // $ANTLR end "ruleNumericalAssignment"


    // $ANTLR start "entryRuleNumericalAssignmentGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:974:1: entryRuleNumericalAssignmentGroup returns [EObject current=null] : iv_ruleNumericalAssignmentGroup= ruleNumericalAssignmentGroup EOF ;
    public final EObject entryRuleNumericalAssignmentGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumericalAssignmentGroup = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:975:2: (iv_ruleNumericalAssignmentGroup= ruleNumericalAssignmentGroup EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:976:2: iv_ruleNumericalAssignmentGroup= ruleNumericalAssignmentGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNumericalAssignmentGroupRule()); 
            }
            pushFollow(FOLLOW_ruleNumericalAssignmentGroup_in_entryRuleNumericalAssignmentGroup1934);
            iv_ruleNumericalAssignmentGroup=ruleNumericalAssignmentGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNumericalAssignmentGroup; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNumericalAssignmentGroup1944); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNumericalAssignmentGroup"


    // $ANTLR start "ruleNumericalAssignmentGroup"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:983:1: ruleNumericalAssignmentGroup returns [EObject current=null] : (this_OPENING_SQUARE_BRACKET_0= RULE_OPENING_SQUARE_BRACKET ( (lv_ingredientConcept_1_0= ruleConcept ) ) this_EQUAL_SIGN_2= RULE_EQUAL_SIGN ( (lv_substance_3_0= ruleRValue ) ) this_COMMA_4= RULE_COMMA ( (lv_numericValue_5_0= ruleNumericalAssignment ) ) this_CLOSING_SQUARE_BRACKET_6= RULE_CLOSING_SQUARE_BRACKET ) ;
    public final EObject ruleNumericalAssignmentGroup() throws RecognitionException {
        EObject current = null;

        Token this_OPENING_SQUARE_BRACKET_0=null;
        Token this_EQUAL_SIGN_2=null;
        Token this_COMMA_4=null;
        Token this_CLOSING_SQUARE_BRACKET_6=null;
        EObject lv_ingredientConcept_1_0 = null;

        EObject lv_substance_3_0 = null;

        EObject lv_numericValue_5_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:986:28: ( (this_OPENING_SQUARE_BRACKET_0= RULE_OPENING_SQUARE_BRACKET ( (lv_ingredientConcept_1_0= ruleConcept ) ) this_EQUAL_SIGN_2= RULE_EQUAL_SIGN ( (lv_substance_3_0= ruleRValue ) ) this_COMMA_4= RULE_COMMA ( (lv_numericValue_5_0= ruleNumericalAssignment ) ) this_CLOSING_SQUARE_BRACKET_6= RULE_CLOSING_SQUARE_BRACKET ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:987:1: (this_OPENING_SQUARE_BRACKET_0= RULE_OPENING_SQUARE_BRACKET ( (lv_ingredientConcept_1_0= ruleConcept ) ) this_EQUAL_SIGN_2= RULE_EQUAL_SIGN ( (lv_substance_3_0= ruleRValue ) ) this_COMMA_4= RULE_COMMA ( (lv_numericValue_5_0= ruleNumericalAssignment ) ) this_CLOSING_SQUARE_BRACKET_6= RULE_CLOSING_SQUARE_BRACKET )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:987:1: (this_OPENING_SQUARE_BRACKET_0= RULE_OPENING_SQUARE_BRACKET ( (lv_ingredientConcept_1_0= ruleConcept ) ) this_EQUAL_SIGN_2= RULE_EQUAL_SIGN ( (lv_substance_3_0= ruleRValue ) ) this_COMMA_4= RULE_COMMA ( (lv_numericValue_5_0= ruleNumericalAssignment ) ) this_CLOSING_SQUARE_BRACKET_6= RULE_CLOSING_SQUARE_BRACKET )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:987:2: this_OPENING_SQUARE_BRACKET_0= RULE_OPENING_SQUARE_BRACKET ( (lv_ingredientConcept_1_0= ruleConcept ) ) this_EQUAL_SIGN_2= RULE_EQUAL_SIGN ( (lv_substance_3_0= ruleRValue ) ) this_COMMA_4= RULE_COMMA ( (lv_numericValue_5_0= ruleNumericalAssignment ) ) this_CLOSING_SQUARE_BRACKET_6= RULE_CLOSING_SQUARE_BRACKET
            {
            this_OPENING_SQUARE_BRACKET_0=(Token)match(input,RULE_OPENING_SQUARE_BRACKET,FOLLOW_RULE_OPENING_SQUARE_BRACKET_in_ruleNumericalAssignmentGroup1980); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_OPENING_SQUARE_BRACKET_0, grammarAccess.getNumericalAssignmentGroupAccess().getOPENING_SQUARE_BRACKETTerminalRuleCall_0()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:991:1: ( (lv_ingredientConcept_1_0= ruleConcept ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:992:1: (lv_ingredientConcept_1_0= ruleConcept )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:992:1: (lv_ingredientConcept_1_0= ruleConcept )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:993:3: lv_ingredientConcept_1_0= ruleConcept
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentGroupAccess().getIngredientConceptConceptParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleConcept_in_ruleNumericalAssignmentGroup2000);
            lv_ingredientConcept_1_0=ruleConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentGroupRule());
              	        }
                     		set(
                     			current, 
                     			"ingredientConcept",
                      		lv_ingredientConcept_1_0, 
                      		"Concept");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_EQUAL_SIGN_2=(Token)match(input,RULE_EQUAL_SIGN,FOLLOW_RULE_EQUAL_SIGN_in_ruleNumericalAssignmentGroup2011); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_EQUAL_SIGN_2, grammarAccess.getNumericalAssignmentGroupAccess().getEQUAL_SIGNTerminalRuleCall_2()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1013:1: ( (lv_substance_3_0= ruleRValue ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1014:1: (lv_substance_3_0= ruleRValue )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1014:1: (lv_substance_3_0= ruleRValue )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1015:3: lv_substance_3_0= ruleRValue
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentGroupAccess().getSubstanceRValueParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleRValue_in_ruleNumericalAssignmentGroup2031);
            lv_substance_3_0=ruleRValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentGroupRule());
              	        }
                     		set(
                     			current, 
                     			"substance",
                      		lv_substance_3_0, 
                      		"RValue");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_COMMA_4=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleNumericalAssignmentGroup2042); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_COMMA_4, grammarAccess.getNumericalAssignmentGroupAccess().getCOMMATerminalRuleCall_4()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1035:1: ( (lv_numericValue_5_0= ruleNumericalAssignment ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1036:1: (lv_numericValue_5_0= ruleNumericalAssignment )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1036:1: (lv_numericValue_5_0= ruleNumericalAssignment )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1037:3: lv_numericValue_5_0= ruleNumericalAssignment
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNumericalAssignmentGroupAccess().getNumericValueNumericalAssignmentParserRuleCall_5_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleNumericalAssignment_in_ruleNumericalAssignmentGroup2062);
            lv_numericValue_5_0=ruleNumericalAssignment();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNumericalAssignmentGroupRule());
              	        }
                     		set(
                     			current, 
                     			"numericValue",
                      		lv_numericValue_5_0, 
                      		"NumericalAssignment");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_CLOSING_SQUARE_BRACKET_6=(Token)match(input,RULE_CLOSING_SQUARE_BRACKET,FOLLOW_RULE_CLOSING_SQUARE_BRACKET_in_ruleNumericalAssignmentGroup2073); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_CLOSING_SQUARE_BRACKET_6, grammarAccess.getNumericalAssignmentGroupAccess().getCLOSING_SQUARE_BRACKETTerminalRuleCall_6()); 
                  
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
    // $ANTLR end "ruleNumericalAssignmentGroup"


    // $ANTLR start "entryRuleRValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1065:1: entryRuleRValue returns [EObject current=null] : iv_ruleRValue= ruleRValue EOF ;
    public final EObject entryRuleRValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRValue = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1066:2: (iv_ruleRValue= ruleRValue EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1067:2: iv_ruleRValue= ruleRValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRValueRule()); 
            }
            pushFollow(FOLLOW_ruleRValue_in_entryRuleRValue2108);
            iv_ruleRValue=ruleRValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRValue; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRValue2118); if (state.failed) return current;

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
    // $ANTLR end "entryRuleRValue"


    // $ANTLR start "ruleRValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1074:1: ruleRValue returns [EObject current=null] : this_Or_0= ruleOr ;
    public final EObject ruleRValue() throws RecognitionException {
        EObject current = null;

        EObject this_Or_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1077:28: (this_Or_0= ruleOr )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1079:2: this_Or_0= ruleOr
            {
            if ( state.backtracking==0 ) {
               
              	  /* */ 
              	
            }
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getRValueAccess().getOrParserRuleCall()); 
                  
            }
            pushFollow(FOLLOW_ruleOr_in_ruleRValue2167);
            this_Or_0=ruleOr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Or_0; 
                      afterParserOrEnumRuleCall();
                  
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
    // $ANTLR end "ruleRValue"


    // $ANTLR start "entryRuleOr"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1098:1: entryRuleOr returns [EObject current=null] : iv_ruleOr= ruleOr EOF ;
    public final EObject entryRuleOr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOr = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1099:2: (iv_ruleOr= ruleOr EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1100:2: iv_ruleOr= ruleOr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrRule()); 
            }
            pushFollow(FOLLOW_ruleOr_in_entryRuleOr2201);
            iv_ruleOr=ruleOr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleOr2211); if (state.failed) return current;

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
    // $ANTLR end "entryRuleOr"


    // $ANTLR start "ruleOr"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1107:1: ruleOr returns [EObject current=null] : (this_And_0= ruleAnd ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )* ) ;
    public final EObject ruleOr() throws RecognitionException {
        EObject current = null;

        Token this_OR_TOKEN_2=null;
        EObject this_And_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1110:28: ( (this_And_0= ruleAnd ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )* ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1111:1: (this_And_0= ruleAnd ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )* )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1111:1: (this_And_0= ruleAnd ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )* )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1112:2: this_And_0= ruleAnd ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )*
            {
            if ( state.backtracking==0 ) {
               
              	  /* */ 
              	
            }
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getOrAccess().getAndParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleAnd_in_ruleOr2261);
            this_And_0=ruleAnd();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_And_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1123:1: ( () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==RULE_OR_TOKEN) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1123:2: () this_OR_TOKEN_2= RULE_OR_TOKEN ( (lv_right_3_0= ruleAnd ) )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1123:2: ()
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1124:2: 
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	  /* */ 
            	      	
            	    }
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getOrAccess().getOrLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    this_OR_TOKEN_2=(Token)match(input,RULE_OR_TOKEN,FOLLOW_RULE_OR_TOKEN_in_ruleOr2284); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_OR_TOKEN_2, grammarAccess.getOrAccess().getOR_TOKENTerminalRuleCall_1_1()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1136:1: ( (lv_right_3_0= ruleAnd ) )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1137:1: (lv_right_3_0= ruleAnd )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1137:1: (lv_right_3_0= ruleAnd )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1138:3: lv_right_3_0= ruleAnd
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getOrAccess().getRightAndParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAnd_in_ruleOr2304);
            	    lv_right_3_0=ruleAnd();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getOrRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"And");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
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
        }
        return current;
    }
    // $ANTLR end "ruleOr"


    // $ANTLR start "entryRuleAnd"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1162:1: entryRuleAnd returns [EObject current=null] : iv_ruleAnd= ruleAnd EOF ;
    public final EObject entryRuleAnd() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAnd = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1163:2: (iv_ruleAnd= ruleAnd EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1164:2: iv_ruleAnd= ruleAnd EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndRule()); 
            }
            pushFollow(FOLLOW_ruleAnd_in_entryRuleAnd2342);
            iv_ruleAnd=ruleAnd();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAnd; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAnd2352); if (state.failed) return current;

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
    // $ANTLR end "entryRuleAnd"


    // $ANTLR start "ruleAnd"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1171:1: ruleAnd returns [EObject current=null] : (this_TerminalRValue_0= ruleTerminalRValue ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )* ) ;
    public final EObject ruleAnd() throws RecognitionException {
        EObject current = null;

        Token this_AND_TOKEN_2=null;
        EObject this_TerminalRValue_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1174:28: ( (this_TerminalRValue_0= ruleTerminalRValue ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )* ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1175:1: (this_TerminalRValue_0= ruleTerminalRValue ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )* )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1175:1: (this_TerminalRValue_0= ruleTerminalRValue ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )* )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1176:2: this_TerminalRValue_0= ruleTerminalRValue ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )*
            {
            if ( state.backtracking==0 ) {
               
              	  /* */ 
              	
            }
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAndAccess().getTerminalRValueParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleTerminalRValue_in_ruleAnd2402);
            this_TerminalRValue_0=ruleTerminalRValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_TerminalRValue_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1187:1: ( () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==RULE_AND_TOKEN) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1187:2: () this_AND_TOKEN_2= RULE_AND_TOKEN ( (lv_right_3_0= ruleTerminalRValue ) )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1187:2: ()
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1188:2: 
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	  /* */ 
            	      	
            	    }
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAndAccess().getAndLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    this_AND_TOKEN_2=(Token)match(input,RULE_AND_TOKEN,FOLLOW_RULE_AND_TOKEN_in_ruleAnd2425); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_AND_TOKEN_2, grammarAccess.getAndAccess().getAND_TOKENTerminalRuleCall_1_1()); 
            	          
            	    }
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1200:1: ( (lv_right_3_0= ruleTerminalRValue ) )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1201:1: (lv_right_3_0= ruleTerminalRValue )
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1201:1: (lv_right_3_0= ruleTerminalRValue )
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1202:3: lv_right_3_0= ruleTerminalRValue
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAndAccess().getRightTerminalRValueParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleTerminalRValue_in_ruleAnd2445);
            	    lv_right_3_0=ruleTerminalRValue();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAndRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"TerminalRValue");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
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
        }
        return current;
    }
    // $ANTLR end "ruleAnd"


    // $ANTLR start "entryRuleNegatableSubExpression"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1226:1: entryRuleNegatableSubExpression returns [EObject current=null] : iv_ruleNegatableSubExpression= ruleNegatableSubExpression EOF ;
    public final EObject entryRuleNegatableSubExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNegatableSubExpression = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1227:2: (iv_ruleNegatableSubExpression= ruleNegatableSubExpression EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1228:2: iv_ruleNegatableSubExpression= ruleNegatableSubExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNegatableSubExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleNegatableSubExpression_in_entryRuleNegatableSubExpression2483);
            iv_ruleNegatableSubExpression=ruleNegatableSubExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNegatableSubExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNegatableSubExpression2493); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNegatableSubExpression"


    // $ANTLR start "ruleNegatableSubExpression"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1235:1: ruleNegatableSubExpression returns [EObject current=null] : ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET ( (lv_expression_2_0= ruleExpression ) ) this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) ;
    public final EObject ruleNegatableSubExpression() throws RecognitionException {
        EObject current = null;

        Token lv_negated_0_0=null;
        Token this_OPENING_ROUND_BRACKET_1=null;
        Token this_CLOSING_ROUND_BRACKET_3=null;
        EObject lv_expression_2_0 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1238:28: ( ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET ( (lv_expression_2_0= ruleExpression ) ) this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1239:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET ( (lv_expression_2_0= ruleExpression ) ) this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1239:1: ( ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET ( (lv_expression_2_0= ruleExpression ) ) this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1239:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )? this_OPENING_ROUND_BRACKET_1= RULE_OPENING_ROUND_BRACKET ( (lv_expression_2_0= ruleExpression ) ) this_CLOSING_ROUND_BRACKET_3= RULE_CLOSING_ROUND_BRACKET
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1239:2: ( (lv_negated_0_0= RULE_NOT_TOKEN ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_NOT_TOKEN) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1240:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1240:1: (lv_negated_0_0= RULE_NOT_TOKEN )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1241:3: lv_negated_0_0= RULE_NOT_TOKEN
                    {
                    lv_negated_0_0=(Token)match(input,RULE_NOT_TOKEN,FOLLOW_RULE_NOT_TOKEN_in_ruleNegatableSubExpression2535); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_negated_0_0, grammarAccess.getNegatableSubExpressionAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getNegatableSubExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"negated",
                              		true, 
                              		"NOT_TOKEN");
                      	    
                    }

                    }


                    }
                    break;

            }

            this_OPENING_ROUND_BRACKET_1=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleNegatableSubExpression2552); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_OPENING_ROUND_BRACKET_1, grammarAccess.getNegatableSubExpressionAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_1()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1261:1: ( (lv_expression_2_0= ruleExpression ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1262:1: (lv_expression_2_0= ruleExpression )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1262:1: (lv_expression_2_0= ruleExpression )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1263:3: lv_expression_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getNegatableSubExpressionAccess().getExpressionExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleNegatableSubExpression2572);
            lv_expression_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getNegatableSubExpressionRule());
              	        }
                     		set(
                     			current, 
                     			"expression",
                      		lv_expression_2_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            this_CLOSING_ROUND_BRACKET_3=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleNegatableSubExpression2583); if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_CLOSING_ROUND_BRACKET_3, grammarAccess.getNegatableSubExpressionAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_3()); 
                  
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
    // $ANTLR end "ruleNegatableSubExpression"


    // $ANTLR start "entryRuleTerminalRValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1291:1: entryRuleTerminalRValue returns [EObject current=null] : iv_ruleTerminalRValue= ruleTerminalRValue EOF ;
    public final EObject entryRuleTerminalRValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTerminalRValue = null;


        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1292:2: (iv_ruleTerminalRValue= ruleTerminalRValue EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1293:2: iv_ruleTerminalRValue= ruleTerminalRValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTerminalRValueRule()); 
            }
            pushFollow(FOLLOW_ruleTerminalRValue_in_entryRuleTerminalRValue2618);
            iv_ruleTerminalRValue=ruleTerminalRValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerminalRValue; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerminalRValue2628); if (state.failed) return current;

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
    // $ANTLR end "entryRuleTerminalRValue"


    // $ANTLR start "ruleTerminalRValue"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1300:1: ruleTerminalRValue returns [EObject current=null] : ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) | this_NegatableSubExpression_3= ruleNegatableSubExpression | this_LValue_4= ruleLValue ) ;
    public final EObject ruleTerminalRValue() throws RecognitionException {
        EObject current = null;

        Token this_OPENING_ROUND_BRACKET_0=null;
        Token this_CLOSING_ROUND_BRACKET_2=null;
        EObject this_RValue_1 = null;

        EObject this_NegatableSubExpression_3 = null;

        EObject this_LValue_4 = null;


         enterRule(); 
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1303:28: ( ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) | this_NegatableSubExpression_3= ruleNegatableSubExpression | this_LValue_4= ruleLValue ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:1: ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) | this_NegatableSubExpression_3= ruleNegatableSubExpression | this_LValue_4= ruleLValue )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:1: ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) | this_NegatableSubExpression_3= ruleNegatableSubExpression | this_LValue_4= ruleLValue )
            int alt23=3;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:2: (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:2: (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:3: this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET
                    {
                    this_OPENING_ROUND_BRACKET_0=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTerminalRValue2665); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_ROUND_BRACKET_0, grammarAccess.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0()); 
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getTerminalRValueAccess().getRValueParserRuleCall_0_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleRValue_in_ruleTerminalRValue2689);
                    this_RValue_1=ruleRValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_RValue_1; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    this_CLOSING_ROUND_BRACKET_2=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTerminalRValue2699); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_ROUND_BRACKET_2, grammarAccess.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2()); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1326:2: this_NegatableSubExpression_3= ruleNegatableSubExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getTerminalRValueAccess().getNegatableSubExpressionParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleNegatableSubExpression_in_ruleTerminalRValue2730);
                    this_NegatableSubExpression_3=ruleNegatableSubExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_NegatableSubExpression_3; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1339:2: this_LValue_4= ruleLValue
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getTerminalRValueAccess().getLValueParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleLValue_in_ruleTerminalRValue2760);
                    this_LValue_4=ruleLValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_LValue_4; 
                              afterParserOrEnumRuleCall();
                          
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
    // $ANTLR end "ruleTerminalRValue"


    // $ANTLR start "entryRuleTerm"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1358:1: entryRuleTerm returns [String current=null] : iv_ruleTerm= ruleTerm EOF ;
    public final String entryRuleTerm() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTerm = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1362:2: (iv_ruleTerm= ruleTerm EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1363:2: iv_ruleTerm= ruleTerm EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTermRule()); 
            }
            pushFollow(FOLLOW_ruleTerm_in_entryRuleTerm2802);
            iv_ruleTerm=ruleTerm();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerm.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerm2813); if (state.failed) return current;

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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1373:1: ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) ;
    public final AntlrDatatypeRuleToken ruleTerm() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WS_1=null;
        AntlrDatatypeRuleToken this_TermCharacter_0 = null;

        AntlrDatatypeRuleToken this_TermCharacter_2 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1377:28: ( ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1378:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1378:1: ( (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )* )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1378:2: (this_TermCharacter_0= ruleTermCharacter )+ ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1378:2: (this_TermCharacter_0= ruleTermCharacter )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=RULE_PLUS_SIGN && LA24_0<=RULE_CARET)||(LA24_0>=RULE_SUBTYPE && LA24_0<=RULE_INCLUSIVE_SUBTYPE)||(LA24_0>=RULE_OPENING_CURLY_BRACKET && LA24_0<=RULE_OTHER_ALLOWED_TERM_CHARACTER)||LA24_0==36) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1379:5: this_TermCharacter_0= ruleTermCharacter
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	              newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0()); 
            	          
            	    }
            	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm2865);
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
            	    if ( cnt24 >= 1 ) break loop24;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1389:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*
            loop27:
            do {
                int alt27=2;
                alt27 = dfa27.predict(input);
                switch (alt27) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1389:4: (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+
            	    {
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1389:4: (this_WS_1= RULE_WS )+
            	    int cnt25=0;
            	    loop25:
            	    do {
            	        int alt25=2;
            	        int LA25_0 = input.LA(1);

            	        if ( (LA25_0==RULE_WS) ) {
            	            alt25=1;
            	        }


            	        switch (alt25) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1389:9: this_WS_1= RULE_WS
            	    	    {
            	    	    this_WS_1=(Token)match(input,RULE_WS,FOLLOW_RULE_WS_in_ruleTerm2889); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      		current.merge(this_WS_1);
            	    	          
            	    	    }
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	          newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0()); 
            	    	          
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt25 >= 1 ) break loop25;
            	    	    if (state.backtracking>0) {state.failed=true; return current;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(25, input);
            	                throw eee;
            	        }
            	        cnt25++;
            	    } while (true);

            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1396:3: (this_TermCharacter_2= ruleTermCharacter )+
            	    int cnt26=0;
            	    loop26:
            	    do {
            	        int alt26=2;
            	        int LA26_0 = input.LA(1);

            	        if ( ((LA26_0>=RULE_PLUS_SIGN && LA26_0<=RULE_CARET)||(LA26_0>=RULE_SUBTYPE && LA26_0<=RULE_INCLUSIVE_SUBTYPE)||(LA26_0>=RULE_OPENING_CURLY_BRACKET && LA26_0<=RULE_OTHER_ALLOWED_TERM_CHARACTER)||LA26_0==36) ) {
            	            alt26=1;
            	        }


            	        switch (alt26) {
            	    	case 1 :
            	    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1397:5: this_TermCharacter_2= ruleTermCharacter
            	    	    {
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	              newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1()); 
            	    	          
            	    	    }
            	    	    pushFollow(FOLLOW_ruleTermCharacter_in_ruleTerm2919);
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
            	    	    if ( cnt26 >= 1 ) break loop26;
            	    	    if (state.backtracking>0) {state.failed=true; return current;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(26, input);
            	                throw eee;
            	        }
            	        cnt26++;
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop27;
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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1418:1: entryRuleConceptId returns [String current=null] : iv_ruleConceptId= ruleConceptId EOF ;
    public final String entryRuleConceptId() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleConceptId = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1422:2: (iv_ruleConceptId= ruleConceptId EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1423:2: iv_ruleConceptId= ruleConceptId EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptIdRule()); 
            }
            pushFollow(FOLLOW_ruleConceptId_in_entryRuleConceptId2979);
            iv_ruleConceptId=ruleConceptId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptId.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleConceptId2990); if (state.failed) return current;

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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1433:1: ruleConceptId returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1437:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1438:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1438:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1438:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3034); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current.merge(this_DIGIT_NONZERO_0);
                  
            }
            if ( state.backtracking==0 ) {
               
                  newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                  
            }
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1445:1: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_DIGIT_NONZERO) ) {
                alt28=1;
            }
            else if ( (LA28_0==RULE_ZERO) ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1445:6: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3055); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_1_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1453:10: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId3081); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_2, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_1_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1460:2: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_DIGIT_NONZERO) ) {
                alt29=1;
            }
            else if ( (LA29_0==RULE_ZERO) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1460:7: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3103); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_3);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_2_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1468:10: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId3129); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_4, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_2_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1475:2: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==RULE_DIGIT_NONZERO) ) {
                alt30=1;
            }
            else if ( (LA30_0==RULE_ZERO) ) {
                alt30=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1475:7: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3151); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_5);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_3_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1483:10: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId3177); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_6);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_6, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_3_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1490:2: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_DIGIT_NONZERO) ) {
                alt31=1;
            }
            else if ( (LA31_0==RULE_ZERO) ) {
                alt31=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1490:7: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3199); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_7);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_4_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1498:10: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId3225); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_8);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_8, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_4_1()); 
                          
                    }

                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1505:2: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt32=0;
            loop32:
            do {
                int alt32=3;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==RULE_DIGIT_NONZERO) ) {
                    alt32=1;
                }
                else if ( (LA32_0==RULE_ZERO) ) {
                    alt32=2;
                }


                switch (alt32) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1505:7: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3247); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_DIGIT_NONZERO_9);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getConceptIdAccess().getDIGIT_NONZEROTerminalRuleCall_5_0()); 
            	          
            	    }

            	    }
            	    break;
            	case 2 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1513:10: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleConceptId3273); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      		current.merge(this_ZERO_10);
            	          
            	    }
            	    if ( state.backtracking==0 ) {
            	       
            	          newLeafNode(this_ZERO_10, grammarAccess.getConceptIdAccess().getZEROTerminalRuleCall_5_1()); 
            	          
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1531:1: entryRuleTermCharacter returns [String current=null] : iv_ruleTermCharacter= ruleTermCharacter EOF ;
    public final String entryRuleTermCharacter() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTermCharacter = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1535:2: (iv_ruleTermCharacter= ruleTermCharacter EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1536:2: iv_ruleTermCharacter= ruleTermCharacter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTermCharacterRule()); 
            }
            pushFollow(FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter3331);
            iv_ruleTermCharacter=ruleTermCharacter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTermCharacter.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTermCharacter3342); if (state.failed) return current;

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
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1546:1: ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_SUBTYPE_3= RULE_SUBTYPE | this_INCLUSIVE_SUBTYPE_4= RULE_INCLUSIVE_SUBTYPE | this_COMMA_5= RULE_COMMA | this_CARET_6= RULE_CARET | this_NOT_TOKEN_7= RULE_NOT_TOKEN | this_OPTIONAL_8= RULE_OPTIONAL | this_OPENING_CURLY_BRACKET_9= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_10= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_11= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_12= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_13= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_14= RULE_PLUS_SIGN | this_COLON_15= RULE_COLON | this_PERIOD_16= RULE_PERIOD | this_UnitType_17= ruleUnitType | this_AND_TOKEN_18= RULE_AND_TOKEN | this_OR_TOKEN_19= RULE_OR_TOKEN | this_OPENING_SQUARE_BRACKET_20= RULE_OPENING_SQUARE_BRACKET | this_CLOSING_SQUARE_BRACKET_21= RULE_CLOSING_SQUARE_BRACKET | this_OTHER_ALLOWED_TERM_CHARACTER_22= RULE_OTHER_ALLOWED_TERM_CHARACTER ) ;
    public final AntlrDatatypeRuleToken ruleTermCharacter() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DIGIT_NONZERO_0=null;
        Token this_ZERO_1=null;
        Token this_LETTER_2=null;
        Token this_SUBTYPE_3=null;
        Token this_INCLUSIVE_SUBTYPE_4=null;
        Token this_COMMA_5=null;
        Token this_CARET_6=null;
        Token this_NOT_TOKEN_7=null;
        Token this_OPTIONAL_8=null;
        Token this_OPENING_CURLY_BRACKET_9=null;
        Token this_CLOSING_CURLY_BRACKET_10=null;
        Token this_EQUAL_SIGN_11=null;
        Token this_OPENING_ROUND_BRACKET_12=null;
        Token this_CLOSING_ROUND_BRACKET_13=null;
        Token this_PLUS_SIGN_14=null;
        Token this_COLON_15=null;
        Token this_PERIOD_16=null;
        Token this_AND_TOKEN_18=null;
        Token this_OR_TOKEN_19=null;
        Token this_OPENING_SQUARE_BRACKET_20=null;
        Token this_CLOSING_SQUARE_BRACKET_21=null;
        Token this_OTHER_ALLOWED_TERM_CHARACTER_22=null;
        AntlrDatatypeRuleToken this_UnitType_17 = null;


         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1550:28: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_SUBTYPE_3= RULE_SUBTYPE | this_INCLUSIVE_SUBTYPE_4= RULE_INCLUSIVE_SUBTYPE | this_COMMA_5= RULE_COMMA | this_CARET_6= RULE_CARET | this_NOT_TOKEN_7= RULE_NOT_TOKEN | this_OPTIONAL_8= RULE_OPTIONAL | this_OPENING_CURLY_BRACKET_9= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_10= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_11= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_12= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_13= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_14= RULE_PLUS_SIGN | this_COLON_15= RULE_COLON | this_PERIOD_16= RULE_PERIOD | this_UnitType_17= ruleUnitType | this_AND_TOKEN_18= RULE_AND_TOKEN | this_OR_TOKEN_19= RULE_OR_TOKEN | this_OPENING_SQUARE_BRACKET_20= RULE_OPENING_SQUARE_BRACKET | this_CLOSING_SQUARE_BRACKET_21= RULE_CLOSING_SQUARE_BRACKET | this_OTHER_ALLOWED_TERM_CHARACTER_22= RULE_OTHER_ALLOWED_TERM_CHARACTER ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1551:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_SUBTYPE_3= RULE_SUBTYPE | this_INCLUSIVE_SUBTYPE_4= RULE_INCLUSIVE_SUBTYPE | this_COMMA_5= RULE_COMMA | this_CARET_6= RULE_CARET | this_NOT_TOKEN_7= RULE_NOT_TOKEN | this_OPTIONAL_8= RULE_OPTIONAL | this_OPENING_CURLY_BRACKET_9= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_10= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_11= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_12= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_13= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_14= RULE_PLUS_SIGN | this_COLON_15= RULE_COLON | this_PERIOD_16= RULE_PERIOD | this_UnitType_17= ruleUnitType | this_AND_TOKEN_18= RULE_AND_TOKEN | this_OR_TOKEN_19= RULE_OR_TOKEN | this_OPENING_SQUARE_BRACKET_20= RULE_OPENING_SQUARE_BRACKET | this_CLOSING_SQUARE_BRACKET_21= RULE_CLOSING_SQUARE_BRACKET | this_OTHER_ALLOWED_TERM_CHARACTER_22= RULE_OTHER_ALLOWED_TERM_CHARACTER )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1551:1: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO | this_ZERO_1= RULE_ZERO | this_LETTER_2= RULE_LETTER | this_SUBTYPE_3= RULE_SUBTYPE | this_INCLUSIVE_SUBTYPE_4= RULE_INCLUSIVE_SUBTYPE | this_COMMA_5= RULE_COMMA | this_CARET_6= RULE_CARET | this_NOT_TOKEN_7= RULE_NOT_TOKEN | this_OPTIONAL_8= RULE_OPTIONAL | this_OPENING_CURLY_BRACKET_9= RULE_OPENING_CURLY_BRACKET | this_CLOSING_CURLY_BRACKET_10= RULE_CLOSING_CURLY_BRACKET | this_EQUAL_SIGN_11= RULE_EQUAL_SIGN | this_OPENING_ROUND_BRACKET_12= RULE_OPENING_ROUND_BRACKET | this_CLOSING_ROUND_BRACKET_13= RULE_CLOSING_ROUND_BRACKET | this_PLUS_SIGN_14= RULE_PLUS_SIGN | this_COLON_15= RULE_COLON | this_PERIOD_16= RULE_PERIOD | this_UnitType_17= ruleUnitType | this_AND_TOKEN_18= RULE_AND_TOKEN | this_OR_TOKEN_19= RULE_OR_TOKEN | this_OPENING_SQUARE_BRACKET_20= RULE_OPENING_SQUARE_BRACKET | this_CLOSING_SQUARE_BRACKET_21= RULE_CLOSING_SQUARE_BRACKET | this_OTHER_ALLOWED_TERM_CHARACTER_22= RULE_OTHER_ALLOWED_TERM_CHARACTER )
            int alt33=23;
            switch ( input.LA(1) ) {
            case RULE_DIGIT_NONZERO:
                {
                alt33=1;
                }
                break;
            case RULE_ZERO:
                {
                alt33=2;
                }
                break;
            case RULE_LETTER:
                {
                alt33=3;
                }
                break;
            case RULE_SUBTYPE:
                {
                alt33=4;
                }
                break;
            case RULE_INCLUSIVE_SUBTYPE:
                {
                alt33=5;
                }
                break;
            case RULE_COMMA:
                {
                alt33=6;
                }
                break;
            case RULE_CARET:
                {
                alt33=7;
                }
                break;
            case RULE_NOT_TOKEN:
                {
                alt33=8;
                }
                break;
            case RULE_OPTIONAL:
                {
                alt33=9;
                }
                break;
            case RULE_OPENING_CURLY_BRACKET:
                {
                alt33=10;
                }
                break;
            case RULE_CLOSING_CURLY_BRACKET:
                {
                alt33=11;
                }
                break;
            case RULE_EQUAL_SIGN:
                {
                alt33=12;
                }
                break;
            case RULE_OPENING_ROUND_BRACKET:
                {
                alt33=13;
                }
                break;
            case RULE_CLOSING_ROUND_BRACKET:
                {
                alt33=14;
                }
                break;
            case RULE_PLUS_SIGN:
                {
                alt33=15;
                }
                break;
            case RULE_COLON:
                {
                alt33=16;
                }
                break;
            case RULE_PERIOD:
                {
                alt33=17;
                }
                break;
            case 36:
                {
                alt33=18;
                }
                break;
            case RULE_AND_TOKEN:
                {
                alt33=19;
                }
                break;
            case RULE_OR_TOKEN:
                {
                alt33=20;
                }
                break;
            case RULE_OPENING_SQUARE_BRACKET:
                {
                alt33=21;
                }
                break;
            case RULE_CLOSING_SQUARE_BRACKET:
                {
                alt33=22;
                }
                break;
            case RULE_OTHER_ALLOWED_TERM_CHARACTER:
                {
                alt33=23;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1551:6: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter3386); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_0);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1559:10: this_ZERO_1= RULE_ZERO
                    {
                    this_ZERO_1=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleTermCharacter3412); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_1, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1567:10: this_LETTER_2= RULE_LETTER
                    {
                    this_LETTER_2=(Token)match(input,RULE_LETTER,FOLLOW_RULE_LETTER_in_ruleTermCharacter3438); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_LETTER_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_LETTER_2, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1575:10: this_SUBTYPE_3= RULE_SUBTYPE
                    {
                    this_SUBTYPE_3=(Token)match(input,RULE_SUBTYPE,FOLLOW_RULE_SUBTYPE_in_ruleTermCharacter3464); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_SUBTYPE_3);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_SUBTYPE_3, grammarAccess.getTermCharacterAccess().getSUBTYPETerminalRuleCall_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1583:10: this_INCLUSIVE_SUBTYPE_4= RULE_INCLUSIVE_SUBTYPE
                    {
                    this_INCLUSIVE_SUBTYPE_4=(Token)match(input,RULE_INCLUSIVE_SUBTYPE,FOLLOW_RULE_INCLUSIVE_SUBTYPE_in_ruleTermCharacter3490); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_INCLUSIVE_SUBTYPE_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_INCLUSIVE_SUBTYPE_4, grammarAccess.getTermCharacterAccess().getINCLUSIVE_SUBTYPETerminalRuleCall_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1591:10: this_COMMA_5= RULE_COMMA
                    {
                    this_COMMA_5=(Token)match(input,RULE_COMMA,FOLLOW_RULE_COMMA_in_ruleTermCharacter3516); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_COMMA_5);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COMMA_5, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1599:10: this_CARET_6= RULE_CARET
                    {
                    this_CARET_6=(Token)match(input,RULE_CARET,FOLLOW_RULE_CARET_in_ruleTermCharacter3542); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CARET_6);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CARET_6, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1607:10: this_NOT_TOKEN_7= RULE_NOT_TOKEN
                    {
                    this_NOT_TOKEN_7=(Token)match(input,RULE_NOT_TOKEN,FOLLOW_RULE_NOT_TOKEN_in_ruleTermCharacter3568); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_NOT_TOKEN_7);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_NOT_TOKEN_7, grammarAccess.getTermCharacterAccess().getNOT_TOKENTerminalRuleCall_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1615:10: this_OPTIONAL_8= RULE_OPTIONAL
                    {
                    this_OPTIONAL_8=(Token)match(input,RULE_OPTIONAL,FOLLOW_RULE_OPTIONAL_in_ruleTermCharacter3594); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPTIONAL_8);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPTIONAL_8, grammarAccess.getTermCharacterAccess().getOPTIONALTerminalRuleCall_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1623:10: this_OPENING_CURLY_BRACKET_9= RULE_OPENING_CURLY_BRACKET
                    {
                    this_OPENING_CURLY_BRACKET_9=(Token)match(input,RULE_OPENING_CURLY_BRACKET,FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleTermCharacter3620); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPENING_CURLY_BRACKET_9);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_CURLY_BRACKET_9, grammarAccess.getTermCharacterAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1631:10: this_CLOSING_CURLY_BRACKET_10= RULE_CLOSING_CURLY_BRACKET
                    {
                    this_CLOSING_CURLY_BRACKET_10=(Token)match(input,RULE_CLOSING_CURLY_BRACKET,FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleTermCharacter3646); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CLOSING_CURLY_BRACKET_10);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_CURLY_BRACKET_10, grammarAccess.getTermCharacterAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1639:10: this_EQUAL_SIGN_11= RULE_EQUAL_SIGN
                    {
                    this_EQUAL_SIGN_11=(Token)match(input,RULE_EQUAL_SIGN,FOLLOW_RULE_EQUAL_SIGN_in_ruleTermCharacter3672); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_EQUAL_SIGN_11);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_EQUAL_SIGN_11, grammarAccess.getTermCharacterAccess().getEQUAL_SIGNTerminalRuleCall_11()); 
                          
                    }

                    }
                    break;
                case 13 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1647:10: this_OPENING_ROUND_BRACKET_12= RULE_OPENING_ROUND_BRACKET
                    {
                    this_OPENING_ROUND_BRACKET_12=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTermCharacter3698); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPENING_ROUND_BRACKET_12);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_ROUND_BRACKET_12, grammarAccess.getTermCharacterAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_12()); 
                          
                    }

                    }
                    break;
                case 14 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1655:10: this_CLOSING_ROUND_BRACKET_13= RULE_CLOSING_ROUND_BRACKET
                    {
                    this_CLOSING_ROUND_BRACKET_13=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTermCharacter3724); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CLOSING_ROUND_BRACKET_13);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_ROUND_BRACKET_13, grammarAccess.getTermCharacterAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_13()); 
                          
                    }

                    }
                    break;
                case 15 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1663:10: this_PLUS_SIGN_14= RULE_PLUS_SIGN
                    {
                    this_PLUS_SIGN_14=(Token)match(input,RULE_PLUS_SIGN,FOLLOW_RULE_PLUS_SIGN_in_ruleTermCharacter3750); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_PLUS_SIGN_14);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PLUS_SIGN_14, grammarAccess.getTermCharacterAccess().getPLUS_SIGNTerminalRuleCall_14()); 
                          
                    }

                    }
                    break;
                case 16 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1671:10: this_COLON_15= RULE_COLON
                    {
                    this_COLON_15=(Token)match(input,RULE_COLON,FOLLOW_RULE_COLON_in_ruleTermCharacter3776); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_COLON_15);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_COLON_15, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_15()); 
                          
                    }

                    }
                    break;
                case 17 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1679:10: this_PERIOD_16= RULE_PERIOD
                    {
                    this_PERIOD_16=(Token)match(input,RULE_PERIOD,FOLLOW_RULE_PERIOD_in_ruleTermCharacter3802); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_PERIOD_16);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PERIOD_16, grammarAccess.getTermCharacterAccess().getPERIODTerminalRuleCall_16()); 
                          
                    }

                    }
                    break;
                case 18 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1688:5: this_UnitType_17= ruleUnitType
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getTermCharacterAccess().getUnitTypeParserRuleCall_17()); 
                          
                    }
                    pushFollow(FOLLOW_ruleUnitType_in_ruleTermCharacter3835);
                    this_UnitType_17=ruleUnitType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_UnitType_17);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 19 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1699:10: this_AND_TOKEN_18= RULE_AND_TOKEN
                    {
                    this_AND_TOKEN_18=(Token)match(input,RULE_AND_TOKEN,FOLLOW_RULE_AND_TOKEN_in_ruleTermCharacter3861); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_AND_TOKEN_18);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_AND_TOKEN_18, grammarAccess.getTermCharacterAccess().getAND_TOKENTerminalRuleCall_18()); 
                          
                    }

                    }
                    break;
                case 20 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1707:10: this_OR_TOKEN_19= RULE_OR_TOKEN
                    {
                    this_OR_TOKEN_19=(Token)match(input,RULE_OR_TOKEN,FOLLOW_RULE_OR_TOKEN_in_ruleTermCharacter3887); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OR_TOKEN_19);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OR_TOKEN_19, grammarAccess.getTermCharacterAccess().getOR_TOKENTerminalRuleCall_19()); 
                          
                    }

                    }
                    break;
                case 21 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1715:10: this_OPENING_SQUARE_BRACKET_20= RULE_OPENING_SQUARE_BRACKET
                    {
                    this_OPENING_SQUARE_BRACKET_20=(Token)match(input,RULE_OPENING_SQUARE_BRACKET,FOLLOW_RULE_OPENING_SQUARE_BRACKET_in_ruleTermCharacter3913); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OPENING_SQUARE_BRACKET_20);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OPENING_SQUARE_BRACKET_20, grammarAccess.getTermCharacterAccess().getOPENING_SQUARE_BRACKETTerminalRuleCall_20()); 
                          
                    }

                    }
                    break;
                case 22 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1723:10: this_CLOSING_SQUARE_BRACKET_21= RULE_CLOSING_SQUARE_BRACKET
                    {
                    this_CLOSING_SQUARE_BRACKET_21=(Token)match(input,RULE_CLOSING_SQUARE_BRACKET,FOLLOW_RULE_CLOSING_SQUARE_BRACKET_in_ruleTermCharacter3939); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_CLOSING_SQUARE_BRACKET_21);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_CLOSING_SQUARE_BRACKET_21, grammarAccess.getTermCharacterAccess().getCLOSING_SQUARE_BRACKETTerminalRuleCall_21()); 
                          
                    }

                    }
                    break;
                case 23 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1731:10: this_OTHER_ALLOWED_TERM_CHARACTER_22= RULE_OTHER_ALLOWED_TERM_CHARACTER
                    {
                    this_OTHER_ALLOWED_TERM_CHARACTER_22=(Token)match(input,RULE_OTHER_ALLOWED_TERM_CHARACTER,FOLLOW_RULE_OTHER_ALLOWED_TERM_CHARACTER_in_ruleTermCharacter3965); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_OTHER_ALLOWED_TERM_CHARACTER_22);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_OTHER_ALLOWED_TERM_CHARACTER_22, grammarAccess.getTermCharacterAccess().getOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_22()); 
                          
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


    // $ANTLR start "entryRuleDecimalNumber"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1751:1: entryRuleDecimalNumber returns [String current=null] : iv_ruleDecimalNumber= ruleDecimalNumber EOF ;
    public final String entryRuleDecimalNumber() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDecimalNumber = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1755:2: (iv_ruleDecimalNumber= ruleDecimalNumber EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1756:2: iv_ruleDecimalNumber= ruleDecimalNumber EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalNumberRule()); 
            }
            pushFollow(FOLLOW_ruleDecimalNumber_in_entryRuleDecimalNumber4023);
            iv_ruleDecimalNumber=ruleDecimalNumber();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalNumber.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecimalNumber4034); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDecimalNumber"


    // $ANTLR start "ruleDecimalNumber"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1766:1: ruleDecimalNumber returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )? ) ;
    public final AntlrDatatypeRuleToken ruleDecimalNumber() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;
        Token this_PERIOD_4=null;
        Token this_DIGIT_NONZERO_5=null;
        Token this_ZERO_6=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1770:28: ( ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )? ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1771:1: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )? )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1771:1: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1771:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )?
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1771:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==RULE_ZERO) ) {
                alt35=1;
            }
            else if ( (LA35_0==RULE_DIGIT_NONZERO) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1771:7: this_ZERO_0= RULE_ZERO
                    {
                    this_ZERO_0=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleDecimalNumber4079); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_ZERO_0);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ZERO_0, grammarAccess.getDecimalNumberAccess().getZEROTerminalRuleCall_0_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1779:6: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1779:6: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1779:11: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4106); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_DIGIT_NONZERO_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getDecimalNumberAccess().getDIGIT_NONZEROTerminalRuleCall_0_1_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1786:1: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    loop34:
                    do {
                        int alt34=3;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==RULE_DIGIT_NONZERO) ) {
                            alt34=1;
                        }
                        else if ( (LA34_0==RULE_ZERO) ) {
                            alt34=2;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1786:6: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4127); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      		current.merge(this_DIGIT_NONZERO_2);
                    	          
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getDecimalNumberAccess().getDIGIT_NONZEROTerminalRuleCall_0_1_1_0()); 
                    	          
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1794:10: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleDecimalNumber4153); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      		current.merge(this_ZERO_3);
                    	          
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_ZERO_3, grammarAccess.getDecimalNumberAccess().getZEROTerminalRuleCall_0_1_1_1()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);


                    }


                    }
                    break;

            }

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1801:5: (this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+ )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==RULE_PERIOD) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1801:10: this_PERIOD_4= RULE_PERIOD (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+
                    {
                    this_PERIOD_4=(Token)match(input,RULE_PERIOD,FOLLOW_RULE_PERIOD_in_ruleDecimalNumber4178); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_PERIOD_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_PERIOD_4, grammarAccess.getDecimalNumberAccess().getPERIODTerminalRuleCall_1_0()); 
                          
                    }
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1808:1: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=3;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==RULE_DIGIT_NONZERO) ) {
                            alt36=1;
                        }
                        else if ( (LA36_0==RULE_ZERO) ) {
                            alt36=2;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1808:6: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4199); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      		current.merge(this_DIGIT_NONZERO_5);
                    	          
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getDecimalNumberAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0()); 
                    	          
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1816:10: this_ZERO_6= RULE_ZERO
                    	    {
                    	    this_ZERO_6=(Token)match(input,RULE_ZERO,FOLLOW_RULE_ZERO_in_ruleDecimalNumber4225); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      		current.merge(this_ZERO_6);
                    	          
                    	    }
                    	    if ( state.backtracking==0 ) {
                    	       
                    	          newLeafNode(this_ZERO_6, grammarAccess.getDecimalNumberAccess().getZEROTerminalRuleCall_1_1_1()); 
                    	          
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt36 >= 1 ) break loop36;
                    	    if (state.backtracking>0) {state.failed=true; return current;}
                                EarlyExitException eee =
                                    new EarlyExitException(36, input);
                                throw eee;
                        }
                        cnt36++;
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
    // $ANTLR end "ruleDecimalNumber"


    // $ANTLR start "entryRuleOperator"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1834:1: entryRuleOperator returns [String current=null] : iv_ruleOperator= ruleOperator EOF ;
    public final String entryRuleOperator() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleOperator = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1838:2: (iv_ruleOperator= ruleOperator EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1839:2: iv_ruleOperator= ruleOperator EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOperatorRule()); 
            }
            pushFollow(FOLLOW_ruleOperator_in_entryRuleOperator4285);
            iv_ruleOperator=ruleOperator();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOperator.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleOperator4296); if (state.failed) return current;

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
    // $ANTLR end "entryRuleOperator"


    // $ANTLR start "ruleOperator"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1849:1: ruleOperator returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_EQUALS_OPERATOR_0= RULE_EQUALS_OPERATOR | this_SUBTYPE_1= RULE_SUBTYPE | this_GREATER_THAN_OPERATOR_2= RULE_GREATER_THAN_OPERATOR | this_LESS_EQUALS_OPERATOR_3= RULE_LESS_EQUALS_OPERATOR | this_GREATER_EQUALS_OPERATOR_4= RULE_GREATER_EQUALS_OPERATOR | this_NOT_EQUALS_OPERATOR_5= RULE_NOT_EQUALS_OPERATOR ) ;
    public final AntlrDatatypeRuleToken ruleOperator() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_EQUALS_OPERATOR_0=null;
        Token this_SUBTYPE_1=null;
        Token this_GREATER_THAN_OPERATOR_2=null;
        Token this_LESS_EQUALS_OPERATOR_3=null;
        Token this_GREATER_EQUALS_OPERATOR_4=null;
        Token this_NOT_EQUALS_OPERATOR_5=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1853:28: ( (this_EQUALS_OPERATOR_0= RULE_EQUALS_OPERATOR | this_SUBTYPE_1= RULE_SUBTYPE | this_GREATER_THAN_OPERATOR_2= RULE_GREATER_THAN_OPERATOR | this_LESS_EQUALS_OPERATOR_3= RULE_LESS_EQUALS_OPERATOR | this_GREATER_EQUALS_OPERATOR_4= RULE_GREATER_EQUALS_OPERATOR | this_NOT_EQUALS_OPERATOR_5= RULE_NOT_EQUALS_OPERATOR ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1854:1: (this_EQUALS_OPERATOR_0= RULE_EQUALS_OPERATOR | this_SUBTYPE_1= RULE_SUBTYPE | this_GREATER_THAN_OPERATOR_2= RULE_GREATER_THAN_OPERATOR | this_LESS_EQUALS_OPERATOR_3= RULE_LESS_EQUALS_OPERATOR | this_GREATER_EQUALS_OPERATOR_4= RULE_GREATER_EQUALS_OPERATOR | this_NOT_EQUALS_OPERATOR_5= RULE_NOT_EQUALS_OPERATOR )
            {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1854:1: (this_EQUALS_OPERATOR_0= RULE_EQUALS_OPERATOR | this_SUBTYPE_1= RULE_SUBTYPE | this_GREATER_THAN_OPERATOR_2= RULE_GREATER_THAN_OPERATOR | this_LESS_EQUALS_OPERATOR_3= RULE_LESS_EQUALS_OPERATOR | this_GREATER_EQUALS_OPERATOR_4= RULE_GREATER_EQUALS_OPERATOR | this_NOT_EQUALS_OPERATOR_5= RULE_NOT_EQUALS_OPERATOR )
            int alt38=6;
            switch ( input.LA(1) ) {
            case RULE_EQUALS_OPERATOR:
                {
                alt38=1;
                }
                break;
            case RULE_SUBTYPE:
                {
                alt38=2;
                }
                break;
            case RULE_GREATER_THAN_OPERATOR:
                {
                alt38=3;
                }
                break;
            case RULE_LESS_EQUALS_OPERATOR:
                {
                alt38=4;
                }
                break;
            case RULE_GREATER_EQUALS_OPERATOR:
                {
                alt38=5;
                }
                break;
            case RULE_NOT_EQUALS_OPERATOR:
                {
                alt38=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1854:6: this_EQUALS_OPERATOR_0= RULE_EQUALS_OPERATOR
                    {
                    this_EQUALS_OPERATOR_0=(Token)match(input,RULE_EQUALS_OPERATOR,FOLLOW_RULE_EQUALS_OPERATOR_in_ruleOperator4340); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_EQUALS_OPERATOR_0);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_EQUALS_OPERATOR_0, grammarAccess.getOperatorAccess().getEQUALS_OPERATORTerminalRuleCall_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1862:10: this_SUBTYPE_1= RULE_SUBTYPE
                    {
                    this_SUBTYPE_1=(Token)match(input,RULE_SUBTYPE,FOLLOW_RULE_SUBTYPE_in_ruleOperator4366); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_SUBTYPE_1);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_SUBTYPE_1, grammarAccess.getOperatorAccess().getSUBTYPETerminalRuleCall_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1870:10: this_GREATER_THAN_OPERATOR_2= RULE_GREATER_THAN_OPERATOR
                    {
                    this_GREATER_THAN_OPERATOR_2=(Token)match(input,RULE_GREATER_THAN_OPERATOR,FOLLOW_RULE_GREATER_THAN_OPERATOR_in_ruleOperator4392); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_GREATER_THAN_OPERATOR_2);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_GREATER_THAN_OPERATOR_2, grammarAccess.getOperatorAccess().getGREATER_THAN_OPERATORTerminalRuleCall_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1878:10: this_LESS_EQUALS_OPERATOR_3= RULE_LESS_EQUALS_OPERATOR
                    {
                    this_LESS_EQUALS_OPERATOR_3=(Token)match(input,RULE_LESS_EQUALS_OPERATOR,FOLLOW_RULE_LESS_EQUALS_OPERATOR_in_ruleOperator4418); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_LESS_EQUALS_OPERATOR_3);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_LESS_EQUALS_OPERATOR_3, grammarAccess.getOperatorAccess().getLESS_EQUALS_OPERATORTerminalRuleCall_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1886:10: this_GREATER_EQUALS_OPERATOR_4= RULE_GREATER_EQUALS_OPERATOR
                    {
                    this_GREATER_EQUALS_OPERATOR_4=(Token)match(input,RULE_GREATER_EQUALS_OPERATOR,FOLLOW_RULE_GREATER_EQUALS_OPERATOR_in_ruleOperator4444); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_GREATER_EQUALS_OPERATOR_4);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_GREATER_EQUALS_OPERATOR_4, grammarAccess.getOperatorAccess().getGREATER_EQUALS_OPERATORTerminalRuleCall_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1894:10: this_NOT_EQUALS_OPERATOR_5= RULE_NOT_EQUALS_OPERATOR
                    {
                    this_NOT_EQUALS_OPERATOR_5=(Token)match(input,RULE_NOT_EQUALS_OPERATOR,FOLLOW_RULE_NOT_EQUALS_OPERATOR_in_ruleOperator4470); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      		current.merge(this_NOT_EQUALS_OPERATOR_5);
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_NOT_EQUALS_OPERATOR_5, grammarAccess.getOperatorAccess().getNOT_EQUALS_OPERATORTerminalRuleCall_5()); 
                          
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
    // $ANTLR end "ruleOperator"


    // $ANTLR start "entryRuleUnitType"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1912:1: entryRuleUnitType returns [String current=null] : iv_ruleUnitType= ruleUnitType EOF ;
    public final String entryRuleUnitType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUnitType = null;


         
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        	
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1916:2: (iv_ruleUnitType= ruleUnitType EOF )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1917:2: iv_ruleUnitType= ruleUnitType EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnitTypeRule()); 
            }
            pushFollow(FOLLOW_ruleUnitType_in_entryRuleUnitType4526);
            iv_ruleUnitType=ruleUnitType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnitType.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleUnitType4537); if (state.failed) return current;

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
    // $ANTLR end "entryRuleUnitType"


    // $ANTLR start "ruleUnitType"
    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1927:1: ruleUnitType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : kw= 'mg' ;
    public final AntlrDatatypeRuleToken ruleUnitType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
            
        try {
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1931:28: (kw= 'mg' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1933:2: kw= 'mg'
            {
            kw=(Token)match(input,36,FOLLOW_36_in_ruleUnitType4578); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      current.merge(kw);
                      newLeafNode(kw, grammarAccess.getUnitTypeAccess().getMgKeyword()); 
                  
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
    // $ANTLR end "ruleUnitType"

    // $ANTLR start synpred24_InternalESCG
    public final void synpred24_InternalESCG_fragment() throws RecognitionException {   
        Token this_OPENING_ROUND_BRACKET_0=null;
        Token this_CLOSING_ROUND_BRACKET_2=null;
        EObject this_RValue_1 = null;


        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:2: ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) )
        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:2: (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET )
        {
        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:2: (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET )
        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1304:3: this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET
        {
        this_OPENING_ROUND_BRACKET_0=(Token)match(input,RULE_OPENING_ROUND_BRACKET,FOLLOW_RULE_OPENING_ROUND_BRACKET_in_synpred24_InternalESCG2665); if (state.failed) return ;
        pushFollow(FOLLOW_ruleRValue_in_synpred24_InternalESCG2689);
        this_RValue_1=ruleRValue();

        state._fsp--;
        if (state.failed) return ;
        this_CLOSING_ROUND_BRACKET_2=(Token)match(input,RULE_CLOSING_ROUND_BRACKET,FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_synpred24_InternalESCG2699); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred24_InternalESCG

    // $ANTLR start synpred25_InternalESCG
    public final void synpred25_InternalESCG_fragment() throws RecognitionException {   
        EObject this_NegatableSubExpression_3 = null;


        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1326:2: (this_NegatableSubExpression_3= ruleNegatableSubExpression )
        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1326:2: this_NegatableSubExpression_3= ruleNegatableSubExpression
        {
        if ( state.backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleNegatableSubExpression_in_synpred25_InternalESCG2730);
        this_NegatableSubExpression_3=ruleNegatableSubExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_InternalESCG

    // Delegated rules

    public final boolean synpred24_InternalESCG() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_InternalESCG_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_InternalESCG() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_InternalESCG_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA19 dfa19 = new DFA19(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA27 dfa27 = new DFA27(this);
    static final String DFA19_eotS =
        "\101\uffff";
    static final String DFA19_eofS =
        "\101\uffff";
    static final String DFA19_minS =
        "\1\7\1\uffff\1\30\1\uffff\10\30\2\11\1\5\1\uffff\31\5\1\12\27\5";
    static final String DFA19_maxS =
        "\1\30\1\uffff\1\31\1\uffff\10\31\2\41\1\44\1\uffff\31\44\1\41\27"+
        "\44";
    static final String DFA19_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\13\uffff\1\2\61\uffff";
    static final String DFA19_specialS =
        "\101\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\1\1\uffff\2\1\6\uffff\1\3\5\uffff\1\2",
            "",
            "\1\4\1\5",
            "",
            "\1\6\1\7",
            "\1\6\1\7",
            "\1\10\1\11",
            "\1\10\1\11",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\14\1\15",
            "\1\14\1\15",
            "\1\16\1\17\6\uffff\1\1\6\uffff\1\14\1\15\3\uffff\5\17",
            "\1\16\1\17\6\uffff\1\1\6\uffff\1\14\1\15\3\uffff\5\17",
            "\1\37\1\40\1\30\1\27\1\uffff\1\24\1\25\1\20\1\32\1\33\1\26"+
            "\1\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41"+
            "\1\47\7\uffff\1\42",
            "",
            "\1\37\1\40\1\30\1\27\1\uffff\1\24\1\25\1\20\1\32\1\33\1\26"+
            "\1\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41"+
            "\1\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\37\1\40\1\30\1\27\1\51\1\24\1\25\1\50\1\32\1\33\1\26\1"+
            "\31\1\34\1\45\1\46\1\44\1\43\1\35\1\36\1\21\1\22\1\23\1\41\1"+
            "\47\7\uffff\1\42",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\17\6\uffff\1\1\13\uffff\5\17",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73",
            "\1\70\1\71\1\61\1\60\1\51\1\55\1\56\1\50\1\63\1\64\1\57\1"+
            "\62\1\65\1\76\1\77\1\75\1\74\1\66\1\67\1\52\1\53\1\54\1\72\1"+
            "\100\7\uffff\1\73"
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "774:1: (this_ConceptAssignment_0= ruleConceptAssignment | this_NumericalAssignment_1= ruleNumericalAssignment | this_NumericalAssignmentGroup_2= ruleNumericalAssignmentGroup )";
        }
    }
    static final String DFA23_eotS =
        "\u0084\uffff";
    static final String DFA23_eofS =
        "\u0084\uffff";
    static final String DFA23_minS =
        "\2\7\1\10\1\uffff\1\10\4\30\2\uffff\17\30\2\4\2\30\1\5\1\0\2\4"+
        "\32\5\1\4\57\5\1\4\27\5";
    static final String DFA23_maxS =
        "\3\30\1\uffff\3\30\1\31\1\30\2\uffff\23\31\1\44\1\0\2\31\32\44"+
        "\1\27\57\44\1\27\27\44";
    static final String DFA23_acceptS =
        "\3\uffff\1\3\5\uffff\1\2\1\1\171\uffff";
    static final String DFA23_specialS =
        "\37\uffff\1\0\144\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\2\1\3\1\uffff\2\3\12\uffff\1\1\1\uffff\1\3",
            "\1\4\1\10\1\uffff\1\5\1\6\12\uffff\1\12\1\11\1\7",
            "\1\3\1\uffff\2\3\12\uffff\1\11\1\uffff\1\3",
            "",
            "\1\10\1\uffff\1\5\1\6\12\uffff\1\12\1\uffff\1\7",
            "\1\7",
            "\1\7",
            "\1\13\1\14",
            "\1\15",
            "",
            "",
            "\1\16\1\17",
            "\1\16\1\17",
            "\1\20\1\21",
            "\1\22\1\23",
            "\1\22\1\23",
            "\1\24\1\25",
            "\1\24\1\25",
            "\1\26\1\27",
            "\1\26\1\27",
            "\1\30\1\31",
            "\1\30\1\31",
            "\1\32\1\33",
            "\1\32\1\33",
            "\1\34\1\35",
            "\1\34\1\35",
            "\3\11\2\uffff\1\36\12\uffff\2\12\1\uffff\1\37\1\32\1\33",
            "\3\11\2\uffff\1\36\12\uffff\2\12\1\uffff\1\37\1\32\1\33",
            "\1\40\1\41",
            "\1\40\1\41",
            "\1\61\1\62\1\52\1\51\1\uffff\1\46\1\47\1\42\1\54\1\55\1\50"+
            "\1\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63"+
            "\1\71\7\uffff\1\64",
            "\1\uffff",
            "\3\11\2\uffff\1\72\12\uffff\2\12\1\uffff\1\37\1\40\1\41",
            "\3\11\2\uffff\1\72\12\uffff\2\12\1\uffff\1\37\1\40\1\41",
            "\1\61\1\62\1\52\1\51\1\uffff\1\46\1\47\1\42\1\54\1\55\1\50"+
            "\1\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63"+
            "\1\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\61\1\62\1\52\1\51\1\74\1\46\1\47\1\73\1\54\1\55\1\50\1"+
            "\53\1\56\1\67\1\70\1\66\1\65\1\57\1\60\1\43\1\44\1\45\1\63\1"+
            "\71\7\uffff\1\64",
            "\1\113\1\114\1\104\1\103\1\uffff\1\100\1\101\1\uffff\1\106"+
            "\1\107\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112"+
            "\1\75\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\3\11\15\uffff\2\12\1\uffff\1\37",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\113\1\114\1\104\1\103\1\154\1\100\1\101\1\153\1\106\1\107"+
            "\1\102\1\105\1\110\1\121\1\122\1\120\1\117\1\111\1\112\1\75"+
            "\1\76\1\77\1\115\1\123\7\uffff\1\116",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\142\1\143\1\133\1\132\1\74\1\127\1\130\1\73\1\135\1\136"+
            "\1\131\1\134\1\137\1\150\1\151\1\147\1\146\1\140\1\141\1\124"+
            "\1\125\1\126\1\144\1\152\7\uffff\1\145",
            "\1\173\1\174\1\164\1\163\1\uffff\1\160\1\161\1\153\1\166\1"+
            "\167\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1"+
            "\172\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\3\11\15\uffff\2\12\1\uffff\1\37",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176",
            "\1\173\1\174\1\164\1\163\1\154\1\160\1\161\1\153\1\166\1\167"+
            "\1\162\1\165\1\170\1\u0081\1\u0082\1\u0080\1\177\1\171\1\172"+
            "\1\155\1\156\1\157\1\175\1\u0083\7\uffff\1\176"
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "1304:1: ( (this_OPENING_ROUND_BRACKET_0= RULE_OPENING_ROUND_BRACKET this_RValue_1= ruleRValue this_CLOSING_ROUND_BRACKET_2= RULE_CLOSING_ROUND_BRACKET ) | this_NegatableSubExpression_3= ruleNegatableSubExpression | this_LValue_4= ruleLValue )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA23_31 = input.LA(1);

                         
                        int index23_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred24_InternalESCG()) ) {s = 10;}

                        else if ( (synpred25_InternalESCG()) ) {s = 9;}

                         
                        input.seek(index23_31);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 23, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA27_eotS =
        "\4\uffff";
    static final String DFA27_eofS =
        "\1\1\3\uffff";
    static final String DFA27_minS =
        "\1\11\1\uffff\1\5\1\uffff";
    static final String DFA27_maxS =
        "\1\14\1\uffff\1\44\1\uffff";
    static final String DFA27_acceptS =
        "\1\uffff\1\2\1\uffff\1\1";
    static final String DFA27_specialS =
        "\4\uffff}>";
    static final String[] DFA27_transitionS = {
            "\1\1\2\uffff\1\2",
            "",
            "\4\3\1\1\2\3\1\2\20\3\7\uffff\1\3",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "()* loopback of 1389:3: ( (this_WS_1= RULE_WS )+ (this_TermCharacter_2= ruleTermCharacter )+ )*";
        }
    }
 

    public static final BitSet FOLLOW_ruleExpression_in_entryRuleExpression87 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpression97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubExpression_in_ruleExpression147 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_UNION_TOKEN_in_ruleExpression159 = new BitSet(new long[]{0x0000000001000D80L});
    public static final BitSet FOLLOW_ruleSubExpression_in_ruleExpression179 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleSubExpression_in_entryRuleSubExpression222 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSubExpression232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLValue_in_ruleSubExpression278 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_RULE_PLUS_SIGN_in_ruleSubExpression290 = new BitSet(new long[]{0x0000000001000D80L});
    public static final BitSet FOLLOW_ruleLValue_in_ruleSubExpression310 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleSubExpression324 = new BitSet(new long[]{0x0000000001052D80L});
    public static final BitSet FOLLOW_ruleRefinements_in_ruleSubExpression344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLValue_in_entryRuleLValue382 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLValue392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptGroup_in_ruleLValue442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefSet_in_ruleLValue472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefSet_in_entryRuleRefSet507 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefSet517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_TOKEN_in_ruleRefSet559 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleRefSet576 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_ruleConceptId_in_ruleRefSet596 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleRefSet608 = new BitSet(new long[]{0x000000101FFFEDE0L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleRefSet628 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleRefSet639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptGroup_in_entryRuleConceptGroup676 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptGroup686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_TOKEN_in_ruleConceptGroup728 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_RULE_SUBTYPE_in_ruleConceptGroup753 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_RULE_INCLUSIVE_SUBTYPE_in_ruleConceptGroup773 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleConceptGroup803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConcept_in_entryRuleConcept839 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConcept849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptId_in_ruleConcept895 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConcept907 = new BitSet(new long[]{0x000000101FFFFDE0L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleConcept918 = new BitSet(new long[]{0x000000101FFFFDE0L});
    public static final BitSet FOLLOW_ruleTerm_in_ruleConcept940 = new BitSet(new long[]{0x0000000000001200L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleConcept952 = new BitSet(new long[]{0x0000000000001200L});
    public static final BitSet FOLLOW_RULE_PIPE_in_ruleConcept964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRefinements_in_entryRuleRefinements1001 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRefinements1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeSet_in_ruleRefinements1058 = new BitSet(new long[]{0x0000000001052D82L});
    public static final BitSet FOLLOW_ruleAttributeGroup_in_ruleRefinements1079 = new BitSet(new long[]{0x0000000001052D82L});
    public static final BitSet FOLLOW_ruleAttributeGroup_in_ruleRefinements1108 = new BitSet(new long[]{0x0000000001052D82L});
    public static final BitSet FOLLOW_ruleAttributeGroup_in_entryRuleAttributeGroup1145 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeGroup1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleAttributeGroup1191 = new BitSet(new long[]{0x0000000001050D80L});
    public static final BitSet FOLLOW_ruleAttributeSet_in_ruleAttributeGroup1215 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleAttributeGroup1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeSet_in_entryRuleAttributeSet1260 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeSet1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleAttributeSet1316 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleAttributeSet1328 = new BitSet(new long[]{0x0000000001050D80L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleAttributeSet1348 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute1386 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPTIONAL_in_ruleAttribute1438 = new BitSet(new long[]{0x0000000001050D80L});
    public static final BitSet FOLLOW_ruleAttributeAssignment_in_ruleAttribute1465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeAssignment_in_entryRuleAttributeAssignment1501 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeAssignment1511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptAssignment_in_ruleAttributeAssignment1561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNumericalAssignment_in_ruleAttributeAssignment1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNumericalAssignmentGroup_in_ruleAttributeAssignment1621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConceptAssignment_in_entryRuleConceptAssignment1656 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptAssignment1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLValue_in_ruleConceptAssignment1712 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RULE_EQUAL_SIGN_in_ruleConceptAssignment1723 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleRValue_in_ruleConceptAssignment1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNumericalAssignment_in_entryRuleNumericalAssignment1779 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNumericalAssignment1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleNumericalAssignment1835 = new BitSet(new long[]{0x00000003E0000400L});
    public static final BitSet FOLLOW_ruleOperator_in_ruleNumericalAssignment1856 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_ruleDecimalNumber_in_ruleNumericalAssignment1877 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_ruleUnitType_in_ruleNumericalAssignment1898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNumericalAssignmentGroup_in_entryRuleNumericalAssignmentGroup1934 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNumericalAssignmentGroup1944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_SQUARE_BRACKET_in_ruleNumericalAssignmentGroup1980 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_ruleConcept_in_ruleNumericalAssignmentGroup2000 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RULE_EQUAL_SIGN_in_ruleNumericalAssignmentGroup2011 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleRValue_in_ruleNumericalAssignmentGroup2031 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleNumericalAssignmentGroup2042 = new BitSet(new long[]{0x0000000001000C80L});
    public static final BitSet FOLLOW_ruleNumericalAssignment_in_ruleNumericalAssignmentGroup2062 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RULE_CLOSING_SQUARE_BRACKET_in_ruleNumericalAssignmentGroup2073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRValue_in_entryRuleRValue2108 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRValue2118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOr_in_ruleRValue2167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOr_in_entryRuleOr2201 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOr2211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAnd_in_ruleOr2261 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_RULE_OR_TOKEN_in_ruleOr2284 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleAnd_in_ruleOr2304 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ruleAnd_in_entryRuleAnd2342 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAnd2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalRValue_in_ruleAnd2402 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_RULE_AND_TOKEN_in_ruleAnd2425 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleTerminalRValue_in_ruleAnd2445 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_ruleNegatableSubExpression_in_entryRuleNegatableSubExpression2483 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNegatableSubExpression2493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_TOKEN_in_ruleNegatableSubExpression2535 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleNegatableSubExpression2552 = new BitSet(new long[]{0x0000000001800D80L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleNegatableSubExpression2572 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleNegatableSubExpression2583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalRValue_in_entryRuleTerminalRValue2618 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerminalRValue2628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTerminalRValue2665 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleRValue_in_ruleTerminalRValue2689 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTerminalRValue2699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNegatableSubExpression_in_ruleTerminalRValue2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLValue_in_ruleTerminalRValue2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerm_in_entryRuleTerm2802 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerm2813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm2865 = new BitSet(new long[]{0x000000101FFFFDE2L});
    public static final BitSet FOLLOW_RULE_WS_in_ruleTerm2889 = new BitSet(new long[]{0x000000101FFFFDE0L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_ruleTerm2919 = new BitSet(new long[]{0x000000101FFFFDE2L});
    public static final BitSet FOLLOW_ruleConceptId_in_entryRuleConceptId2979 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleConceptId2990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3034 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3055 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId3081 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3103 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId3129 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3151 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId3177 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3199 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId3225 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleConceptId3247 = new BitSet(new long[]{0x0000000003000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleConceptId3273 = new BitSet(new long[]{0x0000000003000002L});
    public static final BitSet FOLLOW_ruleTermCharacter_in_entryRuleTermCharacter3331 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTermCharacter3342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleTermCharacter3386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleTermCharacter3412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LETTER_in_ruleTermCharacter3438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SUBTYPE_in_ruleTermCharacter3464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_INCLUSIVE_SUBTYPE_in_ruleTermCharacter3490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COMMA_in_ruleTermCharacter3516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CARET_in_ruleTermCharacter3542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_TOKEN_in_ruleTermCharacter3568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPTIONAL_in_ruleTermCharacter3594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_CURLY_BRACKET_in_ruleTermCharacter3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CLOSING_CURLY_BRACKET_in_ruleTermCharacter3646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUAL_SIGN_in_ruleTermCharacter3672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_ruleTermCharacter3698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_ruleTermCharacter3724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PLUS_SIGN_in_ruleTermCharacter3750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLON_in_ruleTermCharacter3776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_PERIOD_in_ruleTermCharacter3802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUnitType_in_ruleTermCharacter3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_AND_TOKEN_in_ruleTermCharacter3861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OR_TOKEN_in_ruleTermCharacter3887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_SQUARE_BRACKET_in_ruleTermCharacter3913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_CLOSING_SQUARE_BRACKET_in_ruleTermCharacter3939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OTHER_ALLOWED_TERM_CHARACTER_in_ruleTermCharacter3965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecimalNumber_in_entryRuleDecimalNumber4023 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecimalNumber4034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleDecimalNumber4079 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4106 = new BitSet(new long[]{0x000000000B000002L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4127 = new BitSet(new long[]{0x000000000B000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleDecimalNumber4153 = new BitSet(new long[]{0x000000000B000002L});
    public static final BitSet FOLLOW_RULE_PERIOD_in_ruleDecimalNumber4178 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_RULE_DIGIT_NONZERO_in_ruleDecimalNumber4199 = new BitSet(new long[]{0x0000000003000002L});
    public static final BitSet FOLLOW_RULE_ZERO_in_ruleDecimalNumber4225 = new BitSet(new long[]{0x0000000003000002L});
    public static final BitSet FOLLOW_ruleOperator_in_entryRuleOperator4285 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOperator4296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_EQUALS_OPERATOR_in_ruleOperator4340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_SUBTYPE_in_ruleOperator4366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GREATER_THAN_OPERATOR_in_ruleOperator4392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_LESS_EQUALS_OPERATOR_in_ruleOperator4418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_GREATER_EQUALS_OPERATOR_in_ruleOperator4444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_NOT_EQUALS_OPERATOR_in_ruleOperator4470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUnitType_in_entryRuleUnitType4526 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUnitType4537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_ruleUnitType4578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_OPENING_ROUND_BRACKET_in_synpred24_InternalESCG2665 = new BitSet(new long[]{0x0000000001400D80L});
    public static final BitSet FOLLOW_ruleRValue_in_synpred24_InternalESCG2689 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RULE_CLOSING_ROUND_BRACKET_in_synpred24_InternalESCG2699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNegatableSubExpression_in_synpred25_InternalESCG2730 = new BitSet(new long[]{0x0000000000000002L});

}