# 📦 Inventory & Production Control System

## 📖 About the Project

**Inventory & Production Control System** is a web-based application designed to manage products and raw materials used in manufacturing processes.

The system allows:

- CRUD operations for products  
- CRUD operations for raw materials  
- Association between products and required raw materials  
- Intelligent production suggestion based on available stock  
- Calculation of total production value  
- Priority suggestion by highest product value  

This project was developed following a clean architecture approach with separated front-end and back-end layers.

---

## 🏗️ Architecture

The application follows a REST API architecture:

```
Frontend (React)
        ↓
Backend API (Quarkus)
        ↓
Service Layer
        ↓
Repository Layer (JPA/Hibernate)
        ↓
PostgreSQL
```

---

## 🚀 Technologies Used

### Backend
- Quarkus  
- Java 17+  
- JPA / Hibernate  
- RESTEasy  
- Maven  

### Frontend
- React  
- Redux Toolkit  
- Axios  
- Responsive layout (CSS / MUI / Tailwind)  

### Database
- PostgreSQL  

### Testing
- JUnit  
- Mockito  
- Jest  
- React Testing Library  
- Cypress  

---

## 🧠 Business Rule – Production Suggestion

The system calculates which products can be manufactured based on available raw materials.

For each product:

1. Retrieve associated raw materials.  
2. Calculate:

```
possibleQuantity = stockQuantity / requiredQuantity
```

The maximum producible quantity is the **minimum possible quantity** among all materials.

- Products are prioritized by highest price.  
- Total production value is calculated.  

---

## 🗄️ Database Model

### Product
- id  
- code  
- name  
- price  

### RawMaterial
- id  
- code  
- name  
- stockQuantity  

### ProductMaterial
- id  
- product_id  
- raw_material_id  
- requiredQuantity  

---

## 📡 API Endpoints

### Product

```
GET     /products
GET     /products/{id}
POST    /products
PUT     /products/{id}
DELETE  /products/{id}
```

### Raw Material

```
GET     /raw-materials
POST    /raw-materials
PUT     /raw-materials/{id}
DELETE  /raw-materials/{id}
```

### Association

```
POST    /products/{id}/materials
PUT     /products/{id}/materials/{materialId}
DELETE  /products/{id}/materials/{materialId}
```

### Production Suggestion

```
GET     /production/suggestion
```

---

## ▶️ How to Run the Project

### Prerequisites

- Java 17+  
- Maven  
- Node.js 18+  
- PostgreSQL  

---

### Backend

```
cd backend
./mvnw quarkus:dev
```

API available at:

```
http://localhost:8080
```

---

### Frontend

```
cd frontend
npm install
npm start
```

Application available at:

```
http://localhost:3000
```

---

## 🧪 Running Tests

### Backend

```
./mvnw test
```

### Frontend

```
npm test
```

### E2E

```
npx cypress open
```

---

## 📦 Project Structure

### Backend

```
controller
service
repository
entity
dto
exception
```

### Frontend

```
pages
components
services
store
hooks
```

---

## 🎯 Design Principles

- Separation of concerns  
- Clean Architecture  
- RESTful API  
- SOLID principles  
- Testable services  
- Responsive UI  

---

## 📌 Author

Developed by **Matheus Martini**  
Full-stack Developer