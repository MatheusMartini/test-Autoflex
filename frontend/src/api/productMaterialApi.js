import axiosInstance from './axiosInstance';

export const listProductMaterials = () => axiosInstance.get('/product-materials');
export const createProductMaterial = (payload) => axiosInstance.post('/product-materials', payload);
export const updateProductMaterial = (id, payload) => axiosInstance.put(`/product-materials/${id}`, payload);
export const deleteProductMaterial = (id) => axiosInstance.delete(`/product-materials/${id}`);
