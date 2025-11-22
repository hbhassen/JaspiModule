# Architecture du module IdP JASPIC

## Vue d'ensemble
Le module suit l'architecture JASPIC pour WildFly 31 en se basant sur Jakarta EE 10. Les composants principaux sont :

- **IdpServerAuthModule** : point d'entrée JASPIC orchestrant le cycle d'authentification.
- **IdpAuthRequestBuilder** : construit les URLs de redirection IdP.
- **IdpResponseValidator** : valide la réponse IdP et produit un `IdpUserInfo`.
- **SessionManager** : stocke l'identité en session pour éviter des revalidations.
- **ConfigProperties** : expose la configuration fournie par le module WildFly.
- **IdpPrincipal / IdpUserInfo** : représentent respectivement le principal propagé et les données IdP brutes.

## Flux principal
1. **validateRequest** détecte si l'utilisateur est déjà authentifié via la session.
2. Si la requête contient `SAMLResponse` ou `id_token`, la réponse est validée par `IdpResponseValidator`.
3. Si aucune information n'est présente, `IdpAuthRequestBuilder` génère l'URL de redirection vers l'IdP.
4. Après validation, `IdpServerAuthModule` crée un `IdpPrincipal` et le propage via les callbacks JASPIC.

## Diagramme d'interaction simplifié
```
Client -> IdpServerAuthModule -> IdpAuthRequestBuilder -> IdP
Client <- IdpServerAuthModule <- IdpResponseValidator <- IdP
```

## Technologies
- **JDK 17**
- **Jakarta Security 2.x** (jakarta.security.auth.message)
- **Jakarta Servlet 5.x**
- **JBoss Logging** pour la journalisation
