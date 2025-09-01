# 📘 CourseSearch - Assignment A
A Spring Boot application for searching courses using Elasticsearch as the backend search engine.
This project demonstrates how to:
* Run Elasticsearch inside Docker
* Index sample course data on startup
* Perform advanced search queries with filters, pagination, and sorting

## 🚀 Features
* Full-text search on course and description
* Filters:
    * Range filters -> `minAge`, `maxAge`, `minPrice`, `maxPrice`
    * Exact filters -> `category`, `type`
    * Date filter   -> `nextSessionDate`
* Sorting:
    * By `nextSessionDate` (upcoming courses)
    * By `price` (with increasing/ decreasing)
* Pagination: default `page=0`, `size=10`

## 🛠️ Tech Stack
* Spring Boot (Java 17 version)
* Spring Data Elasticsearch
* Elasticsearch (Docker container)
* Maven for build and dependency management

## ⚙️ Setup Instructions
### 1. Install Docker
  * Download & install Docker Desktop
  * Verify installation:
  * 
    ```
    docker --version
    docker-compose --version
    ```
### 2. Start Elasticsearch with Docker Compose
Create a `docker-compose.yml` file in root of project folder with below content

```
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.15.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

volumes:
  es_data:
```
Then open either cmd or docker desktop terminal and run the below code

```
docker-compose up -d
```
Run `docker ps`. It will show Elasticsearch running instance and port number (9200).
To ensure if Elasticsearch is running or not run below request on the browser
```
http://localhost:9200
```

### 3. Spring Boot Configuration
Add Elasticsearch configs in `application.yml`:
```
spring:
  application:
    name: CourseSearch

  data:
    elasticsearch:
      cluster-nodes: localhost:9200
      repositories:
        enabled: true
```
