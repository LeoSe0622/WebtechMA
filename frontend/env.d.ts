/// <reference types="vite/client" />

// Typen für die Variablen aus der .env, die Vite ins Frontend übernimmt (Präfix VITE_)
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
