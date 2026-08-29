# Embedded Systems Management Web Application — Backend

Application web full-stack de gestion et supervision en temps réel 
des systèmes embarqués et de leurs capteurs, développée dans le cadre 
d'un stage de fin d'études chez **Be Wireless Solutions (BWS)**.

---

## Stack technique

| Couche | Technologie |
|---|---|
| Backend | Spring Boot |
| Sécurité | Spring Security + JWT |
| Cache | Hazelcast |
| Base de données | MySQL |
| ORM | JPA / Hibernate |

---

## Fonctionnalités

- **Dashboard** temps réel avec KPIs (systèmes, capteurs, alertes)
- **Gestion des systèmes embarqués** (CRUD complet)
- **Gestion des capteurs** avec types et configurations
- **Moteur de règles** par capteur avec déclenchement automatique d'alertes
- **Validation de qualité** des mesures (plages min/max)
- **Cache Hazelcast** double-map pour performances optimales
- **Authentification JWT** avec gestion des rôles (Admin / Utilisateur)
- **Gestion des utilisateurs** et des profils

---

## Architecture
src/main/java
├── controllers/ → API REST (endpoints)
├── services/ → Logique métier
├── repositories/ → Accès base de données (JPA)
├── models/ → Entités (User, Sensor, Alert, Rule...)
├── security/ → JWT, Spring Security
└── config/ → Hazelcast, CORS


---

## Lancer le projet

### Prérequis
- Java 17+
- Maven
- MySQL 8+ (ou XAMPP)

### Configuration
```bash
# Copiez le fichier exemple
cp src/main/resources/application-example.properties src/main/resources/application.properties

# Modifiez application.properties avec vos valeurs
```

### Démarrage
```bash
mvn spring-boot:run
```

L'API sera disponible sur : `http://localhost:9000`

---

## Frontend

Le frontend Angular est disponible ici :  
[Embedded-Systems-Management-Web-Application---Frontend](https://github.com/Zaineb-Hl/Embedded-Systems-Management-Web-Application---Frontend)