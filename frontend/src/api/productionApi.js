import axiosInstance from './axiosInstance';

export const previewProduction = () => axiosInstance.get('/production/preview');
export const executeProduction = () => axiosInstance.post('/production/execute');

export const listProductionRuns = () => axiosInstance.get('/production/runs');
export const getProductionRun = (id) => axiosInstance.get(`/production/runs/${id}`);