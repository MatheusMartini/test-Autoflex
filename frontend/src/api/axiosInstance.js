import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // porta Quarkus
  headers: {
    'Content-Type': 'application/json',
  },
});

export default axiosInstance;