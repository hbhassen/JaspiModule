# WildFly 31 Integration Guide

## Prerequisites
- JDK 17
- Maven 3.9+
- WildFly 31.x
- Modules available on the server: `jakarta.servlet.api`, `jakarta.security.auth.message.api`, `org.jboss.logging`.

## Build the Module
```bash
mvn clean package
```
Copy `target/idp-jaspi-module-1.0.0.jar` and `module.xml` to your WildFly modules directory:

```
$WILDFLY_HOME/modules/com/example/idp/main/
```

## module.xml
```xml
<module xmlns="urn:jboss:module:1.9" name="com.example.idp">
    <resources>
        <resource-root path="idp-jaspi-module.jar"/>
    </resources>
    <dependencies>
        <module name="jakarta.servlet.api"/>
        <module name="jakarta.security.auth.message.api"/>
        <module name="org.jboss.logging"/>
    </dependencies>
</module>
```

## Configure login-config.xml
Place the following in `standalone/configuration/login-config.xml`:
```xml
<login-config>
    <auth-module code="com.example.idp.IdpServerAuthModule" module="com.example.idp"/>
</login-config>
```

## Configure security-domain
Add to `standalone.xml`:
```xml
<security-domain name="idp-jaspic-domain" cache-type="default">
    <authentication>
        <login-module code="org.jboss.security.auth.spi.DelegateLoginModule" flag="required">
            <module-option name="delegateLoginModule" value="org.jboss.security.auth.spi.JASPILoginModule"/>
        </login-module>
    </authentication>
</security-domain>
```

## Configure the application
In your `web.xml` of the WAR:
```xml
<login-config>
    <auth-method>FORM</auth-method>
    <realm-name>idp-jaspic-domain</realm-name>
</login-config>
```

## Module Options
In `login-config.xml`, supply module options that map to `ConfigProperties` keys, for example:
```xml
<auth-module code="com.example.idp.IdpServerAuthModule" module="com.example.idp">
    <module-option name="idp.auth.endpoint" value="https://idp.example.com/authorize"/>
    <module-option name="idp.client.id" value="wildfly-module"/>
    <module-option name="idp.redirect.uri" value="https://app.example.com/callback"/>
    <module-option name="idp.role.claim" value="roles"/>
</auth-module>
```
