# Classes and Responsibilities

## IdpServerAuthModule
- Implements `ServerAuthModule` to orchestrate the entire JASPIC lifecycle.
- Triggers redirects when authentication is required.
- Validates IdP responses and populates the `Subject` with `IdpPrincipal` and roles.

## IdpAuthRequestBuilder
- Builds IdP redirect URLs using `ConfigProperties` values.
- Ensures state parameter is preserved via the HTTP session identifier.

## IdpResponseValidator
- Detects incoming token type (`SAMLResponse` or `id_token`).
- Delegates validation to `TokenValidator` implementations and returns `IdpUserInfo`.

## SessionManager
- Stores and retrieves `IdpUserInfo` in `HttpSession` to maintain login state.
- Provides clearing logic to support logout and `cleanSubject`.

## ConfigProperties
- Immutable configuration holder for IdP endpoints, client id, redirect URI, and scopes.
- Can be built from module options or property files; validates required keys.

## IdpPrincipal
- `Principal` exposing username and roles to the application layer.
- Wraps `IdpUserInfo` for richer attributes access.

## IdpUserInfo
- Immutable identity representation with username, roles, and arbitrary attributes.

## LoggerUtils
- Factory for JBoss `Logger` instances and helper log methods.

## Interfaces
- `IdpClient`: contract to build IdP authentication URLs.
- `TokenValidator`: pluggable token validation strategy.

## Exceptions
- `IdpConfigurationException`: missing or invalid configuration.
- `IdpValidationException`: IdP response could not be validated.
- `IdpCommunicationException`: network or protocol errors contacting IdP.
