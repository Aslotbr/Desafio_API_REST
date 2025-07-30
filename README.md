
# 📦 Sistema de Processamento de Pedidos Logísticos

Este projeto é uma API REST desenvolvida com Spring Boot que permite o upload e processamento de arquivos `.txt` contendo dados de pedidos logísticos. A aplicação lê os dados em formato de largura fixa, organiza-os em estrutura JSON e disponibiliza endpoints para consulta filtrada.

## 🚀 Funcionalidades

- Upload de arquivos `.txt` via endpoint REST.
- Processamento de dados com base no layout de largura fixa.
- Estruturação dos dados em DTOs (Usuário, Pedido, Produto).
- Filtros por `orderId`, `startDate` e `endDate`.
- Retorno em JSON pronto para consumo por frontend ou outras APIs.

## 📁 Formato do Arquivo .txt

O arquivo deve conter as seguintes informações por linha, no seguinte padrão de largura fixa:

| Campo         | Largura | Descrição               |
|---------------|---------|-------------------------|
| userId        | 10      | ID do usuário           |
| userName      | 30      | Nome do usuário         |
| orderId       | 10      | ID do pedido            |
| productId     | 10      | ID do produto           |
| value         | 10      | Valor do produto        |
| orderDate     | 8       | Data do pedido (yyyyMMdd) |

🔹 Exemplo de linha no arquivo:
```
0000000070          Palmer Prosacco00000007530000000003     1836.7420210308
```

## 🧪 Como Testar

### 1. Clone o projeto

```bash
git clone https://github.com/seuusuario/logistica-projeto.git
cd logistica-projeto
```

### 2. Execute com Maven

```bash
./mvnw spring-boot:run
```

O servidor será iniciado em: `http://localhost:8080`

### 3. Teste com Postman ou Insomnia

#### 📤 POST /arquivo/upload

- Faz o upload do arquivo `.txt`.

Exemplo no Postman:
- Método: POST
- URL: http://localhost:8080/arquivo/upload
- Tipo: multipart/form-data
- Chave: file
- Valor: selecione seu arquivo .txt

Resposta esperada:
```
Arquivo processado com sucesso!
```

#### 📥 GET /arquivo/dados

- Retorna os dados processados com ou sem filtros.

Exemplo de requisição:
```http
GET http://localhost:8080/arquivo/dados?orderId=753&startDate=2021-01-01&endDate=2025-12-31
```

Resposta:
```json
[
  {
    "userId": 70,
    "name": "Palmer Prosacco",
    "orders": [
      {
        "orderId": 753,
        "date": "2021-03-08",
        "total": 1836.74,
        "products": [
          {
            "productId": 3,
            "value": 1836.74
          }
        ]
      }
    ]
  }
]
```

## 📦 Estrutura do Projeto

```bash
src
└── main
    ├── java
    │   └── com.apirest.logistica_projeto
    │       ├── controller
    │       │   └── FileUploadController.java
    │       ├── dto
    │       │   ├── UserOrdersDTO.java
    │       │   ├── OrderDTO.java
    │       │   └── ProductDTO.java
    │       └── service
    │           └── FileProcessorService.java
    └── resources
        └── application.properties
```

## 🛠️ Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.4
- Maven
- Postman (para testes)
- Git

## ✍️ Autor

Felipe Souza  
📧 felipesiqueira2209@gmail.com  
🔗 [LinkedIn](https://www.linkedin.com/in/felipe-souza-siqueira/)

---

<p align="center"><i>“A simplicidade é o último grau da sofisticação.” – Leonardo da Vinci</i></p>
