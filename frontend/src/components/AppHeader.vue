<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import StatusLabel from '@/components/StatusLabel.vue'
import { useAuthStore } from '@/stores/auth'
import { APP_NAME } from '@/config'

const auth = useAuthStore()
const router = useRouter()
const menuOpen = ref(false)

// Navigation aus den Routen mit navLabel, inklusive Status-Label
const navItems = computed(() =>
  router
    .getRoutes()
    .filter((r) => r.meta.navLabel)
    .sort((a, b) => (a.meta.navOrder ?? 99) - (b.meta.navOrder ?? 99))
    .map((r) => ({ path: r.path, label: r.meta.navLabel!, status: r.meta.status })),
)

const initial = computed(() => auth.user?.displayName.charAt(0).toUpperCase() ?? '?')

async function logout() {
  menuOpen.value = false
  auth.logout()
  await router.push('/')
}
</script>

<template>
  <header class="topbar">
    <RouterLink class="brand" to="/">{{ APP_NAME }}</RouterLink>
    <nav class="nav" aria-label="Hauptnavigation">
      <RouterLink v-for="item in navItems" :key="item.path" :to="item.path">
        {{ item.label }}
        <StatusLabel v-if="item.status === 'demo' || item.status === 'wip'" :status="item.status" />
      </RouterLink>
    </nav>
    <div class="account">
      <button class="avatar" :aria-expanded="menuOpen" aria-label="Kontomenü" @click="menuOpen = !menuOpen">
        {{ initial }}
      </button>
      <div v-if="menuOpen" class="menu box">
        <p class="who">{{ auth.user?.displayName }}</p>
        <RouterLink to="/profil" @click="menuOpen = false">Profil <StatusLabel status="wip" /></RouterLink>
        <button class="link" @click="logout">Abmelden</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 16px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text);
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 4px;
  flex: 1;
  overflow-x: auto;
  scrollbar-width: none;
}

.nav a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 44px;
  padding: 0 10px;
  border-radius: 6px;
  color: var(--text);
  text-decoration: none;
  white-space: nowrap;
}

.nav a:hover {
  background: var(--bg);
}

.nav a.router-link-active {
  font-weight: 600;
  box-shadow: inset 0 -2px 0 var(--accent);
}

.account {
  position: relative;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid var(--border);
  background: var(--accent-weak);
  color: var(--accent-strong);
  font-weight: 600;
  cursor: pointer;
}

.menu {
  position: absolute;
  right: 0;
  top: 44px;
  min-width: 200px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu a,
.menu .link {
  display: flex;
  align-items: center;
  gap: 6px;
  min-height: 40px;
  padding: 0 8px;
  color: var(--text);
  text-decoration: none;
  background: none;
  border: none;
  font: inherit;
  text-align: left;
  cursor: pointer;
  border-radius: 6px;
}

.menu a:hover,
.menu .link:hover {
  background: var(--bg);
}

.who {
  margin: 4px 8px;
  color: var(--muted);
  font-size: 0.875rem;
}
</style>
