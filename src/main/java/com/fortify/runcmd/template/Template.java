/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.runcmd.template;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {
	private static final Pattern DEFAULT_TOKEN = Pattern.compile("\\{\\{(.+?)\\}\\}");
	private final Pattern token;
	
	public Template() {
		this(DEFAULT_TOKEN); 
	}
	
	public Template(Pattern token) {
		this.token = token;
	}
	
	public final String resolveTemplateVariables(String text, Function<String, String> valueResolver) {
		final StringBuffer sb = new StringBuffer();
		final Matcher mat = token.matcher(text);
        int last = 0;

        while (mat.find()) {
            final String constant = text.substring(last, mat.start());
            final String key = mat.group(1);

            sb.append(constant);
            sb.append(valueResolver.apply(key));

            last = mat.end();
        }

        final String tail = text.substring(last);
        if (!tail.isEmpty()) {
            sb.append(tail);
        }
        return sb.toString();
	}
}
