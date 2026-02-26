import React, { useEffect, useMemo, useState } from 'react';
import Layout from '../components/Layout';
import ProductionForm from '../components/ProductionForm';
import {
  previewProduction,
  executeProduction,
  listProductionRuns,
  getProductionRun
} from '../api/productionApi';
import {
  listProductMaterials,
  deleteProductMaterial
} from '../api/productMaterialApi';

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Stack,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from '@mui/material';

export default function Production() {
  const [productionList, setProductionList] = useState([]);
  const [grandTotal, setGrandTotal] = useState(0);

  const [associations, setAssociations] = useState([]);
  const [editingAssociation, setEditingAssociation] = useState(null);

  const [runs, setRuns] = useState([]);
  const [runDetails, setRunDetails] = useState(null);
  const [openRunDialog, setOpenRunDialog] = useState(false);

  const [loadingPreview, setLoadingPreview] = useState(false);
  const [loadingExecute, setLoadingExecute] = useState(false);
  const [loadingRuns, setLoadingRuns] = useState(false);
  const [loadingRunDetails, setLoadingRunDetails] = useState(false);

  const [error, setError] = useState('');

  const currency = useMemo(
    () => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
    []
  );

  const formatDateTime = (iso) => {
    if (!iso) return '-';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
  };

  const normalizePreview = (payload) => {
    // supports:
    // { items, grandTotal }
    // array
    const items =
      Array.isArray(payload) ? payload :
      Array.isArray(payload?.items) ? payload.items :
      Array.isArray(payload?.data) ? payload.data :
      [];

    const total =
      payload?.grandTotal ??
      items.reduce((sum, it) => sum + (Number(it.totalValue) || 0), 0);

    return { items, total };
  };

  const fetchAssociations = async () => {
    const res = await listProductMaterials();
    setAssociations(Array.isArray(res.data) ? res.data : []);
  };

  const handleDeleteAssociation = async (associationId) => {
    if (!associationId) return;
    if (!window.confirm('Delete this association?')) return;

    setError('');
    try {
      await deleteProductMaterial(associationId);
      await fetchAssociations();
      await fetchProduction();
      if (editingAssociation?.id === associationId) {
        setEditingAssociation(null);
      }
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to delete association.');
    }
  };

  const fetchProduction = async () => {
    setLoadingPreview(true);
    setError('');
    try {
      const res = await previewProduction();
      const { items, total } = normalizePreview(res.data);
      setProductionList(items);
      setGrandTotal(total);
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to load production preview.');
      setProductionList([]);
      setGrandTotal(0);
    } finally {
      setLoadingPreview(false);
    }
  };

  const fetchRuns = async () => {
    setLoadingRuns(true);
    setError('');
    try {
      const res = await listProductionRuns();
      setRuns(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to load production history.');
      setRuns([]);
    } finally {
      setLoadingRuns(false);
    }
  };

  const handleExecute = async () => {
    setLoadingExecute(true);
    setError('');
    try {
      await executeProduction();
      await fetchProduction();
      await fetchAssociations(); // optional, but keeps screen synced
      await fetchRuns();
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to execute production.');
    } finally {
      setLoadingExecute(false);
    }
  };

  const openRun = async (id) => {
    setLoadingRunDetails(true);
    setError('');
    try {
      const res = await getProductionRun(id);
      setRunDetails(res.data);
      setOpenRunDialog(true);
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to load run details.');
    } finally {
      setLoadingRunDetails(false);
    }
  };

  const closeRunDialog = () => {
    setOpenRunDialog(false);
    setRunDetails(null);
  };

  useEffect(() => {
    fetchProduction();
    fetchAssociations();
    fetchRuns();
  }, []);

  // Helpers to display association fields no matter the backend shape
  const assocProductLabel = (a) =>
    a?.product?.code
      ? `${a.product.code} - ${a.product.name ?? ''}`.trim()
      : a?.productCode
        ? `${a.productCode} - ${a.productName ?? ''}`.trim()
        : a?.productId ?? '-';

  const assocRawMaterialLabel = (a) =>
    a?.rawMaterial?.code
      ? `${a.rawMaterial.code} - ${a.rawMaterial.name ?? ''}`.trim()
      : a?.rawMaterialCode
        ? `${a.rawMaterialCode} - ${a.rawMaterialName ?? ''}`.trim()
        : a?.rawMaterialId ?? '-';

  const assocRequiredQty = (a) =>
    a?.requiredQuantity ?? a?.quantity ?? a?.required ?? '-';

  return (
    <Layout>
      {/* Association form */}
      <ProductionForm
        onSaved={() => {
          fetchAssociations();
          fetchProduction();
          setEditingAssociation(null);
        }}
        editingAssociation={editingAssociation}
        onCancelEdit={() => setEditingAssociation(null)}
      />

      {/* Associations list */}
      <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
        Current Associations
      </Typography>

      <Table sx={{ mt: 1 }}>
        <TableHead>
          <TableRow>
            <TableCell>Product</TableCell>
            <TableCell>Raw Material</TableCell>
            <TableCell align="right">Required Quantity</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {associations.map((a) => (
            <TableRow key={a.id ?? `${a.productId}-${a.rawMaterialId}`}>
              <TableCell>{assocProductLabel(a)}</TableCell>
              <TableCell>{assocRawMaterialLabel(a)}</TableCell>
              <TableCell align="right">{assocRequiredQty(a)}</TableCell>
              <TableCell align="right">
                <Button
                  size="small"
                  variant="outlined"
                  sx={{ mr: 1 }}
                  onClick={() => setEditingAssociation(a)}
                >
                  Edit
                </Button>
                <Button
                  size="small"
                  variant="outlined"
                  color="error"
                  onClick={() => handleDeleteAssociation(a.id)}
                >
                  Delete
                </Button>
              </TableCell>
            </TableRow>
          ))}

          {associations.length === 0 && (
            <TableRow>
              <TableCell colSpan={4}>No associations found.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      {/* Suggested production */}
      <Typography variant="h5" sx={{ mt: 5, mb: 2 }}>
        Suggested Production
      </Typography>

      <Stack direction="row" spacing={2} sx={{ mb: 2, alignItems: 'center' }}>
        <Button variant="contained" onClick={fetchProduction} disabled={loadingPreview || loadingExecute}>
          Refresh
        </Button>

        <Button
          variant="contained"
          color="success"
          onClick={handleExecute}
          disabled={
            loadingExecute ||
            loadingPreview ||
            productionList.length === 0
          }
        >
          Execute Production
        </Button>

        {(loadingPreview || loadingExecute) && <CircularProgress size={22} />}
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Table sx={{ mt: 1 }}>
        <TableHead>
          <TableRow>
            <TableCell>Product Code</TableCell>
            <TableCell>Product</TableCell>
            <TableCell align="right">Quantity</TableCell>
            <TableCell align="right">Total Value</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {productionList.map((p) => {
            const qty = p.maxQuantity ?? p.quantityToProduce ?? 0;
            const total = Number(p.totalValue) || 0;

            return (
              <TableRow key={p.productId ?? `${p.productCode}-${p.productName}`}>
                <TableCell>{p.productCode}</TableCell>
                <TableCell>{p.productName}</TableCell>
                <TableCell align="right">{qty}</TableCell>
                <TableCell align="right">{currency.format(total)}</TableCell>
              </TableRow>
            );
          })}

          {productionList.length === 0 && (
            <TableRow>
              <TableCell colSpan={4}>No producible products found.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
        <Typography variant="h6">
          {grandTotal ? `Grand Total: ${currency.format(Number(grandTotal) || 0)}` : ''}
        </Typography>
      </Box>

      {/* Production history */}
      <Typography variant="h5" sx={{ mt: 6, mb: 2 }}>
        Production History
      </Typography>

      <Stack direction="row" spacing={2} sx={{ mb: 2, alignItems: 'center' }}>
        <Button variant="contained" onClick={fetchRuns} disabled={loadingRuns}>
          Refresh History
        </Button>
        {loadingRuns && <CircularProgress size={22} />}
      </Stack>

      <Table sx={{ mt: 1 }}>
        <TableHead>
          <TableRow>
            <TableCell>Run ID</TableCell>
            <TableCell>Executed At</TableCell>
            <TableCell align="right">Items</TableCell>
            <TableCell align="right">Grand Total</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {runs.map((r) => (
            <TableRow key={r.id}>
              <TableCell>{r.id}</TableCell>
              <TableCell>{formatDateTime(r.executedAt)}</TableCell>
              <TableCell align="right">{r.itemsCount ?? r.items?.length ?? '-'}</TableCell>
              <TableCell align="right">{currency.format(Number(r.grandTotal) || 0)}</TableCell>
              <TableCell align="right">
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => openRun(r.id)}
                  disabled={loadingRunDetails}
                >
                  View
                </Button>
              </TableCell>
            </TableRow>
          ))}

          {runs.length === 0 && (
            <TableRow>
              <TableCell colSpan={5}>No production runs found.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      {/* Run details dialog */}
      <Dialog open={openRunDialog} onClose={closeRunDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          Production Run Details {runDetails?.id ? `#${runDetails.id}` : ''}
        </DialogTitle>

        <DialogContent dividers>
          <Typography sx={{ mb: 1 }}>
            Executed At: {formatDateTime(runDetails?.executedAt)}
          </Typography>
          <Typography sx={{ mb: 2 }}>
            Grand Total: {currency.format(Number(runDetails?.grandTotal) || 0)}
          </Typography>

          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Product Code</TableCell>
                <TableCell>Product</TableCell>
                <TableCell align="right">Quantity</TableCell>
                <TableCell align="right">Total Value</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(runDetails?.items ?? []).map((it) => (
                <TableRow key={it.productId ?? `${it.productCode}-${it.productName}`}>
                  <TableCell>{it.productCode}</TableCell>
                  <TableCell>{it.productName}</TableCell>
                  <TableCell align="right">{it.maxQuantity ?? it.quantity ?? 0}</TableCell>
                  <TableCell align="right">
                    {currency.format(Number(it.totalValue) || 0)}
                  </TableCell>
                </TableRow>
              ))}

              {(runDetails?.items ?? []).length === 0 && (
                <TableRow>
                  <TableCell colSpan={4}>No items in this run.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </DialogContent>

        <DialogActions>
          <Button onClick={closeRunDialog}>Close</Button>
        </DialogActions>
      </Dialog>
    </Layout>
  );
}
