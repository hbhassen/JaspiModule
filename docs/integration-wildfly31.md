# Integration with WildFly 31

## Prerequisites
- JDK 17
- WildFly 31.x
- Maven 3.9+

## Build the module
```
mvn clean package
```
The build produces `idp-jaspi-module-1.0.0.jar` in `target/`.

## Install as WildFly module
1. Create the module directory:
   ```
   mkdir -p $WILDFLY_HOME/modules/com/example/idp/main
   ```
2. Copy the artefacts:
   - `target/idp-jaspi-module-1.0.0.jar`
   - `module.xml`
3. Ensure module dependencies are available from WildFly:
   - `jakarta.servlet.api`
   - `jakarta.security.auth.message.api`
   - `org.jboss.logging`

## Configure JASPIC
Create `login-config.xml` in the WildFly configuration directory:
```xml
<login-config>
    <auth-module code="com.example.idp.IdpServerAuthModule" module="com.example.idp"/>
</login-config>
```

Reference the configuration in your security domain (Elytron or legacy security). Example using legacy subsystem:
```xml
<security-domain name="idp-jaspic-domain" cache-type="default">
    <authentication>
        <login-module code="org.jboss.security.auth.spi.DelegateLoginModule" flag="required">
            <module-option name="delegateLoginModule" value="org.jboss.security.auth.spi.JASPILoginModule"/>
        </login-module>
    </authentication>
</security-domain>
```

## Module options
Provide configuration through login module options or Elytron properties:
- `idp.authorize.endpoint`: IdP authorization endpoint URL
- `idp.client.id`: Client identifier registered at the IdP
- `idp.redirect.uri`: Callback URI handled by the application (must match IdP configuration)
- `idp.requested.scopes` (optional): Space-separated scopes, defaults to `openid profile`

## Application descriptor (web.xml)
```
<login-config>
    <auth-method>FORM</auth-method>
    <realm-name>idp-jaspic-domain</realm-name>
</login-config>
```

## Expected flow
- Anonymous request triggers redirect to the IdP authorization endpoint.
- IdP returns to the configured redirect URI with `SAMLResponse` or `id_token`.
- Token is validated and mapped to `IdpPrincipal` with roles propagated via `GroupPrincipalCallback`.
- Subsequent requests reuse session information without re-contacting the IdP.
