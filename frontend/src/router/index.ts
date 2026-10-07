import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUiStore } from '@/stores/ui'
import StartView from '@/views/StartView.vue'
import DashboardView from '@/views/DashboardView.vue'
import BudgetView from '@/views/BudgetView.vue'
import ShoppingListView from '@/views/ShoppingListView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import PantryView from '@/views/PantryView.vue'
import HabitsView from '@/views/HabitsView.vue'
import LeaderboardView from '@/views/LeaderboardView.vue'
import SavingsPlanView from '@/views/SavingsPlanView.vue'
import WorkInProgressView from '@/views/WorkInProgressView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import { HOME_PATH } from '@/config'

// Zusatzangaben pro Route (AUFTRAG.md, Abschnitt 8): Status, Feature, Milestone, offene Aufgaben
declare module 'vue-router' {
  interface RouteMeta {
    status?: 'ready' | 'demo' | 'wip'
    feature?: string
    milestone?: string
    tasks?: string[]
    navLabel?: string
    navOrder?: number
    public?: boolean
  }
}

export function createAppRouter() {
  const router = createRouter({
    // History-Modus: echte URLs wie /liste statt /#/liste
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
      { path: '/', name: 'start', component: StartView, meta: { status: 'ready', public: true } },
      {
        path: '/dashboard',
        name: 'dashboard',
        component: DashboardView,
        meta: { status: 'ready', navLabel: 'Dashboard', navOrder: 1 },
      },
      {
        path: '/budget',
        name: 'budget',
        component: BudgetView,
        meta: { status: 'ready', navLabel: 'Budget', navOrder: 2 },
      },
      {
        path: '/liste',
        name: 'shopping-list',
        component: ShoppingListView,
        meta: { status: 'ready', navLabel: 'Liste', navOrder: 3 },
      },
      { path: '/liste/abschliessen', name: 'checkout', component: CheckoutView, meta: { status: 'ready' } },
      {
        path: '/vorrat',
        name: 'pantry',
        component: PantryView,
        meta: { status: 'ready', navLabel: 'Vorrat', navOrder: 4 },
      },
      {
        path: '/gewohnheiten',
        name: 'habits',
        component: HabitsView,
        meta: { status: 'demo', navLabel: 'Gewohnheiten', navOrder: 5, feature: 'Gewohnheiten', milestone: 'M4 · 13. Dez.' },
      },
      {
        path: '/rangliste',
        name: 'leaderboard',
        component: LeaderboardView,
        meta: { status: 'demo', navLabel: 'Rangliste', navOrder: 6, feature: 'Rangliste', milestone: 'M4 · 13. Dez.' },
      },
      {
        path: '/sparplan',
        name: 'savings-plan',
        component: SavingsPlanView,
        meta: { status: 'demo', navLabel: 'Sparplan', navOrder: 7, feature: 'Sparplan', milestone: 'M4 · 13. Dez.' },
      },
      {
        path: '/rezepte',
        name: 'recipes',
        component: WorkInProgressView,
        meta: {
          status: 'wip',
          navLabel: 'Rezepte',
          navOrder: 8,
          feature: 'Rezepte',
          milestone: 'nach M4',
          tasks: ['Rezepte passend zum Vorrat vorschlagen', 'Fehlende Zutaten auf die Liste setzen'],
        },
      },
      {
        path: '/preise',
        name: 'price-comparison',
        component: WorkInProgressView,
        meta: {
          status: 'wip',
          navLabel: 'Preise',
          navOrder: 9,
          feature: 'Preisvergleich',
          milestone: 'nach M4',
          tasks: ['Preise über Open Prices abrufen', 'Günstigsten Laden je Produkt anzeigen'],
        },
      },
      {
        path: '/profil',
        name: 'profile',
        component: WorkInProgressView,
        meta: {
          status: 'wip',
          feature: 'Profil',
          milestone: 'M4 · 13. Dez.',
          tasks: ['Pseudonym und Haushaltsgröße ändern', 'Teilnahme an der Rangliste ein- und ausschalten'],
        },
      },
      // Alles andere: 404-Seite, die URL bleibt erhalten
      { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFoundView, meta: { public: true } },
    ],
  })

  // Globaler Guard: Ohne Login nur öffentliche Seiten; eingeloggt führt die Startseite direkt in die App
  router.beforeEach((to) => {
    const auth = useAuthStore()
    if (!to.meta.public && !auth.isAuthenticated) {
      return { name: 'start', query: { redirect: to.fullPath } }
    }
    if (to.name === 'start' && auth.isAuthenticated) {
      return HOME_PATH
    }
    return true
  })

  // Eine 404- oder In-Arbeit-Ersetzung gilt nur für die Seite, auf der sie entstand
  router.afterEach(() => {
    useUiStore().reset()
  })

  return router
}

// Die eine Router-Instanz der App; Tests erzeugen sich mit createAppRouter() eine eigene
const router = createAppRouter()

export default router
