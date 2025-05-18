"use client";
import { handleRegister } from "@/utils/actions";
import { useActionState } from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";

import { FaArrowLeftLong } from "react-icons/fa6";

export default function Setup() {
  const [state, formAction, pending] = useActionState(handleRegister, {
    message: "",
  });

  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");

  const isMismatch = password !== passwordCheck;

  const router = useRouter();

  return (
    <main className="flex flex-col items-center min-h-screen">
      <div
        onClick={() => router.back()}
        className="relative w-full mt-[56px] mb-[12px] h-[32px]"
      >
        <FaArrowLeftLong className="absolute left-0 top-1/2 -translate-y-1/2 ml-6 text-[36px] text-[#5D5D5D] cursor-pointer hover:bg-[#F0F0F0] p-2 rounded-full" />
        <h1 className="font-gmarket font-medium text-[#5D5D5D] text-[20px] text-center">
          Add Information
        </h1>
      </div>

      <div className="w-full flex-grow bg-[#F7F7F7] p-6">
        <p className="text-[20px] text-[#5D5D5D] mt-[24px]">
          Account Information
        </p>
        {state.message && (
          <div className="text-red-500 mb-4">{state.message}</div>
        )}
        <form
          action={formAction}
          className="space-y-6 mt-[24px] p-[16px] bg-white rounded-[8px]"
        >
          <div className="flex justify-between border-b border-b-[#CCC] py-2">
            <span className="text-[#5D5D5D] font-medium">Name</span>
            <input
              type="text"
              name="name"
              placeholder="Name"
              className="text-blue-600 text-right bg-transparent outline-none cursor-pointer"
              required
            />
          </div>

          <div className="flex justify-between border-b border-b-[#CCC] py-2">
            <span className="text-[#5D5D5D] font-medium">ID</span>
            <input
              type="text"
              name="id"
              placeholder="Your ID"
              className="text-blue-600 text-right bg-transparent outline-none cursor-pointer"
              required
            />
          </div>

          <div className="flex justify-between border-b border-b-[#CCC] py-2">
            <span className="text-[#5D5D5D] font-medium">Password</span>
            <input
              type="password"
              name="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="text-blue-600 text-right bg-transparent outline-none cursor-pointer"
              required
            />
          </div>

          <div className="flex justify-between border-b border-b-[#CCC] py-2">
            <span className="text-[#5D5D5D] font-medium">Password Check</span>
            <input
              type="password"
              name="passwordCheck"
              placeholder="********"
              value={passwordCheck}
              onChange={(e) => setPasswordCheck(e.target.value)}
              className="text-blue-600 text-right bg-transparent outline-none cursor-pointer"
              required
            />
          </div>

          {isMismatch && (
            <p className="text-red-500 text-sm text-right">
              Passwords do not match.
            </p>
          )}

          <button
            type="submit"
            disabled={pending || isMismatch}
            className="w-full mt-4 bg-[#4285F4] text-white py-2 rounded hover:bg-[#3367D6] disabled:opacity-50 cursor-pointer"
          >
            Save
          </button>
        </form>
      </div>
    </main>
  );
}
