import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Products from './pages/Products';
import RawMaterials from './pages/RawMaterials';
import Production from './pages/Production';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Products />} />
        <Route path="/raw-materials" element={<RawMaterials />} />
        <Route path="/production" element={<Production />} />
      </Routes>
    </Router>
  );
}