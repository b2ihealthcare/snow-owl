package com.b2international.snowowl.snomed.scg.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.scg.services.ScgGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalScgParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_EQUIVALENT_TO", "RULE_SUBTYPE_OF", "RULE_TERM_STRING", "RULE_ZERO", "RULE_DIGIT_NONZERO", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_COMMA", "RULE_EQUAL", "RULE_COLON", "RULE_PLUS", "RULE_DASH", "RULE_DOT", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_HASH", "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_STRING"
    };
    public static final int RULE_DIGIT_NONZERO=8;
    public static final int RULE_CURLY_OPEN=9;
    public static final int RULE_ROUND_CLOSE=18;
    public static final int RULE_STRING=23;
    public static final int RULE_SL_COMMENT=22;
    public static final int RULE_HASH=19;
    public static final int RULE_EQUIVALENT_TO=4;
    public static final int RULE_DASH=15;
    public static final int RULE_ROUND_OPEN=17;
    public static final int RULE_PLUS=14;
    public static final int RULE_DOT=16;
    public static final int EOF=-1;
    public static final int RULE_EQUAL=12;
    public static final int RULE_SUBTYPE_OF=5;
    public static final int RULE_COMMA=11;
    public static final int RULE_WS=20;
    public static final int RULE_CURLY_CLOSE=10;
    public static final int RULE_ZERO=7;
    public static final int RULE_COLON=13;
    public static final int RULE_ML_COMMENT=21;
    public static final int RULE_TERM_STRING=6;

    // delegates
    // delegators


        public InternalScgParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalScgParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalScgParser.tokenNames; }
    public String getGrammarFileName() { return "InternalScgParser.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */

     	private ScgGrammarAccess grammarAccess;

        public InternalScgParser(TokenStream input, ScgGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Expression";
       	}

       	@Override
       	protected ScgGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleExpression"
    // InternalScgParser.g:75:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


        try {
            // InternalScgParser.g:75:51: (iv_ruleExpression= ruleExpression EOF )
            // InternalScgParser.g:76:2: iv_ruleExpression= ruleExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpression=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpression; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleExpression"


    // $ANTLR start "ruleExpression"
    // InternalScgParser.g:82:1: ruleExpression returns [EObject current=null] : ( () ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )? ) ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        Token lv_primitive_1_0=null;
        Token this_EQUIVALENT_TO_2=null;
        EObject lv_expression_3_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:88:2: ( ( () ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )? ) )
            // InternalScgParser.g:89:2: ( () ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )? )
            {
            // InternalScgParser.g:89:2: ( () ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )? )
            // InternalScgParser.g:90:3: () ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )?
            {
            // InternalScgParser.g:90:3: ()
            // InternalScgParser.g:91:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getExpressionAccess().getExpressionAction_0(),
              					current);
              			
            }

            }

            // InternalScgParser.g:100:3: ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=RULE_EQUIVALENT_TO && LA2_0<=RULE_SUBTYPE_OF)||LA2_0==RULE_DIGIT_NONZERO) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalScgParser.g:101:4: ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )? ( (lv_expression_3_0= ruleSubExpression ) )
                    {
                    // InternalScgParser.g:101:4: ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )?
                    int alt1=3;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==RULE_SUBTYPE_OF) ) {
                        alt1=1;
                    }
                    else if ( (LA1_0==RULE_EQUIVALENT_TO) ) {
                        alt1=2;
                    }
                    switch (alt1) {
                        case 1 :
                            // InternalScgParser.g:102:5: ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) )
                            {
                            // InternalScgParser.g:102:5: ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) )
                            // InternalScgParser.g:103:6: (lv_primitive_1_0= RULE_SUBTYPE_OF )
                            {
                            // InternalScgParser.g:103:6: (lv_primitive_1_0= RULE_SUBTYPE_OF )
                            // InternalScgParser.g:104:7: lv_primitive_1_0= RULE_SUBTYPE_OF
                            {
                            lv_primitive_1_0=(Token)match(input,RULE_SUBTYPE_OF,FollowSets000.FOLLOW_3); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              							newLeafNode(lv_primitive_1_0, grammarAccess.getExpressionAccess().getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0());
                              						
                            }
                            if ( state.backtracking==0 ) {

                              							if (current==null) {
                              								current = createModelElement(grammarAccess.getExpressionRule());
                              							}
                              							setWithLastConsumed(
                              								current,
                              								"primitive",
                              								true,
                              								"com.b2international.snowowl.snomed.scg.Scg.SUBTYPE_OF");
                              						
                            }

                            }


                            }


                            }
                            break;
                        case 2 :
                            // InternalScgParser.g:121:5: this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO
                            {
                            this_EQUIVALENT_TO_2=(Token)match(input,RULE_EQUIVALENT_TO,FollowSets000.FOLLOW_3); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              					newLeafNode(this_EQUIVALENT_TO_2, grammarAccess.getExpressionAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_1());
                              				
                            }

                            }
                            break;

                    }

                    // InternalScgParser.g:126:4: ( (lv_expression_3_0= ruleSubExpression ) )
                    // InternalScgParser.g:127:5: (lv_expression_3_0= ruleSubExpression )
                    {
                    // InternalScgParser.g:127:5: (lv_expression_3_0= ruleSubExpression )
                    // InternalScgParser.g:128:6: lv_expression_3_0= ruleSubExpression
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getExpressionAccess().getExpressionSubExpressionParserRuleCall_1_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expression_3_0=ruleSubExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getExpressionRule());
                      						}
                      						set(
                      							current,
                      							"expression",
                      							lv_expression_3_0,
                      							"com.b2international.snowowl.snomed.scg.Scg.SubExpression");
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
    // $ANTLR end "ruleExpression"


    // $ANTLR start "entryRuleSubExpression"
    // InternalScgParser.g:150:1: entryRuleSubExpression returns [EObject current=null] : iv_ruleSubExpression= ruleSubExpression EOF ;
    public final EObject entryRuleSubExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubExpression = null;


        try {
            // InternalScgParser.g:150:54: (iv_ruleSubExpression= ruleSubExpression EOF )
            // InternalScgParser.g:151:2: iv_ruleSubExpression= ruleSubExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubExpressionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSubExpression=ruleSubExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubExpression; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // InternalScgParser.g:157:1: ruleSubExpression returns [EObject current=null] : ( ( (lv_focusConcepts_0_0= ruleConceptReference ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleSubExpression() throws RecognitionException {
        EObject current = null;

        Token this_PLUS_1=null;
        Token this_COLON_3=null;
        EObject lv_focusConcepts_0_0 = null;

        EObject lv_focusConcepts_2_0 = null;

        EObject lv_refinement_4_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:163:2: ( ( ( (lv_focusConcepts_0_0= ruleConceptReference ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? ) )
            // InternalScgParser.g:164:2: ( ( (lv_focusConcepts_0_0= ruleConceptReference ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? )
            {
            // InternalScgParser.g:164:2: ( ( (lv_focusConcepts_0_0= ruleConceptReference ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? )
            // InternalScgParser.g:165:3: ( (lv_focusConcepts_0_0= ruleConceptReference ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )?
            {
            // InternalScgParser.g:165:3: ( (lv_focusConcepts_0_0= ruleConceptReference ) )
            // InternalScgParser.g:166:4: (lv_focusConcepts_0_0= ruleConceptReference )
            {
            // InternalScgParser.g:166:4: (lv_focusConcepts_0_0= ruleConceptReference )
            // InternalScgParser.g:167:5: lv_focusConcepts_0_0= ruleConceptReference
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsConceptReferenceParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_4);
            lv_focusConcepts_0_0=ruleConceptReference();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSubExpressionRule());
              					}
              					add(
              						current,
              						"focusConcepts",
              						lv_focusConcepts_0_0,
              						"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalScgParser.g:184:3: (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==RULE_PLUS) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalScgParser.g:185:4: this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleConceptReference ) )
            	    {
            	    this_PLUS_1=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_3); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_PLUS_1, grammarAccess.getSubExpressionAccess().getPLUSTerminalRuleCall_1_0());
            	      			
            	    }
            	    // InternalScgParser.g:189:4: ( (lv_focusConcepts_2_0= ruleConceptReference ) )
            	    // InternalScgParser.g:190:5: (lv_focusConcepts_2_0= ruleConceptReference )
            	    {
            	    // InternalScgParser.g:190:5: (lv_focusConcepts_2_0= ruleConceptReference )
            	    // InternalScgParser.g:191:6: lv_focusConcepts_2_0= ruleConceptReference
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsConceptReferenceParserRuleCall_1_1_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_4);
            	    lv_focusConcepts_2_0=ruleConceptReference();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getSubExpressionRule());
            	      						}
            	      						add(
            	      							current,
            	      							"focusConcepts",
            	      							lv_focusConcepts_2_0,
            	      							"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
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

            // InternalScgParser.g:209:3: (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_COLON) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalScgParser.g:210:4: this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) )
                    {
                    this_COLON_3=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_5); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_3, grammarAccess.getSubExpressionAccess().getCOLONTerminalRuleCall_2_0());
                      			
                    }
                    // InternalScgParser.g:214:4: ( (lv_refinement_4_0= ruleRefinement ) )
                    // InternalScgParser.g:215:5: (lv_refinement_4_0= ruleRefinement )
                    {
                    // InternalScgParser.g:215:5: (lv_refinement_4_0= ruleRefinement )
                    // InternalScgParser.g:216:6: lv_refinement_4_0= ruleRefinement
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSubExpressionAccess().getRefinementRefinementParserRuleCall_2_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_refinement_4_0=ruleRefinement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getSubExpressionRule());
                      						}
                      						set(
                      							current,
                      							"refinement",
                      							lv_refinement_4_0,
                      							"com.b2international.snowowl.snomed.scg.Scg.Refinement");
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


    // $ANTLR start "entryRuleRefinement"
    // InternalScgParser.g:238:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // InternalScgParser.g:238:51: (iv_ruleRefinement= ruleRefinement EOF )
            // InternalScgParser.g:239:2: iv_ruleRefinement= ruleRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRefinement=ruleRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRefinement; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleRefinement"


    // $ANTLR start "ruleRefinement"
    // InternalScgParser.g:245:1: ruleRefinement returns [EObject current=null] : ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* ) ;
    public final EObject ruleRefinement() throws RecognitionException {
        EObject current = null;

        Token this_COMMA_1=null;
        Token this_COMMA_4=null;
        EObject lv_attributes_0_0 = null;

        EObject lv_attributes_2_0 = null;

        EObject lv_groups_3_0 = null;

        EObject lv_groups_5_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:251:2: ( ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* ) )
            // InternalScgParser.g:252:2: ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* )
            {
            // InternalScgParser.g:252:2: ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* )
            // InternalScgParser.g:253:3: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )*
            {
            // InternalScgParser.g:253:3: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_DIGIT_NONZERO) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_CURLY_OPEN) ) {
                alt6=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalScgParser.g:254:4: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
                    {
                    // InternalScgParser.g:254:4: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
                    // InternalScgParser.g:255:5: ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
                    {
                    // InternalScgParser.g:255:5: ( (lv_attributes_0_0= ruleAttribute ) )
                    // InternalScgParser.g:256:6: (lv_attributes_0_0= ruleAttribute )
                    {
                    // InternalScgParser.g:256:6: (lv_attributes_0_0= ruleAttribute )
                    // InternalScgParser.g:257:7: lv_attributes_0_0= ruleAttribute
                    {
                    if ( state.backtracking==0 ) {

                      							newCompositeNode(grammarAccess.getRefinementAccess().getAttributesAttributeParserRuleCall_0_0_0_0());
                      						
                    }
                    pushFollow(FollowSets000.FOLLOW_6);
                    lv_attributes_0_0=ruleAttribute();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      							if (current==null) {
                      								current = createModelElementForParent(grammarAccess.getRefinementRule());
                      							}
                      							add(
                      								current,
                      								"attributes",
                      								lv_attributes_0_0,
                      								"com.b2international.snowowl.snomed.scg.Scg.Attribute");
                      							afterParserOrEnumRuleCall();
                      						
                    }

                    }


                    }

                    // InternalScgParser.g:274:5: (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==RULE_COMMA) ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1==RULE_DIGIT_NONZERO) ) {
                                alt5=1;
                            }


                        }


                        switch (alt5) {
                    	case 1 :
                    	    // InternalScgParser.g:275:6: this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) )
                    	    {
                    	    this_COMMA_1=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_3); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(this_COMMA_1, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_0_0_1_0());
                    	      					
                    	    }
                    	    // InternalScgParser.g:279:6: ( (lv_attributes_2_0= ruleAttribute ) )
                    	    // InternalScgParser.g:280:7: (lv_attributes_2_0= ruleAttribute )
                    	    {
                    	    // InternalScgParser.g:280:7: (lv_attributes_2_0= ruleAttribute )
                    	    // InternalScgParser.g:281:8: lv_attributes_2_0= ruleAttribute
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      								newCompositeNode(grammarAccess.getRefinementAccess().getAttributesAttributeParserRuleCall_0_0_1_1_0());
                    	      							
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_6);
                    	    lv_attributes_2_0=ruleAttribute();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      								if (current==null) {
                    	      									current = createModelElementForParent(grammarAccess.getRefinementRule());
                    	      								}
                    	      								add(
                    	      									current,
                    	      									"attributes",
                    	      									lv_attributes_2_0,
                    	      									"com.b2international.snowowl.snomed.scg.Scg.Attribute");
                    	      								afterParserOrEnumRuleCall();
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalScgParser.g:301:4: ( (lv_groups_3_0= ruleAttributeGroup ) )
                    {
                    // InternalScgParser.g:301:4: ( (lv_groups_3_0= ruleAttributeGroup ) )
                    // InternalScgParser.g:302:5: (lv_groups_3_0= ruleAttributeGroup )
                    {
                    // InternalScgParser.g:302:5: (lv_groups_3_0= ruleAttributeGroup )
                    // InternalScgParser.g:303:6: lv_groups_3_0= ruleAttributeGroup
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getRefinementAccess().getGroupsAttributeGroupParserRuleCall_0_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_6);
                    lv_groups_3_0=ruleAttributeGroup();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getRefinementRule());
                      						}
                      						add(
                      							current,
                      							"groups",
                      							lv_groups_3_0,
                      							"com.b2international.snowowl.snomed.scg.Scg.AttributeGroup");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }

            // InternalScgParser.g:321:3: ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==RULE_CURLY_OPEN||LA8_0==RULE_COMMA) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // InternalScgParser.g:322:4: (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) )
            	    {
            	    // InternalScgParser.g:322:4: (this_COMMA_4= RULE_COMMA )?
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);

            	    if ( (LA7_0==RULE_COMMA) ) {
            	        alt7=1;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // InternalScgParser.g:323:5: this_COMMA_4= RULE_COMMA
            	            {
            	            this_COMMA_4=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_5); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_COMMA_4, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_1_0());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalScgParser.g:328:4: ( (lv_groups_5_0= ruleAttributeGroup ) )
            	    // InternalScgParser.g:329:5: (lv_groups_5_0= ruleAttributeGroup )
            	    {
            	    // InternalScgParser.g:329:5: (lv_groups_5_0= ruleAttributeGroup )
            	    // InternalScgParser.g:330:6: lv_groups_5_0= ruleAttributeGroup
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getRefinementAccess().getGroupsAttributeGroupParserRuleCall_1_1_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_6);
            	    lv_groups_5_0=ruleAttributeGroup();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getRefinementRule());
            	      						}
            	      						add(
            	      							current,
            	      							"groups",
            	      							lv_groups_5_0,
            	      							"com.b2international.snowowl.snomed.scg.Scg.AttributeGroup");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
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
    // $ANTLR end "ruleRefinement"


    // $ANTLR start "entryRuleAttributeGroup"
    // InternalScgParser.g:352:1: entryRuleAttributeGroup returns [EObject current=null] : iv_ruleAttributeGroup= ruleAttributeGroup EOF ;
    public final EObject entryRuleAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeGroup = null;


        try {
            // InternalScgParser.g:352:55: (iv_ruleAttributeGroup= ruleAttributeGroup EOF )
            // InternalScgParser.g:353:2: iv_ruleAttributeGroup= ruleAttributeGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeGroupRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeGroup=ruleAttributeGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeGroup; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // InternalScgParser.g:359:1: ruleAttributeGroup returns [EObject current=null] : (this_CURLY_OPEN_0= RULE_CURLY_OPEN ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CURLY_CLOSE_4= RULE_CURLY_CLOSE ) ;
    public final EObject ruleAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_CURLY_OPEN_0=null;
        Token this_COMMA_2=null;
        Token this_CURLY_CLOSE_4=null;
        EObject lv_attributes_1_0 = null;

        EObject lv_attributes_3_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:365:2: ( (this_CURLY_OPEN_0= RULE_CURLY_OPEN ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CURLY_CLOSE_4= RULE_CURLY_CLOSE ) )
            // InternalScgParser.g:366:2: (this_CURLY_OPEN_0= RULE_CURLY_OPEN ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CURLY_CLOSE_4= RULE_CURLY_CLOSE )
            {
            // InternalScgParser.g:366:2: (this_CURLY_OPEN_0= RULE_CURLY_OPEN ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CURLY_CLOSE_4= RULE_CURLY_CLOSE )
            // InternalScgParser.g:367:3: this_CURLY_OPEN_0= RULE_CURLY_OPEN ( (lv_attributes_1_0= ruleAttribute ) ) (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )* this_CURLY_CLOSE_4= RULE_CURLY_CLOSE
            {
            this_CURLY_OPEN_0=(Token)match(input,RULE_CURLY_OPEN,FollowSets000.FOLLOW_3); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_OPEN_0, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_0());
              		
            }
            // InternalScgParser.g:371:3: ( (lv_attributes_1_0= ruleAttribute ) )
            // InternalScgParser.g:372:4: (lv_attributes_1_0= ruleAttribute )
            {
            // InternalScgParser.g:372:4: (lv_attributes_1_0= ruleAttribute )
            // InternalScgParser.g:373:5: lv_attributes_1_0= ruleAttribute
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_7);
            lv_attributes_1_0=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
              					}
              					add(
              						current,
              						"attributes",
              						lv_attributes_1_0,
              						"com.b2international.snowowl.snomed.scg.Scg.Attribute");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalScgParser.g:390:3: (this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalScgParser.g:391:4: this_COMMA_2= RULE_COMMA ( (lv_attributes_3_0= ruleAttribute ) )
            	    {
            	    this_COMMA_2=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_3); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_COMMA_2, grammarAccess.getAttributeGroupAccess().getCOMMATerminalRuleCall_2_0());
            	      			
            	    }
            	    // InternalScgParser.g:395:4: ( (lv_attributes_3_0= ruleAttribute ) )
            	    // InternalScgParser.g:396:5: (lv_attributes_3_0= ruleAttribute )
            	    {
            	    // InternalScgParser.g:396:5: (lv_attributes_3_0= ruleAttribute )
            	    // InternalScgParser.g:397:6: lv_attributes_3_0= ruleAttribute
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_2_1_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_7);
            	    lv_attributes_3_0=ruleAttribute();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
            	      						}
            	      						add(
            	      							current,
            	      							"attributes",
            	      							lv_attributes_3_0,
            	      							"com.b2international.snowowl.snomed.scg.Scg.Attribute");
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

            this_CURLY_CLOSE_4=(Token)match(input,RULE_CURLY_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_CLOSE_4, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
              		
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


    // $ANTLR start "entryRuleAttribute"
    // InternalScgParser.g:423:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // InternalScgParser.g:423:50: (iv_ruleAttribute= ruleAttribute EOF )
            // InternalScgParser.g:424:2: iv_ruleAttribute= ruleAttribute EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttribute; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // InternalScgParser.g:430:1: ruleAttribute returns [EObject current=null] : ( ( (lv_name_0_0= ruleConceptReference ) ) this_EQUAL_1= RULE_EQUAL ( (lv_value_2_0= ruleAttributeValue ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_1=null;
        EObject lv_name_0_0 = null;

        EObject lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:436:2: ( ( ( (lv_name_0_0= ruleConceptReference ) ) this_EQUAL_1= RULE_EQUAL ( (lv_value_2_0= ruleAttributeValue ) ) ) )
            // InternalScgParser.g:437:2: ( ( (lv_name_0_0= ruleConceptReference ) ) this_EQUAL_1= RULE_EQUAL ( (lv_value_2_0= ruleAttributeValue ) ) )
            {
            // InternalScgParser.g:437:2: ( ( (lv_name_0_0= ruleConceptReference ) ) this_EQUAL_1= RULE_EQUAL ( (lv_value_2_0= ruleAttributeValue ) ) )
            // InternalScgParser.g:438:3: ( (lv_name_0_0= ruleConceptReference ) ) this_EQUAL_1= RULE_EQUAL ( (lv_value_2_0= ruleAttributeValue ) )
            {
            // InternalScgParser.g:438:3: ( (lv_name_0_0= ruleConceptReference ) )
            // InternalScgParser.g:439:4: (lv_name_0_0= ruleConceptReference )
            {
            // InternalScgParser.g:439:4: (lv_name_0_0= ruleConceptReference )
            // InternalScgParser.g:440:5: lv_name_0_0= ruleConceptReference
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeAccess().getNameConceptReferenceParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_8);
            lv_name_0_0=ruleConceptReference();

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
              						"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_9); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getAttributeAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalScgParser.g:461:3: ( (lv_value_2_0= ruleAttributeValue ) )
            // InternalScgParser.g:462:4: (lv_value_2_0= ruleAttributeValue )
            {
            // InternalScgParser.g:462:4: (lv_value_2_0= ruleAttributeValue )
            // InternalScgParser.g:463:5: lv_value_2_0= ruleAttributeValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
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
              						"com.b2international.snowowl.snomed.scg.Scg.AttributeValue");
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
    // InternalScgParser.g:484:1: entryRuleAttributeValue returns [EObject current=null] : iv_ruleAttributeValue= ruleAttributeValue EOF ;
    public final EObject entryRuleAttributeValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValue = null;


        try {
            // InternalScgParser.g:484:55: (iv_ruleAttributeValue= ruleAttributeValue EOF )
            // InternalScgParser.g:485:2: iv_ruleAttributeValue= ruleAttributeValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeValue=ruleAttributeValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // InternalScgParser.g:491:1: ruleAttributeValue returns [EObject current=null] : (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue ) ;
    public final EObject ruleAttributeValue() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_1=null;
        Token this_ROUND_CLOSE_3=null;
        EObject this_ConceptReference_0 = null;

        EObject this_SubExpression_2 = null;

        EObject this_StringValue_4 = null;

        EObject this_IntegerValue_5 = null;

        EObject this_DecimalValue_6 = null;



        	enterRule();

        try {
            // InternalScgParser.g:497:2: ( (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue ) )
            // InternalScgParser.g:498:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue )
            {
            // InternalScgParser.g:498:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue )
            int alt10=5;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // InternalScgParser.g:499:3: this_ConceptReference_0= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeValueAccess().getConceptReferenceParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConceptReference_0=ruleConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ConceptReference_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:511:3: (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE )
                    {
                    // InternalScgParser.g:511:3: (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE )
                    // InternalScgParser.g:512:4: this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_1=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_3); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_1, grammarAccess.getAttributeValueAccess().getROUND_OPENTerminalRuleCall_1_0());
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				/* */
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newCompositeNode(grammarAccess.getAttributeValueAccess().getSubExpressionParserRuleCall_1_1());
                      			
                    }
                    pushFollow(FollowSets000.FOLLOW_10);
                    this_SubExpression_2=ruleSubExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = this_SubExpression_2;
                      				afterParserOrEnumRuleCall();
                      			
                    }
                    this_ROUND_CLOSE_3=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_3, grammarAccess.getAttributeValueAccess().getROUND_CLOSETerminalRuleCall_1_2());
                      			
                    }

                    }


                    }
                    break;
                case 3 :
                    // InternalScgParser.g:533:3: this_StringValue_4= ruleStringValue
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeValueAccess().getStringValueParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringValue_4=ruleStringValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringValue_4;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalScgParser.g:545:3: this_IntegerValue_5= ruleIntegerValue
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeValueAccess().getIntegerValueParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValue_5=ruleIntegerValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValue_5;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalScgParser.g:557:3: this_DecimalValue_6= ruleDecimalValue
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeValueAccess().getDecimalValueParserRuleCall_4());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValue_6=ruleDecimalValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValue_6;
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
    // $ANTLR end "ruleAttributeValue"


    // $ANTLR start "entryRuleStringValue"
    // InternalScgParser.g:572:1: entryRuleStringValue returns [EObject current=null] : iv_ruleStringValue= ruleStringValue EOF ;
    public final EObject entryRuleStringValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValue = null;


        try {
            // InternalScgParser.g:572:52: (iv_ruleStringValue= ruleStringValue EOF )
            // InternalScgParser.g:573:2: iv_ruleStringValue= ruleStringValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStringValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringValue=ruleStringValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStringValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleStringValue"


    // $ANTLR start "ruleStringValue"
    // InternalScgParser.g:579:1: ruleStringValue returns [EObject current=null] : ( (lv_value_0_0= RULE_STRING ) ) ;
    public final EObject ruleStringValue() throws RecognitionException {
        EObject current = null;

        Token lv_value_0_0=null;


        	enterRule();

        try {
            // InternalScgParser.g:585:2: ( ( (lv_value_0_0= RULE_STRING ) ) )
            // InternalScgParser.g:586:2: ( (lv_value_0_0= RULE_STRING ) )
            {
            // InternalScgParser.g:586:2: ( (lv_value_0_0= RULE_STRING ) )
            // InternalScgParser.g:587:3: (lv_value_0_0= RULE_STRING )
            {
            // InternalScgParser.g:587:3: (lv_value_0_0= RULE_STRING )
            // InternalScgParser.g:588:4: lv_value_0_0= RULE_STRING
            {
            lv_value_0_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              				newLeafNode(lv_value_0_0, grammarAccess.getStringValueAccess().getValueSTRINGTerminalRuleCall_0());
              			
            }
            if ( state.backtracking==0 ) {

              				if (current==null) {
              					current = createModelElement(grammarAccess.getStringValueRule());
              				}
              				setWithLastConsumed(
              					current,
              					"value",
              					lv_value_0_0,
              					"com.b2international.snowowl.snomed.scg.Scg.STRING");
              			
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
    // $ANTLR end "ruleStringValue"


    // $ANTLR start "entryRuleIntegerValue"
    // InternalScgParser.g:607:1: entryRuleIntegerValue returns [EObject current=null] : iv_ruleIntegerValue= ruleIntegerValue EOF ;
    public final EObject entryRuleIntegerValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValue = null;


        try {
            // InternalScgParser.g:607:53: (iv_ruleIntegerValue= ruleIntegerValue EOF )
            // InternalScgParser.g:608:2: iv_ruleIntegerValue= ruleIntegerValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValue=ruleIntegerValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleIntegerValue"


    // $ANTLR start "ruleIntegerValue"
    // InternalScgParser.g:614:1: ruleIntegerValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:620:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) ) )
            // InternalScgParser.g:621:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) )
            {
            // InternalScgParser.g:621:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) )
            // InternalScgParser.g:622:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getIntegerValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalScgParser.g:626:3: ( (lv_value_1_0= ruleInteger ) )
            // InternalScgParser.g:627:4: (lv_value_1_0= ruleInteger )
            {
            // InternalScgParser.g:627:4: (lv_value_1_0= ruleInteger )
            // InternalScgParser.g:628:5: lv_value_1_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueAccess().getValueIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snowowl.snomed.scg.Scg.Integer");
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
    // $ANTLR end "ruleIntegerValue"


    // $ANTLR start "entryRuleDecimalValue"
    // InternalScgParser.g:649:1: entryRuleDecimalValue returns [EObject current=null] : iv_ruleDecimalValue= ruleDecimalValue EOF ;
    public final EObject entryRuleDecimalValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValue = null;


        try {
            // InternalScgParser.g:649:53: (iv_ruleDecimalValue= ruleDecimalValue EOF )
            // InternalScgParser.g:650:2: iv_ruleDecimalValue= ruleDecimalValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValue=ruleDecimalValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValue; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDecimalValue"


    // $ANTLR start "ruleDecimalValue"
    // InternalScgParser.g:656:1: ruleDecimalValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:662:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) ) )
            // InternalScgParser.g:663:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) )
            {
            // InternalScgParser.g:663:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) )
            // InternalScgParser.g:664:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getDecimalValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalScgParser.g:668:3: ( (lv_value_1_0= ruleDecimal ) )
            // InternalScgParser.g:669:4: (lv_value_1_0= ruleDecimal )
            {
            // InternalScgParser.g:669:4: (lv_value_1_0= ruleDecimal )
            // InternalScgParser.g:670:5: lv_value_1_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueAccess().getValueDecimalParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snowowl.snomed.scg.Scg.Decimal");
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
    // $ANTLR end "ruleDecimalValue"


    // $ANTLR start "entryRuleConceptReference"
    // InternalScgParser.g:691:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // InternalScgParser.g:691:57: (iv_ruleConceptReference= ruleConceptReference EOF )
            // InternalScgParser.g:692:2: iv_ruleConceptReference= ruleConceptReference EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptReferenceRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConceptReference=ruleConceptReference();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptReference; 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleConceptReference"


    // $ANTLR start "ruleConceptReference"
    // InternalScgParser.g:698:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token lv_term_1_0=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;



        	enterRule();

        try {
            // InternalScgParser.g:704:2: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) )
            // InternalScgParser.g:705:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            {
            // InternalScgParser.g:705:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            // InternalScgParser.g:706:3: ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )?
            {
            // InternalScgParser.g:706:3: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // InternalScgParser.g:707:4: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // InternalScgParser.g:707:4: (lv_id_0_0= ruleSnomedIdentifier )
            // InternalScgParser.g:708:5: lv_id_0_0= ruleSnomedIdentifier
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_12);
            lv_id_0_0=ruleSnomedIdentifier();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
              					}
              					set(
              						current,
              						"id",
              						lv_id_0_0,
              						"com.b2international.snowowl.snomed.scg.Scg.SnomedIdentifier");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalScgParser.g:725:3: ( (lv_term_1_0= RULE_TERM_STRING ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_TERM_STRING) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalScgParser.g:726:4: (lv_term_1_0= RULE_TERM_STRING )
                    {
                    // InternalScgParser.g:726:4: (lv_term_1_0= RULE_TERM_STRING )
                    // InternalScgParser.g:727:5: lv_term_1_0= RULE_TERM_STRING
                    {
                    lv_term_1_0=(Token)match(input,RULE_TERM_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_term_1_0, grammarAccess.getConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getConceptReferenceRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"term",
                      						lv_term_1_0,
                      						"com.b2international.snowowl.snomed.scg.Scg.TERM_STRING");
                      				
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
    // $ANTLR end "ruleConceptReference"


    // $ANTLR start "entryRuleSnomedIdentifier"
    // InternalScgParser.g:747:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:749:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // InternalScgParser.g:750:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSnomedIdentifierRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSnomedIdentifier=ruleSnomedIdentifier();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSnomedIdentifier.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleSnomedIdentifier"


    // $ANTLR start "ruleSnomedIdentifier"
    // InternalScgParser.g:759:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
    public final AntlrDatatypeRuleToken ruleSnomedIdentifier() throws RecognitionException {
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
            // InternalScgParser.g:766:2: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // InternalScgParser.g:767:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // InternalScgParser.g:767:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // InternalScgParser.g:768:3: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DIGIT_NONZERO_0);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0());
              		
            }
            // InternalScgParser.g:775:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_DIGIT_NONZERO) ) {
                alt12=1;
            }
            else if ( (LA12_0==RULE_ZERO) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // InternalScgParser.g:776:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:784:4: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_2);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1());
                      			
                    }

                    }
                    break;

            }

            // InternalScgParser.g:792:3: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_DIGIT_NONZERO) ) {
                alt13=1;
            }
            else if ( (LA13_0==RULE_ZERO) ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // InternalScgParser.g:793:4: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_3);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:801:4: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_4);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalScgParser.g:809:3: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_DIGIT_NONZERO) ) {
                alt14=1;
            }
            else if ( (LA14_0==RULE_ZERO) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // InternalScgParser.g:810:4: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_5);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:818:4: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_6);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1());
                      			
                    }

                    }
                    break;

            }

            // InternalScgParser.g:826:3: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
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
                    // InternalScgParser.g:827:4: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_7);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:835:4: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_13); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_8);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1());
                      			
                    }

                    }
                    break;

            }

            // InternalScgParser.g:843:3: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt16=0;
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==RULE_DIGIT_NONZERO) ) {
                    alt16=1;
                }
                else if ( (LA16_0==RULE_ZERO) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // InternalScgParser.g:844:4: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_9);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalScgParser.g:852:4: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_10);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
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
    // $ANTLR end "ruleSnomedIdentifier"


    // $ANTLR start "entryRuleNonNegativeInteger"
    // InternalScgParser.g:867:1: entryRuleNonNegativeInteger returns [String current=null] : iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF ;
    public final String entryRuleNonNegativeInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:869:2: (iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF )
            // InternalScgParser.g:870:2: iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNonNegativeIntegerRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNonNegativeInteger=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNonNegativeInteger.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNonNegativeInteger"


    // $ANTLR start "ruleNonNegativeInteger"
    // InternalScgParser.g:879:1: ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:886:2: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) )
            // InternalScgParser.g:887:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            {
            // InternalScgParser.g:887:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_ZERO) ) {
                alt18=1;
            }
            else if ( (LA18_0==RULE_DIGIT_NONZERO) ) {
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
                    // InternalScgParser.g:888:3: this_ZERO_0= RULE_ZERO
                    {
                    this_ZERO_0=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_ZERO_0);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_ZERO_0, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:896:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // InternalScgParser.g:896:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // InternalScgParser.g:897:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }
                    // InternalScgParser.g:904:4: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    loop17:
                    do {
                        int alt17=3;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==RULE_DIGIT_NONZERO) ) {
                            alt17=1;
                        }
                        else if ( (LA17_0==RULE_ZERO) ) {
                            alt17=2;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // InternalScgParser.g:905:5: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_DIGIT_NONZERO_2);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0());
                    	      				
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalScgParser.g:913:5: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_ZERO_3);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1());
                    	      				
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


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
    // $ANTLR end "ruleNonNegativeInteger"


    // $ANTLR start "entryRuleInteger"
    // InternalScgParser.g:929:1: entryRuleInteger returns [String current=null] : iv_ruleInteger= ruleInteger EOF ;
    public final String entryRuleInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:931:2: (iv_ruleInteger= ruleInteger EOF )
            // InternalScgParser.g:932:2: iv_ruleInteger= ruleInteger EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInteger=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleInteger.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleInteger"


    // $ANTLR start "ruleInteger"
    // InternalScgParser.g:941:1: ruleInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) ;
    public final AntlrDatatypeRuleToken ruleInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:948:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) )
            // InternalScgParser.g:949:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            {
            // InternalScgParser.g:949:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            // InternalScgParser.g:950:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger
            {
            // InternalScgParser.g:950:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt19=3;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==RULE_PLUS) ) {
                alt19=1;
            }
            else if ( (LA19_0==RULE_DASH) ) {
                alt19=2;
            }
            switch (alt19) {
                case 1 :
                    // InternalScgParser.g:951:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getIntegerAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:959:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DASH_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DASH_1, grammarAccess.getIntegerAccess().getDASHTerminalRuleCall_0_1());
                      			
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getIntegerAccess().getNonNegativeIntegerParserRuleCall_1());
              		
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_NonNegativeInteger_2=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeInteger_2);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
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
    // $ANTLR end "ruleInteger"


    // $ANTLR start "entryRuleDecimal"
    // InternalScgParser.g:984:1: entryRuleDecimal returns [String current=null] : iv_ruleDecimal= ruleDecimal EOF ;
    public final String entryRuleDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:986:2: (iv_ruleDecimal= ruleDecimal EOF )
            // InternalScgParser.g:987:2: iv_ruleDecimal= ruleDecimal EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimal=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimal.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDecimal"


    // $ANTLR start "ruleDecimal"
    // InternalScgParser.g:996:1: ruleDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) ;
    public final AntlrDatatypeRuleToken ruleDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeDecimal_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:1003:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) )
            // InternalScgParser.g:1004:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            {
            // InternalScgParser.g:1004:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            // InternalScgParser.g:1005:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal
            {
            // InternalScgParser.g:1005:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt20=3;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RULE_PLUS) ) {
                alt20=1;
            }
            else if ( (LA20_0==RULE_DASH) ) {
                alt20=2;
            }
            switch (alt20) {
                case 1 :
                    // InternalScgParser.g:1006:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getDecimalAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalScgParser.g:1014:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DASH_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DASH_1, grammarAccess.getDecimalAccess().getDASHTerminalRuleCall_0_1());
                      			
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getDecimalAccess().getNonNegativeDecimalParserRuleCall_1());
              		
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_NonNegativeDecimal_2=ruleNonNegativeDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeDecimal_2);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
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
    // $ANTLR end "ruleDecimal"


    // $ANTLR start "entryRuleNonNegativeDecimal"
    // InternalScgParser.g:1039:1: entryRuleNonNegativeDecimal returns [String current=null] : iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF ;
    public final String entryRuleNonNegativeDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:1041:2: (iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF )
            // InternalScgParser.g:1042:2: iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNonNegativeDecimalRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNonNegativeDecimal=ruleNonNegativeDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNonNegativeDecimal.getText(); 
            }
            match(input,EOF,FollowSets000.FOLLOW_2); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNonNegativeDecimal"


    // $ANTLR start "ruleNonNegativeDecimal"
    // InternalScgParser.g:1051:1: ruleNonNegativeDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DOT_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalScgParser.g:1058:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            // InternalScgParser.g:1059:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            {
            // InternalScgParser.g:1059:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            // InternalScgParser.g:1060:3: this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getNonNegativeDecimalAccess().getNonNegativeIntegerParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_15);
            this_NonNegativeInteger_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeInteger_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DOT_1);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOT_1, grammarAccess.getNonNegativeDecimalAccess().getDOTTerminalRuleCall_1());
              		
            }
            // InternalScgParser.g:1077:3: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            loop21:
            do {
                int alt21=3;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==RULE_DIGIT_NONZERO) ) {
                    alt21=1;
                }
                else if ( (LA21_0==RULE_ZERO) ) {
                    alt21=2;
                }


                switch (alt21) {
            	case 1 :
            	    // InternalScgParser.g:1078:4: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_2);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeDecimalAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalScgParser.g:1086:4: this_ZERO_3= RULE_ZERO
            	    {
            	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_14); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_3);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeDecimalAccess().getZEROTerminalRuleCall_2_1());
            	      			
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

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "ruleNonNegativeDecimal"

    // Delegated rules


    protected DFA10 dfa10 = new DFA10(this);
    static final String dfa_1s = "\15\uffff";
    static final String dfa_2s = "\7\uffff\2\11\2\uffff\2\11";
    static final String dfa_3s = "\1\10\3\uffff\3\7\1\11\1\7\2\uffff\2\7";
    static final String dfa_4s = "\1\27\3\uffff\1\17\2\10\2\22\2\uffff\2\22";
    static final String dfa_5s = "\1\uffff\1\1\1\2\1\3\5\uffff\1\4\1\5\2\uffff";
    static final String dfa_6s = "\15\uffff}>";
    static final String[] dfa_7s = {
            "\1\1\10\uffff\1\2\1\uffff\1\4\3\uffff\1\3",
            "",
            "",
            "",
            "\1\7\1\10\5\uffff\1\5\1\6",
            "\1\7\1\10",
            "\1\7\1\10",
            "\3\11\4\uffff\1\12\1\uffff\1\11",
            "\1\14\1\13\3\11\4\uffff\1\12\1\uffff\1\11",
            "",
            "",
            "\1\14\1\13\3\11\4\uffff\1\12\1\uffff\1\11",
            "\1\14\1\13\3\11\4\uffff\1\12\1\uffff\1\11"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final short[] dfa_2 = DFA.unpackEncodedString(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final char[] dfa_4 = DFA.unpackEncodedStringToUnsignedChars(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[] dfa_6 = DFA.unpackEncodedString(dfa_6s);
    static final short[][] dfa_7 = unpackEncodedStringArray(dfa_7s);

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = dfa_1;
            this.eof = dfa_2;
            this.min = dfa_3;
            this.max = dfa_4;
            this.accept = dfa_5;
            this.special = dfa_6;
            this.transition = dfa_7;
        }
        public String getDescription() {
            return "498:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000100L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000006002L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000300L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000B02L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000C00L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000001000L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x00000000008A0100L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000040000L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x000000000000C180L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000000042L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000180L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000182L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000010000L});
    }


}