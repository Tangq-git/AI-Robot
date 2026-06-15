<template>
    <!-- 左边栏 -->
    <div 
        :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
        class="w-64 bg-[#f9fbff] border-r border-gray-200 fixed left-0 top-0 h-full transition-transform duration-300 ease-in-out z-10 overflow-y-auto">
        
        <!-- 侧边栏内容区域 -->
        <div class="p-0 h-full flex flex-col">
            <!-- Logo 与应用名称 -->
            <div class="flex items-center justify-center p-4 cursor-pointer" @click="jumpToIndexPage">
              <SvgIcon name="ai-robot-logo" customCss="w-8 h-8 text-gray-700 mr-3" />
              <span class="text-2xl font-bold font-sans tracking-wide text-gray-800">小哈AI机器人</span>
            </div>

            <!-- 开启新对话按钮 -->
            <button @click="jumpToIndexPage"
              class="mx-auto mb-[34px] my-2 px-6 py-2 text-white rounded-xl transition-colors new-chat-btn w-fit cursor-pointer">
              <SvgIcon name="new-chat" customCss="w-6 h-6 mr-1.5 inline text-[#4d6bfe]" />
              开启新对话
            </button>

            <!-- 历史对话区域 -->
            <div class="my-4 px-2 overflow-y-auto overflow-x-hidden flex-1">
              <div class="space-y-1">
			   <div class="text-xs px-3 py-1 text-gray-500">历史对话</div>
                <div v-for="(historyChat, index) in historyChats" :key="index" class="relative px-3 py-1 rounded-xl hover:bg-[rgb(239,246,255)] cursor-pointer transition-colors flex items-center justify-between"
                @mouseenter="showButton = historyChat.uuid" @mouseleave="showButton = null">  
                    <!-- 正确tooltip放在循环内部，才能拿到historyChat -->
                    <a-tooltip placement="top">
                        <template #title>
                        {{ historyChat.summary }}
                        </template>
                        <p class="text-[14px] text-gray-800 overflow-hidden whitespace-nowrap">{{ historyChat.summary }}</p>
                    </a-tooltip>
                    <!-- 下拉菜单 -->
                    <a-dropdown>
                         <template #overlay>
                            <a-menu>
                              <a-menu-item key="rename">
                                <EditOutlined />
                                重命名
                              </a-menu-item>
                              <a-menu-item key="delete" danger>
                                <DeleteOutlined />
                                删除
                              </a-menu-item>
                          </a-menu>
                        </template>
                        <!-- 右边菜单按钮 -->
                        <button
                            class="z-10 rounded-lg outline-none justify-center items-center bg-white
                            w-6 h-6 flex absolute right-2 top-1/2 transform -translate-y-1/2 transition-all duration-300 hover:bg-gray-50"
                            :style="{ opacity: showButton === historyChat.uuid ? 1 : 0 }">
                            <EllipsisOutlined class="w-4 h-4 text-gray-500" />
                        </button>
                </a-dropdown>
                </div>
              </div>
            </div>
        </div>
    </div>

    <!-- 侧边栏切换按钮 -->
    <a-tooltip placement="bottom">
        <!-- Tooltip 提示文字 -->
        <template #title>
          <span>{{ sidebarOpen ? '收缩边栏' : '打开边栏'}}</span>
        </template>   
        <button 
            :class="sidebarOpen ? 'left-64' : 'left-0'"
            @click="toggleSidebar"
            class="fixed top-4 z-20 bg-white border border-gray-200 rounded-r-lg p-2">
            <!-- 图标 -->
            <SvgIcon :name="sidebarOpen ? 'sidebar-open' : 'sidebar-close'" :customCss="sidebarOpen ? 'w-6 h-6 text-gray-400' : 'w-7 h-7 text-gray-400'" />
        </button>
    </a-tooltip>


    
</template>

<script setup>
import { EditOutlined, EllipsisOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import SvgIcon from '@/components/SvgIcon.vue'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
// 定义 props, 对外部暴露配置项
const props = defineProps({
  sidebarOpen: { type: Boolean, required: true }, // 左边栏是否展开
})

// 定义 emits
const emit = defineEmits(['toggle-sidebar'])

// 切换侧边栏显示/隐藏
const toggleSidebar = () => {
  emit('toggle-sidebar')
}

// 历史对话
const historyChats = ref([
  {"uuid": "9640a419-4b0c-45dd-b16d-1980df2424c4", "summary": "很长很长很长很长很长很长很长很长很长的新对话1"},
  {"uuid": "7c2af48e-dce2-4822-aef6-c7a3c1949805", "summary": "新对话2"},
  {"uuid": "152496bc-2776-422d-ac96-5dbfc903bc1d", "summary": "新对话3"},
])      
// 当前显示右侧栏按钮的聊天 ID
const showButton = ref(null)

// 跳转到首页
const jumpToIndexPage = () => {
  router.push('/')
}
</script>

<style scoped>
.overflow-y-auto {
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent; /* 自定义滚动条颜色 */
}

.new-chat-btn {
  background-color: rgb(219 234 254);
  color: #4d6bfe;
}

.new-chat-btn:hover {
  background-color: #c6dcf8;
}
</style>
