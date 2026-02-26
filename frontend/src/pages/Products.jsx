import React, { useEffect, useState } from 'react';
import { getProducts, deleteProduct } from '../api/productApi';
import ProductForm from '../components/ProductForm';
import Layout from '../components/Layout';
import { DataGrid } from '@mui/x-data-grid';
import { Alert, Button } from '@mui/material';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [editingProduct, setEditingProduct] = useState(null);
  const [error, setError] = useState('');

  const fetchProducts = async () => {
    try {
      const res = await getProducts();
      setProducts(res.data);
    } catch (e) {
      setError(e?.response?.data?.message || 'Failed to load products.');
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const columns = [
    { field: 'id', headerName: 'ID', width: 80 },
    { field: 'code', headerName: 'Code', flex: 1 },
    { field: 'name', headerName: 'Name', flex: 1 },
    { field: 'price', headerName: 'Price', flex: 1 },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      renderCell: (params) => (
        <>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setEditingProduct(params.row)}
            style={{ marginRight: 8 }}
          >
            Edit
          </Button>

          <Button
            variant="outlined"
            size="small"
            color="error"
            onClick={async () => {
              setError('');
              try {
                await deleteProduct(params.row.id);
                fetchProducts();
              } catch (e) {
                setError(e?.response?.data?.message || 'Failed to delete product.');
              }
            }}
          >
            Delete
          </Button>
        </>
      )
    }
  ];

  return (
    <Layout>
    {error && (
      <Alert severity="error" sx={{ mb: 2 }}>
        {error}
      </Alert>
    )}
    <ProductForm
      onSaved={() => {
        fetchProducts();
        setEditingProduct(null);
        setError('');
      }}
      editingProduct={editingProduct}
      />

      <div style={{ height: 400, width: '100%', marginTop: 20 }}>
        <DataGrid
          rows={products}
          columns={columns}
          getRowId={(row) => row.id} 
          pageSizeOptions={[5, 10]}
          initialState={{
            pagination: { paginationModel: { pageSize: 5, page: 0 } }
          }}
        />
      </div>
    </Layout>
  );
}