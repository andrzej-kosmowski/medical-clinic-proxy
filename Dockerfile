FROM amazoncorretto:21-alpine
COPY target/medical_clinic_proxy-0.0.1-SNAPSHOT.jar medical-clinic-proxy-app.jar
ENTRYPOINT ["java", "-jar", "medical-clinic-proxy-app.jar"]