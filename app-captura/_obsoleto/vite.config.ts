import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

// Base relativa: la demo se despliega igual en Cloudflare Pages, Netlify o un subdirectorio.
export default defineConfig({
  base: './',
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      // La semilla se precachea: sin ella la app no arranca en campo.
      includeAssets: ['seed.json', 'icono-192.png', 'icono-512.png'],
      manifest: {
        name: 'FlorLogic — Captura',
        short_name: 'Captura',
        description: 'Prototipo de captura en campo sin conexión',
        theme_color: '#0f5132',
        background_color: '#0b0f0d',
        display: 'standalone',
        orientation: 'portrait',
        lang: 'es-CO',
        icons: [
          { src: 'icono-192.png', sizes: '192x192', type: 'image/png' },
          { src: 'icono-512.png', sizes: '512x512', type: 'image/png' },
          { src: 'icono-512.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' },
        ],
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,png,svg,json}'],
      },
    }),
  ],
})
