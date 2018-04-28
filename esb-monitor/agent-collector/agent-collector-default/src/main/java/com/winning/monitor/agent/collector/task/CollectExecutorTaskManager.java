package com.winning.monitor.agent.collector.task;

import com.winning.monitor.agent.collector.CollectExecutorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectExecutorTaskManager implements Runnable {

    private final static long DURATION = 1000;

    private final CollectExecutorContext collectExecutorContext;

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private List<CollectExecutorTask> executorTasks = new ArrayList<>();
    private Thread currentThread;
    private volatile boolean active;

    public CollectExecutorTaskManager(CollectExecutorContext collectExecutorContext) {
        this.collectExecutorContext = collectExecutorContext;
    }

    public void initialize() {
        this.executorTasks = collectExecutorContext.getCollectExecutorTasks();
    }

    public void start() {
        if (currentThread != null) {
            return;
        }

        this.active = true;
        this.currentThread = new Thread(this);
        this.currentThread.setDaemon(true);
        this.currentThread.start();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (active) {
            boolean hasRemoveTask = false;
            long current = System.currentTimeMillis();
            for (CollectExecutorTask task : executorTasks) {
                if (task.isRunning()) {
                    continue;
                }
                if (task.getDataCollectExecutor().getNextCollectTime() == Long.MAX_VALUE) {
                    hasRemoveTask = true;
                    continue;
                }
                if (current >= task.getDataCollectExecutor().getNextCollectTime()) {
                    cachedThreadPool.execute(task);
                }
            }

            //删除不会被再次执行的任务
            if (hasRemoveTask) {
                this.removeTasks();
            }

            long duration = System.currentTimeMillis() - current;
            try {
                if (duration < DURATION) {
                    Thread.sleep(DURATION - duration);
                }
            } catch (InterruptedException e) {

            }
        }
    }

    public void shutdown() {
        this.active = false;
        try {
            this.currentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.currentThread = null;
    }


    private void removeTasks() {
        List<CollectExecutorTask> toRemoveTasks = new ArrayList<>();
        for (CollectExecutorTask task : executorTasks) {
            if (task.getDataCollectExecutor().getNextCollectTime() == Long.MAX_VALUE) {
                toRemoveTasks.add(task);
            }
        }
        for (CollectExecutorTask task : toRemoveTasks) {
            executorTasks.remove(task);
        }
    }
}
