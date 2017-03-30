package com.b2international.snowowl.snomed.ecl.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.ecl.services.EclGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalEclParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "MINUS", "AND", "OR", "Comma", "RULE_TERM_STRING", "RULE_REVERSED", "RULE_TO", "RULE_ZERO", "RULE_DIGIT_NONZERO", "RULE_COLON", "RULE_CURLY_OPEN", "RULE_CURLY_CLOSE", "RULE_ROUND_OPEN", "RULE_ROUND_CLOSE", "RULE_SQUARE_OPEN", "RULE_SQUARE_CLOSE", "RULE_PLUS", "RULE_DASH", "RULE_CARET", "RULE_NOT", "RULE_DOT", "RULE_WILDCARD", "RULE_EQUAL", "RULE_NOT_EQUAL", "RULE_LT", "RULE_GT", "RULE_DBL_LT", "RULE_DBL_GT", "RULE_LT_EM", "RULE_GT_EM", "RULE_GTE", "RULE_LTE", "RULE_HASH", "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_STRING"
    };
    public static final int RULE_LTE=35;
    public static final int RULE_DIGIT_NONZERO=12;
    public static final int RULE_CURLY_OPEN=14;
    public static final int RULE_TO=10;
    public static final int RULE_ROUND_CLOSE=17;
    public static final int RULE_DBL_GT=31;
    public static final int RULE_GT=29;
    public static final int RULE_STRING=40;
    public static final int RULE_NOT=23;
    public static final int RULE_REVERSED=9;
    public static final int MINUS=4;
    public static final int RULE_GTE=34;
    public static final int RULE_SL_COMMENT=39;
    public static final int Comma=7;
    public static final int RULE_HASH=36;
    public static final int RULE_ROUND_OPEN=16;
    public static final int RULE_DASH=21;
    public static final int RULE_DBL_LT=30;
    public static final int RULE_PLUS=20;
    public static final int RULE_NOT_EQUAL=27;
    public static final int RULE_DOT=24;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=19;
    public static final int OR=6;
    public static final int RULE_SQUARE_OPEN=18;
    public static final int RULE_EQUAL=26;
    public static final int RULE_LT_EM=32;
    public static final int RULE_GT_EM=33;
    public static final int RULE_WS=37;
    public static final int RULE_CURLY_CLOSE=15;
    public static final int RULE_ZERO=11;
    public static final int RULE_COLON=13;
    public static final int RULE_CARET=22;
    public static final int RULE_LT=28;
    public static final int AND=5;
    public static final int RULE_ML_COMMENT=38;
    public static final int RULE_WILDCARD=25;
    public static final int RULE_TERM_STRING=8;

    // delegates
    // delegators


        public InternalEclParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalEclParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalEclParser.tokenNames; }
    public String getGrammarFileName() { return "InternalEclParser.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */

     	private EclGrammarAccess grammarAccess;

        public InternalEclParser(TokenStream input, EclGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Script";
       	}

       	@Override
       	protected EclGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleScript"
    // InternalEclParser.g:75:1: entryRuleScript returns [EObject current=null] : iv_ruleScript= ruleScript EOF ;
    public final EObject entryRuleScript() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleScript = null;


        try {
            // InternalEclParser.g:75:47: (iv_ruleScript= ruleScript EOF )
            // InternalEclParser.g:76:2: iv_ruleScript= ruleScript EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getScriptRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleScript=ruleScript();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleScript; 
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
    // $ANTLR end "entryRuleScript"


    // $ANTLR start "ruleScript"
    // InternalEclParser.g:82:1: ruleScript returns [EObject current=null] : ( () ( (lv_constraint_1_0= ruleExpressionConstraint ) )? ) ;
    public final EObject ruleScript() throws RecognitionException {
        EObject current = null;

        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:88:2: ( ( () ( (lv_constraint_1_0= ruleExpressionConstraint ) )? ) )
            // InternalEclParser.g:89:2: ( () ( (lv_constraint_1_0= ruleExpressionConstraint ) )? )
            {
            // InternalEclParser.g:89:2: ( () ( (lv_constraint_1_0= ruleExpressionConstraint ) )? )
            // InternalEclParser.g:90:3: () ( (lv_constraint_1_0= ruleExpressionConstraint ) )?
            {
            // InternalEclParser.g:90:3: ()
            // InternalEclParser.g:91:4: 
            {
            if ( state.backtracking==0 ) {

              				/* */
              			
            }
            if ( state.backtracking==0 ) {

              				current = forceCreateModelElement(
              					grammarAccess.getScriptAccess().getScriptAction_0(),
              					current);
              			
            }

            }

            // InternalEclParser.g:100:3: ( (lv_constraint_1_0= ruleExpressionConstraint ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==RULE_DIGIT_NONZERO||LA1_0==RULE_ROUND_OPEN||LA1_0==RULE_CARET||LA1_0==RULE_WILDCARD||(LA1_0>=RULE_LT && LA1_0<=RULE_GT_EM)) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // InternalEclParser.g:101:4: (lv_constraint_1_0= ruleExpressionConstraint )
                    {
                    // InternalEclParser.g:101:4: (lv_constraint_1_0= ruleExpressionConstraint )
                    // InternalEclParser.g:102:5: lv_constraint_1_0= ruleExpressionConstraint
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getScriptAccess().getConstraintExpressionConstraintParserRuleCall_1_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_0=ruleExpressionConstraint();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      					if (current==null) {
                      						current = createModelElementForParent(grammarAccess.getScriptRule());
                      					}
                      					set(
                      						current,
                      						"constraint",
                      						lv_constraint_1_0,
                      						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
                      					afterParserOrEnumRuleCall();
                      				
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
    // $ANTLR end "ruleScript"


    // $ANTLR start "entryRuleExpressionConstraint"
    // InternalEclParser.g:123:1: entryRuleExpressionConstraint returns [EObject current=null] : iv_ruleExpressionConstraint= ruleExpressionConstraint EOF ;
    public final EObject entryRuleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpressionConstraint = null;


        try {
            // InternalEclParser.g:123:61: (iv_ruleExpressionConstraint= ruleExpressionConstraint EOF )
            // InternalEclParser.g:124:2: iv_ruleExpressionConstraint= ruleExpressionConstraint EOF
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
    // InternalEclParser.g:130:1: ruleExpressionConstraint returns [EObject current=null] : this_OrExpressionConstraint_0= ruleOrExpressionConstraint ;
    public final EObject ruleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject this_OrExpressionConstraint_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:136:2: (this_OrExpressionConstraint_0= ruleOrExpressionConstraint )
            // InternalEclParser.g:137:2: this_OrExpressionConstraint_0= ruleOrExpressionConstraint
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
    // InternalEclParser.g:151:1: entryRuleOrExpressionConstraint returns [EObject current=null] : iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF ;
    public final EObject entryRuleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpressionConstraint = null;


        try {
            // InternalEclParser.g:151:63: (iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF )
            // InternalEclParser.g:152:2: iv_ruleOrExpressionConstraint= ruleOrExpressionConstraint EOF
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
    // InternalEclParser.g:158:1: ruleOrExpressionConstraint returns [EObject current=null] : (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) ;
    public final EObject ruleOrExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:164:2: ( (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* ) )
            // InternalEclParser.g:165:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            {
            // InternalEclParser.g:165:2: (this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )* )
            // InternalEclParser.g:166:3: this_AndExpressionConstraint_0= ruleAndExpressionConstraint ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getAndExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_3);
            this_AndExpressionConstraint_0=ruleAndExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:177:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==OR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalEclParser.g:178:4: () otherlv_2= OR ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    {
            	    // InternalEclParser.g:178:4: ()
            	    // InternalEclParser.g:179:5: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_2, grammarAccess.getOrExpressionConstraintAccess().getORKeyword_1_1());
            	      			
            	    }
            	    // InternalEclParser.g:192:4: ( (lv_right_3_0= ruleAndExpressionConstraint ) )
            	    // InternalEclParser.g:193:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    {
            	    // InternalEclParser.g:193:5: (lv_right_3_0= ruleAndExpressionConstraint )
            	    // InternalEclParser.g:194:6: lv_right_3_0= ruleAndExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_3);
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
            	    break loop2;
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
    // InternalEclParser.g:216:1: entryRuleAndExpressionConstraint returns [EObject current=null] : iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF ;
    public final EObject entryRuleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpressionConstraint = null;


        try {
            // InternalEclParser.g:216:64: (iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF )
            // InternalEclParser.g:217:2: iv_ruleAndExpressionConstraint= ruleAndExpressionConstraint EOF
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
    // InternalEclParser.g:223:1: ruleAndExpressionConstraint returns [EObject current=null] : (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) ;
    public final EObject ruleAndExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_ExclusionExpressionConstraint_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:229:2: ( (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* ) )
            // InternalEclParser.g:230:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            {
            // InternalEclParser.g:230:2: (this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )* )
            // InternalEclParser.g:231:3: this_ExclusionExpressionConstraint_0= ruleExclusionExpressionConstraint ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_5);
            this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_ExclusionExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:242:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==AND||LA4_0==Comma) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // InternalEclParser.g:243:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    {
            	    // InternalEclParser.g:243:4: ()
            	    // InternalEclParser.g:244:5: 
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

            	    // InternalEclParser.g:253:4: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==AND) ) {
            	        alt3=1;
            	    }
            	    else if ( (LA3_0==Comma) ) {
            	        alt3=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 3, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // InternalEclParser.g:254:5: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_2, grammarAccess.getAndExpressionConstraintAccess().getANDKeyword_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEclParser.g:259:5: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_4); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_3, grammarAccess.getAndExpressionConstraintAccess().getCommaKeyword_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEclParser.g:264:4: ( (lv_right_4_0= ruleExclusionExpressionConstraint ) )
            	    // InternalEclParser.g:265:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    {
            	    // InternalEclParser.g:265:5: (lv_right_4_0= ruleExclusionExpressionConstraint )
            	    // InternalEclParser.g:266:6: lv_right_4_0= ruleExclusionExpressionConstraint
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_5);
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
            	    break loop4;
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
    // InternalEclParser.g:288:1: entryRuleExclusionExpressionConstraint returns [EObject current=null] : iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF ;
    public final EObject entryRuleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExclusionExpressionConstraint = null;


        try {
            // InternalEclParser.g:288:70: (iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF )
            // InternalEclParser.g:289:2: iv_ruleExclusionExpressionConstraint= ruleExclusionExpressionConstraint EOF
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
    // InternalEclParser.g:295:1: ruleExclusionExpressionConstraint returns [EObject current=null] : (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) ;
    public final EObject ruleExclusionExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_RefinedExpressionConstraint_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:301:2: ( (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? ) )
            // InternalEclParser.g:302:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            {
            // InternalEclParser.g:302:2: (this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )? )
            // InternalEclParser.g:303:3: this_RefinedExpressionConstraint_0= ruleRefinedExpressionConstraint ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_6);
            this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_RefinedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:314:3: ( () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==MINUS) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalEclParser.g:315:4: () otherlv_2= MINUS ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    {
                    // InternalEclParser.g:315:4: ()
                    // InternalEclParser.g:316:5: 
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

                    otherlv_2=(Token)match(input,MINUS,FollowSets000.FOLLOW_4); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(otherlv_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSKeyword_1_1());
                      			
                    }
                    // InternalEclParser.g:329:4: ( (lv_right_3_0= ruleRefinedExpressionConstraint ) )
                    // InternalEclParser.g:330:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    {
                    // InternalEclParser.g:330:5: (lv_right_3_0= ruleRefinedExpressionConstraint )
                    // InternalEclParser.g:331:6: lv_right_3_0= ruleRefinedExpressionConstraint
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
    // InternalEclParser.g:353:1: entryRuleRefinedExpressionConstraint returns [EObject current=null] : iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF ;
    public final EObject entryRuleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinedExpressionConstraint = null;


        try {
            // InternalEclParser.g:353:68: (iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF )
            // InternalEclParser.g:354:2: iv_ruleRefinedExpressionConstraint= ruleRefinedExpressionConstraint EOF
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
    // InternalEclParser.g:360:1: ruleRefinedExpressionConstraint returns [EObject current=null] : (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) ;
    public final EObject ruleRefinedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_COLON_2=null;
        EObject this_DottedExpressionConstraint_0 = null;

        EObject lv_refinement_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:366:2: ( (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? ) )
            // InternalEclParser.g:367:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            {
            // InternalEclParser.g:367:2: (this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )? )
            // InternalEclParser.g:368:3: this_DottedExpressionConstraint_0= ruleDottedExpressionConstraint ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getDottedExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_7);
            this_DottedExpressionConstraint_0=ruleDottedExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_DottedExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:379:3: ( () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_COLON) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalEclParser.g:380:4: () this_COLON_2= RULE_COLON ( (lv_refinement_3_0= ruleRefinement ) )
                    {
                    // InternalEclParser.g:380:4: ()
                    // InternalEclParser.g:381:5: 
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

                    this_COLON_2=(Token)match(input,RULE_COLON,FollowSets000.FOLLOW_8); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_COLON_2, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_1());
                      			
                    }
                    // InternalEclParser.g:394:4: ( (lv_refinement_3_0= ruleRefinement ) )
                    // InternalEclParser.g:395:5: (lv_refinement_3_0= ruleRefinement )
                    {
                    // InternalEclParser.g:395:5: (lv_refinement_3_0= ruleRefinement )
                    // InternalEclParser.g:396:6: lv_refinement_3_0= ruleRefinement
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
    // InternalEclParser.g:418:1: entryRuleDottedExpressionConstraint returns [EObject current=null] : iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF ;
    public final EObject entryRuleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDottedExpressionConstraint = null;


        try {
            // InternalEclParser.g:418:67: (iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF )
            // InternalEclParser.g:419:2: iv_ruleDottedExpressionConstraint= ruleDottedExpressionConstraint EOF
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
    // InternalEclParser.g:425:1: ruleDottedExpressionConstraint returns [EObject current=null] : (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* ) ;
    public final EObject ruleDottedExpressionConstraint() throws RecognitionException {
        EObject current = null;

        Token this_DOT_2=null;
        EObject this_SimpleExpressionConstraint_0 = null;

        EObject lv_attribute_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:431:2: ( (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* ) )
            // InternalEclParser.g:432:2: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* )
            {
            // InternalEclParser.g:432:2: (this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )* )
            // InternalEclParser.g:433:3: this_SimpleExpressionConstraint_0= ruleSimpleExpressionConstraint ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSimpleExpressionConstraintParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_9);
            this_SimpleExpressionConstraint_0=ruleSimpleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SimpleExpressionConstraint_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:444:3: ( () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) ) )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==RULE_DOT) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // InternalEclParser.g:445:4: () this_DOT_2= RULE_DOT ( (lv_attribute_3_0= ruleAttribute ) )
            	    {
            	    // InternalEclParser.g:445:4: ()
            	    // InternalEclParser.g:446:5: 
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

            	    this_DOT_2=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_10); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DOT_2, grammarAccess.getDottedExpressionConstraintAccess().getDOTTerminalRuleCall_1_1());
            	      			
            	    }
            	    // InternalEclParser.g:459:4: ( (lv_attribute_3_0= ruleAttribute ) )
            	    // InternalEclParser.g:460:5: (lv_attribute_3_0= ruleAttribute )
            	    {
            	    // InternalEclParser.g:460:5: (lv_attribute_3_0= ruleAttribute )
            	    // InternalEclParser.g:461:6: lv_attribute_3_0= ruleAttribute
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeAttributeParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_9);
            	    lv_attribute_3_0=ruleAttribute();

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
            	      							"com.b2international.snowowl.snomed.ecl.Ecl.Attribute");
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


    // $ANTLR start "entryRuleSimpleExpressionConstraint"
    // InternalEclParser.g:483:1: entryRuleSimpleExpressionConstraint returns [EObject current=null] : iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF ;
    public final EObject entryRuleSimpleExpressionConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleExpressionConstraint = null;


        try {
            // InternalEclParser.g:483:67: (iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF )
            // InternalEclParser.g:484:2: iv_ruleSimpleExpressionConstraint= ruleSimpleExpressionConstraint EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSimpleExpressionConstraintRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSimpleExpressionConstraint=ruleSimpleExpressionConstraint();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSimpleExpressionConstraint; 
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
    // $ANTLR end "entryRuleSimpleExpressionConstraint"


    // $ANTLR start "ruleSimpleExpressionConstraint"
    // InternalEclParser.g:490:1: ruleSimpleExpressionConstraint returns [EObject current=null] : (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) ;
    public final EObject ruleSimpleExpressionConstraint() throws RecognitionException {
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
            // InternalEclParser.g:496:2: ( (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept ) )
            // InternalEclParser.g:497:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            {
            // InternalEclParser.g:497:2: (this_ChildOf_0= ruleChildOf | this_DescendantOf_1= ruleDescendantOf | this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf | this_ParentOf_3= ruleParentOf | this_AncestorOf_4= ruleAncestorOf | this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf | this_FocusConcept_6= ruleFocusConcept )
            int alt8=7;
            switch ( input.LA(1) ) {
            case RULE_LT_EM:
                {
                alt8=1;
                }
                break;
            case RULE_LT:
                {
                alt8=2;
                }
                break;
            case RULE_DBL_LT:
                {
                alt8=3;
                }
                break;
            case RULE_GT_EM:
                {
                alt8=4;
                }
                break;
            case RULE_GT:
                {
                alt8=5;
                }
                break;
            case RULE_DBL_GT:
                {
                alt8=6;
                }
                break;
            case RULE_DIGIT_NONZERO:
            case RULE_ROUND_OPEN:
            case RULE_CARET:
            case RULE_WILDCARD:
                {
                alt8=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // InternalEclParser.g:498:3: this_ChildOf_0= ruleChildOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getChildOfParserRuleCall_0());
                      		
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
                    // InternalEclParser.g:510:3: this_DescendantOf_1= ruleDescendantOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOfParserRuleCall_1());
                      		
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
                    // InternalEclParser.g:522:3: this_DescendantOrSelfOf_2= ruleDescendantOrSelfOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2());
                      		
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
                    // InternalEclParser.g:534:3: this_ParentOf_3= ruleParentOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getParentOfParserRuleCall_3());
                      		
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
                    // InternalEclParser.g:546:3: this_AncestorOf_4= ruleAncestorOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOfParserRuleCall_4());
                      		
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
                    // InternalEclParser.g:558:3: this_AncestorOrSelfOf_5= ruleAncestorOrSelfOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5());
                      		
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
                    // InternalEclParser.g:570:3: this_FocusConcept_6= ruleFocusConcept
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getFocusConceptParserRuleCall_6());
                      		
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
    // $ANTLR end "ruleSimpleExpressionConstraint"


    // $ANTLR start "entryRuleFocusConcept"
    // InternalEclParser.g:585:1: entryRuleFocusConcept returns [EObject current=null] : iv_ruleFocusConcept= ruleFocusConcept EOF ;
    public final EObject entryRuleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFocusConcept = null;


        try {
            // InternalEclParser.g:585:53: (iv_ruleFocusConcept= ruleFocusConcept EOF )
            // InternalEclParser.g:586:2: iv_ruleFocusConcept= ruleFocusConcept EOF
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
    // InternalEclParser.g:592:1: ruleFocusConcept returns [EObject current=null] : (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) ;
    public final EObject ruleFocusConcept() throws RecognitionException {
        EObject current = null;

        EObject this_MemberOf_0 = null;

        EObject this_ConceptReference_1 = null;

        EObject this_Any_2 = null;

        EObject this_NestedExpression_3 = null;



        	enterRule();

        try {
            // InternalEclParser.g:598:2: ( (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression ) )
            // InternalEclParser.g:599:2: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            {
            // InternalEclParser.g:599:2: (this_MemberOf_0= ruleMemberOf | this_ConceptReference_1= ruleConceptReference | this_Any_2= ruleAny | this_NestedExpression_3= ruleNestedExpression )
            int alt9=4;
            switch ( input.LA(1) ) {
            case RULE_CARET:
                {
                alt9=1;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt9=2;
                }
                break;
            case RULE_WILDCARD:
                {
                alt9=3;
                }
                break;
            case RULE_ROUND_OPEN:
                {
                alt9=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // InternalEclParser.g:600:3: this_MemberOf_0= ruleMemberOf
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
                    // InternalEclParser.g:612:3: this_ConceptReference_1= ruleConceptReference
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
                    // InternalEclParser.g:624:3: this_Any_2= ruleAny
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
                    // InternalEclParser.g:636:3: this_NestedExpression_3= ruleNestedExpression
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
    // InternalEclParser.g:651:1: entryRuleChildOf returns [EObject current=null] : iv_ruleChildOf= ruleChildOf EOF ;
    public final EObject entryRuleChildOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleChildOf = null;


        try {
            // InternalEclParser.g:651:48: (iv_ruleChildOf= ruleChildOf EOF )
            // InternalEclParser.g:652:2: iv_ruleChildOf= ruleChildOf EOF
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
    // InternalEclParser.g:658:1: ruleChildOf returns [EObject current=null] : (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleChildOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:664:2: ( (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:665:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:665:2: (this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:666:3: this_LT_EM_0= RULE_LT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_EM_0=(Token)match(input,RULE_LT_EM,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:670:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:671:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:671:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:672:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:693:1: entryRuleDescendantOf returns [EObject current=null] : iv_ruleDescendantOf= ruleDescendantOf EOF ;
    public final EObject entryRuleDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOf = null;


        try {
            // InternalEclParser.g:693:53: (iv_ruleDescendantOf= ruleDescendantOf EOF )
            // InternalEclParser.g:694:2: iv_ruleDescendantOf= ruleDescendantOf EOF
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
    // InternalEclParser.g:700:1: ruleDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:706:2: ( (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:707:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:707:2: (this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:708:3: this_LT_0= RULE_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:712:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:713:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:713:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:714:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:735:1: entryRuleDescendantOrSelfOf returns [EObject current=null] : iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF ;
    public final EObject entryRuleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescendantOrSelfOf = null;


        try {
            // InternalEclParser.g:735:59: (iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF )
            // InternalEclParser.g:736:2: iv_ruleDescendantOrSelfOf= ruleDescendantOrSelfOf EOF
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
    // InternalEclParser.g:742:1: ruleDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:748:2: ( (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:749:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:749:2: (this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:750:3: this_DBL_LT_0= RULE_DBL_LT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:754:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:755:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:755:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:756:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:777:1: entryRuleParentOf returns [EObject current=null] : iv_ruleParentOf= ruleParentOf EOF ;
    public final EObject entryRuleParentOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParentOf = null;


        try {
            // InternalEclParser.g:777:49: (iv_ruleParentOf= ruleParentOf EOF )
            // InternalEclParser.g:778:2: iv_ruleParentOf= ruleParentOf EOF
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
    // InternalEclParser.g:784:1: ruleParentOf returns [EObject current=null] : (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleParentOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_EM_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:790:2: ( (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:791:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:791:2: (this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:792:3: this_GT_EM_0= RULE_GT_EM ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_EM_0=(Token)match(input,RULE_GT_EM,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:796:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:797:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:797:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:798:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:819:1: entryRuleAncestorOf returns [EObject current=null] : iv_ruleAncestorOf= ruleAncestorOf EOF ;
    public final EObject entryRuleAncestorOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOf = null;


        try {
            // InternalEclParser.g:819:51: (iv_ruleAncestorOf= ruleAncestorOf EOF )
            // InternalEclParser.g:820:2: iv_ruleAncestorOf= ruleAncestorOf EOF
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
    // InternalEclParser.g:826:1: ruleAncestorOf returns [EObject current=null] : (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOf() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:832:2: ( (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:833:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:833:2: (this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:834:3: this_GT_0= RULE_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:838:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:839:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:839:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:840:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:861:1: entryRuleAncestorOrSelfOf returns [EObject current=null] : iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF ;
    public final EObject entryRuleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAncestorOrSelfOf = null;


        try {
            // InternalEclParser.g:861:57: (iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF )
            // InternalEclParser.g:862:2: iv_ruleAncestorOrSelfOf= ruleAncestorOrSelfOf EOF
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
    // InternalEclParser.g:868:1: ruleAncestorOrSelfOf returns [EObject current=null] : (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) ;
    public final EObject ruleAncestorOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_GT_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:874:2: ( (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) ) )
            // InternalEclParser.g:875:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            {
            // InternalEclParser.g:875:2: (this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) ) )
            // InternalEclParser.g:876:3: this_DBL_GT_0= RULE_DBL_GT ( (lv_constraint_1_0= ruleFocusConcept ) )
            {
            this_DBL_GT_0=(Token)match(input,RULE_DBL_GT,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:880:3: ( (lv_constraint_1_0= ruleFocusConcept ) )
            // InternalEclParser.g:881:4: (lv_constraint_1_0= ruleFocusConcept )
            {
            // InternalEclParser.g:881:4: (lv_constraint_1_0= ruleFocusConcept )
            // InternalEclParser.g:882:5: lv_constraint_1_0= ruleFocusConcept
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
    // InternalEclParser.g:903:1: entryRuleMemberOf returns [EObject current=null] : iv_ruleMemberOf= ruleMemberOf EOF ;
    public final EObject entryRuleMemberOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberOf = null;


        try {
            // InternalEclParser.g:903:49: (iv_ruleMemberOf= ruleMemberOf EOF )
            // InternalEclParser.g:904:2: iv_ruleMemberOf= ruleMemberOf EOF
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
    // InternalEclParser.g:910:1: ruleMemberOf returns [EObject current=null] : (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleMemberOf() throws RecognitionException {
        EObject current = null;

        Token this_CARET_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;



        	enterRule();

        try {
            // InternalEclParser.g:916:2: ( (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // InternalEclParser.g:917:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // InternalEclParser.g:917:2: (this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // InternalEclParser.g:918:3: this_CARET_0= RULE_CARET ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_CARET_0=(Token)match(input,RULE_CARET,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:922:3: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // InternalEclParser.g:923:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // InternalEclParser.g:923:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // InternalEclParser.g:924:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // InternalEclParser.g:924:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_DIGIT_NONZERO) ) {
                alt10=1;
            }
            else if ( (LA10_0==RULE_WILDCARD) ) {
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
                    // InternalEclParser.g:925:6: lv_constraint_1_1= ruleConceptReference
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
                    // InternalEclParser.g:941:6: lv_constraint_1_2= ruleAny
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
    // InternalEclParser.g:963:1: entryRuleConceptReference returns [EObject current=null] : iv_ruleConceptReference= ruleConceptReference EOF ;
    public final EObject entryRuleConceptReference() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConceptReference = null;


        try {
            // InternalEclParser.g:963:57: (iv_ruleConceptReference= ruleConceptReference EOF )
            // InternalEclParser.g:964:2: iv_ruleConceptReference= ruleConceptReference EOF
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
    // InternalEclParser.g:970:1: ruleConceptReference returns [EObject current=null] : ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) ;
    public final EObject ruleConceptReference() throws RecognitionException {
        EObject current = null;

        Token lv_term_1_0=null;
        AntlrDatatypeRuleToken lv_id_0_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:976:2: ( ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? ) )
            // InternalEclParser.g:977:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            {
            // InternalEclParser.g:977:2: ( ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )? )
            // InternalEclParser.g:978:3: ( (lv_id_0_0= ruleSnomedIdentifier ) ) ( (lv_term_1_0= RULE_TERM_STRING ) )?
            {
            // InternalEclParser.g:978:3: ( (lv_id_0_0= ruleSnomedIdentifier ) )
            // InternalEclParser.g:979:4: (lv_id_0_0= ruleSnomedIdentifier )
            {
            // InternalEclParser.g:979:4: (lv_id_0_0= ruleSnomedIdentifier )
            // InternalEclParser.g:980:5: lv_id_0_0= ruleSnomedIdentifier
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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SnomedIdentifier");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEclParser.g:997:3: ( (lv_term_1_0= RULE_TERM_STRING ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_TERM_STRING) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalEclParser.g:998:4: (lv_term_1_0= RULE_TERM_STRING )
                    {
                    // InternalEclParser.g:998:4: (lv_term_1_0= RULE_TERM_STRING )
                    // InternalEclParser.g:999:5: lv_term_1_0= RULE_TERM_STRING
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
    // InternalEclParser.g:1019:1: entryRuleAny returns [EObject current=null] : iv_ruleAny= ruleAny EOF ;
    public final EObject entryRuleAny() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAny = null;


        try {
            // InternalEclParser.g:1019:44: (iv_ruleAny= ruleAny EOF )
            // InternalEclParser.g:1020:2: iv_ruleAny= ruleAny EOF
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
    // InternalEclParser.g:1026:1: ruleAny returns [EObject current=null] : (this_WILDCARD_0= RULE_WILDCARD () ) ;
    public final EObject ruleAny() throws RecognitionException {
        EObject current = null;

        Token this_WILDCARD_0=null;


        	enterRule();

        try {
            // InternalEclParser.g:1032:2: ( (this_WILDCARD_0= RULE_WILDCARD () ) )
            // InternalEclParser.g:1033:2: (this_WILDCARD_0= RULE_WILDCARD () )
            {
            // InternalEclParser.g:1033:2: (this_WILDCARD_0= RULE_WILDCARD () )
            // InternalEclParser.g:1034:3: this_WILDCARD_0= RULE_WILDCARD ()
            {
            this_WILDCARD_0=(Token)match(input,RULE_WILDCARD,FollowSets000.FOLLOW_2); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1038:3: ()
            // InternalEclParser.g:1039:4: 
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
    // InternalEclParser.g:1052:1: entryRuleRefinement returns [EObject current=null] : iv_ruleRefinement= ruleRefinement EOF ;
    public final EObject entryRuleRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRefinement = null;


        try {
            // InternalEclParser.g:1052:51: (iv_ruleRefinement= ruleRefinement EOF )
            // InternalEclParser.g:1053:2: iv_ruleRefinement= ruleRefinement EOF
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
    // InternalEclParser.g:1059:1: ruleRefinement returns [EObject current=null] : this_OrRefinement_0= ruleOrRefinement ;
    public final EObject ruleRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_OrRefinement_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1065:2: (this_OrRefinement_0= ruleOrRefinement )
            // InternalEclParser.g:1066:2: this_OrRefinement_0= ruleOrRefinement
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
    // InternalEclParser.g:1080:1: entryRuleOrRefinement returns [EObject current=null] : iv_ruleOrRefinement= ruleOrRefinement EOF ;
    public final EObject entryRuleOrRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrRefinement = null;


        try {
            // InternalEclParser.g:1080:53: (iv_ruleOrRefinement= ruleOrRefinement EOF )
            // InternalEclParser.g:1081:2: iv_ruleOrRefinement= ruleOrRefinement EOF
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
    // InternalEclParser.g:1087:1: ruleOrRefinement returns [EObject current=null] : (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) ;
    public final EObject ruleOrRefinement() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndRefinement_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1093:2: ( (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* ) )
            // InternalEclParser.g:1094:2: (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            {
            // InternalEclParser.g:1094:2: (this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )* )
            // InternalEclParser.g:1095:3: this_AndRefinement_0= ruleAndRefinement ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_3);
            this_AndRefinement_0=ruleAndRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:1106:3: ( ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==OR) ) {
                    int LA12_4 = input.LA(2);

                    if ( (synpred19_InternalEclParser()) ) {
                        alt12=1;
                    }


                }


                switch (alt12) {
            	case 1 :
            	    // InternalEclParser.g:1107:4: ( OR )=> ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    {
            	    // InternalEclParser.g:1108:4: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) ) )
            	    // InternalEclParser.g:1109:5: () otherlv_2= OR ( (lv_right_3_0= ruleAndRefinement ) )
            	    {
            	    // InternalEclParser.g:1109:5: ()
            	    // InternalEclParser.g:1110:6: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_8); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      					newLeafNode(otherlv_2, grammarAccess.getOrRefinementAccess().getORKeyword_1_0_1());
            	      				
            	    }
            	    // InternalEclParser.g:1123:5: ( (lv_right_3_0= ruleAndRefinement ) )
            	    // InternalEclParser.g:1124:6: (lv_right_3_0= ruleAndRefinement )
            	    {
            	    // InternalEclParser.g:1124:6: (lv_right_3_0= ruleAndRefinement )
            	    // InternalEclParser.g:1125:7: lv_right_3_0= ruleAndRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_3);
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
    // $ANTLR end "ruleOrRefinement"


    // $ANTLR start "entryRuleAndRefinement"
    // InternalEclParser.g:1148:1: entryRuleAndRefinement returns [EObject current=null] : iv_ruleAndRefinement= ruleAndRefinement EOF ;
    public final EObject entryRuleAndRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndRefinement = null;


        try {
            // InternalEclParser.g:1148:54: (iv_ruleAndRefinement= ruleAndRefinement EOF )
            // InternalEclParser.g:1149:2: iv_ruleAndRefinement= ruleAndRefinement EOF
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
    // InternalEclParser.g:1155:1: ruleAndRefinement returns [EObject current=null] : (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) ;
    public final EObject ruleAndRefinement() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_SubRefinement_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1161:2: ( (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* ) )
            // InternalEclParser.g:1162:2: (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            {
            // InternalEclParser.g:1162:2: (this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )* )
            // InternalEclParser.g:1163:3: this_SubRefinement_0= ruleSubRefinement ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndRefinementAccess().getSubRefinementParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_5);
            this_SubRefinement_0=ruleSubRefinement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubRefinement_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:1174:3: ( ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==AND) ) {
                    int LA14_3 = input.LA(2);

                    if ( (synpred21_InternalEclParser()) ) {
                        alt14=1;
                    }


                }
                else if ( (LA14_0==Comma) ) {
                    int LA14_4 = input.LA(2);

                    if ( (synpred21_InternalEclParser()) ) {
                        alt14=1;
                    }


                }


                switch (alt14) {
            	case 1 :
            	    // InternalEclParser.g:1175:4: ( AND | Comma )=> ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    {
            	    // InternalEclParser.g:1176:4: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) ) )
            	    // InternalEclParser.g:1177:5: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubRefinement ) )
            	    {
            	    // InternalEclParser.g:1177:5: ()
            	    // InternalEclParser.g:1178:6: 
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

            	    // InternalEclParser.g:1187:5: (otherlv_2= AND | otherlv_3= Comma )
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
            	            // InternalEclParser.g:1188:6: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_8); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(otherlv_2, grammarAccess.getAndRefinementAccess().getANDKeyword_1_0_1_0());
            	              					
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEclParser.g:1193:6: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_8); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              						newLeafNode(otherlv_3, grammarAccess.getAndRefinementAccess().getCommaKeyword_1_0_1_1());
            	              					
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEclParser.g:1198:5: ( (lv_right_4_0= ruleSubRefinement ) )
            	    // InternalEclParser.g:1199:6: (lv_right_4_0= ruleSubRefinement )
            	    {
            	    // InternalEclParser.g:1199:6: (lv_right_4_0= ruleSubRefinement )
            	    // InternalEclParser.g:1200:7: lv_right_4_0= ruleSubRefinement
            	    {
            	    if ( state.backtracking==0 ) {

            	      							newCompositeNode(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0());
            	      						
            	    }
            	    pushFollow(FollowSets000.FOLLOW_5);
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
    // $ANTLR end "ruleAndRefinement"


    // $ANTLR start "entryRuleSubRefinement"
    // InternalEclParser.g:1223:1: entryRuleSubRefinement returns [EObject current=null] : iv_ruleSubRefinement= ruleSubRefinement EOF ;
    public final EObject entryRuleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubRefinement = null;


        try {
            // InternalEclParser.g:1223:54: (iv_ruleSubRefinement= ruleSubRefinement EOF )
            // InternalEclParser.g:1224:2: iv_ruleSubRefinement= ruleSubRefinement EOF
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
    // InternalEclParser.g:1230:1: ruleSubRefinement returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) ;
    public final EObject ruleSubRefinement() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_AttributeGroup_1 = null;

        EObject this_NestedRefinement_2 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1236:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement ) )
            // InternalEclParser.g:1237:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            {
            // InternalEclParser.g:1237:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )
            int alt15=3;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // InternalEclParser.g:1238:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
                    // InternalEclParser.g:1250:3: this_AttributeGroup_1= ruleAttributeGroup
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
                    // InternalEclParser.g:1262:3: this_NestedRefinement_2= ruleNestedRefinement
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
    // InternalEclParser.g:1277:1: entryRuleNestedRefinement returns [EObject current=null] : iv_ruleNestedRefinement= ruleNestedRefinement EOF ;
    public final EObject entryRuleNestedRefinement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedRefinement = null;


        try {
            // InternalEclParser.g:1277:57: (iv_ruleNestedRefinement= ruleNestedRefinement EOF )
            // InternalEclParser.g:1278:2: iv_ruleNestedRefinement= ruleNestedRefinement EOF
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
    // InternalEclParser.g:1284:1: ruleNestedRefinement returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedRefinement() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1290:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEclParser.g:1291:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEclParser.g:1291:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEclParser.g:1292:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleRefinement ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_8); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedRefinementAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1296:3: ( (lv_nested_1_0= ruleRefinement ) )
            // InternalEclParser.g:1297:4: (lv_nested_1_0= ruleRefinement )
            {
            // InternalEclParser.g:1297:4: (lv_nested_1_0= ruleRefinement )
            // InternalEclParser.g:1298:5: lv_nested_1_0= ruleRefinement
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedRefinementAccess().getNestedRefinementParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_13);
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
    // InternalEclParser.g:1323:1: entryRuleAttributeGroup returns [EObject current=null] : iv_ruleAttributeGroup= ruleAttributeGroup EOF ;
    public final EObject entryRuleAttributeGroup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeGroup = null;


        try {
            // InternalEclParser.g:1323:55: (iv_ruleAttributeGroup= ruleAttributeGroup EOF )
            // InternalEclParser.g:1324:2: iv_ruleAttributeGroup= ruleAttributeGroup EOF
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
    // InternalEclParser.g:1330:1: ruleAttributeGroup returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) ;
    public final EObject ruleAttributeGroup() throws RecognitionException {
        EObject current = null;

        Token this_CURLY_OPEN_1=null;
        Token this_CURLY_CLOSE_3=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_refinement_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1336:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE ) )
            // InternalEclParser.g:1337:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            {
            // InternalEclParser.g:1337:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE )
            // InternalEclParser.g:1338:3: ( (lv_cardinality_0_0= ruleCardinality ) )? this_CURLY_OPEN_1= RULE_CURLY_OPEN ( (lv_refinement_2_0= ruleAttributeSet ) ) this_CURLY_CLOSE_3= RULE_CURLY_CLOSE
            {
            // InternalEclParser.g:1338:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_SQUARE_OPEN) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalEclParser.g:1339:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalEclParser.g:1339:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalEclParser.g:1340:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeGroupAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_14);
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

            this_CURLY_OPEN_1=(Token)match(input,RULE_CURLY_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:1361:3: ( (lv_refinement_2_0= ruleAttributeSet ) )
            // InternalEclParser.g:1362:4: (lv_refinement_2_0= ruleAttributeSet )
            {
            // InternalEclParser.g:1362:4: (lv_refinement_2_0= ruleAttributeSet )
            // InternalEclParser.g:1363:5: lv_refinement_2_0= ruleAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeGroupAccess().getRefinementAttributeSetParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_16);
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
    // InternalEclParser.g:1388:1: entryRuleAttributeSet returns [EObject current=null] : iv_ruleAttributeSet= ruleAttributeSet EOF ;
    public final EObject entryRuleAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeSet = null;


        try {
            // InternalEclParser.g:1388:53: (iv_ruleAttributeSet= ruleAttributeSet EOF )
            // InternalEclParser.g:1389:2: iv_ruleAttributeSet= ruleAttributeSet EOF
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
    // InternalEclParser.g:1395:1: ruleAttributeSet returns [EObject current=null] : this_OrAttributeSet_0= ruleOrAttributeSet ;
    public final EObject ruleAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_OrAttributeSet_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1401:2: (this_OrAttributeSet_0= ruleOrAttributeSet )
            // InternalEclParser.g:1402:2: this_OrAttributeSet_0= ruleOrAttributeSet
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
    // InternalEclParser.g:1416:1: entryRuleOrAttributeSet returns [EObject current=null] : iv_ruleOrAttributeSet= ruleOrAttributeSet EOF ;
    public final EObject entryRuleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrAttributeSet = null;


        try {
            // InternalEclParser.g:1416:55: (iv_ruleOrAttributeSet= ruleOrAttributeSet EOF )
            // InternalEclParser.g:1417:2: iv_ruleOrAttributeSet= ruleOrAttributeSet EOF
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
    // InternalEclParser.g:1423:1: ruleOrAttributeSet returns [EObject current=null] : (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) ;
    public final EObject ruleOrAttributeSet() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        EObject this_AndAttributeSet_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1429:2: ( (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* ) )
            // InternalEclParser.g:1430:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            {
            // InternalEclParser.g:1430:2: (this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )* )
            // InternalEclParser.g:1431:3: this_AndAttributeSet_0= ruleAndAttributeSet ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getOrAttributeSetAccess().getAndAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_3);
            this_AndAttributeSet_0=ruleAndAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_AndAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:1442:3: ( () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==OR) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalEclParser.g:1443:4: () otherlv_2= OR ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    {
            	    // InternalEclParser.g:1443:4: ()
            	    // InternalEclParser.g:1444:5: 
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

            	    otherlv_2=(Token)match(input,OR,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(otherlv_2, grammarAccess.getOrAttributeSetAccess().getORKeyword_1_1());
            	      			
            	    }
            	    // InternalEclParser.g:1457:4: ( (lv_right_3_0= ruleAndAttributeSet ) )
            	    // InternalEclParser.g:1458:5: (lv_right_3_0= ruleAndAttributeSet )
            	    {
            	    // InternalEclParser.g:1458:5: (lv_right_3_0= ruleAndAttributeSet )
            	    // InternalEclParser.g:1459:6: lv_right_3_0= ruleAndAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getOrAttributeSetAccess().getRightAndAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_3);
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
    // $ANTLR end "ruleOrAttributeSet"


    // $ANTLR start "entryRuleAndAttributeSet"
    // InternalEclParser.g:1481:1: entryRuleAndAttributeSet returns [EObject current=null] : iv_ruleAndAttributeSet= ruleAndAttributeSet EOF ;
    public final EObject entryRuleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndAttributeSet = null;


        try {
            // InternalEclParser.g:1481:56: (iv_ruleAndAttributeSet= ruleAndAttributeSet EOF )
            // InternalEclParser.g:1482:2: iv_ruleAndAttributeSet= ruleAndAttributeSet EOF
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
    // InternalEclParser.g:1488:1: ruleAndAttributeSet returns [EObject current=null] : (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) ;
    public final EObject ruleAndAttributeSet() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        EObject this_SubAttributeSet_0 = null;

        EObject lv_right_4_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1494:2: ( (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* ) )
            // InternalEclParser.g:1495:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            {
            // InternalEclParser.g:1495:2: (this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )* )
            // InternalEclParser.g:1496:3: this_SubAttributeSet_0= ruleSubAttributeSet ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            {
            if ( state.backtracking==0 ) {

              			/* */
              		
            }
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getAndAttributeSetAccess().getSubAttributeSetParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_5);
            this_SubAttributeSet_0=ruleSubAttributeSet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current = this_SubAttributeSet_0;
              			afterParserOrEnumRuleCall();
              		
            }
            // InternalEclParser.g:1507:3: ( () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AND||LA19_0==Comma) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // InternalEclParser.g:1508:4: () (otherlv_2= AND | otherlv_3= Comma ) ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    {
            	    // InternalEclParser.g:1508:4: ()
            	    // InternalEclParser.g:1509:5: 
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

            	    // InternalEclParser.g:1518:4: (otherlv_2= AND | otherlv_3= Comma )
            	    int alt18=2;
            	    int LA18_0 = input.LA(1);

            	    if ( (LA18_0==AND) ) {
            	        alt18=1;
            	    }
            	    else if ( (LA18_0==Comma) ) {
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
            	            // InternalEclParser.g:1519:5: otherlv_2= AND
            	            {
            	            otherlv_2=(Token)match(input,AND,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_2, grammarAccess.getAndAttributeSetAccess().getANDKeyword_1_1_0());
            	              				
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // InternalEclParser.g:1524:5: otherlv_3= Comma
            	            {
            	            otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_15); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	              					newLeafNode(otherlv_3, grammarAccess.getAndAttributeSetAccess().getCommaKeyword_1_1_1());
            	              				
            	            }

            	            }
            	            break;

            	    }

            	    // InternalEclParser.g:1529:4: ( (lv_right_4_0= ruleSubAttributeSet ) )
            	    // InternalEclParser.g:1530:5: (lv_right_4_0= ruleSubAttributeSet )
            	    {
            	    // InternalEclParser.g:1530:5: (lv_right_4_0= ruleSubAttributeSet )
            	    // InternalEclParser.g:1531:6: lv_right_4_0= ruleSubAttributeSet
            	    {
            	    if ( state.backtracking==0 ) {

            	      						newCompositeNode(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0());
            	      					
            	    }
            	    pushFollow(FollowSets000.FOLLOW_5);
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
            	    break loop19;
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
    // InternalEclParser.g:1553:1: entryRuleSubAttributeSet returns [EObject current=null] : iv_ruleSubAttributeSet= ruleSubAttributeSet EOF ;
    public final EObject entryRuleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubAttributeSet = null;


        try {
            // InternalEclParser.g:1553:56: (iv_ruleSubAttributeSet= ruleSubAttributeSet EOF )
            // InternalEclParser.g:1554:2: iv_ruleSubAttributeSet= ruleSubAttributeSet EOF
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
    // InternalEclParser.g:1560:1: ruleSubAttributeSet returns [EObject current=null] : (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) ;
    public final EObject ruleSubAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeConstraint_0 = null;

        EObject this_NestedAttributeSet_1 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1566:2: ( (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet ) )
            // InternalEclParser.g:1567:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            {
            // InternalEclParser.g:1567:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_NestedAttributeSet_1= ruleNestedAttributeSet )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RULE_REVERSED||LA20_0==RULE_DIGIT_NONZERO||LA20_0==RULE_SQUARE_OPEN||LA20_0==RULE_WILDCARD||LA20_0==RULE_LT||LA20_0==RULE_DBL_LT) ) {
                alt20=1;
            }
            else if ( (LA20_0==RULE_ROUND_OPEN) ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // InternalEclParser.g:1568:3: this_AttributeConstraint_0= ruleAttributeConstraint
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
                    // InternalEclParser.g:1580:3: this_NestedAttributeSet_1= ruleNestedAttributeSet
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
    // InternalEclParser.g:1595:1: entryRuleNestedAttributeSet returns [EObject current=null] : iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF ;
    public final EObject entryRuleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedAttributeSet = null;


        try {
            // InternalEclParser.g:1595:59: (iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF )
            // InternalEclParser.g:1596:2: iv_ruleNestedAttributeSet= ruleNestedAttributeSet EOF
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
    // InternalEclParser.g:1602:1: ruleNestedAttributeSet returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedAttributeSet() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1608:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEclParser.g:1609:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEclParser.g:1609:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEclParser.g:1610:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleAttributeSet ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_15); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedAttributeSetAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1614:3: ( (lv_nested_1_0= ruleAttributeSet ) )
            // InternalEclParser.g:1615:4: (lv_nested_1_0= ruleAttributeSet )
            {
            // InternalEclParser.g:1615:4: (lv_nested_1_0= ruleAttributeSet )
            // InternalEclParser.g:1616:5: lv_nested_1_0= ruleAttributeSet
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedAttributeSetAccess().getNestedAttributeSetParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_13);
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
    // InternalEclParser.g:1641:1: entryRuleAttributeConstraint returns [EObject current=null] : iv_ruleAttributeConstraint= ruleAttributeConstraint EOF ;
    public final EObject entryRuleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeConstraint = null;


        try {
            // InternalEclParser.g:1641:60: (iv_ruleAttributeConstraint= ruleAttributeConstraint EOF )
            // InternalEclParser.g:1642:2: iv_ruleAttributeConstraint= ruleAttributeConstraint EOF
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
    // InternalEclParser.g:1648:1: ruleAttributeConstraint returns [EObject current=null] : ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) ;
    public final EObject ruleAttributeConstraint() throws RecognitionException {
        EObject current = null;

        Token lv_reversed_1_0=null;
        EObject lv_cardinality_0_0 = null;

        EObject lv_attribute_2_0 = null;

        EObject lv_comparison_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1654:2: ( ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) ) )
            // InternalEclParser.g:1655:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            {
            // InternalEclParser.g:1655:2: ( ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) ) )
            // InternalEclParser.g:1656:3: ( (lv_cardinality_0_0= ruleCardinality ) )? ( (lv_reversed_1_0= RULE_REVERSED ) )? ( (lv_attribute_2_0= ruleAttribute ) ) ( (lv_comparison_3_0= ruleComparison ) )
            {
            // InternalEclParser.g:1656:3: ( (lv_cardinality_0_0= ruleCardinality ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_SQUARE_OPEN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalEclParser.g:1657:4: (lv_cardinality_0_0= ruleCardinality )
                    {
                    // InternalEclParser.g:1657:4: (lv_cardinality_0_0= ruleCardinality )
                    // InternalEclParser.g:1658:5: lv_cardinality_0_0= ruleCardinality
                    {
                    if ( state.backtracking==0 ) {

                      					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getCardinalityCardinalityParserRuleCall_0_0());
                      				
                    }
                    pushFollow(FollowSets000.FOLLOW_10);
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

            // InternalEclParser.g:1675:3: ( (lv_reversed_1_0= RULE_REVERSED ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_REVERSED) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalEclParser.g:1676:4: (lv_reversed_1_0= RULE_REVERSED )
                    {
                    // InternalEclParser.g:1676:4: (lv_reversed_1_0= RULE_REVERSED )
                    // InternalEclParser.g:1677:5: lv_reversed_1_0= RULE_REVERSED
                    {
                    lv_reversed_1_0=(Token)match(input,RULE_REVERSED,FollowSets000.FOLLOW_10); if (state.failed) return current;
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

            // InternalEclParser.g:1693:3: ( (lv_attribute_2_0= ruleAttribute ) )
            // InternalEclParser.g:1694:4: (lv_attribute_2_0= ruleAttribute )
            {
            // InternalEclParser.g:1694:4: (lv_attribute_2_0= ruleAttribute )
            // InternalEclParser.g:1695:5: lv_attribute_2_0= ruleAttribute
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeAttributeParserRuleCall_2_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_17);
            lv_attribute_2_0=ruleAttribute();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.Attribute");
              					afterParserOrEnumRuleCall();
              				
            }

            }


            }

            // InternalEclParser.g:1712:3: ( (lv_comparison_3_0= ruleComparison ) )
            // InternalEclParser.g:1713:4: (lv_comparison_3_0= ruleComparison )
            {
            // InternalEclParser.g:1713:4: (lv_comparison_3_0= ruleComparison )
            // InternalEclParser.g:1714:5: lv_comparison_3_0= ruleComparison
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


    // $ANTLR start "entryRuleAttribute"
    // InternalEclParser.g:1735:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // InternalEclParser.g:1735:50: (iv_ruleAttribute= ruleAttribute EOF )
            // InternalEclParser.g:1736:2: iv_ruleAttribute= ruleAttribute EOF
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
    // InternalEclParser.g:1742:1: ruleAttribute returns [EObject current=null] : (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeDescendantOf_0 = null;

        EObject this_AttributeDescendantOrSelfOf_1 = null;

        EObject this_ConceptReference_2 = null;

        EObject this_Any_3 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1748:2: ( (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny ) )
            // InternalEclParser.g:1749:2: (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny )
            {
            // InternalEclParser.g:1749:2: (this_AttributeDescendantOf_0= ruleAttributeDescendantOf | this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf | this_ConceptReference_2= ruleConceptReference | this_Any_3= ruleAny )
            int alt23=4;
            switch ( input.LA(1) ) {
            case RULE_LT:
                {
                alt23=1;
                }
                break;
            case RULE_DBL_LT:
                {
                alt23=2;
                }
                break;
            case RULE_DIGIT_NONZERO:
                {
                alt23=3;
                }
                break;
            case RULE_WILDCARD:
                {
                alt23=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // InternalEclParser.g:1750:3: this_AttributeDescendantOf_0= ruleAttributeDescendantOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOfParserRuleCall_0());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeDescendantOf_0=ruleAttributeDescendantOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeDescendantOf_0;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:1762:3: this_AttributeDescendantOrSelfOf_1= ruleAttributeDescendantOrSelfOf
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOrSelfOfParserRuleCall_1());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_AttributeDescendantOrSelfOf_1=ruleAttributeDescendantOrSelfOf();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_AttributeDescendantOrSelfOf_1;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 3 :
                    // InternalEclParser.g:1774:3: this_ConceptReference_2= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeAccess().getConceptReferenceParserRuleCall_2());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConceptReference_2=ruleConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_ConceptReference_2;
                      			afterParserOrEnumRuleCall();
                      		
                    }

                    }
                    break;
                case 4 :
                    // InternalEclParser.g:1786:3: this_Any_3= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      			/* */
                      		
                    }
                    if ( state.backtracking==0 ) {

                      			newCompositeNode(grammarAccess.getAttributeAccess().getAnyParserRuleCall_3());
                      		
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_Any_3=ruleAny();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			current = this_Any_3;
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
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleAttributeDescendantOf"
    // InternalEclParser.g:1801:1: entryRuleAttributeDescendantOf returns [EObject current=null] : iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF ;
    public final EObject entryRuleAttributeDescendantOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeDescendantOf = null;


        try {
            // InternalEclParser.g:1801:62: (iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF )
            // InternalEclParser.g:1802:2: iv_ruleAttributeDescendantOf= ruleAttributeDescendantOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeDescendantOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeDescendantOf=ruleAttributeDescendantOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeDescendantOf; 
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
    // $ANTLR end "entryRuleAttributeDescendantOf"


    // $ANTLR start "ruleAttributeDescendantOf"
    // InternalEclParser.g:1808:1: ruleAttributeDescendantOf returns [EObject current=null] : (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleAttributeDescendantOf() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1814:2: ( (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // InternalEclParser.g:1815:2: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // InternalEclParser.g:1815:2: (this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // InternalEclParser.g:1816:3: this_LT_0= RULE_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getAttributeDescendantOfAccess().getLTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1820:3: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // InternalEclParser.g:1821:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // InternalEclParser.g:1821:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // InternalEclParser.g:1822:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // InternalEclParser.g:1822:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_DIGIT_NONZERO) ) {
                alt24=1;
            }
            else if ( (LA24_0==RULE_WILDCARD) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // InternalEclParser.g:1823:6: lv_constraint_1_1= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_1=ruleConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
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
                    // InternalEclParser.g:1839:6: lv_constraint_1_2= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintAnyParserRuleCall_1_0_1());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
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
    // $ANTLR end "ruleAttributeDescendantOf"


    // $ANTLR start "entryRuleAttributeDescendantOrSelfOf"
    // InternalEclParser.g:1861:1: entryRuleAttributeDescendantOrSelfOf returns [EObject current=null] : iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF ;
    public final EObject entryRuleAttributeDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeDescendantOrSelfOf = null;


        try {
            // InternalEclParser.g:1861:68: (iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF )
            // InternalEclParser.g:1862:2: iv_ruleAttributeDescendantOrSelfOf= ruleAttributeDescendantOrSelfOf EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAttributeDescendantOrSelfOf=ruleAttributeDescendantOrSelfOf();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttributeDescendantOrSelfOf; 
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
    // $ANTLR end "entryRuleAttributeDescendantOrSelfOf"


    // $ANTLR start "ruleAttributeDescendantOrSelfOf"
    // InternalEclParser.g:1868:1: ruleAttributeDescendantOrSelfOf returns [EObject current=null] : (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) ;
    public final EObject ruleAttributeDescendantOrSelfOf() throws RecognitionException {
        EObject current = null;

        Token this_DBL_LT_0=null;
        EObject lv_constraint_1_1 = null;

        EObject lv_constraint_1_2 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1874:2: ( (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) ) )
            // InternalEclParser.g:1875:2: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            {
            // InternalEclParser.g:1875:2: (this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) ) )
            // InternalEclParser.g:1876:3: this_DBL_LT_0= RULE_DBL_LT ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            {
            this_DBL_LT_0=(Token)match(input,RULE_DBL_LT,FollowSets000.FOLLOW_11); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DBL_LT_0, grammarAccess.getAttributeDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1880:3: ( ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) ) )
            // InternalEclParser.g:1881:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            {
            // InternalEclParser.g:1881:4: ( (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny ) )
            // InternalEclParser.g:1882:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            {
            // InternalEclParser.g:1882:5: (lv_constraint_1_1= ruleConceptReference | lv_constraint_1_2= ruleAny )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_DIGIT_NONZERO) ) {
                alt25=1;
            }
            else if ( (LA25_0==RULE_WILDCARD) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // InternalEclParser.g:1883:6: lv_constraint_1_1= ruleConceptReference
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_1=ruleConceptReference();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
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
                    // InternalEclParser.g:1899:6: lv_constraint_1_2= ruleAny
                    {
                    if ( state.backtracking==0 ) {

                      						newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintAnyParserRuleCall_1_0_1());
                      					
                    }
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_constraint_1_2=ruleAny();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      						if (current==null) {
                      							current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
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
    // $ANTLR end "ruleAttributeDescendantOrSelfOf"


    // $ANTLR start "entryRuleCardinality"
    // InternalEclParser.g:1921:1: entryRuleCardinality returns [EObject current=null] : iv_ruleCardinality= ruleCardinality EOF ;
    public final EObject entryRuleCardinality() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCardinality = null;


        try {
            // InternalEclParser.g:1921:52: (iv_ruleCardinality= ruleCardinality EOF )
            // InternalEclParser.g:1922:2: iv_ruleCardinality= ruleCardinality EOF
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
    // InternalEclParser.g:1928:1: ruleCardinality returns [EObject current=null] : (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) ;
    public final EObject ruleCardinality() throws RecognitionException {
        EObject current = null;

        Token this_SQUARE_OPEN_0=null;
        Token this_TO_2=null;
        Token this_SQUARE_CLOSE_4=null;
        AntlrDatatypeRuleToken lv_min_1_0 = null;

        AntlrDatatypeRuleToken lv_max_3_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:1934:2: ( (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE ) )
            // InternalEclParser.g:1935:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            {
            // InternalEclParser.g:1935:2: (this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE )
            // InternalEclParser.g:1936:3: this_SQUARE_OPEN_0= RULE_SQUARE_OPEN ( (lv_min_1_0= ruleNonNegativeInteger ) ) this_TO_2= RULE_TO ( (lv_max_3_0= ruleMaxValue ) ) this_SQUARE_CLOSE_4= RULE_SQUARE_CLOSE
            {
            this_SQUARE_OPEN_0=(Token)match(input,RULE_SQUARE_OPEN,FollowSets000.FOLLOW_18); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_SQUARE_OPEN_0, grammarAccess.getCardinalityAccess().getSQUARE_OPENTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:1940:3: ( (lv_min_1_0= ruleNonNegativeInteger ) )
            // InternalEclParser.g:1941:4: (lv_min_1_0= ruleNonNegativeInteger )
            {
            // InternalEclParser.g:1941:4: (lv_min_1_0= ruleNonNegativeInteger )
            // InternalEclParser.g:1942:5: lv_min_1_0= ruleNonNegativeInteger
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_19);
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

            this_TO_2=(Token)match(input,RULE_TO,FollowSets000.FOLLOW_20); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_TO_2, grammarAccess.getCardinalityAccess().getTOTerminalRuleCall_2());
              		
            }
            // InternalEclParser.g:1963:3: ( (lv_max_3_0= ruleMaxValue ) )
            // InternalEclParser.g:1964:4: (lv_max_3_0= ruleMaxValue )
            {
            // InternalEclParser.g:1964:4: (lv_max_3_0= ruleMaxValue )
            // InternalEclParser.g:1965:5: lv_max_3_0= ruleMaxValue
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_21);
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
    // InternalEclParser.g:1990:1: entryRuleComparison returns [EObject current=null] : iv_ruleComparison= ruleComparison EOF ;
    public final EObject entryRuleComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComparison = null;


        try {
            // InternalEclParser.g:1990:51: (iv_ruleComparison= ruleComparison EOF )
            // InternalEclParser.g:1991:2: iv_ruleComparison= ruleComparison EOF
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
    // InternalEclParser.g:1997:1: ruleComparison returns [EObject current=null] : (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) ;
    public final EObject ruleComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeComparison_0 = null;

        EObject this_DataTypeComparison_1 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2003:2: ( (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison ) )
            // InternalEclParser.g:2004:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            {
            // InternalEclParser.g:2004:2: (this_AttributeComparison_0= ruleAttributeComparison | this_DataTypeComparison_1= ruleDataTypeComparison )
            int alt26=2;
            switch ( input.LA(1) ) {
            case RULE_EQUAL:
                {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==RULE_HASH||LA26_1==RULE_STRING) ) {
                    alt26=2;
                }
                else if ( (LA26_1==RULE_DIGIT_NONZERO||LA26_1==RULE_ROUND_OPEN||LA26_1==RULE_CARET||LA26_1==RULE_WILDCARD||(LA26_1>=RULE_LT && LA26_1<=RULE_GT_EM)) ) {
                    alt26=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;
                }
                }
                break;
            case RULE_NOT_EQUAL:
                {
                int LA26_2 = input.LA(2);

                if ( (LA26_2==RULE_DIGIT_NONZERO||LA26_2==RULE_ROUND_OPEN||LA26_2==RULE_CARET||LA26_2==RULE_WILDCARD||(LA26_2>=RULE_LT && LA26_2<=RULE_GT_EM)) ) {
                    alt26=1;
                }
                else if ( (LA26_2==RULE_HASH||LA26_2==RULE_STRING) ) {
                    alt26=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 2, input);

                    throw nvae;
                }
                }
                break;
            case RULE_LT:
            case RULE_GT:
            case RULE_GTE:
            case RULE_LTE:
                {
                alt26=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // InternalEclParser.g:2005:3: this_AttributeComparison_0= ruleAttributeComparison
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
                    // InternalEclParser.g:2017:3: this_DataTypeComparison_1= ruleDataTypeComparison
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
    // InternalEclParser.g:2032:1: entryRuleAttributeComparison returns [EObject current=null] : iv_ruleAttributeComparison= ruleAttributeComparison EOF ;
    public final EObject entryRuleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeComparison = null;


        try {
            // InternalEclParser.g:2032:60: (iv_ruleAttributeComparison= ruleAttributeComparison EOF )
            // InternalEclParser.g:2033:2: iv_ruleAttributeComparison= ruleAttributeComparison EOF
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
    // InternalEclParser.g:2039:1: ruleAttributeComparison returns [EObject current=null] : (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) ;
    public final EObject ruleAttributeComparison() throws RecognitionException {
        EObject current = null;

        EObject this_AttributeValueEquals_0 = null;

        EObject this_AttributeValueNotEquals_1 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2045:2: ( (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals ) )
            // InternalEclParser.g:2046:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            {
            // InternalEclParser.g:2046:2: (this_AttributeValueEquals_0= ruleAttributeValueEquals | this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==RULE_EQUAL) ) {
                alt27=1;
            }
            else if ( (LA27_0==RULE_NOT_EQUAL) ) {
                alt27=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // InternalEclParser.g:2047:3: this_AttributeValueEquals_0= ruleAttributeValueEquals
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
                    // InternalEclParser.g:2059:3: this_AttributeValueNotEquals_1= ruleAttributeValueNotEquals
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
    // InternalEclParser.g:2074:1: entryRuleDataTypeComparison returns [EObject current=null] : iv_ruleDataTypeComparison= ruleDataTypeComparison EOF ;
    public final EObject entryRuleDataTypeComparison() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataTypeComparison = null;


        try {
            // InternalEclParser.g:2074:59: (iv_ruleDataTypeComparison= ruleDataTypeComparison EOF )
            // InternalEclParser.g:2075:2: iv_ruleDataTypeComparison= ruleDataTypeComparison EOF
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
    // InternalEclParser.g:2081:1: ruleDataTypeComparison returns [EObject current=null] : (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals ) ;
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
            // InternalEclParser.g:2087:2: ( (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals ) )
            // InternalEclParser.g:2088:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )
            {
            // InternalEclParser.g:2088:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )
            int alt28=14;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // InternalEclParser.g:2089:3: this_StringValueEquals_0= ruleStringValueEquals
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
                    // InternalEclParser.g:2101:3: this_StringValueNotEquals_1= ruleStringValueNotEquals
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
                    // InternalEclParser.g:2113:3: this_IntegerValueEquals_2= ruleIntegerValueEquals
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
                    // InternalEclParser.g:2125:3: this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals
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
                    // InternalEclParser.g:2137:3: this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan
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
                    // InternalEclParser.g:2149:3: this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals
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
                    // InternalEclParser.g:2161:3: this_IntegerValueLessThan_6= ruleIntegerValueLessThan
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
                    // InternalEclParser.g:2173:3: this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals
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
                    // InternalEclParser.g:2185:3: this_DecimalValueEquals_8= ruleDecimalValueEquals
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
                    // InternalEclParser.g:2197:3: this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals
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
                    // InternalEclParser.g:2209:3: this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan
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
                    // InternalEclParser.g:2221:3: this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals
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
                    // InternalEclParser.g:2233:3: this_DecimalValueLessThan_12= ruleDecimalValueLessThan
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
                    // InternalEclParser.g:2245:3: this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals
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
    // InternalEclParser.g:2260:1: entryRuleAttributeValueEquals returns [EObject current=null] : iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF ;
    public final EObject entryRuleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueEquals = null;


        try {
            // InternalEclParser.g:2260:61: (iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF )
            // InternalEclParser.g:2261:2: iv_ruleAttributeValueEquals= ruleAttributeValueEquals EOF
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
    // InternalEclParser.g:2267:1: ruleAttributeValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2273:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // InternalEclParser.g:2274:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // InternalEclParser.g:2274:2: (this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // InternalEclParser.g:2275:3: this_EQUAL_0= RULE_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:2279:3: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // InternalEclParser.g:2280:4: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // InternalEclParser.g:2280:4: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // InternalEclParser.g:2281:5: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleSimpleExpressionConstraint();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SimpleExpressionConstraint");
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
    // InternalEclParser.g:2302:1: entryRuleAttributeValueNotEquals returns [EObject current=null] : iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF ;
    public final EObject entryRuleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeValueNotEquals = null;


        try {
            // InternalEclParser.g:2302:64: (iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF )
            // InternalEclParser.g:2303:2: iv_ruleAttributeValueNotEquals= ruleAttributeValueNotEquals EOF
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
    // InternalEclParser.g:2309:1: ruleAttributeValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) ;
    public final EObject ruleAttributeValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        EObject lv_constraint_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2315:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) ) )
            // InternalEclParser.g:2316:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            {
            // InternalEclParser.g:2316:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) ) )
            // InternalEclParser.g:2317:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:2321:3: ( (lv_constraint_1_0= ruleSimpleExpressionConstraint ) )
            // InternalEclParser.g:2322:4: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            {
            // InternalEclParser.g:2322:4: (lv_constraint_1_0= ruleSimpleExpressionConstraint )
            // InternalEclParser.g:2323:5: lv_constraint_1_0= ruleSimpleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_2);
            lv_constraint_1_0=ruleSimpleExpressionConstraint();

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
              						"com.b2international.snowowl.snomed.ecl.Ecl.SimpleExpressionConstraint");
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
    // InternalEclParser.g:2344:1: entryRuleStringValueEquals returns [EObject current=null] : iv_ruleStringValueEquals= ruleStringValueEquals EOF ;
    public final EObject entryRuleStringValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueEquals = null;


        try {
            // InternalEclParser.g:2344:58: (iv_ruleStringValueEquals= ruleStringValueEquals EOF )
            // InternalEclParser.g:2345:2: iv_ruleStringValueEquals= ruleStringValueEquals EOF
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
    // InternalEclParser.g:2351:1: ruleStringValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalEclParser.g:2357:2: ( (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalEclParser.g:2358:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalEclParser.g:2358:2: (this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalEclParser.g:2359:3: this_EQUAL_0= RULE_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_22); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getStringValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:2363:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalEclParser.g:2364:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalEclParser.g:2364:4: (lv_value_1_0= RULE_STRING )
            // InternalEclParser.g:2365:5: lv_value_1_0= RULE_STRING
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
    // InternalEclParser.g:2385:1: entryRuleStringValueNotEquals returns [EObject current=null] : iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF ;
    public final EObject entryRuleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringValueNotEquals = null;


        try {
            // InternalEclParser.g:2385:61: (iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF )
            // InternalEclParser.g:2386:2: iv_ruleStringValueNotEquals= ruleStringValueNotEquals EOF
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
    // InternalEclParser.g:2392:1: ruleStringValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleStringValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token lv_value_1_0=null;


        	enterRule();

        try {
            // InternalEclParser.g:2398:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalEclParser.g:2399:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalEclParser.g:2399:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalEclParser.g:2400:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL ( (lv_value_1_0= RULE_STRING ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_22); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getStringValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:2404:3: ( (lv_value_1_0= RULE_STRING ) )
            // InternalEclParser.g:2405:4: (lv_value_1_0= RULE_STRING )
            {
            // InternalEclParser.g:2405:4: (lv_value_1_0= RULE_STRING )
            // InternalEclParser.g:2406:5: lv_value_1_0= RULE_STRING
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
    // InternalEclParser.g:2426:1: entryRuleIntegerValueEquals returns [EObject current=null] : iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF ;
    public final EObject entryRuleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueEquals = null;


        try {
            // InternalEclParser.g:2426:59: (iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF )
            // InternalEclParser.g:2427:2: iv_ruleIntegerValueEquals= ruleIntegerValueEquals EOF
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
    // InternalEclParser.g:2433:1: ruleIntegerValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2439:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2440:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2440:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2441:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getIntegerValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2449:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2450:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2450:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2451:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2472:1: entryRuleIntegerValueNotEquals returns [EObject current=null] : iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF ;
    public final EObject entryRuleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueNotEquals = null;


        try {
            // InternalEclParser.g:2472:62: (iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF )
            // InternalEclParser.g:2473:2: iv_ruleIntegerValueNotEquals= ruleIntegerValueNotEquals EOF
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
    // InternalEclParser.g:2479:1: ruleIntegerValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2485:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2486:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2486:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2487:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getIntegerValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2495:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2496:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2496:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2497:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2518:1: entryRuleIntegerValueGreaterThan returns [EObject current=null] : iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF ;
    public final EObject entryRuleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThan = null;


        try {
            // InternalEclParser.g:2518:64: (iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF )
            // InternalEclParser.g:2519:2: iv_ruleIntegerValueGreaterThan= ruleIntegerValueGreaterThan EOF
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
    // InternalEclParser.g:2525:1: ruleIntegerValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2531:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2532:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2532:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2533:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getIntegerValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2541:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2542:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2542:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2543:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2564:1: entryRuleIntegerValueLessThan returns [EObject current=null] : iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF ;
    public final EObject entryRuleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThan = null;


        try {
            // InternalEclParser.g:2564:61: (iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF )
            // InternalEclParser.g:2565:2: iv_ruleIntegerValueLessThan= ruleIntegerValueLessThan EOF
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
    // InternalEclParser.g:2571:1: ruleIntegerValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2577:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2578:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2578:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2579:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getIntegerValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2587:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2588:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2588:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2589:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2610:1: entryRuleIntegerValueGreaterThanEquals returns [EObject current=null] : iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF ;
    public final EObject entryRuleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueGreaterThanEquals = null;


        try {
            // InternalEclParser.g:2610:70: (iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF )
            // InternalEclParser.g:2611:2: iv_ruleIntegerValueGreaterThanEquals= ruleIntegerValueGreaterThanEquals EOF
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
    // InternalEclParser.g:2617:1: ruleIntegerValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2623:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2624:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2624:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2625:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2633:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2634:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2634:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2635:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2656:1: entryRuleIntegerValueLessThanEquals returns [EObject current=null] : iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF ;
    public final EObject entryRuleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerValueLessThanEquals = null;


        try {
            // InternalEclParser.g:2656:67: (iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF )
            // InternalEclParser.g:2657:2: iv_ruleIntegerValueLessThanEquals= ruleIntegerValueLessThanEquals EOF
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
    // InternalEclParser.g:2663:1: ruleIntegerValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) ;
    public final EObject ruleIntegerValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2669:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) ) )
            // InternalEclParser.g:2670:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            {
            // InternalEclParser.g:2670:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) ) )
            // InternalEclParser.g:2671:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleInteger ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getIntegerValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2679:3: ( (lv_value_2_0= ruleInteger ) )
            // InternalEclParser.g:2680:4: (lv_value_2_0= ruleInteger )
            {
            // InternalEclParser.g:2680:4: (lv_value_2_0= ruleInteger )
            // InternalEclParser.g:2681:5: lv_value_2_0= ruleInteger
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
    // InternalEclParser.g:2702:1: entryRuleDecimalValueEquals returns [EObject current=null] : iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF ;
    public final EObject entryRuleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueEquals = null;


        try {
            // InternalEclParser.g:2702:59: (iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF )
            // InternalEclParser.g:2703:2: iv_ruleDecimalValueEquals= ruleDecimalValueEquals EOF
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
    // InternalEclParser.g:2709:1: ruleDecimalValueEquals returns [EObject current=null] : (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueEquals() throws RecognitionException {
        EObject current = null;

        Token this_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2715:2: ( (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2716:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2716:2: (this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2717:3: this_EQUAL_0= RULE_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_EQUAL_0=(Token)match(input,RULE_EQUAL,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_EQUAL_0, grammarAccess.getDecimalValueEqualsAccess().getEQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2725:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2726:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2726:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2727:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2748:1: entryRuleDecimalValueNotEquals returns [EObject current=null] : iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF ;
    public final EObject entryRuleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueNotEquals = null;


        try {
            // InternalEclParser.g:2748:62: (iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF )
            // InternalEclParser.g:2749:2: iv_ruleDecimalValueNotEquals= ruleDecimalValueNotEquals EOF
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
    // InternalEclParser.g:2755:1: ruleDecimalValueNotEquals returns [EObject current=null] : (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueNotEquals() throws RecognitionException {
        EObject current = null;

        Token this_NOT_EQUAL_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2761:2: ( (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2762:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2762:2: (this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2763:3: this_NOT_EQUAL_0= RULE_NOT_EQUAL this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_NOT_EQUAL_0=(Token)match(input,RULE_NOT_EQUAL,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getDecimalValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueNotEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2771:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2772:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2772:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2773:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2794:1: entryRuleDecimalValueGreaterThan returns [EObject current=null] : iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF ;
    public final EObject entryRuleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThan = null;


        try {
            // InternalEclParser.g:2794:64: (iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF )
            // InternalEclParser.g:2795:2: iv_ruleDecimalValueGreaterThan= ruleDecimalValueGreaterThan EOF
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
    // InternalEclParser.g:2801:1: ruleDecimalValueGreaterThan returns [EObject current=null] : (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThan() throws RecognitionException {
        EObject current = null;

        Token this_GT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2807:2: ( (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2808:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2808:2: (this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2809:3: this_GT_0= RULE_GT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GT_0=(Token)match(input,RULE_GT,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GT_0, grammarAccess.getDecimalValueGreaterThanAccess().getGTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2817:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2818:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2818:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2819:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2840:1: entryRuleDecimalValueLessThan returns [EObject current=null] : iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF ;
    public final EObject entryRuleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThan = null;


        try {
            // InternalEclParser.g:2840:61: (iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF )
            // InternalEclParser.g:2841:2: iv_ruleDecimalValueLessThan= ruleDecimalValueLessThan EOF
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
    // InternalEclParser.g:2847:1: ruleDecimalValueLessThan returns [EObject current=null] : (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThan() throws RecognitionException {
        EObject current = null;

        Token this_LT_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2853:2: ( (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2854:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2854:2: (this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2855:3: this_LT_0= RULE_LT this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LT_0=(Token)match(input,RULE_LT,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LT_0, grammarAccess.getDecimalValueLessThanAccess().getLTTerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2863:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2864:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2864:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2865:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2886:1: entryRuleDecimalValueGreaterThanEquals returns [EObject current=null] : iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF ;
    public final EObject entryRuleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueGreaterThanEquals = null;


        try {
            // InternalEclParser.g:2886:70: (iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF )
            // InternalEclParser.g:2887:2: iv_ruleDecimalValueGreaterThanEquals= ruleDecimalValueGreaterThanEquals EOF
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
    // InternalEclParser.g:2893:1: ruleDecimalValueGreaterThanEquals returns [EObject current=null] : (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueGreaterThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_GTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2899:2: ( (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2900:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2900:2: (this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2901:3: this_GTE_0= RULE_GTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_GTE_0=(Token)match(input,RULE_GTE,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_GTE_0, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2909:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2910:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2910:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2911:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2932:1: entryRuleDecimalValueLessThanEquals returns [EObject current=null] : iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF ;
    public final EObject entryRuleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecimalValueLessThanEquals = null;


        try {
            // InternalEclParser.g:2932:67: (iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF )
            // InternalEclParser.g:2933:2: iv_ruleDecimalValueLessThanEquals= ruleDecimalValueLessThanEquals EOF
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
    // InternalEclParser.g:2939:1: ruleDecimalValueLessThanEquals returns [EObject current=null] : (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) ;
    public final EObject ruleDecimalValueLessThanEquals() throws RecognitionException {
        EObject current = null;

        Token this_LTE_0=null;
        Token this_HASH_1=null;
        AntlrDatatypeRuleToken lv_value_2_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2945:2: ( (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) ) )
            // InternalEclParser.g:2946:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            {
            // InternalEclParser.g:2946:2: (this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) ) )
            // InternalEclParser.g:2947:3: this_LTE_0= RULE_LTE this_HASH_1= RULE_HASH ( (lv_value_2_0= ruleDecimal ) )
            {
            this_LTE_0=(Token)match(input,RULE_LTE,FollowSets000.FOLLOW_23); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_LTE_0, grammarAccess.getDecimalValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
              		
            }
            this_HASH_1=(Token)match(input,RULE_HASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:2955:3: ( (lv_value_2_0= ruleDecimal ) )
            // InternalEclParser.g:2956:4: (lv_value_2_0= ruleDecimal )
            {
            // InternalEclParser.g:2956:4: (lv_value_2_0= ruleDecimal )
            // InternalEclParser.g:2957:5: lv_value_2_0= ruleDecimal
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
    // InternalEclParser.g:2978:1: entryRuleNestedExpression returns [EObject current=null] : iv_ruleNestedExpression= ruleNestedExpression EOF ;
    public final EObject entryRuleNestedExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNestedExpression = null;


        try {
            // InternalEclParser.g:2978:57: (iv_ruleNestedExpression= ruleNestedExpression EOF )
            // InternalEclParser.g:2979:2: iv_ruleNestedExpression= ruleNestedExpression EOF
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
    // InternalEclParser.g:2985:1: ruleNestedExpression returns [EObject current=null] : (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) ;
    public final EObject ruleNestedExpression() throws RecognitionException {
        EObject current = null;

        Token this_ROUND_OPEN_0=null;
        Token this_ROUND_CLOSE_2=null;
        EObject lv_nested_1_0 = null;



        	enterRule();

        try {
            // InternalEclParser.g:2991:2: ( (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE ) )
            // InternalEclParser.g:2992:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            {
            // InternalEclParser.g:2992:2: (this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE )
            // InternalEclParser.g:2993:3: this_ROUND_OPEN_0= RULE_ROUND_OPEN ( (lv_nested_1_0= ruleExpressionConstraint ) ) this_ROUND_CLOSE_2= RULE_ROUND_CLOSE
            {
            this_ROUND_OPEN_0=(Token)match(input,RULE_ROUND_OPEN,FollowSets000.FOLLOW_4); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedExpressionAccess().getROUND_OPENTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:2997:3: ( (lv_nested_1_0= ruleExpressionConstraint ) )
            // InternalEclParser.g:2998:4: (lv_nested_1_0= ruleExpressionConstraint )
            {
            // InternalEclParser.g:2998:4: (lv_nested_1_0= ruleExpressionConstraint )
            // InternalEclParser.g:2999:5: lv_nested_1_0= ruleExpressionConstraint
            {
            if ( state.backtracking==0 ) {

              					newCompositeNode(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0());
              				
            }
            pushFollow(FollowSets000.FOLLOW_13);
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
    // InternalEclParser.g:3024:1: entryRuleSnomedIdentifier returns [String current=null] : iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF ;
    public final String entryRuleSnomedIdentifier() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSnomedIdentifier = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3026:2: (iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF )
            // InternalEclParser.g:3027:2: iv_ruleSnomedIdentifier= ruleSnomedIdentifier EOF
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
    // InternalEclParser.g:3036:1: ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) ;
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
            // InternalEclParser.g:3043:2: ( (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ ) )
            // InternalEclParser.g:3044:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            {
            // InternalEclParser.g:3044:2: (this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+ )
            // InternalEclParser.g:3045:3: this_DIGIT_NONZERO_0= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO ) (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO ) (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO ) (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO ) (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            {
            this_DIGIT_NONZERO_0=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DIGIT_NONZERO_0);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0());
              		
            }
            // InternalEclParser.g:3052:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO | this_ZERO_2= RULE_ZERO )
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
                    // InternalEclParser.g:3053:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3061:4: this_ZERO_2= RULE_ZERO
                    {
                    this_ZERO_2=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_2);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEclParser.g:3069:3: (this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO | this_ZERO_4= RULE_ZERO )
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
                    // InternalEclParser.g:3070:4: this_DIGIT_NONZERO_3= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_3=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_3);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3078:4: this_ZERO_4= RULE_ZERO
                    {
                    this_ZERO_4=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_4);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEclParser.g:3086:3: (this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO | this_ZERO_6= RULE_ZERO )
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
                    // InternalEclParser.g:3087:4: this_DIGIT_NONZERO_5= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_5=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_5);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3095:4: this_ZERO_6= RULE_ZERO
                    {
                    this_ZERO_6=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_6);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEclParser.g:3103:3: (this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO | this_ZERO_8= RULE_ZERO )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==RULE_DIGIT_NONZERO) ) {
                alt32=1;
            }
            else if ( (LA32_0==RULE_ZERO) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // InternalEclParser.g:3104:4: this_DIGIT_NONZERO_7= RULE_DIGIT_NONZERO
                    {
                    this_DIGIT_NONZERO_7=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_7);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3112:4: this_ZERO_8= RULE_ZERO
                    {
                    this_ZERO_8=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_ZERO_8);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1());
                      			
                    }

                    }
                    break;

            }

            // InternalEclParser.g:3120:3: (this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO | this_ZERO_10= RULE_ZERO )+
            int cnt33=0;
            loop33:
            do {
                int alt33=3;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==RULE_DIGIT_NONZERO) ) {
                    alt33=1;
                }
                else if ( (LA33_0==RULE_ZERO) ) {
                    alt33=2;
                }


                switch (alt33) {
            	case 1 :
            	    // InternalEclParser.g:3121:4: this_DIGIT_NONZERO_9= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_9=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_9);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalEclParser.g:3129:4: this_ZERO_10= RULE_ZERO
            	    {
            	    this_ZERO_10=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_10);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt33 >= 1 ) break loop33;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(33, input);
                        throw eee;
                }
                cnt33++;
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
    // InternalEclParser.g:3144:1: entryRuleNonNegativeInteger returns [String current=null] : iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF ;
    public final String entryRuleNonNegativeInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3146:2: (iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF )
            // InternalEclParser.g:3147:2: iv_ruleNonNegativeInteger= ruleNonNegativeInteger EOF
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
    // InternalEclParser.g:3156:1: ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ZERO_0=null;
        Token this_DIGIT_NONZERO_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3163:2: ( (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ) )
            // InternalEclParser.g:3164:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            {
            // InternalEclParser.g:3164:2: (this_ZERO_0= RULE_ZERO | (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
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
                    // InternalEclParser.g:3165:3: this_ZERO_0= RULE_ZERO
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
                    // InternalEclParser.g:3173:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    {
                    // InternalEclParser.g:3173:3: (this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
                    // InternalEclParser.g:3174:4: this_DIGIT_NONZERO_1= RULE_DIGIT_NONZERO (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
                    {
                    this_DIGIT_NONZERO_1=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_DIGIT_NONZERO_1);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
                      			
                    }
                    // InternalEclParser.g:3181:4: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
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
                    	    // InternalEclParser.g:3182:5: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
                    	    {
                    	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_DIGIT_NONZERO_2);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0());
                    	      				
                    	    }

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEclParser.g:3190:5: this_ZERO_3= RULE_ZERO
                    	    {
                    	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
                    	    if ( state.backtracking==0 ) {

                    	      					current.merge(this_ZERO_3);
                    	      				
                    	    }
                    	    if ( state.backtracking==0 ) {

                    	      					newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1());
                    	      				
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
    // InternalEclParser.g:3206:1: entryRuleMaxValue returns [String current=null] : iv_ruleMaxValue= ruleMaxValue EOF ;
    public final String entryRuleMaxValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleMaxValue = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3208:2: (iv_ruleMaxValue= ruleMaxValue EOF )
            // InternalEclParser.g:3209:2: iv_ruleMaxValue= ruleMaxValue EOF
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
    // InternalEclParser.g:3218:1: ruleMaxValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) ;
    public final AntlrDatatypeRuleToken ruleMaxValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WILDCARD_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3225:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD ) )
            // InternalEclParser.g:3226:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            {
            // InternalEclParser.g:3226:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger | this_WILDCARD_1= RULE_WILDCARD )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( ((LA36_0>=RULE_ZERO && LA36_0<=RULE_DIGIT_NONZERO)) ) {
                alt36=1;
            }
            else if ( (LA36_0==RULE_WILDCARD) ) {
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
                    // InternalEclParser.g:3227:3: this_NonNegativeInteger_0= ruleNonNegativeInteger
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
                    // InternalEclParser.g:3238:3: this_WILDCARD_1= RULE_WILDCARD
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
    // InternalEclParser.g:3252:1: entryRuleInteger returns [String current=null] : iv_ruleInteger= ruleInteger EOF ;
    public final String entryRuleInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInteger = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3254:2: (iv_ruleInteger= ruleInteger EOF )
            // InternalEclParser.g:3255:2: iv_ruleInteger= ruleInteger EOF
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
    // InternalEclParser.g:3264:1: ruleInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) ;
    public final AntlrDatatypeRuleToken ruleInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3271:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger ) )
            // InternalEclParser.g:3272:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            {
            // InternalEclParser.g:3272:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger )
            // InternalEclParser.g:3273:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeInteger_2= ruleNonNegativeInteger
            {
            // InternalEclParser.g:3273:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt37=3;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==RULE_PLUS) ) {
                alt37=1;
            }
            else if ( (LA37_0==RULE_DASH) ) {
                alt37=2;
            }
            switch (alt37) {
                case 1 :
                    // InternalEclParser.g:3274:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_18); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getIntegerAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3282:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_18); if (state.failed) return current;
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
    // InternalEclParser.g:3307:1: entryRuleDecimal returns [String current=null] : iv_ruleDecimal= ruleDecimal EOF ;
    public final String entryRuleDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3309:2: (iv_ruleDecimal= ruleDecimal EOF )
            // InternalEclParser.g:3310:2: iv_ruleDecimal= ruleDecimal EOF
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
    // InternalEclParser.g:3319:1: ruleDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) ;
    public final AntlrDatatypeRuleToken ruleDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_PLUS_0=null;
        Token this_DASH_1=null;
        AntlrDatatypeRuleToken this_NonNegativeDecimal_2 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3326:2: ( ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal ) )
            // InternalEclParser.g:3327:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            {
            // InternalEclParser.g:3327:2: ( (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal )
            // InternalEclParser.g:3328:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )? this_NonNegativeDecimal_2= ruleNonNegativeDecimal
            {
            // InternalEclParser.g:3328:3: (this_PLUS_0= RULE_PLUS | this_DASH_1= RULE_DASH )?
            int alt38=3;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==RULE_PLUS) ) {
                alt38=1;
            }
            else if ( (LA38_0==RULE_DASH) ) {
                alt38=2;
            }
            switch (alt38) {
                case 1 :
                    // InternalEclParser.g:3329:4: this_PLUS_0= RULE_PLUS
                    {
                    this_PLUS_0=(Token)match(input,RULE_PLUS,FollowSets000.FOLLOW_24); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      				current.merge(this_PLUS_0);
                      			
                    }
                    if ( state.backtracking==0 ) {

                      				newLeafNode(this_PLUS_0, grammarAccess.getDecimalAccess().getPLUSTerminalRuleCall_0_0());
                      			
                    }

                    }
                    break;
                case 2 :
                    // InternalEclParser.g:3337:4: this_DASH_1= RULE_DASH
                    {
                    this_DASH_1=(Token)match(input,RULE_DASH,FollowSets000.FOLLOW_24); if (state.failed) return current;
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
    // InternalEclParser.g:3362:1: entryRuleNonNegativeDecimal returns [String current=null] : iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF ;
    public final String entryRuleNonNegativeDecimal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNonNegativeDecimal = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3364:2: (iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF )
            // InternalEclParser.g:3365:2: iv_ruleNonNegativeDecimal= ruleNonNegativeDecimal EOF
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
    // InternalEclParser.g:3374:1: ruleNonNegativeDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) ;
    public final AntlrDatatypeRuleToken ruleNonNegativeDecimal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_DOT_1=null;
        Token this_DIGIT_NONZERO_2=null;
        Token this_ZERO_3=null;
        AntlrDatatypeRuleToken this_NonNegativeInteger_0 = null;



        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalEclParser.g:3381:2: ( (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* ) )
            // InternalEclParser.g:3382:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            {
            // InternalEclParser.g:3382:2: (this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )* )
            // InternalEclParser.g:3383:3: this_NonNegativeInteger_0= ruleNonNegativeInteger this_DOT_1= RULE_DOT (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            {
            if ( state.backtracking==0 ) {

              			newCompositeNode(grammarAccess.getNonNegativeDecimalAccess().getNonNegativeIntegerParserRuleCall_0());
              		
            }
            pushFollow(FollowSets000.FOLLOW_26);
            this_NonNegativeInteger_0=ruleNonNegativeInteger();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_NonNegativeInteger_0);
              		
            }
            if ( state.backtracking==0 ) {

              			afterParserOrEnumRuleCall();
              		
            }
            this_DOT_1=(Token)match(input,RULE_DOT,FollowSets000.FOLLOW_25); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			current.merge(this_DOT_1);
              		
            }
            if ( state.backtracking==0 ) {

              			newLeafNode(this_DOT_1, grammarAccess.getNonNegativeDecimalAccess().getDOTTerminalRuleCall_1());
              		
            }
            // InternalEclParser.g:3400:3: (this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO | this_ZERO_3= RULE_ZERO )*
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==RULE_DIGIT_NONZERO) ) {
                    alt39=1;
                }
                else if ( (LA39_0==RULE_ZERO) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // InternalEclParser.g:3401:4: this_DIGIT_NONZERO_2= RULE_DIGIT_NONZERO
            	    {
            	    this_DIGIT_NONZERO_2=(Token)match(input,RULE_DIGIT_NONZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_DIGIT_NONZERO_2);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeDecimalAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
            	      			
            	    }

            	    }
            	    break;
            	case 2 :
            	    // InternalEclParser.g:3409:4: this_ZERO_3= RULE_ZERO
            	    {
            	    this_ZERO_3=(Token)match(input,RULE_ZERO,FollowSets000.FOLLOW_25); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      				current.merge(this_ZERO_3);
            	      			
            	    }
            	    if ( state.backtracking==0 ) {

            	      				newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeDecimalAccess().getZEROTerminalRuleCall_2_1());
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop39;
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

    // $ANTLR start synpred19_InternalEclParser
    public final void synpred19_InternalEclParser_fragment() throws RecognitionException {   
        // InternalEclParser.g:1107:4: ( OR )
        // InternalEclParser.g:1107:5: OR
        {
        match(input,OR,FollowSets000.FOLLOW_2); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_InternalEclParser

    // $ANTLR start synpred21_InternalEclParser
    public final void synpred21_InternalEclParser_fragment() throws RecognitionException {   
        // InternalEclParser.g:1175:4: ( AND | Comma )
        // InternalEclParser.g:
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
    // $ANTLR end synpred21_InternalEclParser

    // Delegated rules

    public final boolean synpred21_InternalEclParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_InternalEclParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_InternalEclParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_InternalEclParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA15 dfa15 = new DFA15(this);
    protected DFA28 dfa28 = new DFA28(this);
    static final String dfa_1s = "\20\uffff";
    static final String dfa_2s = "\1\11\1\13\3\uffff\2\12\1\13\2\12\1\23\1\13\1\23\1\11\2\13";
    static final String dfa_3s = "\1\36\1\14\3\uffff\1\12\1\14\1\31\2\14\3\23\1\36\2\23";
    static final String dfa_4s = "\2\uffff\1\1\1\2\1\3\13\uffff";
    static final String dfa_5s = "\20\uffff}>";
    static final String[] dfa_6s = {
            "\1\2\2\uffff\1\2\1\uffff\1\3\1\uffff\1\4\1\uffff\1\1\6\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
            "\1\5\1\6",
            "",
            "",
            "",
            "\1\7",
            "\1\7\1\11\1\10",
            "\1\12\1\13\14\uffff\1\14",
            "\1\7\1\11\1\10",
            "\1\7\1\11\1\10",
            "\1\15",
            "\1\17\1\16\6\uffff\1\15",
            "\1\15",
            "\1\2\2\uffff\1\2\1\uffff\1\3\12\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
            "\1\17\1\16\6\uffff\1\15",
            "\1\17\1\16\6\uffff\1\15"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "1237:2: (this_AttributeConstraint_0= ruleAttributeConstraint | this_AttributeGroup_1= ruleAttributeGroup | this_NestedRefinement_2= ruleNestedRefinement )";
        }
    }
    static final String dfa_7s = "\77\uffff";
    static final String dfa_8s = "\21\uffff\2\50\2\uffff\2\53\2\uffff\2\60\2\uffff\2\64\2\uffff\2\70\2\uffff\2\73\2\uffff\2\50\2\uffff\2\53\2\uffff\2\60\2\uffff\2\64\2\uffff\2\70\2\uffff\2\73";
    static final String dfa_9s = "\1\32\6\44\1\uffff\2\13\1\uffff\6\13\2\4\2\13\2\4\2\13\2\4\2\13\2\4\2\13\2\4\2\13\2\4\2\uffff\2\4\2\uffff\2\4\2\uffff\2\4\2\uffff\2\4\2\uffff\2\4\2\uffff\2\4";
    static final String dfa_10s = "\1\43\2\50\4\44\1\uffff\2\25\1\uffff\4\25\2\14\2\30\2\14\2\30\2\14\2\30\2\14\2\30\2\14\2\30\2\14\2\30\2\uffff\2\30\2\uffff\2\30\2\uffff\2\30\2\uffff\2\30\2\uffff\2\30\2\uffff\2\30";
    static final String dfa_11s = "\7\uffff\1\1\2\uffff\1\2\34\uffff\1\11\1\3\2\uffff\1\4\1\12\2\uffff\1\13\1\5\2\uffff\1\14\1\6\2\uffff\1\15\1\7\2\uffff\1\10\1\16\2\uffff";
    static final String dfa_12s = "\77\uffff}>";
    static final String[] dfa_13s = {
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
            "\4\50\7\uffff\1\50\1\uffff\1\50\6\uffff\1\47",
            "\4\50\3\uffff\1\52\1\51\2\uffff\1\50\1\uffff\1\50\6\uffff\1\47",
            "\1\25\1\26",
            "\1\25\1\26",
            "\4\53\7\uffff\1\53\1\uffff\1\53\6\uffff\1\54",
            "\4\53\3\uffff\1\56\1\55\2\uffff\1\53\1\uffff\1\53\6\uffff\1\54",
            "\1\31\1\32",
            "\1\31\1\32",
            "\4\60\7\uffff\1\60\1\uffff\1\60\6\uffff\1\57",
            "\4\60\3\uffff\1\62\1\61\2\uffff\1\60\1\uffff\1\60\6\uffff\1\57",
            "\1\35\1\36",
            "\1\35\1\36",
            "\4\64\7\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\4\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\1\41\1\42",
            "\1\41\1\42",
            "\4\70\7\uffff\1\70\1\uffff\1\70\6\uffff\1\67",
            "\4\70\3\uffff\1\72\1\71\2\uffff\1\70\1\uffff\1\70\6\uffff\1\67",
            "\1\45\1\46",
            "\1\45\1\46",
            "\4\73\7\uffff\1\73\1\uffff\1\73\6\uffff\1\74",
            "\4\73\3\uffff\1\76\1\75\2\uffff\1\73\1\uffff\1\73\6\uffff\1\74",
            "",
            "",
            "\4\50\3\uffff\1\52\1\51\2\uffff\1\50\1\uffff\1\50\6\uffff\1\47",
            "\4\50\3\uffff\1\52\1\51\2\uffff\1\50\1\uffff\1\50\6\uffff\1\47",
            "",
            "",
            "\4\53\3\uffff\1\56\1\55\2\uffff\1\53\1\uffff\1\53\6\uffff\1\54",
            "\4\53\3\uffff\1\56\1\55\2\uffff\1\53\1\uffff\1\53\6\uffff\1\54",
            "",
            "",
            "\4\60\3\uffff\1\62\1\61\2\uffff\1\60\1\uffff\1\60\6\uffff\1\57",
            "\4\60\3\uffff\1\62\1\61\2\uffff\1\60\1\uffff\1\60\6\uffff\1\57",
            "",
            "",
            "\4\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "\4\64\3\uffff\1\66\1\65\2\uffff\1\64\1\uffff\1\64\6\uffff\1\63",
            "",
            "",
            "\4\70\3\uffff\1\72\1\71\2\uffff\1\70\1\uffff\1\70\6\uffff\1\67",
            "\4\70\3\uffff\1\72\1\71\2\uffff\1\70\1\uffff\1\70\6\uffff\1\67",
            "",
            "",
            "\4\73\3\uffff\1\76\1\75\2\uffff\1\73\1\uffff\1\73\6\uffff\1\74",
            "\4\73\3\uffff\1\76\1\75\2\uffff\1\73\1\uffff\1\73\6\uffff\1\74"
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final short[] dfa_8 = DFA.unpackEncodedString(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final char[] dfa_10 = DFA.unpackEncodedStringToUnsignedChars(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[] dfa_12 = DFA.unpackEncodedString(dfa_12s);
    static final short[][] dfa_13 = unpackEncodedStringArray(dfa_13s);

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = dfa_7;
            this.eof = dfa_8;
            this.min = dfa_9;
            this.max = dfa_10;
            this.accept = dfa_11;
            this.special = dfa_12;
            this.transition = dfa_13;
        }
        public String getDescription() {
            return "2088:2: (this_StringValueEquals_0= ruleStringValueEquals | this_StringValueNotEquals_1= ruleStringValueNotEquals | this_IntegerValueEquals_2= ruleIntegerValueEquals | this_IntegerValueNotEquals_3= ruleIntegerValueNotEquals | this_IntegerValueGreaterThan_4= ruleIntegerValueGreaterThan | this_IntegerValueGreaterThanEquals_5= ruleIntegerValueGreaterThanEquals | this_IntegerValueLessThan_6= ruleIntegerValueLessThan | this_IntegerValueLessThanEquals_7= ruleIntegerValueLessThanEquals | this_DecimalValueEquals_8= ruleDecimalValueEquals | this_DecimalValueNotEquals_9= ruleDecimalValueNotEquals | this_DecimalValueGreaterThan_10= ruleDecimalValueGreaterThan | this_DecimalValueGreaterThanEquals_11= ruleDecimalValueGreaterThanEquals | this_DecimalValueLessThan_12= ruleDecimalValueLessThan | this_DecimalValueLessThanEquals_13= ruleDecimalValueLessThanEquals )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000042L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x00000003F2411000L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x00000000000000A2L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000012L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000002002L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000052055200L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000001000002L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000052041200L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000002001000L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000000102L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000020000L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000004000L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000052051200L});
        public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000008000L});
        public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000C3C000000L});
        public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000001800L});
        public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000000400L});
        public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000002001800L});
        public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000080000L});
        public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000010000000000L});
        public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000301800L});
        public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000000001802L});
        public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000001000000L});
    }


}