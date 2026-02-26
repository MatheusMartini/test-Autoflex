import React, { useState, useEffect } from 'react';
import { createRawMaterial, updateRawMaterial } from '../api/rawMaterialApi';
import { TextField, Button, Typography, Grid } from '@mui/material';

export default function RawMaterialForm({ onSaved, editingMaterial, onError }) {
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [stockQuantity, setStockQuantity] = useState(0);

  useEffect(() => {
    if (editingMaterial) {
      setCode(editingMaterial.code || '');
      setName(editingMaterial.name || '');
      setStockQuantity(editingMaterial.stockQuantity ?? 0);
    } else {
      resetForm();
    }
  }, [editingMaterial]);

  const resetForm = () => {
    setCode('');
    setName('');
    setStockQuantity(0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const material = {
      code,
      name,
      stockQuantity: parseFloat(stockQuantity)
    };

    try {
      if (editingMaterial) {
        await updateRawMaterial(editingMaterial.id, material);
      } else {
        await createRawMaterial(material);
      }

      resetForm();
      onSaved();
    } catch (e2) {
      if (onError) {
        onError(e2?.response?.data?.message || 'Failed to save raw material.');
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        {editingMaterial ? 'Edit Material' : 'Add Material'}
      </Typography>

      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid item xs={12} md={4}>
          <TextField
            fullWidth
            label="Material Code"
            value={code}
            onChange={e => setCode(e.target.value)}
            required
          />
        </Grid>

        <Grid item xs={12} md={4}>
          <TextField
            fullWidth
            label="Material Name"
            value={name}
            onChange={e => setName(e.target.value)}
            required
          />
        </Grid>

        <Grid item xs={12} md={4}>
          <TextField
            fullWidth
            type="number"
            label="Stock Quantity"
            value={stockQuantity}
            onChange={e => setStockQuantity(e.target.value)}
            required
          />
        </Grid>
      </Grid>

      <Button variant="contained" color="primary" type="submit">
        {editingMaterial ? 'Update' : 'Add'} Material
      </Button>
    </form>
  );
}
