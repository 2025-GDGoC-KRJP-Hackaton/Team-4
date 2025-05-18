"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image";

export default function LaunchPage() {
  const router = useRouter();

  useEffect(() => {
    const timer = setTimeout(() => {
      router.push("/home");
    }, 3000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <main className="flex flex-col justify-between items-center h-screen bg-white">
      <div className="flex-grow flex justify-center items-center">
        <Image
          src="/watch_out_logo.png"
          alt="Watch Out Logo"
          width={180}
          height={180}
        />
      </div>

      <div className="mb-8 text-center">
        <h1 className="font-open font-bold text-[#5D5D5D] text-[36px]">
          Watch Out!
        </h1>
        <p className="font-open font-normal text-[#5D5D5D] text-[16px] mb-[48px]">
          Powered by Gemini AI
        </p>
      </div>
    </main>
  );
}
