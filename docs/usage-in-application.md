# Utilisation dans une application

## Configuration des propriétés
Les propriétés sont lues via `ConfigProperties` et transmises dans les options du module JASPIC. Exemple d'options à
insérer dans `login-config.xml` ou via Elytron :

- `idp.authorization.endpoint` : URL d'autorisation IdP
- `idp.client.id` : identifiant client
- `idp.client.secret` : secret client (optionnel)
- `app.redirect.uri` : URI de retour après authentification

## Cycle d'authentification
1. L'utilisateur accède à une ressource protégée.
2. `IdpServerAuthModule` redirige vers l'IdP si aucun principal n'est présent.
3. L'IdP renvoie `SAMLResponse` ou `id_token` sur l'`app.redirect.uri`.
4. `IdpResponseValidator` valide le jeton et construit un `IdpUserInfo`.
5. `SessionManager` mémorise l'utilisateur pour les requêtes suivantes.
6. Le conteneur WildFly reçoit un `IdpPrincipal` avec ses rôles.

## Extensibilité
- Implémentez `TokenValidator` pour intégrer un IdP SAML ou OIDC spécifique.
- Implémentez `IdpClient` pour ajuster les paramètres envoyés à l'IdP (scope, prompt, etc.).

## Dépendances serveur requises
- `jakarta.servlet.api`
- `jakarta.security.auth.message.api`
- `org.jboss.logging`
