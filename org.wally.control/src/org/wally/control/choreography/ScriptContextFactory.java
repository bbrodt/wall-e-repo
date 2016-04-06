package org.wally.control.choreography;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

class ScriptContextFactory extends ContextFactory {

	// Custom Context to store execution time.
	public static class WallyScriptContext extends Context {
		public WallyScriptContext(ContextFactory contextFactory) {
			super(contextFactory);
		}

		long startTime;
		boolean stop = false;

		public void stop() {
			stop = true;
		}
	}

	static {
		// Initialize GlobalFactory with custom factory
		ContextFactory.initGlobal(new ScriptContextFactory());
	}

	@Override
	protected Context makeContext() {
		WallyScriptContext cx = new WallyScriptContext(this);
		// Make Rhino runtime to call observeInstructionCount
		// each 10 bytecode instructions
		cx.setInstructionObserverThreshold(10);
		return cx;
	}

	@Override
	public boolean hasFeature(Context cx, int featureIndex) {
		// Turn on maximum compatibility with MSIE scripts
		switch (featureIndex) {
		case Context.FEATURE_NON_ECMA_GET_YEAR:
			return true;

		case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
			return true;

		case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
			return true;

		case Context.FEATURE_PARENT_PROTO_PROPERTIES:
			return false;
		}
		return super.hasFeature(cx, featureIndex);
	}

	@Override
	protected void observeInstructionCount(Context cx, int instructionCount) {
		WallyScriptContext wallyScriptContext = (ScriptContextFactory.WallyScriptContext) cx;
		long currentTime = System.currentTimeMillis();
		if (wallyScriptContext.stop) {
			throw new Error("Execution stopped");
		}
	}

	@Override
	protected Object doTopCall(Callable callable, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) {
		ScriptContextFactory.WallyScriptContext mcx = (ScriptContextFactory.WallyScriptContext) cx;
		mcx.startTime = System.currentTimeMillis();

		return super.doTopCall(callable, cx, scope, thisObj, args);
	}

}