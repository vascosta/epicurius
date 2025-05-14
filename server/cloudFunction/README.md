# Cloud Function - Technical Document
This document contains the cloud function documentation and implementation aspects.

## Introduction
The cloud function provides support for the backend server of the Epicurius Mobile App.

The objective of the cloud function is to interact with the [__Vision API__](https://cloud.google.com/vision/docs) to retrieve labels present in a image previously stored in the __Cloud Storage__.

The backend server must send an ``HTTP POST`` request with the name of the picture stored in the __Cloud Storage__, otherwise, the cloud Function will reject the request. 

The __cloud function__ was developed using __Java__ technology. In order to  __trigger__ it, the backend server must do an HTTP request do the Cloud Function's endpoint. The deployment was made using the __Google Cloud Platform__ and the [__Cloud Run__](https://cloud.google.com/run/docs) service.


## Architecture

![](../docs/imgs/) adicionar foto da ligacao entre backend e cloud function

### Trigger

The cloud function is triggered when an HTTP request is made to its endpoint.

Given the backend context, the access was restricted so only ``HTTP POST`` methods are allowed, reducing unsolicited requests:

```java
public class GetIngredientsFromPicture implements HttpFunction {

    ...

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.getMethod().equals("POST")) {
            response.setStatusCode(405);
            response.getWriter().write(gson.toJson(Map.of("error", "Method not allowed")));
            return;
        }

        ...

    }
}
```

### Deployment

The cloud function can be deployed using the Google Cloud Platform or the following ``gcloud`` command:

```powershell
gcloud functions deploy get-ingredients-from-picture `
  --entry-point epicurius.GetIngredientsFromPicture `
  --runtime java21 `
  --trigger-http `
  --allow-unauthenticated `
  --region=europe-west1 `
  --gen2
```


## Response Example

For example, given a picture with tomatos, a possible response would be:

```json
{
    [
        "red",
        "fruit",
        "cherry tomato",
        "tomato",
        "vegetable"
    ]

}
```