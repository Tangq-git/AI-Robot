<template>
  <div class="markdown-container">
    <div v-html="renderedContent">
    </div>    
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css' // 导入 GitHub 风格的高亮样式
import markdownItHighlightJs from 'markdown-it-highlightjs'
import { message } from 'ant-design-vue'

// 定义一个 content 字段，用于父组件传入 markdown 文本
const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

// 解析后的 HTML
const renderedContent = ref('')

// 初始化 MarkdownIt
const md = new MarkdownIt({
  html: true,        // 允许解析 HTML 标签
  xhtmlOut: true,    // 输出符合 XHTML 规范的标签（如 `<br />` 而不是 `<br>`）。默认 false。
  linkify: true,     // 自动将文本中的 URL 转换为可点击的链接
  typographer: true, // 启用排版优化
  breaks: true,       // 将单个换行符 (\n) 转换为 <br>
  langPrefix: 'language-', // 代码块的语言类名前缀（默认 'language-'）。例如 ```js 会生成 <pre><code class="language-js">
})

// 使用代码高亮插件
md.use(markdownItHighlightJs, { 
  hljs,
  auto: true, // 自动检测语言
  code: true  // 高亮内联代码
})

// 保存默认的代码块渲染规则
const defaultRender = md.renderer.rules.fence || function(tokens, idx, options, env, renderer) {
  // 调用默认的渲染函数处理代码块
  return renderer.renderToken(tokens, idx, options)
}

// 重写 Markdown 渲染器的代码块渲染规则
md.renderer.rules.fence = function (tokens, idx, options, env, renderer) {
  // 获取当前索引对应的 token（代码块）
  const token = tokens[idx]
  // 处理语言信息：移除转义字符并去除首尾空格
  const info = token.info ? md.utils.unescapeAll(token.info).trim() : ''
  let langName = ''
  
  // 如果存在语言信息
  if (info) {
    // 分割信息字符串
    const langCode = info.split(/\s+/g)[0] // 取第一个部分作为语言标识，如 ```js 中的 js
    langName = langCode.toLowerCase() // 转换为小写统一格式
  }
  
// 使用默认渲染器生成原始 HTML 内容
  const originalContent = defaultRender(tokens, idx, options, env, renderer)
  
  // 拼装最终的 HTML
  let finalContent = `<div class="code-block-wrapper">
      <div class="code-header">
      `

// 如果有返回代码块语言信息，需要显示
  if (langName) {
    finalContent += `<div class="code-language-label">${langName}</div>`
  }

  // 代码块中的实际代码
  const codeContent = token.content
  // 为每个代码块分配一个唯一标识，方便知道复制的哪个代码块中的内容
  const codeId = `code-${Math.random().toString(36).substr(2, 9)}`

  // 返回渲染结果
  return finalContent += `
        <button class="copy-code-btn" onclick="copyCode('${codeId}')">  
          <svg t="1750068080826" class="copy-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1529" 
          width="15" height="15"><path d="M761.088 715.3152a38.7072 38.7072 0 0 1 0-77.4144 37.4272 37.4272 0 0 0 37.4272-37.4272V265.0112a37.4272 37.4272 0 0 0-37.4272-37.4272H425.6256a37.4272 37.4272 0 0 0-37.4272 37.4272 38.7072 38.7072 0 1 1-77.4144 0 115.0976 115.0976 0 0 1 114.8416-114.8416h335.4624a115.0976 115.0976 0 0 1 114.8416 114.8416v335.4624a115.0976 115.0976 0 0 1-114.8416 114.8416z" p-id="1530"></path><path d="M589.4656 883.0976H268.1856a121.1392 121.1392 0 0 1-121.2928-121.2928v-322.56a121.1392 121.1392 0 0 1 121.2928-121.344h321.28a121.1392 121.1392 0 0 1 121.2928 121.2928v322.56c1.28 67.1232-54.1696 121.344-121.2928 121.344zM268.1856 395.3152a43.52 43.52 0 0 0-43.8784 43.8784v322.56a43.52 43.52 0 0 0 43.8784 43.8784h321.28a43.52 43.52 0 0 0 43.8784-43.8784v-322.56a43.52 43.52 0 0 0-43.8784-43.8784z" p-id="1531"></path></svg>
          <span class="copy-text">复制</span>
        </button>
      </div>
      <div class="code-content" id="${codeId}" data-code="${encodeURIComponent(codeContent)}">
        ${originalContent}
      </div>
    </div>`
}

// 初始化复制功能
const setupCopyFunction = () => {
  // 确保全局复制函数只定义一次
  if (!window.copyCode) {
    // 定义全局复制函数
    window.copyCode = async (codeId) => {
      try {
        // 1. 获取目标代码元素
        const codeElement = document.getElementById(codeId)
        if (!codeElement) return // 元素不存在则退出
        
        // 2. 获取待复制的代码内容
        // 从元素的 data-code 属性获取 URL 编码的代码内容并解码
        const codeContent = decodeURIComponent(codeElement.getAttribute('data-code'))
        
        // 3. 写入剪贴板
        await navigator.clipboard.writeText(codeContent)
        
        // 显示复制成功反馈
        const btn = codeElement.parentElement.querySelector('.copy-code-btn')
        if (btn) {
          // 保存原始图标 SVG
          const originalIcon = btn.querySelector('.copy-icon').innerHTML
          
          // 替换为对号图标
          btn.querySelector('.copy-icon').innerHTML = `<path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474c-6.1-7.7-15.3-12.2-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9L357.1 864c12.6 16.1 35.5 16.1 48.1 0L918.3 202.9c4.1-5.2 0.4-12.9-6.3-12.9z" p-id="4582"></path>`
          // 添加复制成功状态类
          btn.classList.add('copied')     
          message.success('复制成功')

          // 1秒后恢复原始图标
          setTimeout(() => {
            btn.querySelector('.copy-icon').innerHTML = originalIcon
            btn.classList.remove('copied')
          }, 1000)
        }
      } catch (err) {
        console.error('复制失败:', err)
      }
    }
  }
}

// 监听 content 字段，流式更新处理
watch(() => props.content, (newVal) => {
  if (newVal) {
    // 渲染为 HTML
    const html = md.render(newVal)
    renderedContent.value = html

    // 确保复制功能在 DOM 更新后可用
    nextTick(() => {
      setupCopyFunction()
    })
  }
}, { immediate: true })
</script>

<style scoped>
/* 复制按钮样式 */
:deep(.copy-code-btn) {
  display: flex;
  align-items: center;
  gap: 4px;
  background: transparent;
  border-radius: 12px;
  padding: 0 8px;
  color: #586069;
  font-size: 12px;
  height: 28px;
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.copy-code-btn.copied .copy-icon) {
  fill: #22c55e;
}

:deep(.copy-code-btn:hover) {
  background-color: rgb(0 0 0 / 4%);
}

:deep(.copy-icon) {
  fill: currentColor;
  flex-shrink: 0;
}

:deep(.copy-text) {
  white-space: nowrap;
}
</style>
