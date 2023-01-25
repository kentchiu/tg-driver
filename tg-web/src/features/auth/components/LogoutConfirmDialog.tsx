export const LogoutConfirmDialog = () => {
  return (
    <>
      <div className="h-28 w-96 rounded-md bg-gray-700 p-6 text-gray-300">
        <div>Are you Sure you want to log out?</div>
        <div className="flex flex-row justify-end pt-6">
          <div className="pr-4 text-sky-500">Cancel</div>
          <div className="text-red-500">Log out</div>
        </div>
      </div>
    </>
  );
};
