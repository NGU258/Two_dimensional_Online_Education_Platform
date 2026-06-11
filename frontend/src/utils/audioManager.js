import { ref } from 'vue'

const audioCache = ref({})
const enabled = ref(true)

export const useAudioManager = () => {
  const getResourceUrl = (path) => {
    if (!path) return ''
    const separator = path.includes('?') ? '&' : '?'
    return `${path}${separator}cache_bust=${Date.now()}`
  }

  const playSound = async (soundName) => {
    if (!enabled.value) return
    
    try {
      const cacheKey = soundName
      let audio = audioCache.value[cacheKey]
      
      if (!audio) {
        const url = getResourceUrl(`/audio/${encodeURIComponent(soundName)}.mp3`)
        audio = new Audio(url)
        audio.volume = 0.3
        audioCache.value[cacheKey] = audio
      }
      
      await audio.play().catch(async (err) => {
        console.log('第一次播放失败，尝试重新加载:', err)
        
        const url = getResourceUrl(`/audio/${encodeURIComponent(soundName)}.mp3`)
        const newAudio = new Audio(url)
        newAudio.volume = 0.3
        audioCache.value[cacheKey] = newAudio
        
        await newAudio.play().catch(err2 => {
          console.log('第二次播放也失败:', err2)
        })
      })
    } catch (error) {
      console.log('音效播放错误:', error)
    }
  }

  const playBackgroundMusic = async (musicPath) => {
    if (!enabled.value) return
    
    try {
      const cacheKey = 'background_' + musicPath
      let audio = audioCache.value[cacheKey]
      
      if (!audio) {
        const url = getResourceUrl(musicPath)
        audio = new Audio(url)
        audio.volume = 0.5
        audio.loop = true
        audioCache.value[cacheKey] = audio
      }
      
      await audio.play().catch(async (err) => {
        console.log('背景音乐播放失败:', err)
        
        const url = getResourceUrl(musicPath)
        const newAudio = new Audio(url)
        newAudio.volume = 0.5
        newAudio.loop = true
        audioCache.value[cacheKey] = newAudio
        
        await newAudio.play().catch(err2 => {
          console.log('背景音乐第二次播放也失败:', err2)
        })
      })
    } catch (error) {
      console.log('背景音乐播放错误:', error)
    }
  }

  const stopBackgroundMusic = (musicPath) => {
    const cacheKey = 'background_' + musicPath
    const audio = audioCache.value[cacheKey]
    if (audio) {
      audio.pause()
      audio.currentTime = 0
    }
  }

  const playClickSound = () => {
    const soundIndex = Math.floor(Math.random() * 3) + 1
    playSound(`点击音效${soundIndex}`)
  }

  const setEnabled = (value) => {
    enabled.value = value
  }

  const clearCache = () => {
    Object.values(audioCache.value).forEach(audio => {
      if (audio) {
        audio.pause()
      }
    })
    audioCache.value = {}
  }

  return {
    playSound,
    playBackgroundMusic,
    stopBackgroundMusic,
    playClickSound,
    setEnabled,
    clearCache,
    getResourceUrl
  }
}