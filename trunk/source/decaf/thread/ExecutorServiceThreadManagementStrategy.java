package decaf.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceThreadManagementStrategy implements
		ThreadManagementStrategy {
	private static final int POOL_SIZE = 35;

	private ExecutorService service = null;

	public ExecutorServiceThreadManagementStrategy() {
		service = Executors.newFixedThreadPool(POOL_SIZE);
	}

	public void execute(Runnable runnable) {
		service.execute(runnable);
	}

	public void shutdown() {
		if (service != null) {
			service.shutdownNow();
		}
	}
}
