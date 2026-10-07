import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config.ts'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      // Feste Backend-Adresse für Tests; echte Aufrufe werden in den Tests durch Attrappen ersetzt
      env: { VITE_API_BASE_URL: 'http://backend.test' },
    },
  }),
)
