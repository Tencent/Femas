<img src="https://user-images.githubusercontent.com/22976760/153148783-0ce5ae2f-cc36-4217-8f80-b29a2930b593.png" width="400px">

# Femas: Cloud native multi-runtime microservice framework

**`English` | [`简体中文`](./README-zh.md)**

- [Introduction](#introduction)
    - [ability](#ability)
    - [Feature](#feature)
- [Quick start](#quick-start)
    - [Install the server](#install-the-server)
    - [Stand-alone deployment](#stand-alone-deployment)
    - [Cluster deployment](#cluster-deployment)
    - [Springcloud access](#springcloud-access)
        - [Sample](#sample)
        - [Add dependency](#add-dependency)
        - [Configuration file](#configuration-file)
        - [Start service command](#start-service-command)
- [Dubbo access](#dubbo-access)
    - [Sample](#sample)
- [Documentation](#documentation)
    - [Official Document](#official-document)
    - [FAQ](#faq)
    - [Contribution Manual](#contribution-manual)
    - [Code of Conduct](#code-of-conduct)
- [Participate in Contribution](#participate-in-contribution)
- [contact us](#contact-us)
- [License](#license)

## Introduction
**Femas is an open source cloud-native one-stop microservice management framework of Tencent Microservices Product Center. It is incubated and supported by Tencent's commercial product [`TSF`](https://cloud.tencent.com/product/tsf) Developed Tencent’s billion-scale user ecosystem. The cloud-native microservice technology stack is complicated. Femas uses the idea of ​​`Multi-runtime` standard paradigm API to `standardize` and `modulate` the core capabilities of the underlying microservices, and divide the technology ecology of the microservice field through a reasonable architecture. Assembled together to meet diversified microservice scenarios, lightweight, portable, low-cost, and cloud-free vendor binding, allowing enterprises to more easily build a large-scale distributed architecture based on cloud native.**

>- For end users: Femas provides complete console capabilities, and provides common framework plug-ins, compatible with mainstream open source technologies. Users only need to add `Pom` dependencies to quickly and easily have a full set of visual microservice runtimes ability.

>- For self-developed framework teams: Femas has developed a set of microservice API interface definitions that conform to the cloud-native `Multi-runtime` standard paradigm. The framework team can connect any self-developed framework to Femas through the highly encapsulated `API` interface , To obtain a full set of visual microservice runtime capabilities.

>- For the platform team: Femas abstracts almost all component capabilities that are used in microservices runtime, and provides a large number of implementations. The platform team can also assemble into a private microservice platform that meets the company's internal platform conditions through the realization of custom components and provide it for company research and development.

### ability
![image](https://user-images.githubusercontent.com/22976760/153156369-6fa5626e-f0a5-452b-8519-fe84013b5186.png)

- Registry hosting:
Femas has realized the hosting of mainstream open source registry (currently supports `Consul, nacos, eureka`), including cluster management and service management. Users can configure the registry cluster on the Paas platform to view the cluster status and service list.
- Service governance: **`Authentication`, `API management`, `Fuse downgrade`, `Access current limit`, `Service registration discovery`, `Service routing`, `Service event`**.
- Service configuration: application configuration management, configuration hot update, Femas implements a set of standard configuration API interface, configuration is divided into `governance rules`,`application configuration`, **open source side supports directly issuing `governance rules through Paas platform `, do not rely on other third-party components**.
- Service registration discovery
Femas implements a set of standard registration discovery API interfaces, and users can directly use the SDK provided by Femas to register and discover to mainstream open source registry centers.
- Service monitoring:
 - > Metrics: Femas implements a set of standard API interfaces for business metrics. Femas uses `micrometer` to implement business metrics statistics by default.
 - > Tracing: Femas implements a set of standard tracing API interfaces. The SDK side is responsible for formulating `OpenTracing` log specifications and link collection. By default, Opentelemtry is used to collect Tracing
- TBD


### Feature

- Provide SDKs for `Java` and `Go` to help users realize **multi-language** unified management on the same Paas platform.
- Femas standardizes and encapsulates microservice capabilities and provides unified access to the `Layer` layer of irrelevant protocols, which facilitates the integration of a full set of capabilities into any protocol, and realizes the unified management of **multi-protocol**.
- Femas abstracts the capabilities that a microservice application may need to use in the running process into standard `API` components, which are convenient for expansion and compatible with other open source component ecosystems.
- Femas does not bind any other components, which is convenient for users at all levels to learn, use and secondary development.
- Femas's low-level capabilities are **plug-in, easy to expand**, and users can flexibly combine and match the microservice capability matrix according to their needs.
- **Sink-type non-intrusive access, zero cost for user transformation**.
> - `Agent` bytecode injection (`TODO`)
> - `ServiceMesh` service mesh
- Femas hopes to summarize Tencent's microservice product center's understanding of microservices into a platform to help users quickly build an enterprise-level microservice ecosystem.
- Femas supports Tencent's internal ecosystem of billions of users.
## Quick start

### Install the server

The operating environment depends on:

> 64 bit OS, support Linux/Unix/Mac/Windows, script start support Linux/Unix/Mac;

> 64 bit JDK 1.8+;

> Maven 3.2.x+;

> APM monitoring tool Skywalking

> Metrics monitoring tools promethus, grafana

> External database Mysql (optional)

### Stand-alone deployment

Source code compilation method
> mvn -Dmaven.test.skip=true clean install -U

> cd femas-admin-starter/target/femas-admin-starter-$version/femas-admin/bin

Start with the embedded database: The embedded database only supports stand-alone deployment, and cluster deployment is not currently supported. The embedded database data disk path is `${user.home}/rocksdb/femas/data/`
> sh startup.sh

Start with an external database:
> sh startup.sh external


Download compression method
Press file
> tar -zxvf femas-admin-starter-$version.tar.gz

> cd femas-admin-starter-$version/femas-admin/bin

Startup script, embedded database
> sh startup.sh

Configuration file:

The project configuration file is in the `femas-admin/conf` directory
> cd femas-admin-starter-$version/femas-admin/conf

Configure skywalking backend address
```
femas:
  trace:
    backend:
      addr: http://IP:PORT
#Configure Metrics grafana address
  metrics:
    grafana:
      addr: http://IP:PORT
```

### Cluster deployment

Cluster deployment is the same as single-machine deployment, the only difference is that the data source must be an external data source
The start command is
> sh startup.sh external

Configuration file configuration data source
```
spring:
  datasource:
    url: jdbc:mysql://IP:3306/adminDb?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useSSL=false&serverTimezone=Asia/Shanghai
    username: username
    password: password
    hikari:
      driver-class-name: com.mysql.cj.jdbc.Driver
```

**`Visit http://localhost:8080/index`**

### Springcloud access

##### [Sample](./)

##### Add dependency
```
<!-- Native dependency of registry -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
 
<!-- femas middleware dependency -->
<dependency>
    <groupId>com.tencent.tsf</groupId>
    <artifactId>femas-extension-springcloud-greenwich-starter</artifactId>
    <version>${femas.latest.version}</version>
</dependency>
```

##### Configuration file
```
server:
  port: 18001
spring:
  application:
    name:
      femas-springcloud-consumer
  cloud:
    consul:
      host: 127.0.0.1
      port: 8500
      discovery:
        serviceName: femas-springcloud-consumer
        instanceId: ${spring.application.name}-${server.port}-${spring.cloud.client.hostname}
        heartbeat:
          enabled: true
# nacos:
# discovery:
# server-addr: 127.0.0.1:8848
 
 
# Is to configure the paas backend if there is no configuration, then obtain the rules from the local configuration file
paas_server_address: http://127.0.0.1:8080
 
# Use the method provided by Femas to access the registration center
femas_registry_ip: 127.0.0.1
femas_registry_port: 8500
femas_registry_type: consul
```

##### Start service command
```
-javaagent:"{skywalking agent absolute path}/agent/skywalking-agent.jar"
-Dfemas_namespace_id=Namespace ID
-Dfemas_prog_version=Service version
-Dskywalking.agent.service_name=The name of the service registered on skywalking, which needs to be consistent with the name of the registration center
-Dskywalking.collector.backend_service=skywalking backend address, which can override the agent's conf configuration
```
> 1. Reference agent probe of skywalking
> 2. The service needs to specify its own namespace
> 3. Specify the group to which the service belongs, and cooperate with the realization of service governance on the SDK side
> 4. The service name registered to skywalking must be the same as the name registered to the registration center, otherwise the tracing link observation will not find the corresponding service.

### Dubbo access

##### [Sample](./)
> For details, see the official document below

## Documentation
#### [Official Document]()
#### [FAQ]()
#### [Contribution Manual](./CONTRIBUTING.md)
#### [Code of Conduct](./Code-of-Conduct.md)

## Participate in Contribution
> - Actively participate in the discussion of the Issue, such as answering questions, providing ideas, or reporting unsolvable errors (Issue)
> - Write and improve project documentation (Wiki)
> - Submit patch optimization code (Coding)

**You will get**
> - Join the list of contributors to Tencent open source projects and display them on Tencent open source official website
> - Write [CONTRIBUTING.md](./CONTRIBUTING.md) for specific items
> - Tencent open source contributor certificate ([electronic version & paper](https://opensource.tencent.com/img/example.jpg))
> - Become a special guest of offline technology conference/salon
> - Q coins and souvenirs

## contact us
![image](https://user-images.githubusercontent.com/22976760/153156454-9e1718e8-e676-4f1b-838b-bbe95e9237f7.png)


## License
[BSD v3.](./LICENSE)

