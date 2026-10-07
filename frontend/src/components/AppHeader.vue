<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatusLabel from '@/components/StatusLabel.vue'
import { useAuthStore } from '@/stores/auth'
import { APP_NAME } from '@/config'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const menuOpen = ref(false)
const moreOpen = ref(false)

// Navigation aus den Routen mit navLabel, inklusive Status-Label
const navItems = computed(() =>
  router
    .getRoutes()
    .filter((r) => r.meta.navLabel)
    .sort((a, b) => (a.meta.navOrder ?? 99) - (b.meta.navOrder ?? 99))
    .map((r) => ({ path: r.path, label: r.meta.navLabel!, status: r.meta.status })),
)

// Handy: die echten Bereiche als Tabs unten, Demo- und In-Arbeit-Bereiche hinter „Mehr“
const primaryItems = computed(() => navItems.value.filter((item) => item.status === 'ready'))
const moreItems = computed(() => navItems.value.filter((item) => item.status !== 'ready'))
const moreActive = computed(() => moreItems.value.some((item) => route.path === item.path))

// Menüs schließen, sobald die Seite wechselt
watch(() => route.path, () => {
  menuOpen.value = false
  moreOpen.value = false
})

const initial = computed(() => auth.user?.displayName.charAt(0).toUpperCase() ?? '?')

async function logout() {
  menuOpen.value = false
  auth.logout()
  await router.push('/')
}
</script>

<template>
  <header class="topbar">
    <RouterLink class="brand" to="/dashboard">{{ APP_NAME }}</RouterLink>
    <!-- Breite Bildschirme: Navigation in der Kopfleiste -->
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
        <RouterLink to="/profil">Profil <StatusLabel status="wip" /></RouterLink>
        <button class="link" @click="logout">Abmelden</button>
      </div>
    </div>
  </header>

  <!-- Schmale Bildschirme: Tab-Leiste unten, mit dem Daumen erreichbar -->
  <nav class="tabbar" aria-label="Hauptnavigation (mobil)">
    <RouterLink v-for="item in primaryItems" :key="item.path" :to="item.path">{{ item.label }}</RouterLink>
    <button type="button" :class="{ active: moreActive }" :aria-expanded="moreOpen" @click="moreOpen = !moreOpen">
      Mehr
    </button>
    <div v-if="moreOpen" class="more box">
      <RouterLink v-for="item in moreItems" :key="item.path" :to="item.path">
        {{ item.label }}
        <StatusLabel v-if="item.status === 'demo' || item.status === 'wip'" :status="item.status" />
      </RouterLink>
    </div>
  </nav>
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
  flex-wrap: wrap;
  gap: 4px;
  flex: 1;
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
  margin-left: auto;
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
.menu .link,
.more a {
  display: flex;
  align-items: center;
  gap: 6px;
  min-height: 44px;
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
.menu .link:hover,
.more a:hover {
  background: var(--bg);
}

.who {
  margin: 4px 8px;
  color: var(--muted);
  font-size: 0.875rem;
}

/* Die Tab-Leiste gibt es nur auf schmalen Bildschirmen */
.tabbar {
  display: none;
}

@media (max-width: 719px) {
  .nav {
    display: none;
  }

  .tabbar {
    display: flex;
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 20;
    height: var(--tabbar-height);
    background: var(--surface);
    border-top: 1px solid var(--border);
  }

  .tabbar > a,
  .tabbar > button {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 44px;
    padding: 0 4px;
    color: var(--muted);
    text-decoration: none;
    background: none;
    border: none;
    font: inherit;
    font-size: 0.875rem;
    cursor: pointer;
  }

  .tabbar > a.router-link-active,
  .tabbar > button.active {
    color: var(--accent-strong);
    font-weight: 600;
    box-shadow: inset 0 2px 0 var(--accent);
  }

  .more {
    position: absolute;
    right: 8px;
    bottom: calc(var(--tabbar-height) + 8px);
    min-width: 220px;
    padding: 8px;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
}
</style>
