
# Java API Test

Este é um projeto pessoal desenvolvido em **Spring Boot**, que implementa uma API para gerenciamento de usuários e tickets. O objetivo principal foi o aprimoramento de habilidades em desenvolvimento de APIs utilizando Java, bem como a exploração de conceitos avançados como deploy na AWS, uso de Docker e automação com GitHub Actions.

## 📚 Funcionalidades

### Gerenciamento de Usuários
- **Cadastrar Usuário**: Criação de um novo usuário no banco de dados com autenticação personalizada.
- **Inativar Usuário**: Marca o usuário como inativo, impedindo futuras ações. A inativação é permanente.
- **Renovar Credenciais**: Atualização das chaves de autenticação do usuário (Client ID e Client Secret).

### Gerenciamento de Tickets
- **Criar Ticket**: Registro de um chamado para um usuário específico.
  Rota: `POST /create/user/{userId}/`
- **Concluir Ticket**: Marca um ticket como concluído.
  Rota: `PATCH /complete/user/{userId}/ticket/{ticketId}`
- **Enviar Resumo por E-mail**: Envia ao usuário um resumo dos seus chamados.
  Rota: `POST /extract/email/user/{userId}`
- **Consultar Ticket Específico**: Recupera informações de um ticket específico.
  Rota: `GET /list_one/user/{userId}/ticket/{ticketId}`
- **Listar Todos os Tickets**: Retorna todos os tickets de um usuário.
  Rota: `GET /list_all/user/{userId}`

### Autenticação Personalizada
- Implementada sem bibliotecas nativas do Spring Boot.
- O cliente deve:
  1. Concatenar `Client ID` e `Client Secret`.
  2. Codificar o resultado em Base64.
  3. Enviar no header `Authorization`.

## 🛠️ Tecnologias Utilizadas
- **Spring Boot**: Framework para criação da API.
- **Maven**: Gerenciador de dependências.
- **Docker**: Criação de containers e orquestração com `docker-compose`.
- **AWS**: Deploy da aplicação.
- **GitHub Actions e Secrets**: Configuração de pipelines para CI/CD.

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Java JDK 23** (versões inferiores não são suportadas).
- Maven
- Docker (opcional para execução com container)

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/FelipeFranke5/java-api-test.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd java-api-test
   ```
3. Compile e execute o projeto com Maven:
   ```bash
   mvn spring-boot:run
   ```
4. Para executar com Docker, utilize:
   ```bash
   docker-compose up
   ```

## 🎯 Objetivo do Projeto
Este projeto foi desenvolvido com o intuito de:
- Aprender e implementar a estrutura de APIs RESTful com Java.
- Trabalhar com deploys na AWS.
- Criar e utilizar containers Docker.
- Configurar pipelines completas com GitHub Actions e Runners.

## 📄 Licença
Este projeto é de uso pessoal e não possui uma licença específica.