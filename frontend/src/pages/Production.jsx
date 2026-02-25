import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import Layout from '../components/Layout';
import ProductionForm from '../components/ProductionForm';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableRow, 
  Button, 
  Typography 
} from '@mui/material';

export default function Production() {
  const [productionList, setProductionList] = useState([]);
  const [associations, setAssociations] = useState([]);

  const fetchAssociations = async () => {
    const res = await axiosInstance.get('/product-materials');
    setAssociations(res.data);
  };

  useEffect(() => {
    fetchProduction();
    fetchAssociations();
  }, []);

  const fetchProduction = async () => {
    const res = await axiosInstance.get('/production/preview');
    setProductionList(res.data);
  };

  const executeProduction = async () => {
    await axiosInstance.post('/production');
    fetchProduction();
  };

  return (
    <Layout>

      <ProductionForm onSaved={fetchProduction} />

      <Typography variant="h5" sx={{ mt: 4, mb: 2 }}>
        Suggested Production
      </Typography>

      <Button 
        variant="contained" 
        onClick={fetchProduction} 
        sx={{ mr: 2 }}
      >
        Refresh
      </Button>

      <Button 
        variant="contained" 
        color="success"
        onClick={executeProduction}
      >
        Execute Production
      </Button>

      <Table sx={{ mt: 2 }}>
        <TableHead>
          <TableRow>
            <TableCell>Product Code</TableCell>
            <TableCell>Product</TableCell>
            <TableCell>Quantity</TableCell>
            <TableCell>Total Value</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {productionList.map(p => (
            <TableRow key={p.productId}>
              <TableCell>{p.productCode}</TableCell>
              <TableCell>{p.productName}</TableCell>
              <TableCell>{p.quantityToProduce}</TableCell>
              <TableCell>
                ${Number(p.totalValue).toFixed(2)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

    </Layout>
  );
}