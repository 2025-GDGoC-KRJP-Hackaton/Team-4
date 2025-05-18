// Create a file: src/app/api/proxy/disaster-prompt/route.ts
import { cookies } from "next/headers";

export async function POST(request: Request) {
  // Get the token from the HTTPOnly cookie (server-side only)
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;
  if (!token) {
    console.error("Token not found in cookies");
    return new Response("Token not found in cookies", {
      status: 401,
      headers: {
        "Content-Type": "application/json",
      },
    });
  }
  console.log(`Token: ${token}`);

  // Get the request body
  const formData = await request.formData();

  // Forward the request to the actual API
  const apiUrl = process.env.NEXT_PUBLIC_API_URL + "/api/disaster/prompt";

  const response = await fetch(apiUrl, {
    method: "POST",
    headers: {
      // Automatically set by fetch with formData
      // Add the token from the server-side cookie
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  // Return the response from the API
  const data = await response.text();
  return new Response(data, {
    status: response.status,
    headers: {
      "Content-Type": response.headers.get("Content-Type") || "text/plain",
    },
  });
}
