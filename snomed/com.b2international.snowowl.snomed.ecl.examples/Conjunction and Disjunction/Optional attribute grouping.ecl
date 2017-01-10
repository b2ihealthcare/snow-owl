/* When more than one conjunction or more than one disjunction is used in a 
 * refinement, parentheses can be optionally applied. For example, the following
 * expression constraints are all valid and equivalent to each other:
 * 
 * <404684003|Clinical finding|:
 *	    363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
 *	    116676008|Associated morphology| = <<415582006|Stenosis| AND
 *	    42752001|Due to| = <<445238008|Malignant carcinoid tumor|
 *
 * <404684003|Clinical finding|:
 *	    (363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
 *	    116676008|Associated morphology| = <<415582006|Stenosis|) AND
 *	    42752001|Due to| = <<445238008|Malignant carcinoid tumor|
 */
 
<404684003|Clinical finding|:
    363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
     (116676008|Associated morphology| = <<415582006|Stenosis| AND
     42752001|Due to| = <<445238008|Malignant carcinoid tumor|)