# Build stage
FROM golang:1.22-alpine AS builder

# Set working directory
WORKDIR /app

# Copy go.mod and go.sum files (если есть)
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download || true

# Copy all source files from temperature-api directory
COPY . .

# Build the application binary named "temperature-api"
RUN CGO_ENABLED=0 GOOS=linux go build -o temperature-api main.go

# Runtime stage
FROM alpine:latest

# Install necessary runtime dependencies
RUN apk --no-cache add ca-certificates tzdata

# Set working directory
WORKDIR /app

# Copy the binary from the builder stage
COPY --from=builder /app/temperature-api .

# Expose the port the app listens on
EXPOSE 8081

# Command to run the application
CMD ["./temperature-api"]
