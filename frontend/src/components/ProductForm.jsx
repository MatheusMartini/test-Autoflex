import React, { useState, useEffect } from 'react';
import { createProduct, updateProduct } from '../api/productApi';
import { TextField, Button, Grid, Typography } from '@mui/material';

export default function ProductForm({ onSaved, editingProduct, onCancel }) {
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [price, setPrice] = useState(0);

  useEffect(() => {
    if (editingProduct) {
      setCode(editingProduct.code || '');
      setName(editingProduct.name || '');
      setPrice(editingProduct.price ?? 0);
    } else {
      resetForm();
    }
  }, [editingProduct]);

  const resetForm = () => {
    setCode('');
    setName('');
    setPrice(0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const product = {
      code,
      name,
      price: parseFloat(price)
    };

    if (editingProduct) {
      await updateProduct(editingProduct.id, product);
    } else {
      await createProduct(product);
    }
    resetForm();
    onSaved();
  };

  return (
    <form onSubmit={handleSubmit}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        {editingProduct ? 'Edit Product' : 'Add Product'}
      </Typography>

      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid xs={12} md={4}>
          <TextField 
            fullWidth
            label="Product Code"
            value={code}
            onChange={e => setCode(e.target.value)}
            required
          />
        </Grid>

        <Grid xs={12} md={4}>
          <TextField 
            fullWidth
            label="Product Name" 
            value={name} 
            onChange={e => setName(e.target.value)} 
            required
          />
        </Grid>

        <Grid xs={12} md={4}>
          <TextField 
            fullWidth
            type="number"
            label="Price" 
            value={price} 
            onChange={e => setPrice(e.target.value)} 
            required
          />
        </Grid>
      </Grid>

      <Button variant="contained" color="primary" type="submit">
        {editingProduct ? 'Update' : 'Add'} Product
      </Button>
    </form>
  );
}