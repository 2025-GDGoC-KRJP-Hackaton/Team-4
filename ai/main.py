from fastapi import FastAPI, UploadFile, File, Form
import google.generativeai as genai
from dotenv import load_dotenv
import os
from models import RequestData, UserPrompt
from pydantic import BaseModel

load_dotenv()

app = FastAPI()

api_key = os.getenv("GEMINI_API_KEY")
genai.configure(api_key=api_key)

@app.post("/gemini/multimodal_alert")
async def call_gemini_multimodal_alert(
    earthquake_data: str = Form(...),
    user_profile: str = Form(...),
    image: UploadFile = File(None),
    audio: UploadFile = File(None)
):
    model = genai.GenerativeModel("models/gemini-1.5-pro")

    # AI에게 전달할 프롬프트 고정 포맷
    prompt = f"""
You are an AI disaster alert assistant for blind users.
Based on the following [Disaster Data] and [User Medical Information], generate an emergency warning message tailored to the user's health condition and mobility limitations.
The message should be clear, concise, and formatted in an appropriate tone for voice guidance.

[Disaster Data]
{earthquake_data}

[User Medical Information]
{user_profile}

Important:
- Emphasize the level of danger.
- Include any specific safety instructions based on the user's mobility or medical conditions.
- The message should be in English, and suitable for audio announcement.
"""

    # 입력 리스트 준비
    inputs = [prompt]

    if image:
        image_bytes = await image.read()
        image_file = genai.upload_file(image_bytes, mime_type=image.content_type)
        inputs.append(image_file)

    if audio:
        audio_bytes = await audio.read()
        audio_file = genai.upload_file(audio_bytes, mime_type=audio.content_type)
        inputs.append(audio_file)

    response = model.generate_content(inputs)
    return {"ai_message": response.text}

# 기존 chat도 유지
@app.post("/gemini/chat")
async def chat_with_gemini(user_input: UserPrompt):
    model = genai.GenerativeModel("models/gemini-1.5-pro")
    response = model.generate_content(user_input.prompt)
    return {"ai_response": response.text}
