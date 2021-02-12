package com.b2international.snowowl.snomed.etl.parser.antlr.internal;

import org.antlr.runtime.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;

import com.b2international.snowowl.snomed.etl.services.EtlGrammarAccess;
@SuppressWarnings("all")
public class InternalEtlParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "False", "True", "RULE_DOUBLE_SQUARE_OPEN", "RULE_DOUBLE_SQUARE_CLOSE", "RULE_TILDE", "RULE_AT", "RULE_ID", "RULE_SCG", "RULE_TOK", "RULE_STR", "RULE_INT", "RULE_DEC", "RULE_EQUIVALENT_TO", "RULE_SUBTYPE_OF", "RULE_STRING", "RULE_WS", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_SLOTNAME_STRING", "RULE_TERM_STRING", "RULE_REVERSED", "RULE_TO", "RULE_COMMA", "RULE_CONJUNCTION", "RULE_DISJUNCTION", "RULE_EXCLUSION", "RULE_ZERO", "RULE_DIGIT_NONZERO", "RULE_COLON", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_PLUS", "RULE_DASH", "RULE_CARET", "RULE_DOT", "RULE_WILDCARD", "RULE_EQUAL", "RULE_NOT_EQUAL", "RULE_LT", "RULE_GT", "RULE_DBL_LT", "RULE_DBL_GT", "RULE_LT_EM", "RULE_GT_EM", "RULE_GTE", "RULE_LTE", "RULE_HASH", "RULE_ML_COMMENT", "RULE_SL_COMMENT"
    };
    public static final int RULE_DIGIT_NONZERO=31;
    public static final int RULE_CURLY_OPEN=33;
    public static final int RULE_TO=25;
    public static final int RULE_ROUND_CLOSE=36;
    public static final int RULE_DBL_GT=47;
    public static final int True=5;
    public static final int RULE_GT=45;
    public static final int False=4;
    public static final int RULE_SLOTNAME_STRING=22;
    public static final int RULE_SCG=11;
    public static final int RULE_GTE=50;
    public static final int RULE_DOUBLE_SQUARE_OPEN=6;
    public static final int RULE_EQUIVALENT_TO=16;
    public static final int RULE_ROUND_OPEN=35;
    public static final int RULE_DBL_LT=46;
    public static final int RULE_NOT_EQUAL=43;
    public static final int RULE_SQUARE_CLOSE=21;
    public static final int RULE_ID=10;
    public static final int RULE_SQUARE_OPEN=20;
    public static final int RULE_EQUAL=42;
    public static final int RULE_DEC=15;
    public static final int RULE_COMMA=26;
    public static final int RULE_LT_EM=48;
    public static final int RULE_CURLY_CLOSE=34;
    public static final int RULE_ZERO=30;
    public static final int RULE_COLON=32;
    public static final int RULE_TILDE=8;
    public static final int RULE_LT=44;
    public static final int RULE_INT=14;
    public static final int RULE_ML_COMMENT=53;
    public static final int RULE_DOUBLE_SQUARE_CLOSE=7;
    public static final int RULE_LTE=51;
    public static final int RULE_STRING=18;
    public static final int RULE_AT=9;
    public static final int RULE_REVERSED=24;
    public static final int RULE_SL_COMMENT=54;
    public static final int RULE_HASH=52;
    public static final int RULE_TOK=12;
    public static final int RULE_DASH=38;
    public static final int RULE_PLUS=37;
    public static final int RULE_DOT=40;
    public static final int EOF=-1;
    public static final int RULE_SUBTYPE_OF=17;
    public static final int RULE_WS=19;
    public static final int RULE_GT_EM=49;
    public static final int RULE_EXCLUSION=29;
    public static final int RULE_CARET=39;
    public static final int RULE_CONJUNCTION=27;
    public static final int RULE_STR=13;
    public static final int RULE_WILDCARD=41;
    public static final int RULE_DISJUNCTION=28;
    public static final int RULE_TERM_STRING=23;

    // delegates
    // delegators


        public InternalEtlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalEtlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalEtlParser.tokenNames; }
    public String getGrammarFileName() { return "InternalEtlParser.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */

     	private EtlGrammarAccess grammarAccess;

        public InternalEtlParser(TokenStream input, EtlGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "ExpressionTemplate";
       	}

       	@Override
       	protected EtlGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleExpressionTemplate"
    // InternalEtlParser.g:75:1: entryRuleExpressionTemplate returns [EObject current=null] : iv_ruleExpressionTemplate= ruleExpressionTemplate EOF ;
    public final EObject entryRuleExpressionTemplate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionTemplate = null;


        try {
            // InternalEtlParser.g:75:59: (iv_ruleExpressionTemplate= ruleExpressionTemplate EOF )
            // InternalEtlParser.g:76:2: iv_ruleExpressionTemplate= ruleExpressionTemplate EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionTemplateRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpressionTemplate=ruleExpressionTemplate();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpressionTemplate; 
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
    // $ANTLR end "entryRuleExpressionTemplate"


    // $ANTLR start "ruleExpressionTemplate"
    // InternalEtlParser.g:82:1: ruleExpressionTemplate returns [EObject current=null] : ( () ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )? ) ;
    public final EObject ruleExpressionTemplate() throws RecognitionException {
        EObject current = null;

        Token lv_primitive_1_0=null;
        Token this_EQUIVALENT_TO_2=null;
        EObject lv_slot_3_0 = null;

        EObject lv_expression_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:88:2: ( ( () ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )? ) )
            // InternalEtlParser.g:89:2: ( () ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )? )
            {
            // InternalEtlParser.g:89:2: ( () ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )? )
            // InternalEtlParser.g:90:3: () ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )?
            {
            // InternalEtlParser.g:90:3: ()
            // InternalEtlParser.g:91:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getExpressionTemplateAccess().getExpressionTemplateAction_0(),
              					current);
              			
            }

            }

            // InternalEtlParser.g:100:3: ( ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_DOUBLE_SQUARE_OPEN||(LA3_0>=RULE_EQUIVALENT_TO && LA3_0<=RULE_SUBTYPE_OF)||LA3_0==RULE_DIGIT_NONZERO) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // InternalEtlParser.g:101:4: ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )? ( (lv_expression_4_0= ruleSubExpression ) )
                    {
                    // InternalEtlParser.g:101:4: ( ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO ) | ( (lv_slot_3_0= ruleTokenReplacementSlot ) ) )?
                    int alt2=3;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=RULE_EQUIVALENT_TO && LA2_0<=RULE_SUBTYPE_OF)) ) {
                        alt2=1;
                    }
                    else if ( (LA2_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                        int LA2_2 = input.LA(2);

                        if ( (LA2_2==RULE_PLUS) ) {
                            int LA2_4 = input.LA(3);

                            if ( (LA2_4==RULE_TOK) ) {
                                alt2=2;
                            }
                        }
                    }
                    switch (alt2) {
                        case 1 :
                            // InternalEtlParser.g:102:5: ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )
                            {
                            // InternalEtlParser.g:102:5: ( ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) ) | this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO )
                            int alt1=2;
                            int LA1_0 = input.LA(1);

                            if ( (LA1_0==RULE_SUBTYPE_OF) ) {
                                alt1=1;
                            }
                            else if ( (LA1_0==RULE_EQUIVALENT_TO) ) {
                                alt1=2;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return current;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 1, 0, input);

                                throw nvae;
                            }
                            switch (alt1) {
                                case 1 :
                                    // InternalEtlParser.g:103:6: ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) )
                                    {
                                    // InternalEtlParser.g:103:6: ( (lv_primitive_1_0= RULE_SUBTYPE_OF ) )
                                    // InternalEtlParser.g:104:7: (lv_primitive_1_0= RULE_SUBTYPE_OF )
                                    {
                                    // InternalEtlParser.g:104:7: (lv_primitive_1_0= RULE_SUBTYPE_OF )
                                    // InternalEtlParser.g:105:8: lv_primitive_1_0= RULE_SUBTYPE_OF
                                    {
                                    lv_primitive_1_0=(Token)match(input,RULE_SUBTYPE_OF,FollowSets000.FOLLOW_3); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                      								newLeafNode(lv_primitive_1_0, grammarAccess.getExpressionTemplateAccess().getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0_0());
                                      							
                                    }
                                    if ( state.backtracking==0 ) {

                                      								if (current==null) {
                                      									current = createModelElement(grammarAccess.getExpressionTemplateRule());
                                      								}
                                      								setWithLastConsumed(
                                      									current,
                                      									"primitive",
                                      									true,
                                      									"com.b2international.snowowl.snomed.etl.Etl.SUBTYPE_OF");
                                      							
                                    }

                                    }


                                    }


                                    }
                                    break;
                                case 2 :
                                    // InternalEtlParser.g:122:6: this_EQUIVALENT_TO_2= RULE_EQUIVALENT_TO
                                    {
                                    this_EQUIVALENT_TO_2=(Token)match(input,RULE_EQUIVALENT_TO,FollowSets000.FOLLOW_3); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                      						newLeafNode(this_EQUIVALENT_TO_2, grammarAccess.getExpressionTemplateAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_0_1());
                                      					
                                    }

                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // InternalEtlParser.g:128:5: ( (lv_slot_3_0= ruleTokenReplacementSlot ) )
                            {
                            // InternalEtlParser.g:128:5: ( (lv_slot_3_0= ruleTokenReplacementSlot ) )
                            // InternalEtlParser.g:129:6: (lv_slot_3_0= ruleTokenReplacementSlot )
                            {
                            // InternalEtlParser.g:129:6: (lv_slot_3_0= ruleTokenReplacementSlot )
                            // InternalEtlParser.g:130:7: lv_slot_3_0= ruleTokenReplacementSlot
                            {
                            if ( state.backtracking==0 ) {

                              							newCompositeNode(grammarAccess.getExpressionTemplateAccess().getSlotTokenReplacementSlotParserRuleCall_1_0_1_0());
                              						
                            }
                            pushFollow(FollowSets000.FOLLOW_3);
                            lv_slot_3_0=ruleTokenReplacementSlot();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              							if (current==null) {
                              								current = createModelElementForParent(grammarAccess.getExpressionTemplateRule());
                              							}
                              							set(
                              								current,
                              								"slot",
                              								lv_slot_3_0,
                              								"com.b2international.snowowl.snomed.etl.Etl.TokenReplacementSlot");
                              							afterParserOrEnumRuleCall();
                              						
                            }

                            }


                            }


                            }
                            break;

                    }

                    // InternalEtlParser.g:148:4: ( (lv_expression_4_0= ruleSubExpression ) )
                    // InternalEtlParser.g:149:5: (lv_expression_4_0= ruleSubExpression )
                    {
                    // InternalEtlParser.g:149:5: (lv_expression_4_0= ruleSubExpression )
                    // InternalEtlParser.g:150:6: lv_expression_4_0= ruleSubExpression
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getExpressionTemplateAccess().getExpressionSubExpressionParserRuleCall_1_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_expression_4_0=ruleSubExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getExpressionTemplateRule());
                      						}
                      						set(
                      							current,
                      							"expression",
                      							lv_expression_4_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SubExpression");
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
    // $ANTLR end "ruleExpressionTemplate"


    // $ANTLR start "entryRuleSubExpression"
    // InternalEtlParser.g:172:1: entryRuleSubExpression returns [EObject current=null] : iv_ruleSubExpression= ruleSubExpression EOF ;
    public final EObject entryRuleSubExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubExpression = null;


        try {
            // InternalEtlParser.g:172:54: (iv_ruleSubExpression= ruleSubExpression EOF )
            // InternalEtlParser.g:173:2: iv_ruleSubExpression= ruleSubExpression EOF
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
    // InternalEtlParser.g:179:1: ruleSubExpression returns [EObject current=null] : ( ( (lv_focusConcepts_0_0= ruleFocusConcept ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleSubExpression() throws RecognitionException {
        EObject current = null;

        Token this_PLUS_1=null;
        Token this_COLON_3=null;
        EObject lv_focusConcepts_0_0 = null;

        EObject lv_focusConcepts_2_0 = null;

        EObject lv_refinement_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:185:2: ( ( ( (lv_focusConcepts_0_0= ruleFocusConcept ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? ) )
            // InternalEtlParser.g:186:2: ( ( (lv_focusConcepts_0_0= ruleFocusConcept ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? )
            {
            // InternalEtlParser.g:186:2: ( ( (lv_focusConcepts_0_0= ruleFocusConcept ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )? )
            // InternalEtlParser.g:187:3: ( (lv_focusConcepts_0_0= ruleFocusConcept ) ) (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )* (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )?
            {
            // InternalEtlParser.g:187:3: ( (lv_focusConcepts_0_0= ruleFocusConcept ) )
            // InternalEtlParser.g:188:4: (lv_focusConcepts_0_0= ruleFocusConcept )
            {
            // InternalEtlParser.g:188:4: (lv_focusConcepts_0_0= ruleFocusConcept )
            // InternalEtlParser.g:189:5: lv_focusConcepts_0_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsFocusConceptParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_4);
            lv_focusConcepts_0_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.etl.Etl.FocusConcept");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEtlParser.g:206:3: (this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE_PLUS) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // InternalEtlParser.g:207:4: this_PLUS_1= RULE_PLUS ( (lv_focusConcepts_2_0= ruleFocusConcept ) )
            	    {
            	    this_PLUS_1=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_3); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_PLUS_1, grammarAccess.getSubExpressionAccess().getPLUSTerminalRuleCall_1_0());
            	      			
            	    }
            	    // InternalEtlParser.g:211:4: ( (lv_focusConcepts_2_0= ruleFocusConcept ) )
            	    // InternalEtlParser.g:212:5: (lv_focusConcepts_2_0= ruleFocusConcept )
            	    {
            	    // InternalEtlParser.g:212:5: (lv_focusConcepts_2_0= ruleFocusConcept )
            	    // InternalEtlParser.g:213:6: lv_focusConcepts_2_0= ruleFocusConcept
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsFocusConceptParserRuleCall_1_1_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_4);
            	    lv_focusConcepts_2_0=ruleFocusConcept();

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
            	      							"com.b2international.snowowl.snomed.etl.Etl.FocusConcept");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // InternalEtlParser.g:231:3: (this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_COLON) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalEtlParser.g:232:4: this_COLON_3= RULE_COLON ( (lv_refinement_4_0= ruleRefinement ) )
                    {
                    this_COLON_3=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_5); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_3, grammarAccess.getSubExpressionAccess().getCOLONTerminalRuleCall_2_0());
                      			
                    }
                    // InternalEtlParser.g:236:4: ( (lv_refinement_4_0= ruleRefinement ) )
                    // InternalEtlParser.g:237:5: (lv_refinement_4_0= ruleRefinement )
                    {
                    // InternalEtlParser.g:237:5: (lv_refinement_4_0= ruleRefinement )
                    // InternalEtlParser.g:238:6: lv_refinement_4_0= ruleRefinement
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
                      							"com.b2international.snowowl.snomed.etl.Etl.Refinement");
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


    // $ANTLR start "entryRuleFocusConcept"
    // InternalEtlParser.g:260:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // InternalEtlParser.g:260:53: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // InternalEtlParser.g:261:2: iv_ruleFocusConcept= ruleFocusConcept EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFocusConceptRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleFocusConcept=ruleFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFocusConcept; 
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
    // $ANTLR end "entryRuleFocusConcept"


    // $ANTLR start "ruleFocusConcept"
    // InternalEtlParser.g:267:1: ruleFocusConcept returns [EObject current=null] : ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_concept_1_0= ruleConceptReference ) ) ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject lv_slot_0_0 = null;

        EObject lv_concept_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:273:2: ( ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_concept_1_0= ruleConceptReference ) ) ) )
            // InternalEtlParser.g:274:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_concept_1_0= ruleConceptReference ) ) )
            {
            // InternalEtlParser.g:274:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_concept_1_0= ruleConceptReference ) ) )
            // InternalEtlParser.g:275:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_concept_1_0= ruleConceptReference ) )
            {
            // InternalEtlParser.g:275:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                int LA6_1 = input.LA(2);

                if ( ((LA6_1>=RULE_DOUBLE_SQUARE_CLOSE && LA6_1<=RULE_TILDE)||LA6_1==RULE_SLOTNAME_STRING||(LA6_1>=RULE_ZERO && LA6_1<=RULE_DIGIT_NONZERO)) ) {
                    alt6=1;
                }
            }
            switch (alt6) {
                case 1 :
                    // InternalEtlParser.g:276:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    {
                    // InternalEtlParser.g:276:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    // InternalEtlParser.g:277:5: lv_slot_0_0= ruleTemplateInformationSlot
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getFocusConceptAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_3);
                    lv_slot_0_0=ruleTemplateInformationSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getFocusConceptRule());
                      					}
                      					set(
                      						current,
                      						"slot",
                      						lv_slot_0_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalEtlParser.g:294:3: ( (lv_concept_1_0= ruleConceptReference ) )
            // InternalEtlParser.g:295:4: (lv_concept_1_0= ruleConceptReference )
            {
            // InternalEtlParser.g:295:4: (lv_concept_1_0= ruleConceptReference )
            // InternalEtlParser.g:296:5: lv_concept_1_0= ruleConceptReference
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptConceptReferenceParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_concept_1_0=ruleConceptReference();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getFocusConceptRule());
              					}
              					set(
              						current,
              						"concept",
              						lv_concept_1_0,
              						"com.b2international.snowowl.snomed.etl.Etl.ConceptReference");
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
    // $ANTLR end "ruleFocusConcept"


    // $ANTLR start "entryRuleRefinement"
    // InternalEtlParser.g:317:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // InternalEtlParser.g:317:51: (iv_ruleRefinement= ruleRefinement EOF )
            // InternalEtlParser.g:318:2: iv_ruleRefinement= ruleRefinement EOF
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
    // InternalEtlParser.g:324:1: ruleRefinement returns [EObject current=null] : ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* ) ;
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
            // InternalEtlParser.g:330:2: ( ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* ) )
            // InternalEtlParser.g:331:2: ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* )
            {
            // InternalEtlParser.g:331:2: ( ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )* )
            // InternalEtlParser.g:332:3: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) ) ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )*
            {
            // InternalEtlParser.g:332:3: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // InternalEtlParser.g:333:4: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
                    {
                    // InternalEtlParser.g:333:4: ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* )
                    // InternalEtlParser.g:334:5: ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
                    {
                    // InternalEtlParser.g:334:5: ( (lv_attributes_0_0= ruleAttribute ) )
                    // InternalEtlParser.g:335:6: (lv_attributes_0_0= ruleAttribute )
                    {
                    // InternalEtlParser.g:335:6: (lv_attributes_0_0= ruleAttribute )
                    // InternalEtlParser.g:336:7: lv_attributes_0_0= ruleAttribute
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
                      								"com.b2international.snowowl.snomed.etl.Etl.Attribute");
                      							afterParserOrEnumRuleCall();
                      						
                    }

                    }


                    }

                    // InternalEtlParser.g:353:5: (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*
                    loop7:
                    do {
                        int alt7=2;
                        alt7 = dfa7.predict(input);
                        switch (alt7) {
                    	case 1 :
                    	    // InternalEtlParser.g:354:6: this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) )
                    	    {
                    	    this_COMMA_1=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_3); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(this_COMMA_1, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_0_0_1_0());
                    	      					
                    	    }
                    	    // InternalEtlParser.g:358:6: ( (lv_attributes_2_0= ruleAttribute ) )
                    	    // InternalEtlParser.g:359:7: (lv_attributes_2_0= ruleAttribute )
                    	    {
                    	    // InternalEtlParser.g:359:7: (lv_attributes_2_0= ruleAttribute )
                    	    // InternalEtlParser.g:360:8: lv_attributes_2_0= ruleAttribute
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
                    	      									"com.b2international.snowowl.snomed.etl.Etl.Attribute");
                    	      								afterParserOrEnumRuleCall();
                    	      							
                    	    }

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:380:4: ( (lv_groups_3_0= ruleAttributeGroup ) )
                    {
                    // InternalEtlParser.g:380:4: ( (lv_groups_3_0= ruleAttributeGroup ) )
                    // InternalEtlParser.g:381:5: (lv_groups_3_0= ruleAttributeGroup )
                    {
                    // InternalEtlParser.g:381:5: (lv_groups_3_0= ruleAttributeGroup )
                    // InternalEtlParser.g:382:6: lv_groups_3_0= ruleAttributeGroup
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
                      							"com.b2international.snowowl.snomed.etl.Etl.AttributeGroup");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }


                    }
                    break;

            }

            // InternalEtlParser.g:400:3: ( (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==RULE_DOUBLE_SQUARE_OPEN||LA10_0==RULE_COMMA||LA10_0==RULE_CURLY_OPEN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // InternalEtlParser.g:401:4: (this_COMMA_4= RULE_COMMA )? ( (lv_groups_5_0= ruleAttributeGroup ) )
            	    {
            	    // InternalEtlParser.g:401:4: (this_COMMA_4= RULE_COMMA )?
            	    int alt9=2;
            	    int LA9_0 = input.LA(1);

            	    if ( (LA9_0==RULE_COMMA) ) {
            	        alt9=1;
            	    }
            	    switch (alt9) {
            	        case 1 :
            	            // InternalEtlParser.g:402:5: this_COMMA_4= RULE_COMMA
            	            {
            	            this_COMMA_4=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_5); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_COMMA_4, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_1_0());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEtlParser.g:407:4: ( (lv_groups_5_0= ruleAttributeGroup ) )
            	    // InternalEtlParser.g:408:5: (lv_groups_5_0= ruleAttributeGroup )
            	    {
            	    // InternalEtlParser.g:408:5: (lv_groups_5_0= ruleAttributeGroup )
            	    // InternalEtlParser.g:409:6: lv_groups_5_0= ruleAttributeGroup
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
            	      							"com.b2international.snowowl.snomed.etl.Etl.AttributeGroup");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // InternalEtlParser.g:431:1: entryRuleAttributeGroup returns [EObject current=null] : iv_ruleAttributeGroup= ruleAttributeGroup EOF ;
    public final EObject entryRuleAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeGroup = null;


        try {
            // InternalEtlParser.g:431:55: (iv_ruleAttributeGroup= ruleAttributeGroup EOF )
            // InternalEtlParser.g:432:2: iv_ruleAttributeGroup= ruleAttributeGroup EOF
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
    // InternalEtlParser.g:438:1: ruleAttributeGroup returns [EObject current=null] : ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_attributes_2_0= ruleAttribute ) ) (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )* this_CURLY_CLOSE_5= RULE_CURLY_CLOSE ) ;
    public final EObject ruleAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_CURLY_OPEN_1=null;
        Token this_COMMA_3=null;
        Token this_CURLY_CLOSE_5=null;
        EObject lv_slot_0_0 = null;

        EObject lv_attributes_2_0 = null;

        EObject lv_attributes_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:444:2: ( ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_attributes_2_0= ruleAttribute ) ) (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )* this_CURLY_CLOSE_5= RULE_CURLY_CLOSE ) )
            // InternalEtlParser.g:445:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_attributes_2_0= ruleAttribute ) ) (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )* this_CURLY_CLOSE_5= RULE_CURLY_CLOSE )
            {
            // InternalEtlParser.g:445:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_attributes_2_0= ruleAttribute ) ) (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )* this_CURLY_CLOSE_5= RULE_CURLY_CLOSE )
            // InternalEtlParser.g:446:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_attributes_2_0= ruleAttribute ) ) (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )* this_CURLY_CLOSE_5= RULE_CURLY_CLOSE
            {
            // InternalEtlParser.g:446:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalEtlParser.g:447:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    {
                    // InternalEtlParser.g:447:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    // InternalEtlParser.g:448:5: lv_slot_0_0= ruleTemplateInformationSlot
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeGroupAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_7);
                    lv_slot_0_0=ruleTemplateInformationSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
                      					}
                      					set(
                      						current,
                      						"slot",
                      						lv_slot_0_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            this_CURLY_OPEN_1=(Token)match(input,RULE_CURLY_OPEN,FollowSets000.FOLLOW_3); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:469:3: ( (lv_attributes_2_0= ruleAttribute ) )
            // InternalEtlParser.g:470:4: (lv_attributes_2_0= ruleAttribute )
            {
            // InternalEtlParser.g:470:4: (lv_attributes_2_0= ruleAttribute )
            // InternalEtlParser.g:471:5: lv_attributes_2_0= ruleAttribute
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_8);
            lv_attributes_2_0=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
              					}
              					add(
              						current,
              						"attributes",
              						lv_attributes_2_0,
              						"com.b2international.snowowl.snomed.etl.Etl.Attribute");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEtlParser.g:488:3: (this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==RULE_COMMA) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // InternalEtlParser.g:489:4: this_COMMA_3= RULE_COMMA ( (lv_attributes_4_0= ruleAttribute ) )
            	    {
            	    this_COMMA_3=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_3); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_COMMA_3, grammarAccess.getAttributeGroupAccess().getCOMMATerminalRuleCall_3_0());
            	      			
            	    }
            	    // InternalEtlParser.g:493:4: ( (lv_attributes_4_0= ruleAttribute ) )
            	    // InternalEtlParser.g:494:5: (lv_attributes_4_0= ruleAttribute )
            	    {
            	    // InternalEtlParser.g:494:5: (lv_attributes_4_0= ruleAttribute )
            	    // InternalEtlParser.g:495:6: lv_attributes_4_0= ruleAttribute
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_3_1_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_8);
            	    lv_attributes_4_0=ruleAttribute();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
            	      						}
            	      						add(
            	      							current,
            	      							"attributes",
            	      							lv_attributes_4_0,
            	      							"com.b2international.snowowl.snomed.etl.Etl.Attribute");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            this_CURLY_CLOSE_5=(Token)match(input,RULE_CURLY_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_CLOSE_5, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_4());
              		
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
    // InternalEtlParser.g:521:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // InternalEtlParser.g:521:50: (iv_ruleAttribute= ruleAttribute EOF )
            // InternalEtlParser.g:522:2: iv_ruleAttribute= ruleAttribute EOF
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
    // InternalEtlParser.g:528:1: ruleAttribute returns [EObject current=null] : ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_name_1_0= ruleConceptReference ) ) this_EQUAL_2= RULE_EQUAL ( (lv_value_3_0= ruleAttributeValue ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_2=null;
        EObject lv_slot_0_0 = null;

        EObject lv_name_1_0 = null;

        EObject lv_value_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:534:2: ( ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_name_1_0= ruleConceptReference ) ) this_EQUAL_2= RULE_EQUAL ( (lv_value_3_0= ruleAttributeValue ) ) ) )
            // InternalEtlParser.g:535:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_name_1_0= ruleConceptReference ) ) this_EQUAL_2= RULE_EQUAL ( (lv_value_3_0= ruleAttributeValue ) ) )
            {
            // InternalEtlParser.g:535:2: ( ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_name_1_0= ruleConceptReference ) ) this_EQUAL_2= RULE_EQUAL ( (lv_value_3_0= ruleAttributeValue ) ) )
            // InternalEtlParser.g:536:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )? ( (lv_name_1_0= ruleConceptReference ) ) this_EQUAL_2= RULE_EQUAL ( (lv_value_3_0= ruleAttributeValue ) )
            {
            // InternalEtlParser.g:536:3: ( (lv_slot_0_0= ruleTemplateInformationSlot ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                int LA13_1 = input.LA(2);

                if ( ((LA13_1>=RULE_DOUBLE_SQUARE_CLOSE && LA13_1<=RULE_TILDE)||LA13_1==RULE_SLOTNAME_STRING||(LA13_1>=RULE_ZERO && LA13_1<=RULE_DIGIT_NONZERO)) ) {
                    alt13=1;
                }
            }
            switch (alt13) {
                case 1 :
                    // InternalEtlParser.g:537:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    {
                    // InternalEtlParser.g:537:4: (lv_slot_0_0= ruleTemplateInformationSlot )
                    // InternalEtlParser.g:538:5: lv_slot_0_0= ruleTemplateInformationSlot
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_3);
                    lv_slot_0_0=ruleTemplateInformationSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getAttributeRule());
                      					}
                      					set(
                      						current,
                      						"slot",
                      						lv_slot_0_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalEtlParser.g:555:3: ( (lv_name_1_0= ruleConceptReference ) )
            // InternalEtlParser.g:556:4: (lv_name_1_0= ruleConceptReference )
            {
            // InternalEtlParser.g:556:4: (lv_name_1_0= ruleConceptReference )
            // InternalEtlParser.g:557:5: lv_name_1_0= ruleConceptReference
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeAccess().getNameConceptReferenceParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_9);
            lv_name_1_0=ruleConceptReference();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeRule());
              					}
              					set(
              						current,
              						"name",
              						lv_name_1_0,
              						"com.b2international.snowowl.snomed.etl.Etl.ConceptReference");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_EQUAL_2=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_10); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_2, grammarAccess.getAttributeAccess().getEQUALTerminalRuleCall_2());
              		
            }
            // InternalEtlParser.g:578:3: ( (lv_value_3_0= ruleAttributeValue ) )
            // InternalEtlParser.g:579:4: (lv_value_3_0= ruleAttributeValue )
            {
            // InternalEtlParser.g:579:4: (lv_value_3_0= ruleAttributeValue )
            // InternalEtlParser.g:580:5: lv_value_3_0= ruleAttributeValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_3_0=ruleAttributeValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_3_0,
              						"com.b2international.snowowl.snomed.etl.Etl.AttributeValue");
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
    // InternalEtlParser.g:601:1: entryRuleAttributeValue returns [EObject current=null] : iv_ruleAttributeValue= ruleAttributeValue EOF ;
    public final EObject entryRuleAttributeValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValue = null;


        try {
            // InternalEtlParser.g:601:55: (iv_ruleAttributeValue= ruleAttributeValue EOF )
            // InternalEtlParser.g:602:2: iv_ruleAttributeValue= ruleAttributeValue EOF
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
    // InternalEtlParser.g:608:1: ruleAttributeValue returns [EObject current=null] : (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue | this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot ) ;
    public final EObject ruleAttributeValue() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_1=null;
        Token this_ROUND_CLOSE_3=null;
        EObject this_ConceptReference_0 = null;

        EObject this_SubExpression_2 = null;

        EObject this_StringValue_4 = null;

        EObject this_IntegerValue_5 = null;

        EObject this_DecimalValue_6 = null;

        EObject this_ConcreteValueReplacementSlot_7 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:614:2: ( (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue | this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot ) )
            // InternalEtlParser.g:615:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue | this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot )
            {
            // InternalEtlParser.g:615:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue | this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot )
            int alt14=6;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // InternalEtlParser.g:616:3: this_ConceptReference_0= ruleConceptReference
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
                    // InternalEtlParser.g:628:3: (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE )
                    {
                    // InternalEtlParser.g:628:3: (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE )
                    // InternalEtlParser.g:629:4: this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE
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
                    pushFollow(FollowSets000.FOLLOW_11);
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
                    // InternalEtlParser.g:650:3: this_StringValue_4= ruleStringValue
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
                    // InternalEtlParser.g:662:3: this_IntegerValue_5= ruleIntegerValue
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
                    // InternalEtlParser.g:674:3: this_DecimalValue_6= ruleDecimalValue
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
                case 6 :
                    // InternalEtlParser.g:686:3: this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeValueAccess().getConcreteValueReplacementSlotParserRuleCall_5());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConcreteValueReplacementSlot_7=ruleConcreteValueReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ConcreteValueReplacementSlot_7;
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


    // $ANTLR start "entryRuleConceptIdReplacementSlot"
    // InternalEtlParser.g:701:1: entryRuleConceptIdReplacementSlot returns [EObject current=null] : iv_ruleConceptIdReplacementSlot= ruleConceptIdReplacementSlot EOF ;
    public final EObject entryRuleConceptIdReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptIdReplacementSlot = null;


        try {
            // InternalEtlParser.g:701:65: (iv_ruleConceptIdReplacementSlot= ruleConceptIdReplacementSlot EOF )
            // InternalEtlParser.g:702:2: iv_ruleConceptIdReplacementSlot= ruleConceptIdReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptIdReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConceptIdReplacementSlot=ruleConceptIdReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptIdReplacementSlot; 
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
    // $ANTLR end "entryRuleConceptIdReplacementSlot"


    // $ANTLR start "ruleConceptIdReplacementSlot"
    // InternalEtlParser.g:708:1: ruleConceptIdReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_ID_3= RULE_ID (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleConceptIdReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_ID_3=null;
        Token this_ROUND_OPEN_4=null;
        Token this_ROUND_CLOSE_6=null;
        Token lv_name_7_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_8=null;
        EObject lv_constraint_5_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:714:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_ID_3= RULE_ID (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:715:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_ID_3= RULE_ID (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:715:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_ID_3= RULE_ID (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:716:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_ID_3= RULE_ID (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:716:3: ()
            // InternalEtlParser.g:717:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getConceptIdReplacementSlotAccess().getConceptIdReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getConceptIdReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_13); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getConceptIdReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            this_ID_3=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ID_3, grammarAccess.getConceptIdReplacementSlotAccess().getIDTerminalRuleCall_3());
              		
            }
            // InternalEtlParser.g:738:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_ROUND_OPEN) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalEtlParser.g:739:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getConceptIdReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:743:4: ( (lv_constraint_5_0= ruleExpressionConstraint ) )
                    // InternalEtlParser.g:744:5: (lv_constraint_5_0= ruleExpressionConstraint )
                    {
                    // InternalEtlParser.g:744:5: (lv_constraint_5_0= ruleExpressionConstraint )
                    // InternalEtlParser.g:745:6: lv_constraint_5_0= ruleExpressionConstraint
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getConceptIdReplacementSlotAccess().getConstraintExpressionConstraintParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_11);
                    lv_constraint_5_0=ruleExpressionConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getConceptIdReplacementSlotRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_5_0,
                      							"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_ROUND_CLOSE_6=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_6, grammarAccess.getConceptIdReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_2());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:767:3: ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_SLOTNAME_STRING) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalEtlParser.g:768:4: (lv_name_7_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:768:4: (lv_name_7_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:769:5: lv_name_7_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_7_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_7_0, grammarAccess.getConceptIdReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getConceptIdReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_7_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_8=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_8, grammarAccess.getConceptIdReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleConceptIdReplacementSlot"


    // $ANTLR start "entryRuleExpressionReplacementSlot"
    // InternalEtlParser.g:793:1: entryRuleExpressionReplacementSlot returns [EObject current=null] : iv_ruleExpressionReplacementSlot= ruleExpressionReplacementSlot EOF ;
    public final EObject entryRuleExpressionReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionReplacementSlot = null;


        try {
            // InternalEtlParser.g:793:66: (iv_ruleExpressionReplacementSlot= ruleExpressionReplacementSlot EOF )
            // InternalEtlParser.g:794:2: iv_ruleExpressionReplacementSlot= ruleExpressionReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpressionReplacementSlot=ruleExpressionReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpressionReplacementSlot; 
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
    // $ANTLR end "entryRuleExpressionReplacementSlot"


    // $ANTLR start "ruleExpressionReplacementSlot"
    // InternalEtlParser.g:800:1: ruleExpressionReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS (this_SCG_3= RULE_SCG )? (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleExpressionReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_SCG_3=null;
        Token this_ROUND_OPEN_4=null;
        Token this_ROUND_CLOSE_6=null;
        Token lv_name_7_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_8=null;
        EObject lv_constraint_5_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:806:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS (this_SCG_3= RULE_SCG )? (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:807:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS (this_SCG_3= RULE_SCG )? (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:807:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS (this_SCG_3= RULE_SCG )? (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:808:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS (this_SCG_3= RULE_SCG )? (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )? ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_8= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:808:3: ()
            // InternalEtlParser.g:809:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getExpressionReplacementSlotAccess().getExpressionReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getExpressionReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_18); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getExpressionReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            // InternalEtlParser.g:826:3: (this_SCG_3= RULE_SCG )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==RULE_SCG) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // InternalEtlParser.g:827:4: this_SCG_3= RULE_SCG
                    {
                    this_SCG_3=(Token)match(input,RULE_SCG,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_SCG_3, grammarAccess.getExpressionReplacementSlotAccess().getSCGTerminalRuleCall_3());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:832:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_ROUND_OPEN) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalEtlParser.g:833:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_constraint_5_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_6= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getExpressionReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:837:4: ( (lv_constraint_5_0= ruleExpressionConstraint ) )
                    // InternalEtlParser.g:838:5: (lv_constraint_5_0= ruleExpressionConstraint )
                    {
                    // InternalEtlParser.g:838:5: (lv_constraint_5_0= ruleExpressionConstraint )
                    // InternalEtlParser.g:839:6: lv_constraint_5_0= ruleExpressionConstraint
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getExpressionReplacementSlotAccess().getConstraintExpressionConstraintParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_11);
                    lv_constraint_5_0=ruleExpressionConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getExpressionReplacementSlotRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_5_0,
                      							"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_ROUND_CLOSE_6=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_6, grammarAccess.getExpressionReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_2());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:861:3: ( (lv_name_7_0= RULE_SLOTNAME_STRING ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==RULE_SLOTNAME_STRING) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // InternalEtlParser.g:862:4: (lv_name_7_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:862:4: (lv_name_7_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:863:5: lv_name_7_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_7_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_7_0, grammarAccess.getExpressionReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getExpressionReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_7_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_8=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_8, grammarAccess.getExpressionReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleExpressionReplacementSlot"


    // $ANTLR start "entryRuleTokenReplacementSlot"
    // InternalEtlParser.g:887:1: entryRuleTokenReplacementSlot returns [EObject current=null] : iv_ruleTokenReplacementSlot= ruleTokenReplacementSlot EOF ;
    public final EObject entryRuleTokenReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTokenReplacementSlot = null;


        try {
            // InternalEtlParser.g:887:61: (iv_ruleTokenReplacementSlot= ruleTokenReplacementSlot EOF )
            // InternalEtlParser.g:888:2: iv_ruleTokenReplacementSlot= ruleTokenReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTokenReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleTokenReplacementSlot=ruleTokenReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTokenReplacementSlot; 
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
    // $ANTLR end "entryRuleTokenReplacementSlot"


    // $ANTLR start "ruleTokenReplacementSlot"
    // InternalEtlParser.g:894:1: ruleTokenReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_TOK_3= RULE_TOK (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleTokenReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_TOK_3=null;
        Token this_ROUND_OPEN_4=null;
        Token this_ROUND_CLOSE_7=null;
        Token lv_name_8_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_9=null;
        AntlrDatatypeRuleToken lv_tokens_5_0 = null;

        AntlrDatatypeRuleToken lv_tokens_6_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:900:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_TOK_3= RULE_TOK (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:901:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_TOK_3= RULE_TOK (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:901:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_TOK_3= RULE_TOK (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:902:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_TOK_3= RULE_TOK (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:902:3: ()
            // InternalEtlParser.g:903:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getTokenReplacementSlotAccess().getTokenReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getTokenReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_19); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getTokenReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            this_TOK_3=(Token)match(input,RULE_TOK,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_TOK_3, grammarAccess.getTokenReplacementSlotAccess().getTOKTerminalRuleCall_3());
              		
            }
            // InternalEtlParser.g:924:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_ROUND_OPEN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalEtlParser.g:925:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_tokens_5_0= ruleSlotToken ) ) ( (lv_tokens_6_0= ruleSlotToken ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_20); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getTokenReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:929:4: ( (lv_tokens_5_0= ruleSlotToken ) )
                    // InternalEtlParser.g:930:5: (lv_tokens_5_0= ruleSlotToken )
                    {
                    // InternalEtlParser.g:930:5: (lv_tokens_5_0= ruleSlotToken )
                    // InternalEtlParser.g:931:6: lv_tokens_5_0= ruleSlotToken
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getTokenReplacementSlotAccess().getTokensSlotTokenParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_21);
                    lv_tokens_5_0=ruleSlotToken();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getTokenReplacementSlotRule());
                      						}
                      						add(
                      							current,
                      							"tokens",
                      							lv_tokens_5_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotToken");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalEtlParser.g:948:4: ( (lv_tokens_6_0= ruleSlotToken ) )*
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( ((LA20_0>=RULE_EQUIVALENT_TO && LA20_0<=RULE_SUBTYPE_OF)||LA20_0==RULE_REVERSED||(LA20_0>=RULE_COMMA && LA20_0<=RULE_EXCLUSION)||LA20_0==RULE_CARET||(LA20_0>=RULE_EQUAL && LA20_0<=RULE_LTE)) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // InternalEtlParser.g:949:5: (lv_tokens_6_0= ruleSlotToken )
                    	    {
                    	    // InternalEtlParser.g:949:5: (lv_tokens_6_0= ruleSlotToken )
                    	    // InternalEtlParser.g:950:6: lv_tokens_6_0= ruleSlotToken
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getTokenReplacementSlotAccess().getTokensSlotTokenParserRuleCall_4_2_0());
                    	      					
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_21);
                    	    lv_tokens_6_0=ruleSlotToken();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getTokenReplacementSlotRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"tokens",
                    	      							lv_tokens_6_0,
                    	      							"com.b2international.snowowl.snomed.etl.Etl.SlotToken");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);

                    this_ROUND_CLOSE_7=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getTokenReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:972:3: ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_SLOTNAME_STRING) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalEtlParser.g:973:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:973:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:974:5: lv_name_8_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_8_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_8_0, grammarAccess.getTokenReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getTokenReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_8_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_9=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getTokenReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleTokenReplacementSlot"


    // $ANTLR start "entryRuleTemplateInformationSlot"
    // InternalEtlParser.g:998:1: entryRuleTemplateInformationSlot returns [EObject current=null] : iv_ruleTemplateInformationSlot= ruleTemplateInformationSlot EOF ;
    public final EObject entryRuleTemplateInformationSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTemplateInformationSlot = null;


        try {
            // InternalEtlParser.g:998:64: (iv_ruleTemplateInformationSlot= ruleTemplateInformationSlot EOF )
            // InternalEtlParser.g:999:2: iv_ruleTemplateInformationSlot= ruleTemplateInformationSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTemplateInformationSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleTemplateInformationSlot=ruleTemplateInformationSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTemplateInformationSlot; 
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
    // $ANTLR end "entryRuleTemplateInformationSlot"


    // $ANTLR start "ruleTemplateInformationSlot"
    // InternalEtlParser.g:1005:1: ruleTemplateInformationSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN ( (lv_cardinality_2_0= ruleEtlCardinality ) )? ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_4= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleTemplateInformationSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token lv_name_3_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_4=null;
        EObject lv_cardinality_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1011:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN ( (lv_cardinality_2_0= ruleEtlCardinality ) )? ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_4= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:1012:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN ( (lv_cardinality_2_0= ruleEtlCardinality ) )? ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_4= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:1012:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN ( (lv_cardinality_2_0= ruleEtlCardinality ) )? ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_4= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:1013:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN ( (lv_cardinality_2_0= ruleEtlCardinality ) )? ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_4= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:1013:3: ()
            // InternalEtlParser.g:1014:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getTemplateInformationSlotAccess().getTemplateInformationSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_22); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getTemplateInformationSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:1027:3: ( (lv_cardinality_2_0= ruleEtlCardinality ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==RULE_TILDE||(LA23_0>=RULE_ZERO && LA23_0<=RULE_DIGIT_NONZERO)) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalEtlParser.g:1028:4: (lv_cardinality_2_0= ruleEtlCardinality )
                    {
                    // InternalEtlParser.g:1028:4: (lv_cardinality_2_0= ruleEtlCardinality )
                    // InternalEtlParser.g:1029:5: lv_cardinality_2_0= ruleEtlCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getTemplateInformationSlotAccess().getCardinalityEtlCardinalityParserRuleCall_2_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_16);
                    lv_cardinality_2_0=ruleEtlCardinality();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getTemplateInformationSlotRule());
                      					}
                      					set(
                      						current,
                      						"cardinality",
                      						lv_cardinality_2_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.EtlCardinality");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalEtlParser.g:1046:3: ( (lv_name_3_0= RULE_SLOTNAME_STRING ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_SLOTNAME_STRING) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalEtlParser.g:1047:4: (lv_name_3_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:1047:4: (lv_name_3_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:1048:5: lv_name_3_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_3_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_3_0, grammarAccess.getTemplateInformationSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_3_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getTemplateInformationSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_3_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_4=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_4, grammarAccess.getTemplateInformationSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_4());
              		
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
    // $ANTLR end "ruleTemplateInformationSlot"


    // $ANTLR start "entryRuleConcreteValueReplacementSlot"
    // InternalEtlParser.g:1072:1: entryRuleConcreteValueReplacementSlot returns [EObject current=null] : iv_ruleConcreteValueReplacementSlot= ruleConcreteValueReplacementSlot EOF ;
    public final EObject entryRuleConcreteValueReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConcreteValueReplacementSlot = null;


        try {
            // InternalEtlParser.g:1072:69: (iv_ruleConcreteValueReplacementSlot= ruleConcreteValueReplacementSlot EOF )
            // InternalEtlParser.g:1073:2: iv_ruleConcreteValueReplacementSlot= ruleConcreteValueReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConcreteValueReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConcreteValueReplacementSlot=ruleConcreteValueReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConcreteValueReplacementSlot; 
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
    // $ANTLR end "entryRuleConcreteValueReplacementSlot"


    // $ANTLR start "ruleConcreteValueReplacementSlot"
    // InternalEtlParser.g:1079:1: ruleConcreteValueReplacementSlot returns [EObject current=null] : (this_StringReplacementSlot_0= ruleStringReplacementSlot | this_IntegerReplacementSlot_1= ruleIntegerReplacementSlot | this_DecimalReplacementSlot_2= ruleDecimalReplacementSlot ) ;
    public final EObject ruleConcreteValueReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject this_StringReplacementSlot_0 = null;

        EObject this_IntegerReplacementSlot_1 = null;

        EObject this_DecimalReplacementSlot_2 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1085:2: ( (this_StringReplacementSlot_0= ruleStringReplacementSlot | this_IntegerReplacementSlot_1= ruleIntegerReplacementSlot | this_DecimalReplacementSlot_2= ruleDecimalReplacementSlot ) )
            // InternalEtlParser.g:1086:2: (this_StringReplacementSlot_0= ruleStringReplacementSlot | this_IntegerReplacementSlot_1= ruleIntegerReplacementSlot | this_DecimalReplacementSlot_2= ruleDecimalReplacementSlot )
            {
            // InternalEtlParser.g:1086:2: (this_StringReplacementSlot_0= ruleStringReplacementSlot | this_IntegerReplacementSlot_1= ruleIntegerReplacementSlot | this_DecimalReplacementSlot_2= ruleDecimalReplacementSlot )
            int alt25=3;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==RULE_PLUS) ) {
                    switch ( input.LA(3) ) {
                    case RULE_STR:
                        {
                        alt25=1;
                        }
                        break;
                    case RULE_DEC:
                        {
                        alt25=3;
                        }
                        break;
                    case RULE_INT:
                        {
                        alt25=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 25, 2, input);

                        throw nvae;
                    }

                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // InternalEtlParser.g:1087:3: this_StringReplacementSlot_0= ruleStringReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getStringReplacementSlotParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringReplacementSlot_0=ruleStringReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringReplacementSlot_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:1099:3: this_IntegerReplacementSlot_1= ruleIntegerReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getIntegerReplacementSlotParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerReplacementSlot_1=ruleIntegerReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerReplacementSlot_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:1111:3: this_DecimalReplacementSlot_2= ruleDecimalReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getDecimalReplacementSlotParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalReplacementSlot_2=ruleDecimalReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalReplacementSlot_2;
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
    // $ANTLR end "ruleConcreteValueReplacementSlot"


    // $ANTLR start "entryRuleStringReplacementSlot"
    // InternalEtlParser.g:1126:1: entryRuleStringReplacementSlot returns [EObject current=null] : iv_ruleStringReplacementSlot= ruleStringReplacementSlot EOF ;
    public final EObject entryRuleStringReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringReplacementSlot = null;


        try {
            // InternalEtlParser.g:1126:62: (iv_ruleStringReplacementSlot= ruleStringReplacementSlot EOF )
            // InternalEtlParser.g:1127:2: iv_ruleStringReplacementSlot= ruleStringReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStringReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringReplacementSlot=ruleStringReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStringReplacementSlot; 
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
    // $ANTLR end "entryRuleStringReplacementSlot"


    // $ANTLR start "ruleStringReplacementSlot"
    // InternalEtlParser.g:1133:1: ruleStringReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_STR_3= RULE_STR (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleStringReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_STR_3=null;
        Token this_ROUND_OPEN_4=null;
        Token lv_values_5_0=null;
        Token lv_values_6_0=null;
        Token this_ROUND_CLOSE_7=null;
        Token lv_name_8_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_9=null;


        	enterRule();

        try {
            // InternalEtlParser.g:1139:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_STR_3= RULE_STR (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:1140:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_STR_3= RULE_STR (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:1140:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_STR_3= RULE_STR (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:1141:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_STR_3= RULE_STR (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:1141:3: ()
            // InternalEtlParser.g:1142:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getStringReplacementSlotAccess().getStringReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getStringReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getStringReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            this_STR_3=(Token)match(input,RULE_STR,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_STR_3, grammarAccess.getStringReplacementSlotAccess().getSTRTerminalRuleCall_3());
              		
            }
            // InternalEtlParser.g:1163:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==RULE_ROUND_OPEN) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalEtlParser.g:1164:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= RULE_STRING ) ) ( (lv_values_6_0= RULE_STRING ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_24); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getStringReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:1168:4: ( (lv_values_5_0= RULE_STRING ) )
                    // InternalEtlParser.g:1169:5: (lv_values_5_0= RULE_STRING )
                    {
                    // InternalEtlParser.g:1169:5: (lv_values_5_0= RULE_STRING )
                    // InternalEtlParser.g:1170:6: lv_values_5_0= RULE_STRING
                    {
                    lv_values_5_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_25); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						newLeafNode(lv_values_5_0, grammarAccess.getStringReplacementSlotAccess().getValuesSTRINGTerminalRuleCall_4_1_0());
                      					
                    }
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElement(grammarAccess.getStringReplacementSlotRule());
                      						}
                      						addWithLastConsumed(
                      							current,
                      							"values",
                      							lv_values_5_0,
                      							"com.b2international.snomed.ecl.Ecl.STRING");
                      					
                    }

                    }


                    }

                    // InternalEtlParser.g:1186:4: ( (lv_values_6_0= RULE_STRING ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==RULE_STRING) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // InternalEtlParser.g:1187:5: (lv_values_6_0= RULE_STRING )
                    	    {
                    	    // InternalEtlParser.g:1187:5: (lv_values_6_0= RULE_STRING )
                    	    // InternalEtlParser.g:1188:6: lv_values_6_0= RULE_STRING
                    	    {
                    	    lv_values_6_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_25); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						newLeafNode(lv_values_6_0, grammarAccess.getStringReplacementSlotAccess().getValuesSTRINGTerminalRuleCall_4_2_0());
                    	      					
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElement(grammarAccess.getStringReplacementSlotRule());
                    	      						}
                    	      						addWithLastConsumed(
                    	      							current,
                    	      							"values",
                    	      							lv_values_6_0,
                    	      							"com.b2international.snomed.ecl.Ecl.STRING");
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    this_ROUND_CLOSE_7=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getStringReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:1209:3: ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_SLOTNAME_STRING) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalEtlParser.g:1210:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:1210:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:1211:5: lv_name_8_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_8_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_8_0, grammarAccess.getStringReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getStringReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_8_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_9=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getStringReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleStringReplacementSlot"


    // $ANTLR start "entryRuleIntegerReplacementSlot"
    // InternalEtlParser.g:1235:1: entryRuleIntegerReplacementSlot returns [EObject current=null] : iv_ruleIntegerReplacementSlot= ruleIntegerReplacementSlot EOF ;
    public final EObject entryRuleIntegerReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerReplacementSlot = null;


        try {
            // InternalEtlParser.g:1235:63: (iv_ruleIntegerReplacementSlot= ruleIntegerReplacementSlot EOF )
            // InternalEtlParser.g:1236:2: iv_ruleIntegerReplacementSlot= ruleIntegerReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerReplacementSlot=ruleIntegerReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerReplacementSlot; 
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
    // $ANTLR end "entryRuleIntegerReplacementSlot"


    // $ANTLR start "ruleIntegerReplacementSlot"
    // InternalEtlParser.g:1242:1: ruleIntegerReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_INT_3= RULE_INT (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleIntegerReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_INT_3=null;
        Token this_ROUND_OPEN_4=null;
        Token this_ROUND_CLOSE_7=null;
        Token lv_name_8_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_9=null;
        EObject lv_values_5_0 = null;

        EObject lv_values_6_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1248:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_INT_3= RULE_INT (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:1249:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_INT_3= RULE_INT (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:1249:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_INT_3= RULE_INT (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:1250:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_INT_3= RULE_INT (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:1250:3: ()
            // InternalEtlParser.g:1251:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getIntegerReplacementSlotAccess().getIntegerReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getIntegerReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_26); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getIntegerReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            this_INT_3=(Token)match(input,RULE_INT,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_INT_3, grammarAccess.getIntegerReplacementSlotAccess().getINTTerminalRuleCall_3());
              		
            }
            // InternalEtlParser.g:1272:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==RULE_ROUND_OPEN) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalEtlParser.g:1273:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotInteger ) ) ( (lv_values_6_0= ruleSlotInteger ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_27); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getIntegerReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:1277:4: ( (lv_values_5_0= ruleSlotInteger ) )
                    // InternalEtlParser.g:1278:5: (lv_values_5_0= ruleSlotInteger )
                    {
                    // InternalEtlParser.g:1278:5: (lv_values_5_0= ruleSlotInteger )
                    // InternalEtlParser.g:1279:6: lv_values_5_0= ruleSlotInteger
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getIntegerReplacementSlotAccess().getValuesSlotIntegerParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_28);
                    lv_values_5_0=ruleSlotInteger();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getIntegerReplacementSlotRule());
                      						}
                      						add(
                      							current,
                      							"values",
                      							lv_values_5_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotInteger");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalEtlParser.g:1296:4: ( (lv_values_6_0= ruleSlotInteger ) )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==RULE_TO||LA29_0==RULE_GT||LA29_0==RULE_HASH) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // InternalEtlParser.g:1297:5: (lv_values_6_0= ruleSlotInteger )
                    	    {
                    	    // InternalEtlParser.g:1297:5: (lv_values_6_0= ruleSlotInteger )
                    	    // InternalEtlParser.g:1298:6: lv_values_6_0= ruleSlotInteger
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getIntegerReplacementSlotAccess().getValuesSlotIntegerParserRuleCall_4_2_0());
                    	      					
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_28);
                    	    lv_values_6_0=ruleSlotInteger();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getIntegerReplacementSlotRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"values",
                    	      							lv_values_6_0,
                    	      							"com.b2international.snowowl.snomed.etl.Etl.SlotInteger");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop29;
                        }
                    } while (true);

                    this_ROUND_CLOSE_7=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getIntegerReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:1320:3: ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_SLOTNAME_STRING) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // InternalEtlParser.g:1321:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:1321:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:1322:5: lv_name_8_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_8_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_8_0, grammarAccess.getIntegerReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getIntegerReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_8_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_9=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getIntegerReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleIntegerReplacementSlot"


    // $ANTLR start "entryRuleDecimalReplacementSlot"
    // InternalEtlParser.g:1346:1: entryRuleDecimalReplacementSlot returns [EObject current=null] : iv_ruleDecimalReplacementSlot= ruleDecimalReplacementSlot EOF ;
    public final EObject entryRuleDecimalReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalReplacementSlot = null;


        try {
            // InternalEtlParser.g:1346:63: (iv_ruleDecimalReplacementSlot= ruleDecimalReplacementSlot EOF )
            // InternalEtlParser.g:1347:2: iv_ruleDecimalReplacementSlot= ruleDecimalReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalReplacementSlot=ruleDecimalReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalReplacementSlot; 
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
    // $ANTLR end "entryRuleDecimalReplacementSlot"


    // $ANTLR start "ruleDecimalReplacementSlot"
    // InternalEtlParser.g:1353:1: ruleDecimalReplacementSlot returns [EObject current=null] : ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_DEC_3= RULE_DEC (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) ;
    public final EObject ruleDecimalReplacementSlot() throws RecognitionException {
        EObject current = null;

        Token this_DOUBLE_SQUARE_OPEN_1=null;
        Token this_PLUS_2=null;
        Token this_DEC_3=null;
        Token this_ROUND_OPEN_4=null;
        Token this_ROUND_CLOSE_7=null;
        Token lv_name_8_0=null;
        Token this_DOUBLE_SQUARE_CLOSE_9=null;
        EObject lv_values_5_0 = null;

        EObject lv_values_6_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1359:2: ( ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_DEC_3= RULE_DEC (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:1360:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_DEC_3= RULE_DEC (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:1360:2: ( () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_DEC_3= RULE_DEC (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE )
            // InternalEtlParser.g:1361:3: () this_DOUBLE_SQUARE_OPEN_1= RULE_DOUBLE_SQUARE_OPEN this_PLUS_2= RULE_PLUS this_DEC_3= RULE_DEC (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )? ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )? this_DOUBLE_SQUARE_CLOSE_9= RULE_DOUBLE_SQUARE_CLOSE
            {
            // InternalEtlParser.g:1361:3: ()
            // InternalEtlParser.g:1362:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getDecimalReplacementSlotAccess().getDecimalReplacementSlotAction_0(),
              					current);
              			
            }

            }

            this_DOUBLE_SQUARE_OPEN_1=(Token)match(input,RULE_DOUBLE_SQUARE_OPEN,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getDecimalReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
              		
            }
            this_PLUS_2=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_29); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_PLUS_2, grammarAccess.getDecimalReplacementSlotAccess().getPLUSTerminalRuleCall_2());
              		
            }
            this_DEC_3=(Token)match(input,RULE_DEC,FollowSets000.FOLLOW_14); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DEC_3, grammarAccess.getDecimalReplacementSlotAccess().getDECTerminalRuleCall_3());
              		
            }
            // InternalEtlParser.g:1383:3: (this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==RULE_ROUND_OPEN) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // InternalEtlParser.g:1384:4: this_ROUND_OPEN_4= RULE_ROUND_OPEN ( (lv_values_5_0= ruleSlotDecimal ) ) ( (lv_values_6_0= ruleSlotDecimal ) )* this_ROUND_CLOSE_7= RULE_ROUND_CLOSE
                    {
                    this_ROUND_OPEN_4=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_27); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getDecimalReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
                      			
                    }
                    // InternalEtlParser.g:1388:4: ( (lv_values_5_0= ruleSlotDecimal ) )
                    // InternalEtlParser.g:1389:5: (lv_values_5_0= ruleSlotDecimal )
                    {
                    // InternalEtlParser.g:1389:5: (lv_values_5_0= ruleSlotDecimal )
                    // InternalEtlParser.g:1390:6: lv_values_5_0= ruleSlotDecimal
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getDecimalReplacementSlotAccess().getValuesSlotDecimalParserRuleCall_4_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_28);
                    lv_values_5_0=ruleSlotDecimal();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getDecimalReplacementSlotRule());
                      						}
                      						add(
                      							current,
                      							"values",
                      							lv_values_5_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimal");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalEtlParser.g:1407:4: ( (lv_values_6_0= ruleSlotDecimal ) )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==RULE_TO||LA32_0==RULE_GT||LA32_0==RULE_HASH) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // InternalEtlParser.g:1408:5: (lv_values_6_0= ruleSlotDecimal )
                    	    {
                    	    // InternalEtlParser.g:1408:5: (lv_values_6_0= ruleSlotDecimal )
                    	    // InternalEtlParser.g:1409:6: lv_values_6_0= ruleSlotDecimal
                    	    {
                    	    if ( state.backtracking==0 ) {

                    	      						newCompositeNode(grammarAccess.getDecimalReplacementSlotAccess().getValuesSlotDecimalParserRuleCall_4_2_0());
                    	      					
                    	    }
                    	    pushFollow(FollowSets000.FOLLOW_28);
                    	    lv_values_6_0=ruleSlotDecimal();

                    	    state._fsp--;
                    	    if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      						if (current==null) {
                    	      							current = createModelElementForParent(grammarAccess.getDecimalReplacementSlotRule());
                    	      						}
                    	      						add(
                    	      							current,
                    	      							"values",
                    	      							lv_values_6_0,
                    	      							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimal");
                    	      						afterParserOrEnumRuleCall();
                    	      					
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop32;
                        }
                    } while (true);

                    this_ROUND_CLOSE_7=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_16); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getDecimalReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:1431:3: ( (lv_name_8_0= RULE_SLOTNAME_STRING ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==RULE_SLOTNAME_STRING) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // InternalEtlParser.g:1432:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    {
                    // InternalEtlParser.g:1432:4: (lv_name_8_0= RULE_SLOTNAME_STRING )
                    // InternalEtlParser.g:1433:5: lv_name_8_0= RULE_SLOTNAME_STRING
                    {
                    lv_name_8_0=(Token)match(input,RULE_SLOTNAME_STRING,FollowSets000.FOLLOW_17); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_name_8_0, grammarAccess.getDecimalReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getDecimalReplacementSlotRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"name",
                      						lv_name_8_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
                      				
                    }

                    }


                    }
                    break;

            }

            this_DOUBLE_SQUARE_CLOSE_9=(Token)match(input,RULE_DOUBLE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getDecimalReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
              		
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
    // $ANTLR end "ruleDecimalReplacementSlot"


    // $ANTLR start "entryRuleEtlCardinality"
    // InternalEtlParser.g:1457:1: entryRuleEtlCardinality returns [EObject current=null] : iv_ruleEtlCardinality= ruleEtlCardinality EOF ;
    public final EObject entryRuleEtlCardinality() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEtlCardinality = null;


        try {
            // InternalEtlParser.g:1457:55: (iv_ruleEtlCardinality= ruleEtlCardinality EOF )
            // InternalEtlParser.g:1458:2: iv_ruleEtlCardinality= ruleEtlCardinality EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEtlCardinalityRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEtlCardinality=ruleEtlCardinality();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEtlCardinality; 
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
    // $ANTLR end "entryRuleEtlCardinality"


    // $ANTLR start "ruleEtlCardinality"
    // InternalEtlParser.g:1464:1: ruleEtlCardinality returns [EObject current=null] : ( (this_TILDE_0= RULE_TILDE )? ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) ) ;
    public final EObject ruleEtlCardinality() throws RecognitionException {
        EObject current = null;

        Token this_TILDE_0=null;
        Token this_TO_2=null;
        AntlrDatatypeRuleToken lv_min_1_0 = null;

        AntlrDatatypeRuleToken lv_max_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1470:2: ( ( (this_TILDE_0= RULE_TILDE )? ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) ) )
            // InternalEtlParser.g:1471:2: ( (this_TILDE_0= RULE_TILDE )? ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) )
            {
            // InternalEtlParser.g:1471:2: ( (this_TILDE_0= RULE_TILDE )? ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) )
            // InternalEtlParser.g:1472:3: (this_TILDE_0= RULE_TILDE )? ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) )
            {
            // InternalEtlParser.g:1472:3: (this_TILDE_0= RULE_TILDE )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==RULE_TILDE) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // InternalEtlParser.g:1473:4: this_TILDE_0= RULE_TILDE
                    {
                    this_TILDE_0=(Token)match(input,RULE_TILDE,FollowSets000.FOLLOW_30); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_TILDE_0, grammarAccess.getEtlCardinalityAccess().getTILDETerminalRuleCall_0());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:1478:3: ( (lv_min_1_0= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:1479:4: (lv_min_1_0= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:1479:4: (lv_min_1_0= ruleNonNegativeInteger )
            // InternalEtlParser.g:1480:5: lv_min_1_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEtlCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_31);
            lv_min_1_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getEtlCardinalityRule());
              					}
              					set(
              						current,
              						"min",
              						lv_min_1_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_TO_2=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_TO_2, grammarAccess.getEtlCardinalityAccess().getTOTerminalRuleCall_2());
              		
            }
            // InternalEtlParser.g:1501:3: ( (lv_max_3_0= ruleMaxValue ) )
            // InternalEtlParser.g:1502:4: (lv_max_3_0= ruleMaxValue )
            {
            // InternalEtlParser.g:1502:4: (lv_max_3_0= ruleMaxValue )
            // InternalEtlParser.g:1503:5: lv_max_3_0= ruleMaxValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEtlCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_max_3_0=ruleMaxValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getEtlCardinalityRule());
              					}
              					set(
              						current,
              						"max",
              						lv_max_3_0,
              						"com.b2international.snomed.ecl.Ecl.MaxValue");
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
    // $ANTLR end "ruleEtlCardinality"


    // $ANTLR start "entryRuleConceptReplacementSlot"
    // InternalEtlParser.g:1524:1: entryRuleConceptReplacementSlot returns [EObject current=null] : iv_ruleConceptReplacementSlot= ruleConceptReplacementSlot EOF ;
    public final EObject entryRuleConceptReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReplacementSlot = null;


        try {
            // InternalEtlParser.g:1524:63: (iv_ruleConceptReplacementSlot= ruleConceptReplacementSlot EOF )
            // InternalEtlParser.g:1525:2: iv_ruleConceptReplacementSlot= ruleConceptReplacementSlot EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConceptReplacementSlotRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConceptReplacementSlot=ruleConceptReplacementSlot();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConceptReplacementSlot; 
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
    // $ANTLR end "entryRuleConceptReplacementSlot"


    // $ANTLR start "ruleConceptReplacementSlot"
    // InternalEtlParser.g:1531:1: ruleConceptReplacementSlot returns [EObject current=null] : (this_ConceptIdReplacementSlot_0= ruleConceptIdReplacementSlot | this_ExpressionReplacementSlot_1= ruleExpressionReplacementSlot ) ;
    public final EObject ruleConceptReplacementSlot() throws RecognitionException {
        EObject current = null;

        EObject this_ConceptIdReplacementSlot_0 = null;

        EObject this_ExpressionReplacementSlot_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1537:2: ( (this_ConceptIdReplacementSlot_0= ruleConceptIdReplacementSlot | this_ExpressionReplacementSlot_1= ruleExpressionReplacementSlot ) )
            // InternalEtlParser.g:1538:2: (this_ConceptIdReplacementSlot_0= ruleConceptIdReplacementSlot | this_ExpressionReplacementSlot_1= ruleExpressionReplacementSlot )
            {
            // InternalEtlParser.g:1538:2: (this_ConceptIdReplacementSlot_0= ruleConceptIdReplacementSlot | this_ExpressionReplacementSlot_1= ruleExpressionReplacementSlot )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                int LA36_1 = input.LA(2);

                if ( (LA36_1==RULE_PLUS) ) {
                    int LA36_2 = input.LA(3);

                    if ( (LA36_2==RULE_ID) ) {
                        alt36=1;
                    }
                    else if ( (LA36_2==RULE_DOUBLE_SQUARE_CLOSE||LA36_2==RULE_SCG||LA36_2==RULE_SLOTNAME_STRING||LA36_2==RULE_ROUND_OPEN) ) {
                        alt36=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 36, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 36, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // InternalEtlParser.g:1539:3: this_ConceptIdReplacementSlot_0= ruleConceptIdReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getConceptReplacementSlotAccess().getConceptIdReplacementSlotParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConceptIdReplacementSlot_0=ruleConceptIdReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ConceptIdReplacementSlot_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:1551:3: this_ExpressionReplacementSlot_1= ruleExpressionReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getConceptReplacementSlotAccess().getExpressionReplacementSlotParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ExpressionReplacementSlot_1=ruleExpressionReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ExpressionReplacementSlot_1;
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
    // $ANTLR end "ruleConceptReplacementSlot"


    // $ANTLR start "entryRuleConceptReference"
    // InternalEtlParser.g:1566:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // InternalEtlParser.g:1566:57: (iv_ruleConceptReference= ruleConceptReference EOF )
            // InternalEtlParser.g:1567:2: iv_ruleConceptReference= ruleConceptReference EOF
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
    // InternalEtlParser.g:1573:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_slot_0_0= ruleConceptReplacementSlot ) ) | ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? ) ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token lv_term_2_0=null;
        EObject lv_slot_0_0 = null;

        AntlrDatatypeRuleToken lv_id_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1579:2: ( ( ( (lv_slot_0_0= ruleConceptReplacementSlot ) ) | ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? ) ) )
            // InternalEtlParser.g:1580:2: ( ( (lv_slot_0_0= ruleConceptReplacementSlot ) ) | ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? ) )
            {
            // InternalEtlParser.g:1580:2: ( ( (lv_slot_0_0= ruleConceptReplacementSlot ) ) | ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? ) )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==RULE_DOUBLE_SQUARE_OPEN) ) {
                alt38=1;
            }
            else if ( (LA38_0==RULE_DIGIT_NONZERO) ) {
                alt38=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // InternalEtlParser.g:1581:3: ( (lv_slot_0_0= ruleConceptReplacementSlot ) )
                    {
                    // InternalEtlParser.g:1581:3: ( (lv_slot_0_0= ruleConceptReplacementSlot ) )
                    // InternalEtlParser.g:1582:4: (lv_slot_0_0= ruleConceptReplacementSlot )
                    {
                    // InternalEtlParser.g:1582:4: (lv_slot_0_0= ruleConceptReplacementSlot )
                    // InternalEtlParser.g:1583:5: lv_slot_0_0= ruleConceptReplacementSlot
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getConceptReferenceAccess().getSlotConceptReplacementSlotParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_slot_0_0=ruleConceptReplacementSlot();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
                      					}
                      					set(
                      						current,
                      						"slot",
                      						lv_slot_0_0,
                      						"com.b2international.snowowl.snomed.etl.Etl.ConceptReplacementSlot");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:1601:3: ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? )
                    {
                    // InternalEtlParser.g:1601:3: ( ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )? )
                    // InternalEtlParser.g:1602:4: ( (lv_id_1_0= ruleSnomedIdentifier ) ) ( (lv_term_2_0= RULE_TERM_STRING ) )?
                    {
                    // InternalEtlParser.g:1602:4: ( (lv_id_1_0= ruleSnomedIdentifier ) )
                    // InternalEtlParser.g:1603:5: (lv_id_1_0= ruleSnomedIdentifier )
                    {
                    // InternalEtlParser.g:1603:5: (lv_id_1_0= ruleSnomedIdentifier )
                    // InternalEtlParser.g:1604:6: lv_id_1_0= ruleSnomedIdentifier
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_33);
                    lv_id_1_0=ruleSnomedIdentifier();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
                      						}
                      						set(
                      							current,
                      							"id",
                      							lv_id_1_0,
                      							"com.b2international.snomed.ecl.Ecl.SnomedIdentifier");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    // InternalEtlParser.g:1621:4: ( (lv_term_2_0= RULE_TERM_STRING ) )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==RULE_TERM_STRING) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // InternalEtlParser.g:1622:5: (lv_term_2_0= RULE_TERM_STRING )
                            {
                            // InternalEtlParser.g:1622:5: (lv_term_2_0= RULE_TERM_STRING )
                            // InternalEtlParser.g:1623:6: lv_term_2_0= RULE_TERM_STRING
                            {
                            lv_term_2_0=(Token)match(input,RULE_TERM_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              						newLeafNode(lv_term_2_0, grammarAccess.getConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_1_0());
                              					
                            }
                            if ( state.backtracking==0 ) {

                              						if (current==null) {
                              							current = createModelElement(grammarAccess.getConceptReferenceRule());
                              						}
                              						setWithLastConsumed(
                              							current,
                              							"term",
                              							lv_term_2_0,
                              							"com.b2international.snomed.ecl.Ecl.TERM_STRING");
                              					
                            }

                            }


                            }
                            break;

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
    // $ANTLR end "ruleConceptReference"


    // $ANTLR start "entryRuleSlotToken"
    // InternalEtlParser.g:1644:1: entryRuleSlotToken returns [String current=null] : iv_ruleSlotToken= ruleSlotToken EOF ;
    public final String entryRuleSlotToken() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSlotToken = null;


        try {
            // InternalEtlParser.g:1644:49: (iv_ruleSlotToken= ruleSlotToken EOF )
            // InternalEtlParser.g:1645:2: iv_ruleSlotToken= ruleSlotToken EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotTokenRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotToken=ruleSlotToken();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotToken.getText(); 
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
    // $ANTLR end "entryRuleSlotToken"


    // $ANTLR start "ruleSlotToken"
    // InternalEtlParser.g:1651:1: ruleSlotToken returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_EQUIVALENT_TO_0= RULE_EQUIVALENT_TO | this_SUBTYPE_OF_1= RULE_SUBTYPE_OF | this_COMMA_2= RULE_COMMA | this_CONJUNCTION_3= RULE_CONJUNCTION | this_DISJUNCTION_4= RULE_DISJUNCTION | this_EXCLUSION_5= RULE_EXCLUSION | this_REVERSED_6= RULE_REVERSED | this_CARET_7= RULE_CARET | this_LT_8= RULE_LT | this_LTE_9= RULE_LTE | this_DBL_LT_10= RULE_DBL_LT | this_LT_EM_11= RULE_LT_EM | this_GT_12= RULE_GT | this_GTE_13= RULE_GTE | this_DBL_GT_14= RULE_DBL_GT | this_GT_EM_15= RULE_GT_EM | this_EQUAL_16= RULE_EQUAL | this_NOT_EQUAL_17= RULE_NOT_EQUAL ) ;
    public final AntlrDatatypeRuleToken ruleSlotToken() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_EQUIVALENT_TO_0=null;
        Token this_SUBTYPE_OF_1=null;
        Token this_COMMA_2=null;
        Token this_CONJUNCTION_3=null;
        Token this_DISJUNCTION_4=null;
        Token this_EXCLUSION_5=null;
        Token this_REVERSED_6=null;
        Token this_CARET_7=null;
        Token this_LT_8=null;
        Token this_LTE_9=null;
        Token this_DBL_LT_10=null;
        Token this_LT_EM_11=null;
        Token this_GT_12=null;
        Token this_GTE_13=null;
        Token this_DBL_GT_14=null;
        Token this_GT_EM_15=null;
        Token this_EQUAL_16=null;
        Token this_NOT_EQUAL_17=null;


        	enterRule();

        try {
            // InternalEtlParser.g:1657:2: ( (this_EQUIVALENT_TO_0= RULE_EQUIVALENT_TO | this_SUBTYPE_OF_1= RULE_SUBTYPE_OF | this_COMMA_2= RULE_COMMA | this_CONJUNCTION_3= RULE_CONJUNCTION | this_DISJUNCTION_4= RULE_DISJUNCTION | this_EXCLUSION_5= RULE_EXCLUSION | this_REVERSED_6= RULE_REVERSED | this_CARET_7= RULE_CARET | this_LT_8= RULE_LT | this_LTE_9= RULE_LTE | this_DBL_LT_10= RULE_DBL_LT | this_LT_EM_11= RULE_LT_EM | this_GT_12= RULE_GT | this_GTE_13= RULE_GTE | this_DBL_GT_14= RULE_DBL_GT | this_GT_EM_15= RULE_GT_EM | this_EQUAL_16= RULE_EQUAL | this_NOT_EQUAL_17= RULE_NOT_EQUAL ) )
            // InternalEtlParser.g:1658:2: (this_EQUIVALENT_TO_0= RULE_EQUIVALENT_TO | this_SUBTYPE_OF_1= RULE_SUBTYPE_OF | this_COMMA_2= RULE_COMMA | this_CONJUNCTION_3= RULE_CONJUNCTION | this_DISJUNCTION_4= RULE_DISJUNCTION | this_EXCLUSION_5= RULE_EXCLUSION | this_REVERSED_6= RULE_REVERSED | this_CARET_7= RULE_CARET | this_LT_8= RULE_LT | this_LTE_9= RULE_LTE | this_DBL_LT_10= RULE_DBL_LT | this_LT_EM_11= RULE_LT_EM | this_GT_12= RULE_GT | this_GTE_13= RULE_GTE | this_DBL_GT_14= RULE_DBL_GT | this_GT_EM_15= RULE_GT_EM | this_EQUAL_16= RULE_EQUAL | this_NOT_EQUAL_17= RULE_NOT_EQUAL )
            {
            // InternalEtlParser.g:1658:2: (this_EQUIVALENT_TO_0= RULE_EQUIVALENT_TO | this_SUBTYPE_OF_1= RULE_SUBTYPE_OF | this_COMMA_2= RULE_COMMA | this_CONJUNCTION_3= RULE_CONJUNCTION | this_DISJUNCTION_4= RULE_DISJUNCTION | this_EXCLUSION_5= RULE_EXCLUSION | this_REVERSED_6= RULE_REVERSED | this_CARET_7= RULE_CARET | this_LT_8= RULE_LT | this_LTE_9= RULE_LTE | this_DBL_LT_10= RULE_DBL_LT | this_LT_EM_11= RULE_LT_EM | this_GT_12= RULE_GT | this_GTE_13= RULE_GTE | this_DBL_GT_14= RULE_DBL_GT | this_GT_EM_15= RULE_GT_EM | this_EQUAL_16= RULE_EQUAL | this_NOT_EQUAL_17= RULE_NOT_EQUAL )
            int alt39=18;
            switch ( input.LA(1) ) {
            case RULE_EQUIVALENT_TO:
                {
                alt39=1;
                }
                break;
            case RULE_SUBTYPE_OF:
                {
                alt39=2;
                }
                break;
            case RULE_COMMA:
                {
                alt39=3;
                }
                break;
            case RULE_CONJUNCTION:
                {
                alt39=4;
                }
                break;
            case RULE_DISJUNCTION:
                {
                alt39=5;
                }
                break;
            case RULE_EXCLUSION:
                {
                alt39=6;
                }
                break;
            case RULE_REVERSED:
                {
                alt39=7;
                }
                break;
            case RULE_CARET:
                {
                alt39=8;
                }
                break;
            case RULE_LT:
                {
                alt39=9;
                }
                break;
            case RULE_LTE:
                {
                alt39=10;
                }
                break;
            case RULE_DBL_LT:
                {
                alt39=11;
                }
                break;
            case RULE_LT_EM:
                {
                alt39=12;
                }
                break;
            case RULE_GT:
                {
                alt39=13;
                }
                break;
            case RULE_GTE:
                {
                alt39=14;
                }
                break;
            case RULE_DBL_GT:
                {
                alt39=15;
                }
                break;
            case RULE_GT_EM:
                {
                alt39=16;
                }
                break;
            case RULE_EQUAL:
                {
                alt39=17;
                }
                break;
            case RULE_NOT_EQUAL:
                {
                alt39=18;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // InternalEtlParser.g:1659:3: this_EQUIVALENT_TO_0= RULE_EQUIVALENT_TO
                    {
                    this_EQUIVALENT_TO_0=(Token)match(input,RULE_EQUIVALENT_TO,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_EQUIVALENT_TO_0);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_EQUIVALENT_TO_0, grammarAccess.getSlotTokenAccess().getEQUIVALENT_TOTerminalRuleCall_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:1667:3: this_SUBTYPE_OF_1= RULE_SUBTYPE_OF
                    {
                    this_SUBTYPE_OF_1=(Token)match(input,RULE_SUBTYPE_OF,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_SUBTYPE_OF_1);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_SUBTYPE_OF_1, grammarAccess.getSlotTokenAccess().getSUBTYPE_OFTerminalRuleCall_1());
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:1675:3: this_COMMA_2= RULE_COMMA
                    {
                    this_COMMA_2=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_COMMA_2);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_COMMA_2, grammarAccess.getSlotTokenAccess().getCOMMATerminalRuleCall_2());
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalEtlParser.g:1683:3: this_CONJUNCTION_3= RULE_CONJUNCTION
                    {
                    this_CONJUNCTION_3=(Token)match(input,RULE_CONJUNCTION,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_CONJUNCTION_3);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_CONJUNCTION_3, grammarAccess.getSlotTokenAccess().getCONJUNCTIONTerminalRuleCall_3());
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalEtlParser.g:1691:3: this_DISJUNCTION_4= RULE_DISJUNCTION
                    {
                    this_DISJUNCTION_4=(Token)match(input,RULE_DISJUNCTION,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_DISJUNCTION_4);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_DISJUNCTION_4, grammarAccess.getSlotTokenAccess().getDISJUNCTIONTerminalRuleCall_4());
                      		
                    }

                    }
                    break;
                case 6 :
                    // InternalEtlParser.g:1699:3: this_EXCLUSION_5= RULE_EXCLUSION
                    {
                    this_EXCLUSION_5=(Token)match(input,RULE_EXCLUSION,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_EXCLUSION_5);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_EXCLUSION_5, grammarAccess.getSlotTokenAccess().getEXCLUSIONTerminalRuleCall_5());
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalEtlParser.g:1707:3: this_REVERSED_6= RULE_REVERSED
                    {
                    this_REVERSED_6=(Token)match(input,RULE_REVERSED,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_REVERSED_6);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_REVERSED_6, grammarAccess.getSlotTokenAccess().getREVERSEDTerminalRuleCall_6());
                      		
                    }

                    }
                    break;
                case 8 :
                    // InternalEtlParser.g:1715:3: this_CARET_7= RULE_CARET
                    {
                    this_CARET_7=(Token)match(input,RULE_CARET,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_CARET_7);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_CARET_7, grammarAccess.getSlotTokenAccess().getCARETTerminalRuleCall_7());
                      		
                    }

                    }
                    break;
                case 9 :
                    // InternalEtlParser.g:1723:3: this_LT_8= RULE_LT
                    {
                    this_LT_8=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_LT_8);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_LT_8, grammarAccess.getSlotTokenAccess().getLTTerminalRuleCall_8());
                      		
                    }

                    }
                    break;
                case 10 :
                    // InternalEtlParser.g:1731:3: this_LTE_9= RULE_LTE
                    {
                    this_LTE_9=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_LTE_9);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_LTE_9, grammarAccess.getSlotTokenAccess().getLTETerminalRuleCall_9());
                      		
                    }

                    }
                    break;
                case 11 :
                    // InternalEtlParser.g:1739:3: this_DBL_LT_10= RULE_DBL_LT
                    {
                    this_DBL_LT_10=(Token)match(input,RULE_DBL_LT,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_DBL_LT_10);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_DBL_LT_10, grammarAccess.getSlotTokenAccess().getDBL_LTTerminalRuleCall_10());
                      		
                    }

                    }
                    break;
                case 12 :
                    // InternalEtlParser.g:1747:3: this_LT_EM_11= RULE_LT_EM
                    {
                    this_LT_EM_11=(Token)match(input,RULE_LT_EM,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_LT_EM_11);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_LT_EM_11, grammarAccess.getSlotTokenAccess().getLT_EMTerminalRuleCall_11());
                      		
                    }

                    }
                    break;
                case 13 :
                    // InternalEtlParser.g:1755:3: this_GT_12= RULE_GT
                    {
                    this_GT_12=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_GT_12);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_GT_12, grammarAccess.getSlotTokenAccess().getGTTerminalRuleCall_12());
                      		
                    }

                    }
                    break;
                case 14 :
                    // InternalEtlParser.g:1763:3: this_GTE_13= RULE_GTE
                    {
                    this_GTE_13=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_GTE_13);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_GTE_13, grammarAccess.getSlotTokenAccess().getGTETerminalRuleCall_13());
                      		
                    }

                    }
                    break;
                case 15 :
                    // InternalEtlParser.g:1771:3: this_DBL_GT_14= RULE_DBL_GT
                    {
                    this_DBL_GT_14=(Token)match(input,RULE_DBL_GT,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_DBL_GT_14);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_DBL_GT_14, grammarAccess.getSlotTokenAccess().getDBL_GTTerminalRuleCall_14());
                      		
                    }

                    }
                    break;
                case 16 :
                    // InternalEtlParser.g:1779:3: this_GT_EM_15= RULE_GT_EM
                    {
                    this_GT_EM_15=(Token)match(input,RULE_GT_EM,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_GT_EM_15);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_GT_EM_15, grammarAccess.getSlotTokenAccess().getGT_EMTerminalRuleCall_15());
                      		
                    }

                    }
                    break;
                case 17 :
                    // InternalEtlParser.g:1787:3: this_EQUAL_16= RULE_EQUAL
                    {
                    this_EQUAL_16=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_EQUAL_16);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_EQUAL_16, grammarAccess.getSlotTokenAccess().getEQUALTerminalRuleCall_16());
                      		
                    }

                    }
                    break;
                case 18 :
                    // InternalEtlParser.g:1795:3: this_NOT_EQUAL_17= RULE_NOT_EQUAL
                    {
                    this_NOT_EQUAL_17=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_NOT_EQUAL_17);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_NOT_EQUAL_17, grammarAccess.getSlotTokenAccess().getNOT_EQUALTerminalRuleCall_17());
                      		
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
    // $ANTLR end "ruleSlotToken"


    // $ANTLR start "entryRuleStringValue"
    // InternalEtlParser.g:1806:1: entryRuleStringValue returns [EObject current=null] : iv_ruleStringValue= ruleStringValue EOF ;
    public final EObject entryRuleStringValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValue = null;


        try {
            // InternalEtlParser.g:1806:52: (iv_ruleStringValue= ruleStringValue EOF )
            // InternalEtlParser.g:1807:2: iv_ruleStringValue= ruleStringValue EOF
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
    // InternalEtlParser.g:1813:1: ruleStringValue returns [EObject current=null] : ( (lv_value_0_0= RULE_STRING ) ) ;
    public final EObject ruleStringValue() throws RecognitionException {
        EObject current = null;

        Token lv_value_0_0=null;


        	enterRule();

        try {
            // InternalEtlParser.g:1819:2: ( ( (lv_value_0_0= RULE_STRING ) ) )
            // InternalEtlParser.g:1820:2: ( (lv_value_0_0= RULE_STRING ) )
            {
            // InternalEtlParser.g:1820:2: ( (lv_value_0_0= RULE_STRING ) )
            // InternalEtlParser.g:1821:3: (lv_value_0_0= RULE_STRING )
            {
            // InternalEtlParser.g:1821:3: (lv_value_0_0= RULE_STRING )
            // InternalEtlParser.g:1822:4: lv_value_0_0= RULE_STRING
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
              					"com.b2international.snomed.ecl.Ecl.STRING");
              			
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
    // InternalEtlParser.g:1841:1: entryRuleIntegerValue returns [EObject current=null] : iv_ruleIntegerValue= ruleIntegerValue EOF ;
    public final EObject entryRuleIntegerValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValue = null;


        try {
            // InternalEtlParser.g:1841:53: (iv_ruleIntegerValue= ruleIntegerValue EOF )
            // InternalEtlParser.g:1842:2: iv_ruleIntegerValue= ruleIntegerValue EOF
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
    // InternalEtlParser.g:1848:1: ruleIntegerValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1854:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:1855:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:1855:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) ) )
            // InternalEtlParser.g:1856:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleInteger ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getIntegerValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:1860:3: ( (lv_value_1_0= ruleInteger ) )
            // InternalEtlParser.g:1861:4: (lv_value_1_0= ruleInteger )
            {
            // InternalEtlParser.g:1861:4: (lv_value_1_0= ruleInteger )
            // InternalEtlParser.g:1862:5: lv_value_1_0= ruleInteger
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
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // InternalEtlParser.g:1883:1: entryRuleDecimalValue returns [EObject current=null] : iv_ruleDecimalValue= ruleDecimalValue EOF ;
    public final EObject entryRuleDecimalValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValue = null;


        try {
            // InternalEtlParser.g:1883:53: (iv_ruleDecimalValue= ruleDecimalValue EOF )
            // InternalEtlParser.g:1884:2: iv_ruleDecimalValue= ruleDecimalValue EOF
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
    // InternalEtlParser.g:1890:1: ruleDecimalValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1896:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:1897:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:1897:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) ) )
            // InternalEtlParser.g:1898:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleDecimal ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getDecimalValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:1902:3: ( (lv_value_1_0= ruleDecimal ) )
            // InternalEtlParser.g:1903:4: (lv_value_1_0= ruleDecimal )
            {
            // InternalEtlParser.g:1903:4: (lv_value_1_0= ruleDecimal )
            // InternalEtlParser.g:1904:5: lv_value_1_0= ruleDecimal
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
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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


    // $ANTLR start "entryRuleSlotInteger"
    // InternalEtlParser.g:1925:1: entryRuleSlotInteger returns [EObject current=null] : iv_ruleSlotInteger= ruleSlotInteger EOF ;
    public final EObject entryRuleSlotInteger() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotInteger = null;


        try {
            // InternalEtlParser.g:1925:52: (iv_ruleSlotInteger= ruleSlotInteger EOF )
            // InternalEtlParser.g:1926:2: iv_ruleSlotInteger= ruleSlotInteger EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotIntegerRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotInteger=ruleSlotInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotInteger; 
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
    // $ANTLR end "entryRuleSlotInteger"


    // $ANTLR start "ruleSlotInteger"
    // InternalEtlParser.g:1932:1: ruleSlotInteger returns [EObject current=null] : (this_SlotIntegerRange_0= ruleSlotIntegerRange | this_SlotIntegerValue_1= ruleSlotIntegerValue ) ;
    public final EObject ruleSlotInteger() throws RecognitionException {
        EObject current = null;

        EObject this_SlotIntegerRange_0 = null;

        EObject this_SlotIntegerValue_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1938:2: ( (this_SlotIntegerRange_0= ruleSlotIntegerRange | this_SlotIntegerValue_1= ruleSlotIntegerValue ) )
            // InternalEtlParser.g:1939:2: (this_SlotIntegerRange_0= ruleSlotIntegerRange | this_SlotIntegerValue_1= ruleSlotIntegerValue )
            {
            // InternalEtlParser.g:1939:2: (this_SlotIntegerRange_0= ruleSlotIntegerRange | this_SlotIntegerValue_1= ruleSlotIntegerValue )
            int alt40=2;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // InternalEtlParser.g:1940:3: this_SlotIntegerRange_0= ruleSlotIntegerRange
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSlotIntegerAccess().getSlotIntegerRangeParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SlotIntegerRange_0=ruleSlotIntegerRange();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_SlotIntegerRange_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:1952:3: this_SlotIntegerValue_1= ruleSlotIntegerValue
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSlotIntegerAccess().getSlotIntegerValueParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SlotIntegerValue_1=ruleSlotIntegerValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_SlotIntegerValue_1;
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
    // $ANTLR end "ruleSlotInteger"


    // $ANTLR start "entryRuleSlotIntegerValue"
    // InternalEtlParser.g:1967:1: entryRuleSlotIntegerValue returns [EObject current=null] : iv_ruleSlotIntegerValue= ruleSlotIntegerValue EOF ;
    public final EObject entryRuleSlotIntegerValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotIntegerValue = null;


        try {
            // InternalEtlParser.g:1967:57: (iv_ruleSlotIntegerValue= ruleSlotIntegerValue EOF )
            // InternalEtlParser.g:1968:2: iv_ruleSlotIntegerValue= ruleSlotIntegerValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotIntegerValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotIntegerValue=ruleSlotIntegerValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotIntegerValue; 
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
    // $ANTLR end "entryRuleSlotIntegerValue"


    // $ANTLR start "ruleSlotIntegerValue"
    // InternalEtlParser.g:1974:1: ruleSlotIntegerValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeInteger ) ) ) ;
    public final EObject ruleSlotIntegerValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:1980:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeInteger ) ) ) )
            // InternalEtlParser.g:1981:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeInteger ) ) )
            {
            // InternalEtlParser.g:1981:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeInteger ) ) )
            // InternalEtlParser.g:1982:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeInteger ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_30); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getSlotIntegerValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:1986:3: ( (lv_value_1_0= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:1987:4: (lv_value_1_0= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:1987:4: (lv_value_1_0= ruleNonNegativeInteger )
            // InternalEtlParser.g:1988:5: lv_value_1_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotIntegerValueAccess().getValueNonNegativeIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotIntegerValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
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
    // $ANTLR end "ruleSlotIntegerValue"


    // $ANTLR start "entryRuleSlotIntegerRange"
    // InternalEtlParser.g:2009:1: entryRuleSlotIntegerRange returns [EObject current=null] : iv_ruleSlotIntegerRange= ruleSlotIntegerRange EOF ;
    public final EObject entryRuleSlotIntegerRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotIntegerRange = null;


        try {
            // InternalEtlParser.g:2009:57: (iv_ruleSlotIntegerRange= ruleSlotIntegerRange EOF )
            // InternalEtlParser.g:2010:2: iv_ruleSlotIntegerRange= ruleSlotIntegerRange EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotIntegerRangeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotIntegerRange=ruleSlotIntegerRange();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotIntegerRange; 
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
    // $ANTLR end "entryRuleSlotIntegerRange"


    // $ANTLR start "ruleSlotIntegerRange"
    // InternalEtlParser.g:2016:1: ruleSlotIntegerRange returns [EObject current=null] : ( ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) ) ) ;
    public final EObject ruleSlotIntegerRange() throws RecognitionException {
        EObject current = null;

        Token this_TO_1=null;
        Token this_TO_3=null;
        EObject lv_minimum_0_0 = null;

        EObject lv_maximum_2_0 = null;

        EObject lv_maximum_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2022:2: ( ( ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) ) ) )
            // InternalEtlParser.g:2023:2: ( ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) ) )
            {
            // InternalEtlParser.g:2023:2: ( ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) ) )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==RULE_GT||LA42_0==RULE_HASH) ) {
                alt42=1;
            }
            else if ( (LA42_0==RULE_TO) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // InternalEtlParser.g:2024:3: ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? )
                    {
                    // InternalEtlParser.g:2024:3: ( ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )? )
                    // InternalEtlParser.g:2025:4: ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )?
                    {
                    // InternalEtlParser.g:2025:4: ( (lv_minimum_0_0= ruleSlotIntegerMinimumValue ) )
                    // InternalEtlParser.g:2026:5: (lv_minimum_0_0= ruleSlotIntegerMinimumValue )
                    {
                    // InternalEtlParser.g:2026:5: (lv_minimum_0_0= ruleSlotIntegerMinimumValue )
                    // InternalEtlParser.g:2027:6: lv_minimum_0_0= ruleSlotIntegerMinimumValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMinimumSlotIntegerMinimumValueParserRuleCall_0_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_31);
                    lv_minimum_0_0=ruleSlotIntegerMinimumValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
                      						}
                      						set(
                      							current,
                      							"minimum",
                      							lv_minimum_0_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMinimumValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_TO_1=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_35); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_TO_1, grammarAccess.getSlotIntegerRangeAccess().getTOTerminalRuleCall_0_1());
                      			
                    }
                    // InternalEtlParser.g:2048:4: ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==RULE_LT) ) {
                        alt41=1;
                    }
                    else if ( (LA41_0==RULE_HASH) ) {
                        int LA41_2 = input.LA(2);

                        if ( (LA41_2==RULE_ZERO) ) {
                            int LA41_4 = input.LA(3);

                            if ( (synpred63_InternalEtlParser()) ) {
                                alt41=1;
                            }
                        }
                        else if ( (LA41_2==RULE_DIGIT_NONZERO) ) {
                            int LA41_5 = input.LA(3);

                            if ( (synpred63_InternalEtlParser()) ) {
                                alt41=1;
                            }
                        }
                    }
                    switch (alt41) {
                        case 1 :
                            // InternalEtlParser.g:2049:5: (lv_maximum_2_0= ruleSlotIntegerMaximumValue )
                            {
                            // InternalEtlParser.g:2049:5: (lv_maximum_2_0= ruleSlotIntegerMaximumValue )
                            // InternalEtlParser.g:2050:6: lv_maximum_2_0= ruleSlotIntegerMaximumValue
                            {
                            if ( state.backtracking==0 ) {

                              						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0());
                              					
                            }
                            pushFollow(FollowSets000.FOLLOW_2);
                            lv_maximum_2_0=ruleSlotIntegerMaximumValue();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              						if (current==null) {
                              							current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
                              						}
                              						set(
                              							current,
                              							"maximum",
                              							lv_maximum_2_0,
                              							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMaximumValue");
                              						afterParserOrEnumRuleCall();
                              					
                            }

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:2069:3: (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) )
                    {
                    // InternalEtlParser.g:2069:3: (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) ) )
                    // InternalEtlParser.g:2070:4: this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) )
                    {
                    this_TO_3=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_36); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_TO_3, grammarAccess.getSlotIntegerRangeAccess().getTOTerminalRuleCall_1_0());
                      			
                    }
                    // InternalEtlParser.g:2074:4: ( (lv_maximum_4_0= ruleSlotIntegerMaximumValue ) )
                    // InternalEtlParser.g:2075:5: (lv_maximum_4_0= ruleSlotIntegerMaximumValue )
                    {
                    // InternalEtlParser.g:2075:5: (lv_maximum_4_0= ruleSlotIntegerMaximumValue )
                    // InternalEtlParser.g:2076:6: lv_maximum_4_0= ruleSlotIntegerMaximumValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMaximumSlotIntegerMaximumValueParserRuleCall_1_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_maximum_4_0=ruleSlotIntegerMaximumValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
                      						}
                      						set(
                      							current,
                      							"maximum",
                      							lv_maximum_4_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMaximumValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


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
    // $ANTLR end "ruleSlotIntegerRange"


    // $ANTLR start "entryRuleSlotIntegerMinimumValue"
    // InternalEtlParser.g:2098:1: entryRuleSlotIntegerMinimumValue returns [EObject current=null] : iv_ruleSlotIntegerMinimumValue= ruleSlotIntegerMinimumValue EOF ;
    public final EObject entryRuleSlotIntegerMinimumValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotIntegerMinimumValue = null;


        try {
            // InternalEtlParser.g:2098:64: (iv_ruleSlotIntegerMinimumValue= ruleSlotIntegerMinimumValue EOF )
            // InternalEtlParser.g:2099:2: iv_ruleSlotIntegerMinimumValue= ruleSlotIntegerMinimumValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotIntegerMinimumValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotIntegerMinimumValue=ruleSlotIntegerMinimumValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotIntegerMinimumValue; 
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
    // $ANTLR end "entryRuleSlotIntegerMinimumValue"


    // $ANTLR start "ruleSlotIntegerMinimumValue"
    // InternalEtlParser.g:2105:1: ruleSlotIntegerMinimumValue returns [EObject current=null] : ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) ) ;
    public final EObject ruleSlotIntegerMinimumValue() throws RecognitionException {
        EObject current = null;

        Token lv_exclusive_0_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2111:2: ( ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) ) )
            // InternalEtlParser.g:2112:2: ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) )
            {
            // InternalEtlParser.g:2112:2: ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) )
            // InternalEtlParser.g:2113:3: ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) )
            {
            // InternalEtlParser.g:2113:3: ( (lv_exclusive_0_0= RULE_GT ) )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==RULE_GT) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // InternalEtlParser.g:2114:4: (lv_exclusive_0_0= RULE_GT )
                    {
                    // InternalEtlParser.g:2114:4: (lv_exclusive_0_0= RULE_GT )
                    // InternalEtlParser.g:2115:5: lv_exclusive_0_0= RULE_GT
                    {
                    lv_exclusive_0_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_37); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotIntegerMinimumValueAccess().getExclusiveGTTerminalRuleCall_0_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getSlotIntegerMinimumValueRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"exclusive",
                      						true,
                      						"com.b2international.snomed.ecl.Ecl.GT");
                      				
                    }

                    }


                    }
                    break;

            }

            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_30); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getSlotIntegerMinimumValueAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:2135:3: ( (lv_value_2_0= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:2136:4: (lv_value_2_0= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:2136:4: (lv_value_2_0= ruleNonNegativeInteger )
            // InternalEtlParser.g:2137:5: lv_value_2_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotIntegerMinimumValueAccess().getValueNonNegativeIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotIntegerMinimumValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
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
    // $ANTLR end "ruleSlotIntegerMinimumValue"


    // $ANTLR start "entryRuleSlotIntegerMaximumValue"
    // InternalEtlParser.g:2158:1: entryRuleSlotIntegerMaximumValue returns [EObject current=null] : iv_ruleSlotIntegerMaximumValue= ruleSlotIntegerMaximumValue EOF ;
    public final EObject entryRuleSlotIntegerMaximumValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotIntegerMaximumValue = null;


        try {
            // InternalEtlParser.g:2158:64: (iv_ruleSlotIntegerMaximumValue= ruleSlotIntegerMaximumValue EOF )
            // InternalEtlParser.g:2159:2: iv_ruleSlotIntegerMaximumValue= ruleSlotIntegerMaximumValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotIntegerMaximumValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotIntegerMaximumValue=ruleSlotIntegerMaximumValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotIntegerMaximumValue; 
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
    // $ANTLR end "entryRuleSlotIntegerMaximumValue"


    // $ANTLR start "ruleSlotIntegerMaximumValue"
    // InternalEtlParser.g:2165:1: ruleSlotIntegerMaximumValue returns [EObject current=null] : ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) ) ;
    public final EObject ruleSlotIntegerMaximumValue() throws RecognitionException {
        EObject current = null;

        Token lv_exclusive_0_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2171:2: ( ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) ) )
            // InternalEtlParser.g:2172:2: ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) )
            {
            // InternalEtlParser.g:2172:2: ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) ) )
            // InternalEtlParser.g:2173:3: ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeInteger ) )
            {
            // InternalEtlParser.g:2173:3: ( (lv_exclusive_0_0= RULE_LT ) )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==RULE_LT) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // InternalEtlParser.g:2174:4: (lv_exclusive_0_0= RULE_LT )
                    {
                    // InternalEtlParser.g:2174:4: (lv_exclusive_0_0= RULE_LT )
                    // InternalEtlParser.g:2175:5: lv_exclusive_0_0= RULE_LT
                    {
                    lv_exclusive_0_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_37); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotIntegerMaximumValueAccess().getExclusiveLTTerminalRuleCall_0_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getSlotIntegerMaximumValueRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"exclusive",
                      						true,
                      						"com.b2international.snomed.ecl.Ecl.LT");
                      				
                    }

                    }


                    }
                    break;

            }

            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_30); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getSlotIntegerMaximumValueAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:2195:3: ( (lv_value_2_0= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:2196:4: (lv_value_2_0= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:2196:4: (lv_value_2_0= ruleNonNegativeInteger )
            // InternalEtlParser.g:2197:5: lv_value_2_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotIntegerMaximumValueAccess().getValueNonNegativeIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotIntegerMaximumValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
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
    // $ANTLR end "ruleSlotIntegerMaximumValue"


    // $ANTLR start "entryRuleSlotDecimal"
    // InternalEtlParser.g:2218:1: entryRuleSlotDecimal returns [EObject current=null] : iv_ruleSlotDecimal= ruleSlotDecimal EOF ;
    public final EObject entryRuleSlotDecimal() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotDecimal = null;


        try {
            // InternalEtlParser.g:2218:52: (iv_ruleSlotDecimal= ruleSlotDecimal EOF )
            // InternalEtlParser.g:2219:2: iv_ruleSlotDecimal= ruleSlotDecimal EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotDecimalRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotDecimal=ruleSlotDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotDecimal; 
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
    // $ANTLR end "entryRuleSlotDecimal"


    // $ANTLR start "ruleSlotDecimal"
    // InternalEtlParser.g:2225:1: ruleSlotDecimal returns [EObject current=null] : (this_SlotDecimalRange_0= ruleSlotDecimalRange | this_SlotDecimalValue_1= ruleSlotDecimalValue ) ;
    public final EObject ruleSlotDecimal() throws RecognitionException {
        EObject current = null;

        EObject this_SlotDecimalRange_0 = null;

        EObject this_SlotDecimalValue_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2231:2: ( (this_SlotDecimalRange_0= ruleSlotDecimalRange | this_SlotDecimalValue_1= ruleSlotDecimalValue ) )
            // InternalEtlParser.g:2232:2: (this_SlotDecimalRange_0= ruleSlotDecimalRange | this_SlotDecimalValue_1= ruleSlotDecimalValue )
            {
            // InternalEtlParser.g:2232:2: (this_SlotDecimalRange_0= ruleSlotDecimalRange | this_SlotDecimalValue_1= ruleSlotDecimalValue )
            int alt45=2;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // InternalEtlParser.g:2233:3: this_SlotDecimalRange_0= ruleSlotDecimalRange
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSlotDecimalAccess().getSlotDecimalRangeParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SlotDecimalRange_0=ruleSlotDecimalRange();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_SlotDecimalRange_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:2245:3: this_SlotDecimalValue_1= ruleSlotDecimalValue
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSlotDecimalAccess().getSlotDecimalValueParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SlotDecimalValue_1=ruleSlotDecimalValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_SlotDecimalValue_1;
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
    // $ANTLR end "ruleSlotDecimal"


    // $ANTLR start "entryRuleSlotDecimalValue"
    // InternalEtlParser.g:2260:1: entryRuleSlotDecimalValue returns [EObject current=null] : iv_ruleSlotDecimalValue= ruleSlotDecimalValue EOF ;
    public final EObject entryRuleSlotDecimalValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotDecimalValue = null;


        try {
            // InternalEtlParser.g:2260:57: (iv_ruleSlotDecimalValue= ruleSlotDecimalValue EOF )
            // InternalEtlParser.g:2261:2: iv_ruleSlotDecimalValue= ruleSlotDecimalValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotDecimalValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotDecimalValue=ruleSlotDecimalValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotDecimalValue; 
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
    // $ANTLR end "entryRuleSlotDecimalValue"


    // $ANTLR start "ruleSlotDecimalValue"
    // InternalEtlParser.g:2267:1: ruleSlotDecimalValue returns [EObject current=null] : (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeDecimal ) ) ) ;
    public final EObject ruleSlotDecimalValue() throws RecognitionException {
        EObject current = null;

        Token this_HASH_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2273:2: ( (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeDecimal ) ) ) )
            // InternalEtlParser.g:2274:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeDecimal ) ) )
            {
            // InternalEtlParser.g:2274:2: (this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeDecimal ) ) )
            // InternalEtlParser.g:2275:3: this_HASH_0= RULE_HASH ( (lv_value_1_0= ruleNonNegativeDecimal ) )
            {
            this_HASH_0=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_0, grammarAccess.getSlotDecimalValueAccess().getHASHTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:2279:3: ( (lv_value_1_0= ruleNonNegativeDecimal ) )
            // InternalEtlParser.g:2280:4: (lv_value_1_0= ruleNonNegativeDecimal )
            {
            // InternalEtlParser.g:2280:4: (lv_value_1_0= ruleNonNegativeDecimal )
            // InternalEtlParser.g:2281:5: lv_value_1_0= ruleNonNegativeDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotDecimalValueAccess().getValueNonNegativeDecimalParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleNonNegativeDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotDecimalValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
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
    // $ANTLR end "ruleSlotDecimalValue"


    // $ANTLR start "entryRuleSlotDecimalRange"
    // InternalEtlParser.g:2302:1: entryRuleSlotDecimalRange returns [EObject current=null] : iv_ruleSlotDecimalRange= ruleSlotDecimalRange EOF ;
    public final EObject entryRuleSlotDecimalRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotDecimalRange = null;


        try {
            // InternalEtlParser.g:2302:57: (iv_ruleSlotDecimalRange= ruleSlotDecimalRange EOF )
            // InternalEtlParser.g:2303:2: iv_ruleSlotDecimalRange= ruleSlotDecimalRange EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotDecimalRangeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotDecimalRange=ruleSlotDecimalRange();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotDecimalRange; 
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
    // $ANTLR end "entryRuleSlotDecimalRange"


    // $ANTLR start "ruleSlotDecimalRange"
    // InternalEtlParser.g:2309:1: ruleSlotDecimalRange returns [EObject current=null] : ( ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) ) ) ;
    public final EObject ruleSlotDecimalRange() throws RecognitionException {
        EObject current = null;

        Token this_TO_1=null;
        Token this_TO_3=null;
        EObject lv_minimum_0_0 = null;

        EObject lv_maximum_2_0 = null;

        EObject lv_maximum_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2315:2: ( ( ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) ) ) )
            // InternalEtlParser.g:2316:2: ( ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) ) )
            {
            // InternalEtlParser.g:2316:2: ( ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? ) | (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) ) )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==RULE_GT||LA47_0==RULE_HASH) ) {
                alt47=1;
            }
            else if ( (LA47_0==RULE_TO) ) {
                alt47=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // InternalEtlParser.g:2317:3: ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? )
                    {
                    // InternalEtlParser.g:2317:3: ( ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )? )
                    // InternalEtlParser.g:2318:4: ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) ) this_TO_1= RULE_TO ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )?
                    {
                    // InternalEtlParser.g:2318:4: ( (lv_minimum_0_0= ruleSlotDecimalMinimumValue ) )
                    // InternalEtlParser.g:2319:5: (lv_minimum_0_0= ruleSlotDecimalMinimumValue )
                    {
                    // InternalEtlParser.g:2319:5: (lv_minimum_0_0= ruleSlotDecimalMinimumValue )
                    // InternalEtlParser.g:2320:6: lv_minimum_0_0= ruleSlotDecimalMinimumValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMinimumSlotDecimalMinimumValueParserRuleCall_0_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_31);
                    lv_minimum_0_0=ruleSlotDecimalMinimumValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
                      						}
                      						set(
                      							current,
                      							"minimum",
                      							lv_minimum_0_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMinimumValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_TO_1=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_35); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_TO_1, grammarAccess.getSlotDecimalRangeAccess().getTOTerminalRuleCall_0_1());
                      			
                    }
                    // InternalEtlParser.g:2341:4: ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )?
                    int alt46=2;
                    alt46 = dfa46.predict(input);
                    switch (alt46) {
                        case 1 :
                            // InternalEtlParser.g:2342:5: (lv_maximum_2_0= ruleSlotDecimalMaximumValue )
                            {
                            // InternalEtlParser.g:2342:5: (lv_maximum_2_0= ruleSlotDecimalMaximumValue )
                            // InternalEtlParser.g:2343:6: lv_maximum_2_0= ruleSlotDecimalMaximumValue
                            {
                            if ( state.backtracking==0 ) {

                              						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0());
                              					
                            }
                            pushFollow(FollowSets000.FOLLOW_2);
                            lv_maximum_2_0=ruleSlotDecimalMaximumValue();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              						if (current==null) {
                              							current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
                              						}
                              						set(
                              							current,
                              							"maximum",
                              							lv_maximum_2_0,
                              							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMaximumValue");
                              						afterParserOrEnumRuleCall();
                              					
                            }

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:2362:3: (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) )
                    {
                    // InternalEtlParser.g:2362:3: (this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) ) )
                    // InternalEtlParser.g:2363:4: this_TO_3= RULE_TO ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) )
                    {
                    this_TO_3=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_36); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_TO_3, grammarAccess.getSlotDecimalRangeAccess().getTOTerminalRuleCall_1_0());
                      			
                    }
                    // InternalEtlParser.g:2367:4: ( (lv_maximum_4_0= ruleSlotDecimalMaximumValue ) )
                    // InternalEtlParser.g:2368:5: (lv_maximum_4_0= ruleSlotDecimalMaximumValue )
                    {
                    // InternalEtlParser.g:2368:5: (lv_maximum_4_0= ruleSlotDecimalMaximumValue )
                    // InternalEtlParser.g:2369:6: lv_maximum_4_0= ruleSlotDecimalMaximumValue
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMaximumSlotDecimalMaximumValueParserRuleCall_1_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_maximum_4_0=ruleSlotDecimalMaximumValue();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
                      						}
                      						set(
                      							current,
                      							"maximum",
                      							lv_maximum_4_0,
                      							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMaximumValue");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


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
    // $ANTLR end "ruleSlotDecimalRange"


    // $ANTLR start "entryRuleSlotDecimalMinimumValue"
    // InternalEtlParser.g:2391:1: entryRuleSlotDecimalMinimumValue returns [EObject current=null] : iv_ruleSlotDecimalMinimumValue= ruleSlotDecimalMinimumValue EOF ;
    public final EObject entryRuleSlotDecimalMinimumValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotDecimalMinimumValue = null;


        try {
            // InternalEtlParser.g:2391:64: (iv_ruleSlotDecimalMinimumValue= ruleSlotDecimalMinimumValue EOF )
            // InternalEtlParser.g:2392:2: iv_ruleSlotDecimalMinimumValue= ruleSlotDecimalMinimumValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotDecimalMinimumValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotDecimalMinimumValue=ruleSlotDecimalMinimumValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotDecimalMinimumValue; 
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
    // $ANTLR end "entryRuleSlotDecimalMinimumValue"


    // $ANTLR start "ruleSlotDecimalMinimumValue"
    // InternalEtlParser.g:2398:1: ruleSlotDecimalMinimumValue returns [EObject current=null] : ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) ) ;
    public final EObject ruleSlotDecimalMinimumValue() throws RecognitionException {
        EObject current = null;

        Token lv_exclusive_0_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2404:2: ( ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) ) )
            // InternalEtlParser.g:2405:2: ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) )
            {
            // InternalEtlParser.g:2405:2: ( ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) )
            // InternalEtlParser.g:2406:3: ( (lv_exclusive_0_0= RULE_GT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) )
            {
            // InternalEtlParser.g:2406:3: ( (lv_exclusive_0_0= RULE_GT ) )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==RULE_GT) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // InternalEtlParser.g:2407:4: (lv_exclusive_0_0= RULE_GT )
                    {
                    // InternalEtlParser.g:2407:4: (lv_exclusive_0_0= RULE_GT )
                    // InternalEtlParser.g:2408:5: lv_exclusive_0_0= RULE_GT
                    {
                    lv_exclusive_0_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_37); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotDecimalMinimumValueAccess().getExclusiveGTTerminalRuleCall_0_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getSlotDecimalMinimumValueRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"exclusive",
                      						true,
                      						"com.b2international.snomed.ecl.Ecl.GT");
                      				
                    }

                    }


                    }
                    break;

            }

            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getSlotDecimalMinimumValueAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:2428:3: ( (lv_value_2_0= ruleNonNegativeDecimal ) )
            // InternalEtlParser.g:2429:4: (lv_value_2_0= ruleNonNegativeDecimal )
            {
            // InternalEtlParser.g:2429:4: (lv_value_2_0= ruleNonNegativeDecimal )
            // InternalEtlParser.g:2430:5: lv_value_2_0= ruleNonNegativeDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotDecimalMinimumValueAccess().getValueNonNegativeDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleNonNegativeDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotDecimalMinimumValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
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
    // $ANTLR end "ruleSlotDecimalMinimumValue"


    // $ANTLR start "entryRuleSlotDecimalMaximumValue"
    // InternalEtlParser.g:2451:1: entryRuleSlotDecimalMaximumValue returns [EObject current=null] : iv_ruleSlotDecimalMaximumValue= ruleSlotDecimalMaximumValue EOF ;
    public final EObject entryRuleSlotDecimalMaximumValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSlotDecimalMaximumValue = null;


        try {
            // InternalEtlParser.g:2451:64: (iv_ruleSlotDecimalMaximumValue= ruleSlotDecimalMaximumValue EOF )
            // InternalEtlParser.g:2452:2: iv_ruleSlotDecimalMaximumValue= ruleSlotDecimalMaximumValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSlotDecimalMaximumValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSlotDecimalMaximumValue=ruleSlotDecimalMaximumValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSlotDecimalMaximumValue; 
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
    // $ANTLR end "entryRuleSlotDecimalMaximumValue"


    // $ANTLR start "ruleSlotDecimalMaximumValue"
    // InternalEtlParser.g:2458:1: ruleSlotDecimalMaximumValue returns [EObject current=null] : ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) ) ;
    public final EObject ruleSlotDecimalMaximumValue() throws RecognitionException {
        EObject current = null;

        Token lv_exclusive_0_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2464:2: ( ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) ) )
            // InternalEtlParser.g:2465:2: ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) )
            {
            // InternalEtlParser.g:2465:2: ( ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) ) )
            // InternalEtlParser.g:2466:3: ( (lv_exclusive_0_0= RULE_LT ) )? this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleNonNegativeDecimal ) )
            {
            // InternalEtlParser.g:2466:3: ( (lv_exclusive_0_0= RULE_LT ) )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==RULE_LT) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // InternalEtlParser.g:2467:4: (lv_exclusive_0_0= RULE_LT )
                    {
                    // InternalEtlParser.g:2467:4: (lv_exclusive_0_0= RULE_LT )
                    // InternalEtlParser.g:2468:5: lv_exclusive_0_0= RULE_LT
                    {
                    lv_exclusive_0_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_37); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotDecimalMaximumValueAccess().getExclusiveLTTerminalRuleCall_0_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getSlotDecimalMaximumValueRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"exclusive",
                      						true,
                      						"com.b2international.snomed.ecl.Ecl.LT");
                      				
                    }

                    }


                    }
                    break;

            }

            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getSlotDecimalMaximumValueAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:2488:3: ( (lv_value_2_0= ruleNonNegativeDecimal ) )
            // InternalEtlParser.g:2489:4: (lv_value_2_0= ruleNonNegativeDecimal )
            {
            // InternalEtlParser.g:2489:4: (lv_value_2_0= ruleNonNegativeDecimal )
            // InternalEtlParser.g:2490:5: lv_value_2_0= ruleNonNegativeDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getSlotDecimalMaximumValueAccess().getValueNonNegativeDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleNonNegativeDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getSlotDecimalMaximumValueRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
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
    // $ANTLR end "ruleSlotDecimalMaximumValue"


    // $ANTLR start "entryRuleExpressionConstraint"
    // InternalEtlParser.g:2511:1: entryRuleExpressionConstraint returns [EObject current=null] : iv_ruleExpressionConstraint= ruleExpressionConstraint EOF ;
    public final EObject entryRuleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2511:61: (iv_ruleExpressionConstraint= ruleExpressionConstraint EOF )
            // InternalEtlParser.g:2512:2: iv_ruleExpressionConstraint= ruleExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExpressionConstraint=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpressionConstraint; 
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
    // $ANTLR end "entryRuleExpressionConstraint"


    // $ANTLR start "ruleExpressionConstraint"
    // InternalEtlParser.g:2518:1: ruleExpressionConstraint returns [EObject current=null] : this_OrExpressionConstraint_0= ruleOrExpressionConstraint ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_OrExpressionConstraint_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2524:2: (this_OrExpressionConstraint_0= ruleOrExpressionConstraint )
            // InternalEtlParser.g:2525:2: this_OrExpressionConstraint_0= ruleOrExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getExpressionConstraintAccess().getOrExpressionConstraintParserRuleCall());
              	
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_OrExpressionConstraint_0=ruleOrExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current = this_OrExpressionConstraint_0;
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
    // $ANTLR end "ruleExpressionConstraint"


    // $ANTLR start "entryRuleOrExpressionConstraint"
    // InternalEtlParser.g:2539:1: entryRuleOrExpressionConstraint returns [EObject current=null] : iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF ;
    public final EObject entryRuleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2539:63: (iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF )
            // InternalEtlParser.g:2540:2: iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOrExpressionConstraint=ruleOrExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrExpressionConstraint; 
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
    // $ANTLR end "entryRuleOrExpressionConstraint"


    // $ANTLR start "ruleOrExpressionConstraint"
    // InternalEtlParser.g:2546:1: ruleOrExpressionConstraint returns [EObject current=null] : (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) ;
    public final EObject ruleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_DISJUNCTION_2=null;
        EObject this_AndExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2552:2: ( (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) )
            // InternalEtlParser.g:2553:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            {
            // InternalEtlParser.g:2553:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            // InternalEtlParser.g:2554:3: this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getAndExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_38);
            this_AndExpressionConstraint_0=ruleAndExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:2565:3: ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==RULE_DISJUNCTION) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // InternalEtlParser.g:2566:4: () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    {
            	    // InternalEtlParser.g:2566:4: ()
            	    // InternalEtlParser.g:2567:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    this_DISJUNCTION_2=(Token)match(input,RULE_DISJUNCTION,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrExpressionConstraintAccess().getDISJUNCTIONTerminalRuleCall_1_1());
            	      			
            	    }
            	    // InternalEtlParser.g:2580:4: ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    // InternalEtlParser.g:2581:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    {
            	    // InternalEtlParser.g:2581:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    // InternalEtlParser.g:2582:6: lv_right_3_0= ruleAndExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_38);
            	    lv_right_3_0=ruleAndExpressionConstraint();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getOrExpressionConstraintRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_3_0,
            	      							"com.b2international.snomed.ecl.Ecl.AndExpressionConstraint");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop50;
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
    // $ANTLR end "ruleOrExpressionConstraint"


    // $ANTLR start "entryRuleAndExpressionConstraint"
    // InternalEtlParser.g:2604:1: entryRuleAndExpressionConstraint returns [EObject current=null] : iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF ;
    public final EObject entryRuleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2604:64: (iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF )
            // InternalEtlParser.g:2605:2: iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAndExpressionConstraint=ruleAndExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndExpressionConstraint; 
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
    // $ANTLR end "entryRuleAndExpressionConstraint"


    // $ANTLR start "ruleAndExpressionConstraint"
    // InternalEtlParser.g:2611:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_CONJUNCTION_2=null;
        Token this_COMMA_3=null;
        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2617:2: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // InternalEtlParser.g:2618:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // InternalEtlParser.g:2618:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            // InternalEtlParser.g:2619:3: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_39);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_ExclusionExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:2630:3: ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( ((LA52_0>=RULE_COMMA && LA52_0<=RULE_CONJUNCTION)) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // InternalEtlParser.g:2631:4: () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // InternalEtlParser.g:2631:4: ()
            	    // InternalEtlParser.g:2632:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    // InternalEtlParser.g:2641:4: (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA )
            	    int alt51=2;
            	    int LA51_0 = input.LA(1);

            	    if ( (LA51_0==RULE_CONJUNCTION) ) {
            	        alt51=1;
            	    }
            	    else if ( (LA51_0==RULE_COMMA) ) {
            	        alt51=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 51, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt51) {
            	        case 1 :
            	            // InternalEtlParser.g:2642:5: this_CONJUNCTION_2= RULE_CONJUNCTION
            	            {
            	            this_CONJUNCTION_2=(Token)match(input,RULE_CONJUNCTION,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndExpressionConstraintAccess().getCONJUNCTIONTerminalRuleCall_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEtlParser.g:2647:5: this_COMMA_3= RULE_COMMA
            	            {
            	            this_COMMA_3=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_COMMA_3, grammarAccess.getAndExpressionConstraintAccess().getCOMMATerminalRuleCall_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEtlParser.g:2652:4: ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    // InternalEtlParser.g:2653:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    {
            	    // InternalEtlParser.g:2653:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    // InternalEtlParser.g:2654:6: lv_right_4_0= ruleExclusionExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_39);
            	    lv_right_4_0=ruleExclusionExpressionConstraint();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getAndExpressionConstraintRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_4_0,
            	      							"com.b2international.snomed.ecl.Ecl.ExclusionExpressionConstraint");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop52;
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
    // $ANTLR end "ruleAndExpressionConstraint"


    // $ANTLR start "entryRuleExclusionExpressionConstraint"
    // InternalEtlParser.g:2676:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2676:70: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // InternalEtlParser.g:2677:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExclusionExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExclusionExpressionConstraint=ruleExclusionExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExclusionExpressionConstraint; 
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
    // $ANTLR end "entryRuleExclusionExpressionConstraint"


    // $ANTLR start "ruleExclusionExpressionConstraint"
    // InternalEtlParser.g:2683:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_EXCLUSION_2=null;
        EObject this_RefinedExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2689:2: ( (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) )
            // InternalEtlParser.g:2690:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            {
            // InternalEtlParser.g:2690:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            // InternalEtlParser.g:2691:3: this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_40);
            this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_RefinedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:2702:3: ( () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==RULE_EXCLUSION) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // InternalEtlParser.g:2703:4: () this_EXCLUSION_2= RULE_EXCLUSION ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    {
                    // InternalEtlParser.g:2703:4: ()
                    // InternalEtlParser.g:2704:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					/* */
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElementAndSet(
                      						grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
                      						current);
                      				
                    }

                    }

                    this_EXCLUSION_2=(Token)match(input,RULE_EXCLUSION,FollowSets000.FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_EXCLUSION_2, grammarAccess.getExclusionExpressionConstraintAccess().getEXCLUSIONTerminalRuleCall_1_1());
                      			
                    }
                    // InternalEtlParser.g:2717:4: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    // InternalEtlParser.g:2718:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    {
                    // InternalEtlParser.g:2718:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    // InternalEtlParser.g:2719:6: lv_right_3_0= ruleRefinedExpressionConstraint
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_right_3_0=ruleRefinedExpressionConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getExclusionExpressionConstraintRule());
                      						}
                      						set(
                      							current,
                      							"right",
                      							lv_right_3_0,
                      							"com.b2international.snomed.ecl.Ecl.RefinedExpressionConstraint");
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
    // $ANTLR end "ruleExclusionExpressionConstraint"


    // $ANTLR start "entryRuleRefinedExpressionConstraint"
    // InternalEtlParser.g:2741:1: entryRuleRefinedExpressionConstraint returns [EObject current=null] : iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF ;
    public final EObject entryRuleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinedExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2741:68: (iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF )
            // InternalEtlParser.g:2742:2: iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRefinedExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRefinedExpressionConstraint=ruleRefinedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRefinedExpressionConstraint; 
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
    // $ANTLR end "entryRuleRefinedExpressionConstraint"


    // $ANTLR start "ruleRefinedExpressionConstraint"
    // InternalEtlParser.g:2748:1: ruleRefinedExpressionConstraint returns [EObject current=null] : (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )? ) ;
    public final EObject ruleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_COLON_2=null;
        EObject this_DottedExpressionConstraint_0 = null;

        EObject lv_refinement_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2754:2: ( (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )? ) )
            // InternalEtlParser.g:2755:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )? )
            {
            // InternalEtlParser.g:2755:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )? )
            // InternalEtlParser.g:2756:3: this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getDottedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_41);
            this_DottedExpressionConstraint_0=ruleDottedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_DottedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:2767:3: ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) ) )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==RULE_COLON) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // InternalEtlParser.g:2768:4: () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleEclRefinement ) )
                    {
                    // InternalEtlParser.g:2768:4: ()
                    // InternalEtlParser.g:2769:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					/* */
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElementAndSet(
                      						grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0(),
                      						current);
                      				
                    }

                    }

                    this_COLON_2=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_42); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_2, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_1());
                      			
                    }
                    // InternalEtlParser.g:2782:4: ( (lv_refinement_3_0= ruleEclRefinement ) )
                    // InternalEtlParser.g:2783:5: (lv_refinement_3_0= ruleEclRefinement )
                    {
                    // InternalEtlParser.g:2783:5: (lv_refinement_3_0= ruleEclRefinement )
                    // InternalEtlParser.g:2784:6: lv_refinement_3_0= ruleEclRefinement
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementEclRefinementParserRuleCall_1_2_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_refinement_3_0=ruleEclRefinement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
                      						}
                      						set(
                      							current,
                      							"refinement",
                      							lv_refinement_3_0,
                      							"com.b2international.snomed.ecl.Ecl.EclRefinement");
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
    // $ANTLR end "ruleRefinedExpressionConstraint"


    // $ANTLR start "entryRuleDottedExpressionConstraint"
    // InternalEtlParser.g:2806:1: entryRuleDottedExpressionConstraint returns [EObject current=null] : iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF ;
    public final EObject entryRuleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDottedExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2806:67: (iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF )
            // InternalEtlParser.g:2807:2: iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDottedExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDottedExpressionConstraint=ruleDottedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDottedExpressionConstraint; 
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
    // $ANTLR end "entryRuleDottedExpressionConstraint"


    // $ANTLR start "ruleDottedExpressionConstraint"
    // InternalEtlParser.g:2813:1: ruleDottedExpressionConstraint returns [EObject current=null] : (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* ) ;
    public final EObject ruleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_DOT_2=null;
        EObject this_SubExpressionConstraint_0 = null;

        EObject lv_attribute_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2819:2: ( (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* ) )
            // InternalEtlParser.g:2820:2: (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* )
            {
            // InternalEtlParser.g:2820:2: (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* )
            // InternalEtlParser.g:2821:3: this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSubExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_43);
            this_SubExpressionConstraint_0=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:2832:3: ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==RULE_DOT) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // InternalEtlParser.g:2833:4: () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) )
            	    {
            	    // InternalEtlParser.g:2833:4: ()
            	    // InternalEtlParser.g:2834:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    this_DOT_2=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DOT_2, grammarAccess.getDottedExpressionConstraintAccess().getDOTTerminalRuleCall_1_1());
            	      			
            	    }
            	    // InternalEtlParser.g:2847:4: ( (lv_attribute_3_0= ruleSubExpressionConstraint ) )
            	    // InternalEtlParser.g:2848:5: (lv_attribute_3_0= ruleSubExpressionConstraint )
            	    {
            	    // InternalEtlParser.g:2848:5: (lv_attribute_3_0= ruleSubExpressionConstraint )
            	    // InternalEtlParser.g:2849:6: lv_attribute_3_0= ruleSubExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_43);
            	    lv_attribute_3_0=ruleSubExpressionConstraint();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getDottedExpressionConstraintRule());
            	      						}
            	      						set(
            	      							current,
            	      							"attribute",
            	      							lv_attribute_3_0,
            	      							"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop55;
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
    // $ANTLR end "ruleDottedExpressionConstraint"


    // $ANTLR start "entryRuleSubExpressionConstraint"
    // InternalEtlParser.g:2871:1: entryRuleSubExpressionConstraint returns [EObject current=null] : iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF ;
    public final EObject entryRuleSubExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubExpressionConstraint = null;


        try {
            // InternalEtlParser.g:2871:64: (iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF )
            // InternalEtlParser.g:2872:2: iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSubExpressionConstraint=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubExpressionConstraint; 
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
    // $ANTLR end "entryRuleSubExpressionConstraint"


    // $ANTLR start "ruleSubExpressionConstraint"
    // InternalEtlParser.g:2878:1: ruleSubExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_EclFocusConcept_6= ruleEclFocusConcept ) ;
    public final EObject ruleSubExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_ChildOf_0 = null;

        EObject this_DescendantOf_1 = null;

        EObject this_DescendantOrSelfOf_2 = null;

        EObject this_ParentOf_3 = null;

        EObject this_AncestorOf_4 = null;

        EObject this_AncestorOrSelfOf_5 = null;

        EObject this_EclFocusConcept_6 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2884:2: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_EclFocusConcept_6= ruleEclFocusConcept ) )
            // InternalEtlParser.g:2885:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_EclFocusConcept_6= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:2885:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_EclFocusConcept_6= ruleEclFocusConcept )
            int alt56=7;
            switch ( input.LA(1) ) {
            case RULE_LT_EM:
                {
                alt56=1;
                }
                break;
            case RULE_LT:
                {
                alt56=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt56=3;
                }
                break;
            case RULE_GT_EM:
                {
                alt56=4;
                }
                break;
            case RULE_GT:
                {
                alt56=5;
                }
                break;
            case RULE_DBL_GT:
                {
                alt56=6;
                }
                break;
            case RULE_DIGIT_NONZERO:
            case RULE_ROUND_OPEN:
            case RULE_CARET:
            case RULE_WILDCARD:
                {
                alt56=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // InternalEtlParser.g:2886:3: this_ChildOf_0= ruleChildOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getChildOfParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ChildOf_0=ruleChildOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ChildOf_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:2898:3: this_DescendantOf_1= ruleDescendantOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOfParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DescendantOf_1=ruleDescendantOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DescendantOf_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:2910:3: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DescendantOrSelfOf_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalEtlParser.g:2922:3: this_ParentOf_3= ruleParentOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getParentOfParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ParentOf_3=ruleParentOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ParentOf_3;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalEtlParser.g:2934:3: this_AncestorOf_4= ruleAncestorOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOfParserRuleCall_4());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AncestorOf_4=ruleAncestorOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AncestorOf_4;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 6 :
                    // InternalEtlParser.g:2946:3: this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AncestorOrSelfOf_5=ruleAncestorOrSelfOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AncestorOrSelfOf_5;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalEtlParser.g:2958:3: this_EclFocusConcept_6= ruleEclFocusConcept
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getEclFocusConceptParserRuleCall_6());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_EclFocusConcept_6=ruleEclFocusConcept();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_EclFocusConcept_6;
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
    // $ANTLR end "ruleSubExpressionConstraint"


    // $ANTLR start "entryRuleEclFocusConcept"
    // InternalEtlParser.g:2973:1: entryRuleEclFocusConcept returns [EObject current=null] : iv_ruleEclFocusConcept= ruleEclFocusConcept EOF ;
    public final EObject entryRuleEclFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEclFocusConcept = null;


        try {
            // InternalEtlParser.g:2973:56: (iv_ruleEclFocusConcept= ruleEclFocusConcept EOF )
            // InternalEtlParser.g:2974:2: iv_ruleEclFocusConcept= ruleEclFocusConcept EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEclFocusConceptRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEclFocusConcept=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEclFocusConcept; 
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
    // $ANTLR end "entryRuleEclFocusConcept"


    // $ANTLR start "ruleEclFocusConcept"
    // InternalEtlParser.g:2980:1: ruleEclFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_EclConceptReference_1= ruleEclConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) ;
    public final EObject ruleEclFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_EclConceptReference_1 = null;

        EObject this_Any_2 = null;

        EObject this_NestedExpression_3 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:2986:2: ( (this_MemberOf_0= ruleMemberOf | this_EclConceptReference_1= ruleEclConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) )
            // InternalEtlParser.g:2987:2: (this_MemberOf_0= ruleMemberOf | this_EclConceptReference_1= ruleEclConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            {
            // InternalEtlParser.g:2987:2: (this_MemberOf_0= ruleMemberOf | this_EclConceptReference_1= ruleEclConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            int alt57=4;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt57=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt57=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt57=3;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt57=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // InternalEtlParser.g:2988:3: this_MemberOf_0= ruleMemberOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getMemberOfParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_MemberOf_0=ruleMemberOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_MemberOf_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:3000:3: this_EclConceptReference_1= ruleEclConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getEclConceptReferenceParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_EclConceptReference_1=ruleEclConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_EclConceptReference_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:3012:3: this_Any_2= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getAnyParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_Any_2=ruleAny();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_Any_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalEtlParser.g:3024:3: this_NestedExpression_3= ruleNestedExpression
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getNestedExpressionParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NestedExpression_3=ruleNestedExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_NestedExpression_3;
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
    // $ANTLR end "ruleEclFocusConcept"


    // $ANTLR start "entryRuleChildOf"
    // InternalEtlParser.g:3039:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // InternalEtlParser.g:3039:48: (iv_ruleChildOf= ruleChildOf EOF )
            // InternalEtlParser.g:3040:2: iv_ruleChildOf= ruleChildOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getChildOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleChildOf=ruleChildOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleChildOf; 
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
    // $ANTLR end "entryRuleChildOf"


    // $ANTLR start "ruleChildOf"
    // InternalEtlParser.g:3046:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3052:2: ( (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3053:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3053:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3054:3: this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3058:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3059:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3059:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3060:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getChildOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getChildOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleChildOf"


    // $ANTLR start "entryRuleDescendantOf"
    // InternalEtlParser.g:3081:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // InternalEtlParser.g:3081:53: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // InternalEtlParser.g:3082:2: iv_ruleDescendantOf= ruleDescendantOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDescendantOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDescendantOf=ruleDescendantOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDescendantOf; 
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
    // $ANTLR end "entryRuleDescendantOf"


    // $ANTLR start "ruleDescendantOf"
    // InternalEtlParser.g:3088:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3094:2: ( (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3095:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3095:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3096:3: this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3100:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3101:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3101:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3102:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDescendantOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleDescendantOf"


    // $ANTLR start "entryRuleDescendantOrSelfOf"
    // InternalEtlParser.g:3123:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // InternalEtlParser.g:3123:59: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // InternalEtlParser.g:3124:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDescendantOrSelfOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDescendantOrSelfOf=ruleDescendantOrSelfOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDescendantOrSelfOf; 
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
    // $ANTLR end "entryRuleDescendantOrSelfOf"


    // $ANTLR start "ruleDescendantOrSelfOf"
    // InternalEtlParser.g:3130:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3136:2: ( (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3137:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3137:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3138:3: this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3142:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3143:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3143:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3144:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleDescendantOrSelfOf"


    // $ANTLR start "entryRuleParentOf"
    // InternalEtlParser.g:3165:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // InternalEtlParser.g:3165:49: (iv_ruleParentOf= ruleParentOf EOF )
            // InternalEtlParser.g:3166:2: iv_ruleParentOf= ruleParentOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getParentOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleParentOf=ruleParentOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleParentOf; 
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
    // $ANTLR end "entryRuleParentOf"


    // $ANTLR start "ruleParentOf"
    // InternalEtlParser.g:3172:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3178:2: ( (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3179:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3179:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3180:3: this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3184:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3185:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3185:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3186:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getParentOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getParentOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleParentOf"


    // $ANTLR start "entryRuleAncestorOf"
    // InternalEtlParser.g:3207:1: entryRuleAncestorOf returns [EObject current=null] : iv_ruleAncestorOf= ruleAncestorOf EOF ;
    public final EObject entryRuleAncestorOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOf = null;


        try {
            // InternalEtlParser.g:3207:51: (iv_ruleAncestorOf= ruleAncestorOf EOF )
            // InternalEtlParser.g:3208:2: iv_ruleAncestorOf= ruleAncestorOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAncestorOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAncestorOf=ruleAncestorOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAncestorOf; 
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
    // $ANTLR end "entryRuleAncestorOf"


    // $ANTLR start "ruleAncestorOf"
    // InternalEtlParser.g:3214:1: ruleAncestorOf returns [EObject current=null] : (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleAncestorOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3220:2: ( (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3221:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3221:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3222:3: this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3226:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3227:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3227:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3228:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAncestorOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleAncestorOf"


    // $ANTLR start "entryRuleAncestorOrSelfOf"
    // InternalEtlParser.g:3249:1: entryRuleAncestorOrSelfOf returns [EObject current=null] : iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF ;
    public final EObject entryRuleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOrSelfOf = null;


        try {
            // InternalEtlParser.g:3249:57: (iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF )
            // InternalEtlParser.g:3250:2: iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAncestorOrSelfOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAncestorOrSelfOf=ruleAncestorOrSelfOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAncestorOrSelfOf; 
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
    // $ANTLR end "entryRuleAncestorOrSelfOf"


    // $ANTLR start "ruleAncestorOrSelfOf"
    // InternalEtlParser.g:3256:1: ruleAncestorOrSelfOf returns [EObject current=null] : (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) ;
    public final EObject ruleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3262:2: ( (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) ) )
            // InternalEtlParser.g:3263:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            {
            // InternalEtlParser.g:3263:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) ) )
            // InternalEtlParser.g:3264:3: this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            {
            this_DBL_GT_0=(Token)match(input,RULE_DBL_GT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3268:3: ( (lv_constraint_1_0= ruleEclFocusConcept ) )
            // InternalEtlParser.g:3269:4: (lv_constraint_1_0= ruleEclFocusConcept )
            {
            // InternalEtlParser.g:3269:4: (lv_constraint_1_0= ruleEclFocusConcept )
            // InternalEtlParser.g:3270:5: lv_constraint_1_0= ruleEclFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleEclFocusConcept();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
    // $ANTLR end "ruleAncestorOrSelfOf"


    // $ANTLR start "entryRuleMemberOf"
    // InternalEtlParser.g:3291:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // InternalEtlParser.g:3291:49: (iv_ruleMemberOf= ruleMemberOf EOF )
            // InternalEtlParser.g:3292:2: iv_ruleMemberOf= ruleMemberOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMemberOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleMemberOf=ruleMemberOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMemberOf; 
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
    // $ANTLR end "entryRuleMemberOf"


    // $ANTLR start "ruleMemberOf"
    // InternalEtlParser.g:3298:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;

        EObject lv_constraint_1_3 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3304:2: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) ) )
            // InternalEtlParser.g:3305:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) )
            {
            // InternalEtlParser.g:3305:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) )
            // InternalEtlParser.g:3306:3: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3310:3: ( ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) )
            // InternalEtlParser.g:3311:4: ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) )
            {
            // InternalEtlParser.g:3311:4: ( (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) )
            // InternalEtlParser.g:3312:5: (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression )
            {
            // InternalEtlParser.g:3312:5: (lv_constraint_1_1= ruleEclConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression )
            int alt58=3;
            switch ( input.LA(1) ) {
            case RULE_DIGIT_NONZERO:
                {
                alt58=1;
                }
                break;
            case RULE_WILDCARD:
                {
                alt58=2;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt58=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }

            switch (alt58) {
                case 1 :
                    // InternalEtlParser.g:3313:6: lv_constraint_1_1= ruleEclConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintEclConceptReferenceParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_1=ruleEclConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getMemberOfRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_1_1,
                      							"com.b2international.snomed.ecl.Ecl.EclConceptReference");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:3329:6: lv_constraint_1_2= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintAnyParserRuleCall_1_0_1());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getMemberOfRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_1_2,
                      							"com.b2international.snomed.ecl.Ecl.Any");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:3345:6: lv_constraint_1_3= ruleNestedExpression
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintNestedExpressionParserRuleCall_1_0_2());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_3=ruleNestedExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getMemberOfRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_1_3,
                      							"com.b2international.snomed.ecl.Ecl.NestedExpression");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }
                    break;

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
    // $ANTLR end "ruleMemberOf"


    // $ANTLR start "entryRuleEclConceptReference"
    // InternalEtlParser.g:3367:1: entryRuleEclConceptReference returns [EObject current=null] : iv_ruleEclConceptReference= ruleEclConceptReference EOF ;
    public final EObject entryRuleEclConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEclConceptReference = null;


        try {
            // InternalEtlParser.g:3367:60: (iv_ruleEclConceptReference= ruleEclConceptReference EOF )
            // InternalEtlParser.g:3368:2: iv_ruleEclConceptReference= ruleEclConceptReference EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEclConceptReferenceRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEclConceptReference=ruleEclConceptReference();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEclConceptReference; 
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
    // $ANTLR end "entryRuleEclConceptReference"


    // $ANTLR start "ruleEclConceptReference"
    // InternalEtlParser.g:3374:1: ruleEclConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) ;
    public final EObject ruleEclConceptReference() throws RecognitionException {
        EObject current = null;

        Token lv_term_1_0=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3380:2: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) )
            // InternalEtlParser.g:3381:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            {
            // InternalEtlParser.g:3381:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            // InternalEtlParser.g:3382:3: ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )?
            {
            // InternalEtlParser.g:3382:3: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // InternalEtlParser.g:3383:4: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // InternalEtlParser.g:3383:4: (lv_id_0_0= ruleSnomedIdentifier )
            // InternalEtlParser.g:3384:5: lv_id_0_0= ruleSnomedIdentifier
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEclConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_33);
            lv_id_0_0=ruleSnomedIdentifier();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getEclConceptReferenceRule());
              					}
              					set(
              						current,
              						"id",
              						lv_id_0_0,
              						"com.b2international.snomed.ecl.Ecl.SnomedIdentifier");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEtlParser.g:3401:3: ( (lv_term_1_0= RULE_TERM_STRING ) )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==RULE_TERM_STRING) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // InternalEtlParser.g:3402:4: (lv_term_1_0= RULE_TERM_STRING )
                    {
                    // InternalEtlParser.g:3402:4: (lv_term_1_0= RULE_TERM_STRING )
                    // InternalEtlParser.g:3403:5: lv_term_1_0= RULE_TERM_STRING
                    {
                    lv_term_1_0=(Token)match(input,RULE_TERM_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_term_1_0, grammarAccess.getEclConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getEclConceptReferenceRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"term",
                      						lv_term_1_0,
                      						"com.b2international.snomed.ecl.Ecl.TERM_STRING");
                      				
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
    // $ANTLR end "ruleEclConceptReference"


    // $ANTLR start "entryRuleAny"
    // InternalEtlParser.g:3423:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // InternalEtlParser.g:3423:44: (iv_ruleAny= ruleAny EOF )
            // InternalEtlParser.g:3424:2: iv_ruleAny= ruleAny EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAnyRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAny=ruleAny();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAny; 
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
    // $ANTLR end "entryRuleAny"


    // $ANTLR start "ruleAny"
    // InternalEtlParser.g:3430:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;


        	enterRule();

        try {
            // InternalEtlParser.g:3436:2: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // InternalEtlParser.g:3437:2: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // InternalEtlParser.g:3437:2: (this_WILDCARD_0= RULE_WILDCARD () )
            // InternalEtlParser.g:3438:3: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3442:3: ()
            // InternalEtlParser.g:3443:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getAnyAccess().getAnyAction_1(),
              					current);
              			
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
    // $ANTLR end "ruleAny"


    // $ANTLR start "entryRuleEclRefinement"
    // InternalEtlParser.g:3456:1: entryRuleEclRefinement returns [EObject current=null] : iv_ruleEclRefinement= ruleEclRefinement EOF ;
    public final EObject entryRuleEclRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEclRefinement = null;


        try {
            // InternalEtlParser.g:3456:54: (iv_ruleEclRefinement= ruleEclRefinement EOF )
            // InternalEtlParser.g:3457:2: iv_ruleEclRefinement= ruleEclRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEclRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEclRefinement=ruleEclRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEclRefinement; 
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
    // $ANTLR end "entryRuleEclRefinement"


    // $ANTLR start "ruleEclRefinement"
    // InternalEtlParser.g:3463:1: ruleEclRefinement returns [EObject current=null] : this_OrRefinement_0= ruleOrRefinement ;
    public final EObject ruleEclRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_OrRefinement_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3469:2: (this_OrRefinement_0= ruleOrRefinement )
            // InternalEtlParser.g:3470:2: this_OrRefinement_0= ruleOrRefinement
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getEclRefinementAccess().getOrRefinementParserRuleCall());
              	
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_OrRefinement_0=ruleOrRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current = this_OrRefinement_0;
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
    // $ANTLR end "ruleEclRefinement"


    // $ANTLR start "entryRuleOrRefinement"
    // InternalEtlParser.g:3484:1: entryRuleOrRefinement returns [EObject current=null] : iv_ruleOrRefinement= ruleOrRefinement EOF ;
    public final EObject entryRuleOrRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrRefinement = null;


        try {
            // InternalEtlParser.g:3484:53: (iv_ruleOrRefinement= ruleOrRefinement EOF )
            // InternalEtlParser.g:3485:2: iv_ruleOrRefinement= ruleOrRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOrRefinement=ruleOrRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrRefinement; 
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
    // $ANTLR end "entryRuleOrRefinement"


    // $ANTLR start "ruleOrRefinement"
    // InternalEtlParser.g:3491:1: ruleOrRefinement returns [EObject current=null] : (this_AndRefinement_0= ruleAndRefinement ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) ;
    public final EObject ruleOrRefinement() throws RecognitionException {
        EObject current = null;

        Token this_DISJUNCTION_2=null;
        EObject this_AndRefinement_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3497:2: ( (this_AndRefinement_0= ruleAndRefinement ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) )
            // InternalEtlParser.g:3498:2: (this_AndRefinement_0= ruleAndRefinement ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            {
            // InternalEtlParser.g:3498:2: (this_AndRefinement_0= ruleAndRefinement ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            // InternalEtlParser.g:3499:3: this_AndRefinement_0= ruleAndRefinement ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_38);
            this_AndRefinement_0=ruleAndRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:3510:3: ( ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==RULE_DISJUNCTION) ) {
                    int LA60_4 = input.LA(2);

                    if ( (synpred90_InternalEtlParser()) ) {
                        alt60=1;
                    }


                }


                switch (alt60) {
            	case 1 :
            	    // InternalEtlParser.g:3511:4: ( RULE_DISJUNCTION )=> ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    {
            	    // InternalEtlParser.g:3512:4: ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    // InternalEtlParser.g:3513:5: () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndRefinement ) )
            	    {
            	    // InternalEtlParser.g:3513:5: ()
            	    // InternalEtlParser.g:3514:6: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      						/* */
            	      					
            	    }
            	    if ( state.backtracking==0 ) {

            	      						current = forceCreateModelElementAndSet(
            	      							grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0(),
            	      							current);
            	      					
            	    }

            	    }

            	    this_DISJUNCTION_2=(Token)match(input,RULE_DISJUNCTION,FollowSets000.FOLLOW_42); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrRefinementAccess().getDISJUNCTIONTerminalRuleCall_1_0_1());
            	      				
            	    }
            	    // InternalEtlParser.g:3527:5: ( (lv_right_3_0= ruleAndRefinement ) )
            	    // InternalEtlParser.g:3528:6: (lv_right_3_0= ruleAndRefinement )
            	    {
            	    // InternalEtlParser.g:3528:6: (lv_right_3_0= ruleAndRefinement )
            	    // InternalEtlParser.g:3529:7: lv_right_3_0= ruleAndRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_38);
            	    lv_right_3_0=ruleAndRefinement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      							if (current==null) {
            	      								current = createModelElementForParent(grammarAccess.getOrRefinementRule());
            	      							}
            	      							set(
            	      								current,
            	      								"right",
            	      								lv_right_3_0,
            	      								"com.b2international.snomed.ecl.Ecl.AndRefinement");
            	      							afterParserOrEnumRuleCall();
            	      						
            	    }

            	    }


            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop60;
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
    // $ANTLR end "ruleOrRefinement"


    // $ANTLR start "entryRuleAndRefinement"
    // InternalEtlParser.g:3552:1: entryRuleAndRefinement returns [EObject current=null] : iv_ruleAndRefinement= ruleAndRefinement EOF ;
    public final EObject entryRuleAndRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndRefinement = null;


        try {
            // InternalEtlParser.g:3552:54: (iv_ruleAndRefinement= ruleAndRefinement EOF )
            // InternalEtlParser.g:3553:2: iv_ruleAndRefinement= ruleAndRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAndRefinement=ruleAndRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndRefinement; 
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
    // $ANTLR end "entryRuleAndRefinement"


    // $ANTLR start "ruleAndRefinement"
    // InternalEtlParser.g:3559:1: ruleAndRefinement returns [EObject current=null] : (this_SubRefinement_0= ruleSubRefinement ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) ;
    public final EObject ruleAndRefinement() throws RecognitionException {
        EObject current = null;

        Token this_CONJUNCTION_2=null;
        Token this_COMMA_3=null;
        EObject this_SubRefinement_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3565:2: ( (this_SubRefinement_0= ruleSubRefinement ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) )
            // InternalEtlParser.g:3566:2: (this_SubRefinement_0= ruleSubRefinement ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            {
            // InternalEtlParser.g:3566:2: (this_SubRefinement_0= ruleSubRefinement ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            // InternalEtlParser.g:3567:3: this_SubRefinement_0= ruleSubRefinement ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndRefinementAccess().getSubRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_39);
            this_SubRefinement_0=ruleSubRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:3578:3: ( ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==RULE_CONJUNCTION) ) {
                    int LA62_3 = input.LA(2);

                    if ( (synpred92_InternalEtlParser()) ) {
                        alt62=1;
                    }


                }
                else if ( (LA62_0==RULE_COMMA) ) {
                    int LA62_4 = input.LA(2);

                    if ( (synpred92_InternalEtlParser()) ) {
                        alt62=1;
                    }


                }


                switch (alt62) {
            	case 1 :
            	    // InternalEtlParser.g:3579:4: ( RULE_CONJUNCTION | RULE_COMMA )=> ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    {
            	    // InternalEtlParser.g:3580:4: ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    // InternalEtlParser.g:3581:5: () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubRefinement ) )
            	    {
            	    // InternalEtlParser.g:3581:5: ()
            	    // InternalEtlParser.g:3582:6: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      						/* */
            	      					
            	    }
            	    if ( state.backtracking==0 ) {

            	      						current = forceCreateModelElementAndSet(
            	      							grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0(),
            	      							current);
            	      					
            	    }

            	    }

            	    // InternalEtlParser.g:3591:5: (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA )
            	    int alt61=2;
            	    int LA61_0 = input.LA(1);

            	    if ( (LA61_0==RULE_CONJUNCTION) ) {
            	        alt61=1;
            	    }
            	    else if ( (LA61_0==RULE_COMMA) ) {
            	        alt61=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 61, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt61) {
            	        case 1 :
            	            // InternalEtlParser.g:3592:6: this_CONJUNCTION_2= RULE_CONJUNCTION
            	            {
            	            this_CONJUNCTION_2=(Token)match(input,RULE_CONJUNCTION,FollowSets000.FOLLOW_42); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndRefinementAccess().getCONJUNCTIONTerminalRuleCall_1_0_1_0());
            	              					
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEtlParser.g:3597:6: this_COMMA_3= RULE_COMMA
            	            {
            	            this_COMMA_3=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_42); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(this_COMMA_3, grammarAccess.getAndRefinementAccess().getCOMMATerminalRuleCall_1_0_1_1());
            	              					
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEtlParser.g:3602:5: ( (lv_right_4_0= ruleSubRefinement ) )
            	    // InternalEtlParser.g:3603:6: (lv_right_4_0= ruleSubRefinement )
            	    {
            	    // InternalEtlParser.g:3603:6: (lv_right_4_0= ruleSubRefinement )
            	    // InternalEtlParser.g:3604:7: lv_right_4_0= ruleSubRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_39);
            	    lv_right_4_0=ruleSubRefinement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      							if (current==null) {
            	      								current = createModelElementForParent(grammarAccess.getAndRefinementRule());
            	      							}
            	      							set(
            	      								current,
            	      								"right",
            	      								lv_right_4_0,
            	      								"com.b2international.snomed.ecl.Ecl.SubRefinement");
            	      							afterParserOrEnumRuleCall();
            	      						
            	    }

            	    }


            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop62;
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
    // $ANTLR end "ruleAndRefinement"


    // $ANTLR start "entryRuleSubRefinement"
    // InternalEtlParser.g:3627:1: entryRuleSubRefinement returns [EObject current=null] : iv_ruleSubRefinement= ruleSubRefinement EOF ;
    public final EObject entryRuleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubRefinement = null;


        try {
            // InternalEtlParser.g:3627:54: (iv_ruleSubRefinement= ruleSubRefinement EOF )
            // InternalEtlParser.g:3628:2: iv_ruleSubRefinement= ruleSubRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSubRefinement=ruleSubRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubRefinement; 
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
    // $ANTLR end "entryRuleSubRefinement"


    // $ANTLR start "ruleSubRefinement"
    // InternalEtlParser.g:3634:1: ruleSubRefinement returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_EclAttributeGroup_1= ruleEclAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) ;
    public final EObject ruleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_EclAttributeGroup_1 = null;

        EObject this_NestedRefinement_2 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3640:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_EclAttributeGroup_1= ruleEclAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) )
            // InternalEtlParser.g:3641:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_EclAttributeGroup_1= ruleEclAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            {
            // InternalEtlParser.g:3641:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_EclAttributeGroup_1= ruleEclAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            int alt63=3;
            alt63 = dfa63.predict(input);
            switch (alt63) {
                case 1 :
                    // InternalEtlParser.g:3642:3: this_AttributeConstraint_0= ruleAttributeConstraint
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubRefinementAccess().getAttributeConstraintParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeConstraint_0=ruleAttributeConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeConstraint_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:3654:3: this_EclAttributeGroup_1= ruleEclAttributeGroup
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubRefinementAccess().getEclAttributeGroupParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_EclAttributeGroup_1=ruleEclAttributeGroup();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_EclAttributeGroup_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:3666:3: this_NestedRefinement_2= ruleNestedRefinement
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubRefinementAccess().getNestedRefinementParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NestedRefinement_2=ruleNestedRefinement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_NestedRefinement_2;
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
    // $ANTLR end "ruleSubRefinement"


    // $ANTLR start "entryRuleNestedRefinement"
    // InternalEtlParser.g:3681:1: entryRuleNestedRefinement returns [EObject current=null] : iv_ruleNestedRefinement= ruleNestedRefinement EOF ;
    public final EObject entryRuleNestedRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedRefinement = null;


        try {
            // InternalEtlParser.g:3681:57: (iv_ruleNestedRefinement= ruleNestedRefinement EOF )
            // InternalEtlParser.g:3682:2: iv_ruleNestedRefinement= ruleNestedRefinement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNestedRefinementRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNestedRefinement=ruleNestedRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNestedRefinement; 
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
    // $ANTLR end "entryRuleNestedRefinement"


    // $ANTLR start "ruleNestedRefinement"
    // InternalEtlParser.g:3688:1: ruleNestedRefinement returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedRefinement() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3694:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEtlParser.g:3695:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEtlParser.g:3695:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEtlParser.g:3696:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_42); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedRefinementAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:3700:3: ( (lv_nested_1_0= ruleEclRefinement ) )
            // InternalEtlParser.g:3701:4: (lv_nested_1_0= ruleEclRefinement )
            {
            // InternalEtlParser.g:3701:4: (lv_nested_1_0= ruleEclRefinement )
            // InternalEtlParser.g:3702:5: lv_nested_1_0= ruleEclRefinement
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedRefinementAccess().getNestedEclRefinementParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_11);
            lv_nested_1_0=ruleEclRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getNestedRefinementRule());
              					}
              					set(
              						current,
              						"nested",
              						lv_nested_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclRefinement");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedRefinementAccess().getROUND_CLOSETerminalRuleCall_2());
              		
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
    // $ANTLR end "ruleNestedRefinement"


    // $ANTLR start "entryRuleEclAttributeGroup"
    // InternalEtlParser.g:3727:1: entryRuleEclAttributeGroup returns [EObject current=null] : iv_ruleEclAttributeGroup= ruleEclAttributeGroup EOF ;
    public final EObject entryRuleEclAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEclAttributeGroup = null;


        try {
            // InternalEtlParser.g:3727:58: (iv_ruleEclAttributeGroup= ruleEclAttributeGroup EOF )
            // InternalEtlParser.g:3728:2: iv_ruleEclAttributeGroup= ruleEclAttributeGroup EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEclAttributeGroupRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEclAttributeGroup=ruleEclAttributeGroup();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEclAttributeGroup; 
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
    // $ANTLR end "entryRuleEclAttributeGroup"


    // $ANTLR start "ruleEclAttributeGroup"
    // InternalEtlParser.g:3734:1: ruleEclAttributeGroup returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleEclAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) ;
    public final EObject ruleEclAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_CURLY_OPEN_1=null;
        Token this_CURLY_CLOSE_3=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_refinement_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3740:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleEclAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) )
            // InternalEtlParser.g:3741:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleEclAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            {
            // InternalEtlParser.g:3741:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleEclAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            // InternalEtlParser.g:3742:3: ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleEclAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE
            {
            // InternalEtlParser.g:3742:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==RULE_SQUARE_OPEN) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // InternalEtlParser.g:3743:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalEtlParser.g:3743:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalEtlParser.g:3744:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getEclAttributeGroupAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_7);
                    lv_cardinality_0_0=ruleCardinality();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getEclAttributeGroupRule());
                      					}
                      					set(
                      						current,
                      						"cardinality",
                      						lv_cardinality_0_0,
                      						"com.b2international.snomed.ecl.Ecl.Cardinality");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            this_CURLY_OPEN_1=(Token)match(input,RULE_CURLY_OPEN,FollowSets000.FOLLOW_44); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getEclAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:3765:3: ( (lv_refinement_2_0= ruleEclAttributeSet ) )
            // InternalEtlParser.g:3766:4: (lv_refinement_2_0= ruleEclAttributeSet )
            {
            // InternalEtlParser.g:3766:4: (lv_refinement_2_0= ruleEclAttributeSet )
            // InternalEtlParser.g:3767:5: lv_refinement_2_0= ruleEclAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getEclAttributeGroupAccess().getRefinementEclAttributeSetParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_45);
            lv_refinement_2_0=ruleEclAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getEclAttributeGroupRule());
              					}
              					set(
              						current,
              						"refinement",
              						lv_refinement_2_0,
              						"com.b2international.snomed.ecl.Ecl.EclAttributeSet");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_CURLY_CLOSE_3=(Token)match(input,RULE_CURLY_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_CLOSE_3, grammarAccess.getEclAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
              		
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
    // $ANTLR end "ruleEclAttributeGroup"


    // $ANTLR start "entryRuleEclAttributeSet"
    // InternalEtlParser.g:3792:1: entryRuleEclAttributeSet returns [EObject current=null] : iv_ruleEclAttributeSet= ruleEclAttributeSet EOF ;
    public final EObject entryRuleEclAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEclAttributeSet = null;


        try {
            // InternalEtlParser.g:3792:56: (iv_ruleEclAttributeSet= ruleEclAttributeSet EOF )
            // InternalEtlParser.g:3793:2: iv_ruleEclAttributeSet= ruleEclAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEclAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleEclAttributeSet=ruleEclAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEclAttributeSet; 
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
    // $ANTLR end "entryRuleEclAttributeSet"


    // $ANTLR start "ruleEclAttributeSet"
    // InternalEtlParser.g:3799:1: ruleEclAttributeSet returns [EObject current=null] : this_OrAttributeSet_0= ruleOrAttributeSet ;
    public final EObject ruleEclAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_OrAttributeSet_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3805:2: (this_OrAttributeSet_0= ruleOrAttributeSet )
            // InternalEtlParser.g:3806:2: this_OrAttributeSet_0= ruleOrAttributeSet
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getEclAttributeSetAccess().getOrAttributeSetParserRuleCall());
              	
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_OrAttributeSet_0=ruleOrAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current = this_OrAttributeSet_0;
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
    // $ANTLR end "ruleEclAttributeSet"


    // $ANTLR start "entryRuleOrAttributeSet"
    // InternalEtlParser.g:3820:1: entryRuleOrAttributeSet returns [EObject current=null] : iv_ruleOrAttributeSet= ruleOrAttributeSet EOF ;
    public final EObject entryRuleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrAttributeSet = null;


        try {
            // InternalEtlParser.g:3820:55: (iv_ruleOrAttributeSet= ruleOrAttributeSet EOF )
            // InternalEtlParser.g:3821:2: iv_ruleOrAttributeSet= ruleOrAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOrAttributeSet=ruleOrAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrAttributeSet; 
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
    // $ANTLR end "entryRuleOrAttributeSet"


    // $ANTLR start "ruleOrAttributeSet"
    // InternalEtlParser.g:3827:1: ruleOrAttributeSet returns [EObject current=null] : (this_AndAttributeSet_0= ruleAndAttributeSet ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) ;
    public final EObject ruleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_DISJUNCTION_2=null;
        EObject this_AndAttributeSet_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3833:2: ( (this_AndAttributeSet_0= ruleAndAttributeSet ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) )
            // InternalEtlParser.g:3834:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            {
            // InternalEtlParser.g:3834:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            // InternalEtlParser.g:3835:3: this_AndAttributeSet_0= ruleAndAttributeSet ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrAttributeSetAccess().getAndAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_38);
            this_AndAttributeSet_0=ruleAndAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:3846:3: ( () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==RULE_DISJUNCTION) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // InternalEtlParser.g:3847:4: () this_DISJUNCTION_2= RULE_DISJUNCTION ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    {
            	    // InternalEtlParser.g:3847:4: ()
            	    // InternalEtlParser.g:3848:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    this_DISJUNCTION_2=(Token)match(input,RULE_DISJUNCTION,FollowSets000.FOLLOW_44); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrAttributeSetAccess().getDISJUNCTIONTerminalRuleCall_1_1());
            	      			
            	    }
            	    // InternalEtlParser.g:3861:4: ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    // InternalEtlParser.g:3862:5: (lv_right_3_0= ruleAndAttributeSet )
            	    {
            	    // InternalEtlParser.g:3862:5: (lv_right_3_0= ruleAndAttributeSet )
            	    // InternalEtlParser.g:3863:6: lv_right_3_0= ruleAndAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrAttributeSetAccess().getRightAndAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_38);
            	    lv_right_3_0=ruleAndAttributeSet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getOrAttributeSetRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_3_0,
            	      							"com.b2international.snomed.ecl.Ecl.AndAttributeSet");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop65;
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
    // $ANTLR end "ruleOrAttributeSet"


    // $ANTLR start "entryRuleAndAttributeSet"
    // InternalEtlParser.g:3885:1: entryRuleAndAttributeSet returns [EObject current=null] : iv_ruleAndAttributeSet= ruleAndAttributeSet EOF ;
    public final EObject entryRuleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndAttributeSet = null;


        try {
            // InternalEtlParser.g:3885:56: (iv_ruleAndAttributeSet= ruleAndAttributeSet EOF )
            // InternalEtlParser.g:3886:2: iv_ruleAndAttributeSet= ruleAndAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAndAttributeSet=ruleAndAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndAttributeSet; 
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
    // $ANTLR end "entryRuleAndAttributeSet"


    // $ANTLR start "ruleAndAttributeSet"
    // InternalEtlParser.g:3892:1: ruleAndAttributeSet returns [EObject current=null] : (this_SubAttributeSet_0= ruleSubAttributeSet ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) ;
    public final EObject ruleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_CONJUNCTION_2=null;
        Token this_COMMA_3=null;
        EObject this_SubAttributeSet_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3898:2: ( (this_SubAttributeSet_0= ruleSubAttributeSet ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) )
            // InternalEtlParser.g:3899:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            {
            // InternalEtlParser.g:3899:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            // InternalEtlParser.g:3900:3: this_SubAttributeSet_0= ruleSubAttributeSet ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndAttributeSetAccess().getSubAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_39);
            this_SubAttributeSet_0=ruleSubAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEtlParser.g:3911:3: ( () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( ((LA67_0>=RULE_COMMA && LA67_0<=RULE_CONJUNCTION)) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // InternalEtlParser.g:3912:4: () (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA ) ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    {
            	    // InternalEtlParser.g:3912:4: ()
            	    // InternalEtlParser.g:3913:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    // InternalEtlParser.g:3922:4: (this_CONJUNCTION_2= RULE_CONJUNCTION | this_COMMA_3= RULE_COMMA )
            	    int alt66=2;
            	    int LA66_0 = input.LA(1);

            	    if ( (LA66_0==RULE_CONJUNCTION) ) {
            	        alt66=1;
            	    }
            	    else if ( (LA66_0==RULE_COMMA) ) {
            	        alt66=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 66, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt66) {
            	        case 1 :
            	            // InternalEtlParser.g:3923:5: this_CONJUNCTION_2= RULE_CONJUNCTION
            	            {
            	            this_CONJUNCTION_2=(Token)match(input,RULE_CONJUNCTION,FollowSets000.FOLLOW_44); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndAttributeSetAccess().getCONJUNCTIONTerminalRuleCall_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEtlParser.g:3928:5: this_COMMA_3= RULE_COMMA
            	            {
            	            this_COMMA_3=(Token)match(input,RULE_COMMA,FollowSets000.FOLLOW_44); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(this_COMMA_3, grammarAccess.getAndAttributeSetAccess().getCOMMATerminalRuleCall_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEtlParser.g:3933:4: ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    // InternalEtlParser.g:3934:5: (lv_right_4_0= ruleSubAttributeSet )
            	    {
            	    // InternalEtlParser.g:3934:5: (lv_right_4_0= ruleSubAttributeSet )
            	    // InternalEtlParser.g:3935:6: lv_right_4_0= ruleSubAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_39);
            	    lv_right_4_0=ruleSubAttributeSet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getAndAttributeSetRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_4_0,
            	      							"com.b2international.snomed.ecl.Ecl.SubAttributeSet");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop67;
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
    // $ANTLR end "ruleAndAttributeSet"


    // $ANTLR start "entryRuleSubAttributeSet"
    // InternalEtlParser.g:3957:1: entryRuleSubAttributeSet returns [EObject current=null] : iv_ruleSubAttributeSet= ruleSubAttributeSet EOF ;
    public final EObject entryRuleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubAttributeSet = null;


        try {
            // InternalEtlParser.g:3957:56: (iv_ruleSubAttributeSet= ruleSubAttributeSet EOF )
            // InternalEtlParser.g:3958:2: iv_ruleSubAttributeSet= ruleSubAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSubAttributeSet=ruleSubAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubAttributeSet; 
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
    // $ANTLR end "entryRuleSubAttributeSet"


    // $ANTLR start "ruleSubAttributeSet"
    // InternalEtlParser.g:3964:1: ruleSubAttributeSet returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) ;
    public final EObject ruleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_NestedAttributeSet_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:3970:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) )
            // InternalEtlParser.g:3971:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            {
            // InternalEtlParser.g:3971:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            int alt68=2;
            alt68 = dfa68.predict(input);
            switch (alt68) {
                case 1 :
                    // InternalEtlParser.g:3972:3: this_AttributeConstraint_0= ruleAttributeConstraint
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubAttributeSetAccess().getAttributeConstraintParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeConstraint_0=ruleAttributeConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeConstraint_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:3984:3: this_NestedAttributeSet_1= ruleNestedAttributeSet
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubAttributeSetAccess().getNestedAttributeSetParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NestedAttributeSet_1=ruleNestedAttributeSet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_NestedAttributeSet_1;
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
    // $ANTLR end "ruleSubAttributeSet"


    // $ANTLR start "entryRuleNestedAttributeSet"
    // InternalEtlParser.g:3999:1: entryRuleNestedAttributeSet returns [EObject current=null] : iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF ;
    public final EObject entryRuleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedAttributeSet = null;


        try {
            // InternalEtlParser.g:3999:59: (iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF )
            // InternalEtlParser.g:4000:2: iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNestedAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNestedAttributeSet=ruleNestedAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNestedAttributeSet; 
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
    // $ANTLR end "entryRuleNestedAttributeSet"


    // $ANTLR start "ruleNestedAttributeSet"
    // InternalEtlParser.g:4006:1: ruleNestedAttributeSet returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4012:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEtlParser.g:4013:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEtlParser.g:4013:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEtlParser.g:4014:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleEclAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_44); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedAttributeSetAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4018:3: ( (lv_nested_1_0= ruleEclAttributeSet ) )
            // InternalEtlParser.g:4019:4: (lv_nested_1_0= ruleEclAttributeSet )
            {
            // InternalEtlParser.g:4019:4: (lv_nested_1_0= ruleEclAttributeSet )
            // InternalEtlParser.g:4020:5: lv_nested_1_0= ruleEclAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedAttributeSetAccess().getNestedEclAttributeSetParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_11);
            lv_nested_1_0=ruleEclAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getNestedAttributeSetRule());
              					}
              					set(
              						current,
              						"nested",
              						lv_nested_1_0,
              						"com.b2international.snomed.ecl.Ecl.EclAttributeSet");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedAttributeSetAccess().getROUND_CLOSETerminalRuleCall_2());
              		
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
    // $ANTLR end "ruleNestedAttributeSet"


    // $ANTLR start "entryRuleAttributeConstraint"
    // InternalEtlParser.g:4045:1: entryRuleAttributeConstraint returns [EObject current=null] : iv_ruleAttributeConstraint= ruleAttributeConstraint EOF ;
    public final EObject entryRuleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeConstraint = null;


        try {
            // InternalEtlParser.g:4045:60: (iv_ruleAttributeConstraint= ruleAttributeConstraint EOF )
            // InternalEtlParser.g:4046:2: iv_ruleAttributeConstraint= ruleAttributeConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeConstraint=ruleAttributeConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeConstraint; 
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
    // $ANTLR end "entryRuleAttributeConstraint"


    // $ANTLR start "ruleAttributeConstraint"
    // InternalEtlParser.g:4052:1: ruleAttributeConstraint returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) ;
    public final EObject ruleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        Token lv_reversed_1_0=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_attribute_2_0 = null;

        EObject lv_comparison_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4058:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) )
            // InternalEtlParser.g:4059:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            {
            // InternalEtlParser.g:4059:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            // InternalEtlParser.g:4060:3: ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) )
            {
            // InternalEtlParser.g:4060:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==RULE_SQUARE_OPEN) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // InternalEtlParser.g:4061:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalEtlParser.g:4061:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalEtlParser.g:4062:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_46);
                    lv_cardinality_0_0=ruleCardinality();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
                      					}
                      					set(
                      						current,
                      						"cardinality",
                      						lv_cardinality_0_0,
                      						"com.b2international.snomed.ecl.Ecl.Cardinality");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalEtlParser.g:4079:3: ( (lv_reversed_1_0= RULE_REVERSED ) )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==RULE_REVERSED) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // InternalEtlParser.g:4080:4: (lv_reversed_1_0= RULE_REVERSED )
                    {
                    // InternalEtlParser.g:4080:4: (lv_reversed_1_0= RULE_REVERSED )
                    // InternalEtlParser.g:4081:5: lv_reversed_1_0= RULE_REVERSED
                    {
                    lv_reversed_1_0=(Token)match(input,RULE_REVERSED,FollowSets000.FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					newLeafNode(lv_reversed_1_0, grammarAccess.getAttributeConstraintAccess().getReversedREVERSEDTerminalRuleCall_1_0());
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElement(grammarAccess.getAttributeConstraintRule());
                      					}
                      					setWithLastConsumed(
                      						current,
                      						"reversed",
                      						true,
                      						"com.b2international.snomed.ecl.Ecl.REVERSED");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalEtlParser.g:4097:3: ( (lv_attribute_2_0= ruleSubExpressionConstraint ) )
            // InternalEtlParser.g:4098:4: (lv_attribute_2_0= ruleSubExpressionConstraint )
            {
            // InternalEtlParser.g:4098:4: (lv_attribute_2_0= ruleSubExpressionConstraint )
            // InternalEtlParser.g:4099:5: lv_attribute_2_0= ruleSubExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_47);
            lv_attribute_2_0=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
              					}
              					set(
              						current,
              						"attribute",
              						lv_attribute_2_0,
              						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEtlParser.g:4116:3: ( (lv_comparison_3_0= ruleComparison ) )
            // InternalEtlParser.g:4117:4: (lv_comparison_3_0= ruleComparison )
            {
            // InternalEtlParser.g:4117:4: (lv_comparison_3_0= ruleComparison )
            // InternalEtlParser.g:4118:5: lv_comparison_3_0= ruleComparison
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getComparisonComparisonParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_comparison_3_0=ruleComparison();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
              					}
              					set(
              						current,
              						"comparison",
              						lv_comparison_3_0,
              						"com.b2international.snomed.ecl.Ecl.Comparison");
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
    // $ANTLR end "ruleAttributeConstraint"


    // $ANTLR start "entryRuleCardinality"
    // InternalEtlParser.g:4139:1: entryRuleCardinality returns [EObject current=null] : iv_ruleCardinality= ruleCardinality EOF ;
    public final EObject entryRuleCardinality() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCardinality = null;


        try {
            // InternalEtlParser.g:4139:52: (iv_ruleCardinality= ruleCardinality EOF )
            // InternalEtlParser.g:4140:2: iv_ruleCardinality= ruleCardinality EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getCardinalityRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleCardinality=ruleCardinality();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleCardinality; 
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
    // $ANTLR end "entryRuleCardinality"


    // $ANTLR start "ruleCardinality"
    // InternalEtlParser.g:4146:1: ruleCardinality returns [EObject current=null] : (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) ;
    public final EObject ruleCardinality() throws RecognitionException {
        EObject current = null;

        Token this_SQUARE_OPEN_0=null;
        Token this_TO_2=null;
        Token this_SQUARE_CLOSE_4=null;
        AntlrDatatypeRuleToken lv_min_1_0 = null;

        AntlrDatatypeRuleToken lv_max_3_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4152:2: ( (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) )
            // InternalEtlParser.g:4153:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            {
            // InternalEtlParser.g:4153:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            // InternalEtlParser.g:4154:3: this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE
            {
            this_SQUARE_OPEN_0=(Token)match(input,RULE_SQUARE_OPEN,FollowSets000.FOLLOW_30); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_SQUARE_OPEN_0, grammarAccess.getCardinalityAccess().getSQUARE_OPENTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4158:3: ( (lv_min_1_0= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:4159:4: (lv_min_1_0= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:4159:4: (lv_min_1_0= ruleNonNegativeInteger )
            // InternalEtlParser.g:4160:5: lv_min_1_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_31);
            lv_min_1_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getCardinalityRule());
              					}
              					set(
              						current,
              						"min",
              						lv_min_1_0,
              						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_TO_2=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_TO_2, grammarAccess.getCardinalityAccess().getTOTerminalRuleCall_2());
              		
            }
            // InternalEtlParser.g:4181:3: ( (lv_max_3_0= ruleMaxValue ) )
            // InternalEtlParser.g:4182:4: (lv_max_3_0= ruleMaxValue )
            {
            // InternalEtlParser.g:4182:4: (lv_max_3_0= ruleMaxValue )
            // InternalEtlParser.g:4183:5: lv_max_3_0= ruleMaxValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_48);
            lv_max_3_0=ruleMaxValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getCardinalityRule());
              					}
              					set(
              						current,
              						"max",
              						lv_max_3_0,
              						"com.b2international.snomed.ecl.Ecl.MaxValue");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_SQUARE_CLOSE_4=(Token)match(input,RULE_SQUARE_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_SQUARE_CLOSE_4, grammarAccess.getCardinalityAccess().getSQUARE_CLOSETerminalRuleCall_4());
              		
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
    // $ANTLR end "ruleCardinality"


    // $ANTLR start "entryRuleComparison"
    // InternalEtlParser.g:4208:1: entryRuleComparison returns [EObject current=null] : iv_ruleComparison= ruleComparison EOF ;
    public final EObject entryRuleComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComparison = null;


        try {
            // InternalEtlParser.g:4208:51: (iv_ruleComparison= ruleComparison EOF )
            // InternalEtlParser.g:4209:2: iv_ruleComparison= ruleComparison EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getComparisonRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleComparison=ruleComparison();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleComparison; 
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
    // $ANTLR end "entryRuleComparison"


    // $ANTLR start "ruleComparison"
    // InternalEtlParser.g:4215:1: ruleComparison returns [EObject current=null] : (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) ;
    public final EObject ruleComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeComparison_0 = null;

        EObject this_DataTypeComparison_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4221:2: ( (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) )
            // InternalEtlParser.g:4222:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            {
            // InternalEtlParser.g:4222:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            int alt71=2;
            switch ( input.LA(1) ) {
            case RULE_EQUAL:
                {
                int LA71_1 = input.LA(2);

                if ( ((LA71_1>=False && LA71_1<=True)||LA71_1==RULE_STRING||LA71_1==RULE_HASH) ) {
                    alt71=2;
                }
                else if ( (LA71_1==RULE_DIGIT_NONZERO||LA71_1==RULE_ROUND_OPEN||LA71_1==RULE_CARET||LA71_1==RULE_WILDCARD||(LA71_1>=RULE_LT && LA71_1<=RULE_GT_EM)) ) {
                    alt71=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 71, 1, input);

                    throw nvae;
                }
                }
                break;
            case RULE_NOT_EQUAL:
                {
                int LA71_2 = input.LA(2);

                if ( ((LA71_2>=False && LA71_2<=True)||LA71_2==RULE_STRING||LA71_2==RULE_HASH) ) {
                    alt71=2;
                }
                else if ( (LA71_2==RULE_DIGIT_NONZERO||LA71_2==RULE_ROUND_OPEN||LA71_2==RULE_CARET||LA71_2==RULE_WILDCARD||(LA71_2>=RULE_LT && LA71_2<=RULE_GT_EM)) ) {
                    alt71=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 71, 2, input);

                    throw nvae;
                }
                }
                break;
            case RULE_LT:
            case RULE_GT:
            case RULE_GTE:
            case RULE_LTE:
                {
                alt71=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // InternalEtlParser.g:4223:3: this_AttributeComparison_0= ruleAttributeComparison
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getComparisonAccess().getAttributeComparisonParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeComparison_0=ruleAttributeComparison();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeComparison_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:4235:3: this_DataTypeComparison_1= ruleDataTypeComparison
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getComparisonAccess().getDataTypeComparisonParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DataTypeComparison_1=ruleDataTypeComparison();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DataTypeComparison_1;
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
    // $ANTLR end "ruleComparison"


    // $ANTLR start "entryRuleAttributeComparison"
    // InternalEtlParser.g:4250:1: entryRuleAttributeComparison returns [EObject current=null] : iv_ruleAttributeComparison= ruleAttributeComparison EOF ;
    public final EObject entryRuleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeComparison = null;


        try {
            // InternalEtlParser.g:4250:60: (iv_ruleAttributeComparison= ruleAttributeComparison EOF )
            // InternalEtlParser.g:4251:2: iv_ruleAttributeComparison= ruleAttributeComparison EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeComparisonRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeComparison=ruleAttributeComparison();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeComparison; 
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
    // $ANTLR end "entryRuleAttributeComparison"


    // $ANTLR start "ruleAttributeComparison"
    // InternalEtlParser.g:4257:1: ruleAttributeComparison returns [EObject current=null] : (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) ;
    public final EObject ruleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeValueEquals_0 = null;

        EObject this_AttributeValueNotEquals_1 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4263:2: ( (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) )
            // InternalEtlParser.g:4264:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            {
            // InternalEtlParser.g:4264:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==RULE_EQUAL) ) {
                alt72=1;
            }
            else if ( (LA72_0==RULE_NOT_EQUAL) ) {
                alt72=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // InternalEtlParser.g:4265:3: this_AttributeValueEquals_0= ruleAttributeValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeComparisonAccess().getAttributeValueEqualsParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeValueEquals_0=ruleAttributeValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeValueEquals_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:4277:3: this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeComparisonAccess().getAttributeValueNotEqualsParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeValueNotEquals_1=ruleAttributeValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeValueNotEquals_1;
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
    // $ANTLR end "ruleAttributeComparison"


    // $ANTLR start "entryRuleDataTypeComparison"
    // InternalEtlParser.g:4292:1: entryRuleDataTypeComparison returns [EObject current=null] : iv_ruleDataTypeComparison= ruleDataTypeComparison EOF ;
    public final EObject entryRuleDataTypeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataTypeComparison = null;


        try {
            // InternalEtlParser.g:4292:59: (iv_ruleDataTypeComparison= ruleDataTypeComparison EOF )
            // InternalEtlParser.g:4293:2: iv_ruleDataTypeComparison= ruleDataTypeComparison EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDataTypeComparisonRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDataTypeComparison=ruleDataTypeComparison();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDataTypeComparison; 
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
    // $ANTLR end "entryRuleDataTypeComparison"


    // $ANTLR start "ruleDataTypeComparison"
    // InternalEtlParser.g:4299:1: ruleDataTypeComparison returns [EObject current=null] : (this_BooleanValueEquals_0= ruleBooleanValueEquals | this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals | this_StringValueEquals_2= ruleStringValueEquals | this_StringValueNotEquals_3= ruleStringValueNotEquals | this_IntegerValueEquals_4= ruleIntegerValueEquals | this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_8= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_10= ruleDecimalValueEquals | this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_14= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals ) ;
    public final EObject ruleDataTypeComparison() throws RecognitionException {
        EObject current = null;

        EObject this_BooleanValueEquals_0 = null;

        EObject this_BooleanValueNotEquals_1 = null;

        EObject this_StringValueEquals_2 = null;

        EObject this_StringValueNotEquals_3 = null;

        EObject this_IntegerValueEquals_4 = null;

        EObject this_IntegerValueNotEquals_5 = null;

        EObject this_IntegerValueGreaterThan_6 = null;

        EObject this_IntegerValueGreaterThanEquals_7 = null;

        EObject this_IntegerValueLessThan_8 = null;

        EObject this_IntegerValueLessThanEquals_9 = null;

        EObject this_DecimalValueEquals_10 = null;

        EObject this_DecimalValueNotEquals_11 = null;

        EObject this_DecimalValueGreaterThan_12 = null;

        EObject this_DecimalValueGreaterThanEquals_13 = null;

        EObject this_DecimalValueLessThan_14 = null;

        EObject this_DecimalValueLessThanEquals_15 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4305:2: ( (this_BooleanValueEquals_0= ruleBooleanValueEquals | this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals | this_StringValueEquals_2= ruleStringValueEquals | this_StringValueNotEquals_3= ruleStringValueNotEquals | this_IntegerValueEquals_4= ruleIntegerValueEquals | this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_8= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_10= ruleDecimalValueEquals | this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_14= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals ) )
            // InternalEtlParser.g:4306:2: (this_BooleanValueEquals_0= ruleBooleanValueEquals | this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals | this_StringValueEquals_2= ruleStringValueEquals | this_StringValueNotEquals_3= ruleStringValueNotEquals | this_IntegerValueEquals_4= ruleIntegerValueEquals | this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_8= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_10= ruleDecimalValueEquals | this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_14= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals )
            {
            // InternalEtlParser.g:4306:2: (this_BooleanValueEquals_0= ruleBooleanValueEquals | this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals | this_StringValueEquals_2= ruleStringValueEquals | this_StringValueNotEquals_3= ruleStringValueNotEquals | this_IntegerValueEquals_4= ruleIntegerValueEquals | this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_8= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_10= ruleDecimalValueEquals | this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_14= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals )
            int alt73=16;
            alt73 = dfa73.predict(input);
            switch (alt73) {
                case 1 :
                    // InternalEtlParser.g:4307:3: this_BooleanValueEquals_0= ruleBooleanValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getBooleanValueEqualsParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_BooleanValueEquals_0=ruleBooleanValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_BooleanValueEquals_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:4319:3: this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getBooleanValueNotEqualsParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_BooleanValueNotEquals_1=ruleBooleanValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_BooleanValueNotEquals_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEtlParser.g:4331:3: this_StringValueEquals_2= ruleStringValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueEqualsParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringValueEquals_2=ruleStringValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringValueEquals_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalEtlParser.g:4343:3: this_StringValueNotEquals_3= ruleStringValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueNotEqualsParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringValueNotEquals_3=ruleStringValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringValueNotEquals_3;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalEtlParser.g:4355:3: this_IntegerValueEquals_4= ruleIntegerValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueEqualsParserRuleCall_4());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueEquals_4=ruleIntegerValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueEquals_4;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 6 :
                    // InternalEtlParser.g:4367:3: this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueNotEqualsParserRuleCall_5());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueNotEquals_5=ruleIntegerValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueNotEquals_5;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalEtlParser.g:4379:3: this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanParserRuleCall_6());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueGreaterThan_6=ruleIntegerValueGreaterThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueGreaterThan_6;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 8 :
                    // InternalEtlParser.g:4391:3: this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanEqualsParserRuleCall_7());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueGreaterThanEquals_7=ruleIntegerValueGreaterThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueGreaterThanEquals_7;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 9 :
                    // InternalEtlParser.g:4403:3: this_IntegerValueLessThan_8= ruleIntegerValueLessThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanParserRuleCall_8());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueLessThan_8=ruleIntegerValueLessThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueLessThan_8;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 10 :
                    // InternalEtlParser.g:4415:3: this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanEqualsParserRuleCall_9());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueLessThanEquals_9=ruleIntegerValueLessThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueLessThanEquals_9;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 11 :
                    // InternalEtlParser.g:4427:3: this_DecimalValueEquals_10= ruleDecimalValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueEqualsParserRuleCall_10());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueEquals_10=ruleDecimalValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueEquals_10;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 12 :
                    // InternalEtlParser.g:4439:3: this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueNotEqualsParserRuleCall_11());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueNotEquals_11=ruleDecimalValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueNotEquals_11;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 13 :
                    // InternalEtlParser.g:4451:3: this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanParserRuleCall_12());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueGreaterThan_12=ruleDecimalValueGreaterThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueGreaterThan_12;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 14 :
                    // InternalEtlParser.g:4463:3: this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanEqualsParserRuleCall_13());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueGreaterThanEquals_13=ruleDecimalValueGreaterThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueGreaterThanEquals_13;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 15 :
                    // InternalEtlParser.g:4475:3: this_DecimalValueLessThan_14= ruleDecimalValueLessThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanParserRuleCall_14());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueLessThan_14=ruleDecimalValueLessThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueLessThan_14;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 16 :
                    // InternalEtlParser.g:4487:3: this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanEqualsParserRuleCall_15());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueLessThanEquals_15=ruleDecimalValueLessThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueLessThanEquals_15;
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
    // $ANTLR end "ruleDataTypeComparison"


    // $ANTLR start "entryRuleAttributeValueEquals"
    // InternalEtlParser.g:4502:1: entryRuleAttributeValueEquals returns [EObject current=null] : iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF ;
    public final EObject entryRuleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueEquals = null;


        try {
            // InternalEtlParser.g:4502:61: (iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF )
            // InternalEtlParser.g:4503:2: iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeValueEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeValueEquals=ruleAttributeValueEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeValueEquals; 
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
    // $ANTLR end "entryRuleAttributeValueEquals"


    // $ANTLR start "ruleAttributeValueEquals"
    // InternalEtlParser.g:4509:1: ruleAttributeValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4515:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) )
            // InternalEtlParser.g:4516:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            {
            // InternalEtlParser.g:4516:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            // InternalEtlParser.g:4517:3: this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4521:3: ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            // InternalEtlParser.g:4522:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            {
            // InternalEtlParser.g:4522:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            // InternalEtlParser.g:4523:5: lv_constraint_1_0= ruleSubExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSubExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeValueEqualsRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
    // $ANTLR end "ruleAttributeValueEquals"


    // $ANTLR start "entryRuleAttributeValueNotEquals"
    // InternalEtlParser.g:4544:1: entryRuleAttributeValueNotEquals returns [EObject current=null] : iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF ;
    public final EObject entryRuleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueNotEquals = null;


        try {
            // InternalEtlParser.g:4544:64: (iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF )
            // InternalEtlParser.g:4545:2: iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeValueNotEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeValueNotEquals=ruleAttributeValueNotEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeValueNotEquals; 
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
    // $ANTLR end "entryRuleAttributeValueNotEquals"


    // $ANTLR start "ruleAttributeValueNotEquals"
    // InternalEtlParser.g:4551:1: ruleAttributeValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4557:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) )
            // InternalEtlParser.g:4558:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            {
            // InternalEtlParser.g:4558:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            // InternalEtlParser.g:4559:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4563:3: ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            // InternalEtlParser.g:4564:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            {
            // InternalEtlParser.g:4564:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            // InternalEtlParser.g:4565:5: lv_constraint_1_0= ruleSubExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSubExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeValueNotEqualsRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
    // $ANTLR end "ruleAttributeValueNotEquals"


    // $ANTLR start "entryRuleBooleanValueEquals"
    // InternalEtlParser.g:4586:1: entryRuleBooleanValueEquals returns [EObject current=null] : iv_ruleBooleanValueEquals= ruleBooleanValueEquals EOF ;
    public final EObject entryRuleBooleanValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBooleanValueEquals = null;


        try {
            // InternalEtlParser.g:4586:59: (iv_ruleBooleanValueEquals= ruleBooleanValueEquals EOF )
            // InternalEtlParser.g:4587:2: iv_ruleBooleanValueEquals= ruleBooleanValueEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBooleanValueEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBooleanValueEquals=ruleBooleanValueEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBooleanValueEquals; 
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
    // $ANTLR end "entryRuleBooleanValueEquals"


    // $ANTLR start "ruleBooleanValueEquals"
    // InternalEtlParser.g:4593:1: ruleBooleanValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= ruleBoolean ) ) ) ;
    public final EObject ruleBooleanValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4599:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= ruleBoolean ) ) ) )
            // InternalEtlParser.g:4600:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= ruleBoolean ) ) )
            {
            // InternalEtlParser.g:4600:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= ruleBoolean ) ) )
            // InternalEtlParser.g:4601:3: this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= ruleBoolean ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_49); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getBooleanValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4605:3: ( (lv_value_1_0= ruleBoolean ) )
            // InternalEtlParser.g:4606:4: (lv_value_1_0= ruleBoolean )
            {
            // InternalEtlParser.g:4606:4: (lv_value_1_0= ruleBoolean )
            // InternalEtlParser.g:4607:5: lv_value_1_0= ruleBoolean
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getBooleanValueEqualsAccess().getValueBooleanParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleBoolean();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getBooleanValueEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.Boolean");
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
    // $ANTLR end "ruleBooleanValueEquals"


    // $ANTLR start "entryRuleBooleanValueNotEquals"
    // InternalEtlParser.g:4628:1: entryRuleBooleanValueNotEquals returns [EObject current=null] : iv_ruleBooleanValueNotEquals= ruleBooleanValueNotEquals EOF ;
    public final EObject entryRuleBooleanValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBooleanValueNotEquals = null;


        try {
            // InternalEtlParser.g:4628:62: (iv_ruleBooleanValueNotEquals= ruleBooleanValueNotEquals EOF )
            // InternalEtlParser.g:4629:2: iv_ruleBooleanValueNotEquals= ruleBooleanValueNotEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBooleanValueNotEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBooleanValueNotEquals=ruleBooleanValueNotEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBooleanValueNotEquals; 
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
    // $ANTLR end "entryRuleBooleanValueNotEquals"


    // $ANTLR start "ruleBooleanValueNotEquals"
    // InternalEtlParser.g:4635:1: ruleBooleanValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= ruleBoolean ) ) ) ;
    public final EObject ruleBooleanValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        AntlrDatatypeRuleToken lv_value_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4641:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= ruleBoolean ) ) ) )
            // InternalEtlParser.g:4642:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= ruleBoolean ) ) )
            {
            // InternalEtlParser.g:4642:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= ruleBoolean ) ) )
            // InternalEtlParser.g:4643:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= ruleBoolean ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_49); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getBooleanValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4647:3: ( (lv_value_1_0= ruleBoolean ) )
            // InternalEtlParser.g:4648:4: (lv_value_1_0= ruleBoolean )
            {
            // InternalEtlParser.g:4648:4: (lv_value_1_0= ruleBoolean )
            // InternalEtlParser.g:4649:5: lv_value_1_0= ruleBoolean
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getBooleanValueNotEqualsAccess().getValueBooleanParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleBoolean();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getBooleanValueNotEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.Boolean");
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
    // $ANTLR end "ruleBooleanValueNotEquals"


    // $ANTLR start "entryRuleStringValueEquals"
    // InternalEtlParser.g:4670:1: entryRuleStringValueEquals returns [EObject current=null] : iv_ruleStringValueEquals= ruleStringValueEquals EOF ;
    public final EObject entryRuleStringValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueEquals = null;


        try {
            // InternalEtlParser.g:4670:58: (iv_ruleStringValueEquals= ruleStringValueEquals EOF )
            // InternalEtlParser.g:4671:2: iv_ruleStringValueEquals= ruleStringValueEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStringValueEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringValueEquals=ruleStringValueEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStringValueEquals; 
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
    // $ANTLR end "entryRuleStringValueEquals"


    // $ANTLR start "ruleStringValueEquals"
    // InternalEtlParser.g:4677:1: ruleStringValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalEtlParser.g:4683:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalEtlParser.g:4684:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalEtlParser.g:4684:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalEtlParser.g:4685:3: this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getStringValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4689:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalEtlParser.g:4690:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalEtlParser.g:4690:4: (lv_value_1_0= RULE_STRING )
            // InternalEtlParser.g:4691:5: lv_value_1_0= RULE_STRING
            {
            lv_value_1_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(lv_value_1_0, grammarAccess.getStringValueEqualsAccess().getValueSTRINGTerminalRuleCall_1_0());
              				
            }
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getStringValueEqualsRule());
              					}
              					setWithLastConsumed(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.STRING");
              				
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
    // $ANTLR end "ruleStringValueEquals"


    // $ANTLR start "entryRuleStringValueNotEquals"
    // InternalEtlParser.g:4711:1: entryRuleStringValueNotEquals returns [EObject current=null] : iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF ;
    public final EObject entryRuleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueNotEquals = null;


        try {
            // InternalEtlParser.g:4711:61: (iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF )
            // InternalEtlParser.g:4712:2: iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStringValueNotEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringValueNotEquals=ruleStringValueNotEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStringValueNotEquals; 
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
    // $ANTLR end "entryRuleStringValueNotEquals"


    // $ANTLR start "ruleStringValueNotEquals"
    // InternalEtlParser.g:4718:1: ruleStringValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalEtlParser.g:4724:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalEtlParser.g:4725:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalEtlParser.g:4725:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalEtlParser.g:4726:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getStringValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:4730:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalEtlParser.g:4731:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalEtlParser.g:4731:4: (lv_value_1_0= RULE_STRING )
            // InternalEtlParser.g:4732:5: lv_value_1_0= RULE_STRING
            {
            lv_value_1_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(lv_value_1_0, grammarAccess.getStringValueNotEqualsAccess().getValueSTRINGTerminalRuleCall_1_0());
              				
            }
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getStringValueNotEqualsRule());
              					}
              					setWithLastConsumed(
              						current,
              						"value",
              						lv_value_1_0,
              						"com.b2international.snomed.ecl.Ecl.STRING");
              				
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
    // $ANTLR end "ruleStringValueNotEquals"


    // $ANTLR start "entryRuleIntegerValueEquals"
    // InternalEtlParser.g:4752:1: entryRuleIntegerValueEquals returns [EObject current=null] : iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF ;
    public final EObject entryRuleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueEquals = null;


        try {
            // InternalEtlParser.g:4752:59: (iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF )
            // InternalEtlParser.g:4753:2: iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueEquals=ruleIntegerValueEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueEquals; 
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
    // $ANTLR end "entryRuleIntegerValueEquals"


    // $ANTLR start "ruleIntegerValueEquals"
    // InternalEtlParser.g:4759:1: ruleIntegerValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4765:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4766:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4766:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4767:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getIntegerValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:4775:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:4776:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:4776:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:4777:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueEqualsAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueEquals"


    // $ANTLR start "entryRuleIntegerValueNotEquals"
    // InternalEtlParser.g:4798:1: entryRuleIntegerValueNotEquals returns [EObject current=null] : iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF ;
    public final EObject entryRuleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueNotEquals = null;


        try {
            // InternalEtlParser.g:4798:62: (iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF )
            // InternalEtlParser.g:4799:2: iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueNotEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueNotEquals=ruleIntegerValueNotEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueNotEquals; 
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
    // $ANTLR end "entryRuleIntegerValueNotEquals"


    // $ANTLR start "ruleIntegerValueNotEquals"
    // InternalEtlParser.g:4805:1: ruleIntegerValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4811:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4812:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4812:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4813:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getIntegerValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:4821:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:4822:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:4822:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:4823:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueNotEqualsAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueNotEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueNotEquals"


    // $ANTLR start "entryRuleIntegerValueGreaterThan"
    // InternalEtlParser.g:4844:1: entryRuleIntegerValueGreaterThan returns [EObject current=null] : iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF ;
    public final EObject entryRuleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThan = null;


        try {
            // InternalEtlParser.g:4844:64: (iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF )
            // InternalEtlParser.g:4845:2: iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueGreaterThanRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueGreaterThan=ruleIntegerValueGreaterThan();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueGreaterThan; 
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
    // $ANTLR end "entryRuleIntegerValueGreaterThan"


    // $ANTLR start "ruleIntegerValueGreaterThan"
    // InternalEtlParser.g:4851:1: ruleIntegerValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4857:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4858:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4858:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4859:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getIntegerValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:4867:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:4868:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:4868:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:4869:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueGreaterThanAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueGreaterThanRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueGreaterThan"


    // $ANTLR start "entryRuleIntegerValueLessThan"
    // InternalEtlParser.g:4890:1: entryRuleIntegerValueLessThan returns [EObject current=null] : iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF ;
    public final EObject entryRuleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThan = null;


        try {
            // InternalEtlParser.g:4890:61: (iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF )
            // InternalEtlParser.g:4891:2: iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueLessThanRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueLessThan=ruleIntegerValueLessThan();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueLessThan; 
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
    // $ANTLR end "entryRuleIntegerValueLessThan"


    // $ANTLR start "ruleIntegerValueLessThan"
    // InternalEtlParser.g:4897:1: ruleIntegerValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4903:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4904:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4904:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4905:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getIntegerValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:4913:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:4914:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:4914:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:4915:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueLessThanAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueLessThanRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueLessThan"


    // $ANTLR start "entryRuleIntegerValueGreaterThanEquals"
    // InternalEtlParser.g:4936:1: entryRuleIntegerValueGreaterThanEquals returns [EObject current=null] : iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF ;
    public final EObject entryRuleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThanEquals = null;


        try {
            // InternalEtlParser.g:4936:70: (iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF )
            // InternalEtlParser.g:4937:2: iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueGreaterThanEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueGreaterThanEquals=ruleIntegerValueGreaterThanEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueGreaterThanEquals; 
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
    // $ANTLR end "entryRuleIntegerValueGreaterThanEquals"


    // $ANTLR start "ruleIntegerValueGreaterThanEquals"
    // InternalEtlParser.g:4943:1: ruleIntegerValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4949:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4950:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4950:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4951:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:4959:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:4960:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:4960:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:4961:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueGreaterThanEqualsAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueGreaterThanEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueGreaterThanEquals"


    // $ANTLR start "entryRuleIntegerValueLessThanEquals"
    // InternalEtlParser.g:4982:1: entryRuleIntegerValueLessThanEquals returns [EObject current=null] : iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF ;
    public final EObject entryRuleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThanEquals = null;


        try {
            // InternalEtlParser.g:4982:67: (iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF )
            // InternalEtlParser.g:4983:2: iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIntegerValueLessThanEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerValueLessThanEquals=ruleIntegerValueLessThanEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIntegerValueLessThanEquals; 
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
    // $ANTLR end "entryRuleIntegerValueLessThanEquals"


    // $ANTLR start "ruleIntegerValueLessThanEquals"
    // InternalEtlParser.g:4989:1: ruleIntegerValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:4995:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEtlParser.g:4996:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEtlParser.g:4996:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEtlParser.g:4997:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getIntegerValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5005:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEtlParser.g:5006:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEtlParser.g:5006:4: (lv_value_2_0= ruleInteger )
            // InternalEtlParser.g:5007:5: lv_value_2_0= ruleInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getIntegerValueLessThanEqualsAccess().getValueIntegerParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getIntegerValueLessThanEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Integer");
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
    // $ANTLR end "ruleIntegerValueLessThanEquals"


    // $ANTLR start "entryRuleDecimalValueEquals"
    // InternalEtlParser.g:5028:1: entryRuleDecimalValueEquals returns [EObject current=null] : iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF ;
    public final EObject entryRuleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueEquals = null;


        try {
            // InternalEtlParser.g:5028:59: (iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF )
            // InternalEtlParser.g:5029:2: iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueEquals=ruleDecimalValueEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueEquals; 
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
    // $ANTLR end "entryRuleDecimalValueEquals"


    // $ANTLR start "ruleDecimalValueEquals"
    // InternalEtlParser.g:5035:1: ruleDecimalValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5041:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5042:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5042:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5043:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getDecimalValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5051:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5052:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5052:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5053:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueEqualsAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueEquals"


    // $ANTLR start "entryRuleDecimalValueNotEquals"
    // InternalEtlParser.g:5074:1: entryRuleDecimalValueNotEquals returns [EObject current=null] : iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF ;
    public final EObject entryRuleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueNotEquals = null;


        try {
            // InternalEtlParser.g:5074:62: (iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF )
            // InternalEtlParser.g:5075:2: iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueNotEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueNotEquals=ruleDecimalValueNotEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueNotEquals; 
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
    // $ANTLR end "entryRuleDecimalValueNotEquals"


    // $ANTLR start "ruleDecimalValueNotEquals"
    // InternalEtlParser.g:5081:1: ruleDecimalValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5087:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5088:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5088:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5089:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getDecimalValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5097:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5098:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5098:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5099:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueNotEqualsAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueNotEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueNotEquals"


    // $ANTLR start "entryRuleDecimalValueGreaterThan"
    // InternalEtlParser.g:5120:1: entryRuleDecimalValueGreaterThan returns [EObject current=null] : iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF ;
    public final EObject entryRuleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThan = null;


        try {
            // InternalEtlParser.g:5120:64: (iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF )
            // InternalEtlParser.g:5121:2: iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueGreaterThanRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueGreaterThan=ruleDecimalValueGreaterThan();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueGreaterThan; 
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
    // $ANTLR end "entryRuleDecimalValueGreaterThan"


    // $ANTLR start "ruleDecimalValueGreaterThan"
    // InternalEtlParser.g:5127:1: ruleDecimalValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5133:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5134:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5134:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5135:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getDecimalValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5143:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5144:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5144:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5145:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueGreaterThanAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueGreaterThanRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueGreaterThan"


    // $ANTLR start "entryRuleDecimalValueLessThan"
    // InternalEtlParser.g:5166:1: entryRuleDecimalValueLessThan returns [EObject current=null] : iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF ;
    public final EObject entryRuleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThan = null;


        try {
            // InternalEtlParser.g:5166:61: (iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF )
            // InternalEtlParser.g:5167:2: iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueLessThanRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueLessThan=ruleDecimalValueLessThan();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueLessThan; 
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
    // $ANTLR end "entryRuleDecimalValueLessThan"


    // $ANTLR start "ruleDecimalValueLessThan"
    // InternalEtlParser.g:5173:1: ruleDecimalValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5179:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5180:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5180:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5181:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDecimalValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5189:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5190:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5190:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5191:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueLessThanAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueLessThanRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueLessThan"


    // $ANTLR start "entryRuleDecimalValueGreaterThanEquals"
    // InternalEtlParser.g:5212:1: entryRuleDecimalValueGreaterThanEquals returns [EObject current=null] : iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF ;
    public final EObject entryRuleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThanEquals = null;


        try {
            // InternalEtlParser.g:5212:70: (iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF )
            // InternalEtlParser.g:5213:2: iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueGreaterThanEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueGreaterThanEquals=ruleDecimalValueGreaterThanEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueGreaterThanEquals; 
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
    // $ANTLR end "entryRuleDecimalValueGreaterThanEquals"


    // $ANTLR start "ruleDecimalValueGreaterThanEquals"
    // InternalEtlParser.g:5219:1: ruleDecimalValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5225:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5226:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5226:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5227:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5235:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5236:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5236:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5237:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueGreaterThanEqualsAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueGreaterThanEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueGreaterThanEquals"


    // $ANTLR start "entryRuleDecimalValueLessThanEquals"
    // InternalEtlParser.g:5258:1: entryRuleDecimalValueLessThanEquals returns [EObject current=null] : iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF ;
    public final EObject entryRuleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThanEquals = null;


        try {
            // InternalEtlParser.g:5258:67: (iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF )
            // InternalEtlParser.g:5259:2: iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDecimalValueLessThanEqualsRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDecimalValueLessThanEquals=ruleDecimalValueLessThanEquals();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDecimalValueLessThanEquals; 
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
    // $ANTLR end "entryRuleDecimalValueLessThanEquals"


    // $ANTLR start "ruleDecimalValueLessThanEquals"
    // InternalEtlParser.g:5265:1: ruleDecimalValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5271:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEtlParser.g:5272:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEtlParser.g:5272:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEtlParser.g:5273:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_37); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getDecimalValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5281:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEtlParser.g:5282:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEtlParser.g:5282:4: (lv_value_2_0= ruleDecimal )
            // InternalEtlParser.g:5283:5: lv_value_2_0= ruleDecimal
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDecimalValueLessThanEqualsAccess().getValueDecimalParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_2_0=ruleDecimal();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getDecimalValueLessThanEqualsRule());
              					}
              					set(
              						current,
              						"value",
              						lv_value_2_0,
              						"com.b2international.snomed.ecl.Ecl.Decimal");
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
    // $ANTLR end "ruleDecimalValueLessThanEquals"


    // $ANTLR start "entryRuleNestedExpression"
    // InternalEtlParser.g:5304:1: entryRuleNestedExpression returns [EObject current=null] : iv_ruleNestedExpression= ruleNestedExpression EOF ;
    public final EObject entryRuleNestedExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedExpression = null;


        try {
            // InternalEtlParser.g:5304:57: (iv_ruleNestedExpression= ruleNestedExpression EOF )
            // InternalEtlParser.g:5305:2: iv_ruleNestedExpression= ruleNestedExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNestedExpressionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNestedExpression=ruleNestedExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNestedExpression; 
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
    // $ANTLR end "entryRuleNestedExpression"


    // $ANTLR start "ruleNestedExpression"
    // InternalEtlParser.g:5311:1: ruleNestedExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEtlParser.g:5317:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEtlParser.g:5318:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEtlParser.g:5318:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEtlParser.g:5319:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedExpressionAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:5323:3: ( (lv_nested_1_0= ruleExpressionConstraint ) )
            // InternalEtlParser.g:5324:4: (lv_nested_1_0= ruleExpressionConstraint )
            {
            // InternalEtlParser.g:5324:4: (lv_nested_1_0= ruleExpressionConstraint )
            // InternalEtlParser.g:5325:5: lv_nested_1_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_11);
            lv_nested_1_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getNestedExpressionRule());
              					}
              					set(
              						current,
              						"nested",
              						lv_nested_1_0,
              						"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedExpressionAccess().getROUND_CLOSETerminalRuleCall_2());
              		
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
    // $ANTLR end "ruleNestedExpression"


    // $ANTLR start "entryRuleSnomedIdentifier"
    // InternalEtlParser.g:5350:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5352:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // InternalEtlParser.g:5353:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
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
    // InternalEtlParser.g:5362:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // InternalEtlParser.g:5369:2: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // InternalEtlParser.g:5370:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // InternalEtlParser.g:5370:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // InternalEtlParser.g:5371:3: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DIGIT_NONZERO_0);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0());
              		
            }
            // InternalEtlParser.g:5378:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==RULE_DIGIT_NONZERO) ) {
                alt74=1;
            }
            else if ( (LA74_0==RULE_ZERO) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // InternalEtlParser.g:5379:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5387:4: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_2);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:5395:3: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==RULE_DIGIT_NONZERO) ) {
                alt75=1;
            }
            else if ( (LA75_0==RULE_ZERO) ) {
                alt75=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // InternalEtlParser.g:5396:4: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_3);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5404:4: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_4);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:5412:3: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==RULE_DIGIT_NONZERO) ) {
                alt76=1;
            }
            else if ( (LA76_0==RULE_ZERO) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // InternalEtlParser.g:5413:4: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_5);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5421:4: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_6);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:5429:3: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==RULE_DIGIT_NONZERO) ) {
                alt77=1;
            }
            else if ( (LA77_0==RULE_ZERO) ) {
                alt77=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // InternalEtlParser.g:5430:4: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_7);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5438:4: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_50); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_8);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEtlParser.g:5446:3: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt78=0;
            loop78:
            do {
                int alt78=3;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==RULE_DIGIT_NONZERO) ) {
                    alt78=1;
                }
                else if ( (LA78_0==RULE_ZERO) ) {
                    alt78=2;
                }


                switch (alt78) {
            	case 1 :
            	    // InternalEtlParser.g:5447:4: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_9);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalEtlParser.g:5455:4: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_10);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt78 >= 1 ) break loop78;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(78, input);
                        throw eee;
                }
                cnt78++;
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
    // InternalEtlParser.g:5470:1: entryRuleNonNegativeInteger returns [String current=null] : iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF ;
    public final String entryRuleNonNegativeInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5472:2: (iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF )
            // InternalEtlParser.g:5473:2: iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF
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
    // InternalEtlParser.g:5482:1: ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5489:2: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) )
            // InternalEtlParser.g:5490:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            {
            // InternalEtlParser.g:5490:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==RULE_ZERO) ) {
                alt80=1;
            }
            else if ( (LA80_0==RULE_DIGIT_NONZERO) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // InternalEtlParser.g:5491:3: this_ZERO_0= RULE_ZERO
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
                    // InternalEtlParser.g:5499:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // InternalEtlParser.g:5499:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // InternalEtlParser.g:5500:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }
                    // InternalEtlParser.g:5507:4: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    loop79:
                    do {
                        int alt79=3;
                        int LA79_0 = input.LA(1);

                        if ( (LA79_0==RULE_DIGIT_NONZERO) ) {
                            alt79=1;
                        }
                        else if ( (LA79_0==RULE_ZERO) ) {
                            alt79=2;
                        }


                        switch (alt79) {
                    	case 1 :
                    	    // InternalEtlParser.g:5508:5: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_DIGIT_NONZERO_2);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0());
                    	      				
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEtlParser.g:5516:5: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_ZERO_3);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1());
                    	      				
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop79;
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


    // $ANTLR start "entryRuleMaxValue"
    // InternalEtlParser.g:5532:1: entryRuleMaxValue returns [String current=null] : iv_ruleMaxValue= ruleMaxValue EOF ;
    public final String entryRuleMaxValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMaxValue = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5534:2: (iv_ruleMaxValue= ruleMaxValue EOF )
            // InternalEtlParser.g:5535:2: iv_ruleMaxValue= ruleMaxValue EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMaxValueRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleMaxValue=ruleMaxValue();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMaxValue.getText(); 
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
    // $ANTLR end "entryRuleMaxValue"


    // $ANTLR start "ruleMaxValue"
    // InternalEtlParser.g:5544:1: ruleMaxValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) ;
    public final AntlrDatatypeRuleToken ruleMaxValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WILDCARD_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5551:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) )
            // InternalEtlParser.g:5552:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            {
            // InternalEtlParser.g:5552:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( ((LA81_0>=RULE_ZERO && LA81_0<=RULE_DIGIT_NONZERO)) ) {
                alt81=1;
            }
            else if ( (LA81_0==RULE_WILDCARD) ) {
                alt81=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // InternalEtlParser.g:5553:3: this_NonNegativeInteger_0= ruleNonNegativeInteger
                    {
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getMaxValueAccess().getNonNegativeIntegerParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NonNegativeInteger_0=ruleNonNegativeInteger();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_NonNegativeInteger_0);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5564:3: this_WILDCARD_1= RULE_WILDCARD
                    {
                    this_WILDCARD_1=(Token)match(input,RULE_WILDCARD,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(this_WILDCARD_1);
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newLeafNode(this_WILDCARD_1, grammarAccess.getMaxValueAccess().getWILDCARDTerminalRuleCall_1());
                      		
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
    // $ANTLR end "ruleMaxValue"


    // $ANTLR start "entryRuleInteger"
    // InternalEtlParser.g:5578:1: entryRuleInteger returns [String current=null] : iv_ruleInteger= ruleInteger EOF ;
    public final String entryRuleInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5580:2: (iv_ruleInteger= ruleInteger EOF )
            // InternalEtlParser.g:5581:2: iv_ruleInteger= ruleInteger EOF
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
    // InternalEtlParser.g:5590:1: ruleInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) ;
    public final AntlrDatatypeRuleToken ruleInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5597:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) )
            // InternalEtlParser.g:5598:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            {
            // InternalEtlParser.g:5598:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            // InternalEtlParser.g:5599:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger
            {
            // InternalEtlParser.g:5599:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt82=3;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==RULE_PLUS) ) {
                alt82=1;
            }
            else if ( (LA82_0==RULE_DASH) ) {
                alt82=2;
            }
            switch (alt82) {
                case 1 :
                    // InternalEtlParser.g:5600:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_30); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getIntegerAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5608:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_30); if (state.failed) return current;
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
    // InternalEtlParser.g:5633:1: entryRuleDecimal returns [String current=null] : iv_ruleDecimal= ruleDecimal EOF ;
    public final String entryRuleDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5635:2: (iv_ruleDecimal= ruleDecimal EOF )
            // InternalEtlParser.g:5636:2: iv_ruleDecimal= ruleDecimal EOF
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
    // InternalEtlParser.g:5645:1: ruleDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) ;
    public final AntlrDatatypeRuleToken ruleDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeDecimal_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5652:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) )
            // InternalEtlParser.g:5653:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            {
            // InternalEtlParser.g:5653:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            // InternalEtlParser.g:5654:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal
            {
            // InternalEtlParser.g:5654:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt83=3;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==RULE_PLUS) ) {
                alt83=1;
            }
            else if ( (LA83_0==RULE_DASH) ) {
                alt83=2;
            }
            switch (alt83) {
                case 1 :
                    // InternalEtlParser.g:5655:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_34); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getDecimalAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5663:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_34); if (state.failed) return current;
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
    // InternalEtlParser.g:5688:1: entryRuleNonNegativeDecimal returns [String current=null] : iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF ;
    public final String entryRuleNonNegativeDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5690:2: (iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF )
            // InternalEtlParser.g:5691:2: iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF
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
    // InternalEtlParser.g:5700:1: ruleNonNegativeDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DOT_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5707:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            // InternalEtlParser.g:5708:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            {
            // InternalEtlParser.g:5708:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            // InternalEtlParser.g:5709:3: this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getNonNegativeDecimalAccess().getNonNegativeIntegerParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_52);
            this_NonNegativeInteger_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeInteger_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_51); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DOT_1);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOT_1, grammarAccess.getNonNegativeDecimalAccess().getDOTTerminalRuleCall_1());
              		
            }
            // InternalEtlParser.g:5726:3: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            loop84:
            do {
                int alt84=3;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==RULE_DIGIT_NONZERO) ) {
                    alt84=1;
                }
                else if ( (LA84_0==RULE_ZERO) ) {
                    alt84=2;
                }


                switch (alt84) {
            	case 1 :
            	    // InternalEtlParser.g:5727:4: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_2);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeDecimalAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalEtlParser.g:5735:4: this_ZERO_3= RULE_ZERO
            	    {
            	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_51); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_3);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeDecimalAccess().getZEROTerminalRuleCall_2_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop84;
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


    // $ANTLR start "entryRuleBoolean"
    // InternalEtlParser.g:5750:1: entryRuleBoolean returns [String current=null] : iv_ruleBoolean= ruleBoolean EOF ;
    public final String entryRuleBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBoolean = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5752:2: (iv_ruleBoolean= ruleBoolean EOF )
            // InternalEtlParser.g:5753:2: iv_ruleBoolean= ruleBoolean EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBooleanRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBoolean=ruleBoolean();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBoolean.getText(); 
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
    // $ANTLR end "entryRuleBoolean"


    // $ANTLR start "ruleBoolean"
    // InternalEtlParser.g:5762:1: ruleBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= True | kw= False ) ;
    public final AntlrDatatypeRuleToken ruleBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEtlParser.g:5769:2: ( (kw= True | kw= False ) )
            // InternalEtlParser.g:5770:2: (kw= True | kw= False )
            {
            // InternalEtlParser.g:5770:2: (kw= True | kw= False )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==True) ) {
                alt85=1;
            }
            else if ( (LA85_0==False) ) {
                alt85=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // InternalEtlParser.g:5771:3: kw= True
                    {
                    kw=(Token)match(input,True,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getBooleanAccess().getTrueKeyword_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEtlParser.g:5777:3: kw= False
                    {
                    kw=(Token)match(input,False,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getBooleanAccess().getFalseKeyword_1());
                      		
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
    // $ANTLR end "ruleBoolean"

    // $ANTLR start synpred62_InternalEtlParser
    public final void synpred62_InternalEtlParser_fragment() throws RecognitionException {   
        EObject this_SlotIntegerRange_0 = null;


        // InternalEtlParser.g:1940:3: (this_SlotIntegerRange_0= ruleSlotIntegerRange )
        // InternalEtlParser.g:1940:3: this_SlotIntegerRange_0= ruleSlotIntegerRange
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_SlotIntegerRange_0=ruleSlotIntegerRange();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred62_InternalEtlParser

    // $ANTLR start synpred63_InternalEtlParser
    public final void synpred63_InternalEtlParser_fragment() throws RecognitionException {   
        EObject lv_maximum_2_0 = null;


        // InternalEtlParser.g:2049:5: ( (lv_maximum_2_0= ruleSlotIntegerMaximumValue ) )
        // InternalEtlParser.g:2049:5: (lv_maximum_2_0= ruleSlotIntegerMaximumValue )
        {
        // InternalEtlParser.g:2049:5: (lv_maximum_2_0= ruleSlotIntegerMaximumValue )
        // InternalEtlParser.g:2050:6: lv_maximum_2_0= ruleSlotIntegerMaximumValue
        {
        if ( state.backtracking==0 ) {

          						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0());
          					
        }
        pushFollow(FollowSets000.FOLLOW_2);
        lv_maximum_2_0=ruleSlotIntegerMaximumValue();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred63_InternalEtlParser

    // $ANTLR start synpred67_InternalEtlParser
    public final void synpred67_InternalEtlParser_fragment() throws RecognitionException {   
        EObject this_SlotDecimalRange_0 = null;


        // InternalEtlParser.g:2233:3: (this_SlotDecimalRange_0= ruleSlotDecimalRange )
        // InternalEtlParser.g:2233:3: this_SlotDecimalRange_0= ruleSlotDecimalRange
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_SlotDecimalRange_0=ruleSlotDecimalRange();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred67_InternalEtlParser

    // $ANTLR start synpred68_InternalEtlParser
    public final void synpred68_InternalEtlParser_fragment() throws RecognitionException {   
        EObject lv_maximum_2_0 = null;


        // InternalEtlParser.g:2342:5: ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )
        // InternalEtlParser.g:2342:5: (lv_maximum_2_0= ruleSlotDecimalMaximumValue )
        {
        // InternalEtlParser.g:2342:5: (lv_maximum_2_0= ruleSlotDecimalMaximumValue )
        // InternalEtlParser.g:2343:6: lv_maximum_2_0= ruleSlotDecimalMaximumValue
        {
        if ( state.backtracking==0 ) {

          						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0());
          					
        }
        pushFollow(FollowSets000.FOLLOW_2);
        lv_maximum_2_0=ruleSlotDecimalMaximumValue();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred68_InternalEtlParser

    // $ANTLR start synpred90_InternalEtlParser
    public final void synpred90_InternalEtlParser_fragment() throws RecognitionException {   
        // InternalEtlParser.g:3511:4: ( RULE_DISJUNCTION )
        // InternalEtlParser.g:3511:5: RULE_DISJUNCTION
        {
        match(input,RULE_DISJUNCTION,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred90_InternalEtlParser

    // $ANTLR start synpred92_InternalEtlParser
    public final void synpred92_InternalEtlParser_fragment() throws RecognitionException {   
        // InternalEtlParser.g:3579:4: ( RULE_CONJUNCTION | RULE_COMMA )
        // InternalEtlParser.g:
        {
        if ( (input.LA(1)>=RULE_COMMA && input.LA(1)<=RULE_CONJUNCTION) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }
    }
    // $ANTLR end synpred92_InternalEtlParser

    // $ANTLR start synpred94_InternalEtlParser
    public final void synpred94_InternalEtlParser_fragment() throws RecognitionException {   
        EObject this_AttributeConstraint_0 = null;


        // InternalEtlParser.g:3642:3: (this_AttributeConstraint_0= ruleAttributeConstraint )
        // InternalEtlParser.g:3642:3: this_AttributeConstraint_0= ruleAttributeConstraint
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_AttributeConstraint_0=ruleAttributeConstraint();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred94_InternalEtlParser

    // $ANTLR start synpred95_InternalEtlParser
    public final void synpred95_InternalEtlParser_fragment() throws RecognitionException {   
        EObject this_EclAttributeGroup_1 = null;


        // InternalEtlParser.g:3654:3: (this_EclAttributeGroup_1= ruleEclAttributeGroup )
        // InternalEtlParser.g:3654:3: this_EclAttributeGroup_1= ruleEclAttributeGroup
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_EclAttributeGroup_1=ruleEclAttributeGroup();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred95_InternalEtlParser

    // $ANTLR start synpred100_InternalEtlParser
    public final void synpred100_InternalEtlParser_fragment() throws RecognitionException {   
        EObject this_AttributeConstraint_0 = null;


        // InternalEtlParser.g:3972:3: (this_AttributeConstraint_0= ruleAttributeConstraint )
        // InternalEtlParser.g:3972:3: this_AttributeConstraint_0= ruleAttributeConstraint
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_AttributeConstraint_0=ruleAttributeConstraint();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred100_InternalEtlParser

    // Delegated rules

    public final boolean synpred90_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred90_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred95_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred95_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred68_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred68_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred92_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred92_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred67_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred67_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred62_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred62_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred94_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred94_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred100_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred100_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred63_InternalEtlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred63_InternalEtlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA68 dfa68 = new DFA68(this);
    protected DFA73 dfa73 = new DFA73(this);
    static final String dfa_1s = "\21\uffff";
    static final String dfa_2s = "\1\6\1\7\2\uffff\1\36\2\31\1\7\1\6\1\36\2\31\5\7";
    static final String dfa_3s = "\1\41\1\45\2\uffff\1\37\1\31\1\37\1\7\1\41\1\51\2\37\1\26\1\37\1\26\2\37";
    static final String dfa_4s = "\2\uffff\1\1\1\2\15\uffff";
    static final String dfa_5s = "\21\uffff}>";
    static final String[] dfa_6s = {
            "\1\1\30\uffff\1\2\1\uffff\1\3",
            "\1\10\1\4\15\uffff\1\7\7\uffff\1\5\1\6\5\uffff\1\2",
            "",
            "",
            "\1\5\1\6",
            "\1\11",
            "\1\11\4\uffff\1\13\1\12",
            "\1\10",
            "\1\2\30\uffff\1\2\1\uffff\1\3",
            "\1\14\1\15\11\uffff\1\16",
            "\1\11\4\uffff\1\13\1\12",
            "\1\11\4\uffff\1\13\1\12",
            "\1\10\16\uffff\1\7",
            "\1\10\16\uffff\1\7\7\uffff\1\20\1\17",
            "\1\10\16\uffff\1\7",
            "\1\10\16\uffff\1\7\7\uffff\1\20\1\17",
            "\1\10\16\uffff\1\7\7\uffff\1\20\1\17"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "332:3: ( ( ( (lv_attributes_0_0= ruleAttribute ) ) (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )* ) | ( (lv_groups_3_0= ruleAttributeGroup ) ) )";
        }
    }
    static final String dfa_7s = "\22\uffff";
    static final String dfa_8s = "\1\2\21\uffff";
    static final String dfa_9s = "\2\6\1\uffff\1\7\1\uffff\1\36\2\31\1\7\1\6\1\36\2\31\5\7";
    static final String dfa_10s = "\1\44\1\41\1\uffff\1\45\1\uffff\1\37\1\31\1\37\1\7\1\41\1\51\2\37\1\26\1\37\1\26\2\37";
    static final String dfa_11s = "\2\uffff\1\2\1\uffff\1\1\15\uffff";
    static final String dfa_12s = "\22\uffff}>";
    static final String[] dfa_13s = {
            "\1\2\23\uffff\1\1\6\uffff\1\2\2\uffff\1\2",
            "\1\3\30\uffff\1\4\1\uffff\1\2",
            "",
            "\1\11\1\5\15\uffff\1\10\7\uffff\1\6\1\7\5\uffff\1\4",
            "",
            "\1\6\1\7",
            "\1\12",
            "\1\12\4\uffff\1\14\1\13",
            "\1\11",
            "\1\4\30\uffff\1\4\1\uffff\1\2",
            "\1\15\1\16\11\uffff\1\17",
            "\1\12\4\uffff\1\14\1\13",
            "\1\12\4\uffff\1\14\1\13",
            "\1\11\16\uffff\1\10",
            "\1\11\16\uffff\1\10\7\uffff\1\21\1\20",
            "\1\11\16\uffff\1\10",
            "\1\11\16\uffff\1\10\7\uffff\1\21\1\20",
            "\1\11\16\uffff\1\10\7\uffff\1\21\1\20"
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[][] dfa_13 = unpackEncodedStringArray(dfa_13s);

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "()* loopback of 353:5: (this_COMMA_1= RULE_COMMA ( (lv_attributes_2_0= ruleAttribute ) ) )*";
        }
    }
    static final String dfa_14s = "\20\uffff";
    static final String dfa_15s = "\11\uffff\2\15\3\uffff\2\15";
    static final String dfa_16s = "\1\6\1\45\3\uffff\1\36\1\7\2\36\2\6\3\uffff\2\6";
    static final String dfa_17s = "\1\64\1\45\3\uffff\1\46\1\43\2\37\2\50\3\uffff\2\50";
    static final String dfa_18s = "\2\uffff\1\1\1\2\1\3\6\uffff\1\6\1\5\1\4\2\uffff";
    static final String dfa_19s = "\20\uffff}>";
    static final String[] dfa_20s = {
            "\1\1\13\uffff\1\4\14\uffff\1\2\3\uffff\1\3\20\uffff\1\5",
            "\1\6",
            "",
            "",
            "",
            "\1\11\1\12\5\uffff\1\7\1\10",
            "\1\2\2\uffff\2\2\1\uffff\3\13\6\uffff\1\2\14\uffff\1\2",
            "\1\11\1\12",
            "\1\11\1\12",
            "\1\15\23\uffff\1\15\6\uffff\2\15\1\uffff\1\15\3\uffff\1\14",
            "\1\15\23\uffff\1\15\3\uffff\1\17\1\16\1\uffff\2\15\1\uffff\1\15\3\uffff\1\14",
            "",
            "",
            "",
            "\1\15\23\uffff\1\15\3\uffff\1\17\1\16\1\uffff\2\15\1\uffff\1\15\3\uffff\1\14",
            "\1\15\23\uffff\1\15\3\uffff\1\17\1\16\1\uffff\2\15\1\uffff\1\15\3\uffff\1\14"
    };

    static final short[] dfa_14 = DFA.unpackEncodedString(dfa_14s);
    static final short[] dfa_15 = DFA.unpackEncodedString(dfa_15s);
    static final char[] dfa_16 = DFA.unpackEncodedStringToUnsignedChars(dfa_16s);
    static final char[] dfa_17 = DFA.unpackEncodedStringToUnsignedChars(dfa_17s);
    static final short[] dfa_18 = DFA.unpackEncodedString(dfa_18s);
    static final short[] dfa_19 = DFA.unpackEncodedString(dfa_19s);
    static final short[][] dfa_20 = unpackEncodedStringArray(dfa_20s);

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = dfa_14;
            this.eof = dfa_15;
            this.min = dfa_16;
            this.max = dfa_17;
            this.accept = dfa_18;
            this.special = dfa_19;
            this.transition = dfa_20;
        }
        public String getDescription() {
            return "615:2: (this_ConceptReference_0= ruleConceptReference | (this_ROUND_OPEN_1= RULE_ROUND_OPEN this_SubExpression_2= ruleSubExpression this_ROUND_CLOSE_3= RULE_ROUND_CLOSE ) | this_StringValue_4= ruleStringValue | this_IntegerValue_5= ruleIntegerValue | this_DecimalValue_6= ruleDecimalValue | this_ConcreteValueReplacementSlot_7= ruleConcreteValueReplacementSlot )";
        }
    }
    static final String dfa_21s = "\3\uffff\2\6\1\1\1\uffff\2\6\7\uffff";
    static final String dfa_22s = "\1\31\1\uffff\1\36\3\31\1\uffff\2\31\1\64\2\36\4\0";
    static final String dfa_23s = "\1\64\1\uffff\1\37\3\64\1\uffff\3\64\2\37\4\0";
    static final String dfa_24s = "\1\uffff\1\1\4\uffff\1\2\11\uffff";
    static final String dfa_25s = "\14\uffff\1\1\1\2\1\0\1\3}>";
    static final String[] dfa_26s = {
            "\1\1\23\uffff\1\1\6\uffff\1\2",
            "",
            "\1\3\1\4",
            "\1\5\12\uffff\1\6\10\uffff\1\6\6\uffff\1\6",
            "\1\5\4\uffff\1\10\1\7\4\uffff\1\6\10\uffff\1\6\6\uffff\1\6",
            "\1\1\12\uffff\1\1\7\uffff\1\11\1\1\6\uffff\1\12",
            "",
            "\1\5\4\uffff\1\10\1\7\4\uffff\1\6\10\uffff\1\6\6\uffff\1\6",
            "\1\5\4\uffff\1\10\1\7\4\uffff\1\6\10\uffff\1\6\6\uffff\1\6",
            "\1\13",
            "\1\14\1\15",
            "\1\16\1\17",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };
    static final short[] dfa_21 = DFA.unpackEncodedString(dfa_21s);
    static final char[] dfa_22 = DFA.unpackEncodedStringToUnsignedChars(dfa_22s);
    static final char[] dfa_23 = DFA.unpackEncodedStringToUnsignedChars(dfa_23s);
    static final short[] dfa_24 = DFA.unpackEncodedString(dfa_24s);
    static final short[] dfa_25 = DFA.unpackEncodedString(dfa_25s);
    static final short[][] dfa_26 = unpackEncodedStringArray(dfa_26s);

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = dfa_14;
            this.eof = dfa_21;
            this.min = dfa_22;
            this.max = dfa_23;
            this.accept = dfa_24;
            this.special = dfa_25;
            this.transition = dfa_26;
        }
        public String getDescription() {
            return "1939:2: (this_SlotIntegerRange_0= ruleSlotIntegerRange | this_SlotIntegerValue_1= ruleSlotIntegerValue )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA40_14 = input.LA(1);

                         
                        int index40_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index40_14);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA40_12 = input.LA(1);

                         
                        int index40_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index40_12);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA40_13 = input.LA(1);

                         
                        int index40_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index40_13);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA40_15 = input.LA(1);

                         
                        int index40_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index40_15);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 40, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_27s = "\31\uffff";
    static final String dfa_28s = "\5\uffff\1\13\2\uffff\2\13\1\1\16\uffff";
    static final String dfa_29s = "\1\31\1\uffff\1\36\1\50\1\36\1\31\2\36\3\31\1\uffff\1\64\2\36\1\50\1\36\1\50\1\36\1\0\2\36\1\0\2\36";
    static final String dfa_30s = "\1\64\1\uffff\1\37\2\50\1\64\2\50\3\64\1\uffff\1\64\2\37\4\50\1\0\2\50\1\0\2\50";
    static final String dfa_31s = "\1\uffff\1\1\11\uffff\1\2\15\uffff";
    static final String dfa_32s = "\23\uffff\1\0\2\uffff\1\1\2\uffff}>";
    static final String[] dfa_33s = {
            "\1\1\23\uffff\1\1\6\uffff\1\2",
            "",
            "\1\3\1\4",
            "\1\5",
            "\1\7\1\6\10\uffff\1\5",
            "\1\12\4\uffff\1\11\1\10\4\uffff\1\13\10\uffff\1\13\6\uffff\1\13",
            "\1\7\1\6\10\uffff\1\5",
            "\1\7\1\6\10\uffff\1\5",
            "\1\12\4\uffff\1\11\1\10\4\uffff\1\13\10\uffff\1\13\6\uffff\1\13",
            "\1\12\4\uffff\1\11\1\10\4\uffff\1\13\10\uffff\1\13\6\uffff\1\13",
            "\1\1\12\uffff\1\1\7\uffff\1\14\1\1\6\uffff\1\15",
            "",
            "\1\16",
            "\1\17\1\20",
            "\1\21\1\22",
            "\1\23",
            "\1\25\1\24\10\uffff\1\23",
            "\1\26",
            "\1\30\1\27\10\uffff\1\26",
            "\1\uffff",
            "\1\25\1\24\10\uffff\1\23",
            "\1\25\1\24\10\uffff\1\23",
            "\1\uffff",
            "\1\30\1\27\10\uffff\1\26",
            "\1\30\1\27\10\uffff\1\26"
    };

    static final short[] dfa_27 = DFA.unpackEncodedString(dfa_27s);
    static final short[] dfa_28 = DFA.unpackEncodedString(dfa_28s);
    static final char[] dfa_29 = DFA.unpackEncodedStringToUnsignedChars(dfa_29s);
    static final char[] dfa_30 = DFA.unpackEncodedStringToUnsignedChars(dfa_30s);
    static final short[] dfa_31 = DFA.unpackEncodedString(dfa_31s);
    static final short[] dfa_32 = DFA.unpackEncodedString(dfa_32s);
    static final short[][] dfa_33 = unpackEncodedStringArray(dfa_33s);

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = dfa_27;
            this.eof = dfa_28;
            this.min = dfa_29;
            this.max = dfa_30;
            this.accept = dfa_31;
            this.special = dfa_32;
            this.transition = dfa_33;
        }
        public String getDescription() {
            return "2232:2: (this_SlotDecimalRange_0= ruleSlotDecimalRange | this_SlotDecimalValue_1= ruleSlotDecimalValue )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA45_19 = input.LA(1);

                         
                        int index45_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred67_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index45_19);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA45_22 = input.LA(1);

                         
                        int index45_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred67_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index45_22);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 45, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_34s = "\11\uffff";
    static final String dfa_35s = "\1\3\10\uffff";
    static final String dfa_36s = "\1\31\1\uffff\1\36\1\uffff\1\50\1\36\1\0\2\36";
    static final String dfa_37s = "\1\64\1\uffff\1\37\1\uffff\2\50\1\0\2\50";
    static final String dfa_38s = "\1\uffff\1\1\1\uffff\1\2\5\uffff";
    static final String dfa_39s = "\6\uffff\1\0\2\uffff}>";
    static final String[] dfa_40s = {
            "\1\3\12\uffff\1\3\7\uffff\1\1\1\3\6\uffff\1\2",
            "",
            "\1\4\1\5",
            "",
            "\1\6",
            "\1\10\1\7\10\uffff\1\6",
            "\1\uffff",
            "\1\10\1\7\10\uffff\1\6",
            "\1\10\1\7\10\uffff\1\6"
    };

    static final short[] dfa_34 = DFA.unpackEncodedString(dfa_34s);
    static final short[] dfa_35 = DFA.unpackEncodedString(dfa_35s);
    static final char[] dfa_36 = DFA.unpackEncodedStringToUnsignedChars(dfa_36s);
    static final char[] dfa_37 = DFA.unpackEncodedStringToUnsignedChars(dfa_37s);
    static final short[] dfa_38 = DFA.unpackEncodedString(dfa_38s);
    static final short[] dfa_39 = DFA.unpackEncodedString(dfa_39s);
    static final short[][] dfa_40 = unpackEncodedStringArray(dfa_40s);

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = dfa_34;
            this.eof = dfa_35;
            this.min = dfa_36;
            this.max = dfa_37;
            this.accept = dfa_38;
            this.special = dfa_39;
            this.transition = dfa_40;
        }
        public String getDescription() {
            return "2341:4: ( (lv_maximum_2_0= ruleSlotDecimalMaximumValue ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA46_6 = input.LA(1);

                         
                        int index46_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred68_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index46_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 46, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_41s = "\17\uffff";
    static final String dfa_42s = "\1\24\1\0\12\uffff\1\0\2\uffff";
    static final String dfa_43s = "\1\61\1\0\12\uffff\1\0\2\uffff";
    static final String dfa_44s = "\2\uffff\1\1\12\uffff\1\2\1\3";
    static final String dfa_45s = "\1\uffff\1\0\12\uffff\1\1\2\uffff}>";
    static final String[] dfa_46s = {
            "\1\1\3\uffff\1\2\6\uffff\1\2\1\uffff\1\15\1\uffff\1\14\3\uffff\1\2\1\uffff\1\2\2\uffff\6\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            ""
    };

    static final short[] dfa_41 = DFA.unpackEncodedString(dfa_41s);
    static final char[] dfa_42 = DFA.unpackEncodedStringToUnsignedChars(dfa_42s);
    static final char[] dfa_43 = DFA.unpackEncodedStringToUnsignedChars(dfa_43s);
    static final short[] dfa_44 = DFA.unpackEncodedString(dfa_44s);
    static final short[] dfa_45 = DFA.unpackEncodedString(dfa_45s);
    static final short[][] dfa_46 = unpackEncodedStringArray(dfa_46s);

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = dfa_41;
            this.eof = dfa_41;
            this.min = dfa_42;
            this.max = dfa_43;
            this.accept = dfa_44;
            this.special = dfa_45;
            this.transition = dfa_46;
        }
        public String getDescription() {
            return "3641:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_EclAttributeGroup_1= ruleEclAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_1 = input.LA(1);

                         
                        int index63_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred94_InternalEtlParser()) ) {s = 2;}

                        else if ( (synpred95_InternalEtlParser()) ) {s = 13;}

                         
                        input.seek(index63_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA63_12 = input.LA(1);

                         
                        int index63_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred94_InternalEtlParser()) ) {s = 2;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index63_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_47s = "\16\uffff";
    static final String dfa_48s = "\1\24\13\uffff\1\0\1\uffff";
    static final String dfa_49s = "\1\61\13\uffff\1\0\1\uffff";
    static final String dfa_50s = "\1\uffff\1\1\13\uffff\1\2";
    static final String dfa_51s = "\14\uffff\1\0\1\uffff}>";
    static final String[] dfa_52s = {
            "\1\1\3\uffff\1\1\6\uffff\1\1\3\uffff\1\14\3\uffff\1\1\1\uffff\1\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            ""
    };

    static final short[] dfa_47 = DFA.unpackEncodedString(dfa_47s);
    static final char[] dfa_48 = DFA.unpackEncodedStringToUnsignedChars(dfa_48s);
    static final char[] dfa_49 = DFA.unpackEncodedStringToUnsignedChars(dfa_49s);
    static final short[] dfa_50 = DFA.unpackEncodedString(dfa_50s);
    static final short[] dfa_51 = DFA.unpackEncodedString(dfa_51s);
    static final short[][] dfa_52 = unpackEncodedStringArray(dfa_52s);

    class DFA68 extends DFA {

        public DFA68(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 68;
            this.eot = dfa_47;
            this.eof = dfa_47;
            this.min = dfa_48;
            this.max = dfa_49;
            this.accept = dfa_50;
            this.special = dfa_51;
            this.transition = dfa_52;
        }
        public String getDescription() {
            return "3971:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA68_12 = input.LA(1);

                         
                        int index68_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred100_InternalEtlParser()) ) {s = 1;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index68_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 68, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_53s = "\101\uffff";
    static final String dfa_54s = "\23\uffff\2\52\2\uffff\2\55\2\uffff\2\62\2\uffff\2\66\2\uffff\2\72\2\uffff\2\75\2\uffff\2\52\2\uffff\2\55\2\uffff\2\62\2\uffff\2\66\2\uffff\2\72\2\uffff\2\75";
    static final String dfa_55s = "\1\52\2\4\4\64\1\uffff\1\36\2\uffff\1\36\1\uffff\6\36\2\32\2\36\2\32\2\36\2\32\2\36\2\32\2\36\2\32\2\36\2\32\2\uffff\2\32\2\uffff\2\32\2\uffff\2\32\2\uffff\2\32\2\uffff\2\32\2\uffff\2\32";
    static final String dfa_56s = "\1\63\6\64\1\uffff\1\46\2\uffff\1\46\1\uffff\4\46\2\37\2\50\2\37\2\50\2\37\2\50\2\37\2\50\2\37\2\50\2\37\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50";
    static final String dfa_57s = "\7\uffff\1\3\1\uffff\1\1\1\2\1\uffff\1\4\34\uffff\1\13\1\5\2\uffff\1\6\1\14\2\uffff\1\15\1\7\2\uffff\1\16\1\10\2\uffff\1\17\1\11\2\uffff\1\12\1\20\2\uffff";
    static final String dfa_58s = "\101\uffff}>";
    static final String[] dfa_59s = {
            "\1\1\1\2\1\5\1\3\4\uffff\1\4\1\6",
            "\2\11\14\uffff\1\7\41\uffff\1\10",
            "\2\12\14\uffff\1\14\41\uffff\1\13",
            "\1\15",
            "\1\16",
            "\1\17",
            "\1\20",
            "",
            "\1\23\1\24\5\uffff\1\21\1\22",
            "",
            "",
            "\1\27\1\30\5\uffff\1\25\1\26",
            "",
            "\1\33\1\34\5\uffff\1\31\1\32",
            "\1\37\1\40\5\uffff\1\35\1\36",
            "\1\43\1\44\5\uffff\1\41\1\42",
            "\1\47\1\50\5\uffff\1\45\1\46",
            "\1\23\1\24",
            "\1\23\1\24",
            "\4\52\4\uffff\1\52\1\uffff\1\52\3\uffff\1\51",
            "\4\52\1\54\1\53\2\uffff\1\52\1\uffff\1\52\3\uffff\1\51",
            "\1\27\1\30",
            "\1\27\1\30",
            "\4\55\4\uffff\1\55\1\uffff\1\55\3\uffff\1\56",
            "\4\55\1\60\1\57\2\uffff\1\55\1\uffff\1\55\3\uffff\1\56",
            "\1\33\1\34",
            "\1\33\1\34",
            "\4\62\4\uffff\1\62\1\uffff\1\62\3\uffff\1\61",
            "\4\62\1\64\1\63\2\uffff\1\62\1\uffff\1\62\3\uffff\1\61",
            "\1\37\1\40",
            "\1\37\1\40",
            "\4\66\4\uffff\1\66\1\uffff\1\66\3\uffff\1\65",
            "\4\66\1\70\1\67\2\uffff\1\66\1\uffff\1\66\3\uffff\1\65",
            "\1\43\1\44",
            "\1\43\1\44",
            "\4\72\4\uffff\1\72\1\uffff\1\72\3\uffff\1\71",
            "\4\72\1\74\1\73\2\uffff\1\72\1\uffff\1\72\3\uffff\1\71",
            "\1\47\1\50",
            "\1\47\1\50",
            "\4\75\4\uffff\1\75\1\uffff\1\75\3\uffff\1\76",
            "\4\75\1\100\1\77\2\uffff\1\75\1\uffff\1\75\3\uffff\1\76",
            "",
            "",
            "\4\52\1\54\1\53\2\uffff\1\52\1\uffff\1\52\3\uffff\1\51",
            "\4\52\1\54\1\53\2\uffff\1\52\1\uffff\1\52\3\uffff\1\51",
            "",
            "",
            "\4\55\1\60\1\57\2\uffff\1\55\1\uffff\1\55\3\uffff\1\56",
            "\4\55\1\60\1\57\2\uffff\1\55\1\uffff\1\55\3\uffff\1\56",
            "",
            "",
            "\4\62\1\64\1\63\2\uffff\1\62\1\uffff\1\62\3\uffff\1\61",
            "\4\62\1\64\1\63\2\uffff\1\62\1\uffff\1\62\3\uffff\1\61",
            "",
            "",
            "\4\66\1\70\1\67\2\uffff\1\66\1\uffff\1\66\3\uffff\1\65",
            "\4\66\1\70\1\67\2\uffff\1\66\1\uffff\1\66\3\uffff\1\65",
            "",
            "",
            "\4\72\1\74\1\73\2\uffff\1\72\1\uffff\1\72\3\uffff\1\71",
            "\4\72\1\74\1\73\2\uffff\1\72\1\uffff\1\72\3\uffff\1\71",
            "",
            "",
            "\4\75\1\100\1\77\2\uffff\1\75\1\uffff\1\75\3\uffff\1\76",
            "\4\75\1\100\1\77\2\uffff\1\75\1\uffff\1\75\3\uffff\1\76"
    };

    static final short[] dfa_53 = DFA.unpackEncodedString(dfa_53s);
    static final short[] dfa_54 = DFA.unpackEncodedString(dfa_54s);
    static final char[] dfa_55 = DFA.unpackEncodedStringToUnsignedChars(dfa_55s);
    static final char[] dfa_56 = DFA.unpackEncodedStringToUnsignedChars(dfa_56s);
    static final short[] dfa_57 = DFA.unpackEncodedString(dfa_57s);
    static final short[] dfa_58 = DFA.unpackEncodedString(dfa_58s);
    static final short[][] dfa_59 = unpackEncodedStringArray(dfa_59s);

    class DFA73 extends DFA {

        public DFA73(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 73;
            this.eot = dfa_53;
            this.eof = dfa_54;
            this.min = dfa_55;
            this.max = dfa_56;
            this.accept = dfa_57;
            this.special = dfa_58;
            this.transition = dfa_59;
        }
        public String getDescription() {
            return "4306:2: (this_BooleanValueEquals_0= ruleBooleanValueEquals | this_BooleanValueNotEquals_1= ruleBooleanValueNotEquals | this_StringValueEquals_2= ruleStringValueEquals | this_StringValueNotEquals_3= ruleStringValueNotEquals | this_IntegerValueEquals_4= ruleIntegerValueEquals | this_IntegerValueNotEquals_5= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_6= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_7= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_8= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_9= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_10= ruleDecimalValueEquals | this_DecimalValueNotEquals_11= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_12= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_13= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_14= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_15= ruleDecimalValueLessThanEquals )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000080000040L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000002100000002L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000280000040L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000284000042L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000200000000L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000404000000L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000040000000000L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0010000880040040L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000400L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000800400080L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0003F28880000040L});
        public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000400080L});
        public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000000080L});
        public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000800400880L});
        public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000001000L});
        public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x000FFC803D030000L});
        public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x000FFC903D030000L});
        public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x00000000C0400180L});
        public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000002000L});
        public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000040000L});
        public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000001000040000L});
        public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000004000L});
        public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0010200002000000L});
        public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0010201002000000L});
        public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000000008000L});
        public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x00000000C0000100L});
        public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000002000000L});
        public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x00000200C0000100L});
        public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000800002L});
        public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x00000060C0000100L});
        public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0010100000000002L});
        public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0010100000000000L});
        public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0010000000000000L});
        public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000000010000002L});
        public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x000000000C000002L});
        public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000000020000002L});
        public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000000100000002L});
        public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0003F28A81100040L});
        public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000010000000002L});
        public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0003F28881100040L});
        public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0000000400000000L});
        public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0003F28881000040L});
        public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x000C3C0000000000L});
        public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x0000000000200000L});
        public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x0000000000000030L});
        public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x00000000C0000000L});
        public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x00000000C0000002L});
        public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000010000000000L});
    }


}