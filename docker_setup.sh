az acr login --name prporegistry
docker build -t prporegistry.azurecr.io/notification-service:latest .
docker push prporegistry.azurecr.io/notification-service:latest
