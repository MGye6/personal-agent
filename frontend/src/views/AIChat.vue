<script setup>
import { ref, nextTick, watch, onUnmounted } from 'vue'
import { ElButton, ElInput, ElSwitch } from 'element-plus'
import { chatAPI } from '@/api'
import { useAuthStore } from '@/stores/auth'

const messages = ref([
  { role: 'assistant', content: '你好！我是你的求职助手。我可以帮助你管理公司信息、投递记录、面试安排等。你可以用自然语言告诉我你想做什么。' }
])
const input = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const useStream = ref(true)
const abortController = ref(null)
const authStore = useAuthStore()

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

watch(messages, scrollToBottom, { deep: true })

const handleSendStream = async () => {
  if (!input.value.trim()) return
  
  const userMessage = { role: 'user', content: input.value }
  messages.value.push(userMessage)
  const currentInput = input.value
  input.value = ''
  
  const assistantMessage = { role: 'assistant', content: '' }
  const messageIndex = messages.value.push(assistantMessage) - 1

  if (abortController.value) {
    abortController.value.abort()
  }
  abortController.value = new AbortController()

  try {
    const token = authStore.token
    console.log('[AIChat] Token from store:', token ? `${token.substring(0, 20)}...` : 'EMPTY')
    console.log('[AIChat] localStorage token:', localStorage.getItem('token') ? 'EXISTS' : 'NOT FOUND')
    
    const url = chatAPI.chatStream(currentInput)
    console.log('[AIChat] Request URL:', url)
    
    const response = await fetch(url, {
      method: 'GET',
      signal: abortController.value.signal,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive'
      }
    })
    
    console.log('[AIChat] Response status:', response.status)

    if (!response.ok) {
      console.error('[AIChat] HTTP error:', response.status, response.statusText)
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    if (!response.body) {
      throw new Error('No response body')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    try {
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) {
          // 如果缓冲区还有剩余内容，尝试解析
          if (buffer.trim()) {
            messages.value[messageIndex].content += parseSSEChunk(buffer)
          }
          break
        }

        buffer += decoder.decode(value, { stream: true })
        
        // 按换行分割处理
        while (buffer.includes('\n')) {
          const index = buffer.indexOf('\n')
          const line = buffer.substring(0, index)
          buffer = buffer.substring(index + 1)
          
          // 解析 SSE 格式：data: xxx
          if (line.startsWith('data: ')) {
            const content = line.substring(6)
            messages.value[messageIndex].content += content
          } else if (line.startsWith('data:')) {
            const content = line.substring(5)
            messages.value[messageIndex].content += content
          }
        }
      }
    } finally {
      // 确保 reader 被释放
      reader.releaseLock()
      console.log('[AIChat] Stream reader released')
    }
  } catch (error) {
    console.error('[AIChat] Stream error:', error)
    if (error.name !== 'AbortError') {
      if (!messages.value[messageIndex].content) {
        messages.value[messageIndex].content = '流式连接失败，正在尝试普通模式...'
        handleSendNormal(currentInput, messageIndex)
      } else {
        messages.value[messageIndex].content += '\n\n连接中断'
      }
    } else {
      console.log('[AIChat] Stream aborted by user')
    }
  }
}

const parseSSEChunk = (chunk) => {
  // 尝试多种解析方式
  const lines = chunk.split('\n')
  let result = ''
  
  for (const line of lines) {
    if (line.startsWith('data: ')) {
      result += line.substring(6)
    } else if (line.startsWith('data:')) {
      result += line.substring(5)
    } else if (!line.startsWith('event:') && !line.startsWith('id:') && !line.startsWith('retry:')) {
      // 如果不是标准SSE字段，直接作为内容
      result += line
    }
  }
  
  return result
}

const handleSendNormal = async (message, messageIndex = null) => {
  if (!message.trim()) return
  
  if (messageIndex === null) {
    const userMessage = { role: 'user', content: message }
    messages.value.push(userMessage)
    input.value = ''
    messageIndex = messages.value.push({ role: 'assistant', content: '' }) - 1
  }

  try {
    const res = await chatAPI.chat(message)
    if (res.code === 200) {
      messages.value[messageIndex].content = res.data.reply || '处理完成'
    } else {
      messages.value[messageIndex].content = res.message || '抱歉，我处理失败了'
    }
  } catch (error) {
    console.error('Normal chat error:', error)
    messages.value[messageIndex].content = '抱歉，发生了错误，请稍后重试'
  }
}

const handleSend = () => {
  if (useStream.value) {
    handleSendStream()
  } else {
    handleSendNormal(input.value)
  }
}

const handleKeyUp = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

onUnmounted(() => {
  if (abortController.value) {
    abortController.value.abort()
  }
})
</script>

<template>
  <div style="height: calc(100vh - 120px); display: flex; flex-direction: column; padding: 20px;">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
      <h2 style="margin-top: 0;">AI 助手</h2>
      <div style="display: flex; align-items: center; gap: 8px;">
        <span style="font-size: 14px; color: #606266;">流式响应</span>
        <ElSwitch v-model="useStream" />
      </div>
    </div>
    
    <div style="flex: 1; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); display: flex; flex-direction: column; overflow: hidden;">
      <div ref="messagesContainer" style="flex: 1; overflow: auto; padding: 20px;">
        <div v-for="(msg, index) in messages" :key="index" :style="{ display: 'flex', justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start', marginBottom: '16px' }">
          <div :style="{ 
            maxWidth: '70%', 
            padding: '14px 18px', 
            borderRadius: msg.role === 'user' ? '16px 16px 4px 16px' : '16px 16px 16px 4px', 
            backgroundColor: msg.role === 'user' ? '#409eff' : '#f5f7fa', 
            color: msg.role === 'user' ? '#fff' : '#303133', 
            whiteSpace: 'pre-wrap', 
            wordBreak: 'break-word',
            boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
          }">
            {{ msg.content }}
          </div>
        </div>
      </div>
      <div style="padding: 16px; border-top: 1px solid #ebeef5; display: flex; gap: 12px;">
        <ElInput
          v-model="input"
          type="textarea"
          @keyup="handleKeyUp"
          placeholder="输入消息，Enter发送"
          style="flex: 1;"
          :rows="1"
        />
        <ElButton
          type="primary"
          @click="handleSend"
          :loading="loading"
        >
          发送
        </ElButton>
      </div>
    </div>
  </div>
</template>
