import React, { useState, useEffect } from 'react';
import { createProduct, updateProduct } from '../api/productApi';
import { getRawMaterials } from '../api/rawMaterialApi';
import { TextField, Button, Grid, Typography } from '@mui/material';

export default function ProductForm({ onSaved, editingProduct }) {
  const [name, setName] = useState('');
  const [value, setValue] = useState(0);
  const [materials, setMaterials] = useState([]);
  const [selectedMaterials, setSelectedMaterials] = useState({});

  useEffect(() => {
    getRawMaterials().then(res => setMaterials(res.data));
    if (editingProduct) {
      setName(editingProduct.name);
      setValue(editingProduct.value);
      const map = {};
      editingProduct.materials?.forEach(m => map[m.id] = m.quantity);
      setSelectedMaterials(map);
    }
  }, [editingProduct]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const product = {
      name,
      value,
      materials: Object.entries(selectedMaterials).map(([id, quantity]) => ({
        id: parseInt(id),
        quantity: parseFloat(quantity)
      }))
    };
    if (editingProduct) await updateProduct(editingProduct.id, product);
    else await createProduct(product);
    setName('');
    setValue(0);
    setSelectedMaterials({});
    onSaved();
  };

  return (
    <form onSubmit={handleSubmit}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        {editingProduct ? 'Edit Product' : 'Add Product'}
      </Typography>
      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid xs={12} md={6}>
          <TextField 
            fullWidth
            label="Product Name" 
            value={name} 
            onChange={e => setName(e.target.value)} 
            required
          />
        </Grid>
        <Grid xs={12} md={6}>
          <TextField 
            fullWidth
            type="number"
            label="Value" 
            value={value} 
            onChange={e => setValue(e.target.value)} 
            required
          />
        </Grid>
      </Grid>

      <Typography variant="subtitle1">Raw Materials</Typography>
      <Grid container spacing={2} sx={{ mb: 2 }}>
        {materials.map(m => (
          <Grid xs={12} md={4} key={m.id}>
            <TextField
              fullWidth
              label={m.name}
              type="number"
              value={selectedMaterials[m.id] || ''}
              onChange={e => setSelectedMaterials({ ...selectedMaterials, [m.id]: e.target.value })}
            />
          </Grid>
        ))}
      </Grid>

      <Button variant="contained" color="primary" type="submit">
        {editingProduct ? 'Update' : 'Add'} Product
      </Button>
    </form>
  );
}