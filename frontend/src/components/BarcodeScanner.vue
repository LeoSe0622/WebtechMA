<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { BrowserMultiFormatReader, type IScannerControls } from '@zxing/browser'

// Liest Barcodes über die Kamera. Funktioniert nur unter HTTPS oder auf localhost.
const emit = defineEmits<{ detected: [code: string]; close: [] }>()

const video = ref<HTMLVideoElement | null>(null)
const error = ref<string | null>(null)
let controls: IScannerControls | null = null
let closed = false

onMounted(async () => {
  try {
    const reader = new BrowserMultiFormatReader()
    controls = await reader.decodeFromVideoDevice(undefined, video.value!, (result) => {
      if (result) {
        stop()
        emit('detected', result.getText())
      }
    })
    // Wurde der Scanner geschlossen, während die Kamera noch startete: sofort wieder aus
    if (closed) {
      stop()
    }
  } catch {
    error.value = 'Die Kamera ist nicht verfügbar. Gib die Barcode-Nummer oben von Hand ein.'
  }
})

function stop() {
  controls?.stop()
  controls = null
}

onBeforeUnmount(() => {
  closed = true
  stop()
})
</script>

<template>
  <div class="scanner box">
    <video v-show="!error" ref="video" class="video" muted playsinline></video>
    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <button type="button" class="button" @click="stop(); emit('close')">Kamera schließen</button>
  </div>
</template>

<style scoped>
.scanner {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px;
  margin-bottom: 12px;
}

.video {
  width: 100%;
  max-height: 260px;
  object-fit: cover;
  border-radius: 6px;
  background: #000;
}

.error {
  color: var(--danger);
  margin: 0;
}
</style>
