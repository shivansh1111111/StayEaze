import axios from 'axios';

// Create instance
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // Set your backend URL
});

// Add request interceptor
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;
