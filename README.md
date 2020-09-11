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
* **registry.uri**: URI du service d'annauire -> **http://main.host:18100/registry/api/1.0**
* **redis.uri**: URI du service Redis -> **tcp://localhost:6382**
* **quarkus.http.port**: Port exposée par le service -> **18104**
* **storage.path**: Définitition du chemin de stockage des fichiers -> **user_home**
* **quarkus.log.handler.gelf.enabled**: Activation du logging GRAYLOG -> **false**
* **quarkus.log.handler.gelf.host**: Serveur GRAYLOG -> **vsrvglog.pam.lan**
* **quarkus.log.handler.gelf.port**: Port GRAYLOG -> **12201**
* **quarkus.jaeger.endpoint**: URI Jaeger -> **http://jaeger.service.dev.lan:14268/api/traces**


## Utilisateur nécessaire

Sans objet


## Profil accepté par défaut

Sans objet


## Rôles disponibles pour le(s) service(s)

Sans objet

## Contrat et URL
### API

### UI


