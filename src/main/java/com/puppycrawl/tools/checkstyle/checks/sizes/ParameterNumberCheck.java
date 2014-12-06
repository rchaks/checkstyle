////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.sizes;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks the number of parameters that a method or constructor has.
 * The default allowable number of parameters is 7.
 * To change the number of allowable parameters, set property max.
 * Allows to ignore number of parameters for methods with
 * &#064;{@link java.lang.Override} annotation.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="ParameterNumber"/&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to allow 10 parameters
 * and ignoring parameters for methods with &#064;{@link java.lang.Override}
 * annotation is:
 * </p>
 * <pre>
 * &lt;module name="ParameterNumber"&gt;
 *    &lt;property name="max" value="10"/&gt;
 *    &lt;property name="ignoreOverriddenMethods" value="true"/&gt;
 * &lt;/module&gt;
 * </pre>
 * Java code that will be ignored:
 * <pre>
 * <code>
 *
 *  &#064;Override
 *  public void needsLotsOfParameters(int a,
 *      int b, int c, int d, int e, int f, int g, int h) {
 *      ...
 *  }
 * </code>
 * </pre>
 * @author Oliver Burn
 * @version 1.0
 */
public class ParameterNumberCheck
    extends Check
{
    /** {@link Override Override} annotation name */
    private static final String OVERRIDE = "Override";

    /** fully-qualified {@link Override Override} annotation name */
    private static final String FQ_OVERRIDE = "java.lang." + OVERRIDE;

    /** default maximum number of allowed parameters */
    private static final int DEFAULT_MAX_PARAMETERS = 7;

    /** the maximum number of allowed parameters */
    private int mMax = DEFAULT_MAX_PARAMETERS;

    /** ignore overridden methods */
    private boolean mIgnoreOverriddenMethods;

    /**
     * Sets the maximum number of allowed parameters.
     * @param aMax the max allowed parameters
     */
    public void setMax(int aMax)
    {
        mMax = aMax;
    }

    /**
     * Ignore number of parameters for methods with
     * &#064;{@link java.lang.Override} annotation.
     * @param aIgnoreOverriddenMethods set ignore overridden methods
     */
    public void setIgnoreOverriddenMethods(boolean aIgnoreOverriddenMethods)
    {
        mIgnoreOverriddenMethods = aIgnoreOverriddenMethods;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF};
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        final DetailAST params = aAST.findFirstToken(TokenTypes.PARAMETERS);
        final int count = params.getChildCount(TokenTypes.PARAMETER_DEF);
        if (count > mMax && !ignoreNumberOfParameters(aAST)) {
            final DetailAST name = aAST.findFirstToken(TokenTypes.IDENT);
            log(name.getLineNo(), name.getColumnNo(), "maxParam", mMax, count);
        }
    }

    /** Determine whether to ignore number of parameters for the method.
     *
     * @param aAST the token to process
     * @return true if this is overridden method and number of parameters should be ignored
     *         false otherwise
     */
    private boolean ignoreNumberOfParameters(DetailAST aAST)
    {
        //if you override a method, you have no power over the number of parameters
        return mIgnoreOverriddenMethods
                && (AnnotationUtility.containsAnnotation(aAST, OVERRIDE)
                || AnnotationUtility.containsAnnotation(aAST, FQ_OVERRIDE));
    }
}
