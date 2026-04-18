# Screenmatch
 
Catálogo de filmes e séries desenvolvido como projeto de aprendizado em Java com Spring Boot. Se trata de um sistema backend que oferece operações completa de CRUD para filmes e séries, integrando dados de uma API externa similar ao IMDb.
Os dados são persistidos em um banco PostgreSQL com modelagem relacional, e a API segue os princípios REST. A integração com uma API externa enriquece o catálogo com informações detalhadas sobre as produções.
 
---
 
## Tecnologias utilizadas
 
- **Java 17**
- **Spring Boot** — estrutura principal da aplicação
- **Spring Data JPA** — abstração do acesso ao banco de dados
- **PostgreSQL** — banco de dados relacional
- **Maven** — gerenciamento de dependências
- **API externa (OMDb / IMDb-style)** — integração para busca de dados de filmes e séries
---
 
## Conceitos praticados
 
- Arquitetura REST com Spring MVC
- Relacionamentos entre tabelas (modelagem relacional)
- Consumo de API externa via `RestTemplate` / `HttpClient`
- Configuração de ambiente com `application.properties`
- Separação em camadas: controller, service e repository
