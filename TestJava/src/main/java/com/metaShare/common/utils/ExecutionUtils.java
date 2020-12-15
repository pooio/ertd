package com.metaShare.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionUtils {
	private static Logger log = LoggerFactory.getLogger(ExecutionUtils.class);
	private static ExecutionUtils single = new ExecutionUtils();
	
	private ExecutionUtils() {
	}
	
	public static ExecutionUtils getInstance() {
		return single;
	}
	
	/**
	 * Execute jar from file
	 */
	public Object runJar(File jar) {
		Object ret = null;
		
		try {
			if (jar.exists() && jar.canRead()) {
				ClassLoader cl = getClass().getClassLoader();
				JarClassLoader jcl = new JarClassLoader(jar.toURI().toURL(), cl);
				String mainClass = jcl.getMainClassName();
				
				if (mainClass != null) {
					Class<?> c = jcl.loadClass(mainClass);
					ret = ClassLoaderUtils.invokeMainMethodFromClass(c, new String[] {});
				} else {
					log.error("Main class not defined at: {}", jar.getPath());
				}
			} else {
				log.warn("Unable to read jar: {}", jar.getPath());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		
		log.debug("runJar: {}", ret != null ? ret.toString() : "null");
		return ret;
	}
	
	/**
	 * Execute jar from file
	 */
	public Object runJar(File jar, String methodName) {
		Object ret = null;
		
		try {
			if (jar.exists() && jar.canRead()) {
				ClassLoader cl = getClass().getClassLoader();
				JarClassLoader jcl = new JarClassLoader(jar.toURI().toURL(), cl);
				String mainClass = jcl.getMainClassName();
				
				if (mainClass != null) {
					Class<?> c = jcl.loadClass(mainClass);
					ret = ClassLoaderUtils.invokeMethodFromClass(c, methodName);
				} else {
					log.error("Main class not defined at: {}", jar.getPath());
				}
			} else {
				log.warn("Unable to read jar: {}", jar.getPath());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		
		log.debug("runJar: {}", ret != null ? ret.toString() : "null");
		return ret;
	}
	
	/**
	 * Execute jar
	 */
	public Object runJar(byte[] jar) {
		Object ret = null;
		
		try {
			ClassLoader cl = getClass().getClassLoader();
			BinaryClassLoader jcl = new BinaryClassLoader(jar, cl);
			String mainClass = jcl.getMainClassName();
			
			if (mainClass != null) {
				Class<?> c = jcl.loadClass(mainClass);
				ret = ClassLoaderUtils.invokeMainMethodFromClass(c, new String[] {});
			} else {
				log.error("Main class not defined at jar");
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		
		log.debug("runJar: {}", ret != null ? ret.toString() : "null");
		return ret;
	}
	
	/**
	 * Execute jar
	 */
	public Object runJar(byte[] jar, String methodName) {
		Object ret = null;
		
		try {
			ClassLoader cl = getClass().getClassLoader();
			BinaryClassLoader jcl = new BinaryClassLoader(jar, cl);
			String mainClass = jcl.getMainClassName();
			
			if (mainClass != null) {
				Class<?> c = jcl.loadClass(mainClass);
				ret = ClassLoaderUtils.invokeMethodFromClass(c, methodName);
			} else {
				log.error("Main class not defined at jar");
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		
		log.debug("runJar: {}", ret != null ? ret.toString() : "null");
		return ret;
	}
	
	/**
	 * Execute command line
	 */
	public static ExecutionResult runCmd(String cmd) throws SecurityException, InterruptedException, IOException {
		log.debug("runCmd({})", cmd);
		return runCmdImpl(cmd.split(" "), TimeUnit.MINUTES.toMillis(Config.SYSTEM_EXECUTION_TIMEOUT));
	}
	
	/**
	 * Execute command line
	 */
	public static ExecutionResult runCmd(String cmd[]) throws SecurityException, InterruptedException, IOException {
		log.debug("runCmd({})", Arrays.toString(cmd));
		return runCmdImpl(cmd, TimeUnit.MINUTES.toMillis(Config.SYSTEM_EXECUTION_TIMEOUT));
	}
	
	/**
	 * Execute command line: implementation
	 */
	private static ExecutionResult runCmdImpl(final String cmd[], final long timeout) throws SecurityException,
			InterruptedException, IOException {
		log.debug("runCmdImpl({}, {})", Arrays.toString(cmd), timeout);
		ExecutionResult ret = new ExecutionResult();
		long start = System.currentTimeMillis();
		final ProcessBuilder pb = new ProcessBuilder(cmd);
		final Process process = pb.start();
		
		Timer t = new Timer("Process Execution Timeout");
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				process.destroy();
				log.warn("Process killed due to timeout.");
				log.warn("CommandLine: {}", Arrays.toString(cmd));
			}
		}, timeout);
		
		try {
			ret.setStdout(IOUtils.toString(process.getInputStream()));
			ret.setStderr(IOUtils.toString(process.getErrorStream()));
		} catch (IOException e) {
			// Ignore
		}
		
		process.waitFor();
		t.cancel();
		ret.setExitValue(process.exitValue());
		
		// Check return code
		if (ret.getExitValue() != 0) {
			log.warn("Abnormal program termination: {}", ret.getExitValue());
			log.warn("CommandLine: {}", Arrays.toString(cmd));
			log.warn("STDERR: {}", ret.getStderr());
		} else {
			log.debug("Normal program termination");
		}
		
		process.destroy();
		log.debug("Elapse time: {}", FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		return ret;
	}
}
