# Central Authentication Service (CAS) [![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/Jasig/cas/blob/master/LICENSE)

## Introduction
This branch hosts the [Docker](https://www.docker.com/) build configuration necessary to build a CAS image. See the `Dockerfile` for more info. 

## Versions
A docker image for CAS server `v4.1.3`. Images are tagged to match CAS server releases.

## Requirements
* Minimum of Docker version `1.9.x`

## Configuration

### Image
* The image will be available on the host via ports `80` and `443`
* The image may be accessed via the host browser at the container-provided IP address. You may determine the IP address via `docker inspect $CONTAINER_ID`

* The CAS server will be deployed inside an embedded [Jetty container](http://www.eclipse.org/jetty/) that is built into the [CAS overlay project](http://bit.ly/1PPY47q).

## CAS Overlay
* The build will automatically copy the contents of the `src\main\webapp` to the docker image. 
* You can set up your own custom overlay project based on a directory structure that mimics that of the CAS web application itself. 
 
### SSL
* Update the `thekeystore` file with the server certificate and chain if you need access the CAS server via HTTPS. 
* The password for the keystore is `changeit`.
* The build will automatically copy the keystore file to the image
* The embedded Jetty is pre-configured to use that keystore for HTTPS requests.

## Build

**Make sure** that both `build.sh` and `run.sh` are updated to build the appropriate tag. Docker tags MUST correspond
to CAS server versions. 

```bash
./build.sh
```

The image will be built as `apereo/cas:v$CasVersion`.

## Run

```bash
./run.sh
```

## Release
* New images shall be released at the time of a new CAS server release.
* Image versions are reflected in the `build|run.sh` files and need to be updated per CAS/Image release.
* Images are published to [https://hub.docker.com/r/apereo/cas/](https://hub.docker.com/r/apereo/cas/)
* Log into the Docker Hub via the following command:

```bash
docker-machine ssh default
docker login -u username -p password -e email
docker push apereo/cas:v$CasVersion
```

