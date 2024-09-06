# Image Storage Webservice API
To-Do:
1. Add DOCKERFILE
2. Deployment Plan on aws EKS
3. Automate depoyment on aws using Teraform

### Overview

This project is a web service created using **Spring Boot** that simplifies the process of storing and retrieving images. Developers often spend a lot of time configuring object storage for their images, but this service eliminates the need for extensive configuration. By simply integrating our API, users can upload images directly, which are stored in **AWS S3**, and the service returns a **public URL** that can be used in any web or mobile application.

The service also uses **AWS DynamoDB** to store user data and image URLs, providing a robust and scalable backend for authentication and image metadata.

---

### Key Features

- **User Authentication**: Register and login functionality, generating a token for subsequent API access.
- **Image Upload**: Post images using a simple API call, and receive a public URL in response.
- **Image Retrieval**: Fetch all or individual image URLs linked to the user, ensuring quick integration with your projects.
- **AWS Integration**: Utilizes AWS S3 for object storage and AWS DynamoDB for storing user data and image URLs.

---

### Technologies Used

- **Spring Boot**: Backend service framework.
- **AWS S3**: Stores the uploaded images and returns a public URL.
- **AWS DynamoDB**: NoSQL database for storing user data and image metadata.
---

### API Endpoints

Below is a brief description of the available API endpoints:

#### 1. **User Registration**

   - **POST** `/api/auth/register`
   - Registers a new user.
   - **Request Body**:
     ```json
     {
       "email": "yourEmail",
       "password": "yourPassword"
     }
     ```

#### 2. **User Login**

   - **POST** `/api/auth/login`
   - Authenticates a user and returns a token.
   - **Request Body**:
     ```json
     {
       "email": "yourEmail",
       "password": "yourPassword"
     }
     ```

#### 3. **Upload Image**

   - **POST** `/api/image/upload`
   - Upload an image to S3 and store metadata in DynamoDB.
   - **Headers**:
     - `Authorization: Bearer <JWT_TOKEN>`
   - **Form Data**:
     - `image`: (file to upload)
     - `email`: `yourEmail`

#### 4. **Fetch All Images**

   - **GET** `/api/image/get/all`
   - Fetch all uploaded images with public URLs linked to the authenticated user.
   - **Headers**:
     - `Authorization: Bearer <JWT_TOKEN>`
   - **Form Data**:
     - `email`: `yourEmail` 
   

#### 5. **Fetch a Single Image**

   - **GET** `/api/image/get/one`
   - Fetch a specific image by its `imageName`.
   - **Headers**:
     - `Authorization: Bearer <JWT_TOKEN>`
   - **Form Data**:
     - `email`: `yourEmail`
     - `imageName`: `imageName`
  

---

### Prerequisites

- **Java 17** (or later)
- **AWS Account** with S3 and DynamoDB configured
- **Maven** for building the project

---

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/image-storage-webservice.git
   cd image-storage-webservice
2. **Configure AWS Credentials**
   Set your AWS credentials in .aws/credentials file in C:\Users\yourUser

3. **Run the Project**

   ```bash
   mvn spring-boot:run

4. **Access the API The web service will be available at http://localhost:7002**

---

### Future Enhancements
1. Support for other object types (e.g., Videos).
2. Authentication using OAuth or other authentication provider
3. Deployment Plan

