## 1️⃣ Install Docker

Before starting, make sure Docker is installed on your machine:

- **macOS**: [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)
- **Windows**: [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
- **Linux**: [Docker Engine Install Guide](https://docs.docker.com/engine/install/)

Verify Docker is working:

```bash
docker --version
docker compose version
```

---

## 2️⃣ Start the Project

The project root includes a `Dockerfile` and `docker-compose.yml`.  
Use Docker Compose to build and start all services.

### 2.1 Build and start

```bash
docker compose up --build
```

- **`--build`** forces a rebuild of the images.
- Logs are printed directly in the terminal.

### 2.2 Start in background (detached mode)

```bash
docker compose up -d
```

### 2.3 Check container status

```bash
docker compose ps
```

You should see:

- **`springboot-app`** → Spring Boot server
- **`springboot-mysql`** → MySQL server

### 2.4 View logs

```bash
docker compose logs -f
```

- **`-f`** allows live log following.

### 2.5 Stop the project

```bash
docker compose down
```

To remove associated volumes (e.g., database data):

```bash
docker compose down -v
```

---

## 3️⃣ Test the `/hello` API

1. Open your browser and visit:

   `http://localhost:8080/hello`

2. You should see:

   `Hello Spring Boot!`

3. Or test from the terminal:

```bash
curl http://localhost:8080/hello
```

