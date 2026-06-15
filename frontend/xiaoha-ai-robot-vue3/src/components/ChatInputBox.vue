<template>
    <div class="bg-gray-100 rounded-3xl px-4 py-3 mx-4 border border-gray-200 flex flex-col">
        <textarea placeholder="给小哈 AI 机器人发送消息"
            class="bg-transparent border-none outline-none w-full text-sm resize-none min-h-[24px]" rows="2"
            v-model="userMessage"
            ref="textareaRef"></textarea>

        <!-- 下方容器 -->
        <div class="flex mt-3">
		    <div class="flex gap-2 relative">
 			   <!-- 大模型下拉框 -->
                <div class="border border-gray-300 px-2 py-1 rounded-3xl flex items-center justify-center hover:bg-gray-200 cursor-pointer"
                @click="toggleModelDropdown">
                    <SvgIcon name="deepseek-logo" customCss="w-5 h-5 mr-1.5" />
                    <span class="text-gray-800 text-xs">{{ currSelectedModel.name }}</span>
                                        <SvgIcon name="down-arrow" customCss="w-5 h-5 ml-1 text-gray-800 transform transition-transform duration-300"
                    :class="isModelDropdownOpen ? 'rotate-180' : ''" />
                </div>

                <!-- 下拉框菜单 -->
                <div v-if="isModelDropdownOpen" class="absolute top-8 left-0 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-10 overflow-hidden">
                    <div v-for="model in models" :key="model.id" 
                    class="px-3 py-2 hover:bg-gray-100 cursor-pointer flex items-center justify-between"
                    @click="selectModel(model)">
                        <div class="flex items-center">
                            <SvgIcon :name="model.icon" customCss="w-5 h-5 mr-2" />
                            <div class="flex flex-col text-xs">
                                <div class="text-gray-800">{{ model.name }}</div>
                                <div class="text-gray-500">{{ model.description }}</div>
                            </div>
                        </div>
                        <!-- 右侧对号 -->
                        <SvgIcon v-if="model.selected" name="check" customCss="w-3 h-3 text-gray-600" />
                    </div>
                </div>
				<!-- 联网搜索 -->
                <div class="ml-3 border px-2 py-1 rounded-3xl flex items-center justify-center cursor-pointer"
                :class="isNetworkSearchSelected ? 'border-[#ceddee] bg-[#DBEAFE] hover:bg-[#C3DAF8]' : 'border-gray-300 hover:bg-gray-200'" 
                @click="toggleNetworkSearch">
                    <SvgIcon name="network" customCss="w-5 h-5 mr-1.5" :class="isNetworkSearchSelected ? 'text-[#4D6BFE]' : 'text-gray-500'" />
                    <span class="text-xs mr-1" :class="isNetworkSearchSelected ? 'text-[#4D6BFE]' : 'text-gray-800'">联网搜索</span>
                </div>
            </div>
            <div class="grow"></div>
            
            <!-- 发送按钮 -->
            <button class="flex items-center justify-center bg-[#4d6bfe] rounded-full w-8 h-8 border border-[#4d6bfe] hover:bg-[#3b5bef] transition-colors
                    disabled:opacity-50
                    disabled:cursor-not-allowed"
                    @click="handleSendMessage"
                    >
                <SvgIcon name="up-arrow" customCss="w-5 h-5 text-white"></SvgIcon>
            </button>
        </div>
        
    </div>
</template>


<script setup>
import SvgIcon from '@/components/SvgIcon.vue'
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { message } from 'ant-design-vue'       

// 下拉菜单状态
const isModelDropdownOpen = ref(false)
// 下拉框容器引用
const selectRef = ref(null)

// 接收父组件传递的属性
const props = defineProps({
  // textarea 中用户输入的用户消息
  modelValue: {
    type: String,
    required: true
  },
})

// 定义 emits
const emit = defineEmits(['update:modelValue', 'sendMessage'])

// 计算属性，用于 v-model 的双向绑定
const userMessage = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);
  }
})
// 下拉菜单显示/隐藏
const toggleModelDropdown = () => {
  isModelDropdownOpen.value = !isModelDropdownOpen.value
}
// 模型列表
const models = ref([
  { id: 1, name: 'deepseek-v3', icon: 'deepseek-logo', description: "更流畅", selected: true },
  { id: 2, name: 'deepseek-r1', icon: 'deepseek-logo', description: "深度思考", selected: false },
]);
// 当前选择的模型，默认为第一个 deepseek-v3
const currSelectedModel = ref(models.value[0])
// 点击外部区域关闭下拉菜单
const handleClickOutside = (event) => {
  if (selectRef.value && !selectRef.value.contains(event.target)) {
    isModelDropdownOpen.value = false
  }
}

// 挂载时添加事件监听器
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 卸载时移除事件监听器
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 选择模型
const selectModel = (model) => {
  // 将所有模型的 selected 置为 false
  models.value.forEach(m => {
    m.selected = false;
  });
  
  // 将选中模型的 selected 置为 true
  model.selected = true;
  
  // 更新当前选中的模型
  currSelectedModel.value = model;
  
  // 关闭下拉菜单
  isModelDropdownOpen.value = false;
}

// 是否启用联网搜索
const isNetworkSearchSelected = ref(false)

// 切换联网搜索选中状态
const toggleNetworkSearch = () => {
    isNetworkSearchSelected.value = !isNetworkSearchSelected.value;
}

// 处理发送消息
const handleSendMessage = () => {
  // 若消息为空
  if (!userMessage.value.trim()) {
    message.warning('消息不能为空');
    return
  }

  emit('sendMessage');
  // 清空输入框
  userMessage.value = '';
}
</script>
