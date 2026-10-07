import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { TokenResponse, User } from '@/api/auth'

const TOKEN_KEY = 'korbgeld.token'
const USER_KEY = 'korbgeld.user'

/**
 * Zentrale Ablage für den Login. Das Token liegt zusätzlich im sessionStorage,
 * damit Neuladen der Seite nicht abmeldet; beim Schließen des Tabs ist es weg.
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(sessionStorage.getItem(TOKEN_KEY))
  const user = ref<User | null>(readUser())

  const isAuthenticated = computed(() => token.value !== null)

  function setSession(response: TokenResponse) {
    token.value = response.token
    user.value = response.user
    sessionStorage.setItem(TOKEN_KEY, response.token)
    sessionStorage.setItem(USER_KEY, JSON.stringify(response.user))
  }

  function logout() {
    token.value = null
    user.value = null
    sessionStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
  }

  return { token, user, isAuthenticated, setSession, logout }
})

function readUser(): User | null {
  const raw = sessionStorage.getItem(USER_KEY)
  return raw ? (JSON.parse(raw) as User) : null
}
