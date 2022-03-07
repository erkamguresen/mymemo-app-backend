# [www.mymemo.app](https://www.mymemo.app) - Backend

This repo is the backend of [www.mymemo.app](https://www.mymemo.app).

You can find the api documentation below.

## Getting Started

This backend uses Spring Boot and MongoDB.

## Using The App

- **from postman**
  - `http://localhost:8080/api/v1` - the main entry point to the API
- **from the browser**
  - `http://localhost:8080/` - serves `/src/main/java/resources/static/index.html`, it also contains the API documentation

# The API Documentation

![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen)

The current version of the API is 1.0.0. Thus all the urls starts with `/api/v1`.

## Registration

Registers a new user to the system

- **URL**

`/api/v*/register`

- **Method**

  `POST`

- **Body**

  ```
  {
    "email" : <email>,
    "username": <username>,
    "password" : <password>,
    "firstName": <firstName>,
    "lastName": <lastName>,
    "birthday" : <birthday>,\\ epoch in ms
    "city": <city>,
  }
  ```

- **Result**

  ```
  {
    "username": <username>,
    "email" : <email>,
  }
  ```

## Login

logins an existing user to the system

- **URL**

/api/login

- **Method**

  `POST`

- **Body**

  ```
  {
    "email" : <email>,
    "password" : <password>,
  }
  ```

- **Result**

```
{
  "token": <token>,
  "email": <email>,
  "username": <username>,
  "message": "Session created for user <username>",
  "userId": <userId>
}
```

## Dataset Operations

### Getting Information About
