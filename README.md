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

## 🚀 Technologies Used

### Backend
- Quarkus (Java 17)
- JPA / Hibernate  
- REST API
- Maven  

### Frontend
- React + Vite
- Redux Toolkit
- Axios

### Database
- PostgreSQL (Docker)

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

# ▶️ How to Run the Project

## ✅ Prerequisites

- Java 17+
- Node.js 18+
- Docker

---

## 1️⃣ Start the Database

From the project root:

```bash
docker compose up -d
```

This will start PostgreSQL with:

- Database: `mydb`
- User: `postgres`
- Password: `123456`
- Port: `5432`

---

## 2️⃣ Run Backend (Quarkus)

```bash
cd backend
./mvnw quarkus:dev
```

Backend available at:

```
http://localhost:8080
```

Swagger UI

```
http://localhost:8080/q/swagger-ui
```

---

## 3️⃣ Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend available at:

```
http://localhost:5173
```

---

# 📡 Main API Endpoints

### Products
```
GET     /products
POST    /products
PUT     /products/{id}
DELETE  /products/{id}
```

### Raw Materials
```
GET     /raw-materials
POST    /raw-materials
PUT     /raw-materials/{id}
DELETE  /raw-materials/{id}
```

### Production Suggestion
```
GET /production/suggestion
```

---

# 🧠 Business Logic

For each product, the system calculates the maximum possible production based on available raw material stock:

```
possibleQuantity = stockQuantity / requiredQuantity
```

The final producible quantity is the minimum value among all required materials.

Products are prioritized by highest price.

---

# 🧪 Running Tests

Backend:

```bash
./mvnw test
```

Frontend:

```bash
npm test
```

---

# 👨‍💻 Author

Matheus Martini  
Full-stack Developer