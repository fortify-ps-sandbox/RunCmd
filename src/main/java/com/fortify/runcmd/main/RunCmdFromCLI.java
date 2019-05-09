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
package com.fortify.runcmd.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.fortify.runcmd.jarutil.JarClassLoader;
import com.fortify.runcmd.spi.IValueProvider;
import com.fortify.runcmd.template.Template;

public class RunCmdFromCLI {
	private static final Map<String, IValueProvider> PROVIDERS = getProviders();
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IOException {
		if ( args.length < 1 ) {
			printUsage();
		} else {
			List<String> argsList = new LinkedList<>(Arrays.asList(args));
			String operation = argsList.remove(0);
			switch (operation.toLowerCase()) {
				case "runjar": runJar(argsList);
				case "runcmd": runExe(argsList);
				default: printUsage(); break;
			}
		}
	}

	private static void runExe(List<String> argsList) {
		String cmd = argsList.remove(0);
		String[] processedArgs = getProcessedArgs(argsList);
		// TODO Run cmd with the given processedArgs, connecting stdin and stdout to our own stdin/stdout
	}

	private static void runJar(List<String> argsList) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		String jar = argsList.remove(0);
		String[] processedArgs = getProcessedArgs(argsList);
	    JarClassLoader cl = new JarClassLoader(new File(jar).toURI().toURL());
	    cl.invokeClass(cl.getMainClassName(), processedArgs);
	    cl.close();
	}
	
	private static String[] getProcessedArgs(List<String> argsList) {
		String[] result = new String[argsList.size()];
		for ( int i = 0 ; i < argsList.size() ; i++ ) {
			result[i] = new Template().resolveTemplateVariables(argsList.get(i), RunCmdFromCLI::getArgValue);
		}
		return result;
	}

	private static String getArgValue(String key) {
		String[] elts = key.split(":", 2);
		return PROVIDERS.get(elts[0]).getValue(elts[1]);
	}

	private static final Map<String, IValueProvider> getProviders() {
		Map<String, IValueProvider> result = new HashMap<>();
		ServiceLoader.load(IValueProvider.class).forEach(p->result.put(p.getName(), p));
		return result;
	}
	
	private static final void printUsage() {
		// TODO
	}

}
