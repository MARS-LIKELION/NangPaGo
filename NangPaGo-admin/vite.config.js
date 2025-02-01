import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    server: {
      port: env.VITE_FRONT_SERVER_PORT,
      host: true,
      watch: {
        usePolling: true,
      },
      proxy: {
        '/api': {
          target: env.VITE_HOST,
          changeOrigin: true,
          ws: true,
          proxyTimeout: 60000, // 60초
          timeout: 60000,
        },
      },
    },
  };
});
