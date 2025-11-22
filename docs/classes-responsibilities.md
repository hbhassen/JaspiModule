# Responsabilités des classes

## IdpServerAuthModule
- Implémentation JASPIC complète (initialize, validateRequest, secureResponse, cleanSubject).
- Orchestration du flux : session -> validation -> redirection.
- Propagation du principal et des rôles via `CallerPrincipalCallback` et `GroupPrincipalCallback`.

## IdpAuthRequestBuilder
- Génère l'URL de redirection IdP à partir des propriétés et des paramètres calculés par `IdpClient`.

## IdpResponseValidator
- Extrait le jeton de la requête (`SAMLResponse` ou `id_token`).
- Délègue la validation à `TokenValidator` et retourne `IdpUserInfo`.

## IdpUserInfo
- Transport d'informations d'identité (subject, rôles, attributs complémentaires).

## IdpPrincipal
- Implémentation de `Principal` adaptée à la propagation dans WildFly.

## SessionManager
- Stockage et récupération d'`IdpUserInfo` dans la session HTTP.

## ConfigProperties
- Accès typé aux propriétés nécessaires : endpoints IdP, redirect URI, clientId, secret.

## LoggerUtils
- Fabrique de loggers nommés `IDP-JASPIC.<ClassName>`.

## Interfaces et exceptions
- `IdpClient`, `TokenValidator` pour l'abstraction de protocole.
- Exceptions dédiées : `IdpValidationException`, `IdpConfigurationException`, `IdpCommunicationException`.
