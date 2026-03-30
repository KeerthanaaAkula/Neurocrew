# NeuroCrew Backend API Testing Guide

## New Features Added

### 1. Resume Upload Section

#### Upload Resume
```
POST /api/profile/me/resume
Content-Type: multipart/form-data

Body: file (PDF, max 5MB)
```

#### Download Resume
```
GET /api/profile/{userId}/resume
```

#### Delete Resume
```
DELETE /api/profile/me/resume
```

### 2. Contact Details Section

#### Get Your Contact Details
```
GET /api/profile/me/contact-details
Authorization: Bearer {jwt_token}
```

#### Update Contact Details
```
PUT /api/profile/me/contact-details
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "email": "user@example.com",
  "phone": "+1234567890",
  "linkedin": "https://linkedin.com/in/username",
  "github": "https://github.com/username",
  "website": "https://yourwebsite.com",
  "contactVisible": true
}
```

#### Get Other User's Contact Details
```
GET /api/profile/{userId}/contact-details
```
Note: Email and phone only returned if user has contactVisible=true

## Testing Steps

### 1. Start the Application
```bash
# Using the rebuild script
rebuild.bat

# Or manually:
mvn clean package -DskipTests
java -jar target/neurocrew-backend-1.0.0.jar
```

### 2. Test Authentication First
```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Test Contact Details
```bash
# Update contact details
curl -X PUT http://localhost:8080/api/profile/me/contact-details \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "email": "test@example.com",
    "phone": "+1234567890",
    "linkedin": "https://linkedin.com/in/testuser",
    "github": "https://github.com/testuser",
    "website": "https://testuser.com",
    "contactVisible": true
  }'

# Get your contact details
curl -X GET http://localhost:8080/api/profile/me/contact-details \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Test Resume Upload
```bash
# Upload a PDF resume
curl -X POST http://localhost:8080/api/profile/me/resume \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/resume.pdf"
```

### 5. View Full Profile
```bash
# Get your complete profile with resume info
curl -X GET http://localhost:8080/api/profile/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Available Endpoints Summary

### Profile Endpoints
- `GET /api/profile/me` - Get your full profile
- `GET /api/profile/{userId}` - Get user's public profile
- `PUT /api/profile/me` - Update general profile info
- `GET /api/profile/me/contact-details` - Get your contact details
- `PUT /api/profile/me/contact-details` - Update contact details
- `GET /api/profile/{userId}/contact-details` - Get user's contact details

### Resume Endpoints
- `POST /api/profile/me/resume` - Upload resume
- `GET /api/profile/{userId}/resume` - Download resume
- `DELETE /api/profile/me/resume` - Delete resume

## Features Working
✅ Resume upload with PDF validation
✅ Contact details management
✅ Privacy controls (contactVisible flag)
✅ File size limits (5MB)
✅ Proper error handling
✅ JWT authentication required

## Next Steps
1. Start the application using rebuild.bat
2. Test the endpoints using curl or Postman
3. Check the Swagger UI at http://localhost:8080/swagger-ui.html
4. Verify resume files are saved in uploads/resumes directory
