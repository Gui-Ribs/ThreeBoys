package com.threeboys.presentation.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.concurrent.Task;

// Para motivos de performance, rodar tasks de banco. Em java não tem async/await diferente do c#

public final class Tasks {

	private Tasks() {
		super();
	}

	// Responsável por realizar tarefas em background

	public static <T> void run(Supplier<T> work, Consumer<T> success, Consumer<Throwable> error) {
		Task<T> task = new Task<>() {
			@Override
			protected T call() {
				return work.get();
			}
		};
		task.setOnSucceeded(e -> success.accept(task.getValue()));
		task.setOnFailed(e -> error.accept(task.getException()));
		start(task);
	}

	// Rodar em background tasks do tipo void, sem retorno

	public static void runVoid(Runnable work, Runnable success, Consumer<Throwable> error) {
		run(() -> {
			work.run();
			return null;
		}, ign -> success.run(), error);
	}

	private static void start(Task<?> task) {
		Thread thread = new Thread(task, "tasks");
		thread.setDaemon(true);
		thread.start();
	}

}
