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
              await deleteProduct(params.row.id);
              fetchProducts();
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
    <ProductForm
      onSaved={() => {
        fetchProducts();
        setEditingProduct(null); 
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