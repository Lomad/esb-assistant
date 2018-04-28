package com.winning.monitor.supervisor.core.task.mongodb;

import com.mongodb.WriteResult;
import com.winning.monitor.supervisor.core.task.ITaskDao;
import com.winning.monitor.supervisor.core.task.Task;
import com.winning.monitor.supervisor.core.task.exception.TaskStoreException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/27.
 */
public class MongoTaskDao implements ITaskDao {

    private static final String COLLECTION_NAME = "ReportTasks";
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(Task task) throws TaskStoreException {
        this.mongoTemplate.insert(task, COLLECTION_NAME);
    }

    @Override
    public void upsert(Task task) throws TaskStoreException {

        Query query = new Query();
        query.addCriteria(new Criteria("reportDomain").is(task.getReportDomain()));
        query.addCriteria(new Criteria("reportName").is(task.getReportName()));
        query.addCriteria(new Criteria("reportPeriod").is(task.getReportPeriod()));
        query.addCriteria(new Criteria("taskType").is(task.getTaskType()));

        List<Task> existsTasks = this.mongoTemplate.find(query, Task.class);

        if (existsTasks == null || existsTasks.size() == 0) {
            this.insert(task);
        } else {
            this.mongoTemplate.remove(query, COLLECTION_NAME);
            this.insert(task);
        }
    }

    @Override
    public Task findByStatusConsumer(int statusDoing, String consumer) throws TaskStoreException {
        Query query = new Query();
        if (StringUtils.hasText(consumer)) {
            query.addCriteria(new Criteria("consumer").is(consumer));
        }
        query.addCriteria(new Criteria("status").is(statusDoing));
        query.with(new Sort(Sort.Direction.ASC, "creationDate"));

        List<Task> list =
                this.mongoTemplate.find(query, Task.class, COLLECTION_NAME);

        if (list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public int updateDoingToDone(Task doing) throws TaskStoreException {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(doing.getId()));
        query.addCriteria(new Criteria("status").is(STATUS_DOING));

        Update update = new Update();
        update.set("status", STATUS_DONE);

        WriteResult result = this.mongoTemplate.updateMulti(query, update, COLLECTION_NAME);
        return result.getN();
    }

    @Override
    public int updateTodoToDoing(Task todo) throws TaskStoreException {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(todo.getId()));
        query.addCriteria(new Criteria("status").is(STATUS_TODO));

        Update update = new Update();
        update.set("status", STATUS_DOING);

        WriteResult result = this.mongoTemplate.updateMulti(query, update, COLLECTION_NAME);
        return result.getN();
    }

    @Override
    public int updateDoingToFail(Task doing) throws TaskStoreException {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(doing.getId()));
        query.addCriteria(new Criteria("status").is(STATUS_DOING));

        Update update = new Update();
        update.set("status", STATUS_FAIL);

        WriteResult result = this.mongoTemplate.updateMulti(query, update, COLLECTION_NAME);
        return result.getN();
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
