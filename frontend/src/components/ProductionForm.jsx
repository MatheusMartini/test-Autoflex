import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import {
  TextField,
  Button,
  Grid,
  Typography,
  MenuItem
} from '@mui/material';

export default function ProductionForm({ onSaved }) {

  const [products, setProducts] = useState([]);
  const [rawMaterials, setRawMaterials] = useState([]);

  const [productId, setProductId] = useState('');
  const [rawMaterialId, setRawMaterialId] = useState('');
  const [requiredQuantity, setRequiredQuantity] = useState('');

  useEffect(() => {
    fetchProducts();
    fetchRawMaterials();
  }, []);

  const fetchProducts = async () => {
    const res = await axiosInstance.get('/products');
    setProducts(res.data);
  };

  const fetchRawMaterials = async () => {
    const res = await axiosInstance.get('/raw-materials');
    setRawMaterials(res.data);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!productId || !rawMaterialId || !requiredQuantity) {
      alert("All fields are required");
      return;
    }

    try {

      await axiosInstance.post('/product-materials', {
        productId: Number(productId),
        rawMaterialId: Number(rawMaterialId),
        requiredQuantity: Number(requiredQuantity)
      });

      setProductId('');
      setRawMaterialId('');
      setRequiredQuantity('');

      if (onSaved) onSaved();

    } catch (error) {
      console.error(error.response?.data || error.message);
      alert(error.response?.data?.message || "Error creating association");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Associate Raw Material to Product
      </Typography>

      <Grid container spacing={2} sx={{ mb: 2 }}>

        <Grid item xs={12} md={4}>
          <TextField
            select
            fullWidth
            label="Product"
            value={productId}
            onChange={(e) => setProductId(e.target.value)}
            required
          >
            {products.map(p => (
              <MenuItem key={p.id} value={p.id}>
                {p.code} - {p.name}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={12} md={4}>
          <TextField
            select
            fullWidth
            label="Raw Material"
            value={rawMaterialId}
            onChange={(e) => setRawMaterialId(e.target.value)}
            required
          >
            {rawMaterials.map(r => (
              <MenuItem key={r.id} value={r.id}>
                {r.code} - {r.name}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={12} md={4}>
          <TextField
            fullWidth
            type="number"
            label="Required Quantity"
            value={requiredQuantity}
            onChange={(e) => setRequiredQuantity(e.target.value)}
            inputProps={{ min: 0.01, step: "0.01" }}
            required
          />
        </Grid>

      </Grid>

      <Button variant="contained" type="submit">
        Add Association
      </Button>
    </form>
  );
}