# Base image for Node.js
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy the application source
COPY . .

RUN npm run build

# Expose the port for the application
EXPOSE ${VITE_FRONT_SERVER_PORT}

# Start the application
CMD ["npm", "run", "dev"]
