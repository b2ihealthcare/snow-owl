/* In many cases the value of the matching attribute is allowed to be either the
 * concept itself, or a descendant of that concept. In these cases, the << operator
 * is used prior to the concept representing the attribute value. For example, 
 * the expression constraint below is satisfied only by the set of lung disorders, 
 * which have an associated morphology of |Edema| or any descendant of |Edema|.
 */
 
<19829001|Disorder of lung|: 
	116676008|Associated morphology| = <<79654002|Edema|