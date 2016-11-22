/* When a conjunction and disjunction are both used together in a refinement, it 
 * is mandatory to use brackets to disambiguate the meaning of the expression 
 * constraint. For example, the following expression constraint is not valid:
 *
 * 
 * <404684003|Clinical finding|:
 *	    363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
 *	    116676008|Associated morphology| = <<415582006|Stenosis| OR
 *	    42752001|Due to| = <<445238008|Malignant carcinoid tumor|
 *
 * and must be expressed (depending on the intended meaning) as either:
 *
 * <404684003|Clinical finding|:
 *	    (363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
 *	    116676008|Associated morphology| = <<415582006|Stenosis|) OR
 *	    42752001|Due to| = <<445238008|Malignant carcinoid tumor|
 *
 * or as:
 */
 
<404684003|Clinical finding|:
    363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
     (116676008|Associated morphology| = <<415582006|Stenosis| OR
     42752001|Due to| = <<445238008|Malignant carcinoid tumor|)