import axiosInstance from './axiosInstance';

export const getRawMaterials = () => axiosInstance.get('/raw-materials');
export const createRawMaterial = (material) => axiosInstance.post('/raw-materials', material);
export const updateRawMaterial = (id, material) => axiosInstance.put(`/raw-materials/${id}`, material);
export const deleteRawMaterial = (id) => axiosInstance.delete(`/raw-materials/${id}`);
