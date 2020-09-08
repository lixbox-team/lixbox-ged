# lixbox-param

Ce dépôt contient le µservice de ged
Le site du service est [ici](https://project-site.service.dev.lan/lixbox-ged)


## Dépendances
### API
* Nécessite un service Redis.
* Peut s'enregistrer dans un annuaire de service lixbox-registry.  

### UI
* Sans objet

## Configuration 
### API
Les variables d'environnement suivantes servent à configurer le service:
* **REGISTRY_URI**: URI du service d'annauire initialisée avec **http://main.host:18100/registry/api/1.0**
* **REDIS_URI**: URI du service Redis initialisée avec **tcp://localhost:6381**
* **QUARKUS_HTTP_PORT**: Port exposée par le service initialisé avec **18104**


## Utilisateur nécessaire

Sans objet


## Profil accepté par défaut

Sans objet


## Rôles disponibles pour le(s) service(s)

Sans objet

## Contrat et URL
### API

### UI


