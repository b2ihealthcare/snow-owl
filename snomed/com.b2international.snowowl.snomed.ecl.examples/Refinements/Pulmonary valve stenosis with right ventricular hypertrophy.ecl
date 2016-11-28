/* Similarly to SNOMED CT compositional grammar, expression constraints use curly
 * braces (i.e. "{..}") to indicate that a set of attributes should be grouped 
 * together in an attribute group. For example, the expression constraint below 
 * is satisfied only by the set of clinical findings with an associated morphology 
 * of |Stenosis| (or descendant) at the finding site |Pulmonary valve structure|
 * (or descendant), and also with an associated morphology of |Hypertrophy| 
 * (or descendant) at the finding site |Right ventricular structure| (or descendant).
 */
 
<404684003|Clinical finding|:
	{363698007|Finding site| = <<39057004|Pulmonary valve structure|,
     116676008|Associated morphology| = <<415582006|Stenosis|},
	{363698007|Finding site| = <<53085002|Right ventricular structure|, 
     116676008|Associated morphology| = <<56246009|Hypertrophy|}