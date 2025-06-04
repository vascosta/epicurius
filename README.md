# Epicurius

### Instituto Superior de Engenharia de Lisboa
#### Summer Semester 2024/2025


### Project Description

**Epicurius** is an application designed to make cooking more **accessible**, **personalized**, and **dynamic**. Users can discover new recipes by searching through a form or simply uploading a photo.

The app features an **interactive feed** where users can **share recipes** and engage with the community. With an intuitive interface, **Epicurius** offers a variety of tools, including **daily menu suggestions**, **meal planning**, and **fridge management**.

### Project Organization

- [**App folder**](./app) – Contains the **Android application** source code (**frontend**).
- [**Demo folder**](./demo) – Includes both the **APK** file of the application's **beta** version and **server** file.
- [**Docs folder**](./docs) – Contains **images** and **documentation** relevant for understanding the application.
- [**Server folder**](./server) – Holds the **backend** code of the application (**server side**).

### How to Run the Project

Assuming you already __cloned the repository__, follow these steps to run the project:

1. **Frontend**: Open the project in **Android Studio** and run the application on emulator or use the provided APK file in the **demo** folder.
2. **Backend**: Navigate to the **server** folder and run the __docker-compose file__ to start the backend service. Ensure you have __Docker__ installed and running on your machine.

To run the __backend tests__, we recommend using **IntelliJ IDEA** and run the tests from the IDE. For the **integration tests**, you must have a **PostgreSQL** database running locally and create a environment variable named `DATABASE_URL` with the database URL in the run configuration.


## Technologies
## Frontend

![](https://skillicons.dev/icons?i=kotlin,androidstudio)

## Backend
![](https://skillicons.dev/icons?i=kotlin,spring,postgresql,firebase,gcp)

## Developers

- [Vasco Costa](https://github.com/vascosta)
- [Carolina Tavares](https://github.com/carolinatvs)

## Supervisors

- [Artur Ferreira](https://github.com/arturj)