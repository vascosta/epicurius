const express = require("express");
const vision = require("@google-cloud/vision");
const axios = require("axios");

const app = express();
app.use(express.json());

const bucketName = "epicurius-test-bucket";
const cloudStorageFolder = "ingredients"
const gcsUri = `gs://${bucketName}/${cloudStorageFolder}/`

const visionClient = new vision.ImageAnnotatorClient();

app.post("/", async (req, res) => {
    const imageName = req.body.imageName;

    if (!imageName) {
        return res.status(400).json({ error: "missing image name" });
    }

    try {
        const [result] = await visionClient.objectLocalization(gcsUri + imageName);

        const ingredients  = [
            ... new Set(
                result.localizedObjectAnnotations
                    .map(ingredient => ingredient.name.toLowerCase())
                    .filter(ingredient => !ingredient.includes(" "))
            )
        ];

        console.log("Detected Ingredients", ingredients );

        res.status(200).json({ "ingredients": ingredients });
    } catch (error) {
        res.status(500).json({ "error": error.message });
    }
});

const PORT = 8080;
app.listen(PORT, () => {
    console.log(`Cloud Function running at http://localhost:${PORT}`);
});
