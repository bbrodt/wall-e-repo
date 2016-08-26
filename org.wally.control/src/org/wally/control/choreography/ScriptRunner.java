package org.wally.control.choreography;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.wally.control.WallyController;
import org.wally.control.choreography.ScriptContextFactory.WallyScriptContext;
import org.wally.control.choreography.bindings.ScriptObject;
import org.wally.control.util.FileUtils;

public class ScriptRunner {
	
	protected static ContextFactory cf = new ScriptContextFactory();

	protected String script = "";
	protected String scriptName = "unnamed";
	protected WallyScriptContext scriptContext;
	protected Scriptable scope;
//	protected ScriptEngine engine;
//	protected Bindings bindings;
	protected Hashtable<String, Object> bindings;
	protected List<ScriptStateListener> listeners = new ArrayList<ScriptStateListener>();
	protected Object result = new Object();
	
	public ScriptRunner(String script) {
		setScript(script);
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	
	public String getScriptName() {
		return scriptName;
	}
	
	public void run(String fileName) {
		loadScript(fileName);
		run();
	}

	public void loadScript(String fileName) {
		scriptName = fileName;
		String fullPathName = FileUtils.getScriptDirectory() + "/" + fileName;
		script = FileUtils.load(new File(fullPathName));
	}

	protected WallyScriptContext createContext() {
		if (scriptContext==null) {
			scriptContext = (WallyScriptContext) Context.enter();
			scope = new Global(scriptContext);
//			scope = scriptContext.initStandardObjects();

			addBinding("out", WallyController.getMainWindow().getConsoleObject());
			addBinding("script", new ScriptObject() {
				public void stop() {
					ScriptRunner.this.stop();
				}
				
				public void sleep(int milliseconds) {
					ScriptRunner.this.sleep(milliseconds);
				}
				
				public void run(String fileName) {
					ScriptRunner.this.run(fileName);
				}
				
				public String getScriptName() {
					return ScriptRunner.this.getScriptName();
				}
			});
			createBindings();
		}
		return scriptContext;
	}
	
	public void addBinding(String name, Object object) {
		if (bindings==null) {
			bindings = new Hashtable<String, Object>();
		}
		bindings.put(name, object);
	}
	
	protected void createBindings() {
		for (Entry<String, Object> b : bindings.entrySet()) {
			Object jsObject = Context.javaToJS(b.getValue(), scope);
			ScriptableObject.putProperty(scope, b.getKey(), jsObject);
		}
	}

	public void addStateListener(ScriptStateListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeStateListener(ScriptStateListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}
	
	protected void notifyListeners(ScriptEvent event)
	{
		for (ScriptStateListener l : listeners) {
			l.scriptStateChanged(event);
		}
	}

	public void run() {
		createContext();
		try {
			notifyListeners(new ScriptEvent(ScriptEventType.RUN, this));
			result = scriptContext.evaluateString(scope, script, scriptName, 1, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			result = ex;
		}
		finally {
			Context.exit();
		}
		if (result instanceof Throwable) {
			notifyListeners(new ScriptEvent(this,(Throwable)result));
		}
		else
			notifyListeners(new ScriptEvent(this,"script finished"));
	}

	public void stop() {
		if (scriptContext!=null)
			scriptContext.stop();
	}
	
	public Object getResult() {
		return result;
	}
	
	public void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		}
		catch (Exception e) {
		}
	}
}