<template>
        <!-- 主内容区域 -->
    <div class="h-screen flex flex-col overflow-y-auto" ref="chatContainer">
        
        <a-tooltip placement="right">
          <!-- Tooltip 提示文字 -->
          <template #title>
            <span>返回首页</span>
          </template>
          <!-- 返回首页按钮 -->
          <div class="fixed top-4 left-4 z-10">
            <button class="w-10 h-10 rounded-full bg-white border border-gray-200 flex items-center justify-center cursor-pointer 
            shadow-xs hover:bg-gray-100 transition-all duration-200" @click="jumpHomePage">
                <SvgIcon name="back" customCss="w-5 h-5 text-gray-500"></SvgIcon>
            </button>
          </div>
        </a-tooltip>
        <!-- 聊天记录区域 -->
        <div class="flex-1 max-w-3xl mx-auto pb-24 pt-4 px-4 w-full">
          <!-- 遍历聊天记录 -->
          <template v-for="(chat, index) in chatList" :key="index">
            <!-- 用户提问消息（靠右） -->
            <div v-if="chat.role === 'user'" class="flex justify-end mb-4">
              <div class="quesiton-container">
                <p>{{ chat.content }}</p>
              </div>
            </div>

            <!-- 大模型回复消息（靠左） -->
            <div v-else class="flex mb-4">
              <!-- 头像 -->
              <div class="flex-shrink-0 mr-3">
                <div class="w-8 h-8 rounded-full flex items-center justify-center border border-gray-200">
                  <SvgIcon name="customer-service-logo" customCss="w-5 h-5"></SvgIcon>
                </div>
              </div>
              <!-- 回复的内容 -->
              <div class="p-1 mb-2 max-w-[90%]">
                <LoadingDots v-if="chat.loading" />
                <StreamMarkdownRender :content="chat.content" />
              </div>
            </div>
          </template>
        </div>

        <!-- 提问输入框 -->
        <ChatInputBox v-model="message" containerClass="sticky max-w-3xl mx-auto bg-white bottom-8 left-0 w-full"
          @sendMessage="sendMessage" placeholder="向小哈 AI 智能客服询问" :showModelDropdown="false" :showNetworkSearch="false"/>
      </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import SvgIcon from '@/components/SvgIcon.vue'
import StreamMarkdownRender from '@/components/StreamMarkdownRender.vue'
import LoadingDots from '@/components/LoadingDots.vue'
import ChatInputBox from '@/components/ChatInputBox.vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchEventSource } from '@microsoft/fetch-event-source'

console.log('首页传递过来的消息: ', history.state?.firstMessage)

const route = useRoute()
const router = useRouter()
// 输入的消息
const message = ref(history.state?.firstMessage || '')

// 聊天容器引用
const chatContainer = ref(null)

// 聊天记录，默认给一个欢迎语
const chatList = ref([{ role: 'assistant', content: '你好呀！我是 “犬小哈项目实战专栏” 的 AI 智能客服，基于 Spring AI 开发，欢迎向我咨询项目相关问题哈 😁', loading: false }])


// 对话 ID
const chatId = ref(null)

// 发送消息
const sendMessage = async () => {
  // 校验发送的消息不能为空
  if (!message.value.trim()) return

  // 将用户发送的消息添加到 chatList 聊天列表中
  const userMessage = message.value.trim()
  chatList.value.push({ role: 'user', content: userMessage })

  // 点击发送按钮后，清空输入框
  message.value = ''

  // 添加一个占位的回复消息
  chatList.value.push({ role: 'assistant', content: '', loading: true})

  try {
    // 构建请求体
    const requestBody = {
      message: userMessage,
      chatId: chatId.value,
    }

    // 响应的回答
    let responseText = ''
    // 获取最后一条消息
    const lastMessage = chatList.value[chatList.value.length - 1]

    const controller = new AbortController()
    const signal = controller.signal

    fetchEventSource('http://localhost:8080/customer-service/completion', {
      method: 'POST',
      signal: signal,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
      onmessage(msg) {
        if (msg.event === '') {
          // 收到第一条数据后设置 loading 为 false
          if (lastMessage.loading) {
              lastMessage.loading = false;
          }
          // 解析 JSON
          let parseJson = JSON.parse(msg.data)
          // 持续追加流式回答
          responseText += parseJson.v

          // 更新最后一条消息
          chatList.value[chatList.value.length - 1].content = responseText
          // 滚动到底部
          scrollToBottom()
        }
        else if (msg.event === 'close') {
          console.log('-- sse close')
          controller.abort();
        }
      },
      onerror(err) {
        throw err;    // 必须 throw 才能停止 
      }
    })
  } catch (error) {
    console.error('发送消息错误: ', error)
    // 提示用户 “请求出错”
    const lastMessage = chatList.value[chatList.value.length - 1]
    lastMessage.content = '抱歉，请求出错了，请稍后重试。'
    lastMessage.loading = false
    // 滚动到底部
    scrollToBottom()
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick() // 等待 Vue.js 完成 DOM 更新
  if (chatContainer.value) { // 若容器存在
    // 将容器的滚动条位置设置到最底部
    const container = chatContainer.value;
    container.scrollTop = container.scrollHeight;
  }
}
// 返回首页
const jumpHomePage = () => {
  router.push({ name: 'Index' })
}
</script>

<style scoped>
.quesiton-container {
  font-size: 16px;
  line-height: 28px;
  color: #262626;
  padding: calc((44px - 28px) / 2) 20px;
  box-sizing: border-box;
  white-space: pre-wrap;
  word-break: break-word;
  background-color: #eff6ff;
  border-radius: 14px;
  max-width: calc(100% - 48px);
  position: relative;
}

/* 聊天内容区域样式 */
.overflow-y-auto {
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
  /* 自定义滚动条颜色 */
}
</style>
