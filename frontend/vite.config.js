import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { rmSync } from 'fs'
import path from 'path'

const viteCacheDir = path.join(__dirname, 'node_modules', '.vite')

export default defineConfig(({ command }) => {
  if (command === 'serve') {
    try {
      rmSync(viteCacheDir, { recursive: true, force: true })
      console.log('\x1b[32m✓ Vite 缓存清理成功！\x1b[0m')
    } catch (error) {
      console.log('\x1b[36mVite 缓存目录不存在，跳过清理\x1b[0m')
    }
  }

  return {
    plugins: [vue()],
    server: {
      port: 8066,
      proxy: {
        '/api': {
          target: 'http://localhost:8081',
          changeOrigin: true
        }
      },
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0',
        'X-Vite-Cache-Control': 'no-cache'
      }
    },
    build: {
      rollupOptions: {
        output: {
          chunkFileNames: 'assets/[name]-[hash].js',
          entryFileNames: 'assets/[name]-[hash].js',
          assetFileNames: 'assets/[name]-[hash].[ext]'
        }
      }
    },
    optimizeDeps: {
      cacheDir: './node_modules/.vite/deps',
      force: true
    }
  }
})