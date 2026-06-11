const fs = require('fs')
const path = require('path')

const projectRoot = path.join(__dirname, '..')

// 需要清理的缓存目录列表
const cacheDirs = [
  path.join(projectRoot, 'node_modules', '.vite'),
  path.join(projectRoot, 'node_modules', '.vite-deps'),
  path.join(projectRoot, 'dist'),
  path.join(projectRoot, '.dist'),
  path.join(projectRoot, '.cache'),
  path.join(projectRoot, 'cache')
]

// 清理缓存函数
function clearCache(dir) {
  if (fs.existsSync(dir)) {
    console.log(`\x1b[33m清理缓存目录: ${dir}\x1b[0m`)
    
    try {
      fs.rmSync(dir, { recursive: true, force: true })
      console.log('\x1b[32m✓ 清理成功！\x1b[0m')
    } catch (error) {
      console.log(`\x1b[31m✗ 清理失败: ${error.message}\x1b[0m`)
    }
  } else {
    console.log(`\x1b[36m目录不存在，跳过: ${dir}\x1b[0m`)
  }
}

console.log('\x1b[34m==========================================\x1b[0m')
console.log('\x1b[34m开始清理项目缓存...\x1b[0m')
console.log('\x1b[34m==========================================\x1b[0m')

// 执行清理所有缓存目录
cacheDirs.forEach(dir => {
  clearCache(dir)
})

console.log('\x1b[34m==========================================\x1b[0m')
console.log('\x1b[32m✓ 所有缓存清理完成！\x1b[0m')
console.log('\x1b[34m==========================================\x1b[0m')