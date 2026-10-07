import { apiRequest } from './client'

export interface User {
  id: number
  username: string
  displayName: string
  householdSize: number
  leaderboardOptIn: boolean
  sandbox: boolean
}

export interface TokenResponse {
  token: string
  user: User
}

export interface RegisterRequest {
  username: string
  password: string
  displayName: string
  householdSize: number
  leaderboardOptIn: boolean
}

export function loginDemo(): Promise<TokenResponse> {
  return apiRequest<TokenResponse>('/api/auth/demo', { method: 'POST' })
}

export function login(username: string, password: string): Promise<TokenResponse> {
  return apiRequest<TokenResponse>('/api/auth/login', { method: 'POST', body: { username, password } })
}

export function register(request: RegisterRequest): Promise<TokenResponse> {
  return apiRequest<TokenResponse>('/api/auth/register', { method: 'POST', body: request })
}
