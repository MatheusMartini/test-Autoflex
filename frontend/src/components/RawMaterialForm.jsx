import React, { useState, useEffect } from 'react';
import { createRawMaterial, updateRawMaterial } from '../api/rawMaterialApi';
import { TextField, Button, Typography, Grid } from '@mui/material';

export default function RawMaterialForm({ onSaved, editingMaterial }) {
  const [name, setName] = useState('');
  const [quantity, setQuantity] = useState(0);

  useEffect(() => {
    if (editingMaterial) {
      setName(editingMaterial.name);
      setQuantity(editingMaterial.quantity);
    }
  }, [editingMaterial]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const material = { name, quantity };
    if (editingMaterial) await updateRawMaterial(editingMaterial.id, material);
    else await createRawMaterial(material);
    setName('');
    setQuantity(0);
    onSaved();
  };

  return (
    <form onSubmit={handleSubmit}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        {editingMaterial ? 'Edit Material' : 'Add Material'}
      </Typography>
      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid xs={12} md={6}>
          <TextField 
            fullWidth
            label="Material Name" 
            value={name} 
            onChange={e => setName(e.target.value)} 
            required
          />
        </Grid>
        <Grid xs={12} md={6}>
          <TextField 
            fullWidth
            type="number"
            label="Quantity" 
            value={quantity} 
            onChange={e => setQuantity(e.target.value)} 
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