package com.winning.monitor.agent.logging.transaction;

public interface ForkedTransaction extends Transaction {
	public void fork();

	public String getForkedMessageId();
}
