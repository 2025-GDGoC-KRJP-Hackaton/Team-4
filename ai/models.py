from pydantic import BaseModel

class EarthquakeData(BaseModel):
    properties: dict
    geometry: dict

class UserProfile(BaseModel):
    name: str
    medical_condition: list
    mobility: str
    emergency_contact: str

class RequestData(BaseModel):
    earthquake_data: EarthquakeData
    user_profile: UserProfile

class UserPrompt(BaseModel):
    prompt: str