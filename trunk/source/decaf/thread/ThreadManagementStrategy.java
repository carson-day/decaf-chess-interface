package decaf.thread;

public interface ThreadManagementStrategy {
	public void execute(Runnable runnable);
	public void shutdown();
}
