fabric8-cxf-shiro
======================

This example project is comprehended by the following:
* HSQLDB in-memory datasource
* Aries (JPA 1.1) + Hibernate (4.2.11) powered persistence
* Shiro-based tailored session-management OSGi service
* Hazelcast (2.6) powered session distributed persistence
* JAX-RS (2.0) filters to deal with HTTP requests and map them to Shiro sessions (maintained by the aforementioned OSGi service)
* CXF endpoint to test them all

# Pre-requisites

* JDK 7
* Maven 3.1.0 or newer

# Build and install

```
mvn clean install
```

# Provisioning

## Installation and initial configuration

* Download [latest build](https://repository.jboss.org/nexus/content/repositories/ea/io/fabric8/fabric8-karaf/) for ```fabric-karaf``` and extract it.
*(tested on fabric8-karaf-1.0.0.redhat-362)*
* Extract it
* ```cd``` to the newly extracted folder
* Define default administrative user (login: **admin**, password:**admin**) by uncommenting the last line of ```etc/users.properties```
* Start Fabric
```no-highlight
bin/fusefabric
```

If everything goes well, you should get a Fabric shell that looks like this:

```
Please wait while Fabric8 is loading...
100% [========================================================================]

______    _          _      _____
|  ___|  | |        (_)    |  _  |
| |_ __ _| |__  _ __ _  ___ \ V /
|  _/ _` | '_ \| '__| |/ __|/ _ \
| || (_| | |_) | |  | | (__| |_| |
\_| \__,_|_.__/|_|  |_|\___\_____/
  Fabric8 Container (1.0.0.redhat-362)
  http://fabric8.io/

Type 'help' to get started
and 'help [cmd]' for help on a specific command.
Hit '<ctrl-d>' or 'osgi:shutdown' to shutdown this container.

Open a browser to http://localhost:8181 to access the management console

Create a new Fabric via 'fabric:create'
or join an existing Fabric via 'fabric:join [someUrls]'

Fabric8:karaf@root>
```

## Start Fabric Ensemble
```
fabric:create --clean --wait-for-provisioning
```

## Define our own profile
```
profile-create --parents feature-dosgi cxf-shiro-example
profile-edit --repositories mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.3.2/xml/features cxf-shiro-example
profile-edit --repositories mvn:com.github.pires.example/feature-persistence/0.1-SNAPSHOT/xml/features cxf-shiro-example
profile-edit --repositories mvn:com.github.pires.example/feature-rest/0.1-SNAPSHOT/xml/features cxf-shiro-example
profile-edit --features hazelcast cxf-shiro-example
profile-edit --features persistence-aries-hibernate cxf-shiro-example
profile-edit --features cxf-shiro cxf-shiro-example
profile-edit --bundles mvn:com.github.pires.example/datasource-hsqldb/0.1-SNAPSHOT cxf-shiro-example
profile-edit --bundles mvn:com.github.pires.example/service/0.1-SNAPSHOT cxf-shiro-example
profile-edit --bundles mvn:com.github.pires.example/service-impl/0.1-SNAPSHOT cxf-shiro-example
profile-edit --bundles mvn:com.github.pires.example/cxf-shiro/0.1-SNAPSHOT cxf-shiro-example
```

## Create and run new container with newly created profile

```
container-create-child --profile cxf-shiro-example root cxf-shiro-example-test1
```

# Testing

In Hawt.io UI, go to ```API``` tab (in the parent container), check the host and port where ```AuthenticationManager``` is available and point it down. Test the REST endpoint as you wish!

## REST API (JSON)

### Initialize scenario

```
PUT /demo/auth
```

### Login

```
POST /demo/auth

Example JSON:
{
  "username":"admin@example.com",
  "password":["1","2","3"]
}
```

Check the response headers and write down _be-token_, for usage in authenticated requests.

### View profile

Set _be-token_ header to the value you've written before.

```
GET /demo/auth
```

You should see _admin@example.com_ in the response body.
