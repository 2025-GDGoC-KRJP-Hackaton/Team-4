"use server";
import { redirect } from "next/navigation";
import { cookies } from "next/headers";

export async function handleMedicalInfo(prev: any, formData: FormData) {
  // Convert formData values to strings to avoid null values
  let redirectTo = "";
  const rawData = {
    // name: formData.get("name"),
    age: formData.get("age"),
    // language: formData.get("language"),
    // medications: formData.get("medications"),
    allergies: formData.get("allergies"),
    // conditions: formData.get("conditions"),
    height: formData.get("height"),
    weight: formData.get("weight"),
    bloodType: formData.get("bloodType"),
    // emergencyContacts: formData.get("emergencyContacts"),
  };

  console.log("Form data:", rawData);

  const baseUrl = process.env.URL;
  // TODO change endpoint
  const endpoint = new URL("api/medical/update", baseUrl);
  console.log(endpoint.toString());

  const coockiesStore = await cookies();
  const token = coockiesStore.get("token");
  if (!token) {
    console.error("Token not found in cookies");
    return {
      message: "Token not found in cookies",
    };
  }
  console.log(`Token: ${token.value}`);
  console.log("Cookies:", coockiesStore.get("token"));
  try {
    const response = await fetch(endpoint.toString(), {
      method: "POST",
      body: JSON.stringify(rawData),
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token.value}`,
      },
    });

    console.log("Response status:", response.status);

    // Get the actual error message from the response
    const responseBody = await response.text();
    console.log("Response body:", responseBody);

    if (response.ok) {
      console.log("Setup successful");
      redirectTo = "/evacuation";
    } else {
      console.error(`Setup failed: ${response.status} - ${responseBody}`);
      return {
        message: `Setup failed: ${response.status} - ${responseBody}`,
      };
      // throw new Error(`${response.status} - ${responseBody}`);
    }
  } catch (error) {
    console.error("Request error:", error);
    return {
      message: `Request error: ${
        error instanceof Error ? error.message : "An unknown error occurred"
      }`,
    };
    // throw new Error(error instanceof Error ? error.message : 'An unknown error occurred');
  }
  if (redirectTo) {
    redirect(redirectTo);
  } else {
    return {
      message: "Redirect failed",
    };
  }
}

export async function handleRegister(prev: any, formData: FormData) {
  let redirectTo = "";
  // Convert formData values to strings to avoid null values
  const rawData = {
    name: "name", // Required by your API, based on your curl command
    id: String(formData.get("id") || ""),
    password: String(formData.get("password") || ""),
  };

  console.log("Form data:", rawData);

  const baseUrl = process.env.URL;
  const endpoint = new URL("api/auth/register", baseUrl);
  console.log(endpoint.toString());

  try {
    const response = await fetch(endpoint.toString(), {
      method: "POST",
      body: JSON.stringify(rawData),
      headers: {
        "Content-Type": "application/json",
      },
    });

    console.log("Response status:", response.status);

    // First check if response is OK before consuming the body
    if (!response.ok) {
      // If not OK, then get error text
      const errorText = await response.text();
      console.error(`Setup failed: ${response.status} - ${errorText}`);
      return {
        message: `Setup failed: ${response.status} - ${errorText}`,
      };
    }

    // Only parse JSON for successful responses
    const responseData = await response.json();
    console.log("Setup successful", responseData);

    // Store the token in cookies
    const cookiesStore = await cookies();
    cookiesStore.set({
      name: "token",
      value: responseData.token || "", // Get actual token from response
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "strict",
      path: "/",
    });
    redirectTo = "/setup/medicals";
  } catch (error) {
    console.error("Request error:", error);
    return {
      message: `Request error: ${
        error instanceof Error ? error.message : "An unknown error occurred"
      }`,
    };
  }
  if (redirectTo) {
    redirect(redirectTo);
  } else {
    return {
      message: "Redirect failed",
    };
  }
}
