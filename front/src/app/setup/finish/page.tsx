import Link from "next/link";

export default function FinishSetup() {
  return (
    <main>
      <h1>Setup Complete</h1>
      <p>Your setup is complete. You can now use the app.</p>
      <Link href="/evacuation">Go to page for usage</Link>
    </main>
  );
}
