/* Similarly to the SNOMED CT Compositional Grammar, it is also possible to nest
 * expression constraints within an attribute value. Please note that when the 
 * attribute value is a simple expression constraint, parentheses are not required
 * around the value. However, when the attribute value is either an expression 
 * constraint with a refinement, or a compound expression constraint with a binary 
 * operator, then parentheses must be placed around the attribute value. For example, 
 * the following expression constraint represents the set of clinical findings which
 * are associated with another clinical finding that has an associated morphology
 * of |Infarct| (or subtype).
 */

<404684003|Clinical finding|: 47429007|Associated with| = (
	<404684003|Clinical finding|: 116676008|Associated morphology| = <<55641003|Infarct|)