#   EquiTrack

A web application to track personal income and expenses built with Spring Boot and React.

kadaklae
## Features

- Add income and expenses
- View transaction history
- Budget tracking
- Simple dashboard with charts

## Technology Stack

**Backend:** Spring Boot, MySQL, Spring Security  
**Frontend:** React, Vite, Tailwind CSS

## Getting Started

### Prerequisites

- Java 21+
- Node.js 16+
- MySQL

### Installation

1. **Clone the project**
```bash
git clone https://github.com/Desiigner101/EQUITRACK-BACKEND.git
```

2. **Setup Backend**
```bash
cd backend
mvn spring-boot:run
```
Runs on http://localhost:8080

3. **Setup Frontend**
```bash
cd frontend
npm install
npm run dev
```
Runs on http://localhost:5173

4. **Database Setup**
```sql
CREATE DATABASE equitrack_db;
```
Configure database connection in `application.properties`

## Usage

1. Register/Login to your account
2. Add your income and expenses
3. View your spending patterns on the dashboard
4. Set and track budgets
