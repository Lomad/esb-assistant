package com.winning.monitor.agent.logging.transaction;

import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageManager;
import com.winning.monitor.agent.logging.message.internal.AbstractLogMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class DefaultTransaction extends AbstractLogMessage implements Transaction {

    private long durationInMicro = -1; // must be less than 0

    private List<LogMessage> children;

    private MessageManager manager;

    private boolean standalone;

    private long durationStart;


    public DefaultTransaction(String type, String name, MessageManager manager) {
        super(type, name);

        this.manager = manager;
        this.standalone = true;
        this.durationStart = System.nanoTime();
    }

    @Override
    public DefaultTransaction addChild(LogMessage message) {
        if (this.children == null) {
            this.children = new ArrayList<LogMessage>();
        }

        if (message != null) {
            this.children.add(message);
        } else {
            //Cat.logError(new Exception("null child message"));
        }
        return this;
    }

    @Override
    public String getMessageType() {
        return "DefaultTransaction";
    }

    @Override
    public void complete() {
        try {
            if (isCompleted()) {
                // complete() was called more than once
//                DefaultEvent event = new DefaultEvent("cat", "BadInstrument");
//
//                event.setStatus("TransactionAlreadyCompleted");
//                event.complete();
//                addChild(event);
            } else {
                this.durationInMicro = (System.nanoTime() - this.durationStart) / 1000L;

                setCompleted(true);

                if (this.manager != null) {
                    this.manager.end(this);
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public List<LogMessage> getChildren() {
        if (this.children == null) {
            return Collections.emptyList();
        }

        return this.children;
    }

    @Override
    public long getDurationInMicros() {
        if (this.durationInMicro >= 0) {
            return this.durationInMicro;
        } else { // if it's not completed explicitly
            long duration = 0;
            int len = this.children == null ? 0 : this.children.size();

            if (len > 0) {
                LogMessage lastChild = this.children.get(len - 1);

                if (lastChild instanceof Transaction) {
                    DefaultTransaction trx = (DefaultTransaction) lastChild;

                    duration = (trx.getTimestamp() - getTimestamp()) * 1000L;
                } else {
                    duration = (lastChild.getTimestamp() - getTimestamp()) * 1000L;
                }
            }

            return duration;
        }
    }

    public void setDurationInMicros(long duration) {
        this.durationInMicro = duration;
    }

    @Override
    public long getDurationInMillis() {
        return getDurationInMicros() / 1000L;
    }

    public void setDurationInMillis(long duration) {
        this.durationInMicro = duration * 1000L;
    }

    protected MessageManager getManager() {
        return this.manager;
    }

    @Override
    public boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }

    @Override
    public boolean isStandalone() {
        return this.standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    @Override
    public void success() {
        this.setStatus(SUCCESS);
        this.complete();
    }

    public void setDurationStart(long durationStart) {
        this.durationStart = durationStart;
    }

    public void putDatas(LinkedHashMap<String, Object> datas) {
        if (datas != null) {
            super.data = datas;
        } else {
            datas = new LinkedHashMap<>();
        }
    }

    @Override
    public void setType(String type) {
        super.setType(type);
        if(null!=this.children&&this.children.size()>0){
            for(int i=0,size=this.children.size();i<size;i++){
                LogMessage child = this.children.get(i);
                child.setType(type);
            }
        }
    }
}
