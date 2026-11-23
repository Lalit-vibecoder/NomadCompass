import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { GoogleGenerativeAI, SchemaType } from "@google/generative-ai";

// Securely access the Gemini API key from Firebase Secret Manager
const geminiApiKey = defineSecret("GEMINI_API_KEY");

/**
 * Callable Firebase Cloud Function to securely parse raw itinerary text/documents using Gemini.
 * The client app never sees or stores the API key.
 */
export const parseItinerary = onCall(
  {
    secrets: [geminiApiKey],
    cors: true,
  },
  async (request) => {
    const rawText = request.data?.text;
    const targetTripContext = request.data?.tripContext || "";

    if (!rawText || typeof rawText !== "string" || rawText.trim().length === 0) {
      throw new HttpsError(
        "invalid-argument",
        "The 'text' field is required and must not be empty."
      );
    }

    const apiKey = geminiApiKey.value();
    if (!apiKey) {
      throw new HttpsError(
        "failed-precondition",
        "GEMINI_API_KEY secret is not set in Firebase."
      );
    }

    const genAI = new GoogleGenerativeAI(apiKey);

    // Using gemini-1.5-flash for high speed, low latency, and structured JSON output
    const model = genAI.getGenerativeModel({
      model: "gemini-1.5-flash",
      generationConfig: {
        responseMimeType: "application/json",
        responseSchema: {
          type: SchemaType.ARRAY,
          description: "List of parsed itinerary events extracted from the text.",
          items: {
            type: SchemaType.OBJECT,
            properties: {
              title: {
                type: SchemaType.STRING,
                description: "Concise title of the event (e.g. Flight to Paris, Check-in Hotel, Museum Tour)",
              },
              category: {
                type: SchemaType.STRING,
                description: "Category: FLIGHT, ACCOMMODATION, ACTIVITY, TRANSPORT, RESTAURANT, or OTHER",
              },
              location: {
                type: SchemaType.STRING,
                description: "Name of airport, hotel, station, venue, or address",
              },
              startDateTime: {
                type: SchemaType.STRING,
                description: "Start date and time (ISO-8601 YYYY-MM-DDTHH:mm:ss if available, or best formatted date string)",
              },
              endDateTime: {
                type: SchemaType.STRING,
                description: "End date and time if available (or empty string)",
              },
              confirmationCode: {
                type: SchemaType.STRING,
                description: "Booking reference, PNR, e-ticket or reservation code if found",
              },
              notes: {
                type: SchemaType.STRING,
                description: "Useful details like terminal, seat, check-in instructions, address, phone",
              },
            },
            required: ["title", "category"],
          },
        },
      },
    });

    const prompt = `
Extract all travel reservations, bookings, flights, hotels, tours, activities, and dining events from the following document or email text.
${targetTripContext ? `Context about the trip: ${targetTripContext}` : ""}

Ensure category strictly matches one of: FLIGHT, ACCOMMODATION, ACTIVITY, TRANSPORT, RESTAURANT, OTHER.
If the date is relative (e.g. "Day 1", "Tomorrow"), try to derive plausible chronological dates or keep the best indicator.

Text to parse:
---
${rawText}
---
`;

    try {
      const result = await model.generateContent(prompt);
      const jsonResponseText = result.response.text();
      const events = JSON.parse(jsonResponseText);

      return {
        success: true,
        count: Array.isArray(events) ? events.length : 0,
        events: events,
      };
    } catch (err: any) {
      console.error("Gemini API Error in parseItinerary:", err);
      throw new HttpsError(
        "internal",
        err.message || "Failed to parse itinerary with Gemini AI."
      );
    }
  }
);
