package com.schubergphilis.confluence.html;

import com.schubergphilis.confluence.exceptions.ValidationException;

/**
 * Created by IntelliJ IDEA.
 * User: rgerrits
 * Date: 6/21/11
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseHtmlRenderer
{
    abstract String Validate();
    abstract String RenderHtml();

    public final String Render() throws ValidationException {
        String validationResult = Validate();

        if(validationResult != null)
        {
            throw new ValidationException(validationResult);
        }

        return RenderHtml();
    }
}
