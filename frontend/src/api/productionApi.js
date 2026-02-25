import axiosInstance from './axiosInstance';

export const getProducts = () => axiosInstance.get('/production/preview');
export const createProduct = (production) => axiosInstance.post('/production', production);
export const updateProduct = (id, production) => axiosInstance.put(`/production/${id}`, production);
export const deleteProduct = (id) => axiosInstance.delete(`/production/${id}`);