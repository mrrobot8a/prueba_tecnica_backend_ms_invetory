Sistema de Gestión de Inventario - Microservicio Spring Boot
📌 Descripción General
Microservicio RESTful para la gestión de inventarios que permite:

Registrar productos en el inventario

Actualizar existencias

Procesar compras

Consultar stock bajo

Integración con servicio de productos

⚙️ Requisitos Técnicos
Componente	Versión
Java	17+
Maven	3.9+
Spring Boot	3.2.0
PostgreSQL	14+
Docker	20.10+
🚀 Despliegue Rápido
1. Configuración Inicial
bash
# Clonar repositorio
git clone https://github.com/tu-repositorio/inventario-ms.git
cd inventario-ms
2. Configuración de Base de Datos
bash
# Crear base de datos (ejecutar en PostgreSQL)
CREATE DATABASE inventario_db;
CREATE USER inventario_user WITH PASSWORD 'dbpassword';
GRANT ALL PRIVILEGES ON DATABASE inventario_db TO inventario_user;
3. Ejecución con Docker
bash
docker network create inventario-network (Solo si no existe)
# Construir imagen
docker build -t ms_inventory .

# Ejecutar contenedor
docker run   --name msinventario   --network inventario-network   -p 8081:8081   -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/inventario_db"   -e SPRING_DATASOURCE_USERNAME="postgres"   -e SPRING_DATASOURCE_PASSWORD="123456"   -e APP_SECURITY_API_KEY="inventario-secreto-789"   -e PRODUCT_SERVICE_URL="http://msproduct:8080"   ms_inventory

🔍 Endpoints Principales
Método	Endpoint	Descripción
POST	/api/v1/inventory	Crear nuevo ítem en inventario
PUT	/api/v1/inventory	Actualizar inventario
GET	/api/v1/inventory/{productId}	Consultar por ID de producto
POST	/api/v1/inventory/process-purchase	Procesar compra
GET	/api/v1/inventory/low-stock/{threshold}	Productos con stock bajo
🔌 Configuración de Conexiones
properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://postgres-db:5432/inventario_db
spring.datasource.username=inventario_user
spring.datasource.password=dbpassword

product.service.url=http://product-service:8080
product.service.api-key=tu-api-key-secreta
🧪 Pruebas
bash
# Ejecutar pruebas unitarias
mvn test

# Generar reporte de cobertura
mvn jacoco:report