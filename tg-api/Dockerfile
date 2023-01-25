FROM maven:3.8.6-eclipse-temurin-19

RUN apt-get update -q \
	&& apt-get install -y \
	vim \
	curl \
	git \
	tree \
    htop

# download binary openssl packages from Impish builds
RUN wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl/openssl_1.1.1f-1ubuntu2.16_amd64.deb && \
    wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl-dev_1.1.1f-1ubuntu2.16_amd64.deb && \
    wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl1.1_1.1.1f-1ubuntu2.16_amd64.deb

# install downloaded binary packages
RUN  dpkg -i libssl1.1_1.1.1f-1ubuntu2.16_amd64.deb  && \
  dpkg -i libssl-dev_1.1.1f-1ubuntu2.16_amd64.deb  && \
  dpkg -i openssl_1.1.1f-1ubuntu2.16_amd64.deb


#WORKDIR /app
#
#COPY src ./src
#COPY pom.xml ./pom.xml
#COPY run.sh ./run.sh
#
#RUN mvn dependency:go-offline
#
##RUN mvn compile -U
#
#CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.profiles=docker"]
