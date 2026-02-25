import axiosInstance from './axiosInstance';

export const previewProduction = () => axiosInstance.get('/production/preview');
export const executeProduction = () => axiosInstance.post('/production/execute');