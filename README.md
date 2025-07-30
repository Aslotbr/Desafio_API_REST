
# 📦 API de Processamento de Pedidos - Spring Boot

Este projeto é uma API RESTful construída com Spring Boot que processa arquivos `.txt` contendo informações de usuários, pedidos e produtos. Ela transforma os dados em uma estrutura JSON e permite consultas com filtros.

---

## 🔧 Funcionalidades

- Upload de múltiplos arquivos `.txt`
- Processamento de dados por linha usando expressões regulares
- Conversão de datas e valores monetários (com suporte a vírgula ou ponto)
- Consultas de pedidos com filtros opcionais:
  - ID do pedido (`orderId`)
  - Intervalo de datas (`startDate`, `endDate`)

---

## 📥 Upload de Arquivos

```
POST /arquivo/upload
Content-Type: multipart/form-data
```

Parâmetro:
- `files`: array de arquivos `.txt`

Exemplo com Postman:
- Selecione `POST`, vá até a aba `Body`, marque `form-data`
- Use o nome do campo como `files` e adicione um ou mais arquivos `.txt`

---

## 🔍 Consulta de Pedidos com Filtros

```
GET /arquivo/dados
```

Parâmetros (todos opcionais):
- `orderId`: número do pedido
- `startDate`: data inicial no formato `yyyy-MM-dd`
- `endDate`: data final no formato `yyyy-MM-dd`

Exemplos:
- `/arquivo/dados`: retorna todos os dados
- `/arquivo/dados?orderId=123`: retorna dados do pedido 123
- `/arquivo/dados?startDate=2021-12-01&endDate=2021-12-31`: retorna pedidos feitos em dezembro de 2021

---

## 🧾 Exemplo de Estrutura de Resposta JSON

```json
[
  {
    "user_id": 1,
    "name": "Zarelli",
    "orders": [
      {
        "order_id": 123,
        "date": "2021-12-01",
        "total": "1024.48",
        "products": [
          {
            "product_id": 111,
            "value": "512.24"
          },
          {
            "product_id": 122,
            "value": "512.24"
          }
        ]
      }
    ]
  }
]
```

---

## ▶️ Como Rodar

```bash
./mvnw spring-boot:run
```

Ou:

```bash
mvn clean install
java -jar target/logistica-projeto-0.0.1-SNAPSHOT.jar
```

---

## ✅ Requisitos

- Java 17+
- Maven 3.8+

---

## 📁 Estrutura Esperada do Arquivo `.txt`

Cada linha deve seguir o formato:

```
[10 dígitos usuário][nome até 45 chars][10 dígitos pedido][10 dígitos produto][valor decimal][data no formato yyyyMMdd]
```

---

Se quiser, posso gerar um exemplo de arquivo `.txt` válido para testes também.
