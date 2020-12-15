package com.metaShare.modules.bpm.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metaShare.modules.bpm.graph.ActivitiHistoryGraphBuilder;
import com.metaShare.modules.bpm.graph.Graph;


public class FindHistoryGraphCmd implements Command<Graph> {
    private static Logger logger = LoggerFactory
            .getLogger(FindHistoryGraphCmd.class);
    private String processInstanceId;

    public FindHistoryGraphCmd(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
	public Graph execute(CommandContext commandContext) {
        return new ActivitiHistoryGraphBuilder(processInstanceId).build();
    }
}
