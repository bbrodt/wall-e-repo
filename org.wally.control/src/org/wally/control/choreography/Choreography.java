package org.wally.control.choreography;

import javax.swing.SwingWorker;

public class Choreography extends ScriptRunner {

	private ScriptWorker worker;

	public Choreography() {
		this("");
	}

	public Choreography(String script) {
		super(script);
	}

	public void schedule() {
		worker = new ScriptWorker(this);
		worker.execute();
	}

	public void stop() {
		super.stop();
		worker.cancel(true);
	}

	public class ScriptWorker extends SwingWorker<Integer, Void> {
		Choreography choreography;

		public ScriptWorker(Choreography choreography) {
			this.choreography = choreography;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		protected void done() {
			notifyListeners(new ScriptEvent(ScriptEventType.STOP, choreography));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Integer doInBackground() throws Exception {
			choreography.run();
			return new Integer(0);
		}
	}
}
