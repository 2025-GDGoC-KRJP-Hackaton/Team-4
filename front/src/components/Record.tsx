import { useRef, useState } from "react";
import { getCookie, getCookies } from "cookies-next";
import Cookies from "js-cookie";

interface RecordProps {
  image: Blob | null;
  location: { latitude: number; longitude: number; timestamp: number } | null;
}

export default function Record({ image, location }: RecordProps) {
  const [recording, setRecording] = useState(false);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunks = useRef<Blob[]>([]);

  const handleUpload = async (audioBlob: Blob) => {
    const formData = new FormData();
    if (image) {
      formData.append(`image`, image, `image.jpg`);
    }
    if (location) {
      formData.append(
        `location`,
        new Blob([JSON.stringify(location)], { type: "application/json" }),
        `location.json`
      );
    }
    formData.append("voice", audioBlob, "recording.webm");
    if (!process.env.NEXT_PUBLIC_API_URL) {
      console.error("URL is not defined");
      return;
    }
    try {
      const response = await fetch("/api", {
        method: "POST",
        body: formData,
      });

      const responseText = await response.text();
      console.log("Upload response:", responseText);

      const utterance = new window.SpeechSynthesisUtterance(responseText);
      window.speechSynthesis.speak(utterance);
    } catch (error) {
      console.error("Error uploading audio:", error);
      return;
    }
  };
  const handleRecord = async () => {
    if (!recording) {
      // Start recording
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      audioChunks.current = [];

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunks.current.push(event.data);
        }
      };

      mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunks.current, { type: "audio/webm" });
        const url = URL.createObjectURL(audioBlob);
        setAudioUrl(url);
        stream.getTracks().forEach((track) => track.stop());
        handleUpload(audioBlob);
        navigator.vibrate(0);
      };

      navigator.vibrate(2000);

      mediaRecorder.start();

      // Speak "Recording started"
      const utterance = new window.SpeechSynthesisUtterance(
        "Recording started. Please speak."
      );
      window.speechSynthesis.speak(utterance);
      setRecording(true);
    } else {
      // Stop recording
      mediaRecorderRef.current?.stop();
      setRecording(false);
    }
  };

  return (
    <div className="flex items-center justify-center w-screen">
      <button
        onClick={handleRecord}
        className={`flex items-center justify-center rounded-full ${
          recording ? "bg-red-500" : "bg-green-500"
        } text-white text-[32px] font-bold`}
        style={{
          width: "240px",
          height: "240px",
          minWidth: "200px",
          minHeight: "200px",
        }}
      >
        {recording ? "Stop Recording" : "Start Recording"}
      </button>
      {audioUrl && (
        <audio
          controls
          src={audioUrl}
          className="mt-4 absolute bottom-10 left-1/2 -translate-x-1/2"
        />
      )}
    </div>
  );
}
