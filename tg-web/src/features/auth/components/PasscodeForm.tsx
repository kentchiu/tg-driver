import React from 'react';

export const PasscodeForm = () => {
  return (
    <>
      <form action="" className="mt-6 mb-0 space-y-4 ">
        <p className="text-lg font-medium text-gray-100">Enter You Local passcode</p>
        <div>
          <label htmlFor="password" className="text-sm font-medium text-sky-400">
            Your Passcode
          </label>

          <div className="relative mt-1">
            <input
              type="password"
              id="password"
              className="w-full border-x-0 border-t-0 bg-gray-800 p-4 pr-12 text-sm text-gray-200"
              placeholder="Your passcode"
            />

            <span className="absolute inset-y-0 right-4 inline-flex items-center">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 text-gray-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                />
              </svg>
            </span>
          </div>
        </div>

        <button
          type="submit"
          className="block w-full rounded-lg bg-sky-500 px-5 py-3 text-sm font-medium text-gray-200"
        >
          Submit
        </button>
        <div className="text-center text-sm text-sky-500">Log out</div>
      </form>
    </>
  );
};
