"use client";
import { useState } from "react";
import Link from "next/link";
import Image from "next/image";

import { BsPersonCircle } from "react-icons/bs";

export default function Home() {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");

  return (
    <main className="flex flex-col items-center min-h-screen bg-white">
      <h1 className="font-gmarket font-medium text-[#5D5D5D] text-[20px] mt-[56px] mb-[12px]">
        Sign up to continue
      </h1>
      <div className="w-full h-px bg-[#CCC] mb-8"></div>

      <div className="w-full px-[8px] flex items-center justify-center">
        <div className="w-full bg-[#F0F0F0] mx-[24px] mt-[32px] p-8">
          <div className="flex justify-center mb-6">
            <BsPersonCircle className="text-[120px] text-[#CCC]" />
          </div>

          <div className="w-full flex items-center bg-white px-3 py-[12px]">
            <Image
              src="/google_logo.png"
              alt="Google"
              width={30}
              height={30}
              className="mr-2"
            />
            <div className="w-full flex items-center justify-center">
              <p className="font-gmarket font-medium text-[#5D5D5D]">
                Sign in with Google
              </p>
            </div>
          </div>

          <div className="space-y-2 mt-[24px]">
            <input
              type="text"
              placeholder="ID"
              value={id}
              onChange={(e) => setId(e.target.value)}
              className="cursor-pointer w-full px-3 py-3 bg-white border-[2px] border-[#CCC] rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="cursor-pointer w-full px-3 py-3 bg-white border-[2px] border-[#CCC] rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <button className="w-full flex items-center justify-center mt-3 bg-[#4285F4] text-white py-2 rounded hover:bg-[#3367D6]">
            Sign in
          </button>

          <div className="flex items-center justify-between mt-4 font-gmarket font-medium text-[14px] text-gray-600">
            <label className="flex items-center text-[#5D5D5D] space-x-2">
              <input type="checkbox" className="accent-blue-500" />
              <span>Stay signed in</span>
            </label>
            <Link href="#" className="text-[#4285F4] hover:underline">
              Need help?
            </Link>
          </div>
        </div>
      </div>

      <Link
        href="setup"
        className="mt-6 text-blue-600 text-sm cursor-pointer hover:text-[#5D5D5D]"
      >
        Create an account
      </Link>
    </main>
  );
}
