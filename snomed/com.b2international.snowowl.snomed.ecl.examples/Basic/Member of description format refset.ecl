/* The memberOf function evaluates to the set of concepts that are referenced 
 * by the given reference set (i.e. the set of referencedComponentIds). Please 
 * note that this function may be applied only to reference sets whose referenced
 * components are concepts. The SNOMED CT Expression Constraint language does not
 * support use of the memberOf function on reference sets whose 
 * referencedComponents are not concepts (i.e. descriptions, relationships or 
 * reference sets).
 *
 * The memberOf function is represented using a 'caret' character (i.e. "^") and
 * must be immediately followed by a single concept id for a concept-based 
 * reference set. For example, the following expression constraint is satisfied
 * by the set of concepts which are members of |Description format reference set|.
 */
 
^900000000000538005|Description format reference set|