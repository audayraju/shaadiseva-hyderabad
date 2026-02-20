import apiClient from './client';

export interface VendorApplyRequest {
  email: string;
  phone: string;
  password: string;
  businessName: string;
  categoryId: string;
  gstNumber?: string;
  panNumber?: string;
  address?: string;
  city?: string;
  bio?: string;
  yearsExperience?: number;
}

export const applyAsVendor = (data: VendorApplyRequest) =>
  apiClient.post<{ applicationId: string }>('/api/vendor/applications', data);

export const uploadDocument = (applicationId: string, docType: string, file: File) => {
  const formData = new FormData();
  formData.append('docType', docType);
  formData.append('file', file);
  return apiClient.post(`/api/vendor/applications/${applicationId}/documents`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};
