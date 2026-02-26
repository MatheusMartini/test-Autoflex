import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Layout from '../components/Layout';

describe('Layout', () => {
  it('should render application title', () => {
    render(
      <MemoryRouter>
        <Layout>
          <div>Page Content</div>
        </Layout>
      </MemoryRouter>,
    );

    expect(screen.getByText('Autoflex Inventory')).toBeInTheDocument();
  });

  it('should render navigation links', () => {
    render(
      <MemoryRouter>
        <Layout>
          <div>Page Content</div>
        </Layout>
      </MemoryRouter>,
    );

    expect(screen.getByRole('link', { name: 'Products' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Raw Materials' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Production' })).toBeInTheDocument();
  });

  it('should render children content', () => {
    render(
      <MemoryRouter>
        <Layout>
          <div>Children content rendered</div>
        </Layout>
      </MemoryRouter>,
    );

    expect(screen.getByText('Children content rendered')).toBeInTheDocument();
  });
});
