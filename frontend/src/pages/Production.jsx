import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import Layout from '../components/Layout';
import { Table, TableBody, TableCell, TableHead, TableRow, Button, Typography } from '@mui/material';

export default function Production() {
  const [productionList, setProductionList] = useState([]);

  const fetchProduction = async () => {
    const res = await axiosInstance.get('/production/preview');
    setProductionList(res.data);
  };

  useEffect(() => { fetchProduction(); }, []);

  return (
    <Layout>
      <Typography variant="h5" sx={{ mb: 2 }}>Suggested Production</Typography>
      <Button variant="contained" onClick={fetchProduction} sx={{ mb: 2 }}>Refresh</Button>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Product</TableCell>
            <TableCell>Quantity</TableCell>
            <TableCell>Total Value</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {productionList.map(p => (
            <TableRow key={p.productId}>
              <TableCell>{p.name}</TableCell>
              <TableCell>{p.maxQuantity}</TableCell>
              <TableCell>${p.totalValue.toFixed(2)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Layout>
  );
}