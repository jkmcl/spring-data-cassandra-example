package jkml.scheduling;

class MyTask implements Runnable {

	boolean executed = false;

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	@Override
	public void run() {
		executed = true;
	}

}
