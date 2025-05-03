package epicurius;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;

public class GetIngredientsFromPicture implements HttpFunction {

    private static final String BUCKET_NAME = "epicurius-bucket";
    private static final String CLOUD_STORAGE_FOLDER = "ingredients";
    private static final String GCS_URI = "gs://" + BUCKET_NAME + "/" + CLOUD_STORAGE_FOLDER + "/";

    private static final Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.getMethod().equals("POST")) {
            response.setStatusCode(405);
            response.getWriter().write(gson.toJson(Map.of("error", "Method not allowed")));
            return;
        }

        response.appendHeader("Content-Type", "application/json");

        BufferedReader reader = request.getReader();
        BufferedWriter writer = response.getWriter();
        JsonObject requestBody = gson.fromJson(reader, JsonObject.class);

        if (!requestBody.has("pictureName") || requestBody.get("pictureName").getAsString().isEmpty()) {
            response.setStatusCode(400);
            writer.write(gson.toJson(Map.of("error", "missing image name")));
            return;
        }

        String pictureName = requestBody.get("pictureName").getAsString();

        ImageSource imageSource = ImageSource.newBuilder().setGcsImageUri(GCS_URI + pictureName).build();
        Image image = Image.newBuilder().setSource(imageSource).build();

        Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest visionRequest = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = List.of(visionRequest);
            BatchAnnotateImagesResponse responseBatch = visionClient.batchAnnotateImages(requests);

            Set<String> ingredients = new HashSet<>();
            for (AnnotateImageResponse res : responseBatch.getResponsesList()) {
                if (res.hasError()) {
                    response.setStatusCode(500);
                    writer.write(gson.toJson(Map.of("error", res.getError().getMessage())));
                    return;
                }

                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    ingredients.add(annotation.getDescription().toLowerCase());
                }
            }

            response.setStatusCode(200);
            writer.write(gson.toJson(ingredients));

        } catch (Exception e) {
            response.setStatusCode(500);
            response.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}