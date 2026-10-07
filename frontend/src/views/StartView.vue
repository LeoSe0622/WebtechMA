<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login, loginDemo, register, type TokenResponse } from '@/api/auth'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { APP_NAME, HOME_PATH } from '@/config'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const mode = ref<'login' | 'register'>('login')
const busy = ref(false)
const error = ref<string | null>(null)

const username = ref('')
const password = ref('')
const displayName = ref('')
const householdSize = ref(1)
const leaderboardOptIn = ref(false)

// Gemeinsamer Ablauf für alle drei Wege: Anfrage schicken, Token speichern, weiterleiten
async function signIn(request: () => Promise<TokenResponse>) {
  busy.value = true
  error.value = null
  try {
    auth.setSession(await request())
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : HOME_PATH
    await router.push(redirect)
  } catch (e) {
    error.value = describe(e)
  } finally {
    busy.value = false
  }
}

function describe(e: unknown): string {
  if (e instanceof ApiError) {
    const fieldErrors = e.problem?.errors?.map((f) => `${f.field}: ${f.message}`).join(', ')
    return fieldErrors ? `${e.message} (${fieldErrors})` : e.message
  }
  return 'Das Backend ist gerade nicht erreichbar. Versuch es gleich noch einmal.'
}

function submit() {
  if (mode.value === 'login') {
    signIn(() => login(username.value, password.value))
  } else {
    signIn(() =>
      register({
        username: username.value,
        password: password.value,
        displayName: displayName.value,
        householdSize: householdSize.value,
        leaderboardOptIn: leaderboardOptIn.value,
      }),
    )
  }
}
</script>

<template>
  <section class="start">
    <div class="intro">
      <p class="brand">{{ APP_NAME }}</p>
      <h1>Wie viel sparst du beim Einkaufen?</h1>
      <p class="lead">
        Plane dein Monatsbudget, hake die Einkaufsliste ab und sieh, was aus deiner Ersparnis werden könnte.
      </p>
      <button class="button primary big" :disabled="busy" @click="signIn(loginDemo)">Als Demo testen</button>
      <p class="hint">Die Demo legt dir einen eigenen Zugang mit Beispieldaten an. Nach 7 Tagen wird er gelöscht.</p>
    </div>

    <form class="box form" @submit.prevent="submit">
      <div class="tabs" role="tablist">
        <button type="button" role="tab" :aria-selected="mode === 'login'" @click="mode = 'login'">
          Anmelden
        </button>
        <button type="button" role="tab" :aria-selected="mode === 'register'" @click="mode = 'register'">
          Registrieren
        </button>
      </div>

      <label>Benutzername <input v-model="username" autocomplete="username" required minlength="3" /></label>
      <label>
        Passwort
        <input
          v-model="password"
          type="password"
          :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
          required
          :minlength="mode === 'register' ? 8 : 1"
        />
      </label>

      <template v-if="mode === 'register'">
        <label>Pseudonym (für die Rangliste) <input v-model="displayName" required minlength="3" maxlength="40" /></label>
        <label>
          Personen im Haushalt
          <input v-model.number="householdSize" type="number" min="1" max="6" required />
        </label>
        <label class="check">
          <input v-model="leaderboardOptIn" type="checkbox" /> In der Rangliste erscheinen
        </label>
      </template>

      <p v-if="error" class="error" role="alert">{{ error }}</p>
      <button class="button primary" type="submit" :disabled="busy">
        {{ mode === 'login' ? 'Anmelden' : 'Konto anlegen' }}
      </button>
    </form>
  </section>
</template>

<style scoped>
.start {
  display: grid;
  gap: 32px;
  align-items: start;
  padding-top: 24px;
}

@media (min-width: 800px) {
  .start {
    grid-template-columns: 1.2fr 1fr;
  }
}

.brand {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  color: var(--accent-strong);
  margin: 0;
}

h1 {
  font-family: var(--font-serif);
  font-size: 2.25rem;
  line-height: 1.2;
  margin: 8px 0 16px;
}

.lead {
  color: var(--muted);
  font-size: 1.125rem;
}

.big {
  font-size: 1.125rem;
  padding: 12px 24px;
}

.hint {
  color: var(--muted);
  font-size: 0.875rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
}

.tabs {
  display: flex;
  gap: 4px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 4px;
}

.tabs button {
  flex: 1;
  min-height: 44px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  font: inherit;
  cursor: pointer;
}

.tabs button[aria-selected='true'] {
  border-bottom-color: var(--accent);
  font-weight: 600;
}

label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.875rem;
}

.check {
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.error {
  color: var(--danger);
  margin: 0;
}
</style>
