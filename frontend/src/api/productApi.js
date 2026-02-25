import axiosInstance from './axiosInstance';

export const getProducts = () => axiosInstance.get('/products');
export const createProduct = (product) => axiosInstance.post('/products', product);
export const updateProduct = (id, product) => axiosInstance.put(`/products/${id}`, product);
export const deleteProduct = (id) => axiosInstance.delete(`/products/${id}`);