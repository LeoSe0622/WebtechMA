import { createRouter, createWebHistory } from 'vue-router'
import ShoppingListView from '@/views/ShoppingListView.vue'

const router = createRouter({
  // History-Modus: echte URLs wie /liste statt /#/liste
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Die Startseite (Login) kommt in Phase 2, bis dahin geht es direkt zur Liste
    { path: '/', redirect: '/liste' },
    { path: '/liste', name: 'shopping-list', component: ShoppingListView },
  ],
})

export default router
