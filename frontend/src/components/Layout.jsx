import React from 'react';
import { AppBar, Toolbar, Typography, Button, Container } from '@mui/material';
import { Link } from 'react-router-dom';

export default function Layout({ children }) {
  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Autoflex Inventory
          </Typography>
          <Button color="inherit" component={Link} to="/">Products</Button>
          <Button color="inherit" component={Link} to="/raw-materials">Raw Materials</Button>
          <Button color="inherit" component={Link} to="/production">Production</Button>
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        {children}
      </Container>
    </>
  );
}