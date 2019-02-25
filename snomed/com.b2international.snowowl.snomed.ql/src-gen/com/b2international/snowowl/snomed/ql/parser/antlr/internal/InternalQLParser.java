package com.b2international.snowowl.snomed.ql.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.ql.services.QLGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalQLParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LanguageRefSet", "AcceptableIn", "Description", "PreferredIn", "ModuleId", "Concept", "Active", "TypeId", "MINUS", "Exact", "False", "Match", "Regex", "Term", "True", "AND", "OR", "Comma", "RULE_OPEN_DOUBLE_BRACES", "RULE_CLOSE_DOUBLE_BRACES", "RULE_TERM_STRING", "RULE_REVERSED", "RULE_TO", "RULE_ZERO", "RULE_DIGIT_NONZERO", "RULE_COLON", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_PLUS", "RULE_DASH", "RULE_CARET", "RULE_NOT", "RULE_DOT", "RULE_WILDCARD", "RULE_EQUAL", "RULE_NOT_EQUAL", "RULE_LT", "RULE_GT", "RULE_DBL_LT", "RULE_DBL_GT", "RULE_LT_EM", "RULE_GT_EM", "RULE_GTE", "RULE_LTE", "RULE_HASH", "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_STRING"
    };
    public static final int RULE_DIGIT_NONZERO=28;
    public static final int RULE_CURLY_OPEN=30;
    public static final int RULE_TO=26;
    public static final int RULE_ROUND_CLOSE=33;
    public static final int RULE_DBL_GT=47;
    public static final int LanguageRefSet=4;
    public static final int True=18;
    public static final int RULE_GT=45;
    public static final int False=14;
    public static final int MINUS=12;
    public static final int RULE_GTE=50;
    public static final int ModuleId=8;
    public static final int Regex=16;
    public static final int RULE_ROUND_OPEN=32;
    public static final int RULE_DBL_LT=46;
    public static final int RULE_NOT_EQUAL=43;
    public static final int Concept=9;
    public static final int AcceptableIn=5;
    public static final int TypeId=11;
    public static final int RULE_SQUARE_CLOSE=35;
    public static final int RULE_SQUARE_OPEN=34;
    public static final int RULE_EQUAL=42;
    public static final int RULE_LT_EM=48;
    public static final int RULE_CURLY_CLOSE=31;
    public static final int RULE_ZERO=27;
    public static final int Term=17;
    public static final int RULE_COLON=29;
    public static final int RULE_LT=44;
    public static final int Active=10;
    public static final int AND=19;
    public static final int RULE_ML_COMMENT=54;
    public static final int RULE_LTE=51;
    public static final int Description=6;
    public static final int RULE_STRING=56;
    public static final int RULE_NOT=39;
    public static final int RULE_REVERSED=25;
    public static final int Match=15;
    public static final int RULE_SL_COMMENT=55;
    public static final int RULE_CLOSE_DOUBLE_BRACES=23;
    public static final int PreferredIn=7;
    public static final int Comma=21;
    public static final int RULE_HASH=52;
    public static final int RULE_DASH=37;
    public static final int RULE_PLUS=36;
    public static final int Exact=13;
    public static final int RULE_DOT=40;
    public static final int EOF=-1;
    public static final int OR=20;
    public static final int RULE_GT_EM=49;
    public static final int RULE_WS=53;
    public static final int RULE_OPEN_DOUBLE_BRACES=22;
    public static final int RULE_CARET=38;
    public static final int RULE_WILDCARD=41;
    public static final int RULE_TERM_STRING=24;

    // delegates
    // delegators


        public InternalQLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalQLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalQLParser.tokenNames; }
    public String getGrammarFileName() { return "InternalQLParser.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */

     	private QLGrammarAccess grammarAccess;

        public InternalQLParser(TokenStream input, QLGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Query";
       	}

       	@Override
       	protected QLGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleQuery"
    // InternalQLParser.g:76:1: entryRuleQuery returns [EObject current=null] : iv_ruleQuery= ruleQuery EOF ;
    public final EObject entryRuleQuery() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleQuery = null;


        try {
            // InternalQLParser.g:76:46: (iv_ruleQuery= ruleQuery EOF )
            // InternalQLParser.g:77:2: iv_ruleQuery= ruleQuery EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getQueryRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleQuery=ruleQuery();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleQuery; 
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
    // $ANTLR end "entryRuleQuery"


    // $ANTLR start "ruleQuery"
    // InternalQLParser.g:83:1: ruleQuery returns [EObject current=null] : ( () ( (lv_ecl_1_0= ruleExpressionConstraint ) )? (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )? ) ;
    public final EObject ruleQuery() throws RecognitionException {
        EObject current = null;

        Token this_OPEN_DOUBLE_BRACES_2=null;
        Token this_CLOSE_DOUBLE_BRACES_4=null;
        EObject lv_ecl_1_0 = null;

        EObject lv_constraint_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:89:2: ( ( () ( (lv_ecl_1_0= ruleExpressionConstraint ) )? (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )? ) )
            // InternalQLParser.g:90:2: ( () ( (lv_ecl_1_0= ruleExpressionConstraint ) )? (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )? )
            {
            // InternalQLParser.g:90:2: ( () ( (lv_ecl_1_0= ruleExpressionConstraint ) )? (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )? )
            // InternalQLParser.g:91:3: () ( (lv_ecl_1_0= ruleExpressionConstraint ) )? (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )?
            {
            // InternalQLParser.g:91:3: ()
            // InternalQLParser.g:92:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getQueryAccess().getQueryAction_0(),
              					current);
              			
            }

            }

            // InternalQLParser.g:101:3: ( (lv_ecl_1_0= ruleExpressionConstraint ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==RULE_DIGIT_NONZERO||LA1_0==RULE_ROUND_OPEN||LA1_0==RULE_CARET||LA1_0==RULE_WILDCARD||(LA1_0>=RULE_LT && LA1_0<=RULE_GT_EM)) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // InternalQLParser.g:102:4: (lv_ecl_1_0= ruleExpressionConstraint )
                    {
                    // InternalQLParser.g:102:4: (lv_ecl_1_0= ruleExpressionConstraint )
                    // InternalQLParser.g:103:5: lv_ecl_1_0= ruleExpressionConstraint
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getQueryAccess().getEclExpressionConstraintParserRuleCall_1_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_3);
                    lv_ecl_1_0=ruleExpressionConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getQueryRule());
                      					}
                      					set(
                      						current,
                      						"ecl",
                      						lv_ecl_1_0,
                      						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalQLParser.g:120:3: (this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==RULE_OPEN_DOUBLE_BRACES) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalQLParser.g:121:4: this_OPEN_DOUBLE_BRACES_2= RULE_OPEN_DOUBLE_BRACES ( (lv_constraint_3_0= ruleConstraint ) ) this_CLOSE_DOUBLE_BRACES_4= RULE_CLOSE_DOUBLE_BRACES
                    {
                    this_OPEN_DOUBLE_BRACES_2=(Token)match(input,RULE_OPEN_DOUBLE_BRACES,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_OPEN_DOUBLE_BRACES_2, grammarAccess.getQueryAccess().getOPEN_DOUBLE_BRACESTerminalRuleCall_2_0());
                      			
                    }
                    // InternalQLParser.g:125:4: ( (lv_constraint_3_0= ruleConstraint ) )
                    // InternalQLParser.g:126:5: (lv_constraint_3_0= ruleConstraint )
                    {
                    // InternalQLParser.g:126:5: (lv_constraint_3_0= ruleConstraint )
                    // InternalQLParser.g:127:6: lv_constraint_3_0= ruleConstraint
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getQueryAccess().getConstraintConstraintParserRuleCall_2_1_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_5);
                    lv_constraint_3_0=ruleConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getQueryRule());
                      						}
                      						set(
                      							current,
                      							"constraint",
                      							lv_constraint_3_0,
                      							"com.b2international.snowowl.snomed.ql.QL.Constraint");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_CLOSE_DOUBLE_BRACES_4=(Token)match(input,RULE_CLOSE_DOUBLE_BRACES,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_CLOSE_DOUBLE_BRACES_4, grammarAccess.getQueryAccess().getCLOSE_DOUBLE_BRACESTerminalRuleCall_2_2());
                      			
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
    // $ANTLR end "ruleQuery"


    // $ANTLR start "entryRuleConstraint"
    // InternalQLParser.g:153:1: entryRuleConstraint returns [EObject current=null] : iv_ruleConstraint= ruleConstraint EOF ;
    public final EObject entryRuleConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstraint = null;


        try {
            // InternalQLParser.g:153:51: (iv_ruleConstraint= ruleConstraint EOF )
            // InternalQLParser.g:154:2: iv_ruleConstraint= ruleConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConstraint=ruleConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConstraint; 
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
    // $ANTLR end "entryRuleConstraint"


    // $ANTLR start "ruleConstraint"
    // InternalQLParser.g:160:1: ruleConstraint returns [EObject current=null] : this_Disjunction_0= ruleDisjunction ;
    public final EObject ruleConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_Disjunction_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:166:2: (this_Disjunction_0= ruleDisjunction )
            // InternalQLParser.g:167:2: this_Disjunction_0= ruleDisjunction
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getConstraintAccess().getDisjunctionParserRuleCall());
              	
            }
            pushFollow(FollowSets000.FOLLOW_2);
            this_Disjunction_0=ruleDisjunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		current = this_Disjunction_0;
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
    // $ANTLR end "ruleConstraint"


    // $ANTLR start "entryRuleDisjunction"
    // InternalQLParser.g:181:1: entryRuleDisjunction returns [EObject current=null] : iv_ruleDisjunction= ruleDisjunction EOF ;
    public final EObject entryRuleDisjunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDisjunction = null;


        try {
            // InternalQLParser.g:181:52: (iv_ruleDisjunction= ruleDisjunction EOF )
            // InternalQLParser.g:182:2: iv_ruleDisjunction= ruleDisjunction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDisjunctionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleDisjunction=ruleDisjunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDisjunction; 
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
    // $ANTLR end "entryRuleDisjunction"


    // $ANTLR start "ruleDisjunction"
    // InternalQLParser.g:188:1: ruleDisjunction returns [EObject current=null] : (this_Conjunction_0= ruleConjunction ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )* ) ;
    public final EObject ruleDisjunction() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_Conjunction_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:194:2: ( (this_Conjunction_0= ruleConjunction ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )* ) )
            // InternalQLParser.g:195:2: (this_Conjunction_0= ruleConjunction ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )* )
            {
            // InternalQLParser.g:195:2: (this_Conjunction_0= ruleConjunction ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )* )
            // InternalQLParser.g:196:3: this_Conjunction_0= ruleConjunction ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getDisjunctionAccess().getConjunctionParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_6);
            this_Conjunction_0=ruleConjunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_Conjunction_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:207:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==OR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalQLParser.g:208:4: () otherlv_2= OR ( (lv_right_3_0= ruleConjunction ) )
            	    {
            	    // InternalQLParser.g:208:4: ()
            	    // InternalQLParser.g:209:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getDisjunctionAccess().getDisjunctionLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_2, grammarAccess.getDisjunctionAccess().getORKeyword_1_1());
            	      			
            	    }
            	    // InternalQLParser.g:222:4: ( (lv_right_3_0= ruleConjunction ) )
            	    // InternalQLParser.g:223:5: (lv_right_3_0= ruleConjunction )
            	    {
            	    // InternalQLParser.g:223:5: (lv_right_3_0= ruleConjunction )
            	    // InternalQLParser.g:224:6: lv_right_3_0= ruleConjunction
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getDisjunctionAccess().getRightConjunctionParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_6);
            	    lv_right_3_0=ruleConjunction();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getDisjunctionRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_3_0,
            	      							"com.b2international.snowowl.snomed.ql.QL.Conjunction");
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
    // $ANTLR end "ruleDisjunction"


    // $ANTLR start "entryRuleConjunction"
    // InternalQLParser.g:246:1: entryRuleConjunction returns [EObject current=null] : iv_ruleConjunction= ruleConjunction EOF ;
    public final EObject entryRuleConjunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConjunction = null;


        try {
            // InternalQLParser.g:246:52: (iv_ruleConjunction= ruleConjunction EOF )
            // InternalQLParser.g:247:2: iv_ruleConjunction= ruleConjunction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getConjunctionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConjunction=ruleConjunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleConjunction; 
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
    // $ANTLR end "entryRuleConjunction"


    // $ANTLR start "ruleConjunction"
    // InternalQLParser.g:253:1: ruleConjunction returns [EObject current=null] : (this_Exclusion_0= ruleExclusion ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )* ) ;
    public final EObject ruleConjunction() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_Exclusion_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:259:2: ( (this_Exclusion_0= ruleExclusion ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )* ) )
            // InternalQLParser.g:260:2: (this_Exclusion_0= ruleExclusion ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )* )
            {
            // InternalQLParser.g:260:2: (this_Exclusion_0= ruleExclusion ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )* )
            // InternalQLParser.g:261:3: this_Exclusion_0= ruleExclusion ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getConjunctionAccess().getExclusionParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_7);
            this_Exclusion_0=ruleExclusion();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_Exclusion_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:272:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==AND||LA5_0==Comma) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // InternalQLParser.g:273:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusion ) )
            	    {
            	    // InternalQLParser.g:273:4: ()
            	    // InternalQLParser.g:274:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	      					/* */
            	      				
            	    }
            	    if ( state.backtracking==0 ) {

            	      					current = forceCreateModelElementAndSet(
            	      						grammarAccess.getConjunctionAccess().getConjunctionLeftAction_1_0(),
            	      						current);
            	      				
            	    }

            	    }

            	    // InternalQLParser.g:283:4: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt4=2;
            	    int LA4_0 = input.LA(1);

            	    if ( (LA4_0==AND) ) {
            	        alt4=1;
            	    }
            	    else if ( (LA4_0==Comma) ) {
            	        alt4=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 4, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt4) {
            	        case 1 :
            	            // InternalQLParser.g:284:5: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_2, grammarAccess.getConjunctionAccess().getANDKeyword_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalQLParser.g:289:5: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_3, grammarAccess.getConjunctionAccess().getCommaKeyword_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalQLParser.g:294:4: ( (lv_right_4_0= ruleExclusion ) )
            	    // InternalQLParser.g:295:5: (lv_right_4_0= ruleExclusion )
            	    {
            	    // InternalQLParser.g:295:5: (lv_right_4_0= ruleExclusion )
            	    // InternalQLParser.g:296:6: lv_right_4_0= ruleExclusion
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getConjunctionAccess().getRightExclusionParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_7);
            	    lv_right_4_0=ruleExclusion();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      						if (current==null) {
            	      							current = createModelElementForParent(grammarAccess.getConjunctionRule());
            	      						}
            	      						set(
            	      							current,
            	      							"right",
            	      							lv_right_4_0,
            	      							"com.b2international.snowowl.snomed.ql.QL.Exclusion");
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
    // $ANTLR end "ruleConjunction"


    // $ANTLR start "entryRuleExclusion"
    // InternalQLParser.g:318:1: entryRuleExclusion returns [EObject current=null] : iv_ruleExclusion= ruleExclusion EOF ;
    public final EObject entryRuleExclusion() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusion = null;


        try {
            // InternalQLParser.g:318:50: (iv_ruleExclusion= ruleExclusion EOF )
            // InternalQLParser.g:319:2: iv_ruleExclusion= ruleExclusion EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExclusionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleExclusion=ruleExclusion();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExclusion; 
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
    // $ANTLR end "entryRuleExclusion"


    // $ANTLR start "ruleExclusion"
    // InternalQLParser.g:325:1: ruleExclusion returns [EObject current=null] : (this_Filter_0= ruleFilter ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )? ) ;
    public final EObject ruleExclusion() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_Filter_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:331:2: ( (this_Filter_0= ruleFilter ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )? ) )
            // InternalQLParser.g:332:2: (this_Filter_0= ruleFilter ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )? )
            {
            // InternalQLParser.g:332:2: (this_Filter_0= ruleFilter ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )? )
            // InternalQLParser.g:333:3: this_Filter_0= ruleFilter ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getExclusionAccess().getFilterParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_8);
            this_Filter_0=ruleFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_Filter_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:344:3: ( () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==MINUS) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalQLParser.g:345:4: () otherlv_2= MINUS ( (lv_right_3_0= ruleFilter ) )
                    {
                    // InternalQLParser.g:345:4: ()
                    // InternalQLParser.g:346:5: 
                    {
                    if ( state.backtracking==0 ) {

                      					/* */
                      				
                    }
                    if ( state.backtracking==0 ) {

                      					current = forceCreateModelElementAndSet(
                      						grammarAccess.getExclusionAccess().getExclusionLeftAction_1_0(),
                      						current);
                      				
                    }

                    }

                    otherlv_2=(Token)match(input,MINUS,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_2, grammarAccess.getExclusionAccess().getMINUSKeyword_1_1());
                      			
                    }
                    // InternalQLParser.g:359:4: ( (lv_right_3_0= ruleFilter ) )
                    // InternalQLParser.g:360:5: (lv_right_3_0= ruleFilter )
                    {
                    // InternalQLParser.g:360:5: (lv_right_3_0= ruleFilter )
                    // InternalQLParser.g:361:6: lv_right_3_0= ruleFilter
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getExclusionAccess().getRightFilterParserRuleCall_1_2_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_right_3_0=ruleFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getExclusionRule());
                      						}
                      						set(
                      							current,
                      							"right",
                      							lv_right_3_0,
                      							"com.b2international.snowowl.snomed.ql.QL.Filter");
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
    // $ANTLR end "ruleExclusion"


    // $ANTLR start "entryRuleNestedFilter"
    // InternalQLParser.g:383:1: entryRuleNestedFilter returns [EObject current=null] : iv_ruleNestedFilter= ruleNestedFilter EOF ;
    public final EObject entryRuleNestedFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedFilter = null;


        try {
            // InternalQLParser.g:383:53: (iv_ruleNestedFilter= ruleNestedFilter EOF )
            // InternalQLParser.g:384:2: iv_ruleNestedFilter= ruleNestedFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNestedFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNestedFilter=ruleNestedFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNestedFilter; 
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
    // $ANTLR end "entryRuleNestedFilter"


    // $ANTLR start "ruleNestedFilter"
    // InternalQLParser.g:390:1: ruleNestedFilter returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_constraint_1_0= ruleConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedFilter() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:396:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_constraint_1_0= ruleConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalQLParser.g:397:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_constraint_1_0= ruleConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalQLParser.g:397:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_constraint_1_0= ruleConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalQLParser.g:398:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_constraint_1_0= ruleConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedFilterAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:402:3: ( (lv_constraint_1_0= ruleConstraint ) )
            // InternalQLParser.g:403:4: (lv_constraint_1_0= ruleConstraint )
            {
            // InternalQLParser.g:403:4: (lv_constraint_1_0= ruleConstraint )
            // InternalQLParser.g:404:5: lv_constraint_1_0= ruleConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedFilterAccess().getConstraintConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_9);
            lv_constraint_1_0=ruleConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getNestedFilterRule());
              					}
              					set(
              						current,
              						"constraint",
              						lv_constraint_1_0,
              						"com.b2international.snowowl.snomed.ql.QL.Constraint");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_ROUND_CLOSE_2=(Token)match(input,RULE_ROUND_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedFilterAccess().getROUND_CLOSETerminalRuleCall_2());
              		
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
    // $ANTLR end "ruleNestedFilter"


    // $ANTLR start "entryRuleFilter"
    // InternalQLParser.g:429:1: entryRuleFilter returns [EObject current=null] : iv_ruleFilter= ruleFilter EOF ;
    public final EObject entryRuleFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFilter = null;


        try {
            // InternalQLParser.g:429:47: (iv_ruleFilter= ruleFilter EOF )
            // InternalQLParser.g:430:2: iv_ruleFilter= ruleFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleFilter=ruleFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFilter; 
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
    // $ANTLR end "entryRuleFilter"


    // $ANTLR start "ruleFilter"
    // InternalQLParser.g:436:1: ruleFilter returns [EObject current=null] : (this_ActiveFilter_0= ruleActiveFilter | this_TermFilter_1= ruleTermFilter | this_PreferredInFilter_2= rulePreferredInFilter | this_AcceptableInFilter_3= ruleAcceptableInFilter | this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter | this_TypeFilter_5= ruleTypeFilter | this_ModuleFilter_6= ruleModuleFilter | this_NestedFilter_7= ruleNestedFilter ) ;
    public final EObject ruleFilter() throws RecognitionException {
        EObject current = null;

        EObject this_ActiveFilter_0 = null;

        EObject this_TermFilter_1 = null;

        EObject this_PreferredInFilter_2 = null;

        EObject this_AcceptableInFilter_3 = null;

        EObject this_LanguageRefSetFilter_4 = null;

        EObject this_TypeFilter_5 = null;

        EObject this_ModuleFilter_6 = null;

        EObject this_NestedFilter_7 = null;



        	enterRule();

        try {
            // InternalQLParser.g:442:2: ( (this_ActiveFilter_0= ruleActiveFilter | this_TermFilter_1= ruleTermFilter | this_PreferredInFilter_2= rulePreferredInFilter | this_AcceptableInFilter_3= ruleAcceptableInFilter | this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter | this_TypeFilter_5= ruleTypeFilter | this_ModuleFilter_6= ruleModuleFilter | this_NestedFilter_7= ruleNestedFilter ) )
            // InternalQLParser.g:443:2: (this_ActiveFilter_0= ruleActiveFilter | this_TermFilter_1= ruleTermFilter | this_PreferredInFilter_2= rulePreferredInFilter | this_AcceptableInFilter_3= ruleAcceptableInFilter | this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter | this_TypeFilter_5= ruleTypeFilter | this_ModuleFilter_6= ruleModuleFilter | this_NestedFilter_7= ruleNestedFilter )
            {
            // InternalQLParser.g:443:2: (this_ActiveFilter_0= ruleActiveFilter | this_TermFilter_1= ruleTermFilter | this_PreferredInFilter_2= rulePreferredInFilter | this_AcceptableInFilter_3= ruleAcceptableInFilter | this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter | this_TypeFilter_5= ruleTypeFilter | this_ModuleFilter_6= ruleModuleFilter | this_NestedFilter_7= ruleNestedFilter )
            int alt7=8;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // InternalQLParser.g:444:3: this_ActiveFilter_0= ruleActiveFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getActiveFilterParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ActiveFilter_0=ruleActiveFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ActiveFilter_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:456:3: this_TermFilter_1= ruleTermFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getTermFilterParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_TermFilter_1=ruleTermFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_TermFilter_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalQLParser.g:468:3: this_PreferredInFilter_2= rulePreferredInFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getPreferredInFilterParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_PreferredInFilter_2=rulePreferredInFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_PreferredInFilter_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalQLParser.g:480:3: this_AcceptableInFilter_3= ruleAcceptableInFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getAcceptableInFilterParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AcceptableInFilter_3=ruleAcceptableInFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AcceptableInFilter_3;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalQLParser.g:492:3: this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getLanguageRefSetFilterParserRuleCall_4());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_LanguageRefSetFilter_4=ruleLanguageRefSetFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_LanguageRefSetFilter_4;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 6 :
                    // InternalQLParser.g:504:3: this_TypeFilter_5= ruleTypeFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getTypeFilterParserRuleCall_5());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_TypeFilter_5=ruleTypeFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_TypeFilter_5;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalQLParser.g:516:3: this_ModuleFilter_6= ruleModuleFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getModuleFilterParserRuleCall_6());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ModuleFilter_6=ruleModuleFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ModuleFilter_6;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 8 :
                    // InternalQLParser.g:528:3: this_NestedFilter_7= ruleNestedFilter
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFilterAccess().getNestedFilterParserRuleCall_7());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NestedFilter_7=ruleNestedFilter();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_NestedFilter_7;
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
    // $ANTLR end "ruleFilter"


    // $ANTLR start "entryRuleActiveFilter"
    // InternalQLParser.g:543:1: entryRuleActiveFilter returns [EObject current=null] : iv_ruleActiveFilter= ruleActiveFilter EOF ;
    public final EObject entryRuleActiveFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActiveFilter = null;


        try {
            // InternalQLParser.g:543:53: (iv_ruleActiveFilter= ruleActiveFilter EOF )
            // InternalQLParser.g:544:2: iv_ruleActiveFilter= ruleActiveFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActiveFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleActiveFilter=ruleActiveFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActiveFilter; 
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
    // $ANTLR end "entryRuleActiveFilter"


    // $ANTLR start "ruleActiveFilter"
    // InternalQLParser.g:550:1: ruleActiveFilter returns [EObject current=null] : ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= Active this_EQUAL_3= RULE_EQUAL ( (lv_active_4_0= ruleBoolean ) ) ) ;
    public final EObject ruleActiveFilter() throws RecognitionException {
        EObject current = null;

        Token this_DOT_1=null;
        Token otherlv_2=null;
        Token this_EQUAL_3=null;
        Enumerator lv_domain_0_0 = null;

        AntlrDatatypeRuleToken lv_active_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:556:2: ( ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= Active this_EQUAL_3= RULE_EQUAL ( (lv_active_4_0= ruleBoolean ) ) ) )
            // InternalQLParser.g:557:2: ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= Active this_EQUAL_3= RULE_EQUAL ( (lv_active_4_0= ruleBoolean ) ) )
            {
            // InternalQLParser.g:557:2: ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= Active this_EQUAL_3= RULE_EQUAL ( (lv_active_4_0= ruleBoolean ) ) )
            // InternalQLParser.g:558:3: ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= Active this_EQUAL_3= RULE_EQUAL ( (lv_active_4_0= ruleBoolean ) )
            {
            // InternalQLParser.g:558:3: ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==Description||LA8_0==Concept) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalQLParser.g:559:4: ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT
                    {
                    // InternalQLParser.g:559:4: ( (lv_domain_0_0= ruleDomain ) )
                    // InternalQLParser.g:560:5: (lv_domain_0_0= ruleDomain )
                    {
                    // InternalQLParser.g:560:5: (lv_domain_0_0= ruleDomain )
                    // InternalQLParser.g:561:6: lv_domain_0_0= ruleDomain
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getActiveFilterAccess().getDomainDomainEnumRuleCall_0_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_10);
                    lv_domain_0_0=ruleDomain();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getActiveFilterRule());
                      						}
                      						set(
                      							current,
                      							"domain",
                      							lv_domain_0_0,
                      							"com.b2international.snowowl.snomed.ql.QL.Domain");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_11); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DOT_1, grammarAccess.getActiveFilterAccess().getDOTTerminalRuleCall_0_1());
                      			
                    }

                    }
                    break;

            }

            otherlv_2=(Token)match(input,Active,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getActiveFilterAccess().getActiveKeyword_1());
              		
            }
            this_EQUAL_3=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_13); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_3, grammarAccess.getActiveFilterAccess().getEQUALTerminalRuleCall_2());
              		
            }
            // InternalQLParser.g:591:3: ( (lv_active_4_0= ruleBoolean ) )
            // InternalQLParser.g:592:4: (lv_active_4_0= ruleBoolean )
            {
            // InternalQLParser.g:592:4: (lv_active_4_0= ruleBoolean )
            // InternalQLParser.g:593:5: lv_active_4_0= ruleBoolean
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getActiveFilterAccess().getActiveBooleanParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_active_4_0=ruleBoolean();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getActiveFilterRule());
              					}
              					set(
              						current,
              						"active",
              						lv_active_4_0,
              						"com.b2international.snowowl.snomed.ql.QL.Boolean");
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
    // $ANTLR end "ruleActiveFilter"


    // $ANTLR start "entryRuleModuleFilter"
    // InternalQLParser.g:614:1: entryRuleModuleFilter returns [EObject current=null] : iv_ruleModuleFilter= ruleModuleFilter EOF ;
    public final EObject entryRuleModuleFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModuleFilter = null;


        try {
            // InternalQLParser.g:614:53: (iv_ruleModuleFilter= ruleModuleFilter EOF )
            // InternalQLParser.g:615:2: iv_ruleModuleFilter= ruleModuleFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getModuleFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleModuleFilter=ruleModuleFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleModuleFilter; 
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
    // $ANTLR end "entryRuleModuleFilter"


    // $ANTLR start "ruleModuleFilter"
    // InternalQLParser.g:621:1: ruleModuleFilter returns [EObject current=null] : ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= ModuleId this_EQUAL_3= RULE_EQUAL ( (lv_moduleId_4_0= ruleExpressionConstraint ) ) ) ;
    public final EObject ruleModuleFilter() throws RecognitionException {
        EObject current = null;

        Token this_DOT_1=null;
        Token otherlv_2=null;
        Token this_EQUAL_3=null;
        Enumerator lv_domain_0_0 = null;

        EObject lv_moduleId_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:627:2: ( ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= ModuleId this_EQUAL_3= RULE_EQUAL ( (lv_moduleId_4_0= ruleExpressionConstraint ) ) ) )
            // InternalQLParser.g:628:2: ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= ModuleId this_EQUAL_3= RULE_EQUAL ( (lv_moduleId_4_0= ruleExpressionConstraint ) ) )
            {
            // InternalQLParser.g:628:2: ( ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= ModuleId this_EQUAL_3= RULE_EQUAL ( (lv_moduleId_4_0= ruleExpressionConstraint ) ) )
            // InternalQLParser.g:629:3: ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )? otherlv_2= ModuleId this_EQUAL_3= RULE_EQUAL ( (lv_moduleId_4_0= ruleExpressionConstraint ) )
            {
            // InternalQLParser.g:629:3: ( ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==Description||LA9_0==Concept) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // InternalQLParser.g:630:4: ( (lv_domain_0_0= ruleDomain ) ) this_DOT_1= RULE_DOT
                    {
                    // InternalQLParser.g:630:4: ( (lv_domain_0_0= ruleDomain ) )
                    // InternalQLParser.g:631:5: (lv_domain_0_0= ruleDomain )
                    {
                    // InternalQLParser.g:631:5: (lv_domain_0_0= ruleDomain )
                    // InternalQLParser.g:632:6: lv_domain_0_0= ruleDomain
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getModuleFilterAccess().getDomainDomainEnumRuleCall_0_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_10);
                    lv_domain_0_0=ruleDomain();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getModuleFilterRule());
                      						}
                      						set(
                      							current,
                      							"domain",
                      							lv_domain_0_0,
                      							"com.b2international.snowowl.snomed.ql.QL.Domain");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_14); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DOT_1, grammarAccess.getModuleFilterAccess().getDOTTerminalRuleCall_0_1());
                      			
                    }

                    }
                    break;

            }

            otherlv_2=(Token)match(input,ModuleId,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_2, grammarAccess.getModuleFilterAccess().getModuleIdKeyword_1());
              		
            }
            this_EQUAL_3=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_3, grammarAccess.getModuleFilterAccess().getEQUALTerminalRuleCall_2());
              		
            }
            // InternalQLParser.g:662:3: ( (lv_moduleId_4_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:663:4: (lv_moduleId_4_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:663:4: (lv_moduleId_4_0= ruleExpressionConstraint )
            // InternalQLParser.g:664:5: lv_moduleId_4_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getModuleFilterAccess().getModuleIdExpressionConstraintParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_moduleId_4_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getModuleFilterRule());
              					}
              					set(
              						current,
              						"moduleId",
              						lv_moduleId_4_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // $ANTLR end "ruleModuleFilter"


    // $ANTLR start "entryRuleTermFilter"
    // InternalQLParser.g:685:1: entryRuleTermFilter returns [EObject current=null] : iv_ruleTermFilter= ruleTermFilter EOF ;
    public final EObject entryRuleTermFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTermFilter = null;


        try {
            // InternalQLParser.g:685:51: (iv_ruleTermFilter= ruleTermFilter EOF )
            // InternalQLParser.g:686:2: iv_ruleTermFilter= ruleTermFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTermFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleTermFilter=ruleTermFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTermFilter; 
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
    // $ANTLR end "entryRuleTermFilter"


    // $ANTLR start "ruleTermFilter"
    // InternalQLParser.g:692:1: ruleTermFilter returns [EObject current=null] : (otherlv_0= Term this_EQUAL_1= RULE_EQUAL ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )? ( (lv_term_4_0= RULE_STRING ) ) ) ;
    public final EObject ruleTermFilter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EQUAL_1=null;
        Token this_COLON_3=null;
        Token lv_term_4_0=null;
        Enumerator lv_lexicalSearchType_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:698:2: ( (otherlv_0= Term this_EQUAL_1= RULE_EQUAL ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )? ( (lv_term_4_0= RULE_STRING ) ) ) )
            // InternalQLParser.g:699:2: (otherlv_0= Term this_EQUAL_1= RULE_EQUAL ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )? ( (lv_term_4_0= RULE_STRING ) ) )
            {
            // InternalQLParser.g:699:2: (otherlv_0= Term this_EQUAL_1= RULE_EQUAL ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )? ( (lv_term_4_0= RULE_STRING ) ) )
            // InternalQLParser.g:700:3: otherlv_0= Term this_EQUAL_1= RULE_EQUAL ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )? ( (lv_term_4_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,Term,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getTermFilterAccess().getTermKeyword_0());
              		
            }
            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_16); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getTermFilterAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:708:3: ( ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==Exact||(LA10_0>=Match && LA10_0<=Regex)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // InternalQLParser.g:709:4: ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) ) this_COLON_3= RULE_COLON
                    {
                    // InternalQLParser.g:709:4: ( (lv_lexicalSearchType_2_0= ruleLexicalSearchType ) )
                    // InternalQLParser.g:710:5: (lv_lexicalSearchType_2_0= ruleLexicalSearchType )
                    {
                    // InternalQLParser.g:710:5: (lv_lexicalSearchType_2_0= ruleLexicalSearchType )
                    // InternalQLParser.g:711:6: lv_lexicalSearchType_2_0= ruleLexicalSearchType
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getTermFilterAccess().getLexicalSearchTypeLexicalSearchTypeEnumRuleCall_2_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_17);
                    lv_lexicalSearchType_2_0=ruleLexicalSearchType();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getTermFilterRule());
                      						}
                      						set(
                      							current,
                      							"lexicalSearchType",
                      							lv_lexicalSearchType_2_0,
                      							"com.b2international.snowowl.snomed.ql.QL.LexicalSearchType");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }


                    }

                    this_COLON_3=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_3, grammarAccess.getTermFilterAccess().getCOLONTerminalRuleCall_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalQLParser.g:733:3: ( (lv_term_4_0= RULE_STRING ) )
            // InternalQLParser.g:734:4: (lv_term_4_0= RULE_STRING )
            {
            // InternalQLParser.g:734:4: (lv_term_4_0= RULE_STRING )
            // InternalQLParser.g:735:5: lv_term_4_0= RULE_STRING
            {
            lv_term_4_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					newLeafNode(lv_term_4_0, grammarAccess.getTermFilterAccess().getTermSTRINGTerminalRuleCall_3_0());
              				
            }
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElement(grammarAccess.getTermFilterRule());
              					}
              					setWithLastConsumed(
              						current,
              						"term",
              						lv_term_4_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.STRING");
              				
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
    // $ANTLR end "ruleTermFilter"


    // $ANTLR start "entryRulePreferredInFilter"
    // InternalQLParser.g:755:1: entryRulePreferredInFilter returns [EObject current=null] : iv_rulePreferredInFilter= rulePreferredInFilter EOF ;
    public final EObject entryRulePreferredInFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePreferredInFilter = null;


        try {
            // InternalQLParser.g:755:58: (iv_rulePreferredInFilter= rulePreferredInFilter EOF )
            // InternalQLParser.g:756:2: iv_rulePreferredInFilter= rulePreferredInFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPreferredInFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePreferredInFilter=rulePreferredInFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePreferredInFilter; 
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
    // $ANTLR end "entryRulePreferredInFilter"


    // $ANTLR start "rulePreferredInFilter"
    // InternalQLParser.g:762:1: rulePreferredInFilter returns [EObject current=null] : (otherlv_0= PreferredIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) ;
    public final EObject rulePreferredInFilter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EQUAL_1=null;
        EObject lv_languageRefSetId_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:768:2: ( (otherlv_0= PreferredIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) )
            // InternalQLParser.g:769:2: (otherlv_0= PreferredIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            {
            // InternalQLParser.g:769:2: (otherlv_0= PreferredIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            // InternalQLParser.g:770:3: otherlv_0= PreferredIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            {
            otherlv_0=(Token)match(input,PreferredIn,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getPreferredInFilterAccess().getPreferredInKeyword_0());
              		
            }
            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getPreferredInFilterAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:778:3: ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:779:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:779:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            // InternalQLParser.g:780:5: lv_languageRefSetId_2_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getPreferredInFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_languageRefSetId_2_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getPreferredInFilterRule());
              					}
              					set(
              						current,
              						"languageRefSetId",
              						lv_languageRefSetId_2_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // $ANTLR end "rulePreferredInFilter"


    // $ANTLR start "entryRuleAcceptableInFilter"
    // InternalQLParser.g:801:1: entryRuleAcceptableInFilter returns [EObject current=null] : iv_ruleAcceptableInFilter= ruleAcceptableInFilter EOF ;
    public final EObject entryRuleAcceptableInFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAcceptableInFilter = null;


        try {
            // InternalQLParser.g:801:59: (iv_ruleAcceptableInFilter= ruleAcceptableInFilter EOF )
            // InternalQLParser.g:802:2: iv_ruleAcceptableInFilter= ruleAcceptableInFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAcceptableInFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAcceptableInFilter=ruleAcceptableInFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAcceptableInFilter; 
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
    // $ANTLR end "entryRuleAcceptableInFilter"


    // $ANTLR start "ruleAcceptableInFilter"
    // InternalQLParser.g:808:1: ruleAcceptableInFilter returns [EObject current=null] : (otherlv_0= AcceptableIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) ;
    public final EObject ruleAcceptableInFilter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EQUAL_1=null;
        EObject lv_languageRefSetId_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:814:2: ( (otherlv_0= AcceptableIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) )
            // InternalQLParser.g:815:2: (otherlv_0= AcceptableIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            {
            // InternalQLParser.g:815:2: (otherlv_0= AcceptableIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            // InternalQLParser.g:816:3: otherlv_0= AcceptableIn this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            {
            otherlv_0=(Token)match(input,AcceptableIn,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getAcceptableInFilterAccess().getAcceptableInKeyword_0());
              		
            }
            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getAcceptableInFilterAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:824:3: ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:825:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:825:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            // InternalQLParser.g:826:5: lv_languageRefSetId_2_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAcceptableInFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_languageRefSetId_2_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAcceptableInFilterRule());
              					}
              					set(
              						current,
              						"languageRefSetId",
              						lv_languageRefSetId_2_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // $ANTLR end "ruleAcceptableInFilter"


    // $ANTLR start "entryRuleLanguageRefSetFilter"
    // InternalQLParser.g:847:1: entryRuleLanguageRefSetFilter returns [EObject current=null] : iv_ruleLanguageRefSetFilter= ruleLanguageRefSetFilter EOF ;
    public final EObject entryRuleLanguageRefSetFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLanguageRefSetFilter = null;


        try {
            // InternalQLParser.g:847:61: (iv_ruleLanguageRefSetFilter= ruleLanguageRefSetFilter EOF )
            // InternalQLParser.g:848:2: iv_ruleLanguageRefSetFilter= ruleLanguageRefSetFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getLanguageRefSetFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleLanguageRefSetFilter=ruleLanguageRefSetFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleLanguageRefSetFilter; 
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
    // $ANTLR end "entryRuleLanguageRefSetFilter"


    // $ANTLR start "ruleLanguageRefSetFilter"
    // InternalQLParser.g:854:1: ruleLanguageRefSetFilter returns [EObject current=null] : (otherlv_0= LanguageRefSet this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) ;
    public final EObject ruleLanguageRefSetFilter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EQUAL_1=null;
        EObject lv_languageRefSetId_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:860:2: ( (otherlv_0= LanguageRefSet this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) ) )
            // InternalQLParser.g:861:2: (otherlv_0= LanguageRefSet this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            {
            // InternalQLParser.g:861:2: (otherlv_0= LanguageRefSet this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) ) )
            // InternalQLParser.g:862:3: otherlv_0= LanguageRefSet this_EQUAL_1= RULE_EQUAL ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            {
            otherlv_0=(Token)match(input,LanguageRefSet,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getLanguageRefSetFilterAccess().getLanguageRefSetKeyword_0());
              		
            }
            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getLanguageRefSetFilterAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:870:3: ( (lv_languageRefSetId_2_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:871:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:871:4: (lv_languageRefSetId_2_0= ruleExpressionConstraint )
            // InternalQLParser.g:872:5: lv_languageRefSetId_2_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getLanguageRefSetFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_languageRefSetId_2_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getLanguageRefSetFilterRule());
              					}
              					set(
              						current,
              						"languageRefSetId",
              						lv_languageRefSetId_2_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // $ANTLR end "ruleLanguageRefSetFilter"


    // $ANTLR start "entryRuleTypeFilter"
    // InternalQLParser.g:893:1: entryRuleTypeFilter returns [EObject current=null] : iv_ruleTypeFilter= ruleTypeFilter EOF ;
    public final EObject entryRuleTypeFilter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeFilter = null;


        try {
            // InternalQLParser.g:893:51: (iv_ruleTypeFilter= ruleTypeFilter EOF )
            // InternalQLParser.g:894:2: iv_ruleTypeFilter= ruleTypeFilter EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTypeFilterRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleTypeFilter=ruleTypeFilter();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTypeFilter; 
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
    // $ANTLR end "entryRuleTypeFilter"


    // $ANTLR start "ruleTypeFilter"
    // InternalQLParser.g:900:1: ruleTypeFilter returns [EObject current=null] : (otherlv_0= TypeId this_EQUAL_1= RULE_EQUAL ( (lv_type_2_0= ruleExpressionConstraint ) ) ) ;
    public final EObject ruleTypeFilter() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EQUAL_1=null;
        EObject lv_type_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:906:2: ( (otherlv_0= TypeId this_EQUAL_1= RULE_EQUAL ( (lv_type_2_0= ruleExpressionConstraint ) ) ) )
            // InternalQLParser.g:907:2: (otherlv_0= TypeId this_EQUAL_1= RULE_EQUAL ( (lv_type_2_0= ruleExpressionConstraint ) ) )
            {
            // InternalQLParser.g:907:2: (otherlv_0= TypeId this_EQUAL_1= RULE_EQUAL ( (lv_type_2_0= ruleExpressionConstraint ) ) )
            // InternalQLParser.g:908:3: otherlv_0= TypeId this_EQUAL_1= RULE_EQUAL ( (lv_type_2_0= ruleExpressionConstraint ) )
            {
            otherlv_0=(Token)match(input,TypeId,FollowSets000.FOLLOW_12); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(otherlv_0, grammarAccess.getTypeFilterAccess().getTypeIdKeyword_0());
              		
            }
            this_EQUAL_1=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_1, grammarAccess.getTypeFilterAccess().getEQUALTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:916:3: ( (lv_type_2_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:917:4: (lv_type_2_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:917:4: (lv_type_2_0= ruleExpressionConstraint )
            // InternalQLParser.g:918:5: lv_type_2_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getTypeFilterAccess().getTypeExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_type_2_0=ruleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getTypeFilterRule());
              					}
              					set(
              						current,
              						"type",
              						lv_type_2_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // $ANTLR end "ruleTypeFilter"


    // $ANTLR start "entryRuleBoolean"
    // InternalQLParser.g:939:1: entryRuleBoolean returns [String current=null] : iv_ruleBoolean= ruleBoolean EOF ;
    public final String entryRuleBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBoolean = null;


        try {
            // InternalQLParser.g:939:47: (iv_ruleBoolean= ruleBoolean EOF )
            // InternalQLParser.g:940:2: iv_ruleBoolean= ruleBoolean EOF
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
        }
        return current;
    }
    // $ANTLR end "entryRuleBoolean"


    // $ANTLR start "ruleBoolean"
    // InternalQLParser.g:946:1: ruleBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= True | kw= False ) ;
    public final AntlrDatatypeRuleToken ruleBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalQLParser.g:952:2: ( (kw= True | kw= False ) )
            // InternalQLParser.g:953:2: (kw= True | kw= False )
            {
            // InternalQLParser.g:953:2: (kw= True | kw= False )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==True) ) {
                alt11=1;
            }
            else if ( (LA11_0==False) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // InternalQLParser.g:954:3: kw= True
                    {
                    kw=(Token)match(input,True,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current.merge(kw);
                      			newLeafNode(kw, grammarAccess.getBooleanAccess().getTrueKeyword_0());
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:960:3: kw= False
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
        }
        return current;
    }
    // $ANTLR end "ruleBoolean"


    // $ANTLR start "entryRuleExpressionConstraint"
    // InternalQLParser.g:969:1: entryRuleExpressionConstraint returns [EObject current=null] : iv_ruleExpressionConstraint= ruleExpressionConstraint EOF ;
    public final EObject entryRuleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionConstraint = null;


        try {
            // InternalQLParser.g:969:61: (iv_ruleExpressionConstraint= ruleExpressionConstraint EOF )
            // InternalQLParser.g:970:2: iv_ruleExpressionConstraint= ruleExpressionConstraint EOF
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
    // InternalQLParser.g:976:1: ruleExpressionConstraint returns [EObject current=null] : this_OrExpressionConstraint_0= ruleOrExpressionConstraint ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_OrExpressionConstraint_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:982:2: (this_OrExpressionConstraint_0= ruleOrExpressionConstraint )
            // InternalQLParser.g:983:2: this_OrExpressionConstraint_0= ruleOrExpressionConstraint
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
    // InternalQLParser.g:997:1: entryRuleOrExpressionConstraint returns [EObject current=null] : iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF ;
    public final EObject entryRuleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpressionConstraint = null;


        try {
            // InternalQLParser.g:997:63: (iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF )
            // InternalQLParser.g:998:2: iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF
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
    // InternalQLParser.g:1004:1: ruleOrExpressionConstraint returns [EObject current=null] : (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) ;
    public final EObject ruleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1010:2: ( (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) )
            // InternalQLParser.g:1011:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            {
            // InternalQLParser.g:1011:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            // InternalQLParser.g:1012:3: this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getAndExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_6);
            this_AndExpressionConstraint_0=ruleAndExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1023:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            loop12:
            do {
                int alt12=2;
                alt12 = dfa12.predict(input);
                switch (alt12) {
            	case 1 :
            	    // InternalQLParser.g:1024:4: () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    {
            	    // InternalQLParser.g:1024:4: ()
            	    // InternalQLParser.g:1025:5: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_2, grammarAccess.getOrExpressionConstraintAccess().getORKeyword_1_1());
            	      			
            	    }
            	    // InternalQLParser.g:1038:4: ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    // InternalQLParser.g:1039:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    {
            	    // InternalQLParser.g:1039:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    // InternalQLParser.g:1040:6: lv_right_3_0= ruleAndExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_6);
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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.AndExpressionConstraint");
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
    // InternalQLParser.g:1062:1: entryRuleAndExpressionConstraint returns [EObject current=null] : iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF ;
    public final EObject entryRuleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpressionConstraint = null;


        try {
            // InternalQLParser.g:1062:64: (iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF )
            // InternalQLParser.g:1063:2: iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF
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
    // InternalQLParser.g:1069:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1075:2: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // InternalQLParser.g:1076:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // InternalQLParser.g:1076:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            // InternalQLParser.g:1077:3: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_7);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_ExclusionExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1088:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            loop14:
            do {
                int alt14=2;
                alt14 = dfa14.predict(input);
                switch (alt14) {
            	case 1 :
            	    // InternalQLParser.g:1089:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // InternalQLParser.g:1089:4: ()
            	    // InternalQLParser.g:1090:5: 
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

            	    // InternalQLParser.g:1099:4: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt13=2;
            	    int LA13_0 = input.LA(1);

            	    if ( (LA13_0==AND) ) {
            	        alt13=1;
            	    }
            	    else if ( (LA13_0==Comma) ) {
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
            	            // InternalQLParser.g:1100:5: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_2, grammarAccess.getAndExpressionConstraintAccess().getANDKeyword_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalQLParser.g:1105:5: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_3, grammarAccess.getAndExpressionConstraintAccess().getCommaKeyword_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalQLParser.g:1110:4: ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    // InternalQLParser.g:1111:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    {
            	    // InternalQLParser.g:1111:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    // InternalQLParser.g:1112:6: lv_right_4_0= ruleExclusionExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_7);
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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.ExclusionExpressionConstraint");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

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
    // InternalQLParser.g:1134:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // InternalQLParser.g:1134:70: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // InternalQLParser.g:1135:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
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
    // InternalQLParser.g:1141:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_RefinedExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1147:2: ( (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) )
            // InternalQLParser.g:1148:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            {
            // InternalQLParser.g:1148:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            // InternalQLParser.g:1149:3: this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_8);
            this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_RefinedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1160:3: ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // InternalQLParser.g:1161:4: () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    {
                    // InternalQLParser.g:1161:4: ()
                    // InternalQLParser.g:1162:5: 
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

                    otherlv_2=(Token)match(input,MINUS,FollowSets000.FOLLOW_15); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSKeyword_1_1());
                      			
                    }
                    // InternalQLParser.g:1175:4: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    // InternalQLParser.g:1176:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    {
                    // InternalQLParser.g:1176:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    // InternalQLParser.g:1177:6: lv_right_3_0= ruleRefinedExpressionConstraint
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
                      							"com.b2international.snowowl.snomed.ecl.Ecl.RefinedExpressionConstraint");
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
    // InternalQLParser.g:1199:1: entryRuleRefinedExpressionConstraint returns [EObject current=null] : iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF ;
    public final EObject entryRuleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinedExpressionConstraint = null;


        try {
            // InternalQLParser.g:1199:68: (iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF )
            // InternalQLParser.g:1200:2: iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF
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
    // InternalQLParser.g:1206:1: ruleRefinedExpressionConstraint returns [EObject current=null] : (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_COLON_2=null;
        EObject this_DottedExpressionConstraint_0 = null;

        EObject lv_refinement_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1212:2: ( (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) )
            // InternalQLParser.g:1213:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            {
            // InternalQLParser.g:1213:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            // InternalQLParser.g:1214:3: this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getDottedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_19);
            this_DottedExpressionConstraint_0=ruleDottedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_DottedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1225:3: ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_COLON) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalQLParser.g:1226:4: () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) )
                    {
                    // InternalQLParser.g:1226:4: ()
                    // InternalQLParser.g:1227:5: 
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

                    this_COLON_2=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_20); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_2, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_1());
                      			
                    }
                    // InternalQLParser.g:1240:4: ( (lv_refinement_3_0= ruleRefinement ) )
                    // InternalQLParser.g:1241:5: (lv_refinement_3_0= ruleRefinement )
                    {
                    // InternalQLParser.g:1241:5: (lv_refinement_3_0= ruleRefinement )
                    // InternalQLParser.g:1242:6: lv_refinement_3_0= ruleRefinement
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementRefinementParserRuleCall_1_2_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_refinement_3_0=ruleRefinement();

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
                      							"com.b2international.snowowl.snomed.ecl.Ecl.Refinement");
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
    // InternalQLParser.g:1264:1: entryRuleDottedExpressionConstraint returns [EObject current=null] : iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF ;
    public final EObject entryRuleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDottedExpressionConstraint = null;


        try {
            // InternalQLParser.g:1264:67: (iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF )
            // InternalQLParser.g:1265:2: iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF
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
    // InternalQLParser.g:1271:1: ruleDottedExpressionConstraint returns [EObject current=null] : (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* ) ;
    public final EObject ruleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_DOT_2=null;
        EObject this_SubExpressionConstraint_0 = null;

        EObject lv_attribute_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1277:2: ( (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* ) )
            // InternalQLParser.g:1278:2: (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* )
            {
            // InternalQLParser.g:1278:2: (this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )* )
            // InternalQLParser.g:1279:3: this_SubExpressionConstraint_0= ruleSubExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSubExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_21);
            this_SubExpressionConstraint_0=ruleSubExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1290:3: ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==RULE_DOT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalQLParser.g:1291:4: () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleSubExpressionConstraint ) )
            	    {
            	    // InternalQLParser.g:1291:4: ()
            	    // InternalQLParser.g:1292:5: 
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
            	    // InternalQLParser.g:1305:4: ( (lv_attribute_3_0= ruleSubExpressionConstraint ) )
            	    // InternalQLParser.g:1306:5: (lv_attribute_3_0= ruleSubExpressionConstraint )
            	    {
            	    // InternalQLParser.g:1306:5: (lv_attribute_3_0= ruleSubExpressionConstraint )
            	    // InternalQLParser.g:1307:6: lv_attribute_3_0= ruleSubExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_21);
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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
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
    // $ANTLR end "ruleDottedExpressionConstraint"


    // $ANTLR start "entryRuleSubExpressionConstraint"
    // InternalQLParser.g:1329:1: entryRuleSubExpressionConstraint returns [EObject current=null] : iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF ;
    public final EObject entryRuleSubExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubExpressionConstraint = null;


        try {
            // InternalQLParser.g:1329:64: (iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF )
            // InternalQLParser.g:1330:2: iv_ruleSubExpressionConstraint= ruleSubExpressionConstraint EOF
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
    // InternalQLParser.g:1336:1: ruleSubExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) ;
    public final EObject ruleSubExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_ChildOf_0 = null;

        EObject this_DescendantOf_1 = null;

        EObject this_DescendantOrSelfOf_2 = null;

        EObject this_ParentOf_3 = null;

        EObject this_AncestorOf_4 = null;

        EObject this_AncestorOrSelfOf_5 = null;

        EObject this_FocusConcept_6 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1342:2: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) )
            // InternalQLParser.g:1343:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            {
            // InternalQLParser.g:1343:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            int alt18=7;
            switch ( input.LA(1) ) {
            case RULE_LT_EM:
                {
                alt18=1;
                }
                break;
            case RULE_LT:
                {
                alt18=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt18=3;
                }
                break;
            case RULE_GT_EM:
                {
                alt18=4;
                }
                break;
            case RULE_GT:
                {
                alt18=5;
                }
                break;
            case RULE_DBL_GT:
                {
                alt18=6;
                }
                break;
            case RULE_DIGIT_NONZERO:
            case RULE_ROUND_OPEN:
            case RULE_CARET:
            case RULE_WILDCARD:
                {
                alt18=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // InternalQLParser.g:1344:3: this_ChildOf_0= ruleChildOf
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
                    // InternalQLParser.g:1356:3: this_DescendantOf_1= ruleDescendantOf
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
                    // InternalQLParser.g:1368:3: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
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
                    // InternalQLParser.g:1380:3: this_ParentOf_3= ruleParentOf
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
                    // InternalQLParser.g:1392:3: this_AncestorOf_4= ruleAncestorOf
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
                    // InternalQLParser.g:1404:3: this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf
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
                    // InternalQLParser.g:1416:3: this_FocusConcept_6= ruleFocusConcept
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getFocusConceptParserRuleCall_6());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_FocusConcept_6=ruleFocusConcept();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_FocusConcept_6;
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


    // $ANTLR start "entryRuleFocusConcept"
    // InternalQLParser.g:1431:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // InternalQLParser.g:1431:53: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // InternalQLParser.g:1432:2: iv_ruleFocusConcept= ruleFocusConcept EOF
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
    // InternalQLParser.g:1438:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;

        EObject this_NestedExpression_3 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1444:2: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) )
            // InternalQLParser.g:1445:2: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            {
            // InternalQLParser.g:1445:2: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            int alt19=4;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt19=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt19=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt19=3;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt19=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // InternalQLParser.g:1446:3: this_MemberOf_0= ruleMemberOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0());
                      		
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
                    // InternalQLParser.g:1458:3: this_ConceptReference_1= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConceptReference_1=ruleConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ConceptReference_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalQLParser.g:1470:3: this_Any_2= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2());
                      		
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
                    // InternalQLParser.g:1482:3: this_NestedExpression_3= ruleNestedExpression
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getFocusConceptAccess().getNestedExpressionParserRuleCall_3());
                      		
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
    // $ANTLR end "ruleFocusConcept"


    // $ANTLR start "entryRuleChildOf"
    // InternalQLParser.g:1497:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // InternalQLParser.g:1497:48: (iv_ruleChildOf= ruleChildOf EOF )
            // InternalQLParser.g:1498:2: iv_ruleChildOf= ruleChildOf EOF
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
    // InternalQLParser.g:1504:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1510:2: ( (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1511:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1511:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1512:3: this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1516:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1517:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1517:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1518:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1539:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // InternalQLParser.g:1539:53: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // InternalQLParser.g:1540:2: iv_ruleDescendantOf= ruleDescendantOf EOF
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
    // InternalQLParser.g:1546:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1552:2: ( (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1553:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1553:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1554:3: this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1558:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1559:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1559:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1560:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1581:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // InternalQLParser.g:1581:59: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // InternalQLParser.g:1582:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
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
    // InternalQLParser.g:1588:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1594:2: ( (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1595:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1595:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1596:3: this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1600:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1601:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1601:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1602:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1623:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // InternalQLParser.g:1623:49: (iv_ruleParentOf= ruleParentOf EOF )
            // InternalQLParser.g:1624:2: iv_ruleParentOf= ruleParentOf EOF
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
    // InternalQLParser.g:1630:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1636:2: ( (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1637:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1637:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1638:3: this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1642:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1643:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1643:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1644:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1665:1: entryRuleAncestorOf returns [EObject current=null] : iv_ruleAncestorOf= ruleAncestorOf EOF ;
    public final EObject entryRuleAncestorOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOf = null;


        try {
            // InternalQLParser.g:1665:51: (iv_ruleAncestorOf= ruleAncestorOf EOF )
            // InternalQLParser.g:1666:2: iv_ruleAncestorOf= ruleAncestorOf EOF
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
    // InternalQLParser.g:1672:1: ruleAncestorOf returns [EObject current=null] : (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1678:2: ( (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1679:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1679:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1680:3: this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1684:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1685:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1685:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1686:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1707:1: entryRuleAncestorOrSelfOf returns [EObject current=null] : iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF ;
    public final EObject entryRuleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOrSelfOf = null;


        try {
            // InternalQLParser.g:1707:57: (iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF )
            // InternalQLParser.g:1708:2: iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF
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
    // InternalQLParser.g:1714:1: ruleAncestorOrSelfOf returns [EObject current=null] : (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1720:2: ( (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalQLParser.g:1721:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalQLParser.g:1721:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalQLParser.g:1722:3: this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_GT_0=(Token)match(input,RULE_DBL_GT,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1726:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalQLParser.g:1727:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalQLParser.g:1727:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalQLParser.g:1728:5: lv_constraint_1_0= ruleFocusConcept
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleFocusConcept();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
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
    // InternalQLParser.g:1749:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // InternalQLParser.g:1749:49: (iv_ruleMemberOf= ruleMemberOf EOF )
            // InternalQLParser.g:1750:2: iv_ruleMemberOf= ruleMemberOf EOF
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
    // InternalQLParser.g:1756:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;

        EObject lv_constraint_1_3 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1762:2: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) ) )
            // InternalQLParser.g:1763:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) )
            {
            // InternalQLParser.g:1763:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) ) )
            // InternalQLParser.g:1764:3: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1768:3: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) ) )
            // InternalQLParser.g:1769:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) )
            {
            // InternalQLParser.g:1769:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression ) )
            // InternalQLParser.g:1770:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression )
            {
            // InternalQLParser.g:1770:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny | lv_constraint_1_3= ruleNestedExpression )
            int alt20=3;
            switch ( input.LA(1) ) {
            case RULE_DIGIT_NONZERO:
                {
                alt20=1;
                }
                break;
            case RULE_WILDCARD:
                {
                alt20=2;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt20=3;
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
                    // InternalQLParser.g:1771:6: lv_constraint_1_1= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_1=ruleConceptReference();

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
                      							"com.b2international.snowowl.snomed.ecl.Ecl.ConceptReference");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:1787:6: lv_constraint_1_2= ruleAny
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
                      							"com.b2international.snowowl.snomed.ecl.Ecl.Any");
                      						afterParserOrEnumRuleCall();
                      					
                    }

                    }
                    break;
                case 3 :
                    // InternalQLParser.g:1803:6: lv_constraint_1_3= ruleNestedExpression
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
                      							"com.b2international.snowowl.snomed.ecl.Ecl.NestedExpression");
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


    // $ANTLR start "entryRuleConceptReference"
    // InternalQLParser.g:1825:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // InternalQLParser.g:1825:57: (iv_ruleConceptReference= ruleConceptReference EOF )
            // InternalQLParser.g:1826:2: iv_ruleConceptReference= ruleConceptReference EOF
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
    // InternalQLParser.g:1832:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token lv_term_1_0=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1838:2: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) )
            // InternalQLParser.g:1839:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            {
            // InternalQLParser.g:1839:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            // InternalQLParser.g:1840:3: ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )?
            {
            // InternalQLParser.g:1840:3: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // InternalQLParser.g:1841:4: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // InternalQLParser.g:1841:4: (lv_id_0_0= ruleSnomedIdentifier )
            // InternalQLParser.g:1842:5: lv_id_0_0= ruleSnomedIdentifier
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_22);
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SnomedIdentifier");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalQLParser.g:1859:3: ( (lv_term_1_0= RULE_TERM_STRING ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_TERM_STRING) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalQLParser.g:1860:4: (lv_term_1_0= RULE_TERM_STRING )
                    {
                    // InternalQLParser.g:1860:4: (lv_term_1_0= RULE_TERM_STRING )
                    // InternalQLParser.g:1861:5: lv_term_1_0= RULE_TERM_STRING
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
                      						"com.b2international.snowowl.snomed.ecl.Ecl.TERM_STRING");
                      				
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


    // $ANTLR start "entryRuleAny"
    // InternalQLParser.g:1881:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // InternalQLParser.g:1881:44: (iv_ruleAny= ruleAny EOF )
            // InternalQLParser.g:1882:2: iv_ruleAny= ruleAny EOF
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
    // InternalQLParser.g:1888:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;


        	enterRule();

        try {
            // InternalQLParser.g:1894:2: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // InternalQLParser.g:1895:2: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // InternalQLParser.g:1895:2: (this_WILDCARD_0= RULE_WILDCARD () )
            // InternalQLParser.g:1896:3: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:1900:3: ()
            // InternalQLParser.g:1901:4: 
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


    // $ANTLR start "entryRuleRefinement"
    // InternalQLParser.g:1914:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // InternalQLParser.g:1914:51: (iv_ruleRefinement= ruleRefinement EOF )
            // InternalQLParser.g:1915:2: iv_ruleRefinement= ruleRefinement EOF
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
    // InternalQLParser.g:1921:1: ruleRefinement returns [EObject current=null] : this_OrRefinement_0= ruleOrRefinement ;
    public final EObject ruleRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_OrRefinement_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1927:2: (this_OrRefinement_0= ruleOrRefinement )
            // InternalQLParser.g:1928:2: this_OrRefinement_0= ruleOrRefinement
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getRefinementAccess().getOrRefinementParserRuleCall());
              	
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
    // $ANTLR end "ruleRefinement"


    // $ANTLR start "entryRuleOrRefinement"
    // InternalQLParser.g:1942:1: entryRuleOrRefinement returns [EObject current=null] : iv_ruleOrRefinement= ruleOrRefinement EOF ;
    public final EObject entryRuleOrRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrRefinement = null;


        try {
            // InternalQLParser.g:1942:53: (iv_ruleOrRefinement= ruleOrRefinement EOF )
            // InternalQLParser.g:1943:2: iv_ruleOrRefinement= ruleOrRefinement EOF
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
    // InternalQLParser.g:1949:1: ruleOrRefinement returns [EObject current=null] : (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) ;
    public final EObject ruleOrRefinement() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndRefinement_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:1955:2: ( (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) )
            // InternalQLParser.g:1956:2: (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            {
            // InternalQLParser.g:1956:2: (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            // InternalQLParser.g:1957:3: this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_6);
            this_AndRefinement_0=ruleAndRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:1968:3: ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            loop22:
            do {
                int alt22=2;
                alt22 = dfa22.predict(input);
                switch (alt22) {
            	case 1 :
            	    // InternalQLParser.g:1969:4: ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    {
            	    // InternalQLParser.g:1970:4: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    // InternalQLParser.g:1971:5: () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) )
            	    {
            	    // InternalQLParser.g:1971:5: ()
            	    // InternalQLParser.g:1972:6: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_20); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					newLeafNode(otherlv_2, grammarAccess.getOrRefinementAccess().getORKeyword_1_0_1());
            	      				
            	    }
            	    // InternalQLParser.g:1985:5: ( (lv_right_3_0= ruleAndRefinement ) )
            	    // InternalQLParser.g:1986:6: (lv_right_3_0= ruleAndRefinement )
            	    {
            	    // InternalQLParser.g:1986:6: (lv_right_3_0= ruleAndRefinement )
            	    // InternalQLParser.g:1987:7: lv_right_3_0= ruleAndRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_6);
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
            	      								"com.b2international.snowowl.snomed.ecl.Ecl.AndRefinement");
            	      							afterParserOrEnumRuleCall();
            	      						
            	    }

            	    }


            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
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
    // InternalQLParser.g:2010:1: entryRuleAndRefinement returns [EObject current=null] : iv_ruleAndRefinement= ruleAndRefinement EOF ;
    public final EObject entryRuleAndRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndRefinement = null;


        try {
            // InternalQLParser.g:2010:54: (iv_ruleAndRefinement= ruleAndRefinement EOF )
            // InternalQLParser.g:2011:2: iv_ruleAndRefinement= ruleAndRefinement EOF
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
    // InternalQLParser.g:2017:1: ruleAndRefinement returns [EObject current=null] : (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) ;
    public final EObject ruleAndRefinement() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_SubRefinement_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2023:2: ( (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) )
            // InternalQLParser.g:2024:2: (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            {
            // InternalQLParser.g:2024:2: (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            // InternalQLParser.g:2025:3: this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndRefinementAccess().getSubRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_7);
            this_SubRefinement_0=ruleSubRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:2036:3: ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            loop24:
            do {
                int alt24=2;
                alt24 = dfa24.predict(input);
                switch (alt24) {
            	case 1 :
            	    // InternalQLParser.g:2037:4: ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    {
            	    // InternalQLParser.g:2038:4: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    // InternalQLParser.g:2039:5: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) )
            	    {
            	    // InternalQLParser.g:2039:5: ()
            	    // InternalQLParser.g:2040:6: 
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

            	    // InternalQLParser.g:2049:5: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==AND) ) {
            	        alt23=1;
            	    }
            	    else if ( (LA23_0==Comma) ) {
            	        alt23=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 23, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // InternalQLParser.g:2050:6: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_20); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(otherlv_2, grammarAccess.getAndRefinementAccess().getANDKeyword_1_0_1_0());
            	              					
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalQLParser.g:2055:6: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_20); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(otherlv_3, grammarAccess.getAndRefinementAccess().getCommaKeyword_1_0_1_1());
            	              					
            	            }

            	            }
            	            break;

            	    }

            	    // InternalQLParser.g:2060:5: ( (lv_right_4_0= ruleSubRefinement ) )
            	    // InternalQLParser.g:2061:6: (lv_right_4_0= ruleSubRefinement )
            	    {
            	    // InternalQLParser.g:2061:6: (lv_right_4_0= ruleSubRefinement )
            	    // InternalQLParser.g:2062:7: lv_right_4_0= ruleSubRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_7);
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
            	      								"com.b2international.snowowl.snomed.ecl.Ecl.SubRefinement");
            	      							afterParserOrEnumRuleCall();
            	      						
            	    }

            	    }


            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop24;
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
    // InternalQLParser.g:2085:1: entryRuleSubRefinement returns [EObject current=null] : iv_ruleSubRefinement= ruleSubRefinement EOF ;
    public final EObject entryRuleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubRefinement = null;


        try {
            // InternalQLParser.g:2085:54: (iv_ruleSubRefinement= ruleSubRefinement EOF )
            // InternalQLParser.g:2086:2: iv_ruleSubRefinement= ruleSubRefinement EOF
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
    // InternalQLParser.g:2092:1: ruleSubRefinement returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) ;
    public final EObject ruleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_AttributeGroup_1 = null;

        EObject this_NestedRefinement_2 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2098:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) )
            // InternalQLParser.g:2099:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            {
            // InternalQLParser.g:2099:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            int alt25=3;
            alt25 = dfa25.predict(input);
            switch (alt25) {
                case 1 :
                    // InternalQLParser.g:2100:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
                    // InternalQLParser.g:2112:3: this_AttributeGroup_1= ruleAttributeGroup
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSubRefinementAccess().getAttributeGroupParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeGroup_1=ruleAttributeGroup();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeGroup_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalQLParser.g:2124:3: this_NestedRefinement_2= ruleNestedRefinement
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
    // InternalQLParser.g:2139:1: entryRuleNestedRefinement returns [EObject current=null] : iv_ruleNestedRefinement= ruleNestedRefinement EOF ;
    public final EObject entryRuleNestedRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedRefinement = null;


        try {
            // InternalQLParser.g:2139:57: (iv_ruleNestedRefinement= ruleNestedRefinement EOF )
            // InternalQLParser.g:2140:2: iv_ruleNestedRefinement= ruleNestedRefinement EOF
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
    // InternalQLParser.g:2146:1: ruleNestedRefinement returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedRefinement() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2152:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalQLParser.g:2153:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalQLParser.g:2153:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalQLParser.g:2154:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_20); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedRefinementAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:2158:3: ( (lv_nested_1_0= ruleRefinement ) )
            // InternalQLParser.g:2159:4: (lv_nested_1_0= ruleRefinement )
            {
            // InternalQLParser.g:2159:4: (lv_nested_1_0= ruleRefinement )
            // InternalQLParser.g:2160:5: lv_nested_1_0= ruleRefinement
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedRefinementAccess().getNestedRefinementParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_9);
            lv_nested_1_0=ruleRefinement();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Refinement");
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


    // $ANTLR start "entryRuleAttributeGroup"
    // InternalQLParser.g:2185:1: entryRuleAttributeGroup returns [EObject current=null] : iv_ruleAttributeGroup= ruleAttributeGroup EOF ;
    public final EObject entryRuleAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeGroup = null;


        try {
            // InternalQLParser.g:2185:55: (iv_ruleAttributeGroup= ruleAttributeGroup EOF )
            // InternalQLParser.g:2186:2: iv_ruleAttributeGroup= ruleAttributeGroup EOF
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
    // InternalQLParser.g:2192:1: ruleAttributeGroup returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) ;
    public final EObject ruleAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_CURLY_OPEN_1=null;
        Token this_CURLY_CLOSE_3=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_refinement_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2198:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) )
            // InternalQLParser.g:2199:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            {
            // InternalQLParser.g:2199:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            // InternalQLParser.g:2200:3: ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE
            {
            // InternalQLParser.g:2200:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_SQUARE_OPEN) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalQLParser.g:2201:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalQLParser.g:2201:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalQLParser.g:2202:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeGroupAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_23);
                    lv_cardinality_0_0=ruleCardinality();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
                      					}
                      					set(
                      						current,
                      						"cardinality",
                      						lv_cardinality_0_0,
                      						"com.b2international.snowowl.snomed.ecl.Ecl.Cardinality");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            this_CURLY_OPEN_1=(Token)match(input,RULE_CURLY_OPEN,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:2223:3: ( (lv_refinement_2_0= ruleAttributeSet ) )
            // InternalQLParser.g:2224:4: (lv_refinement_2_0= ruleAttributeSet )
            {
            // InternalQLParser.g:2224:4: (lv_refinement_2_0= ruleAttributeSet )
            // InternalQLParser.g:2225:5: lv_refinement_2_0= ruleAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeGroupAccess().getRefinementAttributeSetParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_25);
            lv_refinement_2_0=ruleAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              					if (current==null) {
              						current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
              					}
              					set(
              						current,
              						"refinement",
              						lv_refinement_2_0,
              						"com.b2international.snowowl.snomed.ecl.Ecl.AttributeSet");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_CURLY_CLOSE_3=(Token)match(input,RULE_CURLY_CLOSE,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_CLOSE_3, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
              		
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
    // InternalQLParser.g:2250:1: entryRuleAttributeSet returns [EObject current=null] : iv_ruleAttributeSet= ruleAttributeSet EOF ;
    public final EObject entryRuleAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeSet = null;


        try {
            // InternalQLParser.g:2250:53: (iv_ruleAttributeSet= ruleAttributeSet EOF )
            // InternalQLParser.g:2251:2: iv_ruleAttributeSet= ruleAttributeSet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeSetRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeSet=ruleAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeSet; 
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
    // $ANTLR end "entryRuleAttributeSet"


    // $ANTLR start "ruleAttributeSet"
    // InternalQLParser.g:2257:1: ruleAttributeSet returns [EObject current=null] : this_OrAttributeSet_0= ruleOrAttributeSet ;
    public final EObject ruleAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_OrAttributeSet_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2263:2: (this_OrAttributeSet_0= ruleOrAttributeSet )
            // InternalQLParser.g:2264:2: this_OrAttributeSet_0= ruleOrAttributeSet
            {
            if ( state.backtracking==0 ) {

              		/* */
              	
            }
            if ( state.backtracking==0 ) {

              		newCompositeNode(grammarAccess.getAttributeSetAccess().getOrAttributeSetParserRuleCall());
              	
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
    // $ANTLR end "ruleAttributeSet"


    // $ANTLR start "entryRuleOrAttributeSet"
    // InternalQLParser.g:2278:1: entryRuleOrAttributeSet returns [EObject current=null] : iv_ruleOrAttributeSet= ruleOrAttributeSet EOF ;
    public final EObject entryRuleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrAttributeSet = null;


        try {
            // InternalQLParser.g:2278:55: (iv_ruleOrAttributeSet= ruleOrAttributeSet EOF )
            // InternalQLParser.g:2279:2: iv_ruleOrAttributeSet= ruleOrAttributeSet EOF
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
    // InternalQLParser.g:2285:1: ruleOrAttributeSet returns [EObject current=null] : (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) ;
    public final EObject ruleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndAttributeSet_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2291:2: ( (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) )
            // InternalQLParser.g:2292:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            {
            // InternalQLParser.g:2292:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            // InternalQLParser.g:2293:3: this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrAttributeSetAccess().getAndAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_6);
            this_AndAttributeSet_0=ruleAndAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:2304:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==OR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // InternalQLParser.g:2305:4: () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    {
            	    // InternalQLParser.g:2305:4: ()
            	    // InternalQLParser.g:2306:5: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_24); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_2, grammarAccess.getOrAttributeSetAccess().getORKeyword_1_1());
            	      			
            	    }
            	    // InternalQLParser.g:2319:4: ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    // InternalQLParser.g:2320:5: (lv_right_3_0= ruleAndAttributeSet )
            	    {
            	    // InternalQLParser.g:2320:5: (lv_right_3_0= ruleAndAttributeSet )
            	    // InternalQLParser.g:2321:6: lv_right_3_0= ruleAndAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrAttributeSetAccess().getRightAndAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_6);
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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.AndAttributeSet");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


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
        }
        return current;
    }
    // $ANTLR end "ruleOrAttributeSet"


    // $ANTLR start "entryRuleAndAttributeSet"
    // InternalQLParser.g:2343:1: entryRuleAndAttributeSet returns [EObject current=null] : iv_ruleAndAttributeSet= ruleAndAttributeSet EOF ;
    public final EObject entryRuleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndAttributeSet = null;


        try {
            // InternalQLParser.g:2343:56: (iv_ruleAndAttributeSet= ruleAndAttributeSet EOF )
            // InternalQLParser.g:2344:2: iv_ruleAndAttributeSet= ruleAndAttributeSet EOF
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
    // InternalQLParser.g:2350:1: ruleAndAttributeSet returns [EObject current=null] : (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) ;
    public final EObject ruleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_SubAttributeSet_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2356:2: ( (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) )
            // InternalQLParser.g:2357:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            {
            // InternalQLParser.g:2357:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            // InternalQLParser.g:2358:3: this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndAttributeSetAccess().getSubAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_7);
            this_SubAttributeSet_0=ruleSubAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalQLParser.g:2369:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==AND||LA29_0==Comma) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // InternalQLParser.g:2370:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    {
            	    // InternalQLParser.g:2370:4: ()
            	    // InternalQLParser.g:2371:5: 
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

            	    // InternalQLParser.g:2380:4: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt28=2;
            	    int LA28_0 = input.LA(1);

            	    if ( (LA28_0==AND) ) {
            	        alt28=1;
            	    }
            	    else if ( (LA28_0==Comma) ) {
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
            	            // InternalQLParser.g:2381:5: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_24); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_2, grammarAccess.getAndAttributeSetAccess().getANDKeyword_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalQLParser.g:2386:5: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_24); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_3, grammarAccess.getAndAttributeSetAccess().getCommaKeyword_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalQLParser.g:2391:4: ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    // InternalQLParser.g:2392:5: (lv_right_4_0= ruleSubAttributeSet )
            	    {
            	    // InternalQLParser.g:2392:5: (lv_right_4_0= ruleSubAttributeSet )
            	    // InternalQLParser.g:2393:6: lv_right_4_0= ruleSubAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_7);
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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.SubAttributeSet");
            	      						afterParserOrEnumRuleCall();
            	      					
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
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
    // InternalQLParser.g:2415:1: entryRuleSubAttributeSet returns [EObject current=null] : iv_ruleSubAttributeSet= ruleSubAttributeSet EOF ;
    public final EObject entryRuleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubAttributeSet = null;


        try {
            // InternalQLParser.g:2415:56: (iv_ruleSubAttributeSet= ruleSubAttributeSet EOF )
            // InternalQLParser.g:2416:2: iv_ruleSubAttributeSet= ruleSubAttributeSet EOF
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
    // InternalQLParser.g:2422:1: ruleSubAttributeSet returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) ;
    public final EObject ruleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_NestedAttributeSet_1 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2428:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) )
            // InternalQLParser.g:2429:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            {
            // InternalQLParser.g:2429:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            int alt30=2;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // InternalQLParser.g:2430:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
                    // InternalQLParser.g:2442:3: this_NestedAttributeSet_1= ruleNestedAttributeSet
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
    // InternalQLParser.g:2457:1: entryRuleNestedAttributeSet returns [EObject current=null] : iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF ;
    public final EObject entryRuleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedAttributeSet = null;


        try {
            // InternalQLParser.g:2457:59: (iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF )
            // InternalQLParser.g:2458:2: iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF
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
    // InternalQLParser.g:2464:1: ruleNestedAttributeSet returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2470:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalQLParser.g:2471:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalQLParser.g:2471:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalQLParser.g:2472:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedAttributeSetAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:2476:3: ( (lv_nested_1_0= ruleAttributeSet ) )
            // InternalQLParser.g:2477:4: (lv_nested_1_0= ruleAttributeSet )
            {
            // InternalQLParser.g:2477:4: (lv_nested_1_0= ruleAttributeSet )
            // InternalQLParser.g:2478:5: lv_nested_1_0= ruleAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedAttributeSetAccess().getNestedAttributeSetParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_9);
            lv_nested_1_0=ruleAttributeSet();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.AttributeSet");
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
    // InternalQLParser.g:2503:1: entryRuleAttributeConstraint returns [EObject current=null] : iv_ruleAttributeConstraint= ruleAttributeConstraint EOF ;
    public final EObject entryRuleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeConstraint = null;


        try {
            // InternalQLParser.g:2503:60: (iv_ruleAttributeConstraint= ruleAttributeConstraint EOF )
            // InternalQLParser.g:2504:2: iv_ruleAttributeConstraint= ruleAttributeConstraint EOF
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
    // InternalQLParser.g:2510:1: ruleAttributeConstraint returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) ;
    public final EObject ruleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        Token lv_reversed_1_0=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_attribute_2_0 = null;

        EObject lv_comparison_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2516:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) )
            // InternalQLParser.g:2517:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            {
            // InternalQLParser.g:2517:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            // InternalQLParser.g:2518:3: ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleSubExpressionConstraint ) ) ( (lv_comparison_3_0= ruleComparison ) )
            {
            // InternalQLParser.g:2518:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_SQUARE_OPEN) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // InternalQLParser.g:2519:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalQLParser.g:2519:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalQLParser.g:2520:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_26);
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
                      						"com.b2international.snowowl.snomed.ecl.Ecl.Cardinality");
                      					afterParserOrEnumRuleCall();
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalQLParser.g:2537:3: ( (lv_reversed_1_0= RULE_REVERSED ) )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==RULE_REVERSED) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalQLParser.g:2538:4: (lv_reversed_1_0= RULE_REVERSED )
                    {
                    // InternalQLParser.g:2538:4: (lv_reversed_1_0= RULE_REVERSED )
                    // InternalQLParser.g:2539:5: lv_reversed_1_0= RULE_REVERSED
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
                      						"com.b2international.snowowl.snomed.ecl.Ecl.REVERSED");
                      				
                    }

                    }


                    }
                    break;

            }

            // InternalQLParser.g:2555:3: ( (lv_attribute_2_0= ruleSubExpressionConstraint ) )
            // InternalQLParser.g:2556:4: (lv_attribute_2_0= ruleSubExpressionConstraint )
            {
            // InternalQLParser.g:2556:4: (lv_attribute_2_0= ruleSubExpressionConstraint )
            // InternalQLParser.g:2557:5: lv_attribute_2_0= ruleSubExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_27);
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalQLParser.g:2574:3: ( (lv_comparison_3_0= ruleComparison ) )
            // InternalQLParser.g:2575:4: (lv_comparison_3_0= ruleComparison )
            {
            // InternalQLParser.g:2575:4: (lv_comparison_3_0= ruleComparison )
            // InternalQLParser.g:2576:5: lv_comparison_3_0= ruleComparison
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Comparison");
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
    // InternalQLParser.g:2597:1: entryRuleCardinality returns [EObject current=null] : iv_ruleCardinality= ruleCardinality EOF ;
    public final EObject entryRuleCardinality() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCardinality = null;


        try {
            // InternalQLParser.g:2597:52: (iv_ruleCardinality= ruleCardinality EOF )
            // InternalQLParser.g:2598:2: iv_ruleCardinality= ruleCardinality EOF
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
    // InternalQLParser.g:2604:1: ruleCardinality returns [EObject current=null] : (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) ;
    public final EObject ruleCardinality() throws RecognitionException {
        EObject current = null;

        Token this_SQUARE_OPEN_0=null;
        Token this_TO_2=null;
        Token this_SQUARE_CLOSE_4=null;
        AntlrDatatypeRuleToken lv_min_1_0 = null;

        AntlrDatatypeRuleToken lv_max_3_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2610:2: ( (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) )
            // InternalQLParser.g:2611:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            {
            // InternalQLParser.g:2611:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            // InternalQLParser.g:2612:3: this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE
            {
            this_SQUARE_OPEN_0=(Token)match(input,RULE_SQUARE_OPEN,FollowSets000.FOLLOW_28); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_SQUARE_OPEN_0, grammarAccess.getCardinalityAccess().getSQUARE_OPENTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:2616:3: ( (lv_min_1_0= ruleNonNegativeInteger ) )
            // InternalQLParser.g:2617:4: (lv_min_1_0= ruleNonNegativeInteger )
            {
            // InternalQLParser.g:2617:4: (lv_min_1_0= ruleNonNegativeInteger )
            // InternalQLParser.g:2618:5: lv_min_1_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_29);
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.NonNegativeInteger");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            this_TO_2=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_30); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_TO_2, grammarAccess.getCardinalityAccess().getTOTerminalRuleCall_2());
              		
            }
            // InternalQLParser.g:2639:3: ( (lv_max_3_0= ruleMaxValue ) )
            // InternalQLParser.g:2640:4: (lv_max_3_0= ruleMaxValue )
            {
            // InternalQLParser.g:2640:4: (lv_max_3_0= ruleMaxValue )
            // InternalQLParser.g:2641:5: lv_max_3_0= ruleMaxValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_31);
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.MaxValue");
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
    // InternalQLParser.g:2666:1: entryRuleComparison returns [EObject current=null] : iv_ruleComparison= ruleComparison EOF ;
    public final EObject entryRuleComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComparison = null;


        try {
            // InternalQLParser.g:2666:51: (iv_ruleComparison= ruleComparison EOF )
            // InternalQLParser.g:2667:2: iv_ruleComparison= ruleComparison EOF
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
    // InternalQLParser.g:2673:1: ruleComparison returns [EObject current=null] : (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) ;
    public final EObject ruleComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeComparison_0 = null;

        EObject this_DataTypeComparison_1 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2679:2: ( (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) )
            // InternalQLParser.g:2680:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            {
            // InternalQLParser.g:2680:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            int alt33=2;
            switch ( input.LA(1) ) {
            case RULE_EQUAL:
                {
                int LA33_1 = input.LA(2);

                if ( (LA33_1==RULE_HASH||LA33_1==RULE_STRING) ) {
                    alt33=2;
                }
                else if ( (LA33_1==RULE_DIGIT_NONZERO||LA33_1==RULE_ROUND_OPEN||LA33_1==RULE_CARET||LA33_1==RULE_WILDCARD||(LA33_1>=RULE_LT && LA33_1<=RULE_GT_EM)) ) {
                    alt33=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 1, input);

                    throw nvae;
                }
                }
                break;
            case RULE_NOT_EQUAL:
                {
                int LA33_2 = input.LA(2);

                if ( (LA33_2==RULE_HASH||LA33_2==RULE_STRING) ) {
                    alt33=2;
                }
                else if ( (LA33_2==RULE_DIGIT_NONZERO||LA33_2==RULE_ROUND_OPEN||LA33_2==RULE_CARET||LA33_2==RULE_WILDCARD||(LA33_2>=RULE_LT && LA33_2<=RULE_GT_EM)) ) {
                    alt33=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 2, input);

                    throw nvae;
                }
                }
                break;
            case RULE_LT:
            case RULE_GT:
            case RULE_GTE:
            case RULE_LTE:
                {
                alt33=2;
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
                    // InternalQLParser.g:2681:3: this_AttributeComparison_0= ruleAttributeComparison
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
                    // InternalQLParser.g:2693:3: this_DataTypeComparison_1= ruleDataTypeComparison
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
    // InternalQLParser.g:2708:1: entryRuleAttributeComparison returns [EObject current=null] : iv_ruleAttributeComparison= ruleAttributeComparison EOF ;
    public final EObject entryRuleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeComparison = null;


        try {
            // InternalQLParser.g:2708:60: (iv_ruleAttributeComparison= ruleAttributeComparison EOF )
            // InternalQLParser.g:2709:2: iv_ruleAttributeComparison= ruleAttributeComparison EOF
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
    // InternalQLParser.g:2715:1: ruleAttributeComparison returns [EObject current=null] : (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) ;
    public final EObject ruleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeValueEquals_0 = null;

        EObject this_AttributeValueNotEquals_1 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2721:2: ( (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) )
            // InternalQLParser.g:2722:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            {
            // InternalQLParser.g:2722:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==RULE_EQUAL) ) {
                alt34=1;
            }
            else if ( (LA34_0==RULE_NOT_EQUAL) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // InternalQLParser.g:2723:3: this_AttributeValueEquals_0= ruleAttributeValueEquals
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
                    // InternalQLParser.g:2735:3: this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals
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
    // InternalQLParser.g:2750:1: entryRuleDataTypeComparison returns [EObject current=null] : iv_ruleDataTypeComparison= ruleDataTypeComparison EOF ;
    public final EObject entryRuleDataTypeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataTypeComparison = null;


        try {
            // InternalQLParser.g:2750:59: (iv_ruleDataTypeComparison= ruleDataTypeComparison EOF )
            // InternalQLParser.g:2751:2: iv_ruleDataTypeComparison= ruleDataTypeComparison EOF
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
    // InternalQLParser.g:2757:1: ruleDataTypeComparison returns [EObject current=null] : (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals ) ;
    public final EObject ruleDataTypeComparison() throws RecognitionException {
        EObject current = null;

        EObject this_StringValueEquals_0 = null;

        EObject this_StringValueNotEquals_1 = null;

        EObject this_IntegerValueEquals_2 = null;

        EObject this_IntegerValueNotEquals_3 = null;

        EObject this_IntegerValueGreaterThan_4 = null;

        EObject this_IntegerValueGreaterThanEquals_5 = null;

        EObject this_IntegerValueLessThan_6 = null;

        EObject this_IntegerValueLessThanEquals_7 = null;

        EObject this_DecimalValueEquals_8 = null;

        EObject this_DecimalValueNotEquals_9 = null;

        EObject this_DecimalValueGreaterThan_10 = null;

        EObject this_DecimalValueGreaterThanEquals_11 = null;

        EObject this_DecimalValueLessThan_12 = null;

        EObject this_DecimalValueLessThanEquals_13 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2763:2: ( (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals ) )
            // InternalQLParser.g:2764:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )
            {
            // InternalQLParser.g:2764:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )
            int alt35=14;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // InternalQLParser.g:2765:3: this_StringValueEquals_0= ruleStringValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueEqualsParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringValueEquals_0=ruleStringValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringValueEquals_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:2777:3: this_StringValueNotEquals_1= ruleStringValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueNotEqualsParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringValueNotEquals_1=ruleStringValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_StringValueNotEquals_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalQLParser.g:2789:3: this_IntegerValueEquals_2= ruleIntegerValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueEqualsParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueEquals_2=ruleIntegerValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueEquals_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalQLParser.g:2801:3: this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueNotEqualsParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueNotEquals_3=ruleIntegerValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueNotEquals_3;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 5 :
                    // InternalQLParser.g:2813:3: this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanParserRuleCall_4());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueGreaterThan_4=ruleIntegerValueGreaterThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueGreaterThan_4;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 6 :
                    // InternalQLParser.g:2825:3: this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanEqualsParserRuleCall_5());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueGreaterThanEquals_5=ruleIntegerValueGreaterThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueGreaterThanEquals_5;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 7 :
                    // InternalQLParser.g:2837:3: this_IntegerValueLessThan_6= ruleIntegerValueLessThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanParserRuleCall_6());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueLessThan_6=ruleIntegerValueLessThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueLessThan_6;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 8 :
                    // InternalQLParser.g:2849:3: this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanEqualsParserRuleCall_7());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerValueLessThanEquals_7=ruleIntegerValueLessThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_IntegerValueLessThanEquals_7;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 9 :
                    // InternalQLParser.g:2861:3: this_DecimalValueEquals_8= ruleDecimalValueEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueEqualsParserRuleCall_8());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueEquals_8=ruleDecimalValueEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueEquals_8;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 10 :
                    // InternalQLParser.g:2873:3: this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueNotEqualsParserRuleCall_9());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueNotEquals_9=ruleDecimalValueNotEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueNotEquals_9;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 11 :
                    // InternalQLParser.g:2885:3: this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanParserRuleCall_10());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueGreaterThan_10=ruleDecimalValueGreaterThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueGreaterThan_10;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 12 :
                    // InternalQLParser.g:2897:3: this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanEqualsParserRuleCall_11());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueGreaterThanEquals_11=ruleDecimalValueGreaterThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueGreaterThanEquals_11;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 13 :
                    // InternalQLParser.g:2909:3: this_DecimalValueLessThan_12= ruleDecimalValueLessThan
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanParserRuleCall_12());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueLessThan_12=ruleDecimalValueLessThan();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueLessThan_12;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 14 :
                    // InternalQLParser.g:2921:3: this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanEqualsParserRuleCall_13());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_DecimalValueLessThanEquals_13=ruleDecimalValueLessThanEquals();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_DecimalValueLessThanEquals_13;
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
    // InternalQLParser.g:2936:1: entryRuleAttributeValueEquals returns [EObject current=null] : iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF ;
    public final EObject entryRuleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueEquals = null;


        try {
            // InternalQLParser.g:2936:61: (iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF )
            // InternalQLParser.g:2937:2: iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF
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
    // InternalQLParser.g:2943:1: ruleAttributeValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2949:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) )
            // InternalQLParser.g:2950:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            {
            // InternalQLParser.g:2950:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            // InternalQLParser.g:2951:3: this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:2955:3: ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            // InternalQLParser.g:2956:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            {
            // InternalQLParser.g:2956:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            // InternalQLParser.g:2957:5: lv_constraint_1_0= ruleSubExpressionConstraint
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
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
    // InternalQLParser.g:2978:1: entryRuleAttributeValueNotEquals returns [EObject current=null] : iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF ;
    public final EObject entryRuleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueNotEquals = null;


        try {
            // InternalQLParser.g:2978:64: (iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF )
            // InternalQLParser.g:2979:2: iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF
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
    // InternalQLParser.g:2985:1: ruleAttributeValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:2991:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) ) )
            // InternalQLParser.g:2992:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            {
            // InternalQLParser.g:2992:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) ) )
            // InternalQLParser.g:2993:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:2997:3: ( (lv_constraint_1_0= ruleSubExpressionConstraint ) )
            // InternalQLParser.g:2998:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            {
            // InternalQLParser.g:2998:4: (lv_constraint_1_0= ruleSubExpressionConstraint )
            // InternalQLParser.g:2999:5: lv_constraint_1_0= ruleSubExpressionConstraint
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
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


    // $ANTLR start "entryRuleStringValueEquals"
    // InternalQLParser.g:3020:1: entryRuleStringValueEquals returns [EObject current=null] : iv_ruleStringValueEquals= ruleStringValueEquals EOF ;
    public final EObject entryRuleStringValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueEquals = null;


        try {
            // InternalQLParser.g:3020:58: (iv_ruleStringValueEquals= ruleStringValueEquals EOF )
            // InternalQLParser.g:3021:2: iv_ruleStringValueEquals= ruleStringValueEquals EOF
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
    // InternalQLParser.g:3027:1: ruleStringValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalQLParser.g:3033:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalQLParser.g:3034:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalQLParser.g:3034:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalQLParser.g:3035:3: this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_18); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getStringValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:3039:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalQLParser.g:3040:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalQLParser.g:3040:4: (lv_value_1_0= RULE_STRING )
            // InternalQLParser.g:3041:5: lv_value_1_0= RULE_STRING
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.STRING");
              				
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
    // InternalQLParser.g:3061:1: entryRuleStringValueNotEquals returns [EObject current=null] : iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF ;
    public final EObject entryRuleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueNotEquals = null;


        try {
            // InternalQLParser.g:3061:61: (iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF )
            // InternalQLParser.g:3062:2: iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF
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
    // InternalQLParser.g:3068:1: ruleStringValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalQLParser.g:3074:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalQLParser.g:3075:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalQLParser.g:3075:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalQLParser.g:3076:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_18); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getStringValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:3080:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalQLParser.g:3081:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalQLParser.g:3081:4: (lv_value_1_0= RULE_STRING )
            // InternalQLParser.g:3082:5: lv_value_1_0= RULE_STRING
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.STRING");
              				
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
    // InternalQLParser.g:3102:1: entryRuleIntegerValueEquals returns [EObject current=null] : iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF ;
    public final EObject entryRuleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueEquals = null;


        try {
            // InternalQLParser.g:3102:59: (iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF )
            // InternalQLParser.g:3103:2: iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF
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
    // InternalQLParser.g:3109:1: ruleIntegerValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3115:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3116:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3116:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3117:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getIntegerValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3125:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3126:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3126:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3127:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3148:1: entryRuleIntegerValueNotEquals returns [EObject current=null] : iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF ;
    public final EObject entryRuleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueNotEquals = null;


        try {
            // InternalQLParser.g:3148:62: (iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF )
            // InternalQLParser.g:3149:2: iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF
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
    // InternalQLParser.g:3155:1: ruleIntegerValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3161:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3162:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3162:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3163:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getIntegerValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3171:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3172:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3172:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3173:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3194:1: entryRuleIntegerValueGreaterThan returns [EObject current=null] : iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF ;
    public final EObject entryRuleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThan = null;


        try {
            // InternalQLParser.g:3194:64: (iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF )
            // InternalQLParser.g:3195:2: iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF
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
    // InternalQLParser.g:3201:1: ruleIntegerValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3207:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3208:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3208:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3209:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getIntegerValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3217:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3218:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3218:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3219:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3240:1: entryRuleIntegerValueLessThan returns [EObject current=null] : iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF ;
    public final EObject entryRuleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThan = null;


        try {
            // InternalQLParser.g:3240:61: (iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF )
            // InternalQLParser.g:3241:2: iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF
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
    // InternalQLParser.g:3247:1: ruleIntegerValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3253:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3254:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3254:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3255:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getIntegerValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3263:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3264:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3264:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3265:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3286:1: entryRuleIntegerValueGreaterThanEquals returns [EObject current=null] : iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF ;
    public final EObject entryRuleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThanEquals = null;


        try {
            // InternalQLParser.g:3286:70: (iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF )
            // InternalQLParser.g:3287:2: iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF
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
    // InternalQLParser.g:3293:1: ruleIntegerValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3299:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3300:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3300:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3301:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3309:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3310:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3310:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3311:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3332:1: entryRuleIntegerValueLessThanEquals returns [EObject current=null] : iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF ;
    public final EObject entryRuleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThanEquals = null;


        try {
            // InternalQLParser.g:3332:67: (iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF )
            // InternalQLParser.g:3333:2: iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF
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
    // InternalQLParser.g:3339:1: ruleIntegerValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3345:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalQLParser.g:3346:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalQLParser.g:3346:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalQLParser.g:3347:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getIntegerValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3355:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalQLParser.g:3356:4: (lv_value_2_0= ruleInteger )
            {
            // InternalQLParser.g:3356:4: (lv_value_2_0= ruleInteger )
            // InternalQLParser.g:3357:5: lv_value_2_0= ruleInteger
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
    // InternalQLParser.g:3378:1: entryRuleDecimalValueEquals returns [EObject current=null] : iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF ;
    public final EObject entryRuleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueEquals = null;


        try {
            // InternalQLParser.g:3378:59: (iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF )
            // InternalQLParser.g:3379:2: iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF
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
    // InternalQLParser.g:3385:1: ruleDecimalValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3391:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3392:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3392:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3393:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getDecimalValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3401:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3402:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3402:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3403:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3424:1: entryRuleDecimalValueNotEquals returns [EObject current=null] : iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF ;
    public final EObject entryRuleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueNotEquals = null;


        try {
            // InternalQLParser.g:3424:62: (iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF )
            // InternalQLParser.g:3425:2: iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF
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
    // InternalQLParser.g:3431:1: ruleDecimalValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3437:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3438:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3438:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3439:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getDecimalValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3447:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3448:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3448:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3449:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3470:1: entryRuleDecimalValueGreaterThan returns [EObject current=null] : iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF ;
    public final EObject entryRuleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThan = null;


        try {
            // InternalQLParser.g:3470:64: (iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF )
            // InternalQLParser.g:3471:2: iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF
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
    // InternalQLParser.g:3477:1: ruleDecimalValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3483:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3484:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3484:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3485:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getDecimalValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3493:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3494:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3494:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3495:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3516:1: entryRuleDecimalValueLessThan returns [EObject current=null] : iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF ;
    public final EObject entryRuleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThan = null;


        try {
            // InternalQLParser.g:3516:61: (iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF )
            // InternalQLParser.g:3517:2: iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF
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
    // InternalQLParser.g:3523:1: ruleDecimalValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3529:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3530:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3530:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3531:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDecimalValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3539:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3540:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3540:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3541:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3562:1: entryRuleDecimalValueGreaterThanEquals returns [EObject current=null] : iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF ;
    public final EObject entryRuleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThanEquals = null;


        try {
            // InternalQLParser.g:3562:70: (iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF )
            // InternalQLParser.g:3563:2: iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF
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
    // InternalQLParser.g:3569:1: ruleDecimalValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3575:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3576:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3576:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3577:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3585:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3586:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3586:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3587:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3608:1: entryRuleDecimalValueLessThanEquals returns [EObject current=null] : iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF ;
    public final EObject entryRuleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThanEquals = null;


        try {
            // InternalQLParser.g:3608:67: (iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF )
            // InternalQLParser.g:3609:2: iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF
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
    // InternalQLParser.g:3615:1: ruleDecimalValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3621:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalQLParser.g:3622:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalQLParser.g:3622:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalQLParser.g:3623:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_32); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getDecimalValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:3631:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalQLParser.g:3632:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalQLParser.g:3632:4: (lv_value_2_0= ruleDecimal )
            // InternalQLParser.g:3633:5: lv_value_2_0= ruleDecimal
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
    // InternalQLParser.g:3654:1: entryRuleNestedExpression returns [EObject current=null] : iv_ruleNestedExpression= ruleNestedExpression EOF ;
    public final EObject entryRuleNestedExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedExpression = null;


        try {
            // InternalQLParser.g:3654:57: (iv_ruleNestedExpression= ruleNestedExpression EOF )
            // InternalQLParser.g:3655:2: iv_ruleNestedExpression= ruleNestedExpression EOF
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
    // InternalQLParser.g:3661:1: ruleNestedExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalQLParser.g:3667:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalQLParser.g:3668:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalQLParser.g:3668:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalQLParser.g:3669:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedExpressionAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:3673:3: ( (lv_nested_1_0= ruleExpressionConstraint ) )
            // InternalQLParser.g:3674:4: (lv_nested_1_0= ruleExpressionConstraint )
            {
            // InternalQLParser.g:3674:4: (lv_nested_1_0= ruleExpressionConstraint )
            // InternalQLParser.g:3675:5: lv_nested_1_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_9);
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
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
    // InternalQLParser.g:3700:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3702:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // InternalQLParser.g:3703:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
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
    // InternalQLParser.g:3712:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // InternalQLParser.g:3719:2: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // InternalQLParser.g:3720:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // InternalQLParser.g:3720:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // InternalQLParser.g:3721:3: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DIGIT_NONZERO_0);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0());
              		
            }
            // InternalQLParser.g:3728:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==RULE_DIGIT_NONZERO) ) {
                alt36=1;
            }
            else if ( (LA36_0==RULE_ZERO) ) {
                alt36=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // InternalQLParser.g:3729:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:3737:4: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_2);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1());
                      			
                    }

                    }
                    break;

            }

            // InternalQLParser.g:3745:3: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==RULE_DIGIT_NONZERO) ) {
                alt37=1;
            }
            else if ( (LA37_0==RULE_ZERO) ) {
                alt37=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // InternalQLParser.g:3746:4: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_3);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:3754:4: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_4);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalQLParser.g:3762:3: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==RULE_DIGIT_NONZERO) ) {
                alt38=1;
            }
            else if ( (LA38_0==RULE_ZERO) ) {
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
                    // InternalQLParser.g:3763:4: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_5);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:3771:4: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_6);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1());
                      			
                    }

                    }
                    break;

            }

            // InternalQLParser.g:3779:3: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==RULE_DIGIT_NONZERO) ) {
                alt39=1;
            }
            else if ( (LA39_0==RULE_ZERO) ) {
                alt39=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // InternalQLParser.g:3780:4: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_7);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:3788:4: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_8);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1());
                      			
                    }

                    }
                    break;

            }

            // InternalQLParser.g:3796:3: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt40=0;
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==RULE_DIGIT_NONZERO) ) {
                    alt40=1;
                }
                else if ( (LA40_0==RULE_ZERO) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // InternalQLParser.g:3797:4: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_9);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalQLParser.g:3805:4: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_10);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt40 >= 1 ) break loop40;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
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
    // InternalQLParser.g:3820:1: entryRuleNonNegativeInteger returns [String current=null] : iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF ;
    public final String entryRuleNonNegativeInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3822:2: (iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF )
            // InternalQLParser.g:3823:2: iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF
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
    // InternalQLParser.g:3832:1: ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3839:2: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) )
            // InternalQLParser.g:3840:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            {
            // InternalQLParser.g:3840:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==RULE_ZERO) ) {
                alt42=1;
            }
            else if ( (LA42_0==RULE_DIGIT_NONZERO) ) {
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
                    // InternalQLParser.g:3841:3: this_ZERO_0= RULE_ZERO
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
                    // InternalQLParser.g:3849:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // InternalQLParser.g:3849:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // InternalQLParser.g:3850:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }
                    // InternalQLParser.g:3857:4: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    loop41:
                    do {
                        int alt41=3;
                        int LA41_0 = input.LA(1);

                        if ( (LA41_0==RULE_DIGIT_NONZERO) ) {
                            alt41=1;
                        }
                        else if ( (LA41_0==RULE_ZERO) ) {
                            alt41=2;
                        }


                        switch (alt41) {
                    	case 1 :
                    	    // InternalQLParser.g:3858:5: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_DIGIT_NONZERO_2);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0());
                    	      				
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalQLParser.g:3866:5: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_ZERO_3);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1());
                    	      				
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop41;
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
    // InternalQLParser.g:3882:1: entryRuleMaxValue returns [String current=null] : iv_ruleMaxValue= ruleMaxValue EOF ;
    public final String entryRuleMaxValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMaxValue = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3884:2: (iv_ruleMaxValue= ruleMaxValue EOF )
            // InternalQLParser.g:3885:2: iv_ruleMaxValue= ruleMaxValue EOF
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
    // InternalQLParser.g:3894:1: ruleMaxValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) ;
    public final AntlrDatatypeRuleToken ruleMaxValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WILDCARD_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3901:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) )
            // InternalQLParser.g:3902:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            {
            // InternalQLParser.g:3902:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( ((LA43_0>=RULE_ZERO && LA43_0<=RULE_DIGIT_NONZERO)) ) {
                alt43=1;
            }
            else if ( (LA43_0==RULE_WILDCARD) ) {
                alt43=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // InternalQLParser.g:3903:3: this_NonNegativeInteger_0= ruleNonNegativeInteger
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
                    // InternalQLParser.g:3914:3: this_WILDCARD_1= RULE_WILDCARD
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
    // InternalQLParser.g:3928:1: entryRuleInteger returns [String current=null] : iv_ruleInteger= ruleInteger EOF ;
    public final String entryRuleInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3930:2: (iv_ruleInteger= ruleInteger EOF )
            // InternalQLParser.g:3931:2: iv_ruleInteger= ruleInteger EOF
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
    // InternalQLParser.g:3940:1: ruleInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) ;
    public final AntlrDatatypeRuleToken ruleInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3947:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) )
            // InternalQLParser.g:3948:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            {
            // InternalQLParser.g:3948:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            // InternalQLParser.g:3949:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger
            {
            // InternalQLParser.g:3949:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt44=3;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==RULE_PLUS) ) {
                alt44=1;
            }
            else if ( (LA44_0==RULE_DASH) ) {
                alt44=2;
            }
            switch (alt44) {
                case 1 :
                    // InternalQLParser.g:3950:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_28); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getIntegerAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:3958:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_28); if (state.failed) return current;
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
    // InternalQLParser.g:3983:1: entryRuleDecimal returns [String current=null] : iv_ruleDecimal= ruleDecimal EOF ;
    public final String entryRuleDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:3985:2: (iv_ruleDecimal= ruleDecimal EOF )
            // InternalQLParser.g:3986:2: iv_ruleDecimal= ruleDecimal EOF
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
    // InternalQLParser.g:3995:1: ruleDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) ;
    public final AntlrDatatypeRuleToken ruleDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeDecimal_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:4002:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) )
            // InternalQLParser.g:4003:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            {
            // InternalQLParser.g:4003:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            // InternalQLParser.g:4004:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal
            {
            // InternalQLParser.g:4004:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt45=3;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==RULE_PLUS) ) {
                alt45=1;
            }
            else if ( (LA45_0==RULE_DASH) ) {
                alt45=2;
            }
            switch (alt45) {
                case 1 :
                    // InternalQLParser.g:4005:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_33); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getDecimalAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalQLParser.g:4013:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_33); if (state.failed) return current;
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
    // InternalQLParser.g:4038:1: entryRuleNonNegativeDecimal returns [String current=null] : iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF ;
    public final String entryRuleNonNegativeDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:4040:2: (iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF )
            // InternalQLParser.g:4041:2: iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF
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
    // InternalQLParser.g:4050:1: ruleNonNegativeDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DOT_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalQLParser.g:4057:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            // InternalQLParser.g:4058:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            {
            // InternalQLParser.g:4058:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            // InternalQLParser.g:4059:3: this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getNonNegativeDecimalAccess().getNonNegativeIntegerParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_10);
            this_NonNegativeInteger_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeInteger_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_34); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DOT_1);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOT_1, grammarAccess.getNonNegativeDecimalAccess().getDOTTerminalRuleCall_1());
              		
            }
            // InternalQLParser.g:4076:3: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            loop46:
            do {
                int alt46=3;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==RULE_DIGIT_NONZERO) ) {
                    alt46=1;
                }
                else if ( (LA46_0==RULE_ZERO) ) {
                    alt46=2;
                }


                switch (alt46) {
            	case 1 :
            	    // InternalQLParser.g:4077:4: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_2);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeDecimalAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalQLParser.g:4085:4: this_ZERO_3= RULE_ZERO
            	    {
            	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_34); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_3);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeDecimalAccess().getZEROTerminalRuleCall_2_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop46;
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


    // $ANTLR start "ruleLexicalSearchType"
    // InternalQLParser.g:4100:1: ruleLexicalSearchType returns [Enumerator current=null] : ( (enumLiteral_0= Match ) | (enumLiteral_1= Regex ) | (enumLiteral_2= Exact ) ) ;
    public final Enumerator ruleLexicalSearchType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;


        	enterRule();

        try {
            // InternalQLParser.g:4106:2: ( ( (enumLiteral_0= Match ) | (enumLiteral_1= Regex ) | (enumLiteral_2= Exact ) ) )
            // InternalQLParser.g:4107:2: ( (enumLiteral_0= Match ) | (enumLiteral_1= Regex ) | (enumLiteral_2= Exact ) )
            {
            // InternalQLParser.g:4107:2: ( (enumLiteral_0= Match ) | (enumLiteral_1= Regex ) | (enumLiteral_2= Exact ) )
            int alt47=3;
            switch ( input.LA(1) ) {
            case Match:
                {
                alt47=1;
                }
                break;
            case Regex:
                {
                alt47=2;
                }
                break;
            case Exact:
                {
                alt47=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // InternalQLParser.g:4108:3: (enumLiteral_0= Match )
                    {
                    // InternalQLParser.g:4108:3: (enumLiteral_0= Match )
                    // InternalQLParser.g:4109:4: enumLiteral_0= Match
                    {
                    enumLiteral_0=(Token)match(input,Match,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getLexicalSearchTypeAccess().getMATCHEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_0, grammarAccess.getLexicalSearchTypeAccess().getMATCHEnumLiteralDeclaration_0());
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // InternalQLParser.g:4116:3: (enumLiteral_1= Regex )
                    {
                    // InternalQLParser.g:4116:3: (enumLiteral_1= Regex )
                    // InternalQLParser.g:4117:4: enumLiteral_1= Regex
                    {
                    enumLiteral_1=(Token)match(input,Regex,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getLexicalSearchTypeAccess().getREGEXEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_1, grammarAccess.getLexicalSearchTypeAccess().getREGEXEnumLiteralDeclaration_1());
                      			
                    }

                    }


                    }
                    break;
                case 3 :
                    // InternalQLParser.g:4124:3: (enumLiteral_2= Exact )
                    {
                    // InternalQLParser.g:4124:3: (enumLiteral_2= Exact )
                    // InternalQLParser.g:4125:4: enumLiteral_2= Exact
                    {
                    enumLiteral_2=(Token)match(input,Exact,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getLexicalSearchTypeAccess().getEXACTEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_2, grammarAccess.getLexicalSearchTypeAccess().getEXACTEnumLiteralDeclaration_2());
                      			
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
    // $ANTLR end "ruleLexicalSearchType"


    // $ANTLR start "ruleDomain"
    // InternalQLParser.g:4135:1: ruleDomain returns [Enumerator current=null] : ( (enumLiteral_0= Concept ) | (enumLiteral_1= Description ) ) ;
    public final Enumerator ruleDomain() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;


        	enterRule();

        try {
            // InternalQLParser.g:4141:2: ( ( (enumLiteral_0= Concept ) | (enumLiteral_1= Description ) ) )
            // InternalQLParser.g:4142:2: ( (enumLiteral_0= Concept ) | (enumLiteral_1= Description ) )
            {
            // InternalQLParser.g:4142:2: ( (enumLiteral_0= Concept ) | (enumLiteral_1= Description ) )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==Concept) ) {
                alt48=1;
            }
            else if ( (LA48_0==Description) ) {
                alt48=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // InternalQLParser.g:4143:3: (enumLiteral_0= Concept )
                    {
                    // InternalQLParser.g:4143:3: (enumLiteral_0= Concept )
                    // InternalQLParser.g:4144:4: enumLiteral_0= Concept
                    {
                    enumLiteral_0=(Token)match(input,Concept,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getDomainAccess().getCONCEPTEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_0, grammarAccess.getDomainAccess().getCONCEPTEnumLiteralDeclaration_0());
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // InternalQLParser.g:4151:3: (enumLiteral_1= Description )
                    {
                    // InternalQLParser.g:4151:3: (enumLiteral_1= Description )
                    // InternalQLParser.g:4152:4: enumLiteral_1= Description
                    {
                    enumLiteral_1=(Token)match(input,Description,FollowSets000.FOLLOW_2); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current = grammarAccess.getDomainAccess().getDESCRIPTIONEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                      				newLeafNode(enumLiteral_1, grammarAccess.getDomainAccess().getDESCRIPTIONEnumLiteralDeclaration_1());
                      			
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
    // $ANTLR end "ruleDomain"

    // $ANTLR start synpred18_InternalQLParser
    public final void synpred18_InternalQLParser_fragment() throws RecognitionException {   
        Token otherlv_2=null;
        EObject lv_right_3_0 = null;


        // InternalQLParser.g:1024:4: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )
        // InternalQLParser.g:1024:4: () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) )
        {
        // InternalQLParser.g:1024:4: ()
        // InternalQLParser.g:1025:5: 
        {
        if ( state.backtracking==0 ) {

          					/* */
          				
        }

        }

        otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_15); if (state.failed) return ;
        // InternalQLParser.g:1038:4: ( (lv_right_3_0= ruleAndExpressionConstraint ) )
        // InternalQLParser.g:1039:5: (lv_right_3_0= ruleAndExpressionConstraint )
        {
        // InternalQLParser.g:1039:5: (lv_right_3_0= ruleAndExpressionConstraint )
        // InternalQLParser.g:1040:6: lv_right_3_0= ruleAndExpressionConstraint
        {
        if ( state.backtracking==0 ) {

          						newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0());
          					
        }
        pushFollow(FollowSets000.FOLLOW_2);
        lv_right_3_0=ruleAndExpressionConstraint();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }
    }
    // $ANTLR end synpred18_InternalQLParser

    // $ANTLR start synpred20_InternalQLParser
    public final void synpred20_InternalQLParser_fragment() throws RecognitionException {   
        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject lv_right_4_0 = null;


        // InternalQLParser.g:1089:4: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )
        // InternalQLParser.g:1089:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
        {
        // InternalQLParser.g:1089:4: ()
        // InternalQLParser.g:1090:5: 
        {
        if ( state.backtracking==0 ) {

          					/* */
          				
        }

        }

        // InternalQLParser.g:1099:4: (otherlv_2= AND | otherlv_3= Comma )
        int alt50=2;
        int LA50_0 = input.LA(1);

        if ( (LA50_0==AND) ) {
            alt50=1;
        }
        else if ( (LA50_0==Comma) ) {
            alt50=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 50, 0, input);

            throw nvae;
        }
        switch (alt50) {
            case 1 :
                // InternalQLParser.g:1100:5: otherlv_2= AND
                {
                otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_15); if (state.failed) return ;

                }
                break;
            case 2 :
                // InternalQLParser.g:1105:5: otherlv_3= Comma
                {
                otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_15); if (state.failed) return ;

                }
                break;

        }

        // InternalQLParser.g:1110:4: ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
        // InternalQLParser.g:1111:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
        {
        // InternalQLParser.g:1111:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
        // InternalQLParser.g:1112:6: lv_right_4_0= ruleExclusionExpressionConstraint
        {
        if ( state.backtracking==0 ) {

          						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
          					
        }
        pushFollow(FollowSets000.FOLLOW_2);
        lv_right_4_0=ruleExclusionExpressionConstraint();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }
    }
    // $ANTLR end synpred20_InternalQLParser

    // $ANTLR start synpred21_InternalQLParser
    public final void synpred21_InternalQLParser_fragment() throws RecognitionException {   
        Token otherlv_2=null;
        EObject lv_right_3_0 = null;


        // InternalQLParser.g:1161:4: ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )
        // InternalQLParser.g:1161:4: () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
        {
        // InternalQLParser.g:1161:4: ()
        // InternalQLParser.g:1162:5: 
        {
        if ( state.backtracking==0 ) {

          					/* */
          				
        }

        }

        otherlv_2=(Token)match(input,MINUS,FollowSets000.FOLLOW_15); if (state.failed) return ;
        // InternalQLParser.g:1175:4: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
        // InternalQLParser.g:1176:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
        {
        // InternalQLParser.g:1176:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
        // InternalQLParser.g:1177:6: lv_right_3_0= ruleRefinedExpressionConstraint
        {
        if ( state.backtracking==0 ) {

          						newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0());
          					
        }
        pushFollow(FollowSets000.FOLLOW_2);
        lv_right_3_0=ruleRefinedExpressionConstraint();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }
    }
    // $ANTLR end synpred21_InternalQLParser

    // $ANTLR start synpred36_InternalQLParser
    public final void synpred36_InternalQLParser_fragment() throws RecognitionException {   
        // InternalQLParser.g:1969:4: ( OR )
        // InternalQLParser.g:1969:5: OR
        {
        match(input,OR,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_InternalQLParser

    // $ANTLR start synpred38_InternalQLParser
    public final void synpred38_InternalQLParser_fragment() throws RecognitionException {   
        // InternalQLParser.g:2037:4: ( AND | Comma )
        // InternalQLParser.g:
        {
        if ( input.LA(1)==AND||input.LA(1)==Comma ) {
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
    // $ANTLR end synpred38_InternalQLParser

    // $ANTLR start synpred40_InternalQLParser
    public final void synpred40_InternalQLParser_fragment() throws RecognitionException {   
        EObject this_AttributeConstraint_0 = null;


        // InternalQLParser.g:2100:3: (this_AttributeConstraint_0= ruleAttributeConstraint )
        // InternalQLParser.g:2100:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
    // $ANTLR end synpred40_InternalQLParser

    // $ANTLR start synpred41_InternalQLParser
    public final void synpred41_InternalQLParser_fragment() throws RecognitionException {   
        EObject this_AttributeGroup_1 = null;


        // InternalQLParser.g:2112:3: (this_AttributeGroup_1= ruleAttributeGroup )
        // InternalQLParser.g:2112:3: this_AttributeGroup_1= ruleAttributeGroup
        {
        if ( state.backtracking==0 ) {

          			/* */
          		
        }
        pushFollow(FollowSets000.FOLLOW_2);
        this_AttributeGroup_1=ruleAttributeGroup();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_InternalQLParser

    // $ANTLR start synpred46_InternalQLParser
    public final void synpred46_InternalQLParser_fragment() throws RecognitionException {   
        EObject this_AttributeConstraint_0 = null;


        // InternalQLParser.g:2430:3: (this_AttributeConstraint_0= ruleAttributeConstraint )
        // InternalQLParser.g:2430:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
    // $ANTLR end synpred46_InternalQLParser

    // Delegated rules

    public final boolean synpred46_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred46_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred21_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred38_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred38_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred41_InternalQLParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_InternalQLParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA7 dfa7 = new DFA7(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA25 dfa25 = new DFA25(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA35 dfa35 = new DFA35(this);
    static final String dfa_1s = "\14\uffff";
    static final String dfa_2s = "\1\4\2\50\10\uffff\1\10";
    static final String dfa_3s = "\1\40\2\50\10\uffff\1\12";
    static final String dfa_4s = "\3\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff";
    static final String dfa_5s = "\14\uffff}>";
    static final String[] dfa_6s = {
            "\1\7\1\6\1\2\1\5\1\11\1\1\1\3\1\10\5\uffff\1\4\16\uffff\1\12",
            "\1\13",
            "\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\11\1\uffff\1\3"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "443:2: (this_ActiveFilter_0= ruleActiveFilter | this_TermFilter_1= ruleTermFilter | this_PreferredInFilter_2= rulePreferredInFilter | this_AcceptableInFilter_3= ruleAcceptableInFilter | this_LanguageRefSetFilter_4= ruleLanguageRefSetFilter | this_TypeFilter_5= ruleTypeFilter | this_ModuleFilter_6= ruleModuleFilter | this_NestedFilter_7= ruleNestedFilter )";
        }
    }
    static final String dfa_7s = "\12\uffff";
    static final String dfa_8s = "\1\1\11\uffff";
    static final String dfa_9s = "\1\14\5\uffff\1\0\3\uffff";
    static final String dfa_10s = "\1\41\5\uffff\1\0\3\uffff";
    static final String dfa_11s = "\1\uffff\1\2\7\uffff\1\1";
    static final String dfa_12s = "\6\uffff\1\0\3\uffff}>";
    static final String[] dfa_13s = {
            "\1\1\6\uffff\1\1\1\6\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[][] dfa_13 = unpackEncodedStringArray(dfa_13s);

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "()* loopback of 1023:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_6 = input.LA(1);

                         
                        int index12_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred18_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index12_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_14s = "\1\14\4\uffff\2\0\3\uffff";
    static final String dfa_15s = "\1\41\4\uffff\2\0\3\uffff";
    static final String dfa_16s = "\5\uffff\1\0\1\1\3\uffff}>";
    static final String[] dfa_17s = {
            "\1\1\6\uffff\1\5\1\1\1\6\2\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };
    static final char[] dfa_14 = DFA.unpackEncodedStringToUnsignedChars(dfa_14s);
    static final char[] dfa_15 = DFA.unpackEncodedStringToUnsignedChars(dfa_15s);
    static final short[] dfa_16 = DFA.unpackEncodedString(dfa_16s);
    static final short[][] dfa_17 = unpackEncodedStringArray(dfa_17s);

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_14;
            this.max = dfa_15;
            this.accept = dfa_11;
            this.special = dfa_16;
            this.transition = dfa_17;
        }
        public String getDescription() {
            return "()* loopback of 1088:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_5 = input.LA(1);

                         
                        int index14_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred20_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index14_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_6 = input.LA(1);

                         
                        int index14_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred20_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index14_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_18s = "\1\2\11\uffff";
    static final String dfa_19s = "\1\14\1\0\10\uffff";
    static final String dfa_20s = "\1\41\1\0\10\uffff";
    static final String dfa_21s = "\2\uffff\1\2\6\uffff\1\1";
    static final String dfa_22s = "\1\uffff\1\0\10\uffff}>";
    static final String[] dfa_23s = {
            "\1\1\6\uffff\5\2\11\uffff\1\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };
    static final short[] dfa_18 = DFA.unpackEncodedString(dfa_18s);
    static final char[] dfa_19 = DFA.unpackEncodedStringToUnsignedChars(dfa_19s);
    static final char[] dfa_20 = DFA.unpackEncodedStringToUnsignedChars(dfa_20s);
    static final short[] dfa_21 = DFA.unpackEncodedString(dfa_21s);
    static final short[] dfa_22 = DFA.unpackEncodedString(dfa_22s);
    static final short[][] dfa_23 = unpackEncodedStringArray(dfa_23s);

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = dfa_7;
            this.eof = dfa_18;
            this.min = dfa_19;
            this.max = dfa_20;
            this.accept = dfa_21;
            this.special = dfa_22;
            this.transition = dfa_23;
        }
        public String getDescription() {
            return "1160:3: ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA15_1 = input.LA(1);

                         
                        int index15_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred21_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index15_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 15, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_24s = "\1\14\3\uffff\1\0\5\uffff";
    static final String dfa_25s = "\1\41\3\uffff\1\0\5\uffff";
    static final String dfa_26s = "\4\uffff\1\0\5\uffff}>";
    static final String[] dfa_27s = {
            "\1\1\6\uffff\1\1\1\4\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };
    static final char[] dfa_24 = DFA.unpackEncodedStringToUnsignedChars(dfa_24s);
    static final char[] dfa_25 = DFA.unpackEncodedStringToUnsignedChars(dfa_25s);
    static final short[] dfa_26 = DFA.unpackEncodedString(dfa_26s);
    static final short[][] dfa_27 = unpackEncodedStringArray(dfa_27s);

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_24;
            this.max = dfa_25;
            this.accept = dfa_11;
            this.special = dfa_26;
            this.transition = dfa_27;
        }
        public String getDescription() {
            return "()* loopback of 1968:3: ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_4 = input.LA(1);

                         
                        int index22_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred36_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index22_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_28s = "\1\14\2\uffff\2\0\5\uffff";
    static final String dfa_29s = "\1\41\2\uffff\2\0\5\uffff";
    static final String dfa_30s = "\3\uffff\1\0\1\1\5\uffff}>";
    static final String[] dfa_31s = {
            "\1\1\6\uffff\1\3\1\1\1\4\2\1\11\uffff\1\1",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };
    static final char[] dfa_28 = DFA.unpackEncodedStringToUnsignedChars(dfa_28s);
    static final char[] dfa_29 = DFA.unpackEncodedStringToUnsignedChars(dfa_29s);
    static final short[] dfa_30 = DFA.unpackEncodedString(dfa_30s);
    static final short[][] dfa_31 = unpackEncodedStringArray(dfa_31s);

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_28;
            this.max = dfa_29;
            this.accept = dfa_11;
            this.special = dfa_30;
            this.transition = dfa_31;
        }
        public String getDescription() {
            return "()* loopback of 2036:3: ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_3 = input.LA(1);

                         
                        int index24_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index24_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA24_4 = input.LA(1);

                         
                        int index24_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_InternalQLParser()) ) {s = 9;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index24_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_32s = "\17\uffff";
    static final String dfa_33s = "\1\31\1\0\12\uffff\1\0\2\uffff";
    static final String dfa_34s = "\1\61\1\0\12\uffff\1\0\2\uffff";
    static final String dfa_35s = "\2\uffff\1\1\12\uffff\1\2\1\3";
    static final String dfa_36s = "\1\uffff\1\0\12\uffff\1\1\2\uffff}>";
    static final String[] dfa_37s = {
            "\1\2\2\uffff\1\2\1\uffff\1\15\1\uffff\1\14\1\uffff\1\1\3\uffff\1\2\2\uffff\1\2\2\uffff\6\2",
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

    static final short[] dfa_32 = DFA.unpackEncodedString(dfa_32s);
    static final char[] dfa_33 = DFA.unpackEncodedStringToUnsignedChars(dfa_33s);
    static final char[] dfa_34 = DFA.unpackEncodedStringToUnsignedChars(dfa_34s);
    static final short[] dfa_35 = DFA.unpackEncodedString(dfa_35s);
    static final short[] dfa_36 = DFA.unpackEncodedString(dfa_36s);
    static final short[][] dfa_37 = unpackEncodedStringArray(dfa_37s);

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = dfa_32;
            this.eof = dfa_32;
            this.min = dfa_33;
            this.max = dfa_34;
            this.accept = dfa_35;
            this.special = dfa_36;
            this.transition = dfa_37;
        }
        public String getDescription() {
            return "2099:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA25_1 = input.LA(1);

                         
                        int index25_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred40_InternalQLParser()) ) {s = 2;}

                        else if ( (synpred41_InternalQLParser()) ) {s = 13;}

                         
                        input.seek(index25_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA25_12 = input.LA(1);

                         
                        int index25_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred40_InternalQLParser()) ) {s = 2;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index25_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 25, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_38s = "\16\uffff";
    static final String dfa_39s = "\1\31\13\uffff\1\0\1\uffff";
    static final String dfa_40s = "\1\61\13\uffff\1\0\1\uffff";
    static final String dfa_41s = "\1\uffff\1\1\13\uffff\1\2";
    static final String dfa_42s = "\14\uffff\1\0\1\uffff}>";
    static final String[] dfa_43s = {
            "\1\1\2\uffff\1\1\3\uffff\1\14\1\uffff\1\1\3\uffff\1\1\2\uffff\1\1\2\uffff\6\1",
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

    static final short[] dfa_38 = DFA.unpackEncodedString(dfa_38s);
    static final char[] dfa_39 = DFA.unpackEncodedStringToUnsignedChars(dfa_39s);
    static final char[] dfa_40 = DFA.unpackEncodedStringToUnsignedChars(dfa_40s);
    static final short[] dfa_41 = DFA.unpackEncodedString(dfa_41s);
    static final short[] dfa_42 = DFA.unpackEncodedString(dfa_42s);
    static final short[][] dfa_43 = unpackEncodedStringArray(dfa_43s);

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = dfa_38;
            this.eof = dfa_38;
            this.min = dfa_39;
            this.max = dfa_40;
            this.accept = dfa_41;
            this.special = dfa_42;
            this.transition = dfa_43;
        }
        public String getDescription() {
            return "2429:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_12 = input.LA(1);

                         
                        int index30_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred46_InternalQLParser()) ) {s = 1;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index30_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String dfa_44s = "\77\uffff";
    static final String dfa_45s = "\21\uffff\2\47\2\uffff\2\54\2\uffff\2\57\2\uffff\2\64\2\uffff\2\67\2\uffff\2\74\2\uffff\2\47\2\uffff\2\54\2\uffff\2\57\2\uffff\2\64\2\uffff\2\67\2\uffff\2\74";
    static final String dfa_46s = "\1\52\6\64\1\uffff\2\33\1\uffff\6\33\2\14\2\33\2\14\2\33\2\14\2\33\2\14\2\33\2\14\2\33\2\14\2\uffff\2\14\2\uffff\2\14\2\uffff\2\14\2\uffff\2\14\2\uffff\2\14\2\uffff\2\14";
    static final String dfa_47s = "\1\63\2\70\4\64\1\uffff\2\45\1\uffff\4\45\2\34\2\50\2\34\2\50\2\34\2\50\2\34\2\50\2\34\2\50\2\34\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50\2\uffff\2\50";
    static final String dfa_48s = "\7\uffff\1\1\2\uffff\1\2\34\uffff\1\3\1\11\2\uffff\1\12\1\4\2\uffff\1\5\1\13\2\uffff\1\14\1\6\2\uffff\1\7\1\15\2\uffff\1\16\1\10\2\uffff";
    static final String dfa_49s = "\77\uffff}>";
    static final String[] dfa_50s = {
            "\1\1\1\2\1\5\1\3\4\uffff\1\4\1\6",
            "\1\10\3\uffff\1\7",
            "\1\11\3\uffff\1\12",
            "\1\13",
            "\1\14",
            "\1\15",
            "\1\16",
            "",
            "\1\21\1\22\7\uffff\1\17\1\20",
            "\1\25\1\26\7\uffff\1\23\1\24",
            "",
            "\1\31\1\32\7\uffff\1\27\1\30",
            "\1\35\1\36\7\uffff\1\33\1\34",
            "\1\41\1\42\7\uffff\1\37\1\40",
            "\1\45\1\46\7\uffff\1\43\1\44",
            "\1\21\1\22",
            "\1\21\1\22",
            "\1\47\6\uffff\5\47\7\uffff\1\47\1\uffff\1\47\6\uffff\1\50",
            "\1\47\6\uffff\5\47\3\uffff\1\52\1\51\2\uffff\1\47\1\uffff\1\47\6\uffff\1\50",
            "\1\25\1\26",
            "\1\25\1\26",
            "\1\54\6\uffff\5\54\7\uffff\1\54\1\uffff\1\54\6\uffff\1\53",
            "\1\54\6\uffff\5\54\3\uffff\1\56\1\55\2\uffff\1\54\1\uffff\1\54\6\uffff\1\53",
            "\1\31\1\32",
            "\1\31\1\32",
            "\1\57\6\uffff\5\57\7\uffff\1\57\1\uffff\1\57\6\uffff\1\60",
            "\1\57\6\uffff\5\57\3\uffff\1\62\1\61\2\uffff\1\57\1\uffff\1\57\6\uffff\1\60",
            "\1\35\1\36",
            "\1\35\1\36",
            "\1\64\6\uffff\5\64\7\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\1\64\6\uffff\5\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\1\41\1\42",
            "\1\41\1\42",
            "\1\67\6\uffff\5\67\7\uffff\1\67\1\uffff\1\67\6\uffff\1\70",
            "\1\67\6\uffff\5\67\3\uffff\1\72\1\71\2\uffff\1\67\1\uffff\1\67\6\uffff\1\70",
            "\1\45\1\46",
            "\1\45\1\46",
            "\1\74\6\uffff\5\74\7\uffff\1\74\1\uffff\1\74\6\uffff\1\73",
            "\1\74\6\uffff\5\74\3\uffff\1\76\1\75\2\uffff\1\74\1\uffff\1\74\6\uffff\1\73",
            "",
            "",
            "\1\47\6\uffff\5\47\3\uffff\1\52\1\51\2\uffff\1\47\1\uffff\1\47\6\uffff\1\50",
            "\1\47\6\uffff\5\47\3\uffff\1\52\1\51\2\uffff\1\47\1\uffff\1\47\6\uffff\1\50",
            "",
            "",
            "\1\54\6\uffff\5\54\3\uffff\1\56\1\55\2\uffff\1\54\1\uffff\1\54\6\uffff\1\53",
            "\1\54\6\uffff\5\54\3\uffff\1\56\1\55\2\uffff\1\54\1\uffff\1\54\6\uffff\1\53",
            "",
            "",
            "\1\57\6\uffff\5\57\3\uffff\1\62\1\61\2\uffff\1\57\1\uffff\1\57\6\uffff\1\60",
            "\1\57\6\uffff\5\57\3\uffff\1\62\1\61\2\uffff\1\57\1\uffff\1\57\6\uffff\1\60",
            "",
            "",
            "\1\64\6\uffff\5\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\1\64\6\uffff\5\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "",
            "",
            "\1\67\6\uffff\5\67\3\uffff\1\72\1\71\2\uffff\1\67\1\uffff\1\67\6\uffff\1\70",
            "\1\67\6\uffff\5\67\3\uffff\1\72\1\71\2\uffff\1\67\1\uffff\1\67\6\uffff\1\70",
            "",
            "",
            "\1\74\6\uffff\5\74\3\uffff\1\76\1\75\2\uffff\1\74\1\uffff\1\74\6\uffff\1\73",
            "\1\74\6\uffff\5\74\3\uffff\1\76\1\75\2\uffff\1\74\1\uffff\1\74\6\uffff\1\73"
    };

    static final short[] dfa_44 = DFA.unpackEncodedString(dfa_44s);
    static final short[] dfa_45 = DFA.unpackEncodedString(dfa_45s);
    static final char[] dfa_46 = DFA.unpackEncodedStringToUnsignedChars(dfa_46s);
    static final char[] dfa_47 = DFA.unpackEncodedStringToUnsignedChars(dfa_47s);
    static final short[] dfa_48 = DFA.unpackEncodedString(dfa_48s);
    static final short[] dfa_49 = DFA.unpackEncodedString(dfa_49s);
    static final short[][] dfa_50 = unpackEncodedStringArray(dfa_50s);

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = dfa_44;
            this.eof = dfa_45;
            this.min = dfa_46;
            this.max = dfa_47;
            this.accept = dfa_48;
            this.special = dfa_49;
            this.transition = dfa_50;
        }
        public String getDescription() {
            return "2764:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000400002L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000100020FF0L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000800000L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000100002L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000280002L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000001002L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000200000000L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000010000000000L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000000400L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000040000000000L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000044000L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000100L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0003F24110000000L});
        public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x010000000001A000L});
        public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000020000000L});
        public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0100000000000000L});
        public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000020000002L});
        public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0003F24552000000L});
        public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000010000000002L});
        public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000001000002L});
        public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000040000000L});
        public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0003F24512000000L});
        public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000080000000L});
        public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0003F24112000000L});
        public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x000C3C0000000000L});
        public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000018000000L});
        public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000004000000L});
        public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000020018000000L});
        public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0010000000000000L});
        public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000003018000000L});
        public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000000018000002L});
    }


}