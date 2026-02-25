import React, { useEffect, useState } from 'react';
import { getRawMaterials, deleteRawMaterial } from '../api/rawMaterialApi';
import RawMaterialForm from '../components/RawMaterialForm';
import Layout from '../components/Layout';
import { DataGrid } from '@mui/x-data-grid';
import { Button } from '@mui/material';

export default function RawMaterials() {
  const [materials, setMaterials] = useState([]);
  const [editingMaterial, setEditingMaterial] = useState(null);

  const fetchMaterials = async () => {
    try {
      const res = await getRawMaterials();
      setMaterials(res.data);
    } catch (error) {
      console.error("Erro ao buscar materiais:", error);
    }
  };

  useEffect(() => {
    fetchMaterials();
  }, []);

  const columns = [
    { field: 'id', headerName: 'ID', width: 80 },
    { field: 'code', headerName: 'Code', flex: 1 },
    { field: 'name', headerName: 'Name', flex: 1 },
    { field: 'stockQuantity', headerName: 'Stock Quantity', flex: 1 },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      renderCell: (params) => (
        <>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setEditingMaterial(params.row)}
            style={{ marginRight: 8 }}
          >
            Edit
          </Button>

          <Button
            variant="outlined"
            size="small"
            color="error"
            onClick={async () => {
              await deleteRawMaterial(params.row.id);
              fetchMaterials();
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
      <RawMaterialForm
        onSaved={() => {
          fetchMaterials();
          setEditingMaterial(null);
        }}
        editingMaterial={editingMaterial}
      />
      <div style={{ height: 400, width: '100%', marginTop: 20 }}>
        <DataGrid
          rows={materials}
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