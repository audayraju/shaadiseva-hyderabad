import apiClient from './client';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  role: string;
  userId: string;
}

export const sendOtp = (phone: string) =>
  apiClient.post('/api/auth/otp/send', { phone });

export const verifyOtp = (phone: string, otp: string) =>
  apiClient.post<AuthResponse>('/api/auth/otp/verify', { phone, otp });

export const login = (username: string, password: string) =>
  apiClient.post<AuthResponse>('/api/auth/login', { username, password });
