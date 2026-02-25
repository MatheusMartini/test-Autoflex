import React, { useEffect, useState } from 'react';
import { getProducts, deleteProduct } from '../api/productApi';
import ProductForm from '../components/ProductForm';
import Layout from '../components/Layout';
import { DataGrid } from '@mui/x-data-grid';
import { Button } from '@mui/material';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [editingProduct, setEditingProduct] = useState(null);

  const fetchProducts = async () => {
    const res = await getProducts();
    setProducts(res.data);
  };

  useEffect(() => { fetchProducts(); }, []);

  const columns = [
    { field: 'name', headerName: 'Name', flex: 1 },
    { field: 'value', headerName: 'Value', flex: 1 },
    {
      field: 'actions',
      headerName: 'Actions',
      renderCell: (params) => (
        <>
          <Button variant="outlined" size="small" onClick={() => setEditingProduct(params.row)}>Edit</Button>
          <Button variant="outlined" size="small" color="error" onClick={async () => { await deleteProduct(params.row.id); fetchProducts(); }}>Delete</Button>
        </>
      )
    }
  ];

  return (
    <Layout>
      <ProductForm onSaved={fetchProducts} editingProduct={editingProduct} />
      <div style={{ height: 400, width: '100%', marginTop: 20 }}>
        <DataGrid rows={products} columns={columns} getRowId={(row) => row.id} />
      </div>
    </Layout>
  );
}