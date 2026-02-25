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
    const res = await getRawMaterials();
    setMaterials(res.data);
  };

  useEffect(() => { fetchMaterials(); }, []);

  const columns = [
    { field: 'name', headerName: 'Name', flex: 1 },
    { field: 'quantity', headerName: 'Quantity', flex: 1 },
    {
      field: 'actions',
      headerName: 'Actions',
      renderCell: (params) => (
        <>
          <Button variant="outlined" size="small" onClick={() => setEditingMaterial(params.row)}>Edit</Button>
          <Button variant="outlined" size="small" color="error" onClick={async () => { await deleteRawMaterial(params.row.id); fetchMaterials(); }}>Delete</Button>
        </>
      )
    }
  ];

  return (
    <Layout>
      <RawMaterialForm onSaved={fetchMaterials} editingMaterial={editingMaterial} />
      <div style={{ height: 400, width: '100%', marginTop: 20 }}>
        <DataGrid rows={materials} columns={columns} getRowId={(row) => row.id} />
      </div>
    </Layout>
  );
}