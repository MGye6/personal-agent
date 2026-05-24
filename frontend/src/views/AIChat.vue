<script setup>import { ref, nextTick, watch } from 'vue';
import { chatAPI } from '@/api';
const messages = ref([
 { role: 'assistant', content: '你好！我是你的求职助手。我可以帮助你管理公司信息、投递记录、面试安排等。你可以用自然语言告诉我你想做什么，比如："帮我添加一个字节跳动的投递记录"或"查看我所有的面试记录"。' }
]);
const input = ref('');
const loading = ref(false);
const messagesContainer = ref(null);
const scrollToBottom = () => {
 nextTick(() => {
 if (messagesContainer.value) {
 messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
 }
 });
};
watch(messages, scrollToBottom, { deep: true });
const handleSend = async () => {
 if (!input.value.trim())
 return;
 const userMessage = { role: 'user', content: input.value };
 messages.value.push(userMessage);
 const currentInput = input.value;
 input.value = '';
 loading.value = true;
 try {
 const res = await chatAPI.chat(currentInput);
 if (res.code === 200) {
 messages.value.push({ role: 'assistant', content: res.data.content || '处理完成' });
 }
 else {
 messages.value.push({ role: 'assistant', content: res.message || '抱歉，我处理失败了' });
 }
 }
 catch (error) {
 messages.value.push({ role: 'assistant', content: '抱歉，发生了错误，请稍后重试' });
 }
 finally {
 loading.value = false;
 }
};
const handleKeyUp = (e) => {
 if (e.key === 'Enter' && !e.shiftKey) {
 e.preventDefault();
 handleSend();
 }
};
</script>

<template>
  <div style="height: calc(100vh - 120px); display: flex; flex-direction: column; padding: 20px;">
    <h2>AI 助手</h2>
    <div style="flex: 1; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); display: flex; flex-direction: column; overflow: hidden;">
      <div ref="messagesContainer" style="flex: 1; overflow: auto; padding: 20px;">
        <div v-for="(msg, index) in messages" :key="index" :style="{ display: 'flex', justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start', marginBottom: '16px' }">
          <div :style="{ maxWidth: '70%', padding: '12px 16px', borderRadius: '8px', backgroundColor: msg.role === 'user' ? '#1890ff' : '#f0f0f0', color: msg.role === 'user' ? '#fff' : '#333', whiteSpace: 'pre-wrap', wordBreak: 'break-word' }">
            {{ msg.content }}
          </div>
        </div>
        <div v-if="loading" style="display: flex; justify-content: flex-start; margin-bottom: 16px;">
          <div style="padding: '12px 16px'; borderRadius: '8px'; backgroundColor: '#f0f0f0';">
            <span>思考中...</span>
          </div>
        </div>
      </div>
      <div style="padding: 16px; border-top: '1px solid #eee'; display: flex; gap: 12px;">
        <textarea
          v-model="input"
          @keyup="handleKeyUp"
          placeholder="输入消息，Enter发送"
          style="flex: 1; padding: '10px'; border: '1px solid #dcdfe6'; border-radius: '4px'; resize: 'none'; font-size: '14px'; font-family: 'inherit';"
          rows="1"
        />
        <button
          @click="handleSend"
          :disabled="loading"
          style="padding: '10px 20px'; background: loading ? '#a0cfff' : '#1890ff'; color: '#fff'; border: 'none'; border-radius: '4px'; cursor: loading ? 'not-allowed' : 'pointer';"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>
