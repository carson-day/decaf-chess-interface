package decaf.thread;

public class NewThreadManagementStrategy implements ThreadManagementStrategy {
	public void execute(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	public void shutdown()
	{}
}
