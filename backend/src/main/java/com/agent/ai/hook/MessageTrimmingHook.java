package com.agent.ai.hook;


import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.messages.AgentCommand;
import com.alibaba.cloud.ai.graph.agent.hook.messages.MessagesModelHook;
import org.springframework.ai.chat.messages.Message;
import java.util.List;


//public class MessageTrimmingHook extends MessagesModelHook {
//    @Override
//    public String getName() {
//        return "message_trimming";
//    }
//    @Override
//    public AgentCommand beforeModel(List<Message> previousMessages, RunnableConfig config) {
//        if(previousMessages.size()>20){
//
//        }
//
//
//    }
//
//
//}
