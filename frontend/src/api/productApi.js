import axiosInstance from './axiosInstance';

export const getProducts = () => axiosInstance.get('/production/preview');
export const createProduct = (product) => axiosInstance.post('/production', product);
export const updateProduct = (id, product) => axiosInstance.put(`/production/${id}`, product);
export const deleteProduct = (id) => axiosInstance.delete(`/production/${id}`);