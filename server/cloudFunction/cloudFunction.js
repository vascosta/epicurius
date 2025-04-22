const express = require("express");
const vision = require("@google-cloud/vision");

const app = express();
app.use(express.json());

const bucketName = "epicurius-bucket";
const cloudStorageFolder = "ingredients"
const gcsUri = `gs://${bucketName}/${cloudStorageFolder}/`

const visionClient = new vision.ImageAnnotatorClient();

app.post("/", async (req, res) => {

    const pictureName = req.body.pictureName;

    if (!pictureName) {
        return res.status(400).json({ error: "missing image name" });
    }

    try {
        const [result] = await visionClient.labelDetection(gcsUri + pictureName);

        const ingredients  = [
            ... new Set(
                result.labelAnnotations
                    .map(ingredient => ingredient.description.toLowerCase())
            )
        ];

        console.log("Detected Ingredients", ingredients );

        res.status(200).json(ingredients);
    } catch (error) {
        res.status(500).json({ "error": error.message });
    }
});

const PORT = 1904;
app.listen(PORT, () => {
    console.log(`Cloud Function running at http://localhost:${PORT}`);
});
