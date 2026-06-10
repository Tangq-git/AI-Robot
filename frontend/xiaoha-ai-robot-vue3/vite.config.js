import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import Components from 'unplugin-vue-components/vite';
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers';
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'path' // 👈 新增

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
    Components({
      resolvers: [
        AntDesignVueResolver({
          importStyle: false, // css in js
        }),
      ],
    }),
    createSvgIconsPlugin({
      // 指定项目中存放 SVG 图标的目录路径（插件会扫描此目录下的所有 .svg 文件）
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
      // 定义生成的 <symbol> 元素的 id 属性的命名格式
      symbolId: 'icon-[dir]-[name]',

      /**
       * 自定义 SVG Sprite 的插入位置
       * @default: body-last
       */
      inject: 'body-last',
      /**
         * 自定义 dom id
         * @default: __svg__icons__dom__
         */
      customDomId: '__svg__icons__dom__',
    }),
  ],

  // 👇 新增别名配置
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})