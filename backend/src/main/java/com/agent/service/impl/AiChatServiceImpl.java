package com.agent.service.impl;

import com.agent.context.UserContext;
import com.agent.dto.CompanyDTO;
import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ChatResponse;
import com.agent.service.AiChatService;
import com.agent.service.CompanyService;
import com.agent.service.InterviewRecordService;
import com.agent.service.InterviewScheduleService;
import com.agent.service.JobApplicationService;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final CompanyService companyService;
    private final JobApplicationService jobApplicationService;
    private final InterviewRecordService interviewRecordService;
    private final InterviewScheduleService interviewScheduleService;

    /**
     * 使用 ReactAgent：已自动注入 CompanyTools、ApplicationTools、InterviewRecordTools、InterviewScheduleTools
     * 这些 @Tool 注解方法可以真正操作数据库
     */
    @Autowired(required = false)
    private ReactAgent reactAgent;

    private static final ExecutorService AI_EXECUTOR = Executors.newFixedThreadPool(4);

    private List<HandlerEntry> handlers;

    @PostConstruct
    public void init() {
        handlers = new ArrayList<>();

        handlers.add(new HandlerEntry(
            Arrays.asList("你好", "您好", "hello", "hi", "嗨", "在吗", "在不"),
            ctx -> "你好！我是你的求职助手 👋\n\n"
                + "我可以帮你管理公司信息、投递记录、面试安排等。"
                + "试试问我：「有哪些公司」「帮我添加一条投递记录」「我有哪些面试安排」等。"
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("公司", "企业", "organization", "company", "有哪些公司", "公司列表", "哪些公司"),
            ctx -> {
                try {
                    List<CompanyDTO> companies = safeGetCompanies();
                    if (companies == null || companies.isEmpty()) {
                        return "你还没有添加任何公司记录 🏢\n\n可以在『公司管理』页面添加你感兴趣的公司！";
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("📊 共 ").append(companies.size()).append(" 家公司：\n\n");
                    int count = 0;
                    for (CompanyDTO c : companies) {
                        count++;
                        sb.append(count).append(". ").append(c.getName() == null ? "(未命名)" : c.getName());
                        if (c.getIndustry() != null && !c.getIndustry().isEmpty()) {
                            sb.append("（").append(c.getIndustry()).append("）");
                        }
                        if (c.getLocation() != null && !c.getLocation().isEmpty()) {
                            sb.append(" - ").append(c.getLocation());
                        }
                        sb.append("\n");
                        if (count >= 15) {
                            int remaining = companies.size() - 15;
                            if (remaining > 0) {
                                sb.append("\n...还有 ").append(remaining).append(" 家公司\n");
                            }
                            break;
                        }
                    }
                    sb.append("\n💡 前往『公司管理』查看详细信息");
                    return sb.toString();
                } catch (Exception e) {
                    log.error("Query companies error", e);
                    return "查询公司信息时出现问题，请稍后重试";
                }
            }
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("投递", "申请", "投简历", "投递记录", "投了", "applications", "apply", "application"),
            ctx -> {
                try {
                    List<?> apps = safeGetApplications();
                    if (apps == null || apps.isEmpty()) {
                        return "你还没有投递记录 📝\n\n可以在『投递记录』页面添加你的求职申请！";
                    }
                    return "📝 你目前共有 **" + apps.size() + "** 条投递记录\n\n💡 前往『投递记录』查看详细信息";
                } catch (Exception e) {
                    log.error("Query applications error", e);
                    return "查询投递记录时出现问题，请稍后重试";
                }
            }
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("面试记录", "面试情况", "面试历史", "面经", "interview records", "interviews"),
            ctx -> {
                try {
                    List<?> records = safeGetInterviewRecords();
                    if (records == null || records.isEmpty()) {
                        return "你还没有面试记录 📋\n\n可以在『面试记录』页面记录你的面试经验";
                    }
                    return "📋 你目前共有 **" + records.size() + "** 条面试记录\n\n💡 前往『面试记录』查看详细信息";
                } catch (Exception e) {
                    log.error("Query interview records error", e);
                    return "查询面试记录时出现问题，请稍后重试";
                }
            }
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("面试安排", "面试", "安排", "日程", "面试日程", "schedule", "upcoming"),
            ctx -> {
                try {
                    List<?> schedules = safeGetInterviewSchedules();
                    if (schedules == null || schedules.isEmpty()) {
                        return "你目前没有面试安排 📅\n\n可以在『面试安排』页面管理即将到来的面试";
                    }
                    return "📅 你目前有 **" + schedules.size() + "** 个面试安排\n\n💡 前往『面试安排』查看详情";
                } catch (Exception e) {
                    log.error("Query interview schedules error", e);
                    return "查询面试安排时出现问题，请稍后重试";
                }
            }
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("统计", "汇总", "多少", "数量", "概况", "概览", "stats", "statistics", "overview"),
            ctx -> {
                try {
                    List<CompanyDTO> companies = safeGetCompanies();
                    List<?> applications = safeGetApplications();
                    List<?> records = safeGetInterviewRecords();
                    List<?> schedules = safeGetInterviewSchedules();

                    int companyCount = companies != null ? companies.size() : 0;
                    int appCount = applications != null ? applications.size() : 0;
                    int recordCount = records != null ? records.size() : 0;
                    int scheduleCount = schedules != null ? schedules.size() : 0;

                    StringBuilder sb = new StringBuilder();
                    sb.append("📊 **你的求职统计概览**\n\n");
                    sb.append("🏢 关注公司：").append(companyCount).append(" 家\n");
                    sb.append("📝 投递记录：").append(appCount).append(" 条\n");
                    sb.append("📋 面试记录：").append(recordCount).append(" 条\n");
                    sb.append("📅 面试安排：").append(scheduleCount).append(" 个\n\n");
                    sb.append("📌 前往『仪表盘』查看更详细的统计图表");
                    return sb.toString();
                } catch (Exception e) {
                    log.error("Query statistics error", e);
                    return "查询统计信息时出现问题，请稍后重试";
                }
            }
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("帮助", "help", "怎么用", "使用方法", "使用", "功能", "能干什么", "可以做什么"),
            ctx -> "📖 **使用帮助**\n\n"
                + "我是一个求职管理助手，可以帮你：\n\n"
                + "• 添加/查询 公司、投递记录、面试记录、面试安排\n\n"
                + "• 查询统计数据\n\n"
                + "• 直接说「帮我添加一条投递记录」或「有哪些公司」试试！"
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("谢谢", "感谢", "thanks", "thank", "ok", "好的", "明白了", "知道了"),
            ctx -> "😊 不客气！祝你找到理想的工作！\n\n随时回来咨询我哦～"
        ));

        handlers.add(new HandlerEntry(
            Arrays.asList("简历", "resume", "cv", "个人简历"),
            ctx -> "📄 **简历管理**\n\n你可以在左侧菜单的『简历管理』页面编辑和导出你的简历。"
        ));
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        return processNaturalLanguageQuery(request.getMessage());
    }

    @Override
    public ChatResponse processNaturalLanguageQuery(String message) {
        log.info("AI Chat query: {}", message);

        // 优先使用 ReactAgent（已注入所有 @Tool 工具，可以真正操作数据库）
        if (reactAgent != null) {
            try {
                String reply = callReactAgent(message);
                if (reply != null && !reply.trim().isEmpty()) {
                    return ChatResponse.builder()
                        .reply(reply)
                        .conversationId(UUID.randomUUID().toString())
                        .build();
                }
            } catch (Exception e) {
                log.warn("ReactAgent call failed, falling back to rule engine: {}", e.getMessage());
            }
        } else {
            log.debug("ReactAgent not available, using rule engine");
        }

        // Fallback：规则引擎
        try {
            String reply = handleMessage(message);
            return ChatResponse.builder()
                .reply(reply)
                .conversationId(UUID.randomUUID().toString())
                .build();
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            return ChatResponse.builder()
                .reply("抱歉，处理你的消息时出现了问题 😓\n请稍后重试！")
                .conversationId(UUID.randomUUID().toString())
                .build();
        }
    }

    @Override
    public void streamProcessNaturalLanguageQuery(String message, SseEmitter emitter) {
        emitter.onTimeout(() -> {
            log.warn("SSE timeout for message: {}", message);
            emitter.complete();
        });
        emitter.onCompletion(() -> log.debug("SSE completed"));

        CompletableFuture.runAsync(() -> {
            boolean success = false;

            // 优先尝试 ReactAgent 流式（已自动调用工具）
            if (reactAgent != null) {
                try {
                    String reply = callReactAgent(message);
                    if (reply != null && !reply.trim().isEmpty()) {
                        String[] parts = reply.split("(?<=[。！？!?.])|(?<=\n)");
                        for (String part : parts) {
                            if (part != null && !part.isEmpty()) {
                                try {
                                    Thread.sleep(40);
                                    emitter.send(SseEmitter.event().name("message").data(part));
                                } catch (Exception e) {
                                    log.warn("Failed to send SSE chunk: {}", e.getMessage());
                                }
                            }
                        }
                        emitter.complete();
                        success = true;
                    }
                } catch (Exception e) {
                    log.warn("ReactAgent stream failed, falling back: {}", e.getMessage());
                }
            }

            // Fallback：规则引擎
            if (!success) {
                try {
                    String reply = handleMessage(message);
                    String[] parts = reply.split("(?<=[。！？!?.])|(?<=\n)");
                    for (String part : parts) {
                        if (part != null && !part.isEmpty()) {
                            try {
                                Thread.sleep(40);
                                emitter.send(SseEmitter.event().name("message").data(part));
                            } catch (Exception e) {
                                log.warn("Failed to send SSE chunk (rule): {}", e.getMessage());
                            }
                        }
                    }
                    emitter.complete();
                } catch (Exception e) {
                    log.error("Stream processing error", e);
                    try {
                        emitter.send(SseEmitter.event().name("error").data("处理出错，请刷新重试"));
                    } catch (Exception ex) {
                        log.error("Failed to send error message", ex);
                    }
                    emitter.completeWithError(e);
                }
            }
        }, AI_EXECUTOR);
    }

    /**
     * 调用 ReactAgent。ReactAgent 已注入了所有 @Tool 工具方法
     * （CompanyTools、ApplicationTools、InterviewRecordTools、InterviewScheduleTools）
     * 这些工具会真正操作数据库
     */
    private String callReactAgent(String message) {
        if (reactAgent == null) return null;
        try {
            Long userId = UserContext.getUserId();
            String threadId = userId != null ? userId.toString() : "anonymous-" + UUID.randomUUID().toString().substring(0, 8);
            RunnableConfig config = RunnableConfig.builder().threadId(threadId).build();

            log.info("Calling ReactAgent with threadId={}", threadId);
            String reply = reactAgent.call(message, config).getText();
            log.info("ReactAgent reply length={}", reply == null ? 0 : reply.length());
            return reply;
        } catch (Exception e) {
            log.warn("ReactAgent call error: {}", e.getMessage());
            return null;
        }
    }

    // ========= 核心消息处理 =========

    private String handleMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "请输入消息，我可以帮你管理求职信息！\n试试问我：『公司』『统计』或『帮助』";
        }

        String normalized = message.trim().toLowerCase();

        for (HandlerEntry entry : handlers) {
            if (containsAnyKeyword(normalized, entry.keywords)) {
                return entry.handler.apply(null);
            }
        }

        return getDefaultReply(message.trim());
    }

    private boolean containsAnyKeyword(String text, List<String> keywords) {
        String lowerText = text.toLowerCase();
        for (String kw : keywords) {
            if (lowerText.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String getDefaultReply(String message) {
        String shortMsg = (message.length() > 20) ? message.substring(0, 20) + "..." : message;
        return "我理解你想聊『**" + shortMsg + "**』 🤔\n\n"
             + "作为你的求职助手，我目前可以帮你：\n\n"
             + "• 📊 查看统计数据 → 说『统计』\n"
             + "• 🏢 查看公司列表 → 说『公司』\n"
             + "• 📝 查看投递记录 → 说『投递记录』\n"
             + "• 📅 查看面试安排 → 说『面试安排』\n\n"
             + "或者说『帮助』查看完整功能列表 ✨";
    }

    // ========= 安全数据访问方法（带异常处理）

    private List<CompanyDTO> safeGetCompanies() {
        try {
            return companyService.getAllCompanies();
        } catch (Exception e) {
            log.error("Failed to get companies", e);
            return null;
        }
    }

    private List<?> safeGetApplications() {
        try {
            if (UserContext.getUserId() == null) {
                return new ArrayList<>();
            }
            return jobApplicationService.getAllApplications();
        } catch (Exception e) {
            log.error("Failed to get applications", e);
            return null;
        }
    }

    private List<?> safeGetInterviewRecords() {
        try {
            if (UserContext.getUserId() == null) {
                return new ArrayList<>();
            }
            return interviewRecordService.getAllInterviewRecords();
        } catch (Exception e) {
            log.error("Failed to get interview records", e);
            return null;
        }
    }

    private List<?> safeGetInterviewSchedules() {
        try {
            if (UserContext.getUserId() == null) {
                return new ArrayList<>();
            }
            return interviewScheduleService.getAllSchedules();
        } catch (Exception e) {
            log.error("Failed to get interview schedules", e);
            return null;
        }
    }

    // ========= 内部数据结构 =========

    private static class HandlerEntry {
        final List<String> keywords;
        final Function<Map<String, Object>, String> handler;

        HandlerEntry(List<String> keywords, Function<Map<String, Object>, String> handler) {
            this.keywords = keywords;
            this.handler = handler;
        }
    }
}
