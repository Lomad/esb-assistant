package com.winning.monitor.supervisor.core.task;

import com.winning.monitor.supervisor.core.task.exception.TaskStoreException;

/**
 * Created by nicholasyan on 16/9/27.
 */
public interface ITaskDao {

    static final int STATUS_TODO = 1;
    static final int STATUS_DOING = 2;
    static final int STATUS_DONE = 3;
    static final int STATUS_FAIL = 4;

    void insert(Task task) throws TaskStoreException;

    void upsert(Task task) throws TaskStoreException;

    Task findByStatusConsumer(int statusDoing, String ip) throws TaskStoreException;

    int updateDoingToDone(Task doing) throws TaskStoreException;

    int updateTodoToDoing(Task todo) throws TaskStoreException;

    int updateDoingToFail(Task doing) throws TaskStoreException;
}
