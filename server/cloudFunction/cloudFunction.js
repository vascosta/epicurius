const express = require("express");
const vision = require("@google-cloud/vision");
const axios = require("axios");

const app = express();
app.use(express.json());

const bucketName = "epicurius-test-bucket";
const cloudStorageFolder = "ingredients"
const gcsUri = `gs://${bucketName}/${cloudStorageFolder}/`

const visionClient = new vision.ImageAnnotatorClient();

const spoonacularAPIKey = process.env.SPOONACULAR_API_KEY;
const AUTOCOMPLETE_INGREDIENTS_URL = "https://api.spoonacular.com/food/ingredients/autocomplete"

const invalidIngredients = ["food"];

async function isValidIngredient(ingredient) {
    const url = AUTOCOMPLETE_INGREDIENTS_URL + `?apiKey=${spoonacularAPIKey}&query=${ingredient}`

    try {
        const response = await axios.get(url);
        return response.data.length >= 1;
    } catch (err) {
        console.error(`Error on Open Food Facts with "${ingredient}":`, err.message);
        return false;
    }
}

async function filterIngredients(ingredients) {
    const results = [];

    for (const ingredient of ingredients) {
        const isValid = await isValidIngredient(ingredient);
        if (isValid) {
            results.push(ingredient);
        }
    }

    return results;
}

app.post("/", async (req, res) => {
    const imageName = req.body.imageName;

    if (!imageName) {
        return res.status(400).json({ error: "missing image name" });
    }

    try {
        const [result] = await visionClient.objectLocalization(gcsUri + imageName);

        const detectedIngredients  = [
            ... new Set(
                result.localizedObjectAnnotations
                    .map(ingredient => ingredient.name.toLowerCase())
                    .filter(ingredient => !invalidIngredients.includes(ingredient))
            )
        ];

        const filteredIngredients = await filterIngredients(detectedIngredients);

        console.log("Detected Ingredients", detectedIngredients );
        console.log("Filtered Ingredients", filteredIngredients);

        res.status(200).json({ "Ingredients": filteredIngredients });
    } catch (error) {
        res.status(500).json({ "error": error.message });
    }
});

const PORT = 8080;
app.listen(PORT, () => {
    console.log(`Cloud Function running at http://localhost:${PORT}`);
});
