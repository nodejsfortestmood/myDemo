package com.strategy.core.agent.interceptor;
import com.strategy.core.agent.AgentResult;
import com.strategy.core.agent.WebAgent;

import java.util.Map;
/**
 */
public interface AfterInterceptor {

    AgentResult interceptor(Map<String, Object> data, WebAgent webAgent);

}
