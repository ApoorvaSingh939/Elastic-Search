# Spring Boot Apache Camel ElasticSearch Project

This project utilizes Spring Boot, Apache Camel and ElasticSearch to perform the following tasks:

- Reading data from a CSV file
- Inserting it into Elasticsearch
- Created endpoints for retrieval, addition, and deletion of data

## Prerequisites

Before running the application, ensure you have the following dependencies installed:

- Java Development Kit (JDK)
- Apache Maven
- Elasticsearch instance

## Getting Started

Follow these steps to set up and run the project:

1. Clone the repository:

```bash
git clone https://github.com/ApoorvaSingh939/ElasticSearch
```

2. Navigate to the project directory:

```bash
cd Elasticsearch
```

3. Build the project using Maven:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

## Configuration

Ensure you have configured the application properties according to your environment. You can find the configuration file at `src/main/resources/application.properties`. Here, you need to specify the Elasticsearch connection details.

## Usage

### Uploading Data

1. Place your CSV file containing the data in the `inputFolder/products` directory.

2. The application will automatically read data from the CSV file and insert it into Elasticsearch.

### Endpoints

The following endpoints are available:

- **Retrieve Data**: 
  - URI: `"http://localhost:8081/api/product/getAll`
  - Method: GET
  - Description: Retrieves all data from Elasticsearch.

- **Add Data**:
  - URI: `https://localhost:8081/api/product/addOne/{id}`
  - Method: POST
  - Description: Adds new data to Elasticsearch. Provide JSON payload with the data to be added.

- **Add Doc**:
  - URI: `http://localhost:8081/api/product/addDoc`
  - Method: POST
  - Description: Adds new doc to Elasticsearch. Provide JSON payload with the data to be added.

- **Delete Data**:
  - URI: `http://localhost:8081/api/product/deleteByid/{id}`
  - Method: DELETE
  - Description: Deletes data with the specified ID from Elasticsearch.

## Contributing

Contributions are welcome! If you have any suggestions or find any issues, feel free to open an issue or create a pull request.

## Acknowledgements

- Thanks to the Apache Camel and Spring Boot communities for providing excellent frameworks for building integration solutions.
- Special thanks to Elasticsearch for their powerful search and analytics engine.
