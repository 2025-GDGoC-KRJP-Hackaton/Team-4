"use client";
import { handleMedicalInfo } from "@/utils/actions";
import { useActionState } from "react";
import { useRouter } from "next/navigation";

import { FaArrowLeftLong } from "react-icons/fa6";

export default function Medicals() {
  const [state, formAction, pending] = useActionState(handleMedicalInfo, {
    message: "",
  });

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
        <p className="text-[20px] text-[#5D5D5D] my-[24px]">
          Additional Information
        </p>
        {state.message && (
          <div className="text-red-500 mb-4">{state.message}</div>
        )}
        <form
          action={formAction}
          className="w-full max-w-md text-[#5D5D5D] space-y-4"
        >
          <div>
            <label className="block mb-1 font-bold">Age</label>
            <input
              type="number"
              name="age"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Date of Birth</label>
            <input
              type="date"
              name="daeOfBirth"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Primary Language</label>
            <input
              type="text"
              name="language"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Medications</label>
            <textarea
              name="medications"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              placeholder="Medication name and dosage"
              rows={3}
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Allergies</label>
            <textarea
              name="allergies"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              placeholder="Allergy name and reaction"
              rows={3}
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Conditions</label>
            <textarea
              name="conditions"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              placeholder="Condition name and details"
              rows={3}
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Height (cm)</label>
            <input
              type="number"
              name="height"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Weight (kg)</label>
            <input
              type="number"
              name="weight"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Blood Type</label>
            <input
              type="text"
              name="bloodType"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              placeholder="e.g. O+, A-, etc."
            />
          </div>
          <div>
            <label className="block mb-1 font-bold">Emergency Contacts</label>
            <textarea
              name="emergencyContacts"
              className="w-full bg-white outline-none border border-transparent focus:border-[#CCC] rounded-[8px] px-3 py-2 cursor-pointer"
              placeholder="Name and phone number(s)"
              rows={3}
            />
          </div>
          <button
            type="submit"
            disabled={pending}
            className="w-full bg-[#4285F4] text-white py-2 rounded hover:bg-[#3367D6] cursor-pointer"
          >
            Save
          </button>
        </form>
      </div>
    </main>
  );
}
